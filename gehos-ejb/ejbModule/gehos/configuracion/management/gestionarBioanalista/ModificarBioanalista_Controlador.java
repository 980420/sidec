package gehos.configuracion.management.gestionarBioanalista;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.BioanalistaInEntidad_configuracion;
import gehos.configuracion.management.entity.Bioanalista_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.gestionarUsuario.Cultura;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarBioanalista_Controlador")
@Scope(ScopeType.CONVERSATION)
public class ModificarBioanalista_Controlador {

	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	// Usuario
	private Usuario_configuracion usuario_conf = new Usuario_configuracion();
	private Boolean desiguales = true, userRep = false;
	private String username, verfpass = new String(), userold,
			contrasennaString = new String();
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private Date fechaGraduacion;
	private Boolean keepPassword = true; // no cambiar la contrasena

	// Roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> listaCultura = new ArrayList<Cultura>();

	// Medico
	private Long id;
	private Bioanalista_configuracion medico = new Bioanalista_configuracion();
	private String name = new String();
	private String matriculaColegio = new String(),
			matriculaMinisterio = new String(), matriculaMold, matriculaCold;
	private Boolean matriculaC = false, mariculaM = false;
	private Date fechaold;

	private Hashtable<Long, BioanalistaInEntidad_configuracion> entidadesExistentes = new Hashtable<Long, BioanalistaInEntidad_configuracion>();
	private List<Entidad_configuracion> entidadesSource = new ArrayList<Entidad_configuracion>();
	private List<Entidad_configuracion> entidadesTarget = new ArrayList<Entidad_configuracion>();

