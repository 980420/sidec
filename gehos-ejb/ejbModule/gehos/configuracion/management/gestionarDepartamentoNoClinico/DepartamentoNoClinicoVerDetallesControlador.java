package gehos.configuracion.management.gestionarDepartamentoNoClinico;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Departamento_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("departamentoNoClinicoVerDetallesControlador")
public class DepartamentoNoClinicoVerDetallesControlador {
			
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	private Long departamentoId;
	private Departamento_configuracion departamento = new Departamento_configuracion();
			
	//other functions
	private String from = "";
	private int error;
	private boolean errorLoadData;
		
	//Methods ---------------------------------------------------------------------
	public void setDepartamentoId(Long departamentoId) {
		errorLoadData = false;
		try {
			this.departamentoId = departamentoId;	
			
			departamento = (Departamento_configuracion) 
			 			   entityManager.createQuery("select departamento from Departamento_configuracion departamento " +
			 					   					 "where departamento.esClinico = false " +
			 						   				 "and departamento.id = :id")
			 						   	.setParameter("id", this.departamentoId)
			 						   	.getSingleResult();
		} catch (NoResultException e) {
			errorLoadData = true;
		} catch (Exception e) {
			errorLoadData = true;
		}
	}
	
	// remove clinic departament
	public void eliminar(){
		try {
			departamento.setCid(
					bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar")));
												
			entityManager.remove(departamento);
			entityManager.flush();
			
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "msjEliminar");
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

	public Departamento_configuracion getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento_configuracion departamento) {
		this.departamento = departamento;
	}

	public Long getDepartamentoId() {
		return departamentoId;
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