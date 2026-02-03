package gehos.ensayo.ensayo_disenno.session.reglas; 
 
import gehos.ensayo.ensayo_disenno.session.reglas.Framework.Family; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleExpressionValue; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operations.IOperation; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.IBinaryOperator; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem.RuleExpressionItemType; 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.testvalues.VariableValue; 
import gehos.ensayo.entity.Variable_ensayo; 
 
import java.math.BigDecimal; 
import java.util.ArrayList; 
import java.util.Arrays; 
import java.util.Collection; 
import java.util.Collections; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
import java.util.Random; 
 
import javax.faces.component.UIComponent; 
import javax.faces.context.FacesContext; 
import javax.persistence.EntityManager; 
 
import org.jboss.seam.ScopeType; 
import org.jboss.seam.annotations.Begin; 
import org.jboss.seam.annotations.Create; 
import org.jboss.seam.annotations.In; 
import org.jboss.seam.annotations.Name; 
import org.jboss.seam.annotations.Out; 
import org.jboss.seam.annotations.Scope; 
import org.jboss.seam.contexts.Context; 
import org.jboss.seam.contexts.Contexts; 
import org.jboss.seam.faces.FacesMessages; 
import org.richfaces.component.html.HtmlTree; 
import org.richfaces.event.DropEvent; 
import org.richfaces.event.DropListener; 
import org.richfaces.json.JSONException; 
import org.richfaces.json.JSONObject; 
 
import com.google.common.base.Predicate; 
import com.google.common.collect.Collections2; 
import com.google.common.collect.Lists; 
import com.udojava.evalex.Expression; 
 
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleConditionsManager; 
 
@Name("dragDropBean") 
@Scope(ScopeType.CONVERSATION) 
public class DragDropExample implements DropListener { 
  private List<RuleExpressionItem> source; 
  private List<RuleExpressionItem> target; 
   
  String expValid; //to know if the last evaluated expression was valid 
  // 
  String nameVariableToChange; 
  Variable_ensayo variable; 
   
  RuleNode node; 
  RuleExpressionItem lastDroppedItem; 
  Object literalValue; 
   
  @In 
  RuleConditionsManager RuleConditionsManager; 
 
  @In 
  EntityManager entityManager; 
   
  @In  
  FacesMessages facesMessages; 
   
  Map<String, String> DataTypeCompatibility; 
 
  @Create 
  @Begin(join = true) 
  public void init() { 
    /*variable = entityManager.find(Variable_ensayo.class, 
        1001000000000001163L);*/ 
    DataTypeCompatibility = new HashMap<String, String>(); 
    DataTypeCompatibility.put(IBinaryOperator.DATETYPE, ""); 
    DataTypeCompatibility.put(IBinaryOperator.INTEGERTYPE, IBinaryOperator.REALTYPE); 
    DataTypeCompatibility.put(IBinaryOperator.REALTYPE, IBinaryOperator.INTEGERTYPE); 
  } 
 
  public void processDrop(DropEvent event) {        
        moveFramework((RuleExpressionItem) event.getDragValue());  
    }  
     
    public String getLastItem() 
    {       
      if(lastDroppedItem==null) 
        return "{}"; 
      else 
      { 
        JSONObject jo = new JSONObject(); 
        try  
        { 
        jo.put("label", lastDroppedItem.getLabel()); 
        jo.put("type", lastDroppedItem.getType()); 
         
      } catch (JSONException e) { 
        // TODO Auto-generated catch block 
        e.printStackTrace(); 
      } 
         
        return jo.toString(); 
      } 
    } 
     
  public Collection<RuleExpressionItem> getSource() { 
    return source; 
  } 
 
  public Collection<RuleExpressionItem> getTarget() { 
    return target; 
  } 
 
  public void moveFramework(RuleExpressionItem framework) { 
     
    RuleExpressionItem clon = framework.clone();     
    lastDroppedItem = clon; 
    if((framework.getType()==RuleExpressionItemType.Variable || framework.getType()==RuleExpressionItemType.Literal) && framework.getValue().equals("PHP")) 
      return; 
    target.add(clon); 
  } 
 
  public void removeExpItem(RuleExpressionItem framework) { 
    target.remove(framework); 
  } 
 
  public void reset() { 
    initList(); 
  } 
   
