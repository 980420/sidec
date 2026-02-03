package gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model;

import gehos.configuracion.management.entity.Servicio_configuracion;

public class ServicioWrapper implements ITreeData {
	private String type = "servicio";
	private Servicio_configuracion value;
	private boolean expanded = false;
	private Long entidadID;
	private Boolean existia;
	private Boolean existe;
	private Boolean visible;
	private Boolean teniaHospitalizacion;
	private Boolean tieneHospitalizacion;
	private Boolean teniaConsultaExterna;
	private Boolean tieneConsultaExterna;
	private Boolean teniaEmergencias;
	private Boolean tieneEmergencias;

	public Servicio_configuracion getValue() {
		return value;
	}

	public void setValue(Servicio_configuracion value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ServicioWrapper(Servicio_configuracion value, boolean expanded,
			Long entidadId) {
		super();
		this.value = value;
		this.expanded = expanded;
		this.entidadID = entidadId;
	}

	@Override
	public String toString() {
		return "servicio";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {

		return this.value.getId();
	}

	public Long getEntidadID() {
		return entidadID;
	}

	public void setEntidadID(Long entidadID) {
		this.entidadID = entidadID;
	}

	public void setValue(Object value) {
		this.value = (Servicio_configuracion) value;
	}

	public Boolean getExiste() {
		return existe;
	}

	public void setExiste(Boolean existe) {
		this.existe = existe;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getTieneHospitalizacion() {
		return tieneHospitalizacion;
	}

	public void setTieneHospitalizacion(Boolean tieneHospitalizacion) {
		this.tieneHospitalizacion = tieneHospitalizacion;
	}

	public Boolean getTieneConsultaExterna() {
		return tieneConsultaExterna;
	}

	public void setTieneConsultaExterna(Boolean tieneConsultaExterna) {
		this.tieneConsultaExterna = tieneConsultaExterna;
	}

	public Boolean getExistia() {
		return existia;
	}

	public void setExistia(Boolean existia) {
		this.existia = existia;
	}

	public Boolean getTeniaHospitalizacion() {
		return teniaHospitalizacion;
	}

	public void setTeniaHospitalizacion(Boolean teniaHospitalizacion) {
		this.teniaHospitalizacion = teniaHospitalizacion;
	}

	public Boolean getTeniaConsultaExterna() {
		return teniaConsultaExterna;
	}

	public void setTeniaConsultaExterna(Boolean teniaConsultaExterna) {
		this.teniaConsultaExterna = teniaConsultaExterna;
	}

	public Boolean getTeniaEmergencias() {
		return teniaEmergencias;
	}

	public void setTeniaEmergencias(Boolean teniaEmergencias) {
		this.teniaEmergencias = teniaEmergencias;
	}

	public Boolean getTieneEmergencias() {
		return tieneEmergencias;
	}

	public void setTieneEmergencias(Boolean tieneEmergencias) {
		this.tieneEmergencias = tieneEmergencias;
	}

}
