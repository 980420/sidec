package gehos.configuracion.management.gestionarTipoCama;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoCama_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("tipoCamaVerDetallesControlador")
public class TipoCamaVerDetallesControlador {
	
	@In IBitacora bitacora;
	@In EntityManager entityManager;	
	@In FacesMessages facesMessages;
	
	private Long id;
	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();
	
	// other functions
	private int error;	
	private String from;
	
	//Methods-----------------------------------------------------------	
	public void setId(Long id) {		
		try {	
			error = 0;	
			this.id = id;		
			tipoCama = new TipoCama_configuracion();
			
			this.tipoCama = (TipoCama_configuracion) entityManager
							.createQuery("select t from TipoCama_configuracion t where t.id =:id")
							.setParameter("id", this.id).getSingleResult();				
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
		
	public void eliminar(){
		
		try {
			error = 0;
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			entityManager.remove(tipoCama);
			entityManager.flush();
			
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}

	//Properties----------------------------------------------------
	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public Long getId() {
		return id;
	}	

	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}

	public void setTipoCama(TipoCama_configuracion tipoCama) {
		this.tipoCama = tipoCama;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
