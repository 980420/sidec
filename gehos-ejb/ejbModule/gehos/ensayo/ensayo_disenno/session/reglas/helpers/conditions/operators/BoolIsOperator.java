package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.math.BigDecimal;

public class BoolIsOperator implements IBinaryOperator
{

	@Override
	public String name() {		
		return "es";
	}

	@Override
	public boolean function(Object... p) {
		boolean a = (Boolean)p[0];
		boolean b = Boolean.valueOf(p[1].toString());
		return a==b;
	}

	@Override
	public String[] types() {		
		return new String[]{this.BOOLEANTYPE};
	}
 
}
