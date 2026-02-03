package gehos.comun.procesos.treebuilders.model;


public interface ITreeData {
	public boolean isExpanded();
	public void setExpanded(boolean expanded);
	public Object getValue(); 	
	public void setValue(Object value); 	
}
