package gehos.configuracion.management.gestionarTipoEntidad;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoEntidad_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("tipoEntidadVerDetallesControlador")
public class TipoEntidadVerDetallesControlador {
		
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	private Long id;
	private TipoEntidad_configuracion tipoEntidad = new TipoEntidad_configuracion();
				
	//otras funcionalidades
	private String from = "";
	private int error;
		
	//Methos ---------------------------------------------------------------------		
	public void setId(Long id) {
		error = 0;
		try {
			this.id = id;
			tipoEntidad = (TipoEntidad_configuracion) 
			  			  entityManager.createQuery("select t from TipoEntidad_configuracion t " +
			  			  						    "where t.id = :idTipoEntidad")
			  			  	           .setParameter("idTipoEntidad", id)
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
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			entityManager.remove(tipoEntidad);
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

	public TipoEntidad_configuracion getTipoEntidad() {
		return tipoEntidad;
	}

	public void setTipoEntidad(TipoEntidad_configuracion tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Long getId() {
		return id;
	}	
}