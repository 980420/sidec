package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeExpressionValueDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeExpressionValueSerializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeJsonDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeJsonSerializer;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = RuleNodeExpressionValueSerializer.class)
@JsonDeserialize(using = RuleNodeExpressionValueDeserializer.class)
public class RuleExpressionValue 
{
	private List<RuleExpressionItem> target;	
	
	@Override
	public String toString() {
		
		String exp = "";
		for (RuleExpressionItem r : target) {
			exp+=r.getLabel() + " ";
		}
		return exp.trim();
	}
	
	public RuleExpressionValue(List<RuleExpressionItem> target) {		
		this.target = target;
	}

	public List<RuleExpressionItem> getTarget() {
		return target;
	}

	public void setTarget(List<RuleExpressionItem> target) {
		this.target = target;
	}
	
	
}
