package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operations;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators.IBinaryOperator;

public class MultiplyOperation implements IOperation
{
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "multiply";
	}
	@Override
	public String symbol() {
		// TODO Auto-generated method stub
		return "*";
	}
	@Override
	public String[] types() {		
		return new String[]{IBinaryOperator.INTEGERTYPE, IBinaryOperator.REALTYPE};
	}
	
}
