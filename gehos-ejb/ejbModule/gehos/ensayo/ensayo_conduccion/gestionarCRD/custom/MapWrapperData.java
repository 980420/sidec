package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import gehos.ensayo.ensayo_conduccion.gestionarCRD.WrapperReReporteexpedito;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Notificacion_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.richfaces.model.UploadItem;

public class MapWrapperData {
	
	protected Variable_ensayo variable;
	protected Object value;
	protected Object[] values;
	protected String fileName;
	protected byte[] data;
	protected String fileSizeString;	
	protected List<UploadItem> fileItems = new ArrayList<UploadItem>();
	protected VariableDato_ensayo variableData;
	protected List<VariableDato_ensayo> variableDatas = new ArrayList<VariableDato_ensayo>();
	protected WrapperReReporteexpedito report;
	protected Notificacion_ensayo notification;
	protected Nota_ensayo noteSite;
	protected Nota_ensayo noteMonitoring;
	protected List<Nota_ensayo> noteSiteResponses;
	protected List<Nota_ensayo> noteMonitoringResponses;
	protected boolean oldFormat;
	
	public MapWrapperData(){
		super();
		this.variable = null;
		this.value = null;
		this.values = null;
		this.fileName = null;
		this.data = null;
		this.fileSizeString = null;
		this.variableData = null;
		this.report = null;
		this.noteSite = null;
		this.noteMonitoring = null;
		this.noteSiteResponses = null;
		this.noteMonitoringResponses = null;
		this.oldFormat = false;
	}
	
	public MapWrapperData(Variable_ensayo variable){
		super();
		this.variable = variable;
		this.value = null;
		this.values = null;
		this.fileName = null;
		this.data = null;
		this.fileSizeString = null;
		this.variableData = null;
		this.report = null;
		this.noteSite = null;
		this.noteMonitoring = null;
		this.noteSiteResponses = null;
		this.noteMonitoringResponses = null;
		this.oldFormat = false;
	}

	public MapWrapperData(Variable_ensayo variable, Object value){
		super();
		this.variable = variable;
		this.value = value;
		this.noteSite = null;
		this.noteMonitoring = null;
		this.noteSiteResponses = null;
		this.noteMonitoringResponses = null;
		this.oldFormat = false;
	}

	public MapWrapperData(Variable_ensayo variable, Object value, Object[] values, String fileName, byte[] data){
		super();
		this.variable = variable;
		this.value = value;
		this.values = values;
		this.fileName = fileName;
		this.data = data;
		if(data != null)
			this.fileSizeString = convertSize(data.length);
		this.variableData = null;
		this.report = null;
		this.noteSite = null;
		this.noteMonitoring = null;
		this.noteSiteResponses = null;
		this.noteMonitoringResponses = null;
		this.oldFormat = false;
	}
	
	public MapWrapperData(Variable_ensayo variable, Object value, Object[] values, String fileName, byte[] data, String fileSizeString, List<UploadItem> fileItems, VariableDato_ensayo variableData, List<VariableDato_ensayo> variableDatas, WrapperReReporteexpedito report, Notificacion_ensayo notification, Nota_ensayo noteSite, Nota_ensayo noteMonitoring){
		super();
		this.variable = variable;
		this.value = value;
		this.values = values;
		this.fileName = fileName;
		this.data = data;
		this.fileSizeString = fileSizeString;
		this.fileItems = fileItems;
		this.variableData = variableData;
		this.variableDatas = variableDatas;
		this.report = report;
		this.notification = notification;
		this.noteSite = noteSite;
		this.noteMonitoring = noteMonitoring;
		this.noteSiteResponses = null;
		this.noteMonitoringResponses = null;
		this.oldFormat = false;
	}

	public static String convertSize(int bytes){
		String unit = "B";
		int divisor = 1024;
		if(bytes > divisor){	
			bytes = bytes/divisor;
			unit = "KB";
		}
		if(bytes > divisor){	
			bytes = bytes/divisor;
			unit = "MB";
		}
		if(bytes > divisor){	
			bytes = bytes/divisor;
			unit = "GB";
		}
		return Math.round(bytes * 10) / 10 + unit;
	}
	
	public boolean isCheckBox(){
		return (this.variable != null && this.variable.getPresentacionFormulario().getNombre().equals("checkbox")); 
	}
	
