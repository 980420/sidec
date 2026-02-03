package gehos.ensayo.ensayo_estadisticas.reporteEstadoSujetoIncEst;

public class EstadoSujetoIncEst {
	private String nombreSujeto;
	private String fechaConsentimiento;
	private String fechaInclusion;
	private String inicioTratamiento;
	private String fechaInterrupcion;
	private String causaInterrupcion;
	private String fechaFallecimiento;
	private String causaFallecimiento;
	private String style;
	
	public EstadoSujetoIncEst(String nombreSujeto, String fechaConsentimiento,
			String fechaInclusion, String inicioTratamiento,
			String fechaInterrupcion, String causaInterrupcion,
			String fechaFallecimiento, String causaFallecimiento, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaConsentimiento = fechaConsentimiento;
		this.fechaInclusion = fechaInclusion;
		this.inicioTratamiento = inicioTratamiento;
		this.fechaInterrupcion = fechaInterrupcion;
		this.causaInterrupcion = causaInterrupcion;
		this.fechaFallecimiento = fechaFallecimiento;
		this.causaFallecimiento = causaFallecimiento;
		this.style = style;
	}

	public EstadoSujetoIncEst(String nombreSujeto, String fechaConsentimiento,
			String fechaInclusion, String inicioTratamiento,
			String fechaInterrupcion, String causaInterrupcion,
			String fechaFallecimiento, String causaFallecimiento) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaConsentimiento = fechaConsentimiento;
		this.fechaInclusion = fechaInclusion;
		this.inicioTratamiento = inicioTratamiento;
		this.fechaInterrupcion = fechaInterrupcion;
		this.causaInterrupcion = causaInterrupcion;
		this.fechaFallecimiento = fechaFallecimiento;
		this.causaFallecimiento = causaFallecimiento;
	}

	
	public String getNombreSujeto() {
		return nombreSujeto;
	}

	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}

	public String getFechaConsentimiento() {
		return fechaConsentimiento;
	}

	public void setFechaConsentimiento(String fechaConsentimiento) {
		this.fechaConsentimiento = fechaConsentimiento;
	}

	public String getFechaInclusion() {
		return fechaInclusion;
	}

	public void setFechaInclusion(String fechaInclusion) {
		this.fechaInclusion = fechaInclusion;
	}

	public String getInicioTratamiento() {
		return inicioTratamiento;
	}

	public void setInicioTratamiento(String inicioTratamiento) {
		this.inicioTratamiento = inicioTratamiento;
	}

	public String getFechaInterrupcion() {
		return fechaInterrupcion;
	}

	public void setFechaInterrupcion(String fechaInterrupcion) {
		this.fechaInterrupcion = fechaInterrupcion;
	}

	public String getCausaInterrupcion() {
		return causaInterrupcion;
	}

	public void setCausaInterrupcion(String causaInterrupcion) {
		this.causaInterrupcion = causaInterrupcion;
	}

	public String getFechaFallecimiento() {
		return fechaFallecimiento;
	}

	public void setFechaFallecimiento(String fechaFallecimiento) {
		this.fechaFallecimiento = fechaFallecimiento;
	}

	public String getCausaFallecimiento() {
		return causaFallecimiento;
	}

	public void setCausaFallecimiento(String causaFallecimiento) {
		this.causaFallecimiento = causaFallecimiento;
	}	
	
}
