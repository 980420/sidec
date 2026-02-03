package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions;

import gehos.ensayo.ensayo_disenno.session.reglas.ReglaPlayer;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("NotifyRuleAction")
public class NotifyRuleAction extends IRuleAction{
	
	@In EntityManager entityManager;
	@In(create=true)
	IdUtil idUtil;
		
	static int ERROR = 0;
	static int WARN = 1;
	
	int type = ERROR;
	//String message;
	String msg;
		
	public String getName() {
		return "notify";
	}
	public String getLabel() {
			return "Notificar";
	}
	@Override
	public String getDescription() {
		return "Muestra notificaci\u00F3n";
	}
	
	@Override
	public String Resume() {		
		return String.format("Notificar\u00E1 con un mensaje de %s.", (type==ERROR ? "error":"advertencia"));
	}
	public boolean Execute(){
		//This way of notification has the issue of how to hide the message in contrast facesMessages show it on the next request instead of the current
		//uiNotifier.setMessage(message);
		if(this.type == ERROR){
			String id = this.idUtil.For(getRootVariable(),true);
			UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
			UIInput formComponent  = (UIInput)viewRoot.findComponent(id);
			formComponent.setValue(null);
			formComponent.setSubmittedValue(null);		
			formComponent.setValid(true);
			formComponent.setLocalValueSet(false);			
			ReglaPlayer rp = (ReglaPlayer) Component.getInstance(ReglaPlayer.class);
			rp.clearValue(this.rootVariable);			
		}			
		return false;
	}
	
	@Override
	public String InstanceDescription() {		
		return String.format("Notificar (%s) \"%s\"", (type==ERROR ? "error":"advertencia"),msg );
	}
		
	@Override
	public void LoadComponentState() {
		if(super.state!=null && !super.state.isEmpty())
		{
			if(super.state.containsKey("varid"))
			{
				Long id = (Long)super.state.get("varid");			
				Variable_ensayo variable = (Variable_ensayo)entityManager.find(Variable_ensayo.class, id);		
				super.setRootVariable(variable);
			}
			type = (Integer)super.state.get("type");
			msg = (String)super.state.get("message");
		}
	}
	
	public Map<String, Object> GetComponentState() 
	{
		Map<String, Object> state = new HashMap<String, Object>();
		if(super.getRootVariable()!=null)
			state.put("varid", super.getRootVariable().getId());	
		state.put("type", type);
		state.put("message", msg);		
		return state;
	}
	
	@Override
	public String IdToRerender(){		
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIComponent formComponent = viewRoot.findComponent(this.idUtil.For(this.getRootVariable(), true));
		@SuppressWarnings("unused")
		String parentId = null;
		if(formComponent.getParent() != null)
			parentId = formComponent.getParent().getParent().getClientId(FacesContext.getCurrentInstance());
		return formComponent.getParent().getParent().getId();
	}
	
	@Override
	public String ClientSideCode(){		
		return String.format("uiNotify('%s',%d)", msg, type);
	}
	
	@Override
	public boolean RequiresUserInput(){		
		return false;
	}
	
	/*public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}*/
	
	public int getType() {
		return type;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public int orderPosition() {		
		return 6;
	}

}
