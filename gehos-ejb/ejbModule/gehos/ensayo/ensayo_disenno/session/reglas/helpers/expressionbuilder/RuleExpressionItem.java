package gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeExpressionItemDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeExpressionValueDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeExpressionItemSerializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeExpressionValueSerializer;

import org.jboss.seam.annotations.Name;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = RuleNodeExpressionItemSerializer.class)
@JsonDeserialize(using = RuleNodeExpressionItemDeserializer.class)
public class RuleExpressionItem 
{
	String label;
	String type;
	Object value;
	
	
	
	
	public RuleExpressionItem(String label, String type,
			Object value) {
		super();
		this.label = label;
		this.type = type;
		this.value = value;
	}

	public RuleExpressionItem clone()
	{
		return new RuleExpressionItem(this.label, this.type, this.value);
	}
	



	public static class RuleExpressionItemType{
		public static String 
		Variable="var",
		Literal="lit",
		Operator="op",
		ParenthesesOpen="openparent",
		ParenthesesClose="closeparent";
	}




	public String getLabel() {
		return label;
	}




	public void setLabel(String label) {
		this.label = label;
	}




	




	public Object getValue() {
		return value;
	}




	public void setValue(Object value) {
		this.value = value;
	}




	public String getType() {
		return type;
	}




	public void setType(String type) {
		this.type = type;
	}
}

