package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions;

import gehos.ensayo.ensayo_disenno.session.reglas.AffectedAttributes;
import gehos.ensayo.ensayo_disenno.session.reglas.ReglaPlayer;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.entity.Variable_ensayo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.json.JSONObject;

@Name("EnableFieldRuleAction")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings({"unchecked","rawtypes"})
public class EnableFieldRuleAction extends IRuleAction
{

	@In
	FacesMessages facesMessages; 
	
	@In EntityManager entityManager;
	@In(create=true)
	IdUtil idUtil;
	
	Variable_ensayo variable;	
	public String getName() {
		return "enable_field";
	}
	public String getLabel() {
			return "Habilitar campo";
	}
	@Override
	public String getDescription() {
		return "Habilita el campo relacionado con la variable seleccionada";
	}
	@Override
	public boolean Execute() 
	{		
		/*UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIComponent formComponent  = viewRoot.findComponent(idUtil.For(variable,true));
		Method m;
		try 
		{
			m = formComponent.getClass().getMethod("setDisabled", boolean.class);
			m.setAccessible(true);
			m.invoke(formComponent, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/ 	
		
		ReglaPlayer rp = (ReglaPlayer) Component.getInstance(ReglaPlayer.class);
		if(this.forGroup)
			rp.SetVarStateOnGroup(variable, variable, AffectedAttribute(), AffectedAttributeValue());
		else
			rp.SetVarState(variable, variable, AffectedAttribute(), AffectedAttributeValue());
		return true;
	}
	
	@Override
	public boolean Validate() 
	{		
		if(variable==null)
		{
			facesMessages.add("Debe seleccionar una variable.");
			return false;
		}
		
		return true;		
	}	
	
	@Override
	public String Resume() {		
		return "Se habilitar\u00E1 el campo asociado a la variable " + this.variable.getNombreVariable();
	}
	
	@Override
	public String InstanceDescription() {		
		return "Habilitar " + variable.getNombreVariable();
	}
	
	@Override
	public void LoadComponentState() {
		if(super.state!=null && !super.state.isEmpty())
		{
			Long id = (Long)super.state.get("varid");
			variable = (Variable_ensayo)entityManager.find(Variable_ensayo.class, id);			
		}
	}
	
	public Map<String, Object> GetComponentState() 
	{
		Map<String, Object> state = new HashMap<String, Object>();
		state.put("varid", variable.getId());		
		return state;
	}
	public Variable_ensayo getVariable() {
		return variable;
	}
	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}
	
	@Override
	public String IdToRerender() {		
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIComponent formComponent = null;
		if(!this.forGroup)
			formComponent = viewRoot.findComponent(idUtil.For(variable, true));
		else
			formComponent = viewRoot.findComponent(idUtil.ForGroup(variable, true));
		/*String parentId = null;
		if(formComponent != null && formComponent.getParent()!=null)
			parentId = formComponent.getParent().getClientId(FacesContext.getCurrentInstance());*/		
		return ((formComponent != null) ? formComponent.getParent().getParent().getId() : null);
	}
	
	@Override
	public String AffectedAttribute() {		
		return AffectedAttributes.DISABLED;
	}
	@Override
	public Object AffectedAttributeValue() {		
		return false;
	}
	@Override
	public Variable_ensayo TargetVariable() {		
		return getVariable();
	}
	
	@Override
	public IRuleAction OppositeAction() {		
		return new DisableFieldRuleAction();
	}
	
	@Override
	public boolean HtmlState() {		
		return true;
	}
	
	@Override
	public int orderPosition() {		
		return 0;
	}

}
