package gehos.comun.procesos.treebuilders.model;

public class ReglasProcesoWrapper implements ITreeData {
	private String type = "reglas";
	private ReglasProceso value;
	private boolean expanded = false;

	public String rulePath() {
		return "/business_processes/" + value.getModulo() + "/"
				+ value.getNombreProceso() + "/revision-"
				+ value.getNumeroRevision() + "/processrules.drl";
	}

	public ReglasProceso getValue() {
		return value;
	}

	public void setValue(ReglasProceso value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ReglasProcesoWrapper(ReglasProceso value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "reglas";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return new Long(0);
	}

	public void setValue(Object value) {
		this.value = (ReglasProceso) value;
	}

}
