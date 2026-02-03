package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("momentoSeguimientoEspecificoList_ensayo")
public class MomentoSeguimientoEspecificoList_ensayo extends
		EntityQuery<MomentoSeguimientoEspecifico_ensayo> {

	private static final String EJBQL = "select momentoSeguimientoEspecifico from MomentoSeguimientoEspecifico_ensayo momentoSeguimientoEspecifico";

	private static final String[] RESTRICTIONS = {};

	private MomentoSeguimientoEspecifico_ensayo momentoSeguimientoEspecifico = new MomentoSeguimientoEspecifico_ensayo();

	public MomentoSeguimientoEspecificoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MomentoSeguimientoEspecifico_ensayo getMomentoSeguimientoEspecifico() {
		return momentoSeguimientoEspecifico;
	}
}
