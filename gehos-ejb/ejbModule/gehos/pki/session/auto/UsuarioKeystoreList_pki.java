package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("usuarioKeystoreList_pki")
public class UsuarioKeystoreList_pki extends EntityQuery<UsuarioKeystore_pki> {

	private static final String EJBQL = "select usuarioKeystore from UsuarioKeystore_pki usuarioKeystore";

	private static final String[] RESTRICTIONS = { "lower(usuarioKeystore.keystore) like concat(lower(#{usuarioKeystoreList_pki.usuarioKeystore.keystore}),'%')", };

	private UsuarioKeystore_pki usuarioKeystore = new UsuarioKeystore_pki();

	public UsuarioKeystoreList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public UsuarioKeystore_pki getUsuarioKeystore() {
		return usuarioKeystore;
	}
}
