package gehos.autorizacion.treebuilders.logical.model;

import java.util.List;

import gehos.configuracion.management.entity.Entidad_configuracion;

public class EntidadWrapper implements ITreeData {
	private String type = "entidad";
	private Entidad_configuracion value;
	private boolean expanded = false;
	private Long entidadID;

	public Entidad_configuracion getValue() {
		return value;
	}

	public void setValue(Entidad_configuracion value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public EntidadWrapper(Entidad_configuracion value, boolean expanded,
			Long entidadId) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.entidadID = entidadId;
	}

	@Override
	public String toString() {
		return "entidad";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return this.value.getId();
	}

	public Long getEntidadID() {
		return entidadID;
	}

	public void setEntidadID(Long entidadID) {
		this.entidadID = entidadID;
	}

	public void setValue(Object value) {
		this.value = (Entidad_configuracion) value;
	}

	public String getModuleFatherName() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ITreeData> getRootToNodePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setModuleFatherName(String moduleFather) {
		// TODO Auto-generated method stub
		
	}

	public void setRootToNodePath(List<ITreeData> rootToNodePath) {
		// TODO Auto-generated method stub
		
	}

}
