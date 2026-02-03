package gehos.ensayo.ensayo_disenno.session.reglas.util;

import java.util.ArrayList;
import java.util.List;

public class Useful 
{
	/**
	 * Joins a list of values by a separator.
	 * eg:
	 * List ["a","b", "c"]
	 * Separator ","
	 * => "a,b,c"
	 * @param values list of strings to join
	 * @param separator
	 * @return string of joined values
	 */
	public static String join(List<String> values, String separator)
	{
		String out = "";		
		for (int i = 0; i < values.size(); i++)		
			out+=values.get(i)+(i < values.size()-1 ? separator : "");
		return out;		
	}
}
