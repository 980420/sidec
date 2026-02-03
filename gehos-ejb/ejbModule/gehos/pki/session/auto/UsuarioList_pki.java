package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("usuarioList_pki")
public class UsuarioList_pki extends EntityQuery<Usuario_pki> {

	private static final String EJBQL = "select usuario from Usuario_pki usuario";

	private static final String[] RESTRICTIONS = {
			"lower(usuario.nombre) like concat(lower(#{usuarioList_pki.usuario.nombre}),'%')",
			"lower(usuario.username) like concat(lower(#{usuarioList_pki.usuario.username}),'%')",
			"lower(usuario.password) like concat(lower(#{usuarioList_pki.usuario.password}),'%')",
			"lower(usuario.primerApellido) like concat(lower(#{usuarioList_pki.usuario.primerApellido}),'%')",
			"lower(usuario.segundoApellido) like concat(lower(#{usuarioList_pki.usuario.segundoApellido}),'%')",
			"lower(usuario.direccionParticular) like concat(lower(#{usuarioList_pki.usuario.direccionParticular}),'%')",
			"lower(usuario.cedula) like concat(lower(#{usuarioList_pki.usuario.cedula}),'%')",
			"lower(usuario.pasaporte) like concat(lower(#{usuarioList_pki.usuario.pasaporte}),'%')",
			"lower(usuario.telefono) like concat(lower(#{usuarioList_pki.usuario.telefono}),'%')",
			"lower(usuario.idRis) like concat(lower(#{usuarioList_pki.usuario.idRis}),'%')", };

	private Usuario_pki usuario = new Usuario_pki();

	public UsuarioList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Usuario_pki getUsuario() {
		return usuario;
	}
}
