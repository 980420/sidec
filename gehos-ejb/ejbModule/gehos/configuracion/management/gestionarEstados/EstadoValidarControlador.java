package gehos.configuracion.management.gestionarEstados;

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

@Name("estadoValidarControlador")
public class EstadoValidarControlador {
	
	@In EntityManager entityManager; 
	@In FacesMessages facesMessages;
	@In(required = false, value = "estadoModificarControlador", scope = ScopeType.CONVERSATION)
	EstadoModificarControlador estadoModificarControlador;	
	Validations_configuracion validations = new Validations_configuracion();
	
	FacesMessage facesMessage;
	private String message;
	
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

			validations.number(context, component, value);
					
			List<String> le; 
			
			if(estadoModificarControlador != null){
				/*Validating that the estado has not been previously created*/
				le = entityManager.createQuery("select e from Estado_configuracion e where e.codigo =:codigo and e.id <>:id")
								  .setParameter("codigo", value.toString().trim())								
								  .setParameter("id", estadoModificarControlador.getEstado().getId())	
								  .getResultList();				
			}
			else{
				/*Validating that the description of location type has not been previously created*/
				le = entityManager.createQuery("select e from Estado_configuracion e where e.codigo =:codigo")
								  .setParameter("codigo", value.toString().trim())
								  .getResultList();				
			}
			if(le.size() != 0){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));			
			}			
		}
	}	
	
	//Letters, less than 25 characters
	@SuppressWarnings("unchecked")
	public void valor(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			validations.text(context, component, value);
					
			List<String> le; 
			
			if(estadoModificarControlador != null){
				/*Validating that the estado has not been previously created*/
				le = entityManager.createQuery("select e from Estado_configuracion e where e.valor =:valor and e.id <>:id")
								  .setParameter("valor", value.toString().trim())								
								  .setParameter("id", estadoModificarControlador.getEstado().getId())	
								  .getResultList();				
			}
			else{
				/*Validating that the description of location type has not been previously created*/
				le = entityManager.createQuery("select e from Estado_configuracion e where e.valor =:valor")
								  .setParameter("valor", value.toString().trim())
								  .getResultList();				
			}
			if(le.size() != 0){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));			
			}			
		}
	}
}
