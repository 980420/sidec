package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.DateSigec;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operations.IOperation;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.FileSizeGreaterOperator;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.FileSizeLessOperator;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.IBinaryOperator;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.IOperatorDate;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.IOperatorNumber;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.expressionbuilder.RuleExpressionItem.RuleExpressionItemType;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeJsonDeserializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.json.RuleNodeJsonSerializer;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.testvalues.VariableValue;
import gehos.ensayo.entity.Variable_ensayo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.udojava.evalex.Expression;

@JsonSerialize(using = RuleNodeJsonSerializer.class)
@JsonDeserialize(using = RuleNodeJsonDeserializer.class)
public class RuleNode {//this class would benefit a lot from inheritance instead of using node types

	public enum RuleNodeType {
		All(0),
		Any(1),
		None(2),
		Expression(3);		
		int val;		
		RuleNodeType(int val){
			this.val = val;
		}
	}
	
	Variable_ensayo variable;
	RuleNodeType type;	
	RuleNode parent;	
	List<RuleNode> children;
	String operator; //if it is an expression
	IBinaryOperator operatorObject;
	Object value;	
	int id;
	int lastGivenId;//this only valid for the root node and its purpose is to know where to start on json deserialisation	
	Boolean boolValue;//this is to help debugging, it shows if a condition is met
	Map<RuleNode.RuleNodeType, String> ruleNodeTypesLogic;
	Map<String,RuleNodeType> ruleNodeTypes;
	String ruleNodeTypeString;
	List<String> operators;
	
	public RuleNode(){
		this.children = new ArrayList<RuleNode>();
		this.ruleNodeTypes = new HashMap<String,RuleNode.RuleNodeType>();
		this.ruleNodeTypes.put("Todas", RuleNodeType.All);
		this.ruleNodeTypes.put("Cualquiera", RuleNodeType.Any);
		this.ruleNodeTypes.put("Ninguna", RuleNodeType.None);
		this.ruleNodeTypeString = "Todas";		
		this.ruleNodeTypesLogic = new HashMap<RuleNode.RuleNodeType, String>();
		this.ruleNodeTypesLogic.put(RuleNodeType.All, "y");
		this.ruleNodeTypesLogic.put(RuleNodeType.None,"o");
		this.ruleNodeTypesLogic.put(RuleNodeType.Any,"o");		
	}
	
	public String describe(String out){
		String grouperWrap = "<span class='rule-grouper'>%s";
		String grouperSignWrap = "<span class='rule-grouper-sign'>%s</span>";
		if (this.type != RuleNodeType.Expression){
			if (this.parent != null && this.type != RuleNodeType.None)
				out += String.format(grouperWrap, String.format(grouperSignWrap, "("));
			if (this.type == RuleNodeType.None)
				out += String.format(grouperWrap, String.format(grouperSignWrap, "!("));
			String logicSymbol = this.ruleNodeTypesLogic.get(this.type);
			for (int i = 0; i < this.children.size(); i++){
				RuleNode child = this.children.get(i);
				if (this.children.size() > 0 && i > 0 && i < this.children.size())
					out += " <span class='rule-operator'>" + logicSymbol + " </span>";
				out += child.describe("");
			}
			if (this.parent != null || this.type == RuleNodeType.None)
				out += String.format(grouperSignWrap, ")") + "</span>";
			return out;
		} else
			return String.format("<span class='rule-exp'><span class='rule-var'>%s</span> <span class='rule-operation'> %s </span>%s</span>", this.variable.getNombreVariable(), this.operator, this.formatValue(this.value));
	}
	
	public String describeOut(String out){
	    String grouperWrap = "%s";
	    String grouperSignWrap = "%s";
	    if (this.type != RuleNodeType.Expression){
	        if (this.parent != null && this.type != RuleNodeType.None)
	            out += String.format(grouperWrap, String.format(grouperSignWrap, "("));
	        if (this.type == RuleNodeType.None)
	            out += String.format(grouperWrap, String.format(grouperSignWrap, "!("));
	        String logicSymbol = this.ruleNodeTypesLogic.get(this.type);
	        for (int i = 0; i < this.children.size(); i++){
	            RuleNode child = this.children.get(i);
	            if (this.children.size() > 0 && i > 0 && i < this.children.size())
	                out += " " + logicSymbol + " ";
	            out += child.describeOut("");
	        }
	        if (this.parent != null || this.type == RuleNodeType.None)
	            out += String.format(grouperSignWrap, ")") + "";
	        return out;
	    } else
	        return String.format("%s %s %s", this.variable.getNombreVariable(), this.operator, this.formatValueOut(this.value));
	}
	
	public String formatValue(Object value){
		String template = "<span class='rule-val rule-val-type-%s'>%s</span>";
		String out = value.toString();
		String code = variable.getTipoDato().getCodigo();
		String ruleValType = code.toLowerCase();
		if (code.equals("ST"))
			out = String.format("\"%s\"", out);
		return String.format(template, ruleValType, out);
	}
	public String formatValueOut(Object value){
	    String out = value.toString();
	    String code = variable.getTipoDato().getCodigo();
	    if (code.equals("ST"))
	        out = String.format("\"%s\"", out);
	    return out;
	}
	
