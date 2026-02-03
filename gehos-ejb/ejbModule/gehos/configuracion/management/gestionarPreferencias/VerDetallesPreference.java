package gehos.configuracion.management.gestionarPreferencias;


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

import gehos.autenticacion.entity.Profile;
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

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.theme.ThemeSelector;


@Scope(ScopeType.CONVERSATION)
@Name("verDetallesPreference") 
public class VerDetallesPreference {
	
	
	@In
	Usuario user;
	
	@In
	LocaleSelector localeSelector;
	
	@In
	ThemeSelector themeSelector;
	
	@In
	EntityManager entityManager;
	
	@In
	FacesMessages facesMessages;
	
	
	
	
	
	
	//Tema
	private String theme;
	private List<String> temas = new ArrayList<String>();
	
	//Culturas
	private String culturaSelec = "";	
	List<Cultura> listaCultura = new ArrayList<Cultura>();
	
	//Menu
	private Boolean treeLikeMenu;
	
	//Instancias	
	private Usuario_configuracion usuario = new Usuario_configuracion();
	private Usuario userAux = new Usuario();
	
	
	//Metodos
	
	public void inicio(){
		cultura();		
		
		usuario = entityManager.find(Usuario_configuracion.class, user.getId());
		
		for (int i = 0; i < listaCultura.size(); i++) {
			if(listaCultura.get(i).getLocalString().equals(usuario.getProfile().getLocaleString())){
				culturaSelec = listaCultura.get(i).getIdioma();
			}	
		}
		
		theme = usuario.getProfile().getTheme();		
		
		temas.clear();
		for (int i = 0; i < themeSelector.getThemes().size(); i++) {
			temas.add( themeSelector.getThemes().get(i).getValue().toString());
		}
		
		treeLikeMenu = usuario.getProfile().getTreeLikeMenu();		
	}
	
	public List<String> cultura(){			
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		listaCultura = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {					
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(), listaSelectItem.get(i).getValue().toString());			
			listaCultura.add(c);
			lista.add(c.cultura());
		}		
		return lista;		
	}
	
	
	//Propiedades
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

	public Boolean getTreeLikeMenu() {
		return treeLikeMenu;
	}

	public void setTreeLikeMenu(Boolean treeLikeMenu) {
		this.treeLikeMenu = treeLikeMenu;
	}	
	
}
