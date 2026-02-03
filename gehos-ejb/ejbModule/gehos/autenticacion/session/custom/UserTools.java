package gehos.autenticacion.session.custom;

import gehos.autenticacion.entity.Usuario;

import java.util.HashSet;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

public class UserTools {

	private Usuario user;
	private HashSet<String> rolesStrings = new HashSet<String>();
	private boolean menuExpanded = true;
	private int IpAttempts;

	public void expandOrCollapseCategory() {
		this.menuExpanded = !this.menuExpanded;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public boolean userImageExist(String username) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context
				.getRealPath("/resources/modCommon/userphotos/" + username
						+ ".png");
		java.io.File dir = new java.io.File(rootpath);
		return dir.exists();
	}

	public boolean userSignatureExist(String username) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context
				.getRealPath("/resources/modCommon/usersignatures/" + username
						+ ".png");
		java.io.File dir = new java.io.File(rootpath);
		return dir.exists();
	}

	public String getPresentableName() {
		if (user == null)
			return "";
		else if (user.getNombre().length() < 25)
			return user.getNombre();
		return user.getNombre().substring(0, 22) + "...";
	}

	public HashSet<String> getRolesStrings() {
		return rolesStrings;
	}

	public void setRolesStrings(HashSet<String> rolesStrings) {
		this.rolesStrings = rolesStrings;
	}

	public boolean isMenuExpanded() {
		return menuExpanded;
	}

	public void setMenuExpanded(boolean menuExpanded) {
		this.menuExpanded = menuExpanded;
	}
	
	public int getIpAttempts() {
		return IpAttempts;
	}

	public void setIpAttempts(int ipAttempts) {
		this.IpAttempts = ipAttempts;
	}
}
