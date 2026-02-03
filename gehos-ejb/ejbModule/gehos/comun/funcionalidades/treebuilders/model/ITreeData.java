package gehos.comun.funcionalidades.treebuilders.model;

import gehos.comun.funcionalidades.entity.Funcionalidad;

public interface ITreeData {
	public boolean isExpanded();
	public void setExpanded(boolean expanded);
	public Funcionalidad getValue(); 	
	public void setValue(Funcionalidad func); 	
	public Long getId(); 	
}
