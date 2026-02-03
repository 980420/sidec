package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.ConditionsActionsHolder;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode.RuleNodeType;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.IBinaryOperator;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem.RuleExpressionItemType;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.testvalues.VariableValue;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Regla_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.component.state.TreeState;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udojava.evalex.Expression;

@Name("RuleConditionsManager")
@Scope(ScopeType.CONVERSATION)
public class RuleConditionsManager 
{
   RuleNode firstNode;
   RuleNode nodeToChangeVariable;
   Variable_ensayo variableToChange;
   String nameVariableToChange;
   List<RuleNode> nodes;
   List<String> conditionsNames;
   @In
	FacesMessages facesMessages;
   @In
   EntityManager entityManager;
   List<Estudio_ensayo> estudios;
   
   Map<String,RuleNodeType> ruleNodeTypes;
   
   List<VariableValue> testVariableValues;
   int lastRuleNodeId = 0;
   
   @In(create=true)
   RuleNodeErrorManager ruleNodeErrorManager;
   
   @Create
   @Begin(join=true)
   public void init()
   {	   
	   reinit();
   }
   
   public void reinit()
   {	   
	   Expression expression = new Expression("1+1/3");
	   BigDecimal result = expression.eval();
	   estudios = entityManager.createQuery("select e from Estudio_ensayo e order by e.nombre DESC").getResultList();
	   for (Estudio_ensayo estudio : estudios) {
	   for (GrupoSujetos_ensayo g : estudio.getGrupoSujetoses()) {
			for (Cronograma_ensayo c : g.getCronogramas()) 
			{
				for (MomentoSeguimientoGeneral_ensayo m : c.getMomentoSeguimientoGenerals()) 
				{
//					for (HojaCrd_ensayo h : m.getHojaCrds()) 
//					{
//						String name = h.getNombreHoja();
//						name = h.getNombreHoja();
//					}
				}
			}
	   }
		}
	   firstNode = new RuleNode();
	   firstNode.setType(RuleNodeType.All);
	   lastRuleNodeId = 0;
	   Variable_ensayo var = entityManager.find(Variable_ensayo.class, 1001000000000001169L/*1001000000000001163L*/);
	   firstNode.setVariable(var);
	   	   
	   /*RuleNode childNode = new RuleNode();
	   childNode.setType(RuleNodeType.Any);
	   
	   RuleNode secondNode = new RuleNode();
	   secondNode.setType(RuleNodeType.None);
	   
	   RuleNode thirdNode = (new RuleNode());
	   thirdNode.type = RuleNodeType.Expression;
	   thirdNode.setLevel(3);
	   
	   secondNode.childAdd(thirdNode);
	   secondNode.setLevel(2);
	   
	   childNode.childAdd(secondNode);	   
	   childNode.setLevel(1);
	   
	   firstNode.childAdd(childNode);
	   nodes = new ArrayList<RuleNode>();
	   nodes.add(firstNode);*/
	   	   
	   conditionsNames = new ArrayList<String>();	   
	   
	   /*RuleNode thirdNode = (new RuleNode());
	   thirdNode.type = RuleNodeType.Expression;
	   thirdNode.setLevel(3);*/
	   testVariableValues = new ArrayList<VariableValue>();
   }
   
   public void LoadConditions(ConditionsActionsHolder holder) throws JsonParseException, JsonMappingException, IOException
   {	 
	  testVariableValues.clear(); 
	  firstNode = holder.getNode();	  
	  PropagateVariableToNestedNode(firstNode);
	  lastRuleNodeId = firstNode.lastGivenId;
	  PickTestVariables(firstNode);
   }
   
   public void SaveConditions()
   {
	  firstNode.setLastGivenId(lastRuleNodeId);	 
   }
   
   private void PropagateVariableToNestedNode(RuleNode node)//this is necessary to set variable to nested conditions (except expressions), this is done on DB retrieval
   {
	   if(node.type!=RuleNodeType.Expression && node.parent!=null)
		   node.setVariable(node.parent.getVariable());
	   
	   for (RuleNode c : node.children) 
		   PropagateVariableToNestedNode(c);
   }
   
