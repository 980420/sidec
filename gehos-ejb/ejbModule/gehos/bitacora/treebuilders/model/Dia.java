package gehos.bitacora.treebuilders.model;


public class Dia implements ITreeData{
	private Integer value;
	private Integer mes;
	private Integer anno;
	private boolean expanded = false;

	public Dia(Integer value, Integer mes, Integer anno, boolean expanded) {
		super();
		this.value = value;
		this.mes = mes;
		this.anno = anno;
		this.expanded = expanded;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public String toString(){
		return "dia";
	}

	public boolean isExpanded() {
		return this.expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	
	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}
}
