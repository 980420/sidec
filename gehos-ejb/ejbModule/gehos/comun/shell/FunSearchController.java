package gehos.comun.shell;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.funcionalidades.entity.custom.FuncionalidadWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("funSearchController")
@Scope(ScopeType.SESSION)
public class FunSearchController {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION, required = false)
	IActiveModule activeModule;
	@In
	Usuario user;

	@SuppressWarnings("unchecked")
	public List SearchFuncionalidad(Object arg) {
		System.out.print("buscando por: " + (String) arg);
		String key = (String) arg;
		List<FuncionalidadWrapper> searchResult = new ArrayList<FuncionalidadWrapper>();
		List<Funcionalidad> parents = this.activeModule.getModuleMenu();
		findRecursively(key, parents, searchResult);
		System.out.print("devuelto resulta de: " + (String) arg);
		return searchResult;

	}

	@SuppressWarnings("unchecked")
	private void findRecursively(String key,
			java.util.Collection<Funcionalidad> list,
			List<FuncionalidadWrapper> searchResult) {
		if (key == null)
			return;
		for (Iterator<Funcionalidad> iterator = list.iterator(); iterator
				.hasNext();) {
			Funcionalidad f = (Funcionalidad) iterator.next();
			if (f.getLabel().toLowerCase().indexOf(key.toLowerCase()) >= 0) {
				String newurl = f.getUrl();
				if (newurl.indexOf("codebase") >= 0) {
					List<String> resultList = Arrays.asList(activeModule
							.getActiveModule().getUrl().split("/"));
					int modules = resultList.indexOf("modules");
					String substitude = resultList.get(modules) + "/"
							+ resultList.get(modules + 1);
					newurl = newurl.replace("codebase", substitude);
				} else if (f.getUrl().indexOf("selector") >= 0) {
					newurl = "/modCommons/funSelector/selector.gehos"
					/*
					 * + "?idFuncionalidad=" + f.getId() + "&idancestors=" +
					 * f.getId() + "a"
					 */;
				}
				if (!this.activeModule.getActiveModule().getNombre()
						.equals("configuracion"))
					searchResult
							.add(new FuncionalidadWrapper(f.getId(), f
									.getLabel(), newurl, this.activeModule
									.getActiveModule().getFuncionalidadPadre()
									.getFuncionalidadPadre().getNombre(), f
									.getImagen()));
				else
					searchResult
							.add(new FuncionalidadWrapper(f.getId(), f
									.getLabel(), newurl, "configuracion", f
									.getImagen()));

			}
			List<Funcionalidad> hijas = entityManager
					.createQuery(
							"from Funcionalidad f where f.funcionalidadPadre.id = :pid")
					.setParameter("pid", f.getId()).getResultList();
			findRecursively(key, hijas, searchResult);
		}
	}

	private Long selectedFuncionality;

	public String redirectToSelectedFuncionality() {
		Funcionalidad f;
		f = entityManager.find(Funcionalidad.class, selectedFuncionality);
		String newurl = f.getUrl();
		if (newurl.indexOf("codebase") >= 0) {
			List<String> resultList = Arrays.asList(activeModule
					.getActiveModule().getUrl().split("/"));
			int modules = resultList.indexOf("modules");
			String substitude = resultList.get(modules) + "/"
					+ resultList.get(modules + 1);
			newurl = newurl.replace("codebase", substitude);
			return newurl;
		}
		if (f.getUrl().indexOf("selector") >= 0)
			return "/modCommons/funSelector/selector.gehos?idFuncionalidad="
					+ f.getId() + "&idancestors=" + f.getId() + "a";
		return f.getUrl();
	}

	/*
	 * public String redirectToSelectedFuncionality(Integer
	 * selectedFuncionality){ Funcionalidad f; f =
	 * entityManager.find(Funcionalidad.class, selectedFuncionality);
	 * if(f.getUrl().indexOf("selector") >= 0) return f.getUrl() +
	 * "?idFuncionalidad=" + f.getId() + "&idancestors=" + f.getId() + "a";
	 * return f.getUrl(); }
	 */

	public Long getSelectedFuncionality() {
		return selectedFuncionality;
	}

	public void setSelectedFuncionality(Long selectedFuncionality) {
		this.selectedFuncionality = selectedFuncionality;
	}

}
