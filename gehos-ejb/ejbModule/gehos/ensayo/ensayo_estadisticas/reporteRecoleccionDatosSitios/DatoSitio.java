package gehos.ensayo.ensayo_estadisticas.reporteRecoleccionDatosSitios;

public class DatoSitio {
	private String nombreSitio;
	private int totalIncluidos;
	private int planMomentosProgramados;
	private int crdFirmada;
	private int crdFalta;
	private String porcientoCumplimiento;
	private String style;
	
	
	public DatoSitio(String nombreSitio, int totalIncluidos, int crdTotal,
			int crdFirmada, int crdFalta, String porcientoCumplimiento, String style) {
		super();
		this.nombreSitio = nombreSitio;
		this.totalIncluidos = totalIncluidos;
		this.planMomentosProgramados = crdTotal;
		this.crdFirmada = crdFirmada;
		this.crdFalta = crdFalta;
		this.porcientoCumplimiento = porcientoCumplimiento;
		this.style = style;
	}
	
	public DatoSitio(String nombreSitio, int totalIncluidos, int crdTotal,
			int crdFirmada, int crdFalta, String porcientoCumplimiento) {
		super();
		this.nombreSitio = nombreSitio;
		this.totalIncluidos = totalIncluidos;
		this.planMomentosProgramados = crdTotal;
		this.crdFirmada = crdFirmada;
		this.crdFalta = crdFalta;
		this.porcientoCumplimiento = porcientoCumplimiento;
	}
	
	
	public String getNombreSitio() {
		return nombreSitio;
	}
	public void setNombreSitio(String nombreSitio) {
		this.nombreSitio = nombreSitio;
	}
	public int getTotalIncluidos() {
		return totalIncluidos;
	}
	public void setTotalIncluidos(int totalIncluidos) {
		this.totalIncluidos = totalIncluidos;
	}
	public int getPlanMomentosProgramados() {
		return planMomentosProgramados;
	}
	public void setPlanMomentosProgramados(int crdTotal) {
		this.planMomentosProgramados = crdTotal;
	}
	public int getCrdFirmada() {
		return crdFirmada;
	}
	public void setCrdFirmada(int crdFirmada) {
		this.crdFirmada = crdFirmada;
	}
	public int getCrdFalta() {
		return crdFalta;
	}
	public void setCrdFalta(int crdFalta) {
		this.crdFalta = crdFalta;
	}
	public String getPorcientoCumplimiento() {
		return porcientoCumplimiento;
	}
	public void setPorcientoCumplimiento(String porcientoCumplimiento) {
		this.porcientoCumplimiento = porcientoCumplimiento;
	}
	
	
}
