package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("entidadList_ensayo")
public class EntidadList_ensayo extends EntityQuery<Entidad_ensayo> {

	private static final String EJBQL = "select entidad from Entidad_ensayo entidad";

	private static final String[] RESTRICTIONS = {
			"lower(entidad.nombre) like concat(lower(#{entidadList_ensayo.entidad.nombre}),'%')",
			"lower(entidad.direccion) like concat(lower(#{entidadList_ensayo.entidad.direccion}),'%')",
			"lower(entidad.telefonos) like concat(lower(#{entidadList_ensayo.entidad.telefonos}),'%')",
			"lower(entidad.fax) like concat(lower(#{entidadList_ensayo.entidad.fax}),'%')",
			"lower(entidad.correo) like concat(lower(#{entidadList_ensayo.entidad.correo}),'%')",
			"lower(entidad.logo) like concat(lower(#{entidadList_ensayo.entidad.logo}),'%')",
			"lower(entidad.idInstanciaHis) like concat(lower(#{entidadList_ensayo.entidad.idInstanciaHis}),'%')",
			"lower(entidad.rif) like concat(lower(#{entidadList_ensayo.entidad.rif}),'%')", };

	private Entidad_ensayo entidad = new Entidad_ensayo();

	public EntidadList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Entidad_ensayo getEntidad() {
		return entidad;
	}
}
