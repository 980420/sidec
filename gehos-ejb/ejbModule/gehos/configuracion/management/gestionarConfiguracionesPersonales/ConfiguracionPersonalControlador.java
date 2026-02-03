package gehos.configuracion.management.gestionarConfiguracionesPersonales;

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

import gehos.bitacora.session.traces.IBitacora;
import gehos.autenticacion.entity.Usuario;
import gehos.configuracion.management.entity.Profile_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.gestionarUsuario.Cultura;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.theme.ThemeSelector;

@Scope(ScopeType.CONVERSATION)
@Name("configuracionPersonalControlador")
public class ConfiguracionPersonalControlador {

	@In IBitacora bitacora;
	@In Usuario user;
	@In ThemeSelector themeSelector;
	@In LocaleSelector localeSelector;
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;

	// password field
	private String passOld, passNew, confPass;
	
	// user photo	
	private byte[] data;
	private String photoName = "";
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	
	// theme
	private String theme;
	private List<String> themes;
	
	// Culturas
	private String cultureSelect;
	List<Cultura> listaCultura;
	
	private Profile_configuracion profile;
	private Usuario_configuracion usuario;
	
	// other functions
	private boolean changeMade;
	private boolean keepPassword;
	private boolean photoChange;
	private boolean themeChange;
	private boolean cultureChange;
	private boolean treeStateChange;
	private boolean treeState;	
	private boolean errorLoadData;
	private int error;
	private Long cid = -1l;
	
	// Methods---------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {				
		passOld = "";
		passNew = "";
		confPass = "";
		
		photoName = "";
		
		changeMade = false;
		keepPassword = true;
		photoChange = false;
		themeChange = false;
		cultureChange = false;
		treeStateChange = false;
		
		listaCultura = new ArrayList<Cultura>();
		themes = new ArrayList<String>();
				
