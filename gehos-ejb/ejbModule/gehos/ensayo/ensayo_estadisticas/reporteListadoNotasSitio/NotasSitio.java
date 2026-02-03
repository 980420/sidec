package gehos.ensayo.ensayo_estadisticas.reporteListadoNotasSitio;

public class NotasSitio {
	private String nombreSujeto;
	private String fechaCreacionNota;
	private String nombreCRD;
	private String nombreVariable;
	private String valorVariable;
	private String descripcionNota;
	private String style;
	
	public NotasSitio(String nombreSujeto, String fechaCreacionNota,
			String nombreCRD, String nombreVariable, String valorVariable,
			String descripcionNota,
			String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaCreacionNota = fechaCreacionNota;
		this.nombreCRD = nombreCRD;
		this.nombreVariable = nombreVariable;
		this.valorVariable = valorVariable;
		this.descripcionNota = descripcionNota;
		this.style = style;
	}
	
	public NotasSitio(String nombreSujeto, String fechaCreacionNota,
			String nombreCRD, String nombreVariable, 
			String valorVariable,
			String descripcionNota,
			int numeroNotas) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaCreacionNota = fechaCreacionNota;
		this.nombreCRD = nombreCRD;
		this.nombreVariable = nombreVariable;
		this.valorVariable = valorVariable;
		this.descripcionNota = descripcionNota;
	}
	
	public String getNombreSujeto() {
		return nombreSujeto;
	}

	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}

	public String getFechaCreacionNota() {
		return fechaCreacionNota;
	}

	public void setFechaCreacionNota(String fechaCreacionNota) {
		this.fechaCreacionNota = fechaCreacionNota;
	}

	public String getNombreCRD() {
		return nombreCRD;
	}

	public void setNombreCRD(String nombreCRD) {
		this.nombreCRD = nombreCRD;
	}

	public String getNombreVariable() {
		return nombreVariable;
	}

	public void setNombreVariable(String nombreVariable) {
		this.nombreVariable = nombreVariable;
	}

	public String getValorVariable() {
		return valorVariable;
	}

	public void setValorVariable(String valorVariable) {
		this.valorVariable = valorVariable;
	}

	public String getDescripcionNota() {
		return descripcionNota;
	}

	public void setDescripcionNota(String descripcionNota) {
		this.descripcionNota = descripcionNota;
	}
	
	
}
