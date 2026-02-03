package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("entidadOrganizacionalList_configuracion")
public class EntidadOrganizacionalList_configuracion extends
		EntityQuery<EntidadOrganizacional_configuracion> {

	private static final String EJBQL = "select entidadOrganizacional from EntidadOrganizacional entidadOrganizacional";

	private static final String[] RESTRICTIONS = { "lower(entidadOrganizacional.valor) like concat(lower(#{entidadOrganizacionalList.entidadOrganizacional.valor}),'%')", };

	private EntidadOrganizacional_configuracion entidadOrganizacional = new EntidadOrganizacional_configuracion();

	public EntidadOrganizacionalList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EntidadOrganizacional_configuracion getEntidadOrganizacional() {
		return entidadOrganizacional;
	}
}
