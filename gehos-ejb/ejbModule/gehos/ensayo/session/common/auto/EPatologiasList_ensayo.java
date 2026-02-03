package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ePatologiasList_ensayo")
public class EPatologiasList_ensayo extends EntityQuery<EPatologias_ensayo> {

	private static final String EJBQL = "select ePatologias from EPatologias_ensayo ePatologias";

	private static final String[] RESTRICTIONS = {
			"lower(ePatologias.nombre) like concat(lower(#{ePatologiasList_ensayo.ePatologias.nombre}),'%')",
			"lower(ePatologias.descripcion) like concat(lower(#{ePatologiasList_ensayo.ePatologias.descripcion}),'%')", };

	private EPatologias_ensayo ePatologias = new EPatologias_ensayo();

	public EPatologiasList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EPatologias_ensayo getEPatologias() {
		return ePatologias;
	}
}
