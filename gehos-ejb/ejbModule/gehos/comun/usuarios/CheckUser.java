package gehos.comun.usuarios;

import java.security.MessageDigest;
import java.util.Calendar;

import gehos.autenticacion.entity.Profile;
import gehos.autenticacion.entity.Usuario;

import javax.persistence.EntityManager;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("checkUser")
public class CheckUser {

	@In
	private EntityManager entityManager;
	
	public void check(){
		System.out.println("Chequeando usuario activos");
		Long count = (Long)entityManager.createQuery("select count(u) from Usuario u " +
				"where (u.eliminado = false or u.eliminado is null) " +
				"and (u.cuentaHabilitada = false or u.cuentaHabilitada is null)")
				.getSingleResult();
		if(count == 0){
			try{	
				Profile prof = new Profile();
				prof.setCid(-1L);
				prof.setEliminado(false);
				prof.setLocaleString("es_VE");
				prof.setTheme("alas-verde");
				prof.setTreeLikeMenu(true);
				entityManager.persist(prof);
				
				Usuario u = new Usuario();
				u.setCid(-1L);
				u.setCedula("123456789");
				u.setCuentaHabilitada(true);
				u.setEliminado(false);
				u.setFechaNacimiento(Calendar.getInstance().getTime());
				u.setNombre("Administrador");
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update("root".getBytes());
				String md5pass = new String(Hex.encodeHex(digest.digest()));
				u.setPassword(md5pass);
				u.setPrimerApellido("Administrador");			
				u.setProfile(prof);
				u.setUsername("root");
				
				entityManager.persist(u);
				entityManager.flush();
				System.out.println("Usuario root creado");
			}
			catch(Exception e){
				System.out.println("Usuario root no pudo ser creado");
			}			
		}
		else{
			System.out.println("Existen usuarios activos");
		}
	}
	
}
