package gehos.configuracion.management.gestionarServiciosClinicos;

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

@Name("servicioClinicoValidarControlador")
public class ServicioClinicoValidarControlador {
	
	@In EntityManager entityManager;
	@In(required = false, value = "servicioClinicoModificarControlador", scope = ScopeType.CONVERSATION)
	ServicioClinicoModificarControlador servicioClinicoModificarControlador;
	
	@In(required = false, value = "servicioClinicoCrearControlador", scope = ScopeType.CONVERSATION)
	ServicioClinicoCrearControlador servicioClinicoCrearControlador;	
	
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
	public void code(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey("form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-z0-9/_]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}			
					
			List<String> lsc; 
			
			if(servicioClinicoModificarControlador != null){
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.codigo =:codigo and s.id <>:servicioId " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = true")
								   .setParameter("codigo", value.toString().trim())
								   .setParameter("servicioId", this.servicioClinicoModificarControlador.getServicio().getId())
				.getResultList();				
			}
			else{
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.codigo =:codigo " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = true")
								   .setParameter("codigo", value.toString().trim()).getResultList();			
			}
			if(lsc.size() != 0){
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("valorExistente"));				
			}			
		}
	}
		
	@SuppressWarnings("unchecked")
	public void name(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0-9]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 30) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}	
			
			List<String> lsc; 
									
			if(servicioClinicoModificarControlador != null){
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.nombre =:nombre and s.id <>:servicioId " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = true")
								   .setParameter("nombre", value.toString().trim())
								   .setParameter("servicioId", this.servicioClinicoModificarControlador.getServicio().getId())
				.getResultList();
			}
			else{
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.nombre =:nombre " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = true ")
								   .setParameter("nombre", value.toString().trim())								 
								   .getResultList();			
			}
			if(lsc.size() != 0){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));				
			}		
		}
	}
}
