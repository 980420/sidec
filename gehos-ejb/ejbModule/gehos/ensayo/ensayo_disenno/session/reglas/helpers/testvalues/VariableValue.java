package gehos.ensayo.ensayo_disenno.session.reglas.helpers.testvalues;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.FileSigec;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeJsonSerializer;
import gehos.ensayo.entity.Variable_ensayo;


public class VariableValue 
{
	Variable_ensayo variable;
	Object value;
	int variableReps = 1;//repetitions of the variable. This is to avoid tree visits
	
	public VariableValue(Variable_ensayo variable, Object value) {		
		this.variable = variable;
		this.setValue(value);
	}
	public Variable_ensayo getVariable() {
		return variable;
	}
	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}
	public Object getValue() {
		return value;
	}	
	public void setValue(Object value) {
		if(variable.getTipoDato().getCodigo().equals("FILE"))
		{
			try 
			{
				this.value = new ObjectMapper().readValue(value.toString(), FileSigec.class);
			} 
			catch (Exception e){
				e.printStackTrace();
			} 
		}
		else this.value = value;
	}
	public int getVariableReps() {
		return variableReps;
	}
	public void setVariableReps(int variableReps) {
		this.variableReps = variableReps;
	}
	
	public void IncreaseReps()
	{
		variableReps++;
	}
	public void DecreaseReps()
	{
		variableReps--;
	}
	@Override
	public String toString() {		
		return String.format("%s=%s [%d]", this.variable.getNombreVariable(),value,variableReps);
	}
}
