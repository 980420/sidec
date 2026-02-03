package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.ConditionsActionsHolderSerializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.IRuleActionJsonDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.IRuleActionJsonSerializer;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = IRuleActionJsonSerializer.class)
@JsonDeserialize(using = IRuleActionJsonDeserializer.class)
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class IRuleAction 
{
	boolean executeIfRuleTrue;
	String componentName;
	Map<String,Object> state;
	boolean forGroup = false;
	/**
	 * The variable for which the rule was created. So far the only action that needs it is Notify
	 */
	Variable_ensayo rootVariable;
	
	public Variable_ensayo getRootVariable() {
		return rootVariable;
	}
	public void setRootVariable(Variable_ensayo rootVariable) {
		this.rootVariable = rootVariable;
	}
	public String getName(){return null;}	
	public String getLabel(){return null;}
	public String getDescription(){return null;}
	public boolean Execute(){return false;}
	public void setParams(Object... objects){}	
	public void setComponentState(Map<String,Object> state){this.state = state;LoadComponentState();}
	public void LoadComponentState(){}
	public Map<String,Object> GetComponentState(){return null;}
	public void setDBState(Map<String,Object> state){this.state = state;}
	public Map<String,Object> getDBState(){return state;}
	public String IdToRerender(){return null;}
	
	public boolean isForGroup(){
		return forGroup;
	}
	
	public void setForGroup(boolean forGroup){
		this.forGroup = forGroup;
	}
	
	public String InstanceDescription(){return null;}
	
	/**
	 * Perform additional checks before saving the rule
	 */
	public boolean Validate(){
		return true;
	}
	
	/*This 3 methods are to consider in-group variables, this could be done with inheritance also, PERHAPS!!*/
	public Variable_ensayo TargetVariable(){return null;}
	public String AffectedAttribute(){return null;}
	public Object AffectedAttributeValue(){return null;}
	
	public int orderPosition(){return 1000;}
	
	/*
	 * To know what is the opposite action, if applicable
	 */
	public IRuleAction OppositeAction(){return null;}
	
	/*
	 * It is the JS code to be executed (essentially is a JS function without head, just the body. For instance: 
	 * function somename()
	 * {
	 * 		//WHAT GOES HERE
	 * })
	 */
	public String ClientSideCode(){return null;}
	/**
	 * Most actions do not need user input to complete, but there are use cases on this is necessary. For example, when opening a modal panel, how will
	 * I know if data was entered and saved?
	 * @return True if the action requires user input to complete. False, otherwise.
	 */
	public boolean RequiresUserInput(){return false;}
	
	/**
	 * It is necessary to restore (shown-hidden and disabled-enabled)
	 * @return
	 */
	public boolean HtmlState(){return false;}
	
	public String getViewPath()
	{
		String rootPath = "/modEnsayo/ensayo_disenno/codebase/reglas/actionviews/";
		String view = String.format("%s%s.xhtml",rootPath, this.getName());
		ServletContext context = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = context.getRealPath(view);
		File d = new File(realPath);
		if(!d.exists())
			return null;
		return view;
	}
	public String Resume(){return null;}	
	public Map getExtra(){return Collections.EMPTY_MAP;}		
	
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public boolean getExecuteIfRuleTrue() {
		return executeIfRuleTrue;
	}
	public void setExecuteIfRuleTrue(boolean executeIfRuleTrue) {
		this.executeIfRuleTrue = executeIfRuleTrue;
	}
	
	public String ParentComponentId(String childId)
	{
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIComponent formComponent  = viewRoot.findComponent(childId);		
		return formComponent.getParent().getParent().getId();
	}
	
}

