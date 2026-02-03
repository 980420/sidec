package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("seccionList_ensayo")
public class SeccionList_ensayo extends EntityQuery<Seccion_ensayo> {

	private static final String EJBQL = "select seccion from Seccion_ensayo seccion";

	private static final String[] RESTRICTIONS = {
			"lower(seccion.etiquetaSeccion) like concat(lower(#{seccionList_ensayo.seccion.etiquetaSeccion}),'%')",
			"lower(seccion.tituloSeccion) like concat(lower(#{seccionList_ensayo.seccion.tituloSeccion}),'%')",
			"lower(seccion.subtitulo) like concat(lower(#{seccionList_ensayo.seccion.subtitulo}),'%')",
			"lower(seccion.instrucciones) like concat(lower(#{seccionList_ensayo.seccion.instrucciones}),'%')", };

	private Seccion_ensayo seccion = new Seccion_ensayo();

	public SeccionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Seccion_ensayo getSeccion() {
		return seccion;
	}
}
