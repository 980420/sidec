package gehos.comun.procesos.treebuilders.model;

public class DefinicionProcesoWrapper implements ITreeData {
	private String type = "definicion";
	private DefinicionProceso value;
	private boolean expanded = false;

	public DefinicionProceso getValue() {
		return value;
	}

	public void setValue(DefinicionProceso value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public DefinicionProcesoWrapper(DefinicionProceso value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "definicion";
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
		this.value = (DefinicionProceso) value;
	}

}
