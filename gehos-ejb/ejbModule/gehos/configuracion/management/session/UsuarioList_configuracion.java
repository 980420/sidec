package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("usuarioList_configuracion")
public class UsuarioList_configuracion extends
		EntityQuery<Usuario_configuracion> {

	private static final String EJBQL = "select usuario from Usuario usuario";

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

	private Usuario_configuracion usuario = new Usuario_configuracion();

	public UsuarioList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Usuario_configuracion getUsuario() {
		return usuario;
	}
}
