package gehos.ensayo.ensayo_disenno.gestionarGruposujetos;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.EstadoGruposujeto_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

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
import org.jboss.seam.international.StatusMessage.Severity;

@Name("adicionarGruposujetos")
@Scope(ScopeType.CONVERSATION)
public class AdicionarGruposujetos {

	@In
	EntityManager entityManager;
	@In
	Usuario user;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;

	private GrupoSujetos_ensayo gruposujetos;
	String nombreGS, descripcionGS;
	Date fechaCreacionGS, fechaActualizacionGS;
	protected List<GrupoSujetos_ensayo> listarGrupo;
	int error = -1;
	Long idGruposujetos;
	EstadoGruposujeto_ensayo estados_GS = new EstadoGruposujeto_ensayo();

	// Methods----------------------------------------------------
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
		try {
			gruposujetos = new GrupoSujetos_ensayo();
			nombreGS = "";
			descripcionGS = "";
			fechaActualizacionGS = new Date();
			fechaCreacionGS = new Date();
			fechaCreacionGS = Calendar.getInstance().getTime();
			error = 0;
			this.end();

		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}

	}

	@End
	public void end() {
	}
	private boolean submitAttempted = false;
	@Transactional
	public void adicionar() {
		this.submitAttempted = true;
		try {
			listarGrupo = (List<GrupoSujetos_ensayo>) entityManager
					.createQuery(
							"select g from GrupoSujetos_ensayo g where g.nombreGrupo=:idGSujeto and g.estudio.id=:estud and g.eliminado = false")
					.setParameter("idGSujeto", gruposujetos.getNombreGrupo().trim())
					.setParameter("estud",
							seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
					.getResultList();
			
			if (!listarGrupo.isEmpty()) {
				facesMessages.addToControlFromResourceBundle("message",
						Severity.INFO, "msg_gsExiste1");
				error = 1;

			} else {
				estados_GS = (EstadoGruposujeto_ensayo) entityManager
						.createQuery(
								"select estados from EstadoGruposujeto_ensayo estados where estados.codigo ='1'")
						.getSingleResult();
				gruposujetos.setEstudio(seguridadEstudio
						.getEstudioEntidadActivo().getEstudio());
				gruposujetos.setEstadoGruposujeto(estados_GS);
				gruposujetos.setEliminado(false);
				gruposujetos.setHabilitado(true);
				gruposujetos.setFechaCreacion(fechaCreacionGS);
				Usuario_ensayo usuario = entityManager.find(
						Usuario_ensayo.class, user.getId());
				gruposujetos.setUsuario(usuario);
				gruposujetos.setCid(bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("bitcreandoGS_enDis")));
				entityManager.persist(gruposujetos);
				entityManager.flush();
				cambiarEstadoestudio();
				idGruposujetos = gruposujetos.getId();
				facesMessages.addToControlFromResourceBundle("message",
						Severity.INFO, "msg_addGS_enDis");

				error = 0;

			}

		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("addGruposujetos",
					Severity.ERROR, "errorinesperado");
			error = 1;
		}
	}
	public boolean isSubmitAttempted() {
        return this.submitAttempted;
    }
	// Cambia de estado el estudio de creado a en diseño al adicionar el primer
	// Grupo de sujeto
	public void cambiarEstadoestudio() {
		EstadoEstudio_ensayo estadoEstudio = (EstadoEstudio_ensayo) entityManager
				.createQuery(
						"select e from EstadoEstudio_ensayo e where e.codigo='2'")
				.getSingleResult();
		Estudio_ensayo estudio = seguridadEstudio.getEstudioEntidadActivo()
				.getEstudio();
		if (estudio.getGrupoSujetoses().size() > 0) {
			estudio.setEstadoEstudio(estadoEstudio);
			entityManager.persist(estudio);
			entityManager.flush();
		}
	}
	
	// Properties--------------------------------------------------
	public EstadoGruposujeto_ensayo getEstados() {
		return estados_GS;
	}

	public void setEstados(EstadoGruposujeto_ensayo estados_GS) {
		this.estados_GS = estados_GS;
	}

	public List<GrupoSujetos_ensayo> getListarGrupo() {
		return listarGrupo;
	}

	public void setListarGrupo(List<GrupoSujetos_ensayo> listarGrupo) {
		this.listarGrupo = listarGrupo;
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

	public String getNombreGS() {
		return nombreGS;
	}

	public void setNombreGS(String nombreGS) {
		this.nombreGS = nombreGS;
	}
}