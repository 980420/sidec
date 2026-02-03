package gehos.ensayo.ensayo_estadisticas.session.reporte_causas;

public class CausaEnsayo {
	
	private String nombreUsuario;
	private String nombreEstudio;
	private String nombreGrupo;
	private String nombreSujeto;	
	private String nombreMomento;
	private String nombreCrd;
	private String descripcion;
	private String tipoCausa;	
	private String fecha;
	
	public CausaEnsayo(String nombreUsuario, String nombreEstudio,
			String nombreGrupo, String nombreSujeto, String nombreMomento,
			String nombreCrd, String descripcion, String tipoCausa, String fecha) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.nombreEstudio = nombreEstudio;
		this.nombreGrupo = nombreGrupo;
		this.nombreSujeto = nombreSujeto;
		this.nombreMomento = nombreMomento;
		this.nombreCrd = nombreCrd;
		this.descripcion = descripcion;
		this.tipoCausa = tipoCausa;
		this.fecha = fecha;
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public String getNombreSujeto() {
		return nombreSujeto;
	}

	public String getNombreMomento() {
		return nombreMomento;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getTipoCausa() {
		return tipoCausa;
	}

	public String getFecha() {
		return fecha;
	}

	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}

	public void setNombreMomento(String nombreMomento) {
		this.nombreMomento = nombreMomento;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setTipoCausa(String tipoCausa) {
		this.tipoCausa = tipoCausa;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public String getNombreEstudio() {
		return nombreEstudio;
	}

	public String getNombreCrd() {
		return nombreCrd;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public void setNombreEstudio(String nombreEstudio) {
		this.nombreEstudio = nombreEstudio;
	}

	public void setNombreCrd(String nombreCrd) {
		this.nombreCrd = nombreCrd;
	}
}
