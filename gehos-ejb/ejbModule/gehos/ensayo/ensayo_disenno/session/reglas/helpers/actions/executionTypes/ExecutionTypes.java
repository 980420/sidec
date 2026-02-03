package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.executionTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExecutionTypes 
{
	public static String PHASEFILL = "on_fill";
	public static String PHASESAVE = "on_save";
	public static String PHASEFILLSAVE = "on_fill_save";
	
	/**
	 * Special phase (it is when is necessary to restore the html state of some components)
	 */
	public static String PHASE_RESTORE_STATE = "on_restore_state";
	
	public static String ONKEY = "onkeypress";
	public static String ONBLUR = "onblur";
	public static String ONSELECT = "onselect";
	public static String ONCHANGE = "onchange";
	
	public static Map<String, String> GetPhases()
	{
		Map<String, String> types = new HashMap<String, String>();
		types.put("Al llenar", PHASEFILL);
		types.put("Al guardar",PHASESAVE);
		//types.put("Al llenar/guardar",PHASEFILLSAVE);
		
		return types;		
	}
	
	public static String GetPhaseByKey(String name)
	{
		return GetPhases().get(name);
	}
	
	public static String GetPhaseByValue(String name)
	{
		Map<String,String> phases = GetPhases();
		for (Entry<String, String> e : phases.entrySet()) {
			if(e.getValue().equals(name))
				return e.getKey();
		}
		return null;
	}
	
		
	public static Map<String, String> GetEvents()
	{
		Map<String, String> types = new HashMap<String, String>();
		types.put("Cuando se escribe", ONKEY);
		types.put("Cuando se sale",ONBLUR);
		types.put("Cuando se selecciona",ONSELECT);
		//types.put("Al llenar/guardar",ONCHANGE);			
		
		return types;		
	}
	
	public static String GetEventByKey(String name)
	{
		return GetEvents().get(name);
	}
	
	public static String GetEventByValue(String name)
	{
		Map<String,String> events = GetEvents();
		for (Entry<String, String> e : events.entrySet()) {
			if(e.getValue().equals(name))
				return e.getKey();
		}
		return null;
	}
}
