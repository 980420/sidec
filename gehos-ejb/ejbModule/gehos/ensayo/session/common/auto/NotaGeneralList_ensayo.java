package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("notaGeneralList_ensayo")
public class NotaGeneralList_ensayo extends EntityQuery<NotaGeneral_ensayo> {

	private static final String EJBQL = "select notaGeneral from NotaGeneral_ensayo notaGeneral";

	private static final String[] RESTRICTIONS = {
			"lower(notaGeneral.descripcion) like concat(lower(#{notaGeneralList_ensayo.notaGeneral.descripcion}),'%')",
			"lower(notaGeneral.detallesNota) like concat(lower(#{notaGeneralList_ensayo.notaGeneral.detallesNota}),'%')", };

	private NotaGeneral_ensayo notaGeneral = new NotaGeneral_ensayo();

	public NotaGeneralList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public NotaGeneral_ensayo getNotaGeneral() {
		return notaGeneral;
	}
}
