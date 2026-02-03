package gehos.notificaciones.session.auto;

import gehos.notificaciones.entity.Usuario_notifications;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("usuarioList")
public class UsuarioList extends EntityQuery<Usuario_notifications> {

	private static final String EJBQL = "select usuario from Usuario_notifications usuario";

	private static final String[] RESTRICTIONS = {
			"lower(usuario.nombre) like concat(lower(#{usuarioList.usuario.nombre}),'%')",
			"lower(usuario.username) like concat(lower(#{usuarioList.usuario.username}),'%')",
			"lower(usuario.password) like concat(lower(#{usuarioList.usuario.password}),'%')",
			"lower(usuario.primerApellido) like concat(lower(#{usuarioList.usuario.primerApellido}),'%')",
			"lower(usuario.segundoApellido) like concat(lower(#{usuarioList.usuario.segundoApellido}),'%')",
			"lower(usuario.direccionParticular) like concat(lower(#{usuarioList.usuario.direccionParticular}),'%')",
			"lower(usuario.cedula) like concat(lower(#{usuarioList.usuario.cedula}),'%')",
			"lower(usuario.pasaporte) like concat(lower(#{usuarioList.usuario.pasaporte}),'%')",
			"lower(usuario.telefono) like concat(lower(#{usuarioList.usuario.telefono}),'%')", };

	private Usuario_notifications usuario = new Usuario_notifications();

	public UsuarioList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Usuario_notifications getUsuario() {
		return usuario;
	}
}
