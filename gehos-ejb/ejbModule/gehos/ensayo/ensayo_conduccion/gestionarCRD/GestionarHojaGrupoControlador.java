package gehos.ensayo.ensayo_conduccion.gestionarCRD;

import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Nomenclador_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;


@Name("gestionarHojaGrupoControlador_ec") // ec from ensayo conduccion
@Scope(ScopeType.CONVERSATION)
public class GestionarHojaGrupoControlador {
	
	private Seccion_ensayo section;
	private GrupoVariables_ensayo group;	
	private WrapperGroupData data;
	private List<Variable_ensayo> variables;
	private Long[][] indexes;
	private Integer repetition;
	private static final int columns = 3;
	private boolean editing = false;
	
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	
	@In EntityManager entityManager;
	
	@In FacesMessages facesMessages;
	
	@In(scope = ScopeType.CONVERSATION, value = "gestionarHoja")
	GestionarHoja parentController;
		
	public GestionarHojaGrupoControlador(){
		super();
	}
	
	private void reset(){
		this.section = null;
		this.group = null;
		this.data = null;
		this.variables = new ArrayList<Variable_ensayo>();
		this.editing = false;
		this.data = null;
		this.indexes = null;
		this.repetition = null;
	}
	
	@SuppressWarnings("unchecked")
	private void loadVariables(){
		try {
			this.variables = this.entityManager.createQuery("select variable from Variable_ensayo variable where (variable.eliminado = null or variable.eliminado = false) and variable.seccion.id = :idS and variable.grupoVariables.id = :idG ORDER BY variable.idClinica, variable.numeroPregunta ASC").setParameter("idS", this.section.getId()).setParameter("idG", this.group.getId()).getResultList();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void loadIndexes(){
		if(this.variables == null || this.variables.isEmpty())
			this.loadVariables();
		if(this.variables != null && !this.variables.isEmpty()){
			int rows = (this.variables.size() / columns);
			rows = (((this.variables.size() < columns) || ((this.variables.size() > columns) && (this.variables.size() % columns) != 0)) ? (rows + 1) : rows);
			this.indexes = new Long[rows][columns];
			List<Variable_ensayo> l = this.getVariables();
			int rIterator = 0, cIterator = 0;			
			for (Variable_ensayo itemVariable : l){
				if(this.data != null && this.data.getData() != null && this.data.getData().containsKey(itemVariable.getId())){
					if(rIterator < rows && cIterator < columns){
						this.indexes[rIterator][cIterator++] = itemVariable.getId();
						if(!(cIterator < columns)){
							rIterator++;
							cIterator = 0;
						}
					}
				}
			}
			while(cIterator < columns && (this.variables.size() % columns) != 0)
				this.indexes[rIterator][cIterator++] = null;
		}
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public String assignValues(Long sectionId, Long groupId, Integer repetition){
		
		if(!this.parentController.checkAllRequiredVariablesBeforeAddGroup().isEmpty()){
			this.facesMessages.addToControlFromResourceBundle("idForm",
					Severity.INFO, this.parentController.checkAllRequiredVariablesBeforeAddGroup());
			return null;
		}
		

		this.setRepetition(repetition);
		this.editing = (this.repetition != null && this.repetition.toString() != null && !this.repetition.toString().trim().isEmpty() && this.repetition > 0);
		if(sectionId != null && sectionId.toString() != null && !sectionId.toString().trim().isEmpty()){
			try {
				this.section = (Seccion_ensayo) this.entityManager.createQuery("select section from Seccion_ensayo section where (section.eliminado = null or section.eliminado = false) and section.id = :id").setParameter("id", sectionId).getSingleResult();
			} catch (Exception e){
				this.section = null;
			}
		} else
			this.section = null;			
		if(groupId != null && groupId.toString() != null && !groupId.toString().trim().isEmpty()){
			try {
				this.group = (GrupoVariables_ensayo) this.entityManager.createQuery("select g from GrupoVariables_ensayo g where (g.eliminado = null or g.eliminado = false) and g.id = :id").setParameter("id", groupId).getSingleResult();
			} catch (Exception e){
				this.group = null;
			}
		} else
			this.group = null;
		if(this.section != null && this.group != null){
			this.data = null;
			if(this.editing){
				String tempKey = this.parentController.convertKey(this.section.getId(), this.group.getId());
				if(this.parentController.getMapWGD() != null && !this.parentController.getMapWGD().isEmpty() && this.parentController.getMapWGD().containsKey(tempKey)){
					int index = this.parentController.indexOfGroupData(this.parentController.getMapWGD().get(tempKey), this.repetition);
					if(index != -1){
						this.data = new WrapperGroupData(this.parentController.getMapWGD().get(tempKey).get(index).getSection(), this.parentController.getMapWGD().get(tempKey).get(index).getGroup(), this.parentController.getMapWGD().get(tempKey).get(index).getRepetition());
						this.data.setCreating(this.parentController.getMapWGD().get(tempKey).get(index).getCreating());
						this.data.setNotification(this.parentController.getMapWGD().get(tempKey).get(index).getNotification());
						this.data.setReport(this.parentController.getMapWGD().get(tempKey).get(index).getReport());
						this.data.getData().putAll(this.parentController.getMapWGD().get(tempKey).get(index).copyData());						
					}
				}
			}
			this.data = ((this.data == null) ? (new WrapperGroupData(this.section, this.group, this.repetition)) : this.data);			
			if(this.data.getData() == null || this.data.getData().isEmpty()){				
				List<Variable_ensayo> l = this.getVariables();
				for (Variable_ensayo itemVariable : l){
					if(!this.data.getData().containsKey(itemVariable.getId())){
						MapWrapperDataPlus itemData = new MapWrapperDataPlus(this.section, this.group, itemVariable);						
						this.data.getData().put(itemVariable.getId(), itemData);
					}						
				}
			}
			if(this.data != null && this.data.getData() != null && !this.data.getData().isEmpty())
				this.loadIndexes();
			return "access"; 
		} else
			return null;
	}
	
	public Long getIndex(Integer row, Integer column){
		return (this.validPosition(row, column) ? this.indexes[row][column] : null);
	}
	
	public boolean validPosition(Integer row, Integer column){
		return (row >= 0 && column >= 0 && row < this.indexes.length && column < this.indexes[row].length && this.indexes[row][column] != null);		
	}
	
	public Integer rows(){
		return (this.indexes != null ? this.indexes.length : 0);
	}
	
	public Integer cols(Integer row){
		return ((this.indexes != null && this.indexes[row] != null) ? this.indexes[row].length : 0);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List valoresRadio(Nomenclador_ensayo nomenclador){
		List<NomencladorValor_ensayo> lista = new ArrayList<NomencladorValor_ensayo>();
		List listaRadio = new ArrayList();
		lista = (List<NomencladorValor_ensayo>) this.entityManager.createQuery("select nom from NomencladorValor_ensayo nom where nom.nomenclador = :nomenclador and nom.valor != null ORDER BY nom.valorCalculado ASC").setParameter("nomenclador", nomenclador).getResultList();
		for (int i = 0; i < lista.size(); i++)
			listaRadio.add(new SelectItem(lista.get(i).getValor(), lista.get(i).getValor()));
		return listaRadio;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> valoresCEA(Nomenclador_ensayo nomenclador){
		List<String> lista = new ArrayList<String>();
		if (nomenclador.getNombre().equals("CEA"))
			lista = (List<String>) this.entityManager
				.createQuery("select nom.nombreCategoria from CtcCategoria_ensayo nom where nom.diccionario.id =:idDicc ORDER BY nom.nombreCategoria ASC")
				.setParameter("idDicc", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getDiccionario().getId())
				.getResultList();
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> valoresEA(Nomenclador_ensayo nomenclador){
		String categoryName = null;
		for(MapWrapperDataPlus itemData : this.data.getData().values()){
			if (itemData.getVariable().getNomenclador() != null && itemData.getVariable().getNomenclador().getNombre().equals("CEA")){
				categoryName = (String) itemData.getValue(); 
				break;
			}
		}		
		
		List<String> lista = new ArrayList<String>();		
		if(nomenclador.getNombre().equals("EA") && categoryName != null && !categoryName.isEmpty() && !categoryName.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			lista = (List<String>) this.entityManager.createQuery("select nom.eventoAdverso from Ctc_ensayo nom where nom.ctcCategoria.nombreCategoria = :nombreCategoria and nom.ctcCategoria.diccionario.id =:idDicc ORDER BY nom.eventoAdverso ASC")
			.setParameter("nombreCategoria", categoryName)
			.setParameter("idDicc", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getDiccionario().getId())
			.getResultList();
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> valores(Nomenclador_ensayo nomenclador){
		List<String> lista = new ArrayList<String>();
		if(nomenclador != null)
			lista = (List<String>) this.entityManager.createQuery("select nom.valor from NomencladorValor_ensayo nom where nom.nomenclador = :nomenc ORDER BY nom.valorCalculado ASC").setParameter("nomenc", nomenclador).getResultList();
		return lista;
	}
	
	public int sizeMaxString(List<String> listReturn){
		if (listReturn.size() == 0)
			return 150;
		int max = listReturn.get(0).length();
		for (int i = 1; i < listReturn.size(); i++){
			if (listReturn.get(i).length() > max)
				max = listReturn.get(i).length();
		}
		if ((max * 5) > 150)
			return max * 5;
		return 150;
	}
	
	@SuppressWarnings("unchecked")
	public void resetEA(String category){  
		Variable_ensayo tempVariable = this.data.findVariable("EA");
		if(tempVariable != null && this.data.getData().containsKey(tempVariable.getId())){
			if(category != null && !category.isEmpty() && !category.equals(SeamResourceBundle.getBundle().getString("cbxSeleccionPorDefecto"))){
				List<String> eas = this.entityManager.createQuery("select distinct(ctc.eventoAdverso) from Ctc_ensayo ctc where ctc.ctcCategoria.nombreCategoria = :categoryName").setParameter("categoryName", category).getResultList();
				if(!eas.isEmpty() && this.data.getData().get(tempVariable.getId()) != null && this.data.getData().get(tempVariable.getId()).getValue() != null && this.data.getData().get(tempVariable.getId()).getValue().toString() != null && !this.data.getData().get(tempVariable.getId()).getValue().toString().isEmpty() && !eas.contains(this.data.getData().get(tempVariable.getId()).getValue().toString()))
					this.data.getData().get(tempVariable.getId()).setValue(null);
			} else
				this.data.getData().get(tempVariable.getId()).setValue(null);
		}
	}
	
	public String save(){
		try {
			String tempKey = this.parentController.convertKey(this.section.getId(), this.group.getId());
			if(this.parentController.getMapPositions() == null)
				this.parentController.setMapPositions(new HashMap<String, Integer>());			
			Integer tempPosition = null;
			if(!this.editing){							
				tempPosition = 0;
				if(!this.parentController.getMapPositions().containsKey(tempKey))
					this.parentController.getMapPositions().put(tempKey, tempPosition);
				else
					tempPosition = this.parentController.getMapPositions().get(tempKey);
				tempPosition++;				
			} else
				tempPosition = this.repetition;
			for (MapWrapperDataPlus itemData : this.data.getData().values()){
				if(itemData.isCalendar() && !itemData.notValued())
					itemData.setValue(new SimpleDateFormat("dd/MM/yyyy").format((Date) itemData.getValue()));
			}
			if(!this.editing){
				for (MapWrapperDataPlus itemData : this.data.getData().values())
					itemData.setRepetition(tempPosition);					
				this.data.setRepetition(tempPosition);
				if(!this.parentController.getMapWGD().containsKey(tempKey))
					this.parentController.getMapWGD().put(tempKey, new ArrayList<WrapperGroupData>());
			}
			int index = this.parentController.indexOfGroupData(this.parentController.getMapWGD().get(tempKey), tempPosition);
			if(index != -1 && this.editing)
				this.parentController.getMapWGD().get(tempKey).remove(index);
			this.parentController.getMapWGD().get(tempKey).add(this.data);
			this.parentController.orderValues(tempKey);
			if(tempPosition != null && !this.editing)
				this.parentController.getMapPositions().put(tempKey, tempPosition);
			final String parentResult = this.parentController.persistir();
			if(parentResult != null){
				this.reset();
				return "access";
			}
			return parentResult;
			
		} catch (Exception e){
			this.facesMessages.add(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public void cancelar(){
		this.reset();
	}
	
	public List<Variable_ensayo> getVariables(){
		if((variables == null || variables.isEmpty()) && this.section != null && this.group != null)
			this.loadVariables();
		return variables;
	}
	
	public void setVariables(List<Variable_ensayo> variables){
		this.variables = variables;
	}
	
	public Seccion_ensayo getSection(){
		return section;
	}
	
	public void setSection(Seccion_ensayo section){
		this.section = section;
	}
	
	public GrupoVariables_ensayo getGroup(){
		return group;
	}
	
	public void setGroup(GrupoVariables_ensayo group){
		this.group = group;
	}
	
	public WrapperGroupData getData(){
		return data;
	}
	
	public void setData(WrapperGroupData data){
		this.data = data;
	}
	
	public Integer getRepetition(){
		return repetition;
	}
	
	public void setRepetition(Integer repetition){
		this.repetition = repetition;
	}
	
	public boolean isEditing(){
		return editing;
	}
	
	public void setEditing(boolean editing){
		this.editing = editing;
	}
	
	public GestionarHoja getParentController(){
		return parentController;
	}

}