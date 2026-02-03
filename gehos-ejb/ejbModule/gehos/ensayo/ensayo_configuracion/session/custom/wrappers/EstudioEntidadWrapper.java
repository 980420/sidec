package gehos.ensayo.ensayo_configuracion.session.custom.wrappers;

import gehos.ensayo.entity.EstudioEntidad_ensayo;

public class EstudioEntidadWrapper implements IWrapper{

	private String type = "centro";
	private EstudioEntidad_ensayo value;
	private boolean expanded = false;
	private Long estudioID;

	public EstudioEntidadWrapper(EstudioEntidad_ensayo estudioCentro, boolean expanded,
			Long entidadId) {
		super();
		this.value = estudioCentro;
		this.expanded = expanded;
		this.estudioID = entidadId;
	}
	
	public String getType() {
		return type;
	}	
	
	public void setType(String type) {
		this.type = type;
	}
	
	public EstudioEntidad_ensayo getValue() {
		return this.value;
	}
	
	public void setValue(EstudioEntidad_ensayo find) {
	    this.value = find;			
	}
	
	public Long getEntidadID() {
		return estudioID;
	}

	public void setEntidadID(Long entidadID) {
		this.estudioID = entidadID;
	}
	
	
	

	@Override
	public String toString() {		
		return "centro";
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
		return this.value.getCid();
	}
	
	@Override
	public void setValue(Object find) {
		this.value = (EstudioEntidad_ensayo) find;		
	}
			
}
