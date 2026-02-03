package gehos.comun.procesos.treebuilders.model;

public class Revision {
	public Revision(String nombreProceso, String modulo, Integer numero,
			Boolean tieneReglas) {
		super();
		this.nombreProceso = nombreProceso;
		this.modulo = modulo;
		this.numero = numero;
		this.tieneReglas = tieneReglas;
	}

	private String nombreProceso;
	private String modulo;
	private Integer numero;
	private Boolean tieneReglas;

	public Boolean getTieneReglas() {
		return tieneReglas;
	}

	public void setTieneReglas(Boolean tieneReglas) {
		this.tieneReglas = tieneReglas;
	}

	public String getNombreProceso() {
		return nombreProceso;
	}

	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

}
