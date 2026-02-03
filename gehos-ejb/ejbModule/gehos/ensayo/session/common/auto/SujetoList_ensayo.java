package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("sujetoList_ensayo")
public class SujetoList_ensayo extends EntityQuery<Sujeto_ensayo> {

	private static final String EJBQL = "select sujeto from Sujeto_ensayo sujeto";

	private static final String[] RESTRICTIONS = {
			"lower(sujeto.nombre) like concat(lower(#{sujetoList_ensayo.sujeto.nombre}),'%')",
			"lower(sujeto.codigoPaciente) like concat(lower(#{sujetoList_ensayo.sujeto.codigoPaciente}),'%')",
			"lower(sujeto.inicialesCentro) like concat(lower(#{sujetoList_ensayo.sujeto.inicialesCentro}),'%')",
			"lower(sujeto.inicialesPaciente) like concat(lower(#{sujetoList_ensayo.sujeto.inicialesPaciente}),'%')", };

	private Sujeto_ensayo sujeto = new Sujeto_ensayo();

	public SujetoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
}
