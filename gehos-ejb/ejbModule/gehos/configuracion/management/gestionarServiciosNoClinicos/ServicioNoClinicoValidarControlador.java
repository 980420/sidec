package gehos.configuracion.management.gestionarServiciosNoClinicos;

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

@Name("servicioNoClinicoValidarControlador")
public class ServicioNoClinicoValidarControlador {
	
	@In EntityManager entityManager;
	@In(required = false, value = "servicioNoClinicoModificarControlador", scope = ScopeType.CONVERSATION)
	ServicioNoClinicoModificarControlador servicioNoClinicoModificarControlador;
	
	@In(required = false, value = "servicioNoClinicoCrearControlador", scope = ScopeType.CONVERSATION)
	ServicioNoClinicoCrearControlador servicioNoClinicoCrearControlador;	
	
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
			
			if(servicioNoClinicoModificarControlador != null){
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.codigo =:codigo and s.id <>:servicioId " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = false")
								   .setParameter("codigo", value.toString().trim())
								   .setParameter("servicioId", this.servicioNoClinicoModificarControlador.getServicio().getId())
				.getResultList();				
			}
			else{
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.codigo =:codigo " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = false")
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

			if (!value.toString().matches("^(\\s*[A-Za-z0-9áéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 30) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}	
			
			List<String> lsc; 
									
			if(servicioNoClinicoModificarControlador != null){
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.nombre =:nombre and s.id <>:servicioId " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = false")
								   .setParameter("nombre", value.toString().trim())
								   .setParameter("servicioId", this.servicioNoClinicoModificarControlador.getServicio().getId())
				.getResultList();
			}
			else{
				/*Validating that the service code has not been previously used*/
				lsc = entityManager.createQuery("select s from Servicio_configuracion s " +
												"where s.nombre =:nombre " +
												"and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
												"and s.departamento.esClinico = false ")
								   .setParameter("nombre", value.toString().trim())								 
								   .getResultList();			
			}
			if(lsc.size() != 0){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("valorExistente"));				
			}		
		}
	}
}
