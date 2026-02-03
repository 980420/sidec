//CU 10 Visualizar MS por sujeto (progamados)
package gehos.ensayo.ensayo_conduccion.gestionarMS;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.session.common.auto.MomentoSeguimientoEspecificoList_ensayo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

@SuppressWarnings("serial")
@Name("listarMSP")
@Scope(ScopeType.CONVERSATION)
public class ListarMSP extends MomentoSeguimientoEspecificoList_ensayo {

	private static final String EJBQL = "select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.id = #{listarMSP.idSujeto} and ms.momentoSeguimientoGeneral.programado = 'true' and ms.momentoSeguimientoGeneral.nombre != 'Pesquisaje' and ms.eliminado = 'false' and ms.fechaInicio <= #{listarMSP.fechaActual}";
	// Obtiene todas las hojas crd de un momento de seguimiento que NO ESTAN DESHASOCIADAS 
	private static final String consultaHojaCrdMomentoSeguimiento="select crd from CrdEspecifico_ensayo crd inner join crd.hojaCrd HojaCRD inner join crd.momentoSeguimientoEspecifico MSE inner join MSE.momentoSeguimientoGeneral MSG inner join HojaCRD.momentoSeguimientoGeneralHojaCrds MSHoja where MSE.id=:momentoSeguimientoEspecifico and crd.eliminado = 'false' and MSHoja.eliminado = 'false' and MSHoja.momentoSeguimientoGeneral.id = MSG.id";
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;

	private boolean gestionar = false;
	
	private Sujeto_ensayo sujeto;
	private Long idSujeto;
	private int pagina;
	private Date fechaActual= Calendar.getInstance().getTime();
	
	@SuppressWarnings("unchecked")
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo();
		Calendar cal=Calendar.getInstance();
		long idEstadoSeguimientoAtra=4;
		long idEstadoSeguimientoInic=1;
		long idEstadoSeguimientoComp=3;
		long idEstadoSeguimientoFrim=5;
		
		//Monitoreo
		long idEstadoMonitoreoInic=1;
		long idEstadoMonitoreoComp=3;
		

