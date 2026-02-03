//CU 28 Responder nota de monitoreo
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import javax.persistence.EntityManager;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("responderNotaMonitoreo")
@Scope(ScopeType.CONVERSATION)
public class ResponderNotaMonitoreo {

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	protected @In(create = true) FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In private Usuario user;
	@In private IActiveModule activeModule;
	@In(scope = ScopeType.CONVERSATION, value = "gestionarHoja") GestionarHoja parentController;
	
	private Nota_ensayo primaryNote;
	private Nota_ensayo responseNote;
	private String estado;
	private String from, desde;
	private long id;
	private boolean estadoRequired = false;
	private long idGrupo, idNota, idSujeto, idcrd, idMS;
	private Role_ensayo rolLogueado;
	private Long sectionId;
	private Long groupId;
	private Integer repetition;
	private Long variableId;
	private boolean inicializado = false;
	private Long cid;

	public ResponderNotaMonitoreo(){}
	
	private boolean validId(Object id){
		return (id != null && id.toString() != null && !id.toString().isEmpty() && !id.toString().equals("0"));
	}
	
	public String assignValues(Long sectionId, Long groupId, Integer repetition, Long variableId){
		if(this.validId(sectionId) && this.validId(groupId) && this.validId(repetition) && this.validId(variableId)){
			this.sectionId = sectionId;
			this.groupId = groupId;
			this.variableId = variableId;
			this.repetition = repetition;			
			this.inicializado = false;
			return "access"; 
		} else
			return null;
	}
	
	public String assignValues(Long sectionId, Long variableId){
		if(this.validId(sectionId) && this.validId(variableId)){
			this.sectionId = sectionId;
			this.groupId = null;
			this.variableId = variableId;
			this.repetition = null;
			this.inicializado = false;
			return "access"; 
		} else
			return null;
	}

	public void inicializarNota(){
		this.rolLogueado = null;
		this.responseNote = new Nota_ensayo();
		if(this.sectionId == null || this.variableId == null)
			this.primaryNote = this.entityManager.find(Nota_ensayo.class, this.idNota);
		else if(this.sectionId != null && this.variableId != null){
			MapWrapperDataPlus itemData = null;
			if(this.repetition == null)
				itemData = ((this.parentController.getMapWD() != null && this.parentController.getMapWD().containsKey(this.sectionId) && this.parentController.getMapWD().get(this.sectionId).getData().containsKey(this.variableId)) ? this.parentController.getMapWD().get(this.sectionId).getData().get(this.variableId) : null);											
			else{
				String tempKey = this.parentController.convertKey(this.sectionId, this.groupId);
				if(this.parentController.getMapWGD() != null && this.parentController.getMapWGD().containsKey(tempKey)){
					int index = this.parentController.indexOfGroupData(this.parentController.getMapWGD().get(tempKey), this.repetition);
					itemData = ((index != -1) ? this.parentController.getMapWGD().get(tempKey).get(index).getData().get(this.variableId) : null);
				}
			}
			this.primaryNote = ((itemData != null) ? itemData.getNoteMonitoring() : null);
		}
		this.inicializado = true;		
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitResponder"));
		this.estadoRequired = true;
	}

