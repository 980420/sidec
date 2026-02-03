package gehos.configuracion.management.gestionarNaciones;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Nacion_configuracion;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("nacionVerDetallesControlador")
public class NacionVerDetallesControlador {
			
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In(create = true) FacesMessages facesMessages;
	@In IBitacora bitacora;
		
	private Long nacionId;
	private Nacion_configuracion nacion;
			
	//other functions
	private String from = "";
	private int error;
		
	//Methods ---------------------------------------------------------------------
	public void setNacionId(Long nacionId) {
		error = 0;	
		try {
			this.nacionId = nacionId;
			nacion = (Nacion_configuracion)
							   entityManager.createQuery("select n from Nacion_configuracion n " +
									  					 "where n.id = :id")
									  		.setParameter("id", this.nacionId)
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
	
	@Transactional
	public void eliminar(){
		error = 0;
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			entityManager.remove(nacion);
			entityManager.flush();			
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
			e.printStackTrace();
		}	
	}

	//Properties -------------------------------------------------
	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
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

	public Nacion_configuracion getNacion() {
		return nacion;
	}

	public void setNacion(Nacion_configuracion nacion) {
		this.nacion = nacion;
	}

	public Long getNacionId() {
		return nacionId;
	}
}