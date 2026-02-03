package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class MedicacionConco {
	
	private String medicamento;
	private String dosisT;
	private String frecuencia;
	private String viaAdmon;
	private String fechaI;
	private String fechaF;
	private String indicacion;
	
	public MedicacionConco(String medicamento, String dosisT,
			String frecuencia, String viaAdmon, String fechaI, String fechaF,
			String indicacion) {
		super();
		this.medicamento = medicamento;
		this.dosisT = dosisT;
		this.frecuencia = frecuencia;
		this.viaAdmon = viaAdmon;
		this.fechaI = fechaI;
		this.fechaF = fechaF;
		this.indicacion = indicacion;
	}
	public String getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}
	public String getDosisT() {
		return dosisT;
	}
	public void setDosisT(String dosisT) {
		this.dosisT = dosisT;
	}
	public String getFrecuencia() {
		return frecuencia;
	}
	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}
	public String getViaAdmon() {
		return viaAdmon;
	}
	public void setViaAdmon(String viaAdmon) {
		this.viaAdmon = viaAdmon;
	}
	public String getFechaI() {
		return fechaI;
	}
	public void setFechaI(String fechaI) {
		this.fechaI = fechaI;
	}
	public String getFechaF() {
		return fechaF;
	}
	public void setFechaF(String fechaF) {
		this.fechaF = fechaF;
	}
	public String getIndicacion() {
		return indicacion;
	}
	public void setIndicacion(String indicacion) {
		this.indicacion = indicacion;
	}
	
	
	
}
