package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gehos.autenticacion.entity.Profile;
import gehos.ensayo.entity.Sujeto_ensayo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("validations_Cronograma_Especifico")
public class Validations_Cronograma_Especifico {

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

	// mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message", Severity.ERROR,
				mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}

	public void validarTelefono(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"idForm:aceptar")) {
			Pattern patron = Pattern.compile("^[0-9]{1,25}$");
			Matcher match = patron.matcher(value.toString());
			if (!match.find())
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("numTelfInc"));
		}
	}

	// Validacion de numeros
	public void number(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey("idForm:aceptarr")) {
			// valida que no sea negativo
			if (value.toString().matches("-^?\\d+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("nonegativo"));
			}

			if (!value.toString().matches("^(?:\\+)?\\d+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			/*
			 * try {// valida que sea entero
			 * 
			 * @SuppressWarnings("unused") Integer val = new
			 * Integer(value.toString()); } catch (Exception e) {
			 * validatorManagerExeption
			 * (SeamResourceBundle.getBundle().getString( "entero")); }
			 */
		}
	}

	// Validacion de letras (Entidades del sistema)
	public void text(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey("idForm:aceptarr")) {

			if (!value.toString().matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("soloLetras"));
			}

			if (value.toString().length() > 25 || value.toString() == " ") {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres"));
			}
		}
	}

	public boolean textnumber(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value
					.toString()
					.matches(
							"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25 || value.toString() == " ") {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"));
				return true;
			}
		}
		return false;

	}
	
	
	public boolean textnumberCausa(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value
					.toString()
					.matches(
							"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

		}
		return false;

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

		if (!value
				.toString()
				.matches(
						"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
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

		if (!value
				.toString()
				.matches(
						"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
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
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value
					.toString()
					.matches(
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
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {
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
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

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
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

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
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"valorRequerido"));
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

	public boolean longitudM(Object value, String componente,
			FacesMessages facesMessages) {
		if (value.toString().length() > 25) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"maximoCaracteres"));
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
	
	
	
	public void validarFechaNacimiento(FacesContext context,
			UIComponent component, Object value)
	{
		
		Calendar cal=Calendar.getInstance();
		Date hoy =cal.getTime();
		Date fechanacimiento=new Date(value.toString());
		/*String[] fecha=value.toString().split("/");
		Integer dia = new Integer(fecha[0]);
		Integer mes = new Integer(fecha[1]);
		Integer anno = new Integer(fecha[2]);
		
		cal.set(anno,mes,dia);
		Date fechaentrada=cal.getTime();*/
			if(hoy.before(fechanacimiento)){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString("validator.pattern.fechanacimiento"));
			
		}
		
	}
	

	

	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