		EstadoMonitoreo_ensayo estadoMonitoreoInic=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoMonitoreoInic);
		EstadoMonitoreo_ensayo estaMonitoreoComp=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoMonitoreoComp);
		//Fin Monitoreo
		
		
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasado=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoAtra);
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasadoInic=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoInic);
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasadoComp=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoComp);
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasadoFirm=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoFrim);
		List<MomentoSeguimientoEspecifico_ensayo> listaMomentos = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager.createQuery("select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id=:id and momento.eliminado = 'false' and momento.momentoSeguimientoGeneral.programado = TRUE and momento.momentoSeguimientoGeneral.nombre != 'Pesquisaje' and momento.fechaInicio <= #{listarMSP.fechaActual}").setParameter("id", this.idSujeto).getResultList();
		for (int i = 0; i < listaMomentos.size(); i++) {
			momentoEspecifico = listaMomentos.get(i);
			if(((cal.getTime()).compareTo(momentoEspecifico.getFechaFin()) > 0) && (momentoEspecifico.getEstadoMomentoSeguimiento().getCodigo() == 2)){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasado);
				entityManager.persist(momentoEspecifico);
			}
			if(atrasadoIniciado(momentoEspecifico)){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasadoInic);
				entityManager.persist(momentoEspecifico);
			}
			if(atrasadoCompletado(momentoEspecifico) ){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasadoComp);
				entityManager.persist(momentoEspecifico);
			}
			
			if(atrasadoFirmado(momentoEspecifico) ){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasadoFirm);
				entityManager.persist(momentoEspecifico);
			}
			
			
			if(monitoreoIniciado(momentoEspecifico)){
				momentoEspecifico.setEstadoMonitoreo(estadoMonitoreoInic);
				entityManager.persist(momentoEspecifico);
			}
			if(monitoreoCompletado(momentoEspecifico) ){
				momentoEspecifico.setEstadoMonitoreo(estaMonitoreoComp);
				entityManager.persist(momentoEspecifico);
			}
			
		}
		entityManager.flush();
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean atrasadoFirmado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
		boolean firmado = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())){
				firmado = false;
			}
			if(listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 4 && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())){
				firmado = false;
				break;
			}
		}
		return firmado;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean atrasadoIniciado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
		boolean completa = false;
		for (int i = 0; i < listaHojas.size(); i++) {
			if((listaHojas.get(i).getEstadoHojaCrd().getCodigo() == 1 && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())) || (listaHojas.get(i).getEstadoHojaCrd().getCodigo() == 1 && momeSegui.getEstadoMomentoSeguimiento().getCodigo() == 5)){
				completa = true;
				break;
			}
		}
		return completa;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean atrasadoCompletado(MomentoSeguimientoEspecifico_ensayo momeSegui){
        List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
        listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
        boolean completa = true;
        for (int i = 0; i < listaHojas.size(); i++) {
                if((listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 3) || (listaHojas.get(i).getEstadoHojaCrd().getCodigo() == 3 && momeSegui.getEstadoMomentoSeguimiento().getCodigo() == 5)){
                        completa = false;
                        break;
                }
        }
        return completa;
		
	}
	
	
	
	//Monitoreo
	@SuppressWarnings("unchecked")
	public boolean monitoreoIniciado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
		boolean completa = false;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(((listaHojas.get(i).getEstadoMonitoreo().getCodigo() == 1 || listaHojas.get(i).getEstadoMonitoreo().getCodigo() == 3) && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd()))){
				completa = true;
				break;
			}
		}
		return completa;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean monitoreoCompletado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.size() == 1 && estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())){
				completa = false;
			}
			if(listaHojas.get(i).getEstadoMonitoreo().getCodigo() != 3 && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())){
				completa = false;
				break;
			}
		}
		return completa;
		
	}
	
	
	
	public boolean estaEliminadaMomentoHojaCrd(MomentoSeguimientoGeneral_ensayo general, HojaCrd_ensayo hoja){
		try {
			boolean esta = false;
			MomentoSeguimientoGeneralHojaCrd_ensayo otro = (MomentoSeguimientoGeneralHojaCrd_ensayo) entityManager.createQuery("select mom from MomentoSeguimientoGeneralHojaCrd_ensayo mom where mom.momentoSeguimientoGeneral.id=:idMomGen and mom.hojaCrd.id=:idHoja")
					.setParameter("idMomGen", general.getId()).setParameter("idHoja", hoja.getId()).getSingleResult();
			
			if(otro!=null && otro.getEliminado()){
				esta = true;
			}
			return esta;
		} catch (NoResultException e) {
			return false;
		}
	}

	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = new Role_ensayo();
		rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}
	
	
	@Override
	public List<MomentoSeguimientoEspecifico_ensayo> getResultList() {

		if (entityManager.find(Sujeto_ensayo.class,this.idSujeto).getFechaInterrupcion() != null && !this.DevolverRol().getCodigo().equals("ecLab")) {

			String tempEJB = "select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.id = #{listarMSP.idSujeto} and ms.momentoSeguimientoGeneral.programado = 'true' and ms.eliminado = 'false' and ms.momentoSeguimientoGeneral.nombre != 'Pesquisaje' and ms.fechaInicio <= #{listarMSP.fechaActual} and (ms.sujeto.fechaInterrupcion >= ms.fechaInicio)";
			this.setEjbql(tempEJB);
		}else if(this.DevolverRol().getCodigo().equals("ecLab") && entityManager.find(Sujeto_ensayo.class,this.idSujeto).getFechaInterrupcion() != null){
			String tempEJB = "select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.id = #{listarMSP.idSujeto} and ms.momentoSeguimientoGeneral.programado = 'true' and ms.eliminado = 'false' and ms.momentoSeguimientoGeneral.nombre = 'Laboratorio' and ms.fechaInicio <= #{listarMSP.fechaActual} and (ms.sujeto.fechaInterrupcion >= ms.fechaInicio)";
			this.setEjbql(tempEJB);
		}else if(this.DevolverRol().getCodigo().equals("ecLab") && entityManager.find(Sujeto_ensayo.class,this.idSujeto).getFechaInterrupcion() == null){
			String tempEJB = "select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.id = #{listarMSP.idSujeto} and ms.momentoSeguimientoGeneral.programado = 'true' and ms.eliminado = 'false' and ms.momentoSeguimientoGeneral.nombre = 'Laboratorio' and ms.fechaInicio <= #{listarMSP.fechaActual}";
			this.setEjbql(tempEJB);
		}else {
			this.setEjbql(EJBQL);
		}

		return super.getResultList();

	}
		
	
	public Sujeto_ensayo ObtenerSujeto(){
		sujeto = (Sujeto_ensayo) entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:idSujeto").setParameter("idSujeto", this.idSujeto).getSingleResult();
		return sujeto;
	}

	public String NombreSujetoById() {
		String nom = "";
		Sujeto_ensayo otro = (Sujeto_ensayo) entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:idSujeto")
				.setParameter("idSujeto", this.idSujeto).getSingleResult();
		nom = otro.getCodigoPaciente();
		return nom;
	}

	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> NotasMonitoreoActONueva(Long idMomento) {
		String queryNotas = "select notaEnsayo "
				+ "from Nota_ensayo notaEnsayo "
				//+ "inner join notaEnsayo.estadoNota estado "
				+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
				+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
				+ "where momento.id=:idMomento and notaEnsayo.notaSitio='FALSE' and notaEnsayo.eliminado='FALSE' and notaEnsayo.notaPadre = null ";
				
				

		
		List<Nota_ensayo> datosNotas = entityManager.createQuery(queryNotas)
				.setParameter("idMomento", idMomento).getResultList();
		
		List<Nota_ensayo> nuevasOActualizadas = new ArrayList<Nota_ensayo>();
		for (int i = 0; i < datosNotas.size(); i++) {
			if(datosNotas.get(i).getEstadoNota().getCodigo() == 1 || datosNotas.get(i).getEstadoNota().getCodigo() == 2){
				nuevasOActualizadas.add(datosNotas.get(i));
			}
		}
		
		return datosNotas;
	}
	
	@SuppressWarnings("unchecked")
	public boolean ObtenerCRD(){
		List<CrdEspecifico_ensayo> Listcrd = new ArrayList<CrdEspecifico_ensayo>();
		boolean completa = false;
		String queryCRD = "select CrdEspecifico "
				+ "from CrdEspecifico_ensayo CrdEspecifico "
				+ "inner join CrdEspecifico.momentoSeguimientoEspecifico momentoEspecifico "
				+ "inner join momentoEspecifico.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "inner join momentoEspecifico.sujeto sujeto "
				+ "where sujeto.id=:idSujeto and momentoSeguimientoGeneral.nombre = 'Evaluaci\u00F3n Inicial' ";
				

		
		Listcrd = (List<CrdEspecifico_ensayo>)entityManager.createQuery(queryCRD)
				.setParameter("idSujeto", this.idSujeto).getResultList();
		if(Listcrd.size() == 0){
			completa = true;
		}
		
		for (int i = 0; i < Listcrd.size(); i++) {
			if(Listcrd.get(i).getEstadoHojaCrd().getCodigo() != 1 && Listcrd.get(i).getEstadoHojaCrd().getCodigo() != 3){
				completa = true;
				break;
			}
		}
		
		return completa;
	}
	
	
	
	public Integer CantidadNotasMActNuevas(Long idMomento){
		return NotasMonitoreoActONueva(idMomento).size();
	}

	@End 
	public void salir(){		
		
	}

	public ListarMSP() {
		setEjbql(EJBQL);
		setMaxResults(10);
		setOrder("ms.fechaInicio desc, ms.id desc");
		this.gestionar = true;
	}

	public Long getidSujeto() {
		return idSujeto;
	}

	public void setidSujeto(Long idS) {
		this.idSujeto = idS;
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	
	public int getPagina() {
		if(this.getNextFirstResult() != 0)
			return this.getNextFirstResult()/10;
			else
				return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
		
		long num=(getResultCount()/10)+1;
		if(this.pagina>0){
		if(getResultCount()%10!=0){
			if(pagina<=num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		else{
			if(pagina<num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		}
	}

	public Date getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(Date fechaActual) {
		this.fechaActual = fechaActual;
	}


	/*public Integer momentosSeguimientoAtrasados() {
		
		Integer totalMomentosAtrasados= 0;
		
		//Necesario para actualizar el estado de los momentos
		Calendar cal=Calendar.getInstance();
		long idEstadoSeguimientoAtra=4;
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasado=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoAtra);
		//obtener los grupos del estudio
		List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery("select grupo from GrupoSujetos_ensayo grupo "
						   + "where grupo.eliminado = FALSE and grupo.estudio.id =:idEstudioActivo")
				.setParameter("idEstudioActivo", seguridadEstudio.idEstudioActivo).getResultList();
		//Obtener los sujetos del grupo
		for (int i = 0; i < grupos.size(); i++) {
			List<Sujeto_ensayo> sujetosDelGrupo = entityManager.createQuery(
					"select sujeto from Sujeto_ensayo sujeto "
				  + "where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudioActivo and sujeto.eliminado = false order by sujeto.numeroInclucion")				
			.setParameter("idEstudioActivo", seguridadEstudio.idEstudioActivo)
			.setParameter("idGrupo",grupos.get(i).getId()).getResultList();
		//Obtener los MS
			if(!sujetosDelGrupo.isEmpty()){
				for(int k=0;k<sujetosDelGrupo.size();k++){					
					Integer momentosAtrasados=0;
					
					Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
					List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
							.createQuery(
									"select momento from MomentoSeguimientoEspecifico_ensayo momento "
								  + "where momento.sujeto.id =:idSujeto and momento.eliminado = false")
							.setParameter("idSujeto", sujeto.getId()).getResultList();
					//Calcular los momentos atrazados
					for(int l=0;l<momentosSujeto.size();l++){								
						
						MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
						
						//Actualizar el estado de los momentos atrasados
						if(momento.getFechaFin()!=null && ((cal.getTime()).compareTo(momento.getFechaFin()) > 0) && (momento.getEstadoMomentoSeguimiento().getCodigo() == 2)){
							momento.setEstadoMomentoSeguimiento(estadoMomentoAtrasado);
							entityManager.persist(momento);
						}
						
							//Estado de los momentos si el paciente es interrumpido	
						if(sujeto.getFechaInterrupcion()!=null && momentosSujeto.get(l).getFechaInicio().before(sujeto.getFechaInterrupcion()))	{
							if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
								momentosAtrasados++;									
						}
						//Estado de los momentos si el paciente no es interrumpido	
						if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
							momentosAtrasados++;
						
					}
					totalMomentosAtrasados+=momentosAtrasados;
				}
			}			
		}		
		
		return totalMomentosAtrasados;
	}*/
	
	@SuppressWarnings("unchecked")
	public Integer momentosSeguimientoAtrasados(){		
		
		Integer totalMomentosAtrasados=0;			
				
		//Necesario para actualizar el estado atrasado de los momentos de seguimiento
		Calendar cal=Calendar.getInstance();
		long idEstadoSeguimientoAtra=4;
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasado=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoAtra);
		
		List<EstudioEntidad_ensayo> listaEntidadEst;
		listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
				.createQuery("select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false")
				.setParameter("idEst",seguridadEstudio.getActiveStudyId()).getResultList();
		
		for (int z = 0; z < listaEntidadEst.size(); z++) {
			List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager
					.createQuery("select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
					.setParameter("idEstudio",seguridadEstudio.getActiveStudyId()).getResultList();
			
			for (int i = 0; i < grupos.size(); i++) {
				List<Sujeto_ensayo> sujetosDelGrupo = entityManager
						.createQuery("select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false order by sujeto.numeroInclucion")				
				.setParameter("idEstudio",seguridadEstudio.getActiveStudyId())	
				.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
				.setParameter("idGrupo",grupos.get(i).getId()).getResultList();
									
				if(!sujetosDelGrupo.isEmpty()){
					for(int k=0;k<sujetosDelGrupo.size();k++){								
						Integer momentosAtrasados=0;								
						
						Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
						List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
								.createQuery("select momento from MomentoSeguimientoEspecifico_ensayo momento "
										   + "where momento.sujeto.id =:idSujeto and momento.eliminado = false")
								.setParameter("idSujeto", sujeto.getId()).getResultList();			
						
						for(int l=0;l<momentosSujeto.size();l++){								
								
							MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
							
							//Actualizar el estado de los momentos atrasados
							if(momento.getFechaFin()!=null && ((cal.getTime()).compareTo(momento.getFechaFin()) > 0) && (momento.getEstadoMomentoSeguimiento().getCodigo() == 2)){
								momento.setEstadoMomentoSeguimiento(estadoMomentoAtrasado);
								entityManager.persist(momento);
							}
							
								//Estado de los momentos si el paciente es interrumpido	
							if(sujeto.getFechaInterrupcion()!=null && momentosSujeto.get(l).getFechaInicio().before(sujeto.getFechaInterrupcion()))	{
								if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
									momentosAtrasados++;								
							}
							//Estado de los momentos si el paciente no es interrumpido	
							else if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
								momentosAtrasados++;						
						}
						totalMomentosAtrasados+=momentosAtrasados;							 
					}					
				}						
			}								
		}
		return totalMomentosAtrasados;
	}
}