	public List<String> loadOperators(){
		IBinaryOperator[] operators = IBinaryOperator.operators;
		List<String> varOperators = new ArrayList<String>();
		for (IBinaryOperator o : operators) {
			if (Arrays.asList(o.types()).contains(this.variable.getTipoDato().getCodigo()))
				varOperators.add(o.name());
		}
		return varOperators;
	}
	 
	public static String FILE_CLASS_VALIDATION = "isinteger";

	/**
	 * This is only to validate the file type, because depending on the operator
	 * the validation change
	 * 
	 * @return
	 */
	public String findValidationFor(){
		if (this.operator == null)
			return "";
		IBinaryOperator[] operators = IBinaryOperator.operators;
		for (IBinaryOperator o : operators){
			if (Arrays.asList(o.types()).contains(this.variable.getTipoDato().getCodigo()) && o.name().equals(this.operator)){
				if (o instanceof FileSizeGreaterOperator || o instanceof FileSizeLessOperator)
					return FILE_CLASS_VALIDATION;
			}
		}
		return "";
	}
	 
	public boolean Visit(List<VariableValue> testVariableValues){
		switch (type){
			case All:
				return VisitConditionalAll(testVariableValues);
			case Any:
				return VisitConditionalAny(testVariableValues);
			case None:
				return VisitConditionalNone(testVariableValues);
			default:
				return VisitExpression(testVariableValues);
		}
	}
	
	public boolean VisitConditionalAll(List<VariableValue> testVariableValues){		
		boolean allTrue = true;
		for (RuleNode rn : this.children){
			boolean res = rn.Visit(testVariableValues);
			if(!res)
				allTrue = false;
		}		
		this.boolValue = allTrue;
		return allTrue;
	}
	
	public boolean VisitConditionalAny(List<VariableValue> testVariableValues){
		boolean anyTrue = false;
		for (RuleNode rn : this.children){
			boolean res = rn.Visit(testVariableValues);
			if (res)
				anyTrue = true;
		}
		this.boolValue = anyTrue;
		return anyTrue;
	}
	
	public boolean VisitConditionalNone(List<VariableValue> testVariableValues){
		boolean allFalse = true;
		for (RuleNode rn : this.children){
			boolean res = rn.Visit(testVariableValues);
			if (res)
				allFalse = false;
		}
		this.boolValue = allFalse;
		return allFalse;
	}
	
	public boolean VisitExpression(List<VariableValue> testVariableValues){
		IBinaryOperator o = OperatorByName();
		VariableValue vv = TestVariable(this.variable, testVariableValues);
		Object leftMember = null;
		Object val = null;
		if (o instanceof IOperatorNumber){
			val = this.EvalRightMemberExpression(testVariableValues);
			val = this.EvalMathExpression(val);
			leftMember = new BigDecimal(vv.getValue().toString());
		} else if (o instanceof IOperatorDate){
			val = EvalRightMemberExpression(testVariableValues);
			leftMember = vv.getValue();
			try {
				val = DateSigec.parse(val.toString());
				leftMember = DateSigec.parse(leftMember.toString());
			} catch (Exception e){
				e.printStackTrace();
			}
		} else {
			val = this.value;
			leftMember = vv.getValue();
		}
		boolean res = o.function(leftMember, val);
		this.boolValue = res;
		return res;
	}	
	
	private Object EvalRightMemberExpression(List<VariableValue> testVariableValues){
		String exp = "";
		if (this.getValueIsExpression()){
			RuleExpressionValue rev = (RuleExpressionValue) this.value;
			for (RuleExpressionItem i : rev.getTarget()){
				if (i.getType().equals(RuleExpressionItemType.Variable)){
					VariableValue vv = TestVariable((Variable_ensayo) i.getValue(), testVariableValues);
					exp += vv.getValue();
				} else
					exp += i.getLabel();
			}
		} else
			exp = this.value.toString();
		return exp;
	}
	
	private VariableValue TestVariable(Variable_ensayo v, List<VariableValue> testVariableValues){
		for (VariableValue vv : testVariableValues){
			if (vv.getVariable().getId() == v.getId())
				return vv;
		}
		return null;
	}
	
	public void FindDependencyVariables(List<Variable_ensayo> vars){
		if(this.children.size() == 0 && this.parent != null){
			vars.add(this.getVariable());
			if(getValueIsExpression()){
				RuleExpressionValue rev = (RuleExpressionValue) this.value;
				for (RuleExpressionItem i : rev.getTarget()){
					if(i.getType().equals(RuleExpressionItemType.Variable)){
						Variable_ensayo vv = (Variable_ensayo)i.getValue();
						vars.add(vv);
					}
				}
			}
		}		
		for (RuleNode child : this.children)		
			child.FindDependencyVariables(vars);		
	}
	
