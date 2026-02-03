package gehos.ensayo.ensayo_disenno.session.reglas.helpers;


import gehos.ensayo.ensayo_disenno.session.reglas.components.mailer.MailSender;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNode;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.RuleNodeErrorManager;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import java.util.Date;
import java.util.Iterator;

import org.hibernate.validator.EmailValidator;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

@Name("valida")
@Scope(ScopeType.CONVERSATION)
public class Validaciones {

	protected FacesMessage facesMessage;
	protected String message;
	protected @In EntityManager entityManager;
	@In RuleNodeErrorManager ruleNodeErrorManager;
	
	//Para el correo (http://www.docjar.com/html/api/org/hibernate/validator/EmailValidator.java.html)
	private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
	private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
	private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

	
	public void validarAlfanumerico(FacesContext context, UIComponent component, Object value) 
	{        
        if(!value.toString().matches("^(\\s*[A-Za-z0123456789]+\\s*)++$"))
        {
               postMessage("Valor incorrecto");                      
        }
	}
	
	
	public void validarCadenaNumerica(FacesContext context, UIComponent component, Object value) 
	{        
		   if(!value.toString().matches("^(\\s*[ 0123456789]+\\s*)++$"))
	        {
	               postMessage("Valor incorrecto");                      
	        }
		
		
		
		
//		try {
//        		Long a=(Long.parseLong((String) value));
//				
//			} 
//        	catch (Exception e)
//        	{
//        		
//        		postMessage("Cadena no numérica o separada por espacios");
//				
//			}
			
		
		
		
       
	}
	
	public void validarDouble(FacesContext context, UIComponent component, Object value) 
	{        
      try 
      {
    	  Double.parseDouble(value.toString());  
      } catch (Exception e) 
      {
    	  postMessage("Valor incorrecto");      
      }
	}
	
	public void validateInteger(FacesContext context, UIComponent component, Object value) 
	{
	      
			if(!value.toString().matches("\\d+"))
				postMessage("El valor no es un n\u00FAmero positivo");
	}
	   
	public void validarCI(FacesContext context, UIComponent component, Object value) 
	{        
		if(!value.toString().matches("\\d{11}"))
			postMessage("CI debe contener 11 d\u00EDgitos");	 
      
	}
	
	public void validarCaracteres(FacesContext context,UIComponent component, Object value) {
			
			Pattern patronCar =Pattern.compile("^(\\s*[A-Za-z]+\\s*)++$");			
			Matcher match=patronCar.matcher(value.toString());
			if (!match.find())
				postMessage("Caracteres incorrectos");
			if (value.toString().length()>25)
				postMessage("Hasta 25 caracteres");
		
	}
	
		
	public void validarEmail(FacesContext context,UIComponent component, Object value) {
			
			Pattern pattern = Pattern.compile("^" + ATOM + "+(\\." + ATOM + "+)*@"
					      						  + DOMAIN + "|" + IP_DOMAIN + ")$",
					      				java.util.regex.Pattern.CASE_INSENSITIVE);
			Matcher match=pattern.matcher(value.toString());
			if(!match.find())	
				postMessage("Email inv\u00E1lido");			
			
	}

	public void validateNotNull(FacesContext context,UIComponent component, Object value) {
		
		if(value==null)
			postMessage(SeamResourceBundle.getBundle().getString("javax.faces.component.UIInput.REQUIRED"));			
		
	}
	
	public void validateWeight(FacesContext context,UIComponent component, Object value)
	{
		String er = "\\d{2,3}.\\d{1}";
		if(!Pattern.matches(er, value.toString()))
			postMessage("Peso no v\u00E1lido");
		if((Double.parseDouble(value.toString())<1 || Double.parseDouble(value.toString())>300))
		postMessage("El valor debe econtrarse en el rango de [1 a 300] Kg");
	}
	
	public void validateHeight(FacesContext context,UIComponent component, Object value)
	{
		String er = "\\d{1}.\\d{1,2}";
		if(!Pattern.matches(er, value.toString()))
			postMessage("Talla no v\u00E1lida");
		if((Double.parseDouble(value.toString())<1 || Double.parseDouble(value.toString())>3))
			postMessage("El valor debe econtrarse en el rango de [1 a 3] m");
	}
	
	public void validateDiameter(FacesContext context,UIComponent component, Object value)
	{
		String er = "\\d{1}.\\d+";
		if(!Pattern.matches(er, value.toString()))
			postMessage("El formato v\u00E1lido es a[.][d]*");
	}
	
	public void validateLength(FacesContext context,UIComponent component, Object value)
	{
		String er = "\\d{2}.\\d{0,2}";
		if(!Pattern.matches(er, value.toString()))
			postMessage("El formato v\u00E1lido es ab[.cd]");
	}	
	
