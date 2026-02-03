package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("ruleNodeErrorManager")
@Scope(ScopeType.CONVERSATION)
public class RuleNodeErrorManager 
{
	Map<Integer, String> errorsMap;
	
	@Create
	public void init()
	{
		errorsMap = new HashMap<Integer, String>();
	}
	
	public boolean hasError(Integer nodeid)
	{
		return errorsMap.containsKey(nodeid)==true && !errorsMap.get(nodeid).equals("ok");
	}
	
	public boolean hasRequired(Integer nodeid)
	{
		return !errorsMap.containsKey(nodeid) || (errorsMap.containsKey(nodeid) && !errorsMap.get(nodeid).equals("ok"));
	}
	
	public String giveError(Integer nodeid)
	{
		return errorsMap.get(nodeid);
	}
	
	public void putError(Integer nodeid, String error)
	{		
		errorsMap.put(nodeid, error);		
	}
	
	public void removeError(Integer nodeid)
	{		
		errorsMap.put(nodeid,"ok");//should be removed but it's needed for the 'required'		
	}
}
