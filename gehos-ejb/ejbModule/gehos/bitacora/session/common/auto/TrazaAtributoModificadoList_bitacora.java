package gehos.bitacora.session.common.auto;

import gehos.bitacora.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("trazaAtributoModificadoList_bitacora")
public class TrazaAtributoModificadoList_bitacora extends EntityQuery {

	private static final String[] RESTRICTIONS = {
			"lower(trazaAtributoModificado.entidad) like concat(lower(#{trazaAtributoModificadoList_bitacora.trazaAtributoModificado.entidad}),'%')",
			"lower(trazaAtributoModificado.atributo) like concat(lower(#{trazaAtributoModificadoList_bitacora.trazaAtributoModificado.atributo}),'%')",
			"lower(trazaAtributoModificado.valorAntes) like concat(lower(#{trazaAtributoModificadoList_bitacora.trazaAtributoModificado.valorAntes}),'%')",
			"lower(trazaAtributoModificado.valorDespues) like concat(lower(#{trazaAtributoModificadoList_bitacora.trazaAtributoModificado.valorDespues}),'%')", };

	private TrazaAtributoModificado trazaAtributoModificado = new TrazaAtributoModificado();

	@Override
	public String getEjbql() {
		return "select trazaAtributoModificado from TrazaAtributoModificado trazaAtributoModificado";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public TrazaAtributoModificado getTrazaAtributoModificado() {
		return trazaAtributoModificado;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
