package gehos.ensayo.ensayo_configuracion.session.custom.wrappers;

import gehos.ensayo.entity.Entidad_ensayo;


public class EntidadWrapper implements IWrapper{

	private String type = "entidad";
	private Entidad_ensayo value;
	private boolean expanded = false;
	private Long entidadID;
	
	public EntidadWrapper(Entidad_ensayo value, boolean expanded,
			Long entidadID) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.entidadID = entidadID;
	}
	
	public String getType() {
		return type;
	}	
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Entidad_ensayo getValue() {
		return this.value;
	}
	
	public void setValue(Entidad_ensayo find) {
	    this.value = find;			
	}
	
	public Long getEntidadID() {
		return entidadID;
	}

	public void setEntidadID(Long entidadID) {
		this.entidadID = entidadID;
	}
		
	
	@Override
	public String toString() {		
		return "entidad";
	}

	@Override
	public boolean isExpanded() {
		return this.expanded;
	}

	@Override
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public Long getId() {
		return this.value.getId();
	}
	
	@Override
	public void setValue(Object find) {
		this.value = (Entidad_ensayo) find;		
	}
}
