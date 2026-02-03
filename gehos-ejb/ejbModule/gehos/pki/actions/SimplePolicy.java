package gehos.pki.actions;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.component.javasign1.trust.NotTrustedException;
import org.component.javasign1.trust.TrustAbstract;
import org.component.javasign1.trust.TrustException;
import org.component.javasign1.trust.TrustFactory;
import org.component.javasign1.xml.xades.policy.IValidacionPolicy;
import org.component.javasign1.xml.xades.policy.PolicyResult;
import org.component.javasign2.libreria.utilidades.I18n;
import org.component.javasign2.libreria.xades.DatosFirma;
import org.component.javasign2.libreria.xades.ResultadoValidacion;
import org.component.javasign2.libreria.xades.errores.PolicyException;

import org.w3c.dom.Element;

public class SimplePolicy implements IValidacionPolicy{

	/**
	 * <p>
	 * Validador de confianza de los elementos de la factura electrónica.
	 * </p>
	 */
	private TrustAbstract truster = null;
	
	
	public TrustAbstract getTruster() {
		return truster;
	}

	public void setTruster(TrustAbstract truster) {
		this.truster = truster;
	}

	public SimplePolicy() {
		
	}

	public String getIdentidadPolicy() {
		return "Política de validacion-Alas-HIS";
	}

	/**
	 * <p>
	 * Se valida que el certificado empleado en la firma se encuentre en el
	 * periodo de validez.
	 * </p>
	 * 
	 * @param element
	 *            Nodo de firma
	 * @param resultadoValidacion
	 *            Estructura de datos de resultado de validación
	 */
	public PolicyResult validaPolicy(Element element,
			ResultadoValidacion resultadovalidacion){
		PolicyResult pr = new PolicyResult();

		X509Certificate cert = (X509Certificate) resultadovalidacion
				.getDatosFirma().getCadenaFirma().getCertificates().get(0);

		try {
			cert.checkValidity(new Date());
			checkTrustSigningCertificate(element, resultadovalidacion);
			pr.setResult(PolicyResult.StatusValidation.valid);
			//System.out.println("Validación de política superada.");
		} catch (CertificateExpiredException e) {
			pr.setResult(PolicyResult.StatusValidation.invalid);
			pr.setDescriptionResult( 
					". Validación de política NO superada. Certificado caducado");
			//System.out.println("Validación de política NO superada. Certificado caducado");
		} catch (CertificateNotYetValidException e) {
			pr.setResult(PolicyResult.StatusValidation.invalid);
			pr.setDescriptionResult( 
					". Validación de política NO superada. Certificado aún no válido");
			//System.out.println("Validación de política NO superada. Certificado aún no válido");
		} catch (PolicyException ex) {
			pr.setResult(PolicyResult.StatusValidation.invalid);
			pr.setDescriptionResult(ex.getMessage() + 
			". " + I18n.getResource("libreriaxades.validarfirmaxml.error176"));
		}
		return pr;
		
	}
	
	/**
	 * <p>Se comprueba que el certificado firmante es de confianza.</p>
	 * @param signatureNode Elemento que es la firma con la política que se valida
	 * @param rs Resultado de validación de la firma
	 * @throws PolicyException Si la informacion obtenida no corresponde con la policy o no se pudo comprobar
	 */
	private void checkTrustSigningCertificate(final Element signatureNode, final ResultadoValidacion rs) throws PolicyException  {
		// chequeo de que el certificado firmante es de confianza (según la ley)
		DatosFirma df = rs.getDatosFirma();
		if (df != null) {
			try {
				truster.isTrusted(df.getCadenaFirma());
			} catch (TrustException ex) {
				String text = "";
				if(ex instanceof NotTrustedException)
					text = I18n.getResource("libreriaxades.validarfirmaxml.error174");
				throw new PolicyException(text);
			}
		}
		else {
			throw new PolicyException("");
		}
	}
}
