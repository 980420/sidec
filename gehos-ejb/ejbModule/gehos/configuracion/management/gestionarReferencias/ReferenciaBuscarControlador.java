package gehos.configuracion.management.gestionarReferencias;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Referencia_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

@Name("referenciaBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class ReferenciaBuscarControlador {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	private Entidad_configuracion entidadOrigen;
	private Entidad_configuracion entidadDestino;

	// Entidades origen
	private List<Entidad_configuracion> entidadesOrigen = new ArrayList<Entidad_configuracion>();
	private ListadoControler<Entidad_configuracion> listaControlerEntityOrigen;

	// Entidades referenciadas
	private List<Entidad_configuracion> entidadesReferenciadas = new ArrayList<Entidad_configuracion>();
	private ListadoControler<Entidad_configuracion> listadoControlerEntityDestino = new ListadoControler<Entidad_configuracion>(
			entidadesReferenciadas);

	// Servicios
	private List<Servicio_configuracion> listaServicios = new ArrayList<Servicio_configuracion>();
	private ListadoControler<Servicio_configuracion> listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
			listaServicios);

	// Referencias
	private List<Referencia_configuracion> listaReferencias = new ArrayList<Referencia_configuracion>();

	// Referencia del servicio seleccionado
	private Referencia_configuracion refAux = new Referencia_configuracion();

	// Atributos
	private String nombreServicio = "";

	private Referencia_configuracion referencia = new Referencia_configuracion();

	// Methods --------------------------------------------------
	@SuppressWarnings("unchecked")
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.entidadesOrigen = entityManager.createQuery(
				"select entity from Entidad_configuracion entity "
						+ "where entity.eliminado = false "
						/**
						 * @author yurien 27/03/2014
						 * Se cambia la forma en que se muestran las entidades que pertenecen al anillo
						 * **/
//						 + "and entity.perteneceARhio = true "
                         + "and entity.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
						+ "order by entity.nombre asc").getResultList();

		if (this.listaControlerEntityOrigen == null)
			this.listaControlerEntityOrigen = new ListadoControler<Entidad_configuracion>(
					this.entidadesOrigen);

	}

	@SuppressWarnings("unchecked")
	public void actualizarEntidadesReferenciadas() {
		// Busco las entidades destinos
		this.entidadesReferenciadas = entityManager
				.createQuery(
						"select distinct referencia.entidadByIdEntidadDestino from Referencia_configuracion referencia "
								+ "where referencia.entidadByIdEntidadOrigen.nombre=:entityOrigen "
								+ "and referencia.eliminado = false "
								+ "order by referencia.entidadByIdEntidadDestino.nombre asc")
				.setParameter("entityOrigen", this.entidadOrigen.getNombre())
				.getResultList();

		this.listadoControlerEntityDestino = new ListadoControler<Entidad_configuracion>(
				this.entidadesReferenciadas);
	}

	@SuppressWarnings("unchecked")
	public void actualizarServicios() {
		this.listaServicios = entityManager
				.createQuery(
						"select ref.servicio from Referencia_configuracion ref "
								+ "where ref.entidadByIdEntidadOrigen.nombre=:entityOrigen "
								+ "and ref.entidadByIdEntidadDestino.nombre=:entityDestino "
								+ "and ref.servicio.eliminado=false")
				.setParameter("entityOrigen", this.entidadOrigen.getNombre())
				.setParameter("entityDestino", this.entidadDestino.getNombre())
				.getResultList();

		this.listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
				this.listaServicios);
		this.listadoControlerServicio.setOrder("nombre");
	}

	// Seleccion de la entidad
	@SuppressWarnings("unchecked")
	public void seleccionEntity(Long idEntity) {
		// Selecciono la Entity origen
		this.entidadOrigen = entityManager.find(Entidad_configuracion.class,
				idEntity);

		// Busco las entidades destinos
		this.entidadesReferenciadas = entityManager
				.createQuery(
						"select distinct referencia.entidadByIdEntidadDestino from Referencia_configuracion referencia "
								+ "where referencia.entidadByIdEntidadOrigen.nombre=:entityOrigen "
								+ "and referencia.eliminado = false "
								+ "order by referencia.entidadByIdEntidadDestino.nombre asc")
				.setParameter("entityOrigen", this.entidadOrigen.getNombre())
				.getResultList();
		this.listadoControlerEntityDestino = new ListadoControler<Entidad_configuracion>(
				this.entidadesReferenciadas);

		// Limpio la lista de servicios
		this.listaServicios.clear();
		this.listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
				this.listaServicios);
		this.listadoControlerServicio.setOrder("nombre");

		/*
		 * Limpio la entidad referenciada seleccionada a partir de la cual se
		 * cargaran los servicios
		 */
		this.entidadDestino = null;
	}

	// Selecciona una entidad destino para cargar los servicios
	@SuppressWarnings("unchecked")
	public void seleccionServices(Long idEntity) {

		try {
			// Selecciono la Entity destino
			this.entidadDestino = entityManager.find(
					Entidad_configuracion.class, idEntity);

			List<Referencia_configuracion> refConcu = entityManager
					.createQuery(
							"select ref from Referencia_configuracion ref "
									+ "where ref.entidadByIdEntidadOrigen.nombre=:entityOrigen "
									+ "and ref.entidadByIdEntidadDestino.nombre=:entityDestino")
					.setParameter("entityOrigen",
							this.entidadOrigen.getNombre())
					.setParameter("entityDestino",
							this.entidadDestino.getNombre()).getResultList();
			if (refConcu.size() == 0) {
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "eliminado");
				Transaction.instance().rollback();
				seleccionEntity(entidadOrigen.getId());
				return;
			}

			this.listaServicios = entityManager
					.createQuery(
							"select ref.servicio from Referencia_configuracion ref "
									+ "where ref.entidadByIdEntidadOrigen.nombre=:entityOrigen "
									+ "and ref.entidadByIdEntidadDestino.nombre=:entityDestino "
									+ "and ref.servicio.eliminado=false")
					.setParameter("entityOrigen",
							this.entidadOrigen.getNombre())
					.setParameter("entityDestino",
							this.entidadDestino.getNombre()).getResultList();

			this.listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
					this.listaServicios);
			this.listadoControlerServicio.setOrder("nombre");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
				this.listaServicios);
	}

	// Selecciona la entidad referenciada a eliminar
	@SuppressWarnings("unchecked")
	public void seleccionarRef(Long refId) throws IllegalStateException,
			SecurityException, SystemException {
		// Selecciono la Entity referenciada
		this.entidadDestino = (Entidad_configuracion) entityManager
				.createQuery(
						"select entidad from Entidad_configuracion entidad where entidad.id =:idEnt")
				.setParameter("idEnt", refId).getSingleResult();

		List<Referencia_configuracion> refConcu = entityManager
				.createQuery(
						"select ref from Referencia_configuracion ref "
								+ "where ref.entidadByIdEntidadOrigen.nombre=:entityOrigen "
								+ "and ref.entidadByIdEntidadDestino.nombre=:entityDestino")
				.setParameter("entityOrigen", this.entidadOrigen.getNombre())
				.setParameter("entityDestino", this.entidadDestino.getNombre())
				.getResultList();

		if (refConcu.size() == 0) {
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			seleccionEntity(entidadOrigen.getId());
			return;
		}

		/*
		 * Selecciono todas las referencias que coincida con esa entidad origen
		 * y la entidad destino. Existe una referencia por cada servicio
		 */
		this.listaReferencias = entityManager
				.createQuery(
						"select referencia  from Referencia_configuracion referencia "
								+ "where referencia.entidadByIdEntidadOrigen.nombre=:nombreEntityO "
								+ "and referencia.entidadByIdEntidadDestino.nombre=:nombreEntityD")
				.setParameter("nombreEntityO", this.entidadOrigen.getNombre())
				.setParameter("nombreEntityD", this.entidadDestino.getNombre())
				.getResultList();
	}

	// Eliminar entidad referenciada y referencias
	public void eliminarEntidad() throws IllegalStateException,
			SecurityException, SystemException {
		try {
			Referencia_configuracion aux = new Referencia_configuracion();

			if (listaReferencias.size() > 0) {
				for (int i = 0; i < this.listaReferencias.size(); i++) {

					aux = (Referencia_configuracion) entityManager
							.createQuery(
									"select r from Referencia_configuracion r where r.id = :idReferencia")
							.setParameter("idReferencia",
									listaReferencias.get(i).getId())
							.getSingleResult();

					entityManager.merge(aux);
					entityManager.remove(aux);
					entityManager.flush();
				}

				this.entidadesReferenciadas.remove(entidadDestino);
				this.listadoControlerEntityDestino = new ListadoControler<Entidad_configuracion>(
						this.entidadesReferenciadas);

				this.listaServicios.clear();
				this.listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
						this.listaServicios);
				this.entidadDestino = null;
			}
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "eliminado");
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "enuso");
			Transaction.instance().rollback();
		}
	}

	// Seleccionar servicio para eliminar
	public void seleccionarServicio(Long idServ) throws IllegalStateException,
			SecurityException, SystemException {
		try {
			this.refAux = (Referencia_configuracion) entityManager
					.createQuery(
							"select referencia  from Referencia_configuracion referencia "
									+ "where referencia.entidadByIdEntidadOrigen.nombre=:nombreEntityO "
									+ "and referencia.entidadByIdEntidadDestino.nombre=:nombreEntityD and referencia.servicio.id=:idServicio")
					.setParameter("nombreEntityO",
							this.entidadOrigen.getNombre())
					.setParameter("nombreEntityD",
							this.entidadDestino.getNombre())
					.setParameter("idServicio", idServ).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "eliminado");
			seleccionServices(entidadDestino.getId());
		}

	}

	// Eliminar servicio seleccionado
	public void eliminarServicio() throws IllegalStateException,
			SecurityException, SystemException {
		try {

			Long cid = bitacora
					.registrarInicioDeAccion("Eliminando servicio para referencia.");
			Servicio_configuracion servicio = refAux.getServicio();
			refAux.setCid(cid);
			this.refAux.setServicio(null);
			entityManager.remove(refAux);
			entityManager.flush();

			this.listaServicios.remove(servicio);
			listadoControlerServicio = new ListadoControler<Servicio_configuracion>(
					listaServicios);
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "enuso");
			Transaction.instance().rollback();
		}

	}

	private Long id;

	public Boolean mostrarEntityReferenciadas() {
		if (this.entidadOrigen == null) {
			return true;
		}
		return false;
	}

	public Boolean mostrarServicios() {
		if (this.entidadDestino == null) {
			return true;
		}
		return false;
	}

	// Propiedades

	public Referencia_configuracion getReferencia() {
		return referencia;
	}

	public void setReferencia(Referencia_configuracion referencia) {
		this.referencia = referencia;
	}

	public Entidad_configuracion getEntidadOrigen() {
		return entidadOrigen;
	}

	public void setEntidadOrigen(Entidad_configuracion entidadOrigen) {
		this.entidadOrigen = entidadOrigen;
	}

	public Entidad_configuracion getEntidadDestino() {
		return entidadDestino;
	}

	public void setEntidadDestino(Entidad_configuracion entidadDestino) {
		this.entidadDestino = entidadDestino;
	}

	public String getNombreServicio() {
		return nombreServicio;
	}

	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Entidad_configuracion> getEntidadesOrigen() {
		return entidadesOrigen;
	}

	public void setEntidadesOrigen(List<Entidad_configuracion> entidadesOrigen) {
		this.entidadesOrigen = entidadesOrigen;
	}

	public List<Entidad_configuracion> getEntidadesReferenciadas() {
		return entidadesReferenciadas;
	}

	public void setEntidadesReferenciadas(
			List<Entidad_configuracion> entidadesReferenciadas) {
		this.entidadesReferenciadas = entidadesReferenciadas;
	}

	public List<Servicio_configuracion> getListaServicios() {
		return listaServicios;
	}

	public void setListaServicios(List<Servicio_configuracion> listaServicios) {
		this.listaServicios = listaServicios;
	}

	public ListadoControler<Entidad_configuracion> getListaControlerEntityOrigen() {
		return listaControlerEntityOrigen;
	}

	public void setListaControlerEntityOrigen(
			ListadoControler<Entidad_configuracion> listaControlerEntityOrigen) {
		this.listaControlerEntityOrigen = listaControlerEntityOrigen;
	}

	public ListadoControler<Entidad_configuracion> getListadoControlerEntityDestino() {
		return listadoControlerEntityDestino;
	}

	public void setListadoControlerEntityDestino(
			ListadoControler<Entidad_configuracion> listadoControlerEntityDestino) {
		this.listadoControlerEntityDestino = listadoControlerEntityDestino;
	}

	public ListadoControler<Servicio_configuracion> getListadoControlerServicio() {
		return listadoControlerServicio;
	}

	public void setListadoControlerServicio(
			ListadoControler<Servicio_configuracion> listadoControlerServicio) {
		this.listadoControlerServicio = listadoControlerServicio;
	}

	public List<Referencia_configuracion> getListaReferencias() {
		return listaReferencias;
	}

	public void setListaReferencias(
			List<Referencia_configuracion> listaReferencias) {
		this.listaReferencias = listaReferencias;
	}

	public Referencia_configuracion getRefAux() {
		return refAux;
	}

	public void setRefAux(Referencia_configuracion refAux) {
		this.refAux = refAux;
	}

}
