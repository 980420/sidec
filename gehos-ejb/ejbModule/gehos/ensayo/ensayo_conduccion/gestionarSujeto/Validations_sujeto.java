package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("validations_sujeto")
public class Validations_sujeto {

	@In
	LocaleSelector localeSelector;
	@In
	FacesMessages facesMessages;

	FacesMessage facesMessage;
	private String message = "";
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle()
			.getString("caracteresEspeciales");

	// mensajes en los campos (azterizcos rojos)
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}
	
	public void validatorManagerExeption(String key, UIComponent component){
		this.facesMessages.addToControlFromResourceBundle(component.getId(), key);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(this.facesMessage);
	}

	// mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message", Severity.ERROR,
				mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}

	public void validarTelefono(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptar")) {
			Pattern patron = Pattern.compile("^[0-9]{1,25}$");
			Matcher match = patron.matcher(value.toString());
			if (!match.find())
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("numTelfInc"));
		}
	}

	// Validacion de numeros double
	public void validarDouble(FacesContext context, UIComponent component,Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptarr")) {
			
			try {
				Double.parseDouble(value.toString());
			} catch (NumberFormatException nfe) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"caracteresIncorrectos"));
			}
		}
	}
	
	// Validacion de numeros double sin mas ni menos
		public void validarDoubleNoMasMenos(FacesContext context, UIComponent component,Object value) {
			if (context.getExternalContext().getRequestParameterMap()
					.containsKey("idForm:aceptarr")) {
				
				if (!value.toString().matches("\\d+\\b(.\\d+){0,1}\\b")) {
					validatorManagerExeption(SeamResourceBundle.getBundle()
							.getString("caracteresIncorrectos"));
				}
				
				try {
					Double.parseDouble(value.toString());
				} catch (NumberFormatException nfe) {
					validatorManagerExeption(SeamResourceBundle.getBundle().getString(
							"caracteresIncorrectos"));
				}
			}
		}
		
		// Validacion de numeros
		public void number(FacesContext context, UIComponent component, Object value){
			if (context.getExternalContext().getRequestParameterMap().containsKey("idForm:aceptarr")){
				// valida que no sea negativo
				if (value.toString().matches("-^?\\d+$"))
					validatorManagerExeption(SeamResourceBundle.getBundle().getString("nonegativo"));			
				else if (!value.toString().matches("^(?:\\+)?\\d+$"))
					validatorManagerExeption(SeamResourceBundle.getBundle().getString("caracteresIncorrectos"));
				else if ((value.toString().length() > 11 && value.toString().matches("^(?:\\+)?\\d+$")))
					validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCifras"));
				

				
			}
		}	


	//Validación numero 20 cifras
	public void number20Cifras(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptarr")) {
			// valida que no sea negativo
			if (value.toString().matches("-^?\\d+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("nonegativo"));
			}

			if (!value.toString().matches("^(?:\\+)?\\d+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if ((value.toString().length() > 11 && value.toString().matches(
					"^(?:\\+)?\\d+$"))) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCifrasEntero"));
			}
		}
	}
	// Validacion de numeros
	public void numberEdad(FacesContext context, UIComponent component,
			Object value) {

		// valida que no sea negativo
		if (value.toString().matches("-^?\\d+$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"nonegativo"));
		}

		if (!value.toString().matches("^(?:\\+)?\\d+$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if ((value.toString().length() > 3 && value.toString().matches(
				"^(?:\\+)?\\d+$"))) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCifrasEdad"));
		}

		/*
		 * try {// valida que sea entero
		 * 
		 * @SuppressWarnings("unused") Integer val = new
		 * Integer(value.toString()); } catch (Exception e) {
		 * validatorManagerExeption (SeamResourceBundle.getBundle().getString(
		 * "entero")); }
		 */

	}
	
	// Validacion de letras (Entidades del sistema)
		public void text(FacesContext context, UIComponent component, Object value) {
			if (context.getExternalContext().getRequestParameterMap()
					.containsKey("idForm:aceptarr")) {

				if (!value.toString().matches(
						"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"]+\\s*)++$")) {
					validatorManagerExeption(SeamResourceBundle.getBundle()
							.getString("soloLetras"));
				}

				if (value.toString().length() > 25) {
					validatorManagerExeption(SeamResourceBundle.getBundle()
							.getString("maximoCaracteres"));
				}
			}
		}
	
	public void text2(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptarr")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?. :;,\\\\\\-0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caractererIncorrectosString"));
			}
			

			if (value.toString().length() > 50) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres50"));
			}

			
		}
	}


	public void textnumber250(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptarr") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addMedicacionP") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addMedicacion") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addEnfermedad") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addMedicacionF")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.:,0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"caracteresInvalidos"));
			}

			if (value.toString().length() > 250) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"maximoCaracteres250"));
			}
		}	
	}
	
	public void textnumber1000(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptarr") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addMedicacionP") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addMedicacion") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addEnfermedad") || context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:addMedicacionF")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.:,0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"caracteresInvalidos"));
			}

			if (value.toString().length() > 1000) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"maximoCaracteres1000"));
			}
		}
	}
	
	public void todosCaracter(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}

	}

	// Validacion de espacio blanco
	public void causaEnBlanco(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptarr")) {
			int longitud = value.toString().length();
			if (!(longitud <= 250)) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres250"));
			}

			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresInvalidos"));
			}

		}
	}

	public void textnumber(Object value, String componente,
			FacesMessages facesMessages) {
		if (value.toString().equals("")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres"));
		}

	}

	public boolean textnumberlowercase(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches("^(\\s*[a-z0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"));
				return true;
			}
		}
		return false;

	}

	public void textnumber50(FacesContext context, UIComponent component,
			Object value) {
		// if
		// (context.getExternalContext().getRequestParameterMap().containsKey(
		// "form:buttonAceptar")) {

		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 50) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres50"));
		}
		// }
	}

	// Validacion de letras y numeros particulares (Entidades del sistema)
	public void textnumber(FacesContext context, UIComponent component,
			Object value) {
		// if
		// (context.getExternalContext().getRequestParameterMap().containsKey(
		// "form:buttonAceptar")) {

		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 25) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres"));
		}
		// }
	}

	// Validacion de letras y numeros particulares (Entidades del sistema)
	public void textnumber30(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	// Validacion de numeros de telefonos (Entidades del sistema)
	public void phone(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {
			if (!value.toString().matches("^(\\s*[0123456789]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	// Validacion de direcciones particulares (Entidades del sistema)
	public void addres(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-z0-9#/.]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public void email(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}

			if (!value
					.toString()
					.matches(
							"(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("emailAddres"));
			}
		}
	}

	// Variante
	// 2-----------------------------------------------------------------------------------
	public boolean requeridoM(Object value, String componente,
			FacesMessages facesMessages) {
		if (value == null) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("valorRequerido"));
			return true;
		}

		if (value.toString().isEmpty()) {// valida que no sea negativo
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("valorRequerido"));
			return true;
		}
		return false;
	}

	public boolean longitudM(Object value, String componente,
			FacesMessages facesMessages) {
		if (value.toString().length() > 25) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("maximoCaracteres"));
			return true;
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
								.getString("datosIncorrectos"));
				return true;
			}

			if (!value.toString().matches("^(?:\\+)?\\d+$")) {// valida que no
				// tenga
				// caracteres extranos
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			try {// valida que sea entero
				@SuppressWarnings("unused")
				Integer val = new Integer(value.toString());
			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("datosIncorrectos"));
				return true;
			}
		}

		return false;
	}

	// Validacion de numeros de telefonos (Entidades del sistema)
	public boolean phoneM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches("^(\\s*[0123456789]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"));
				return true;
			}
		}
		return false;

	}

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public boolean emailM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {

			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"));
			}

			if (!value
					.toString()
					.matches(
							"(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("emailAddres"));
				return true;
			}
		}
		return false;

	}

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public boolean passwordsM(Object value, Object value2, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("") && !value2.toString().equals("")) {
			if (!value.toString().equals(value2)) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("contraenasRepetidas"));
				return true;
			}
		}
		return false;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
