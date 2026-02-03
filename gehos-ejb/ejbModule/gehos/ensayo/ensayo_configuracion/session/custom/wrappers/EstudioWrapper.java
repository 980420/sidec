package gehos.ensayo.ensayo_configuracion.session.custom.wrappers;

import gehos.ensayo.entity.Estudio_ensayo;

public class EstudioWrapper implements IWrapper{

	private String type = "estudio";
	private Estudio_ensayo value;
	private boolean expanded = false;
	private Long estudioID;
	
	public EstudioWrapper(Estudio_ensayo value, boolean expanded,
			Long entidadId) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.estudioID = entidadId;
	}
	
	public String getType() {
		return type;
	}	
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Estudio_ensayo getValue() {
		return this.value;
	}
	
	public void setValue(Estudio_ensayo find) {
	    this.value = find;			
	}
	
	public Long getEstudioID() {
		return estudioID;
	}

	public void setEstudioID(Long entidadID) {
		this.estudioID = entidadID;
	}
		
	
	@Override
	public String toString() {		
		return "estudio";
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
		this.value = (Estudio_ensayo) find;		
	}
}
