package gehos.configuracion.management.gestionarServiciosClinicos;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Servicio_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("servicioClinicoVerDetallesControlador")
public class ServicioClinicoVerDetallesControlador {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	Long servicioId;
	String from = "";
	Servicio_configuracion servicio;
	
	// other functions
	private int error;
	private boolean errorLoadData;
	
	public void setServicioId(Long id) {
		errorLoadData = false;
		this.servicioId = id;
		try {
			this.servicio = (Servicio_configuracion) 
			entityManager.createQuery("select s from Servicio_configuracion s " +
									  "join s.departamento where s.id = :servicioId " +									  
									  "and (s.departamento.eliminado = false or s.departamento.eliminado = null)")
						 .setParameter("servicioId", this.servicioId)
						 .getSingleResult();
		} catch (Exception e) {
			errorLoadData = true;
		}		
	}

	// removal clinic service
	public void eliminar() {
		error = 0;
		try {
			bitacora.registrarInicioDeAccion("Eliminando servicio clínico");
			
			Servicio_configuracion s = (Servicio_configuracion) 
									   entityManager.createQuery("select s from Servicio_configuracion s " +
									   							 "where s.id = :id")
									   				.setParameter("id", this.servicioId)
									   				.getSingleResult();
			entityManager.remove(s);
			entityManager.flush();
		} catch (Exception e) {
			error = 1;	
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR,"msjEliminar");
		}
	}
	
	// Properties --------------------------------------------------

	public Servicio_configuracion getServicio() {
		return servicio;
	}

	public void setServicio(Servicio_configuracion servicio) {
		this.servicio = servicio;
	}

	public Long getServicioId() {
		return servicioId;
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

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}	
}