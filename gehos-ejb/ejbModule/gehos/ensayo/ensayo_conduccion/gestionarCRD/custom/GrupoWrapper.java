package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import java.util.ArrayList;
import java.util.List;

import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.Variable_ensayo;


public class GrupoWrapper {
	
	private GrupoVariables_ensayo grupoVariables;
	private List<VariableGrupoWrapper> listaVarWrapper;

	public GrupoWrapper(GrupoVariables_ensayo grupoVariables) {
		super();
		this.grupoVariables = grupoVariables;
		this.listaVarWrapper = new ArrayList<VariableGrupoWrapper>();
		
		for (Variable_ensayo variable : grupoVariables.getVariables()) {
			VariableGrupoWrapper var = new VariableGrupoWrapper(variable);
			listaVarWrapper.add(var);
		}		
	}

	public GrupoVariables_ensayo getGrupoVariables() {
		return grupoVariables;
	}

	public void setGrupoVariables(GrupoVariables_ensayo grupoVariables) {
		this.grupoVariables = grupoVariables;
	}

	public List<VariableGrupoWrapper> getListaVarWrapper() {
		return listaVarWrapper;
	}

	public void setListaVarWrapper(List<VariableGrupoWrapper> listaVarWrapper) {
		this.listaVarWrapper = listaVarWrapper;
	}
}
