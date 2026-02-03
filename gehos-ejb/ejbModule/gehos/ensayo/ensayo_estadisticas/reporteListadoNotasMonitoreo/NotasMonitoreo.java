package gehos.ensayo.ensayo_estadisticas.reporteListadoNotasMonitoreo;

public class NotasMonitoreo {
	private String nombreSujeto;
	private String fechaCreacionNota;
	private String fechaActualizacionNota;
	private String fechaMS;
	private String estadoMS;
	private String nombreCRD;
	private String nombreVariable;
	private String valorVariable;
	private String descripcionNota;
	private String detallesNota;
	private String estadoNota;
	private int numeroNotas;
	private String style;
	
	public NotasMonitoreo(String nombreSujeto, String fechaCreacionNota,
			String fechaActualizacionNota, String fechaMS, String estadoMS,
			String nombreCRD, String nombreVariable, String valorVariable,
			String descripcionNota, String detallesNota, String estadoNota,
			int numeroNotas, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaCreacionNota = fechaCreacionNota;
		this.fechaActualizacionNota = fechaActualizacionNota;
		this.fechaMS = fechaMS;
		this.estadoMS = estadoMS;
		this.nombreCRD = nombreCRD;
		this.nombreVariable = nombreVariable;
		this.valorVariable = valorVariable;
		this.descripcionNota = descripcionNota;
		this.detallesNota = detallesNota;
		this.estadoNota = estadoNota;
		this.numeroNotas = numeroNotas;
		this.style = style;
	}
	
	public NotasMonitoreo(String nombreSujeto, String fechaCreacionNota,
			String fechaActualizacionNota, String fechaMS, String estadoMS,
			String nombreCRD, String nombreVariable, String valorVariable,
			String descripcionNota, String detallesNota, String estadoNota,
			int numeroNotas) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.fechaCreacionNota = fechaCreacionNota;
		this.fechaActualizacionNota = fechaActualizacionNota;
		this.fechaMS = fechaMS;
		this.estadoMS = estadoMS;
		this.nombreCRD = nombreCRD;
		this.nombreVariable = nombreVariable;
		this.valorVariable = valorVariable;
		this.descripcionNota = descripcionNota;
		this.detallesNota = detallesNota;
		this.estadoNota = estadoNota;
		this.numeroNotas = numeroNotas;
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

	public String getFechaActualizacionNota() {
		return fechaActualizacionNota;
	}

	public void setFechaActualizacionNota(String fechaActualizacionNota) {
		this.fechaActualizacionNota = fechaActualizacionNota;
	}

	public String getFechaMS() {
		return fechaMS;
	}

	public void setFechaMS(String fechaMS) {
		this.fechaMS = fechaMS;
	}

	public String getEstadoMS() {
		return estadoMS;
	}

	public void setEstadoMS(String estadoMS) {
		this.estadoMS = estadoMS;
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

	public String getDetallesNota() {
		return detallesNota;
	}

	public void setDetallesNota(String detallesNota) {
		this.detallesNota = detallesNota;
	}

	public String getEstadoNota() {
		return estadoNota;
	}

	public void setEstadoNota(String estadoNota) {
		this.estadoNota = estadoNota;
	}

	public int getNumeroNotas() {
		return numeroNotas;
	}

	public void setNumeroNotas(int numeroNotas) {
		this.numeroNotas = numeroNotas;
	}	
}
