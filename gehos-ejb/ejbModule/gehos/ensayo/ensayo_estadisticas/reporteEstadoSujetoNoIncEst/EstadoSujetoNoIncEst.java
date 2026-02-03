package gehos.ensayo.ensayo_estadisticas.reporteEstadoSujetoNoIncEst;

public class EstadoSujetoNoIncEst {
	private String nombreSujeto;
	private String fechaEvaluacion;
	private String causaNoInc;	
	private String style;
	
	public EstadoSujetoNoIncEst(String nombreSujeto, String fechaEvaluacion,
			String causaNoInc, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaEvaluacion = fechaEvaluacion;
		this.causaNoInc = causaNoInc;
		this.style = style;
	}

	public EstadoSujetoNoIncEst(String nombreSujeto, String fechaEvaluacion,
			String causaNoInc) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaEvaluacion = fechaEvaluacion;
		this.causaNoInc = causaNoInc;
	}

	public String getNombreSujeto() {
		return nombreSujeto;
	}

	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}

	public String getFechaEvaluacion() {
		return fechaEvaluacion;
	}

	public void setFechaEvaluacion(String fechaEvaluacion) {
		this.fechaEvaluacion = fechaEvaluacion;
	}

	public String getCausaNoInc() {
		return causaNoInc;
	}

	public void setCausaNoInc(String causaNoInc) {
		this.causaNoInc = causaNoInc;
	}
	
	
	
}
