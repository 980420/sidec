package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("trazaAtributoModificadoList_ensayo")
public class TrazaAtributoModificadoList_ensayo extends
		EntityQuery<TrazaAtributoModificado_ensayo> {

	private static final String EJBQL = "select trazaAtributoModificado from TrazaAtributoModificado_ensayo trazaAtributoModificado";

	private static final String[] RESTRICTIONS = {
			"lower(trazaAtributoModificado.entidad) like concat(lower(#{trazaAtributoModificadoList_ensayo.trazaAtributoModificado.entidad}),'%')",
			"lower(trazaAtributoModificado.atributo) like concat(lower(#{trazaAtributoModificadoList_ensayo.trazaAtributoModificado.atributo}),'%')",
			"lower(trazaAtributoModificado.valorAntes) like concat(lower(#{trazaAtributoModificadoList_ensayo.trazaAtributoModificado.valorAntes}),'%')",
			"lower(trazaAtributoModificado.valorDespues) like concat(lower(#{trazaAtributoModificadoList_ensayo.trazaAtributoModificado.valorDespues}),'%')", };

	private TrazaAtributoModificado_ensayo trazaAtributoModificado = new TrazaAtributoModificado_ensayo();

	public TrazaAtributoModificadoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TrazaAtributoModificado_ensayo getTrazaAtributoModificado() {
		return trazaAtributoModificado;
	}
}