   private void PickTestVariables(RuleNode node)//finding variables to add as TestVariable, this is done on DB retrieval
   {
	   if(node.type == RuleNodeType.Expression)
	   {
		   ProcessTestVarForAddition(node.getVariable());
		   if(node.getValueIsExpression())
		   {
			   RuleExpressionValue rev = (RuleExpressionValue)node.getValue();
			   for (RuleExpressionItem r : rev.getTarget()) 
				{
					if(r.getType().equals(RuleExpressionItemType.Variable))					
						ProcessTestVarForAddition((Variable_ensayo)r.getValue());								
				}
		   }
	   }
	   else
	   {
		   for (RuleNode r : node.children) 
		   {
			   PickTestVariables(r);
		   }
	   }
   }
   
   
   public List<String> SuggestionValues(Variable_ensayo n)
   {
	   List<String> list = entityManager.createQuery("select distinct nv.valor from Variable_ensayo v inner join v.nomenclador n inner join n.nomencladorValors nv where v.id=:vid and nv.valor!=null").setParameter("vid", n.getId()).getResultList();
	   return list;
   }
   public List<SelectItem> SuggestionValuesAsSelectItem(Variable_ensayo n)
   {
	   if(n==null)
		   return new ArrayList<SelectItem>();
	   List<String> vals = SuggestionValues(n);
	   List<SelectItem> items = new ArrayList<SelectItem>();
	   for (String string : vals) {
		   items.add(new SelectItem(string, string));
	   }
	   return items;
   }
   
