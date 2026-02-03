package gehos.ensayo.ensayo_disenno.session.reglas;

import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;
import gehos.comun.reportes.session.FileType;
import gehos.ensayo.codebase.genericComponents.yesNoModal.GenericYesNoModalPanel;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.ConditionsActionsHolder;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.RuleActionExecuter;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.SendEmailRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.executionTypes.ExecutionTypes;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleConditionsManager;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode.RuleNodeType;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.testvalues.VariableValue;
import gehos.ensayo.ensayo_disenno.session.reglas.util.TraitRuleComponent;
import gehos.ensayo.ensayo_disenno.session.reglas.util.Useful;
import gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.ListadoControler_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoRegla_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Regla_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.Variable_ensayo;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.jboss.el.MethodExpressionImpl;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@AutoCreate
@Name("ReglasControlador")
@Scope(ScopeType.CONVERSATION)
public class ReglasControlador extends GenericYesNoModalPanel
{
	@In
	FacesMessages facesMessages;
	
	@In
	EntityManager entityManager;

	@In(create=true)
	RuleConditionsManager RuleConditionsManager;

	@In
	TraitRuleComponent traitRuleComponent;

	@In SeguridadEstudio seguridadEstudio;
	
	@In
	Usuario user;
	
	ListadoControler_ensayo<ReglaWrapper> listadoReglas = new ListadoControler_ensayo<ReglaWrapper>(new ArrayList<ReglaWrapper>());

	Estudio_ensayo estudio;
	List<Estudio_ensayo> estudios;

	
	boolean selVariableDependiente = true;

	List<String> ejecucionesRegla;
	List<String> eventosEjecucion;

	// datos de una regla
	String reglaNombre;
	String reglaEjecucion;
	String reglaEventoEjecucion;

	List<IRuleAction> actionsAvailable;
	List<IRuleAction> actionsConfigured;
	IRuleAction actionSelected;
	boolean modifiyingAction = false;
	boolean modifiyingRule = false;
	Regla_ensayo reglaModify = null;

	//fields for interpretation
	String ifPart,thenPart,elsePart;
	String si,sino,entonces;
	Regla_ensayo interpretedRule;
	
	//Exportar
	ReglasExport exportarControlador;
	private final String template = "reporteListadoReglas.jrxml";
	private Map<String, String> parametros;
	private List<String> subReports;
	
	private List<String> filesFormatCombo; 
	private String fileformatToExport; 
	private String pathExportedReport;
	
	//no hay forma de llegar desde la variable hasta el grupo de sujetos
	//asi que al escoger la variable en el arbol tambien se escoge el momento-hoja
	MomentoSeguimientoGeneralHojaCrd_ensayo momentoGenHojaCrdForVar;
	Variable_ensayo variableSeleccionada;
	
	MomentoSeguimientoGeneralHojaCrd_ensayo momentoGenHojaCrd;
	
	MomentoSeguimientoGeneral_ensayo momentoGeneralSeleccionado;
	
	GrupoSujetos_ensayo grupoSujetosSeleccionado;

	//BEGIN-- Implementing deletion modals 
	
	// A form is not found by the framework inside another form. In this case is for the TestVariable template of the rule creation
	String fileValue;
	String fileVariableId;
	
	//Richtree binding, required by the tree, here for no particular purpose
	 	HtmlTree binding;
	
	
	@Override
	public void ExecuteYes(String modalId)
	{   
		if(modalId.endsWith("modal_eliminar_regla"))
		{
			Object o = super.GetObjectIdForModalId(modalId).getValue();
			if(o!=null)
			{
				Long ruleId = Long.parseLong(o.toString());
				Regla_ensayo r = entityManager.find(Regla_ensayo.class, ruleId);
				entityManager.remove(r);
				entityManager.flush();
				RefreshRuleList();
			}
		}
		else if(modalId.endsWith("modal_completar_regla"))
		{
			CompleteRules();
		}
		else if(modalId.endsWith("modal_descompletar_regla"))
		{
			DesCompleteRules();
		}
			
	}
	//END-- Implementing deletion modals 
	
