package gehos.ensayo.codebase.genericComponents.yesNoModal;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps make easy to have 1 or more "Yes or No" modal panels. Which means no code repetition
 * OOK
 * @author Yoandry González Castro
 *
 */
public abstract class GenericYesNoModalPanel 
{
	
	Map<String, SettableValue> idSelectedPerModal;
	
	public GenericYesNoModalPanel() 
	{
		idSelectedPerModal = new HashMap<String, SettableValue>();
	}
	
	public void SetObjectIdForModalId(String modalId, Object objectId)
	{
		//idSelectedPerModal.put(modalId, objectId);
	}
	
	public SettableValue GetObjectIdForModalId(String modalId)
	{
		SettableValue sv = null;
		if(idSelectedPerModal.containsKey(modalId))
			sv = idSelectedPerModal.get(modalId);
		else
		{
			sv = new SettableValue(null);
			idSelectedPerModal.put(modalId, sv);
		}		
		return sv;
	}
	
	/**
	 * This is the method that will be executed when the Yes button is clicked
	 * @param modalId
	 */
	public abstract void ExecuteYes(String modalId);
	
	public class SettableValue
	{
		Object value;
		
		public SettableValue(Object value) {		
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
		
	}	
	
}


