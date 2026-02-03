package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("establecimientoAreaInfluenciaHospitalList_configuracion")
public class EstablecimientoAreaInfluenciaHospitalList_configuracion extends
		EntityQuery<EstablecimientoAreaInfluenciaHospital_configuracion> {

	private static final String EJBQL = "select establecimientoAreaInfluenciaHospital from EstablecimientoAreaInfluenciaHospital establecimientoAreaInfluenciaHospital";

	private static final String[] RESTRICTIONS = { "lower(establecimientoAreaInfluenciaHospital.nombre) like concat(lower(#{establecimientoAreaInfluenciaHospitalList.establecimientoAreaInfluenciaHospital.nombre}),'%')", };

	private EstablecimientoAreaInfluenciaHospital_configuracion establecimientoAreaInfluenciaHospital = new EstablecimientoAreaInfluenciaHospital_configuracion();

	public EstablecimientoAreaInfluenciaHospitalList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstablecimientoAreaInfluenciaHospital_configuracion getEstablecimientoAreaInfluenciaHospital() {
		return establecimientoAreaInfluenciaHospital;
	}
}
