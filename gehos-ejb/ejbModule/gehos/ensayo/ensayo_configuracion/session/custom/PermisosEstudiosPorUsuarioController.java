package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

@Name("permisosEstudiosPorUsuarioController")
@Scope(ScopeType.CONVERSATION)
public class PermisosEstudiosPorUsuarioController {
	
	private Long idEstudio;
	

	public Long getIdEstudio() {
		return idEstudio;
	}

	public void setIdEstudio(Long idEstudio) {
		this.idEstudio = idEstudio;
	}
	
	public String NombreEstudio(){
		Estudio_ensayo estEnsayo = entityManager.find(Estudio_ensayo.class , this.getIdEstudio());
		return estEnsayo.getNombre();
		
	}
	

	/**
	 * Gestionar las trazas en la bitacora
	 */
	protected @In IBitacora bitacora;

	/**
	 * Para manejar los mensajes a mostrar
	 * 
	 */
	@In
	FacesMessages facesMessages;

	/**
	 * Registro en la bitacora
	 */
	Long cid = -1l;

	/**
	 * Para manejar los datos en la BD
	 */
	@In
	private EntityManager entityManager;

	
	// Roles en el sistema
	private List<String> roles = new ArrayList<String>();

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	// Rol seleccionado
	private String selectedRol = "";

	public String getSelectedRol() {
		return selectedRol;
	}

	public void setSelectedRol(String selectedRol) {
		this.selectedRol = selectedRol;
	}

	// Usuarios en el sistema
	private List<String> usuarios = new ArrayList<String>();