  public void save()  
  { 
    expValid = "ok"; 
    String leftHandVarDataType = node.getVariable().getTipoDato().getCodigo(); 
    boolean varIsMathType = leftHandVarDataType.equalsIgnoreCase("int") || leftHandVarDataType.equalsIgnoreCase("REAL"); 
     
    if(!varIsMathType) 
    { 
      if(target.size()>1) 
      { 
        facesMessages.addToControl("form","El tipo de dato fecha solo admite un s\u00EDmbolo"); 
        return; 
      } 
    } 
     
      List<RuleExpressionItem> targetCopy = new ArrayList<RuleExpressionItem>(); 
      targetCopy.addAll(target); 
      RuleExpressionValue rev = new RuleExpressionValue(targetCopy); 
      node.setValue(rev);     
       
      for (RuleExpressionItem r : target)  
      { 
        if(r.getType().equals(RuleExpressionItemType.Variable)) 
        { 
          RuleConditionsManager.ProcessTestVarForAddition((Variable_ensayo)r.getValue()); 
        }       
      } 
       
      if(varIsMathType) 
      { 
        boolean isMathWellformed = IsMathExpWellFormed(rev); 
        if(!isMathWellformed) 
        {       
          expValid = null; 
          facesMessages.addToControl("form","La expresi\u00F3n no esta bien formada"); 
          return; 
        }     
      } 
     
    target.clear(); 
  } 
   
  /** 
   * Check that the math expression is well-fornmed 
   * @param rev 
   * @return 
   */ 
  private boolean IsMathExpWellFormed(RuleExpressionValue rev) 
  { 
    try  
    {       
     
      String exp = "";     
       
      Random random = new Random(123456789); 
       
      for (RuleExpressionItem i : rev.getTarget())  
      { 
        if(i.getType().equals(RuleExpressionItemType.Variable)) 
        { 
          int val = random.nextInt();           
          exp+=val + " "; 
        }         
        else exp+=i.getLabel() + " "; 
      } 
      exp = exp.trim(); 
       
      org.mariuszgromada.math.mxparser.Expression t = new org.mariuszgromada.math.mxparser.Expression(exp); 
      boolean res = t.checkSyntax(); 
       
      //TODO the below method to show a more specific info 
      //t.getCopyOfInitialTokens().get(0). 
       
      return res; 
    }  
    catch (Exception e)  
    { 
      return false; 
    } 
     
  } 
   
  public boolean load()  
  { 
    if(this.node.getValueIsExpression()) 
    { 
      RuleExpressionValue exp = (RuleExpressionValue)this.node.getValue(); 
      this.target = exp.getTarget(); 
    } 
    else 
    { 
      target.clear(); 
    } 
    return true; 
  } 
   
  private void initList() { 
    source = Lists.newArrayList(); 
    target = Lists.newArrayList(); 
     
    source = SymbolsExp(); 
    /* 
     * for (IOperator o : IOperator.operators) { 
     * if(Arrays.asList(o.types()). 
     * contains(this.variable.getTipoDato().getCodigo())) source.add(new 
     * RuleExpressionItem(o.name(), 
     * RuleExpressionItem.IRuleExpressionItemType.Operator , "PHP")); } 
     */ 
 
  } 
   
  public List<RuleExpressionItem> SymbolsExp() 
  { 
    String acceptedType = "PHP"; 
   
  List<RuleExpressionItem> source = new ArrayList<RuleExpressionItem>(); 
  String leftHandVarDataType = node.getVariable().getTipoDato().getCodigo(); 
  boolean varIsMathType = leftHandVarDataType.equalsIgnoreCase("int") || leftHandVarDataType.equalsIgnoreCase("REAL"); 
   
  //special symbols 
  source.add(new RuleExpressionItem("Variable", 
      RuleExpressionItem.RuleExpressionItemType.Variable, acceptedType)); 
  if(varIsMathType) 
  { 
    source.add(new RuleExpressionItem("Literal", RuleExpressionItem.RuleExpressionItemType.Literal, acceptedType)); 
    source.add(new RuleExpressionItem("(", RuleExpressionItem.RuleExpressionItemType.ParenthesesOpen, acceptedType)); 
    source.add(new RuleExpressionItem(")", RuleExpressionItem.RuleExpressionItemType.ParenthesesClose, acceptedType)); 
     
  //specific symbols 
  //operators 
  for (IOperation o : IOperation.operations)  
  { 
    if(Arrays.asList(o.types()).contains(node.getVariable().getTipoDato().getCodigo())) 
    { 
      source.add(new RuleExpressionItem(o.symbol(), RuleExpressionItem.RuleExpressionItemType.Operator, acceptedType)); 
    } 
  } 
   
  } 
   
  return source; 
} 
   
