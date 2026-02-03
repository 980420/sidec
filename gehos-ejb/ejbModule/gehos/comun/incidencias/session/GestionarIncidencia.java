package gehos.comun.incidencias.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.incidencias.entity.Componente_Incidencias;
import gehos.comun.incidencias.entity.Estados_Incidencias;
import gehos.comun.incidencias.entity.Incidencia_Incidencias;
import gehos.comun.incidencias.entity.Usuario_Incidencias;
import gehos.comun.shell.IActiveModule;
import gehos.notificaciones.NotificationManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("gestionarIncidencia")
@Scope(ScopeType.PAGE)
public class GestionarIncidencia {
	
	private Incidencia_Incidencias incidencia;
	private long id;
	private String componenteSelected;
	private String estadoSelected;
	private String usuarioAsig;	
	private long idSub;
	
	@In
	private EntityManager entityManager;
	@In
	private Usuario user;
	@In 
	private NotificationManager notificationManager;
	
	@SuppressWarnings("unchecked")
	public List<String> getComponentes(){
		return (List<String>)entityManager.createQuery(
				"select comp.valor from Componente_Incidencias comp where comp.eliminado = false ")
				.getResultList();
	}
	
	public Componente_Incidencias getComponente(String valor){
		return (Componente_Incidencias)entityManager.createQuery(
				"select comp from Componente_Incidencias comp where comp.eliminado = false " +
				"and comp.valor = :val")
				.setParameter("val", valor)
				.setMaxResults(1)
				.getSingleResult();
	}
	
	public Estados_Incidencias getEstado(String valor){
		return (Estados_Incidencias)entityManager.createQuery(
				"select est from Estados_Incidencias est where est.eliminado = false " +
				"and est.valor = :val")
				.setParameter("val", valor)
				.setMaxResults(1)
				.getSingleResult();
	}
	
	public Usuario_Incidencias getUsuario(String user){
		return (Usuario_Incidencias)entityManager.createQuery(
				"select us from Usuario_Incidencias us where us.username = :user")
				.setParameter("user", user)
				.setMaxResults(1)
				.getSingleResult();
	}
	
	public Estados_Incidencias getEstadoInicial(){
		return (Estados_Incidencias)entityManager.createQuery(
				"select est from Estados_Incidencias est where est.eliminado = false " +
				"and est.codigo = 'new'")
				.setMaxResults(1)
				.getSingleResult();
	}
	
	public void insertar(){
		incidencia.setComponente(getComponente(componenteSelected));
		incidencia.setEstado(getEstadoInicial());
		incidencia.setFechaInicio(Calendar.getInstance().getTime());
		incidencia.setProciento(0);
		incidencia.setUsuarioByIdUsuario(entityManager.find(Usuario_Incidencias.class, user.getId()));
		entityManager.persist(incidencia);
		List<String> roles = new ArrayList<String>();
		roles.add("root");
		notificationManager.generateNotification("incidenciaCreated", null, roles, false, incidencia.getId());
		entityManager.flush();
	}
	
	public void modificar(){
		incidencia.setComponente(getComponente(componenteSelected));
		incidencia.setEstado(getEstado(estadoSelected));
		incidencia.setUsuarioByIdAsignado(getUsuario(usuarioAsig));
		if(incidencia.getEstado().getCodigo().equals("solved")){
			List<String> users = new ArrayList<String>();
			users.add(incidencia.getUsuarioByIdUsuario().getUsername());
			notificationManager.generateNotification("incidenciaSolved", users, null, false, incidencia.getId());
		}
		entityManager.flush();
	}

	public Incidencia_Incidencias getIncidencia() {
		if(incidencia == null){
			incidencia = new Incidencia_Incidencias();
		}
		return incidencia;
	}

	public void setIncidencia(Incidencia_Incidencias incidencia) {
		this.incidencia = incidencia;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		if(this.id != id && id != 0){
			incidencia = entityManager.find(Incidencia_Incidencias.class, id);
		}
		this.id = id;
	}

	public String getComponenteSelected() {
		if(componenteSelected == null){
			Funcionalidad fun = entityManager.find(Funcionalidad.class, idSub);
			String name = fun.getId() == -1 ?
					fun.getNombre() :
						fun.getFuncionalidadPadre().getFuncionalidadPadre().getNombre();
			componenteSelected = (String)entityManager.createQuery("select comp.valor " +
					"from Componente_Incidencias comp where comp.eliminado = false " +
					"and comp.codigo = :name ")
					.setParameter("name", name)
					.setMaxResults(1)
					.getSingleResult();
		}
		return componenteSelected;
	}

	public void setComponenteSelected(String componenteSelected) {
		this.componenteSelected = componenteSelected;
	}

	public long getIdSub() {
		return idSub;
	}

	public void setIdSub(long idSub) {
		this.idSub = idSub;
	}
	
	
}
