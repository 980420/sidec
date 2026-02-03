package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("medicoExternoList_configuracion")
public class MedicoExternoList_configuracion extends
		EntityQuery<MedicoExterno_configuracion> {

	private static final String EJBQL = "select medicoExterno from MedicoExterno medicoExterno";

	private static final String[] RESTRICTIONS = {
			"lower(medicoExterno.cedula) like concat(lower(#{medicoExternoList.medicoExterno.cedula}),'%')",
			"lower(medicoExterno.nombre) like concat(lower(#{medicoExternoList.medicoExterno.nombre}),'%')",
			"lower(medicoExterno.apellido1) like concat(lower(#{medicoExternoList.medicoExterno.apellido1}),'%')",
			"lower(medicoExterno.apellido2) like concat(lower(#{medicoExternoList.medicoExterno.apellido2}),'%')",
			"lower(medicoExterno.matriculaColegioMedico) like concat(lower(#{medicoExternoList.medicoExterno.matriculaColegioMedico}),'%')",
			"lower(medicoExterno.matriculaMinisterio) like concat(lower(#{medicoExternoList.medicoExterno.matriculaMinisterio}),'%')",
			"lower(medicoExterno.nombreClinica) like concat(lower(#{medicoExternoList.medicoExterno.nombreClinica}),'%')", };

	private MedicoExterno_configuracion medicoExterno = new MedicoExterno_configuracion();

	public MedicoExternoList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MedicoExterno_configuracion getMedicoExterno() {
		return medicoExterno;
	}
}
