package gehos.ensayo.ensayo_estadisticas.notificaciones;

public class Notificacion {
	private String nombreSujeto;
	private String eventoAdverso;	
	private String fechaInicioEA;
	private String fechaFinEA;
	private String style;
	
	public Notificacion(String nombreSujeto, String eventoAdverso, String fechaInicioEA, String fechaFinEA, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.eventoAdverso = eventoAdverso;
		this.fechaInicioEA = fechaInicioEA;
		this.fechaFinEA = fechaFinEA;
		this.style = style;
	}
	
	public Notificacion(String nombreSujeto, String eventoAdverso, String fechaInicioEA, String fechaFinEA) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.eventoAdverso = eventoAdverso;
		this.fechaInicioEA = fechaInicioEA;
		this.fechaFinEA = fechaFinEA;
	}

	public String getEventoAdverso() {
		return eventoAdverso;
	}

	public void setEventoAdverso(String eventoAdverso) {
		this.eventoAdverso = eventoAdverso;
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
