package gehos.configuracion.management.gestionarTipoEstablecimiento;

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

@Name("tipoEstablecimientoValidarControlador")
public class TipoEstablecimientoValidarControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In(required = false, value = "tipoEstablecimientoModificarControlador", scope = ScopeType.CONVERSATION)
	TipoEstablecimientoModificarControlador tipoEstablecimientoModificarControlador;	
	Validations_configuracion validations = new Validations_configuracion();
	
	FacesMessage facesMessage;
	
	//mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message",Severity.ERROR, mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}
	
	//Letters, less than 25 characters
	@SuppressWarnings("unchecked")
	public void valor(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			validations.text(context, component, value);			
					
			List<String> lr; 
			
			if(tipoEstablecimientoModificarControlador != null){
				/*Validating that the rol has not been previously created*/
				lr = entityManager.createQuery("select t from TipoEstablecimientoSalud_configuracion t where t.valor =:valor and t.id <>:id")
								  .setParameter("valor", value.toString().trim())		
								  .setParameter("id", tipoEstablecimientoModificarControlador.getTipoEstablecimiento().getId())	
								  .getResultList();				
			}
			else{
				/*Validating that the description of location type has not been previously created*/
				lr = entityManager.createQuery("select t from TipoEstablecimientoSalud_configuracion t where t.valor =:valor")
								  .setParameter("valor", value.toString().trim())										  
								  .getResultList();				
			}
			if(lr.size() != 0){				
				validatorManagerGlobalExeption("entidadExistente");
			}			
		}
	}	
}
