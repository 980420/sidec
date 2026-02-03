package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("momentoSeguimientoGeneralList_ensayo")
public class MomentoSeguimientoGeneralList_ensayo extends
		EntityQuery<MomentoSeguimientoGeneral_ensayo> {

	private static final String EJBQL = "select momentoSeguimientoGeneral from MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral";

	private static final String[] RESTRICTIONS = {
			"lower(momentoSeguimientoGeneral.nombre) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.nombre}),'%')",
			"lower(momentoSeguimientoGeneral.descripcion) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.descripcion}),'%')",
			"lower(momentoSeguimientoGeneral.dia) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.dia}),'%')",
			"lower(momentoSeguimientoGeneral.etapa) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.etapa}),'%')",
			"lower(momentoSeguimientoGeneral.diasEvaluacion) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.diasEvaluacion}),'%')",
			"lower(momentoSeguimientoGeneral.diasTratamiento) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.diasTratamiento}),'%')",
			"lower(momentoSeguimientoGeneral.diasSeguimiento) like concat(lower(#{momentoSeguimientoGeneralList_ensayo.momentoSeguimientoGeneral.diasSeguimiento}),'%')", };

	private MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral = new MomentoSeguimientoGeneral_ensayo();

	public MomentoSeguimientoGeneralList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MomentoSeguimientoGeneral_ensayo getMomentoSeguimientoGeneral() {
		return momentoSeguimientoGeneral;
	}
}
