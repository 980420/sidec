package gehos.comun.modulos.porentidad.treebuilders.model;

import gehos.comun.funcionalidades.entity.Funcionalidad;

public class ModuloWrapper implements ITreeData {
	private String type = "modulo";
	private Funcionalidad value;
	private boolean expanded = false;
	private boolean moduloUnico = false;
	private Long entidadID;

	public Funcionalidad getValue() {
		return value;
	}

	public void setValue(Funcionalidad value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ModuloWrapper(Funcionalidad value, boolean expanded,
			Long entidadId) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.entidadID = entidadId;
	}

	@Override
	public String toString() {
		return type;
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
		this.value = (Funcionalidad) value;
	}

	public boolean isModuloUnico() {
		return moduloUnico;
	}

	public void setModuloUnico(boolean moduloUnico) {
		this.moduloUnico = moduloUnico;
	}

}
