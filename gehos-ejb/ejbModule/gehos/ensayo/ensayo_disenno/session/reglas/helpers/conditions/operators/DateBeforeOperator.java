package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.DateSigec;

import java.math.BigDecimal;
import java.util.Date;

public class DateBeforeOperator implements IOperatorDate
{

	@Override
	public String name() {		
		return "antes";
	}

	@Override
	public boolean function(Object... p) {
		DateSigec a = (DateSigec)(p[0]);
		DateSigec b = (DateSigec)(p[1]);
		return a.Before(b);
	}

	@Override
	public String[] types() {		
		return new String[]{this.DATETYPE};
	}
 
}
