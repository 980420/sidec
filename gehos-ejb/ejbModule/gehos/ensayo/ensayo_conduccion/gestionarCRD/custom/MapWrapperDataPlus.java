package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

public class MapWrapperDataPlus extends MapWrapperData {
	
	private Seccion_ensayo section;
	private GrupoVariables_ensayo group;	
	private Integer repetition;
	
	public MapWrapperDataPlus(){
		super();
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section){
		super();
		this.section = section;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group){
		super();
		this.section = section;
		this.group = group;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Integer repetition){
		super();
		this.section = section;
		this.group = group;
		this.repetition = repetition;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, Variable_ensayo variable){
		super(variable);
		this.section = section;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Variable_ensayo variable){
		super(variable);
		this.section = section;
		this.group = group;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Integer repetition, Variable_ensayo variable){
		super(variable);
		this.section = section;
		this.group = group;
		this.repetition = repetition;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, Variable_ensayo variable, Object value){
		super(variable, value);
		this.section = section;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Variable_ensayo variable, Object value){
		super(variable, value);
		this.section = section;
		this.group = group;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Integer repetition, Variable_ensayo variable, Object value){
		super(variable, value);
		this.section = section;
		this.group = group;
		this.repetition = repetition;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, Variable_ensayo variable, Object value, Object[] values, String fileName, byte[] data){
		super(variable, value, values, fileName, data);
		this.section = section;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Variable_ensayo variable, Object value, Object[] values, String fileName, byte[] data){
		super(variable, value, values, fileName, data);
		this.section = section;
		this.group = group;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Integer repetition, Variable_ensayo variable, Object value, Object[] values, String fileName, byte[] data){
		super(variable, value, values, fileName, data);
		this.section = section;
		this.group = group;
		this.repetition = repetition;
	}
	
	public MapWrapperDataPlus(Seccion_ensayo section, GrupoVariables_ensayo group, Integer repetition, Boolean creating, Variable_ensayo variable, Object value, Object[] values, String fileName, byte[] data){
		super(variable, value, values, fileName, data);
		this.section = section;
		this.group = group;
		this.repetition = repetition;
	}
	
	public MapWrapperDataPlus(MapWrapperData object){
		super(object.variable, object.value, object.values, object.fileName, object.data, object.fileSizeString, object.fileItems, object.variableData, object.variableDatas, object.report, object.notification, object.noteSite, object.noteMonitoring);
	}
	
	@Override
	public boolean validElements(){
		return (super.validElements() && this.section != null);
	}
	
	@Override
	public MapWrapperDataPlus copyData(){
		MapWrapperDataPlus itemDataCopy = new MapWrapperDataPlus(super.copyData());
		itemDataCopy.setSection(this.section);
		itemDataCopy.setGroup(this.group);
		itemDataCopy.setRepetition(this.repetition);
		return itemDataCopy;
	}
	
	public boolean validFullElements(){
		return (this.validElements() && this.group != null && this.repetition != null && this.repetition.toString() != null && !this.repetition.toString().isEmpty());
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
	
}