package gehos.configuracion.management.gestionarReferencias;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.anillo.AnilloHisConfig;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Referencia_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

import java.util.ArrayList;
import java.util.List;

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

@Name("seleccionarServicios")
@Scope(ScopeType.CONVERSATION)
public class SeleccionarServicios {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	@In(required = false, value = "referenciaBuscarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "referenciaBuscarControlador", scope = ScopeType.CONVERSATION)
	ReferenciaBuscarControlador referenciaBuscarControlador;
	
	/**
	 * @author yurien
	 * Se incluye el componente que gestiona el anillo actual configurado
	 * **/
	@In
	AnilloHisConfig anilloHisConfig;

	private int error;

	private Entidad_configuracion entidadOrigen = new Entidad_configuracion();
	private Entidad_configuracion entidadDestino = new Entidad_configuracion();

	// Posibles ervicios a seleccionar
	private List<Servicio_configuracion> servicios = new ArrayList<Servicio_configuracion>();
	private ListadoControler<Servicio_configuracion> listaControlServicios;

	// Servicios seleccionados
	private List<Servicio_configuracion> serviciosSeleccionados = new ArrayList<Servicio_configuracion>();

	@SuppressWarnings("unchecked")
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void cargarDatos(Long idOrigen, Long idDestino) {
		try {
			error = 0;

			this.entidadOrigen = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidadOrigen")
					.setParameter("idEntidadOrigen", idOrigen)
					.getSingleResult();

			this.entidadDestino = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidadDestino")
					.setParameter("idEntidadDestino", idDestino)
					.getSingleResult();

			// Lista de todos las servicios no seleccionados
			/**
			 * @author yurien 27/03/2014
			 * Se cambia la restriccion para que muestre las entidades
			 * que pertenecen al anillo actual configurado
			 * **/
//			if(entidadDestino.isPerteneceARhio()){	
			if(entidadDestino.getInstanciaHis().getId() == anilloHisConfig.getHisInstanceNumber()){	
				
				/**
				 * @author yurien 27/03/2014
				 * ACA SE MUESTRAN TODOS LOS SERVICIOS CUYOS DEPARTAMENTOS
				 * SON CLINICOS Y YA NO ESTAN SELECCIONADOS PARA REFERENCIAS
				 * ADEMAS DE AQUELLOS QUE QUE PERTENEZCAN A LA ENTIDAD DESTINO
				 * **/
				this.servicios = entityManager
						.createQuery(
								"select servicios from Servicio_configuracion servicios "
										+ "where servicios.eliminado=false "
										+ "and servicios.departamento.esClinico = true "
										+ "and servicios not in (select referencia.servicio from Referencia_configuracion referencia "
										+ "where referencia.eliminado = false "
										+ "and referencia.entidadByIdEntidadOrigen.id=:idEntidadO "
										+ "and referencia.entidadByIdEntidadDestino.id=:idEntidadD) "
										+ "and servicios.id in (select serv.id from Servicio_configuracion serv "
										+ "join serv.servicioInEntidads se join se.entidad ent "
										+ "where ent.id = :idEntidadD)")
						.setParameter("idEntidadO", this.entidadOrigen.getId())
						.setParameter("idEntidadD", this.entidadDestino.getId())
						.getResultList();
			}
			else{
				/**
				 * @author yurien 27/03/2014
				 * ACA SE MUESTRAN TODOS LOS SERVICIOS CUYOS DEPARTAMENTOS
				 * SON CLINICOS Y YA NO ESTAN SELECCIONADOS PARA REFERENCIAS
				 * 
				 * REVISAR CUANDO EL SISTEMA COMIENCE A FUNCIONAR CON LA REPLICA
				 * YA QUE ACA MUESTRA TODOS LOS SERVICIOS QUE HAY EN EL SISTEMA SIN TENER EN CUENTA
				 * LA ENTIDAD DESTINO. ESTO SUPUESTAMENTE SE HIZO PARA PODER HACER REFERENCIAS
				 * PARA OTRAS ENTIDADES DE PDVSA QUE AUN NO ESTUVIERAN CONFIGURADAS.
				 * 
				 * **/
				this.servicios = entityManager
						.createQuery(
								"select servicios from Servicio_configuracion servicios "
										+ "where servicios.eliminado=false "
										+ "and servicios.departamento.esClinico = true "
										+ "and servicios not in (select referencia.servicio from Referencia_configuracion referencia "
										+ "where referencia.eliminado = false "
										+ "and referencia.entidadByIdEntidadOrigen.id=:idEntidadO "
										+ "and referencia.entidadByIdEntidadDestino.id=:idEntidadD) ")
						.setParameter("idEntidadO", this.entidadOrigen.getId())
						.setParameter("idEntidadD", this.entidadDestino.getId())
						.getResultList();
			}

			listaControlServicios = new ListadoControler<Servicio_configuracion>(
					this.servicios);
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

	/* Este metodo hay que terminar de revisarlo. */
	@SuppressWarnings("unchecked")
	public void adicionar() {
		error = 0;
		try {
			if (serviciosSeleccionados.size() == 0) {
				facesMessages
						.addToControlFromResourceBundle("message",
								Severity.ERROR,
								"Debe seleccionar al menos un servicio");
				error = 1;
				return;
			}

			// Prueba de concurrencia
			for (int i = 0; i < serviciosSeleccionados.size(); i++) {
				List<Referencia_configuracion> concu = entityManager
						.createQuery(
								"select referencia from Referencia_configuracion referencia "
										+ "where referencia.eliminado = false "
										+ "and referencia.entidadByIdEntidadOrigen.id=:idEntidadO "
										+ "and referencia.entidadByIdEntidadDestino.id=:idEntidadD "
										+ "and referencia.servicio.id=:idServicio")
						.setParameter("idEntidadO", this.entidadOrigen.getId())
						.setParameter("idEntidadD", this.entidadDestino.getId())
						.setParameter("idServicio",
								this.serviciosSeleccionados.get(i).getId())
						.getResultList();

				if (concu.size() != 0) {
					facesMessages.addToControlFromResourceBundle("message",
							Severity.ERROR, "refe_existente");
					error = 1;
					return;
				}
			}
			
			for (int i = 0; i < serviciosSeleccionados.size(); i++) {
				List<ServicioInEntidad_configuracion> servs = entityManager
						.createQuery("select serv from ServicioInEntidad_configuracion serv "
								+ "where serv.entidad.id = :idEnt "
								+ "and serv.servicio.id = :idServ")
								.setParameter("idEnt", entidadDestino.getId())
								.setParameter("idServ", serviciosSeleccionados.get(i).getId())
								.getResultList();
				if(servs.size() > 0){
					ServicioInEntidad_configuracion serv = servs.get(0);
					serv.setEliminado(false);
					serv.setEntidad(entidadDestino);
					serv.setServicio(serviciosSeleccionados.get(i));
					serv.setTieneConsultaExterna(true);
				}
				else{
					ServicioInEntidad_configuracion serv = new ServicioInEntidad_configuracion();
					serv.setEliminado(false);
					serv.setEntidad(entidadDestino);
					serv.setServicio(serviciosSeleccionados.get(i));
					serv.setTieneConsultaExterna(true);
					entityManager.persist(serv);
				}
			}
			
			entityManager.flush();
			
			// Busco todos los obj que tengan las entidades de origen y destino
			// seleccionadas y los borros
			List<Referencia_configuracion> aux = entityManager
					.createQuery(
							"select referencia from Referencia_configuracion referencia "
									+ "where referencia.eliminado = false "
									+ "and referencia.entidadByIdEntidadOrigen.id=:idEntidadO "
									+ "and referencia.entidadByIdEntidadDestino.id=:idEntidadD "
									+ "and referencia.servicio=null")
					.setParameter("idEntidadO", this.entidadOrigen.getId())
					.setParameter("idEntidadD", this.entidadDestino.getId())
					.getResultList();

			for (int j = 0; j < aux.size(); j++) {
				entityManager.remove(aux.get(j));
				entityManager.flush();
			}

			if (serviciosSeleccionados.size() > 0) {
				Long cid = bitacora
						.registrarInicioDeAccion("Adicionando servicios para referencias..");
				// Creo nuevas instancias de estos obj rellenados con los nuevos
				// servicios
				for (int i = 0; i < serviciosSeleccionados.size(); i++) {

					Referencia_configuracion newReferencia = new Referencia_configuracion();
					newReferencia
							.setEntidadByIdEntidadOrigen(this.entidadOrigen);
					newReferencia
							.setEntidadByIdEntidadDestino(this.entidadDestino);
					newReferencia.setServicio(serviciosSeleccionados.get(i));
					newReferencia.setCid(cid);
					newReferencia.setEliminado(false);
					entityManager.persist(newReferencia);
					entityManager.flush();
				}
			}

			referenciaBuscarControlador.actualizarServicios();
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

	// Atributos

	private String seleccionada;

	private List<Referencia_configuracion> listaReferencias = new ArrayList<Referencia_configuracion>();

	// Metodos
	public boolean estaSeleccionada(String id) {
		for (int i = 0; i < serviciosSeleccionados.size(); i++) {
			if (serviciosSeleccionados.get(i).getId().equals(id)) {
				return true;
			}
		}

		return false;
	}

	public void adicionartoSeleccionadas() {

		if (estaSeleccionada(this.seleccionada)) {
			Servicio_configuracion aux = entityManager.find(
					Servicio_configuracion.class, Long.parseLong(this.seleccionada));
			serviciosSeleccionados.remove(aux);
		}

		else {
			Servicio_configuracion aux = entityManager.find(
					Servicio_configuracion.class, Long.parseLong(this.seleccionada));
			serviciosSeleccionados.add(aux);
		}

	}

	public void eliminar(Long id) {
		Servicio_configuracion aux = entityManager.find(
				Servicio_configuracion.class, id);
		serviciosSeleccionados.remove(aux);
	}

	// Propiedades
	public List<Referencia_configuracion> getListaReferencias() {
		return listaReferencias;
	}

	public void setListaReferencias(
			List<Referencia_configuracion> listaReferencias) {
		this.listaReferencias = listaReferencias;
	}

	public Entidad_configuracion getEntidadOrigen() {
		return entidadOrigen;
	}

	public void setEntidadOrigen(Entidad_configuracion entidadOrigen) {
		this.entidadOrigen = entidadOrigen;
	}

	public String getSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(String seleccionada) {
		this.seleccionada = seleccionada;
	}

	public Entidad_configuracion getEntidadDestino() {
		return entidadDestino;
	}

	public void setEntidadDestino(Entidad_configuracion entidadDestino) {
		this.entidadDestino = entidadDestino;
	}

	public List<Servicio_configuracion> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio_configuracion> servicios) {
		this.servicios = servicios;
	}

	public List<Servicio_configuracion> getServiciosSeleccionados() {
		return serviciosSeleccionados;
	}

	public void setServiciosSeleccionados(
			List<Servicio_configuracion> serviciosSeleccionados) {
		this.serviciosSeleccionados = serviciosSeleccionados;
	}

	public ListadoControler<Servicio_configuracion> getListaControlServicios() {
		return listaControlServicios;
	}

	public void setListaControlServicios(
			ListadoControler<Servicio_configuracion> listaControlServicios) {
		this.listaControlServicios = listaControlServicios;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

}
