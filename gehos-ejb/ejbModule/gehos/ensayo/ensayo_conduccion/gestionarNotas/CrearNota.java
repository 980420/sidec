package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_conduccion.gestionarMS.WrapperMomento;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.EstadoNota_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("crearNota")
@Scope(ScopeType.CONVERSATION)
public class CrearNota{
	
	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;	
	protected @In(create=true) FacesMessages facesMessages;	
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;	
	@In private Usuario user;	
	@In private IActiveModule activeModule;
	@In(create = true) WrapperMomento wrapperMomento;	
	@In(scope = ScopeType.CONVERSATION, value = "gestionarHoja") GestionarHoja parentController;
	
	private Nota_ensayo nota;
	private long id;
	private String from = "";
	private long idVariable,idMS ,idSujeto;
	private long idHoja;
	private long posicion;
	private CrdEspecifico_ensayo hoja;
	private Variable_ensayo variable;
	
	private Long sectionId;
	private Long groupId;
	private Integer repetition;
	private Long variableId;
	private boolean site = false, monitoring = false;
			
	private Long cid;
	private Boolean notaSitio;
	private boolean inicializado = false;	
	
	private String tipoNota;
	private List<String> tiposNota; 
	
	public CrearNota(){}
	
	public String assignValuesSite(Long sectionId, Long groupId, Integer repetition, Long variableId){
		if(this.validId(sectionId) && this.validId(groupId) && this.validId(repetition) && this.validId(variableId)){
			this.assignValues(sectionId, groupId, repetition, variableId);			
			this.inicializado = false;
			this.site = true;
			this.monitoring = false;
			return "access"; 
		} else
			return null;
	}
	
	public String assignValuesMonitoring(Long sectionId, Long groupId, Integer repetition, Long variableId){
		if(this.validId(sectionId) && this.validId(groupId) && this.validId(repetition) && this.validId(variableId)){
			this.assignValues(sectionId, groupId, repetition, variableId);			
			this.inicializado = false;
			this.site = false;
			this.monitoring = true;
			return "access"; 
		} else
			return null;
	}
	
	public String assignValuesSite(Long sectionId, Long variableId){
		if(this.validId(sectionId) && this.validId(variableId)){
			this.assignValues(sectionId, variableId);
			this.inicializado = false;
			this.site = true;
			this.monitoring = false;
			return "access"; 
		} else
			return null;
	}
	
	public String assignValuesMonitoring(Long sectionId, Long variableId){
		if(sectionId != null && sectionId.toString() != null && !sectionId.toString().trim().isEmpty() && variableId != null && variableId.toString() != null && !variableId.toString().trim().isEmpty()){
			this.assignValues(sectionId, variableId);
			this.inicializado = false;
			this.site = false;
			this.monitoring = true;
			return "access"; 
		} else
			return null;
	}
	
	private void assignValues(Long sectionId, Long groupId, Integer repetition, Long variableId){
		this.sectionId = sectionId;
		this.groupId = groupId;
		this.variableId = variableId;
		this.repetition = repetition;
	}
	
	private void assignValues(Long sectionId, Long variableId){
		this.sectionId = sectionId;
		this.groupId = null;
		this.variableId = variableId;
		this.repetition = null;
	}
	
	private boolean validId(Object id){
		return (id != null && id.toString() != null && !id.toString().isEmpty() && !id.toString().equals("0"));
	}
	
	public void inicializarNota(){
		this.nota = new Nota_ensayo();
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
	}
	
	public Role_ensayo devolverRol(){
		Role_ensayo rol = (Role_ensayo) this.entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true").setParameter("estudId", this.seguridadEstudio.getEstudioEntidadActivo().getId()).setParameter("idusua", this.user.getId()).getSingleResult();
		return rol;
	}
	
	public String devolverTitulo(){
		String nombre = "";
		Role_ensayo rol = this.devolverRol();
		if(rol.getCodigo().equals("ecCord"))
			nombre = SeamResourceBundle.getBundle().getString("crearnotaSitio");
		else if(rol.getCodigo().equals("ecMon"))
			nombre = SeamResourceBundle.getBundle().getString("crearnotaMonitoreo");
		return nombre;
	}	
	
