package gehos.configuracion.management.gestionarConfiguracionesPersonales;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("configuracionPersonalValidarControlador")
public class ConfiguracionPersonalValidarControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;	
	@In(required = false, value = "configuracionPersonalControlador", scope = ScopeType.CONVERSATION)
	ConfiguracionPersonalControlador configuracionPersonalControlador;
	
	FacesMessage facesMessage;
	private String message;		
	
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}
	
	// validate if the new password and the confirm password are equeals
	public void confirmPassword(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {


						
			//save value in "confirmPassword" if the value is diferent from null or "".
			String confirmPassword = context.getExternalContext().getRequestParameterMap().get("form:newPassword") != null ? context.getExternalContext().getRequestParameterMap().get("form:newPassword").toString() : "";
			if(!value.toString().equals(confirmPassword)){
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("passwordUnequal"));
			}					
		}
	}	
	
	// validate if the old password is correct
	public void oldPassword(FacesContext context, UIComponent component, Object value) throws NoSuchAlgorithmException {
		if (context.getExternalContext().getRequestParameterMap().containsKey("form:buttonAceptar")) {
			

						
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(value.toString().getBytes());
			String md5pass = new String(Hex.encodeHex(digest.digest()));
						
			if(!configuracionPersonalControlador.getUser().getPassword().equals(md5pass)){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("incorrectPassword"));
			}			
		}
	}
}
