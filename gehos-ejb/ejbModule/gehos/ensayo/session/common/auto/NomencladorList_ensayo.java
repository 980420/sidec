package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("nomencladorList_ensayo")
public class NomencladorList_ensayo extends EntityQuery<Nomenclador_ensayo> {

	private static final String EJBQL = "select nomenclador from Nomenclador_ensayo nomenclador";

	private static final String[] RESTRICTIONS = {
			"lower(nomenclador.nombre) like concat(lower(#{nomencladorList_ensayo.nomenclador.nombre}),'%')",
			"lower(nomenclador.descripcion) like concat(lower(#{nomencladorList_ensayo.nomenclador.descripcion}),'%')",
			"lower(nomenclador.valorDefecto) like concat(lower(#{nomencladorList_ensayo.nomenclador.valorDefecto}),'%')", };

	private Nomenclador_ensayo nomenclador = new Nomenclador_ensayo();

	public NomencladorList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Nomenclador_ensayo getNomenclador() {
		return nomenclador;
	}
}
