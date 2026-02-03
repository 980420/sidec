package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reMedicacionList_ensayo")
public class ReMedicacionList_ensayo extends EntityQuery<ReMedicacion_ensayo> {

	private static final String EJBQL = "select reMedicacion from ReMedicacion_ensayo reMedicacion";

	private static final String[] RESTRICTIONS = {
			"lower(reMedicacion.medicamento) like concat(lower(#{reMedicacionList_ensayo.reMedicacion.medicamento}),'%')",
			"lower(reMedicacion.frecuencia) like concat(lower(#{reMedicacionList_ensayo.reMedicacion.frecuencia}),'%')",
			"lower(reMedicacion.indicacion) like concat(lower(#{reMedicacionList_ensayo.reMedicacion.indicacion}),'%')", };

	private ReMedicacion_ensayo reMedicacion = new ReMedicacion_ensayo();

	public ReMedicacionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReMedicacion_ensayo getReMedicacion() {
		return reMedicacion;
	}
}
