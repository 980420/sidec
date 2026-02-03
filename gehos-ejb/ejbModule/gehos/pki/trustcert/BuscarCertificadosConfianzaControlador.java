package gehos.pki.trustcert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import gehos.pki.actions.TrustManagerController;
import gehos.pki.entity.CertificadosConfiables_pki;
import gehos.pki.entity.ServidorSelloTiempo_pki;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.component.javasign1.trust.ITrustServices;
import org.component.javasign1.trust.TrustFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import sun.misc.BASE64Decoder;

@Name("buscarCertificadosConfianza")
@Scope(ScopeType.CONVERSATION)
public class BuscarCertificadosConfianzaControlador extends
		EntityQuery<CertificadosConfiables_pki> {

	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;

	private static final String EJBQL = "select certificadoConfiable from "
			+ "CertificadosConfiables_pki certificadoConfiable where certificadoConfiable.eliminado = false";

	private static final String[] RESTRICTIONS = {
			"lower(certificadoConfiable.certificadoC) like concat(lower(#{buscarCertificadosConfianza.certificadoC}),'%')",
			"lower(certificadoConfiable.certificadoSt) like concat(lower(#{buscarCertificadosConfianza.certificadoSt}),'%')",
			"lower(certificadoConfiable.certificadoL) like concat(lower(#{buscarCertificadosConfianza.certificadoL}),'%')",
			"lower(certificadoConfiable.certificadoO) like concat(lower(#{buscarCertificadosConfianza.certificadoO}),'%')",
			"lower(certificadoConfiable.certificadoOu) like concat(lower(#{buscarCertificadosConfianza.certificadoOu}),'%')",
			"lower(certificadoConfiable.certificadoCn) like concat(lower(#{buscarCertificadosConfianza.certificadoCn}),'%')", };
	
	private static final String tmpFolder = "tmpFolder";
	private String certificadoC = "";
	private String certificadoSt = "";
	private String certificadoL = "";
	private String certificadoO = "";
	private String certificadoOu = "";
	private String certificadoCn = "";
	private boolean open = true;
	private boolean avanzada = false;
	private int idcertificadoConfiable;
	private int certToSlected;
	private Hashtable<Integer, CertificadosConfiables_pki> certstoexport = new Hashtable<Integer, CertificadosConfiables_pki>();
	private static String key = "alas.his";
	private ITrustServices trustServ = null;
	
	public BuscarCertificadosConfianzaControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("certificadoConfiable.id desc");
		
	}

	public void eliminarseleccionarAllCertsToExport() {
		certstoexport.clear();
	}

	public void seleccionarAllCertsToExport() {
		Integer aux = this.getFirstResult();
		List<CertificadosConfiables_pki> lis = new ArrayList<CertificadosConfiables_pki>();

		while (this.isNextExists()) {

			lis.addAll(this.getResultList());
			this.setFirstResult(this.getNextFirstResult());
		}
		lis.addAll(this.getResultList());
		for (Iterator iterator = lis.iterator(); iterator.hasNext();) {
			CertificadosConfiables_pki certificadosConfiablesPki = (CertificadosConfiables_pki) iterator
					.next();
			if (!certstoexport.contains(certificadosConfiablesPki.getId()))
				certstoexport.put(certificadosConfiablesPki.getId(),
						certificadosConfiablesPki);

		}

		this.setFirstResult(aux == null ? 0 : aux);
		
	}

	public void seleccionarCertsToExport() {
		if (certstoexport.containsKey(certToSlected)) {
			certstoexport.remove(certToSlected);
		} else {
			CertificadosConfiables_pki selected = (CertificadosConfiables_pki) entityManager
					.find(CertificadosConfiables_pki.class, certToSlected);
			certstoexport.put(certToSlected, selected);
		}
	}

	public Boolean EstaSeleccionado(Integer id) {

		return certstoexport.containsKey(id);
	}

	private String path = null;

	public void export() {
		if(!this.certstoexport.isEmpty()){
			try {
				FacesContext aFacesContext = FacesContext.getCurrentInstance();
				ServletContext context = (ServletContext) aFacesContext
						.getExternalContext().getContext();
				String rootpath = context.getRealPath(File.separator
						+ "modConfiguracion" + File.separator + "pki"
						+ File.separator + tmpFolder + File.separator);
				SimpleDateFormat format = new SimpleDateFormat("dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");
				String zipName = format.format(new Date()) + ".zip";
				ZipOutputStream os = new ZipOutputStream(new FileOutputStream(
						new File(rootpath, zipName)));
				Enumeration<Integer> keys = certstoexport.keys();
				while(keys.hasMoreElements()){
					Integer next = keys.nextElement();
					ZipEntry entrada = new ZipEntry(next.toString() + ".cer");
					os.putNextEntry(entrada);
					ByteArrayInputStream in = new ByteArrayInputStream(
							new BASE64Decoder().decodeBuffer(certstoexport.get(next).getCertificado()));
					byte[] buffer = new byte[1024];
					int leido = 0;
					while (0 < (leido = in.read(buffer))) {
						os.write(buffer, 0, leido);
					}
					in.close();
					os.closeEntry();
				}
				os.close();
				this.path = "../" + tmpFolder + "/" + zipName;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		update();
	}

	public void cambiarTipodeBusqueda(boolean a) {
		this.avanzada = a;
	}

	public void buscar(boolean avan) {
		setFirstResult(0);
		setOrder("certificadoConfiable.id desc");
	}

	private void update() {
		this.refresh();
		
		if (this.getResultList() != null && this.getResultList().size() == 0
				&& this.getFirstResult() != null && this.getFirstResult() != 0)
			setFirstResult(getFirstResult() - getMaxResults());
	}

	public void eliminar() {
		try {
			CertificadosConfiables_pki certificado = entityManager.find(
					CertificadosConfiables_pki.class,
					this.idcertificadoConfiable);
			this.idcertificadoConfiable = -1;
			certificado.setEliminado(true);
			entityManager.merge(certificado);
			entityManager.flush();
			update();
			if(this.certstoexport.containsKey(certificado.getId()))
				this.certstoexport.remove(certificado.getId());
			
			trustServ = (ITrustServices) TrustFactory.getInstance().getTruster(key);
			((TrustManagerController)trustServ).setEntityManager(entityManager);

		} catch (Exception e) {
			facesMessages
					.addToControlFromResourceBundle("btnSi", Severity.ERROR,
							"msg_certUso_modConfig");
			e.printStackTrace();
		}

	}
	
	public List<CertificadosConfiables_pki> certToExportToList(){
		List<CertificadosConfiables_pki> ret = new ArrayList<CertificadosConfiables_pki>();
		ret.addAll(this.certstoexport.values());
		return ret;
	}

	public void seleccionar(int id) {
		this.idcertificadoConfiable = id;
	}

	public void abrirCerrar() {
		this.open = !open;
	}

	

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {
		this.avanzada = avanzada;
	}

	public int getCertToSlected() {
		return certToSlected;
	}

	public void setCertToSlected(int certToSlected) {
		this.certToSlected = certToSlected;
	}

	public Hashtable<Integer, CertificadosConfiables_pki> getCertstoexport() {
		return certstoexport;
	}

	public void setCertstoexport(
			Hashtable<Integer, CertificadosConfiables_pki> certstoexport) {
		this.certstoexport = certstoexport;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCertificadoC() {
		return certificadoC;
	}

	public void setCertificadoC(String certificadoC) {
		this.certificadoC = certificadoC;
	}

	public String getCertificadoSt() {
		return certificadoSt;
	}

	public void setCertificadoSt(String certificadoSt) {
		this.certificadoSt = certificadoSt;
	}

	public String getCertificadoL() {
		return certificadoL;
	}

	public void setCertificadoL(String certificadoL) {
		this.certificadoL = certificadoL;
	}

	public String getCertificadoO() {
		return certificadoO;
	}

	public void setCertificadoO(String certificadoO) {
		this.certificadoO = certificadoO;
	}

	public String getCertificadoOu() {
		return certificadoOu;
	}

	public void setCertificadoOu(String certificadoOu) {
		this.certificadoOu = certificadoOu;
	}

	public String getCertificadoCn() {
		return certificadoCn;
	}

	public void setCertificadoCn(String certificadoCn) {
		this.certificadoCn = certificadoCn;
	}
	
	

}
