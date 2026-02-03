package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoDatoList_ensayo")
public class TipoDatoList_ensayo extends EntityQuery<TipoDato_ensayo> {

	private static final String EJBQL = "select tipoDato from TipoDato_ensayo tipoDato";

	private static final String[] RESTRICTIONS = {
			"lower(tipoDato.codigo) like concat(lower(#{tipoDatoList_ensayo.tipoDato.codigo}),'%')",
			"lower(tipoDato.nombre) like concat(lower(#{tipoDatoList_ensayo.tipoDato.nombre}),'%')",
			"lower(tipoDato.descripcion) like concat(lower(#{tipoDatoList_ensayo.tipoDato.descripcion}),'%')", };

	private TipoDato_ensayo tipoDato = new TipoDato_ensayo();

	public TipoDatoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoDato_ensayo getTipoDato() {
		return tipoDato;
	}
}
