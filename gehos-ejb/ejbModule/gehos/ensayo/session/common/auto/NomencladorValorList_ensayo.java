package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("nomencladorValorList_ensayo")
public class NomencladorValorList_ensayo extends
		EntityQuery<NomencladorValor_ensayo> {

	private static final String EJBQL = "select nomencladorValor from NomencladorValor_ensayo nomencladorValor";

	private static final String[] RESTRICTIONS = {
			"lower(nomencladorValor.valor) like concat(lower(#{nomencladorValorList_ensayo.nomencladorValor.valor}),'%')",
			"lower(nomencladorValor.valorCalculado) like concat(lower(#{nomencladorValorList_ensayo.nomencladorValor.valorCalculado}),'%')", };

	private NomencladorValor_ensayo nomencladorValor = new NomencladorValor_ensayo();

	public NomencladorValorList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public NomencladorValor_ensayo getNomencladorValor() {
		return nomencladorValor;
	}
}
