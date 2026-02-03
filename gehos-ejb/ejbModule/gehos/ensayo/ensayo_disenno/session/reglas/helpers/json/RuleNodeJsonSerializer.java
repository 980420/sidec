package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

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

public class RuleNodeJsonSerializer extends JsonSerializer<RuleNode> 
{

	@Override
	public void serialize(RuleNode object, JsonGenerator jsonGenerator,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException 
			{
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("type", object.getType().name());
				jsonGenerator.writeNumberField("nodeid", object.getId());
				if(object.getId()==0)
				{
					jsonGenerator.writeNumberField("lastgivenid", object.getLastGivenId());
				}
				
				if(object.getType() == RuleNodeType.Expression)
				{
					jsonGenerator.writeNumberField("varid", object.getVariable().getId());
					jsonGenerator.writeStringField("varcode", object.getVariable().getTipoDato().getCodigo());
					jsonGenerator.writeStringField("opname", object.getOperator());
					jsonGenerator.writeObjectField("value", object.getValue());
				}
				else
				{				
					List<RuleNode> childs = new ArrayList<RuleNode>();
					for (int i = 0; i < object.childCount(); i++) 
					{
						RuleNode child = object.child(i);
						childs.add(child);
					}							
					jsonGenerator.writeObjectField("childs", childs);	
					
				}
				jsonGenerator.writeEndObject();

			}
	
	
	

}
