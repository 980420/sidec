package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class MedicacionOtra {
	
	private String medicamento1;
	private String dosisT1;
	private String frecuencia1;
	private String viaAdmon1;
	private String fechaI1;
	private String fechaF1;
	private String indicacion1;
	
	public MedicacionOtra(String medicamento1, String dosisT1,
			String frecuencia1, String viaAdmon1, String fechaI1,
			String fechaF1, String indicacion1) {
		super();
		this.medicamento1 = medicamento1;
		this.dosisT1 = dosisT1;
		this.frecuencia1 = frecuencia1;
		this.viaAdmon1 = viaAdmon1;
		this.fechaI1 = fechaI1;
		this.fechaF1 = fechaF1;
		this.indicacion1 = indicacion1;
	}

	public String getMedicamento1() {
		return medicamento1;
	}

	public void setMedicamento1(String medicamento1) {
		this.medicamento1 = medicamento1;
	}

	public String getDosisT1() {
		return dosisT1;
	}

	public void setDosisT1(String dosisT1) {
		this.dosisT1 = dosisT1;
	}

	public String getFrecuencia1() {
		return frecuencia1;
	}

	public void setFrecuencia1(String frecuencia1) {
		this.frecuencia1 = frecuencia1;
	}

	public String getViaAdmon1() {
		return viaAdmon1;
	}

	public void setViaAdmon1(String viaAdmon1) {
		this.viaAdmon1 = viaAdmon1;
	}

	public String getFechaI1() {
		return fechaI1;
	}

	public void setFechaI1(String fechaI1) {
		this.fechaI1 = fechaI1;
	}

	public String getFechaF1() {
		return fechaF1;
	}

	public void setFechaF1(String fechaF1) {
		this.fechaF1 = fechaF1;
	}

	public String getIndicacion1() {
		return indicacion1;
	}

	public void setIndicacion1(String indicacion1) {
		this.indicacion1 = indicacion1;
	}
		
}
