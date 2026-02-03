package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cieGrupoList_ensayo")
public class CieGrupoList_ensayo extends EntityQuery<CieGrupo_ensayo> {

	private static final String EJBQL = "select cieGrupo from CieGrupo_ensayo cieGrupo";

	private static final String[] RESTRICTIONS = {
			"lower(cieGrupo.codigoGrupo) like concat(lower(#{cieGrupoList_ensayo.cieGrupo.codigoGrupo}),'%')",
			"lower(cieGrupo.descripcion) like concat(lower(#{cieGrupoList_ensayo.cieGrupo.descripcion}),'%')",
			"lower(cieGrupo.incluye) like concat(lower(#{cieGrupoList_ensayo.cieGrupo.incluye}),'%')",
			"lower(cieGrupo.excluye) like concat(lower(#{cieGrupoList_ensayo.cieGrupo.excluye}),'%')",
			"lower(cieGrupo.observaciones) like concat(lower(#{cieGrupoList_ensayo.cieGrupo.observaciones}),'%')", };

	private CieGrupo_ensayo cieGrupo = new CieGrupo_ensayo();

	public CieGrupoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CieGrupo_ensayo getCieGrupo() {
		return cieGrupo;
	}
}
