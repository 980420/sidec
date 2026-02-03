package gehos.configuracion.management.gestionarTipoEstablecimiento;

import gehos.bitacora.session.traces.IBitacora;

import gehos.configuracion.management.entity.TipoEstablecimientoSalud_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;


@Name("tipoEstablecimientoCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoEstablecimientoCrearControlador {	
	
	@In IBitacora bitacora;
	@In FacesMessages facesMessages;	
	@In EntityManager entityManager;
	
	private Long cid = -1l;
	private TipoEstablecimientoSalud_configuracion tipoEstablecimiento;
	
	// other functionalities
	private int error;
		
	// Methods----------------------------------------------------	
	@Begin(flushMode=FlushModeType.MANUAL, join = true)
	public void begin(){
		try {
			error = 0;
			tipoEstablecimiento = new TipoEstablecimientoSalud_configuracion();
			tipoEstablecimiento.setEliminado(false);
			
			if (cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
				tipoEstablecimiento.setCid(cid);
			}
		} catch(Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	@End
	public void end() {
	}
			
	@Transactional
	public void crear() {		
		try {	
			error = 0;
			tipoEstablecimiento.setCid(cid);
			tipoEstablecimiento.setValor(tipoEstablecimiento.getValor().trim());
			entityManager.persist(tipoEstablecimiento);
			entityManager.flush();			
		}			
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	// Properties--------------------------------------------------	
	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public TipoEstablecimientoSalud_configuracion getTipoEstablecimiento() {
		return tipoEstablecimiento;
	}

	public void setTipoEstablecimiento(
			TipoEstablecimientoSalud_configuracion tipoEstablecimiento) {
		this.tipoEstablecimiento = tipoEstablecimiento;
	}
}
