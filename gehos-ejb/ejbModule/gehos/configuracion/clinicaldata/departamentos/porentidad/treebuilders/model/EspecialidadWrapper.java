package gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model;

import gehos.configuracion.management.entity.Especialidad_configuracion;

public class EspecialidadWrapper implements ITreeData {
	private String type = "especialidad";
	private Especialidad_configuracion value;
	private boolean expanded = false;
	private Long entidadID;
	private Boolean existia;
	private Boolean existe;
	private Boolean visible;

	public Especialidad_configuracion getValue() {
		return value;
	}

	public void setValue(Especialidad_configuracion value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public EspecialidadWrapper(Especialidad_configuracion value,
			boolean expanded, Long entidadId) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.entidadID = entidadId;
	}

	@Override
	public String toString() {
		return "especialidad";
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
		this.value = (Especialidad_configuracion) value;
	}

	public Boolean getExiste() {
		return existe;
	}

	public void setExiste(Boolean existe) {
		this.existe = existe;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getExistia() {
		return existia;
	}

	public void setExistia(Boolean existia) {
		this.existia = existia;
	}

}
