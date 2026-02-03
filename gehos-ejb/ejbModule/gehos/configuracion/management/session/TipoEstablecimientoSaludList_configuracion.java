package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoEstablecimientoSaludList_configuracion")
public class TipoEstablecimientoSaludList_configuracion extends
		EntityQuery<TipoEstablecimientoSalud_configuracion> {

	private static final String EJBQL = "select tipoEstablecimientoSalud from TipoEstablecimientoSalud tipoEstablecimientoSalud";

	private static final String[] RESTRICTIONS = { "lower(tipoEstablecimientoSalud.valor) like concat(lower(#{tipoEstablecimientoSaludList.tipoEstablecimientoSalud.valor}),'%')", };

	private TipoEstablecimientoSalud_configuracion tipoEstablecimientoSalud = new TipoEstablecimientoSalud_configuracion();

	public TipoEstablecimientoSaludList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoEstablecimientoSalud_configuracion getTipoEstablecimientoSalud() {
		return tipoEstablecimientoSalud;
	}
}
