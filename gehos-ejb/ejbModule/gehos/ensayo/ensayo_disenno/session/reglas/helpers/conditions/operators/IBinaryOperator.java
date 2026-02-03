package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operators;

import java.util.List;
import java.util.ArrayList;
public interface IBinaryOperator 
{
	static String BOOLEANTYPE = "BL";
	static String STRINGTYPE = "ST";
	static String INTEGERTYPE = "INT";
	static String REALTYPE = "REAL";
	static String NOMTYPE = "NOM";
	static String DATETYPE = "DATE";
	static String FILETYPE = "FILE";
	static IBinaryOperator[] operators = new IBinaryOperator[]
	{
	 new NumberEqualOperator(),new NumberGreaterOperator(),new NumberLessOperator(),new NumberGreaterEqualOperator(),new NumberLessEqualOperator(),new NumberNotEqualOperator(),
	 new StringEqualOperator(), new StringEqualCaseOperator(), new StringStartsWithOperator(), new StringEndsWithOperator(), new StringContainsOperator(),
	 new NomenEqualOperator(), new NomenNotEqualOperator(),
	 new BoolIsOperator(),
	 new DateEqualOperator(), new DateNotEqualOperator(), new DateBeforeOperator(), new DateAfterOperator(),
	 new FileSizeLessOperator(), new FileSizeGreaterOperator(), new FileExtensionEqualOperator(), new FileExtensionNotEqualOperator()
	};
	
	
	String name();	
	boolean function(Object... p);
	/*"BL"
	"ST"
	"INT"
	"REAL"
	"DATE" //this should disapear
	"NOM"
	"PDATE"
	"FILE"*/
	/**
	 * types to which this can be applied
	 * @return
	 */
	String[] types();

}
