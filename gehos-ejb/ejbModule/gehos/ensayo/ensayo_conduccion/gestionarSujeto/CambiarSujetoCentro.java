//CU 5 Cambiar sujeto de centro 
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.session.common.auto.SujetoList_ensayo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("cambiarSujetoCentro")
@Scope(ScopeType.CONVERSATION)
public class CambiarSujetoCentro extends SujetoList_ensayo {
	
	private static final String EJBQL = "select sujeto from Sujeto_ensayo sujeto "
			+ "where sujeto.eliminado = false and sujeto.grupoSujetos.estudio = #{cambiarSujetoCentro.seguridadEstudio.getEstudioEntidadActivo().getEstudio()} "
			+ "and sujeto.entidad = #{cambiarSujetoCentro.getHospitalActivo()} "
			+ "and sujeto.grupoSujetos.nombreGrupo <> 'Grupo Pesquisaje'";

	private static final String[] RESTRICTIONS = {
			"lower(sujeto.codigoPaciente) like concat('%',concat(lower(#{cambiarSujetoCentro.sujeto.codigoPaciente.trim()}),'%'))",
			"lower(sujeto.grupoSujetos.nombreGrupo) like concat(lower(#{cambiarSujetoCentro.nombeGrup.trim()}),'%')",
			"lower(sujeto.estadoInclusion.nombre) like concat(lower(#{cambiarSujetoCentro.estadoI.trim()}),'%')",
			"lower(sujeto.estadoTratamiento.nombre) like concat(lower(#{cambiarSujetoCentro.estadoT.trim()}),'%')",
			"lower(sujeto.fechaCreacion) like concat(lower(#{cambiarSujetoCentro.fechaInicioA.trim()}),'%')",
			"lower(sujeto.fechaCreacion) like concat(lower(#{cambiarSujetoCentro.fechaInicioP.trim()}),'%')",			
			"#{cambiarSujetoCentro.idSujeto} <> sujeto.id" };

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;

	private boolean existResultados = true;
	private String estadoI;
	private String estadoM;
	private String estadoT;
	private String nombeGrup;
	private Date fechaInicioA;
	private Date fechaInicioP;
	
	


	protected List<EstadoInclusion_ensayo> listarEstados;

	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private int pagina;
	private boolean gestionar = false;

	private Sujeto_ensayo sujeto = new Sujeto_ensayo();

	private Long idSujeto;

