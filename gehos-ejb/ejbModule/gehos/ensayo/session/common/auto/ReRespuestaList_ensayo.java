package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reRespuestaList_ensayo")
public class ReRespuestaList_ensayo extends EntityQuery<ReRespuesta_ensayo> {

	private static final String EJBQL = "select reRespuesta from ReRespuesta_ensayo reRespuesta";

	private static final String[] RESTRICTIONS = { "lower(reRespuesta.descripcion) like concat(lower(#{reRespuestaList_ensayo.reRespuesta.descripcion}),'%')", };

	private ReRespuesta_ensayo reRespuesta = new ReRespuesta_ensayo();

	public ReRespuestaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReRespuesta_ensayo getReRespuesta() {
		return reRespuesta;
	}
}
