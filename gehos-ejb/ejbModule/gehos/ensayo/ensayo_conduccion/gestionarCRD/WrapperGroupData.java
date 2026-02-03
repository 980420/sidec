package gehos.ensayo.ensayo_conduccion.gestionarCRD;

import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.Notificacion_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WrapperGroupData implements Comparable<WrapperGroupData> {
	
	private Seccion_ensayo section;
	private GrupoVariables_ensayo group;
	private Integer repetition;
	private Map<Long, MapWrapperDataPlus> data = new HashMap<Long, MapWrapperDataPlus>();
	private WrapperReReporteexpedito report;
	private Notificacion_ensayo notification;
	private Boolean creating;
	
	public WrapperGroupData(){
		super();
		this.section = null;
		this.group = null;
		this.repetition = null;
		this.data = new HashMap<Long, MapWrapperDataPlus>();
		this.report = null;
		this.notification = null;
		this.creating = true;
	}

	public WrapperGroupData(Seccion_ensayo section, GrupoVariables_ensayo group, Integer repetition){
		super();
		this.section = section;
		this.group = group;
		this.repetition = repetition;
		this.report = null;
		this.notification = null;
		this.creating = true;
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

	public Integer getRepetition(){
		return repetition;
	}

	public void setRepetition(Integer repetition){
		this.repetition = repetition;
	}

	public Map<Long, MapWrapperDataPlus> getData(){
		return data;
	}

	public void setData(Map<Long, MapWrapperDataPlus> data){
		this.data = data;
	}
	
	public WrapperReReporteexpedito getReport(){
		return report;
	}
	
	public void setReport(WrapperReReporteexpedito report){
		this.report = report;
	}
	
	public Notificacion_ensayo getNotification(){
		return notification;
	}
	
	public void setNotification(Notificacion_ensayo notification){
		this.notification = notification;
	}
	
	public Boolean getCreating(){
		return creating;
	}
	
	public void setCreating(Boolean creating){
		this.creating = creating;
	}
	
	public boolean hasReport(){
		return (this.report != null && this.report.getReport() != null);
	}
	
	public boolean hasNotification(){
		return (this.notification != null);
	}
	
	public Variable_ensayo findVariable(Long sectionId, Long groupId, Integer repetition, String description){
		Variable_ensayo tempVariable = null;
		if(sectionId != null && sectionId.toString() != null && !sectionId.toString().isEmpty() && groupId != null && groupId.toString() != null && !groupId.toString().isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().isEmpty() && description != null && !description.isEmpty() && this.data != null && !this.data.isEmpty()){
			for(MapWrapperDataPlus itemData : this.data.values()){
				if(itemData != null && itemData.getSection() != null && sectionId.equals(itemData.getSection().getId()) && itemData.getGroup() != null && groupId.equals(itemData.getGroup().getId()) && itemData.getRepetition() != null && repetition.equals(itemData.getRepetition()) && itemData.getVariable() != null && itemData.getVariable().getDescripcionVariable().equals(description)){
					tempVariable = itemData.getVariable();
					break;
				}
			}
		}
		return tempVariable;
	}
	
	public Variable_ensayo findVariable(String nomenclature){
		Variable_ensayo tempVariable = null;
		if(nomenclature != null && !nomenclature.isEmpty() && this.data != null && !this.data.isEmpty()){
			for(MapWrapperDataPlus itemData : this.data.values()){
				if(itemData.getVariable() != null && itemData.getVariable().getNomenclador() != null && itemData.getVariable().getNomenclador().getNombre() != null && !itemData.getVariable().getNomenclador().getNombre().isEmpty() && itemData.getVariable().getNomenclador().getNombre().equals(nomenclature)){
					tempVariable = itemData.getVariable();
					break;
				}
			}
		}
		return tempVariable;
	}
	
	public boolean hasChanges(WrapperGroupData other){
		if((this.hasReport() && !other.hasReport()) || (!this.hasReport() && other.hasReport()))
			return true;
		else if(this.hasReport() && other.hasReport() && this.report.hasChanges(other.getReport()))
			return true;
		if((this.hasNotification() && !other.hasNotification()) || (!this.hasNotification() && other.hasNotification()))
			return true;
		if((this.data == null && other.data != null) || (this.data != null && other.data == null) || (this.data.isEmpty() && !other.data.isEmpty()) || (!this.data.isEmpty() && other.data.isEmpty()) || (this.data.size() != other.data.size()))
			return true;
		else if(this.data != null && !this.data.isEmpty() && other.data != null && !other.data.isEmpty()){
			if(this.data.keySet().size() != other.data.keySet().size())
				return true;
			for(Long tempKey : this.data.keySet()){
				if(!other.data.containsKey(tempKey))
					return true;
				else if(this.data.get(tempKey) != null && other.data.get(tempKey) != null){
					boolean changes = this.data.get(tempKey).hasChanges(other.data.get(tempKey));
					if(changes)
						return true;
				}
			}
		}
		return false;
	}
	
	public boolean validElements(){
		return (this.validPartialElements() && this.group != null && this.repetition != null && this.repetition.toString() != null && !this.repetition.toString().isEmpty());
	}
	
	public boolean validPartialElements(){
		return (this.section != null && this.data != null && !this.data.isEmpty());
	}
	
	public Map<Long, MapWrapperDataPlus> copyData(){
		Map<Long, MapWrapperDataPlus> copyData = new HashMap<Long, MapWrapperDataPlus>();
		if(this.data != null && !this.data.isEmpty()){
			for (Entry<Long, MapWrapperDataPlus> itemEntry : this.data.entrySet()){
				if(!copyData.containsKey(itemEntry.getKey()) && itemEntry.getValue() != null){
					MapWrapperDataPlus itemDataCopy = new MapWrapperDataPlus();
					itemDataCopy.setSection(itemEntry.getValue().getSection());
					itemDataCopy.setGroup(itemEntry.getValue().getGroup());
					itemDataCopy.setRepetition(itemEntry.getValue().getRepetition());
					itemDataCopy.setVariable(itemEntry.getValue().getVariable());
					itemDataCopy.setNoteSite(itemEntry.getValue().getNoteSite());
					itemDataCopy.setNoteMonitoring(itemEntry.getValue().getNoteMonitoring());
					itemDataCopy.setNotification(itemEntry.getValue().getNotification());
					itemDataCopy.setReport(itemEntry.getValue().getReport());
					itemDataCopy.setData(itemEntry.getValue().getData());
					itemDataCopy.setFileItems(itemEntry.getValue().getFileItems());
					itemDataCopy.setFileName(itemEntry.getValue().getFileName());
					itemDataCopy.setFileSizeString(itemEntry.getValue().getFileSizeString());
					itemDataCopy.setValue(itemEntry.getValue().getValue());
					itemDataCopy.setValues(itemEntry.getValue().getValues());
					itemDataCopy.setVariableData(itemEntry.getValue().getVariableData());
					itemDataCopy.setVariableDatas(itemEntry.getValue().getVariableDatas());
					copyData.put(itemEntry.getKey(), itemDataCopy);
				}
			}
		}
		return copyData;
	}
	
	@Override
	public int compareTo(WrapperGroupData o){
		if(this.repetition == null || this.repetition.toString().isEmpty() || o.getRepetition() == null || o.getRepetition().toString().isEmpty())
			return 0;
		else
			return this.repetition.compareTo(o.getRepetition());			
	}

}