//CU 28 Responder nota de monitoreo
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.List;
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

@Name("responderNotaGeneral")
@Scope(ScopeType.CONVERSATION)
public class ResponderNotaGeneral {

	protected @In
	EntityManager entityManager;
	protected @In
	IBitacora bitacora;
	protected @In(create = true)
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;
	@In
	private IActiveModule activeModule;

	private NotaGeneral_ensayo nota;
	private NotaGeneral_ensayo notaNueva;
	private long id;
	private long idGrupo, idNota;

	private Long cid;

	public ResponderNotaGeneral() {
	}

	public void inicializarNota() {
		this.nota = entityManager.find(NotaGeneral_ensayo.class, idNota);
		this.notaNueva = new NotaGeneral_ensayo();
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitResponder"));
	}

	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}

	public void crear() {
		try {
			EstadoNota_ensayo nueva = (EstadoNota_ensayo) entityManager
					.createQuery(
							"select e from EstadoNota_ensayo e where e.codigo=1")
					.getSingleResult();
			EstadoNota_ensayo actualizada = (EstadoNota_ensayo) entityManager
					.createQuery(
							"select e from EstadoNota_ensayo e where e.codigo=2")
					.getSingleResult();

			notaNueva.setEstadoNota(nueva);
			notaNueva.setCid(cid);
			notaNueva.setFechaCreacion(Calendar.getInstance().getTime());
			notaNueva.setEliminado(false);
			notaNueva.setNotaGeneralPadre(nota);
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					this.user.getId());
			notaNueva.setUsuario(usuario);
			notaNueva.setEntidad(nota.getEntidad());
			entityManager.persist(notaNueva);
			notaNueva.getNotaGeneralPadre().setEstadoNota(actualizada);
			entityManager.persist(notaNueva.getNotaGeneralPadre());
			entityManager.flush();
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}

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

	public long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public long getIdNota() {
		return idNota;
	}

	public void setIdNota(long idNota) {
		this.idNota = idNota;
	}

	public NotaGeneral_ensayo getNotaNueva() {
		return notaNueva;
	}

	public void setNotaNueva(NotaGeneral_ensayo notaNueva) {
		this.notaNueva = notaNueva;
	}

	public NotaGeneral_ensayo getNota() {
		return nota;
	}

	public void setNota(NotaGeneral_ensayo nota) {
		this.nota = nota;
	}

}