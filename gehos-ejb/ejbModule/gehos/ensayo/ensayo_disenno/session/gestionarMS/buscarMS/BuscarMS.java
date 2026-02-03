package gehos.ensayo.ensayo_disenno.session.gestionarMS.buscarMS;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoCronograma_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("buscarMS")
@Scope(ScopeType.CONVERSATION)
public class BuscarMS {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	private Long cid;
	private long id;
	private MomentoSeguimientoGeneral_ensayo ms = new MomentoSeguimientoGeneral_ensayo();
	
	@Begin(join=true,flushMode=FlushModeType.MANUAL)
	@Create
	public void start(){}
	
	public BuscarMS(){}
	
	
	public void seleccionarMS(MomentoSeguimientoGeneral_ensayo ms)
	{
		this.ms = ms;
	}
	/**
	 *  Habilitar el boton eliminar en el listado de los Momentos de Seguimiento si se llaman Pesquisaje, Eval Inicial e Interrupcion
	 * @param nombreMS
	 * @return true si el nombre coincide, falso sino
	 * @author Tania
	 */
	public boolean habilitarEliminar(String nombreMS){
	if (nombreMS.equals(SeamResourceBundle.getBundle().getString("prm_pesquisaje_ens")) || nombreMS.equals(SeamResourceBundle.getBundle().getString("prm_evaluacionInicial_ens")) || nombreMS.equals(SeamResourceBundle.getBundle().getString("prm_interrupcion_ens"))) {
		return true;
	}
	else return false;
				
	}
	
	@SuppressWarnings("unchecked")
	public void eliminar()
	{
		ms.setEliminado(true);
		List<MomentoSeguimientoGeneralHojaCrd_ensayo> msCRD = new ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>();
		msCRD = entityManager.createQuery("Select mscrd from MomentoSeguimientoGeneralHojaCrd_ensayo mscrd where mscrd.momentoSeguimientoGeneral.id=:idms").setParameter("idms", ms.getId()).getResultList();
		for(int i=0; i < msCRD.size(); i++ ){
			if(ms.getEliminado())
				entityManager.remove(msCRD.get(i));
		}
		
		entityManager.persist(ms);
		entityManager.flush();
		if(ms.getProgramado())
			ActualizarEstadoCronograma();
		
	}	
	public void ActualizarEstadoCronograma()
	{
		Cronograma_ensayo cronograma = (Cronograma_ensayo) entityManager.find(Cronograma_ensayo.class, ms.getCronograma().getId());
		
		long cantidadMSEnCronograma =(Long) entityManager.createQuery("select count(ms) from MomentoSeguimientoGeneral_ensayo ms "
				+ "where ms.cronograma.id = :idCronograma "
				+ "and ms.eliminado <> true "
				+ "and ms.programado = true")				  
				  .setParameter("idCronograma", cronograma.getId())
					 .getSingleResult();
		if( cantidadMSEnCronograma == 0 )
		{
			//Al quedar sin MS el cronograma, el estado es Nuevo
			long codigoEstado = 1; 
			EstadoCronograma_ensayo estadoCronograma =(EstadoCronograma_ensayo) entityManager.createQuery("select e from EstadoCronograma_ensayo e "
					+ "where e.codigo = :codigo")				  
					  .setParameter("codigo", codigoEstado)
						 .getSingleResult();
			cronograma.setEstadoCronograma(estadoCronograma);
			entityManager.persist(cronograma);
			entityManager.flush();
		}
	}

	public MomentoSeguimientoGeneral_ensayo getMs() {
		return ms;
	}

	public void setMs(MomentoSeguimientoGeneral_ensayo ms) {
		this.ms = ms;
	}

	

}
