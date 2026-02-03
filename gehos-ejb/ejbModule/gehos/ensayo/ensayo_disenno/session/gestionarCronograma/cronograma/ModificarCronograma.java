package gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Cronograma_ensayo;






import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;




@Name("modificarCronograma")
@Scope(ScopeType.CONVERSATION)
public class ModificarCronograma {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In IBitacora bitacora;
	
	FacesMessage facesMessage;
	
	private Cronograma_ensayo cronograma; 	
	private List<Cronograma_ensayo> cronogramas;
	private List<Etapa_ensayo> etapas;
	
	private long cid;	
	private String periodo;
	private GrupoSujetos_ensayo grupoSubjeto;
	private long idGrupoSujeto;	
	private String tiempoDuracion;
	//Inicializar.
	public void inicializarCronogramaEtapas(){			
		
		this.periodo = "dias";
		this.grupoSubjeto =(GrupoSujetos_ensayo) entityManager.find(GrupoSujetos_ensayo.class, idGrupoSujeto);
		cronogramas = new ArrayList<Cronograma_ensayo>(grupoSubjeto.getCronogramas());
		this.cronograma =(Cronograma_ensayo) entityManager.createQuery("select c from Cronograma_ensayo c "
				+ "where c.id = :idCrono")				  
				  .setParameter("idCrono", cronogramas.get(0).getId())
					 .getSingleResult();
		etapas = new ArrayList<Etapa_ensayo>(cronograma.getEtapas());		
		tiempoDuracion = cronograma.getTiempoDuracion().toString();
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraModificar_ens"));
	}
	
	
	
	public Integer tiempoDuracionDias()
	{
		
		
		Integer tiempoResult = Integer.parseInt(tiempoDuracion);
		
		if(periodo.equals("semanas"))
			tiempoResult = tiempoResult*7;
		else if(periodo.equals("meses"))
			tiempoResult = tiempoResult*30;
		else if(periodo.equals("annos"))
			tiempoResult = tiempoResult*365;	
		
		return tiempoResult;
		
	}
	
	public void numeroEnteroPositivo(FacesContext context, UIComponent component, Object value) {
		
		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
		{
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle().getString(
		
					"msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}			

		if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
			// tenga caracteres
			// // extranos
		{
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}
			
		if(value.toString().length()>4)
			//que no exceda las 4 cifras
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle().getString(
					"mas4cifras"));


		try {// valida que sea entero
			@SuppressWarnings("unused")
			Integer val = new Integer(value.toString());
		} catch (Exception e) {
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}
		// }
	}
	//Metodo para modificar el cronograma.
	public String modificarCronograma()
	{
		try {
			Integer tiempoDuracion = tiempoDuracionDias();
			Integer tiempoDuracionOld = cronograma.getTiempoDuracion();
			if (tiempoDuracion<6){
				facesMessages.clear();
				facesMessages.addFromResourceBundle("msg_validacionTiempo_ens");
				return "error";
			}			
			cronograma.setFechaActualizacion(Calendar.getInstance().getTime());			
			cronograma.setTiempoDuracion(tiempoDuracionDias());
			cronograma.setCid(cid);
			entityManager.persist(cronograma);
			entityManager.flush();
			if(!tiempoDuracion.equals(tiempoDuracionOld))
			{
				modificarEtapa();
				
			}			
			return "ok";
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return "error";
			
		}
	}
	//Metodo para modificar las etapas.
	public void modificarEtapa()
	{
		try {
			
			for(int i = 0;i<etapas.size();i++)
			{
				this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraModificarEtapa_ens"));
				Etapa_ensayo etapa = etapas.get(i);
				etapa.setCid(cid);
				etapa.setFechaActualizacion(Calendar.getInstance().getTime());				
				if(etapa.getInicioEtapa()==0){
					etapa.setInicioEtapa(0);
					etapa.setFinEtapa(0);
				}
				else if(etapa.getInicioEtapa()==1){
					etapa.setInicioEtapa(1);
					etapa.setFinEtapa(cronograma.getTiempoDuracion()/2);
				}
				else{
					etapa.setInicioEtapa(cronograma.getTiempoDuracion()/2+1);
					etapa.setFinEtapa(cronograma.getTiempoDuracion()-1);
				}
				entityManager.persist(etapa);
				entityManager.flush();
					
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.print(e.getMessage());
		}
	}

	public Cronograma_ensayo getCronograma() {
		return cronograma;
	}


	public void setCronograma(Cronograma_ensayo cronograma) {
		this.cronograma = cronograma;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}
	
	public String getTiempoDuracion() {
		return tiempoDuracion;
	}
	public void setTiempoDuracion(String tiempoDuracion) {
		this.tiempoDuracion = tiempoDuracion;
	}
	
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}



	public List<Etapa_ensayo> getEtapas() {
		return etapas;
	}



	public void setEtapas(List<Etapa_ensayo> etapas) {
		this.etapas = etapas;
	}



	public List<Cronograma_ensayo> getCronogramas() {
		return cronogramas;
	}



	public void setCronogramas(List<Cronograma_ensayo> cronogramas) {
		this.cronogramas = cronogramas;
	}



	public GrupoSujetos_ensayo getGrupoSubjeto() {
		return grupoSubjeto;
	}



	public void setGrupoSubjeto(GrupoSujetos_ensayo grupoSubjeto) {
		this.grupoSubjeto = grupoSubjeto;
	}



	public long getIdGrupoSujeto() {
		return idGrupoSujeto;
	}



	public void setIdGrupoSujeto(long idGrupoSujeto) {
		this.idGrupoSujeto = idGrupoSujeto;
	}
	
	
	

}
