package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class ReporteExpeditoTres {
	
	private List<HistoriaRelevante> listResultados;
	private String otrosE;
	private String datosLab;
	private String copiaBiopsia;
	private String copiaAutopsia;
	private String otrosE1;
	private String reportado;
	private String responsabilidad;
	private String fechaT;
	private String revisadoDir;
	private String responsabilidad1;
	private String fechaT1;
	private String revisadoDpto;
	private String responsabilidad2;
	private String fechaT2;
	
	
	public ReporteExpeditoTres(List<HistoriaRelevante> listResultados, String otrosE,
			String datosLab, String copiaBiopsia, String copiaAutopsia,
			String otrosE1, String reportado, String responsabilidad,
			String fechaT, String revisadoDir, String responsabilidad1,
			String fechaT1, String revisadoDpto, String responsabilidad2,
			String fechaT2) {
		super();
		this.listResultados = listResultados;
		this.otrosE = otrosE;
		this.datosLab = datosLab;
		this.copiaBiopsia = copiaBiopsia;
		this.copiaAutopsia = copiaAutopsia;
		this.otrosE1 = otrosE1;
		this.reportado = reportado;
		this.responsabilidad = responsabilidad;
		this.fechaT = fechaT;
		this.revisadoDir = revisadoDir;
		this.responsabilidad1 = responsabilidad1;
		this.fechaT1 = fechaT1;
		this.revisadoDpto = revisadoDpto;
		this.responsabilidad2 = responsabilidad2;
		this.fechaT2 = fechaT2;
	}
	
	
	public List<HistoriaRelevante> getListResultados() {
		return listResultados;
	}
	public void setListResultados(List<HistoriaRelevante> listResultados) {
		this.listResultados = listResultados;
	}
	public String getOtrosE() {
		return otrosE;
	}
	public void setOtrosE(String otrosE) {
		this.otrosE = otrosE;
	}
	public String getDatosLab() {
		return datosLab;
	}
	public void setDatosLab(String datosLab) {
		this.datosLab = datosLab;
	}
	public String getCopiaBiopsia() {
		return copiaBiopsia;
	}
	public void setCopiaBiopsia(String copiaBiopsia) {
		this.copiaBiopsia = copiaBiopsia;
	}
	public String getCopiaAutopsia() {
		return copiaAutopsia;
	}
	public void setCopiaAutopsia(String copiaAutopsia) {
		this.copiaAutopsia = copiaAutopsia;
	}
	public String getOtrosE1() {
		return otrosE1;
	}
	public void setOtrosE1(String otrosE1) {
		this.otrosE1 = otrosE1;
	}
	public String getReportado() {
		return reportado;
	}
	public void setReportado(String reportado) {
		this.reportado = reportado;
	}
	public String getResponsabilidad() {
		return responsabilidad;
	}
	public void setResponsabilidad(String responsabilidad) {
		this.responsabilidad = responsabilidad;
	}
	public String getFechaT() {
		return fechaT;
	}
	public void setFechaT(String fechaT) {
		this.fechaT = fechaT;
	}
	public String getRevisadoDir() {
		return revisadoDir;
	}
	public void setRevisadoDir(String revisadoDir) {
		this.revisadoDir = revisadoDir;
	}
	public String getResponsabilidad1() {
		return responsabilidad1;
	}
	public void setResponsabilidad1(String responsabilidad1) {
		this.responsabilidad1 = responsabilidad1;
	}
	public String getFechaT1() {
		return fechaT1;
	}
	public void setFechaT1(String fechaT1) {
		this.fechaT1 = fechaT1;
	}
	public String getRevisadoDpto() {
		return revisadoDpto;
	}
	public void setRevisadoDpto(String revisadoDpto) {
		this.revisadoDpto = revisadoDpto;
	}
	public String getResponsabilidad2() {
		return responsabilidad2;
	}
	public void setResponsabilidad2(String responsabilidad2) {
		this.responsabilidad2 = responsabilidad2;
	}
	public String getFechaT2() {
		return fechaT2;
	}
	public void setFechaT2(String fechaT2) {
		this.fechaT2 = fechaT2;
	}
	
}
