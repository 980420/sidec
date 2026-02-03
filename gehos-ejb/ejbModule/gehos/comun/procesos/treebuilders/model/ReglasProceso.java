package gehos.comun.procesos.treebuilders.model;

public class ReglasProceso {

	public ReglasProceso(String nombreProceso, String modulo, Integer numeroRev) {
		super();
		this.nombreProceso = nombreProceso;
		this.modulo = modulo;
		this.numeroRevision = numeroRev;
	}

	public String toString() {
		return "reglas";
	}

	private String modulo;
	private String nombreProceso;
	private Integer numeroRevision;

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
