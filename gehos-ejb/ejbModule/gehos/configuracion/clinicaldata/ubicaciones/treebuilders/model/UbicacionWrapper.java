package gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model;

import gehos.configuracion.management.entity.Ubicacion_configuracion;

public class UbicacionWrapper implements ITreeData {
	private String type = "ubicacion";
	private Ubicacion_configuracion value;
	private boolean expanded = false;

	public Ubicacion_configuracion getValue() {
		return value;
	}

	public void setValue(Ubicacion_configuracion value) {
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = (Ubicacion_configuracion) value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public UbicacionWrapper(Ubicacion_configuracion value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "ubicacion";
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
