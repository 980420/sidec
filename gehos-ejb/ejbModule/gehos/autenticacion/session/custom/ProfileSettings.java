package gehos.autenticacion.session.custom;

import javax.persistence.EntityManager;

import gehos.autenticacion.entity.Profile;
import gehos.autenticacion.entity.Usuario;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.theme.ThemeSelector;

@Name("profileSettings")
public class ProfileSettings {

	@In
	private Usuario user;
	@In private EntityManager entityManager;
	@In	private ThemeSelector themeSelector;
	@In	private LocaleSelector localeSelector;
	
	
	public void updateUserLocale() {
		localeSelector.select();
		Profile profile = entityManager.find(Profile.class, this.user.getProfile().getId());		
		profile.setLocaleString(localeSelector.getLocaleString());		
		entityManager.persist(profile);
		entityManager.flush();		
	}

	
	public void updateUserTheme() {
		themeSelector.select();
		Profile profile = entityManager.find(Profile.class, this.user.getProfile().getId());
		profile.setTheme(themeSelector.getTheme());		
		entityManager.persist(profile);
		entityManager.flush();
	}

	public void updateSettings() {
		updateUserLocale();
		updateUserTheme();
	}

	public void updateMenuPreferences() {
		entityManager.merge(user);
		entityManager.flush();
	}
}
