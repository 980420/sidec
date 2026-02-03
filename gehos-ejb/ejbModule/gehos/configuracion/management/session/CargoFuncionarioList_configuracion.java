package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cargoFuncionarioList_configuracion")
public class CargoFuncionarioList_configuracion extends
		EntityQuery<CargoFuncionario_configuracion> {

	private static final String EJBQL = "select cargoFuncionario from CargoFuncionario cargoFuncionario";

	private static final String[] RESTRICTIONS = { "lower(cargoFuncionario.valor) like concat(lower(#{cargoFuncionarioList.cargoFuncionario.valor}),'%')", };

	private CargoFuncionario_configuracion cargoFuncionario = new CargoFuncionario_configuracion();

	public CargoFuncionarioList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CargoFuncionario_configuracion getCargoFuncionario() {
		return cargoFuncionario;
	}
}
