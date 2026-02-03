package gehos.ensayo.ensayo_estadisticas.cantMSxSujeto;

public class cantMSxSujetoSource {
	
	String rowSujeto;
	String columMsg;
	String measureCantMSE;
	
	
	public cantMSxSujetoSource(String rowSujeto, String columMsg, String measureCantMSE) {
		super();
		this.rowSujeto = rowSujeto;
		this.columMsg = columMsg;
		this.measureCantMSE = measureCantMSE;
		
	}
	
	public cantMSxSujetoSource() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getRowSujeto() {
		return rowSujeto;
	}

	public void setRowSujeto(String rowSujeto) {
		this.rowSujeto = rowSujeto;
	}

	public String getColumMsg() {
		return columMsg;
	}

	public void setColumMsg(String columMsg) {
		this.columMsg = columMsg;
	}

	public String getMeasureCantMSE() {
		return measureCantMSE;
	}

	public void setMeasureCantMSE(String measureCantMSE) {
		this.measureCantMSE = measureCantMSE;
	}

	

}
