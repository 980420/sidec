package gehos.comun.confirmarOperacion;

import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.LDAPAuth;
import gehos.configuracion.management.entity.Enfermera_configuracion;
import gehos.configuracion.management.entity.Medico_configuracion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.component.html.HtmlInputHidden;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("confirmarOperacion")
@Scope(ScopeType.PAGE)
public class ConfirmarOperacionControler {
	
	private String user;
	private String password;
	private String domain;
	
	private Usuario usuario;
	
	private Boolean nurse;
	private Boolean doctor;
	private String roles;
	
	private boolean error = false;
	private boolean initialiced = false;
	private boolean executed = false;
	private String errorValue;	
	
	@In
	private EntityManager entityManager;
	@In
	private FacesMessages facesMessages;
	
	private String toMD5II(String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			String md5pass = new String(Hex.encodeHex(digest.digest()));
			return md5pass;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return password;
	}
	
	public void initialiceValues(Boolean nurse, Boolean doctor, String roles){
		initialiced = true;
		this.nurse = nurse;
		this.doctor = doctor;
		this.roles = roles;
	}
	
	public Usuario getUser(Boolean nurse, Boolean doctor, String roles){
		initialiceValues(nurse, doctor, roles);
		validate();
		if(!error){
			return usuario;
		}
		else
			return null;
	}
	
	public void validate(){	
		error = false;
		if (domain.equals("local")) {
			try{
				String md5password = toMD5II(password);
				usuario = (Usuario) entityManager
				.createQuery(
						"select u from Usuario u where u.username = :username and u.password = :password")
				.setParameter("username", user)
				.setParameter("password", md5password)
				.getSingleResult();
			}
			catch (NoResultException e) {
				errorValue = SeamResourceBundle.getBundle().getString("loginError");
				error = true;
				return;
			}
		}
		else if (domain.equals("pdvsa.com")) {
			LDAPAuth ldapAuth = new LDAPAuth();
			String cedula = ldapAuth.autenticacionLDAP(user	+ "@pdvsa.com", password);
			if (cedula != null) {
				usuario = (Usuario) entityManager
				.createQuery(
						"select u from Usuario u where u.username = :username")
				.setParameter("username", user + "@pdvsa.com")
				.getSingleResult();
			}
			else{
				errorValue = SeamResourceBundle.getBundle().getString("loginError");
				error = true;
				return;
			}
		}
		
		if(nurse != null && nurse.booleanValue()){
			try{
				Enfermera_configuracion enfermera = (Enfermera_configuracion)entityManager.createQuery(
						"select e from Enfermera_configuracion e where e.usuario.id = :id and (e.eliminado = false or e.eliminado = null)")
						.setParameter("id", usuario.getId())
						.getSingleResult();
			}
			catch (NoResultException e) {
				errorValue = SeamResourceBundle.getBundle().getString("nurseError");
				error = true;
				return;
			}
		}
		
		if(doctor != null && doctor.booleanValue()){
			try{
				Medico_configuracion enfermera = (Medico_configuracion)entityManager.createQuery(
						"select m from Medico_configuracion m where m.usuario.id = :id and (m.eliminado = false or m.eliminado = null)")
						.setParameter("id", usuario.getId())
						.getSingleResult();
			}
			catch (NoResultException e) {
				errorValue = SeamResourceBundle.getBundle().getString("doctorError");
				error = true;
				return;
			}
		}
		
		if(roles != null && roles.length() > 0){
			String[] lista = roles.split(",");
			for (Role rol : usuario.getRoles()) {
				for (String string : lista) {
					if(rol.getName().equals(string)){
						executed = true;
						return;
					}
				}
			}
			errorValue = SeamResourceBundle.getBundle().getString("roleError");
			error = true;
			return;
		}
		if(!error){
			executed = true;
		}			
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Boolean getNurse() {
		return nurse;
	}

	public void setNurse(Boolean nurse) {
		this.nurse = nurse;
	}

	public Boolean getDoctor() {
		return doctor;
	}

	public void setDoctor(Boolean doctor) {
		this.doctor = doctor;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorValue() {
		return errorValue;
	}

	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}	

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}	
}
