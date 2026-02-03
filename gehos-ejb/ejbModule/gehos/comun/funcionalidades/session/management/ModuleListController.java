package gehos.comun.funcionalidades.session.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

import gehos.comun.funcionalidades.entity.Funcionalidad;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("moduleListController")
public class ModuleListController {

	@In EntityManager entityManager;
	private Long moduleId; 
	private List<Funcionalidad> moduleFunctionalities;
	
	@SuppressWarnings("unchecked")
	public List<Funcionalidad> allModules(){
		return entityManager.createQuery("from Funcionalidad f " +
				"where f.esModulo = true and f.id > 0 order by f.id").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public void funcionalidadesHijas(Funcionalidad fun){
		List<Funcionalidad> temp =
				entityManager.createQuery("from Funcionalidad f " +
						"where f.esModulo = false and f.funcionalidadPadre.id = :idpadre " +
						"order by f.id").setParameter("idpadre", fun.getId()).getResultList();
		
		result.addAll(temp);
		for (Funcionalidad funcionalidad : temp) {
			funcionalidadesHijas(funcionalidad);
		}
	}
	
	List<Funcionalidad> result = new ArrayList<Funcionalidad>();
	
	@SuppressWarnings("unchecked")
	public List<Funcionalidad> allFuntionalities(){
		result = new ArrayList<Funcionalidad>();
		Funcionalidad mod = entityManager.find(Funcionalidad.class, this.moduleId);
		funcionalidadesHijas(mod);
		Collections.sort(result, new Comparator<Funcionalidad>(){
			public int compare(Funcionalidad arg0, Funcionalidad arg1) {
				return new Long(arg0.getId()).compareTo(new Long(arg1.getId()));
			}
		});
		return result;
	}
	
	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public List<Funcionalidad> getModuleFunctionalities() {
		if(moduleFunctionalities == null)
			this.moduleFunctionalities = this.allFuntionalities();
		return moduleFunctionalities;
	}

	public void setModuleFunctionalities(List<Funcionalidad> moduleFunctionalities) {
		this.moduleFunctionalities = moduleFunctionalities;
	}	
}

	