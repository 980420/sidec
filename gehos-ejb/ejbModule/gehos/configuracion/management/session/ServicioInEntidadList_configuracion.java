package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("servicioInEntidadList_configuracion")
public class ServicioInEntidadList_configuracion extends
		EntityQuery<ServicioInEntidad_configuracion> {

	private static final String EJBQL = "select servicioInEntidad from ServicioInEntidad_configuracion servicioInEntidad";

	private static final String[] RESTRICTIONS = {
			"lower(servicioInEntidad.servicio.nombre) like concat(lower(#{servicioInEntidadList_configuracion.nombreServicio}),'%')",
			"servicioInEntidad.entidad.nombre like #{servicioInEntidadList_configuracion.nombreEntidad} ", };

	private ServicioInEntidad_configuracion servicioInEntidad = new ServicioInEntidad_configuracion();
	private String nombreServicio = "";
	private String nombreEntidad = "";

	public ServicioInEntidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
	}

	public ServicioInEntidad_configuracion getServicioInEntidad() {
		return servicioInEntidad;
	}

	public String getNombreServicio() {
		return nombreServicio;
	}

	public void buscar() {
		this.setFirstResult(0);
	}

	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}

	public String getNombreEntidad() {
		return nombreEntidad;
	}

	public void setNombreEntidad(String nombreEntidad) {
		this.nombreEntidad = nombreEntidad;
	}
}
