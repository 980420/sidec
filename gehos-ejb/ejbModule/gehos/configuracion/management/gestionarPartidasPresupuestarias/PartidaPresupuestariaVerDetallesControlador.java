package gehos.configuracion.management.gestionarPartidasPresupuestarias;

import gehos.configuracion.management.entity.PartidaPresupuestariaHospital_configuracion;
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

@Name("partidaPresupuestariaVerDetallesControlador")
@Scope(ScopeType.CONVERSATION)
public class PartidaPresupuestariaVerDetallesControlador {

	// departamento
	private Long partidaPresupuestariaId;
	private PartidaPresupuestariaHospital_configuracion partidaPresupuestaria = new PartidaPresupuestariaHospital_configuracion();

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
			partidaPresupuestaria = entidadSistemaCrearControlador
					.getPartidaPresupuestaria();

		} else {
			partidaPresupuestaria = entidadSistemaModificarControlador
					.getPartidaPresupuestaria();

		}
	}

	public String eliminar() {
		try {

			if (from.equals("crear")) {
				entidadSistemaCrearControlador.getPartidaPresupuestariaList()
						.remove(partidaPresupuestaria);
				entidadSistemaCrearControlador
						.getPartidaPresupuestariaList_controler()
						.getElementos().remove(partidaPresupuestaria);

			} else {
				entidadSistemaModificarControlador.getPartidaPresupuestariaList()
						.remove(partidaPresupuestaria);
				entidadSistemaModificarControlador
						.getPartidaPresupuestariaList_controler()
						.getElementos().remove(partidaPresupuestaria);
			}

		} catch (Exception e) {
			return "fail";
		}
		return "eliminar";
	}

	// PROPIEDADES-------------------------------------------------
	public String getFrom() {
		return from;
	}

	public Long getPartidaPresupuestariaId() {
		return partidaPresupuestariaId;
	}

	public void setPartidaPresupuestariaId(Long partidaPresupuestariaId) {
		this.partidaPresupuestariaId = partidaPresupuestariaId;
	}

	public PartidaPresupuestariaHospital_configuracion getPartidaPresupuestaria() {
		return partidaPresupuestaria;
	}

	public void setPartidaPresupuestaria(
			PartidaPresupuestariaHospital_configuracion partidaPresupuestaria) {
		this.partidaPresupuestaria = partidaPresupuestaria;
	}

	

}
