package gehos.configuracion.management.gestionarUsuario;

import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;


public class DepartamentoEntidad {
	private Departamento_configuracion departamento;
	private Entidad_configuracion entidad;
	
	public DepartamentoEntidad(Departamento_configuracion departamento, Entidad_configuracion entidad){
		this.departamento = departamento;
		this.entidad = entidad;
	}
	
	public DepartamentoEntidad()
	{
		departamento = new Departamento_configuracion();
		entidad = new Entidad_configuracion();
	}

	public Departamento_configuracion getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento_configuracion departamento) {
		this.departamento = departamento;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}
}
