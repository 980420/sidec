package gehos.configuracion.management.gestionarMedicos;

import java.util.List;

import gehos.configuracion.management.entity.Entidad_configuracion;

public class Entity_Dep_Serv_Especialidades {
	
	private Entidad_configuracion entity;
	private List<Dep_Serv_Especialidades> listaDepServEsp;
	
	public Entity_Dep_Serv_Especialidades(Entidad_configuracion entity,List<Dep_Serv_Especialidades> listaDepServEsp){
		this.entity = entity;
		this.listaDepServEsp = listaDepServEsp;
	}

	public Entidad_configuracion getEntity() {
		return entity;
	}

	public void setEntity(Entidad_configuracion entity) {
		this.entity = entity;
	}

	public List<Dep_Serv_Especialidades> getListaDepServEsp() {
		return listaDepServEsp;
	}

	public void setListaDepServEsp(List<Dep_Serv_Especialidades> listaDepServEsp) {
		this.listaDepServEsp = listaDepServEsp;
	}
	

}
