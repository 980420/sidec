package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("variableDatoList_ensayo")
public class VariableDatoList_ensayo extends EntityQuery<VariableDato_ensayo> {

	private static final String EJBQL = "select variableDato from VariableDato_ensayo variableDato";

	private static final String[] RESTRICTIONS = { "lower(variableDato.valor) like concat(lower(#{variableDatoList_ensayo.variableDato.valor}),'%')", };

	private VariableDato_ensayo variableDato = new VariableDato_ensayo();

	public VariableDatoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public VariableDato_ensayo getVariableDato() {
		return variableDato;
	}
}
