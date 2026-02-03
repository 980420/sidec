package gehos.autenticacion.session.custom;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.management.entity.PasswordHistory_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.codec.binary.Hex;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Identity;


@Scope(ScopeType.CONVERSATION)
@Name("passwordChgController")
public class PasswordChgController {
	@In
	Usuario user;
	
	@In
	EntityManager entityManager;

	@In
	FacesMessages facesMessages;
	
	@In
	private RulesParser rulesParser;
	
	@In
	Identity identity;

	// Contrasenna
	private String passOld, passNew, confPass;
	private String username;
	private Boolean pass_desigual = true;// validar que las constrasenas sean
											// iguales
	private Boolean pass_error = false, render = true;


	// Instancias
	private Usuario_configuracion usuario = new Usuario_configuracion();
	private Usuario userAux = new Usuario();

	// Metodos

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		username = user.getUsername();
	}
	
	private PasswordStrength getPasswordStrength() {
		RuleBase ruleBase = null;
		try {

			ruleBase = rulesParser.readRule("/comun/passwordStrength.drl",
					RulesDirectoryBase.business_rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PasswordStrength passwordStrength = new PasswordStrength();
		
		StatefulSession session = ruleBase.newStatefulSession();
		session.insert(passwordStrength);
		session.fireAllRules();		
		session = ruleBase.newStatefulSession();
		session.insert(passwordStrength);
		session.fireAllRules();
		return passwordStrength;
	}
	
	@Transactional
	@End
	public String cambiarPass() throws NoSuchAlgorithmException {

		PasswordStrength passwordStrength = this.getPasswordStrength();
		try {

			userAux = (Usuario) entityManager
					.createQuery(
							"select u from Usuario u where u.username = :username and u.password = MD5(:password)")
					.setParameter("username", this.username)
					.setParameter("password", this.passOld).getSingleResult();
		} catch (NoResultException ex) {			
			facesMessages.add(SeamResourceBundle.getBundle().getString(
					"contrasenha_anterior_incorrecta"));
			return "fail";
		}
		
		if(this.passNew.isEmpty()) {
			facesMessages.add(SeamResourceBundle.getBundle().getString(
					"contrasenha_nueva_vacia"));
			return "fail";
		}

		this.pass_desigual = this.passNew.equals(this.confPass);

		if (!pass_desigual) {
			facesMessages.add(SeamResourceBundle.getBundle().getString(
					"contrasenha_no_coincide"));
			return "fail";
		}
		
		Validations_configuracion validations = new Validations_configuracion();
		if(validations.passwordStrength(this.passNew, passwordStrength, "passNew", facesMessages)) {
			return "fail";
		}		

		usuario = entityManager.find(Usuario_configuracion.class, userAux.getId());
		
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(passNew.getBytes());
		String md5pass = new String(Hex.encodeHex(digest.digest()));
		
		/*Comprobando si el password no esta ya en las 24 contrasenas anteriores*/
		
		List<PasswordHistory_configuracion> phl = entityManager.createQuery(
				"select ph from PasswordHistory_configuracion ph where ph.usuario.id = :id and ph.password = :password")
				.setParameter("id", this.usuario.getId())
				.setParameter("password", md5pass)
				.getResultList();
		if(phl.size() > 0){
			facesMessages.addToControlFromResourceBundle("passwordNew",
				Severity.ERROR, SeamResourceBundle.getBundle()
						.getString("passwordHistoryError"));
			return "fail";
		}
		PasswordHistory_configuracion passwordHistory = new PasswordHistory_configuracion();
		passwordHistory.setUsuario(usuario);
		passwordHistory.setPassword(md5pass);
		entityManager.persist(passwordHistory);
		usuario.setPassword(md5pass);
		usuario.setPasswordDate(new Date());

		entityManager.persist(usuario);
		entityManager.flush();

		this.passNew = "";
		this.confPass = "";
		this.passOld = "";
		facesMessages.clear();
		identity.logout();
		facesMessages.add(SeamResourceBundle.getBundle().getString(
				"contrasenha_cambiada"));
		return "changed";
	}	

	@End
	public void cancelar() {
		this.passNew = "";
		this.confPass = "";
		this.passOld = "";
		identity.logout();
	}

	// Propiedades
	@Length(min = 1, max = 25)
	@NotEmpty
	public String getPassOld() {
		return passOld;
	}

	public void setPassOld(String passOld) {
		this.passOld = passOld;
	}

	@Length(min = 1, max = 25)
	@NotEmpty
	public String getPassNew() {
		return passNew;
	}

	public void setPassNew(String passNew) {
		this.passNew = passNew;
	}

	@Length(min = 1, max = 25)
	@NotEmpty
	public String getConfPass() {
		return confPass;
	}

	public void setConfPass(String confPass) {
		this.confPass = confPass;
	}

	public Boolean getPass_desigual() {
		return pass_desigual;
	}

	public void setPass_desigual(Boolean pass_desigual) {
		this.pass_desigual = pass_desigual;
	}	

	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}
	
	public Usuario getUserAux() {
		return userAux;
	}

	public void setUserAux(Usuario userAux) {
		this.userAux = userAux;
	}

	public Boolean getPass_error() {
		return pass_error;
	}

	public void setPass_error(Boolean pass_error) {
		this.pass_error = pass_error;
	}

	public Boolean getRender() {
		return render;
	}

	public void setRender(Boolean render) {
		this.render = render;
	}
	
	@Length(min = 1, max = 25)	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
