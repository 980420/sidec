package gehos.ensayo.ensayo_disenno.session.gestionarCronograma.etapas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



import gehos.bitacora.session.traces.IBitacora;







import gehos.ensayo.entity.Etapa_ensayo;



import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;




@Name("modificarEtapa")
@Scope(ScopeType.CONVERSATION)
public class ModificarEtapa {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	FacesMessage facesMessage;	
	
	private Etapa_ensayo etapa = null;
	Etapa_ensayo otraEtapa = new Etapa_ensayo();
	private long cid;
	private long idEtapa;	
	private List<Integer> listaInicioEtapa = new ArrayList<Integer>();
	private List<Integer> listaFinEtapa = new ArrayList<Integer>(); 
	private Integer inicio;
	private Integer fin;
	
	public void inicializarEtapa(){//este id despues hay que pasarlo por parametros.		
		
		if(etapa == null){
			this.etapa =(Etapa_ensayo) entityManager.find(Etapa_ensayo.class, idEtapa);
			this.otraEtapa = (Etapa_ensayo)entityManager.createQuery("select e from Etapa_ensayo e "
					+ "where e.finEtapa<>0 and e.finEtapa <> :fin "
					+ "and e.cronograma.id = :idCrono")
					  .setParameter("fin",etapa.getFinEtapa())
					  .setParameter("idCrono", etapa.getCronograma().getId())
						 .getSingleResult();
			//		
			if(esEtapaSeguimiento(etapa.getFinEtapa(), otraEtapa.getFinEtapa()))
			{
				for(int i=etapa.getInicioEtapa();i<etapa.getCronograma().getTiempoDuracion();i++)
				{			
					listaInicioEtapa.add(i);
					if(i!=etapa.getInicioEtapa())
						listaFinEtapa.add(i);
				}
				
			}
			else
			{
				listaInicioEtapa.add(1);
				for(int i=2;i<=etapa.getFinEtapa();i++)
					listaFinEtapa.add(i);
			}
		}
		
			
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraModificar_ens"));
	}
	
	public String actualizarEtapas()
	{
		try {
			if(etapa.getInicioEtapa()>=etapa.getFinEtapa())
			{
				facesMessages.clear();
				facesMessages.addFromResourceBundle("msg_validacionIniMayorIgualFin_ens");
				
			}
			else if(!listaInicioEtapa.contains(etapa.getInicioEtapa()))
			{	
				facesMessages.clear();
				facesMessages.add(new FacesMessage(
						SeamResourceBundle.getBundle().getString(
								"msg_validacionIni_ens")
								+ " " + listaInicioEtapa.get(0).toString()+ " " +SeamResourceBundle.getBundle().getString(
										"prm_y_ens")+ " " + listaInicioEtapa.get(listaInicioEtapa.size()-1).toString(),
						null));				
				
				
			}
			else if(!listaFinEtapa.contains(etapa.getFinEtapa()))
			{	
				facesMessages.clear();
				facesMessages.add(new FacesMessage(
						SeamResourceBundle.getBundle().getString(
								"msg_validacionFin_ens")
								+ " " + listaFinEtapa.get(0).toString()+ " " +SeamResourceBundle.getBundle().getString(
										"prm_y_ens")+ " " + listaFinEtapa.get(listaFinEtapa.size()-1).toString(),
						null));				
				
				
			}
			else{	
				etapa.setFechaActualizacion(Calendar.getInstance().getTime());
				entityManager.persist(etapa);
				entityManager.flush();
				actualizarOtraEtapa();			
				return "ok";
			}
			return "error";
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return "error";
			
		}
	}
	
	public void actualizarOtraEtapa()
	{		
		if(esEtapaSeguimiento(etapa.getFinEtapa(),otraEtapa.getFinEtapa()))		
			otraEtapa.setFinEtapa(etapa.getInicioEtapa()-1);		
		else
			otraEtapa.setInicioEtapa(etapa.getFinEtapa()+1);
		entityManager.persist(otraEtapa);
		entityManager.flush();
		
	}
	
	public boolean esEtapaSeguimiento(int finEtapa, int finOtraEtapa)
	{
		if(finEtapa>finOtraEtapa)		
			return true;		
		else
			return false;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}
	
	public Etapa_ensayo getEtapa() {
		return etapa;
	}

	public void setEtapa(Etapa_ensayo etapa) {
		this.etapa = etapa;
	}

	public long getIdEtapa() {
		return idEtapa;
	}

	public void setIdEtapa(long idEtapa) {
		this.idEtapa = idEtapa;
	}

	public Etapa_ensayo getOtraEtapa() {
		return otraEtapa;
	}

	public void setOtraEtapa(Etapa_ensayo otraEtapa) {
		this.otraEtapa = otraEtapa;
	}

	public List<Integer> getListaInicioEtapa() {
		return listaInicioEtapa;
	}

	public void setListaInicioEtapa(List<Integer> listaInicioEtapa) {
		this.listaInicioEtapa = listaInicioEtapa;
	}

	public List<Integer> getListaFinEtapa() {
		return listaFinEtapa;
	}

	public void setListaFinEtapa(List<Integer> listaFinEtapa) {
		this.listaFinEtapa = listaFinEtapa;
	}

	public Integer getInicio() {
		return inicio;
	}

	public void setInicio(Integer inicio) {
		this.inicio = inicio;
	}

	public Integer getFin() {
		return fin;
	}

	public void setFin(Integer fin) {
		this.fin = fin;
	}
	
	

}
