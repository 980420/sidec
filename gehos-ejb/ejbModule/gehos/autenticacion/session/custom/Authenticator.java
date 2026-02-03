package gehos.autenticacion.session.custom;

import gehos.autenticacion.entity.PersonLdap;
import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.datoslab.entity.Persona;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.management.entity.Profile_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.pki.ra.RaFunctions;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.theme.ThemeSelector;

@Name("authenticator")
public class Authenticator {
	@Logger
	Log log;
	@In
	Identity identity;
	@In
	Credentials credentials;
	@In
	EntityManager entityManager;
	@In(value = "actor", required = false)
	@Out(required = false)
	private Actor actor;
	@In(required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	private Usuario user;
	@In(required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	private UserTools userTools;
	@In(create = true)
	private SessionDestroy sessionDestroy;
	@In
	private SessionIdentifier sessionIdentifier;
	@In
	IBitacora bitacora;
	List<SelectItem> dominios = new ArrayList<SelectItem>();
	private String domain = SeamResourceBundle.getBundle().getString("local");

	@In
	ThemeSelector themeSelector;
	@In
	LocaleSelector localeSelector;
	@In
	FacesMessages facesMessages;
	@In(create = true)
	RaFunctions raFunctions;

	@In
	IActiveModule activeModule;

	@In
	@Out
	UserLogged userLogged;
	@In(value = "#{remoteAddr}", required = false)
	private String ipString;

	@In
	private RulesParser rulesParser;

	@SuppressWarnings("unchecked")
	private List<String> getDomains() {

		List<String> result = new ArrayList<String>();
		result = (ArrayList<String>) entityManager.createQuery(
				"select l.domain from Ldap l where l.active = true")
				.getResultList();
		result.add(SeamResourceBundle.getBundle().getString("local"));
		return result;
	}

	public void inicializar() {
		List<String> list = getDomains();
		if (list.size() - 1 != 0) {
			this.domain = list.get(0);

		} else {
			this.domain = SeamResourceBundle.getBundle().getString("local");
		}

	}

	private int conectionsLimit(String username) {
		RuleBase ruleBase = null;
		try {

			ruleBase = rulesParser.readRule("/comun/conexionesPorUsuario.drl",
					RulesDirectoryBase.business_rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConectionLimit limit = new ConectionLimit();
		limit.setUser(username);
		StatefulSession session = ruleBase.newStatefulSession();
		session.insert(limit);
		session.fireAllRules();
		return limit.getMaxConections() == -1 ? Integer.MAX_VALUE : limit
				.getMaxConections();
	}

	private int maxFailedAccessAttempts() {
		RuleBase ruleBase = null;
		try {

			ruleBase = rulesParser.readRule(
					"/comun/maxFailedAccessAttempts.drl",
					RulesDirectoryBase.business_rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MaxFailedAccessAttempts limit = new MaxFailedAccessAttempts();

		StatefulSession session = ruleBase.newStatefulSession();
		session.insert(limit);
		session.fireAllRules();
		return limit.getMaxFailedAccesAttempts() == -1 ? Integer.MAX_VALUE
				: limit.getMaxFailedAccesAttempts();
	}

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

	@SuppressWarnings("unused")
	private String toMD5(String password) {
		byte[] defaultBytes = password.getBytes();

		MessageDigest algorithm;
		try {
			algorithm = MessageDigest.getInstance("MD5");

			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return password;
	}

	/*
	 * boolean downloadFileByFTP(String server, String user, String pass, String
	 * localPath, String remotePath) { try { URL url = new URL("ftp://" + user +
	 * ":" + pass + "@" + server + remotePath + ";type=i"); URLConnection urlc =
	 * url.openConnection(); InputStream is = urlc.getInputStream();
	 * BufferedWriter bw = new BufferedWriter(new FileWriter(localPath)); int c;
	 * while ((c = is.read()) != -1) { bw.write(c); } is.close(); bw.flush();
	 * bw.close(); return true; } catch (Exception ex) { ex.printStackTrace();
	 * System.out.println(ex.getMessage()); return false; } }
	 */

	private void resizeImage(String url, String destination) {
		try {
			BufferedImage originalImage = ImageIO.read(new URL(url));
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
					: originalImage.getType();
			BufferedImage resizedImage = new BufferedImage(74, 74, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, 74, 74, null);
			g.dispose();
			ImageIO.write(resizedImage, "png", new File(destination));
		} catch (IOException e) {
		}
	}

	public boolean authenticate() {
		if (identity.isLoggedIn()) {
			identity.logout();
		}
		int maxFailedAccessAttempts = this
				.maxFailedAccessAttempts();
		if(userTools != null && userTools.getIpAttempts() > maxFailedAccessAttempts)
		{
			facesMessages.clear();
			facesMessages.add(SeamResourceBundle.getBundle().getString(
					"toomanyattempts"));
			return false;
		}
		log.info("authenticating #0", credentials.getUsername());

		String atDomain = "@" + domain;
		String userAtDomain = credentials.getUsername();

		if (!domain.equals("local")) {
			userAtDomain = credentials.getUsername() + atDomain;
		}

		if (userLogged.getUsers().containsKey(userAtDomain)) {
			HashMap<String, List<SessionTime>> regip = userLogged.getUsers()
					.get(userAtDomain);
			int cantIp = conectionsLimit(userAtDomain);
			if (!regip.containsKey(ipString) && regip.size() == cantIp) {
				facesMessages.clear();
				facesMessages.add(SeamResourceBundle.getBundle().getString(
						"cantmaxses"));
				return false;
			}
		}

		int attemptsCount = 0;

		if (domain.equals("local")) {
			try {				
				user = (Usuario) entityManager
						.createQuery(
								"select u from Usuario u where u.username = :username and u.eliminado = false")
						.setParameter("username", credentials.getUsername())
						.getSingleResult();
				// Cambio Dunior-Comprobando que el usuario tiene la cuenta habilitada
				// en el sistema
				if(!user.getCuentaHabilitada()) {
					facesMessages.clear();
					facesMessages.add(SeamResourceBundle.getBundle().getString(
							"ctadesab"));
					return false;
				}
				attemptsCount = user.getFailedAccessAttemptCount();

				String md5password = toMD5II(credentials.getPassword());

				user = (Usuario) entityManager
						.createQuery(
								"select u from Usuario u where u.username = :username and u.password = :password and u.eliminado = false")
						.setParameter("username", credentials.getUsername())
						.setParameter("password", md5password)
						.getSingleResult();				

				if (user.getFailedAccessAttemptCount() != 0) {
					user.setFailedAccessAttemptCount(0);
					entityManager.persist(user);
				}

			} catch (NoResultException ex) {
				facesMessages.clear();
				facesMessages.add(SeamResourceBundle.getBundle().getString(
						"uspassinvalido"));
				if (user != null) {
					user.setFailedAccessAttemptCount(attemptsCount + 1);					
					if (user.getFailedAccessAttemptCount() >= maxFailedAccessAttempts) {
						user.setCuentaHabilitada(false);
					}
					entityManager.persist(user);					
				}
				if (userTools == null)
					userTools = new UserTools();
				userTools.setIpAttempts(userTools.getIpAttempts()+1);
				return false;
			}

		} else if (!domain.equals("local")) {

			/** @author Fiax **/

			ControllerLdapAuth controllerLdapAuth = new ControllerLdapAuth(
					credentials.getUsername(), credentials.getPassword(),
					getDomain());
			boolean isActive = false;
			try {
				isActive = controllerLdapAuth.correctLogin();

			} catch (Exception e) {
				facesMessages.clear();
				facesMessages.add(SeamResourceBundle.getBundle().getString(
						"ldap_exception"));
				return false;
			}

			if (isActive) {

				try {
					user = (Usuario) entityManager
							.createQuery(
									"select u from Usuario u where lower(u.username) = lower(:username) "
											+ "and (u.eliminado = false or u.eliminado = null)")
							.setParameter("username", userAtDomain)
							.getSingleResult();
					// Cambio Dunior-Comprobando que el usuario tiene la cuenta habilitada
					// en el sistema
					if(!user.getCuentaHabilitada()) {
						facesMessages.clear();
						facesMessages.add(SeamResourceBundle.getBundle().getString(
								"ctadesab"));
						return false;
					}
					if (user.getFailedAccessAttemptCount() != 0) {
						user.setFailedAccessAttemptCount(0);
						entityManager.persist(user);
					}

					System.out.println("User registered .");

				} catch (Exception ex) {
					try {

						System.out.println("Unregistered user.");
						System.out.println("Registering user...");

						PersonLdap personLdap = controllerLdapAuth.getPerson();
						Persona personaHIS = new Persona();
						// personaHIS.setCedula(personLdap.getDistinguishedName());
						personaHIS.setNombres(personLdap.getFirstname());
						personaHIS.setApellido1(personLdap.getLastname1());
						personaHIS.setApellido2(personLdap.getLastname2());
						personaHIS.setCorreoElectronico(personLdap.getEmail());
						personaHIS.setEliminado(false);
						// personaHIS.setFechaNacimiento();
						// personaHIS.setFoto();
						// personaHIS.setIdSexo();
						// personaHIS.setIdDireccionParticular();
						personaHIS.setTelfCelular(personLdap.getMobile());

						Usuario_configuracion usuarioHIS = new Usuario_configuracion();

						usuarioHIS.setUsername(personLdap.getUser() + atDomain);
						// usuarioHIS.setCedula();
						usuarioHIS.setCuentaHabilitada(true);
						usuarioHIS.setEliminado(false);
						usuarioHIS.setNombre(personLdap.getFirstname());
						usuarioHIS.setPrimerApellido(personLdap.getLastname1());
						usuarioHIS
								.setSegundoApellido(personLdap.getLastname2());
						usuarioHIS.setTelefono(personLdap.getMobile());
						usuarioHIS.setPersona(personaHIS);
						usuarioHIS.setPassword(getCadenaAleatoria(10));

						Profile_configuracion perfil = new Profile_configuracion();
						perfil.setEliminado(false);
						perfil.setLocaleString("es_CU");
						perfil.setTheme("alas-verde");
						perfil.setTreeLikeMenu(true);
						perfil.setCid(-1L);

						perfil.setUsuario(usuarioHIS);
						usuarioHIS.setProfile(perfil);

						entityManager.persist(personaHIS);
						entityManager.persist(usuarioHIS);
						entityManager.persist(perfil);

						// boolean res;
						//
						// res = raFunctions.createUserKeyStore(usuarioHIS);
						//
						// if (!res) {
						// facesMessages.add(SeamResourceBundle.getBundle()
						// .getString("keystoreerror"));
						// return false;
						// }

						entityManager.flush();

						user = (Usuario) entityManager
								.createQuery(
										"select u from Usuario u where lower(u.username) = lower(:username) and u.eliminado = false")
								.setParameter("username", userAtDomain)
								.getSingleResult();

						System.out.println("User registered.");

					} catch (EntityExistsException e) {
						facesMessages.clear();
						facesMessages.add(SeamResourceBundle.getBundle()
								.getString("userexist"));
						return false;
					} catch (Exception e) {
						facesMessages.clear();
						facesMessages.add(SeamResourceBundle.getBundle()
								.getString("usnoconf"));
						return false;
					}

				}

			} else {
				facesMessages.clear();
				facesMessages.add(SeamResourceBundle.getBundle().getString(
						"uspassinvalido"));
				try {
					user = (Usuario) entityManager
							.createQuery(
									"select u from Usuario u where lower(u.username) = lower(:username) "
											+ "and (u.eliminado = false or u.eliminado = null)")
							.setParameter("username", userAtDomain)
							.getSingleResult();
					if (user != null) {
						user.setFailedAccessAttemptCount(attemptsCount + 1);						
						if (user.getFailedAccessAttemptCount() >= maxFailedAccessAttempts) {
							user.setCuentaHabilitada(false);
						}
						entityManager.persist(user);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;

			}

		} else if (domain.equals("pdvsa.com")) {
			LDAPAuth ldapAuth = new LDAPAuth();
			String cedula = ldapAuth.autenticacionLDAP(
					credentials.getUsername() + "@pdvsa.com",
					credentials.getPassword());
			if (cedula != null) {
				try {
					user = (Usuario) entityManager
							.createQuery(
									"select u from Usuario u where lower(u.username) = lower(:username) "
											+ "and u.eliminado = false")
							.setParameter("username",
									credentials.getUsername() + "@pdvsa.com")
							.getSingleResult();
					cedula = user.getCedula();
				} catch (Exception e) {
					try {
						Persona persona = (Persona) entityManager
								.createQuery(
										"select p from Persona p where p.cedula = :cedula and p.datosLaborales is not null")
								.setParameter("cedula", cedula)
								.setMaxResults(1).getSingleResult();

						Usuario_configuracion nuevoldap = new Usuario_configuracion();

						nuevoldap.setUsername(credentials.getUsername()
								.toLowerCase() + "@pdvsa.com");
						nuevoldap.setNombre(persona.getNombres());
						nuevoldap.setPrimerApellido(persona.getApellido1());
						nuevoldap.setSegundoApellido(persona.getApellido2());
						nuevoldap.setCedula(persona.getCedula());
						nuevoldap.setTelefono(persona.getTelfCelular());
						nuevoldap.setFechaNacimiento(persona
								.getFechaNacimiento());
						nuevoldap.setPassword("ldap");
						nuevoldap.setEliminado(false);
						nuevoldap.setCid(-1L);

						Profile_configuracion perfil = new Profile_configuracion();
						perfil.setEliminado(false);
						perfil.setLocaleString("es_VE");
						perfil.setTheme("alas-verde");
						perfil.setTreeLikeMenu(true);
						perfil.setUsuario(nuevoldap);
						perfil.setCid(-1L);
						// nuevoldap.setProfile(perfil);
						nuevoldap.setPersona(persona);

						// falta foto

						FacesContext aFacesContext = FacesContext
								.getCurrentInstance();
						ServletContext context = (ServletContext) aFacesContext
								.getExternalContext().getContext();
						String rootpath = context
								.getRealPath("resources/modCommon/userphotos");
						rootpath += "/" + nuevoldap.getUsername() + ".png";

						String ced2 = cedula;

						while (ced2.length() < 9) {
							ced2 = "0" + ced2;
						}

						resizeImage("http://ccschu14.pdvsa.com/photos/" + ced2
								+ ".jpg", rootpath);

						entityManager.persist(nuevoldap);
						entityManager.persist(perfil);
						nuevoldap.setProfile(perfil);

						boolean res = raFunctions.createUserKeyStore(nuevoldap);
						if (!res) {
							facesMessages.add(SeamResourceBundle.getBundle()
									.getString("keystoreerror"));
							return false;
						}

						entityManager.flush();

						user = (Usuario) entityManager
								.createQuery(
										"select u from Usuario u where lower(u.username) = lower(:username) and u.eliminado = false")
								.setParameter(
										"username",
										credentials.getUsername()
												+ "@pdvsa.com")
								.getSingleResult();
					} catch (Exception e1) {
						facesMessages.clear();
						facesMessages.add(SeamResourceBundle.getBundle()
								.getString("usnoconf"));
						return false;
					}
				}
			} else {
				facesMessages.clear();
				facesMessages.add(SeamResourceBundle.getBundle().getString(
						"uspassinvalido"));
				return false;
			}

		}

		try {
			if (userTools == null)
				userTools = new UserTools();
			userTools.setUser(user);

			if (actor != null) {
				actor.setId(user.getUsername());
				if (user.getRoles() != null) {
					for (Role mr : user.getRoles()) {
						Identity.instance().addRole(mr.getName());
						actor.getGroupActorIds().add(mr.getName());
						userTools.getRolesStrings().add(mr.getName());
					}
				}
			}
			/** 15/01/2014 @author yurien **/

			// Para cuando se monte el HIS por primera vez en una region
			// Se verifica que cuando se autentique el user si no tiene profile
			// se cree uno por defecto
			if (user.getProfile() == null) {

				Profile_configuracion perfil = null;

				if (user.getProfile() == null) {
					perfil = new Profile_configuracion();
					perfil.setEliminado(false);
					perfil.setLocaleString("es_cu");
					perfil.setTheme("alas-verde");
					perfil.setTreeLikeMenu(true);
					perfil.setCid(-1L);

					// Se asocia el perfil al usuario
					Usuario_configuracion u = (Usuario_configuracion) entityManager
							.find(Usuario_configuracion.class, user.getId());

					perfil.setUsuario(u);
					entityManager.persist(perfil);

					// Se cargan en la session los temas del perfil y el
					// lenguaje
					themeSelector.setTheme(perfil.getTheme());
					localeSelector.setLocaleString(perfil.getLocaleString());

				}

			} else {// Si el usuario tiene profile cargarlos en la session

				if (user.getProfile().getTheme() != null)
					if (Arrays.asList(themeSelector.getAvailableThemes())
							.contains(user.getProfile().getTheme()))
						themeSelector.setTheme(user.getProfile().getTheme());
					else
						themeSelector.setTheme("default");
				else
					themeSelector.setTheme("default");

				if (user.getProfile().getLocaleString() != null)
					localeSelector.setLocaleString(user.getProfile()
							.getLocaleString());
				else
					localeSelector.setLocaleString("es");
			}
			// Registro de usuarios loggeados
			String sessionId = sessionIdentifier.getId();
			sessionDestroy.setSessionId(sessionId);

			userLogged.registerUser(userAtDomain, ipString, sessionId);

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Observer("org.jboss.seam.security.loginSuccessful")
	public void updateUserStats() {
		activeModule.setActiveModuleName("alas_his", false);
		bitacora.registrarInicioDeSession(user.getUsername());
	}

	public String moveOn() {
		// activeModule.setActiveModuleName("alas_his", false);
		return "moveon";
	}

	public Usuario getUser() {
		return userTools.getUser();
	}

	public void setUser(Usuario user) {
		userTools.setUser(user);
	}

	private String getCadenaAleatoria(int longitud) {
		String cadenaAleatoria = "";
		long milis = new java.util.GregorianCalendar().getTimeInMillis();
		Random r = new Random(milis);
		int i = 0;
		while (i < longitud) {
			char c = (char) r.nextInt(255);
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
				cadenaAleatoria += c;
				i++;
			}
		}
		return cadenaAleatoria;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public List<SelectItem> getDominios() {
		if (this.dominios == null || this.dominios.isEmpty()) {
			this.dominios = new ArrayList<SelectItem>();
			List<String> list = getDomains();

			for (int i = 0; i < list.size(); i++) {
				SelectItem item = new SelectItem();
				item.setValue(list.get(i));
				item.setLabel(list.get(i));
				this.dominios.add(item);
			}

		}
		return dominios;
	}

	public void setDominios(List<SelectItem> dominios) {
		this.dominios = dominios;
	}

}
