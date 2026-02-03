package gehos.comun.shell;

import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name(value = "activeModuleSelector")
@Scope(ScopeType.SESSION)
public class ActiveModuleSelector {

	private EntityManager entityManager;
	private IActiveModule activeModule;

	private List<Funcionalidad> modulos; 
	private List<Pattern> patterns;
	private ConcurrentHashMap<Pattern, Funcionalidad> modulosByPattern = new ConcurrentHashMap<Pattern, Funcionalidad>();
	
	@SuppressWarnings("unchecked")
	@Create
	public void constructor(){
		entityManager = (EntityManager)Component.getInstance("entityManager");
		activeModule = (IActiveModule)Component.getInstance("activeModule");
		modulos = entityManager.createQuery("select m from Funcionalidad m " +
				"where m.esModulo = true and m.moduloFisico = true").getResultList();
		patterns = new ArrayList<Pattern>(modulos.size());
		for (Funcionalidad mod : modulos) {
			String homeViewId = mod.getUrl();
			homeViewId = homeViewId.substring(0, homeViewId.lastIndexOf("/") + 1);
			homeViewId += "/*";
			Pattern pattern = Pattern.compile(homeViewId);
			patterns.add(pattern);
			modulosByPattern.put(pattern, mod);			
		}
	}

	public void selectModuleByViewId(String viewId) {
		CharSequence sequence = viewId;
		for (Pattern pat : patterns) {
			Matcher matcher = pat.matcher(sequence);
			if (matcher.find()) {
				activeModule.setActiveModuleName(modulosByPattern.get(pat).getNombre(), true);
				return;
			}
		}
	}


}
