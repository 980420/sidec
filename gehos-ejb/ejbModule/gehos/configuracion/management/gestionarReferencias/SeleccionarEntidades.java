package gehos.configuracion.management.gestionarReferencias;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Referencia_configuracion;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("seleccionarEntidades")
@Scope(ScopeType.CONVERSATION)
public class SeleccionarEntidades {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	@In(required = false, value = "referenciaBuscarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "referenciaBuscarControlador", scope = ScopeType.CONVERSATION)
	ReferenciaBuscarControlador referenciaBuscarControlador;

	private Long id; // Id de la entidad origen
	private Long seleccionada; // Id de la entidad seleccionada para adicionar
	private int error;

	private Entidad_configuracion entidadOrigen = new Entidad_configuracion();

	// Posibles entidades a referenciar
	private List<Entidad_configuracion> listaEntidades = new ArrayList<Entidad_configuracion>();
	private ListadoControler<Entidad_configuracion> listaControlerEntidades;

	// Entidades seleccionadas
	private List<Entidad_configuracion> listanewEntidades = new ArrayList<Entidad_configuracion>();

	@SuppressWarnings("unchecked")
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {
		try {
			error = 0;
			this.id = id;

			this.entidadOrigen = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidadOrigen")
					.setParameter("idEntidadOrigen", id).getSingleResult();
			entityManager.refresh(entidadOrigen);

			this.listaEntidades = entityManager
					.createQuery(
							"select entidades from Entidad_configuracion entidades "
									+ "where entidades.eliminado = false "
									// + "and entidades.id <>:idEntidadOrigen "
									+ "and entidades not in (select referencia.entidadByIdEntidadDestino from Referencia_configuracion referencia "
									+ "where referencia.eliminado = false "
									+ "and referencia.entidadByIdEntidadOrigen.id=:idEntidadOrigen)")
					.setParameter("idEntidadOrigen", entidadOrigen.getId())
					.getResultList();
			this.listaControlerEntidades = new ListadoControler<Entidad_configuracion>(
					this.listaEntidades);

		} catch (NoResultException e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "eliminado");
		} catch (Exception e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
		}
	}

	/*
	 * Metodo complementario para saber si una entidad esta seleccionada o noSe
	 * utiliza en los checkbox
	 */
	public boolean estaSeleccionada(Long id) {
		for (int i = 0; i < listanewEntidades.size(); i++)
			if (listanewEntidades.get(i).getId().equals(id))
				return true;
		return false;
	}

	// Selecciona una entidad
	public void adicionartoSeleccionadas() {

		// Si esta seleccionada la elimina sino la adiciona
		if (estaSeleccionada(this.seleccionada)) {
			Entidad_configuracion aux = entityManager.find(
					Entidad_configuracion.class, this.seleccionada);
			listanewEntidades.remove(aux);
		} else {
			Entidad_configuracion aux = entityManager.find(
					Entidad_configuracion.class, this.seleccionada);
			listanewEntidades.add(aux);
		}
	}

	// Elimina una entidad de la lista de entidades seleccionadas
	public void eliminar(Long id) {
		Entidad_configuracion aux = entityManager.find(
				Entidad_configuracion.class, id);
		listanewEntidades.remove(aux);
	}

	// Adicionar las nuevas entidades como entidades referenciadas
	@SuppressWarnings("deprecation")
	public void adicionar() {
		error = 0;
		try {
			if (listanewEntidades.size() == 0) {
				error = 1;
				facesMessages.add(new FacesMessage(
						"Debe que seleccionar al menos una entidad"));
				return;
			}

			entityManager.flush();

			if (listanewEntidades.size() > 0) {
				Long cid = bitacora
						.registrarInicioDeAccion("Adicionando entidades para referencias.");
				for (int i = 0; i < listanewEntidades.size(); i++) {
					Referencia_configuracion aux = new Referencia_configuracion();

					entityManager.refresh(listanewEntidades.get(i));
					entityManager.flush();

					aux.setEliminado(false);
					aux.setEntidadByIdEntidadOrigen(this.entidadOrigen);
					aux.setEntidadByIdEntidadDestino(listanewEntidades.get(i));
					aux.setCid(cid);
					entityManager.persist(aux);
					entityManager.flush();

					referenciaBuscarControlador
							.actualizarEntidadesReferenciadas();
				}
			}

			end();
		} catch (Exception e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
		}
	}

	@End
	public void end() {
	}

	// Propiedades
	public List<Entidad_configuracion> getListaEntidades() {
		return listaEntidades;
	}

	public void setListaEntidades(List<Entidad_configuracion> listaEntidades) {
		this.listaEntidades = listaEntidades;
	}

	public ListadoControler<Entidad_configuracion> getListaControlerEntidades() {
		return listaControlerEntidades;
	}

	public void setListaControlerEntidades(
			ListadoControler<Entidad_configuracion> listaControlerEntidades) {
		this.listaControlerEntidades = listaControlerEntidades;
	}

	public Entidad_configuracion getEntidadOrigen() {
		return entidadOrigen;
	}

	public void setEntidadOrigen(Entidad_configuracion entidadOrigen) {
		this.entidadOrigen = entidadOrigen;
	}

	public Long getId() {
		return id;
	}

	public Long getSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(Long seleccionada) {
		this.seleccionada = seleccionada;
	}

	public List<Entidad_configuracion> getListanewEntidades() {
		return listanewEntidades;
	}

	public void setListanewEntidades(
			List<Entidad_configuracion> listanewEntidades) {
		this.listanewEntidades = listanewEntidades;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

}
