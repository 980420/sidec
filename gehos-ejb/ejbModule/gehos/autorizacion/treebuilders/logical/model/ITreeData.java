package gehos.autorizacion.treebuilders.logical.model;

import java.util.List;


public interface ITreeData {
	public boolean isExpanded();
	public void setExpanded(boolean expanded);
	public List<ITreeData> getRootToNodePath();
	public void setRootToNodePath(List<ITreeData> rootToNodePath);
	public Long getId();
	public String getModuleFatherName();
	public void setModuleFatherName(String moduleFather);
	public Object getValue();
	public void setValue(Object find);
}
