package gehos.autorizacion.treebuilders.logical.model;

import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.util.List;

public class ModuloWrapper implements ITreeData {
	private String type = "modulo";
	private Funcionalidad value;
	private boolean expanded = false;
	private List<ITreeData> rootToNodePath;
	private String moduleFatherName;
	private Long entidadID;

	public Long getEntidadID() {
		return entidadID;
	}

	public void setEntidadID(Long entidadID) {
		this.entidadID = entidadID;
	}

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

	public ModuloWrapper(Funcionalidad value, boolean expanded,
			List<ITreeData> rootToNodePath, Long entidadID) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.entidadID = entidadID;
		// this.rootToNodePath = rootToNodePath;
	}

	@Override
	public String toString() {
		return "modulo";
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
		return this.value.getNombre();
	}

	public void setModuleFatherName(String moduleFatherId) {
		this.moduleFatherName = moduleFatherId;
	}

	public void setModuloUnico(boolean b) {

	}

}
