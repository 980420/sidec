package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;

public class ReFarmacosDataSource {
	
	private String farmaco;
	private String dosis;
	private String frecuencia;
	private String via;
	private String fecha;
	private String hora;
		
	public ReFarmacosDataSource(String farmaco, String dosis,
			String frecuencia, String via, String fecha, String hora) {
		super();
		this.farmaco = farmaco;
		this.dosis = dosis;
		this.frecuencia = frecuencia;
		this.via = via;
		this.fecha = fecha;
		this.hora = hora;
	}

	public ReFarmacosDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFarmaco() {
		return farmaco;
	}

	public void setFarmaco(String farmaco) {
		this.farmaco = farmaco;
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
