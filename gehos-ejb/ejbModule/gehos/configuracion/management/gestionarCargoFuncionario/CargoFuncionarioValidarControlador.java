package gehos.configuracion.management.gestionarCargoFuncionario;

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

@Name("cargoFuncionarioValidarControlador")
public class CargoFuncionarioValidarControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In(required = false, value = "cargoFuncionarioModificarControlador", scope = ScopeType.CONVERSATION)
	CargoFuncionarioModificarControlador cargoFuncionarioModificarControlador;	
	
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

			validations.textnumber(context, component, value);
					
			List<String> lcf; 
		
			if(cargoFuncionarioModificarControlador != null){
				/*Validating that the official functionary rate has not been previously created*/
				lcf = entityManager.createQuery("select c.valor from CargoFuncionario_configuracion c where c.valor =:valor and c.id <>:id")
								  .setParameter("valor", value.toString().trim())	
								  .setParameter("id", cargoFuncionarioModificarControlador.getCargoFuncionario().getId())
								  .getResultList();
			}
			else{
				/*Validating that the location type has not been previously created*/
				lcf = entityManager.createQuery("select c.valor from CargoFuncionario_configuracion c where c.valor =:valor")
					     		  .setParameter("valor", value.toString().trim())										   
								  .getResultList();
			}
			if (lcf.size() != 0) {
				validatorManagerGlobalExeption("entidadExistente");
			}				
		}
	}
}