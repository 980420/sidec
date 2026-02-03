package gehos.configuracion.management.gestionarRoles;

import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("rolVerDetallesControlador")
public class RolVerDetallesControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;	
	@In FacesMessages facesMessages;

	private Long id;
	private Role_configuracion rol;	

	// other functions
	private String from = "";
	private int error;
	
	// Methods-------------------------------------------------	
	public void setId(Long id) {	
		error = 0;
		
		try {
			this.id = id;	
			rol = new Role_configuracion();		
			
			this.rol = (Role_configuracion) 
					   entityManager.createQuery("select r from Role_configuracion r " +
					   							 "where r.id =:id " +
					   							 "and r.eliminado <> true")
					   .setParameter("id", id)
					   .getSingleResult();
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public void eliminar() {
		try {
			error = 0;
			rol.setCid(
					bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar")));
			
			
			List<Usuario_configuracion> usuarios = entityManager.createQuery("select u from Usuario_configuracion u " +
																			 "join u.roles r where r.id = :idRol")
																.setParameter("idRol", id)
																.getResultList();
			
			// Validando que el rol está asociado a un usuario
			if(usuarios.size() != 0) {
				error = 1;
				facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
				return;
			}
			
			entityManager.remove(rol);
			entityManager.flush();			
			
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}

	// Properties--------------------------------------------
	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public Long getId() {
		return id;
	}

	public Role_configuracion getRol() {
		return rol;
	}

	public void setRol(Role_configuracion rol) {
		this.rol = rol;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}