	// Metodos
	@SuppressWarnings("unchecked")
	public void source() {
		entidadesExistentes.clear();
		entidadesTarget.clear();
		Set<BioanalistaInEntidad_configuracion> temp = this.medico
				.getBioanalistaInEntidads();
		for (BioanalistaInEntidad_configuracion bioanalistaInEntidad : temp) {
			entidadesExistentes.put(bioanalistaInEntidad.getEntidad().getId(),
					bioanalistaInEntidad);
			entidadesTarget.add(bioanalistaInEntidad.getEntidad());
		}
		/**
		 * @author yurien 09/04/2014
		 * Se agrega la nueva restriccion para buscar las entidades que pertenecen al anillo
		 * **/
		entidadesSource = entityManager.createQuery(
				"from Entidad_configuracion e where e.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber}")
//				"from Entidad_configuracion e where e.perteneceARhio=true")
				.getResultList();
		for (BioanalistaInEntidad_configuracion bioanalistaInEntidad : temp) {
			entidadesSource.remove(bioanalistaInEntidad.getEntidad());
		}

		cultura();

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getLocalString()
					.equals(medico.getUsuario().getProfile().getLocaleString())) {
				culturaSelec = listaCultura.get(i).getIdioma();
			}
		}

		// roles
		rolsTarget.clear();
		rolsTarget.addAll(usuario_conf.getRoles());
		// validacion de roles
		List<Role_configuracion> roles = new ArrayList<Role_configuracion>();
		roles.clear();
		roles = entityManager
				.createQuery(
						"from Role_configuracion r where r.eliminado=false or r.eliminado=null order by r.name")
				.getResultList();
		for (int i = 0; i < roles.size(); i++) {
			if (estaRol(roles.get(i).getId()) == -1)
				rolsSource.add(roles.get(i));
		}

	}

	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		listaCultura = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			listaCultura.add(c);
			lista.add(c.cultura());
		}
		return lista;
	}

	private int estaRol(Long id) {
		for (int i = 0; i < rolsTarget.size(); i++) {
			if (rolsTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public List<String> cargarListaEntity() {
		return entityManager.createQuery(
				"select ent.nombre from Entidad_configuracion ent")
				.getResultList();
	}

	public void subirPhoto() {
		// para acceder a la direccion deseada
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath("resources/modCommon/userphotos");
		rootpath += "/" + this.usuario_conf.getUsername() + ".png";

		try {
			// escribo el fichero primero
			File file = new File(rootpath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);
			dataOutputStream.write(this.data);

			// le hago el procesamiento
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
					: originalImage.getType();

			// le cambio de tamaï¿½o
			BufferedImage risizeImagePng = resizeImage(originalImage, type);

			// la sobreescribo
			// RenderedImage renderedImage = new RenderedImage();
			ImageIO.write(risizeImagePng, "png", new File(rootpath));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static BufferedImage resizeImage(BufferedImage originalImage,
			int type) {
		BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
		g.dispose();

		return resizedImage;
	}

	@SuppressWarnings({ "unchecked" })
	public String modificar() throws NoSuchAlgorithmException {

		// Validacion------------(los campos requeridos, caracteres incorrectosy
		// longitud, se hace akki enel server pq se marea la pag cuando
		// refresca)

		boolean error = false;
		String us = username.toLowerCase();
		if(us.endsWith("@pdvsa.com")){
			us = us.substring(0, us.length() - 10);
		}
		
		if (us.equals("")) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (!us.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("nick",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (username.length() > 25) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		if (culturaSelec.equals("")) {
			facesMessages.addToControlFromResourceBundle("cultura",
					Severity.ERROR, "Valor requerido");
			error = true;
		}

		if (name.equals("")) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (!name.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("name",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (name.length() > 25) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		if (usuario_conf.getPrimerApellido().length() > 25) {
			facesMessages.addToControlFromResourceBundle("papellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		if (!usuario_conf.getPrimerApellido().equals("")) {
			if (!usuario_conf.getPrimerApellido().toString()
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("papellido",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		} else {
			facesMessages.addToControlFromResourceBundle("papellido",
					Severity.ERROR, "Valor requerido");
			error = true;
		}

		if (!usuario_conf.getSegundoApellido().equals("")) {
			if (!usuario_conf.getSegundoApellido().toString()
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("sapellido",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (usuario_conf.getSegundoApellido().length() > 25) {
			facesMessages.addToControlFromResourceBundle("sapellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		// if (matriculaColegio.equals("")) {
		// facesMessages.addToControlFromResourceBundle("matriculaColegio",
		// Severity.ERROR, "Valor requerido");
		// error = true;
		// } else {
		if (!matriculaColegio.equals("")
				&& !matriculaColegio.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("matriculaColegio",
					Severity.ERROR, "Caracteres incorrectos");
			error = true;
		}
		// }

		if (matriculaColegio.length() > 25) {
			facesMessages.addToControlFromResourceBundle("matriculaColegio",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		if (!keepPassword) {
			if (contrasennaString.equals("")) {
				facesMessages.addToControlFromResourceBundle("pass",
						Severity.ERROR, "Valor requerido");
				error = true;
			} else {
				if (contrasennaString.length() > 25) {
					facesMessages.addToControlFromResourceBundle("pass",
							Severity.ERROR,
							"Cantidad de caracteres entre 1 y 25");
					error = true;
				}
			}

			if (verfpass.equals("")) {
				facesMessages.addToControlFromResourceBundle("verfpass",
						Severity.ERROR, "Valor requerido");
				error = true;
			} else {
				if (verfpass.length() > 25) {
					facesMessages.addToControlFromResourceBundle("verfpass",
							Severity.ERROR,
							"Cantidad de caracteres entre 1 y 25");
					error = true;
				}
			}
		}
		
		if (entidadesTarget.size() ==0) {
			facesMessages.addToControlFromResourceBundle("test",
					Severity.ERROR, "Valor requerido");
			error = true;
		}
		
		if (error) {
			return "";
		}

		this.userRep = false;
		this.mariculaM = false;
		this.matriculaC = false;

		userRep = false;
		desiguales = this.contrasennaString.equals(verfpass);

		List<Usuario_configuracion> aux = entityManager
				.createQuery(
						"select usuario from Usuario_configuracion usuario where usuario.username =:username")
				.setParameter("username", username).getResultList();

		List<Bioanalista_configuracion> medAux = new ArrayList<Bioanalista_configuracion>();
		if (!matriculaColegio.equals("")) {
			medAux = entityManager
					.createQuery(
							"select medico from Bioanalista_configuracion medico where medico.matriculaColegio =:matriculaColegio")
					.setParameter("matriculaColegio", matriculaColegio)
					.getResultList();
		}
		if ((!keepPassword && !desiguales)
				|| (!username.equals(usuario_conf.getUsername()) && aux.size() != 0)
				|| (!matriculaColegio.equals(matriculaCold) && medAux.size() != 0)) {
			if (!username.equals(usuario_conf.getUsername())) {
				if (aux.size() != 0) {
					userRep = true;
				}
			}

			if (!matriculaColegio.equals(matriculaCold)) {
				if (medAux.size() != 0) {
					matriculaC = true;
				}
			}

			return "valor requerido";

		}

		usuario_conf.setPrimerApellido(usuario_conf.getPrimerApellido().trim());
		usuario_conf.setSegundoApellido(usuario_conf.getSegundoApellido()
				.trim());
		usuario_conf.setEliminado(false);
		usuario_conf.setNombre(name);
		usuario_conf.getRoles().clear();
		usuario_conf.getRoles().addAll(rolsTarget);

		if (!keepPassword) {
			if (!contrasennaString.equals(usuario_conf.getPassword())) {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(contrasennaString.getBytes());
				String md5pass = new String(Hex.encodeHex(digest.digest()));
				usuario_conf.setPassword(md5pass);
			}
		}

		Long cid = bitacora
				.registrarInicioDeAccion("Modificando bioanalista..");

		usuario_conf.setEliminado(false);
		usuario_conf.setUsername(username);
		usuario_conf.setCid(cid);
		entityManager.persist(usuario_conf);

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getIdioma().equals(culturaSelec)) {
				usuario_conf.getProfile().setLocaleString(
						listaCultura.get(i).getLocalString());
			}
		}

		medico.setUsuario(usuario_conf);
		medico.setMatriculaColegio(matriculaColegio);
		medico.setEliminado(false);

		medico.setCid(cid);
		entityManager.persist(medico);

		for (Entidad_configuracion ent : entidadesTarget) {
			if (!entidadesExistentes.containsKey(ent.getId())) {
				BioanalistaInEntidad_configuracion bioanalistaInEntidad = new BioanalistaInEntidad_configuracion();
				bioanalistaInEntidad.setBioanalista(medico);
				bioanalistaInEntidad.setEntidad(ent);
				bioanalistaInEntidad.setCid(cid);
				entityManager.persist(bioanalistaInEntidad);
			}
		}

		for (BioanalistaInEntidad_configuracion bio : entidadesExistentes
				.values()) {
			if (!entidadesTarget.contains(bio.getEntidad())) {
				this.medico.getBioanalistaInEntidads().remove(bio);
				entityManager.remove(bio);
			}
		}

		entityManager.flush();

		return "modificar";

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if (this.id == null) {

			this.id = id;
			userRep = false;
			usuario_conf = entityManager.find(Usuario_configuracion.class,
					this.id);
			medico = entityManager.find(Bioanalista_configuracion.class,
					this.id);
			matriculaColegio = medico.getMatriculaColegio();
			username = usuario_conf.getUsername();
			name = usuario_conf.getNombre();
			userold = username;
			// contrasennaString = usuario_conf.getPassword();
			// verfpass = usuario_conf.getPassword();
			matriculaCold = medico.getMatriculaColegio();

			source();
		}

		// Propiedades

	}

	public Usuario_configuracion getUsuario_conf() {
		return usuario_conf;
	}

	public void setUsuario_conf(Usuario_configuracion usuario_conf) {
		this.usuario_conf = usuario_conf;
	}

	public Bioanalista_configuracion getMedico() {
		return medico;
	}

	public void setMedico(Bioanalista_configuracion medico) {
		this.medico = medico;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getMatriculaColegio() {
		return matriculaColegio;
	}

	public void setMatriculaColegio(String matriculaColegio) {
		this.matriculaColegio = matriculaColegio.trim();
	}

	public String getMatriculaMinisterio() {
		return matriculaMinisterio;
	}

	public void setMatriculaMinisterio(String matriculaMinisterio) {
		this.matriculaMinisterio = matriculaMinisterio.trim();
	}

	public String getVerfpass() {
		return verfpass;
	}

	public void setVerfpass(String verfpass) {
		this.verfpass = verfpass;
	}

	public Boolean getDesiguales() {
		return desiguales;
	}

	public void setDesiguales(Boolean desiguales) {
		this.desiguales = desiguales;
	}

	public Boolean getUserRep() {
		return userRep;
	}

	public void setUserRep(Boolean userRep) {
		this.userRep = userRep;
	}

	public Boolean getMatriculaC() {
		return matriculaC;
	}

	public void setMatriculaC(Boolean matriculaC) {
		this.matriculaC = matriculaC;
	}

	public Boolean getMariculaM() {
		return mariculaM;
	}

	public void setMariculaM(Boolean mariculaM) {
		this.mariculaM = mariculaM;
	}

	public String getUserold() {
		return userold;
	}

	public void setUserold(String userold) {
		this.userold = userold;
	}

	public String getMatriculaMold() {
		return matriculaMold;
	}

	public void setMatriculaMold(String matriculaMold) {
		this.matriculaMold = matriculaMold;
	}

	public String getMatriculaCold() {
		return matriculaCold;
	}

	public void setMatriculaCold(String matriculaCold) {
		this.matriculaCold = matriculaCold;
	}

	public Date getFechaold() {
		return fechaold;
	}

	public void setFechaold(Date fechaold) {
		this.fechaold = fechaold;
	}

	public String getContrasennaString() {
		return contrasennaString;
	}

	public void setContrasennaString(String contrasennaString) {
		this.contrasennaString = contrasennaString;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public List<Role_configuracion> getRolsTarget() {
		return rolsTarget;
	}

	public void setRolsTarget(List<Role_configuracion> rolsTarget) {
		this.rolsTarget = rolsTarget;
	}

	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}

	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
	}

	public String getCulturaSelec() {
		return culturaSelec;
	}

	public void setCulturaSelec(String culturaSelec) {
		this.culturaSelec = culturaSelec;
	}

	public List<Cultura> getListaCultura() {
		return listaCultura;
	}

	public void setListaCultura(List<Cultura> listaCultura) {
		this.listaCultura = listaCultura;
	}

	public String getNombrePhoto() {
		return nombrePhoto;
	}

	public void setNombrePhoto(String nombrePhoto) {
		this.nombrePhoto = nombrePhoto;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public static int getImgWidth() {
		return imgWidth;
	}

	public static int getImgHeight() {
		return imgHeight;
	}

	public Date getFechaGraduacion() {
		return fechaGraduacion;
	}

	public void setFechaGraduacion(Date fechaGraduacion) {
		this.fechaGraduacion = fechaGraduacion;
	}

	public Boolean getKeepPassword() {
		return keepPassword;
	}

	public void setKeepPassword(Boolean keepPassword) {
		this.keepPassword = keepPassword;
	}

	public List<Entidad_configuracion> getEntidadesSource() {
		return entidadesSource;
	}

	public void setEntidadesSource(List<Entidad_configuracion> entidadesSource) {
		this.entidadesSource = entidadesSource;
	}

	public List<Entidad_configuracion> getEntidadesTarget() {
		return entidadesTarget;
	}

	public void setEntidadesTarget(List<Entidad_configuracion> entidadesTarget) {
		this.entidadesTarget = entidadesTarget;
	}

}