	public boolean isCalendar(){
		return (this.variable != null && this.variable.getPresentacionFormulario().getNombre().equals("calendar") && this.variable.getTipoDato().getCodigo().equals("DATE")); 
	}
	
	public String cleanFileName(){
		if(this.fileName == null)
			return null;
		String out = this.fileName;
		out = out.replaceFirst(String.valueOf(this.variable.getId()), "");
		return out;
	}
	
	public boolean notValued(){
		return ((this.value == null || this.value.toString() == null || this.value.toString().isEmpty()) && (this.values == null || this.values.length == 0) && (this.data == null || this.data.length == 0 || this.fileName == null || this.fileName.isEmpty()));
	}
	
	public boolean hasNoteSite(){
		return (this.noteSite != null);
	}
	
	public boolean hasNoteMonitoring(){
		return (this.noteMonitoring != null);
	}
	
	public boolean hasNoteSiteResponse(){
		return (this.noteSiteResponses != null && !this.noteSiteResponses.isEmpty());
	}
	
	public boolean hasNoteMonitoringResponse(){
		return (this.noteMonitoringResponses != null && !this.noteMonitoringResponses.isEmpty());
	}
	
	public boolean hasReport(){
		return (this.report != null && this.report.getReport() != null);
	}
	
	public boolean hasNotification(){
		return (this.notification != null);
	}
	
	public boolean hasChanges(Object obj){
		if(obj == null)
			return false;
		MapWrapperData other = (MapWrapperData) obj;		
		if((this.data == null && other.data != null) || (this.data != null && other.data == null) || !Arrays.equals(this.data, other.data))		
			return true;
		if((this.fileItems == null && other.fileItems != null) || (this.fileItems != null && other.fileItems == null) || (!this.fileItems.equals(other.fileItems)))
			return true;
		if((this.fileName == null && other.fileName != null) || (this.fileName != null && other.fileName == null) || (this.fileName != null && other.fileName != null && !this.fileName.equals(other.fileName)))
			return true;
		if((this.fileSizeString == null && other.fileSizeString != null) || (this.fileSizeString != null && other.fileSizeString == null) || (this.fileSizeString != null && other.fileSizeString != null && !this.fileSizeString.equals(other.fileSizeString)))
			return true;
		if((this.value == null && other.value != null) || (this.value != null && other.value == null) || (this.value != null && other.value != null && !this.value.equals(other.value)))
			return true;
		if((this.values == null && other.values != null) || (this.values != null && other.values == null) || (this.data == null && other.data != null) || (this.data != null && other.data == null) || !Arrays.equals(this.data, other.data) || !Arrays.equals(this.values, other.values))
			return true;
		if(this.oldFormat != other.oldFormat)
			return true;
		if((this.hasNoteSite() && !other.hasNoteSite()) || (!this.hasNoteSite() && other.hasNoteSite()))
			return true;
		if((this.hasNoteMonitoring() && !other.hasNoteMonitoring()) || (!this.hasNoteMonitoring() && other.hasNoteMonitoring()))
			return true;
		if((this.hasNoteSiteResponse() && !other.hasNoteSiteResponse()) || (!this.hasNoteSiteResponse() && other.hasNoteSiteResponse()))
			return true;
		if((this.hasNoteMonitoringResponse() && !other.hasNoteMonitoringResponse()) || (!this.hasNoteMonitoringResponse() && other.hasNoteMonitoringResponse()))
			return true;
		if((this.hasReport() && !other.hasReport()) || (!this.hasReport() && other.hasReport()))
			return true;
		else if(this.hasReport() && other.hasReport() && this.report.hasChanges(other.getReport()))
			return true;
		if((this.hasNotification() && !other.hasNotification()) || (!this.hasNotification() && other.hasNotification()))
			return true;
		if((this.variableData == null && other.variableData != null) || (this.variableData != null && other.variableData == null))
			return true;
		else if(this.variableData != null && other.variableData != null)
			return ((this.variableData.getValor() == null && other.variableData.getValor() != null) || (this.variableData.getValor() != null && other.variableData.getValor() == null) || (this.variableData.getValor() != null && other.variableData.getValor() != null && !this.variableData.getValor().equals(other.variableData.getValor())));
		if(this.notValued() != other.notValued())
			return true;
		return false;
	}
	
