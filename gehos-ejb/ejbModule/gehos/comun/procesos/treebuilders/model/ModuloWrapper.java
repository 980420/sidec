package gehos.comun.procesos.treebuilders.model;

import gehos.comun.funcionalidades.entity.Funcionalidad;

public class ModuloWrapper implements ITreeData {
	private String type = "modulo";
	private Funcionalidad value;
	private boolean expanded = false;
	private boolean moduloUnico = false;

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

	public ModuloWrapper(Funcionalidad value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
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

	public Long getId() {
		return this.value.getId();
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
