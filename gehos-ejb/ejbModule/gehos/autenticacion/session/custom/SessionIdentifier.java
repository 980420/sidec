package gehos.autenticacion.session.custom;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Startup
@Scope(ScopeType.APPLICATION)
@Name("sessionIdentifier")
public class SessionIdentifier {
	
	private long lastID = 0;
	
	public synchronized String getId(){
		lastID++;
		return Long.toString(lastID);
	}
	
}
