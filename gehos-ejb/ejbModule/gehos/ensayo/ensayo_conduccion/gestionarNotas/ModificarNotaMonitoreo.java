package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

@Name("modificarNotaMonitoreo")
@Scope(ScopeType.CONVERSATION)
public class ModificarNotaMonitoreo {

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
	private boolean estadoRequired = false;
	private Nota_ensayo nota;
	private String estado;
	private long id;
	private long idNota;
	private long idSujeto, idGrupo;

	private Long cid;
	private Boolean notaSitio;

	public ModificarNotaMonitoreo() {
	}

	public void inicializarNota() {
		this.nota = entityManager.find(Nota_ensayo.class, idNota);
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitModificarNotaMon"));
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

	public void modificar() {
		if(estado.equals("")){
			estado = nota.getEstadoNota().getNombre();
		}
		EstadoNota_ensayo estadoVieja = null;
		if (DevolverRol().getCodigo().equals("ecMon")) {
			if (estado.equals("Nueva")) {
				estadoVieja = (EstadoNota_ensayo) entityManager
						.createQuery(
								"select e from EstadoNota_ensayo e where e.codigo=1")
						.getSingleResult();
			} else if (estado.equals("Resuelta")) {
				estadoVieja = (EstadoNota_ensayo) entityManager
						.createQuery(
								"select e from EstadoNota_ensayo e where e.codigo=3")
						.getSingleResult();
			} else {
				estadoVieja = (EstadoNota_ensayo) entityManager
						.createQuery(
								"select e from EstadoNota_ensayo e where e.codigo=4")
						.getSingleResult();
			}
		}
		nota.setEstadoNota(estadoVieja);
		nota.setCid(cid);
		nota.setFechaActualizacion(Calendar.getInstance().getTime());
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
				this.user.getId());
		nota.setUsuario(usuario);
		entityManager.persist(nota);
		entityManager.flush();

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

	public Boolean getNotaSitio() {
		return notaSitio;
	}

	public void setNotaSitio(Boolean notaSitio) {
		this.notaSitio = notaSitio;
	}

	public long getIdNota() {
		return idNota;
	}

	public long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public void setIdNota(long idNota) {
		this.idNota = idNota;
	}

	public long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(long idSujeto) {
		this.idSujeto = idSujeto;
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

}