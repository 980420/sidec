package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;

public class ReVacunasDataSource {
	
	private String productoV;
	private String presentacionV;
	private String loteV;
	private String dosisV;
	private String frecuenciaV;
	private String viaV;
	private String fechaV;
	private String horaV;
	
	
	public ReVacunasDataSource(String productoV, String presentacionV,
			String loteV, String dosisV, String frecuenciaV, String viaV,
			String fechaV, String horaV) {
		super();
		this.productoV = productoV;
		this.presentacionV = presentacionV;
		this.loteV = loteV;
		this.dosisV = dosisV;
		this.frecuenciaV = frecuenciaV;
		this.viaV = viaV;
		this.fechaV = fechaV;
		this.horaV = horaV;
	}
	
	public ReVacunasDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getProductoV() {
		return productoV;
	}

	public void setProductoV(String productoV) {
		this.productoV = productoV;
	}

	public String getPresentacionV() {
		return presentacionV;
	}

	public void setPresentacionV(String presentacionV) {
		this.presentacionV = presentacionV;
	}

	public String getLoteV() {
		return loteV;
	}

	public void setLoteV(String loteV) {
		this.loteV = loteV;
	}

	public String getDosisV() {
		return dosisV;
	}

	public void setDosisV(String dosisV) {
		this.dosisV = dosisV;
	}

	public String getFrecuenciaV() {
		return frecuenciaV;
	}

	public void setFrecuenciaV(String frecuenciaV) {
		this.frecuenciaV = frecuenciaV;
	}

	public String getViaV() {
		return viaV;
	}

	public void setViaV(String viaV) {
		this.viaV = viaV;
	}

	public String getFechaV() {
		return fechaV;
	}

	public void setFechaV(String fechaV) {
		this.fechaV = fechaV;
	}

	public String getHoraV() {
		return horaV;
	}

	public void setHoraV(String horaV) {
		this.horaV = horaV;
	}

}
