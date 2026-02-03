package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("usuarioList_ensayo")
public class UsuarioList_ensayo extends EntityQuery<Usuario_ensayo> {

	private static final String EJBQL = "select usuario from Usuario_ensayo usuario";

	private static final String[] RESTRICTIONS = {
			"lower(usuario.nombre) like concat(lower(#{usuarioList_ensayo.usuario.nombre}),'%')",
			"lower(usuario.username) like concat(lower(#{usuarioList_ensayo.usuario.username}),'%')",
			"lower(usuario.password) like concat(lower(#{usuarioList_ensayo.usuario.password}),'%')",
			"lower(usuario.primerApellido) like concat(lower(#{usuarioList_ensayo.usuario.primerApellido}),'%')",
			"lower(usuario.segundoApellido) like concat(lower(#{usuarioList_ensayo.usuario.segundoApellido}),'%')",
			"lower(usuario.direccionParticular) like concat(lower(#{usuarioList_ensayo.usuario.direccionParticular}),'%')",
			"lower(usuario.cedula) like concat(lower(#{usuarioList_ensayo.usuario.cedula}),'%')",
			"lower(usuario.pasaporte) like concat(lower(#{usuarioList_ensayo.usuario.pasaporte}),'%')",
			"lower(usuario.telefono) like concat(lower(#{usuarioList_ensayo.usuario.telefono}),'%')",
			"lower(usuario.idRis) like concat(lower(#{usuarioList_ensayo.usuario.idRis}),'%')", };

	private Usuario_ensayo usuario = new Usuario_ensayo();

	public UsuarioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Usuario_ensayo getUsuario() {
		return usuario;
	}
}
