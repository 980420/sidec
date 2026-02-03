package gehos.ensayo.ensayo_disenno.session.reglas.util;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

@Name("traitRuleComponent")
@Scope(ScopeType.SESSION)
@AutoCreate
public class TraitRuleComponent {
	
	int createRuleActions;
	
	public IRuleAction CreateActionComponent(IRuleAction ruleAction){
		String key = String.format("%s_%s", ruleAction.getName(), this.createRuleActions);
		// Context appContext = Contexts.getApplicationContext();
		// Context sessContext = Contexts.getSessionContext();
		Context convContext = Contexts.getConversationContext();
		if (!convContext.isSet(key)){
			Component c = new Component(ruleAction.getClass(), key, ScopeType.CONVERSATION, false, null, null);
			Object o = c.newInstance();
			IRuleAction actionComponent = (IRuleAction) o;// Component.getInstance(ruleAction.getClass(), ScopeType.CONVERSATION, true);
			actionComponent.setComponentState(ruleAction.getDBState());
			actionComponent.setExecuteIfRuleTrue(ruleAction.getExecuteIfRuleTrue());
			actionComponent.setComponentName(key);
			convContext.set(key, actionComponent);
			this.createRuleActions++;
			return actionComponent;
		}
		return null;
	}
	
	public IRuleAction CreateActionComponent(IRuleAction ruleAction, boolean forGroup){
		String key = String.format("%s_%s", ruleAction.getName(), this.createRuleActions);
		// Context appContext = Contexts.getApplicationContext();
		// Context sessContext = Contexts.getSessionContext();
		Context convContext = Contexts.getConversationContext();
		if (!convContext.isSet(key)){
			Component c = new Component(ruleAction.getClass(), key, ScopeType.CONVERSATION, false, null, null);
			Object o = c.newInstance();
			IRuleAction actionComponent = (IRuleAction) o;// Component.getInstance(ruleAction.getClass(), ScopeType.CONVERSATION, true);
			actionComponent.setComponentState(ruleAction.getDBState());
			actionComponent.setExecuteIfRuleTrue(ruleAction.getExecuteIfRuleTrue());
			actionComponent.setComponentName(key);
			actionComponent.setForGroup(forGroup);
			convContext.set(key, actionComponent);
			this.createRuleActions++;
			return actionComponent;
		}
		return null;
	}
	
}