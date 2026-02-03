package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoFuncionarioList_configuracion")
public class TipoFuncionarioList_configuracion extends
		EntityQuery<TipoFuncionario_configuracion> {

	private static final String EJBQL = "select tipoFuncionario from TipoFuncionario tipoFuncionario";

	private static final String[] RESTRICTIONS = { "lower(tipoFuncionario.valor) like concat(lower(#{tipoFuncionarioList.tipoFuncionario.valor}),'%')", };

	private TipoFuncionario_configuracion tipoFuncionario = new TipoFuncionario_configuracion();

	public TipoFuncionarioList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoFuncionario_configuracion getTipoFuncionario() {
		return tipoFuncionario;
	}
}
