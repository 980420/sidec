package gehos.ensayo.ensayo_estadisticas.session.reporte_estudio;

public class CrosstabElement 
{
	String 
		row,
		column,
		value;

	public CrosstabElement(String row, String column, String value) 
	{
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
	
}
