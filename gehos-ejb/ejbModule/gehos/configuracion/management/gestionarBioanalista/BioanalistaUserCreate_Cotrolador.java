package gehos.configuracion.management.gestionarBioanalista;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.BioanalistaInEntidad_configuracion;
import gehos.configuracion.management.entity.Bioanalista_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Profile_configuracion;
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
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("bioanalistaUserCreate_Cotrolador")
@Scope(ScopeType.CONVERSATION)
public class BioanalistaUserCreate_Cotrolador {

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
	private String username = new String(), pass = new String(),
			verfpass = new String(), name = new String();
	private Boolean desiguales = true, userRep = false;
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private Date fechaGaduacion;

	// Roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> culturaSource = new ArrayList<Cultura>();

	// Medico
	private Bioanalista_configuracion medico = new Bioanalista_configuracion();
	private String matriculaColegio = new String(),
			matriculaMinisterio = new String();
	private Boolean matriculaC = false, mariculaM = false;
	private Long id, idEspInEn;
	private int posEspInEn;

	// Entidad
	private String entidadSelecc;
	private List<Entidad_configuracion> entidadesSource = new ArrayList<Entidad_configuracion>();
	private List<Entidad_configuracion> entidadesTarget = new ArrayList<Entidad_configuracion>();

	// Metodos
	@SuppressWarnings("unchecked")
	@Create
	public void source() {
		cultura();

		/**
		 * @author yurien 28/03/2014
		 * Se agrega la nueva restriccion para que muestre las entidades 
		 * que pertenecen al anillo configurado
		 * **/
		this.entidadesSource = entityManager
				.createQuery(
						"select ent from Entidad_configuracion ent "
							+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//							+ "where ent.perteneceARhio = true "
						+ "order by ent.nombre asc")
				.getResultList();

		this.rolsSource = entityManager
				.createQuery(
						"select r from Role_configuracion r "
								+ "where r.eliminado = false or r.eliminado = null order by r.name asc")
				.getResultList();

	}

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
	public String crear() throws NoSuchAlgorithmException {

		// Validacion

		boolean error = false;

		if (username.equals("")) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (!username.toString().matches(
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
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
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
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
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
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
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

		if (pass.equals("")) {
			facesMessages.addToControlFromResourceBundle("pass",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (pass.length() > 25) {
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
		
		if (entidadesTarget.size() ==0) {
			facesMessages.addToControlFromResourceBundle("test",
					Severity.ERROR, "Valor requerido");
			error = true;
		}
		
		if (error) {
			return "";
		}

		userRep = false;
		mariculaM = false;
		matriculaC = false;
		desiguales = this.pass.equals(verfpass);

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
		if (!desiguales || aux.size() != 0 || medAux.size() != 0) {
			if (aux.size() != 0) {
				userRep = true;
			}

			if (medAux.size() != 0) {
				matriculaC = true;
			}

			return "valor requerido";

		}

		usuario_conf.setUsername(username);
		usuario_conf.setPrimerApellido(usuario_conf.getPrimerApellido().trim());
		usuario_conf.setSegundoApellido(usuario_conf.getSegundoApellido()
				.trim());
		usuario_conf.setNombre(name);
		usuario_conf.getRoles().addAll(rolsTarget);
		// usuario_conf.getTipoFuncionarios().clear();
		// usuario_conf.getTipoFuncionarios().addAll(tipoFuncionarioTarget);
		// usuario_conf.getCargoFuncionarios().addAll(cargoTarget);
		usuario_conf.setEliminado(false);

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		Long cid = bitacora
				.registrarInicioDeAccion("Creando nuevo bioanalista");

		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(pass.getBytes());
		String md5pass = new String(Hex.encodeHex(digest.digest()));
		usuario_conf.setPassword(md5pass);

		Profile_configuracion perfil = new Profile_configuracion();
		perfil.setEliminado(false);
		//perfil.setLocaleString("es");
		perfil.setTheme("alas-verde");
		perfil.setTreeLikeMenu(true);
		perfil.setUsuario(usuario_conf);
		usuario_conf.setProfile(perfil);
		usuario_conf.setCid(cid);

		entityManager.persist(usuario_conf);

		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
				perfil.setLocaleString(culturaSource.get(i).getLocalString());
			}
		}

		perfil.setCid(cid);
		entityManager.persist(perfil);

		medico.setUsuario(usuario_conf);
		medico.setId(usuario_conf.getId());
		medico.setMatriculaColegio(matriculaColegio);

		for (Entidad_configuracion ent : entidadesTarget) {
			ent = entityManager.merge(ent);
			BioanalistaInEntidad_configuracion bioanalista = new BioanalistaInEntidad_configuracion();
			bioanalista.setEntidad(ent);
			bioanalista.setBioanalista(medico);
			bioanalista.setCid(cid);
			entityManager.persist(bioanalista);
		}

		medico.setEliminado(false);

		medico.setCid(cid);
		entityManager.persist(medico);

		entityManager.flush();

		return "crear";

	}

	// Propiedades

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getVerfpass() {
		return verfpass;
	}

	public void setVerfpass(String verfpass) {
		this.verfpass = verfpass;
	}

	public Boolean getUserRep() {
		return userRep;
	}

	public void setUserRep(Boolean userRep) {
		this.userRep = userRep;
	}

	public Boolean getDesiguales() {
		return desiguales;
	}

	public void setDesiguales(Boolean desiguales) {
		this.desiguales = desiguales;
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

	public String getEntidadSelecc() {
		return entidadSelecc;
	}

	public void setEntidadSelecc(String entidadSelecc) {
		this.entidadSelecc = entidadSelecc;
	}

	public Long getIdEspInEn() {
		return idEspInEn;
	}

	public void setIdEspInEn(Long idEspInEn) {
		this.idEspInEn = idEspInEn;
	}

	public int getPosEspInEn() {
		return posEspInEn;
	}

	public void setPosEspInEn(int posEspInEn) {
		this.posEspInEn = posEspInEn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
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

	public List<Cultura> getCulturaSource() {
		return culturaSource;
	}

	public void setCulturaSource(List<Cultura> culturaSource) {
		this.culturaSource = culturaSource;
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

	public Date getFechaGaduacion() {
		return fechaGaduacion;
	}

	public void setFechaGaduacion(Date fechaGaduacion) {
		this.fechaGaduacion = fechaGaduacion;
	}

}
