package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import gehos.ensayo.entity.Variable_ensayo;

public class valorGrupoWraper{
	Variable_ensayo variable;
	String valor;
	
	
	public valorGrupoWraper(Variable_ensayo variable, String valor) {
		super();
		this.variable = variable;
		this.valor = valor;
	}


	public valorGrupoWraper() {
		super();
	}


	public Variable_ensayo getVariable() {
		return variable;
	}


	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}


	public String getValor() {
		return valor;
	}


	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
	
	
}
