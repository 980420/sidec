
package gehos.ensayo.ensayo_conduccion.gestionarNotificaciones;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd.Notificacion;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoTratamiento_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Notificacion_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.session.common.auto.NotificacionList_ensayo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("notificacionCustomList")
@Scope(ScopeType.CONVERSATION)
public class NotificacionCustomList extends NotificacionList_ensayo {

	private static final String EJBQL = "select notificacion from Notificacion_ensayo notificacion "
			+ "where notificacion.idSujeto = #{notificacionCustomList.idSujeto} "
			+ "and notificacion.variableDato.eliminado = FALSE";
	
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Map<Long, List<MomentoSeguimientoEspecifico_ensayo>> listaFechasMomentos;
	MomentoSeguimientoEspecifico_ensayo momentoAnterior;
	MomentoSeguimientoEspecifico_ensayo momentoPosterior;
	private boolean existResultados = true;
	Calendar calenadrio = Calendar.getInstance();
	private String estadoI;
	private String estadoM;
	private String estadoT;
	private String nombeGrup;
	private Date fechaInicioA;
	private Date fechaInicioP;
	private Long idSujeto;

	@In
	private Usuario user;
	@In ReportManager reportManager;

	protected List<EstadoInclusion_ensayo> listarEstados;

	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private boolean seleccionar = false;
	private boolean modificar = false;
	private boolean ver = false;
	private int pagina;
	private boolean crear = false;
	private boolean gestionar = false;
	private boolean interrumpir = false;
	private List<String> filesFormatCombo;
	private String fileformatToExport;
	private String pathExportedReport;
	private Map pars;
	private ArrayList<Notificacion> listaNotificacion;
	
	//private Notificacion_ensayo notificacion = new Notificacion_ensayo();

	private Long ideliminarSuj, idhabilitado;

	public List<Notificacion_ensayo> getResultList() {
			this.setEjbql(EJBQL);
			Map<Integer, Notificacion_ensayo> noti=new HashMap<Integer, Notificacion_ensayo>();
			List<Notificacion_ensayo> original = super.getResultList();			
			for (int i=0; i < original.size(); i++) {
				int idGrupo=original.get(i).getVariableDato().getContGrupo();
				noti.put(idGrupo,original.get(i) );			
				
			}
			return new ArrayList<Notificacion_ensayo>(noti.values());
	}

	public NotificacionCustomList() {
		setEjbql(EJBQL);
		setMaxResults(10);
		setOrder("notificacion.id asc");
		this.ver = true;
		this.modificar = true;
		this.interrumpir = true;
		this.crear = true;
		this.gestionar = true;		
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
	
	public boolean pesquisajeHabilitado(){
		boolean habilitado = false;
		GrupoSujetos_ensayo grupo = new GrupoSujetos_ensayo();
		try {
			grupo = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select g from GrupoSujetos_ensayo g where g.estudio=:estud and g.nombreGrupo = 'Grupo Pesquisaje'")
					.setParameter("estud",
							seguridadEstudio.getEstudioEntidadActivo().getEstudio())
					.getSingleResult();
		} catch (Exception e) {
			grupo = null;
		}
		
		
		if(grupo != null && grupo.getHabilitado()){
			habilitado = true;
		}
		
		return habilitado;
	}

	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}

	@SuppressWarnings("unchecked")
	public String estadoMonitoreo(Long idSujeto) {
		List<MomentoSeguimientoEspecifico_ensayo> listarMomentos = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
				.createQuery(
						"select mom from MomentoSeguimientoEspecifico_ensayo mom where mom.sujeto.id=:idSuj")
				.setParameter("idSuj", idSujeto).getResultList();

		boolean iniciado = false;

		int contCompletado = 0;
		for (int i = 0; i < listarMomentos.size(); i++) {
			if (listarMomentos.get(i).getEstadoMonitoreo().getCodigo() == 1) {
				iniciado = true;
				break;
			} else if (listarMomentos.get(i).getEstadoMonitoreo().getCodigo() == 3) {
				contCompletado++;
			}
		}

		if (iniciado
				|| (contCompletado != 0 && contCompletado < listarMomentos
						.size())) {
			return "Iniciado";
		} else if (contCompletado == listarMomentos.size()) {
			return "Completado";
		} else {
			return "No iniciado";
		}

	}

