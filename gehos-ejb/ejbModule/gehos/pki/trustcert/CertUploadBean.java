package gehos.pki.trustcert;


import gehos.pki.actions.TrustManagerController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;

import org.component.javasign1.trust.ITrustServices;
import org.component.javasign1.trust.TrustException;
import org.component.javasign1.trust.TrustFactory;
import org.component.javasign1.trust.TrusterType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("fileUploadBean")
@Scope(ScopeType.CONVERSATION)
public class CertUploadBean {

	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;

	private boolean error = false;
	private static String key = "alas.his";
	private ITrustServices trustServ = null;
	private CertFile certFiles;
	private String contenType="";
	List<String> extensionsAccept = Arrays.asList(new String("application/zip"),new String("application/pkix-cert"),new String("application/x-x509-ca-cert"));

	public CertUploadBean() {
		this.certFiles = new CertFile();
	}

	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void begin() {

	}

	@Create
	public void crete() {

		System.out.println("---------Creando el bean "
				+ this.getClass().getName() + " [" + this.toString() + "] ");
		
		trustServ = (ITrustServices) TrustFactory.getInstance().getTruster(key);
		((TrustManagerController)trustServ).setEntityManager(entityManager);

	}

	@Destroy
	public void destroy() {
		System.out.println("---------Eliminando el bean "
				+ this.getClass().getName() + " [" + this.toString() + "] ");
	}
	
	private boolean validExtension(){
		boolean result = false;
		for (int i = 0; i < extensionsAccept.size(); i++) {
			String ext = extensionsAccept.get(i);
			if (getContenType().equals(ext)) {
				result = true;				
			}
			
		}
		return result;
		
		
	}

	public void saveCerts() {
		if(getContenType().equals("application/octet-stream")){
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR,SeamResourceBundle.getBundle().getString(
							"archivoVacio"));
			return;
		}
		if (!validExtension()) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"wrongExt"));		
			return;
			
		}
		
		
		System.out.println(certFiles.getName());
		System.out.println(certFiles.getData() == null ? "nooooo" : certFiles
				.getData().length);
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e1) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, e1.getMessage());
			e1.printStackTrace();
			return;
		}
		if (certFiles.getName().endsWith(".zip")) {
			try {
				ByteArrayOutputStream dest = null;
				ByteArrayInputStream fis = new ByteArrayInputStream(certFiles.getData());
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					System.out.println("Extracting: " + entry);
					if (entry.isDirectory()) {
						/*File dirs = new File(entry.getName());
						dirs.mkdirs();*/
					} else {
						int count;
						byte data[] = new byte[2048];
						
						dest = new ByteArrayOutputStream();
						while ((count = zis.read(data, 0, 2048)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						addCertificate(dest.toByteArray(), cf);
						dest.close();
					}
				}
				zis.close();
			} catch (Exception e) {
				this.error = true;
				facesMessages.addToControlFromResourceBundle("btnSi",
						Severity.ERROR, e.getMessage());
				e.printStackTrace();
			}

		} else if (certFiles.getName().endsWith(".cer") || certFiles.getName().endsWith(".pem"))
			addCertificate(certFiles.getData(), cf);
		
		
		entityManager.flush();

	}
	
	private void addCertificate(byte[] certbyte, CertificateFactory cf){
		try {

			
			X509Certificate cert = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(certbyte));

			trustServ.addCA(cert, TrusterType.TRUSTER_SIGNCERTS_ISSUER);
		} catch (CertificateException e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"certInvalid"));
			e.printStackTrace();
			// return;

		} catch (TrustException e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"certInvalid"));
			e.printStackTrace();
			// return;
		} catch (Exception e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"certInvalid"));
			e.printStackTrace();
			// return;
		}
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public CertFile getCertFiles() {
		return certFiles;
	}

	public void setCertFiles(CertFile certFiles) {
		this.certFiles = certFiles;
	}

	public String getContenType() {
		return contenType;
	}

	public void setContenType(String contenType) {
		this.contenType = contenType;
	}

}