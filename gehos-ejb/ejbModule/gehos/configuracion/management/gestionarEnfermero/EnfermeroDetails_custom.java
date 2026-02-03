package gehos.configuracion.management.gestionarEnfermero;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.EnfermeraInEntidad;
import gehos.configuracion.management.entity.Enfermera_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.gestionarUsuario.Cultura;
import gehos.configuracion.management.gestionarUsuario.DepartamentoEntidad;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;

@Name("enfermeroDetails_custom")
@Scope(ScopeType.CONVERSATION)
public class EnfermeroDetails_custom {	
	
String selectedTab = "";

//otras funcionalidades
private String from = "";
	
	@In 
	EntityManager entityManager;
	
	@In
	LocaleSelector localeSelector;
	
	private Long cid = -1l,id;
	private String culturaSelec = "";
	
	List<Cultura> listaCultura = new ArrayList<Cultura>();
			
	private Usuario_configuracion usuario_conf = new Usuario_configuracion();
	
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();
	private List<Servicio_configuracion> serviciosSource = new ArrayList<Servicio_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();
	
	private List<DepartamentoEntidad> listaDepartamentoEntidad = new ArrayList<DepartamentoEntidad>();
	
	private List<Entidad_configuracion> listaEntidadServicios = new ArrayList<Entidad_configuracion>();
	private List<Departamento_configuracion> listaDepartamentoServicios = new ArrayList<Departamento_configuracion>();
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	public Long getCid() {
		return cid;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public Long getId() {
		return id;
	}
	
	
	
	private Enfermera_configuracion enfermero = new Enfermera_configuracion();
	private Long enfermeroId = 0l;


	//METODOS-------------------------------------------------------------------
	//carga el enfermero a mostrar
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setEnfermeroId(Long enfermeroId) {
		this.enfermeroId = enfermeroId;
		enfermero = entityManager.find(Enfermera_configuracion.class, enfermeroId);
		servicioInEntidadConfiguracion();
		
		cultura();
		
		usuario_conf = (Usuario_configuracion)entityManager.createQuery("select u from Usuario_configuracion u where u.id =:id").setParameter("id", enfermero.getUsuario().getId()).getSingleResult();
		servicioInEntidadConfiguracion();		
			
		for (int i = 0; i < listaCultura.size(); i++) {
			if(listaCultura.get(i).getLocalString().equals(usuario_conf.getProfile().getLocaleString())){
				culturaSelec = listaCultura.get(i).getIdioma();
			}	
		}
				
		tipoFuncionarioSource.clear();
		tipoFuncionarioSource.addAll(usuario_conf.getTipoFuncionarios());
		
		cargoSource.clear();
		cargoSource.addAll(usuario_conf.getCargoFuncionarios());
		
		serviciosSource.clear();
		//serviciosSource.addAll(usuario_conf.getServicios());----------
		
		rolsSource.clear();
		rolsSource.addAll(usuario_conf.getRoles());		
	}
	
	public List<String> cultura(){		
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		listaCultura = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {					
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(), listaSelectItem.get(i).getValue().toString());			
			listaCultura.add(c);
			lista.add(c.cultura());
		}		
		return lista;		
	}
	
	//carga los servicios y los departamentos asignados al usuario
	@SuppressWarnings("unchecked")
	public void servicioInEntidadConfiguracion(){
		listaEntidadServicios = entityManager.createQuery("select ent from Departamento_configuracion dep, Entidad_configuracion ent " +
														  "where (dep.id, ent.id) in (select d.id, e.id from ServicioInEntidad_configuracion sie " +
														  							 "join sie.usuarios u join sie.entidad e join sie.servicio.departamento d " +
														  							 "where u.id =:idUsuario)")
											 .setParameter("idUsuario", this.enfermero.getUsuario().getId())
											 .getResultList();
		listaDepartamentoServicios = entityManager.createQuery("select dep from Departamento_configuracion dep, Entidad_configuracion ent " +
															   "where (dep.id, ent.id) in(select d.id, e.id from ServicioInEntidad_configuracion sie " +
															   							 "join sie.usuarios u join sie.entidad e join sie.servicio.departamento d where u.id =:idUsuario)")
												  .setParameter("idUsuario", this.enfermero.getUsuario().getId())
												  .getResultList();
		
		listaDepartamentoEntidad = new ArrayList<DepartamentoEntidad>();
		
		for (int i = 0; i < listaDepartamentoServicios.size(); i++) {
			listaDepartamentoEntidad.add(new DepartamentoEntidad(listaDepartamentoServicios.get(i), listaEntidadServicios.get(i)));
		}
		
	}
		
