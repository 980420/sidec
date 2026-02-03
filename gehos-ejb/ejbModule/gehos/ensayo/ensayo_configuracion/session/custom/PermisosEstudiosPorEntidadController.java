package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

@Scope(ScopeType.CONVERSATION)
@Name("permisosEstudiosPorEntidadController")
public class PermisosEstudiosPorEntidadController {

	private Long idEstudio;
	

	public Long getIdEstudio() {
		return idEstudio;
	}

	public void setIdEstudio(Long idEstudio) {
		this.idEstudio = idEstudio;
	}
	
	
	public String NombreEstudio(){
		Estudio_ensayo estEnsayo = entityManager.find(Estudio_ensayo.class , this.getIdEstudio());
		return estEnsayo.getNombre();
		
	}
	/**
	 * Gestionar las trazas en la bitacora
	 */
	protected @In IBitacora bitacora;
	
	/**
	 * Registro en la bitacora
	 */
	Long cid = -1l;
	
	/**
	 * Para manejar los datos en la BD
	 */
	@In
	private EntityManager entityManager;

	
		
	/**
	 * Entidades Disponibles
	 */
	private List<Entidad_ensayo> listaEntidadSource = new ArrayList<Entidad_ensayo>();

	public List<Entidad_ensayo> getListaEntidadSource() {
		return listaEntidadSource;
	}

	public void setListaEntidadSource(List<Entidad_ensayo> listaEntidadSource) {
		this.listaEntidadSource = listaEntidadSource;
	}

	
	/**
	 * Entidades Asignadas
	 */
	private List<Entidad_ensayo> listaEntidadTarget = new ArrayList<Entidad_ensayo>();

	public List<Entidad_ensayo> getListaEntidadTarget() {
		return listaEntidadTarget;
	}

	public void setListaEntidadTarget(List<Entidad_ensayo> listaEntidadTarget) {
		this.listaEntidadTarget = listaEntidadTarget;
	}

	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		if(cid.equals(-1l)) {
			cid = bitacora.registrarInicioDeAccion("Modificando permisos en estudios");
		}
		
		if (this.idEstudio != null) {
			
			listaEntidadSource = entityManager
					.createQuery(
							"from Entidad_ensayo ent where eliminado <> true order by ent.nombre")
					.getResultList();

			listaEntidadTarget = entityManager
					.createQuery(
							"Select ent from Entidad_ensayo ent "
							+ "JOIN ent.estudioEntidads ee "
							+ "JOIN  ee.estudio e "
							+ "where ee.eliminado <> true "
							+ "and e.id =:nombre "
							+ "and e.eliminado <> true "
							+ "and ent.eliminado <> true "
							+ "order by ent.nombre")
					.setParameter("nombre", idEstudio).getResultList();

			validateEntidadTarget();
		}
		
	}
	
	@Create
	@SuppressWarnings("unchecked")
	public void SourceLoad() {
		// Lista de las entidades en blanco
		listaEntidadSource = new ArrayList<Entidad_ensayo>();
		listaEntidadTarget = new ArrayList<Entidad_ensayo>();

	}
	



	public void validateEntidadTarget() {
		for (int j = 0; j < listaEntidadTarget.size(); j++) {
			for (int i = 0; i < listaEntidadSource.size(); i++) {
				if (listaEntidadSource.get(i).equals(listaEntidadTarget.get(j))) {
					listaEntidadSource.remove(i);
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	@End
	public void salvar(){
		
		// Cargo el estudio actual 
		Estudio_ensayo estudio = (Estudio_ensayo) entityManager
				.createQuery(
						"from Estudio_ensayo ent where eliminado <> true and id=:nombre")	
				.setParameter("nombre", idEstudio)
				.getSingleResult();
		
	
		//
		List<EstudioEntidad_ensayo> listaEntidadActuales = entityManager
				.createQuery("Select ent from EstudioEntidad_ensayo ent "
						+"JOIN ent.estudio est "
						+"where est.id =:idEST and est.eliminado<>true and ent.eliminado <> true")
				.setParameter("idEST", estudio.getId()).getResultList();
		
		
		
		//Las que no este las desmarco
		for (int i = 0; i < listaEntidadActuales.size(); i++) {
			boolean kill = true;
			
			for (int j = 0; j < listaEntidadTarget.size(); j++) {	
				
				//La entidad esta 
				if (listaEntidadTarget.get(j).getId()==listaEntidadActuales.get(i).getEntidad().getId()) {
					kill=false;
					listaEntidadTarget.remove(j);
					break;
				}
				
			}
			
			if(kill){				
				
				EstudioEntidad_ensayo entidad_ensayo = entityManager.find(EstudioEntidad_ensayo.class,listaEntidadActuales.get(i).getId());
				entidad_ensayo.setEliminado(true);
				entidad_ensayo.setCid(cid);
				entityManager.persist(entidad_ensayo);
				entityManager.flush();			
			}
			
			
			
		}
		
	
		
		for (int j = 0; j < listaEntidadTarget.size(); j++) {	
			
			EstudioEntidad_ensayo entidad_ensayo = new EstudioEntidad_ensayo();		
			
			Entidad_ensayo ee =entityManager.find(Entidad_ensayo.class, listaEntidadTarget.get(j).getId());			
			entidad_ensayo.setEntidad(ee);
			
			Estudio_ensayo est  =entityManager.find(Estudio_ensayo.class, estudio.getId());			
			entidad_ensayo.setEstudio(est);
			
			entidad_ensayo.setEliminado(false);
			entidad_ensayo.setCid(cid);
			
			entityManager.persist(entidad_ensayo);
			entityManager.flush();			
			
		}
			
		SourceLoad();	
	}
}
