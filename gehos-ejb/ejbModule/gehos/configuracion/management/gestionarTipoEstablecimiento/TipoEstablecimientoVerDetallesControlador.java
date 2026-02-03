package gehos.configuracion.management.gestionarTipoEstablecimiento;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoEstablecimientoSalud_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;

@Name("tipoEstablecimientoVerDetallesControlador")
public class TipoEstablecimientoVerDetallesControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private TipoEstablecimientoSalud_configuracion tipoEstablecimiento;	
		
	// other functions
	private String from = "";
	private int error;
	
	// Methods-------------------------------------------------	
	public void setId(Long id) {	
		error = 0;
		this.id = id;			
		try {
			this.tipoEstablecimiento = (TipoEstablecimientoSalud_configuracion) entityManager.createQuery("select r from TipoEstablecimientoSalud_configuracion r where r.id =:id")
					   .setParameter("id", id)
					   .getSingleResult();
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", "errorInesperado");
			e.printStackTrace();
		}
	}
	
	public void eliminar() {
		try {
			error = 0;
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			entityManager.remove(tipoEstablecimiento);
			entityManager.flush();			
			
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", "enuso");
			e.printStackTrace();
		}
	}

	// Properties--------------------------------------------
	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public Long getId() {
		return id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
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
