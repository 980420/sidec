package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eSexoList_ensayo")
public class ESexoList_ensayo extends EntityQuery<ESexo_ensayo> {

	private static final String EJBQL = "select eSexo from ESexo_ensayo eSexo";

	private static final String[] RESTRICTIONS = {
			"lower(eSexo.valor) like concat(lower(#{eSexoList_ensayo.eSexo.valor}),'%')",
			"lower(eSexo.abreviatura) like concat(lower(#{eSexoList_ensayo.eSexo.abreviatura}),'%')", };

	private ESexo_ensayo eSexo = new ESexo_ensayo();

	public ESexoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ESexo_ensayo getESexo() {
		return eSexo;
	}
}
