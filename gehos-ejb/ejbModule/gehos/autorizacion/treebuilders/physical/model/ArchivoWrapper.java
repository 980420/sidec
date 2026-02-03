package gehos.autorizacion.treebuilders.physical.model;

import java.io.File;

public class ArchivoWrapper implements ITreeData{

	private String type = "archivo";
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
	public ArchivoWrapper(File value, boolean expanded, String virtualPath) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.virtualPath = virtualPath;
	}
	@Override
	public String toString() {
		return "archivo";
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
