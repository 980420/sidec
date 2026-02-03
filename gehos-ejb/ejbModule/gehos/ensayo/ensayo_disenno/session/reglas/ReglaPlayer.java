package gehos.ensayo.ensayo_disenno.session.reglas;

import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHojaGrupoControlador;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.WrapperGroupData;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.ConditionsActionsHolder;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.FileSigec;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.executionTypes.ExecutionTypes;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.testvalues.VariableValue;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.ensayo_disenno.session.reglas.util.TraitRuleComponent;
import gehos.ensayo.ensayo_disenno.session.reglas.util.Useful;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Regla_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.ajax4jsf.component.html.HtmlAjaxSupport;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Name("ruleManager")
@Scope(ScopeType.CONVERSATION)
public class ReglaPlayer 
{
	@In
	EntityManager entityManager;
	
	@In(value="gestionarHoja", required=false) 
	GestionarHoja gestionarHoja;
	
	@In(value="gestionarHojaGrupoControlador_ec", required=false) 
	GestionarHojaGrupoControlador gestionarHojaGrupoControlador;
		
	@In(create=true)
	IdUtil idUtil;
	
	@In
	TraitRuleComponent traitRuleComponent;  
	
	@Logger
	Log log;
	
	String clientDataDefault = "[]";
	String clientData = null; //client side data needed to perform some actions there - this was specically driven by the need to show a modal panel
	List<String> clientDataList = new ArrayList<String>();	
	Map<String, Map<String,Object>> varState;
	
	Iterator<ConditionsActionsHolder> itHoldersWithUserActions;
	Iterator<IRuleAction> itUserActions;
	ConditionsActionsHolder lastHolder = null;
	IRuleAction lastUserInputAction = null;
	
	boolean restorePhase = true;
	boolean restorePhaseGroup = true;
	
	@Create
	@Begin(join=true)
	public void init()
	{
		varState = new HashMap<String, Map<String,Object>>();		
	}
	
	public String CreateVarStateId(Variable_ensayo v, Integer row)
	{
		return String.valueOf(v.getId()) + (row!=null && row!=-1 ? "_"+String.valueOf(row) : "");
	}
	
	public void PutVarState(Variable_ensayo v, Integer row, String key, Object value)
	{
		String id = CreateVarStateId(v,row);	
		Map<String,Object> map;
		if(varState.containsKey(id))
		{
			map = varState.get(id);			
		}
		else
		{
			map = new HashMap<String, Object>();
			varState.put(id, map);
		}
		map.put(key, value);
	}	
	
	public Object GiveVarState(Variable_ensayo v, Integer row, String key){
		String id = CreateVarStateId(v,row);
		if(varState.containsKey(id))
		{
			Map<String,Object> map = varState.get(id);
			if(map.containsKey(key))
				return map.get(key);
			return "rendered".equals(key) ? true : false; //this should be changed to a more state (key) dependant value, but as for now it's "rendered" and "disabled" only
		}
		return "rendered".equals(key) ? true : false;//this should be changed to a more state (key) dependant value, but as for now it's "rendered" and "disabled" only
	}
	
	public String createVarStateId(Variable_ensayo v){
		return String.valueOf(v.getId());
	}
	
	public void putVarState(Variable_ensayo v, String key, Object value){
		String id = this.createVarStateId(v);	
		Map<String, Object> map;
		if(this.varState.containsKey(id))
			map = this.varState.get(id);			
		else{
			map = new HashMap<String, Object>();
			this.varState.put(id, map);
		}
		map.put(key, value);
	}
	
	public Object giveVarState(Variable_ensayo v, String key){
		String id = this.createVarStateId(v);
		if(this.varState.containsKey(id)){
			Map<String,Object> map = this.varState.get(id);
			if(map.containsKey(key))
				return map.get(key);
			return "rendered".equals(key) ? true : false; //this should be changed to a more state (key) dependant value, but as for now it's "rendered" and "disabled" only
		}
		return "rendered".equals(key) ? true : false;//this should be changed to a more state (key) dependant value, but as for now it's "rendered" and "disabled" only
	}
	
	public void evaluate(long idVariable, Object value, Map<String, Object> dependencies){
		@SuppressWarnings("unused")
		Variable_ensayo v = entityManager.find(Variable_ensayo.class, idVariable);
		//Regla_ensayo regla = v.get;
		//regla.
	}
	
	/**
	 * CREEPPY CODEEEEE, I WARN YOU
	 * This is to execute operations requiring user input (an example of this is the modal) and thus require execution before the view (form data) is actually saved. The form data is not saved 
	 * while there are operations of this type
	 * @return True is all operations have been completed, False otherwise.
	 * @throws Exception
	 */
	public boolean beforeSaveOnGroup() throws Exception{
		if(this.itHoldersWithUserActions == null)
			this.itHoldersWithUserActions = this.pickUserInputActionsOnSaveOnGroup();//this only neeed to be done once
		if(this.lastHolder == null && this.itHoldersWithUserActions.hasNext())
			this.lastHolder = this.itHoldersWithUserActions.next();		
		if(this.lastHolder == null && !this.itHoldersWithUserActions.hasNext())
			return true;		
		if(this.itUserActions == null && this.lastHolder != null)
			this.itUserActions = this.lastHolder.getActions().iterator();		
		if(this.lastUserInputAction != null){
			Context convContext = Contexts.getConversationContext();
			IActionWithUserInput actionManager = (IActionWithUserInput)convContext.get(IActionWithUserInput.actionManagerName);
			if(actionManager == null)
				throw new Exception("This shouldn't happen!!!");
			if(!actionManager.getCompleted()) //if not completed then stop here and let the user act
				return false;			
			this.lastUserInputAction = null;
		}		
		if(this.itUserActions.hasNext()){
		    Boolean res = this.nodeEvalOnGroup(this.lastHolder);		    
		    do {
		    	this.lastUserInputAction = this.itUserActions.next();		    	
			} 
		    while (this.itUserActions.hasNext() && !res.equals(this.lastUserInputAction.getExecuteIfRuleTrue()));		    
		    if(res.equals(this.lastUserInputAction.getExecuteIfRuleTrue())){ //this must be checked again because it the While could stop because of no more elements
		    	List<String> results = new ArrayList<String>();
				String code = this.lastUserInputAction.ClientSideCode();
				results.add(code);
				String jsonRes = new ObjectMapper().writeValueAsString(results);
				this.clientData = jsonRes;
				return false;
		    } else {
		    	this.lastUserInputAction = null;
		    	this.clientData = this.clientDataDefault;
		    	return this.beforeSaveOnGroup();	
		    }			
		}		
		this.lastHolder = null;		
		return this.beforeSaveOnGroup();		
	}
	
