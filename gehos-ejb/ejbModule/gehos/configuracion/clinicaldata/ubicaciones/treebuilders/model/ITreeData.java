package gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model;

public interface ITreeData {
	public boolean isExpanded();

	public void setExpanded(boolean expanded);

	public Object getValue();

	public void setValue(Object val);

	public Long getId();
}
