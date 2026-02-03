package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("grupoSujetosList_ensayo")
public class GrupoSujetosList_ensayo extends EntityQuery<GrupoSujetos_ensayo> {

	private static final String EJBQL = "select grupoSujetos from GrupoSujetos_ensayo grupoSujetos";

	private static final String[] RESTRICTIONS = {
			"lower(grupoSujetos.nombreGrupo) like concat(lower(#{grupoSujetosList_ensayo.grupoSujetos.nombreGrupo}),'%')",
			"lower(grupoSujetos.descripcion) like concat(lower(#{grupoSujetosList_ensayo.grupoSujetos.descripcion}),'%')", };

	private GrupoSujetos_ensayo grupoSujetos = new GrupoSujetos_ensayo();

	public GrupoSujetosList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public GrupoSujetos_ensayo getGrupoSujetos() {
		return grupoSujetos;
	}
}
