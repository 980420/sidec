package gehos.autorizacion.treebuilders.physical.model;

import java.io.File;

public class DirectoryWrapper implements ITreeData{

	private String type = "directorio";
	private String virtualPath;
	private File value;
	private boolean expanded = false;
	
	public File getValue() {
		return value;
	}
	public void setValue(File value) {
		this.value = value;
	}
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	public DirectoryWrapper(File value, boolean expanded, String virtualPath) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.virtualPath = virtualPath;
	}
	@Override
	public String toString() {
		return "directorio";
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVirtualPath() {
		return virtualPath;
	}
	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}
}