	public void validateAge(FacesContext context,UIComponent component, Object value)
	{
		String er = "\\d{1,3}";
		if(!Pattern.matches(er, value.toString()))
			postMessage("Edad no v\u00E1lida");
		if((Integer.parseInt(value.toString())<1 || Integer.parseInt(value.toString())>150))
			postMessage("El valor debe econtrarse en el rango de [1 a 150] a\u00F1os");
	}
	
	public void validate0To300(FacesContext context,UIComponent component, Object value)
	{
		if(!value.toString().matches("\\d+"))		
			postMessage("Valor m\u00E1ximo 300");
		if((Integer.parseInt(value.toString())<0 || Integer.parseInt(value.toString())>300))
			postMessage("Valor m\u00E1ximo 300");
	}	
	
	public void postMessage(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}
	
	public boolean isNullOrEmpty(String val)
	{
		return val == null || val.equals("");
	}
	
	public void validateDateSintom(FacesContext context,UIComponent component, Object value)
	{
		DateTime dt = DateTime.now();
		Date d =	dt.toDate();

		
		if(d.before((Date)value))		
			postMessage("Fecha de inicio de s\u00EDntomas incorrecta");		
	}	
	
	public void validateEmails(FacesContext context,UIComponent component, Object value)
	{
		String MAIL_REGEX = "(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$";
		
		if(value==null)
			return;
		
		String input = value.toString().trim();
		String[] emails = input.split(";");
		
		for (int i = 0; i < emails.length; i++) {
			String e = emails[i].trim();
			if(!e.matches(MAIL_REGEX))
				postMessage(String.format("El correo %d no es v\u00E1lido",i+1));
		}		
	}

	public void validateDateSigec(FacesContext context,UIComponent component, Object value)
	{
		if(value==null || value.toString().trim().equals(""))
		{	
			putError(component,"Requerido");
			return;
		}		
		try
		{
			DateSigec.parse(value.toString());
		}
		catch(Exception e)
		{
			putError(component, e.getMessage());
			postMessage("Fecha incorrecta");//this message does not matter because it is required to avoid form submission on failed validation
			return;
		}			
		Integer nodeid = getNodeId(component);
		if(nodeid==null)
			return;
		ruleNodeErrorManager.removeError(nodeid);
	}
	
	public void validateIntegerConds(FacesContext context, UIComponent component, Object value) 
	{		
			if(!value.toString().matches("\\d+"))
			{	
				putError(component,"El valor no es un n\u00FAmero positivo");
				return;
			}
			
			Integer nodeid = getNodeId(component);
			if(nodeid==null)
				return;
			ruleNodeErrorManager.removeError(nodeid);
	}
	
	public void validateFileInputConds(FacesContext context, UIComponent component, Object value) 
	{		
		   	HtmlInputText h = (HtmlInputText)component;
		   	String styleClass = h.getStyleClass();
		   	if(styleClass.contains(RuleNode.FILE_CLASS_VALIDATION))
		   	{
		   		validateIntegerConds(context,component,value);
		   	}			
	}
	
	public void validateDoubleConds(FacesContext context, UIComponent component, Object value) 
	{		
		try 
		{
			Double.parseDouble(value.toString());  
		} 
		catch (Exception e) 
		{
			putError(component,"El valor no es un n\u00FAmero real");
			return;
		}

		Integer nodeid = getNodeId(component);
		if(nodeid==null)
			return;
		ruleNodeErrorManager.removeError(nodeid);
	}
	/*
	 * This way finding the node id requires that the component has it in its jsf id
	 */
	public void putError(UIComponent component, String error)
	{
		Integer nodeid = getNodeId(component);
		if(nodeid==null)
			return;
		ruleNodeErrorManager.putError(nodeid,error);
		postMessage(error);
	}
	public Integer getNodeId(UIComponent component)
	{
		try
		{
		String id = component.getId();
		StringBuffer buffer = new StringBuffer();
		for (int i = id.length()-1; i >=0 ; i--) {
			if(Character.isDigit(id.charAt(i)))
			{
				buffer.append(id.charAt(i));
			}
			else
			{
				break;
			}
		}
		
		if(buffer.length()==0)//this shouldn't be
			return null;
		id = buffer.toString();
		Integer nodeid = Integer.parseInt(id);
		return nodeid;
		}
		catch(Exception e){}
		return null;
	}
	
	/**
	 * This is for the Literal Input of the modal for that purpose, on the modal Build Expression
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validateDoubleOrInteger(FacesContext context, UIComponent component, Object value) 
	{		
		HtmlInputText h = (HtmlInputText)component;
	   	String styleClass = h.getStyleClass().toLowerCase();
	   	
	   	if(styleClass.contains("int"))
	   	{
	   		validateInteger(context,component,value);
	   	}
	   	else if(styleClass.contains("real"))
	   	{
	   		validarDouble(context,component,value);
	   	}
	}
	
	
}