   String readableConditions;
   public void Eval()
   {	  
	   
	   readableConditions = firstNode.children.size()>0 ? firstNode.describe(""):"";
	   if(readableConditions.isEmpty())
		{
			if(firstNode.getType()==RuleNodeType.All || firstNode.getType()==RuleNodeType.None)
				readableConditions = "VERDADERO";
			else
				readableConditions = "FALSO";
		}
	   String json = null;
	   RuleNode lnode = null;
	   try 
	   {	   
		   	 List<RuleNode> nods = new ArrayList<RuleNode>();
		   	 nods.add(firstNode);
			 json = new ObjectMapper().writeValueAsString(firstNode);
			 lnode = new ObjectMapper().readValue(json, RuleNode.class);
			 int m = 0;
			
			 boolean res = firstNode.Visit(testVariableValues);
			   /*if(res)
				   facesMessages.add("Se cumple");
			   else 
				   facesMessages.add("No se cumple");*/
			
	   } catch (JsonProcessingException e) 
	   {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   } catch (IOException e) 
	   {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	  
   }
   public String getReadableConditions() {
	return readableConditions;
}

public void setReadableConditions(String readableConditions) {
	this.readableConditions = readableConditions;
}

public void removeNode(RuleNode n)
   {
	   if(n.getValueIsExpression())//before the node is actually removed a check of possibly existing variables must be processed
	   {
		   RuleExpressionValue rev = (RuleExpressionValue)n.value;
		   for (RuleExpressionItem rei : rev.getTarget()) 
		   {
			   if(rei.getType().equals(RuleExpressionItemType.Variable))
			   {
				   ProcessTestVarForRemoval((Variable_ensayo)rei.getValue());
			   }
		   }
	   }
	   
	   n.parent.children.remove(n);
	   ProcessTestVarForRemoval(n.variable);//this is the left member variable
   }
   
   public void NodeToNormalValue(RuleNode n)
   {
	   if(n.getValueIsExpression())//before the node expression is actually removed a check of possibly existing variables must be processed
	   {
		   RuleExpressionValue rev = (RuleExpressionValue)n.value;
		   for (RuleExpressionItem rei : rev.getTarget()) 
		   {
			   if(rei.getType().equals(RuleExpressionItemType.Variable))
			   {
				   ProcessTestVarForRemoval((Variable_ensayo)rei.getValue());
			   }
		   }
	   }
	   
	   n.ToNormalValue();
   }
   
   public void addExpression(RuleNode n)
   {
	   n.addExpression().setId(++lastRuleNodeId);
	   ProcessTestVarForAddition(n.variable);
	   //ruleNodeErrorManager.putError(lastRuleNodeId, "no!");
   }
   /*
    * The whole idea of use repetitions avoids to check the tree, which is a performance gain.
    * This method check if a test variable exists, if so increase is repetitions otherwise create it.
    * */
   public void ProcessTestVarForAddition(Variable_ensayo v)
   {
	   VariableValue tv = TestVariablesContain(v);
	   if(tv==null)	   
	   {
		   Object val = null;
		   if(v.getPresentacionFormulario().getNombre().equals("checkbox") && !v.getTipoDato().getCodigo().equals("BL"))
			   val = new Object[10];//necessary coz the component requires it, with 10 is working, as long as there is no need to restore values this is OK
		   testVariableValues.add(new VariableValue(v,val));
	   }
	   else
		   tv.IncreaseReps();
   }
   
   /*
    * The whole idea of use repetitions avoids to check the tree, which is a performance gain.
    * This method check if a test variable has been completely removed, if it does is deleted otherwise its repetitions are decreased.
    * */
   public void ProcessTestVarForRemoval(Variable_ensayo v)
   {
	   VariableValue tv = TestVariablesContain(v);
	   if(tv!=null)
	   {
		   tv.DecreaseReps();	   
		   if(tv.getVariableReps()==0)
			   testVariableValues.remove(tv);
	   }
   }
   
   public void addSubCondition(RuleNode n)
   {
	   n.addSubCondition().setId(++lastRuleNodeId);;
   }
   
   private VariableValue TestVariablesContain(Variable_ensayo v)
   {	  
	   for (VariableValue vv : testVariableValues) 
	   {
		   if(vv.getVariable().getId() == v.getId())
		   {
			   return vv;
		   }
	   }
	   return null;
	   
   }
   //this is used in the modal to change a variable of a node
   public void ChangeVariable(Variable_ensayo v)
   {
	   if(nodeToChangeVariable.getVariable()==v)return;
	   VariableValue tv = TestVariablesContain(nodeToChangeVariable.getVariable());
	   tv.DecreaseReps();
	   if(tv.getVariableReps()==0)
		   testVariableValues.remove(tv);
	   if(nodeToChangeVariable.variable.getTipoDato().getId()!=v.getTipoDato().getId())//if the type is not equal the value can produce an error
	   {		   
		   nodeToChangeVariable.setValue(null);
	   }
	   
	   nodeToChangeVariable.setVariable(v);	   
	   nodeToChangeVariable.setType(nodeToChangeVariable.getType());//this is to trigger an operators reload
	   
	   tv = TestVariablesContain(v);
	   if(tv==null)	   
	   {
		   Object val = null;
		   if(v.getTipoDato().getCodigo().equals("NOM") && v.getPresentacionFormulario().getNombre().equals("checkbox"))
			   val = new Object[10];
		   testVariableValues.add(new VariableValue(v,val));
	   }
	   else
		   tv.IncreaseReps();   	   
   }   
   
   public String CorrectId(String formId, int nodeid)
   {
	   return String.format("Richfaces.componentControl.performOperation(event,'#%s\\\\:cm%s','show',{},false)", formId, String.valueOf(nodeid));
   }


public RuleNode getFirstNode() {
	return firstNode;
}

public void setFirstNode(RuleNode firstNode) {
	this.firstNode = firstNode;
}


public List<RuleNode> getNodes() {
	return nodes;
}


public void setNodes(List<RuleNode> nodes) {
	this.nodes = nodes;
}


public List<String> getConditionsNames() {
	return conditionsNames;
}


public void setConditionsNames(List<String> conditionsNames) {
	this.conditionsNames = conditionsNames;
}



public List<Estudio_ensayo> getEstudios() {
	return estudios;
}



public void setEstudios(List<Estudio_ensayo> estudios) {
	this.estudios = estudios;
}



public RuleNode getNodeToChangeVariable() {
	return nodeToChangeVariable;
}



public void setNodeToChangeVariable(RuleNode nodeToChangeVariable) {
	this.nodeToChangeVariable = nodeToChangeVariable;
	//variableToChange = null;	
	variableToChange= nodeToChangeVariable.variable;
	nameVariableToChange=variableToChange.getNombreVariable();
	//nameVariableToChange = null;
	//This piece of code is to collapse all nodes when the VariableSelection (when clicking a node variable to change it) modal is open. 
	//This is necessary because reRender is not working on the tree, depite it works perfectly on other components at the same level (eg. selected variable details template)
	//seems a bug
	try 
	{
		UIComponent comp = FacesContext.getCurrentInstance().getViewRoot().findComponent("varSelForm");
		HtmlTree c = (HtmlTree)comp.getChildren().get(0).getChildren().get(2);//TODO search the tree in another way
		c.queueCollapseAll();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public EntityManager getEntityManager() {
	return entityManager;
}

public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
}

public List<VariableValue> getTestVariableValues() {
	return testVariableValues;
}

public void setTestVariableValues(List<VariableValue> testVariableValues) {
	this.testVariableValues = testVariableValues;
}

public Variable_ensayo getVariableToChange() {
	return variableToChange;
}

public void setVariableToChange(Variable_ensayo variableToChange)throws Exception{
	this.variableToChange = variableToChange;
	if(variableToChange!=null)
		nameVariableToChange = variableToChange.getNombreVariable();
	
}

public String getNameVariableToChange()  {
	return nameVariableToChange;
	
}

public void setNameVariableToChange(String nameVariableToChange) {
	this.nameVariableToChange = nameVariableToChange;
}
   
}
