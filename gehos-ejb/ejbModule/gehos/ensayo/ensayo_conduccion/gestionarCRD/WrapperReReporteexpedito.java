package gehos.ensayo.ensayo_conduccion.gestionarCRD;

import java.util.ArrayList;
import java.util.List;

import gehos.ensayo.entity.EFaseEstudio_ensayo;
import gehos.ensayo.entity.ReMedicacion_ensayo;
import gehos.ensayo.entity.ReReporteexpedito_ensayo;

public class WrapperReReporteexpedito {
	
	private ReReporteexpedito_ensayo report;
	private EFaseEstudio_ensayo fase;
	private String[] consecuencias;
	private List<ReMedicacion_ensayo> productosEstudios;
	private List<ReMedicacion_ensayo> medicacionConcomitante;
	private List<ReMedicacion_ensayo> enfermedadesInter;
	private List<ReMedicacion_ensayo> medicacionReciente;
	private boolean modified = false;
	
	public WrapperReReporteexpedito(ReReporteexpedito_ensayo report){
		super();
		this.report = report;
		this.fase = null;
		this.consecuencias = null;
		this.productosEstudios = new ArrayList<ReMedicacion_ensayo>();
		this.medicacionConcomitante = new ArrayList<ReMedicacion_ensayo>();
		this.enfermedadesInter = new ArrayList<ReMedicacion_ensayo>();
		this.medicacionReciente = new ArrayList<ReMedicacion_ensayo>();
		this.modified = false;
	}
	
	public WrapperReReporteexpedito(){
		super();
		this.report = null;
		this.fase = null;
		this.consecuencias = null;
		this.productosEstudios = new ArrayList<ReMedicacion_ensayo>();
		this.medicacionConcomitante = new ArrayList<ReMedicacion_ensayo>();
		this.enfermedadesInter = new ArrayList<ReMedicacion_ensayo>();
		this.medicacionReciente = new ArrayList<ReMedicacion_ensayo>();
		this.modified = false;
	}
	
	public boolean hasChanges(WrapperReReporteexpedito other){
		if((this.report != null && other.report == null) || (this.report == null && other.report != null))
			return true;
		else if(this.report != null && other.report != null && this.modified)
			return true;
		if((this.fase != null && other.fase == null) || (this.fase == null && other.fase != null) || (this.fase != null && other.fase != null && this.fase.getId() != other.fase.getId()))
			return true;
		if((this.consecuencias != null && other.consecuencias == null) || (this.consecuencias == null && other.consecuencias != null) || (this.consecuencias.length != other.consecuencias.length) || (!this.consecuencias.equals(other.consecuencias)))
			return true;
		if((this.productosEstudios != null && other.productosEstudios == null) || (this.productosEstudios == null && other.productosEstudios != null) || (this.productosEstudios.size() != other.productosEstudios.size()) || (!this.productosEstudios.equals(other.productosEstudios)))
			return true;
		if((this.medicacionConcomitante != null && other.medicacionConcomitante == null) || (this.medicacionConcomitante == null && other.medicacionConcomitante != null) || (this.medicacionConcomitante.size() != other.medicacionConcomitante.size()) || (!this.medicacionConcomitante.equals(other.medicacionConcomitante)))
			return true;
		if((this.enfermedadesInter != null && other.enfermedadesInter == null) || (this.enfermedadesInter == null && other.enfermedadesInter != null) || (this.enfermedadesInter.size() != other.enfermedadesInter.size()) || (!this.enfermedadesInter.equals(other.enfermedadesInter)))
			return true;
		if((this.medicacionReciente != null && other.medicacionReciente == null) || (this.medicacionReciente == null && other.medicacionReciente != null) || (this.medicacionReciente.size() != other.medicacionReciente.size()) || (!this.medicacionReciente.equals(other.medicacionReciente)))
			return true;
		return false;
	}

	public ReReporteexpedito_ensayo getReport(){
		return report;
	}

	public void setReport(ReReporteexpedito_ensayo report){
		this.report = report;
	}

	public EFaseEstudio_ensayo getFase(){
		return fase;
	}

	public void setFase(EFaseEstudio_ensayo fase){
		this.fase = fase;
	}
	
	public String[] getConsecuencias(){
		return consecuencias;
	}
	
	public void setConsecuencias(String[] consecuencias){
		this.consecuencias = consecuencias;
	}
	
	public List<ReMedicacion_ensayo> getProductosEstudios(){
		return productosEstudios;
	}
	
	public void setProductosEstudios(List<ReMedicacion_ensayo> productosEstudios){
		this.productosEstudios = productosEstudios;
	}
	
	public List<ReMedicacion_ensayo> getMedicacionConcomitante(){
		return medicacionConcomitante;
	}
	
	public void setMedicacionConcomitante(List<ReMedicacion_ensayo> medicacionConcomitante){
		this.medicacionConcomitante = medicacionConcomitante;
	}
	
	public List<ReMedicacion_ensayo> getEnfermedadesInter(){
		return enfermedadesInter;
	}
	
	public void setEnfermedadesInter(List<ReMedicacion_ensayo> enfermedadesInter){
		this.enfermedadesInter = enfermedadesInter;
	}
	
	public List<ReMedicacion_ensayo> getMedicacionReciente(){
		return medicacionReciente;
	}
	
	public void setMedicacionReciente(List<ReMedicacion_ensayo> medicacionReciente){
		this.medicacionReciente = medicacionReciente;
	}
	
	public boolean isModified(){
		return modified;
	}
	
	public void setModified(boolean modified){
		this.modified = modified;
	}

}