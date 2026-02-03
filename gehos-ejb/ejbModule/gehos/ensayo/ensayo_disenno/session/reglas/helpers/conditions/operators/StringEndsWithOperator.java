package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.math.BigDecimal;

public class StringEndsWithOperator implements IBinaryOperator
{

	@Override
	public String name() {		
		return "empieza con";
	}

	@Override
	public boolean function(Object... p) {
		String a = (String)p[0];
		String b = (String)p[1];
		return a.startsWith(b);
	}

	@Override
	public String[] types() {		
		return new String[]{this.STRINGTYPE};
	}
 
}
