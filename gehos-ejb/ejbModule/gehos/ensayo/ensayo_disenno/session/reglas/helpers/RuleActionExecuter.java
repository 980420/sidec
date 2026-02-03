package gehos.ensayo.ensayo_disenno.session.reglas.helpers;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IModalRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

public class RuleActionExecuter 
{
	public RuleActionExecuter() {
		// TODO Auto-generated constructor stub
	}
		
	public List<IRuleAction> Actions()
	{
		Context context = Contexts.getApplicationContext();
		String[] seamComponents = context.getNames();
		List<Class> actions = new ArrayList<Class>();
		for (String c : seamComponents) 
		{			
			if(!(context.get(c) instanceof Component)) continue;
			Component o = (Component)context.get(c);					
			if(o.getBeanClass().getSuperclass()==IRuleAction.class || o.getBeanClass().getSuperclass()==IModalRuleAction.class)
			{	
				Class realClass;
				try 
				{
					realClass = Class.forName(o.getBeanClass().getName());//this must be done because o.getBeanClass() is not the complete Class (for example there no methods, etc)
					actions.add(realClass);	
				} catch (ClassNotFoundException e) {}
				
			}
		}
		
		List<IRuleAction> actionsInstances = new ArrayList<IRuleAction>();
		for (Class c : actions) 
		{	
			
			try 
			{
				IRuleAction action = (IRuleAction)c.newInstance();
				actionsInstances.add(action);				
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		Collections.sort(actionsInstances, new Comparator<IRuleAction>()
		{
			@Override
			public int compare(IRuleAction o1, IRuleAction o2) {				
				return ((Integer)o1.orderPosition()).compareTo(o2.orderPosition());
			}
		});			
		
		return actionsInstances;
	}
	
	


}
