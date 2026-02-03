package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class IRuleActionJsonDeserializer extends JsonDeserializer<IRuleAction> 
{

	@Override
	public IRuleAction deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException 
	{
		ObjectCodec oc = jsonParser.getCodec();
		IRuleAction ruleAction = null;
        JsonNode node = oc.readTree(jsonParser);
        try 
		{
        	
			String className = node.get("class").asText();
			boolean execiftrue = true;
			if(node.has("execiftrue"))
				execiftrue = node.get("execiftrue").asBoolean();
			node = node.get("state");
			JsonParser stateParser = node.traverse();
			Map<String, Object> map = (Map<String, Object>)oc.readValue(stateParser, Map.class);
		
		
			Class clazz = Class.forName(className);
			ruleAction= (IRuleAction)clazz.newInstance();
			ruleAction.setDBState(map);
			ruleAction.setExecuteIfRuleTrue(execiftrue);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ruleAction;
	}

}