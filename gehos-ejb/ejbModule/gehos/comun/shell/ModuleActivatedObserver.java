package gehos.comun.shell;

import gehos.comun.funcionalidades.entity.Funcionalidad;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Log;

@Name("moduleActivatedObserver")
public class ModuleActivatedObserver {
	
	@Logger Log log;
	
	@Observer(create=true, value="moduleActivatedEvent")
	public void logModuleActivatedEvent(Funcionalidad modulo){
		log.info("el modulo " + modulo.getNombre() + " fue activado");
	}

}
