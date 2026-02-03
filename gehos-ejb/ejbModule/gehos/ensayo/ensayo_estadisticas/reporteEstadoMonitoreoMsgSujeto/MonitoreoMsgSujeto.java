package gehos.ensayo.ensayo_estadisticas.reporteEstadoMonitoreoMsgSujeto;

public class MonitoreoMsgSujeto {
	String rowEntidad;
	String rowSujeto;	
	String columnMsg;
	String columnEstado;
	String measureEstadoMsg;
	
	
	public MonitoreoMsgSujeto(String rowEntidad, String rowSujeto,
			String columnMsg, String columnEstado, String measureEstadoMsg) {
		super();
		this.rowEntidad = rowEntidad;
		this.rowSujeto = rowSujeto;
		this.columnMsg = columnMsg;
		this.columnEstado = columnEstado;
		this.measureEstadoMsg = measureEstadoMsg;
	}
	
	public String getRowSujeto() {
		return rowSujeto;
	}
	public void setRowSujeto(String rowSujeto) {
		this.rowSujeto = rowSujeto;
	}
	public String getRowEntidad() {
		return rowEntidad;
	}
	public void setRowEntidad(String rowEntidad) {
		this.rowEntidad = rowEntidad;
	}
	public String getColumnMsg() {
		return columnMsg;
	}
	public void setColumnMsg(String columnMsg) {
		this.columnMsg = columnMsg;
	}

	public String getMeasureEstadoMsg() {
		return measureEstadoMsg;
	}

	public void setMeasureEstadoMsg(String measureEstadoMsg) {
		this.measureEstadoMsg = measureEstadoMsg;
	}

	public String getColumnEstado() {
		return columnEstado;
	}

	public void setColumnEstado(String columnEstado) {
		this.columnEstado = columnEstado;
	}
	
}
