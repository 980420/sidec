package gehos.ensayo.ensayo_estadisticas.session.reportes.wrappers;

public class NotasMonitoreoEstadoSujetoWrapper {
	String columnHeaderValue;
	String columnBucketValue;
	String rowBucketValue;
	Long measureValueMeasure;
	
	public NotasMonitoreoEstadoSujetoWrapper(String columnHeaderValue,
			String columnBucketValue, String rowBucketValue,
			Long measureValueMeasure) {
		this.columnHeaderValue = columnHeaderValue;
		this.columnBucketValue = columnBucketValue;
		this.rowBucketValue = rowBucketValue;
		this.measureValueMeasure = measureValueMeasure;
	}
	
	public String getColumnHeaderValue() {
		return columnHeaderValue;
	}
	public void setColumnHeaderValue(String columnHeaderValue) {
		this.columnHeaderValue = columnHeaderValue;
	}
	public String getColumnBucketValue() {
		return columnBucketValue;
	}
	public void setColumnBucketValue(String columnBucketValue) {
		this.columnBucketValue = columnBucketValue;
	}
	public String getRowBucketValue() {
		return rowBucketValue;
	}
	public void setRowBucketValue(String rowBucketValue) {
		this.rowBucketValue = rowBucketValue;
	}
	public Long getMeasureValueMeasure() {
		return measureValueMeasure;
	}
	public void setMeasureValueMeasure(Long measureValueMeasure) {
		this.measureValueMeasure = measureValueMeasure;
	}
	
	
}
