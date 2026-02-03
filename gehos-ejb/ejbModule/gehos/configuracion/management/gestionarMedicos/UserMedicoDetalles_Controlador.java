package gehos.configuracion.management.gestionarMedicos;

import gehos.bitacora.session.traces.Bitacora;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("userMedicoDetalles_Controlador")
public class UserMedicoDetalles_Controlador {
	
	
	@In 
	EntityManager entityManager;
	
	
	
	private Long cid = -1l,id;
	
	private Usuario_configuracion usuario_conf = new Usuario_configuracion();
	
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();
	private List<ServicioInEntidad_configuracion> serviciosSource = new ArrayList<ServicioInEntidad_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();
	
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
	public void setId(Long id) {
		this.id = id;
		
		usuario_conf = entityManager.find(Usuario_configuracion.class, this.id);
		tipoFuncionarioSource.clear();
		tipoFuncionarioSource.addAll(usuario_conf.getTipoFuncionarios());
		
		cargoSource.clear();
		cargoSource.addAll(usuario_conf.getCargoFuncionarios());
		
		serviciosSource.clear();
		serviciosSource.addAll(usuario_conf.getServicioInEntidads());
		
		rolsSource.clear();
		rolsSource.addAll(usuario_conf.getRoles());	
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
	public List<ServicioInEntidad_configuracion> getServiciosSource() {
		return serviciosSource;
	}
	public void setServiciosSource(List<ServicioInEntidad_configuracion> serviciosSource) {
		this.serviciosSource = serviciosSource;
	}
	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}
	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
	}

}
