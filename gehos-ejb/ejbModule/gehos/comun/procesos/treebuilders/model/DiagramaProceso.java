package gehos.comun.procesos.treebuilders.model;

public class DiagramaProceso {

	public DiagramaProceso(String nombreProceso, String modulo,
			Integer numeroRev, Boolean tieneReglas) {
		super();
		this.nombreProceso = nombreProceso;
		this.modulo = modulo;
		this.numeroRevision = numeroRev;
		this.tieneReglas = tieneReglas;
	}

	public String toString() {
		return "diagrama";
	}

	private String modulo;
	private String nombreProceso;
	private Integer numeroRevision;
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

	public Integer getNumeroRevision() {
		return numeroRevision;
	}

	public void setNumeroRevision(Integer numeroRevision) {
		this.numeroRevision = numeroRevision;
	}

}
