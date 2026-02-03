package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gehos.autenticacion.entity.Profile;

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

@Name("validaciones_CRD")
public class Validaciones_CRD {

	@In
	LocaleSelector localeSelector;
	@In
	FacesMessages facesMessages;

	FacesMessage facesMessage;
	private String message = "";
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

	// Validacion de numeros
	public void number4cifras(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 4)
			// que no exceda las 4 cifras
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"mas4cifras"));
		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"datosIncorrectos"));

		if (!value.toString().matches("^(\\s*[.0-9]+\\s*)++$"))// valida que no //
			// tenga caracteres
			// // extranos
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));

		try {// valida que sea entero
			@SuppressWarnings("unused")
			Float val = new Float(value.toString());
		} catch (Exception e) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"datosIncorrectos"));
		}

	}

	// Validacion de letras (Entidades del sistema)
	public void text(FacesContext context, UIComponent component, Object value) {

		if (value.toString().length() > 25 || value.toString() == " ") {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres"));
		}
		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "\u00BF?.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}
	}

	public void textnumber50(FacesContext context, UIComponent component,
			Object value) {
		if (value.toString().length() > 50) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres50"));
		}

		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "\u00BF?.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

	}
	
	public void caracteresEsp(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 100)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres100"));
	}
	public void caracteresEsp500(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 500)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres500"));
		
	}
	public void caracteresEsp4000(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 4000)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres4000"));
		
	}

	public void textnumber100(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 100)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres100"));
		else if(!Pattern.compile("^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "0-9]+\\s*)++$").matcher(value.toString()).matches())
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("caracteresIncorrectos"));
	}
	
	public void textnumber500(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 500)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres500"));
		else if(!Pattern.compile("^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "0-9]+\\s*)++$").matcher(value.toString()).matches())
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("caracteresIncorrectos"));
	}
	
	public boolean tn100(Object value, String componente,
			FacesMessages facesMessages) {

		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[0-9A-Za-z" + CARACTERES_ESPECIALES + "."
							+ "]+\\s*)++$")) {
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
	
	public void textnumber250R(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("modificarHojaCrd:aceptarBtn")) {

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}
		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "\u00BF?.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}
		}
	}
	

	public void textnumber250(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}
		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "\u00BF?.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}
	}
}
