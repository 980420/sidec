package gehos.bitacora.treebuilders.model;

import gehos.bitacora.entity.TrazaSession;

public class SessionWrapper implements ITreeData {
	private boolean expanded;
	private TrazaSession value;
	
	public SessionWrapper(boolean expanded, TrazaSession value) {
		super();
		this.expanded = expanded;
		this.value = value;
	}

	public TrazaSession getValue() {
		return value;
	}

	public void setValue(TrazaSession value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String toString(){
		return "session";
	}
}
