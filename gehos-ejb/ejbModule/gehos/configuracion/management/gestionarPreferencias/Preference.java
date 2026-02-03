package gehos.configuracion.management.gestionarPreferencias;

import gehos.autenticacion.entity.Profile;
import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.PasswordHistory_configuracion;
import gehos.configuracion.management.entity.Profile_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.gestionarUsuario.Cultura;
import gehos.configuracion.management.utilidades.Validations_configuracion;

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
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.theme.ThemeSelector;

@Scope(ScopeType.CONVERSATION)
@Name("preference")
public class Preference {

	@In
	Usuario user;

	@In
	IBitacora bitacora;

	@In
	LocaleSelector localeSelector;

	@In
	ThemeSelector themeSelector;

	@In
	EntityManager entityManager;

	@In
	FacesMessages facesMessages;

	// Contrasenna
	private String passOld, passNew, confPass;
	private Boolean pass_desigual = true;// validar que las constrasenas sean
											// iguales
	private Boolean pass_error = false, render = true;

	// Foto
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private String url_foto = "";
	private String nombrePhoto = "";

	// Tema
	private String theme;
	private List<String> temas = new ArrayList<String>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> listaCultura = new ArrayList<Cultura>();

	// Menu
	private Boolean treeLikeMenu;

	// VerificarCambios
	private Boolean treeLikeMenuVerif;
	private String culturaSelecVerif = "";
	private String themeVerif;
	private String nombreFotoVerif = "";

	// Instancias
	private Usuario_configuracion usuario = new Usuario_configuracion();
	private Usuario userAux = new Usuario();

	// Metodos

