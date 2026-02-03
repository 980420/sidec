package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("etapaList_ensayo")
public class EtapaList_ensayo extends EntityQuery<Etapa_ensayo> {

	private static final String EJBQL = "select etapa from Etapa_ensayo etapa";

	private static final String[] RESTRICTIONS = {
			"lower(etapa.nombreEtapa) like concat(lower(#{etapaList_ensayo.etapa.nombreEtapa}),'%')",
			"lower(etapa.descripcion) like concat(lower(#{etapaList_ensayo.etapa.descripcion}),'%')", };

	private Etapa_ensayo etapa = new Etapa_ensayo();

	public EtapaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Etapa_ensayo getEtapa() {
		return etapa;
	}
}