	//carga los servicios dado un departamento y la entidad a la cual pertenece este ultimo
	@SuppressWarnings("unchecked")
	public String servicioConfiguracion(Long idDepartamento, Long idEntidad){
		List<String> l =  entityManager.createQuery("select distinct sie.servicio.nombre from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario and sie.entidad.id =:idEntidad and sie.servicio.departamento.id =:idDepartamento").setParameter("idUsuario", this.enfermero.getUsuario().getId()).setParameter("idEntidad", idEntidad).setParameter("idDepartamento", idDepartamento).getResultList();
		String lista = "";
		for (int i = 0; i < l.size()-1; i++) {
			lista+=l.get(i) + ", ";
		}
		lista+=l.get(l.size()-1);
		return lista;	
	}
	
	//eliminar usuario
	@SuppressWarnings("unchecked")
	public void eliminar(){
		Enfermera_configuracion e = (Enfermera_configuracion)entityManager.find(Enfermera_configuracion.class, this.enfermero.getId());
		e.setEliminado(true);		
		entityManager.persist(e);
		
		List<EnfermeraInEntidad> aux = entityManager.createQuery("select enferInEntity from EnfermeraInEntidad enferInEntity where enferInEntity.enfermera.id =:id").setParameter("id", e.getId()).getResultList();
		for (int i = 0; i < aux.size(); i++) {
			entityManager.remove(aux.get(i));
		}
		
		entityManager.flush();	
	}
	
	//guarda cual es el tab seleccionado
	public void cambiarTabSelected(String selectedTab){
		this.selectedTab = selectedTab;
	}
	
	//PROPIEDADES------------------------------------------------------------------------------
	public Usuario_configuracion getUsuario_conf() {
		return usuario_conf;
	}
	public void setUsuario_conf(Usuario_configuracion usuario_conf) {
		this.usuario_conf = usuario_conf;
	}
	public List<TipoFuncionario_configuracion> getTipoFuncionarioSource() {
		return tipoFuncionarioSource;
	}
	public void setTipoFuncionarioSource(List<TipoFuncionario_configuracion> tipoFuncionarioSource) {
		this.tipoFuncionarioSource = tipoFuncionarioSource;
	}
	public List<CargoFuncionario_configuracion> getCargoSource() {
		return cargoSource;
	}
	public void setCargoSource(List<CargoFuncionario_configuracion> cargoSource) {
		this.cargoSource = cargoSource;
	}
	public List<Servicio_configuracion> getServiciosSource() {
		return serviciosSource;
	}
	public void setServiciosSource(List<Servicio_configuracion> serviciosSource) {
		this.serviciosSource = serviciosSource;
	}
	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}
	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource; 
	}	
	public String getCulturaSelec() {
		return culturaSelec;
	}
	public void setCulturaSelec(String culturaSelec) {
		this.culturaSelec = culturaSelec;
	}
	public List<Cultura> getListaCultura() {
		return listaCultura;
	}
	public void setListaCultura(List<Cultura> listaCultura) {
		this.listaCultura = listaCultura;
	}
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	

	public List<DepartamentoEntidad> getListaDepartamentoEntidad() {
		return listaDepartamentoEntidad;
	}

	public void setListaDepartamentoEntidad(
			List<DepartamentoEntidad> listaDepartamentoEntidad) {
		this.listaDepartamentoEntidad = listaDepartamentoEntidad;
	}

	

	public List<Entidad_configuracion> getListaEntidadServicios() {
		return listaEntidadServicios;
	}

	public void setListaEntidadServicios(
			List<Entidad_configuracion> listaEntidadServicios) {
		this.listaEntidadServicios = listaEntidadServicios;
	}

	public List<Departamento_configuracion> getListaDepartamentoServicios() {
		return listaDepartamentoServicios;
	}

	public void setListaDepartamentoServicios(
			List<Departamento_configuracion> listaDepartamentoServicios) {
		this.listaDepartamentoServicios = listaDepartamentoServicios;
	}

	public Enfermera_configuracion getEnfermero() {
		return enfermero;
	}

	public void setEnfermero(Enfermera_configuracion enfermero) {
		this.enfermero = enfermero;
	}

	public Long getEnfermeroId() {
		return enfermeroId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
