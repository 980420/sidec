package gehos.ensayo.ensayo_disenno.session.reglas.helpers;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.ConditionsActionsHolderDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.ConditionsActionsHolderSerializer;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ConditionsActionsHolderSerializer.class)
@JsonDeserialize(using = ConditionsActionsHolderDeserializer.class)
public class ConditionsActionsHolder
{
	RuleNode node;
	List<IRuleAction> actions;
	
	public RuleNode getNode() {
		return node;
	}
	public void setNode(RuleNode node) {
		this.node = node;
	}
	public List<IRuleAction> getActions() {
		return actions;
	}
	public void setActions(List<IRuleAction> actions) {
		this.actions = actions;
	}
	
}
