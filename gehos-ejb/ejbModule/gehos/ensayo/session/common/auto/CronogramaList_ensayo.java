package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cronogramaList_ensayo")
public class CronogramaList_ensayo extends EntityQuery<Cronograma_ensayo> {

	private static final String EJBQL = "select cronograma from Cronograma_ensayo cronograma";

	private static final String[] RESTRICTIONS = { "lower(cronograma.descripcion) like concat(lower(#{cronogramaList_ensayo.cronograma.descripcion}),'%')", };

	private Cronograma_ensayo cronograma = new Cronograma_ensayo();

	public CronogramaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Cronograma_ensayo getCronograma() {
		return cronograma;
	}
}
