package gehos.autorizacion.treebuilders.logical.model;

import java.util.List;

import gehos.comun.funcionalidades.entity.Funcionalidad;

public class FuncionalidadWrapper implements ITreeData {
	private String type = "funcionalidad";
	private Funcionalidad value;
	private boolean expanded = false;
	private List<ITreeData> rootToNodePath;
	private String moduleFatherName;

	public Funcionalidad getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = (Funcionalidad) value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public FuncionalidadWrapper(Funcionalidad value, boolean expanded,
			List<ITreeData> rootToNodePath, String moduleFatherId) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.rootToNodePath = rootToNodePath;
		this.moduleFatherName = moduleFatherId;
	}

	@Override
	public String toString() {
		return "funcionalidad";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ITreeData> getRootToNodePath() {
		return rootToNodePath;
	}

	public void setRootToNodePath(List<ITreeData> rootToNodePath) {
		this.rootToNodePath = rootToNodePath;
	}

	public Long getId() {
		return this.value.getId();
	}

	public String getModuleFatherName() {
		return moduleFatherName;
	}

	public void setModuleFatherName(String moduleFatherId) {
		this.moduleFatherName = moduleFatherId;
	}
}
