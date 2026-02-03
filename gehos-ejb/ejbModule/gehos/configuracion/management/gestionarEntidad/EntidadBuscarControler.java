package gehos.configuracion.management.gestionarEntidad;

import gehos.comun.shell.ModSelectorController;
import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("entidadBuscarControler")
public class EntidadBuscarControler extends EntityQuery<Entidad_configuracion> {

//	private static final String EJBQL = "select entidad from Entidad_configuracion entidad where entidad.perteneceARhio = false";
	
	/**
	 * @author yurien
	 * Se buscan las entidades que no pertenecen al anillo actual configurado
	 * **/
	private static final String EJBQL = "select entidad from Entidad_configuracion entidad "
			                               + "where (entidad.instanciaHis.id <> #{anilloHisConfig.hisInstanceNumber} "
			                                     + "or entidad.instanciaHis.id is null) ";

	private static final String[] RESTRICTIONS = {
			"lower(entidad.nombre) like concat(lower(#{entidadBuscarControler.nombre.trim()}),'%')",
			"#{entidadBuscarControler.entidadId} <> entidad.id" };

	// departamento
	private Entidad_configuracion entidad = new Entidad_configuracion();
	private String nombre = "";	
	private Long entidadId = -1l;

	// otras funcionalidades
	private boolean openSimpleTogglePanel = true;
	private Parameters parametros = new Parameters(); 

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	public EntidadBuscarControler() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("entidad.id desc");
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {		
	}

	/*
	 * public boolean logoExist(Entidad_configuracion entidad) { FacesContext
	 * aFacesContext = FacesContext.getCurrentInstance(); ServletContext context
	 * = (ServletContext) aFacesContext .getExternalContext().getContext();
	 * String rootpath = context
	 * .getRealPath("/resources/modCommon/entidades_logos/" +
	 * entidad.getLogo()); java.io.File dir = new java.io.File(rootpath); return
	 * dir.exists(); }
	 */

	public void cambiarVisibilidad(Long id) {
		Entidad_configuracion aux = entityManager.find(
				Entidad_configuracion.class, id);
		aux.setEliminado(!aux.getEliminado());
		entityManager.merge(aux);
		entityManager.flush();
	}

	public void seleccionarEliminar(Long entidadId) {
		this.entidadId = entidadId;
	}

	@SuppressWarnings("unchecked")
	public void eliminar() {
		try {
			Entidad_configuracion ent = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidad")
					.setParameter("idEntidad", entidadId).getSingleResult();

			// eliminando poblacion de area de influencia
			List<PoblacionAreaInfluencia_configuracion> p = entityManager
					.createQuery(
							"select p from PoblacionAreaInfluencia_configuracion p where p.entidad.id = :idEntidad")
					.setParameter("idEntidad", entidadId).getResultList();
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

			entityManager.remove(ent);
			entityManager.flush();

			ModSelectorController.reloadsEntities();
		} catch (Exception exc) {
			facesMessages
					.addToControlFromResourceBundle("btnSi", Severity.ERROR,
							SeamResourceBundle.getBundle()
							.getString("msjEliminar"));
		}

	}

	public void cambiarEstadoSimpleTogglePanel() {
		openSimpleTogglePanel = !openSimpleTogglePanel;
	}

	public void buscar() {
		setFirstResult(0);	
	}

	public void cancelar() {
		this.nombre = "";
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = parametros.decodec(nombre);
	}

	public boolean isOpenSimpleTogglePanel() {
		return openSimpleTogglePanel;
	}

	public void setOpenSimpleTogglePanel(boolean openSimpleTogglePanel) {
		this.openSimpleTogglePanel = openSimpleTogglePanel;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public Long getEntidadId() {
		return entidadId;
	}

	public void setEntidadId(Long entidadId) {
		this.entidadId = entidadId;
	}

	public Parameters getParametros() {
		return parametros;
	}

	public void setParametros(Parameters parametros) {
		this.parametros = parametros;
	}
}
