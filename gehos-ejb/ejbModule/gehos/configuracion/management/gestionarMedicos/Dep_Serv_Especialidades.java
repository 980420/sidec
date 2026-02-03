package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

public class Dep_Serv_Especialidades {
	
	private Departamento_configuracion departamento;
	private Servicio_configuracion servicio;
	private String especialidades;
	
	public Dep_Serv_Especialidades(){		
	}
	
	public Dep_Serv_Especialidades(Departamento_configuracion departamento,Servicio_configuracion servicio,String especialidades){
		this.departamento = departamento;
		this.servicio = servicio;
		this.especialidades = especialidades;
	}

	public Departamento_configuracion getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento_configuracion departamento) {
		this.departamento = departamento;
	}

	public Servicio_configuracion getServicio() {
		return servicio;
	}

	public void setServicio(Servicio_configuracion servicio) {
		this.servicio = servicio;
	}

	public String getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(String especialidades) {
		this.especialidades = especialidades;
	}
}
