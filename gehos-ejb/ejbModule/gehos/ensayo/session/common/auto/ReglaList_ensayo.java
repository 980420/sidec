package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reglaList_ensayo")
public class ReglaList_ensayo extends EntityQuery<Regla_ensayo> {

	private static final String EJBQL = "select regla from Regla_ensayo regla";

	private static final String[] RESTRICTIONS = {
			"lower(regla.tipoRegla) like concat(lower(#{reglaList_ensayo.regla.tipoRegla}),'%')",
			"lower(regla.opciones) like concat(lower(#{reglaList_ensayo.regla.opciones}),'%')",
			"lower(regla.mensajeRegla) like concat(lower(#{reglaList_ensayo.regla.mensajeRegla}),'%')", };

	private Regla_ensayo regla = new Regla_ensayo();

	public ReglaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Regla_ensayo getRegla() {
		return regla;
	}
}
