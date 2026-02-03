package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("medicoList_configuracion")
public class MedicoList_configuracion extends EntityQuery<Medico_configuracion> {

	private static final String EJBQL = "select medico from Medico medico";

	private static final String[] RESTRICTIONS = {
			"lower(medico.matriculaMinisterio) like concat(lower(#{medicoList.medico.matriculaMinisterio}),'%')",
			"lower(medico.matriculaColegioMedico) like concat(lower(#{medicoList.medico.matriculaColegioMedico}),'%')", };

	private Medico_configuracion medico = new Medico_configuracion();

	public MedicoList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Medico_configuracion getMedico() {
		return medico;
	}
}
