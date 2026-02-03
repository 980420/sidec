package gehos.configuracion.management.gestionarTipoUbicacion;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoUbicacion_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("tipoUbicacionVerDetallesControlador")
public class TipoUbicacionVerDetallesControlador {
	
	@In IBitacora bitacora;
	@In EntityManager entityManager;	
	@In FacesMessages facesMessages;	
			
	private Long id;	
	private int error;
	private boolean errorLoadData;
	private TipoUbicacion_configuracion tipo;
	
	//other functionalities
	private String from = "";

	//Methods--------------------------------------------------
	public void setId(Long id) {	
		errorLoadData = false;		
		this.id = id;	
		tipo = new TipoUbicacion_configuracion();
		
		try{								
			this.tipo = (TipoUbicacion_configuracion) 
						entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
			   			    		 .setParameter("id", this.id)
			   						 .getSingleResult();
		}
		catch(NoResultException e){	
			errorLoadData = true;			
		}
	}
	
		
	@Transactional
	public void eliminar(){
		try {
			error = 0;		
			bitacora.registrarInicioDeAccion("Eliminando tipo de ubicacion");
			
			TipoUbicacion_configuracion t = (TipoUbicacion_configuracion) 
			  								  entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
			  								  			   .setParameter("id", id)
			  								  			   .getSingleResult();			
			entityManager.remove(t);		
			entityManager.flush();
		}		
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR,"msjEliminar");							
		}		
	}
	
	//Properties--------------------------------------	
	public Long getId() {
		return id;
	}	
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public TipoUbicacion_configuracion getTipo() {
		return tipo;
	}

	public void setTipo(TipoUbicacion_configuracion tipo) {
		this.tipo = tipo;
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
