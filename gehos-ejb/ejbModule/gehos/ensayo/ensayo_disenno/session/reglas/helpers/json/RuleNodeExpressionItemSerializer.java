package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RuleNodeExpressionItemSerializer extends JsonSerializer<RuleExpressionItem> 
{

	@Override
	public void serialize(RuleExpressionItem object, JsonGenerator jsonGenerator,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException 
			{
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("label", object.getLabel());		
				jsonGenerator.writeStringField("type", object.getType());
				
				if(object.getValue() instanceof Variable_ensayo)//Should Variable_ensayo class has its own serializer???? 
				{
					Variable_ensayo var = (Variable_ensayo)object.getValue();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", var.getId());
					jsonGenerator.writeObjectField("value", map);
				}
				else
				{
					jsonGenerator.writeObjectField("value", object.getValue());
				}
								
				jsonGenerator.writeEndObject();

			}
	
	
	

}
