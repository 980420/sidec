package gehos.configuracion.management.gestionarEstados;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Estado_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("estadoVerDetallesControlador")
public class EstadoVerDetallesControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages; 	

	private Long id;
	private Estado_configuracion estado;	

	// other functions
	private String from = "";
	private int error;
	
	// Methods-------------------------------------------------	
	public void setId(Long id) {	
		error = 0;
		this.id = id;			
		try {
			this.estado = (Estado_configuracion) 
			entityManager.createQuery("select r from Estado_configuracion r where r.id =:id")
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
	
	public void eliminar() {
		try {
			error = 0;
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			entityManager.remove(estado);
			entityManager.flush();			
			
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
			e.printStackTrace();
		}	
	}

	// Properties--------------------------------------------
	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public Long getId() {
		return id;
	}

	public Estado_configuracion getEstado() {
		return estado;
	}

	public void setEstado(Estado_configuracion estado) {
		this.estado = estado;
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