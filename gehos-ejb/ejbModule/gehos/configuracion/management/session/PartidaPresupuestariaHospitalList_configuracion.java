package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("partidaPresupuestariaHospitalList_configuracion")
public class PartidaPresupuestariaHospitalList_configuracion extends
		EntityQuery<PartidaPresupuestariaHospital_configuracion> {

	private static final String EJBQL = "select partidaPresupuestariaHospital from PartidaPresupuestariaHospital partidaPresupuestariaHospital";

	private static final String[] RESTRICTIONS = {};

	private PartidaPresupuestariaHospital_configuracion partidaPresupuestariaHospital = new PartidaPresupuestariaHospital_configuracion();

	public PartidaPresupuestariaHospitalList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PartidaPresupuestariaHospital_configuracion getPartidaPresupuestariaHospital() {
		return partidaPresupuestariaHospital;
	}
}
