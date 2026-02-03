package gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model;


public interface ITreeData {
	public boolean isExpanded();
	public void setExpanded(boolean expanded);
	public Object getValue(); 	
	public void setValue(Object value); 	
	public Long getId(); 
	public Long getEntidadID();
}
