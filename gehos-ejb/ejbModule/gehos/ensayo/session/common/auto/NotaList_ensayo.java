package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("notaList_ensayo")
public class NotaList_ensayo extends EntityQuery<Nota_ensayo> {

	private static final String EJBQL = "select nota from Nota_ensayo nota";

	private static final String[] RESTRICTIONS = {
			"lower(nota.descripcion) like concat(lower(#{notaList_ensayo.nota.descripcion}),'%')",
			"lower(nota.detallesNota) like concat(lower(#{notaList_ensayo.nota.detallesNota}),'%')",
			"lower(nota.tipoEntidad) like concat(lower(#{notaList_ensayo.nota.tipoEntidad}),'%')", };

	private Nota_ensayo nota = new Nota_ensayo();

	public NotaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Nota_ensayo getNota() {
		return nota;
	}
}
