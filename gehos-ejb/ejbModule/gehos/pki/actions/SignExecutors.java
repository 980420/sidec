package gehos.pki.actions;

import gehos.pki.entity.ServidorSelloTiempo_pki;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.component.javasign1.bridge.ConfigurationException;
import org.component.javasign1.bridge.ISignFacade;
import org.component.javasign1.bridge.InvalidCertificateException;
import org.component.javasign1.bridge.InvalidSignatureException;
import org.component.javasign1.bridge.SigningException;
import org.component.javasign1.pkstore.CertStoreException;
import org.component.javasign1.pkstore.IPKStoreManager;
import org.component.javasign1.trust.TrustAbstract;
import org.component.javasign1.trust.TrustFactory;
import org.component.javasign1.xml.refs.AllXMLToSign;
import org.component.javasign1.xml.refs.ObjectToSign;
import org.component.javasign1.xml.xades.policy.IValidacionPolicy;
import org.component.javasign1.xml.xades.policy.PoliciesManager;
import org.component.javasign2.libreria.utilidades.I18n;
import org.component.javasign2.libreria.xades.DataToSign;
import org.component.javasign2.libreria.xades.EnumFormatoFirma;
import org.component.javasign2.libreria.xades.ExtraValidators;
import org.component.javasign2.libreria.xades.FirmaXML;
import org.component.javasign2.libreria.xades.ResultadoValidacion;
import org.component.javasign2.libreria.xades.ValidarFirmaXML;
import org.component.javasign2.libreria.xades.XAdESSchemas;
import org.jboss.seam.core.SeamResourceBundle;
import org.w3c.dom.Document;

public class SignExecutors implements ISignFacade {

	public static String storeManagerKey = "storeManager";
	public static String entityManagerKey = "entityManager";

	private IPKStoreManager storeManager;
	private EntityManager entityManager;
	private ServidorSelloTiempo_pki servidor = null;

	
	public void init(Properties props) throws ConfigurationException {
		this.storeManager = (IPKStoreManager) props
				.get(SignExecutors.storeManagerKey);
		this.entityManager = (EntityManager) props
				.get(SignExecutors.entityManagerKey);
		I18n.setLocale(SeamResourceBundle.getBundle().getLocale());
		
	}
	
	public List<X509Certificate> getSignCertificates() {

		try {
			return this.storeManager.getSignCertificates();
		} catch (CertStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new LinkedList<X509Certificate>();
		}
	}


	public Document sign(X509Certificate arg0, Document arg1) throws SigningException{
		
		try {
			servidor = (ServidorSelloTiempo_pki) entityManager.createQuery(
					"select ser from ServidorSelloTiempo_pki ser "
							+ "where ser.activo = true").getSingleResult();
		} catch (NoResultException e) {
			servidor = null;
		}
		// Obtencion del certificado para firmar. Utilizaremos el primer
		// certificado del almacen.
		X509Certificate certificate = this.getSignCertificates().get(0);
		if (certificate == null) {
			throw new SigningException("No existe ningún certificado para firmar.");
		}

		// Obtención de la clave privada asociada al certificado
		PrivateKey privateKey;
		try {
			privateKey = storeManager.getPrivateKey(certificate);
		} catch (CertStoreException e) {
			e.printStackTrace();
			throw new SigningException("Error al acceder al almacén.");
		}

		// Obtención del provider encargado de las labores criptográficas
		Provider provider = storeManager.getProvider(certificate);

		/*
		 * Creación del objeto que contiene tanto los datos a firmar como la
		 * configuración del tipo de firma
		 */
		DataToSign dataToSign = createDataToSign();
		dataToSign.setDocument(arg1);
		// Firmamos el documento
		Document docSigned = null;
		try {
			/*
			 * Creación del objeto encargado de realizar la firma
			 */
			FirmaXML firma = createFirmaXML();
			Object[] res = firma.signFile(certificate, dataToSign, privateKey,
					provider);
			docSigned = (Document) res[0];
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SigningException("Error realizando la firma. " + ex.getMessage());
			
		}

		return docSigned;
	}

	public Map<String, Object> validate(Document doc) throws InvalidSignatureException
			 {
		Map<String, Object> rets = new HashMap<String, Object>();
		// Política
		List<IValidacionPolicy> arrayPolicies = new ArrayList<IValidacionPolicy>(
				1);
		PoliciesManager polMan = PoliciesManager.getInstance();
				try {
					TrustAbstract truster = TrustFactory.getInstance().getTruster("alas.his");
					((TrustManagerController)truster).setEntityManager(entityManager);
					IValidacionPolicy ival = polMan.getValidadorPolicy(polMan.newPolicyKey(new URI("self:policy/alas/his/trust"), "alas.his"));
					((SimplePolicy)ival).setTruster(truster);
					arrayPolicies
					.add(ival);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
					throw new InvalidSignatureException(e1.getMessage());
				}

		// Validación extra de confianza de certificados

		/*
		 * truster = TrustFactory.getInstance().getTruster(TRUSTER_NAME); if
		 * (truster == null) {
		 * System.out.println("No se encontro el validador de confianza"); }
		 */
		

		// Validadores extra
		ExtraValidators validator = new ExtraValidators(arrayPolicies, null,
				null);

		// La siguente línea es una alternativa para realizar la misma acción
		// anterior, en caso de que los tres argumentos de ExtraValidators
		// fueran nulos
		// validator.setTrusterCerts((TrustAbstract)(((TrustExtendFactory)TrustFactory.getInstance()).getSignCertsTruster("mityc")));

		// Se declara la estructura de datos que almacenará el resultado de la
		// validación
		ArrayList<ResultadoValidacion> results = null;

		if (doc == null) {

			return rets;
		}

		// Se instancia el validador y se realiza la validación
		try {
			ValidarFirmaXML vXml = new ValidarFirmaXML();
			results = vXml.validar(doc, "./", validator);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidSignatureException(e.getMessage());
		}

		rets.put("result", results.get(0));

		return rets;
	}

	public void validateCert(X509Certificate arg0)
			throws InvalidCertificateException {

	}

	// Otros metodos auxiliares.

	private DataToSign createDataToSign() {
		DataToSign dataToSign = new DataToSign();
		if (servidor != null)
			dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_T);
		else
			dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
		dataToSign.setEsquema(XAdESSchemas.XAdES_132);
		dataToSign.setXMLEncoding("UTF-8");
		dataToSign.setEnveloped(true);
		dataToSign.addObject(new ObjectToSign(new AllXMLToSign(),
				"Documento CDA", null, "text/xml", null));
		return dataToSign;
	}

	private FirmaXML createFirmaXML() {
		FirmaXML firma = new FirmaXML();
		if (servidor != null){
			firma.setTSA(servidor.getUrl());
			if(servidor.getProxy() != null){
				firma.setProxyTSA(servidor.getProxy().getDireccion());
				firma.setProxyPortTSA(servidor.getProxy().getPuerto());
			}
		}
		return firma;
	}

}
