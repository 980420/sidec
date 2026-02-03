package gehos.bitacora.treebuilders.model;

import gehos.bitacora.entity.TrazaModuloAccedido;

import org.jboss.seam.annotations.Name;

@Name("moduloAccedidoWrapper")
public class ModuloAccedidoWrapper implements ITreeData {
	
	private boolean expanded;
	private TrazaModuloAccedido value;
	
	public ModuloAccedidoWrapper(boolean expanded, TrazaModuloAccedido value) {
		super();
		this.expanded = expanded;
		this.value = value;
	}

	public TrazaModuloAccedido getValue() {
		return value;
	}

	public void setValue(TrazaModuloAccedido value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String toString(){
		return "modulo";
	}
}
