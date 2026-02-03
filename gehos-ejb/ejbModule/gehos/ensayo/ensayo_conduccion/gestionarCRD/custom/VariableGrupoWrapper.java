package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;


import java.util.ArrayList;
import java.util.List;

import gehos.ensayo.entity.Variable_ensayo;

public class VariableGrupoWrapper {
	
	private Variable_ensayo variable;
	private List<ValorVariable> listaValores;
	Boolean creando;
	
	public VariableGrupoWrapper(Variable_ensayo variable) {
		super();
		this.variable = variable;
		listaValores = new ArrayList<ValorVariable>();
		listaValores.add(new ValorVariable());
		creando = true;
	}

	public Variable_ensayo getVariable() {
		return variable;
	}

	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}

	public List<ValorVariable> getListaValores() {
		List<ValorVariable> salida = new ArrayList<ValorVariable>();
		for (ValorVariable valorVariable : listaValores) {
			if(!valorVariable.isYaesta()){
				salida.add(valorVariable);
			}
			
			for (ValorVariable vv : salida) {
				if (vv.isEliminado()) {
					salida.remove(vv);
					valorVariable.setYaesta(true);
					break;
				}
			}
		}
		
		
		return salida;
	}
	
	public List<ValorVariable> getListaValoresGuardar() {
		return listaValores;
	}

	public void setListaValores(List<ValorVariable> listaValores) {
		this.listaValores = listaValores;
	}

	public Boolean getCreando() {
		return creando;
	}

	public void setCreando(Boolean creando) {
		if(!creando){
			if (listaValores.size() > 0 && listaValores.get(0).valor == null && listaValores.get(0).valores == null) {
				listaValores.remove(0);
			}
		}
		
		this.creando = creando;
	}		
}
