package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode.RuleNodeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.component.javasign1.xml.refs.ObjectToSign;
import org.jboss.seam.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class IRuleActionJsonSerializer extends JsonSerializer<IRuleAction> 
{

	@Override
	public void serialize(IRuleAction object, JsonGenerator jsonGenerator,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException 
			{
				jsonGenerator.writeStartObject();	
				String clazz = object.getClass().getName();
				if(clazz.contains("_$$"))
				{
					clazz = clazz.split("_[$]")[0];
				}
				jsonGenerator.writeStringField("class", clazz);
				jsonGenerator.writeBooleanField("execiftrue", object.getExecuteIfRuleTrue());
				jsonGenerator.writeObjectField("state", object.GetComponentState());				
				jsonGenerator.writeEndObject();

			}
	
	
	

}