		try {				
			cultura();
			
			// load user
			usuario = (Usuario_configuracion) entityManager
						.createQuery("select u from Usuario_configuracion u where u.id =:id " +
								     "and (u.eliminado = false or u.eliminado = null)")
						.setParameter("id", this.user.getId())					
						.getSingleResult();			
			
			// load profile
			profile = (Profile_configuracion) 
					  entityManager.createQuery("select p from Profile_configuracion p where p.id =:id " +
					  							"and (p.eliminado = false or p.eliminado = null)")
						  		   .setParameter("id", this.usuario.getProfile().getId())
								   .getSingleResult();
			
			if(cid.equals(-1l)){
				cid = bitacora.registrarInicioDeAccion("Modificando configuraciones personales");
				profile.setCid(cid);
			}
			
			// load user theme list
			theme = usuario.getProfile().getTheme();			
			for (int i = 0; i < themeSelector.getThemes().size(); i++) {
				themes.add(themeSelector.getThemes().get(i).getValue().toString());
			}

			//select user culture
			for (int i = 0; i < listaCultura.size(); i++) {
				if (listaCultura.get(i).getLocalString().equals(usuario.getProfile().getLocaleString())) {
					cultureSelect = listaCultura.get(i).getIdioma();					
				}
			}

			//load menu state
			treeState = usuario.getProfile().getTreeLikeMenu();			

		} catch (NoResultException e) {
			errorLoadData = true;
		}
		catch (Exception e) {
			errorLoadData = true;
		}
	}

	// returns the cultures
	public List<String> cultura() {
		try {
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
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}
		
	// change user password
	public void changePassword() throws NoSuchAlgorithmException{
		changeMade = true;
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(passNew.getBytes());
		String md5pass = new String(Hex.encodeHex(digest.digest()));
		usuario.setPassword(md5pass);
		user.setPassword(md5pass);
	}
	
	// change user theme
	public void themeChange(){
		changeMade = true;
		themeSelector.setTheme(theme);
		profile.setTheme(theme);			
	}
	
	// change culture
	public void cultureChange(){
		changeMade = true;
		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getIdioma().equals(cultureSelect)) {				
				profile.setLocaleString(listaCultura.get(i).getLocalString());
				localeSelector.setLanguage(cultureSelect);
				
				if (cultureSelect.equals("English")) {
					profile.setLocaleString("en");
					localeSelector.setLocaleString("en");
				}
				else if (cultureSelect.equals("espa\u00F1ol (Cuba)")) {
					profile.setLocaleString("es_CU");
					localeSelector.setLocaleString("es_CU");
				}
				else if(cultureSelect.equals("espa\u00F1ol (Venezuela)")) {
					profile.setLocaleString("es_VE");
					localeSelector.setLocaleString("es_VE");
				}
				return;
			}
		}
	}
	
	// change menu state 
	public void menuStateChange(){		
		changeMade = true;
		profile.setTreeLikeMenu(treeState);			
		user.getProfile().setTreeLikeMenu(treeState);
	}
	
	// load a new user photo
	public void photoChange(){
		changeMade = true;
		try {			
			// para acceder a la direccion deseada
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();
			String rootpath = context.getRealPath("resources/modCommon/userphotos");
			rootpath += "/" + this.usuario.getUsername() + ".png";
		
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
			ImageIO.write(risizeImagePng, "png", new File(rootpath));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// methods used by photoChange(), to resize the photo
	private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight,	type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
		g.dispose();

		return resizedImage;
	}
	
	@End @Transactional
	public void cambiar() throws NoSuchAlgorithmException {		
		error = 0;
		try {				
			if(!keepPassword)
				changePassword();
			if(photoChange)
				photoChange();
			if(themeChange)
				themeChange();
			if(cultureChange)
				cultureChange();
			if(treeStateChange)
				menuStateChange();	
			
			if(changeMade) {			
				entityManager.merge(profile);
				entityManager.merge(usuario);			
				usuario.setProfile(profile);			
				entityManager.flush();	
				usuario = null;
			} 
			else {
				error = 1;
				facesMessages.addToControlFromResourceBundle("error",Severity.ERROR,"noCambios");
											
			}
		}
		catch(Exception e){	
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR,"errorInesperado");						
		}
	}
	
	public void setPhotoName(String photoName) {
		if(!this.photoName.equals(photoName))
			photoChange = true;
		this.photoName = photoName;
	}
	
	public void setTheme(String theme) {
		if(!this.theme.equals(theme))
			themeChange = true;
		this.theme = theme;
	}
	
	public void setCultureSelect(String cultureSelect) {
		if(!this.cultureSelect.equals(cultureSelect))
			cultureChange = true;
		this.cultureSelect = cultureSelect;
	}
	
	public void setTreeState(boolean treeState) {	
		if(this.treeState != treeState)
			treeStateChange = true;		
		this.treeState = treeState;
	}	
	
	public void setKeepPassword(boolean keepPassword) {
		this.keepPassword = keepPassword;
		if(!keepPassword){
			passOld = "";
			passNew = "";
			confPass = "";
		}			
	}
	
	// Properties-----------------------------------------------------------
	public String getPassOld() {
		return passOld;
	}

	public void setPassOld(String passOld) {
		this.passOld = passOld;
	}

	public String getPassNew() {
		return passNew;
	}

	public void setPassNew(String passNew) {
		this.passNew = passNew;
	}

	public String getConfPass() {
		return confPass;
	}

	public void setConfPass(String confPass) {
		this.confPass = confPass;
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
	
	public String getTheme() {
		return theme;
	}	

	public ThemeSelector getThemeSelector() {
		return themeSelector;
	}

	public void setThemeSelector(ThemeSelector themeSelector) {
		this.themeSelector = themeSelector;
	}	

	public List<Cultura> getListaCultura() {
		return listaCultura;
	}

	public void setListaCultura(List<Cultura> listaCultura) {
		this.listaCultura = listaCultura;
	}	

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public String getPhotoName() {
		return photoName;
	}

	public String getCultureSelect() {
		return cultureSelect;
	}

	public boolean getTreeState() {
		return treeState;
	}

	public List<String> getThemes() {
		return themes;
	}

	public void setThemes(List<String> themes) {
		this.themes = themes;
	}
	
	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}

	public boolean isKeepPassword() {
		return keepPassword;
	}

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}	
}