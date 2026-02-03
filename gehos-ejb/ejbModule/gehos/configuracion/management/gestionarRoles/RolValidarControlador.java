package gehos.configuracion.management.gestionarRoles;

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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("rolValidarControlador")
public class RolValidarControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In(required = false, value = "rolModificarControlador", scope = ScopeType.CONVERSATION)
	RolModificarControlador rolModificarControlador;
	
	Validations_configuracion validation = new Validations_configuracion();
	
	FacesMessage facesMessage;
	
	//mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message",Severity.ERROR, mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}
	
	//Letters, less than 25 characters
	@SuppressWarnings("unchecked")
	public void name(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			validation.textnumber(context, component, value);
					
			List<String> lr; 
			
			if(rolModificarControlador != null){
				/*Validating that the rol has not been previously created*/
				lr = entityManager.createQuery("select r from Role_configuracion r where r.name =:name " +
											   "and r.id <>:id " +
						                       "and r.eliminado <> true")
								  .setParameter("name", value.toString().trim())		
								  .setParameter("id", rolModificarControlador.getRol().getId())	
								  .getResultList();				
			}
			else{
				/*Validating that the description of location type has not been previously created*/
				lr = entityManager.createQuery("select r from Role_configuracion r where r.name =:name " +
											   "and r.eliminado <> true")
								  .setParameter("name", value.toString().trim())										  
								  .getResultList();				
			}
			if(lr.size() != 0){
				validatorManagerGlobalExeption("entidadExistente");			
			}			
		}
	}	
	
	//Letters, less than 25 characters
		@SuppressWarnings("unchecked")
		public void codigo(FacesContext context, UIComponent component, Object value) {
			if (context.getExternalContext().getRequestParameterMap().containsKey(
					"form:buttonAceptar")) {

				validation.textnumber(context, component, value);
						
				List<String> lr; 
				
				if(rolModificarControlador != null){
					/*Validating that the rol has not been previously created*/
					lr = entityManager.createQuery("select r from Role_configuracion r where r.codigo =:codigo " +
												   "and r.id <>:id " +
							                       "and r.eliminado <> true")
									  .setParameter("codigo", value.toString().trim())		
									  .setParameter("id", rolModificarControlador.getRol().getId())	
									  .getResultList();				
				}
				else{
					/*Validating that the description of location type has not been previously created*/
					lr = entityManager.createQuery("select r from Role_configuracion r where r.codigo =:codigo " +
												   "and r.eliminado <> true")
									  .setParameter("codigo", value.toString().trim())										  
									  .getResultList();				
				}
				if(lr.size() != 0){
					validatorManagerGlobalExeption("entidadExistente");			
				}			
			}
		}	
}