	public Object EvalMathExpression(Object value){
		Expression expression = new Expression(value.toString());
		BigDecimal result = expression.eval();
		return result;
	}
	
	public IBinaryOperator OperatorByName(){
		for (IBinaryOperator o : IBinaryOperator.operators){
			if(o.name().equals(this.operator) && Arrays.asList(o.types()).contains(this.variable.getTipoDato().getCodigo()))
				return o;
		}
		return null;
	}
	
	public List<RuleExpressionItem> SymbolsExp(){
			String acceptedType = "PHP";
		
		List<RuleExpressionItem> source = new ArrayList<RuleExpressionItem>();
		//special symbols
		source.add(new RuleExpressionItem("Variable",
				RuleExpressionItem.RuleExpressionItemType.Variable, acceptedType));
		source.add(new RuleExpressionItem("Literal",
				RuleExpressionItem.RuleExpressionItemType.Literal, acceptedType));
		source.add(new RuleExpressionItem("(",
				RuleExpressionItem.RuleExpressionItemType.ParenthesesOpen, acceptedType));
		source.add(new RuleExpressionItem(")",
				RuleExpressionItem.RuleExpressionItemType.ParenthesesClose, acceptedType));
		
		//specific symbols
		//operators
		for (IOperation o : IOperation.operations) 
		{
			if(Arrays.asList(o.types()).contains(this.variable.getTipoDato().getCodigo()))
			{
				source.add(new RuleExpressionItem(o.symbol(), RuleExpressionItem.RuleExpressionItemType.Operator, acceptedType));
			}
		}
		
		return source;
	}
	
	public RuleNode addExpression()
	{
		RuleNode newChild = new RuleNode();			
		newChild.parent = this;
		newChild.setVariable(this.variable);
		newChild.setType(RuleNodeType.Expression);
		children.add(newChild);
		return newChild; 
	}
	
	public RuleNode addSubCondition()
	{
		RuleNode newChild = new RuleNode();		
		newChild.setType(RuleNodeType.All);
		
		newChild.parent = this;
		newChild.setVariable(this.variable);
		
		children.add(newChild);
		return newChild;
	}
	
	public int childCount()
	{
		return children.size();
	}
	
	public RuleNode child(int i)
	{
		return children.get(i);
	}
	
	public boolean childAdd(RuleNode i)
	{
		return children.add(i);
	}
		

	public RuleNodeType getType() {
		return type;
	}

	public void setType(RuleNodeType type) {
		this.type = type;
		
		if(this.type==RuleNodeType.Expression && this.variable!=null)
		{
			this.operators = loadOperators();
			if(operators.size()>0 && operator==null)
				operator = operators.get(0);
		}
		ruleNodeTypeString = StringForType(type);
			
	}
	
	private String StringForType(RuleNodeType t)
	{
		for (Entry<String, RuleNodeType> e : ruleNodeTypes.entrySet()) {
			if(e.getValue() == t)
				return e.getKey();
		}
		return "Not found!!";
	}


	public int getLevel() {
		if(parent==null)//the root node only
			return 0;
		return parent.getLevel()+1;
	}

	public List<RuleNode> getChildren() {
		return children;
	}

	public void setChildren(List<RuleNode> children) {
		this.children = children;
	}

	public RuleNode getParent() {
		return parent;
	}

	public void setParent(RuleNode parent) {
		this.parent = parent;
	}

	public Variable_ensayo getVariable() {
		return variable;
	}

	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}

	public String getOperator() {
		if(operators.contains(operator)){
			return operator;
		}else{
			return operators.get(0);
		}
		
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getRuleNodeTypeString() {
		return ruleNodeTypeString;
	}

	public void setRuleNodeTypeString(String ruleNodeTypeString) {
		this.ruleNodeTypeString = ruleNodeTypeString;
		this.type = ruleNodeTypes.get(ruleNodeTypeString);
	}

	public Map<String, RuleNodeType> getRuleNodeTypes() {
		return ruleNodeTypes;
	}

	public void setRuleNodeTypes(Map<String, RuleNodeType> ruleNodeTypes) {
		this.ruleNodeTypes = ruleNodeTypes;
	}

	public List<String> getOperators() {
		return operators;
	}

	public void setOperators(List<String> operators) {
		this.operators = operators;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getBoolValue() {
		return boolValue;
	}

	public void setBoolValue(Boolean boolValue) {
		this.boolValue = boolValue;
	}

	public boolean getValueIsExpression() 
	{
		return this.value instanceof RuleExpressionValue;		
	}
	
	public void ToNormalValue()
	{		
		value = null;
	}

	public int getLastGivenId() {
		return lastGivenId;
	}

	public void setLastGivenId(int lastGivenId) {
		this.lastGivenId = lastGivenId;
	}

	public IBinaryOperator getOperatorObject() {
		return operatorObject;
	}

	public void setOperatorObject(IBinaryOperator operatorObject) {
		this.operatorObject = operatorObject;
	}
}
