package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("medicoInEntidadList_configuracion")
public class MedicoInEntidadList_configuracion extends
		EntityQuery<MedicoInEntidad_configuracion> {

	private static final String EJBQL = "select medicoInEntidad from MedicoInEntidad medicoInEntidad";

	private static final String[] RESTRICTIONS = {};

	private MedicoInEntidad_configuracion medicoInEntidad = new MedicoInEntidad_configuracion();

	public MedicoInEntidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MedicoInEntidad_configuracion getMedicoInEntidad() {
		return medicoInEntidad;
	}
}
