package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.math.BigDecimal;

public class StringEqualOperator implements IBinaryOperator
{

	@Override
	public String name() {		
		return "igual (aa)";
	}

	@Override
	public boolean function(Object... p) {
		String a = (String)p[0];
		String b = (String)p[1];
		return a.equalsIgnoreCase(b);
	}

	@Override
	public String[] types() {		
		return new String[]{this.STRINGTYPE};
	}
 
}
