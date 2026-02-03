package gehos.ensayo.ensayo_conduccion.gestionarMS;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstadoTratamiento_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

@Name("wrapperMomento")
public class WrapperMomento {
	
	protected @In EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public void cambiarEstadoACompletado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery("select hoja from CrdEspecifico_ensayo hoja where hoja.momentoSeguimientoEspecifico=:momentoSeguimientoEspecifico and hoja.eliminado= 'false'").setParameter("momentoSeguimientoEspecifico", momeSegui).getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 3){
				completa = false;
				break;
			}
		}
		if(completa && momeSegui.getEstadoMomentoSeguimiento().getCodigo() == 1){
			EstadoMomentoSeguimiento_ensayo estadoCompletadoMom = (EstadoMomentoSeguimiento_ensayo) entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.codigo = 3").getSingleResult();
			momeSegui.setEstadoMomentoSeguimiento(estadoCompletadoMom);
			entityManager.persist(momeSegui);
			entityManager.flush();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void cambiarEstadoAFirmado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery("select hoja from CrdEspecifico_ensayo hoja where hoja.momentoSeguimientoEspecifico=:momentoSeguimientoEspecifico and hoja.eliminado= 'false'").setParameter("momentoSeguimientoEspecifico", momeSegui).getResultList();
		boolean firmado = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 4){
				firmado = false;
				break;
			}
		}
		if(firmado && momeSegui.getEstadoMomentoSeguimiento().getCodigo() == 3){
			EstadoMomentoSeguimiento_ensayo estadoFirmadoMom = (EstadoMomentoSeguimiento_ensayo) entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.codigo = 5").getSingleResult();
			momeSegui.setEstadoMomentoSeguimiento(estadoFirmadoMom);
			entityManager.persist(momeSegui);
			entityManager.flush();
		}
	}
	

	@SuppressWarnings("unchecked")
	public void cambiarEstadoMomento(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery("select hoja from CrdEspecifico_ensayo hoja where hoja.momentoSeguimientoEspecifico=:momentoSeguimientoEspecifico and hoja.eliminado= 'false'").setParameter("momentoSeguimientoEspecifico", momeSegui).getResultList();
		boolean completado = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 3){
				completado = false;
				break;
			}
		}
		if(completado && momeSegui.getEstadoMomentoSeguimiento().getCodigo() == 5){
			EstadoMomentoSeguimiento_ensayo estadoFirmadoMom = (EstadoMomentoSeguimiento_ensayo) entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.codigo = 3").getSingleResult();
			momeSegui.setEstadoMomentoSeguimiento(estadoFirmadoMom);
			entityManager.persist(momeSegui);
			entityManager.flush();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void cambiarEstadoMonitoreoACompletado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		long idEstadoSeguimiento=3;
		EstadoMonitoreo_ensayo estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery("select hoja from CrdEspecifico_ensayo hoja where hoja.momentoSeguimientoEspecifico=:momentoSeguimientoEspecifico and hoja.eliminado= 'false'").setParameter("momentoSeguimientoEspecifico", momeSegui).getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.get(i).getEstadoMonitoreo().getCodigo() != 3){
				completa = false;
				break;
			}
		}
		if(completa && (momeSegui.getEstadoMonitoreo().getCodigo() == 1 || momeSegui.getEstadoMonitoreo().getCodigo() == 2)){
			momeSegui.setEstadoMonitoreo(estadoMonitoreo);
			entityManager.persist(momeSegui);
			entityManager.flush();
		}
	}
	
	public void cambiarEstadoMonitoreoAIniciado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		long idEstadoSeguimiento=1;
		EstadoMonitoreo_ensayo estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
		
			momeSegui.setEstadoMonitoreo(estadoMonitoreo);
			entityManager.persist(momeSegui);
			entityManager.flush();
		
	}
	
	public void cambiarEstadoMonitoreoANoIniciado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		long idEstadoSeguimiento=2;
		EstadoMonitoreo_ensayo estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
		
			momeSegui.setEstadoMonitoreo(estadoMonitoreo);
			entityManager.persist(momeSegui);
			entityManager.flush();
		
	}
	
	
	
	public void cambiarEstadoTratamientoSuj(MomentoSeguimientoEspecifico_ensayo momento){
		try {
			
		Sujeto_ensayo sujeto = momento.getSujeto();
		EstadoTratamiento_ensayo estadoTratamiento= (EstadoTratamiento_ensayo) entityManager.createQuery("select est from EstadoTratamiento_ensayo est where est.nombre='Tratamiento'").getSingleResult();
		EstadoTratamiento_ensayo estadoSeguimiento= (EstadoTratamiento_ensayo) entityManager.createQuery("select est from EstadoTratamiento_ensayo est where est.nombre='Seguimiento'").getSingleResult();
		if(momento.getEstadoMomentoSeguimiento().getCodigo() == 3 && sujeto.getEstadoTratamiento().getCodigo() != 4){
			if(momento.getMomentoSeguimientoGeneral().getEtapa().equals("Seguimiento")){
				sujeto.setEstadoTratamiento(estadoSeguimiento);
				entityManager.persist(sujeto);
				entityManager.flush();
			}else{
				if(momento.getMomentoSeguimientoGeneral().getEtapa().equals("Tratamiento")){
					sujeto.setEstadoTratamiento(estadoTratamiento);
					entityManager.persist(sujeto);
					entityManager.flush();
				}
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	
	public void cambiarEstadoAMonitoreoIniciado(long idHoja){
		CrdEspecifico_ensayo crd = entityManager.find(CrdEspecifico_ensayo.class, idHoja);
		long idEstadoSeguimiento=1;
		EstadoMonitoreo_ensayo estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
		if(crd.getEstadoMonitoreo().getCodigo() == 2){
			crd.setEstadoMonitoreo(estadoMonitoreo);
			entityManager.persist(crd);
		}
		if(crd.getMomentoSeguimientoEspecifico().getEstadoMonitoreo().getCodigo() == 2){
			crd.getMomentoSeguimientoEspecifico().setEstadoMonitoreo(estadoMonitoreo);
			entityManager.persist(crd.getMomentoSeguimientoEspecifico());
		}
		entityManager.flush();
		
	}
	
	
	
	
	

}
