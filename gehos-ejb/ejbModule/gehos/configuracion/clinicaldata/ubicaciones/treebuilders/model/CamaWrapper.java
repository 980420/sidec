package gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model;

import gehos.configuracion.management.entity.Cama_configuracion;

public class CamaWrapper implements ITreeData {
	private String type = "cama";
	private Cama_configuracion value;
	private boolean expanded = false;

	public Cama_configuracion getValue() {
		return value;
	}

	public void setValue(Cama_configuracion value) {
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = (Cama_configuracion) value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public CamaWrapper(Cama_configuracion value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "cama";
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