	public String crear(){
		String result = null;
		try {			
			Role_ensayo rol = this.devolverRol();
			if(rol.getCodigo().equals("ecCord") || rol.getCodigo().equals("ecInv"))
				this.nota.setNotaSitio(true);
			else if(rol.getCodigo().equals("ecMon")){
				this.nota.setNotaSitio(false);
				this.wrapperMomento.cambiarEstadoAMonitoreoIniciado(this.idHoja);
			}			
			EstadoNota_ensayo nueva = (EstadoNota_ensayo) this.entityManager.createQuery("select e from EstadoNota_ensayo e where e.codigo = 1").getSingleResult();
			this.nota.setEstadoNota(nueva);
			Variable_ensayo variable = ((this.variableId != null) ? this.entityManager.find(Variable_ensayo.class, this.variableId) : this.entityManager.find(Variable_ensayo.class, idVariable));
			CrdEspecifico_ensayo crd = this.entityManager.find(CrdEspecifico_ensayo.class, this.idHoja);
			this.nota.setVariable(variable);
			this.nota.setCrdEspecifico(crd);				
			this.nota.setCid(this.cid);
			this.nota.setFechaCreacion(Calendar.getInstance().getTime());
			Usuario_ensayo usuario = this.entityManager.find(Usuario_ensayo.class, this.user.getId());
			this.nota.setUsuario(usuario);
			this.nota.setEliminado(false);			
			if(this.sectionId == null || this.variableId == null){
				this.nota.setVariableDato(this.entityManager.find(VariableDato_ensayo.class, this.posicion));
				this.entityManager.persist(this.nota);
				this.entityManager.flush();
				result = "access";
			} else{
				if(this.site)
					this.nota.setNotaSitio(true);
				else if(this.monitoring)
					this.nota.setNotaSitio(false);				
				if(this.sectionId != null && this.variableId != null){		
					if(this.repetition == null){
						MapWrapperDataPlus itemData = ((this.parentController.getMapWD() != null && this.parentController.getMapWD().containsKey(this.sectionId) && this.parentController.getMapWD().get(this.sectionId).getData().containsKey(this.variableId)) ? this.parentController.getMapWD().get(this.sectionId).getData().get(this.variableId) : null);
						if(itemData != null){
							this.nota.setVariableDato(itemData.getVariableData());
							if(this.site)								
								itemData.setNoteSite(this.nota);
							else if(this.monitoring)								
								itemData.setNoteMonitoring(this.nota);							
							this.parentController.getMapWD().get(this.sectionId).getData().put(this.variableId, itemData);
							result = "access";
						}							
					} else{
						String tempKey = this.parentController.convertKey(this.sectionId, this.groupId);
						if(this.parentController.getMapWGD() != null && this.parentController.getMapWGD().containsKey(tempKey)){
							int index = this.parentController.indexOfGroupData(this.parentController.getMapWGD().get(tempKey), this.repetition);
							MapWrapperDataPlus itemData = ((index != -1) ? this.parentController.getMapWGD().get(tempKey).get(index).getData().get(this.variableId) : null);
							if(itemData != null){
								this.nota.setVariableDato(itemData.getVariableData());
								if(this.site)								
									itemData.setNoteSite(this.nota);
								else if(this.monitoring)								
									itemData.setNoteMonitoring(this.nota);							
								this.parentController.getMapWGD().get(tempKey).get(index).getData().put(this.variableId, itemData);
								result = "access";
							}
						}
					}					
				}
			}
			this.cancelar();
		} catch (Exception e){
			System.err.print(e.getMessage());
		}
		return result;
	}
	
	public void cancelar(){
		this.sectionId = null;
		this.groupId = null;
		this.variableId = null;
		this.repetition = null;
		this.inicializado = false;
		this.site = false;
		this.monitoring = false;
		this.hoja = null;
		this.variable = null;
	}
	
	public CrdEspecifico_ensayo devolverCRD(){
		if(this.hoja == null)
			this.hoja = this.entityManager.find(CrdEspecifico_ensayo.class, this.idHoja);
		return this.hoja;
	}
	
	public String devolverVariableNombre(){
		if(this.variable == null)
			this.variable = entityManager.find(Variable_ensayo.class, ((this.variableId != null) ? this.variableId : this.idVariable));
		return this.variable.getNombreVariable();
	}

	public long getIdSujeto(){
		return idSujeto;
	}

	public void setIdSujeto(long idSujeto){
		this.idSujeto = idSujeto;
	}

	public Nota_ensayo getNota(){
		return nota;
	}

	public void setNota(Nota_ensayo nota){
		this.nota = nota;
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

	public Boolean getNotaSitio(){
		return notaSitio;
	}

	public void setNotaSitio(Boolean notaSitio){
		this.notaSitio = notaSitio;
	}

	public String getTipoNota(){
		return tipoNota;
	}

	public long getIdVariable(){
		return idVariable;
	}

	public void setIdVariable(long idVariable){
		this.idVariable = idVariable;
	}

	public void setTipoNota(String tipoNota){
		this.tipoNota = tipoNota;
	}

	public List<String> getTiposNota(){
		return tiposNota;
	}

	public void setTiposNota(List<String> tiposNota){
		this.tiposNota = tiposNota;
	}

	public long getIdHoja(){
		return idHoja;
	}

	public void setIdHoja(long idHoja){
		this.idHoja = idHoja;
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

	public long getPosicion(){
		return posicion;
	}

	public void setPosicion(long posicion){
		this.posicion = posicion;
	}
	
	public boolean isInicializado(){
		return inicializado;
	}

	public void setInicializado(boolean inicializado){
		this.inicializado = inicializado;
	}	
	
}