package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class ReporteExpeditoDos {
	
	private String producto;
	private String lote;
	private String fechaCadu;
	private String iniTrata;
	private String cantDosis;
	private String fechaUD;
	private String dosisAdmin;
	private String viaAdmin;
	private String eventoOcurrio;
	private String tiempoAparicion;
	private String conductaAsumida;
	private String modInten;
	private String eaReaparecio;
	private String secuela;
	private String tipoResultado;
	private List<MedicacionConco> medicacionC;
	private List<MedicacionOtra> medicacionO;
	
	public ReporteExpeditoDos(String producto, String lote, String fechaCadu,
			String iniTrata, String cantDosis, String fechaUD,
			String dosisAdmin, String viaAdmin, String eventoOcurrio,
			String tiempoAparicion, String conductaAsumida, String modInten,
			String eaReaparecio, String secuela, String tipoResultado,
			List<MedicacionConco> medicacionC, List<MedicacionOtra> medicacionO) {
		super();
		this.producto = producto;
		this.lote = lote;
		this.fechaCadu = fechaCadu;
		this.iniTrata = iniTrata;
		this.cantDosis = cantDosis;
		this.fechaUD = fechaUD;
		this.dosisAdmin = dosisAdmin;
		this.viaAdmin = viaAdmin;
		this.eventoOcurrio = eventoOcurrio;
		this.tiempoAparicion = tiempoAparicion;
		this.conductaAsumida = conductaAsumida;
		this.modInten = modInten;
		this.eaReaparecio = eaReaparecio;
		this.secuela = secuela;
		this.tipoResultado = tipoResultado;
		this.medicacionC = medicacionC;
		this.medicacionO = medicacionO;
	}
	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
	public String getLote() {
		return lote;
	}
	public void setLote(String lote) {
		this.lote = lote;
	}
	public String getFechaCadu() {
		return fechaCadu;
	}
	public void setFechaCadu(String fechaCadu) {
		this.fechaCadu = fechaCadu;
	}
	public String getIniTrata() {
		return iniTrata;
	}
	public void setIniTrata(String iniTrata) {
		this.iniTrata = iniTrata;
	}
	public String getCantDosis() {
		return cantDosis;
	}
	public void setCantDosis(String cantDosis) {
		this.cantDosis = cantDosis;
	}
	public String getFechaUD() {
		return fechaUD;
	}
	public void setFechaUD(String fechaUD) {
		this.fechaUD = fechaUD;
	}
	public String getDosisAdmin() {
		return dosisAdmin;
	}
	public void setDosisAdmin(String dosisAdmin) {
		this.dosisAdmin = dosisAdmin;
	}
	public String getViaAdmin() {
		return viaAdmin;
	}
	public void setViaAdmin(String viaAdmin) {
		this.viaAdmin = viaAdmin;
	}
	public String getEventoOcurrio() {
		return eventoOcurrio;
	}
	public void setEventoOcurrio(String eventoOcurrio) {
		this.eventoOcurrio = eventoOcurrio;
	}
	public String getTiempoAparicion() {
		return tiempoAparicion;
	}
	public void setTiempoAparicion(String tiempoAparicion) {
		this.tiempoAparicion = tiempoAparicion;
	}
	public String getConductaAsumida() {
		return conductaAsumida;
	}
	public void setConductaAsumida(String conductaAsumida) {
		this.conductaAsumida = conductaAsumida;
	}
	public String getModInten() {
		return modInten;
	}
	public void setModInten(String modInten) {
		this.modInten = modInten;
	}
	public String getEaReaparecio() {
		return eaReaparecio;
	}
	public void setEaReaparecio(String eaReaparecio) {
		this.eaReaparecio = eaReaparecio;
	}
	public String getSecuela() {
		return secuela;
	}
	public void setSecuela(String secuela) {
		this.secuela = secuela;
	}
	public String getTipoResultado() {
		return tipoResultado;
	}
	public void setTipoResultado(String tipoResultado) {
		this.tipoResultado = tipoResultado;
	}
	public List<MedicacionConco> getMedicacionC() {
		return medicacionC;
	}
	public void setMedicacionC(List<MedicacionConco> medicacionC) {
		this.medicacionC = medicacionC;
	}
	public List<MedicacionOtra> getMedicacionO() {
		return medicacionO;
	}
	public void setMedicacionO(List<MedicacionOtra> medicacionO) {
		this.medicacionO = medicacionO;
	}
}
