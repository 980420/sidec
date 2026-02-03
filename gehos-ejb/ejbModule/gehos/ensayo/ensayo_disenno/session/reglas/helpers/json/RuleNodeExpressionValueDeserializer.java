package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleExpressionValue;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode.RuleNodeType;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.component.javasign1.xml.refs.ObjectToSign;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RuleNodeExpressionValueDeserializer extends JsonDeserializer<RuleExpressionValue> 
{
	
	@Override
	public RuleExpressionValue deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException 
			{						
				
				ObjectCodec oc = jsonParser.getCodec();
		        JsonNode node = oc.readTree(jsonParser);
		        ArrayNode items = (ArrayNode)node.get("items");
		        List<RuleExpressionItem> reItems = new ArrayList<RuleExpressionItem>();
		        
		        for (JsonNode n : items) {
		        	JsonParser parser = n.traverse();
		        	parser.setCodec(oc);
		        	RuleExpressionItem i = oc.readValue(parser, RuleExpressionItem.class);					
					reItems.add(i);
				}  
		        		       
		        return new RuleExpressionValue(reItems);              		
			}
	
	private RuleNode ReadRuleNodeFrom(JsonNode node, RuleNode parent, JsonParser jsonParser) throws JsonProcessingException, IOException
	{
			RuleNode rulenode = new RuleNode();
			RuleNodeType nodeType = RuleNodeType.valueOf((node.get("type").asText()));
	        
	        if(nodeType == RuleNodeType.Expression)
	        {
	        	rulenode.setId((int)node.get("varid").asLong());
	        	rulenode.setOperator(node.get("opname").asText());
	        	
	        	Object value = null;
	        	if(node.get("value").isObject())
	        	{
	        		ObjectCodec oc = jsonParser.getCodec();
	        		value = oc.readValue(jsonParser, RuleExpressionValue.class);
	        	}
	        	else
	        	{
	        		value = node.get("value").toString();
	        	}
	        	
	        	rulenode.setValue(value);				
	        }
	        else
	        {
	        	ArrayNode childs = (ArrayNode)node.get("childs");
	        	for (JsonNode jsonNode : childs) 
	        	{
	        		RuleNode child = ReadRuleNodeFrom(jsonNode,rulenode,jsonParser);
	        		rulenode.childAdd(child);
				}
	        }
	        rulenode.setType(nodeType);
	        return rulenode;
	}
	

}
