package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reReporteexpeditoList_ensayo")
public class ReReporteexpeditoList_ensayo extends
		EntityQuery<ReReporteexpedito_ensayo> {

	private static final String EJBQL = "select reReporteexpedito from ReReporteexpedito_ensayo reReporteexpedito";

	private static final String[] RESTRICTIONS = {
			"lower(reReporteexpedito.eventoadverso) like concat(lower(#{reReporteexpeditoList_ensayo.reReporteexpedito.eventoadverso}),'%')",
			"lower(reReporteexpedito.descripcionea) like concat(lower(#{reReporteexpeditoList_ensayo.reReporteexpedito.descripcionea}),'%')",
			"lower(reReporteexpedito.productoinvestigado) like concat(lower(#{reReporteexpeditoList_ensayo.reReporteexpedito.productoinvestigado}),'%')",
			"lower(reReporteexpedito.lote) like concat(lower(#{reReporteexpeditoList_ensayo.reReporteexpedito.lote}),'%')",
			"lower(reReporteexpedito.descripcionsecuela) like concat(lower(#{reReporteexpeditoList_ensayo.reReporteexpedito.descripcionsecuela}),'%')",
			"lower(reReporteexpedito.otroscomentarios) like concat(lower(#{reReporteexpeditoList_ensayo.reReporteexpedito.otroscomentarios}),'%')", };

	private ReReporteexpedito_ensayo reReporteexpedito = new ReReporteexpedito_ensayo();

	public ReReporteexpeditoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReReporteexpedito_ensayo getReReporteexpedito() {
		return reReporteexpedito;
	}
}
