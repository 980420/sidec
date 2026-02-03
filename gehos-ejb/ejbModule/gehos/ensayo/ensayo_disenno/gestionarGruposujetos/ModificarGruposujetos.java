package gehos.ensayo.ensayo_disenno.gestionarGruposujetos;

import javax.persistence.EntityManager;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;

import java.util.Hashtable;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import java.util.ArrayList;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarGruposujetos")
@Scope(ScopeType.CONVERSATION)
public class ModificarGruposujetos {

	private GrupoSujetos_ensayo gruposujetos = new GrupoSujetos_ensayo();
	@In
	EntityManager entityManager;
	@In
	Usuario user;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	int error = -1;
	private Long cid = -1l;
	Long idGruposujetos;
	EstadoGruposujeto_ensayo estados_GS = new EstadoGruposujeto_ensayo();
	String desde = "";
	private String estadoGS = "";
	private String nombreGS;
	private String descripcionGS;
	private Date fechaCreacionGS;
	private Date fechaActualizacionGS;
	private List<GrupoSujetos_ensayo> listarGrupo;

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void cargarGruposujetos() {
		try {
			this.gruposujetos = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", this.idGruposujetos)
					.getSingleResult();
			nombreGS = gruposujetos.getNombreGrupo();
			descripcionGS = gruposujetos.getDescripcion();
			fechaCreacionGS = gruposujetos.getFechaCreacion();
			error = 0;
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("addGruposujetos",
					Severity.ERROR, "errorinesperado");
			error = 1;
		}
	}

	@End
	public void end() {
	}

	@Transactional
	public void adicionarGruposujetos() {
		try {
			if (!nombreGS.equals(gruposujetos.getNombreGrupo())) {

				listarGrupo = (List<GrupoSujetos_ensayo>) entityManager
						.createQuery(
								"select g from GrupoSujetos_ensayo g where g.nombreGrupo=:idGSujeto and g.estudio.id=:estud and g.eliminado = false")
						.setParameter("idGSujeto",
								gruposujetos.getNombreGrupo().trim())
						.setParameter(
								"estud",
								seguridadEstudio.getEstudioEntidadActivo()
										.getEstudio().getId()).getResultList();

				if (!listarGrupo.isEmpty()) {
					facesMessages.addToControlFromResourceBundle("message",
							Severity.INFO, "msg_gsExiste1");
					error = 1;
				} else {
					gruposujetos.setFechaActualizacion(Calendar.getInstance()
							.getTime());
					Usuario_ensayo usuario = entityManager.find(
							Usuario_ensayo.class, user.getId());
					gruposujetos.setUsuario(usuario);
					entityManager.persist(gruposujetos);
					entityManager.flush();
					idGruposujetos = gruposujetos.getId();
					error = 0;
				}
			} else {
				gruposujetos.setFechaActualizacion(Calendar.getInstance()
						.getTime());
				entityManager.persist(gruposujetos);
				entityManager.flush();
				idGruposujetos = gruposujetos.getId();
				error = 0;
			}
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("addGruposujetos",
					Severity.ERROR, "errorinesperado");
			error = 1;
		}
	}

	public GrupoSujetos_ensayo getGruposujetos() {
		return gruposujetos;
	}

	public void setGruposujetos(GrupoSujetos_ensayo gruposujetos) {
		this.gruposujetos = gruposujetos;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Long getIdGruposujetos() {
		return idGruposujetos;
	}

	public void setIdGruposujetos(Long idGruposujetos) {
		this.idGruposujetos = idGruposujetos;
	}

	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}

	public String getNombreGS() {
		return nombreGS;
	}

	public void setNombreGS(String nombreGS) {
		this.nombreGS = nombreGS;
	}

	public String getDescripcionGS() {
		return descripcionGS;
	}

	public void setDescripcionGS(String descripcionGS) {
		this.descripcionGS = descripcionGS;
	}

	public Date getFechaCreacionGS() {
		return fechaCreacionGS;
	}

	public void setFechaCreacionGS(Date fechaCreacionGS) {
		this.fechaCreacionGS = fechaCreacionGS;
	}

	public Date getFechaActualizacionGS() {
		return fechaActualizacionGS;
	}

	public void setFechaActualizacionGS(Date fechaActualizacionGS) {
		this.fechaActualizacionGS = fechaActualizacionGS;
	}

	protected List<GrupoSujetos_ensayo> getListarGrupo() {
		return listarGrupo;
	}

	protected void setListarGrupo(List<GrupoSujetos_ensayo> listarGrupo) {
		this.listarGrupo = listarGrupo;
	}
}