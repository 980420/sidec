package gehos.comun.farmacia.session;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.APPLICATION)
@Name("recipes")
@AutoCreate
public class Recipes {

	@In
	private EntityManager entityManager;
	
	private int lastId;
	
	public synchronized int numeroRecipe(){
		if(lastId == 0){
			List<Integer> aux = (List<Integer>)
					entityManager.createQuery("select max(r.numeroRecipe) from Recipe_comun r")
					.getResultList();
			if(aux.size() != 0 && aux.get(0) != null){
				lastId = aux.get(0);
			}
		}
		lastId++;
		return lastId;
	}
	
}
