package gehos.comun.procesos.treebuilders.model;


public class ProcesoWrapper implements ITreeData {
	private String type = "proceso";
	private Proceso value;
	private boolean expanded = false;

	public Proceso getValue() {
		return value;
	}

	public void setValue(Proceso value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ProcesoWrapper(Proceso value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "proceso";
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
		this.value = (Proceso) value;
	}

}
