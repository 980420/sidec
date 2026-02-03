package gehos.ensayo.ensayo_estadisticas.reporteModelosFirmados;

public class ModeloFirmado {
	private Integer cantidadFirmados, primeraFirma;
	private String style;
	
	public ModeloFirmado(Integer cantidadFirmados, Integer primeraFirma) {
		super();
		this.cantidadFirmados = cantidadFirmados;
		this.primeraFirma = primeraFirma;
	}
	public ModeloFirmado(Integer cantidadFirmados, Integer primeraFirma,
			String style) {
		super();
		this.cantidadFirmados = cantidadFirmados;
		this.primeraFirma = primeraFirma;
		this.style = style;
	}
	public Integer getCantidadFirmados() {
		return cantidadFirmados;
	}
	public void setCantidadFirmados(Integer cantidadFirmados) {
		this.cantidadFirmados = cantidadFirmados;
	}
	public Integer getPrimeraFirma() {
		return primeraFirma;
	}
	public void setPrimeraFirma(Integer primeraFirma) {
		this.primeraFirma = primeraFirma;
	}
	
}
