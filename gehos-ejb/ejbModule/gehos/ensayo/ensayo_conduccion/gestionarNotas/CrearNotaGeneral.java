//CU 22 Crear nota general
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.Calendar;

import javax.persistence.EntityManager;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("crearNotaGeneral")
@Scope(ScopeType.CONVERSATION)
public class CrearNotaGeneral{
	
	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;	
	protected @In(create=true) FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;
	@In
	private IActiveModule activeModule;
	
	private NotaGeneral_ensayo nota;
	private long id;
	private long idGrupo;
	
	private Long cid;
	
	public CrearNotaGeneral(){}
	
	public void inicializarNota(){
		this.nota = new NotaGeneral_ensayo();
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrearNotaGeneral"));
	}
	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua",user.getId())
				.getSingleResult();
		
		return rol;
	}
	
	
	
	
	public void crear(){
		try {
			EstadoNota_ensayo nueva = (EstadoNota_ensayo) entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo=1").getSingleResult();
			nota.setEstadoNota(nueva);
			nota.setCid(cid);
			nota.setFechaCreacion(Calendar.getInstance().getTime());
			nota.setEliminado(false);
			Entidad_ensayo entidad = entityManager.find(Entidad_ensayo.class , this.activeModule.getActiveModule().getEntidad().getId());
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class , this.user.getId());
			GrupoSujetos_ensayo grupo = entityManager.find(GrupoSujetos_ensayo.class , this.idGrupo);
			nota.setUsuario(usuario);
			nota.setEntidad(entidad);
			nota.setGrupoSujetos(grupo);
			entityManager.persist(nota);
			entityManager.flush();
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
		
	}
	

	public NotaGeneral_ensayo getNota() {
		return nota;
	}

	public void setNota(NotaGeneral_ensayo nota) {
		this.nota = nota;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	

	public long getidGrupo() {
		return idGrupo;
	}

	public void setidGrupo(long idGrupo) {
		this.idGrupo = idGrupo;
	}
	
	
	
	
}