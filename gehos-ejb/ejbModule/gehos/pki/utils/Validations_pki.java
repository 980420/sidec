package gehos.pki.utils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("validations_pki")
public class Validations_pki {

	@In
	FacesMessages facesMessages;

	FacesMessage facesMessage;
	private String message = "";

	// mensajes en los campos (azterizcos rojos)
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	// mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message", Severity.ERROR,
				mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}

	// Validacion de numeros
	public void number(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (value.toString().matches("-^?\\d+$"))// valida que no sea //
				// negativo
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("datosIncorrectos"));

			if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
				// tenga caracteres
				// // extranos
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));

			try {// valida que sea entero
				@SuppressWarnings("unused")
				Integer val = new Integer(value.toString());
			} catch (Exception e) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("datosIncorrectos"));
			}
		}
	}

	// Validacion de letras
	public void text(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value.toString().equals("")) {
				if (!value.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
					validatorManagerExeption(SeamResourceBundle.getBundle()
									.getString("validator.pattern"));
				}

				
			}
		}
	}
	
	// Validacion de numeros y letras
	public void textandnumber(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value.toString().equals("")) {
				if (!value.toString().matches(
						"[a-zA-Z\\d]*")) {
					validatorManagerExeption(SeamResourceBundle.getBundle()
									.getString("validator.pattern"));
				}

				
			}
		}
	}
	
	/**
	 * Valida una cadena alfanumerica
	 */
	public void validarCadenaAlfanumerica(FacesContext context,
			UIComponent component, Object value) {	
			if(!value.toString().matches("^(\\s*[0-9A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("validator.pattern"));
			
		}
	}
	
	/**
     * Valida el combobox
     */
    public void comboRequerido(FacesContext context, UIComponent component, Object value) {
    	if(value.toString().equalsIgnoreCase(SeamResourceBundle.getBundle().getString("seleccione"))){
    		validatorManagerExeption(SeamResourceBundle.getBundle()
    				.getString("javax.faces.component.UIInput.REQUIRED"));
    	}
    }
    
    
 // Variante
	// 2-----------------------------------------------------------------------------------
    
    public boolean requeridoM(Object value, String componente,
			FacesMessages facesMessages) {
		if (value == null) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"javax.faces.component.UIInput.REQUIRED"));
			return true;
		}

		if (value.toString().isEmpty()) {// valida que no sea negativo
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"valorRequerido"));
			return true;
		}
		return false;
	}
    
    public boolean comborequeridoM(Object value, String componente,
			FacesMessages facesMessages) {
		if (value == null || value.toString().isEmpty() || value.toString().equals(
				SeamResourceBundle.getBundle().getString("seleccione"))) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"valorRequerido"));
			return true;
		}

		
		return false;
	}
    
    public boolean longitudM(Object value, String componente,
			FacesMessages facesMessages, int max) {
		if (value.toString().length() > max) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"maximoCaracteres"),max);
			return true;
		}
		return false;
	}
    
    
    
    public boolean descripcionesM(Object value, String componente,
			FacesMessages facesMessages) {
    	if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("validator.pattern"));
				return true;
			}

			if (value.toString().length() > 150) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"),150);
				return true;
			}
		}
		return false;
	}

	// Validacion de numeros (Entidades del sistema)
	public boolean numberM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (value.toString().matches("-^?\\d+$")) {// valida que no sea
				// negativo
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("validator.pattern"));
				return true;
			}

			if (!value.toString().matches("^(?:\\+)?\\d+$")) {// valida que no
				// tenga
				// caracteres extranos
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("validator.pattern"));
				return true;
			}

			try {// valida que sea entero
				@SuppressWarnings("unused")
				Integer val = new Integer(value.toString());
			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("validator.pattern"));
				return true;
			}
		}

		return false;
	}

	// Validacion de letras (Entidades del sistema)
	public boolean textM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"),25);
				return true;
			}
		}
		return false;

	}

}
