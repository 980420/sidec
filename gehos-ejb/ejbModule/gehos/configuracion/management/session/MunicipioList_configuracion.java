package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("municipioList_configuracion")
public class MunicipioList_configuracion extends
		EntityQuery<Municipio_configuracion> {

	private static final String EJBQL = "select municipio from Municipio municipio";

	private static final String[] RESTRICTIONS = {
			"lower(municipio.valor) like concat(lower(#{municipioList.municipio.valor}),'%')",
			"lower(municipio.codigo) like concat(lower(#{municipioList.municipio.codigo}),'%')", };

	private Municipio_configuracion municipio = new Municipio_configuracion();

	public MunicipioList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Municipio_configuracion getMunicipio() {
		return municipio;
	}
}
