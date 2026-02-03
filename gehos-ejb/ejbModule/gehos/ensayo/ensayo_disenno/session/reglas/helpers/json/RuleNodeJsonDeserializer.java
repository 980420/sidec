package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleExpressionValue;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode.RuleNodeType;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.component.javasign1.xml.refs.ObjectToSign;
import org.jboss.seam.Component;

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

public class RuleNodeJsonDeserializer extends JsonDeserializer<RuleNode> 
{
	
	@Override
	public RuleNode deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException 
			{						
				
				ObjectCodec oc = jsonParser.getCodec();
		        JsonNode node = oc.readTree(jsonParser);
		        
		        RuleNode rulenode = ReadRuleNodeFrom(node,null, jsonParser);	        
		       
		        return rulenode;              		
			}
	
	private RuleNode ReadRuleNodeFrom(JsonNode node, RuleNode parent, JsonParser jsonParser) throws JsonProcessingException, IOException
	{
			RuleNode rulenode = new RuleNode();
			rulenode.setParent(parent);
			RuleNodeType nodeType = RuleNodeType.valueOf((node.get("type").asText()));
			int nodeid = node.get("nodeid").asInt();
			
	        rulenode.setId(nodeid);	        
	        if(nodeid==0)
	        {
	        	int lastGivenId = node.get("lastgivenid").asInt();
	        	rulenode.setLastGivenId(lastGivenId);     
	        }
	        
	        if(nodeType == RuleNodeType.Expression)
	        {
	        	EntityManager em = (EntityManager) Component.getInstance("entityManager");
	        	Variable_ensayo variable = em.find(Variable_ensayo.class, node.get("varid").asLong());
	        	rulenode.setVariable(variable);
	        	
	        	rulenode.setOperator(node.get("opname").asText());	        		        	
	        	Object value = null;
	        	JsonNode nodeValue = node.get("value");
	        	if(nodeValue.isObject())
	        	{
	        		ObjectCodec oc = jsonParser.getCodec();
	        		JsonParser parser = nodeValue.traverse();
	        		parser.setCodec(oc);
	        		value = oc.readValue(parser, RuleExpressionValue.class);
	        	}
	        	else
	        	{
	        		value = node.get("value").asText();
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
