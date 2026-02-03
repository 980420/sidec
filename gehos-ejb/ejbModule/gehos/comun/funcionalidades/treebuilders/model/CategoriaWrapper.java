package gehos.comun.funcionalidades.treebuilders.model;

import gehos.comun.funcionalidades.entity.Funcionalidad;


public class CategoriaWrapper implements ITreeData{
	private String type = "categoria";
	private Funcionalidad value;
	private boolean expanded = false;
	
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
	public CategoriaWrapper(Funcionalidad value, boolean expanded) {
		super();
		this.value = value;
		this.expanded = expanded;
	}
	@Override
	public String toString() {
		return "categoria";
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
