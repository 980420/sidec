package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;


import gehos.ensayo.ensayo_disenno.session.reglas.helpers.ConditionsActionsHolder;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleExpressionValue;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;

public class ConditionsActionsHolderDeserializer extends JsonDeserializer<ConditionsActionsHolder>
{

	@Override
	public ConditionsActionsHolder deserialize(JsonParser jsonParser,
			DeserializationContext arg1) throws IOException,
			JsonProcessingException 
	{
		ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        
        JsonParser conditionsParser = node.get("conditions").traverse();       
        conditionsParser.setCodec(oc);
        RuleNode ruleNode = oc.readValue(conditionsParser, RuleNode.class);
        
        List<IRuleAction> actions = Lists.newArrayList();
        ArrayNode arr = (ArrayNode)node.get("actions");
        for (JsonNode n : arr) 
        {
        	JsonParser parser = n.traverse();
        	parser.setCodec(oc);
        	IRuleAction action = oc.readValue(parser, IRuleAction.class);
        	actions.add(action);
		}   
        
        ConditionsActionsHolder h = new ConditionsActionsHolder();
        h.setNode(ruleNode);
        h.setActions(actions);
        
        return h;
        
	}

}
