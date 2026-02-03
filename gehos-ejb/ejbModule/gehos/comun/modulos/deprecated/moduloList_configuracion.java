package gehos.comun.modulos.deprecated;

import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("moduloList_configuracion")
public class moduloList_configuracion {
	
	@In EntityManager entityManager;
	private List<Funcionalidad> modulos;
	
	@SuppressWarnings("unchecked")
	public List<Funcionalidad> modulos(){
		modulos = entityManager
			.createQuery("from Funcionalidad m where m.funcionalidadPadre.id = 0 order by m.label asc")
			.getResultList();
		for (Funcionalidad fun : modulos) {
			fun.setEliminado(!fun.getEliminado());
		}
		return modulos;
	}
	
	public void aceptar(){
		for (Funcionalidad fun : modulos) {
			fun.setEliminado(!fun.getEliminado());
			entityManager.persist(fun);
		}
		entityManager.flush();
	}

}