	/**
	 * CREEPPY CODEEEEE, I WARN YOU
	 * This is to execute operations requiring user input (an example of this is the modal) and thus require execution before the view (form data) is actually saved. The form data is not saved 
	 * while there are operations of this type
	 * @return True is all operations have been completed, False otherwise.
	 * @throws Exception
	 */
	public boolean beforeSave() throws Exception{
		if(this.itHoldersWithUserActions == null)
			this.itHoldersWithUserActions = this.pickUserInputActionsOnSave();//this only neeed to be done once
		if(this.lastHolder == null && this.itHoldersWithUserActions.hasNext())
			this.lastHolder = this.itHoldersWithUserActions.next();		
		if(this.lastHolder == null && !this.itHoldersWithUserActions.hasNext())
			return true;		
		if(this.itUserActions == null && this.lastHolder != null)
			this.itUserActions = this.lastHolder.getActions().iterator();		
		if(this.lastUserInputAction != null){
			Context convContext = Contexts.getConversationContext();
			IActionWithUserInput actionManager = (IActionWithUserInput)convContext.get(IActionWithUserInput.actionManagerName);
			if(actionManager == null)
				throw new Exception("This shouldn't happen!!!");
			if(!actionManager.getCompleted()) //if not completed then stop here and let the user act
				return false;			
			this.lastUserInputAction = null;
		}		
		if(this.itUserActions.hasNext()){
		    Boolean res = this.NodeEval(this.lastHolder);		    
		    do {
		    	this.lastUserInputAction = this.itUserActions.next();		    	
			} 
		    while (this.itUserActions.hasNext() && !res.equals(this.lastUserInputAction.getExecuteIfRuleTrue()));		    
		    if(res.equals(this.lastUserInputAction.getExecuteIfRuleTrue())){ //this must be checked again because it the While could stop because of no more elements
		    	List<String> results = new ArrayList<String>();
				String code = this.lastUserInputAction.ClientSideCode();
				results.add(code);
				String jsonRes = new ObjectMapper().writeValueAsString(results);
				this.clientData = jsonRes;
				return false;
		    } else {
		    	this.lastUserInputAction = null;
		    	this.clientData = this.clientDataDefault;
		    	return this.beforeSave();	
		    }			
		}		
		this.lastHolder = null;		
		return this.beforeSave();		
	}
	
