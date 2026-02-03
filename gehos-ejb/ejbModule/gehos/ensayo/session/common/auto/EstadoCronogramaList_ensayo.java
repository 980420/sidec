package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoCronogramaList_ensayo")
public class EstadoCronogramaList_ensayo extends
		EntityQuery<EstadoCronograma_ensayo> {

	private static final String EJBQL = "select estadoCronograma from EstadoCronograma_ensayo estadoCronograma";

	private static final String[] RESTRICTIONS = {
			"lower(estadoCronograma.nombre) like concat(lower(#{estadoCronogramaList_ensayo.estadoCronograma.nombre}),'%')",
			"lower(estadoCronograma.descripcion) like concat(lower(#{estadoCronogramaList_ensayo.estadoCronograma.descripcion}),'%')", };

	private EstadoCronograma_ensayo estadoCronograma = new EstadoCronograma_ensayo();

	public EstadoCronogramaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoCronograma_ensayo getEstadoCronograma() {
		return estadoCronograma;
	}
}