	public List<String> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<String> usuarios) {
		this.usuarios = usuarios;
	}

	// Usuario seleccionado
	private String selectedUsuario = "";

	public String getSelectedUsuario() {
		return selectedUsuario;
	}

	public void setSelectedUsuario(String selectedUsuario) {
		this.selectedUsuario = selectedUsuario;
	}

	/**
	 * Entidades Disponibles
	 */
	private List<Entidad_ensayo> listaEntidadSource = new ArrayList<Entidad_ensayo>();

	public List<Entidad_ensayo> getListaEntidadSource() {
		return listaEntidadSource;
	}

	public void setListaEntidadSource(List<Entidad_ensayo> listaEntidadSource) {
		this.listaEntidadSource = listaEntidadSource;
	}

	/**
	 * Entidades Asignadas
	 */
	private List<Entidad_ensayo> listaEntidadTarget = new ArrayList<Entidad_ensayo>();

	public List<Entidad_ensayo> getListaEntidadTarget() {
		return listaEntidadTarget;
	}

	public void setListaEntidadTarget(List<Entidad_ensayo> listaEntidadTarget) {
		this.listaEntidadTarget = listaEntidadTarget;
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		if (cid.equals(-1l)) {
			cid = bitacora
					.registrarInicioDeAccion("Modificando permisos en uusuarios por estudios");
		}
	}

	@Create
	@SuppressWarnings("unchecked")
	public void SourceLoad() {

		// Lista de las entidades en blanco
		listaEntidadSource = new ArrayList<Entidad_ensayo>();
		listaEntidadTarget = new ArrayList<Entidad_ensayo>();

	
		// Cargo todos los roles
		this.roles = entityManager
				.createQuery(
						"select r.name from Role_ensayo r where r.eliminado <> true order by r.name asc")
				.getResultList();
		this.selectedRol = "";

		this.usuarios = new ArrayList<String>();
		this.selectedUsuario = "";
		
		Studios();

	}

	@SuppressWarnings("unchecked")
	public void update() {

		Studios();

	}

	@SuppressWarnings("unchecked")
	public void updateRoles() {

		if (this.selectedRol != null) {

			this.usuarios = entityManager
					.createQuery(
							"select u.username from Usuario_ensayo u JOIN u.roles r where u.eliminado <> true and r.eliminado <> true and r.name = :rname")
					.setParameter("rname", this.selectedRol).getResultList();

			this.selectedUsuario = "";

		}
		Studios();
	}

	@SuppressWarnings("unchecked")
	private void Studios() {

		if (this.idEstudio != null && this.selectedRol != ""
				&& this.selectedUsuario != "") {

			// Todas las entidades que tiene asociada el estudio
			listaEntidadSource = entityManager
					.createQuery(
							"Select ent from Entidad_ensayo ent "
									+ "JOIN ent.estudioEntidads ee "
									+ "JOIN  ee.estudio e "
									+ "where ee.eliminado <> true "
									+ "and e.id =:nombre "
									+ "and e.eliminado <> true "
									+ "and ent.eliminado <> true "
									+ "order by ent.nombre")
					.setParameter("nombre", this.idEstudio).getResultList();

			// Todas las entidades a los que esta asociado el usuario
			listaEntidadTarget = entityManager
					.createQuery(
							"Select ent from Entidad_ensayo ent "
									+ "JOIN ent.estudioEntidads ee "
									+ "JOIN ee.estudio e "
									+ "JOIN ee.usuarioEstudios ue "
									+ "JOIN ue.role r " + "JOIN ue.usuario u "

									+ "where ee.eliminado <> true "
									+ "and e.id =:nombre "
									+ "and r.name =:rnombre "
									+ "and u.username =:unombre "

									+ "and e.eliminado <> true "
									+ "and ent.eliminado <> true "
									+ "and ue.eliminado <> true "
									+ "and r.eliminado <> true "
									+ "and u.eliminado <> true "

									+ "order by ent.nombre")

					.setParameter("rnombre", selectedRol)
					.setParameter("unombre", selectedUsuario)
					.setParameter("nombre", this.idEstudio).getResultList();

			validateEntidadTarget();

		} else {
			listaEntidadSource = new ArrayList<Entidad_ensayo>();
			listaEntidadTarget = new ArrayList<Entidad_ensayo>();
		}

	}

	public void validateEntidadTarget() {
		for (int j = 0; j < listaEntidadTarget.size(); j++) {
			for (int i = 0; i < listaEntidadSource.size(); i++) {
				if (listaEntidadSource.get(i).equals(listaEntidadTarget.get(j))) {
					listaEntidadSource.remove(i);
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@End
	public String salvar() {
		
		if (this.idEstudio != null && this.selectedRol != ""	&& this.selectedUsuario != "") {
			
			// El rol
			Role_ensayo role = (Role_ensayo) entityManager
					.createQuery(
							"from Role_ensayo r " + "where r.name = :role"
									+ " and r.eliminado <> true")
					.setParameter("role", this.selectedRol).getSingleResult();

			
			// El usuario
			Usuario_ensayo usuario = (Usuario_ensayo) entityManager
					.createQuery(
							"from Usuario_ensayo u "
									+ "where u.username = :username"
									+ " and u.eliminado <> true")
					.setParameter("username", this.selectedUsuario)
					.getSingleResult();

			// El Estudio
			Estudio_ensayo estudio = (Estudio_ensayo) entityManager
					.createQuery(
							"from Estudio_ensayo e "
									+ "where e.id = :nombre"
									+ " and e.eliminado <> true")
					.setParameter("nombre", this.idEstudio)
					.getSingleResult();

			// Todos los centros actuales del tipo en cuestion
			List<UsuarioEstudio_ensayo> estudiosUser = entityManager
					.createQuery(

							"Select uee from UsuarioEstudio_ensayo uee "
									+ "JOIN uee.usuario u "
									+ "JOIN uee.role r "
									+ "JOIN uee.estudioEntidad ee "
									+ "JOIN ee.estudio e "

									+ "where "

									+ " e.id =:nombre "
									+ "and u.username =:unombre "

									+ "and e.eliminado <> true "
									+ "and ee.eliminado <> true "
									+ "and uee.eliminado <> true "
									+ "and r.eliminado <> true "
									+ "and u.eliminado <> true "

					)

					.setParameter("unombre", selectedUsuario)
					.setParameter("nombre", idEstudio).getResultList();

			for (int j = 0; j < estudiosUser.size(); j++) {
				UsuarioEstudio_ensayo usuarioEstudio_ensayo = estudiosUser.get(j);
                boolean eliminar = true;
                
				for (int i = 0; i < listaEntidadTarget.size(); i++) {
					Entidad_ensayo entidad_ensayo = listaEntidadTarget.get(i);
					

					// El usuario actual esta relacioando a la entidad
					if (usuarioEstudio_ensayo.getEstudioEntidad().getEntidad().equals(entidad_ensayo)) {
						// El usuario tiene distinto rol
						eliminar = false;
						if (!usuarioEstudio_ensayo.getRole().equals(role)) {
							facesMessages
									.add("El usuario: "
											+ usuario.getUsername()
											+ " ya esta relacionado al estudio: "
											+ estudio.getNombre()
											+ " en la entiadad: "
											+ entidad_ensayo.getNombre()
											+ " con el rol: "
											+ usuarioEstudio_ensayo.getRole().getName());
							listaEntidadTarget.remove(i);
							break;

						} else {
							//Se mantiene igual
							listaEntidadTarget.remove(i);
							break;
						}						
					}

				}
				
				if (eliminar) {
					usuarioEstudio_ensayo.setEliminado(true);
					usuarioEstudio_ensayo.setCid(cid);
					entityManager.persist(usuarioEstudio_ensayo);
					entityManager.flush();
				}

			}
			
			//Adiciono las nuevas relaciones
			for (int i = 0; i < listaEntidadTarget.size(); i++) {
				
				Entidad_ensayo entidad_ensayo = listaEntidadTarget.get(i);
			
				EstudioEntidad_ensayo estudioEntidad = (EstudioEntidad_ensayo) entityManager.createQuery(
						 "Select eee from EstudioEntidad_ensayo eee "
						 
						 + "JOIN eee.entidad ent "
						 + "JOIN eee.estudio est "
						 
						 + "where "
						 
						 + "eee.eliminado <> true "
						 + "and ent.eliminado <> true "
						 + "and est.eliminado <> true "
						 
						 + "and est.id=:estID "
						 + "and ent.id=:entID "
						 
						)
						.setParameter("estID", estudio.getId())
						.setParameter("entID", entidad_ensayo.getId())
						.getSingleResult();
				
				
				int a = entityManager
						.createQuery(
								"select uee "
										+ " from UsuarioEstudio_ensayo uee where uee.eliminado <> true"
										+ " and uee.estudioEntidad.id=:estID"
										+ " and uee.usuario.id=:usrID"
										+ " order by uee.usuario.nombre")
										.setParameter("estID", estudioEntidad.getId())
										.setParameter("usrID", usuario.getId())							
						.getResultList().size();

				/*if(a!=0){
					facesMessages
					.add("El usuario: "
							+ usuario.getUsername()
							+ " ya esta relacionado al estudio: "
							+ estudio.getNombre()
							+ " en la entiadad: "
							+ entidad_ensayo.getNombre());
					continue;
				}*/
				
				a = entityManager
						.createQuery(
								"select uee "
										+ " from UsuarioEstudio_ensayo uee where uee.eliminado <> true"
										+ " and uee.estudioEntidad.id=:estID"
										+ " and uee.role.id=:rolID")
										.setParameter("estID", estudioEntidad.getId())
										.setParameter("rolID", role.getId())							
						.getResultList().size();
				/* Validacion que hubo que quitar
				if(a!=0){
						facesMessages.add(
								  "Ya existe un usuario en el estudio: "+ estudioEntidad.getEstudio().getNombre()
								+ " con el rol: " + role.getName()
								+ " en la entidad: " + estudioEntidad.getEntidad().getNombre());
						continue;
				}*/
				
				
				
				UsuarioEstudio_ensayo  usuarioEstudio= new UsuarioEstudio_ensayo();
				
								
				usuario = entityManager.find(Usuario_ensayo.class, usuario.getId());
				usuarioEstudio.setUsuario(usuario);
				
				role = entityManager.find(Role_ensayo.class, role.getId());
				usuarioEstudio.setRole(role);
				
				//Cargado en cada iteracion
				usuarioEstudio.setEstudioEntidad(estudioEntidad);
		       
				usuarioEstudio.setEliminado(false);
				
				usuarioEstudio.setCid(cid);
				
				entityManager.persist(usuarioEstudio);
				entityManager.flush();
				
			}
			
			

		} else {
			facesMessages
					.add("Operacion no realizada FALTAN DATOS  Entidad -- ROL -- Usuario");
			return "error";
		}

		SourceLoad();
		return "ok";
	}
}
