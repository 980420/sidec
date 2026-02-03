package gehos.configuracion.management.utilidades;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gehos.autenticacion.entity.Profile;
import gehos.autenticacion.session.custom.PasswordStrength;

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

/**
 * @author yurien 20/05/2014 Se modifican todos los metodos agregandoles los
 *         caracteres especiales del .properties
 * **/
@Name("validations_configuracion")
public class Validations_configuracion {

	@In
	LocaleSelector localeSelector;
	@In
	FacesMessages facesMessages;

	FacesMessage facesMessage;
	private String message = "";

	/**
	 * @author yurien 20/05/2014 Contiene los caracteres especiales
	 * **/
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle
			.getBundle().getString("caracteresEspeciales");

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
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("idForm:aceptar")) {
			Pattern patron = Pattern.compile("^[0-9]{1,25}$");
			Matcher match = patron.matcher(value.toString());
			if (!match.find())
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("numTelfInc"));
		}
	}

	// Validacion de numeros
	public void number(FacesContext context, UIComponent component, Object value) {
		// if
		// (context.getExternalContext().getRequestParameterMap().containsKey(
		// "form:buttonAceptar")) {

		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"datosIncorrectos"));

		if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
			// tenga caracteres
			// // extranos
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));

		try {// valida que sea entero
			@SuppressWarnings("unused")
			Integer val = new Integer(value.toString());
		} catch (Exception e) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"datosIncorrectos"));
		}
		// }
	}

	// Validacion de letras (Entidades del sistema)
	public void text(FacesContext context, UIComponent component, Object value) {
		// if
		// (context.getExternalContext().getRequestParameterMap().containsKey(
		// "form:buttonAceptar")) {

		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "." + "]+\\s*)+$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 25) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres"));
		}
		// }
	}

	public boolean textnumber(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "0-9]+\\s*)+$")) {
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

	public boolean textnumberlowercase(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches("^(\\s*[a-z0-9]+\\s*)+$")) {
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

		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "." + "0-9]+\\s*)+$")) {
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

		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "." + "0-9]+\\s*)+$")) {
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
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "0-9]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	// Validacion de letras (Entidades del sistema)
	public void textExt30(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "-]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 30) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	// Valida Nombre del Modulo
	public void textExtModulo(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("ubicacionesForm:acpbtn")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "-]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 30) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	// Valida Nombre y cod Rol
	public void textExtNombre(FacesContext context, UIComponent component,
			Object value) {
		if (!value.toString().matches("^(\\s*[A-Za-z]+\\s*)+$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 25) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres"));
		}
	}

	// Valida Descripcion del modulo
	public void textnumber250(FacesContext context, UIComponent component,
			Object value) {

		if (!value.toString().matches(
				"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ¿?.,0-9]+\\s*)+$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}

	}

	public void textnumber500(FacesContext context, UIComponent component,
			Object value) {

		if (!value.toString().matches(
				"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ¿?.,0-9]+\\s*)+$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 1000) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres500"));
		}

	}

	// Validacion de numeros de telefonos (Entidades del sistema)
	public void phone(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {
			if (!value.toString().matches("^(\\s*[0123456789]+\\s*)+$")) {
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
	// Rafa
	public void addres(FacesContext context, UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-z0-9#/.]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}
		}
	}

	

	public boolean requeridoList(Object value, String componente,
			FacesMessages facesMessages) {
		if (((Collection) value).isEmpty()) {
			facesMessages.addToControlFromResourceBundle(componente,
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("valorRequerido"));
			return true;
		}
		return false;
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
								.getString("msg_negativo"));
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
			if (value.toString().length() > 10) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_10cigras"));
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

	// Validacion de numeros (Entidades del sistema)
	public void numberME(FacesContext context, UIComponent component,
			Object value) {
		if (!value.toString().equals("")) {
			if (value.toString().matches("-^?\\d+$")) {// valida que no sea
				// negativo
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("msg_negativo"));
			}

			if (!value.toString().matches("^(?:\\+)?\\d+$")) {// valida que no
				// tenga
				// caracteres extranos
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}
			if (value.toString().length() > 10) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("msg_10cigras"));

			}

			try {// valida que sea entero
				@SuppressWarnings("unused")
				Integer val = new Integer(value.toString());
			} catch (Exception e) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("datosIncorrectos"));
			}
		}
	}

	// Validacion Orden de las Funcionalidades
	public boolean numberOrden(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (value.toString().matches("-^?\\d+$")) {// valida que no sea
				// negativo
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_negativo"));
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
			if (value.toString().length() > 2) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("mas2cifras"));
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

	// Validacion de letras (Entidades del sistema)
	public boolean textM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ ".]+\\s*)+$")) {
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

	public boolean textM75(Object value, String componente,
			FacesMessages facesMessages) {

		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[0-9A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 75) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres75"));
				return true;
			}
		}
		return false;

	}

	public boolean textM250(Object value, String componente,
			FacesMessages facesMessages) {

		if (!value.toString().equals("")) {

			if (value.toString().length() > 75) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres75"));
				return true;
			}
		}
		return false;

	}

	public void textM250E(FacesContext context, UIComponent component,
			Object value) {

		if (!value.toString().equals("")) {

			if (value.toString().length() > 75) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres75"));

			}
		}

	}

	// Validacion de numeros de telefonos (Entidades del sistema)
	public boolean phoneM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches("^(\\s*[0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (((value.toString().length() > 25))
					|| (value.toString().length() < 8)) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_rangTamaTelf_conf"));
				return true;
			}
		}
		return false;

	}

	// Validacion de numeros de telefonos (Entidades del sistema)
	public void phoneME(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches("^(\\s*[0123456789]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (((value.toString().length() > 25))
					|| (value.toString().length() < 8)) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("msg_rangTamaTelf_conf"));
			}
		}

	}

	// Validacion de direcciones particulares (Entidades del sistema)
	public boolean addresM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "0-9#/.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 100) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres100"));
				return true;
			}
		}
		return false;
	}

	// Validacion de direcciones particulares (Entidades del sistema)
	public void addresME(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "0-9#/.]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 100) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres100"));
			}
		}
	}

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public boolean emailM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {

			/**
			 * @author yurien 19/05/2014 Esta restriccion se quita porque pueden
			 *         existir direcciones de correo con mas de 25 caracteres
			 * **/
			if (value.toString().length() > 50) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres50"));
			}

			if (!value
					.toString()
					.matches(
							"(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("correoForm"));
				return true;
			}
		}
		return false;

	}

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public void emailME(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {

			/**
			 * @author yurien 19/05/2014 Esta restriccion se quita porque pueden
			 *         existir direcciones de correo con mas de 25 caracteres
			 * **/
			if (value.toString().length() > 50) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres50"));
			}

			if (!value
					.toString()
					.matches(
							"(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("correoForm"));
			}
		}

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

	// validacion: cedula (numeros, letras)
	public boolean cedulaM(Object value, Profile profile, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches("^(\\s*[0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("carneIncorrecto"));
				return true;
			}
			if (value.toString().length() != 11) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("carneIncorrecto"));
				return true;
			}
		}
		return false;
	}

	// validacion: pasaporte (letras, numero)
	public boolean pasaporteM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"));
				return true;
			}
			if (!value.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}
		}
		return false;
	}

	// validacion: archivo jpg y png
	public boolean imagen(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().endsWith(".png")
					&& !value.toString().endsWith(".jpg")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("errorfoto"));
				return true;
			}
		}
		return false;
	}

	public boolean passwordStrength(String password,
			PasswordStrength passwordStrength, String componente,
			FacesMessages facesMessages) {
		if (password.length() < passwordStrength.getPasswordLength()) {
			facesMessages.addToControlFromResourceBundle(
					componente,
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"passwordMaxLengthError"));
			return true;
		}
		Pattern pattern = Pattern.compile(SeamResourceBundle.getBundle()
				.getString(passwordStrength.getPasswordStrengthRegex()));
		Matcher matcher = pattern.matcher(password);
		if (!matcher.lookingAt()) {
			if (passwordStrength.getPasswordStrengthType() == "Strong")
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("passwordStrongError"));
			if (passwordStrength.getPasswordStrengthType() == "Medium")
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("passwordMediumError"));
			if (passwordStrength.getPasswordStrengthType() == "Weak")
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("passwordWeakError"));
			if (passwordStrength.getPasswordStrengthType() == "Weaker")
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("passwordWeakerError"));
			return true;
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