	@SuppressWarnings("unchecked")
	public MomentoSeguimientoEspecifico_ensayo obtenerfechaMSAnterior(Long id) {
		momentoAnterior = null;
		try {
			Calendar cal = Calendar.getInstance();
			// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			List<MomentoSeguimientoEspecifico_ensayo> listaMomentosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
					.createQuery(
							"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.fechaInicio <:fecha and momento.eliminado = false and momento.momentoSeguimientoGeneral.programado = TRUE and (momento.estadoMomentoSeguimiento.codigo = 3 or momento.estadoMomentoSeguimiento.codigo = 5) ORDER BY momento.fechaInicio DESC")
							.setParameter("fecha", cal.getTime())
							.setParameter("id", id).getResultList();
			
			momentoAnterior = listaMomentosxSujeto.get(0);
		} catch (Exception e) {
			return null;
		}
		return momentoAnterior;
	}

	@SuppressWarnings("unchecked")
	public MomentoSeguimientoEspecifico_ensayo obtenerfechaProximoMS(Long id) {
		
		try {
			momentoPosterior = null;
			List<MomentoSeguimientoEspecifico_ensayo> listaMomentosAtrasadosxSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// la consulta devuelve los momentos de seguimiento no iniciados y
			// atrasados no iniciados por sujeto.

			listaMomentosAtrasadosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
					.createQuery(
							"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.fechaInicio <:fecha and momento.eliminado = false and momento.momentoSeguimientoGeneral.programado = TRUE and (momento.estadoMomentoSeguimiento.codigo = 4) and momento.id IN (select e.momentoSeguimientoEspecifico.id from CrdEspecifico_ensayo e where e.estadoHojaCrd.codigo = 2) ORDER BY momento.fechaInicio ASC")
							.setParameter("fecha", calenadrio.getTime())
							.setParameter("id", id).getResultList();
			//List<MomentoSeguimientoEspecifico_ensayo> listaMayoresAtrasados = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			if (listaMomentosAtrasadosxSujeto.size() > 0) {
				momentoPosterior = listaMomentosAtrasadosxSujeto.get(0);
			} else {
				List<MomentoSeguimientoEspecifico_ensayo> listaMomentosNoIniciadosxSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
				listaMomentosNoIniciadosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
						.createQuery(
								"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.fechaInicio <:fecha and momento.eliminado = false and momento.momentoSeguimientoGeneral.programado = TRUE and (momento.estadoMomentoSeguimiento.codigo = 2) ORDER BY momento.fechaInicio ASC")
								.setParameter("fecha", calenadrio.getTime())
								.setParameter("id", id).getResultList();
				momentoPosterior = listaMomentosNoIniciadosxSujeto.get(0);
			}
		} catch (Exception e) {
			return null;
		}
		return momentoPosterior;
	}

	@SuppressWarnings("unchecked")
	public boolean momentoAtrasadoNoIniciado(
			MomentoSeguimientoEspecifico_ensayo momeSegui) {
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();

		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(
				"select e from CrdEspecifico_ensayo e where e.momentoSeguimientoEspecifico.id=:IdMomento").setParameter("IdMomento", momeSegui.getId()).getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if (listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 2) {
				completa = false;
				break;
			}
		}
		return completa;

	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoI() {
		listarEstados = (List<EstadoInclusion_ensayo>) entityManager
				.createQuery("select e from EstadoInclusion_ensayo e")
				.getResultList();
		List<String> nombreEst = new ArrayList<String>();
		nombreEst.add("<Seleccione>");
		for (int i = 0; i < listarEstados.size(); i++) {
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoT() {
		List<String> nombreEstados = new ArrayList<String>();
		nombreEstados.add("<Seleccione>");
		List<EstadoTratamiento_ensayo> nombreEst = (List<EstadoTratamiento_ensayo>) entityManager
				.createQuery("select e from EstadoTratamiento_ensayo e")
				.getResultList();
		for (int i = 0; i < nombreEst.size(); i++) {
			nombreEstados.add(nombreEst.get(i).getNombre());
		}

		return nombreEstados;
	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoM() {
		List<String> nombreEst = (List<String>) entityManager.createQuery(
				"select e.nombre from EstadoMonitoreo_ensayo e")
				.getResultList();
		nombreEst.add(0, "<Seleccione>");
		return nombreEst;
	}

	@SuppressWarnings("unchecked")
	public List<String> listNombreG() {
		List<GrupoSujetos_ensayo> listarGrupo = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select g from GrupoSujetos_ensayo g where g.estudio=:estud")
				.setParameter("estud",
						seguridadEstudio.getEstudioEntidadActivo().getEstudio())
				.getResultList();
		List<String> nombreG = new ArrayList<String>();
		nombreG.add("<Seleccione>");
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

	public void SeleccionarInstanciaSuj(long id) {
		this.setideliminarSuj(id);
	}

	public void SeleccionarDeshabilitarSuj(long id) {
		this.setIdhabilitado(id);
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
	
	public String MomentoSegEstado(String momentoSeguimiento, String estado){
		return momentoSeguimiento + " (" + estado + ")";
	}
	
	public String evenetoAnombre(Long id)
	{
		String evento="";
		List<VariableDato_ensayo> var = (List<VariableDato_ensayo>) entityManager.createQuery("select v from VariableDato_ensayo v where v.cid=:id").setParameter("id", id).getResultList();
		
		for (int i = 0; i < var.size(); i++) 
		{
			if(var.get(i).getVariable().getNombreVariable().equals("Evento Adverso"))
			{
				evento= var.get(i).getValor();
			}
		}
		
		return evento;
	}
	
	//RF68 - Exportar reporte de notificaciones
	public void exportReportToFileFormat(){
		this.pathExportedReport = "";
		if (this.fileformatToExport.equals(this.filesFormatCombo.get(0)))
			this.pathExportedReport = this.reportManager.ExportReport("reportNotificacion", this.pars, this.listaNotificacion, FileType.PDF_FILE);
		else if (this.fileformatToExport.equals(this.filesFormatCombo.get(1)))
			this.pathExportedReport = this.reportManager.ExportReport("reportNotificacion", this.pars, this.listaNotificacion, FileType.RTF_FILE);
		else if (this.fileformatToExport.equals(this.filesFormatCombo.get(2)))
			this.pathExportedReport = this.reportManager.ExportReport("reportNotificacion", this.pars, this.listaNotificacion, FileType.EXCEL_FILE);
	}
	
	//RF61 - Visualizar notificaciones por sujeto
	@SuppressWarnings({"unchecked" })
	// Exportar reporte de notificaciones
	public void exportarDesdeAfuera(Long idNotificacion){
		this.listaNotificacion = new ArrayList<Notificacion>();
		this.pathExportedReport = "";
		this.pars = new HashMap();
		Notificacion_ensayo notificacion = (Notificacion_ensayo) entityManager.createQuery("select n from Notificacion_ensayo n where n.id=:id").setParameter("id", idNotificacion).getResultList().get(0);
				
		if (notificacion!=null){
			String d;
			String m;
			String a;
			String s;
			String n;
			this.pars.put("lbl_n_tituloEstudio", SeamResourceBundle.getBundle().getString("lbl_n_tituloEstudio"));
			this.pars.put("lbl_n_codEstudio", SeamResourceBundle.getBundle().getString("lbl_n_codEstudio"));
			this.pars.put("lbl_n_datosGSuj", SeamResourceBundle.getBundle().getString("lbl_n_datosGSuj"));
			this.pars.put("lbl_n_ini", SeamResourceBundle.getBundle().getString("lbl_n_ini"));
			this.pars.put("lbl_n_edad", SeamResourceBundle.getBundle().getString("lbl_n_edad"));
			this.pars.put("lbl_n_dias", SeamResourceBundle.getBundle().getString("lbl_n_dias"));
			this.pars.put("lbl_n_mes", SeamResourceBundle.getBundle().getString("lbl_n_mes"));
			this.pars.put("lbl_n_anno", SeamResourceBundle.getBundle().getString("lbl_n_anno"));
			this.pars.put("lbl_n_producto", SeamResourceBundle.getBundle().getString("lbl_n_producto"));
			this.pars.put("lbl_n_sitio", SeamResourceBundle.getBundle().getString("lbl_n_sitio"));
			this.pars.put("lbl_n_investigador", SeamResourceBundle.getBundle().getString("lbl_n_investigador"));
			this.pars.put("lbl_n_promotor", SeamResourceBundle.getBundle().getString("lbl_n_promotor"));
			this.pars.put("lbl_n_evento", SeamResourceBundle.getBundle().getString("lbl_n_evento"));
			this.pars.put("lbl_n_fechaO", SeamResourceBundle.getBundle().getString("lbl_n_fechaO"));
			this.pars.put("lbl_n_fechaU", SeamResourceBundle.getBundle().getString("lbl_n_fechaU"));
			this.pars.put("lbl_n_viaN", SeamResourceBundle.getBundle().getString("lbl_n_viaN"));
			this.pars.put("lbl_n_notifico", SeamResourceBundle.getBundle().getString("lbl_n_notifico"));
			this.pars.put("lbl_n_sii", SeamResourceBundle.getBundle().getString("lbl_n_sii"));
			this.pars.put("lbl_n_no", SeamResourceBundle.getBundle().getString("lbl_n_no"));
			this.pars.put("lbl_n_fechaN", SeamResourceBundle.getBundle().getString("lbl_n_fechaN"));
			this.pars.put("lbl_n_descripE", SeamResourceBundle.getBundle().getString("lbl_n_descripE"));
			this.pars.put("lbl_n_accion", SeamResourceBundle.getBundle().getString("lbl_n_accion"));
			this.pars.put("lbl_n_datosNotifica", SeamResourceBundle.getBundle().getString("lbl_n_datosNotifica"));
			this.pars.put("lbl_n_nombre", SeamResourceBundle.getBundle().getString("lbl_n_nombre"));
			this.pars.put("lbl_n_ape", SeamResourceBundle.getBundle().getString("lbl_n_ape"));
			this.pars.put("lbl_n_cargo", SeamResourceBundle.getBundle().getString("lbl_n_cargo"));
			this.pars.put("lbl_n_direccion", SeamResourceBundle.getBundle().getString("lbl_n_direccion"));
			this.pars.put("lbl_n_telefono", SeamResourceBundle.getBundle().getString("lbl_n_telefono"));
			this.pars.put("lbl_n_opinion", SeamResourceBundle.getBundle().getString("lbl_n_opinion"));
			this.pars.put("cuadroDialogImpr_Applet", SeamResourceBundle.getBundle().getString("cuadroDialogImpr_Applet"));
			this.pars.put("tituloEs", notificacion.gettituloEst());
			this.pars.put("codigoEs", notificacion.getcodigoEst());
			this.pars.put("iniciales", notificacion.getidentSujeto());
			this.pars.put("edad", notificacion.getedadSuj().toString());
			this.pars.put("productoIn", notificacion.getprodInv());
			this.pars.put("sitioIn", notificacion.getsitioInv());
			this.pars.put("investigador", notificacion.getinvestigador());
			this.pars.put("promotor", notificacion.getpromotor());
			this.pars.put("fechaOC", notificacion.getfechaOcu());
			this.pars.put("fechaAD", notificacion.getfechaUltA());
			this.pars.put("fechaN", notificacion.getfecha_notifico());
			this.pars.put("viaNoti", notificacion.getViaNoti());
			this.pars.put("descripEV", notificacion.getdescrEven());
			this.pars.put("accionTom", notificacion.getaccTomada());
			this.pars.put("nomb", notificacion.getnombreN());
			this.pars.put("apell", notificacion.getapellidosN());
			this.pars.put("cargo", notificacion.getcargoN());
			this.pars.put("telefono", notificacion.gettelefonoN());
			this.pars.put("direccion", notificacion.getdireccN());
			this.pars.put("opinion", notificacion.getopiInves());
			if (notificacion.getTipoEdad().equals(SeamResourceBundle.getBundle().getString("lbl_n_dias"))){
				d = "x";
				m = "";
				a = "";
			} else if (notificacion.getTipoEdad().equals(SeamResourceBundle.getBundle().getString("lbl_n_mes"))){
				d = "";
				m = "x";
				a = "";
			} else {
				d = "";
				m = "";
				a = "x";
			}
			this.pars.put("d", d);
			this.pars.put("m", m);
			this.pars.put("a", a);
			s = ((notificacion.getnotifico() != null && notificacion.getnotifico()) ? "x" : "");
			n = ((notificacion.getnotifico() == null || !notificacion.getnotifico()) ? "x" : "");
			this.pars.put("s", s);
			this.pars.put("n", n);
		}
		
		
//		for (int i = 0; i < var.size(); i++) 
//		{
//			if(var.get(i).getVariable().getNombreVariable().equals("Evento Adverso"))
//			{
//				evento= var.get(i).getValor();
//			}
//		}
	}
	
	public Sujeto_ensayo getSujeto(){
		return entityManager.find(Sujeto_ensayo.class, this.idSujeto);
	}

	public Long getideliminarSuj() {
		return ideliminarSuj;
	}

	public void setideliminarSuj(Long ideliminarSuj) {
		this.ideliminarSuj = ideliminarSuj;
	}

	public Long getIdhabilitado() {
		return idhabilitado;
	}

	public void setIdhabilitado(Long idhabilitado) {
		this.idhabilitado = idhabilitado;
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
		this.filesFormatCombo = this.reportManager.fileFormatsToExport();
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

	public boolean isSeleccionar() {
		return seleccionar;
	}

	public void setSeleccionar(boolean seleccionar) {
		this.seleccionar = seleccionar;
	}

	public int getPagina() {
		if (this.getNextFirstResult() != 0)
			return this.getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getResultCount() / 10) + 1;
		if (this.pagina > 0) {
			if (getResultCount() % 10 != 0) {
				if (pagina <= num)
					this.setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

	public boolean isModificar() {
		return modificar;
	}

	public boolean isInterrumpir() {
		return interrumpir;
	}

	public void setInterrumpir(boolean interrumpir) {
		this.interrumpir = interrumpir;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public boolean isVer() {
		return ver;
	}

	public void setVer(boolean ver) {
		this.ver = ver;
	}

	public boolean isCrear() {
		return crear;
	}

	public void setCrear(boolean crear) {
		this.crear = crear;
	}

	public String getEstadoI() {
		return estadoI;
	}

	public void setEstadoI(String estadoI) {
		this.estadoI = estadoI;
		if (this.estadoI.equals("<Seleccione>")) {
			this.estadoI = "";
		}
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
		if (this.nombeGrup.equals("<Seleccione>")) {
			this.nombeGrup = "";
		}
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
		if (this.estadoT.equals("<Seleccione>")) {
			this.estadoT = "";
		}
	}

	public String getEstadoM() {
		return estadoM;
	}

	public void setEstadoM(String estadoM) {
		this.estadoM = estadoM;
		if (this.estadoM.equals("<Seleccione>")) {
			this.estadoM = "";
		}
	}

	public Date getfechaInicioA() {
		return fechaInicioA;
	}

	public void setfechaInicioA(Date fechaInicioA) {
		this.fechaInicioA = fechaInicioA;
	}

	public MomentoSeguimientoEspecifico_ensayo getMomentoAnterior() {
		return momentoAnterior;
	}

	public void setMomentoAnterior(
			MomentoSeguimientoEspecifico_ensayo momentoAnterior) {
		this.momentoAnterior = momentoAnterior;
	}

	public MomentoSeguimientoEspecifico_ensayo getMomentoPosterior() {
		return momentoPosterior;
	}

	public void setMomentoPosterior(
			MomentoSeguimientoEspecifico_ensayo momentoPosterior) {
		this.momentoPosterior = momentoPosterior;
	}

	public Map<Long, List<MomentoSeguimientoEspecifico_ensayo>> getListaFechasMomentos() {
		return listaFechasMomentos;
	}

	public void setListaFechasMomentos(
			Map<Long, List<MomentoSeguimientoEspecifico_ensayo>> listaFechasMomentos) {
		this.listaFechasMomentos = listaFechasMomentos;
	}

	public Calendar getCalenadrio() {
		return calenadrio;
	}

	public void setCalenadrio(Calendar calenadrio) {
		this.calenadrio = calenadrio;
	}

	public List<String> getFilesFormatCombo() {
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}

	public ReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(ReportManager reportManager) {
		this.reportManager = reportManager;
	}

	public String getFileformatToExport() {
		return fileformatToExport;
	}

	public void setFileformatToExport(String fileformatToExport) {
		this.fileformatToExport = fileformatToExport;
	}

	public String getPathExportedReport() {
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}

	public Map getPars() {
		return pars;
	}

	public void setPars(Map pars) {
		this.pars = pars;
	}

	public ArrayList<Notificacion> getListaNotificacion() {
		return listaNotificacion;
	}

	public void setListaNotificacion(ArrayList<Notificacion> listaNotificacion) {
		this.listaNotificacion = listaNotificacion;
	}

}
