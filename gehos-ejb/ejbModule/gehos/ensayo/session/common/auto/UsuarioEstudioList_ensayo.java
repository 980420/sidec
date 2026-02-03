package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("usuarioEstudioList_ensayo")
public class UsuarioEstudioList_ensayo extends
		EntityQuery<UsuarioEstudio_ensayo> {

	private static final String EJBQL = "select usuarioEstudio from UsuarioEstudio_ensayo usuarioEstudio";

	private static final String[] RESTRICTIONS = {};

	private UsuarioEstudio_ensayo usuarioEstudio = new UsuarioEstudio_ensayo();

	public UsuarioEstudioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public UsuarioEstudio_ensayo getUsuarioEstudio() {
		return usuarioEstudio;
	}
}
