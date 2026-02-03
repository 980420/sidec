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

@Name("responderNotaMonitoreoListado")
@Scope(ScopeType.CONVERSATION)
public class ResponderNotaMonitoreoListado {

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	protected @In(create = true) FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;
	@In
	private IActiveModule activeModule;

	private Nota_ensayo nota;
	private Nota_ensayo notaNueva;
	private String estado;
	private String from, desde;
	private long id;
	private boolean estadoRequired = false;
	private long idGrupo, idNota, idSujeto, idcrd, idMS;

	private Long cid;

	public ResponderNotaMonitoreoListado() {
	}

	public void inicializarNota() {
		this.nota = entityManager.find(Nota_ensayo.class, idNota);
		this.notaNueva = new Nota_ensayo();
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitResponder"));
		this.estadoRequired = true;
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

	@SuppressWarnings("unchecked")
	public List<String> listarEstados() {
		List<EstadoNota_ensayo> listaEstados = new ArrayList<EstadoNota_ensayo>();
		listaEstados = (List<EstadoNota_ensayo>) entityManager.createQuery(
				"select est from EstadoNota_ensayo est ORDER BY est.id ASC").getResultList();
		List<String> nombres = new ArrayList<String>();
		for (int i = 0; i < listaEstados.size(); i++) {
			if (listaEstados.get(i).getCodigo() != 2) {
				nombres.add(listaEstados.get(i).getNombre());
			}
		}
		return nombres;
	}

	public void crear() {

		try {
			EstadoNota_ensayo estadoVieja = null;
			if (DevolverRol().getCodigo().equals("ecCord") || DevolverRol().getCodigo().equals("ecInv")) {
				estadoVieja = (EstadoNota_ensayo) entityManager.createQuery(
						"select e from EstadoNota_ensayo e where e.codigo=2")
						.getSingleResult();
			} else if (DevolverRol().getCodigo().equals("ecMon")) {
				if(estado.equals("")){
					estado = nota.getEstadoNota().getNombre();
				}
				if (estado.equals("Nueva") || estado.equals("Actualizada")) {
					estadoVieja = (EstadoNota_ensayo) entityManager
							.createQuery(
									"select e from EstadoNota_ensayo e where e.codigo=1")
							.getSingleResult();
				} else if (estado.equals("Resuelta")) {
					estadoVieja = (EstadoNota_ensayo) entityManager
							.createQuery(
									"select e from EstadoNota_ensayo e where e.codigo=3")
							.getSingleResult();
				} else if (estado.equals("Cerrada")){
					estadoVieja = (EstadoNota_ensayo) entityManager
							.createQuery(
									"select e from EstadoNota_ensayo e where e.codigo=4")
							.getSingleResult();
				}
			}
			nota.setEstadoNota(estadoVieja);
			entityManager.persist(nota);
			EstadoNota_ensayo nueva = (EstadoNota_ensayo) entityManager
					.createQuery(
							"select e from EstadoNota_ensayo e where e.codigo=1")
					.getSingleResult();
			notaNueva.setEstadoNota(nueva);
			notaNueva.setCid(cid);
			notaNueva.setFechaCreacion(Calendar.getInstance().getTime());
			notaNueva.setEliminado(false);
			notaNueva.setNotaSitio(false);
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					this.user.getId());
			notaNueva.setUsuario(usuario);
			notaNueva.setNotaPadre(nota);
			notaNueva.setCrdEspecifico(nota.getCrdEspecifico());
			notaNueva.setVariable(nota.getVariable());
			entityManager.persist(notaNueva);
			entityManager.flush();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public Nota_ensayo getNota() {
		return nota;
	}

	public void setNota(Nota_ensayo nota) {
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

	public Nota_ensayo getNotaNueva() {
		return notaNueva;
	}

	public void setNotaNueva(Nota_ensayo notaNueva) {
		this.notaNueva = notaNueva;
	}

	public boolean isEstadoRequired() {
		return estadoRequired;
	}

	public void setEstadoRequired(boolean estadoRequired) {
		this.estadoRequired = estadoRequired;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public long getIdcrd() {
		return idcrd;
	}

	public void setIdcrd(long idcrd) {
		this.idcrd = idcrd;
	}

	public long getIdMS() {
		return idMS;
	}

	public void setIdMS(long idMS) {
		this.idMS = idMS;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}

}