package gehos.bitacora.treebuilders.model;


public class Mes implements ITreeData{
	private Integer value;
	private Integer anno;
	private boolean expanded = false;

	public Mes(Integer value, Integer anno, boolean expanded) {
		super();
		this.value = value;
		this.anno = anno;
		this.expanded = expanded;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public String getMessagesKey(){
		return "month_" + value.toString();
	}
	
	public String toString(){
		return "mes";
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
}
