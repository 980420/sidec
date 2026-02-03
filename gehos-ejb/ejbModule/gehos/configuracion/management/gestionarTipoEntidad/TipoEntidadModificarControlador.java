package gehos.configuracion.management.gestionarTipoEntidad;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoEntidad_configuracion;


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

@Name("tipoEntidadModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoEntidadModificarControlador {

	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;

	private Long id;
	private String valor;
	private TipoEntidad_configuracion tipoEntidad = new TipoEntidad_configuracion();
	
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
			tipoEntidad = (TipoEntidad_configuracion) 
			  			  entityManager.createQuery("select t from TipoEntidad_configuracion t " +
			  			  						    "where t.id = :id")
			  			  	           .setParameter("id", id)
			  			  	           .getSingleResult();
			this.valor = this.tipoEntidad.getValor();
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
			tipoEntidad.setValor(valor.trim());
			tipoEntidad.setCid(cid);
			
			entityManager.persist(tipoEntidad);
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

	public Long getId() {
		return id;
	}	
}