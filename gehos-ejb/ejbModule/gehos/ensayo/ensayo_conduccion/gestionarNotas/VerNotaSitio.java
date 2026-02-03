//CU 32 Visualizar detalles de nota de sitio
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

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

@Name("verNotaSitio")
@Scope(ScopeType.CONVERSATION)
public class VerNotaSitio {

	protected @In EntityManager entityManager;	
	protected @In(create=true) FacesMessages facesMessages;	
	@In private Usuario user;
	@In(scope = ScopeType.CONVERSATION, value = "gestionarHoja") GestionarHoja parentController;
	
	private Nota_ensayo notaSitio;
	private String from = "";
	private Long idSujeto, idNota, idcrd, idMS, idGrupo;
	
	private Long sectionId;
	private Long groupId;
	private Integer repetition;
	private Long variableId;
	private Object variableData = null;
	private boolean inicializado = false;
	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void inicializarNota(){
		this.notaSitio = null;
		if(this.sectionId == null || this.variableId == null)
			this.notaSitio = this.entityManager.find(Nota_ensayo.class, this.idNota);
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
			this.notaSitio = ((itemData != null) ? itemData.getNoteSite() : null);
			this.variableData = ((itemData != null) ? ((this.notaSitio != null && this.notaSitio.getVariableDato() != null && this.notaSitio.getVariableDato().getValor() != null && !this.notaSitio.getVariableDato().getValor().isEmpty()) ? this.notaSitio.getVariableDato().getValor() : itemData.getValue()) : null);
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
		this.notaSitio = null;
	}
	
	public boolean fromManagedCrd(){
		return (this.sectionId != null && this.variableId != null);
	}
	
	private boolean validId(Object id){
		return (id != null && id.toString() != null && !id.toString().isEmpty() && !id.toString().equals("0"));
	}
	
	@End 
	public void salir(){}
	
	@Remove @Destroy
	public void destroy(){}
	
	public VariableDato_ensayo obtenerValorVariable(){
		VariableDato_ensayo variableDato = new VariableDato_ensayo();
		try {
			variableDato = (VariableDato_ensayo) this.entityManager.createQuery("select variableD from VariableDato_ensayo variableD where (variableD.eliminado = null or variableD.eliminado = false) and variableD.variable.id = :variableId and variableD.crdEspecifico.id = :hojaId").setParameter("variableId", this.notaSitio.getVariable().getId()).setParameter("hojaId", this.notaSitio.getCrdEspecifico().getId()).getSingleResult();
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

	public Nota_ensayo getNotaSitio(){
		return notaSitio;
	}

	public void setNotaSitio(Nota_ensayo notaSitio){
		this.notaSitio = notaSitio;
	}

	public Long getIdNota(){
		return idNota;
	}

	public Long getIdMS(){
		return idMS;
	}

	public void setIdMS(Long idMS){
		this.idMS = idMS;
	}

	public void setIdNota(Long idNota){
		this.idNota = idNota;
	}

	public Usuario getUser(){
		return user;
	}

	public void setUser(Usuario user){
		this.user = user;
	}

	public Long getIdcrd(){
		return idcrd;
	}

	public Long getIdGrupo(){
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo){
		this.idGrupo = idGrupo;
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
	
}