package gehos.configuracion.management.gestionarTipoEntidad;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoEntidad_configuracion;

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
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("tipoEntidadCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoEntidadCrearControlador {

	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
			
	private String valor;
	private TipoEntidad_configuracion tipoEntidad;

	// other functions
	private Long cid = -1l;
	private int error;	

	// Methods ------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		error = 0;
		try {
			valor = "";
			tipoEntidad = new TipoEntidad_configuracion();
			
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
		} catch (Exception e) {
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
		error = 0;
		try { 			
			this.tipoEntidad.setValor(valor.trim());
			this.tipoEntidad.setEliminado(false);
			this.tipoEntidad.setCid(cid);
			
			entityManager.persist(tipoEntidad);
			entityManager.flush();
			
			end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}		
	}
	
	// Properties ----------------------------------------
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public TipoEntidad_configuracion getTipoEntidad() {
		return tipoEntidad;
	}

	public void setTipoEntidad(TipoEntidad_configuracion tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}	
}