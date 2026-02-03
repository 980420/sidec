//<!--CU 8 Interrumpir sujeto-->
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstadoTratamiento_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("interrumpirSujeto")
@Scope(ScopeType.CONVERSATION)
public class InterrumpirSujeto {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	
	
	private Sujeto_ensayo sujeto;
	protected boolean fechaInterrupcionRequired = false;
	private Long idSujeto;
	
	private Date fechainterrupcion;
	private Date fechaInclusion;
	protected @In IBitacora bitacora;
	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void loadData() throws ParseException {
		if (this.fechaInterrupcionRequired)
			return;
		this.sujeto = (Sujeto_ensayo)entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:id").setParameter("id", idSujeto).getSingleResult();
		// Modificar fecha de interrupcion
		if(this.sujeto.getFechaInterrupcion() != null)
			this.fechainterrupcion = this.sujeto.getFechaInterrupcion();
		this.fechaInterrupcionRequired = true;
		if(this.sujeto.getFechaInclucion() != null){
			fechaInclucion();
		}
	}
	
	public String NombreSujetoById(){
		return sujeto.getCodigoPaciente();
	}
	
	public String validarCampo(){	
		if(fechainterrupcion != null){
			return "Richfaces.showModalPanel('mpAdvertenciaInterrumpirSuj')";
		}
		return "";
	}
	
