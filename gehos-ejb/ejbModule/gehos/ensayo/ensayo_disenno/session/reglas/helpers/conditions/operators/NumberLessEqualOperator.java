package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.math.BigDecimal;

public class NumberLessEqualOperator implements IOperatorNumber
{

	@Override
	public String name() {		
		return "mayor o igual";
	}

	@Override
	public boolean function(Object... p) {
		BigDecimal a = (BigDecimal)p[0];
		BigDecimal b = (BigDecimal)p[1];
		return a.compareTo(b)>=0;
	}

	@Override
	public String[] types() {		
		return new String[]{INTEGERTYPE, REALTYPE};
	}
 
}
