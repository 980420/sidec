package gehos.ensayo.ensayo_disenno.session.reglas.helpers;

import java.util.ArrayList;
import java.util.List;

public class CheckRuleResult 
{
	boolean result;
	List<String> messages;
	
	public CheckRuleResult() {
		result = false;
		messages = new ArrayList<String>();
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	
	
}
