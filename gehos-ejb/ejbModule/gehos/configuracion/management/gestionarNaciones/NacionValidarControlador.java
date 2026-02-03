package gehos.configuracion.management.gestionarNaciones;

import gehos.configuracion.management.utilidades.Validations_configuracion;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("nacionValidarControlador")
public class NacionValidarControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In(required = false, value = "nacionModificarControlador", scope = ScopeType.CONVERSATION)
	NacionModificarControlador nacionModificarControlador;	
	
	Validations_configuracion validation = new Validations_configuracion();
	FacesMessage facesMessage;
	
	private String message;
	
	//mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message",Severity.ERROR, mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}
	
	//mensajes en los campos (azterizcos rojos)
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}
	
	//Letters, less than 25 characters
	@SuppressWarnings("unchecked")
	public void codigo(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {
			
			validation.number(context, component, value);			
					
			List<String> ln; 
		
			if(nacionModificarControlador != null){
				/*Validating that the official functionary rate has not been previously created*/
				ln = entityManager.createQuery("select n.codigo from Nacion_configuracion n where n.codigo =:valor and n.id <>:id")
								  .setParameter("valor", value.toString().trim())	
								  .setParameter("id", nacionModificarControlador.getNacion().getId())
								  .getResultList();
			}
			else{
				/*Validating that the location type has not been previously created*/
				ln = entityManager.createQuery("select n.codigo from Nacion_configuracion n where n.codigo =:valor")
					     		  .setParameter("valor", value.toString().trim())										   
								  .getResultList();
			}
			if (ln.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));
			}				
	    }
	}
	
	//Letters, less than 25 characters
	@SuppressWarnings("unchecked")
	public void valor(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			validation.text(context, component, value);
					
			List<String> ln; 
		
			if(nacionModificarControlador != null){
				/*Validating that the official functionary rate has not been previously created*/
				ln = entityManager.createQuery("select n.valor from Nacion_configuracion n where n.valor =:valor and n.id <>:id")
								  .setParameter("valor", value.toString().trim())	
								  .setParameter("id", nacionModificarControlador.getNacion().getId())
								  .getResultList();
			}
			else{
				/*Validating that the location type has not been previously created*/
				ln = entityManager.createQuery("select n.valor from Nacion_configuracion n where n.valor =:valor")
					     		  .setParameter("valor", value.toString().trim())										   
								  .getResultList();
			}
			if (ln.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));
			}				
	    }
	}
		
	//Letters, less than 25 characters
	@SuppressWarnings("unchecked")
	public void nacionalidad(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			validation.text(context, component, value);
						
			List<String> ln; 
			
			if(nacionModificarControlador != null){
				/*Validating that the official functionary rate has not been previously created*/
				ln = entityManager.createQuery("select n.nacionalidad from Nacion_configuracion n where n.nacionalidad =:valor and n.id <>:id")
								  .setParameter("valor", value.toString().trim())	
								  .setParameter("id", nacionModificarControlador.getNacion().getId())
								  .getResultList();
			}
			else{
				/*Validating that the location type has not been previously created*/
				ln = entityManager.createQuery("select n.nacionalidad from Nacion_configuracion n where n.nacionalidad =:valor")
					     		  .setParameter("valor", value.toString().trim())										   
								  .getResultList();
			}
			if (ln.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));
			}				
		}
	}
}