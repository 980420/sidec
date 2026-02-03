package gehos.bitacora.treebuilders.model;

import java.util.List;

import gehos.bitacora.entity.TrazaAtributoModificado;

import org.jboss.seam.annotations.Name;

@Name("atributoModificadoWrapper")
public class AtributoModificadoWrapper implements ITreeData {
	
	private boolean expanded;
	private List<TrazaAtributoModificado> value;
	
	public AtributoModificadoWrapper(boolean expanded, List<TrazaAtributoModificado> value) {
		super();
		this.expanded = expanded;
		this.value = value;
	}

	public List<TrazaAtributoModificado> getValue() {
		return value;
	}

	public void setValue(List<TrazaAtributoModificado> value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String toString(){
		return "atributos";
	}
}
