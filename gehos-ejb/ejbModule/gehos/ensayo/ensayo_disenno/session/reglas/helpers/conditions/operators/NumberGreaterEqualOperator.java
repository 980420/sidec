package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.math.BigDecimal;

public class NumberGreaterEqualOperator implements IOperatorNumber
{

	@Override
	public String name() {		
		return "mayor";
	}

	@Override
	public boolean function(Object... p) {
		BigDecimal a = (BigDecimal)p[0];
		BigDecimal b = (BigDecimal)p[1];
		return a.compareTo(b)==1;
	}

	@Override
	public String[] types() {		
		return new String[]{INTEGERTYPE, REALTYPE};
	}
 
}
