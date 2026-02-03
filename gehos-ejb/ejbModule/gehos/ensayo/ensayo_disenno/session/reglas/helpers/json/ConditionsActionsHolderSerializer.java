package gehos.ensayo.ensayo_disenno.session.reglas.helpers.json;


import java.io.IOException;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.ConditionsActionsHolder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ConditionsActionsHolderSerializer extends JsonSerializer<ConditionsActionsHolder>
{

	@Override
	public void serialize(ConditionsActionsHolder holder, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException 
	{
		jsonGenerator.writeStartObject();
		jsonGenerator.writeObjectField("conditions", holder.getNode());
		jsonGenerator.writeObjectField("actions", holder.getActions());
		jsonGenerator.writeEndObject();		
	}

}
