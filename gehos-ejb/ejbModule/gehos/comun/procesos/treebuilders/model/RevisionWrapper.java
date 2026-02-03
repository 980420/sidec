package gehos.comun.procesos.treebuilders.model;


public class RevisionWrapper implements ITreeData {
	private String type = "revision";
	private Revision value;
	private boolean expanded = false;

	public Revision getValue() {
		return value;
	}

	public void setValue(Revision value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public RevisionWrapper(Revision value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "revision";
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
		this.value = (Revision) value;
	}

}
