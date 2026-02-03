package gehos.configuracion.management.gestionarTipoFuncionario;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;

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

@Name("tipoFuncionarioCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoFuncionarioCrearControlador {

	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
			
	private String valor;
	private TipoFuncionario_configuracion tipoFuncionario;

	// other functions
	private Long cid = -1l;
	private int error;	

	// Methods ------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		error = 0;
		try {
			valor = "";
			tipoFuncionario = new TipoFuncionario_configuracion();
			
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
			this.tipoFuncionario.setValor(valor.trim());
			this.tipoFuncionario.setEliminado(false);
			this.tipoFuncionario.setCid(cid);
			
			entityManager.persist(tipoFuncionario);
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

	public TipoFuncionario_configuracion getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario_configuracion tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}	
}