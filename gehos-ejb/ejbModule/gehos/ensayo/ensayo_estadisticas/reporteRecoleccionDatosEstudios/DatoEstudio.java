package gehos.ensayo.ensayo_estadisticas.reporteRecoleccionDatosEstudios;

public class DatoEstudio {
	private String nombreEstudio;
	private int totalIncluidos;
	private int planMomentosProgramados;
	private int crdFirmada;
	private int crdFalta;
	private String porcientoCumplimiento;
	private String style;
	
	
	public DatoEstudio(String nombreEstudio, int totalIncluidos, int crdTotal,
			int crdFirmada, int crdFalta, String porcientoCumplimiento, String style) {
		super();
		this.nombreEstudio = nombreEstudio;
		this.totalIncluidos = totalIncluidos;
		this.planMomentosProgramados = crdTotal;
		this.crdFirmada = crdFirmada;
		this.crdFalta = crdFalta;
		this.porcientoCumplimiento = porcientoCumplimiento;
		this.style = style;
	}
	
	public DatoEstudio(String nombreEstudio, int totalIncluidos, int crdTotal,
			int crdFirmada, int crdFalta, String porcientoCumplimiento) {
		super();
		this.nombreEstudio = nombreEstudio;
		this.totalIncluidos = totalIncluidos;
		this.planMomentosProgramados = crdTotal;
		this.crdFirmada = crdFirmada;
		this.crdFalta = crdFalta;
		this.porcientoCumplimiento = porcientoCumplimiento;
	}
	
	
	public String getNombreEstudio() {
		return nombreEstudio;
	}
	public void setNombreSitio(String nombreEstudio) {
		this.nombreEstudio = nombreEstudio;
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
