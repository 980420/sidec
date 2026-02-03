//CU 32 Visualizar detalles de nota de sitio
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("verNotaMonitoreo")
@Scope(ScopeType.CONVERSATION)
public class VerNotaMonitoreo {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	@In private Usuario user;
	@In(scope = ScopeType.CONVERSATION, value = "gestionarHoja") GestionarHoja parentController;	
	
	private Nota_ensayo notaMonitoreo;
	private String from = "";
	private Long idSujeto, idNota, idcrd, idMS,idGrupo;
	
	private Long sectionId;
	private Long groupId;
	private Integer repetition;
	private Long variableId;
	private Object variableData = null;
	private boolean inicializado = false;

	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void inicializarNota(){
		this.notaMonitoreo = null;
		if(this.sectionId == null || this.variableId == null)
			this.notaMonitoreo = this.entityManager.find(Nota_ensayo.class, this.idNota);
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
			this.notaMonitoreo = ((itemData != null) ? itemData.getNoteMonitoring() : null);
			this.variableData = ((itemData != null) ? ((this.notaMonitoreo != null && this.notaMonitoreo.getVariableDato() != null && this.notaMonitoreo.getVariableDato().getValor() != null && !this.notaMonitoreo.getVariableDato().getValor().isEmpty()) ? this.notaMonitoreo.getVariableDato().getValor() : itemData.getValue()) : null);
		}
		this.inicializado = true;
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
	
	public void cancelar(){
		this.sectionId = null;
		this.groupId = null;
		this.variableId = null;
		this.repetition = null;
		this.variableData = null;
		this.inicializado = false;
		this.notaMonitoreo = null;
	}
	
	public boolean fromManagedCrd(){
		return (this.sectionId != null && this.variableId != null);
	}
	
	public boolean enabledGroups(){
		return (this.fromManagedCrd() && this.groupId != null && this.repetition != null);
	}
	
	private boolean validId(Object id){
		return (id != null && id.toString() != null && !id.toString().isEmpty() && !id.toString().equals("0"));
	}
	
	@End 
	public void salir(){}
	
	@Remove @Destroy
	public void destroy(){}
	
	private List<Nota_ensayo> loadUnsavedRelatedNotes(){
		MapWrapperDataPlus itemData = null;
		if(this.enabledGroups()){
			String tempKey = this.parentController.convertKey(this.sectionId, this.groupId);
			if(this.parentController.getMapWGD() != null && this.parentController.getMapWGD().containsKey(tempKey)){
				int index = this.parentController.indexOfGroupData(this.parentController.getMapWGD().get(tempKey), this.repetition);
				itemData = ((index != -1) ? this.parentController.getMapWGD().get(tempKey).get(index).getData().get(this.variableId) : null);
			}
		} else if(this.fromManagedCrd())
			itemData = ((this.parentController.getMapWD() != null && this.parentController.getMapWD().containsKey(this.sectionId) && this.parentController.getMapWD().get(this.sectionId).getData().containsKey(this.variableId)) ? this.parentController.getMapWD().get(this.sectionId).getData().get(this.variableId) : null);
		return ((itemData != null) ? itemData.getNoteMonitoringResponses() : new ArrayList<Nota_ensayo>());
	}
	
	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> devolverNotaMonitoreoAsociadas(Nota_ensayo nota){
		List<Nota_ensayo> listaHijas = (List<Nota_ensayo>) this.entityManager.createQuery("select nota from Nota_ensayo nota where nota.notaPadre.id = :notaPadreId and nota.notaSitio = false and (nota.eliminado = null or nota.eliminado = false) ORDER BY nota.id DESC").setParameter("notaPadreId", nota.getId()).getResultList();
		if(this.enabledGroups() || this.fromManagedCrd()){
			List<Nota_ensayo> temp = this.loadUnsavedRelatedNotes();
			if(temp != null){
				for (Nota_ensayo itemNote : temp)				
					listaHijas.add(0, itemNote);	
			}	
		}
		return listaHijas;
	}
	
	public VariableDato_ensayo obtenerValorVariable(){
		VariableDato_ensayo variableDato = new VariableDato_ensayo();
		try {
			variableDato = (VariableDato_ensayo) this.entityManager.createQuery("select variableD from VariableDato_ensayo variableD where (variableD.eliminado = null or variableD.eliminado = false) and variableD.variable.id = :variableId and variableD.crdEspecifico.id = :hojaId").setParameter("variableId", this.notaMonitoreo.getVariable().getId()).setParameter("hojaId", this.notaMonitoreo.getCrdEspecifico().getId()).getSingleResult();
		} catch (Exception e){
			return null;
		}
		return variableDato;
	}
	
	public Long getIdSujeto(){
		return idSujeto;
	}
	
	public void setIdSujeto(Long idSujeto){
		this.idSujeto = idSujeto;
	}

	public Long getIdNota(){
		return idNota;
	}

	public void setIdNota(Long idNota){
		this.idNota = idNota;
	}

	public Usuario getUser(){
		return user;
	}

	public Long getIdMS(){
		return idMS;
	}

	public void setIdMS(Long idMS){
		this.idMS = idMS;
	}

	public void setUser(Usuario user){
		this.user = user;
	}

	public Long getIdGrupo(){
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo){
		this.idGrupo = idGrupo;
	}

	public Long getIdcrd(){
		return idcrd;
	}

	public Nota_ensayo getNotaMonitoreo(){
		return notaMonitoreo;
	}

	public void setNotaMonitoreo(Nota_ensayo notaMonitoreo){
		this.notaMonitoreo = notaMonitoreo;
	}

	public void setIdcrd(Long idcrd){
		this.idcrd = idcrd;
	}

	public String getFrom(){
		return from;
	}

	public void setFrom(String from){
		this.from = from;
	}
	
	public boolean isInicializado(){
		return inicializado;
	}

	public void setInicializado(boolean inicializado){
		this.inicializado = inicializado;
	}
	
	public Object getVariableData(){
		return variableData;
	}
	
	public void setVariableData(Object variableData){
		this.variableData = variableData;
	}
	
	public Long getSectionId(){
		return sectionId;
	}
	
	public void setSectionId(Long sectionId){
		this.sectionId = sectionId;
	}
	
	public Long getGroupId(){
		return groupId;
	}
	
	public void setGroupId(Long groupId){
		this.groupId = groupId;
	}
	
	public Integer getRepetition(){
		return repetition;
	}
	
	public void setRepetition(Integer repetition){
		this.repetition = repetition;
	}
	
	public Long getVariableId(){
		return variableId;
	}
	
	public void setVariableId(Long variableId){
		this.variableId = variableId;
	}
	
}