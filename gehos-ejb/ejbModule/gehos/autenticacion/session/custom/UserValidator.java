package gehos.autenticacion.session.custom;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("userValidator")
public class UserValidator implements Validator {
	
	@In
	FacesMessages facesMessages;
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle()
			.getString("caracteresEspeciales");

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!value.toString().matches(
				"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+".@" + "0-9]+\\s*)+$")) {
			((UIInput) component).setValid(false);

			FacesMessage message = new FacesMessage(
					SeamResourceBundle.getBundle()
					.getString("caracteresIncorrectos"));
			context.addMessage(component.getClientId(context), message);			
		}

	}

}
