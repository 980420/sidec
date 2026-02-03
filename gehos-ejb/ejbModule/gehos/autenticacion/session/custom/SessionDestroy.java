package gehos.autenticacion.session.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

import gehos.autenticacion.entity.Usuario;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;

@Name("sessionDestroy")
@Scope(ScopeType.SESSION)
public class SessionDestroy {

	@In
	private UserLogged userLogged;
	@In(required = false)
	private Usuario user;
	@In
	private Credentials credentials;
	@In(value = "#{remoteAddr}", required = false)
	private String ipString;
	private String sessionId;
	
	@Destroy
	public void unLog(){
		if(user != null){
			userLogged.unregisterUser(user.getUsername(), sessionId);
		}	
	}
	
	public void update(){
		userLogged.updateSessionLastTime(user.getUsername(), sessionId);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
	
	
	
}
