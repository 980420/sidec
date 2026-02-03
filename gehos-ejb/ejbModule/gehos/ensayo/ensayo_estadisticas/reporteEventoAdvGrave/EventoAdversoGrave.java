package gehos.ensayo.ensayo_estadisticas.reporteEventoAdvGrave;

public class EventoAdversoGrave {
	private String nombreSujeto;
	private String eventoAdverso;	
	private String fechaInicioEA;
	private String causalidad;
	private String fechaFinEA;
	private String style;
	
	public EventoAdversoGrave(String nombreSujeto, String eventoAdverso, String fechaInicioEA, String fechaFinEA, String causalidad, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.eventoAdverso = eventoAdverso;
		this.fechaInicioEA = fechaInicioEA;
		this.fechaFinEA = fechaFinEA;
		this.causalidad = causalidad;
		this.style = style;
	}
	
	public EventoAdversoGrave(String nombreSujeto, String eventoAdverso, String fechaInicioEA, String fechaFinEA, String causalidad) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.eventoAdverso = eventoAdverso;
		this.fechaInicioEA = fechaInicioEA;
		this.fechaFinEA = fechaFinEA;
		this.causalidad = causalidad;
	}

	public String getEventoAdverso() {
		return eventoAdverso;
	}

	public void setEventoAdverso(String eventoAdverso) {
		this.eventoAdverso = eventoAdverso;
	}

	public String getCausalidad() {
		return causalidad;
	}

	public void setCausalidad(String causalidad) {
		this.causalidad = causalidad;
	}
	
	public String getNombreSujeto() {
		return nombreSujeto;
	}

	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}

	public String getFechaInicioEA() {
		return fechaInicioEA;
	}

	public void setFechaInicioEA(String fechaInicioEA) {
		this.fechaInicioEA = fechaInicioEA;
	}

	public String getFechaFinEA() {
		return fechaFinEA;
	}

	public void setFechaFinEA(String fechaFinEA) {
		this.fechaFinEA = fechaFinEA;
	}
	
	
}
