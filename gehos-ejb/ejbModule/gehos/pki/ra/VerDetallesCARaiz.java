package gehos.pki.ra;

import gehos.pki.entity.CaKeystore_pki;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.bouncycastle.jce.X509Principal;
import org.component.javasign2.libreria.utilidades.UtilidadCertificados;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import sun.misc.BASE64Decoder;

@Name("verDetallesCARaiz")
public class VerDetallesCARaiz {
	
	@In
	EntityManager entityManager;
	
	private CaKeystore_pki ca_keystore;
	private String rootCacommonName = "";
	private String rootCacountry = "";
	private String rootCastate = "";
	private String rootCaorganizationalUnit = "";
	private String rootCaorganization = "";
	private String rootCalocality = "";
	
	public void init() {

		try {
			ca_keystore = (CaKeystore_pki) entityManager.createQuery(
					"select ca from CaKeystore_pki ca where "
							+ "ca.eliminado = false").getSingleResult();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new ByteArrayInputStream(new BASE64Decoder()
					.decodeBuffer(this.ca_keystore.getKeystore())), new RaFunctions().getStringPass());
			X509Certificate cert = (X509Certificate) ks
					.getCertificate(RaFunctions.getrootalias());
			this.rootCacommonName = UtilidadCertificados.getPartDN(cert,
					X509Principal.CN);
			this.rootCacountry = UtilidadCertificados.getPartDN(cert,
					X509Principal.C);
			String nacion = (String) entityManager
					.createQuery(
							"select nacion.valor from Nacion_configuracion nacion where nacion.eliminado = false and "
									+ "nacion.codigo = :cod").setParameter(
							"cod", this.rootCacountry).getSingleResult();
			this.rootCacountry = this.rootCacountry + "(" + nacion + ")";
			
			this.rootCalocality = UtilidadCertificados.getPartDN(cert,
					X509Principal.L);
			this.rootCaorganization = UtilidadCertificados.getPartDN(cert,
					X509Principal.O);
			this.rootCaorganizationalUnit = UtilidadCertificados.getPartDN(
					cert, X509Principal.OU);
			this.rootCastate = UtilidadCertificados.getPartDN(cert,
					X509Principal.ST);

			
		} catch (NoResultException e) {
			
		} catch (Exception e) {

		}
		
	}

	public CaKeystore_pki getCa_keystore() {
		return ca_keystore;
	}

	public void setCa_keystore(CaKeystore_pki caKeystore) {
		ca_keystore = caKeystore;
	}

	public String getRootCacommonName() {
		return rootCacommonName;
	}

	public void setRootCacommonName(String rootCacommonName) {
		this.rootCacommonName = rootCacommonName;
	}

	public String getRootCacountry() {
		return rootCacountry;
	}

	public void setRootCacountry(String rootCacountry) {
		this.rootCacountry = rootCacountry;
	}

	public String getRootCastate() {
		return rootCastate;
	}

	public void setRootCastate(String rootCastate) {
		this.rootCastate = rootCastate;
	}

	public String getRootCaorganizationalUnit() {
		return rootCaorganizationalUnit;
	}

	public void setRootCaorganizationalUnit(String rootCaorganizationalUnit) {
		this.rootCaorganizationalUnit = rootCaorganizationalUnit;
	}

	public String getRootCaorganization() {
		return rootCaorganization;
	}

	public void setRootCaorganization(String rootCaorganization) {
		this.rootCaorganization = rootCaorganization;
	}

	public String getRootCalocality() {
		return rootCalocality;
	}

	public void setRootCalocality(String rootCalocality) {
		this.rootCalocality = rootCalocality;
	}
	
	
}
