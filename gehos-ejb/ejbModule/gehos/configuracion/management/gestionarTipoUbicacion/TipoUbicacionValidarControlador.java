package gehos.configuracion.management.gestionarTipoUbicacion;

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

@Name("tipoUbicacionValidarControlador")
public class TipoUbicacionValidarControlador {
	
	@In EntityManager entityManager;
	@In(required = false, value = "tipoUbicacionModificarControlador", scope = ScopeType.CONVERSATION)
	TipoUbicacionModificarControlador tipoUbicacionModificarControlador;	
	
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
	public void description(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789-]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
					
			List<String> ld; 
			
			if(tipoUbicacionModificarControlador != null){
				/*Validating that the description of location type has not been previously created*/
				ld = entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.descripcion =:description and t.id <>:id")
								  .setParameter("description", value.toString().trim())		
								  .setParameter("id", tipoUbicacionModificarControlador.getTipo().getId())	
								  .getResultList();				
			}
			else{
				/*Validating that the description of location type has not been previously created*/
				ld = entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.descripcion =:description")
								  .setParameter("description", value.toString().trim())											  
								  .getResultList();				
			}
			if(ld.size() != 0){
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("valorExistente"));				
			}
			
		}
	}
				
	@SuppressWarnings("unchecked")
	public void code(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-z0-9]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
			
			List<String> lt;
			if(tipoUbicacionModificarControlador != null){
				/*Validating that the location type has not been previously created*/
				lt = entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.codigo =:codigo and t.id <>:id")
								  .setParameter("codigo", value.toString().trim())	
								  .setParameter("id", tipoUbicacionModificarControlador.getTipo().getId())
								  .getResultList();
				
			}
			else{
				/*Validating that the location type has not been previously created*/
				lt = entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.codigo =:codigo")
					     		  .setParameter("codigo", value.toString().trim())										   
								  .getResultList();
			}
			if (lt.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("valorExistente"));	
			}									
		}
	}
}
