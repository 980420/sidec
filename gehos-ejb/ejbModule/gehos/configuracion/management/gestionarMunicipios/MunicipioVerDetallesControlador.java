package gehos.configuracion.management.gestionarMunicipios;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Municipio_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("municipioVerDetallesControlador")
public class MunicipioVerDetallesControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages; 
	
	private Long id;
	private Municipio_configuracion municipio;	

	// other functions
	private String from = "";
	private int error;
	
	// Methods-------------------------------------------------	
	public void setId(Long id) {	
		error = 0;
		this.id = id;			
		try {
			this.municipio = (Municipio_configuracion) 
			  entityManager.createQuery("select m from Municipio_configuracion m " +
			  							"where m.id =:id " +
			  							"and m.eliminado = false " +
			  							"and m.estado.eliminado = fase " +
			  							"and m.estado.nacion.eliminado = false")
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
	
	// remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));			
						
			entityManager.remove (municipio);
			entityManager.flush();	
			
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
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

	public Municipio_configuracion getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio_configuracion municipio) {
		this.municipio = municipio;
	}
}