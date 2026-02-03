package gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model;

import gehos.configuracion.management.entity.Entidad_configuracion;

public class EntidadWrapper implements ITreeData {
	private String type = "entidad";
	private Entidad_configuracion value;
	private boolean expanded = false;

	public Entidad_configuracion getValue() {
		return value;
	}

	public void setValue(Entidad_configuracion value) {
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = (Entidad_configuracion) value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public EntidadWrapper(Entidad_configuracion value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
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

}
