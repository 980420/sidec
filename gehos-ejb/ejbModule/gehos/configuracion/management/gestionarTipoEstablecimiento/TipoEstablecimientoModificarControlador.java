package gehos.configuracion.management.gestionarTipoEstablecimiento;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoEstablecimientoSalud_configuracion;


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

@Name("tipoEstablecimientoModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoEstablecimientoModificarControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private TipoEstablecimientoSalud_configuracion tipoEstablecimiento;
	
	//other functions
	private int error;
	private Long cid = -1l;
	
	// Methods------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {	
		error = 0;
		this.id = id;
		
		try {
			this.tipoEstablecimiento = (TipoEstablecimientoSalud_configuracion) entityManager.createQuery("select r from TipoEstablecimientoSalud_configuracion r where r.id =:id")
					   .setParameter("id", id)
					   .getSingleResult();
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));
			tipoEstablecimiento.setCid(cid);
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
	
	@End @Transactional
	public void modificar() {
		try {
			error = 0;
			this.tipoEstablecimiento.setValor(tipoEstablecimiento.getValor().trim());
			entityManager.persist(tipoEstablecimiento);
			entityManager.flush();		
		} catch(Exception e) {
			error = 1;
			facesMessages.addFromResourceBundle("errorInesperado");
			e.printStackTrace();
		}		
	}

	// Properties-----------------------------------------------------
	public Long getId() {
		return id;
	}	
	
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