	/**
	 * Wraps the execution of "onSave" rules and the actual persistence of the form data (which is as before delegated to gestionarHojaGrupo.save();)
	 */
	public String saveGroup(){
		try {
			if(!this.beforeSaveOnGroup())
				return null;
			List<String> results = this.execOnSaveRulesOnGroup();
			String jsonRes = new ObjectMapper().writeValueAsString(results);
			this.clientData = jsonRes;
			// Persistir los grupos de variables al adicionarlos usando la misma controladora que se usa para la hoja
			return this.gestionarHojaGrupoControlador.save();
		} catch (Exception e){
			e.printStackTrace();
			this.log.fatal(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Wraps the execution of "onSave" rules and the actual persistence of the form data (which is as before delegated to gestionarHoja.persistir();)
	 */
	public String save(){		
		try{
			if(!this.beforeSave())
				return null;			
			List<String> results = this.execOnSaveRules();
			String jsonRes = new ObjectMapper().writeValueAsString(results);
			this.clientData = jsonRes;
			return this.gestionarHoja.persistir();
		} catch (Exception e){
			e.printStackTrace();
			this.log.fatal(e.getMessage());
			return null;
		}
	}
	
	/* 
	 * This must be changed because I'm considering only that the conditions is always fulfilled
	 */
	public Iterator<ConditionsActionsHolder> pickUserInputActionsOnSave() {
		LinkedList<ConditionsActionsHolder> output = new LinkedList<ConditionsActionsHolder>();
		for(Seccion_ensayo itemSection : this.gestionarHoja.getSecciones()){
			for(Variable_ensayo itemVariable : this.gestionarHoja.loadVariables(itemSection.getId())){
				Set<Regla_ensayo> rules = itemVariable.getReglas();
				for (Regla_ensayo r : rules){
					if (r.getExecPhase().equals(ExecutionTypes.PHASESAVE)) {
						ConditionsActionsHolder holder = ConvertStringToHolder(r.getOpciones());
						if (holder == null)
							continue;
						List<IRuleAction> actionsDB = holder.getActions();
						List<IRuleAction> actionsComponents = new ArrayList<IRuleAction>();	
						for (IRuleAction a : actionsDB){
							if (a.RequiresUserInput()){ // just add the actions requiring user input							
								IRuleAction aPrime = this.traitRuleComponent.CreateActionComponent(a);
								actionsComponents.add(aPrime);
							}
						}
						if (actionsComponents.size() > 0){
							holder.setActions(actionsComponents);
							output.add(holder);
						}
					}
				}
			}
		}
		return output.iterator();
	}
	
	/* 
	 * This must be changed because I'm considering only that the conditions is always fulfilled
	 */
	public Iterator<ConditionsActionsHolder> pickUserInputActionsOnSaveOnGroup(){
		LinkedList<ConditionsActionsHolder> output = new LinkedList<ConditionsActionsHolder>();
		for (Variable_ensayo v : this.gestionarHojaGrupoControlador.getVariables()){
			Set<Regla_ensayo> rules = v.getReglas();
			for (Regla_ensayo r : rules)
				if (r.getExecPhase().equals(ExecutionTypes.PHASESAVE)){
					ConditionsActionsHolder holder = ConvertStringToHolder(r.getOpciones());
					if (holder == null)
						continue;
					List<IRuleAction> actionsDB = holder.getActions();
					List<IRuleAction> actionsComponents = new ArrayList<IRuleAction>();
					for (IRuleAction a : actionsDB){
						if (a.RequiresUserInput()){ // just add the actions requiring user input
							IRuleAction aPrime = this.traitRuleComponent.CreateActionComponent(a);
							actionsComponents.add(aPrime);
						}
					}
					if (actionsComponents.size() > 0){
						holder.setActions(actionsComponents);
						output.add(holder);
					}
				}
		}
		return output.iterator();
	}
	
	public List<String> execOnSaveRulesOnGroup(){
		List<String> results = new ArrayList<String>();		
		Map<Long, List<Variable_ensayo>> map = new HashMap<Long, List<Variable_ensayo>>();
		Map<Long, List<Variable_ensayo>> mapy = new HashMap<Long, List<Variable_ensayo>>();
		map.put(this.gestionarHojaGrupoControlador.getSection().getId(), this.gestionarHojaGrupoControlador.getVariables());	
		mapy.putAll(map);		
		for (Entry<Long, List<Variable_ensayo>> e : mapy.entrySet()){
			List<Variable_ensayo> sVars = e.getValue();
			for (Variable_ensayo var : sVars) 
				results.addAll(this.execRulesForOnGroup(var));			
		}		
		return results;
	}
	
	public List<String> execOnSaveRules(){
		List<String> results = new ArrayList<String>();
		for(Long itemKey : this.gestionarHoja.getMapWD().keySet()){
			for(MapWrapperDataPlus itemData : this.gestionarHoja.getMapWD().get(itemKey).getData().values()){
				results.addAll(this.execRulesFor(itemData.getVariable()));
			}
		}		
		return results;
	}
	
	public List<String> execRulesFor(Variable_ensayo v){			
		List<String> results = new ArrayList<String>();		
		Set<Regla_ensayo> rules = v.getReglas();
		for (Regla_ensayo r : rules)					
			if(r.getExecPhase().equals(ExecutionTypes.PHASESAVE))			
				results.addAll(this.execRule(r));		
		return results;
	}
	
	public List<String> execRulesForOnGroup(Variable_ensayo v){			
		List<String> results = new ArrayList<String>();		
		Set<Regla_ensayo> rules = v.getReglas();
		for (Regla_ensayo r : rules)					
			if(r.getExecPhase().equals(ExecutionTypes.PHASESAVE))			
				results.addAll(execRuleOnGroup(r));		
		return results;
	}
	
	public List<String> execRule(Regla_ensayo r){		
		ConditionsActionsHolder holder = ConvertStringToHolder(r.getOpciones());
		if(holder == null)
			return new ArrayList<String>();		
		List<Regla_ensayo> rules = new ArrayList<Regla_ensayo>();
		rules.add(r);
		String path = this.buildRulePath(rules,ExecutionTypes.PHASESAVE);
		List<String> results = this.check(path, null);
		return results; 
	}
	
	public List<String> execRuleOnGroup(Regla_ensayo r){		
		ConditionsActionsHolder holder = this.ConvertStringToHolder(r.getOpciones());
		if(holder == null)
			return new ArrayList<String>();		
		List<Regla_ensayo> rules = new ArrayList<Regla_ensayo>();
		rules.add(r);
		String path = this.buildRulePathOnGroup(rules, ExecutionTypes.PHASESAVE);
		List<String> results = this.checkOnGroup(path, null);
		return results; 
	}
	
	/**
	 * Groups rules by PHASE_FILL. This is necessary because the html component has 1 one property for each event.
	 * @param rules
	 * @return
	 */
	private Map<String, List<Regla_ensayo>> group(Set<Regla_ensayo> rules){
		Map<String, List<Regla_ensayo>> out = new HashMap<String, List<Regla_ensayo>>();			
		for (Regla_ensayo r : rules){
			if(r.getExecPhase().equals(ExecutionTypes.PHASEFILL)){
				if(out.containsKey(r.getExecEvent()))				
					out.get(r.getExecEvent()).add(r);
				else {
					List<Regla_ensayo> erules = new ArrayList<Regla_ensayo>();
					erules.add(r);
					out.put(r.getExecEvent(),erules);
				}
			}
		}
		return out;			
	}
	
	public void installRules(){
		executeAllRules();//TODO analize if this should go here or apart
		if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty()){
			for(WrapperGroupData itemWD : this.gestionarHoja.getMapWD().values()){
				if(itemWD.getData() != null && !itemWD.getData().isEmpty()){
					for(MapWrapperDataPlus itemData : itemWD.getData().values()){
						this.installRulesTo(itemData.getVariable());
					}
				}
			}
		}		
	}
	
	public void installRulesOnGroup(){
		List<Variable_ensayo> sVars = this.gestionarHojaGrupoControlador.getVariables();
		for (Variable_ensayo itemVar : sVars)
			this.installRulesOnGroupTo(itemVar);			
	}
	
	private void installRulesOnGroupTo(Variable_ensayo v){
		Set<Regla_ensayo> rules = v.getReglas();
		String componentId =  this.idUtil.ForGroup(v, true);
		UIInput component = FindComponent(componentId);		
		if(component == null)
			return;
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, List<Regla_ensayo>> grouped = this.group(rules);
		for (Entry<String, List<Regla_ensayo>> g: grouped.entrySet()){					
			String event = g.getKey();
			String onSubmit = null, onComplete = null;
			//TODO this should only be done in case there is at least one action having js code
			onComplete = "pullJsCode()";			
			if(v.getPresentacionFormulario().getNombre().matches("checkbox|radio"))
				event = ExecutionTypes.ONCHANGE;
			if(v.getPresentacionFormulario().getNombre().equals("file")){	
				//this must be done coz if the component has already, in this case, the onchange event set then the ajax support is not added
				//so we put it in the onSubmit of the ajax support
				/*HtmlFileUpload fuComponent = (HtmlFileUpload)component;
				String onChange = fuComponent.getOnchange();
				fuComponent.setOnchange(null);
				onSubmit = onChange;*/
				
				//TODO it does not work by using a a4j:support
				//event = "onuploadcomplete";
				//onComplete+=String.format(";js%s();",idUtil.For(v));				
				continue;
			}			
			if(event != null){
				// > For the special case of the action OpenModalRuleAction
				boolean hasModal = false;
				for (Regla_ensayo r : g.getValue()){
					hasModal = r.getOpciones().contains("OpenModalRuleAction");
					if(hasModal)
						break;
				}				
				// < For the special case of the action OpenModalRuleAction				
				String elMethodCall = String.format("#{ruleManager.checkOnGroup('%s', null)}", this.buildRulePathOnGroup(g.getValue(), ExecutionTypes.PHASEFILL));					
				ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), elMethodCall, (new ArrayList<String>()).getClass());							
				this.addAjaxSupport(component, ve, "as"+v.getId()+event, event, onSubmit, onComplete);
			}			
		}
		
		//in case the variable has no rules and is in a group then ajax support is added for the sake of update its value immediately
		if(grouped.isEmpty() && v.getGrupoVariables() != null){
			String event = "onselect";
			String ajaxSupportId = "as" + v.getId() + event;
			HtmlAjaxSupport es = new HtmlAjaxSupport();
			es.setId(ajaxSupportId);
			es.setEvent(event);
			es.setAjaxSingle(true); 
			component.getFacets().put(component.getId()+event, es);
		}		
	}
	