	public void inicio() {
		cultura();

		usuario = entityManager.find(Usuario_configuracion.class, user.getId());

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getLocalString()
					.equals(usuario.getProfile().getLocaleString())) {
				culturaSelec = listaCultura.get(i).getIdioma();
				culturaSelecVerif = listaCultura.get(i).getIdioma();
			}
		}

		theme = usuario.getProfile().getTheme();
		themeVerif = usuario.getProfile().getTheme();

		temas.clear();
		for (int i = 0; i < themeSelector.getThemes().size(); i++) {
			if(themeSelector.getThemes().get(i).getValue().toString().equals("alas-verde")){
				temas.add(themeSelector.getThemes().get(i).getValue().toString());
			}
		}

		treeLikeMenu = usuario.getProfile().getTreeLikeMenu();
		treeLikeMenuVerif = usuario.getProfile().getTreeLikeMenu();
	}

	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		listaCultura = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			/** puse un if para eliminar de momento los otros idiomas**/
			//if(listaSelectItem.get(i).getLabel().equals("español (Cuba)")){
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			listaCultura.add(c);
			lista.add(c.cultura());
			//}
		}
		return lista;
	}

	public void cambiarTheme() {

		usuario = entityManager.find(Usuario_configuracion.class, user.getId());

		for (int i = 0; i < themeSelector.getThemes().size(); i++) {
			if (themeSelector.getThemes().get(i).getValue().toString()
					.equals(theme)) {
				themeSelector.setTheme(theme);

			}
		}

		Profile_configuracion profile = entityManager.find(
				Profile_configuracion.class, this.user.getProfile().getId());
		profile.setTheme(theme);
		entityManager.merge(profile);
		entityManager.flush();

		usuario.setProfile(profile);
		entityManager.merge(usuario);
		entityManager.flush();

		Profile profileUser = entityManager.find(Profile.class, this.user
				.getProfile().getId());
		profile.setTheme(theme);
		entityManager.persist(profileUser);
		entityManager.flush();

		user.setProfile(profileUser);
		entityManager.merge(user);
		entityManager.flush();

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getIdioma().equals(culturaSelec)) {
				usuario.getProfile().setLocaleString(
						listaCultura.get(i).getLocalString());
				localeSelector.setLanguage(culturaSelec);
			}
		}

		entityManager.merge(usuario);
		entityManager.flush();
	}

	public void updateMenuPreferences() {
		entityManager.merge(usuario);
		user.getProfile().setTreeLikeMenu(
				usuario.getProfile().getTreeLikeMenu());
		entityManager.merge(user);
		entityManager.flush();
	}

	public String cambiarPass() throws NoSuchAlgorithmException {

		pass_desigual = true;

		try {

			userAux = (Usuario) entityManager
					.createQuery(
							"select u from Usuario u where u.username = :username and u.password = MD5(:password)")
					.setParameter("username", user.getUsername())
					.setParameter("password", this.passOld).getSingleResult();
		} catch (NoResultException ex) {
			this.pass_error = true;
			return "fail";
		}

		this.pass_desigual = this.passNew.equals(this.confPass);

		if (!pass_desigual) {
			return "fail";
		}

		usuario = entityManager.find(Usuario_configuracion.class, user.getId());

		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(passNew.getBytes());
		String md5pass = new String(Hex.encodeHex(digest.digest()));
		usuario.setPassword(md5pass);

		entityManager.merge(usuario);

		this.passNew = "";
		this.confPass = "";
		this.passOld = "";

		facesMessages.addToControlFromResourceBundle("btnAcept",
				Severity.ERROR,
				"Se ha cambiado su contrase\u00F1a satisfactoriamente.");

		return "good";

	}

	@SuppressWarnings("unchecked") 
	public String cambiar() throws NoSuchAlgorithmException {
		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[2];
		r[0] = validations.imagen(this.nombrePhoto,"addPhoto1", facesMessages);
		for (int i = 0; i < r.length; i++) {
			if (r[i]) {
				return null;
			}
		}
		if (!render || !nombrePhoto.equals(nombreFotoVerif)
				|| !theme.equals(themeVerif)
				|| !culturaSelec.equals(culturaSelecVerif)
				|| !treeLikeMenu.equals(treeLikeMenuVerif)) {

			if (!render) {
				// Password
				pass_desigual = true;
				pass_error = false;

				try {

					userAux = (Usuario) entityManager
							.createQuery(
									"select u from Usuario u where u.username = :username and u.password = MD5(:password)")
							.setParameter("username", user.getUsername())
							.setParameter("password", this.passOld)
							.getSingleResult();
				} catch (NoResultException ex) {
					this.pass_error = true;
					return "fail";
				}

				this.pass_desigual = this.passNew.equals(this.confPass);

				if (!pass_desigual) {
					return "fail";
				}

				usuario = entityManager.find(Usuario_configuracion.class,
						user.getId());

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
		          this.pass_error = true;   
		          return "fail"; 
		        } 
		        PasswordHistory_configuracion passwordHistory = new PasswordHistory_configuracion(); 
		        passwordHistory.setUsuario(usuario); 
		        passwordHistory.setPassword(md5pass); 
		        entityManager.persist(passwordHistory); 
		        
				usuario.setPassword(md5pass);
				usuario.setPasswordDate(new Date()); 

				this.passNew = "";
				this.confPass = "";
				this.passOld = "";
			}

			// Subir foto

			if (!this.nombrePhoto.equals("")) {

				// para acceder a la direccion deseada
				FacesContext aFacesContext = FacesContext.getCurrentInstance();
				ServletContext context = (ServletContext) aFacesContext
						.getExternalContext().getContext();
				String rootpath = context
						.getRealPath("resources/modCommon/userphotos");
				rootpath += "/" + this.usuario.getUsername() + ".png";

				try {
					// escribo el fichero primero
					File file = new File(rootpath);
					FileOutputStream fileOutputStream = new FileOutputStream(
							file);
					DataOutputStream dataOutputStream = new DataOutputStream(
							fileOutputStream);
					dataOutputStream.write(this.data);

					// le hago el procesamiento
					BufferedImage originalImage = ImageIO.read(file);
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
							: originalImage.getType();

					// le cambio de tamano
					BufferedImage risizeImagePng = resizeImage(originalImage,
							type);

					// la sobreescribo
					// RenderedImage renderedImage = new RenderedImage();
					ImageIO.write(risizeImagePng, "png", new File(rootpath));

					/*
					 * facesMessages.clear();
					 * facesMessages.addToControlFromResourceBundle("Acept",
					 * Severity.ERROR,
					 * "Se ha cambiado su foto satisfactoriamente.");
					 */

				} catch (IOException e) {
					System.out.println(e.getMessage());
				}

			}

			// Tema

			for (int i = 0; i < themeSelector.getThemes().size(); i++) {
				if (themeSelector.getThemes().get(i).getValue().toString()
						.equals(theme)) {
					themeSelector.setTheme(theme);

				}
			}

			/*
			 * Profile profileUser = entityManager.find(Profile.class,
			 * this.user.getProfile().getId()); profile.setTheme(theme);
			 * entityManager.persist(profileUser); entityManager.flush();
			 * 
			 * user.setProfile(profileUser); entityManager.merge(user);
			 * entityManager.flush();
			 */

			// Cultura

			for (int i = 0; i < listaCultura.size(); i++) {
				if (listaCultura.get(i).getIdioma().equals(culturaSelec)) {
					usuario.getProfile().setLocaleString(
							listaCultura.get(i).getLocalString());
					localeSelector.setLanguage(culturaSelec);
				}
			}

			Long cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));

			// usuario.setCid(cid);

			// Menu
			usuario.getProfile().setTreeLikeMenu(treeLikeMenu);
			entityManager.merge(usuario);
			user.getProfile().setTreeLikeMenu(
					usuario.getProfile().getTreeLikeMenu());

			Profile_configuracion profile = entityManager
					.find(Profile_configuracion.class, this.user.getProfile()
							.getId());

			profile.setTheme(theme);

			if (culturaSelec.equals("English")) {
				profile.setLocaleString("en");
				localeSelector.setLocaleString("en");
			}
			if (culturaSelec.equals("espa\u00F1ol (Cuba)")) {
				profile.setLocaleString("es_CU");
				localeSelector.setLocaleString("es_CU");
			}
			if (culturaSelec.equals("espa\u00F1ol (Venezuela)")) {
				profile.setLocaleString("es_VE");
				localeSelector.setLocaleString("es_VE");
			}

			profile.setCid(cid);

			entityManager.merge(profile);
			entityManager.flush();

			usuario.setProfile(profile);

			// user.setCid(cid);

			entityManager.merge(user);
			entityManager.flush();

			/*
			 * facesMessages.addToControlFromResourceBundle("aceptar",
			 * Severity.ERROR,
			 * "Se ha actualizado su perfil satisfactoriamente.");
			 */

			return "good";

		} else {
			facesMessages.addToControlFromResourceBundle("aceptar",
					Severity.ERROR, "No se ha realizado ning\u00FAn cambio");
			return "fail";
		}

	}

	public String cambiarFoto() {

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		return "good";

	}

	public void subirPhoto() {

		if (!this.nombrePhoto.equals("")) {

			usuario = entityManager.find(Usuario_configuracion.class,
					user.getId());

			// para acceder a la direccion deseada
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();
			String rootpath = context
					.getRealPath("resources/modCommon/userphotos");
			rootpath += "/" + this.usuario.getUsername() + ".png";

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

				// le cambio de tamano
				BufferedImage risizeImagePng = resizeImage(originalImage, type);

				// la sobreescribo
				// RenderedImage renderedImage = new RenderedImage();
				ImageIO.write(risizeImagePng, "png", new File(rootpath));

				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("Acept",
						Severity.ERROR,
						"Se ha cambiado su foto satisfactoriamente.");

			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		} else {
			facesMessages.addToControlFromResourceBundle("Acept",
					Severity.ERROR, "Debe seleccionar una imagen.");
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

	public void cancelar() {
		this.passNew = "";
		this.confPass = "";
		this.passOld = "";
	}

	// Propiedades
	@Length(min = 1, max = 25, message = "El m\u00E1ximo de caracteres es: 25")
	@NotEmpty
	public String getPassOld() {
		return passOld;
	}

	public void setPassOld(String passOld) {
		this.passOld = passOld;
	}

	@Length(min = 1, max = 25, message = "El m\u00E1ximo de caracteres es: 25")
	@NotEmpty
	public String getPassNew() {
		return passNew;
	}

	public void setPassNew(String passNew) {
		this.passNew = passNew;
	}

	@Length(min = 1, max = 25, message = "El m\u00E1ximo de caracteres es: 25")
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getUrl_foto() {
		return url_foto;
	}

	public void setUrl_foto(String url_foto) {
		this.url_foto = url_foto;
	}

	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}

	public static int getImgWidth() {
		return imgWidth;
	}

	public static int getImgHeight() {
		return imgHeight;
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

	public String getNombrePhoto() {
		return nombrePhoto;
	}

	public void setNombrePhoto(String nombrePhoto) {
		this.nombrePhoto = nombrePhoto;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public ThemeSelector getThemeSelector() {
		return themeSelector;
	}

	public void setThemeSelector(ThemeSelector themeSelector) {
		this.themeSelector = themeSelector;
	}

	public List<String> getTemas() {
		return temas;
	}

	public void setTemas(List<String> temas) {
		this.temas = temas;
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

	public Boolean getRender() {
		return render;
	}

	public void setRender(Boolean render) {
		this.render = render;
	}

	public Boolean getTreeLikeMenu() {
		return treeLikeMenu;
	}

	public void setTreeLikeMenu(Boolean treeLikeMenu) {
		this.treeLikeMenu = treeLikeMenu;
	}

	public Boolean getTreeLikeMenuVerif() {
		return treeLikeMenuVerif;
	}

	public void setTreeLikeMenuVerif(Boolean treeLikeMenuVerif) {
		this.treeLikeMenuVerif = treeLikeMenuVerif;
	}

	public String getCulturaSelecVerif() {
		return culturaSelecVerif;
	}

	public void setCulturaSelecVerif(String culturaSelecVerif) {
		this.culturaSelecVerif = culturaSelecVerif;
	}

	public String getThemeVerif() {
		return themeVerif;
	}

	public void setThemeVerif(String themeVerif) {
		this.themeVerif = themeVerif;
	}

	public String getNombreFotoVerif() {
		return nombreFotoVerif;
	}

	public void setNombreFotoVerif(String nombreFotoVerif) {
		this.nombreFotoVerif = nombreFotoVerif;
	}

}
