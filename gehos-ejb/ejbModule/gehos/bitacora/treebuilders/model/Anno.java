package gehos.bitacora.treebuilders.model;

public class Anno implements ITreeData {
	private Integer value;
	private boolean expanded = false;

	public Anno(Integer value) {
		super();
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public String toString(){
		return "anno";
	}

	public boolean isExpanded() {
		return this.expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
}
