package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

public class EntidadServicio {
	
	Especialidad_configuracion especialidad;
	Entidad_configuracion entidad;
	Servicio_configuracion servicio;
	
	public EntidadServicio(Especialidad_configuracion especialidad, Entidad_configuracion entidad, Servicio_configuracion servicio){
		this.especialidad = especialidad;
		this.entidad = entidad;
		this.servicio = servicio;
	}

	

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}



	public Especialidad_configuracion getEspecialidad() {
		return especialidad;
	}



	public void setEspecialidad(Especialidad_configuracion especialidad) {
		this.especialidad = especialidad;
	}



	public Servicio_configuracion getServicio() {
		return servicio;
	}



	public void setServicio(Servicio_configuracion servicio) {
		this.servicio = servicio;
	}

}
