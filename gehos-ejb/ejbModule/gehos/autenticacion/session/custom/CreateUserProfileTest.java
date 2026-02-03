package gehos.autenticacion.session.custom;

import gehos.autenticacion.entity.Profile;
import gehos.autenticacion.entity.Usuario;
import gehos.autorizacion.entity.Funcionalidad_permissions;
import gehos.autorizacion.entity.MenuItem;
import gehos.autorizacion.entity.RoleMenuitemPermission;
import gehos.autorizacion.entity.UserMenuitemPermission;
import gehos.comun.funcionalidades.entity.Funcionalidad;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("createUserProfileTest")
public class CreateUserProfileTest {

	@In EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public void moduloToFunc(){
		
/*		List<Modulo> modulos = entityManager.createQuery("from Modulo m where m.id > -1").getResultList();
		for (Modulo modulo : modulos) {
			Funcionalidad f = entityManager.find(Funcionalidad.class, modulo.getId());
			f.setNombre(modulo.getNombre());
			entityManager.persist(f);
			entityManager.flush();
		}*/
	}
	
	@SuppressWarnings("unchecked")
	public void changeIdModuloPorIdFuncionalidPadre(){
/*		List<Funcionalidad> func = entityManager.createQuery("from Funcionalidad f " +
				"where f.funcionalidadPadre = null")
				.getResultList();
		System.out.println("total de funcionalidades hijas directas de modulo: " + func.size());
		for (Funcionalidad funcionalidad : func) {
			if (funcionalidad.getModulo() != null) {
				Funcionalidad funModulo = entityManager
					.find(Funcionalidad.class, funcionalidad.getModulo().getId());
			
				funcionalidad.setFuncionalidadPadre(funModulo);
				funModulo.getFuncionalidadesHijas().add(funcionalidad);
				
				entityManager.persist(funModulo);
				entityManager.persist(funcionalidad);
				System.out.println("fixed");
			}
		}
		entityManager.flush();
		System.out.println("fin del arreglo");*/
	}
	
	@SuppressWarnings("unchecked")
	public void updateFuncionalidadPadre(){
/*		List<Funcionalidad> func = entityManager.createQuery("from Funcionalidad").getResultList();
		System.out.println("total de funcionalidades: " + func.size());
		int totalarregladas = 0;
		int totalconmasdeunpadre = 0;
		for (Funcionalidad funcionalidad : func) {
			if(funcionalidad.getFuncionalidadsForCategoryId().size() > 1){
				totalconmasdeunpadre++;
				continue;
			}
			if(funcionalidad.getFuncionalidadsForCategoryId().size() == 1){
				totalarregladas++;
				ArrayList<Funcionalidad> arr = new ArrayList<Funcionalidad>(funcionalidad.getFuncionalidadsForCategoryId());
				funcionalidad.setFuncionalidadPadre(arr.get(0));
				entityManager.persist(funcionalidad);
				entityManager.flush();
			}			
		}
		System.out.println("total de funcionalidades arregladas: " + totalarregladas);
		System.out.println("total de funcionalidades mas de un padre: " + totalconmasdeunpadre);*/
	}

	@SuppressWarnings("unchecked")
	public void fixMenuitem(){
		List<MenuItem> items = entityManager.
			createQuery("from MenuItem").getResultList();
		for (MenuItem menuitem : items) {
			Funcionalidad_permissions funcionalidad = menuitem.getFuncionalidad();
			if (funcionalidad == null) {
				entityManager.remove(menuitem);
				entityManager.flush();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void fixRoleMenuitemPermission(){
/*		List<RoleMenuitemPermission> items = entityManager.
			createQuery("from RoleMenuitemPermission").getResultList();
		for (RoleMenuitemPermission roleMenuitemPermission : items) {
			Funcionalidad_permissions funcionalidad = entityManager.
				find(Funcionalidad_permissions.class, 
						roleMenuitemPermission.getMenuItem().getFunctionalityId());
			if (funcionalidad != null) {
				roleMenuitemPermission.setModulo(funcionalidad.getModulo());
				entityManager.persist(roleMenuitemPermission);
				entityManager.flush();
			}
			else{
				entityManager.remove(roleMenuitemPermission);
				entityManager.flush();
			}
		}*/
	}
	
	@SuppressWarnings("unchecked")
	public void fixUserMenuitemPermission(){
/*		List<UserMenuitemPermission> items = entityManager.
			createQuery("from UserMenuitemPermission").getResultList();
		for (UserMenuitemPermission roleMenuitemPermission : items) {
			Funcionalidad_permissions funcionalidad = entityManager.
				find(Funcionalidad_permissions.class, 
						roleMenuitemPermission.getMenuItem().getFunctionalityId());
			if (funcionalidad != null) {
				roleMenuitemPermission.setModulo(funcionalidad.getModulo());
				entityManager.persist(roleMenuitemPermission);
				entityManager.flush();
			}
			else{
				entityManager.remove(roleMenuitemPermission);
				entityManager.flush();
			}
		}*/
	}
	
	public void createUserProfile() {
		Usuario usuario = new Usuario();
		usuario.setNombre("alejo name");
		usuario.setUsername("alejo user");
		usuario.setPassword("alejo pass");
		
		Profile profile = new Profile();
		profile.setTheme("wine");
		
		usuario.setProfile(profile);
		profile.setUsuario(usuario);
		
		entityManager.persist(usuario);
		entityManager.persist(profile);

		entityManager.flush();
	}
}
