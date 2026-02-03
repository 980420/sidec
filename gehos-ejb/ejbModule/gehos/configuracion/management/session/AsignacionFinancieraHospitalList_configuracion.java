package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("asignacionFinancieraHospitalList_configuracion")
public class AsignacionFinancieraHospitalList_configuracion extends
		EntityQuery<AsignacionFinancieraHospital_configuracion> {

	private static final String EJBQL = "select asignacionFinancieraHospital from AsignacionFinancieraHospital asignacionFinancieraHospital";

	private static final String[] RESTRICTIONS = {};

	private AsignacionFinancieraHospital_configuracion asignacionFinancieraHospital = new AsignacionFinancieraHospital_configuracion();

	public AsignacionFinancieraHospitalList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AsignacionFinancieraHospital_configuracion getAsignacionFinancieraHospital() {
		return asignacionFinancieraHospital;
	}
}
