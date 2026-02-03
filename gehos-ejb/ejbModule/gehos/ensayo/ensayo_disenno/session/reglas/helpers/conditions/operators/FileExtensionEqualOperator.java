package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.FileSigec;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class FileExtensionEqualOperator implements IBinaryOperator
{

	@Override
	public String name() {		
		return "extensi\u00F3n igual a";
	}

	@Override
	public boolean function(Object... p) {
		FileSigec p0 = (FileSigec)p[0];
		String b = (String)p[1];
		
		return b.equalsIgnoreCase(p0.Extension());
	}

	@Override
	public String[] types() {		
		return new String[]{this.FILETYPE};
	}
 
}
