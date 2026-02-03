package gehos.configuracion.management.gestionarEntidadOrganizacional;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.EntidadOrganizacional_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("entidadOrganizacionalVerDetallesControlador")
public class EntidadOrganizacionalVerDetallesControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private EntidadOrganizacional_configuracion entidadOrganizacional;	
	
	@In(required = false, value = "entidadOrganizacionalBuscarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadOrganizacionalBuscarControlador", scope = ScopeType.CONVERSATION)
	EntidadOrganizacionalBuscarControlador entidadOrganizacionalBuscarControlador;

	// other functions
	private String from = "";
	private int error;
	private boolean errorLoadData;
	
	// Methods-------------------------------------------------
	@Begin(flushMode=FlushModeType.MANUAL, nested=true)
	public void setId(Long id) {
		this.id = id;		
		errorLoadData = false;
		this.id = id;			
		try {
			this.entidadOrganizacional = (EntidadOrganizacional_configuracion) 
										 entityManager.createQuery("select r from EntidadOrganizacional_configuracion r " +
										 						   "where r.id =:id")
										 			  .setParameter("id", id)
										 			  .getSingleResult();
		}
		catch(NoResultException e) {			
			errorLoadData = true;
		}	
	}	
	
	// remove organitational entity
	public void eliminar() {
		try {
			error = 0;
			bitacora.registrarInicioDeAccion("Eliminando Entidad Organizacional");
			
			entityManager.remove(entidadOrganizacional);
			entityManager.flush();
			
			entidadOrganizacionalBuscarControlador.refresh();
		} 
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "errorInesperado");			
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

	public EntidadOrganizacional_configuracion getEntidadOrganizacional() {
		return entidadOrganizacional;
	}

	public void setEntidadOrganizacional(EntidadOrganizacional_configuracion entidadOrganizacional) {
		this.entidadOrganizacional = entidadOrganizacional;
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

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}	
}
