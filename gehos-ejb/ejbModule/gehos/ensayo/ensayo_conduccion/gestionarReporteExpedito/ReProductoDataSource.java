package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;

public class ReProductoDataSource {
	
	private String producto;
	private String lote;
	private String dosis;
	private String frecuencia;
	private String via;
	private String fecha;
	private String hora;
	
	
	public ReProductoDataSource(String producto, String lote, String dosis,
			String frecuencia, String via, String fecha, String hora) {
		super();
		this.producto = producto;
		this.lote = lote;
		this.dosis = dosis;
		this.frecuencia = frecuencia;
		this.via = via;
		this.fecha = fecha;
		this.hora = hora;
	}
	
	public ReProductoDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getDosis() {
		return dosis;
	}

	public void setDosis(String dosis) {
		this.dosis = dosis;
	}

	public String getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}

	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}
	

}