	private void installRulesTo(Variable_ensayo v){
		log.info("Installing rule #0", v.getNombreVariable());
		Set<Regla_ensayo> rules = v.getReglas();
		String componentId =  idUtil.For(v,true);
		UIInput component = FindComponent(componentId);		
		if(component == null)
			return;
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, List<Regla_ensayo>> grouped = group(rules);
		for (Entry<String, List<Regla_ensayo>> g: grouped.entrySet()){					
			String event = g.getKey();
			String onSubmit = null, onComplete = null;
			//TODO this should only be done in case there is at least one action having js code
			onComplete = "pullJsCode()";
			if(v.getPresentacionFormulario().getNombre().matches("checkbox|radio"))
				event = ExecutionTypes.ONCHANGE;
			if(v.getPresentacionFormulario().getNombre().equals("file")){	
				//this must be done coz if the component has already, in this case, the onchange event set then the ajax support is not added
				//so we put it in the onSubmit of the ajax support
				/*HtmlFileUpload fuComponent = (HtmlFileUpload)component;
				String onChange = fuComponent.getOnchange();
				fuComponent.setOnchange(null);
				onSubmit = onChange;*/
				
				//TODO it does not work by using a a4j:support
				//event = "onuploadcomplete";
				//onComplete+=String.format(";js%s();",idUtil.For(v));
				
				continue;

			}			
			if(event != null){
				// > For the special case of the action OpenModalRuleAction
				boolean hasModal = false;
				for (Regla_ensayo r : g.getValue()){
					hasModal = r.getOpciones().contains("OpenModalRuleAction");
					if(hasModal)
						break;
				}
				// < For the special case of the action OpenModalRuleAction				
				String elMethodCall = String.format("#{ruleManager.check('%s',null)}", buildRulePath(g.getValue(), ExecutionTypes.PHASEFILL));					
				ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(),elMethodCall, (new ArrayList<String>()).getClass());							
				addAjaxSupport(component, ve, "as"+v.getId()+event, event, onSubmit, onComplete);
			}			
		}
		
