package gehos.configuracion.management.gestionarTipoFuncionario;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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

@Name("tipoFuncionarioModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoFuncionarioModificarControlador {

	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;

	private Long id;
	private String valor;
	private TipoFuncionario_configuracion tipoFuncionario = new TipoFuncionario_configuracion();
	
	// other functions
	private int error;
	private Long cid;

	// Methods ---------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {
		error = 0;
		
		try {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));
			
			this.id = id;
			tipoFuncionario = (TipoFuncionario_configuracion) 
			  			  entityManager.createQuery("select t from TipoFuncionario_configuracion t " +
			  			  						    "where t.id = :id")
			  			  	           .setParameter("id", id)
			  			  	           .getSingleResult();
			this.valor = this.tipoFuncionario.getValor();
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	@End
	public void end(){		
	}
	
	@Transactional
	public void modificar() {
		error = 0;
		try {			
			tipoFuncionario.setValor(valor.trim());
			tipoFuncionario.setCid(cid);
			
			entityManager.persist(tipoFuncionario);
			entityManager.flush();
			
			end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	// Properties -------------------------------------------------

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

	public Long getId() {
		return id;
	}	
}