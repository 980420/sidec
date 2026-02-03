package gehos.ensayo.ensayo_estadisticas.reporteVariablesCritias;

public class VariablesCriticasConstruc {
	private String nombreSujeto;
	private String msE;
	private String nombreCRD;
	private String nombreVariable;
	private String valorVariable;
	private String style;
	
	public VariablesCriticasConstruc(String nombreSujeto, String msE,
			String nombreCRD, String nombreVariable,String valorVariable, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.msE = msE;
		this.nombreCRD = nombreCRD;
		this.nombreVariable = nombreVariable;
		this.valorVariable = valorVariable;
		this.style = style;
	}
	
	public VariablesCriticasConstruc(String nombreSujeto, String msE,
			String nombreCRD, String nombreVariable,String valorVariable) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.msE = msE;
		this.nombreCRD = nombreCRD;
		this.nombreVariable = nombreVariable;
		this.valorVariable = valorVariable;
	}
	
	

	public String getNombreSujeto() {
		return nombreSujeto;
	}

	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}

	public String getmsE() {
		return msE;
	}

	public void setmsE(String msE) {
		this.msE = msE;
	}

	public String getNombreCRD() {
		return nombreCRD;
	}

	public void setNombreCRD(String nombreCRD) {
		this.nombreCRD = nombreCRD;
	}

	public String getNombreVariable() {
		return nombreVariable;
	}

	public void setNombreVariable(String nombreVariable) {
		this.nombreVariable = nombreVariable;
	}


	public String getValorVariable() {
		return valorVariable;
	}

	public void setValorVariable(String valorVariable) {
		this.valorVariable = valorVariable;
	}

		
}