		//in case the variable has no rules and is in a group then ajax support is added for the sake of update its value immediately
		if(grouped.isEmpty() && v.getGrupoVariables() != null){
			String event = "onselect";
			String ajaxSupportId = "as"+v.getId()+event;
			HtmlAjaxSupport es = new HtmlAjaxSupport();
			es.setId(ajaxSupportId);
			//es.setAction(me);
			//es.setValueExpression("reRender", reRenderProperty);	
			es.setEvent(event);
			es.setAjaxSingle(true); 
			//es.setLimitToList(true);		
			component.getFacets().put(component.getId()+event, es);
		}		
	}
	
	public List<String> executeForFile(Variable_ensayo v){
		List<Regla_ensayo> rules = new ArrayList<Regla_ensayo>();
		rules.addAll(v.getReglas());		
		if(rules.size() == 0)
			return new ArrayList<String>();		
		String rulePath = this.buildRulePath(rules, ExecutionTypes.PHASE_RESTORE_STATE);
		String id = "opFor" + this.idUtil.For(v);
		List<String> reRender = this.check(rulePath, null);
		reRender.add(id);
		return reRender;
	}
	
	/**
	 * When a CRDSheet is saved a given component could have had a particular state (shown-hidden, enabled-disabled) that is necessary to restore
	 * when editing again, so to address this problem there are 2 options:
	 * 1- save those particular states on the data store (DataBase)
	 * 2- run the rules when opening a saved CRDSheet, the rules are the ones associated to html properties because others are applied, that is, the action "send an email" should not be executed
	 *
	 *	This method does the 2 option
	 */
	public void executeAllRules(){
		this.restorePhase = true;
		if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty()){
			for(WrapperGroupData itemWD : this.gestionarHoja.getMapWD().values()){
				if(itemWD.getData() != null && !itemWD.getData().isEmpty()){
					for(MapWrapperDataPlus itemData : itemWD.getData().values()){
						this.executeRules(itemData.getVariable());
					}
				}
			}
		}
		this.restorePhase = false;					
	}
	
	private void executeRules(Variable_ensayo v){
		try {		
			log.info("Executing rule #0", v.getNombreVariable());			
			Set<Regla_ensayo> rules = v.getReglas();
			String componentId =  this.idUtil.For(v,true);
			UIInput component = this.FindComponent(componentId);			
			if(component == null)
				return;
			Map<String, List<Regla_ensayo>> grouped = this.group(rules);
			for (Entry<String, List<Regla_ensayo>> g: grouped.entrySet()){				
				Function<IRuleAction, Boolean> actionFilterFunc = new Function<IRuleAction, Boolean>(){
					@Override
					public Boolean apply(IRuleAction a){				
						return a.HtmlState();
					}
				};
				String rulePath = this.buildRulePath(g.getValue(), ExecutionTypes.PHASE_RESTORE_STATE);
				this.check(rulePath, actionFilterFunc);			
			}
		} catch (Exception e){}		
	}
	
	public MapWrapperDataPlus wrapperForFile(Variable_ensayo v){
		if(this.gestionarHoja.getMapWD().containsKey(v.getSeccion().getId()) && this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData().containsKey(v.getId())){
			MapWrapperDataPlus tempData = this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData().get(v.getId());
			this.activateFileUpload(tempData.getVariable());
			return tempData;
		}		
		return null;
	}
	
	public void activateFileUpload(Variable_ensayo v){
		String componentId = this.idUtil.For(v, true);
		org.richfaces.component.html.HtmlFileUpload c = (org.richfaces.component.html.HtmlFileUpload)FindComponent(componentId);
		c.setDisabled(false);
	}
		
	/*
	 * Configure the rule execution with more than one rule because right now it is executing the last one because the ui component only accept one action per event type e.g the html component only has 1 blur property
	 */
	private String buildRulePath(List<Regla_ensayo> r, String phase){		
		Variable_ensayo v = r.get(0).getVariable();
		List<String> ids = Lists.transform(r, new Function<Regla_ensayo, String>(){
			@Override
			public String apply(Regla_ensayo arg0){				
				return String.valueOf(arg0.getId());
			}
		});		
		String rules = Useful.join(ids, ".");
		String path = String.format("%s_%s_%s_%s", v.getSeccion().getId(), v.getId(), rules, phase);
		return path;
	}
	
	private String buildRulePathOnGroup(List<Regla_ensayo> r, String phase){		
		Variable_ensayo v = r.get(0).getVariable();
		List<String> ids = Lists.transform(r, new Function<Regla_ensayo, String>(){
			@Override
			public String apply(Regla_ensayo arg0){				
				return String.valueOf(arg0.getId());
			}
		});		
		String rules = Useful.join(ids, ".");
		String path = String.format("%s_%s_%s", v.getId(), rules, phase);
		return path;
	}
	
	//TODO be careful with using this, it is used in the Notify error to clear a variable's value which would cause problem when persisting
	public void clearValue(Variable_ensayo var){
		if(var.getGrupoVariables() == null){
			if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty() && this.gestionarHoja.getMapWD().containsKey(var.getSeccion().getId())){
				WrapperGroupData itemWD = this.gestionarHoja.getMapWD().get(var.getSeccion().getId());
				if(itemWD != null && itemWD.getData() != null && !itemWD.getData().isEmpty() && itemWD.getData().containsKey(var.getId()))
					this.gestionarHoja.getMapWD().get(var.getSeccion().getId()).getData().get(var.getId()).setValue(null);
			}
		}
	}
	/**
	 * Checks if a rule is met and execute actions accordingly.
	 * @param cc is the rule path in the form <IdSection>_<IdVariable>_<IdRule>-<PHASE>
	 * @return list of ids to reRender or js codes to be executed
	 */
	public List<String> check(String cc, Function<IRuleAction, Boolean> actionFilterFunc){
		this.ClearClientData();
		//HtmlSelectManyCheckbox s = (HtmlSelectManyCheckbox)FindComponent("var10010000000000002961001000000000001216");
		//Object tc = s.getSelectedValues();
		
		//HtmlSelectOneRadio rs = (HtmlSelectOneRadio)FindComponent("var10010000000000002961001000000000001217");
		
		//HtmlFileUpload fu = (HtmlFileUpload)FindComponent("var10010000000000002961001000000000001222");
		//Object lv = fu.getLocalValue();
		
		List<String> listIdsRerender = new ArrayList<String>();		
		
		
		String[] varRuleArr = cc.split("_",4);//this is because the phase contains the char '_' and the split is done at it too
		long varSeccion = Long.parseLong(varRuleArr[0]);
		long varId = Long.parseLong(varRuleArr[1]);
		String[] varRulesPart = varRuleArr[2].split("\\.");	
		
		List<String> ruleIds = Lists.newArrayList(varRulesPart);
		List<Long> rids= Lists.transform(ruleIds, new Function<String, Long>() {
			@Override
			public Long apply(String arg0) {				
				return Long.parseLong(arg0);
			}
		});
		Variable_ensayo var = null;
		if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty() && this.gestionarHoja.getMapWD().containsKey(varSeccion)){			
			if(this.gestionarHoja.getMapWD().get(varSeccion).getData() != null && !this.gestionarHoja.getMapWD().get(varSeccion).getData().isEmpty()){
				for(MapWrapperDataPlus itemData : this.gestionarHoja.getMapWD().get(varSeccion).getData().values()){
					if(itemData.getVariable().getId() == varId){
						var = itemData.getVariable();
						break;
					}
				}
			}
		}	
		for (Long varRule : rids){			
			Regla_ensayo rule = null;
			for (Regla_ensayo r : var.getReglas()){
				if(r.getId() == varRule){
					rule = r;
					break;
				}
			}
			ConditionsActionsHolder holder = this.ConvertStringToHolder(rule.getOpciones());		
			Boolean result = this.NodeEval(holder);		
			if(result == null) //in case the evaluation could not be done
				continue;			
			List<IRuleAction> actionsDB = holder.getActions();
			List<IRuleAction> actionsComponents = new ArrayList<IRuleAction>();			
			for (IRuleAction a : actionsDB){
				if(a.getExecuteIfRuleTrue() == result){
					IRuleAction aPrime = this.traitRuleComponent.CreateActionComponent(a);
					if(aPrime.getRootVariable() == null)
						aPrime.setRootVariable(rule.getVariable());
					actionsComponents.add(aPrime);
				}
			}			
			for (IRuleAction a : actionsComponents){	
				if(actionFilterFunc != null){
					boolean couldBeExecuted = actionFilterFunc.apply(a);
					if(!couldBeExecuted)
						continue;
				} 
				a.Execute();
				String id = a.IdToRerender();
				if(id != null)
					listIdsRerender.add(id);
				if(a.ClientSideCode() != null){
					String code = a.ClientSideCode();
					PutClientData(code);
					String idd = a.IdToRerender();
					if(idd != null && !listIdsRerender.contains(idd))
						listIdsRerender.add(id);
				}			
			}		
		}
		return listIdsRerender;		
	}
	
	/**
	 * Checks if a rule is met and execute actions accordingly.
	 * @param cc is the rule path in the form <IdSection>_<IdVariable>_<IdRule>-<PHASE>
	 * @return list of ids to reRender or js codes to be executed
	 */
	public List<String> checkOnGroup(String cc, Function<IRuleAction, Boolean> actionFilterFunc){
		this.ClearClientData();
		List<String> listIdsRerender = new ArrayList<String>();
		String[] varRuleArr = cc.split("_", 3);//this is because the phase contains the char '_' and the split is done at it too
		long varId = Long.parseLong(varRuleArr[0]);
		String[] varRulesPart = varRuleArr[1].split("\\.");
		List<String> ruleIds = Lists.newArrayList(varRulesPart);
		List<Long> rids = Lists.transform(ruleIds, new Function<String, Long>() {
			@Override
			public Long apply(String arg0){				
				return Long.parseLong(arg0);
			}
		});				
		List<Variable_ensayo> vars = this.gestionarHojaGrupoControlador.getVariables();		
		Variable_ensayo var = null;
		for (Variable_ensayo v : vars){
			if(v.getId() == varId){
				var = v;
				break;
			}
		}		
		for (Long varRule : rids){	
			Regla_ensayo rule = null;
			for (Regla_ensayo r : var.getReglas()){
				if(r.getId() == varRule){
					rule = r;
					break;
				}
			}
			ConditionsActionsHolder holder = this.ConvertStringToHolder(rule.getOpciones());		
			Boolean result = this.nodeEvalOnGroup(holder);		
			if(result == null) //in case the evaluation could not be done
				continue;
			List<IRuleAction> actionsDB = holder.getActions();
			List<IRuleAction> actionsComponents = new ArrayList<IRuleAction>();			
			for (IRuleAction a : actionsDB){
				if(a.getExecuteIfRuleTrue() == result){
					IRuleAction aPrime = this.traitRuleComponent.CreateActionComponent(a, true);
					if(aPrime.getRootVariable() == null)
						aPrime.setRootVariable(var);
					actionsComponents.add(aPrime);
				}
			}			
			for (IRuleAction a : actionsComponents){
				if(actionFilterFunc != null){
					boolean couldBeExecuted = actionFilterFunc.apply(a);
					if(!couldBeExecuted)
						continue;
				}
				String id = null;
				try {
					a.Execute();
					id = a.IdToRerender();
				} catch (Exception e){
					e.printStackTrace();
				}				
				if(id != null)
					listIdsRerender.add(id);
				if(a.ClientSideCode() != null){
					String code = a.ClientSideCode();
					this.PutClientData(code);
					/*String idd = a.IdToRerender();
					if(idd != null && !listIdsRerender.contains(idd))
						listIdsRerender.add(idd);*/
				}			
			}		
		}
		return listIdsRerender;		
	}
	
	/**
	 * TODO for now the source and target variable will be the same but should not
	 * @param var
	 * @param targetVariable
	 * @param attributeName
	 * @param attributeValue
	 */
	public void SetVarState(Variable_ensayo var ,Variable_ensayo targetVariable, String attributeName, Object attributeValue)
	{
		Integer row = null;
		if(targetVariable!=null)
		{
			UIInput cmp = FindComponent(idUtil.For(targetVariable,true));
			if(cmp == null)
		        cmp = FindComponent(idUtil.For(targetVariable));
		    if(var.getGrupoVariables()!=null && cmp != null){
		    	String targetClientId = cmp.getClientId(FacesContext.getCurrentInstance());				
				String[] parts = targetClientId.split(":");
				try 
				{
					row = Integer.valueOf(parts[parts.length-2]);//TODO wierd thing, when restoring state the index (row) is not available
				} catch (Exception e) 
				{
					row = null;					
				}
		    }	
			PutVarState(targetVariable, row, attributeName, attributeValue);
		}
	}
	
	/**
	 * TODO for now the source and target variable will be the same but should not
	 * @param var
	 * @param targetVariable
	 * @param attributeName
	 * @param attributeValue
	 */
	public void SetVarStateOnGroup(Variable_ensayo var ,Variable_ensayo targetVariable, String attributeName, Object attributeValue){
		if(targetVariable != null){
			//UIInput cmp = FindComponent(idUtil.ForGroup(targetVariable, true));
			putVarState(targetVariable, attributeName, attributeValue);
		}
	}
	
	/**
	 * Checks if the rule conditions are met or not
	 * @return True if the conditions are met, False if not and Null otherwise which means the evaluation could not be performed.
	 */
	private Boolean NodeEval(ConditionsActionsHolder holder){	
		try {
			List<VariableValue> testVariableValues = new ArrayList<VariableValue>();
			List<Variable_ensayo> ruleNodeVars = new ArrayList<Variable_ensayo>();
			holder.getNode().FindDependencyVariables(ruleNodeVars);			
			for (Variable_ensayo v : ruleNodeVars){
				String vId = this.idUtil.For(v, true);
				Object val = null;				
				if(v.getSeccion().getHojaCrd().getId() == this.gestionarHoja.getHoja().getHojaCrd().getId()){
					if(!(this.restorePhase == true && v.getGrupoVariables() != null)){
						if(v.getPresentacionFormulario().getNombre().equals("file")){	
							if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty() && this.gestionarHoja.getMapWD().containsKey(v.getSeccion().getId())){
								if(this.gestionarHoja.getMapWD().get(v.getSeccion().getId()) != null && this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData() != null && !this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData().isEmpty() && this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData().containsKey(v.getId())){
									byte[] data = this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData().get(v.getId()).getData();
									String fileName = this.gestionarHoja.getMapWD().get(v.getSeccion().getId()).getData().get(v.getId()).cleanFileName();
									FileSigec f = new FileSigec();
									f.setName(fileName);
									f.setSize(data.length);
									f.setType("");	
									val = f.valueAsjsonString();
								}
							}		
						} else {
							UIInput comp = FindComponent(vId);
							val = comp.getValue();
						}
					}
				} else {//if this variable belongs to another HojaCRD, which means, that is not in the html form and its value must be retrieved from the database
					long idHojaCrdVar = v.getSeccion().getHojaCrd().getId();
					long IdSujeto = this.gestionarHoja.getIdSujeto();
					Sujeto_ensayo sujeto = this.entityManager.find(Sujeto_ensayo.class, IdSujeto);
					Cronograma_ensayo cronograma = sujeto.getGrupoSujetos().getCronogramas().iterator().next();
					MomentoSeguimientoGeneral_ensayo momentoGeneral = null;
					Set<MomentoSeguimientoGeneral_ensayo> momentosGenerales = cronograma.getMomentoSeguimientoGenerals();
					for (MomentoSeguimientoGeneral_ensayo mg : momentosGenerales){
						Set<MomentoSeguimientoGeneralHojaCrd_ensayo> mgh = mg.getMomentoSeguimientoGeneralHojaCrds();
						for (MomentoSeguimientoGeneralHojaCrd_ensayo gh : mgh){
							if(gh.getHojaCrd().getId() == idHojaCrdVar){	
								momentoGeneral = mg;			
								break;
							}
						}
						if(momentoGeneral != null)
							break;
					}						
					MomentoSeguimientoEspecifico_ensayo momentoEspecifico = (MomentoSeguimientoEspecifico_ensayo)entityManager.createQuery("select e from MomentoSeguimientoEspecifico_ensayo e where e.momentoSeguimientoGeneral.id=:msg and e.sujeto.id=:sujeto").setParameter("msg", momentoGeneral.getId()).setParameter("sujeto", sujeto.getId()).getSingleResult();
					CrdEspecifico_ensayo crdEspVarExtern = (CrdEspecifico_ensayo) this.entityManager.createQuery("select e from CrdEspecifico_ensayo e where e.momentoSeguimientoEspecifico.id=:idMomento and e.hojaCrd.id=:idHoja").setParameter("idHoja", idHojaCrdVar).setParameter("idMomento", momentoEspecifico.getId()).getSingleResult();
					long idCrdEsp = crdEspVarExtern.getId();					
					VariableDato_ensayo dato = (VariableDato_ensayo) this.entityManager.createQuery("select NomVar from VariableDato_ensayo NomVar where NomVar.variable.id=:Variable and NomVar.crdEspecifico.id=:Hoja").setParameter("Variable", v.getId()).setParameter("Hoja", idCrdEsp).getSingleResult();					
					val = dato.getValor();
				}				
				if(val == null )//|| val.toString().trim().isEmpty() TODO some validation must be done so the RuleNode evaluates correctly
					return null;
				VariableValue vval = new VariableValue(v,val);		
				testVariableValues.add(vval);
			}			
			boolean result = holder.getNode().Visit(testVariableValues);
			return result;
		} catch(Exception e){
			return null;
		}
	}
	
	private Boolean nodeEvalOnGroup(ConditionsActionsHolder holder){	
		try {
			List<VariableValue> testVariableValues = new ArrayList<VariableValue>();
			List<Variable_ensayo> ruleNodeVars = new ArrayList<Variable_ensayo>();
			holder.getNode().FindDependencyVariables(ruleNodeVars);			
			for (Variable_ensayo v : ruleNodeVars){
				String vId = this.idUtil.ForGroup(v, true);
				Object val = null;	
				if(v.getSeccion().getHojaCrd().getId() == this.gestionarHoja.getHoja().getHojaCrd().getId()){
					if(this.restorePhase == true && v.getGrupoVariables() != null) //this case happens on executing all rules at restore phase					
						val = this.gestionarHojaGrupoControlador.getData().getData().get(v.getId()).getValue();					
					else {
						if(v.getPresentacionFormulario().getNombre().equals("file")){//the metadata of the file upload component is in a hidden input so this must be done!! 
							byte[] data = this.gestionarHojaGrupoControlador.getData().getData().get(v.getId()).getData();
							String fileName = this.gestionarHojaGrupoControlador.getData().getData().get(v.getId()).cleanFileName();
							FileSigec f = new FileSigec();
							f.setName(fileName);
							f.setSize(data.length);
							f.setType("");	
							val = f.valueAsjsonString();
						} else {
							UIInput comp = FindComponent(vId);
							val = comp.getValue();
						}
					}
				} else {//if this variable belongs to another HojaCRD, which means, that is not in the html form and its value must be retrieved from the database
					long idHojaCrdVar = v.getSeccion().getHojaCrd().getId();					
					long IdSujeto = this.gestionarHoja.getIdSujeto();
					Sujeto_ensayo sujeto = this.entityManager.find(Sujeto_ensayo.class, IdSujeto);
					Cronograma_ensayo cronograma = sujeto.getGrupoSujetos().getCronogramas().iterator().next();
					MomentoSeguimientoGeneral_ensayo momentoGeneral = null;					
					Set<MomentoSeguimientoGeneral_ensayo> momentosGenerales = cronograma.getMomentoSeguimientoGenerals();
					for (MomentoSeguimientoGeneral_ensayo mg : momentosGenerales){
						Set<MomentoSeguimientoGeneralHojaCrd_ensayo> mgh = mg.getMomentoSeguimientoGeneralHojaCrds();
						for (MomentoSeguimientoGeneralHojaCrd_ensayo gh : mgh){
							if(gh.getHojaCrd().getId() == idHojaCrdVar){	
								momentoGeneral = mg;			
								break;
							}
						}
						if(momentoGeneral != null)
							break;
					}							
					MomentoSeguimientoEspecifico_ensayo momentoEspecifico = (MomentoSeguimientoEspecifico_ensayo) this.entityManager.createQuery("select e from MomentoSeguimientoEspecifico_ensayo e where e.momentoSeguimientoGeneral.id = :msg and e.sujeto.id = :sujeto").setParameter("msg", momentoGeneral.getId()).setParameter("sujeto", sujeto.getId()).getSingleResult();									
					CrdEspecifico_ensayo crdEspVarExtern = (CrdEspecifico_ensayo) this.entityManager.createQuery("select e from CrdEspecifico_ensayo e where e.momentoSeguimientoEspecifico.id = :idMomento and e.hojaCrd.id = :idHoja").setParameter("idHoja", idHojaCrdVar).setParameter("idMomento", momentoEspecifico.getId()).getSingleResult();
					long idCrdEsp = crdEspVarExtern.getId();					
					VariableDato_ensayo dato = (VariableDato_ensayo) this.entityManager.createQuery("select NomVar from VariableDato_ensayo NomVar where NomVar.variable.id = :Variable and NomVar.crdEspecifico.id = :Hoja").setParameter("Variable", v.getId()).setParameter("Hoja", idCrdEsp).getSingleResult();					
					val = dato.getValor();
				}				
				if(val == null )//|| val.toString().trim().isEmpty() TODO some validation must be done so the RuleNode evaluates correctly
					return null;
				VariableValue vval = new VariableValue(v,val);		
				testVariableValues.add(vval);
			}
			boolean result = holder.getNode().Visit(testVariableValues);
			return result;
		} catch(Exception e){
			return null;
		}
	}
			
	private ConditionsActionsHolder ConvertStringToHolder(String jsonString){
		ConditionsActionsHolder holder = null;
		try {
			holder = new ObjectMapper().readValue(jsonString, ConditionsActionsHolder.class);
		} catch (Exception e){
			e.printStackTrace();
		}
		return holder;
	}	
			
	public void addAjaxSupport(UIInput formComponent, ValueExpression reRenderProperty, String ajaxSupportId, String event, String onsubmit, String oncomplete){
		HtmlAjaxSupport es = new HtmlAjaxSupport();
		es.setId(ajaxSupportId);
		//es.setAction(me);
		es.setValueExpression("reRender", reRenderProperty);	
		es.setEvent(event);
		es.setOnsubmit(onsubmit);		
		es.setOncomplete(oncomplete);
		es.setAjaxSingle(true); 
		//es.setLimitToList(true);		
		formComponent.getFacets().put(formComponent.getId()+event, es);
	}
	
	public UIInput FindComponent(String componentId){
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIInput component  = (UIInput)viewRoot.findComponent(componentId);
		return component;
	}

	private void PutClientData(String code){
		this.clientDataList.add(code);
		try{
			this.clientData = new ObjectMapper().writeValueAsString(this.clientDataList);
		} catch (JsonProcessingException e){			
			e.printStackTrace();
		}
	}
	
	private void ClearClientData(){
		this.clientDataList.clear();
		this.setClientData(this.clientDataDefault);
	}
	
	public String getClientData(){		
		return clientData;
	}

	public void setClientData(String clientData){
		this.clientData = clientData;
	}

	public IRuleAction getLastUserInputAction(){
		return lastUserInputAction;
	}

	public void setLastUserInputAction(IRuleAction lastUserInputAction){
		this.lastUserInputAction = lastUserInputAction;
	}
	
}