	public Role_ensayo devolverRol(){
		if(this.rolLogueado == null)
			this.rolLogueado = (Role_ensayo) this.entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id = :estudId and usuario.id = :idusua and (usuarioE.eliminado = null or usuarioE.eliminado = false)").setParameter("estudId", this.seguridadEstudio.getEstudioEntidadActivo().getId()).setParameter("idusua", this.user.getId()).getSingleResult();
		return this.rolLogueado;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarEstados(){
		List<EstadoNota_ensayo> listaEstados = new ArrayList<EstadoNota_ensayo>();
		listaEstados = (List<EstadoNota_ensayo>) this.entityManager.createQuery("select est from EstadoNota_ensayo est ORDER BY est.id ASC").getResultList();
		List<String> nombres = new ArrayList<String>();
		for (int i = 0; i < listaEstados.size(); i++){
			if (listaEstados.get(i).getCodigo() != 2)
				nombres.add(listaEstados.get(i).getNombre());
		}
		return nombres;
	}
	
	public String crear(){
		String result = null;
		try {
			EstadoNota_ensayo estadoVieja = null;
			if (this.devolverRol().getCodigo().equals("ecCord") || this.devolverRol().getCodigo().equals("ecInv"))
				estadoVieja = (EstadoNota_ensayo) this.entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo = 2").getSingleResult();
			else if (this.devolverRol().getCodigo().equals("ecMon")){
				if(this.estado.equals("") && this.primaryNote != null)
					this.estado = this.primaryNote.getEstadoNota().getNombre();				
				if (this.estado.equals("Nueva") || this.estado.equals("Actualizada"))
					estadoVieja = (EstadoNota_ensayo) this.entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo = 1").getSingleResult();
				else if (this.estado.equals("Resuelta"))
					estadoVieja = (EstadoNota_ensayo) this.entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo = 3").getSingleResult();
				else if (this.estado.equals("Cerrada"))
					estadoVieja = (EstadoNota_ensayo) this.entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo = 4").getSingleResult();
			}
			if(estadoVieja != null)
				this.primaryNote.setEstadoNota(estadoVieja);
			EstadoNota_ensayo nueva = (EstadoNota_ensayo) this.entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo = 1").getSingleResult();
			this.responseNote.setEstadoNota(nueva);
			this.responseNote.setCid(this.cid);
			this.responseNote.setFechaCreacion(Calendar.getInstance().getTime());
			this.responseNote.setEliminado(false);
			this.responseNote.setNotaSitio(false);
			Usuario_ensayo usuario = this.entityManager.find(Usuario_ensayo.class, this.user.getId());
			this.responseNote.setUsuario(usuario);
			this.responseNote.setNotaPadre((this.primaryNote != null) ? this.primaryNote : null);
			this.responseNote.setCrdEspecifico((this.primaryNote != null) ? this.primaryNote.getCrdEspecifico() : null);
			this.responseNote.setVariable((this.primaryNote != null) ? this.primaryNote.getVariable() : null);
			if(this.sectionId == null || this.variableId == null){
				this.entityManager.persist(this.responseNote);
				if(this.primaryNote != null)
					this.primaryNote = this.entityManager.merge(this.primaryNote);
				this.entityManager.flush();
				result = "access";
			} else if(this.sectionId != null && this.variableId != null){
				if(this.repetition == null){
					MapWrapperDataPlus itemData = ((this.parentController.getMapWD() != null && this.parentController.getMapWD().containsKey(this.sectionId) && this.parentController.getMapWD().get(this.sectionId).getData().containsKey(this.variableId)) ? this.parentController.getMapWD().get(this.sectionId).getData().get(this.variableId) : null);
					if(itemData != null){
						this.responseNote.setVariableDato(itemData.getVariableData());
						itemData.addNoteResponse(this.responseNote, false);								
						this.parentController.getMapWD().get(this.sectionId).getData().put(this.variableId, itemData);
						result = "access";
					}							
				} else{
					String tempKey = this.parentController.convertKey(this.sectionId, this.groupId);
					if(this.parentController.getMapWGD() != null && this.parentController.getMapWGD().containsKey(tempKey)){
						int index = this.parentController.indexOfGroupData(this.parentController.getMapWGD().get(tempKey), this.repetition);
						MapWrapperDataPlus itemData = ((index != -1) ? this.parentController.getMapWGD().get(tempKey).get(index).getData().get(this.variableId) : null);
						if(itemData != null){
							this.responseNote.setVariableDato(itemData.getVariableData());
							itemData.addNoteResponse(this.responseNote, false);						
							this.parentController.getMapWGD().get(tempKey).get(index).getData().put(this.variableId, itemData);
							result = "access";
						}
					}
				}
			}
			this.cancelar();
			return result;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void cancelar(){
		this.sectionId = null;
		this.groupId = null;
		this.variableId = null;
		this.repetition = null;
		this.inicializado = false;
		this.primaryNote = null;
		this.responseNote = null;
	}

	public long getIdSujeto(){
		return idSujeto;
	}

	public void setIdSujeto(long idSujeto){
		this.idSujeto = idSujeto;
	}

	public Nota_ensayo getPrimaryNote(){
		return primaryNote;
	}
	
	public void setPrimaryNote(Nota_ensayo primaryNote){
		this.primaryNote = primaryNote;
	}
	
	public Nota_ensayo getResponseNote(){
		return responseNote;
	}
	
	public void setResponseNote(Nota_ensayo responseNote){
		this.responseNote = responseNote;
	}

	public long getId(){
		return id;
	}

	public void setId(long id){
		this.id = id;
	}

	public Long getCid(){
		return cid;
	}

	public void setCid(Long cid){
		this.cid = cid;
	}

	public long getIdGrupo(){
		return idGrupo;
	}

	public void setIdGrupo(long idGrupo){
		this.idGrupo = idGrupo;
	}

	public long getIdNota(){
		return idNota;
	}

	public void setIdNota(long idNota){
		this.idNota = idNota;
	}

	public boolean isEstadoRequired(){
		return estadoRequired;
	}

	public void setEstadoRequired(boolean estadoRequired){
		this.estadoRequired = estadoRequired;
	}

	public String getEstado(){
		return estado;
	}

	public void setEstado(String estado){
		this.estado = estado;
	}

	public long getIdcrd(){
		return idcrd;
	}

	public void setIdcrd(long idcrd){
		this.idcrd = idcrd;
	}

	public long getIdMS(){
		return idMS;
	}

	public void setIdMS(long idMS){
		this.idMS = idMS;
	}

	public String getFrom(){
		return from;
	}

	public void setFrom(String from){
		this.from = from;
	}

	public String getDesde(){
		return desde;
	}

	public void setDesde(String desde){
		this.desde = desde;
	}
	
	public boolean isInicializado(){
		return inicializado;
	}

	public void setInicializado(boolean inicializado){
		this.inicializado = inicializado;
	}

}