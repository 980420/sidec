package gehos.configuracion.management.gestionarEspecialidades;

import gehos.configuracion.management.entity.Especialidad_configuracion;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("especialidadVerDetallesControlador")
@Scope(ScopeType.CONVERSATION)
public class EspecialidadVerDetallesControlador {
			
	//departamento
	private Long especialidadId;
	Especialidad_configuracion especialidad = new Especialidad_configuracion();
			
	//otras funcionalidades
	private String from = "";
	
	@In 
	EntityManager entityManager;
	
	@In
	LocaleSelector localeSelector;
	
	@In(create = true)
	FacesMessages facesMessages;
		
	//METODOS---------------------------------------------------------------------	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void setEspecialidadId(Long especialidadId) {
		this.especialidadId = especialidadId;
		this.especialidad = entityManager.find(Especialidad_configuracion.class, this.especialidadId);
	}	
	
	public String eliminar(){
		try {
			entityManager.remove(especialidad);
			entityManager.flush();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, "Esta especialidad está en uso por lo tanto no puede ser eliminada.");
			return "fail";
		}
		return "eliminar";		
	}

	//PROPIEDADES-------------------------------------------------
	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Long getEspecialidadId() {
		return especialidadId;
	}

	public Especialidad_configuracion getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad_configuracion especialidad) {
		this.especialidad = especialidad;
	}

	

	

}
