package gehos.configuracion.management.gestionarEntidadesSistema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.anillo.AnilloHisConfig;
import gehos.comun.shell.ModSelectorController;
import gehos.configuracion.management.entity.AsignacionFinancieraHospital_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.InstanciaHis_configuracion;
import gehos.configuracion.management.entity.PartidaPresupuestariaHospital_configuracion;
import gehos.configuracion.management.entity.PoblacionAreaInfluencia_configuracion;

@SuppressWarnings("serial")
@Name("entidadSistemaSeleccionarControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadSistemaSeleccionarControlador extends
		EntityQuery<Entidad_configuracion> {

	private static final String[] RESTRICTIONS = {
			"lower(entidad.nombre) like concat(lower(#{entidadSistemaSeleccionarControlador.entidad.nombre}),'%')",
			"entidad.id <> #{entidadSistemaSeleccionarControlador.idEntAsociada}" };

	private Entidad_configuracion entidad = new Entidad_configuracion();

	// Lista entidades sistema
	List<Entidad_configuracion> entidadSistemaList;

	// Entidad seleccionada visualmente
	private Entidad_configuracion entidadSelec = new Entidad_configuracion();

	// Id entidad seleccionada
	private Long idEntidadSelec = -1l;

	// Id entidad a desasociar
	private Long idEntAsociada;

	// Id entidad a asociar
	private Long idEntidad;

	private Long entidadIdEliminar = -1L;

	// Validación - No existen entidades principales
	private boolean noExistEntPerteneceARhio = false;

	// Lista entidades no asociadas - ModalPanel
	private ListControlerEntidad entidadNoAsociadaList = new ListControlerEntidad(
			new ArrayList<Entidad_configuracion>());

	// Lista entidades a asociar - ModalPanel
	private List<Entidad_configuracion> entidadAsociadaList = new ArrayList<Entidad_configuracion>();
	// Posicion entidad asociada a eliminar
	private int posEntidad = -1;

	// otras funcionalidades
	private String provinciaEtic = "Estado";
	private String municipioEtic = "Municipio";
	private String localidadEtic = "Parroquia";
	List<String> naciones = new ArrayList<String>();

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;
	
	@In
	IBitacora bitacora;
	
	/**
	 * @author yurien
	 * CONTROLADOR QUE GESTIONA LA INSTANCIA DEL HIS QUE ESTA CONFIGURADA
	 * **/
	@In
	AnilloHisConfig anilloHisConfig;

	// CONTRUCTOR---------------------------------------------------------
	public EntidadSistemaSeleccionarControlador() {
		setEjbql("select entidad from Entidad_configuracion entidad "
				+ "where entidad.perteneceARhio = true");
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("entidad.id asc");
	}

	// METODOS------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		naciones = naciones();

		// String[] provincias = new String[naciones.size()];
		// provincias[0] = "Provincia:";
		// provincias[1] = "Provincia:";
		// provincias[2] = "Estado:";
		//
		// String[] municipios = new String[naciones.size()];
		// municipios[0] = "Municipio:";
		// municipios[1] = "Municipio:";
		// municipios[2] = "Municipio:";
		//
		// String[] localidades = new String[naciones.size()];
		// localidades[0] = "Localidad:";
		// localidades[1] = "Localidad:";
		// localidades[2] = "Parroquia:";

		/**
		 * @author yurien 27/03/2014
		 * Se agrega la restriccion para que busque las entidades
		 * configuradas para el anillo actual de la instancia
		 * **/
		entidadSistemaList = entityManager
				.createQuery(
						"select entidad from Entidad_configuracion entidad "
								+ "where entidad.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} order by entidad.id desc")
//								+ "where entidad.perteneceARhio = true order by entidad.id desc")
				.getResultList();
		if (entidadSistemaList.size() == 0)
			noExistEntPerteneceARhio = true;
		else if (!idEntidadSelec.equals(-1l)) {
			entidadSelec = entidadSistemaList.get(0);
			for (int i = 0; i < entidadSistemaList.size(); i++) {
				if (entidadSistemaList.get(i).getId().equals(idEntidadSelec)) {
					entidadSelec = entidadSistemaList.get(i);
					break;
				}
			}
		} else if (entidadSelec == null) {
			entidadSelec = entidadSistemaList.get(0);
			idEntidadSelec = entidadSelec.getId();
		} else if (!entidadSistemaList.get(0).getId()
				.equals(entidadSelec.getId())
				&& !idEntidadSelec.equals(entidadSelec.getId())) {
			entidadSelec = entidadSistemaList.get(0);
			idEntidadSelec = entidadSelec.getId();
		}
		if (entidadSistemaList.size() != 0) {
			int pos = naciones.indexOf(this.entidadSelec.getEstado()
					.getNacion().getValor());
			// provinciaEtic = provincias[pos];
			// municipioEtic = municipios[pos];
			// localidadEtic = localidades[pos];
		}

	}

	// carga lista de naciones
	@SuppressWarnings("unchecked")
	public List<String> naciones() {
		return entityManager.createQuery(
				"select n.valor from Nacion_configuracion n order by n.valor")
				.getResultList();
	}

	// Eliminar la entidad seleccionada
	@SuppressWarnings("unchecked")
	public void eliminarEntidad() {
		try {
			Entidad_configuracion ent = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidad")
					.setParameter("idEntidad", entidadIdEliminar)
					.getSingleResult();

			// eliminando poblacion de area de influencia
			List<PoblacionAreaInfluencia_configuracion> p = entityManager
					.createQuery(
							"select p from PoblacionAreaInfluencia_configuracion p where p.entidad.id = :idEntidad")
					.setParameter("idEntidad", entidadIdEliminar)
					.getResultList();
			for (int i = 0; i < p.size(); i++) {
				entityManager.remove(p.get(i));
			}

			// asingacion financiera
			List<AsignacionFinancieraHospital_configuracion> asignacionFinancieraList = entityManager
					.createQuery(
							"select a from AsignacionFinancieraHospital_configuracion a join a.entidad ent where ent.id = :idEntidad and a.eliminado = false")
					.setParameter("idEntidad", ent.getId()).getResultList();

			for (int i = 0; i < asignacionFinancieraList.size(); i++) {
				entityManager.remove(asignacionFinancieraList.get(i));
			}

			// partida presupuestaria
			List<PartidaPresupuestariaHospital_configuracion> partidaPresupuestariaList = entityManager
					.createQuery(
							"select p from PartidaPresupuestariaHospital_configuracion p join p.entidad ent where ent.id = :idEntidad and p.eliminado = false")
					.setParameter("idEntidad", ent.getId()).getResultList();

			for (int i = 0; i < partidaPresupuestariaList.size(); i++) {
				entityManager.remove(partidaPresupuestariaList.get(i));
			}
			ent.setCid(
					bitacora.registrarInicioDeAccion("eliminando"+" -"+ent.getNombre()));
			entityManager.remove(ent);
			entityManager.flush();

			ModSelectorController.reloadsEntities();
		} catch (Exception exc) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("msjEliminar"));
		}
	}

	// Cambiar la visibilidad de la entidad en el sistema
	public void cambiarVisibilidad() {
		Entidad_configuracion ent = (Entidad_configuracion) entityManager
				.createQuery(
						"select e from Entidad_configuracion e where e.id = :idEntidad")
				.setParameter("idEntidad", this.idEntAsociada)
				.getSingleResult();

		ent.setEliminado(!ent.getEliminado());
		ent.setCid(
				bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitcambiarVES")+" -"+ent.getNombre()));
		entityManager.persist(ent);
		entityManager.flush();

		ModSelectorController.reloadsEntities();
	}

	// Desasociar entidad
	public void eliminarAsociacion() {
		Entidad_configuracion ent = (Entidad_configuracion) entityManager
				.createQuery(
						"select e from Entidad_configuracion e where e.id = :idEntidad")
				.setParameter("idEntidad", idEntAsociada).getSingleResult();

		/**
		 * @author yurien 27/03/2014
		 * Cuando se selecciona una entidad para desasociarla
		 * se le quita como instanciaHis la actual y se pone null
		 * **/
		ent.setInstanciaHis(null);
		//ent.setPerteneceARhio(false);
		ent.setCid(
				bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitdesasociarES")+" -"+ent.getNombre()));
		entityManager.persist(ent);
		entityManager.flush();

		entidadSelec = entidadSistemaList.get(0);
		entidadesNoAsociadas();

		if (entidadSistemaList.size() == 0)
			noExistEntPerteneceARhio = true;
		else
			noExistEntPerteneceARhio = false;

		ModSelectorController.reloadsEntities();
	}

	// Cargar entidades no asociadas
	@SuppressWarnings("unchecked")
	public void entidadesNoAsociadas() {
		
		/**
		 * @author yurien 27/03/2014
		 * Se agrega la restriccion para que muestre las entidades que no pertenezcan
		 * al anillo actual configurado para la instancia
		 * **/
		if (entidadNoAsociadaList.getValor().equals("")) {
			List<Entidad_configuracion> list = entityManager
					.createQuery(
							"select entidad from Entidad_configuracion entidad "
									+ "where (entidad.instanciaHis.id <> #{anilloHisConfig.hisInstanceNumber} "
									+ "or entidad.instanciaHis.id is null) and entidad.eliminado = false order by entidad.nombre")
					// +
					// "where entidad.perteneceARhio = false and entidad.eliminado = false order by entidad.nombre")
					.getResultList();
			entidadNoAsociadaList = new ListControlerEntidad(list);
		}
	}

	// Realizar busqueda entidad no asociada
	public void buscar() {
		entidadNoAsociadaList.setFirstResult(0);
	}

	// Terminar busqueda entidad no asociada
	public void cancelarBusqueda() {
		entidadNoAsociadaList.setValor("");
		setFirstResult(0);
	}

	// Complementario del selecEntidadAsociada()
	public boolean buscarEntidadad(Long idEntidad) {
		for (int i = 0; i < entidadAsociadaList.size(); i++) {
			if (entidadAsociadaList.get(i).getId().equals(idEntidad)) {
				this.posEntidad = i;
				return true;
			}
		}
		return false;
	}

	// Seleccionar entidades a asociar
	public void selecEntidadAsociada() {
		if (buscarEntidadad(idEntidad)) {
			entidadAsociadaList.get(this.posEntidad).setPerteneceARhio(false);
			entidadAsociadaList.remove(this.posEntidad);
		} else {
			Entidad_configuracion ent = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidad")
					.setParameter("idEntidad", idEntidad).getSingleResult();
			
			/**
			 * @author yurien 27/03/2014
			 * Cuando se selecciona una entidad para asociarla
			 * se actualiza el campo instanciaHis
			 * **/
			
     		ent.setInstanciaHis(anilloHisConfig.getHisInstance());
			//ent.setPerteneceARhio(true);
			entidadAsociadaList.add(ent);
		}
	}

	// Asociar entidades
	public void asociarEntidades() {
		if (entidadAsociadaList.size() != 0) {
			for (int i = 0; i < entidadAsociadaList.size(); i++) {
				entityManager.persist(entidadAsociadaList.get(i));
			}
			entityManager.flush();
			entidadesNoAsociadas();
		}
		noExistEntPerteneceARhio = false;
		ModSelectorController.reloadsEntities();
	}

	// Se ejecuta cuando se abre el modalpanel
	public void crearEntidadAsociada() {
		entidadNoAsociadaList.setFirstResult(0);
		entidadAsociadaList = new ArrayList<Entidad_configuracion>();
		entidadNoAsociadaList.setValor("");
		entidadesNoAsociadas();
	}

	// Seleccionar entidad asociada
	public void seleccionarEntidad(Long idEntidad) {
		this.idEntidadSelec = idEntidad;
		entidadSelec = (Entidad_configuracion) entityManager
				.createQuery(
						"select entidad from Entidad_configuracion entidad "
								+ "where entidad.id = :idEntidad")
				.setParameter("idEntidad", idEntidad).getSingleResult();
	}

	// PROPIEDADES----------------------------------------------------------
	public void setIdEntidadSelec(Long idEntidadSelec) {
		if (this.idEntidadSelec.equals(-1) && !idEntidadSelec.equals(-1)) {
			entidadesNoAsociadas();
			this.idEntidadSelec = idEntidadSelec;
		}
	}

	public boolean isNoExistEntPerteneceARhio() {
		return noExistEntPerteneceARhio;
	}

	public void setNoExistEntPerteneceARhio(boolean noExistEntPerteneceARhio) {
		this.noExistEntPerteneceARhio = noExistEntPerteneceARhio;
	}

	public Entidad_configuracion getEntidadSelec() {
		return entidadSelec;
	}

	public void setEntidadSelec(Entidad_configuracion entidadSelec) {
		this.entidadSelec = entidadSelec;
	}

	public Long getIdEntAsociada() {
		return idEntAsociada;
	}

	public void setIdEntAsociada(Long idEntAsociada) {
		this.idEntAsociada = idEntAsociada;
	}

	public ListControlerEntidad getEntidadNoAsociadaList() {
		return entidadNoAsociadaList;
	}

	public void setEntidadNoAsociadaList(
			ListControlerEntidad entidadNoAsociadaList) {
		this.entidadNoAsociadaList = entidadNoAsociadaList;
	}

	public Long getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}

	public List<Entidad_configuracion> getEntidadSistemaList() {
		return entidadSistemaList;
	}

	public void setEntidadSistemaList(
			List<Entidad_configuracion> entidadSistemaList) {
		this.entidadSistemaList = entidadSistemaList;
	}

	public Long getIdEntidadSelec() {
		return idEntidadSelec;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public Long getEntidadIdEliminar() {
		return entidadIdEliminar;
	}

	public void setEntidadIdEliminar(Long entidadIdEliminar) {
		this.entidadIdEliminar = entidadIdEliminar;
	}

	public List<Entidad_configuracion> getEntidadAsociadaList() {
		return entidadAsociadaList;
	}

	public void setEntidadAsociadaList(
			List<Entidad_configuracion> entidadAsociadaList) {
		this.entidadAsociadaList = entidadAsociadaList;
	}

	public int getPosEntidad() {
		return posEntidad;
	}

	public void setPosEntidad(int posEntidad) {
		this.posEntidad = posEntidad;
	}

	public String getProvinciaEtic() {
		return provinciaEtic;
	}

	public void setProvinciaEtic(String provinciaEtic) {
		this.provinciaEtic = provinciaEtic;
	}

	public String getMunicipioEtic() {
		return municipioEtic;
	}

	public void setMunicipioEtic(String municipioEtic) {
		this.municipioEtic = municipioEtic;
	}

	public String getLocalidadEtic() {
		return localidadEtic;
	}

	public void setLocalidadEtic(String localidadEtic) {
		this.localidadEtic = localidadEtic;
	}

}
