package gehos.bitacora.session.traces;

import javax.ejb.Remove;

import org.jboss.seam.annotations.Destroy;

public interface IBitacora {

	public abstract void registrarInicioDeSession(String username);

	public abstract void registrarModuloAccedido(Long moduleId);

	public abstract Long registrarInicioDeAccion(String accion);

	@Remove
	@Destroy
	public void destructor();

}