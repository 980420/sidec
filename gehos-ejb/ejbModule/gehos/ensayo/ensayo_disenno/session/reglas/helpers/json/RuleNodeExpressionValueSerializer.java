package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleExpressionValue;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode.RuleNodeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.component.javasign1.xml.refs.ObjectToSign;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RuleNodeExpressionValueSerializer extends JsonSerializer<RuleExpressionValue> 
{

	@Override
	public void serialize(RuleExpressionValue object, JsonGenerator jsonGenerator,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException 
			{
				jsonGenerator.writeStartObject();
				jsonGenerator.writeObjectField("items", object.getTarget());
				jsonGenerator.writeEndObject();

			}
	
	
	

}