	public Date fechaInclucion() throws ParseException{
		 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		 String[] fechaArreglo = this.sujeto.getFechaInclucion().split("/");
		 String fecha = "";
		 if(fechaArreglo.length == 1){
			 fecha = "01" + "/" + "01" + "/" + fechaArreglo[0];
		 }else if(fechaArreglo.length == 2){
			 fecha = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
		 }
		 else if(fechaArreglo.length == 3){
			 if(fechaArreglo[0].equals("****")){
				 fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
			 }else{
				 fecha = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
			 }
			 
		 }
	     this.fechaInclusion = formatter.parse(fecha);
		
		return fechaInclusion;
	}

	
	public void InterrumpirSujeto() throws ParseException {
		// Modificar fecha de interrupcion y otras cosas
		if(sujeto.getFechaInterrupcion() == null){
			Calendar cal=Calendar.getInstance();
			cal.setTime(fechainterrupcion);
			sujeto.setFechaInterrupcion(cal.getTime());
			long idEstado = 4;
			EstadoTratamiento_ensayo estado = entityManager.find(EstadoTratamiento_ensayo.class, idEstado);
			sujeto.setEstadoTratamiento(estado);
			entityManager.persist(sujeto);
			//Busco el cronograma que le corresponde al sujeto.			
	        Cronograma_ensayo cronograma_General=(Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:id").setParameter("id", sujeto.getGrupoSujetos().getId()).getSingleResult();
	        //Aqui tengo el momento de seguimiento general interrupcion
			MomentoSeguimientoGeneral_ensayo MomentosGeneral=(MomentoSeguimientoGeneral_ensayo)entityManager.createQuery("select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id=:id and msg.programado=False and msg.nombre='Interrupci\u00F3n' and msg.eliminado = false").setParameter("id", cronograma_General.getId()).getSingleResult();
		
			//List<String> listaDiasPlanificados =picarCadena(ms);
			long idEstadoSeguimiento=2;
			//EstadoMomentoSeguimiento_ensayo estadoMomento=(EstadoMomentoSeguimiento_ensayo) entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.id=:id").setParameter("id", idEstadoSeguimiento).getSingleResult();
			//EstadoMonitoreo_ensayo estadoMonitoreo=(EstadoMonitoreo_ensayo) entityManager.createQuery("select e from EstadoMonitoreo_ensayo e where e.id=:id").setParameter("id", idEstadoSeguimiento).getSingleResult();
			EstadoMomentoSeguimiento_ensayo estadoMomento=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimiento);
			EstadoMonitoreo_ensayo estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
			
				@SuppressWarnings("unchecked")
				List<HojaCrd_ensayo> listaHojas=entityManager.createQuery("select momentoGhojaCRD.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoGhojaCRD where momentoGhojaCRD.momentoSeguimientoGeneral.id=:id and momentoGhojaCRD.eliminado = 'false'").setParameter("id", MomentosGeneral.getId()).getResultList();
				
				MomentoSeguimientoEspecifico_ensayo momentoEspecifico=new MomentoSeguimientoEspecifico_ensayo();
				
				momentoEspecifico.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("momentoCrear")));
				momentoEspecifico.setEliminado(false);
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomento);
				momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo);
				momentoEspecifico.setFechaInicio(cal.getTime());
				cal=Calendar.getInstance();
				momentoEspecifico.setFechaCreacion(cal.getTime());
				momentoEspecifico.setMomentoSeguimientoGeneral(MomentosGeneral);
				momentoEspecifico.setSujeto(sujeto);
				
				
				//Poner dia nuevo
				@SuppressWarnings("unchecked")
				List<MomentoSeguimientoEspecifico_ensayo> momentosPro = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
						.createQuery(
								"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.eliminado = FALSE and mse.sujeto.id=:id order by  mse.dia, mse.fechaInicio asc")
						.setParameter("id", sujeto.getId()).getResultList();

				List<MomentoSeguimientoEspecifico_ensayo> momentos = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
				for (int k = 0; k < momentosPro.size(); k++) {
					if (!momentosPro.get(k).getMomentoSeguimientoGeneral()
							.getNombre().equals("Pesquisaje")
							&& !momentosPro.get(k)
									.getMomentoSeguimientoGeneral().getNombre()
									.equals("Evaluaci\u00F3n Inicial")) {
						momentos.add(momentosPro.get(k));
					}
				}

				if (momentos.size() != 0) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date fechaInicial = dateFormat.parse(dateFormat
							.format(momentos.get(0).getFechaInicio()));
					Date fechaFinal = dateFormat.parse(dateFormat
							.format(momentoEspecifico.getFechaInicio()));
					int dias = (int) ((fechaFinal.getTime() - fechaInicial
							.getTime()) / 86400000);
					momentoEspecifico.setDia(dias + momentos.get(0).getDia());
				} else {
					momentoEspecifico.setDia(0);
				}
				
				
				entityManager.persist(momentoEspecifico);
				
				String noIniciada="No iniciada";
				EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.nombre=:noIniciada").setParameter("noIniciada", noIniciada).getSingleResult();
				for (int j2 = 0; j2 < listaHojas.size(); j2++) {
					CrdEspecifico_ensayo crdEsp=new CrdEspecifico_ensayo();
					crdEsp.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("hojaCrear")));
					crdEsp.setEliminado(false);
					crdEsp.setEstadoHojaCrd(listaHojas.get(j2).getEstadoHojaCrd());
					crdEsp.setEstadoMonitoreo(estadoMonitoreo);
					crdEsp.setHojaCrd(listaHojas.get(j2));
					crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
					crdEsp.setEstadoHojaCrd(estadoNoIniciada);
					entityManager.persist(crdEsp);
				}
		}else{
			Calendar cal=Calendar.getInstance();
			cal.setTime(fechainterrupcion);
			Date fechaVieja = this.sujeto.getFechaInterrupcion();
			sujeto.setFechaInterrupcion(cal.getTime());
			entityManager.persist(sujeto);
			
	        //Aqui tengo el momento de seguimiento especificico interrupcion
			/*MomentoSeguimientoEspecifico_ensayo MomentosEspe =(MomentoSeguimientoEspecifico_ensayo)entityManager.createQuery("select msg from MomentoSeguimientoEspecifico_ensayo msg where msg.sujeto.id=:idSujeto and msg.fechaInicio=:fechaVieja and msg.momentoSeguimientoGeneral.nombre='Interrupci\u00F3n' and msg.eliminado = false").setParameter("idSujeto", this.sujeto.getId())
					.setParameter("fechaVieja", fechaVieja)
					.getSingleResult();*/
			MomentoSeguimientoEspecifico_ensayo MomentosEspe = (MomentoSeguimientoEspecifico_ensayo)entityManager.createQuery("select msg from MomentoSeguimientoEspecifico_ensayo msg where msg.sujeto.id=:idSujeto and msg.momentoSeguimientoGeneral.nombre='Interrupción' and msg.eliminado = false")
					.setParameter("idSujeto", this.sujeto.getId()).getSingleResult();
			
			MomentosEspe.setFechaInicio(cal.getTime());
		}
		
			
		entityManager.flush();

	}
	
	@End 
	public void salir(){		
		
	}
	
	@Remove @Destroy
	public void destroy(){}
	
	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	public Long getIdSujeto() {
		return idSujeto;
	}
	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}


	public Date getFechainterrupcion() {
		return fechainterrupcion;
	}

	public void setFechainterrupcion(Date fechainterrupcion) {
		this.fechainterrupcion = fechainterrupcion;
	}

	public boolean isFechaInterrupcionRequired() {
		return fechaInterrupcionRequired;
	}

	public void setFechaInterrupcionRequired(boolean fechaInterrupcionRequired) {
		this.fechaInterrupcionRequired = fechaInterrupcionRequired;
	}

	public Date getFechaInclusion() {
		return fechaInclusion;
	}

	public void setFechaInclusion(Date fechaInclusion) {
		this.fechaInclusion = fechaInclusion;
	}
	
}
