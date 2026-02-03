package gehos.configuracion.management.gestionarAsignacionesFinancieras;

import gehos.configuracion.management.entity.AsignacionFinancieraHospital_configuracion;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaCrearControlador;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaModificarControlador;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;

@Name("asignacionFinancieraVerDetallesControlador")
@Scope(ScopeType.CONVERSATION)
public class AsignacionFinancieraVerDetallesControlador {

	// departamento
	private Long asignacionFinancieraId;
	private AsignacionFinancieraHospital_configuracion asignacionFinanciera = new AsignacionFinancieraHospital_configuracion();

	// otras funcionalidades
	private String from = "";

	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	@In(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaModificarControlador entidadSistemaModificarControlador;

	@In(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaCrearControlador entidadSistemaCrearControlador;

	// METODOS---------------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setFrom(String from) {
		this.from = from;
		if (from.equals("crear")) {
			asignacionFinanciera = entidadSistemaCrearControlador
					.getAsingacionFinanciera();			
		} else {
			asignacionFinanciera = entidadSistemaModificarControlador
					.getAsignacionFinanciera();			
		}
	}

	public String eliminar() {
		try {

			if (from.equals("crear")) {
				entidadSistemaCrearControlador.getAsignacionFinancieraList()
						.remove(asignacionFinanciera);
				entidadSistemaCrearControlador.getAsignacionFinancieraList_controler().getElementos().remove(asignacionFinanciera);
				
			} else {
				entidadSistemaModificarControlador.getAsignacionFinancieraList().remove(
						asignacionFinanciera);
				entidadSistemaModificarControlador.getAsignacionFinancieraList_controler().getElementos().remove(asignacionFinanciera);
			}

		} catch (Exception e) {
			return "fail";
		}
		return "eliminar";
	}

	// PROPIEDADES-------------------------------------------------
	public Long getAsignacionFinancieraId() {
		return asignacionFinancieraId;
	}

	public String getFrom() {
		return from;
	}

	public AsignacionFinancieraHospital_configuracion getAsignacionFinanciera() {
		return asignacionFinanciera;
	}

	public void setAsignacionFinanciera(
			AsignacionFinancieraHospital_configuracion asignacionFinanciera) {
		this.asignacionFinanciera = asignacionFinanciera;
	}

}
