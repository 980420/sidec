package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class NomenEqualOperator implements IBinaryOperator
{

	@Override
	public String name() {		
		return "igual (contiene)";
	}

	@Override
	public boolean function(Object... p) {
		Object p0 = p[0];//can be String or String[]
		String b = (String)p[1];
		if(p0 instanceof String)
		{
			String a = (String)p0;			
			return a.equals(b);
		}
		else
		{			
			List<String> list = Arrays.asList((String[])p0);
			return list.contains(b);
		}
	}

	@Override
	public String[] types() {		
		return new String[]{this.NOMTYPE};
	}
 
}
