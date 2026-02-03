package gehos.ensayo.ensayo_disenno.gestionarEstudio;

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

@Name("validations_ensayo")
public class Validations_ensayo {

	@In
	LocaleSelector localeSelector;
	@In
	FacesMessages facesMessages;

	FacesMessage facesMessage;
	private String message = "";
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle
			.getBundle().getString("caracteresEspeciales");
	private static final String CARACTERES_ESP = SeamResourceBundle
			.getBundle().getString("caracteresESP");
	

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
	
	public void caracteresEsp(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 100)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres100"));
	}

	public void caracteresEsp500(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 500)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres500"));
	}
	public void caracteresEsp255(FacesContext context, UIComponent component, Object value) {
		if (value.toString().length() > 255)
			validatorManagerExeption(SeamResourceBundle.getBundle().getString("maximoCaracteres255"));
	}
	// Validacion de numeros
	public void number(FacesContext context, UIComponent component, Object value) {
		
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
		
	}
	
	// Validacion edades
	
	public void edades(FacesContext context, UIComponent component, Object value) {
		
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
			if(val.compareTo(new Integer(120)) > 0){
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"msg_a_ensCLin"));
			}
		} catch (Exception e) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"datosIncorrectos"));
		}
		
	}
	
		// Validacion de letras (Entidades del sistema)
	public void text(FacesContext context, UIComponent component, Object value) {
		// if
		// (context.getExternalContext().getRequestParameterMap().containsKey(
		// "form:buttonAceptar")) {

		if (!value.toString().matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 25 || value.toString()== " ") {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"msg_maxEs25_conf"));
		}
		// }
	}

	public boolean textnumber(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25 || value.toString()== " ") {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_maxEs25_conf"));
				return true;
			}
		}
		return false;

	}

	public boolean textnumberlowercase(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[a-z0-9]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_maxEs25_conf"));
				return true;
			}
		}
		return false;

	}
	/**
	 * Valida que el identificador del Estidio no tenga mas de 50 caracteres y que acepte -
	 * @param context
	 * @param component
	 * @param value
	 */
	public void ValIdentEst(FacesContext context, UIComponent component,
			Object value) {
		
		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\-_/s*){1,}-?(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\-_/s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 50) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres50"));
		}
		
	}
	
	/**
     * Valida que el identificador del Estidio no tenga mas de 50 caracteres y que acepte -
     * @param context
     * @param component
     * @param value
     */
    public void ValIdentEstIdent(FacesContext context, UIComponent component,
                    Object value) {
           /* if(value.toString().length() == 1){
                    if(!value.toString()
                                    .matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)"))
                            validatorManagerExeption(SeamResourceBundle.getBundle().getString(
                                            "caracteresIncorrectos"));
                    else return;
            }
            
            if (!value.toString()
                            .matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*){1,}-?(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)+$")) {
                    validatorManagerExeption(SeamResourceBundle.getBundle().getString(
                                    "caracteresIncorrectos"));
            }*/

            if (value.toString().length() > 50) {
                    validatorManagerExeption(SeamResourceBundle.getBundle().getString(
                                    "maximoCaracteres50"));
            }
            
    }

	

	public void textnumber50(FacesContext context, UIComponent component,
			Object value) {
		
		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 50) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres50"));
		}
		
	}
	public void textnumber50Nuevo(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 50) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres50"));
		}
		
	}
	
	public void textnumber500(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 500) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres500"));
		}
		
	}
	/**
	 * Valida que tenga un maximo de 100 caracteres no importa si son letras numeros o punto.
	 * @Tania 
	 *
	 */
	public void textnumberPUNTO100(FacesContext context, UIComponent component,
			Object value) {
		
		if (!value.toString()
				.matches("^(\\.*\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 100) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres100"));
		}
		
	}
	
	/**
	 * Valida que tenga un maximo de 100 caracteres no importa si son letras numeros o guion.
	 * @Tania 
	 *
	 */
	public void textnumberGUION100(FacesContext context, UIComponent component,
			Object value) {
		
		if (!value.toString()
				.matches("^(\\-*\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 100) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres100"));
		}
		
	}
	
	public void textnumber100(FacesContext context, UIComponent component,
			Object value) {
		
		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 100) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres100"));
		}
		
	}
	// Validacion de numeros
	public void number4cifras(FacesContext context, UIComponent component, Object value) {
		
		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		
		try {// valida que sea entero
			@SuppressWarnings("unused")
			Integer val = new Integer(value.toString());
		} catch (Exception e) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}
		
		if(value.toString().length()>4)
			//que no exceda las 4 cifras
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"mas4cifras"));
	}

	// Validacion de numeros 3
		public void number3cifras(FacesContext context, UIComponent component, Object value) {

			if (value.toString().matches("-^?\\d+$"))// valida que no sea //
				// negativo
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"datosIncorrectos"));

			if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
				// tenga caracteres
				// // extranos
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"caracteresIncorrectos"));
			if(value.toString().length()>3)
				//que no exceda las 3 cifras
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"mas3cifras"));
			try {// valida que sea entero
				@SuppressWarnings("unused")
				Integer val = new Integer(value.toString());
			} catch (Exception e) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"datosIncorrectos"));
			}

		}
		
		// Validacion de telefono
				public void numeroTelefono(FacesContext context, UIComponent component, Object value) {

					if (value.toString().matches("-^?\\d+$"))// valida que no sea //
						// negativo
						validatorManagerExeption(SeamResourceBundle.getBundle().getString(
								"datosIncorrectos"));

					if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
						// tenga caracteres
						// // extranos
						validatorManagerExeption(SeamResourceBundle.getBundle().getString(
								"caracteresIncorrectos"));
					
					//que no exceda las 20 cifras
					if((value.toString().length()>25 || value.toString().length()<8)&& value.toString().length()>0)
					{
						validatorManagerExeption(SeamResourceBundle.getBundle().getString(
								"telefonoRestricc"));
					}
						
						
					

				}
				
		
		
	public void textnumber250(FacesContext context, UIComponent component,
			Object value) {
		
		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}
		
	}
	
	public void allstrings250(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}
		
	}
	//Nuevo
	public void textnumber250Nuevo(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}
		
	}
	
	public void textnumber1000(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 1000) {
			validatorManagerExeption("Máximo de 1000 caracteres superado");
		}
		
	}
	
	public boolean textM250(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 250) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres250"));
				return true;
			}
		}
		return false;

	}
	String texto;
	
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public boolean textnumber250B(FacesContext context, UIComponent component,
			Object value) {
		
		
		if (!texto
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
			return true;
		}

		if (texto.length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
			return true;
		}
		
		return false;
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
					"msg_maxEs25_conf"));
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

			if (value.toString().length() > 30) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("msg_maxEs30_conf"));
			}
		}
	}

	// Validacion de letras (Entidades del sistema)
	public void textExt30(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 30) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("msg_maxEs30_conf"));
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
						.getString("msg_maxEs25_conf"));
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
						.getString("msg_maxEs25_conf"));
			}
		}
	}

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public void email(FacesContext context, UIComponent component, Object value) {
			if (value.toString().length() > 50) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres50"));
			}

			if (!value.toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("emailAddres"));
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
							.getString("msg_maxEs25_conf"));
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
	
	

	// Validacion de letras (Entidades del sistema)
	public boolean textM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 25) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_maxEs25_conf"));
				return true;
			}
		}
		return false;

	}

	public boolean textM75(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
				return true;
			}

			if (value.toString().length() > 75) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_maxEs75_conf"));
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
								.getString("msg_maxEs25_conf"));
				return true;
			}
		}
		return false;

	}

	// Validacion de direcciones particulares (Entidades del sistema)
	public boolean addresM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9#/.]+\\s*)++$")) {
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

	// Validacion de direcciones de correo electronico (Entidades del sistema)
	public boolean emailM(Object value, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {

			if (value.toString().length() > 50) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_maxEs50_conf"));
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

	// validacion: cedula (numeros, letras)
	public boolean cedulaM(Object value, Profile profile, String componente,
			FacesMessages facesMessages) {
		if (!value.toString().equals("")) {
			if (profile.getLocaleString().equals("es_CU")) {
				if (!value.toString().matches(
						"^(\\s*[A-Za-z0123456789]+\\s*)++$")) {
					facesMessages.addToControlFromResourceBundle(componente,
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("carneIncorrecto"));
					return true;
				}
				// if (value.toString().length() != 11) {
				// facesMessages.addToControlFromResourceBundle(componente,
				// Severity.ERROR, SeamResourceBundle.getBundle()
				// .getString("carneIncorrecto"));
				// return true;
				// }
			}
			if (profile.getLocaleString().equals("es_VE")) {
				if (!value.toString().matches(
						"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
					facesMessages.addToControlFromResourceBundle(componente,
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("caracteresIncorrectos"));
					return true;
				}

				if (value.toString().length() > 25) {
					facesMessages.addToControlFromResourceBundle(componente,
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("msg_maxEs25_conf"));
					return true;
				}
			}
			if (profile.getLocaleString().equals("en")) {
				if (!value.toString().matches(
						"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
					facesMessages.addToControlFromResourceBundle(componente,
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("caracteresIncorrectos"));
					return true;
				}
				if (value.toString().length() > 25) {
					facesMessages.addToControlFromResourceBundle(componente,
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("msg_maxEs25_conf"));
					return true;
				}
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
								.getString("msg_maxEs25_conf"));
				return true;
			}
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("caracteresIncorrectos"));
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