  public void AcceptSelection() 
  { 
    if(variable.getTipoDato().getCodigo().equalsIgnoreCase("date") && target.size()==1) 
    { 
      facesMessages.addToControl("form","El tipo de dato fecha solo admite un s\u00EDmbolo"); 
      return; 
    } 
    lastDroppedItem.setLabel(variable.getNombreVariable()); 
    lastDroppedItem.setValue(variable); 
    target.add(lastDroppedItem);     
  } 
   
  //TODO: swap this method with SetVariable 
  public void TrySelectVariable(Variable_ensayo v)  
  { 
    if(!IsCompatible(v)) 
    { 
      facesMessages.add("La variable que intenta seleccionar (\"#0\") es incompatible con \"#1\".",v.getNombreVariable(), node.getVariable().getNombreVariable()); 
      return; 
    } 
    SetVariable(v); 
     
  } 
   
  public boolean IsCompatible(Variable_ensayo v)  
  { 
    String NodeDataType = node.getVariable().getTipoDato().getCodigo(); 
    String VarDataType = v.getTipoDato().getCodigo(); 
    if(VarDataType.equals(NodeDataType)) 
      return true;     
     
    return VarDataType.matches(DataTypeCompatibility.get(NodeDataType));     
  } 
 
  public void SetVariable(Variable_ensayo v) { 
    this.variable = v;   
    if(variable==null) 
      nameVariableToChange = null; 
    else       
      nameVariableToChange = variable.getNombreVariable(); 
  } 
   
  public void PrepareVariableSelection() 
  { 
    literalValue = null; 
    SetVariable(null); 
    //This piece of code is to collapse all nodes when the VariableSelection (when clicking a node variable to change it) modal is open.  
    //This is necessary because reRender is not working on the tree, depite it works perfectly on other components at the same level (eg. selected variable details template) 
    //seems a bug 
    try  
    { 
      UIComponent comp = FacesContext.getCurrentInstance().getViewRoot().findComponent("ruleexpModal"); 
      HtmlTree c = (HtmlTree)comp.getChildren().get(0).getChildren().get(2);//TODO search the tree in another way 
      c.queueCollapseAll(); 
    } catch (Exception e) { 
      // TODO Auto-generated catch block 
      e.printStackTrace(); 
    } 
  } 
   
  public void SetLiteral() {     
    lastDroppedItem.setLabel(literalValue.toString()); 
    //lastDroppedItem.setValue(literalValue); 
    target.add(lastDroppedItem);     
  } 
   
  public void SetNode(RuleNode v) { 
    this.node = v; 
    initList(); 
    load();     
  } 
   
 
  public RuleExpressionItem getLastDroppedItem() { 
    return lastDroppedItem; 
  } 
 
  public void setLastDroppedItem(RuleExpressionItem lastDroppedItem) { 
    this.lastDroppedItem = lastDroppedItem; 
  } 
 
  public Object getLiteralValue() { 
    return literalValue; 
  } 
 
  public void setLiteralValue(Object literalValue) { 
    this.literalValue = literalValue; 
  } 
 
  public RuleNode getNode() { 
    return node; 
  } 
 
  public void setNode(RuleNode node) { 
    this.node = node; 
  } 
 
  public Variable_ensayo getVariable() { 
    return variable; 
  } 
 
  public void setVariable(Variable_ensayo variable) { 
    this.variable = variable; 
  } 
 
  public String getNameVariableToChange() { 
    return nameVariableToChange; 
  } 
 
  public void setNameVariableToChange(String nameVariableToChange) { 
    this.nameVariableToChange = nameVariableToChange; 
  } 
 
  public String getExpValid() { 
    return expValid; 
  } 
 
  public void setExpValid(String expValid) { 
    this.expValid = expValid; 
  } 
}