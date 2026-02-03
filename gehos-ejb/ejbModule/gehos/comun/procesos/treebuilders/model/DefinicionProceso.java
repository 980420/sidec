package gehos.comun.procesos.treebuilders.model;

public class DefinicionProceso {

	public DefinicionProceso(String nombreProceso, String modulo,
			Integer numeroRev, Boolean tieneReglas) {
		super();
		this.nombreProceso = nombreProceso;
		this.modulo = modulo;
		this.numeroRevision = numeroRev;
		this.tieneReglas = tieneReglas;
	}

	public DefinicionProceso() {
	}

	@Override
	public String toString() {
		return "definicion";
	}

	private String modulo;
	private String nombreProceso;
	private Integer numeroRevision;
	private Integer numeroInstancias;
	private Integer version;
	private Boolean tieneReglas;

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

	public Boolean getTieneReglas() {
		return tieneReglas;
	}

	public void setTieneReglas(Boolean tieneReglas) {
		this.tieneReglas = tieneReglas;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getNumeroInstancias() {
		return numeroInstancias;
	}

	public void setNumeroInstancias(Integer numeroInstancias) {
		this.numeroInstancias = numeroInstancias;
	}

}
