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
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearBioanalistaUserExist_Controlador")
@Scope(ScopeType.CONVERSATION)
public class CrearBioanalistaUserExist_Controlador {

	@In
	EntityManager entityManager;

	@In
	IBitacora bitacora;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	private Long id;

	// Datos usuario
	private Usuario_configuracion usuario_conf;
	private Boolean userRep = false, desiguales = true;
	private String username, contrasennaString = new String(),
			verfpass = new String(), name = new String(), userold;
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private Boolean keepPassword = true; // no cambiar la contrasena

	// Culturas
	private String culturaSelec = "";
	List<Cultura> culturaSource = new ArrayList<Cultura>();

	// Roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Medico
	private Bioanalista_configuracion medico = new Bioanalista_configuracion();
	private String matriculaColegio = new String();

	// Entidad
	private List<Entidad_configuracion> entidadesSource = new ArrayList<Entidad_configuracion>();
	private List<Entidad_configuracion> entidadesTarget = new ArrayList<Entidad_configuracion>();

	private boolean matriculaCRep;

	// Metodos
	@SuppressWarnings("unchecked")
	public void datosSource() {
		cultura();

		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getLocalString()
					.equals(usuario_conf.getProfile().getLocaleString())) {
				culturaSelec = culturaSource.get(i).getIdioma();
			}
		}

		// roles
		rolsTarget.clear();
		rolsTarget.addAll(usuario_conf.getRoles());
		// validacion de roles
		List<Role_configuracion> roles = new ArrayList<Role_configuracion>();
		roles.clear();
		roles = entityManager.createQuery(
				"from Role_configuracion r order by r.name").getResultList();
		for (int i = 0; i < roles.size(); i++) {
			if (estaRol(roles.get(i).getId()) == -1)
				rolsSource.add(roles.get(i));
		}

		this.entidadesSource = entityManager
				.createQuery(
						"select ent from Entidad_configuracion ent where ent.perteneceARhio = true order by ent.nombre asc")
				.getResultList();

	}

	// Para cargar la cultura del usuario exixtente
	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		culturaSource = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			culturaSource.add(c);
			lista.add(c.cultura());
		}
		return lista;
	}

	// Metodo auxiliar que se usa para la validacion del source
	private int estaRol(Long id) {
		for (int i = 0; i < rolsTarget.size(); i++) {
			if (rolsTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
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
	@End
	public String crear() throws NoSuchAlgorithmException {

		List<Bioanalista_configuracion> medicoAux = (List<Bioanalista_configuracion>) entityManager
				.createQuery(
						"select medico from Bioanalista_configuracion medico where medico.id=:id")
				.setParameter("id", usuario_conf.getId()).getResultList();

		if (medicoAux.size() != 0) {
			try {
				throw new Exception(
						"El usuario ha sido asociado a un bioanalista previamente");
			} catch (Exception e) {
				facesMessages
						.addToControlFromResourceBundle("buttonCrear",
								Severity.ERROR,
								"El usuario ha sido asociado a un bioanalista previamente");
			}
		}

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
					Severity.ERROR, "El máximo de caracteres es: 25");
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
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		if (usuario_conf.getPrimerApellido().length() > 25) {
			facesMessages.addToControlFromResourceBundle("papellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		if (!usuario_conf.getPrimerApellido().equals("")) {
			if (!usuario_conf.getPrimerApellido().toString()
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789..]+\\s*)+$")) {
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
			if (!usuario_conf.getPrimerApellido().toString()
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
					Severity.ERROR, "El máximo de caracteres es: 25");
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
							Severity.ERROR, "El máximo de caracteres es: 25");
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
							Severity.ERROR, "El máximo de caracteres es: 25");
					error = true;
				}
			}
		}

		if (error) {
			return "";
		}

		this.userRep = false;

		matriculaCRep = false;

		desiguales = this.contrasennaString.equals(verfpass);

		List<Bioanalista_configuracion> medAux = new ArrayList<Bioanalista_configuracion>();
		if (!matriculaColegio.equals("")) {
			medAux = entityManager
					.createQuery(
							"select medico from Bioanalista_configuracion medico where medico.matriculaColegio =:matriculaColegio")
					.setParameter("matriculaColegio", matriculaColegio)
					.getResultList();
		}
		List<Usuario_configuracion> aux = entityManager
				.createQuery(
						"select usuario from Usuario_configuracion usuario where usuario.username =:username")
				.setParameter("username", username).getResultList();

		if (!desiguales
				|| (!username.equals(usuario_conf.getUsername()) && aux.size() != 0)
				|| medAux.size() != 0) {

			if (medAux.size() != 0) {
				matriculaCRep = true;
			}

			if (!username.equals(usuario_conf.getUsername())) {
				if (aux.size() != 0) {
					userRep = true;
				}
			}

			return "valor requerido";
		}

		if (!keepPassword) {
			if (!contrasennaString.equals(usuario_conf.getPassword())) {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(contrasennaString.getBytes());
				String md5pass = new String(Hex.encodeHex(digest.digest()));
				usuario_conf.setPassword(md5pass);
			}
		}

		medico.setEliminado(false);
		medico.setMatriculaColegio(this.matriculaColegio);
		this.usuario_conf.getServicioInEntidads().clear();

		this.usuario_conf.getRoles().clear();
		this.usuario_conf.getRoles().addAll(rolsTarget);
		this.usuario_conf.setUsername(username);
		this.usuario_conf.setNombre(name);
		this.usuario_conf.setPrimerApellido(this.usuario_conf
				.getPrimerApellido().trim());
		this.usuario_conf.setSegundoApellido(this.usuario_conf
				.getSegundoApellido().trim());

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
				usuario_conf.getProfile().setLocaleString(
						culturaSource.get(i).getLocalString());
			}
		}

		Long cid = bitacora
				.registrarInicioDeAccion("Creando nuevo bioanalista");

		entityManager.merge(this.usuario_conf);
		medico.setUsuario(this.usuario_conf);

		entityManager.persist(medico);

		for (Entidad_configuracion ent : entidadesTarget) {
			ent = entityManager.merge(ent);
			BioanalistaInEntidad_configuracion bioanalista = new BioanalistaInEntidad_configuracion();
			bioanalista.setEntidad(ent);
			bioanalista.setBioanalista(medico);
			bioanalista.setCid(cid);
			entityManager.persist(bioanalista);
		}

		entityManager.flush();

		return "crear";
	}

	// Propiedades
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

	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}

	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
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
			username = usuario_conf.getUsername();
			name = usuario_conf.getNombre();
			userold = username;
			/*
			 * contrasennaString = usuario_conf.getPassword(); verfpass =
			 * usuario_conf.getPassword();
			 */
			datosSource();

		}
	}

	public String getMatriculaColegio() {
		return matriculaColegio;
	}

	public void setMatriculaColegio(String matriculaColegio) {
		this.matriculaColegio = matriculaColegio.trim();
	}

	public List<Role_configuracion> getRolsTarget() {
		return rolsTarget;
	}

	public void setRolsTarget(List<Role_configuracion> rolsTarget) {
		this.rolsTarget = rolsTarget;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getContrasennaString() {
		return contrasennaString;
	}

	public void setContrasennaString(String contrasennaString) {
		this.contrasennaString = contrasennaString;
	}

	public String getVerfpass() {
		return verfpass;
	}

	public void setVerfpass(String verfpass) {
		this.verfpass = verfpass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public Boolean getUserRep() {
		return userRep;
	}

	public void setUserRep(Boolean userRep) {
		this.userRep = userRep;
	}

	public String getUserold() {
		return userold;
	}

	public void setUserold(String userold) {
		this.userold = userold;
	}

	public Boolean getDesiguales() {
		return desiguales;
	}

	public void setDesiguales(Boolean desiguales) {
		this.desiguales = desiguales;
	}

	public String getCulturaSelec() {
		return culturaSelec;
	}

	public void setCulturaSelec(String culturaSelec) {
		this.culturaSelec = culturaSelec;
	}

	public List<Cultura> getCulturaSource() {
		return culturaSource;
	}

	public void setCulturaSource(List<Cultura> culturaSource) {
		this.culturaSource = culturaSource;
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

	public String getNombrePhoto() {
		return nombrePhoto;
	}

	public void setNombrePhoto(String nombrePhoto) {
		this.nombrePhoto = nombrePhoto;
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

	public boolean isMatriculaCRep() {
		return matriculaCRep;
	}

	public void setMatriculaCRep(boolean matriculaCRep) {
		this.matriculaCRep = matriculaCRep;
	}

}
