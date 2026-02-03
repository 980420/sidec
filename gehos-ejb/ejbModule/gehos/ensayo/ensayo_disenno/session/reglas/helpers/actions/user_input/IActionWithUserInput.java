package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

public abstract class IActionWithUserInput {
	
	public static String actionManagerName = "currentActionManager";
	private boolean completed = false;
	private IRuleAction action;

	@Create
	public void init(){
		this.register();
	}

	public boolean getCompleted(){
		return completed;
	}

	public void setCompleted(boolean completed){
		this.completed = completed;
	}
	
	public IRuleAction getAction(){
		return action;
	}
	
	public void setAction(IRuleAction action){
		this.action = action;
	}

	public void register(){
		Context convContext = Contexts.getConversationContext();
		convContext.set(actionManagerName, this);
	}

	/**
	 * Method to save the data and set if it is completed or not
	 */
	public abstract void Process();

	/**
	 * Exports a file
	 * 
	 * @return the file path of the exported file (the real path eg.
	 *         "/home/....../some-file-name.ext")
	 */
	public abstract String Export();

	/**
	 * Tells if the Export method should be executed
	 * 
	 * @return
	 */
	public abstract boolean CanExport();
	
}