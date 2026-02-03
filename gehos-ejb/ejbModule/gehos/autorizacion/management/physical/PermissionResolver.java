package gehos.autorizacion.management.physical;

import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.UserTools;
import gehos.comun.anillo.AnilloHisConfig;
import gehos.comun.shell.ActiveModuleSelector;
import gehos.comun.shell.IActiveModule;

import java.util.HashSet;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

import com.sun.org.apache.bcel.internal.generic.RETURN;

@AutoCreate
@Name("permissionResolver")
@Scope(ScopeType.SESSION)
public class PermissionResolver {
	@In
	FacesContext facesContext;
	@In(required = false)
	Usuario user;
	@In(required = false)
	UserTools userTools;
	@In("permissionResolverDataLoader")
	PermissionResolverDataLoader dataLoader;
	@In 
	FacesMessages facesMessages;

	@SuppressWarnings("static-access")
	public String currentViewId() {
		return facesContext.getCurrentInstance().getViewRoot().getViewId();
	}

	private final String MODULE_NOT_ACTIVATED = "module-not-activated";
	private final String NO_ACCESS = "not-authorized";
	private final String NO_HIS_INSTANCE = "no-his-instance";
	private final String NO_HIS_INSTANCE_ROOT = "no-his-instance_root";
	private final String OK = "ok";
	private final String PASSWORD_EXPIRED = "password-expired"; 

	@In
	ActiveModuleSelector activeModuleSelector;
	@In
	IActiveModule activeModule;

	@In
	AnilloHisConfig anilloHisConfig;

	@SuppressWarnings("static-access")
	public String checkCurrentUserRights() {
		String viewId = facesContext.getCurrentInstance().getViewRoot()
				.getViewId();
		// Para las paginas necesarias
		if (viewId.equals("/error.xhtml")
				|| viewId.equals("/modConfiguracion/anillo/ConfigurarAnillo.xhtml")
				|| viewId.equals("/modCommons/login/login.xhtml")
				|| viewId.equals("/modCommons/passwordchg/passwordchg.xhtml"))
			return this.OK;

		// Comprobar si hay anillo (destanque) (Evitando problema de seguridad)
		// ----------------------------------------------------------------------------------
		if (!anilloHisConfig.hayAnilloConfigurado()) {

			if (user.getUsername().equals("root"))
				return this.NO_HIS_INSTANCE_ROOT;
			else
				return this.NO_HIS_INSTANCE;

		}
		// ----------------------------------------------------------------------------------

		//Si no es un usuario del dominio y su password expiro 
	    if(user != null && user.getUsername().indexOf(64) == -1 && dataLoader.isPasswordExpired(user)) { 
	      facesMessages.clear(); 
	      facesMessages.add(SeamResourceBundle.getBundle().getString( 
	          "passwordExpiredError")); 
	      return this.PASSWORD_EXPIRED; 
	    }
	    
		// Para las paginas necesarias
		if (viewId.equals("/index.html")
				|| viewId.equals("/profile/home.xhtml")
				|| viewId.indexOf("codebase") != -1
				|| viewId.indexOf("modCommons") != -1
				|| viewId.startsWith("/modConfiguracion/incidencias/"))
			return this.OK;

		// Continuar con la logica de seguridad
		if (activeModule.getActiveModule() != null
				&& activeModule.getActiveModule().getModuloFisico()
				&& !activeModule.getActiveModule().getActivo()
				&& !viewId.contains("/config/"))
			return this.MODULE_NOT_ACTIVATED;

		if (userTools == null)
			return this.OK;

		if (user.getUsername().equals("root"))
			return this.OK;

		do {
			if (dataLoader.getPermissionsTable().containsKey(viewId)) {
				Permission temp = dataLoader.getPermissionsTable().get(viewId);
				if (temp.getRestrictedUsersHashMap().containsKey(
						this.user.getUsername()))
					return this.NO_ACCESS;
				if (temp.getPermittedUsersHashMap().containsKey(
						this.user.getUsername()))
					return this.OK;
				HashSet<String> intersection = new HashSet<String>(temp
						.getRestrictedRolesHashMap().keySet());
				intersection.retainAll(userTools.getRolesStrings());
				if (intersection.size() >= 1)
					return this.NO_ACCESS;
				HashSet<String> intersection2 = new HashSet<String>(temp
						.getPermittedRolesHashMap().keySet());
				intersection2.retainAll(userTools.getRolesStrings());
				if (intersection2.size() >= 1)
					return this.OK;
			}

			viewId = viewId.substring(0, viewId.lastIndexOf("/"));

		} while (viewId.length() > 1);

		return this.NO_ACCESS;
	}

}