	public boolean ShowRuleCompletionButton()
	{		
		if(grupoSujetosSeleccionado==null)
			return false;
		
		boolean esGerente = false;
		for (Role r : user.getRoles()) 
		{
			if("ecGerDatos".equals(r.getCodigo()))
			{
				esGerente=true;break;
			}
		}
		
		Cronograma_ensayo c = (Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:pid").setParameter("pid", grupoSujetosSeleccionado.getId()).getSingleResult();		
		entityManager.refresh(c);
		
		return esGerente && c.getEstadoCronograma()!=null && c.getEstadoCronograma().getCodigo()==3 && c.getEstadoReglas()!=null && c.getEstadoReglas().getCodigo()!=2;
	}
	
	public boolean ShowRuleDesCompletionButton()
	{		
		if(grupoSujetosSeleccionado==null)
			return false;
		
		boolean esGerente = false;
		for (Role r : user.getRoles()) 
		{
			if("ecGerDatos".equals(r.getCodigo()))
			{
				esGerente=true;break;
			}
		}
		
		Cronograma_ensayo c = (Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:pid").setParameter("pid", grupoSujetosSeleccionado.getId()).getSingleResult();		
		entityManager.refresh(c);
		
		return esGerente && c.getEstadoCronograma()!=null && c.getEstadoCronograma().getCodigo()==3 && c.getEstadoReglas()!=null && c.getEstadoReglas().getCodigo()==2;
	}
	
	
	//Solamente se puede visualizar los botones de gestionar reglas si es Gerente de datos y (si el estado es 1 [Iniciado] o 4 [Desaprobado])
	public boolean IsCompletedRules()
	{
		boolean esGerente = false;
		for (Role r : user.getRoles()) 
		{
			if("ecGerDatos".equals(r.getCodigo()))
			{
				esGerente=true;
				break;
			}
		}
		if(!esGerente)
			return false;
		
		if(grupoSujetosSeleccionado==null && momentoGeneralSeleccionado==null && this.momentoGenHojaCrd==null && variableSeleccionada==null)
			return false;		
		
		GrupoSujetos_ensayo g = null;
		
		if(grupoSujetosSeleccionado!=null)
			g = grupoSujetosSeleccionado;
		else if(momentoGeneralSeleccionado!=null)
			g = momentoGeneralSeleccionado.getCronograma().getGrupoSujetos();
		else if(this.momentoGenHojaCrd!=null)
		{				
			g = momentoGenHojaCrd.getMomentoSeguimientoGeneral().getCronograma().getGrupoSujetos();
		}
		else if(this.momentoGenHojaCrdForVar!=null)
		{					
			g = momentoGenHojaCrdForVar.getMomentoSeguimientoGeneral().getCronograma().getGrupoSujetos();
		}
		
		Cronograma_ensayo c = (Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:pid").setParameter("pid", g.getId()).getSingleResult();		
		entityManager.refresh(c);
		if(c.getEstadoReglas()==null)
			return false;
		
		return c.getEstadoReglas().getCodigo()==1 || c.getEstadoReglas().getCodigo()==4;				
	}

	public void CompleteRules()
	{
		Cronograma_ensayo c = (Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:pid").setParameter("pid", grupoSujetosSeleccionado.getId()).getSingleResult();		
		
		//estado completa
		EstadoRegla_ensayo er = (EstadoRegla_ensayo)entityManager.createQuery("select e from EstadoRegla_ensayo e where e.codigo=2").getSingleResult();
		c.setEstadoReglas(er);
		
		entityManager.persist(c);
		entityManager.flush();		
	}
	
	public void DesCompleteRules()
	{
		Cronograma_ensayo c = (Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:pid").setParameter("pid", grupoSujetosSeleccionado.getId()).getSingleResult();		
		
		//estado completa
		EstadoRegla_ensayo er = (EstadoRegla_ensayo)entityManager.createQuery("select e from EstadoRegla_ensayo e where e.codigo=1").getSingleResult();
		c.setEstadoReglas(er);
		
		entityManager.persist(c);
		entityManager.flush();		
	}
	
	
	
	public Long ReglasSize(long varId)
	{   
		Long c = (Long)entityManager.createQuery("select count(c) from Regla_ensayo c where c.variable.id=:pid").setParameter("pid", varId).getSingleResult();	
		return c;
	}
	
	public void Eval()
	{
		RuleConditionsManager.Eval();
		ifPart = RuleConditionsManager.getReadableConditions();	
		si = RuleConditionsManager.getReadableConditions();
		List<IRuleAction> allActions = actionsConfigured;
		List<IRuleAction> thenActions = new ArrayList<IRuleAction>();
		List<IRuleAction> elseActions = new ArrayList<IRuleAction>();

		for (IRuleAction a : allActions)		
		{				
			if(a.getExecuteIfRuleTrue())
				thenActions.add(a);
			else
				elseActions.add(a);
		}
		thenPart = InterpretActions(thenActions);
		elsePart = InterpretActions(elseActions);
		
		entonces = InterpretActionsExport(thenActions);
		sino = InterpretActionsExport(elseActions);
	}

	public void Interpret(Regla_ensayo r)
	{
		interpretedRule = r;
		
		if(r.getOpciones()==null)
		{
			ifPart = thenPart = elsePart = null;
			si = sino = entonces = null;
			return;
		}
		ConditionsActionsHolder holder = ConvertStringToHolder(r.getOpciones());
		RuleNode rootNode = holder.getNode();
		ifPart = rootNode.describe("");
		si = rootNode.describe("");
		if(ifPart.isEmpty())
		{
			if(rootNode.getType()==RuleNodeType.All || rootNode.getType()==RuleNodeType.None){
				ifPart = "VERDADERO";
				si = "VERDADERO";
				}
			else
				ifPart = "FALSO";
				si = "FALSO";
		}

		List<IRuleAction> allActions = holder.getActions();
		List<IRuleAction> thenActions = new ArrayList<IRuleAction>();
		List<IRuleAction> elseActions = new ArrayList<IRuleAction>();

		for (IRuleAction a : allActions)		
		{	
			a = traitRuleComponent.CreateActionComponent(a);
			if(a.getExecuteIfRuleTrue())
				thenActions.add(a);
			else
				elseActions.add(a);
		}
		thenPart = InterpretActions(thenActions);
		elsePart = InterpretActions(elseActions);	
		
		entonces = InterpretActionsExport(thenActions);
		sino = InterpretActionsExport(elseActions);
	}

	public String InterpretActions(List<IRuleAction> actions)
	{
		String out="";
		String imageTemplate = "<img style='width:18px' src='/gehos/resources/modEnsayo/img/actions/#{act.name}.png'>";
		for (IRuleAction a : actions)
		{			
			String image = imageTemplate.replace("#{act.name}", a.getName());
			out+= "<div class='rule-action'>"+image+ "<div>" + a.InstanceDescription() + "</div>"+"</div>";
		}
		return out;
	}
	
	public String InterpretActionsExport(List<IRuleAction> actions)
	{
		String out="";
		for (IRuleAction a : actions)
		{			
			
			out+= (a.InstanceDescription() + ", ");
		}
		return out;
	}

	public void PrepareModalForNewRule() {
		//TODO reset everything that could left opened for a previous opened rule creation (eg, if you left open a new action creation)
		RuleConditionsManager.reinit();
		RuleConditionsManager.getFirstNode().setVariable(variableSeleccionada);

		actionsConfigured.clear();
		
		superClear("formform:codigo");
		reglaNombre = null;
		superClear("formform:richcbRuleExec");
		reglaEjecucion = null;
		reglaEventoEjecucion = null;
		modifiyingRule = false;
		actionSelected = null;
		ifPart = thenPart = elsePart = null;
		si = sino = entonces = null;
		interpretedRule = null;
	}

	String ruleAdded = "";
	final String ruleAddedPositive = "yes";
	final String ruleAddedNegative = "no";
	
	public void adicionarNuevaRegla() 
	{
		ruleAdded = ruleAddedNegative;
		if(actionsConfigured.size()==0)
		{
			facesMessages.add("Debe adicionar al menos una acci\u00F3n.");
			return;
		}
		try 
		{
			String jsonString = null;
			//RuleConditionsManager.Eval();
			RuleConditionsManager.SaveConditions();
			RuleNode firstNode = RuleConditionsManager.getFirstNode();
			ConditionsActionsHolder holder = new ConditionsActionsHolder();
			holder.setNode(firstNode);
			holder.setActions(actionsConfigured);

			jsonString = new ObjectMapper().writeValueAsString(holder);

			String phase = ExecutionTypes.GetPhaseByKey(reglaEjecucion);
			//String event = ExecutionTypes.GetEventByKey(reglaEventoEjecucion);

			Regla_ensayo regla = new Regla_ensayo();
			
			if (modifiyingRule)
				regla = reglaModify;
			else
			{
				regla.setVariable(variableSeleccionada);				
			}

			if(ExecutionTypes.PHASEFILL.equals(phase))
			{
				Variable_ensayo v = null;
				if(modifiyingRule)
					v = regla.getVariable();
				else
					v = variableSeleccionada;
				
				String e = detectBestEventFor(v);
				regla.setExecEvent(e);
			}
			
			regla.setOpciones(jsonString);			
			regla.setExecPhase(phase);			
			regla.setNombre(reglaNombre);
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					user.getId());
			regla.setUsuario(usuario);

			// ReglaAux r = new ReglaAux();
			// r.setNombre(reglaNombre);
			// r.setFase(reglaEjecucion);
			// r.setEvento(reglaEventoEjecucion);
			// r.actions.addAll(actionsConfigured);
			regla.setTipoRegla(" ");
			regla.setTipoRegla(" ");
			entityManager.persist(regla);
			entityManager.flush();
			ruleAdded = ruleAddedPositive;
			SetTreeElementsNull(this.variableSeleccionada);	
		} 
		catch (Exception e) 
		{			
			e.printStackTrace();
		}
	}
	
	private String detectBestEventFor(Variable_ensayo v)
	{
		String dataTypeCode = v.getTipoDato().getCodigo();
		String event = ExecutionTypes.ONBLUR;
		if(dataTypeCode.matches("INT|ST|REAL|DATE"))
			event = ExecutionTypes.ONBLUR;
		else if(dataTypeCode.equals("NOM"))
		{
			if(v.getPresentacionFormulario().getNombre().equals("checkbox|radio|file"))
				event = ExecutionTypes.ONCHANGE;
			else
				event = ExecutionTypes.ONSELECT;
		}
		else if(dataTypeCode.equals("FILE"))
			event = ExecutionTypes.ONSELECT;
		
		return event;
	}

	public void InvertRule(IRuleAction action)
	{
		IRuleAction opposite = action.OppositeAction();
		IRuleAction oppositeComponent = traitRuleComponent.CreateActionComponent(opposite);
		Map<String, Object> state = action.GetComponentState();
		oppositeComponent.setComponentState(state);
		oppositeComponent.setExecuteIfRuleTrue(!action.getExecuteIfRuleTrue());		
		actionsConfigured.add(oppositeComponent);
	}

	public void RemoveRule(Regla_ensayo rule) {
		entityManager.remove(rule);
		entityManager.flush();
		RefreshRuleList();
	}

	public void RemoveAction(IRuleAction a)
	{
		actionsConfigured.remove(a);
	}

	private void LoadVariableData() {
		interpretedRule = null;		
	}

	public String describeRuleExec(Regla_ensayo r)
	{
		if(r==null) return "";
		
		String phase = "";
		String event = null;

		if(ExecutionTypes.PHASEFILL.equals(r.getExecPhase()))
			phase = "Al llenar";
		else if(ExecutionTypes.PHASESAVE.equals(r.getExecPhase()))
			phase = "Al guardar";
		else phase = "Al llenar y guardar";

		if(ExecutionTypes.ONBLUR.equals(r.getExecEvent()))
			event = "Al perder foco";
		else if(ExecutionTypes.ONCHANGE.equals(r.getExecEvent()))
			event = "Al cambiar valor";
		else if(ExecutionTypes.ONSELECT.equals(r.getExecEvent()))
			event = "Al seleccionar";

		return String.format("%s %s", phase, event==null?"":"> "+event);		

	}

	public String describeRuleActions(Regla_ensayo r)
	{
		if(r==null || r.getOpciones()==null) return "";
		
		ConditionsActionsHolder holder = ConvertStringToHolder(r.getOpciones());
		List<String> actionNames = Lists.transform(holder.getActions(), new Function<IRuleAction, String>() {
			@Override
			public String apply(IRuleAction a) {				
				return String.format("%s (%s)", a.getLabel(), a.getExecuteIfRuleTrue() ? "Si":"No");
			}
		});
		String out = Useful.join(actionNames, ", ");
		return out;
	}
	private ConditionsActionsHolder ConvertStringToHolder(String jsonString) {
		ConditionsActionsHolder holder = null;
		try {
			holder = new ObjectMapper().readValue(jsonString,
					ConditionsActionsHolder.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;
	}

	public void LoadRule(Regla_ensayo regla, Variable_ensayo v) {

		try 
		{
			
			if (regla != null) {
				superClear("formform:codigo");				
				this.reglaNombre = regla.getNombre();
				
				superClear("formform:richcbRuleExec");
				this.reglaEjecucion = ExecutionTypes.GetPhaseByValue(regla
						.getExecPhase());
				
				if (regla.getExecEvent() != null) {
					this.reglaEventoEjecucion = ExecutionTypes
							.GetEventByValue(regla.getExecEvent());
				}

				ConditionsActionsHolder holder = ConvertStringToHolder(regla
						.getOpciones());

				holder.getNode().setVariable(v);				
				RuleConditionsManager.LoadConditions(holder);

				List<IRuleAction> actionsDB = holder.getActions();
				List<IRuleAction> actionsComponents = new ArrayList<IRuleAction>();
				for (IRuleAction a : actionsDB) {
					IRuleAction aPrime = traitRuleComponent.CreateActionComponent(a);
					actionsComponents.add(aPrime);
				}

				actionsConfigured = actionsComponents;

				modifiyingRule = true;
				reglaModify = regla;
				actionSelected = null;
				ifPart = thenPart = elsePart = null;
				si = sino = entonces = null;
				interpretedRule = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ChangeVariable(Variable_ensayo v) 
	{
		RuleConditionsManager.ChangeVariable(v);
		ifPart = thenPart = elsePart = null;
		si = sino = entonces = null;
		
	}
	public void superClear(String fullId) 
	{
		try 
		{		
			UIViewRoot viewRoot = javax.faces.context.FacesContext
					.getCurrentInstance().getViewRoot();
			UIInput formComponent = (UIInput)viewRoot
					.findComponent(fullId);
	
			formComponent.setValue(null);
			formComponent.setSubmittedValue(null);
			formComponent.setLocalValueSet(false);
			formComponent.setValid(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		
	}

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	@Transactional
	public void inicio() {	
		this.filesFormatCombo =new ArrayList<String>(); 
    	this.filesFormatCombo.add("PDF");
    	this.filesFormatCombo.add("WORD");
    	this.filesFormatCombo.add("EXCEL");

		/*estudios = entityManager.createQuery(
				"select e from Estudio_ensayo e order by e.nombre DESC")
				.getResultList();*/

		/*estudios = new ArrayList<Estudio_ensayo>();
		estudios.add(seguridadEstudio.EstudioActivo());*/
		estudio = seguridadEstudio.EstudioActivo();
		estudio = entityManager.merge(estudio);
		entityManager.refresh(estudio);
		Hibernate.initialize(estudio.getGrupoSujetoses());
		//entityManager.refresh(estudio.getGrupoSujetoses());
		for (GrupoSujetos_ensayo g : estudio.getGrupoSujetoses()) { 
			for(Cronograma_ensayo c : g.getCronogramas()) { 
				for(MomentoSeguimientoGeneral_ensayo m : c.getMomentoSeguimientoGenerals()) 
				{
					m.setMomentoSeguimientoGeneralHojaCrds(ordenarPorNombre(m.getMomentoSeguimientoGeneralHojaCrds()));	
					Hibernate.initialize(m.getMomentoSeguimientoGeneralHojaCrds());			
					for (MomentoSeguimientoGeneralHojaCrd_ensayo s : m.getMomentoSeguimientoGeneralHojaCrds()) 
					{  						  
						for (Seccion_ensayo sec : s.getHojaCrd().getSeccions()) {
							Hibernate.initialize(sec.getGrupoVariableses());
							for (GrupoVariables_ensayo gv : sec.getGrupoVariableses()) {
								Hibernate.initialize(gv.getVariables());
							}
							for (Variable_ensayo var : sec.getVariables()) {

							}
						}

					}			  

				} 

			}

		}


		ejecucionesRegla = new ArrayList<String>(ExecutionTypes.GetPhases()
				.keySet());

		eventosEjecucion = new ArrayList<String>(ExecutionTypes.GetEvents()
				.keySet());

		RuleActionExecuter executer = new RuleActionExecuter();
		actionsAvailable = executer.Actions();

		actionsConfigured = new ArrayList<IRuleAction>();
	}
	
	private Set<MomentoSeguimientoGeneralHojaCrd_ensayo> ordenarPorNombre(Set<MomentoSeguimientoGeneralHojaCrd_ensayo> set) {
        List<MomentoSeguimientoGeneralHojaCrd_ensayo> lista = new ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>(set);

        Comparator<MomentoSeguimientoGeneralHojaCrd_ensayo> comparador = new Comparator<MomentoSeguimientoGeneralHojaCrd_ensayo>() {
            @Override
            public int compare(MomentoSeguimientoGeneralHojaCrd_ensayo momento1, MomentoSeguimientoGeneralHojaCrd_ensayo momento2) {
                String nombre1 = momento1.getHojaCrd().getNombreHoja();
                String nombre2 = momento2.getHojaCrd().getNombreHoja();
                return nombre1.compareTo(nombre2);
            }
        };

        Collections.sort(lista, comparador);

        return new LinkedHashSet<MomentoSeguimientoGeneralHojaCrd_ensayo>(lista);
    }

	//currently if the action is modified the changes remains even if the Cancel button is clicked, this happens because every action is actually a SEAM COMPONENT
	//this is a workaround to solve this
	
	Map<String, Object> modifyeeActionComponentState = null;
	public void SaveActionConf() 
	{			
		boolean isValid = actionSelected.Validate();
		if(!isValid)
			return;
		
		if (!modifiyingAction)
			actionsConfigured.add(actionSelected);
		actionSelected = null;
	}

	public void CancelActionConf() {		
		actionSelected.setComponentState(modifyeeActionComponentState);
		actionSelected.LoadComponentState();
		actionSelected = null;
	}


	String itype;// instance type
	IRuleAction actionInstance;
	int createRuleActions = 0;
	String idModal;

	public void CreateNewAction(IRuleAction ruleAction) {
		modifiyingAction = false;
		IRuleAction newAction = traitRuleComponent.CreateActionComponent(ruleAction);
		newAction.setRootVariable(RuleConditionsManager.getFirstNode().getVariable());
		actionSelected = newAction;
		actionSelected.setExecuteIfRuleTrue(true);	
	}

	public void setInstance() {
		try {
			JSONObject vv = new JSONObject(itype);
			Object id = vv.get("id");
			Object actionName = vv.get("type");
			String key = String.format("%s_%s", actionName, id);
			// Context appContext = Contexts.getApplicationContext();
			// Context sessContext = Contexts.getSessionContext();
			Context convContext = Contexts.getConversationContext();
			Object ee = convContext.get("entityManager");
			if (!convContext.isSet(key)) {
				IRuleAction ruleAction = null;//findRuleActionBy(actionName.toString());
				Component c = new Component(ruleAction.getClass(), key,
						ScopeType.CONVERSATION, false, null, null);
				Object o = c.newInstance();

				actionInstance = (IRuleAction) o;// Component.getInstance(ruleAction.getClass(),
				// ScopeType.CONVERSATION,
				// true);

				convContext.set(key, actionInstance);
			} else {
				actionInstance = (IRuleAction) convContext.get(key);
			}
			Object usuSelec = ((SendEmailRuleAction) actionInstance)
					.getUsuariosSeleccionados();
			idModal = vv.get("modalid").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String ruleToSave;

	public String getItype() {
		return itype;
	}

	public void setItype(String itype) {
		this.itype = itype;
	}

	public IRuleAction getActionInstance() {
		return actionInstance;
	}

	public void setActionInstance(IRuleAction actionInstance) {
		this.actionInstance = actionInstance;
	}


	public String getRuleToSave() {
		return ruleToSave;
	}

	public void setRuleToSave(String ruleToSave) {
		this.ruleToSave = ruleToSave;
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}

	public Variable_ensayo getVariableSeleccionada() {
		return variableSeleccionada;
	}

	public void setVariableSeleccionada(Variable_ensayo variableSeleccionada) {
		this.variableSeleccionada = variableSeleccionada;
		LoadVariableData();
		SetTreeElementsNull(this.variableSeleccionada);
	}
	
	public void setVarMon(Variable_ensayo variableSeleccionada, MomentoSeguimientoGeneralHojaCrd_ensayo m) {
		this.variableSeleccionada = variableSeleccionada;		
		this.momentoGenHojaCrdForVar = m;
		LoadVariableData();
		SetTreeElementsNull(this.variableSeleccionada);
	}

	public List<Estudio_ensayo> getEstudios() {
		return estudios;
	}

	public void setEstudios(List<Estudio_ensayo> estudios) {
		this.estudios = estudios;
	}

	public boolean isSelVariableDependiente() {
		return selVariableDependiente;
	}

	public void setSelVariableDependiente(boolean selVariableDependiente) {
		this.selVariableDependiente = selVariableDependiente;
	}

	public String getReglaNombre() {
		return reglaNombre;
	}

	public void setReglaNombre(String reglaNombre) {
		this.reglaNombre = reglaNombre;
	}

	public List<String> getEjecucionesRegla() {
		return ejecucionesRegla;
	}

	public void setEjecucionesRegla(List<String> ejecucionesRegla) {
		this.ejecucionesRegla = ejecucionesRegla;
	}

	public String getReglaEjecucion() {
		return reglaEjecucion;
	}

	public void setReglaEjecucion(String reglaEjecucion) {
		this.reglaEjecucion = reglaEjecucion;
	}

	public List<String> getEventosEjecucion() {
		return eventosEjecucion;
	}

	public void setEventosEjecucion(List<String> eventosEjecucion) {
		this.eventosEjecucion = eventosEjecucion;
	}

	public String getReglaEventoEjecucion() {
		return reglaEventoEjecucion;
	}

	public void setReglaEventoEjecucion(String reglaEventoEjecucion) {
		this.reglaEventoEjecucion = reglaEventoEjecucion;
	}

	public List<IRuleAction> getActionsAvailable() {
		return actionsAvailable;
	}

	public void setActionsAvailable(List<IRuleAction> actionsAvailable) {
		this.actionsAvailable = actionsAvailable;
	}

	public List<IRuleAction> getActionsConfigured() {
		return actionsConfigured;
	}

	public void setActionsConfigured(List<IRuleAction> actionsConfigured) {
		this.actionsConfigured = actionsConfigured;
	}

	public IRuleAction getActionSelected() {
		return actionSelected;
	}

	public void setActionSelected(IRuleAction actionSelected) {
		this.actionSelected = actionSelected;
		modifiyingAction = true;
		this.modifyeeActionComponentState = actionSelected.GetComponentState();
		actionSelected.setExecuteIfRuleTrue(true);
	}
	
	List<ReglasExport> reglaSimples = new ArrayList<ReglasExport>();

	public List<ReglaWrapper> getReglas() 
	{
    	
		List<ReglaWrapper> reglas = new ArrayList<ReglaWrapper>();
		
		if(grupoSujetosSeleccionado!=null)
		{
			reglas = FetchReglas(grupoSujetosSeleccionado);
		}
		else if(momentoGeneralSeleccionado!=null)
		{
			reglas = FetchReglas(momentoGeneralSeleccionado,null);
		}	
		else if(momentoGenHojaCrd!=null && momentoGenHojaCrd.getHojaCrd()!=null)
		{
			reglas = FetchReglas(momentoGenHojaCrd.getHojaCrd(),null,null);
		}			 
		else if (this.variableSeleccionada != null)
			reglas = FetchReglas(this.variableSeleccionada,null,null,null);		
		
		return reglas;
	}
	@SuppressWarnings("unchecked")
	public List<ReglasExport> generarListaReglasExport() {
		
		// Limpiar la lista reglaSimples al principio del método
	    reglaSimples.clear();
	    
	    List<ReglaWrapper> listadoReglas = getReglas();
		
		//Es el valor del nombre estudio
		parametros = new HashMap<String, String>();
		this.parametros.put("nombreEstudio", this.estudio.getNombre());
		
	    for (ReglaWrapper wrapper : listadoReglas) {
	        Regla_ensayo r = wrapper.getRegla();
	        String variableNombre = r.getVariable().getNombreVariable();
	        String mensajeRegla ="";
	     // Inicializa el nombre de la hoja
	        String nombreHoja = ""; 
	        if (wrapper.getHoja() != null) {
	            nombreHoja = wrapper.getHoja().getNombreHoja(); // Asegúrate de que esto sea correcto
	        }
    
	     // Parsear el JSON de opciones
	        ConditionsActionsHolder holder = ConvertStringToHolder(r.getOpciones());
	        RuleNode rootNode = holder.getNode();
	        String ifPart = rootNode.describeOut("");
	        if (ifPart.isEmpty()) {
	            ifPart = (rootNode.getType() == RuleNodeType.All || rootNode.getType() == RuleNodeType.None) ? "VERDADERO" : "FALSO";
	        }
	        
	        List<IRuleAction> actions = holder.getActions();
//	        List<IRuleAction> entonces = new ArrayList<IRuleAction>();
//	        List<IRuleAction> sino = new ArrayList<IRuleAction>();
	        List<IRuleAction> thenActions = new ArrayList();
	        List<IRuleAction> elseActions = new ArrayList();
//	        
	        for (IRuleAction a : actions) {
	        	a = traitRuleComponent.CreateActionComponent(a);
	            if (a.getExecuteIfRuleTrue() && a.getDescription() != null) {
	            	thenActions.add(a);
	            } else {
	            	elseActions.add(a);
	            }
	        }
//
//	        // Concatenar entonces y sino, si sino existe
	        String entonces = InterpretActionsExport(thenActions);
	        String sino = InterpretActionsExport(elseActions);
//
//	        // Concatenar entonces y sino, si sino existe
	        if(!entonces.isEmpty()){
//	 	        for (String entonce : entonces)
	 				mensajeRegla += ("Entonces: " + entonces);
	        }
	      
	        if (!sino.isEmpty()) {
//	            for (String sn : sino)
					mensajeRegla += ("Sino: " + sino);
	        }
	        // Verificar si mensajeRegla es null y asignar una cadena vacia si es necesario
	        if (mensajeRegla.isEmpty()) {
	            mensajeRegla = "No hay condiciones valida para la regla";
	        }
	       
	        // Crear la instancia de ReglasExport utilizando el constructor con parámetros
	        ReglasExport simple = new ReglasExport( variableNombre , ifPart , mensajeRegla , r.getNombre(),nombreHoja);
	       
	        reglaSimples.add(simple);
	    }

	    return reglaSimples;
	}


	private List<ReglaWrapper> FetchReglas(Variable_ensayo v, HojaCrd_ensayo h, MomentoSeguimientoGeneral_ensayo m, GrupoSujetos_ensayo g)
	{
		List<ReglaWrapper> reglaWrappers = new ArrayList<ReglaWrapper>();
		List<Regla_ensayo> reglas = entityManager
				.createQuery("select r from Regla_ensayo r where r.variable.id=:pid")
				.setParameter("pid", v.getId())
				.getResultList();
		
		for (Regla_ensayo r : reglas) 
		{
			ReglaWrapper w = new ReglaWrapper();
			w.setRegla(r);
			w.setHoja(h);
			w.setMomento(m);
			w.setGrupo(g);
			
			reglaWrappers.add(w);
		}
		return reglaWrappers;
	}

	@SuppressWarnings("unchecked")
	private List<ReglaWrapper> FetchReglas(HojaCrd_ensayo h, MomentoSeguimientoGeneral_ensayo m, GrupoSujetos_ensayo gg)
	{
		this.filesFormatCombo = this.reportManager.fileFormatsToExport();
		List<ReglaWrapper> reglas = new ArrayList<ReglaWrapper>();
		Set<Seccion_ensayo> secciones = h.getSeccions();
		for (Seccion_ensayo s : secciones) 
		{
			
			List<Variable_ensayo> vars = (List<Variable_ensayo>)entityManager.createQuery("select variable from Variable_ensayo variable where (variable.eliminado = null or variable.eliminado = false) and variable.seccion.id = :idS and variable.grupoVariables = null").setParameter("idS", s.getId()).getResultList();
			//Set<Variable_ensayo> vars = s.getVariables();
			for (Variable_ensayo v : vars)
				reglas.addAll(FetchReglas(v,h,m,gg));

			Set<GrupoVariables_ensayo> grupos = s.getGrupoVariableses();
			for (GrupoVariables_ensayo g : grupos) 
			{
				vars = (List<Variable_ensayo>)entityManager.createQuery("select variable from Variable_ensayo variable where (variable.eliminado = null or variable.eliminado = false) and variable.grupoVariables.id = :idG").setParameter("idG", g.getId()).getResultList();
				for (Variable_ensayo v : vars)
					reglas.addAll(FetchReglas(v,h,m,gg));
			}				
		}
		return reglas;
	}
	private List<ReglaWrapper> FetchReglas(MomentoSeguimientoGeneral_ensayo m, GrupoSujetos_ensayo gg)
	{
		List<ReglaWrapper> reglas = new ArrayList<ReglaWrapper>();
		Set<MomentoSeguimientoGeneralHojaCrd_ensayo> moments = m.getMomentoSeguimientoGeneralHojaCrds();

		for (MomentoSeguimientoGeneralHojaCrd_ensayo mh : moments)
			if(mh.getEliminado() == false){
				reglas.addAll(FetchReglas(mh.getHojaCrd(),m,gg));	
			}
					

		return reglas;
	}

	private List<ReglaWrapper> FetchReglas(GrupoSujetos_ensayo g)
	{
		List<ReglaWrapper> reglas = new ArrayList<ReglaWrapper>();
		Cronograma_ensayo c = g.getCronogramas().iterator().next();
		Set<MomentoSeguimientoGeneral_ensayo> moments = c.getMomentoSeguimientoGenerals();

		for (MomentoSeguimientoGeneral_ensayo mh : moments)		
			reglas.addAll(FetchReglas(mh,g));		

		return reglas;
	}
	@In ReportManager reportManager; 
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reporteListadoReglas", parametros, reglaSimples, FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reporteListadoReglas", parametros, reglaSimples, FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reporteListadoReglas", parametros, reglaSimples, FileType.EXCEL_FILE);
		}
	}
	public String getIfPart() {
		return ifPart;
	}

	public void setIfPart(String ifPart) {
		this.ifPart = ifPart;
	}

	public String getThenPart() {
		return thenPart;
	}

	public void setThenPart(String thenPart) {
		this.thenPart = thenPart;
	}

	public String getElsePart() {
		return elsePart;
	}

	public void setElsePart(String elsePart) {
		this.elsePart = elsePart;
	}	
	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}

	public String getEntonces() {
		return entonces;
	}

	public void setEntonces(String entonces) {
		this.entonces = entonces;
	}

	public String getSino() {
		return sino;
	}

	public void setSino(String sino) {
		this.sino = sino;
	}
	public Regla_ensayo getInterpretedRule() {
		return interpretedRule;
	}

	public void setInterpretedRule(Regla_ensayo interpretedRule) {
		this.interpretedRule = interpretedRule;
	}
	
	public MomentoSeguimientoGeneral_ensayo getMomentoGeneralSeleccionado() {
		return momentoGeneralSeleccionado;
	}

	public void setMomentoGeneralSeleccionado(
			MomentoSeguimientoGeneral_ensayo momentoGeneralSeleccionado) {
		this.momentoGeneralSeleccionado = momentoGeneralSeleccionado;

		SetTreeElementsNull(this.momentoGeneralSeleccionado);
	}

	public GrupoSujetos_ensayo getGrupoSujetosSeleccionado() {
		return grupoSujetosSeleccionado;
	}

	public void setGrupoSujetosSeleccionado(GrupoSujetos_ensayo grupoSujetosSeleccionado) 
	{
		this.grupoSujetosSeleccionado = grupoSujetosSeleccionado;

		SetTreeElementsNull(this.grupoSujetosSeleccionado);
		
	}

	/**
	 * Sets to null every tree selectable element except the one specified
	 * @param except the only tree element that will not be set to null
	 * Note: it was tried to use an array instead of using ifs but did not work. Another alternative is out of context because those would involve much more work
	 */
	private void SetTreeElementsNull(Object except)
	{	
		interpretedRule = null;
		ifPart = thenPart = elsePart = null;
		si = sino = entonces = null;
		
		if(except==null)
			return;
		if(grupoSujetosSeleccionado!=null && grupoSujetosSeleccionado.getClass() != except.getClass())
			grupoSujetosSeleccionado = null;

		if(momentoGeneralSeleccionado!=null && momentoGeneralSeleccionado.getClass() != except.getClass())
			momentoGeneralSeleccionado = null;

		if(this.momentoGenHojaCrd!=null && this.momentoGenHojaCrd.getHojaCrd().getClass() != except.getClass())
			this.momentoGenHojaCrd = null;

		if(variableSeleccionada!=null && variableSeleccionada.getClass() != except.getClass())
			variableSeleccionada = null;
		
		RefreshRuleList();
		
		
	}
	
	private void RefreshRuleList()
	{
		List<ReglaWrapper> reglas = getReglas();
		listadoReglas = new ListadoControler_ensayo<ReglaWrapper>(reglas);
		
		interpretedRule = null;
		ifPart = thenPart = elsePart = null;
		si = sino = entonces = null;
	}

	public ListadoControler_ensayo<ReglaWrapper> getListadoReglas() {
		return listadoReglas;
	}

	public void setListadoReglas(ListadoControler_ensayo<ReglaWrapper> listadoReglas) {
		this.listadoReglas = listadoReglas;
	}

	public String getRuleAdded() {
		return ruleAdded;
	}

	public void setRuleAdded(String ruleAdded) {
		this.ruleAdded = ruleAdded;
	}
	public List<ReglasExport> getReglaSimples() {
		return reglaSimples;
	}

	public void setReglaSimples(List<ReglasExport> reglaSimples) {
		this.reglaSimples = reglaSimples;
	}
	public MomentoSeguimientoGeneralHojaCrd_ensayo getMomentoGenHojaCrd() {
		return momentoGenHojaCrd;
	}

	public void setMomentoGenHojaCrd(
			MomentoSeguimientoGeneralHojaCrd_ensayo momentoGenHojaCrd) {
		this.momentoGenHojaCrd = momentoGenHojaCrd;
		SetTreeElementsNull(this.momentoGenHojaCrd.getHojaCrd());
	}

	public MomentoSeguimientoGeneralHojaCrd_ensayo getMomentoGenHojaCrdForVar() {
		return momentoGenHojaCrdForVar;
	}
	public List<String> getFilesFormatCombo() { 
		return filesFormatCombo; 
	} 
 
	public void setFilesFormatCombo(List<String> filesFormatCombo) { 
		this.filesFormatCombo = filesFormatCombo; 
	} 
	public String getFileformatToExport() { 
		return fileformatToExport; 
	} 
 
	public void setFileformatToExport(String fileformatToExport) { 
		this.fileformatToExport = fileformatToExport; 
	} 
 
	public String getPathExportedReport() { 
		return pathExportedReport; 
	} 
 
	public void setPathExportedReport(String pathExportedReport) { 
		this.pathExportedReport = pathExportedReport; 
	} 
	
	public void setMomentoGenHojaCrdForVar(
			MomentoSeguimientoGeneralHojaCrd_ensayo momentoGenHojaCrdForVar) {
		this.momentoGenHojaCrdForVar = momentoGenHojaCrdForVar;
	}

	public boolean isModifiyingRule() {
		return modifiyingRule;
	}

	public void setModifiyingRule(boolean modifiyingRule) {
		this.modifiyingRule = modifiyingRule;
	}

	public String getFileValue() {
		return fileValue;
	}

	public void setFileValue(String fileValue) {
		this.fileValue = fileValue;
	}

	public String getFileVariableId() {
		return fileVariableId;
	}

	public void setFileVariableId(String fileVariableId) 
	{
		try 
		{
			this.fileVariableId = fileVariableId;
			Long id = Long.parseLong(fileVariableId);
			List<VariableValue> tests = RuleConditionsManager.getTestVariableValues();
			for (VariableValue vv : tests) 
			{
				if(vv.getVariable().getId()==id)
				{
					vv.setValue(fileValue);
					break;
				}
			}
		} catch (Exception e) 
		{
			// TODO: handle exception
		}
		
	}

	public HtmlTree getBinding() {
		return binding;
	}

	public void setBinding(HtmlTree binding) {
		this.binding = binding;
	}	

}
