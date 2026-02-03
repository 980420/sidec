package gehos.configuracion.management.gestionarUsuario;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Profile_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("usuarioVerDetallesControlador")
@Scope(ScopeType.CONVERSATION)
public class UsuarioVerDetallesControlador {

	// datos del usuario
	private Long id = -1l;
	private Usuario_configuracion usuario_conf = new Usuario_configuracion();

	// cultura
	private String culturaSelec = "";
	List<Cultura> listaCultura = new ArrayList<Cultura>();

	// roles
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();
	private ListadoControler<Role_configuracion> rolsSource_controler;

	// tipo de funcionarios
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();

	// cargos
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();

	// servicios
	private List<Servicio_configuracion> serviciosSource = new ArrayList<Servicio_configuracion>();
	private List<DepartamentoEntidad> listaDepartamentoEntidad = new ArrayList<DepartamentoEntidad>();
	private List<Entidad_configuracion> listaEntidadServicios = new ArrayList<Entidad_configuracion>();
	private List<Departamento_configuracion> listaDepartamentoServicios = new ArrayList<Departamento_configuracion>();

	// otras funcionalidades
	String selectedTab = "";
	String from = "";

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	@In
	LocaleSelector localeSelector;

	@SuppressWarnings("unchecked")	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setId(Long id) {
		if (this.id.equals(-1l)) {
			this.id = id;
			cultura();

			usuario_conf = (Usuario_configuracion) entityManager.createQuery(
					"select u from Usuario_configuracion u where u.id =:id")
					.setParameter("id", this.id).getSingleResult();
			servicioInEntidadConfiguracion();

			for (int i = 0; i < listaCultura.size(); i++) {
				if (listaCultura.get(i).getLocalString().equals(
						usuario_conf.getProfile().getLocaleString())) {
					culturaSelec = listaCultura.get(i).getIdioma();
				}
			}

			tipoFuncionarioSource.clear();
			tipoFuncionarioSource = entityManager
					.createQuery(
							"select t from TipoFuncionario_configuracion t join t.usuarios u where u.id =:idUsuario order by t.valor")
					.setParameter("idUsuario", usuario_conf.getId())
					.getResultList();

			cargoSource.clear();
			cargoSource = entityManager
					.createQuery(
							"select c from CargoFuncionario_configuracion c join c.usuarios u where u.id =:idUsuario order by c.valor")
					.setParameter("idUsuario", usuario_conf.getId())
					.getResultList();

			serviciosSource.clear();

			rolsSource.clear();
			rolsSource = entityManager
					.createQuery(
							"select r from Role_configuracion r join r.usuarios u where u.id =:idUsuario order by r.name")
					.setParameter("idUsuario", usuario_conf.getId())
					.getResultList();
			rolsSource_controler = new ListadoControler<Role_configuracion>(
					rolsSource);
		}
	}

	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		listaCultura = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			listaCultura.add(c);
			lista.add(c.cultura());
		}
		return lista;
	}

	// guarda cual es el tab seleccionado
	public void cambiarTabSelected(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	@SuppressWarnings("unchecked")
	public void servicioInEntidadConfiguracion() {
		listaEntidadServicios = entityManager
				.createQuery(
						"select ent from Departamento_configuracion dep, Entidad_configuracion ent where (dep.id, ent.id) in(select d.id, e.id from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.entidad e join sie.servicio.departamento d where u.id =:idUsuario)")
				.setParameter("idUsuario", this.id).getResultList();
		listaDepartamentoServicios = entityManager
				.createQuery(
						"select dep from Departamento_configuracion dep, Entidad_configuracion ent where (dep.id, ent.id) in(select d.id, e.id from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.entidad e join sie.servicio.departamento d where u.id =:idUsuario)")
				.setParameter("idUsuario", this.id).getResultList();
		listaDepartamentoEntidad = new ArrayList<DepartamentoEntidad>();

		for (int i = 0; i < listaDepartamentoServicios.size(); i++) {
			listaDepartamentoEntidad.add(new DepartamentoEntidad(
					listaDepartamentoServicios.get(i), listaEntidadServicios
							.get(i)));
		}
	}

	@SuppressWarnings("unchecked")
	public List<Departamento_configuracion> departamentoConfiguracion(
			int idEntidad) {
		List<Departamento_configuracion> ld = entityManager
				.createQuery(
						"select distinct sie.servicio.departamento from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario and sie.entidad.id =:idEntidad")
				.setParameter("idUsuario", this.id).setParameter("idEntidad",
						idEntidad).getResultList();
		return ld;
	}

	@SuppressWarnings("unchecked")
	public String servicioConfiguracion(Long idDepartamento, Long idEntidad) {
		List<String> l = entityManager
				.createQuery(
						"select distinct sie.servicio.nombre from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario and sie.entidad.id =:idEntidad and sie.servicio.departamento.id =:idDepartamento order by sie.servicio.nombre")
				.setParameter("idUsuario", this.id).setParameter("idEntidad",
						idEntidad).setParameter("idDepartamento",
						idDepartamento).getResultList();
		String lista = "";
		for (int i = 0; i < l.size() - 1; i++) {
			lista += l.get(i) + ", ";
		}
		lista += l.get(l.size() - 1);
		return lista;
	}

	// eliminar usuario
	@End
	@Transactional
	public String eliminar() {
		try {
			Usuario_configuracion u = (Usuario_configuracion) entityManager
					.find(Usuario_configuracion.class, this.id);

			Profile_configuracion prof = entityManager.find(
					Profile_configuracion.class, this.id);

			prof.setEliminado(true);
			entityManager.persist(prof);			
			u.setEliminado(true);
			entityManager.persist(u);
			entityManager.flush();

			entityManager.flush();
			
			return "listar";
		} catch (Exception exc) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"msjEliminar"));
			return "fail";
		}
	}

	public Usuario_configuracion getUsuario_conf() {
		return usuario_conf;
	}

	public void setUsuario_conf(Usuario_configuracion usuario_conf) {
		this.usuario_conf = usuario_conf;
	}

	public List<TipoFuncionario_configuracion> getTipoFuncionarioSource() {
		return tipoFuncionarioSource;
	}

	public void setTipoFuncionarioSource(
			List<TipoFuncionario_configuracion> tipoFuncionarioSource) {
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

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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

	public ListadoControler<Role_configuracion> getRolsSource_controler() {
		return rolsSource_controler;
	}

	public void setRolsSource_controler(
			ListadoControler<Role_configuracion> rolsSource_controler) {
		this.rolsSource_controler = rolsSource_controler;
	}
}
