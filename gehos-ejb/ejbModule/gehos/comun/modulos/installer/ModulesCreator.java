package gehos.comun.modulos.installer;

import gehos.comun.modulos.porentidad.ModulosPorEntidadManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Startup
@Scope(ScopeType.APPLICATION)
@Name("modulesCreator")
public class ModulesCreator {

	@In(create = true)
	ModulosPorEntidadManager modulosPorEntidadManager;

	@Create
	public void constructor() {
		System.out.println("creando m\u00F3dulos f\u00EDsicamente");
		modulosPorEntidadManager.crearModulosFisicamente();
	}

}
