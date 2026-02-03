package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.FileSigec;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileSizeLessOperator implements IBinaryOperator
{

	@Override
	public String name() {		
		return "tama\u00F1o menor a (en KB)";
	}

	@Override
	public boolean function(Object... p) {
		FileSigec p0 = (FileSigec)p[0];
		String b = (String)p[1];
		
		int sizeFile = p0.SizeKB();
		int sizeUser = Integer.parseInt(b);
		
		return sizeFile < sizeUser;
	}

	@Override
	public String[] types() {		
		return new String[]{this.FILETYPE};
	} 
}