	public boolean validElements(){
		return (this.variable != null);
	}
	
	public void addNoteResponse(Nota_ensayo noteResponse, boolean site){
		if(noteResponse != null){
			if(site){
				if(this.noteSiteResponses == null)
					this.noteSiteResponses = new ArrayList<Nota_ensayo>();
				this.noteSiteResponses.add(noteResponse);
			} else{
				if(this.noteMonitoringResponses == null)
					this.noteMonitoringResponses = new ArrayList<Nota_ensayo>();
				this.noteMonitoringResponses.add(noteResponse);
			}
		}
	}
	
	public MapWrapperData copyData(){
		MapWrapperData itemDataCopy = new MapWrapperData();
		itemDataCopy.setVariable(this.variable);
		itemDataCopy.setValue(this.value);
		itemDataCopy.setValues(this.values);
		itemDataCopy.setFileName(this.fileName);
		itemDataCopy.setData(this.data);
		itemDataCopy.setFileSizeString(this.fileSizeString);
		itemDataCopy.setFileItems(this.fileItems);
		itemDataCopy.setVariableData(this.variableData);
		itemDataCopy.setVariableDatas(this.variableDatas);
		itemDataCopy.setReport(this.report);
		itemDataCopy.setNotification(this.notification);
		itemDataCopy.setNoteSite(this.noteSite);
		itemDataCopy.setNoteMonitoring(this.noteMonitoring);
		itemDataCopy.setNoteSiteResponses(this.noteSiteResponses);
		itemDataCopy.setNoteMonitoringResponses(this.noteMonitoringResponses);		
		itemDataCopy.setOldFormat(this.oldFormat ? !this.oldFormat : this.oldFormat);
		return itemDataCopy;
	}

	public Variable_ensayo getVariable(){
		return variable;
	}

	public void setVariable(Variable_ensayo variable){
		this.variable = variable;
	}

	public Object getValue(){
		return value;
	}

	public void setValue(Object value){
		this.value = value;
	}

	public Object[] getValues(){
		return values;
	}

	public void setValues(Object[] values){
		this.values = values;
	}

	public String getFileName(){
		return fileName;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public byte[] getData(){
		return data;
	}

	public void setData(byte[] data){
		this.data = data;
		if(this.data != null)
			this.fileSizeString = convertSize(data.length);
		else
			this.fileSizeString = null;
	}

	public String getFileSizeString(){
		return fileSizeString;
	}

	public void setFileSizeString(String fileSizeString){
		this.fileSizeString = fileSizeString;
	}

	public List<UploadItem> getFileItems(){
		return fileItems;
	}

	public void setFileItems(List<UploadItem> fileItems){
		this.fileItems = fileItems;
	}
	
	public VariableDato_ensayo getVariableData(){
		return variableData;
	}
	
	public void setVariableData(VariableDato_ensayo variableData){
		this.variableData = variableData;
	}
	
	public List<VariableDato_ensayo> getVariableDatas(){
		return variableDatas;
	}
	
	public void setVariableDatas(List<VariableDato_ensayo> variableDatas){
		this.variableDatas = variableDatas;
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
	
	public Nota_ensayo getNoteSite(){
		return noteSite;
	}
	
	public void setNoteSite(Nota_ensayo noteSite){
		this.noteSite = noteSite;
	}
	
	public Nota_ensayo getNoteMonitoring(){
		return noteMonitoring;
	}
	
	public void setNoteMonitoring(Nota_ensayo noteMonitoring){
		this.noteMonitoring = noteMonitoring;
	}
	
	public List<Nota_ensayo> getNoteMonitoringResponses(){
		return noteMonitoringResponses;
	}
	
	public void setNoteSiteResponses(List<Nota_ensayo> noteSiteResponses){
		this.noteSiteResponses = noteSiteResponses;
	}
	
	public List<Nota_ensayo> getNoteSiteResponses(){
		return noteSiteResponses;
	}
	
	public void setNoteMonitoringResponses(List<Nota_ensayo> noteMonitoringResponses){
		this.noteMonitoringResponses = noteMonitoringResponses;
	}
	
	public boolean isOldFormat(){
		return oldFormat;
	}
	
	public void setOldFormat(boolean oldFormat){
		this.oldFormat = oldFormat;
	}

}