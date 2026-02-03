package gehos.comun.updater;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("scheduledManagement")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class ScheduledManagement {

	private String hora;

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}
	
}
