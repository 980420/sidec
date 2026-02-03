package gehos.ensayo.ensayo_disenno.session.reglas.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import gehos.ensayo.entity.Variable_ensayo;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

@Name("yEL")
@Scope(ScopeType.CONVERSATION)
public class YEL 
{
	
	public static MethodExpression createMethodExpression(String expression, Class<?> expectedReturnType, Class<?>... expectedParameterTypes) {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    return facesContext.getApplication().getExpressionFactory().createMethodExpression(
	        facesContext.getELContext(), expression, expectedReturnType, expectedParameterTypes);
	}
	/*
	 * This is necessary because JSF (EL, better said) do not support to pass parameters (this parameters are already in the included part) in method when passing Controller and Method alias as parameters
	 * Example:
	 * bean=#{SomeBean}
	 * method="SetValue"
	 * 
	 * This problem can be addressed in a lot of ways but not as good as would be if the above issue was not present
	 * */
	public MethodExpression GetELMethodCall(Object component, String methodName, String... paramNames)
	{
		Class<? extends Object> componentClass = component.getClass();		
		String componentName = Component.getComponentName(componentClass);
		Method[] methods = componentClass.getMethods();
		Method method = null;
		
		for (Method m : methods) {
			if(m.getName().equals(methodName))//fix this later: && m.getParameterTypes().length == paramNames.length
			{
				method = m;
				break;
			}
		}
		String params = "";
		for (int i = 0; i < paramNames.length; i++) 
		{
			params+=paramNames[i] + (i<paramNames.length-1 ? ",":"");			
		}	
		
		String elName = String.format("#{%s.%s(%s)}", componentName, methodName, params);
		return createMethodExpression(elName, method.getReturnType(), method.getParameterTypes());
	}
	public MethodExpression GetELMethodCall(String componentName, String methodName, String... paramNames)
	{
		Context convContext = Contexts.getConversationContext();		
		Object component = convContext.get(componentName);
		Class<? extends Object> componentClass = component.getClass();		
		
		Method[] methods = componentClass.getMethods();
		Method method = null;
		
		for (Method m : methods) {
			if(m.getName().equals(methodName) && m.getParameterTypes().length == paramNames.length)
			{
				method = m;
				break;
			}
		}
		String params = "";
		for (int i = 0; i < paramNames.length; i++) 
		{
			params+=paramNames[i] + (i<paramNames.length-1 ? ",":"");			
		}	
		
		String elName = String.format("#{%s.%s(%s)}", componentName, methodName, params);
		return createMethodExpression(elName, method.getReturnType(), method.getParameterTypes());
	}
	
}
