package gehos.bitacora.treebuilders.model;

import gehos.bitacora.entity.TrazaAccion;

import org.jboss.seam.annotations.Name;

@Name("accionRealizadaWrapper")
public class AccionRealizadaWrapper implements ITreeData {
	
	private boolean expanded;
	private TrazaAccion value;
	
	public AccionRealizadaWrapper(boolean expanded, TrazaAccion value) {
		super();
		this.expanded = expanded;
		this.value = value;
	}

	public TrazaAccion getValue() {
		return value;
	}

	public void setValue(TrazaAccion value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String toString(){
		return "accion";
	}
}