	public CambiarSujetoCentro() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("sujeto.id desc");
		this.gestionar = true;
	}
	
	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class , this.activeModule.getActiveModule().getEntidad().getId());
		
		return entidadEnsayo;
	}
	
	
	@SuppressWarnings("unchecked")
	public String  estadoMonitoreo(Long idSujeto){
		List<MomentoSeguimientoEspecifico_ensayo> listarMomentos = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager.createQuery(
				"select mom from MomentoSeguimientoEspecifico_ensayo mom where mom.sujeto.id=:idSuj").setParameter("idSuj", idSujeto).getResultList();
		String estado = "";
		boolean iniciado = false;
		boolean completado = true;
		
		for (int i = 0; i < listarMomentos.size(); i++) {
			if(listarMomentos.get(i).getEstadoMonitoreo().getCodigo() == 1){
				iniciado = true;
				break;
			}else if(listarMomentos.get(i).getEstadoMonitoreo().getCodigo() != 3){
				completado = false; 
				break;
			}
			
		}
		
		if(iniciado){
			estado =  "Iniciado";
		}else if(completado){
			estado = "Completado";
		}else {
			estado = "No iniciado";
		}
		return estado;
	}
	@SuppressWarnings("unchecked")
	public MomentoSeguimientoEspecifico_ensayo obtenerfechaMSAnterior(Long id){
		MomentoSeguimientoEspecifico_ensayo momento = null;
		try {
			Calendar cal=Calendar.getInstance();
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			List<MomentoSeguimientoEspecifico_ensayo> listaMomentosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>)entityManager.createQuery("select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.eliminado = false").setParameter("id", id).getResultList();
			List<MomentoSeguimientoEspecifico_ensayo> listaMenores = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			for (int i = 0; i < listaMomentosxSujeto.size(); i++) {
				if(listaMomentosxSujeto.get(i).getFechaInicio().before(cal.getTime())){
					listaMenores.add(listaMomentosxSujeto.get(i));
				}
			}
			momento = listaMenores.get(0);
			java.util.Date mayor = listaMenores.get(0).getFechaInicio();
			for (int j = 1; j < listaMenores.size(); j++) {
				if(listaMenores.get(j).getFechaInicio().after(mayor)){
					mayor = listaMenores.get(j).getFechaFin();
					momento = listaMenores.get(j);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return momento;
	}
	@SuppressWarnings("unchecked")
	public MomentoSeguimientoEspecifico_ensayo obtenerfechaProximoMS(Long id){
		MomentoSeguimientoEspecifico_ensayo momento = null;
		try {
			Calendar cal=Calendar.getInstance();
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			List<MomentoSeguimientoEspecifico_ensayo> listaMomentosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>)entityManager.createQuery("select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.eliminado = false").setParameter("id", id).getResultList();
			List<MomentoSeguimientoEspecifico_ensayo> listaMayores = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			for (int i = 0; i < listaMomentosxSujeto.size(); i++) {
				if(listaMomentosxSujeto.get(i).getFechaInicio().after(cal.getTime())){
					listaMayores.add(listaMomentosxSujeto.get(i));
				}
			}
			momento = listaMayores.get(0);
			java.util.Date menor = listaMayores.get(0).getFechaInicio();
			for (int j = 1; j < listaMayores.size(); j++) {
				if(listaMayores.get(j).getFechaInicio().before(menor)){
					menor = listaMayores.get(j).getFechaFin();
					momento = listaMayores.get(j);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return momento;
	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoI() {
		listarEstados = (List<EstadoInclusion_ensayo>) entityManager
				.createQuery("select e from EstadoInclusion_ensayo e")
				.getResultList();
		List<String> nombreEst = new ArrayList<String>();
		for (int i = 0; i < listarEstados.size(); i++) {
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}
	@SuppressWarnings("unchecked")
	public List<String> listEstadoT() {
		List<String> nombreEst = (List<String>) entityManager
				.createQuery("select e.nombre from EstadoTratamiento_ensayo e")
				.getResultList();
		return nombreEst;
	}
	@SuppressWarnings("unchecked")
	public List<String> listEstadoM() {
		List<String> nombreEst = (List<String>) entityManager
				.createQuery("select e.nombre from EstadoMonitoreo_ensayo e")
				.getResultList();
		return nombreEst;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listNombreG() {
		List<GrupoSujetos_ensayo> listarGrupo = (List<GrupoSujetos_ensayo>) entityManager.createQuery(
				"select g from GrupoSujetos_ensayo g where g.estudio=:estud").setParameter("estud", seguridadEstudio.getEstudioEntidadActivo().getEstudio()).getResultList();
		List<String> nombreG = new ArrayList<String>();
		for (int i = 0; i < listarGrupo.size(); i++) {
			nombreG.add(listarGrupo.get(i).getNombreGrupo());
		}
		return nombreG;
	}
	
	public void buscar() {
		this.refresh();
		this.setFirstResult(0);
		this.getResultList();
		this.existResultados = (this.getResultCount() != 0);
		setOrder("sujeto.id desc");
		}

	
	public void cambiarBusqueda() {
		if (this.displayBA.equals("display:none"))
			this.displayBA = "display:block";
		else
			this.displayBA = "display:none";

		if (this.displayBN.equals("display:none"))
			this.displayBN = "display:block";
		else
			this.displayBN = "display:none";
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	
	public Date getFechaInicioP() {
		return fechaInicioP;
	}


	public void setFechaInicioP(Date fechaInicioP) {
		this.fechaInicioP = fechaInicioP;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public String getDisplayBA() {
		return displayBA;
	}

	public void setDisplayBA(String displayBA) {
		this.displayBA = displayBA;
	}

	public String getDisplayBN() {
		return displayBN;
	}

	public void setDisplayBN(String displayBN) {
		this.displayBN = displayBN;
	}

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
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
	public String getEstadoI() {
		return estadoI;
	}

	public void setEstadoI(String estadoI) {
		this.estadoI = estadoI;
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public String getNombeGrup() {
		return nombeGrup;
	}

	public void setNombeGrup(String nombeGrup) {
		this.nombeGrup = nombeGrup;
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public String getEstadoT() {
		return estadoT;
	}

	public void setEstadoT(String estadoT) {
		this.estadoT = estadoT;
	}


	public String getEstadoM() {
		return estadoM;
	}


	public void setEstadoM(String estadoM) {
		this.estadoM = estadoM;
	}


	public Date getfechaInicioA() {
		return fechaInicioA;
	}


	public void setfechaInicioA(Date fechaInicioA) {
		this.fechaInicioA = fechaInicioA;
	}

}
