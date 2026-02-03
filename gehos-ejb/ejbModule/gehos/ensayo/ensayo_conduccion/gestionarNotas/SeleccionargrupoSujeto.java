package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import javax.faces.model.SelectItem;
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

@Name("seleccionargrupoSujeto")
@Scope(ScopeType.CONVERSATION)
public class SeleccionargrupoSujeto{
	
	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;	
	protected @In(create=true) FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;
	@In
	private IActiveModule activeModule;
	
	private long idGrupo;
	private String nombreGrupo;
	
	
	private Long cid;
	
	public SeleccionargrupoSujeto(){}
	
	public void inicializarNota(){
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
	}
	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua",user.getId())
				.getSingleResult();
		
		return rol;
	}
	
	@SuppressWarnings("unchecked")
	public List ValoresRadio() {
		List<GrupoSujetos_ensayo> lista = new ArrayList<GrupoSujetos_ensayo>();
		List listaRadio = new ArrayList();
		lista = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select grupo from GrupoSujetos_ensayo grupo where grupo.estudio=:estudio and grupo.habilitado = true and grupo.eliminado=false")
				.setParameter("estudio", this.seguridadEstudio.getEstudioEntidadActivo().getEstudio()).getResultList();
		for (int i = 0; i < lista.size(); i++) {
			listaRadio.add(new SelectItem(lista.get(i).getId(), lista.get(i)
					.getNombreGrupo()));
		}
		return listaRadio;
	}
	
	
	public void crear(){
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

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	
}