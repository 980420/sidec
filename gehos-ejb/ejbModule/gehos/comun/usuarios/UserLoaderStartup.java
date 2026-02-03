package gehos.comun.usuarios;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Startup
@Scope(ScopeType.APPLICATION)
@Name("userLoaderStartup")
public class UserLoaderStartup {

}
