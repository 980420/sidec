package gehos.comun.procesos.treebuilders.model;

public class DiagramaProcesoWrapper implements ITreeData {
	private String type = "diagrama";
	private DiagramaProceso value;
	private boolean expanded = false;

	public DiagramaProceso getValue() {
		return value;
	}

	public void setValue(DiagramaProceso value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public DiagramaProcesoWrapper(DiagramaProceso value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "diagrama";
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
		this.value = (DiagramaProceso) value;
	}

}
