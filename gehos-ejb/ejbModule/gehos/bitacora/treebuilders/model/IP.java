package gehos.bitacora.treebuilders.model;

import gehos.bitacora.entity.User_Bitacora;


public class IP implements ITreeData{
	private String value;
	private Integer dia;
	private Integer mes;
	private Integer anno;
	private boolean expanded = false;

	public IP(String value, Integer dia , Integer mes, Integer anno, boolean expanded) {
		super();
		this.value = value;
		this.dia = dia;
		this.mes = mes;
		this.anno = anno;
		this.expanded = expanded;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		return "ip";
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
	
	public Integer getDia() {
		return dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}
}
