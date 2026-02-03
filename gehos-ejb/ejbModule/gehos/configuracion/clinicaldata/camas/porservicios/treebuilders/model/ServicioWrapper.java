package gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model;

import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;

public class ServicioWrapper implements ITreeData {
	private String type = "servicio";
	private ServicioInEntidad_configuracion value;
	private boolean expanded = false;
	private Long entidadID;
	private Boolean existía;
	private Boolean existe;
	private Boolean visible;
	private Boolean teníaHospitalizacion;
	private Boolean tieneHospitalizacion;
	private Boolean teníaConsultaExterna;
	private Boolean tieneConsultaExterna;

	public ServicioInEntidad_configuracion getValue() {
		return value;
	}

	public void setValue(ServicioInEntidad_configuracion value) {
		this.value = value;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ServicioWrapper(ServicioInEntidad_configuracion value,
			boolean expanded, Long entidadId) {
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
		this.value = (ServicioInEntidad_configuracion) value;
	}

	public Boolean getExistía() {
		return existía;
	}

	public void setExistía(Boolean existía) {
		this.existía = existía;
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

	public Boolean getTeníaHospitalizacion() {
		return teníaHospitalizacion;
	}

	public void setTeníaHospitalizacion(Boolean teníaHospitalizacion) {
		this.teníaHospitalizacion = teníaHospitalizacion;
	}

	public Boolean getTeníaConsultaExterna() {
		return teníaConsultaExterna;
	}

	public void setTeníaConsultaExterna(Boolean teníaConsultaExterna) {
		this.teníaConsultaExterna = teníaConsultaExterna;
	}

}
