//CU 13 Gestionar datos de hoja CRD
package gehos.ensayo.ensayo_conduccion.gestionarCRD;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.ReporteExpeditoDos;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.ReporteExpeditoTres;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.ReporteExpeditoUno;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.valorGrupoWraper;
import gehos.ensayo.ensayo_conduccion.gestionarMS.WrapperMomento;
import gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito.ReDataSource;
import gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito.ReEnfermedadesDataSource;
import gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito.ReFarmacosDataSource;
import gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito.ReProductoDataSource;
import gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito.ReVacunasDataSource;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd.Notificacion;
import gehos.ensayo.ensayo_disenno.session.reglas.ReglaPlayer;
import gehos.ensayo.ensayo_disenno.session.reglas.components.mailer.mail;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.OpenModalRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.ensayo_disenno.session.reporteExpedito.ReporteExpeditoConduccion;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EFaseEstudio_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Notificacion_ensayo;
import gehos.ensayo.entity.ReConsecuenciaspaciente_ensayo;
import gehos.ensayo.entity.ReEnfermedadesinter_ensayo;
import gehos.ensayo.entity.ReHistoriarelevante_ensayo;
import gehos.ensayo.entity.ReInfoproducto_ensayo;
import gehos.ensayo.entity.ReMedicacion_ensayo;
import gehos.ensayo.entity.ReMedicacionconcomitante_ensayo;
import gehos.ensayo.entity.ReMedicacionreciente_ensayo;
import gehos.ensayo.entity.ReReporteexpedito_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Remove;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.persistence.NonUniqueResultException;


import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

@Name("gestionarHoja")
@Scope(ScopeType.CONVERSATION)
public class GestionarHoja {

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	@In(create = true) WrapperMomento wrapperMomento;
	protected @In(create = true) FacesMessages facesMessages;
	@In(create = true) IdUtil idUtil;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In private Usuario user;
	@In private IActiveModule activeModule;
	@In ReportManager reportManager;

	private List<ReReporteexpedito_ensayo> listaReporte = new ArrayList<ReReporteexpedito_ensayo>();
	private long cid = -1;
	private String descripcionCausaReiniciarHoja;
	@SuppressWarnings("rawtypes")
	private Map pars;
	@SuppressWarnings("rawtypes")
	private List<Collection> listadoreporteExpeditoGeneral;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	private List<ReporteExpeditoUno> listadoreporteExpeditoUno;
	private List<ReporteExpeditoDos> listadoreporteExpeditoDos;
	private List<ReporteExpeditoTres> listadoreporteExpeditoTres;
	protected List<GrupoSujetos_ensayo> listarGrupo;

	private List<ReMedicacion_ensayo> listaProductosEstudios = new ArrayList<ReMedicacion_ensayo>();
	private List<ReProductoDataSource> listaProductos = new ArrayList<ReProductoDataSource>();
	private List<ReMedicacion_ensayo> listaMedicacionConm = new ArrayList<ReMedicacion_ensayo>();
	private List<ReVacunasDataSource> listaVacunas = new ArrayList<ReVacunasDataSource>();
	private List<ReMedicacion_ensayo> listaEnfermedadesInter = new ArrayList<ReMedicacion_ensayo>();
	private List<ReEnfermedadesDataSource> listaEnfermedades = new ArrayList<ReEnfermedadesDataSource>();
	private List<ReMedicacion_ensayo> listaMedicacionRec = new ArrayList<ReMedicacion_ensayo>();
	private List<ReFarmacosDataSource> listaFarmacos = new ArrayList<ReFarmacosDataSource>();

	private String pathExportedReport;
	private String fileformatToExport;
	private List<String> filesFormatCombo;
	private List<String> subReportNames;
	private Estudio_ensayo estudioE;

	FacesContext aFacesContext = FacesContext.getCurrentInstance();
	ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
	@SuppressWarnings("rawtypes")
	private List<Map> subReportPars;
	private List<Seccion_ensayo> secciones;
	private List<GrupoVariables_ensayo> grupoVariables;
	private Map<Long, List<GrupoVariables_ensayo>> listaGrupoVariables;
	private ArrayList<Integer> listaCant;
	private ArrayList<Notificacion> listaNotificacion;
	private Role_ensayo rolLogueado;
	private CrdEspecifico_ensayo hoja, hojaCRD;
	private Long idcrd;
	private Sujeto_ensayo sujetoIncluido;
	
	private HSSFFont headerFont;
	private HSSFFont contentFont;
	private String path;
	private String nombreReport;
	private String fromP;
	private EstudioEntidad_ensayo estudioEntidad;
	private static final String tmpFolder = "tmpFolder";

	private boolean completada = false, completedFromStart = false , desdeCompleta = false;
	private String causaGuardar = "";

	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle().getString("caracteresEspeciales");

	// Optimizar el flujo relacionado con la gestion de grupos de variables
	private Map<String, Integer> mapPositions = new HashMap<String, Integer>();
	private Map<String, List<WrapperGroupData>> mapWGD = new HashMap<String, List<WrapperGroupData>>();
	private Map<String, List<WrapperGroupData>> mapBackupWGD = new HashMap<String, List<WrapperGroupData>>();
	private Map<Long, WrapperGroupData> mapWD = new HashMap<Long, WrapperGroupData>();
	private Map<Long, WrapperGroupData> mapBackupWD = new HashMap<Long, WrapperGroupData>();
	private static final String keySeparator = ".";
	private String selectedTab;
	private boolean monitoringCompleted = false;
	private boolean monitoringNotStarted = false;
	private boolean firmada = false;
	private Map<String, List<Variable_ensayo>> mapVariablesGroupsList = new HashMap<String, List<Variable_ensayo>>();
	private Map<Long, List<Variable_ensayo>> mapVariablesNotGroupsList = new HashMap<Long, List<Variable_ensayo>>();
	private Map<Long, List<GrupoVariables_ensayo>> mapGroupsList = new HashMap<Long, List<GrupoVariables_ensayo>>();
	
	

	// End Refactoring changes atributes

	public String getCausaGuardar(){
		return causaGuardar;
	}

	public void setCausaGuardar(String causaGuardar){
		this.causaGuardar = causaGuardar;
	}

	private String causaDescMonitoreo;

	private String from = "";
	private boolean causaRequired = false;
	private boolean esReq = true;
	private Long idTabala;
	private Long idSujeto, idGrupo;
	private Long idMS;
	private boolean inicializado = false;
	// folder to store files
	File fileFolder;

	// @Begin(join=true, flushMode=FlushModeType.MANUAL)
	// /public void cargar()
	// /{
	// / filesFormatCombo = reportManager.fileFormatsToExport();
	// /}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation(){
		if (this.inicializado)
			return;
		this.mapWGD = new HashMap<String, List<WrapperGroupData>>();
		this.mapBackupWGD = new HashMap<String, List<WrapperGroupData>>();
		this.mapWD = new HashMap<Long, WrapperGroupData>();
		this.mapBackupWD = new HashMap<Long, WrapperGroupData>();
		this.selectedTab = "";
		if (this.mapPositions == null)
			this.mapPositions = new HashMap<String, Integer>();
		this.filesFormatCombo = this.reportManager.fileFormatsToExport();
		this.estudioEntidad = this.seguridadEstudio.getEstudioEntidadActivo();
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
		String rootpath = context.getRealPath("/");
		this.usuario = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
		this.fileFolder = new File(rootpath);
		this.fileFolder = this.fileFolder.getParentFile().getParentFile(); // here we are in deploy folder
		this.fileFolder = new File(this.fileFolder.getPath() + File.separator + ".archivos");// this folder has a dot (".") to prevent the server from deploying its files
		if (!this.fileFolder.exists())
			this.fileFolder.mkdir();
		if (this.idcrd != null){
			this.hoja = this.entityManager.find(CrdEspecifico_ensayo.class, this.idcrd);
			if (this.hoja != null){
				this.loadSections();
				if (this.secciones != null && !this.secciones.isEmpty())
					this.cambiarTab(this.secciones.get(0).getId());
				for (Iterator<Seccion_ensayo> iterator = this.secciones.iterator(); iterator.hasNext();){
					Seccion_ensayo seccion_ensayo = iterator.next();
					this.loadVariableData(seccion_ensayo);
					this.loadGroupData(seccion_ensayo);
				}
				this.inicializado = true;
				this.causaRequired = true;
				this.monitoringCompleted = (this.hoja.getEstadoMonitoreo() != null && this.hoja.getEstadoMonitoreo().getNombre() != null && !this.hoja.getEstadoMonitoreo().getNombre().isEmpty() && this.hoja.getEstadoMonitoreo().getNombre().equals("Completado"));
				this.monitoringNotStarted = (this.hoja.getEstadoMonitoreo() != null && this.hoja.getEstadoMonitoreo().getNombre() != null && !this.hoja.getEstadoMonitoreo().getNombre().isEmpty() && this.hoja.getEstadoMonitoreo().getNombre().equals("No iniciado"));
				this.completada = (this.hoja.getEstadoHojaCrd() != null && this.hoja.getEstadoHojaCrd().getNombre() != null && !this.hoja.getEstadoHojaCrd().getNombre().isEmpty() && (this.hoja.getEstadoHojaCrd().getNombre().equals("Completada") || this.hoja.getEstadoHojaCrd().getNombre().equals("Firmada")));
				this.completedFromStart = this.completada;
				this.firmada=this.hoja.getEstadoHojaCrd().getNombre().equals("Firmada");
			}
			this.sujetoIncluido = this.hoja.getMomentoSeguimientoEspecifico().getSujeto();
			this.cantidadNotasDiscrepancias();
			this.obtainRole();
			this.listarGrupo();
			for (Entry<Long, WrapperGroupData> itemEntry : this.mapWD.entrySet()){
				WrapperGroupData owgd = new WrapperGroupData();
				if (itemEntry.getValue() != null){
					owgd.setSection(itemEntry.getValue().getSection());
					if (itemEntry.getValue().getData() != null && !itemEntry.getValue().getData().isEmpty()){
						for (MapWrapperDataPlus itemData : itemEntry.getValue().getData().values()){
							if (!owgd.getData().containsKey(itemData.getVariable().getId())){
								owgd.getData().put(itemData.getVariable().getId(), itemData.copyData());
								if (!owgd.hasReport() && itemData.hasReport())
									owgd.setReport(itemData.getReport());
								if (!owgd.hasNotification() && itemData.hasNotification())
									owgd.setNotification(itemData.getNotification());
							}
						}
					}
					if (owgd.getData() != null && !owgd.getData().isEmpty())
						this.mapBackupWD.put(itemEntry.getKey(), owgd);
				}
			}
			for (Entry<String, List<WrapperGroupData>> itemEntry : this.mapWGD.entrySet()){
				List<WrapperGroupData> owgds = new ArrayList<WrapperGroupData>();
				if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
					for (WrapperGroupData itemWGD : itemEntry.getValue()){
						WrapperGroupData owgd = new WrapperGroupData();
						owgd.setSection(itemWGD.getSection());
						owgd.setGroup(itemWGD.getGroup());
						owgd.setRepetition(itemWGD.getRepetition());
						if (itemWGD != null && itemWGD.getData() != null && !itemWGD.getData().isEmpty()){
							for (MapWrapperDataPlus itemData : itemWGD.getData().values()){
								if (!owgd.getData().containsKey(itemData.getVariable().getId())){
									owgd.getData().put(itemData.getVariable().getId(), itemData.copyData());
									if (!owgd.hasReport() && itemData.hasReport())
										owgd.setReport(itemData.getReport());
									if (!owgd.hasNotification() && itemData.hasNotification())
										owgd.setNotification(itemData.getNotification());
								}
							}
						}
						if (owgd.getData() != null && !owgd.getData().isEmpty())
							owgds.add(owgd);
					}
				}
				if (!owgds.isEmpty()){
					Collections.sort(owgds);
					this.mapBackupWGD.put(itemEntry.getKey(), owgds);
				}
			}
			
			this.refillData();
		}
	}
	
	
	private void refillData(){
		for (Iterator<Seccion_ensayo> iterator = this.secciones.iterator(); iterator.hasNext();) {
			this.loadGroupData(iterator.next());
		}
		for (Entry<Long, WrapperGroupData> itemEntry : this.mapWD
				.entrySet()) {
			WrapperGroupData owgd = new WrapperGroupData();
			if (itemEntry.getValue() != null) {
				owgd.setSection(itemEntry.getValue().getSection());
				if (itemEntry.getValue().getData() != null
						&& !itemEntry.getValue().getData().isEmpty()) {
					for (MapWrapperDataPlus itemData : itemEntry.getValue()
							.getData().values()) {
						if (!owgd.getData().containsKey(
								itemData.getVariable().getId())) {
							owgd.getData().put(
									itemData.getVariable().getId(),
									itemData.copyData());
							if (!owgd.hasReport() && itemData.hasReport())
								owgd.setReport(itemData.getReport());
							if (!owgd.hasNotification()
									&& itemData.hasNotification())
								owgd.setNotification(itemData
										.getNotification());
						}
					}
				}
				if (owgd.getData() != null && !owgd.getData().isEmpty())
					this.mapBackupWD.put(itemEntry.getKey(), owgd);
			}
		}
		for (Entry<String, List<WrapperGroupData>> itemEntry : this.mapWGD
				.entrySet()) {
			List<WrapperGroupData> owgds = new ArrayList<WrapperGroupData>();
			if (itemEntry.getValue() != null
					&& !itemEntry.getValue().isEmpty()) {
				for (WrapperGroupData itemWGD : itemEntry.getValue()) {
					WrapperGroupData owgd = new WrapperGroupData();
					owgd.setSection(itemWGD.getSection());
					owgd.setGroup(itemWGD.getGroup());
					owgd.setRepetition(itemWGD.getRepetition());
					if (itemWGD != null && itemWGD.getData() != null
							&& !itemWGD.getData().isEmpty()) {
						for (MapWrapperDataPlus itemData : itemWGD
								.getData().values()) {
							if (!owgd.getData().containsKey(
									itemData.getVariable().getId())) {
								owgd.getData().put(
										itemData.getVariable().getId(),
										itemData.copyData());
								if (!owgd.hasReport()
										&& itemData.hasReport())
									owgd.setReport(itemData.getReport());
								if (!owgd.hasNotification()
										&& itemData.hasNotification())
									owgd.setNotification(itemData
											.getNotification());
							}
						}
					}
					if (owgd.getData() != null && !owgd.getData().isEmpty())
						owgds.add(owgd);
				}
			}
			if (!owgds.isEmpty()) {
				Collections.sort(owgds);
				this.mapBackupWGD.put(itemEntry.getKey(), owgds);
			}
		}
	}

	public boolean seccioRequerida(Long idSeccion){
		boolean requerida = false;
		
		if (this.mapVariablesNotGroupsList.containsKey(idSeccion)){
			for (Variable_ensayo item : this.mapVariablesNotGroupsList.get(idSeccion)){
				if (item.getRequerido() == true){
					requerida = true;
					break;
				}
			}
		}
		
		for (int z = 0; z < this.groupsFromSection(idSeccion).size(); z++){
			Long idGrupo = this.groupsFromSection(idSeccion).get(z).getId();
			if (this.mapVariablesGroupsList.containsKey(this.convertKey(idSeccion, idGrupo))){
				for (Variable_ensayo item : this.mapVariablesGroupsList.get(this.convertKey(idSeccion, idGrupo))){
					if (item.getRequerido() == true){
						requerida = true;
						break;
					}
				}
			}
		}	
		return requerida;
	}

	public void setearEsReq(){
		esReq = false;
	}
	
	@SuppressWarnings("unchecked")
	public void descompletarHoja(){
		// Cambiar el estado de la hoja a Iniciado
		EstadoHojaCrd_ensayo estadoCompletada = (EstadoHojaCrd_ensayo) this.entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.codigo = 1").getSingleResult();
		this.hoja.setEstadoHojaCrd(estadoCompletada);
		
		// Cambiar el estado de monitoreo a No iniciado
		long idMon = 2;
		this.hoja.setEstadoMonitoreo(this.entityManager.find(EstadoMonitoreo_ensayo.class, idMon));
		this.hoja.getMomentoSeguimientoEspecifico().setEstadoMonitoreo(this.entityManager.find(EstadoMonitoreo_ensayo.class, idMon));
		// Eliminar notas si existen
		List<Nota_ensayo> lista = new ArrayList<Nota_ensayo>();
		lista = (List<Nota_ensayo>) this.entityManager.createQuery("select nota from Nota_ensayo nota where nota.crdEspecifico=:Hoj and nota.eliminado = 'FALSE' and nota.notaPadre = null").setParameter("Hoj", this.hoja).getResultList();
		if(!lista.isEmpty()){
			for (Nota_ensayo nota_ensayo : lista) {
				nota_ensayo.setEliminado(true);
				this.entityManager.merge(nota_ensayo);
			}
		}
		
		this.entityManager.merge(hoja);
		this.entityManager.flush();
	}

	public String esEvento(){
		if (!this.hoja.getHojaCrd().getNombreHoja().equals("Eventos Adversos") && !this.hoja.getHojaCrd().getNombreHoja().equals("Evento Adverso"))
			return "onSave(data)";
		else
			return "nadie";
	}

	public String esEventoReRender(){
		if (!this.hoja.getHojaCrd().getNombreHoja().equals("Eventos Adversos") && !this.hoja.getHojaCrd().getNombreHoja().equals("Evento Adverso"))
			return "modalContainerDiv";
		else
			return "nadie";
	}

	public int sizeMaxString(List<String> listReturn){
		if (listReturn.size() == 0)
			return 150;
		int max = listReturn.get(0).length();
		for (int i = 1; i < listReturn.size(); i++){
			if (listReturn.get(i).length() > max)
				max = listReturn.get(i).length();
		}
		if ((max * 5) > 150)
			return max * 5;
		return 150;
	}

	public void SeleccionarInstanciaHoja(CrdEspecifico_ensayo hojaCRD){
		this.hojaCRD = hojaCRD;
	}
	
	public void desdeCompletar(){
		this.desdeCompleta = true;
	}
	

	public boolean Mostrar(int lista, int repeticiones){
		return (lista < repeticiones);
	}

	public boolean showGroup(Long seccionId, Long groupId){
		if (seccionId != null && groupId != null){
			String tempKey = this.convertKey(seccionId, groupId);
			if (tempKey != null){
				GrupoVariables_ensayo tempGroup = null;
				try {
					tempGroup = this.entityManager.find(GrupoVariables_ensayo.class, groupId);
				} catch (Exception e){
					tempGroup = null;
				}
				return (tempGroup != null ? ((this.mapWGD.get(tempKey) != null) ? (this.mapWGD.get(tempKey).size() < tempGroup.getNumMaxRepeticiones()) : true) : false);
			} else
				return false;
		} else
			return false;
	}

	public int longitud(Long id){
		String cad_valor = String.valueOf(id);
		int longitud = cad_valor.length();
		return longitud;
	}

	public void iniciarMonitoreo(){
		this.monitoringNotStarted = false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void exportNotificacion(Long sectionId, Long groupId, Integer repetition){
		this.listaNotificacion = new ArrayList<Notificacion>();
		this.pars = new HashMap();
		if (sectionId != null && sectionId.toString() != null && !sectionId.toString().isEmpty() && groupId != null && groupId.toString() != null && !groupId.toString().isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().isEmpty()){
			String tempKey = this.convertKey(sectionId, groupId);
			if (tempKey != null && this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(tempKey)){
				int index = this.indexOfGroupData(this.mapWGD.get(tempKey), repetition);
				if (index != -1){
					Notificacion_ensayo tempNotification = ((this.mapWGD.get(tempKey).get(index) != null && this.mapWGD.get(tempKey).get(index).getNotification() != null) ? this.mapWGD.get(tempKey).get(index).getNotification() : null);
					if (tempNotification != null){
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
						this.pars.put("tituloEs", tempNotification.gettituloEst());
						this.pars.put("codigoEs", tempNotification.getcodigoEst());
						this.pars.put("iniciales", tempNotification.getidentSujeto());
						this.pars.put("edad", tempNotification.getedadSuj().toString());
						this.pars.put("productoIn", tempNotification.getprodInv());
						this.pars.put("sitioIn", tempNotification.getsitioInv());
						this.pars.put("investigador", tempNotification.getinvestigador());
						this.pars.put("promotor", tempNotification.getpromotor());
						this.pars.put("fechaOC", tempNotification.getfechaOcu());
						this.pars.put("fechaAD", tempNotification.getfechaUltA());
						this.pars.put("fechaN", tempNotification.getfecha_notifico());
						this.pars.put("viaNoti", tempNotification.getViaNoti());
						this.pars.put("descripEV", tempNotification.getdescrEven());
						this.pars.put("accionTom", tempNotification.getaccTomada());
						this.pars.put("nomb", tempNotification.getnombreN());
						this.pars.put("apell", tempNotification.getapellidosN());
						this.pars.put("cargo", tempNotification.getcargoN());
						this.pars.put("telefono", tempNotification.gettelefonoN());
						this.pars.put("direccion", tempNotification.getdireccN());
						this.pars.put("opinion", tempNotification.getopiInves());
						if (tempNotification.getTipoEdad().equals(SeamResourceBundle.getBundle().getString("lbl_n_dias"))){
							d = "x";
							m = "";
							a = "";
						} else if (tempNotification.getTipoEdad().equals(SeamResourceBundle.getBundle().getString("lbl_n_mes"))){
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
						s = ((tempNotification.getnotifico() != null && tempNotification.getnotifico()) ? "x" : "");
						n = ((tempNotification.getnotifico() == null || !tempNotification.getnotifico()) ? "x" : "");
						this.pars.put("s", s);
						this.pars.put("n", n);
					}
				}
			}
		}
	}

	public void exportReportToFileFormat(){
		this.pathExportedReport = "";
		if (this.fileformatToExport.equals(this.filesFormatCombo.get(0)))
			this.pathExportedReport = this.reportManager.ExportReport("reportNotificacion", this.pars, this.listaNotificacion, FileType.PDF_FILE);
		else if (this.fileformatToExport.equals(this.filesFormatCombo.get(1)))
			this.pathExportedReport = this.reportManager.ExportReport("reportNotificacion", this.pars, this.listaNotificacion, FileType.RTF_FILE);
		else if (this.fileformatToExport.equals(this.filesFormatCombo.get(2)))
			this.pathExportedReport = this.reportManager.ExportReport("reportNotificacion", this.pars, this.listaNotificacion, FileType.EXCEL_FILE);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void exportReportExpedito(Long sectionId, Long groupId,
			Integer repetition){
		this.pathExportedReport = "";
		this.pars = new HashMap();
		this.subReportNames = new ArrayList<String>();
		this.subReportPars = new ArrayList<Map>();
		this.listadoreporteExpeditoGeneral = new ArrayList<Collection>();
		this.listaProductosEstudios = new ArrayList<ReMedicacion_ensayo>();
		this.listaMedicacionConm = new ArrayList<ReMedicacion_ensayo>();
		this.listaEnfermedadesInter = new ArrayList<ReMedicacion_ensayo>();
		this.listaMedicacionRec = new ArrayList<ReMedicacion_ensayo>();
		this.listaProductos = new ArrayList<ReProductoDataSource>();
		this.listaVacunas = new ArrayList<ReVacunasDataSource>();
		this.listaEnfermedades = new ArrayList<ReEnfermedadesDataSource>();
		this.listaFarmacos = new ArrayList<ReFarmacosDataSource>();
		if (sectionId != null && sectionId.toString() != null && !sectionId.toString().isEmpty() && groupId != null && groupId.toString() != null && !groupId.toString().isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().isEmpty()){
			String tempKey = this.convertKey(sectionId, groupId);
			if (tempKey != null && this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(tempKey)){
				int index = this.indexOfGroupData(this.mapWGD.get(tempKey), repetition);
				if (index != -1){
					ReReporteexpedito_ensayo tempReport = ((this.mapWGD.get(tempKey).get(index) != null && this.mapWGD.get(tempKey).get(index).getReport() != null && this.mapWGD.get(tempKey).get(index).getReport().getReport() != null) ? this.mapWGD.get(tempKey).get(index).getReport().getReport() : null);
					if (tempReport != null){
						this.estudioE = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio();
						String nombreEst = "";
						String codigoEst = "";
						String paciente = "";
						String iniciales = "";
						nombreEst = this.estudioE.getNombre();
						codigoEst = this.estudioE.getIdentificador();
						EFaseEstudio_ensayo phase = ((this.mapWGD.get(tempKey).get(index) != null && this.mapWGD.get(tempKey).get(index).getReport() != null && this.mapWGD.get(tempKey).get(index).getReport().getFase() != null) ? this.mapWGD.get(tempKey).get(index).getReport().getFase() : null);
						paciente = this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
						iniciales = this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getInicialesPaciente();
						Usuario_ensayo usuarioCambiar = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
						String[] consecuencia = new String[10];
						List<ReDataSource> listTEmp = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp1 = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp2 = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp3 = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp4 = new ArrayList<ReDataSource>();
						ReDataSource temp = new ReDataSource();
						ReDataSource temp1 = new ReDataSource();
						ReDataSource temp2 = new ReDataSource();
						ReDataSource temp3 = new ReDataSource();
						ReDataSource temp4 = new ReDataSource();
						ReProductoDataSource producto = new ReProductoDataSource();
						ReVacunasDataSource vacunas = new ReVacunasDataSource();
						ReEnfermedadesDataSource enfermedades = new ReEnfermedadesDataSource();
						ReFarmacosDataSource farmacos = new ReFarmacosDataSource();
						List<ReHistoriarelevante_ensayo> listaHistoria = new ArrayList<ReHistoriarelevante_ensayo>();
						if (tempReport.getId() != 0){
							this.listaProductosEstudios = this.entityManager.createQuery("select medicacion.reMedicacion from ReInfoproducto_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							this.listaMedicacionConm = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionconcomitante_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							this.listaEnfermedadesInter = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReEnfermedadesinter_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							this.listaMedicacionRec = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionreciente_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							listaHistoria = (List<ReHistoriarelevante_ensayo>) this.entityManager.createQuery("select historia from ReHistoriarelevante_ensayo historia where historia.reReporteexpedito.id = :reporteId").setParameter("reporteId", tempReport.getId()).getResultList();
						} else {
							List<ReInfoproducto_ensayo> tempL = new ArrayList<ReInfoproducto_ensayo>(tempReport.getReInfoproducto());
							for (int i = 0; i < tempL.size(); i++)
								this.listaProductosEstudios.add(tempL.get(i).getReMedicacion());
							List<ReMedicacionconcomitante_ensayo> tempL1 = new ArrayList<ReMedicacionconcomitante_ensayo>(tempReport.getReMedicacionconcomitantes());
							for (int i = 0; i < tempL1.size(); i++)
								this.listaMedicacionConm.add(tempL1.get(i).getReMedicacion());
							List<ReEnfermedadesinter_ensayo> tempL2 = new ArrayList<ReEnfermedadesinter_ensayo>(tempReport.getReEnfermedadesinter());
							for (int i = 0; i < tempL2.size(); i++)
								this.listaEnfermedadesInter.add(tempL2.get(i).getReMedicacion());
							List<ReMedicacionreciente_ensayo> tempL3 = new ArrayList<ReMedicacionreciente_ensayo>(tempReport.getReMedicacionrecientes());
							for (int i = 0; i < tempL3.size(); i++)
								this.listaMedicacionRec.add(tempL3.get(i).getReMedicacion());
							listaHistoria = new ArrayList<ReHistoriarelevante_ensayo>(tempReport.getReHistoriarelevantes());
						}
						for (int i = 0; i < listaHistoria.size(); i++){
							ReConsecuenciaspaciente_ensayo enfermedad = this.entityManager.find(ReConsecuenciaspaciente_ensayo.class, listaHistoria.get(i).getIdReEnfermedades());
							consecuencia[i] = enfermedad.getDescripcion();
						}
						Map pars1 = new HashMap();
						pars1.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars1.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars1.put("lbl_fechaReporteA", SeamResourceBundle.getBundle().getString("lbl_fechaReporteA"));
						pars1.put("lbl_informacion_general", SeamResourceBundle.getBundle().getString("lbl_informacion_general1"));
						pars1.put("lbl_informeIni", SeamResourceBundle.getBundle().getString("lbl_informeIni"));
						pars1.put("lbl_informeAdd", SeamResourceBundle.getBundle().getString("lbl_informeAdd"));
						pars1.put("lbl_protocolo_titulo", SeamResourceBundle.getBundle().getString("lbl_protocolo_titulo1"));
						pars1.put("lbl_fase", SeamResourceBundle.getBundle().getString("lbl_fase"));
						pars1.put("lbl_revisado_dir", SeamResourceBundle.getBundle().getString("lbl_revisado_dir"));
						pars1.put("lbl_institucion", SeamResourceBundle.getBundle().getString("lbl_institucion"));
						pars1.put("lbl_prod_investigacion", SeamResourceBundle.getBundle().getString("lbl_prod_investigacion"));
						pars1.put("lbl_dosis_Admin", SeamResourceBundle.getBundle().getString("lbl_dosis_Admin"));
						pars1.put("lbl_presentacion", SeamResourceBundle.getBundle().getString("lbl_presentacion"));
						pars1.put("lbl_via_Admin", SeamResourceBundle.getBundle().getString("lbl_via_Admin"));
						pars1.put("lbl_investigador", SeamResourceBundle.getBundle().getString("lbl_investigador"));
						pars1.put("lbl_consecuencias_paciente_single", SeamResourceBundle.getBundle().getString("lbl_consecuencias_paciente_single"));
						pars1.put("lbl_muerte", SeamResourceBundle.getBundle().getString("lbl_muerte"));
						pars1.put("lbl_autopsia", SeamResourceBundle.getBundle().getString("lbl_autopsia"));
						pars1.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars1.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars1.put("lbl_peligro_vida", SeamResourceBundle.getBundle().getString("lbl_peligro_vida"));
						pars1.put("lbl_prod_discapacidad", SeamResourceBundle.getBundle().getString("lbl_prod_discapacidad"));
						pars1.put("lbl_requiere_hos", SeamResourceBundle.getBundle().getString("lbl_requiere_hos"));
						pars1.put("lbl_fecha_ingreso", SeamResourceBundle.getBundle().getString("lbl_fecha_ingreso"));
						pars1.put("lbl_fecha_alta", SeamResourceBundle.getBundle().getString("lbl_fecha_alta"));
						pars1.put("lbl_prolonga_estancia", SeamResourceBundle.getBundle().getString("lbl_prolonga_estancia"));
						pars1.put("lbl_anomalia", SeamResourceBundle.getBundle().getString("lbl_anomalia"));
						pars1.put("lbl_acont_medico", SeamResourceBundle.getBundle().getString("lbl_acont_medico"));
						pars1.put("lbl_especifique", SeamResourceBundle.getBundle().getString("lbl_especifique"));
						pars1.put("lbl_otros", SeamResourceBundle.getBundle().getString("lbl_otros"));
						pars1.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars1.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars1.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars1.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars1.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars1.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars1.put("protCod", codigoEst);
						pars1.put("iniPa", paciente);
						pars1.put("fechaReporteA", tempReport.getFechaCECMEDA());
						if (tempReport.getInformeinicial() != null){
							if (tempReport.getInformeinicial()){
								String a = "x";
								String b = "";
								pars1.put("informeIni", a);
								pars1.put("informeAdd", b);
							} else {
								String a = "";
								String b = "x";
								pars1.put("informeIni", a);
								pars1.put("informeAdd", b);
							}
						} else {
							String a = "";
							String b = "";
							pars1.put("informeIni", a);
							pars1.put("informeAdd", b);
						}
						pars1.put("protocolo_titulo", nombreEst);
						pars1.put("fase", ((phase != null && phase.getNombre() != null && !phase.getNombre().isEmpty()) ? phase.getNombre() : SeamResourceBundle.getBundle().getString("vacio")));
						pars1.put("revisado_dir", tempReport.getPromotor());
						pars1.put("institucion", nombreEst);
						pars1.put("prod_investigacion", tempReport.getProductoinvestigado());
						pars1.put("dosis_Admin", tempReport.getDosisadministrada());
						pars1.put("presentacion", tempReport.getPresentacion());
						pars1.put("via_Admin", tempReport.getReViasadmi().getDescripcion());
						pars1.put("investigador", tempReport.getInvestigador());
						for (int i = 0; i < consecuencia.length; i++){
							if (consecuencia[i] != null && !consecuencia[i].isEmpty()){
								if (consecuencia[i].trim() == SeamResourceBundle.getBundle().getString("lbl_muerte").trim()){
									pars1.put("muerte", "x");
									pars1.put((tempReport.getAutopsia() ? "autopsiaS" : "autopsiaN"), "x");
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_peligro_vida").trim()))
									pars1.put("peligro_vida", "x");
								else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_prod_discapacidad").trim()))
									pars1.put("prod_discapacidad", "x");
								else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_requiere_hos").trim())){
									pars1.put("requiere_hos", "x");
									pars1.put("fecha_ingreso", tempReport.getFechaingreso());
									pars1.put("fecha_alta", tempReport.getFechaalta());
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_prolonga_estancia").trim())){
									pars1.put("prolonga_estancia", "x");
									pars1.put("fecha_alta2", tempReport.getFechaaltaprol());
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_anomalia").trim()))
									pars1.put("anomalia", "x");
								else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_acont_medico").trim())){
									pars1.put("acont_medico", "x");
									pars1.put("especifiqueAcont", tempReport.getEspecifiqueacont());
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_otros").trim())){
									pars1.put("otros", "x");
									pars1.put("especifiqueOtros", tempReport.getEspecifiqueotros());
								}
							}
						}
						pars1.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars1.put("direccion", tempReport.getDireccion());
						pars1.put("telefono", usuarioCambiar.getTelefono());
						pars1.put("profesion", tempReport.getProfesion());
						pars1.put("fechaReporte1", tempReport.getFechareporteA());
						pars1.put("firma", "____");
						this.subReportPars.add(pars1);
						this.subReportNames.add("reporExpedito_subreport1");
						listTEmp.add(temp);
						this.listadoreporteExpeditoGeneral.add(listTEmp);
						Map pars2 = new HashMap();
						pars2.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars2.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars2.put("lbl_fechaReporteA", SeamResourceBundle.getBundle().getString("lbl_fechaReporteA"));
						pars2.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars2.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars2.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars2.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars2.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars2.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars2.put("lbl_informacion_datos_demograficos", SeamResourceBundle.getBundle().getString("lbl_informacion_datos_demograficos"));
						pars2.put("lbl_iniciales", SeamResourceBundle.getBundle().getString("lbl_iniciales"));
						pars2.put("lbl_fecha_nacimiento", SeamResourceBundle.getBundle().getString("lbl_fecha_nacimiento"));
						pars2.put("lbl_sexo", SeamResourceBundle.getBundle().getString("lbl_sexo"));
						pars2.put("lbl_masculino", SeamResourceBundle.getBundle().getString("lbl_masculino"));
						pars2.put("lbl_femenino", SeamResourceBundle.getBundle().getString("lbl_femenino"));
						pars2.put("lbl_peso", SeamResourceBundle.getBundle().getString("lbl_peso"));
						pars2.put("lbl_talla", SeamResourceBundle.getBundle().getString("lbl_talla"));
						pars2.put("lbl_raza", SeamResourceBundle.getBundle().getString("lbl_raza"));
						pars2.put("lbl_bl", SeamResourceBundle.getBundle().getString("lbl_bl"));
						pars2.put("lbl_ne", SeamResourceBundle.getBundle().getString("lbl_ne"));
						pars2.put("lbl_me", SeamResourceBundle.getBundle().getString("lbl_me"));
						pars2.put("lbl_otra", SeamResourceBundle.getBundle().getString("lbl_otra"));
						pars2.put("lbl_accionEmprendida", SeamResourceBundle.getBundle().getString("lbl_accionEmprendida"));
						pars2.put("lbl_ninguna", SeamResourceBundle.getBundle().getString("lbl_ninguna"));
						pars2.put("lbl_mod_dosis", SeamResourceBundle.getBundle().getString("lbl_mod_dosis"));
						pars2.put("lbl_pospone", SeamResourceBundle.getBundle().getString("lbl_pospone"));
						pars2.put("lbl_interrumpe", SeamResourceBundle.getBundle().getString("lbl_interrumpe"));
						pars2.put("lbl_acontecimientos", SeamResourceBundle.getBundle().getString("lbl_acontecimientos"));
						pars2.put("lbl_reaparicion", SeamResourceBundle.getBundle().getString("lbl_reaparicion"));
						pars2.put("lbl_no_aparece", SeamResourceBundle.getBundle().getString("lbl_no_aparece"));
						pars2.put("lbl_desconocimiento", SeamResourceBundle.getBundle().getString("lbl_desconocimiento"));
						pars2.put("lbl_no_procede", SeamResourceBundle.getBundle().getString("lbl_no_procede"));
						pars2.put("lbl_evento_adverso", SeamResourceBundle.getBundle().getString("lbl_evento_adverso"));
						pars2.put("lbl_relacioCausalidad", SeamResourceBundle.getBundle().getString("lbl_relacioCausalidad"));
						pars2.put("lbl_fuente", SeamResourceBundle.getBundle().getString("lbl_fuente"));
						pars2.put("lbl_oms", SeamResourceBundle.getBundle().getString("lbl_oms"));
						pars2.put("lbl_fda", SeamResourceBundle.getBundle().getString("lbl_fda"));
						pars2.put("lbl_otras", SeamResourceBundle.getBundle().getString("lbl_otras"));
						pars2.put("lbl_fecha_hora_ini", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_ini"));
						pars2.put("lbl_fecha_hora_fina", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_fina"));
						pars2.put("lbl_intensidad", SeamResourceBundle.getBundle().getString("lbl_intensidad"));
						pars2.put("lbl_lugarOcurrio", SeamResourceBundle.getBundle().getString("lbl_lugarOcurrio"));
						pars2.put("lbl_hogar", SeamResourceBundle.getBundle().getString("lbl_hogar"));
						pars2.put("lbl_hospital", SeamResourceBundle.getBundle().getString("lbl_hospital"));
						pars2.put("lbl_piliclinico", SeamResourceBundle.getBundle().getString("lbl_piliclinico"));
						pars2.put("lbl_vacunatorio", SeamResourceBundle.getBundle().getString("lbl_vacunatorio"));
						pars2.put("lbl_cinfantil", SeamResourceBundle.getBundle().getString("lbl_cinfantil"));
						pars2.put("lbl_escuela", SeamResourceBundle.getBundle().getString("lbl_escuela"));
						pars2.put("lbl_asilo", SeamResourceBundle.getBundle().getString("lbl_asilo"));
						pars2.put("lbl_otros", SeamResourceBundle.getBundle().getString("lbl_otrosL"));
						pars2.put("lbl_especifique", SeamResourceBundle.getBundle().getString("lbl_especifique"));
						pars2.put("lbl_desenlace", SeamResourceBundle.getBundle().getString("lbl_desenlace"));
						pars2.put("lbl_recu", SeamResourceBundle.getBundle().getString("lbl_recu"));
						pars2.put("lbl_recu_secuelas", SeamResourceBundle.getBundle().getString("lbl_recu_secuelas"));
						pars2.put("lbl_mejor", SeamResourceBundle.getBundle().getString("lbl_mejor"));
						pars2.put("lbl_persist", SeamResourceBundle.getBundle().getString("lbl_persist"));
						pars2.put("lbl_muerteD", SeamResourceBundle.getBundle().getString("lbl_muerteD"));
						pars2.put("lbl_abandonado", SeamResourceBundle.getBundle().getString("lbl_abandonado"));
						pars2.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars2.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars2.put("protCod", codigoEst);
						pars2.put("iniPa", paciente);
						pars2.put("fechaReporteA", tempReport.getFechaCECMEDB());
						pars2.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars2.put("direccion", tempReport.getDireccion());
						pars2.put("telefono", usuarioCambiar.getTelefono());
						pars2.put("profesion", tempReport.getProfesion());
						pars2.put("fechaReporte1", tempReport.getFechareporteB());
						pars2.put("iniciales", iniciales);
						pars2.put("fecha_nacimiento", tempReport.getFechanacimiento());
						pars2.put("masculino", (tempReport.getSexo().equals(SeamResourceBundle.getBundle().getString("lbl_masculino")) ? "x" : ""));
						pars2.put("femenino", (!tempReport.getSexo().equals(SeamResourceBundle.getBundle().getString("lbl_masculino")) ? "x" : ""));
						pars2.put("peso", tempReport.getPeso().toString());
						pars2.put("talla", tempReport.getTalla().toString());
						if (tempReport.getRaza().equals(SeamResourceBundle.getBundle().getString("lbl_bl"))){
							pars2.put("bl", "x");
							pars2.put("ne", "");
							pars2.put("me", "");
							pars2.put("otra", "");
							pars2.put("espe", "");
						} else if (tempReport.getRaza().equals(SeamResourceBundle.getBundle().getString("lbl_ne"))){
							pars2.put("bl", "");
							pars2.put("ne", "x");
							pars2.put("me", "");
							pars2.put("otra", "");
							pars2.put("espe", "");
						} else if (tempReport.getRaza().equals(SeamResourceBundle.getBundle().getString("lbl_me"))){
							pars2.put("bl", "");
							pars2.put("ne", "");
							pars2.put("me", "x");
							pars2.put("otra", "");
							pars2.put("espe", "");
						} else {
							pars2.put("bl", "");
							pars2.put("ne", "");
							pars2.put("me", "");
							pars2.put("otra", "x");
							pars2.put("espe", tempReport.getOtraraza());
						}
						if (tempReport.getReCrealizadaeas().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_ninguna"))){
							pars2.put("ninguna", "x");
							pars2.put("pospone", "");
							pars2.put("interrumpe", "");
							pars2.put("mod_dosis", "");
						} else if (tempReport.getReCrealizadaeas().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_pospone"))){
							pars2.put("ninguna", "");
							pars2.put("pospone", "x");
							pars2.put("interrumpe", "");
							pars2.put("mod_dosis", "");
						} else if (tempReport.getReCrealizadaeas().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_interrumpe"))){
							pars2.put("ninguna", "");
							pars2.put("pospone", "");
							pars2.put("interrumpe", "x");
							pars2.put("mod_dosis", "");
						} else {
							pars2.put("ninguna", "");
							pars2.put("pospone", "");
							pars2.put("interrumpe", "");
							pars2.put("mod_dosis", "x");
						}
						if (tempReport.getAcontecimiento() != null){
							if (tempReport.getAcontecimiento().equals(SeamResourceBundle.getBundle().getString("lbl_reaparicion"))){
								pars2.put("reaparicion", "x");
								pars2.put("no_aparece", "");
								pars2.put("desconocimiento", "");
								pars2.put("no_procede", "");
							} else if (tempReport.getAcontecimiento().equals(SeamResourceBundle.getBundle().getString("lbl_no_aparece"))){
								pars2.put("reaparicion", "");
								pars2.put("no_aparece", "x");
								pars2.put("desconocimiento", "");
								pars2.put("no_procede", "");
							} else if (tempReport.getAcontecimiento().equals(SeamResourceBundle.getBundle().getString("lbl_desconocimiento"))){
								pars2.put("reaparicion", "");
								pars2.put("no_aparece", "");
								pars2.put("desconocimiento", "x");
								pars2.put("no_procede", "");
							} else {
								pars2.put("reaparicion", "");
								pars2.put("no_aparece", "");
								pars2.put("desconocimiento", "");
								pars2.put("no_procede", "x");
							}
						} else {
							pars2.put("reaparicion", "");
							pars2.put("no_aparece", "");
							pars2.put("desconocimiento", "");
							pars2.put("no_procede", "");
						}
						pars2.put("evento_adverso", tempReport.getEventoadverso());
						pars2.put("relacioCausalidad", tempReport.getCausalidad());
						if (tempReport.getFuente().equals(SeamResourceBundle.getBundle().getString("lbl_oms"))){
							pars2.put("oms", "x");
							pars2.put("fda", "");
							pars2.put("otras", "");
						} else if (tempReport.getFuente().equals(SeamResourceBundle.getBundle().getString("lbl_fda"))){
							pars2.put("oms", "");
							pars2.put("fda", "x");
							pars2.put("otras", "");
						} else {
							pars2.put("oms", "");
							pars2.put("fda", "");
							pars2.put("otras", "x");
						}
						pars2.put("otrasespe", tempReport.getOtrafuente());
						pars2.put("fechai", tempReport.getFechainicio());
						pars2.put("horai", tempReport.getHorainicio());
						pars2.put("fechaf", tempReport.getFechafinalizacion());
						pars2.put("horaf", tempReport.getHorafinalizacion());
						if (tempReport.getReIntensidad().getCodigo().toString().equals("1")){
							pars2.put("1", "x");
							pars2.put("2", "");
							pars2.put("3", "");
							pars2.put("4", "");
							pars2.put("5", "");
						} else if (tempReport.getReIntensidad().getCodigo().toString().equals("2")){
							pars2.put("1", "");
							pars2.put("2", "x");
							pars2.put("3", "");
							pars2.put("4", "");
							pars2.put("5", "");
						} else if (tempReport.getReIntensidad().getCodigo().toString().equals("3")){
							pars2.put("1", "");
							pars2.put("2", "");
							pars2.put("3", "x");
							pars2.put("4", "");
							pars2.put("5", "");
						} else if (tempReport.getReIntensidad().getCodigo().toString().equals("4")){
							pars2.put("1", "");
							pars2.put("2", "");
							pars2.put("3", "");
							pars2.put("4", "x");
							pars2.put("5", "");
						} else {
							pars2.put("1", "");
							pars2.put("2", "");
							pars2.put("3", "");
							pars2.put("4", "");
							pars2.put("5", "x");
						}
						if (tempReport.getDondeocurrio().equals("1")){
							pars2.put("hogar", "x");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("2")){
							pars2.put("hogar", "");
							pars2.put("hospital", "x");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("3")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "x");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("4")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "x");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("5")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "x");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("6")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "x");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("7")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "x");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else {
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "x");
							pars2.put("especifiqueL", tempReport.getOtrolugar());
						}
						if (tempReport.getReResultadofinal() != null){
							if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_recu"))){
								pars2.put("recu", "x");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "");
								pars2.put("persist", "");
								pars2.put("muerteD", "");
							} else if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_recu_secuelas"))){
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "x");
								pars2.put("mejor", "");
								pars2.put("persist", "");
								pars2.put("muerteD", "");
							} else if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_mejor"))){
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "x");
								pars2.put("persist", "");
								pars2.put("muerteD", "");
							} else if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_persist"))){
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "");
								pars2.put("persist", "x");
								pars2.put("muerteD", "");
							} else {
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "");
								pars2.put("persist", "");
								pars2.put("muerteD", "x");
							}
						} else {
							pars2.put("recu", "");
							pars2.put("recu_secuelas", "");
							pars2.put("mejor", "");
							pars2.put("persist", "");
							pars2.put("muerteD", "");
						}
						pars2.put("siA", ((tempReport.getAbndonoelsujeto() != null && tempReport.getAbndonoelsujeto()) ? "x" : ""));
						pars2.put("noA", ((tempReport.getAbndonoelsujeto() == null || !tempReport.getAbndonoelsujeto()) ? "x" : ""));
						pars2.put("firma", "____");
						this.subReportPars.add(pars2);
						this.subReportNames.add("reporExpedito_subreport2");
						listTEmp1.add(temp1);
						this.listadoreporteExpeditoGeneral.add(listTEmp1);
						Map pars3 = new HashMap();
						pars3.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars3.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars3.put("lbl_EAGI", SeamResourceBundle.getBundle().getString("lbl_EAGI"));
						pars3.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars3.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars3.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars3.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars3.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars3.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars3.put("lbl_productos_estudio", SeamResourceBundle.getBundle().getString("lbl_productos_estudio"));
						pars3.put("lbl_producto", SeamResourceBundle.getBundle().getString("lbl_producto"));
						pars3.put("lbl_lote", SeamResourceBundle.getBundle().getString("lbl_lote"));
						pars3.put("lbl_dosis", SeamResourceBundle.getBundle().getString("lbl_dosis"));
						pars3.put("lbl_frecuencia", SeamResourceBundle.getBundle().getString("lbl_frecuencia"));
						pars3.put("lbl_via", SeamResourceBundle.getBundle().getString("lbl_via"));
						pars3.put("lbl_fecha_hora_A", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_A"));
						pars3.put("lbl_terminoTrat", SeamResourceBundle.getBundle().getString("lbl_terminoTrat"));
						pars3.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars3.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars3.put("lbl_indicacion", SeamResourceBundle.getBundle().getString("lbl_indicacion"));
						pars3.put("lbl_vacunas_concamitante", SeamResourceBundle.getBundle().getString("lbl_vacunas_concamitante"));
						pars3.put("lbl_presentacion", SeamResourceBundle.getBundle().getString("lbl_presentacionV"));
						pars3.put("lbl_enfermedades_intecurrentes", SeamResourceBundle.getBundle().getString("lbl_enfermedades_intecurrentes"));
						pars3.put("lbl_persiste", SeamResourceBundle.getBundle().getString("lbl_persiste"));
						pars3.put("protCod", codigoEst);
						pars3.put("iniPa", paciente);
						pars3.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars3.put("direccion", tempReport.getDireccion());
						pars3.put("telefono", usuarioCambiar.getTelefono());
						pars3.put("profesion", tempReport.getProfesion());
						pars3.put("fechaReporte1", tempReport.getFechareporteC());
						for (int i = 0; i < this.listaProductosEstudios.size(); i++){
							producto = new ReProductoDataSource();
							String productoTemp = "";
							String loteTemp = "";
							String dosisTemp = "";
							String frecuenciaTemp = "";
							String viaTemp = "";
							String fechaTemp = "";
							String horaTemp = "";
							productoTemp = this.listaProductosEstudios.get(i).getMedicamento();
							loteTemp = this.listaProductosEstudios.get(i).getLote();
							dosisTemp = this.listaProductosEstudios.get(i).getDosisxdia();
							frecuenciaTemp = this.listaProductosEstudios.get(i).getFrecuencia();
							viaTemp = this.listaProductosEstudios.get(i).getReViasadmi().getDescripcion();
							fechaTemp = this.listaProductosEstudios.get(i).getFechaAdm();
							horaTemp = this.listaProductosEstudios.get(i).getHoraAdm();
							producto.setProducto(productoTemp);
							producto.setLote(loteTemp);
							producto.setDosis(dosisTemp);
							producto.setFrecuencia(frecuenciaTemp);
							producto.setVia(viaTemp);
							producto.setFecha(fechaTemp);
							producto.setHora(horaTemp);
							this.listaProductos.add(producto);
						}
						temp2.setListaProductos(this.listaProductos);
						temp2.setSi((tempReport.getTerminotratamiento() && tempReport.getTerminotratamiento()) ? "x" : "");
						temp2.setNo((tempReport.getTerminotratamiento() == null || !tempReport.getTerminotratamiento()) ? "x" : "");
						temp2.setIndicacion(tempReport.getIndicacion());
						for (int i = 0; i < this.listaMedicacionConm.size(); i++){
							vacunas = new ReVacunasDataSource();
							String productoVTemp = "";
							String presentacionVTemp = "";
							String loteVTemp = "";
							String dosisVTemp = "";
							String frecuenciaVTemp = "";
							String viaVTemp = "";
							String fechaVTemp = "";
							String horaVTemp = "";
							productoVTemp = this.listaMedicacionConm.get(i).getMedicamento();
							presentacionVTemp = this.listaMedicacionConm.get(i).getPresentacion();
							loteVTemp = this.listaMedicacionConm.get(i).getLote();
							dosisVTemp = this.listaMedicacionConm.get(i).getDosisxdia();
							frecuenciaVTemp = this.listaMedicacionConm.get(i).getFrecuencia();
							viaVTemp = this.listaMedicacionConm.get(i).getReViasadmi().getDescripcion();
							fechaVTemp = this.listaMedicacionConm.get(i).getFechaAdm();
							horaVTemp = this.listaMedicacionConm.get(i).getHoraAdm();
							vacunas.setProductoV(productoVTemp);
							vacunas.setPresentacionV(presentacionVTemp);
							vacunas.setLoteV(loteVTemp);
							vacunas.setDosisV(dosisVTemp);
							vacunas.setFrecuenciaV(frecuenciaVTemp);
							vacunas.setViaV(viaVTemp);
							vacunas.setFechaV(fechaVTemp);
							vacunas.setHoraV(horaVTemp);
							this.listaVacunas.add(vacunas);
						}
						temp2.setListaVacunas(this.listaVacunas);
						for (int i = 0; i < this.listaEnfermedadesInter.size(); i++){
							enfermedades = new ReEnfermedadesDataSource();
							enfermedades.setEnfermedad(this.listaEnfermedadesInter.get(i).getMedicamento());
							enfermedades.setSi(this.listaEnfermedadesInter.get(i).getPresentacion().equals(SeamResourceBundle.getBundle().getString("lbl_si")) ? "x" : "");
							enfermedades.setNo(!this.listaEnfermedadesInter.get(i).getPresentacion().equals(SeamResourceBundle.getBundle().getString("lbl_si")) ? "x" : "");
							this.listaEnfermedades.add(enfermedades);
						}
						pars3.put("firma", "____");
						temp2.setListaEnfermedades(this.listaEnfermedades);
						this.subReportPars.add(pars3);
						this.subReportNames.add("reporExpedito_subreport3");
						listTEmp2.add(temp2);
						this.listadoreporteExpeditoGeneral.add(listTEmp2);
						Map pars4 = new HashMap();
						pars4.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars4.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars4.put("lbl_EAGI", SeamResourceBundle.getBundle().getString("lbl_EAGI"));
						pars4.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars4.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars4.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars4.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars4.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars4.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars4.put("lbl_farmacos_otros", SeamResourceBundle.getBundle().getString("lbl_farmacos_otros"));
						pars4.put("lbl_dosis", SeamResourceBundle.getBundle().getString("lbl_dosis"));
						pars4.put("lbl_frecuencia", SeamResourceBundle.getBundle().getString("lbl_frecuencia"));
						pars4.put("lbl_via", SeamResourceBundle.getBundle().getString("lbl_via"));
						pars4.put("lbl_fecha_hora_A", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_A"));
						pars4.put("lbl_farmaco", SeamResourceBundle.getBundle().getString("lbl_farmaco"));
						pars4.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars4.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars4.put("lbl_tratamientoQuir", SeamResourceBundle.getBundle().getString("lbl_tratamientoQuir"));
						pars4.put("lbl_esp", SeamResourceBundle.getBundle().getString("lbl_esp"));
						pars4.put("protCod", codigoEst);
						pars4.put("iniPa", paciente);
						pars4.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars4.put("direccion", tempReport.getDireccion());
						pars4.put("telefono", usuarioCambiar.getTelefono());
						pars4.put("profesion", tempReport.getProfesion());
						pars4.put("fechaReporte1", tempReport.getFechareporteD());
						for (int i = 0; i < this.listaMedicacionRec.size(); i++){
							farmacos = new ReFarmacosDataSource();
							String farmacoFTemp = "";
							String dosisFTemp = "";
							String frecuenciaFTemp = "";
							String viaFTemp = "";
							String fechaFTemp = "";
							String horaFTemp = "";
							farmacoFTemp = this.listaMedicacionRec.get(i).getMedicamento();
							dosisFTemp = this.listaMedicacionRec.get(i).getDosisxdia();
							frecuenciaFTemp = this.listaMedicacionRec.get(i).getFrecuencia();
							viaFTemp = this.listaMedicacionRec.get(i).getReViasadmi().getDescripcion();
							fechaFTemp = this.listaMedicacionRec.get(i).getFechaAdm();
							horaFTemp = this.listaMedicacionRec.get(i).getHoraAdm();
							farmacos.setFarmaco(farmacoFTemp);
							farmacos.setDosis(dosisFTemp);
							farmacos.setFrecuencia(frecuenciaFTemp);
							farmacos.setVia(viaFTemp);
							farmacos.setFecha(fechaFTemp);
							farmacos.setHora(horaFTemp);
							this.listaFarmacos.add(farmacos);
						}
						temp3.setListaFarmacos(this.listaFarmacos);
						pars4.put("siF", (tempReport.getTratamientoquirurgico() != null && tempReport.getTratamientoquirurgico()) ? "x" : "");
						pars4.put("noF", (tempReport.getTratamientoquirurgico() == null || !tempReport.getTratamientoquirurgico()) ? "x" : "");
						pars4.put("especifique", tempReport.getEspecifiquetrat());
						pars4.put("firma", "____");
						this.subReportPars.add(pars4);
						this.subReportNames.add("reporExpedito_subreport4");
						listTEmp3.add(temp3);
						this.listadoreporteExpeditoGeneral.add(listTEmp3);
						Map pars5 = new HashMap();
						pars5.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars5.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars5.put("lbl_EAGI", SeamResourceBundle.getBundle().getString("lbl_EAGI"));
						pars5.put("lbl_fechaReportepromotor", SeamResourceBundle.getBundle().getString("lbl_fechaReportepromotor"));
						pars5.put("lbl_horaReportepromotor", SeamResourceBundle.getBundle().getString("lbl_horaReportepromotor"));
						pars5.put("lbl_inv_nom", SeamResourceBundle.getBundle().getString("lbl_inv_nom"));
						pars5.put("lbl_firmaInv", SeamResourceBundle.getBundle().getString("lbl_firmaInv"));
						pars5.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars5.put("lbl_descripcion", SeamResourceBundle.getBundle().getString("lbl_descripcion"));
						pars5.put("protCod", codigoEst);
						pars5.put("iniPa", paciente);
						pars5.put("fechaRP", tempReport.getFechareportepromotor());
						pars5.put("horaRP", tempReport.getHorareportepromotor());
						pars5.put("nombreInvF", tempReport.getNombreinvestigador());
						pars5.put("fechaF", tempReport.getFechareportee());
						pars5.put("descr", tempReport.getDescripciongeneral());
						pars5.put("firma", "_____________________");
						this.subReportPars.add(pars5);
						this.subReportNames.add("reporExpedito_subreport5");
						listTEmp4.add(temp4);
						this.listadoreporteExpeditoGeneral.add(listTEmp4);
						this.pathExportedReport = this.reportManager.ExportReportWithSubReports("reporExpedito", this.pars, new ArrayList<String>(), FileType.PDF_FILE, this.subReportNames, this.listadoreporteExpeditoGeneral, this.subReportPars);
					}
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void exportReportExpeditoEvento(Long repExpedito){
		this.pathExportedReport = "";
		this.pars = new HashMap();
		this.subReportNames = new ArrayList<String>();
		this.subReportPars = new ArrayList<Map>();
		this.listadoreporteExpeditoGeneral = new ArrayList<Collection>();
		this.listaProductosEstudios = new ArrayList<ReMedicacion_ensayo>();
		this.listaMedicacionConm = new ArrayList<ReMedicacion_ensayo>();
		this.listaEnfermedadesInter = new ArrayList<ReMedicacion_ensayo>();
		this.listaMedicacionRec = new ArrayList<ReMedicacion_ensayo>();
		this.listaProductos = new ArrayList<ReProductoDataSource>();
		this.listaVacunas = new ArrayList<ReVacunasDataSource>();
		this.listaEnfermedades = new ArrayList<ReEnfermedadesDataSource>();
		this.listaFarmacos = new ArrayList<ReFarmacosDataSource>();
		
					ReReporteexpedito_ensayo tempReport = (ReReporteexpedito_ensayo) this.entityManager.find(ReReporteexpedito_ensayo.class, repExpedito);
					if (tempReport != null){
						this.estudioE = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio();
						String nombreEst = "";
						String codigoEst = "";
						String paciente = "";
						String iniciales = "";
						nombreEst = this.estudioE.getNombre();
						codigoEst = this.estudioE.getIdentificador();
						
						String fase = "-";
						//EFaseEstudio_ensayo phase = ((this.mapWGD.get(tempKey).get(index) != null && this.mapWGD.get(tempKey).get(index).getReport() != null && this.mapWGD.get(tempKey).get(index).getReport().getFase() != null) ? this.mapWGD.get(tempKey).get(index).getReport().getFase() : null);
						List<EFaseEstudio_ensayo> lf = new ArrayList<EFaseEstudio_ensayo>();
						Long idE = tempReport.getIdEstudio();
						lf = this.entityManager.createQuery("select phase from EFaseEstudio_ensayo phase inner join phase.estudios e where (e.eliminado = null or e.eliminado = false) and e.id = :estudioId order by phase.id desc").setParameter("estudioId", idE).getResultList();
						if (lf != null && !lf.isEmpty())
							fase = lf.get(0).getNombre();
						this.hoja = this.entityManager.find(CrdEspecifico_ensayo.class, tempReport.getCrdEspecifico().getId());
						paciente = this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
						iniciales = this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getInicialesPaciente();
						Usuario_ensayo usuarioCambiar = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
						String[] consecuencia = new String[10];
						List<ReDataSource> listTEmp = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp1 = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp2 = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp3 = new ArrayList<ReDataSource>();
						List<ReDataSource> listTEmp4 = new ArrayList<ReDataSource>();
						ReDataSource temp = new ReDataSource();
						ReDataSource temp1 = new ReDataSource();
						ReDataSource temp2 = new ReDataSource();
						ReDataSource temp3 = new ReDataSource();
						ReDataSource temp4 = new ReDataSource();
						ReProductoDataSource producto = new ReProductoDataSource();
						ReVacunasDataSource vacunas = new ReVacunasDataSource();
						ReEnfermedadesDataSource enfermedades = new ReEnfermedadesDataSource();
						ReFarmacosDataSource farmacos = new ReFarmacosDataSource();
						List<ReHistoriarelevante_ensayo> listaHistoria = new ArrayList<ReHistoriarelevante_ensayo>();
						if (tempReport.getId() != 0){
							this.listaProductosEstudios = this.entityManager.createQuery("select medicacion.reMedicacion from ReInfoproducto_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							this.listaMedicacionConm = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionconcomitante_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							this.listaEnfermedadesInter = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReEnfermedadesinter_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							this.listaMedicacionRec = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionreciente_ensayo medicacion where medicacion.reReporteexpedito.id = :reporteId and (medicacion.reMedicacion.eliminado = null or medicacion.reMedicacion.eliminado = false)").setParameter("reporteId", tempReport.getId()).getResultList();
							listaHistoria = (List<ReHistoriarelevante_ensayo>) this.entityManager.createQuery("select historia from ReHistoriarelevante_ensayo historia where historia.reReporteexpedito.id = :reporteId").setParameter("reporteId", tempReport.getId()).getResultList();
						} else {
							List<ReInfoproducto_ensayo> tempL = new ArrayList<ReInfoproducto_ensayo>(tempReport.getReInfoproducto());
							for (int i = 0; i < tempL.size(); i++)
								this.listaProductosEstudios.add(tempL.get(i).getReMedicacion());
							List<ReMedicacionconcomitante_ensayo> tempL1 = new ArrayList<ReMedicacionconcomitante_ensayo>(tempReport.getReMedicacionconcomitantes());
							for (int i = 0; i < tempL1.size(); i++)
								this.listaMedicacionConm.add(tempL1.get(i).getReMedicacion());
							List<ReEnfermedadesinter_ensayo> tempL2 = new ArrayList<ReEnfermedadesinter_ensayo>(tempReport.getReEnfermedadesinter());
							for (int i = 0; i < tempL2.size(); i++)
								this.listaEnfermedadesInter.add(tempL2.get(i).getReMedicacion());
							List<ReMedicacionreciente_ensayo> tempL3 = new ArrayList<ReMedicacionreciente_ensayo>(tempReport.getReMedicacionrecientes());
							for (int i = 0; i < tempL3.size(); i++)
								this.listaMedicacionRec.add(tempL3.get(i).getReMedicacion());
							listaHistoria = new ArrayList<ReHistoriarelevante_ensayo>(tempReport.getReHistoriarelevantes());
						}
						for (int i = 0; i < listaHistoria.size(); i++){
							ReConsecuenciaspaciente_ensayo enfermedad = this.entityManager.find(ReConsecuenciaspaciente_ensayo.class, listaHistoria.get(i).getIdReEnfermedades());
							consecuencia[i] = enfermedad.getDescripcion();
						}
						Map pars1 = new HashMap();
						pars1.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars1.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars1.put("lbl_fechaReporteA", SeamResourceBundle.getBundle().getString("lbl_fechaReporteA"));
						pars1.put("lbl_informacion_general", SeamResourceBundle.getBundle().getString("lbl_informacion_general1"));
						pars1.put("lbl_informeIni", SeamResourceBundle.getBundle().getString("lbl_informeIni"));
						pars1.put("lbl_informeAdd", SeamResourceBundle.getBundle().getString("lbl_informeAdd"));
						pars1.put("lbl_protocolo_titulo", SeamResourceBundle.getBundle().getString("lbl_protocolo_titulo1"));
						pars1.put("lbl_fase", SeamResourceBundle.getBundle().getString("lbl_fase"));
						pars1.put("lbl_revisado_dir", SeamResourceBundle.getBundle().getString("lbl_revisado_dir"));
						pars1.put("lbl_institucion", SeamResourceBundle.getBundle().getString("lbl_institucion"));
						pars1.put("lbl_prod_investigacion", SeamResourceBundle.getBundle().getString("lbl_prod_investigacion"));
						pars1.put("lbl_dosis_Admin", SeamResourceBundle.getBundle().getString("lbl_dosis_Admin"));
						pars1.put("lbl_presentacion", SeamResourceBundle.getBundle().getString("lbl_presentacion"));
						pars1.put("lbl_via_Admin", SeamResourceBundle.getBundle().getString("lbl_via_Admin"));
						pars1.put("lbl_investigador", SeamResourceBundle.getBundle().getString("lbl_investigador"));
						pars1.put("lbl_consecuencias_paciente_single", SeamResourceBundle.getBundle().getString("lbl_consecuencias_paciente_single"));
						pars1.put("lbl_muerte", SeamResourceBundle.getBundle().getString("lbl_muerte"));
						pars1.put("lbl_autopsia", SeamResourceBundle.getBundle().getString("lbl_autopsia"));
						pars1.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars1.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars1.put("lbl_peligro_vida", SeamResourceBundle.getBundle().getString("lbl_peligro_vida"));
						pars1.put("lbl_prod_discapacidad", SeamResourceBundle.getBundle().getString("lbl_prod_discapacidad"));
						pars1.put("lbl_requiere_hos", SeamResourceBundle.getBundle().getString("lbl_requiere_hos"));
						pars1.put("lbl_fecha_ingreso", SeamResourceBundle.getBundle().getString("lbl_fecha_ingreso"));
						pars1.put("lbl_fecha_alta", SeamResourceBundle.getBundle().getString("lbl_fecha_alta"));
						pars1.put("lbl_prolonga_estancia", SeamResourceBundle.getBundle().getString("lbl_prolonga_estancia"));
						pars1.put("lbl_anomalia", SeamResourceBundle.getBundle().getString("lbl_anomalia"));
						pars1.put("lbl_acont_medico", SeamResourceBundle.getBundle().getString("lbl_acont_medico"));
						pars1.put("lbl_especifique", SeamResourceBundle.getBundle().getString("lbl_especifique"));
						pars1.put("lbl_otros", SeamResourceBundle.getBundle().getString("lbl_otros"));
						pars1.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars1.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars1.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars1.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars1.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars1.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars1.put("protCod", codigoEst);
						pars1.put("iniPa", paciente);
						pars1.put("fechaReporteA", tempReport.getFechaCECMEDA());
						if (tempReport.getInformeinicial() != null){
							if (tempReport.getInformeinicial()){
								String a = "x";
								String b = "";
								pars1.put("informeIni", a);
								pars1.put("informeAdd", b);
							} else {
								String a = "";
								String b = "x";
								pars1.put("informeIni", a);
								pars1.put("informeAdd", b);
							}
						} else {
							String a = "";
							String b = "";
							pars1.put("informeIni", a);
							pars1.put("informeAdd", b);
						}
						pars1.put("protocolo_titulo", nombreEst);
						pars1.put("fase", fase);
						pars1.put("revisado_dir", tempReport.getPromotor());
						pars1.put("institucion", nombreEst);
						pars1.put("prod_investigacion", tempReport.getProductoinvestigado());
						pars1.put("dosis_Admin", tempReport.getDosisadministrada());
						pars1.put("presentacion", tempReport.getPresentacion());
						pars1.put("via_Admin", tempReport.getReViasadmi().getDescripcion());
						pars1.put("investigador", tempReport.getInvestigador());
						for (int i = 0; i < consecuencia.length; i++){
							if (consecuencia[i] != null && !consecuencia[i].isEmpty()){
								if (consecuencia[i].trim() == SeamResourceBundle.getBundle().getString("lbl_muerte").trim()){
									pars1.put("muerte", "x");
									pars1.put((tempReport.getAutopsia() ? "autopsiaS" : "autopsiaN"), "x");
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_peligro_vida").trim()))
									pars1.put("peligro_vida", "x");
								else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_prod_discapacidad").trim()))
									pars1.put("prod_discapacidad", "x");
								else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_requiere_hos").trim())){
									pars1.put("requiere_hos", "x");
									pars1.put("fecha_ingreso", tempReport.getFechaingreso());
									pars1.put("fecha_alta", tempReport.getFechaalta());
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_prolonga_estancia").trim())){
									pars1.put("prolonga_estancia", "x");
									pars1.put("fecha_alta2", tempReport.getFechaaltaprol());
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_anomalia").trim()))
									pars1.put("anomalia", "x");
								else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_acont_medico").trim())){
									pars1.put("acont_medico", "x");
									pars1.put("especifiqueAcont", tempReport.getEspecifiqueacont());
								} else if (consecuencia[i].trim().equals(SeamResourceBundle.getBundle().getString("lbl_otros").trim())){
									pars1.put("otros", "x");
									pars1.put("especifiqueOtros", tempReport.getEspecifiqueotros());
								}
							}
						}
						pars1.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars1.put("direccion", tempReport.getDireccion());
						pars1.put("telefono", usuarioCambiar.getTelefono());
						pars1.put("profesion", tempReport.getProfesion());
						pars1.put("fechaReporte1", tempReport.getFechareporteA());
						pars1.put("firma", "____");
						this.subReportPars.add(pars1);
						this.subReportNames.add("reporExpedito_subreport1");
						listTEmp.add(temp);
						this.listadoreporteExpeditoGeneral.add(listTEmp);
						Map pars2 = new HashMap();
						pars2.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars2.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars2.put("lbl_fechaReporteA", SeamResourceBundle.getBundle().getString("lbl_fechaReporteA"));
						pars2.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars2.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars2.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars2.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars2.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars2.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars2.put("lbl_informacion_datos_demograficos", SeamResourceBundle.getBundle().getString("lbl_informacion_datos_demograficos"));
						pars2.put("lbl_iniciales", SeamResourceBundle.getBundle().getString("lbl_iniciales"));
						pars2.put("lbl_fecha_nacimiento", SeamResourceBundle.getBundle().getString("lbl_fecha_nacimiento"));
						pars2.put("lbl_sexo", SeamResourceBundle.getBundle().getString("lbl_sexo"));
						pars2.put("lbl_masculino", SeamResourceBundle.getBundle().getString("lbl_masculino"));
						pars2.put("lbl_femenino", SeamResourceBundle.getBundle().getString("lbl_femenino"));
						pars2.put("lbl_peso", SeamResourceBundle.getBundle().getString("lbl_peso"));
						pars2.put("lbl_talla", SeamResourceBundle.getBundle().getString("lbl_talla"));
						pars2.put("lbl_raza", SeamResourceBundle.getBundle().getString("lbl_raza"));
						pars2.put("lbl_bl", SeamResourceBundle.getBundle().getString("lbl_bl"));
						pars2.put("lbl_ne", SeamResourceBundle.getBundle().getString("lbl_ne"));
						pars2.put("lbl_me", SeamResourceBundle.getBundle().getString("lbl_me"));
						pars2.put("lbl_otra", SeamResourceBundle.getBundle().getString("lbl_otra"));
						pars2.put("lbl_accionEmprendida", SeamResourceBundle.getBundle().getString("lbl_accionEmprendida"));
						pars2.put("lbl_ninguna", SeamResourceBundle.getBundle().getString("lbl_ninguna"));
						pars2.put("lbl_mod_dosis", SeamResourceBundle.getBundle().getString("lbl_mod_dosis"));
						pars2.put("lbl_pospone", SeamResourceBundle.getBundle().getString("lbl_pospone"));
						pars2.put("lbl_interrumpe", SeamResourceBundle.getBundle().getString("lbl_interrumpe"));
						pars2.put("lbl_acontecimientos", SeamResourceBundle.getBundle().getString("lbl_acontecimientos"));
						pars2.put("lbl_reaparicion", SeamResourceBundle.getBundle().getString("lbl_reaparicion"));
						pars2.put("lbl_no_aparece", SeamResourceBundle.getBundle().getString("lbl_no_aparece"));
						pars2.put("lbl_desconocimiento", SeamResourceBundle.getBundle().getString("lbl_desconocimiento"));
						pars2.put("lbl_no_procede", SeamResourceBundle.getBundle().getString("lbl_no_procede"));
						pars2.put("lbl_evento_adverso", SeamResourceBundle.getBundle().getString("lbl_evento_adverso"));
						pars2.put("lbl_relacioCausalidad", SeamResourceBundle.getBundle().getString("lbl_relacioCausalidad"));
						pars2.put("lbl_fuente", SeamResourceBundle.getBundle().getString("lbl_fuente"));
						pars2.put("lbl_oms", SeamResourceBundle.getBundle().getString("lbl_oms"));
						pars2.put("lbl_fda", SeamResourceBundle.getBundle().getString("lbl_fda"));
						pars2.put("lbl_otras", SeamResourceBundle.getBundle().getString("lbl_otras"));
						pars2.put("lbl_fecha_hora_ini", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_ini"));
						pars2.put("lbl_fecha_hora_fina", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_fina"));
						pars2.put("lbl_intensidad", SeamResourceBundle.getBundle().getString("lbl_intensidad"));
						pars2.put("lbl_lugarOcurrio", SeamResourceBundle.getBundle().getString("lbl_lugarOcurrio"));
						pars2.put("lbl_hogar", SeamResourceBundle.getBundle().getString("lbl_hogar"));
						pars2.put("lbl_hospital", SeamResourceBundle.getBundle().getString("lbl_hospital"));
						pars2.put("lbl_piliclinico", SeamResourceBundle.getBundle().getString("lbl_piliclinico"));
						pars2.put("lbl_vacunatorio", SeamResourceBundle.getBundle().getString("lbl_vacunatorio"));
						pars2.put("lbl_cinfantil", SeamResourceBundle.getBundle().getString("lbl_cinfantil"));
						pars2.put("lbl_escuela", SeamResourceBundle.getBundle().getString("lbl_escuela"));
						pars2.put("lbl_asilo", SeamResourceBundle.getBundle().getString("lbl_asilo"));
						pars2.put("lbl_otros", SeamResourceBundle.getBundle().getString("lbl_otrosL"));
						pars2.put("lbl_especifique", SeamResourceBundle.getBundle().getString("lbl_especifique"));
						pars2.put("lbl_desenlace", SeamResourceBundle.getBundle().getString("lbl_desenlace"));
						pars2.put("lbl_recu", SeamResourceBundle.getBundle().getString("lbl_recu"));
						pars2.put("lbl_recu_secuelas", SeamResourceBundle.getBundle().getString("lbl_recu_secuelas"));
						pars2.put("lbl_mejor", SeamResourceBundle.getBundle().getString("lbl_mejor"));
						pars2.put("lbl_persist", SeamResourceBundle.getBundle().getString("lbl_persist"));
						pars2.put("lbl_muerteD", SeamResourceBundle.getBundle().getString("lbl_muerteD"));
						pars2.put("lbl_abandonado", SeamResourceBundle.getBundle().getString("lbl_abandonado"));
						pars2.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars2.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars2.put("protCod", codigoEst);
						pars2.put("iniPa", paciente);
						pars2.put("fechaReporteA", tempReport.getFechaCECMEDB());
						pars2.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars2.put("direccion", tempReport.getDireccion());
						pars2.put("telefono", usuarioCambiar.getTelefono());
						pars2.put("profesion", tempReport.getProfesion());
						pars2.put("fechaReporte1", tempReport.getFechareporteB());
						pars2.put("iniciales", iniciales);
						pars2.put("fecha_nacimiento", tempReport.getFechanacimiento());
						pars2.put("masculino", (tempReport.getSexo().equals(SeamResourceBundle.getBundle().getString("lbl_masculino")) ? "x" : ""));
						pars2.put("femenino", (!tempReport.getSexo().equals(SeamResourceBundle.getBundle().getString("lbl_masculino")) ? "x" : ""));
						pars2.put("peso", tempReport.getPeso().toString());
						pars2.put("talla", tempReport.getTalla().toString());
						if (tempReport.getRaza().equals(SeamResourceBundle.getBundle().getString("lbl_bl"))){
							pars2.put("bl", "x");
							pars2.put("ne", "");
							pars2.put("me", "");
							pars2.put("otra", "");
							pars2.put("espe", "");
						} else if (tempReport.getRaza().equals(SeamResourceBundle.getBundle().getString("lbl_ne"))){
							pars2.put("bl", "");
							pars2.put("ne", "x");
							pars2.put("me", "");
							pars2.put("otra", "");
							pars2.put("espe", "");
						} else if (tempReport.getRaza().equals(SeamResourceBundle.getBundle().getString("lbl_me"))){
							pars2.put("bl", "");
							pars2.put("ne", "");
							pars2.put("me", "x");
							pars2.put("otra", "");
							pars2.put("espe", "");
						} else {
							pars2.put("bl", "");
							pars2.put("ne", "");
							pars2.put("me", "");
							pars2.put("otra", "x");
							pars2.put("espe", tempReport.getOtraraza());
						}
						if (tempReport.getReCrealizadaeas().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_ninguna"))){
							pars2.put("ninguna", "x");
							pars2.put("pospone", "");
							pars2.put("interrumpe", "");
							pars2.put("mod_dosis", "");
						} else if (tempReport.getReCrealizadaeas().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_pospone"))){
							pars2.put("ninguna", "");
							pars2.put("pospone", "x");
							pars2.put("interrumpe", "");
							pars2.put("mod_dosis", "");
						} else if (tempReport.getReCrealizadaeas().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_interrumpe"))){
							pars2.put("ninguna", "");
							pars2.put("pospone", "");
							pars2.put("interrumpe", "x");
							pars2.put("mod_dosis", "");
						} else {
							pars2.put("ninguna", "");
							pars2.put("pospone", "");
							pars2.put("interrumpe", "");
							pars2.put("mod_dosis", "x");
						}
						if (tempReport.getAcontecimiento() != null){
							if (tempReport.getAcontecimiento().equals(SeamResourceBundle.getBundle().getString("lbl_reaparicion"))){
								pars2.put("reaparicion", "x");
								pars2.put("no_aparece", "");
								pars2.put("desconocimiento", "");
								pars2.put("no_procede", "");
							} else if (tempReport.getAcontecimiento().equals(SeamResourceBundle.getBundle().getString("lbl_no_aparece"))){
								pars2.put("reaparicion", "");
								pars2.put("no_aparece", "x");
								pars2.put("desconocimiento", "");
								pars2.put("no_procede", "");
							} else if (tempReport.getAcontecimiento().equals(SeamResourceBundle.getBundle().getString("lbl_desconocimiento"))){
								pars2.put("reaparicion", "");
								pars2.put("no_aparece", "");
								pars2.put("desconocimiento", "x");
								pars2.put("no_procede", "");
							} else {
								pars2.put("reaparicion", "");
								pars2.put("no_aparece", "");
								pars2.put("desconocimiento", "");
								pars2.put("no_procede", "x");
							}
						} else {
							pars2.put("reaparicion", "");
							pars2.put("no_aparece", "");
							pars2.put("desconocimiento", "");
							pars2.put("no_procede", "");
						}
						pars2.put("evento_adverso", tempReport.getEventoadverso());
						pars2.put("relacioCausalidad", tempReport.getCausalidad());
						if (tempReport.getFuente().equals(SeamResourceBundle.getBundle().getString("lbl_oms"))){
							pars2.put("oms", "x");
							pars2.put("fda", "");
							pars2.put("otras", "");
						} else if (tempReport.getFuente().equals(SeamResourceBundle.getBundle().getString("lbl_fda"))){
							pars2.put("oms", "");
							pars2.put("fda", "x");
							pars2.put("otras", "");
						} else {
							pars2.put("oms", "");
							pars2.put("fda", "");
							pars2.put("otras", "x");
						}
						pars2.put("otrasespe", tempReport.getOtrafuente());
						pars2.put("fechai", tempReport.getFechainicio());
						pars2.put("horai", tempReport.getHorainicio());
						pars2.put("fechaf", tempReport.getFechafinalizacion());
						pars2.put("horaf", tempReport.getHorafinalizacion());
						if (tempReport.getReIntensidad().getCodigo().toString().equals("1")){
							pars2.put("1", "x");
							pars2.put("2", "");
							pars2.put("3", "");
							pars2.put("4", "");
							pars2.put("5", "");
						} else if (tempReport.getReIntensidad().getCodigo().toString().equals("2")){
							pars2.put("1", "");
							pars2.put("2", "x");
							pars2.put("3", "");
							pars2.put("4", "");
							pars2.put("5", "");
						} else if (tempReport.getReIntensidad().getCodigo().toString().equals("3")){
							pars2.put("1", "");
							pars2.put("2", "");
							pars2.put("3", "x");
							pars2.put("4", "");
							pars2.put("5", "");
						} else if (tempReport.getReIntensidad().getCodigo().toString().equals("4")){
							pars2.put("1", "");
							pars2.put("2", "");
							pars2.put("3", "");
							pars2.put("4", "x");
							pars2.put("5", "");
						} else {
							pars2.put("1", "");
							pars2.put("2", "");
							pars2.put("3", "");
							pars2.put("4", "");
							pars2.put("5", "x");
						}
						if (tempReport.getDondeocurrio().equals("1")){
							pars2.put("hogar", "x");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("2")){
							pars2.put("hogar", "");
							pars2.put("hospital", "x");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("3")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "x");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("4")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "x");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("5")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "x");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("6")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "x");
							pars2.put("asilo", "");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else if (tempReport.getDondeocurrio().equals("7")){
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "x");
							pars2.put("otrosL", "");
							pars2.put("especifiqueL", "");
						} else {
							pars2.put("hogar", "");
							pars2.put("hospital", "");
							pars2.put("piliclinico", "");
							pars2.put("vacunatorio", "");
							pars2.put("cinfantil", "");
							pars2.put("escuela", "");
							pars2.put("asilo", "");
							pars2.put("otrosL", "x");
							pars2.put("especifiqueL", tempReport.getOtrolugar());
						}
						if (tempReport.getReResultadofinal() != null){
							if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_recu"))){
								pars2.put("recu", "x");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "");
								pars2.put("persist", "");
								pars2.put("muerteD", "");
							} else if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_recu_secuelas"))){
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "x");
								pars2.put("mejor", "");
								pars2.put("persist", "");
								pars2.put("muerteD", "");
							} else if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_mejor"))){
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "x");
								pars2.put("persist", "");
								pars2.put("muerteD", "");
							} else if (tempReport.getReResultadofinal().getDescripcion().equals(SeamResourceBundle.getBundle().getString("lbl_persist"))){
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "");
								pars2.put("persist", "x");
								pars2.put("muerteD", "");
							} else {
								pars2.put("recu", "");
								pars2.put("recu_secuelas", "");
								pars2.put("mejor", "");
								pars2.put("persist", "");
								pars2.put("muerteD", "x");
							}
						} else {
							pars2.put("recu", "");
							pars2.put("recu_secuelas", "");
							pars2.put("mejor", "");
							pars2.put("persist", "");
							pars2.put("muerteD", "");
						}
						pars2.put("siA", ((tempReport.getAbndonoelsujeto() != null && tempReport.getAbndonoelsujeto()) ? "x" : ""));
						pars2.put("noA", ((tempReport.getAbndonoelsujeto() == null || !tempReport.getAbndonoelsujeto()) ? "x" : ""));
						pars2.put("firma", "____");
						this.subReportPars.add(pars2);
						this.subReportNames.add("reporExpedito_subreport2");
						listTEmp1.add(temp1);
						this.listadoreporteExpeditoGeneral.add(listTEmp1);
						Map pars3 = new HashMap();
						pars3.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars3.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars3.put("lbl_EAGI", SeamResourceBundle.getBundle().getString("lbl_EAGI"));
						pars3.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars3.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars3.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars3.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars3.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars3.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars3.put("lbl_productos_estudio", SeamResourceBundle.getBundle().getString("lbl_productos_estudio"));
						pars3.put("lbl_producto", SeamResourceBundle.getBundle().getString("lbl_producto"));
						pars3.put("lbl_lote", SeamResourceBundle.getBundle().getString("lbl_lote"));
						pars3.put("lbl_dosis", SeamResourceBundle.getBundle().getString("lbl_dosis"));
						pars3.put("lbl_frecuencia", SeamResourceBundle.getBundle().getString("lbl_frecuencia"));
						pars3.put("lbl_via", SeamResourceBundle.getBundle().getString("lbl_via"));
						pars3.put("lbl_fecha_hora_A", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_A"));
						pars3.put("lbl_terminoTrat", SeamResourceBundle.getBundle().getString("lbl_terminoTrat"));
						pars3.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars3.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars3.put("lbl_indicacion", SeamResourceBundle.getBundle().getString("lbl_indicacion"));
						pars3.put("lbl_vacunas_concamitante", SeamResourceBundle.getBundle().getString("lbl_vacunas_concamitante"));
						pars3.put("lbl_presentacion", SeamResourceBundle.getBundle().getString("lbl_presentacionV"));
						pars3.put("lbl_enfermedades_intecurrentes", SeamResourceBundle.getBundle().getString("lbl_enfermedades_intecurrentes"));
						pars3.put("lbl_persiste", SeamResourceBundle.getBundle().getString("lbl_persiste"));
						pars3.put("protCod", codigoEst);
						pars3.put("iniPa", paciente);
						pars3.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars3.put("direccion", tempReport.getDireccion());
						pars3.put("telefono", usuarioCambiar.getTelefono());
						pars3.put("profesion", tempReport.getProfesion());
						pars3.put("fechaReporte1", tempReport.getFechareporteC());
						for (int i = 0; i < this.listaProductosEstudios.size(); i++){
							producto = new ReProductoDataSource();
							String productoTemp = "";
							String loteTemp = "";
							String dosisTemp = "";
							String frecuenciaTemp = "";
							String viaTemp = "";
							String fechaTemp = "";
							String horaTemp = "";
							productoTemp = this.listaProductosEstudios.get(i).getMedicamento();
							loteTemp = this.listaProductosEstudios.get(i).getLote();
							dosisTemp = this.listaProductosEstudios.get(i).getDosisxdia();
							frecuenciaTemp = this.listaProductosEstudios.get(i).getFrecuencia();
							viaTemp = this.listaProductosEstudios.get(i).getReViasadmi().getDescripcion();
							fechaTemp = this.listaProductosEstudios.get(i).getFechaAdm();
							horaTemp = this.listaProductosEstudios.get(i).getHoraAdm();
							producto.setProducto(productoTemp);
							producto.setLote(loteTemp);
							producto.setDosis(dosisTemp);
							producto.setFrecuencia(frecuenciaTemp);
							producto.setVia(viaTemp);
							producto.setFecha(fechaTemp);
							producto.setHora(horaTemp);
							this.listaProductos.add(producto);
						}
						temp2.setListaProductos(this.listaProductos);
						temp2.setSi((tempReport.getTerminotratamiento() && tempReport.getTerminotratamiento()) ? "x" : "");
						temp2.setNo((tempReport.getTerminotratamiento() == null || !tempReport.getTerminotratamiento()) ? "x" : "");
						temp2.setIndicacion(tempReport.getIndicacion());
						for (int i = 0; i < this.listaMedicacionConm.size(); i++){
							vacunas = new ReVacunasDataSource();
							String productoVTemp = "";
							String presentacionVTemp = "";
							String loteVTemp = "";
							String dosisVTemp = "";
							String frecuenciaVTemp = "";
							String viaVTemp = "";
							String fechaVTemp = "";
							String horaVTemp = "";
							productoVTemp = this.listaMedicacionConm.get(i).getMedicamento();
							presentacionVTemp = this.listaMedicacionConm.get(i).getPresentacion();
							loteVTemp = this.listaMedicacionConm.get(i).getLote();
							dosisVTemp = this.listaMedicacionConm.get(i).getDosisxdia();
							frecuenciaVTemp = this.listaMedicacionConm.get(i).getFrecuencia();
							viaVTemp = this.listaMedicacionConm.get(i).getReViasadmi().getDescripcion();
							fechaVTemp = this.listaMedicacionConm.get(i).getFechaAdm();
							horaVTemp = this.listaMedicacionConm.get(i).getHoraAdm();
							vacunas.setProductoV(productoVTemp);
							vacunas.setPresentacionV(presentacionVTemp);
							vacunas.setLoteV(loteVTemp);
							vacunas.setDosisV(dosisVTemp);
							vacunas.setFrecuenciaV(frecuenciaVTemp);
							vacunas.setViaV(viaVTemp);
							vacunas.setFechaV(fechaVTemp);
							vacunas.setHoraV(horaVTemp);
							this.listaVacunas.add(vacunas);
						}
						temp2.setListaVacunas(this.listaVacunas);
						for (int i = 0; i < this.listaEnfermedadesInter.size(); i++){
							enfermedades = new ReEnfermedadesDataSource();
							enfermedades.setEnfermedad(this.listaEnfermedadesInter.get(i).getMedicamento());
							enfermedades.setSi(this.listaEnfermedadesInter.get(i).getPresentacion().equals(SeamResourceBundle.getBundle().getString("lbl_si")) ? "x" : "");
							enfermedades.setNo(!this.listaEnfermedadesInter.get(i).getPresentacion().equals(SeamResourceBundle.getBundle().getString("lbl_si")) ? "x" : "");
							this.listaEnfermedades.add(enfermedades);
						}
						pars3.put("firma", "____");
						temp2.setListaEnfermedades(this.listaEnfermedades);
						this.subReportPars.add(pars3);
						this.subReportNames.add("reporExpedito_subreport3");
						listTEmp2.add(temp2);
						this.listadoreporteExpeditoGeneral.add(listTEmp2);
						Map pars4 = new HashMap();
						pars4.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars4.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars4.put("lbl_EAGI", SeamResourceBundle.getBundle().getString("lbl_EAGI"));
						pars4.put("lbl_nombre_apellidosN", SeamResourceBundle.getBundle().getString("lbl_nombre_apellidosN"));
						pars4.put("lbl_direccion", SeamResourceBundle.getBundle().getString("lbl_direccion"));
						pars4.put("lbl_direccion_telefono", SeamResourceBundle.getBundle().getString("lbl_direccion_telefono"));
						pars4.put("lbl_profesion", SeamResourceBundle.getBundle().getString("lbl_profesion"));
						pars4.put("lbl_firma", SeamResourceBundle.getBundle().getString("lbl_firma"));
						pars4.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars4.put("lbl_farmacos_otros", SeamResourceBundle.getBundle().getString("lbl_farmacos_otros"));
						pars4.put("lbl_dosis", SeamResourceBundle.getBundle().getString("lbl_dosis"));
						pars4.put("lbl_frecuencia", SeamResourceBundle.getBundle().getString("lbl_frecuencia"));
						pars4.put("lbl_via", SeamResourceBundle.getBundle().getString("lbl_via"));
						pars4.put("lbl_fecha_hora_A", SeamResourceBundle.getBundle().getString("lbl_fecha_hora_A"));
						pars4.put("lbl_farmaco", SeamResourceBundle.getBundle().getString("lbl_farmaco"));
						pars4.put("lbl_no", SeamResourceBundle.getBundle().getString("lbl_no"));
						pars4.put("lbl_si", SeamResourceBundle.getBundle().getString("lbl_si"));
						pars4.put("lbl_tratamientoQuir", SeamResourceBundle.getBundle().getString("lbl_tratamientoQuir"));
						pars4.put("lbl_esp", SeamResourceBundle.getBundle().getString("lbl_esp"));
						pars4.put("protCod", codigoEst);
						pars4.put("iniPa", paciente);
						pars4.put("nombre_apellidosN", tempReport.getNombrereporta());
						pars4.put("direccion", tempReport.getDireccion());
						pars4.put("telefono", usuarioCambiar.getTelefono());
						pars4.put("profesion", tempReport.getProfesion());
						pars4.put("fechaReporte1", tempReport.getFechareporteD());
						for (int i = 0; i < this.listaMedicacionRec.size(); i++){
							farmacos = new ReFarmacosDataSource();
							String farmacoFTemp = "";
							String dosisFTemp = "";
							String frecuenciaFTemp = "";
							String viaFTemp = "";
							String fechaFTemp = "";
							String horaFTemp = "";
							farmacoFTemp = this.listaMedicacionRec.get(i).getMedicamento();
							dosisFTemp = this.listaMedicacionRec.get(i).getDosisxdia();
							frecuenciaFTemp = this.listaMedicacionRec.get(i).getFrecuencia();
							viaFTemp = this.listaMedicacionRec.get(i).getReViasadmi().getDescripcion();
							fechaFTemp = this.listaMedicacionRec.get(i).getFechaAdm();
							horaFTemp = this.listaMedicacionRec.get(i).getHoraAdm();
							farmacos.setFarmaco(farmacoFTemp);
							farmacos.setDosis(dosisFTemp);
							farmacos.setFrecuencia(frecuenciaFTemp);
							farmacos.setVia(viaFTemp);
							farmacos.setFecha(fechaFTemp);
							farmacos.setHora(horaFTemp);
							this.listaFarmacos.add(farmacos);
						}
						temp3.setListaFarmacos(this.listaFarmacos);
						pars4.put("siF", (tempReport.getTratamientoquirurgico() != null && tempReport.getTratamientoquirurgico()) ? "x" : "");
						pars4.put("noF", (tempReport.getTratamientoquirurgico() == null || !tempReport.getTratamientoquirurgico()) ? "x" : "");
						pars4.put("especifique", tempReport.getEspecifiquetrat());
						pars4.put("firma", "____");
						this.subReportPars.add(pars4);
						this.subReportNames.add("reporExpedito_subreport4");
						listTEmp3.add(temp3);
						this.listadoreporteExpeditoGeneral.add(listTEmp3);
						Map pars5 = new HashMap();
						pars5.put("lbl_protocolo_codigo", SeamResourceBundle.getBundle().getString("lbl_protocolo_codigo"));
						pars5.put("lbl_iniciales_paciente", SeamResourceBundle.getBundle().getString("lbl_iniciales_paciente"));
						pars5.put("lbl_EAGI", SeamResourceBundle.getBundle().getString("lbl_EAGI"));
						pars5.put("lbl_fechaReportepromotor", SeamResourceBundle.getBundle().getString("lbl_fechaReportepromotor"));
						pars5.put("lbl_horaReportepromotor", SeamResourceBundle.getBundle().getString("lbl_horaReportepromotor"));
						pars5.put("lbl_inv_nom", SeamResourceBundle.getBundle().getString("lbl_inv_nom"));
						pars5.put("lbl_firmaInv", SeamResourceBundle.getBundle().getString("lbl_firmaInv"));
						pars5.put("lbl_fechaReporte1", SeamResourceBundle.getBundle().getString("lbl_fechaReporte1"));
						pars5.put("lbl_descripcion", SeamResourceBundle.getBundle().getString("lbl_descripcion"));
						pars5.put("protCod", codigoEst);
						pars5.put("iniPa", paciente);
						pars5.put("fechaRP", tempReport.getFechareportepromotor());
						pars5.put("horaRP", tempReport.getHorareportepromotor());
						pars5.put("nombreInvF", tempReport.getNombreinvestigador());
						pars5.put("fechaF", tempReport.getFechareportee());
						pars5.put("descr", tempReport.getDescripciongeneral());
						pars5.put("firma", "_____________________");
						this.subReportPars.add(pars5);
						this.subReportNames.add("reporExpedito_subreport5");
						listTEmp4.add(temp4);
						this.listadoreporteExpeditoGeneral.add(listTEmp4);
						this.pathExportedReport = this.reportManager.ExportReportWithSubReports("reporExpedito", this.pars, new ArrayList<String>(), FileType.PDF_FILE, this.subReportNames, this.listadoreporteExpeditoGeneral, this.subReportPars);
					}
				
	}

	@SuppressWarnings("unchecked")
	private void loadSections(){
		if(this.secciones == null || this.secciones.isEmpty())
			this.secciones = (List<Seccion_ensayo>) this.entityManager.createQuery("select seccion from Seccion_ensayo seccion where (seccion.eliminado = null or seccion.eliminado = false) and seccion.hojaCrd.id = :hojaId").setParameter("hojaId", this.hoja.getHojaCrd().getId()).getResultList();
	}

	public String validarCampo(){
		if (this.causaDescMonitoreo != null){
			this.causaGuardar = this.causaDescMonitoreo.toString();
			int longitud = this.causaDescMonitoreo.toString().length();
			boolean noExtranno = this.causaDescMonitoreo.toString().matches("^(\\s*[A-Za-z" + CARACTERES_ESPECIALES + "\u00BF?.,0-9]+\\s*)++$");
			if (this.causaDescMonitoreo != null && noExtranno && longitud <= 250){
				return "Richfaces.showModalPanel('mpAdvertenciaDescompletarMon')";
			}
		}
		return "";
	}

	public void mandarDatos(Long sectionId, Long groupId, Integer repetition){
		if (sectionId != null && sectionId.toString() != null && !sectionId.toString().isEmpty() && groupId != null && groupId.toString() != null && !groupId.toString().isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().isEmpty() && this.mapWGD != null && !this.mapWGD.isEmpty()){
			String key = this.convertKey(sectionId, groupId);
			if (this.mapWGD.containsKey(key)){
				int index = this.indexOfGroupData(this.mapWGD.get(key), repetition);
				if (index != -1){
					Variable_ensayo tempvariable = this.mapWGD.get(key).get(index).findVariable(sectionId, groupId, repetition, "Evento Adverso");
					if (tempvariable != null){
						Context convContext = Contexts.getConversationContext();
						ReporteExpeditoConduccion o = (ReporteExpeditoConduccion) Component.getInstance(ReporteExpeditoConduccion.class, true);
						convContext.set(IActionWithUserInput.actionManagerName, o);
						ReglaPlayer rp = (ReglaPlayer) Component.getInstance(ReglaPlayer.class);
						OpenModalRuleAction objectRuleAction = (OpenModalRuleAction) Component.getInstance(OpenModalRuleAction.class);
						objectRuleAction.setPathFileContent("/modEnsayo/ensayo_disenno/codebase/reglas/actionModals/Reporte_expedito.xhtml");
						rp.setLastUserInputAction(objectRuleAction);
						o.assignValues(sectionId, groupId, repetition, index);
						o.setVariableRegla(tempvariable);
						o.initConversation();
					}
				}
			}
		}
	}

	// CU 39 Completar monitoreo
	public void CompletarInstanciaHoja(){
		if(!this.desdeCompleta)
			this.monitoringCompleted = this.monitoringCompleted ? true : false;
		else
			this.monitoringCompleted = true;
		this.causaGuardar = null;
	}

	// CU 40 Descompletar monitoreo
	public void DescompletarInstanciaHoja(){
		this.desdeCompleta = false;
		this.monitoringCompleted = false;
	}

	public void uploadFile(String fileName, byte[] data){
		String filePath = this.fileFolder.getPath() + File.separator + fileName;
		try {
			if (!this.fileFolder.exists())
				this.fileFolder.mkdir();
			File file = new File(filePath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);
			dataOutputStream.write(data);
			fileOutputStream.close();
			dataOutputStream.close();
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	@SuppressWarnings("resource")
	private byte[] readFileData(File f){
		try {
			byte[] data = new byte[(int) f.length()];
			InputStream is = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(is);
			bis.read(data);
			return data;
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public UploadItem fileDataFor(Variable_ensayo var){
		if (!this.fileFolder.exists())
			return null;
		VariableDato_ensayo dato = null;
		try {
			dato = (VariableDato_ensayo) this.entityManager
					.createQuery(
							"select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId")
					.setParameter("hojaId", this.hoja.getId())
					.setParameter("variableId", var.getId()).getSingleResult();
		} catch (Exception e){
			return null;
		}
		File f = new File(this.fileFolder, dato.getValor());
		if (!f.exists())
			return null;
		UploadItem item = new UploadItem(f.getName(), (int) f.length(), "", f);
		return item;
	}

	/*
	 * Devuelve la url del icono para estado pasado por parametro.
	 * 
	 * @parametro: estado, estado del cual vamos a obtener el icono.
	 */
	public String estadoIcon(EstadoHojaCrd_ensayo estado){
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
		String path = "/resources/modEnsayo/estadosIcon/" + estado.getClass().getSimpleName().split("_")[0] + "/" + estado.getCodigo() + ".png";
		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		return (dir.exists() ? path : "/resources/modEnsayo/estados/" + "generic.png");
	}
	
	public List<GrupoVariables_ensayo> gruposObligatorios(){
		//|| (this.mapWGD.containsKey(this.convertKey(idSeccion, idGrupo)) && this.mapWGD.get(this.convertKey(idSeccion, idGrupo)).size() == 0)
		List<GrupoVariables_ensayo> lista = new ArrayList<GrupoVariables_ensayo>();
 		for (int y = 0; y < this.secciones.size(); y++){
			Long idSeccion = this.secciones.get(y).getId();
			for (int z = 0; z < this.groupsFromSection(idSeccion).size(); z++){
				Long idGrupo = this.groupsFromSection(idSeccion).get(z).getId();
				if((!this.mapWGD.containsKey(this.convertKey(idSeccion, idGrupo)) || (this.mapWGD.containsKey(this.convertKey(idSeccion, idGrupo)) && this.mapWGD.get(this.convertKey(idSeccion, idGrupo)).size() == 0)) && this.variableGrupoObligatoria(idGrupo)){
					lista.add(this.groupsFromSection(idSeccion).get(z));
				}
			}	
			
			
		}
		
		return lista;
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean variableGrupoObligatoria(long idGrupo){
		List<Variable_ensayo> lista = new ArrayList<Variable_ensayo>();
		try {
			lista = (List<Variable_ensayo>) this.entityManager.createQuery("select var from Variable_ensayo var where var.grupoVariables.id = :Grupo and var.eliminado = 'FALSE' order by var.idClinica, var.numeroPregunta ASC").setParameter("Grupo", idGrupo).getResultList();
		} catch (Exception e){
			return false;
		}
		for (int i = 0; i < lista.size(); i++) {
			if(lista.get(i).getRequerido()){
				return true;
			}
		}
		
		return false;
		
		
	}
	

	
	public boolean estanResueltasOCerradasNotas(){
		List<Nota_ensayo> lista = new ArrayList<Nota_ensayo>();
		for (int y = 0; y < this.secciones.size(); y++){
			Long idSeccion = this.secciones.get(y).getId();
			if(this.mapWD != null && !this.mapWD.isEmpty() && this.mapWD.containsKey(idSeccion)){
				for (MapWrapperDataPlus item : this.mapWD.get(idSeccion).getData().values()){
					if(item.getNoteMonitoring() != null){
						lista.add(item.getNoteMonitoring());
					}
				}
			}
			
			for (int z = 0; z < this.groupsFromSection(idSeccion).size(); z++){
				Long idGrupo = this.groupsFromSection(idSeccion).get(z).getId();
				if(this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(this.convertKey(idSeccion, idGrupo))){
					for (WrapperGroupData GroupData : this.mapWGD.get(this.convertKey(idSeccion, idGrupo))){
							for (MapWrapperDataPlus item : GroupData.getData().values()){
								if(item.getNoteMonitoring() != null){
									lista.add(item.getNoteMonitoring());
								}						
						}
					}
				}
			}	
			
			
		}
		
		boolean estan = true;
		if(lista.isEmpty()){
			estan = true;
		}else{
			for (int i = 0; i < lista.size(); i++){
				if (lista.get(i).getEstadoNota().getCodigo() != 3 && lista.get(i).getEstadoNota().getCodigo() != 4){
					estan = false;
					break;
				}
			}
		}
		
		return estan;
	}

	@SuppressWarnings("unchecked")
	public boolean listaNotas(){
		List<Nota_ensayo> lista = new ArrayList<Nota_ensayo>();
		try {
			lista = (List<Nota_ensayo>) this.entityManager.createQuery("select nota from Nota_ensayo nota where nota.crdEspecifico=:Hoj and nota.notaSitio = 'FALSE' and nota.eliminado = 'FALSE' and nota.notaPadre = null").setParameter("Hoj", this.hoja).getResultList();
		} catch (Exception e){
			return false;
		}
		return (!lista.isEmpty());
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Integer> cantidadNotasDiscrepancias(){
		this.listaCant = new ArrayList<Integer>();
		List<Nota_ensayo> listaNotas = new ArrayList<Nota_ensayo>();
		listaNotas = (List<Nota_ensayo>) this.entityManager.createQuery("select nota from Nota_ensayo nota where nota.crdEspecifico=:Hoj and nota.notaSitio = 'FALSE' and (nota.eliminado = null or nota.eliminado = false) and nota.notaPadre = null").setParameter("Hoj", this.hoja).getResultList();
		int contNuevas = 0;
		int contResueltas = 0;
		int contCerradas = 0;
		int contActualizadas = 0;
		for (int i = 0; i < listaNotas.size(); i++){
			if (listaNotas.get(i).getEstadoNota().getCodigo() == 1)
				contNuevas++;
			else if (listaNotas.get(i).getEstadoNota().getCodigo() == 2)
				contActualizadas++;
			else if (listaNotas.get(i).getEstadoNota().getCodigo() == 3)
				contResueltas++;
			else
				contCerradas++;
		}
		this.listaCant.add(0, contNuevas);
		this.listaCant.add(1, contActualizadas);
		this.listaCant.add(2, contResueltas);
		this.listaCant.add(3, contCerradas);
		return listaCant;
	}

	@SuppressWarnings("unchecked")
	public Object[] obtenerValores(Variable_ensayo var){
		Object[] valores;
		List<VariableDato_ensayo> aux = new ArrayList<VariableDato_ensayo>();
		if (var.getTipoDato().getCodigo().equals("NOM")){
			try {
				aux = (List<VariableDato_ensayo>) this.entityManager.createQuery("select NomVar from VariableDato_ensayo NomVar where NomVar.variable=:Variable and NomVar.crdEspecifico=:Hoja").setParameter("Variable", var).setParameter("Hoja", this.hoja).getResultList();
			} catch (Exception e){
				return null;
			}
		}
		if (aux.size() == 0)
			return null;
		valores = new Object[aux.size()];
		for (int i = 0; i < aux.size(); i++)
			valores[i] = aux.get(i).getValor().toString();
		return valores;
	}

	public Role_ensayo obtainRole(){
		if (this.rolLogueado == null){
			try {
				this.rolLogueado = (Role_ensayo) this.entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true").setParameter("estudId", this.seguridadEstudio.getEstudioEntidadActivo().getId()).setParameter("idusua", this.user.getId()).getSingleResult();
			} catch (Exception e){
				this.rolLogueado = null;
			}
		}
		return rolLogueado;
	}

	public Object obtenerValor(Variable_ensayo var){
		Object valor = null;
		if (var.getTipoDato().getCodigo().equals("FILE") || var.getTipoDato().getCodigo().equals("BL") || var.getTipoDato().getCodigo().equals("ST") || var.getTipoDato().getCodigo().equals("INT") || var.getTipoDato().getCodigo().equals("REAL") || (var.getTipoDato().getCodigo().equals("DATE") && var.getPresentacionFormulario().getNombre().equals("text"))){
			try {
				valor = this.entityManager.createQuery("select variableD.valor from VariableDato_ensayo variableD where variableD.variable=:Variable and variableD.crdEspecifico=:Hoja").setParameter("Variable", var).setParameter("Hoja", this.hoja).getSingleResult();
			} catch (Exception e){
				return null;
			}
		}
		if (var.getTipoDato().getCodigo().equals("NOM")){
			try {
				valor = this.entityManager.createQuery("select NomVar.valor from VariableDato_ensayo NomVar where NomVar.variable=:Variable and NomVar.crdEspecifico=:Hoja").setParameter("Variable", var).setParameter("Hoja", this.hoja).getSingleResult();
			} catch (Exception e){
				return null;
			}
		}
		if ((var.getTipoDato().getCodigo().equals("DATE") && var.getPresentacionFormulario().getNombre().equals("calendar"))){
			Object valoraux = null;
			try {
				valoraux = this.entityManager.createQuery("select variableD.valor from VariableDato_ensayo variableD where variableD.variable=:Variable and variableD.crdEspecifico=:Hoja").setParameter("Variable", var).setParameter("Hoja", this.hoja).getSingleResult();
			} catch (Exception e){
				return null;
			}
			String[] fechaArreglo = valoraux.toString().split("/");
			String fecha = "";
			if (fechaArreglo.length == 1){
				fecha = "01" + "/" + "01" + "/" + fechaArreglo[0];
			} else if (fechaArreglo.length == 2)
				fecha = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
			else if (fechaArreglo.length == 3){
				if (fechaArreglo[0].equals("****"))
					fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
				else if (fechaArreglo[0].length() == 4 && !fechaArreglo[0].equals("****"))
					fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
				else
					fecha = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
			}
			valor = fecha;
		}
		return valor;
	}

	public String concatenarId(long idGrupo, long idSeccion){
		String dev = "var" + String.valueOf(idGrupo) + String.valueOf(idSeccion);
		return dev;
	}

	public String concatenarId(Variable_ensayo v){
		return idUtil.For(v);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List valoresRadio(Long nomencladorId){
		List listaRadio = new ArrayList();
		if (nomencladorId != null && nomencladorId.toString() != null && !nomencladorId.toString().isEmpty()){
			List<NomencladorValor_ensayo> lista = (List<NomencladorValor_ensayo>) this.entityManager.createQuery("select nom from NomencladorValor_ensayo nom where (nom.eliminado = null or nom.eliminado = false) and nom.nomenclador.id = :nomencladorId and nom.valor != null ORDER BY nom.valorCalculado ASC").setParameter("nomencladorId", nomencladorId).getResultList();
			for (int i = 0; i < lista.size(); i++)
				listaRadio.add(new SelectItem(lista.get(i).getValor(), lista.get(i).getValor()));
		}
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public List<String> valores(Long nomencladorId){
		List<String> lista = new ArrayList<String>();
		if (nomencladorId != null && nomencladorId.toString() != null && !nomencladorId.toString().isEmpty())
			lista = (List<String>) this.entityManager.createQuery("select nom.valor from NomencladorValor_ensayo nom where (nom.eliminado = null or nom.eliminado = false) and nom.nomenclador.id = :nomencladorId ORDER BY nom.valorCalculado ASC").setParameter("nomencladorId", nomencladorId).getResultList();
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> tieneVariableGrupoSeccion(Seccion_ensayo seccion, GrupoVariables_ensayo grupo){
		List<Variable_ensayo> lista = new ArrayList<Variable_ensayo>();
		try {
			lista = (List<Variable_ensayo>) this.entityManager.createQuery("select var from Variable_ensayo var where var.grupoVariables = :Grupo and var.seccion = :Seccion and var.eliminado = 'FALSE' order by var.idClinica, var.numeroPregunta ASC").setParameter("Grupo", grupo).setParameter("Seccion", seccion).getResultList();
		} catch (Exception e){
			return null;
		}
		return lista;
	}

	// Excel crear exel

	@SuppressWarnings({ "unchecked", "resource", "static-access" })
	public void main() throws IOException {
		Workbook libro = new HSSFWorkbook();
		// Para poner la letra del excel
		HSSFFont fuente = (HSSFFont) libro.createFont();
		fuente.setFontHeightInPoints((short) 11);
		fuente.setFontName(fuente.FONT_ARIAL);
		fuente.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// Luego creamos el objeto que se encargará de aplicar el estilo a la
		// celda
		HSSFCellStyle estiloCelda = (HSSFCellStyle) libro.createCellStyle();
		estiloCelda.setWrapText(false);
		estiloCelda.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);
		estiloCelda.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);

		// Creamos el estilo de celda del color ROJO
		HSSFCellStyle styleGroup3 = (HSSFCellStyle) libro.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setWrapText(true);
		styleGroup3.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

		// Creamos el estilo de celda del color AMARILLO
		HSSFCellStyle styleGroup2 = (HSSFCellStyle) libro.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

		// Creamos el estilo de celda del color VERDE
		HSSFCellStyle styleGroup1 = (HSSFCellStyle) libro.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);

		// Para crear la hoja del CRD y sus columnas
		Sheet hoja0 = libro.createSheet(SeamResourceBundle.getBundle().getString("msg_crd_ens"));
		hoja0.setColumnWidth(0, 256 * 30);
		hoja0.setColumnWidth(1, 256 * 20);
		hoja0.setColumnWidth(2, 256 * 30);
		hoja0.setColumnWidth(3, 256 * 40);

		// Para crear la hoja secciones y sus columnas
		Sheet hoja1 = libro.createSheet(SeamResourceBundle.getBundle().getString("msg_seccion_ens"));
		hoja1.setColumnWidth(0, 256 * 30);
		hoja1.setColumnWidth(1, 256 * 30);
		hoja1.setColumnWidth(2, 256 * 30);
		hoja1.setColumnWidth(3, 256 * 30);

		// Para crear la hoja grupo y sus columnas
		Sheet hoja2 = libro.createSheet(SeamResourceBundle.getBundle().getString("msg_grupo_ens"));
		hoja2.setColumnWidth(0, 256 * 30);
		hoja2.setColumnWidth(1, 256 * 30);
		hoja2.setColumnWidth(2, 256 * 30);
		hoja2.setColumnWidth(3, 256 * 40);
		hoja2.setColumnWidth(4, 256 * 40);
		hoja2.setColumnWidth(5, 256 * 40);
		hoja2.setColumnWidth(6, 256 * 40);

		// Para crear la hoja variables y sus columnas
		Sheet hoja3 = libro.createSheet(SeamResourceBundle.getBundle().getString("msg_variable_ens"));
		hoja3.setColumnWidth(0, 256 * 40);
		hoja3.setColumnWidth(1, 256 * 40);
		hoja3.setColumnWidth(2, 256 * 40);
		hoja3.setColumnWidth(3, 256 * 40);
		hoja3.setColumnWidth(4, 256 * 40);
		hoja3.setColumnWidth(5, 256 * 40);
		hoja3.setColumnWidth(6, 256 * 40);
		hoja3.setColumnWidth(7, 256 * 40);
		hoja3.setColumnWidth(8, 256 * 40);
		hoja3.setColumnWidth(9, 256 * 40);
		hoja3.setColumnWidth(10, 256 * 40);
		hoja3.setColumnWidth(11, 256 * 40);
		hoja3.setColumnWidth(12, 256 * 40);
		hoja3.setColumnWidth(13, 256 * 40);
		hoja3.setColumnWidth(14, 256 * 40);
		hoja3.setColumnWidth(15, 256 * 40);
		hoja3.setColumnWidth(16, 256 * 40);
		hoja3.setColumnWidth(17, 256 * 40);
		hoja3.setColumnWidth(18, 256 * 40);

		// para la hoja crd
		Row filacrd = hoja0.createRow((short) 0);
		Cell celdacrd0 = filacrd.createCell((short) 0);
		celdacrd0.setCellStyle(styleGroup3);
		celdacrd0.setCellValue(SeamResourceBundle.getBundle().getString("lbl_crdname_ensClin"));
		Cell celdacrd1 = filacrd.createCell((short) 1);
		celdacrd1.setCellStyle(styleGroup3);
		celdacrd1.setCellValue(SeamResourceBundle.getBundle().getString("lbl_versioncrd_ensClin"));
		Cell celdacrd2 = filacrd.createCell((short) 2);
		celdacrd2.setCellStyle(styleGroup3);
		celdacrd2.setCellValue(SeamResourceBundle.getBundle().getString("lbl_descversioncrd_ensClin"));
		Cell celdacrd3 = filacrd.createCell((short) 3);
		celdacrd3.setCellStyle(styleGroup3);
		celdacrd3.setCellValue(SeamResourceBundle.getBundle().getString("lbl_notascrd_ensClin"));
		Row filacrd1 = hoja0.createRow((short) 1);
		Cell celdacrd9 = filacrd1.createCell((short) 0);
		celdacrd9.setCellValue(this.hoja.getHojaCrd().getNombreHoja());
		if (this.hoja.getVersion() != null){
			Cell celdacrd10 = filacrd1.createCell((short) 1);
			celdacrd10.setCellValue(this.hoja.getVersion().toString());
		}
		Cell celdacrd11 = filacrd1.createCell((short) 2);
		celdacrd11.setCellValue(this.hoja.getHojaCrd().getDescripcion());

		// para la hoja seccion
		Row filaseciones = hoja1.createRow((short) 0);
		Cell celdasecion = filaseciones.createCell((short) 0);
		celdasecion.setCellValue(SeamResourceBundle.getBundle().getString("lbl_nameseccion_ensClin"));
		celdasecion.setCellStyle(styleGroup3);
		Cell celdasecion1 = filaseciones.createCell((short) 1);
		celdasecion1.setCellValue(SeamResourceBundle.getBundle().getString("lbl_tituloseccion_ensClin"));
		celdasecion1.setCellStyle(styleGroup3);
		Cell celdasecion2 = filaseciones.createCell((short) 2);
		celdasecion2.setCellValue(SeamResourceBundle.getBundle().getString("lbl_subtituloseccion_ensClin"));
		celdasecion2.setCellStyle(styleGroup3);
		Cell celdasecion3 = filaseciones.createCell((short) 3);
		celdasecion3.setCellValue(SeamResourceBundle.getBundle().getString("lbl_instruccioneseccion_ensClin"));
		celdasecion3.setCellStyle(styleGroup3);
		for (int x = 0; x < this.secciones.size(); x++){
			Row fila5 = hoja1.createRow((short) x + 1);
			Cell celdasecion5 = fila5.createCell((short) 0);
			celdasecion5.setCellValue(this.secciones.get(x).getEtiquetaSeccion());
			Cell celdasecion6 = fila5.createCell((short) 1);
			celdasecion6.setCellValue(this.secciones.get(x).getTituloSeccion());
			Cell celdasecion7 = fila5.createCell((short) 2);
			celdasecion7.setCellValue(this.secciones.get(x).getSubtitulo());
			Cell celdasecion8 = fila5.createCell((short) 3);
			celdasecion8.setCellValue(this.secciones.get(x).getInstrucciones());
		}

		// para la hoja grupo
		Row filagrupo = hoja2.createRow((short) 0);
		Cell celdagrupo = filagrupo.createCell((short) 0);
		celdagrupo.setCellValue(SeamResourceBundle.getBundle().getString("lbl_namegroup_ensClin"));
		celdagrupo.setCellStyle(styleGroup3);
		Cell celdagrupo1 = filagrupo.createCell((short) 1);
		celdagrupo1.setCellValue(SeamResourceBundle.getBundle().getString("lbl_descgroup_ensClin"));
		celdagrupo1.setCellStyle(styleGroup3);
		Cell celdagrupo2 = filagrupo.createCell((short) 2);
		celdagrupo2.setCellValue(SeamResourceBundle.getBundle().getString("lbl_headergroup_ensClin"));
		celdagrupo2.setCellStyle(styleGroup3);
		Cell celdagrupo3 = filagrupo.createCell((short) 3);
		celdagrupo3.setCellValue(SeamResourceBundle.getBundle().getString("lbl_nameseccion_ensClin"));
		celdagrupo3.setCellStyle(styleGroup3);
		Cell celdagrupo4 = filagrupo.createCell((short) 4);
		celdagrupo4.setCellValue(SeamResourceBundle.getBundle().getString("lbl_repeatnumber_ensClin"));
		celdagrupo4.setCellStyle(styleGroup3);
		Cell celdagrupo5 = filagrupo.createCell((short) 5);
		celdagrupo5.setCellValue(SeamResourceBundle.getBundle().getString("lbl_repeatmaxnumber_ensClin"));
		celdagrupo5.setCellStyle(styleGroup3);
		Cell celdagrupo6 = filagrupo.createCell((short) 6);
		celdagrupo6.setCellValue(SeamResourceBundle.getBundle().getString("lbl_visibilitygroup_ensClin"));
		celdagrupo6.setCellStyle(styleGroup3);
		for (int y = 0; y < this.secciones.size(); y++){
			for (int z = 0; z < this.groupsFromSection(this.secciones.get(y).getId()).size(); z++){
				GrupoVariables_ensayo grupo = this.groupsFromSection(this.secciones.get(y).getId()).get(z);
				Row fila5 = hoja2.createRow((short) z + 1);
				Cell celdagrupo7 = fila5.createCell((short) 0);
				celdagrupo7.setCellValue(grupo.getEtiquetaGrupo());
				Cell celdagrupo8 = fila5.createCell((short) 1);
				celdagrupo8.setCellValue(grupo.getDescripcionGrupo());
				Cell celdagrupo9 = fila5.createCell((short) 2);
				celdagrupo9.setCellValue(grupo.getEncabezado());
				Cell celdagrupo10 = fila5.createCell((short) 3);
				celdagrupo10.setCellValue(grupo.getSeccion().getEtiquetaSeccion());
				Cell celdagrupo11 = fila5.createCell((short) 4);
				if (grupo.getNumRepeticiones() != null)
					celdagrupo11.setCellValue(grupo.getNumRepeticiones());
				else
					celdagrupo10.setCellValue("");
				Cell celdagrupo12 = fila5.createCell((short) 5);
				if (grupo.getNumMaxRepeticiones() != null)
					celdagrupo12.setCellValue(grupo.getNumMaxRepeticiones());
				else
					celdagrupo12.setCellValue("");
				Cell celdagrupo13 = fila5.createCell((short) 6);
				celdagrupo13.setCellValue("SHOW");
			}
		}

		// para la hoja variable
		Row filavariable = hoja3.createRow((short) 0);
		Cell celdavariable = filavariable.createCell((short) 0);
		celdavariable.setCellValue(SeamResourceBundle.getBundle().getString("lbl_namevar_ensClin"));
		celdavariable.setCellStyle(styleGroup3);
		Cell celdavariableva = filavariable.createCell((short) 1);
		celdavariableva.setCellValue(SeamResourceBundle.getBundle().getString("lbl_valorvar_ensClin"));
		celdavariableva.setCellStyle(styleGroup3);
		Cell celdavariable1 = filavariable.createCell((short) 2);
		celdavariable1.setCellValue(SeamResourceBundle.getBundle().getString("lbl_descvar_ensClin"));
		celdavariable1.setCellStyle(styleGroup3);
		Cell celdavariable2 = filavariable.createCell((short) 3);
		celdavariable2.setCellValue(SeamResourceBundle.getBundle().getString("lbl_txtleftvar_ensClin"));
		celdavariable2.setCellStyle(styleGroup3);
		Cell celdavariable3 = filavariable.createCell((short) 4);
		celdavariable3.setCellValue(SeamResourceBundle.getBundle().getString("lbl_unidadesvar_ensClin"));
		celdavariable3.setCellStyle(styleGroup3);
		Cell celdavariable4 = filavariable.createCell((short) 5);
		celdavariable4.setCellValue(SeamResourceBundle.getBundle().getString("lbl_txtrigthvar_ensClin"));
		celdavariable4.setCellStyle(styleGroup3);
		Cell celdavariable5 = filavariable.createCell((short) 6);
		celdavariable5.setCellValue(SeamResourceBundle.getBundle().getString("lbl_nameseccion_ensClin"));
		celdavariable5.setCellStyle(styleGroup3);
		Cell celdavariable6 = filavariable.createCell((short) 7);
		celdavariable6.setCellValue(SeamResourceBundle.getBundle().getString("lbl_namegroup_ensClin"));
		celdavariable6.setCellStyle(styleGroup3);
		Cell celdavariable7 = filavariable.createCell((short) 8);
		celdavariable7.setCellValue(SeamResourceBundle.getBundle().getString("lbl_headervar_ensClin"));
		celdavariable7.setCellStyle(styleGroup3);
		Cell celdavariable8 = filavariable.createCell((short) 9);
		celdavariable8.setCellValue(SeamResourceBundle.getBundle().getString("lbl_subheadervar_ensClin"));
		celdavariable8.setCellStyle(styleGroup3);
		Cell celdavariable9 = filavariable.createCell((short) 10);
		celdavariable9.setCellValue(SeamResourceBundle.getBundle().getString("lbl_columnumbervar_ensClin"));
		celdavariable9.setCellStyle(styleGroup3);
		Cell celdavariable10 = filavariable.createCell((short) 11);
		celdavariable10.setCellValue(SeamResourceBundle.getBundle().getString("lbl_asknumbervar_ensClin"));
		celdavariable10.setCellStyle(styleGroup3);
		Cell celdavariable11 = filavariable.createCell((short) 12);
		celdavariable11.setCellValue(SeamResourceBundle.getBundle().getString("lbl_pfvar_ensClin"));
		celdavariable11.setCellStyle(styleGroup3);
		Cell celdavariable16 = filavariable.createCell((short) 13);
		celdavariable16.setCellValue(SeamResourceBundle.getBundle().getString("lbl_tdvar_ensClin"));
		celdavariable16.setCellStyle(styleGroup3);
		Cell celdavariable12 = filavariable.createCell((short) 14);
		celdavariable12.setCellValue(SeamResourceBundle.getBundle().getString("lbl_nameNOMvar_ensClin"));
		celdavariable12.setCellStyle(styleGroup3);
		Cell celdavariable13 = filavariable.createCell((short) 15);
		celdavariable13.setCellValue(SeamResourceBundle.getBundle().getString("lbl_optvar_ensClin"));
		celdavariable13.setCellStyle(styleGroup3);
		Cell celdavariable15 = filavariable.createCell((short) 16);
		celdavariable15.setCellValue(SeamResourceBundle.getBundle().getString("lbl_vpdvar_ensClin"));
		celdavariable15.setCellStyle(styleGroup3);
		Cell celdavariable14 = filavariable.createCell((short) 17);
		celdavariable14.setCellValue(SeamResourceBundle.getBundle().getString("lbl_ubicacionrespuestavar_ensClin"));
		celdavariable14.setCellStyle(styleGroup3);
		Cell celdavariable17 = filavariable.createCell((short) 18);
		celdavariable17.setCellValue(SeamResourceBundle.getBundle().getString("lbl_reqvar_ensClin"));
		celdavariable17.setCellStyle(styleGroup3);
		Cell celdavariable18 = filavariable.createCell((short) 19);
		celdavariable18.setCellValue(SeamResourceBundle.getBundle().getString("lbl_unicvar_ensClin"));
		celdavariable18.setCellStyle(styleGroup3);
		int cont = 0;
		int x = 0;
		for (int y = 0; y < this.secciones.size(); y++){
			Long idSeccion = this.secciones.get(y).getId();
			if(!this.mapWD.isEmpty()){
			for (MapWrapperDataPlus item : this.mapWD.get(idSeccion).getData().values()){
				Row filavariablea = hoja3.createRow((short) x + 1);
				x++;
				cont++;
				Cell celdavariablea = filavariablea.createCell((short) 0);
				celdavariablea.setCellValue(item.getVariable().getNombreVariable());
				Cell celdavariableava = filavariablea.createCell((short) 1);
				if (item.getValue() != null)
					celdavariableava.setCellValue(item.getValue().toString());
				else
					celdavariableava.setCellValue("");
				Cell celdavariablea1 = filavariablea.createCell((short) 2);
				celdavariablea1.setCellValue(item.getVariable()
						.getDescripcionVariable());
				Cell celdavariablea2 = filavariablea.createCell((short) 3);
				celdavariablea2.setCellValue(item.getVariable().getTextoIzquierdaVariable());
				Cell celdavariablea3 = filavariablea.createCell((short) 4);
				celdavariablea3.setCellValue(item.getVariable().getUnidadesVariable());
				Cell celdavariablea4 = filavariablea.createCell((short) 5);
				celdavariablea4.setCellValue(item.getVariable().getTextoDerechaVariable());
				Cell celdavariablea5 = filavariablea.createCell((short) 6);
				celdavariablea5.setCellValue(item.getVariable().getSeccion().getEtiquetaSeccion());
				Cell celdavariablea6 = filavariablea.createCell((short) 7);
				if (item.getVariable().getGrupoVariables() != null)
					celdavariablea6.setCellValue(item.getVariable().getGrupoVariables().getEtiquetaGrupo());
				else
					celdavariablea6.setCellValue("");
				Cell celdavariablea7 = filavariablea.createCell((short) 8);
				celdavariablea7.setCellValue(item.getVariable().getEncabezadoVariable());
				Cell celdavariablea8 = filavariablea.createCell((short) 9);
				celdavariablea8.setCellValue(item.getVariable().getSubencabezadoVariable());
				Cell celdavariablea9 = filavariablea.createCell((short) 10);
				if (item.getVariable().getNumeroColumna() != null)
					celdavariablea9.setCellValue(item.getVariable().getNumeroColumna().toString());
				else
					celdavariablea9.setCellValue("1");
				Cell celdavariablea10 = filavariablea.createCell((short) 11);
				if (item.getVariable().getNumeroPregunta() != null)
					celdavariablea10.setCellValue(item.getVariable().getNumeroPregunta().toString());
				else
					celdavariablea10.setCellValue("1");
				Cell celdavariablea11 = filavariablea.createCell((short) 12);
				celdavariablea11.setCellValue(item.getVariable().getPresentacionFormulario().getNombre());
				Cell celdavariablea16 = filavariablea.createCell((short) 13);
				celdavariablea16.setCellValue(item.getVariable().getTipoDato().getCodigo());
				Cell celdavariablea12 = filavariablea.createCell((short) 14);
				Cell celdavariablea13 = filavariablea.createCell((short) 15);
				Cell celdavariablea15 = filavariablea.createCell((short) 16);
				if (item.getVariable().getNomenclador() != null){
					celdavariablea12.setCellValue(item.getVariable().getNomenclador().getNombre());
					List<NomencladorValor_ensayo> valores = (List<NomencladorValor_ensayo>) this.entityManager.createQuery("select valorNOM from NomencladorValor_ensayo valorNOM join valorNOM.nomenclador nom where nom.id = :nomId").setParameter("nomId", item.getVariable().getNomenclador().getId()).getResultList();
					String a = "";
					for (int i = 0; i < valores.size() - 1; i++)
						a = a + valores.get(i).getValor() + ",";
					a = a + valores.get(valores.size() - 1).getValor();
					celdavariablea13.setCellValue(a);
					celdavariablea15.setCellValue(item.getVariable().getNomenclador().getValorDefecto());
				} else {
					celdavariablea12.setCellValue("");
					celdavariablea13.setCellValue("");
					celdavariablea15.setCellValue("");
					Cell celdavariablea14 = filavariablea.createCell((short) 17);
					celdavariablea14.setCellValue(item.getVariable().getUbicacionRespuesta());
					celdavariablea14.setCellValue("Vertical");
					Cell celdavariablea17 = filavariablea.createCell((short) 18);
					if (item.getVariable().getRequerido() != null)
						celdavariablea17.setCellValue(item.getVariable().getRequerido());
					else
						celdavariablea17.setCellValue("");
				}
			}
			}
			for (int z = 0; z < this.groupsFromSection(idSeccion).size(); z++){
				Long idGrupo = this.groupsFromSection(idSeccion).get(z).getId();
				List<valorGrupoWraper> listaValor = new ArrayList<valorGrupoWraper>();
				if(!this.mapWGD.isEmpty()){
					for (WrapperGroupData GroupData : this.mapWGD.get(this.convertKey(idSeccion, idGrupo))){
						for (MapWrapperDataPlus item : GroupData.getData().values()){
							if (listaValor.size() == 0)
								listaValor.add(new valorGrupoWraper(item.getVariable(), item.getValue() != null ? item.getValue().toString() : ""));
							else {
								boolean esta = false;
								for (int i = 0; i < listaValor.size(); i++){
									if (listaValor.get(i).getVariable().getId() == item.getVariable().getId()){
										listaValor.get(i).setValor(listaValor.get(i).getValor().toString() + ", " + (item.getValue() != null ? item.getValue().toString() : ""));
										esta = true;
										break;
									} 
								}
								if(!esta)									
									listaValor.add(new valorGrupoWraper(item.getVariable(), item.getValue() != null ? item.getValue().toString() : ""));									
							}
						}
					}
				}
				for (int j = 0; j < listaValor.size(); j++){
					Row filavariablea = hoja3.createRow((short) cont + 1);
					cont++;
					Cell celdavariablea = filavariablea.createCell((short) 0);
					celdavariablea.setCellValue(listaValor.get(j).getVariable().getNombreVariable());
					Cell celdavariableava = filavariablea.createCell((short) 1);
					if (listaValor.get(j).getValor() != null)
						celdavariableava.setCellValue(listaValor.get(j).getValor());
					else
						celdavariableava.setCellValue("");
					Cell celdavariablea1 = filavariablea.createCell((short) 2);
					celdavariablea1.setCellValue(listaValor.get(j).getVariable().getDescripcionVariable());
					Cell celdavariablea2 = filavariablea.createCell((short) 3);
					celdavariablea2.setCellValue(listaValor.get(j).getVariable().getTextoIzquierdaVariable());
					Cell celdavariablea3 = filavariablea.createCell((short) 4);
					celdavariablea3.setCellValue(listaValor.get(j).getVariable().getUnidadesVariable());
					Cell celdavariablea4 = filavariablea.createCell((short) 5);
					celdavariablea4.setCellValue(listaValor.get(j).getVariable().getTextoDerechaVariable());
					Cell celdavariablea5 = filavariablea.createCell((short) 6);
					celdavariablea5.setCellValue(listaValor.get(j).getVariable().getSeccion().getEtiquetaSeccion());
					Cell celdavariablea6 = filavariablea.createCell((short) 7);
					if (listaValor.get(j).getVariable().getGrupoVariables() != null)
						celdavariablea6.setCellValue(listaValor.get(j).getVariable().getGrupoVariables().getEtiquetaGrupo());
					else
						celdavariablea6.setCellValue("");
					Cell celdavariablea7 = filavariablea.createCell((short) 8);
					celdavariablea7.setCellValue(listaValor.get(j).getVariable().getEncabezadoVariable());
					Cell celdavariablea8 = filavariablea.createCell((short) 9);
					celdavariablea8.setCellValue(listaValor.get(j).getVariable().getSubencabezadoVariable());
					Cell celdavariablea9 = filavariablea.createCell((short) 10);
					if (listaValor.get(j).getVariable().getNumeroColumna() != null)
						celdavariablea9.setCellValue(listaValor.get(j).getVariable().getNumeroColumna().toString());
					else
						celdavariablea9.setCellValue('1');
					Cell celdavariablea10 = filavariablea.createCell((short) 11);
					if (listaValor.get(j).getVariable().getNumeroPregunta() != null)
						celdavariablea10.setCellValue(listaValor.get(j).getVariable().getNumeroPregunta().toString());
					else
						celdavariablea10.setCellValue('1');
					Cell celdavariablea11 = filavariablea.createCell((short) 12);
					celdavariablea11.setCellValue(listaValor.get(j).getVariable().getPresentacionFormulario().getNombre());
					Cell celdavariablea16 = filavariablea.createCell((short) 13);
					celdavariablea16.setCellValue(listaValor.get(j).getVariable().getTipoDato().getCodigo());
					Cell celdavariablea12 = filavariablea.createCell((short) 14);
					Cell celdavariablea13 = filavariablea.createCell((short) 15);
					Cell celdavariablea15 = filavariablea.createCell((short) 16);
					if (listaValor.get(j).getVariable().getNomenclador() != null){
						celdavariablea12.setCellValue(listaValor.get(j).getVariable().getNomenclador().getNombre());
						List<NomencladorValor_ensayo> valores = (List<NomencladorValor_ensayo>) this.entityManager.createQuery("select valorNOM from NomencladorValor_ensayo valorNOM join valorNOM.nomenclador nom where nom.id = :idnom").setParameter("idnom", listaValor.get(j).getVariable().getNomenclador().getId()).getResultList();
						String a = "";
						for (int i = 0; i < valores.size() - 1; i++)
							a = a + valores.get(i).getValor() + ",";
						a = a + valores.get(valores.size() - 1).getValor();
						celdavariablea13.setCellValue(a);
						celdavariablea15.setCellValue(listaValor.get(j).getVariable().getNomenclador().getValorDefecto());
					} else {
						celdavariablea12.setCellValue("");
						celdavariablea13.setCellValue("");
						celdavariablea15.setCellValue("");
						Cell celdavariablea14 = filavariablea.createCell((short) 17);
						celdavariablea14.setCellValue(listaValor.get(j).getVariable().getUbicacionRespuesta());
						celdavariablea14.setCellValue("Vertical");
						Cell celdavariablea17 = filavariablea.createCell((short) 18);
						if (listaValor.get(j).getVariable().getRequerido() != null)
							celdavariablea17.setCellValue(listaValor.get(j).getVariable().getRequerido());
						else
							celdavariablea17.setCellValue("");
					}
				}
			}
		}
		/*
		 * libro.write(archivo); archivo.close();
		 */
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
		String rootpath = context.getRealPath(File.separator + "resources" + File.separator + "modEnsayo" + File.separator + "crdExport" + File.separator + tmpFolder + File.separator);
		SimpleDateFormat format = new SimpleDateFormat("dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");
		String name_crd = stripAccents(this.hoja.getHojaCrd().getNombreHoja());
		String zipName = name_crd + " " + format.format(new Date()) + ".xls";
		this.path = "/resources/modEnsayo/crdExport" + "/" + tmpFolder + "/" + zipName;
		try {
			FileOutputStream archivo = new FileOutputStream(new File(rootpath, zipName));
			libro.write(archivo);
			archivo.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean pesquisajeHabilitado(){
		GrupoSujetos_ensayo grupo = null;
		try {
			grupo = (GrupoSujetos_ensayo) this.entityManager.createQuery("select g from GrupoSujetos_ensayo g where g.estudio = :estud and g.nombreGrupo = 'Grupo Pesquisaje'").setParameter("estud", this.seguridadEstudio.getEstudioEntidadActivo().getEstudio()).getSingleResult();
		} catch (Exception e){
			grupo = null;
		}
		return (grupo != null && grupo.getHabilitado());
	}

	public boolean obtenerMomentoPesquisaje(){
		return (this.hoja.getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre().equals("Pesquisaje"));
	}
	
	//Verifica que la hoja crd tenga variables independientes
	private boolean hasVariables(){
		for (Entry<Long, WrapperGroupData> a : this.mapWD.entrySet()) {
			if (a.getValue().getData().size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public String checkAllRequiredVariablesBeforeAddGroup() {

		String message = "";

		Map<Long, WrapperGroupData> listvariables = this.mapWD;
		boolean hasVariables = false;

		for (Entry<Long, WrapperGroupData> a : listvariables.entrySet()) {
			if (a.getValue().getData().size() > 0) {
				hasVariables = true;
				break;
			}
		}

		boolean requiredWithNoData = false;

		if (hasVariables) {
			for (Entry<Long, WrapperGroupData> seccion : listvariables
					.entrySet()) {
				WrapperGroupData seccion2 = seccion.getValue();
				Map<Long, MapWrapperDataPlus> seccion3 = seccion2.getData();
				for (Entry<Long, MapWrapperDataPlus> a : seccion3.entrySet()) {
					MapWrapperDataPlus variable = a.getValue();
					if (!((variable.getValue() != null
							&& variable.getValue().toString() != null
							&& !variable.getValue().toString()
									.isEmpty() && variable.getValue()
							.toString().length() != 0)
							|| (variable.getValues() != null && variable
									.getValues().length > 0)
							|| (variable.getFileName() != null
									&& !variable.getFileName()
											.isEmpty()
									&& variable.getData() != null && variable
									.getData().length > 0))&&variable.getVariable().getRequerido()) {
						requiredWithNoData=true;
						break;
					}
				}
			}
		}
		
		if (requiredWithNoData) {
			message += "Todas las variables requeridas deben tener valor.";
		}	

		return message;
	}
	
	public boolean anyVariableWithValueOnGroup() {
		Map<String, List<WrapperGroupData>> groupDataAdded = this
				.groupDataAdded();;
		for (Entry<String, List<WrapperGroupData>> itemEntry : groupDataAdded.entrySet()){
			if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
				for (WrapperGroupData wgd : itemEntry.getValue()){
					if (wgd.validElements()){
						for (MapWrapperDataPlus itemData : wgd.getData().values()){
							if (itemData.validFullElements()){
								VariableDato_ensayo vd = ((itemData.getVariableData() != null) ? itemData.getVariableData() : new VariableDato_ensayo());
								List<VariableDato_ensayo> vds = new ArrayList<VariableDato_ensayo>();
								if (!itemData.notValued()) {
									if ((itemData.getValue() != null
											&& itemData.getValue()
													.toString() != null && !itemData
											.getValue().toString()
											.isEmpty())
											|| (itemData.getValues() != null && itemData
													.getValues().length > 0)
											|| (itemData.getFileName() != null
													&& !itemData
															.getFileName()
															.isEmpty()
													&& itemData
															.getData() != null && itemData
													.getData().length > 0)) {
										if (itemData.getVariable()
												.getTipoDato()
												.getCodigo()
												.equals("ST")
												|| itemData
														.getVariable()
														.getTipoDato()
														.getCodigo()
														.equals("BL")
												|| itemData
														.getVariable()
														.getTipoDato()
														.getCodigo()
														.equals("INT")
												|| itemData
														.getVariable()
														.getTipoDato()
														.getCodigo()
														.equals("REAL")
												|| (itemData
														.getVariable()
														.getTipoDato()
														.getCodigo()
														.equals("DATE") && itemData
														.getVariable()
														.getPresentacionFormulario()
														.getNombre()
														.equals("text"))
												|| (itemData
														.getVariable()
														.getTipoDato()
														.getCodigo()
														.equals("NOM")
														&& !itemData
																.getVariable()
																.getPresentacionFormulario()
																.getNombre()
																.equals("checkbox") && !itemData
														.getVariable()
														.getPresentacionFormulario()
														.getNombre()
														.equals("multi-select"))){
											return true;
										}
										else if (itemData.getVariable()
												.getTipoDato()
												.getCodigo()
												.equals("DATE")
												&& itemData
														.getVariable()
														.getPresentacionFormulario()
														.getNombre()
														.equals("calendar")){
											return true;
										}
											
										else if (itemData.getVariable().getTipoDato().getCodigo().equals("NOM")
												&& (itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox")
														|| itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select"))) {
											vd = null;
											if (itemData.getValues() != null && itemData.getValues().length > 0){
												for (int i = 0; i < itemData.getValues().length; i++){
													if (itemData.getValues()[i] != null && itemData.getValues()[i].toString() != null && !itemData.getValues()[i].toString().trim().isEmpty()){
														VariableDato_ensayo tempVd = new VariableDato_ensayo();
														tempVd.setValor(((String) itemData.getValues()[i]).trim());
														vds.add(tempVd);
														return true;
													}
												}
											}
										} else if (itemData.getVariable().getTipoDato().getCodigo().equals("FILE") && itemData.getFileName() != null && !itemData.getFileName().trim().isEmpty() && itemData.getData() != null && itemData.getData().length > 0){
											String tempName = itemData.getVariable().getId() + itemData.getFileName().trim();
											this.uploadFile(tempName, itemData.getData());
											vd.setValor(tempName);
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void deleteGroup(Map<String, List<WrapperGroupData>> groupDataDeleted){
		String qHr = "select hr from ReHistoriarelevante_ensayo hr where hr.reReporteexpedito.id = :reportId";
		String qIp = "select ip from ReInfoproducto_ensayo ip where ip.reReporteexpedito.id = :reportId";
		String qMc = "select mc from ReMedicacionconcomitante_ensayo mc where mc.reReporteexpedito.id = :reportId";
		String qMr = "select mr from ReMedicacionreciente_ensayo mr where mr.reReporteexpedito.id = :reportId";
		String qEi = "select ei from ReEnfermedadesinter_ensayo ei where ei.reReporteexpedito.id = :reportId";
		String qMonitoringNotesRelated = "select nota from Nota_ensayo nota where nota.notaPadre.id = :notaPadreId and nota.notaSitio = false and (nota.eliminado = null or nota.eliminado = false)";
		String qSiteNotesRelated = "select nota from Nota_ensayo nota where nota.notaPadre.id = :notaPadreId and nota.notaSitio = true and (nota.eliminado = null or nota.eliminado = false)";
		

		// Group Data to delete
		for (Entry<String, List<WrapperGroupData>> itemEntry : groupDataDeleted.entrySet()){
			if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
				for (WrapperGroupData wgd : itemEntry.getValue()){
					if (wgd.validElements()){
						for (MapWrapperDataPlus itemData : wgd.getData().values()){
							if (itemData.validElements()){
								if (itemData.hasReport()){
									try {
										List<ReHistoriarelevante_ensayo> lHr = this.entityManager.createQuery(qHr).setParameter("reportId", itemData.getReport().getReport().getId()).getResultList();
										for (ReHistoriarelevante_ensayo itemHR : lHr)
											this.entityManager.remove(itemHR);
										List<ReInfoproducto_ensayo> lIp = this.entityManager.createQuery(qIp).setParameter("reportId", itemData.getReport().getReport().getId()).getResultList();
										for (ReInfoproducto_ensayo itemIP : lIp)
											this.entityManager.remove(itemIP);
										List<ReMedicacionconcomitante_ensayo> lMc = this.entityManager.createQuery(qMc).setParameter("reportId", itemData.getReport().getReport().getId()).getResultList();
										for (ReMedicacionconcomitante_ensayo itemMC : lMc)
											this.entityManager.remove(itemMC);
										List<ReMedicacionreciente_ensayo> lMr = this.entityManager.createQuery(qMr).setParameter("reportId", itemData.getReport().getReport().getId()).getResultList();
										for (ReMedicacionreciente_ensayo itemMR : lMr)
											this.entityManager.remove(itemMR);
										List<ReEnfermedadesinter_ensayo> lEi = this.entityManager.createQuery(qEi).setParameter("reportId", itemData.getReport().getReport().getId()).getResultList();
										for (ReEnfermedadesinter_ensayo itemEI : lEi)
											this.entityManager.remove(itemEI);
										this.entityManager.remove(itemData.getReport().getReport());
									} catch (Exception e){
										e.printStackTrace();
									}
								}
								if (itemData.hasNotification()){
									try {
										this.entityManager.remove(itemData.getNotification());
									} catch (Exception e){
										e.printStackTrace();
									}
								}
								if (itemData.hasNoteSite()){
									itemData.getNoteSite().setCid(this.cid);
									itemData.getNoteSite().setEliminado(true);
									List<Nota_ensayo> notesRelated = (List<Nota_ensayo>) this.entityManager.createQuery(qSiteNotesRelated).setParameter("notaPadreId", itemData.getNoteSite().getId()).getResultList();
									for (Nota_ensayo itemNote : notesRelated){
										itemNote.setCid(this.cid);
										itemNote.setEliminado(true);
										itemNote = this.entityManager.merge(itemNote);
									}
									this.entityManager.merge(itemData.getNoteSite());
								}
								if (itemData.hasNoteMonitoring()){
									itemData.getNoteMonitoring().setCid(this.cid);
									itemData.getNoteMonitoring().setEliminado(true);
									List<Nota_ensayo> notesRelated = (List<Nota_ensayo>) this.entityManager.createQuery(qMonitoringNotesRelated).setParameter("notaPadreId", itemData.getNoteMonitoring().getId()).getResultList();
									for (Nota_ensayo itemNote : notesRelated){
										itemNote.setCid(this.cid);
										itemNote.setEliminado(true);
										itemNote = this.entityManager.merge(itemNote);
									}
									this.entityManager.merge(itemData.getNoteMonitoring());
								}
								if (itemData.getVariableDatas() != null && !itemData.getVariableDatas().isEmpty()){
									for (VariableDato_ensayo itemVD : itemData.getVariableDatas()){
										if (itemVD != null){
											itemVD.setCid(this.cid);
											itemVD.setEliminado(true);
											itemVD = this.entityManager.merge(itemVD);
										}
									}
								} else if (itemData.getVariableData() != null){
									itemData.getVariableData().setCid(this.cid);
									itemData.getVariableData().setEliminado(true);
									this.entityManager.merge(itemData.getVariableData());
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean hasAnyGroupWithData(){
		return mapWGD.entrySet().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public void reiniciarHoja(){
		try {
			this.cid = this.bitacora.registrarInicioDeAccion("Reiniciando hoja");
			this.hoja.setCid(this.cid);
			String qMonitoringNotesRelated = "select nota from Nota_ensayo nota where nota.notaPadre.id = :notaPadreId and nota.notaSitio = false and (nota.eliminado = null or nota.eliminado = false)";
			String qSiteNotesRelated = "select nota from Nota_ensayo nota where nota.notaPadre.id = :notaPadreId and nota.notaSitio = true and (nota.eliminado = null or nota.eliminado = false)";
					
			// Borra los grupos
			this.deleteGroup(this.mapBackupWGD);
			
			// Borrar las variables independientes si tiene
			if(this.hasVariables()){
				for (Entry<Long, WrapperGroupData> itemEntry : this.mapWD
						.entrySet()) {
					if (itemEntry.getValue() != null) {
						if (itemEntry.getValue().getData() != null
								&& !itemEntry.getValue().getData().isEmpty() && itemEntry.getValue()
								.getData().values()!=null && !itemEntry.getValue()
										.getData().values().isEmpty()) {
							for (MapWrapperDataPlus itemData : itemEntry.getValue()
									.getData().values()) {
								if (itemData.hasNotification()){
									try {
										this.entityManager.remove(itemData.getNotification());
									} catch (Exception e){
										e.printStackTrace();
									}
								}
								if (itemData.hasNoteSite()){
									itemData.getNoteSite().setCid(this.cid);
									itemData.getNoteSite().setEliminado(true);
									List<Nota_ensayo> notesRelated = (List<Nota_ensayo>) this.entityManager.createQuery(qSiteNotesRelated).setParameter("notaPadreId", itemData.getNoteSite().getId()).getResultList();
									for (Nota_ensayo itemNote : notesRelated){
										itemNote.setCid(this.cid);
										itemNote.setEliminado(true);
										this.entityManager.merge(itemNote);
									}
									this.entityManager.merge(itemData.getNoteSite());
								}
								if (itemData.hasNoteMonitoring()){
									itemData.getNoteMonitoring().setCid(this.cid);
									itemData.getNoteMonitoring().setEliminado(true);
									List<Nota_ensayo> notesRelated = (List<Nota_ensayo>) this.entityManager.createQuery(qMonitoringNotesRelated).setParameter("notaPadreId", itemData.getNoteMonitoring().getId()).getResultList();
									for (Nota_ensayo itemNote : notesRelated){
										itemNote.setCid(this.cid);
										itemNote.setEliminado(true);
										itemNote = this.entityManager.merge(itemNote);
									}
									this.entityManager.merge(itemData.getNoteMonitoring());
								}
								if (itemData.getVariableDatas() != null && !itemData.getVariableDatas().isEmpty()){
									for (VariableDato_ensayo itemVD : itemData.getVariableDatas()){
										if (itemVD != null){
											itemVD.setCid(this.cid);
											itemVD.setEliminado(true);
											itemVD = this.entityManager.merge(itemVD);
										}
									}
								} else if (itemData.getVariableData() != null){
									itemData.getVariableData().setCid(this.cid);
									itemData.getVariableData().setEliminado(true);
									this.entityManager.merge(itemData.getVariableData());
								}
								
							}
						}
					}
				}
			}
			 
			// Cambiar el estado del monitoreo del momento a No iniciado 
			long idMon = 2; 
			this.hoja.setEstadoMonitoreo(this.entityManager.find(EstadoMonitoreo_ensayo.class, idMon)); 
			this.hoja.getMomentoSeguimientoEspecifico().setEstadoMonitoreo(this.entityManager.find(EstadoMonitoreo_ensayo.class, idMon)); 
			
			// Cambiar el estado de la Hoja a No iniciado
			EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) this.entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.codigo = 2").getSingleResult();
			this.hoja.setEstadoHojaCrd(estadoNoIniciada);
			
			// Cambia el estado del Momento de seguimiento a No iniciado
			EstadoMomentoSeguimiento_ensayo estadoNoIniciadaMom = (EstadoMomentoSeguimiento_ensayo) this.entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.codigo = 2").getSingleResult();
			this.hoja.getMomentoSeguimientoEspecifico().setEstadoMomentoSeguimiento(estadoNoIniciadaMom);
			this.entityManager.merge(this.hoja.getMomentoSeguimientoEspecifico());
				
			// Anadir causa 
			
			Causa_ensayo causa=new Causa_ensayo();
			causa.setCid(this.cid);
			causa.setTipoCausa("Reiniciar hoja");
			causa.setDescripcion(descripcionCausaReiniciarHoja);
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					user.getId());
			causa.setUsuario(usuario);
			causa.setFecha(Calendar.getInstance().getTime());
			causa.setEstudio(seguridadEstudio.getEstudioActivo());
			causa.setCrdEspecifico(this.entityManager.find(CrdEspecifico_ensayo.class, this.idcrd));
			causa.setSujeto(this.sujetoIncluido);
			causa.setMomentoSeguimientoEspecifico(this.hoja.getMomentoSeguimientoEspecifico());
			causa.setCronograma(this.hoja.getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getCronograma());
			this.entityManager.persist(causa);			
			this.entityManager.merge(this.hoja);
			this.entityManager.flush();
		} catch (Exception e) {
			this.facesMessages.add(e.getMessage());
		}
	}	
	

	@SuppressWarnings("unchecked")
	public boolean isValorUnico(String valorUnico,Variable_ensayo variable){
		if(variable.getUnica()!=null && variable.getUnica()){
				List<VariableDato_ensayo> valorUnicoLista = this.entityManager.
						createQuery("select vd from VariableDato_ensayo vd "
								+ "inner join vd.crdEspecifico crde "
								+ "inner join crde.momentoSeguimientoEspecifico mse "
								+ "inner join mse.momentoSeguimientoGeneral msg "
								+ "inner join msg.cronograma cronograma "
								+ "inner join cronograma.grupoSujetos gs "
								+ "inner join gs.estudio estudio "
								+ "inner join estudio.estudioEntidads eu "
								+ "where vd.valor=:valor and "
								+ "estudio.id=:idEstudio and "
								+ "eu.entidad.id=:idEntidad ")
						.setParameter("valor", valorUnico)
						.setParameter("idEstudio", this.estudioEntidad.getEstudio().getId())
						.setParameter("idEntidad", this.estudioEntidad.getEntidad().getId())
						.getResultList();
				if (valorUnicoLista.size()>=1) {
					this.facesMessages
							.addToControlFromResourceBundle(
									"idForm",
									Severity.ERROR,
									"Ya existe el valor "+valorUnicoLista.get(0).getValor()+" registrado en el sistema para la variable "+valorUnicoLista.get(0).getVariable().getNombreVariable()+".");
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		
	}

	@SuppressWarnings("unchecked")
	@Transactional
	// Guarda todos los datos de la hoja CRD
	// El coordinador no puede completar la hoja si la misma esta vacia
	// Es usada tambien para persistir los grupos de variables
	public String persistir() throws ParseException, Exception {
			try { 				
				this.cid = this.bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("modificandoHoja"));
				this.hoja.setCid(this.cid);
				Map<String, List<WrapperGroupData>> listGroupsOfVariables = this.mapWGD;
				Map<Long, WrapperGroupData> listvariables = this.mapWD;
				boolean anyvariableingruop = false;
				boolean hasVariables = hasVariables();
				Map<String, List<WrapperGroupData>> groupDataAdded = this
						.groupDataAdded();
				Map<String, List<WrapperGroupData>> groupDataUpdated = this
						.groupDataUpdated();
				Map<Long, List<MapWrapperDataPlus>> variableDataAdded = this
						.variableDataAdded();
				Map<Long, List<MapWrapperDataPlus>> variableDataUpdated = this
						.variableDataUpdated();
				String qCs = "select cs from ReConsecuenciaspaciente_ensayo cs where cs.descripcion = :paramDescription";
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");				
				boolean anyvariable=false;
				if (listGroupsOfVariables.entrySet().size() > 0
						&& this.completada) {
					for (Entry<String, List<WrapperGroupData>> grupo : listGroupsOfVariables
							.entrySet()) {
						if (grupo.getValue() != null
								&& !grupo.getValue().isEmpty()) {
							List<WrapperGroupData> filas = grupo.getValue();
							for (WrapperGroupData fila : filas) {
								if (fila.getData().size() > 0) {
									Map<Long, MapWrapperDataPlus> data = fila
											.getData();
									for (Entry<Long, MapWrapperDataPlus> itemData : data
											.entrySet()) {
										if (itemData.getValue().getValue() != null || itemData.getValue().getValues()!=null) {
											if ((itemData.getValue().getValue() != null
													&& itemData.getValue()
															.getValue()
															.toString() != null
													&& !itemData.getValue()
															.getValue()
															.toString()
															.isEmpty() && itemData
													.getValue().getValue()
													.toString().length() != 0)
													|| (itemData.getValue()
															.getValues() != null && itemData
															.getValue()
															.getValues().length > 0)
													|| (itemData.getValue()
															.getFileName() != null
															&& !itemData
																	.getValue()
																	.getFileName()
																	.isEmpty()
															&& itemData
																	.getValue()
																	.getData() != null && itemData
															.getValue()
															.getData().length > 0)) {
												anyvariableingruop=true;
											}
										}
									}
									if (anyvariableingruop) {
										break;
									}
								}
								if (anyvariableingruop) {
									break;
								}
							}
						}						
					}
				}

				if (hasVariables && this.completada) {
					for (Entry<Long, WrapperGroupData> seccion : listvariables
							.entrySet()) {
						WrapperGroupData seccion2 = seccion.getValue();
						Map<Long, MapWrapperDataPlus> seccion3 = seccion2
								.getData();
						for (Entry<Long, MapWrapperDataPlus> a : seccion3
								.entrySet()) {
							MapWrapperDataPlus variable = a.getValue();
							
							if ((variable.getValue() != null
									&& variable.getValue().toString() != null
									&& !variable.getValue().toString()
											.isEmpty() && variable.getValue()
									.toString().length() != 0)
									|| (variable.getValues() != null && variable
											.getValues().length > 0)
									|| (variable.getFileName() != null
											&& !variable.getFileName()
													.isEmpty()
											&& variable.getData() != null && variable
											.getData().length > 0)) {
								anyvariable=true;
							}

						}

						if (anyvariable) {
							break;
						}
					}
				}
				
				
				if (!hasVariables && listGroupsOfVariables.entrySet().size() == 0 && this.completada) {
					this.facesMessages
							.addToControlFromResourceBundle(
									"idForm",
									Severity.ERROR,
									"Para marcar la hoja como Completada debe tener al menos una variable con datos en la hoja CRD.");
					return null;
				}
				
				
				if (!anyvariable && hasVariables && this.completada && listGroupsOfVariables.entrySet().size()==0) {
					this.facesMessages
							.addToControlFromResourceBundle(
									"idForm",
									Severity.ERROR,
									"Para marcar la hoja como Completada debe tener al menos una variable con datos en la hoja CRD.");
					return null;
				}
				
				if(!anyvariableingruop && listGroupsOfVariables.entrySet().size() > 0 && this.completada){
					this.facesMessages
					.addToControlFromResourceBundle(
							"idForm",
							Severity.ERROR,
							"Para marcar la hoja como Completada debe tener al menos una variable con datos en la hoja CRD.");
						return null;
				}
				
				// Group Data to delete
				this.deleteGroup(this.groupDataDeleted());
				
				// Group Data to add
				List<String> itemsNoValid = new ArrayList<String>();
				for (Entry<String, List<WrapperGroupData>> itemEntry : groupDataAdded.entrySet()){
					if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
						for (WrapperGroupData wgd : itemEntry.getValue()){
							if (wgd.validElements()){
								boolean test=anyVariableWithValueOnGroup();
								if(!test){
									itemsNoValid.add(itemEntry.getKey());									
								} else {
									for (MapWrapperDataPlus itemData : wgd.getData().values()){
										if (itemData.validFullElements()){
											VariableDato_ensayo vd = ((itemData.getVariableData() != null) ? itemData.getVariableData() : new VariableDato_ensayo());
											List<VariableDato_ensayo> vds = new ArrayList<VariableDato_ensayo>();
											if (!itemData.notValued()) {
												if ((itemData.getValue() != null
														&& itemData.getValue()
																.toString() != null && !itemData
														.getValue().toString()
														.isEmpty())
														|| (itemData.getValues() != null && itemData
																.getValues().length > 0)
														|| (itemData.getFileName() != null
																&& !itemData
																		.getFileName()
																		.isEmpty()
																&& itemData
																		.getData() != null && itemData
																.getData().length > 0)) {
													// Verifica que el valor de la variable sea unico para el estudio X en la entidad Y												
													if(itemData.getValue()!=null && itemData.getVariable()!=null &&  this.isValorUnico(itemData.getValue().toString(),itemData.getVariable())){
														return null;
													}
													
													if (itemData.getVariable()
															.getTipoDato()
															.getCodigo()
															.equals("ST")
															|| itemData
																	.getVariable()
																	.getTipoDato()
																	.getCodigo()
																	.equals("BL")
															|| itemData
																	.getVariable()
																	.getTipoDato()
																	.getCodigo()
																	.equals("INT")
															|| itemData
																	.getVariable()
																	.getTipoDato()
																	.getCodigo()
																	.equals("REAL")
															|| (itemData
																	.getVariable()
																	.getTipoDato()
																	.getCodigo()
																	.equals("DATE") && itemData
																	.getVariable()
																	.getPresentacionFormulario()
																	.getNombre()
																	.equals("text"))
															|| (itemData
																	.getVariable()
																	.getTipoDato()
																	.getCodigo()
																	.equals("NOM")
																	&& !itemData
																			.getVariable()
																			.getPresentacionFormulario()
																			.getNombre()
																			.equals("checkbox") && !itemData
																	.getVariable()
																	.getPresentacionFormulario()
																	.getNombre()
																	.equals("multi-select")))
														vd.setValor((itemData
																.getValue() != null) ? ((itemData
																.getVariable()
																.getTipoDato()
																.getCodigo()
																.equals("BL") && Boolean
																.valueOf(itemData
																		.getValue()
																		.toString()) != null) ? (Boolean
																.valueOf(itemData
																		.getValue()
																		.toString()) ? Boolean.TRUE
																.toString()
																: Boolean.FALSE
																		.toString())
																: ((String) itemData
																		.getValue())
																		.trim())
																: null);
													else if (itemData.getVariable()
															.getTipoDato()
															.getCodigo()
															.equals("DATE")
															&& itemData
																	.getVariable()
																	.getPresentacionFormulario()
																	.getNombre()
																	.equals("calendar"))
														vd.setValor((itemData
																.getValue() != null) ? itemData
																.getValue()
																.toString() : null);
													else if (itemData.getVariable().getTipoDato().getCodigo().equals("NOM")
															&& (itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox")
																	|| itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select"))) {
														vd = null;
														if (itemData.getValues() != null && itemData.getValues().length > 0){
															for (int i = 0; i < itemData.getValues().length; i++){
																if (itemData.getValues()[i] != null && itemData.getValues()[i].toString() != null && !itemData.getValues()[i].toString().trim().isEmpty()){
																	VariableDato_ensayo tempVd = new VariableDato_ensayo();
																	tempVd.setValor(((String) itemData.getValues()[i]).trim());
																	vds.add(tempVd);
																}
															}
														}
													} else if (itemData.getVariable().getTipoDato().getCodigo().equals("FILE") && itemData.getFileName() != null && !itemData.getFileName().trim().isEmpty() && itemData.getData() != null && itemData.getData().length > 0){
														String tempName = itemData.getVariable().getId() + itemData.getFileName().trim();
														this.uploadFile(tempName, itemData.getData());
														vd.setValor(tempName);
													}
												}
											}
											if (vds != null && !vds.isEmpty()){
												for (VariableDato_ensayo tempVD : vds){
													tempVD.setCid(this.cid);
													tempVD.setEliminado(false);
													tempVD.setContGrupo(itemData.getRepetition());
													tempVD.setCrdEspecifico(this.hoja);
													tempVD.setVariable(itemData.getVariable());
													tempVD.setUsuario(this.usuario);
													this.entityManager.persist(tempVD);
												}
												itemData.setVariableDatas(vds);
												itemData.setVariableData(itemData.getVariableDatas().get(0));
											} else {
												if (vd == null){
													vd = new VariableDato_ensayo();
													vd.setValor(null);
												}
												vd.setCid(this.cid);
												vd.setEliminado(false);
												vd.setContGrupo(itemData.getRepetition());
												vd.setCrdEspecifico(this.hoja);
												vd.setVariable(itemData.getVariable());
												vd.setUsuario(this.usuario);
												
												this.entityManager.persist(vd);
												itemData.setVariableData(vd);
											}
											if (itemData.hasNoteSite()){
												Nota_ensayo noteSite = itemData.getNoteSite();
												noteSite.setCid(this.cid);
												noteSite.setEliminado(false);
												noteSite.setCrdEspecifico(this.hoja);
												noteSite.setUsuario(this.usuario);
												noteSite.setVariable(itemData.getVariable());
												noteSite.setVariableDato(itemData.getVariableData());
												this.entityManager.persist(noteSite);
												itemData.setNoteSite(noteSite);
												if (itemData.hasNoteSiteResponse()){
													for (Nota_ensayo itemNote : itemData.getNoteSiteResponses()){
														itemNote.setCid(this.cid);
														itemNote.setEliminado(false);
														itemNote.setCrdEspecifico(this.hoja);
														itemNote.setUsuario(this.usuario);
														itemNote.setVariable(itemData.getVariable());
														itemNote.setVariableDato(itemData.getVariableData());
														itemNote.setNotaPadre(noteSite);
														this.entityManager.persist(itemNote);
													}
												}
											}
											if (itemData.hasNoteMonitoring()){
												Nota_ensayo noteMonitoring = itemData.getNoteMonitoring();
												noteMonitoring.setCid(this.cid);
												noteMonitoring.setEliminado(false);
												noteMonitoring.setCrdEspecifico(this.hoja);
												noteMonitoring.setUsuario(this.usuario);
												noteMonitoring.setVariable(itemData.getVariable());
												noteMonitoring.setVariableDato(itemData.getVariableData());
												this.entityManager.persist(noteMonitoring);
												itemData.setNoteMonitoring(noteMonitoring);
												if (itemData.hasNoteMonitoringResponse()){
													for (Nota_ensayo itemNote : itemData.getNoteMonitoringResponses()){
														itemNote.setCid(this.cid);
														itemNote.setEliminado(false);
														itemNote.setCrdEspecifico(this.hoja);
														itemNote.setUsuario(this.usuario);
														itemNote.setVariable(itemData.getVariable());
														itemNote.setVariableDato(itemData.getVariableData());
														itemNote.setNotaPadre(noteMonitoring);
														this.entityManager.persist(itemNote);
													}
												}
											}
											if (itemData.hasNotification()){
												Notificacion_ensayo notification = itemData.getNotification();
												notification.setCrdEspecifico(this.hoja);
												notification.setUsuario(this.usuario);
												notification.setVariable(itemData.getVariable());
												notification.setVariableDato(itemData.getVariableData());
												this.entityManager.persist(notification);
												itemData.setNotification(notification);
											}
											if (itemData.hasReport()){
												WrapperReReporteexpedito report = itemData.getReport();
												if (this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEFaseEstudio() == null && report.getFase() != null){
													this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().setEFaseEstudio(report.getFase());
													this.entityManager.persist(this.seguridadEstudio.getEstudioEntidadActivo().getEstudio());
												}
												report.getReport().setVariableDato(itemData.getVariableData());
												this.entityManager.persist(report.getReport());
												if (report.getConsecuencias() != null && report.getConsecuencias().length > 0){
													for (int i = 0; i < report.getConsecuencias().length; i++){
														if (report.getConsecuencias()[i] != null && !report.getConsecuencias()[i].isEmpty()){
															ReConsecuenciaspaciente_ensayo tempCs = null;
															try {
																tempCs = (ReConsecuenciaspaciente_ensayo) this.entityManager.createQuery(qCs).setParameter("paramDescription", report.getConsecuencias()[i]).getSingleResult();
															} catch (Exception e){
																tempCs = null;
															}
															if (tempCs != null){
																ReHistoriarelevante_ensayo tempHR = new ReHistoriarelevante_ensayo();
																tempHR.setReReporteexpedito(report.getReport());
																tempHR.setIdReEnfermedades(tempCs.getId());
																this.entityManager.persist(tempHR);
															}
														}
													}
												}
												if (report.getProductosEstudios() != null && !report.getProductosEstudios().isEmpty()){
													for (ReMedicacion_ensayo itemRM : report.getProductosEstudios()){
														if (itemRM.getEliminado() == null || !itemRM.getEliminado()){
															ReInfoproducto_ensayo tempIP = new ReInfoproducto_ensayo();
															tempIP.setReReporteexpedito(report.getReport());
															tempIP.setReMedicacion(itemRM);
															this.entityManager.persist(tempIP);
														}
													}
												}
												if (report.getMedicacionConcomitante() != null && !report.getMedicacionConcomitante().isEmpty()){
													for (ReMedicacion_ensayo itemMC : report.getMedicacionConcomitante()){
														if (itemMC.getEliminado() == null || !itemMC.getEliminado()){
															ReMedicacionconcomitante_ensayo tempMC = new ReMedicacionconcomitante_ensayo();
															tempMC.setReReporteexpedito(report.getReport());
															tempMC.setReMedicacion(itemMC);
															this.entityManager.persist(tempMC);
														}
													}
												}
												if (report.getEnfermedadesInter() != null&& !report.getEnfermedadesInter().isEmpty()){
													for (ReMedicacion_ensayo itemEI : report.getEnfermedadesInter()){
														if (itemEI.getEliminado() == null || !itemEI.getEliminado()){
															ReEnfermedadesinter_ensayo tempEI = new ReEnfermedadesinter_ensayo();
															tempEI.setReReporteexpedito(report.getReport());
															tempEI.setReMedicacion(itemEI);
															this.entityManager.persist(tempEI);
														}
													}
												}
												if (report.getMedicacionReciente() != null && !report.getMedicacionReciente().isEmpty()){
													for (ReMedicacion_ensayo itemMR : report.getMedicacionReciente()){
														if (itemMR.getEliminado() == null || !itemMR.getEliminado()){
															ReMedicacionreciente_ensayo tempMR = new ReMedicacionreciente_ensayo();
															tempMR.setReReporteexpedito(report.getReport());
															tempMR.setReMedicacion(itemMR);
															this.entityManager.persist(tempMR);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}										
				}
				if(!itemsNoValid.isEmpty()){
					this.facesMessages.addFromResourceBundle("errorVariablesVacias", new Object());
					if(!groupDataAdded.isEmpty()){
						for(String item : itemsNoValid){
							if(groupDataAdded.containsKey(item)){
								if(this.mapWGD != null && this.mapWGD.containsKey(item)){
									int repetition = (this.mapPositions != null && this.mapPositions.containsKey(item)) ? this.mapPositions.get(item) : -1; 
									int index = this.indexOfGroupData(this.mapWGD.get(item), repetition);
									if (index != -1)
										this.mapWGD.get(item).remove(index);
								}
								groupDataAdded.remove(item);
							}
						}
					}
					return null;
				}

				// Group Data to update
				for (Entry<String, List<WrapperGroupData>> itemEntry : groupDataUpdated.entrySet()){
					if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
						for (WrapperGroupData wgd : itemEntry.getValue()){
							if (wgd.validElements()){
								for (MapWrapperDataPlus itemData : wgd.getData().values()){
									MapWrapperDataPlus itemDataPrevious = this.loadPreviousData(itemEntry.getKey(), wgd.getRepetition(), itemData.getVariable().getId());
									if (itemData.validFullElements() && itemDataPrevious != null){
										VariableDato_ensayo vd = ((itemData.getVariableData() != null) ? itemData.getVariableData() : itemDataPrevious.getVariableData());
										VariableDato_ensayo previousVd = ((itemDataPrevious.getVariableData() != null) ? itemDataPrevious.getVariableData() : null);
										List<VariableDato_ensayo> vds = new ArrayList<VariableDato_ensayo>();
										List<VariableDato_ensayo> previousVds = ((itemDataPrevious.getVariableDatas() != null && !itemDataPrevious.getVariableDatas().isEmpty()) ? itemDataPrevious.getVariableDatas() : null);
										if (!itemData.notValued()){
											if ((itemData.getValue() != null && itemData.getValue().toString() != null && !itemData.getValue().toString().isEmpty()) || (itemData.getValues() != null && itemData.getValues().length > 0) || (itemData.getFileName() != null && !itemData.getFileName().isEmpty() && itemData.getData() != null && itemData.getData().length > 0)){
												// Verifica que el valor de la variable sea unico para el estudio X en la entidad Y												
												if(itemData.getValue()!=null && itemData.getVariable()!=null &&  this.isValorUnico(itemData.getValue().toString(),itemData.getVariable())){
													return null;
												}
												
												if (itemData.getVariable().getTipoDato().getCodigo().equals("ST") || itemData.getVariable().getTipoDato().getCodigo().equals("BL") || itemData.getVariable().getTipoDato().getCodigo().equals("INT") || itemData.getVariable().getTipoDato().getCodigo().equals("REAL") || (itemData.getVariable().getTipoDato().getCodigo().equals("DATE") && itemData.getVariable().getPresentacionFormulario().getNombre().equals("text")) || (itemData.getVariable().getTipoDato().getCodigo().equals("NOM") && !itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox") && !itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select")))
													vd.setValor((itemData.getValue() != null) ? ((itemData.getVariable().getTipoDato().getCodigo().equals("BL") && Boolean.valueOf(itemData.getValue().toString()) != null) ? (Boolean.valueOf(itemData.getValue().toString()) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()) : ((String) itemData.getValue()).trim()) : null);
												else if (itemData.getVariable().getTipoDato().getCodigo().equals("DATE") && itemData.getVariable().getPresentacionFormulario().getNombre().equals("calendar"))
													vd.setValor((itemData.getValue() != null) ? itemData.getValue().toString() : null);
												else if (itemData.getVariable().getTipoDato().getCodigo().equals("NOM") && (itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox") || itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select"))){
													vd = null;
													if (itemData.getValues() != null && itemData.getValues().length > 0){
														for (int i = 0; i < itemData.getValues().length; i++){
															if (itemData.getValues()[i] != null && itemData.getValues()[i].toString() != null && !itemData.getValues()[i].toString().trim().isEmpty()){
																VariableDato_ensayo tempVd = new VariableDato_ensayo();
																tempVd.setValor(((String) itemData.getValues()[i]).trim());
																vds.add(tempVd);
															}
														}
													}
												} else if (itemData.getVariable().getTipoDato().getCodigo().equals("FILE") && itemData.getFileName() != null && !itemData.getFileName().trim().isEmpty() && itemData.getData() != null && itemData.getData().length > 0){
													String tempName = itemData.getVariable().getId() + itemData.getFileName().trim();
													this.uploadFile(tempName, itemData.getData());
													vd.setValor(tempName);
												}
											}
										}
										if (vds != null && !vds.isEmpty()){
											List<VariableDato_ensayo> allVds = new ArrayList<VariableDato_ensayo>();
											List<VariableDato_ensayo> tempVdAdd = this.extractVdsFor(vds, previousVds);
											List<VariableDato_ensayo> tempVdDel = this.extractVdsFor(previousVds, vds);
											List<VariableDato_ensayo> tempVdNone = this.extractVdsForNone(previousVds, vds);
											for (VariableDato_ensayo itemVD : tempVdAdd){
												itemVD.setCid(this.cid);
												itemVD.setEliminado(false);
												itemVD.setContGrupo(itemData.getRepetition());
												itemVD.setCrdEspecifico(this.hoja);
												itemVD.setVariable(itemData.getVariable());
												itemVD.setUsuario(this.usuario);
												this.entityManager.persist(itemVD);
												allVds.add(itemVD);
											}
											for (VariableDato_ensayo itemVD : tempVdDel){
												itemVD.setCid(this.cid);
												itemVD.setEliminado(true);
												itemVD.setContGrupo(itemData.getRepetition());
												itemVD.setCrdEspecifico(this.hoja);
												itemVD.setVariable(itemData.getVariable());
												itemVD.setUsuario(this.usuario);
												itemVD = this.entityManager.merge(itemVD);
											}
											/*for (VariableDato_ensayo itemVD : tempVdNone) {
												itemVD.setCid(this.cid);
												itemVD.setEliminado(false);
												itemVD.setContGrupo(itemData.getRepetition());
												itemVD.setCrdEspecifico(this.hoja);
												itemVD.setVariable(itemData.getVariable());
												itemVD.setUsuario(this.usuario);
												itemVD = this.entityManager.merge(itemVD);
												allVds.add(itemVD);
											}*/
											itemData.setVariableDatas(allVds);
											if(!itemData.getVariableDatas().isEmpty())
											itemData.setVariableData(itemData.getVariableDatas().get(0));
										} else {
											if (previousVd != null){
												previousVd.setValor((!itemData.notValued() && vd != null && vd.getValor() != null && !vd.getValor().trim().isEmpty()) ? vd.getValor().trim() : null);
												previousVd.setCid(this.cid);
												previousVd.setEliminado(false);
												previousVd.setContGrupo(itemData.getRepetition());
												previousVd.setCrdEspecifico(this.hoja);
												previousVd.setVariable(itemData.getVariable());
												previousVd.setUsuario(this.usuario);
												previousVd = this.entityManager.merge(previousVd);
												itemData.setVariableData(previousVd);
											} else {
												if (vd == null){
													vd = new VariableDato_ensayo();
													vd.setValor(null);
												}
												vd.setCid(this.cid);
												vd.setEliminado(false);
												vd.setContGrupo(itemData.getRepetition());
												vd.setCrdEspecifico(this.hoja);
												vd.setVariable(itemData.getVariable());
												vd.setUsuario(this.usuario);
												this.entityManager.persist(vd);
												itemData.setVariableData(vd);
											}
										}
										if (itemData.hasNoteSite()){
											Nota_ensayo noteSite = itemData.getNoteSite();
											noteSite.setCid(this.cid);
											noteSite.setEliminado(false);
											noteSite.setCrdEspecifico(this.hoja);
											noteSite.setUsuario(this.usuario);
											noteSite.setVariable(itemData.getVariable());
											noteSite.setVariableDato(itemData.getVariableData());
											if (!itemDataPrevious.hasNoteSite())
												this.entityManager.persist(noteSite);
											else
												noteSite = this.entityManager.merge(noteSite);
											itemData.setNoteSite(noteSite);
											if (itemData.hasNoteSiteResponse()){
												for (Nota_ensayo itemNote : itemData.getNoteSiteResponses()){
													itemNote.setCid(this.cid);
													itemNote.setEliminado(false);
													itemNote.setCrdEspecifico(this.hoja);
													itemNote.setUsuario(this.usuario);
													itemNote.setVariable(itemData.getVariable());
													itemNote.setVariableDato(itemData.getVariableData());
													itemNote.setNotaPadre(noteSite);
													this.entityManager.persist(itemNote);
												}
											}
										}
										if (itemData.hasNoteMonitoring()){
											Nota_ensayo noteMonitoring = itemData.getNoteMonitoring();
											noteMonitoring.setCid(this.cid);
											noteMonitoring.setEliminado(false);
											noteMonitoring.setCrdEspecifico(this.hoja);
											noteMonitoring.setUsuario(this.usuario);
											noteMonitoring.setVariable(itemData.getVariable());
											noteMonitoring.setVariableDato(itemData.getVariableData());
											if (!itemDataPrevious.hasNoteMonitoring())
												this.entityManager.persist(noteMonitoring);
											else
												noteMonitoring = this.entityManager.merge(noteMonitoring);
											itemData.setNoteMonitoring(noteMonitoring);
											if (itemData.hasNoteMonitoringResponse()){
												for (Nota_ensayo itemNote : itemData.getNoteMonitoringResponses()){
													itemNote.setCid(this.cid);
													itemNote.setEliminado(false);
													itemNote.setCrdEspecifico(this.hoja);
													itemNote.setUsuario(this.usuario);
													itemNote.setVariable(itemData.getVariable());
													itemNote.setVariableDato(itemData.getVariableData());
													itemNote.setNotaPadre(noteMonitoring);
													this.entityManager.persist(itemNote);
												}
											}
										}
										if (itemData.hasNotification()){
											Notificacion_ensayo notification = itemData.getNotification();
											notification.setCrdEspecifico(this.hoja);
											notification.setUsuario(this.usuario);
											notification.setVariable(itemData.getVariable());
											notification.setVariableDato(itemData.getVariableData());
											if (!itemDataPrevious.hasNotification())
												this.entityManager.persist(notification);
											else{
												notification = this.entityManager.merge(this.copyWithSameId(itemDataPrevious.getNotification(), notification));
												itemData.setNotification(notification);
											}
										}
										// if(itemData.hasReport() &&
										// (itemData.getReport().isModified() ||
										// (!itemDataPrevious.hasReport() ||
										// itemData.getReport().hasChanges(itemDataPrevious.getReport())))){
										if (itemData.hasReport()){
											itemData.getReport().getReport().setVariableDato(itemData.getVariableData());
											Long tempReportId = null;
											try {
												if (itemDataPrevious.hasReport() && itemDataPrevious.getReport().getReport().getId() != 0)
													tempReportId = Long.valueOf(itemDataPrevious.getReport().getReport().getId());
											} catch (Exception e){
												tempReportId = null;
											}
											if (tempReportId != null){
												List<ReHistoriarelevante_ensayo> tempListCsDel = this.entityManager.createQuery("select hr from ReHistoriarelevante_ensayo hr where hr.reReporteexpedito.id = :reportId").setParameter("reportId", tempReportId).getResultList();
												List<ReInfoproducto_ensayo> tempListIpDel = this.entityManager.createQuery("select ip from ReInfoproducto_ensayo ip where ip.reReporteexpedito.id = :reportId").setParameter("reportId", tempReportId).getResultList();
												List<ReMedicacionconcomitante_ensayo> tempListMcDel = this.entityManager.createQuery("select mc from ReMedicacionconcomitante_ensayo mc where mc.reReporteexpedito.id = :reportId").setParameter("reportId", tempReportId).getResultList();
												List<ReEnfermedadesinter_ensayo> tempListEiDel = this.entityManager.createQuery("select ei from ReEnfermedadesinter_ensayo ei where ei.reReporteexpedito.id = :reportId").setParameter("reportId", tempReportId).getResultList();
												List<ReMedicacionreciente_ensayo> tempListMrDel = this.entityManager.createQuery("select mr from ReMedicacionreciente_ensayo mr where mr.reReporteexpedito.id = :reportId").setParameter("reportId", tempReportId).getResultList();
												for (ReHistoriarelevante_ensayo itemHR : tempListCsDel)
													this.entityManager.remove(itemHR);
												for (ReInfoproducto_ensayo itemIP : tempListIpDel)
													this.entityManager.remove(itemIP);
												for (ReMedicacionconcomitante_ensayo itemMC : tempListMcDel)
													this.entityManager.remove(itemMC);
												for (ReEnfermedadesinter_ensayo itemEI : tempListEiDel)
													this.entityManager.remove(itemEI);
												for (ReMedicacionreciente_ensayo itemMR : tempListMrDel)
													this.entityManager.remove(itemMR);
											}
											this.entityManager.persist(itemData.getReport().getReport());
											List<ReConsecuenciaspaciente_ensayo> tempListCsAdd = new ArrayList<ReConsecuenciaspaciente_ensayo>(this.extractCs(itemData.getReport().getConsecuencias()));
											for (ReConsecuenciaspaciente_ensayo itemCs : tempListCsAdd){
												ReHistoriarelevante_ensayo tempHR = new ReHistoriarelevante_ensayo();
												tempHR.setReReporteexpedito(itemData.getReport().getReport());
												tempHR.setIdReEnfermedades(itemCs.getId());
												this.entityManager.persist(tempHR);
											}
											for (ReMedicacion_ensayo itemRM : itemData.getReport().getProductosEstudios()){
												ReInfoproducto_ensayo tempIP = new ReInfoproducto_ensayo();
												tempIP.setReReporteexpedito(itemData.getReport().getReport());
												tempIP.setReMedicacion(itemRM);
												this.entityManager.persist(tempIP);
											}
											for (ReMedicacion_ensayo itemRM : itemData.getReport().getMedicacionConcomitante()){
												ReMedicacionconcomitante_ensayo tempMC = new ReMedicacionconcomitante_ensayo();
												tempMC.setReReporteexpedito(itemData.getReport().getReport());
												tempMC.setReMedicacion(itemRM);
												this.entityManager.persist(tempMC);
											}
											for (ReMedicacion_ensayo itemRM : itemData.getReport().getEnfermedadesInter()){
												ReEnfermedadesinter_ensayo tempEI = new ReEnfermedadesinter_ensayo();
												tempEI.setReReporteexpedito(itemData.getReport().getReport());
												tempEI.setReMedicacion(itemRM);
												this.entityManager.persist(tempEI);
											}
											for (ReMedicacion_ensayo itemRM : itemData.getReport().getMedicacionReciente()){
												ReMedicacionreciente_ensayo tempMR = new ReMedicacionreciente_ensayo();
												tempMR.setReReporteexpedito(itemData.getReport().getReport());
												tempMR.setReMedicacion(itemRM);
												this.entityManager.persist(tempMR);
											}
										}
									}
								}
							}
						}
					}
				}

				String variablesP = null;
				// Variable Data to add
				for (Entry<Long, List<MapWrapperDataPlus>> itemEntry : variableDataAdded.entrySet()){
					if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
						List<MapWrapperDataPlus> mwdps = itemEntry.getValue();
						for (MapWrapperDataPlus itemData : mwdps){
							if (itemData.getSection() != null && itemEntry.getKey().equals(itemData.getSection().getId()) && itemData.getVariable() != null && !itemData.notValued()){
								VariableDato_ensayo vd = ((itemData.getVariableData() != null) ? itemData.getVariableData() : new VariableDato_ensayo());
								List<VariableDato_ensayo> vds = new ArrayList<VariableDato_ensayo>();
								if (!itemData.notValued()){
									if ((itemData.getValue() != null && itemData.getValue().toString() != null && !itemData.getValue().toString().isEmpty()) || (itemData.getValues() != null && itemData.getValues().length > 0) || (itemData.getFileName() != null && !itemData.getFileName().isEmpty() && itemData.getData() != null && itemData.getData().length > 0)){
										// Verifica que el valor de la variable sea unico para el estudio X en la entidad Y												
										if(itemData.getValue()!=null && itemData.getVariable()!=null && this.isValorUnico(itemData.getValue().toString(),itemData.getVariable())){
											return null;
										}
										
										if (itemData.getVariable().getTipoDato().getCodigo().equals("ST") || itemData.getVariable().getTipoDato().getCodigo().equals("BL") || itemData.getVariable().getTipoDato().getCodigo().equals("INT") || itemData.getVariable().getTipoDato().getCodigo().equals("REAL") || (itemData.getVariable().getTipoDato().getCodigo().equals("DATE") && itemData.getVariable().getPresentacionFormulario().getNombre().equals("text")) || (itemData.getVariable().getTipoDato().getCodigo().equals("NOM") && !itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox") && !itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select")))
											vd.setValor((itemData.getValue() != null) ? ((itemData.getVariable().getTipoDato().getCodigo().equals("BL") && Boolean.valueOf(itemData.getValue().toString()) != null) ? (Boolean.valueOf(itemData.getValue().toString()) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()) : ((String) itemData.getValue()).trim()) : null);
										else if (itemData.getVariable().getTipoDato().getCodigo().equals("DATE") && itemData.getVariable().getPresentacionFormulario().getNombre().equals("calendar"))
											vd.setValor((itemData.getValue() != null) ? df.format(itemData.getValue()) : null);
										else if (itemData.getVariable().getTipoDato().getCodigo().equals("NOM") && (itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox") || itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select"))){
											vd = null;
											if (itemData.getValues() != null && itemData.getValues().length > 0){
												for (int i = 0; i < itemData.getValues().length; i++){
													if (itemData.getValues()[i] != null && itemData.getValues()[i].toString() != null && !itemData.getValues()[i].toString().trim().isEmpty()){
														VariableDato_ensayo tempVd = new VariableDato_ensayo();
														tempVd.setValor(((String) itemData.getValues()[i]).trim());
														vds.add(tempVd);
													}
												}
											}
										} else if (itemData.getVariable().getTipoDato().getCodigo().equals("FILE") && itemData.getFileName() != null && !itemData.getFileName().trim().isEmpty() && itemData.getData() != null && itemData.getData().length > 0){
											String tempName = itemData .getVariable().getId() + itemData.getFileName().trim();
											this.uploadFile(tempName, itemData.getData());
											vd.setValor(tempName);
										}
									}
								}
								if (vds != null && !vds.isEmpty()){
									for (VariableDato_ensayo tempVD : vds){
										tempVD.setCid(this.cid);
										tempVD.setEliminado(false);
										tempVD.setContGrupo(null);
										tempVD.setCrdEspecifico(this.hoja);
										tempVD.setVariable(itemData.getVariable());
										tempVD.setUsuario(this.usuario);
										this.entityManager.persist(tempVD);
									}
									itemData.setVariableDatas(vds);
									itemData.setVariableData(itemData.getVariableDatas().get(0));
								} else {
									if (vd == null){
										vd = new VariableDato_ensayo();
										vd.setValor(null);
									}
									vd.setCid(this.cid);
									vd.setEliminado(false);
									vd.setContGrupo(null);
									vd.setCrdEspecifico(this.hoja);
									vd.setVariable(itemData.getVariable());
									vd.setUsuario(this.usuario);
									this.entityManager.persist(vd);
									itemData.setVariableData(vd);
								}
							

							if(itemData.getVariable().getCritica() && 
								     (itemData.getVariableData().getValor().isEmpty() || 
										itemData.getVariableData().getValor()== null)){
									variablesP +=  itemData.getVariable().getNombreVariable() + "\n";
								   }
							
								if (itemData.hasNoteSite()) {
									Nota_ensayo noteSite = itemData
											.getNoteSite();
									noteSite.setCid(this.cid);
									noteSite.setEliminado(false);
									noteSite.setCrdEspecifico(this.hoja);
									noteSite.setUsuario(this.usuario);
									noteSite.setVariable(itemData.getVariable());
									noteSite.setVariableDato(itemData.getVariableData());
									this.entityManager.persist(noteSite);
									itemData.setNoteSite(noteSite);
									if (itemData.hasNoteSiteResponse()){
										for (Nota_ensayo itemNote : itemData.getNoteSiteResponses()){
											itemNote.setCid(this.cid);
											itemNote.setEliminado(false);
											itemNote.setCrdEspecifico(this.hoja);
											itemNote.setUsuario(this.usuario);
											itemNote.setVariable(itemData.getVariable());
											itemNote.setVariableDato(itemData.getVariableData());
											itemNote.setNotaPadre(noteSite);
											this.entityManager.persist(itemNote);
										}
									}
								}
								if (itemData.hasNoteMonitoring()){
									Nota_ensayo noteMonitoring = itemData.getNoteMonitoring();
									noteMonitoring.setCid(this.cid);
									noteMonitoring.setEliminado(false);
									noteMonitoring.setCrdEspecifico(this.hoja);
									noteMonitoring.setUsuario(this.usuario);
									noteMonitoring.setVariable(itemData.getVariable());
									noteMonitoring.setVariableDato(itemData.getVariableData());
									this.entityManager.persist(noteMonitoring);
									itemData.setNoteMonitoring(noteMonitoring);
									if (itemData.hasNoteMonitoringResponse()){
										for (Nota_ensayo itemNote : itemData.getNoteMonitoringResponses()){
											itemNote.setCid(this.cid);
											itemNote.setEliminado(false);
											itemNote.setCrdEspecifico(this.hoja);
											itemNote.setUsuario(this.usuario);
											itemNote.setVariable(itemData.getVariable());
											itemNote.setVariableDato(itemData.getVariableData());
											itemNote.setNotaPadre(noteMonitoring);
											this.entityManager.persist(itemNote);
										}
									}
								}
								if (itemData.hasNotification()){
									Notificacion_ensayo notification = itemData.getNotification();
									notification.setCrdEspecifico(this.hoja);
									notification.setUsuario(this.usuario);
									notification.setVariable(itemData.getVariable());
									notification.setVariableDato(itemData.getVariableData());
									notification = this.entityManager.merge(this.copyWithSameId(itemData.getNotification(), notification));
									itemData.setNotification(notification);
								}
							}
						}
					}
				}

				// Variable Data to update
				for (Entry<Long, List<MapWrapperDataPlus>> itemEntry : variableDataUpdated.entrySet()){
					if (itemEntry.getValue() != null && !itemEntry.getValue().isEmpty()){
						List<MapWrapperDataPlus> mwdps = itemEntry.getValue();
						for (MapWrapperDataPlus itemData : mwdps){
							if (itemData.getSection() != null && itemEntry.getKey().equals(itemData.getSection().getId()) && itemData.getVariable() != null){
								MapWrapperDataPlus itemDataPrevious = this.loadPreviousData(itemEntry.getKey(), itemData.getVariable().getId());
								VariableDato_ensayo vd = ((itemData.getVariableData() != null) ? itemData.getVariableData() : new VariableDato_ensayo());
								VariableDato_ensayo previousVd = ((itemDataPrevious != null && itemDataPrevious.getVariableData() != null) ? itemDataPrevious.getVariableData() : null);
								List<VariableDato_ensayo> vds = new ArrayList<VariableDato_ensayo>();
								List<VariableDato_ensayo> previousVds = ((itemDataPrevious != null && itemDataPrevious.getVariableDatas() != null && !itemDataPrevious.getVariableDatas().isEmpty()) ? itemDataPrevious.getVariableDatas() : null);
								if (!itemData.notValued()){
									if ((itemData.getValue() != null && itemData.getValue().toString() != null && !itemData.getValue().toString().isEmpty()) || (itemData.getValues() != null && itemData.getValues().length > 0) || (itemData.getFileName() != null && !itemData.getFileName().isEmpty() && itemData.getData() != null && itemData.getData().length > 0)){
										// Verifica que el valor de la variable sea unico para el estudio X en la entidad Y												
										if(itemData.getValue()!=null && itemData.getVariable()!=null && this.isValorUnico(itemData.getValue().toString(),itemData.getVariable())){
											return null;
										}
										
										if (itemData.getVariable().getTipoDato().getCodigo().equals("ST") || itemData.getVariable().getTipoDato().getCodigo().equals("BL") || itemData.getVariable().getTipoDato().getCodigo().equals("INT") || itemData.getVariable().getTipoDato().getCodigo().equals("REAL") || (itemData.getVariable().getTipoDato().getCodigo().equals("DATE") && itemData.getVariable().getPresentacionFormulario().getNombre().equals("text")) || (itemData.getVariable().getTipoDato().getCodigo().equals("NOM") && !itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox") && !itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select")))
											vd.setValor((itemData.getValue() != null) ? ((itemData.getVariable().getTipoDato().getCodigo().equals("BL") && Boolean.valueOf(itemData.getValue().toString()) != null) ? (Boolean.valueOf(itemData.getValue().toString()) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()) : ((String) itemData.getValue()).trim()) : null);
										else if (itemData.getVariable().getTipoDato().getCodigo().equals("DATE") && itemData.getVariable().getPresentacionFormulario().getNombre().equals("calendar"))
											vd.setValor((itemData.getValue() != null) ? df.format(itemData.getValue()) : null);
										else if (itemData.getVariable().getTipoDato().getCodigo().equals("NOM") && (itemData.getVariable().getPresentacionFormulario().getNombre().equals("checkbox") || itemData.getVariable().getPresentacionFormulario().getNombre().equals("multi-select"))){
											vd = null;
											if (itemData.getValues() != null && itemData.getValues().length > 0){
												for (int i = 0; i < itemData.getValues().length; i++){
													VariableDato_ensayo tempVd = new VariableDato_ensayo();
													tempVd.setValor((String) itemData.getValues()[i]);
													vds.add(tempVd);
												}
											}
										} else if (itemData.getVariable().getTipoDato().getCodigo().equals("FILE") && itemData.getFileName() != null && !itemData.getFileName().trim().isEmpty()){
											String tempName = itemData.getVariable().getId() + itemData.getFileName().trim();
											this.uploadFile(tempName, itemData.getData());
											vd.setValor(tempName);
										}
									}
								}
								if (vds != null && !vds.isEmpty()){
									List<VariableDato_ensayo> allVds = new ArrayList<VariableDato_ensayo>();
									List<VariableDato_ensayo> tempVdAdd = this.extractVdsFor(vds, previousVds);
									List<VariableDato_ensayo> tempVdDel = this.extractVdsFor(previousVds, vds);
									List<VariableDato_ensayo> tempVdNone = this.extractVdsForNone(previousVds, vds);
									for (VariableDato_ensayo itemVD : tempVdAdd){
										itemVD.setCid(this.cid);
										itemVD.setEliminado(false);
										itemVD.setContGrupo(null);
										itemVD.setCrdEspecifico(this.hoja);
										itemVD.setVariable(itemData.getVariable());
										itemVD.setUsuario(this.usuario);
										this.entityManager.persist(itemVD);
										allVds.add(itemVD);
									}
									for (VariableDato_ensayo itemVD : tempVdDel){
										itemVD.setCid(this.cid);
										itemVD.setEliminado(true);
										itemVD.setContGrupo(null);
										itemVD.setCrdEspecifico(this.hoja);
										itemVD.setVariable(itemData.getVariable());
										itemVD.setUsuario(this.usuario);
										itemVD = this.entityManager.merge(itemVD);
									}
									for (VariableDato_ensayo itemVD : tempVdNone){
										itemVD.setCid(this.cid);
										itemVD.setEliminado(false);
										itemVD.setContGrupo(null);
										itemVD.setCrdEspecifico(this.hoja);
										itemVD.setVariable(itemData.getVariable());
										itemVD.setUsuario(this.usuario);
										itemVD = this.entityManager.merge(itemVD);
										allVds.add(itemVD);
									}
									itemData.setVariableDatas(allVds);
									if(!itemData.getVariableDatas().isEmpty())
									itemData.setVariableData(itemData.getVariableDatas().get(0));
								} else {
									if (previousVd != null){
										previousVd.setValor((!itemData.notValued() && vd != null && vd.getValor() != null && !vd.getValor().trim().isEmpty()) ? vd.getValor().trim() : null);
										previousVd.setCid(this.cid);
										previousVd.setEliminado(false);
										previousVd.setContGrupo(null);
										previousVd.setCrdEspecifico(this.hoja);
										previousVd.setVariable(itemData.getVariable());
										previousVd.setUsuario(this.usuario);
										previousVd = this.entityManager.merge(previousVd);
										itemData.setVariableData(previousVd);
									} else {
										if (vd == null){
											vd = new VariableDato_ensayo();
											vd.setValor(null);
										}
										vd.setCid(this.cid);
										vd.setEliminado(false);
										vd.setContGrupo(null);
										vd.setCrdEspecifico(this.hoja);
										vd.setVariable(itemData.getVariable());
										vd.setUsuario(this.usuario);
										this.entityManager.persist(vd);
										itemData.setVariableData(vd);
									}
								}
								
								if(itemData.getVariable().getCritica() && itemData.getVariableData().getValor()== null){
										variablesP += itemData.getVariable().getNombreVariable() + "\n";
								}
								
								if (itemData.hasNoteSite()) {
									Nota_ensayo noteSite = itemData
											.getNoteSite();
									noteSite.setCid(this.cid);
									noteSite.setEliminado(false);
									noteSite.setCrdEspecifico(this.hoja);
									noteSite.setUsuario(this.usuario);
									noteSite.setVariable(itemData.getVariable());
									noteSite.setVariableDato(itemData.getVariableData());
									if (!itemDataPrevious.hasNoteSite())
										this.entityManager.persist(noteSite);
									else
										noteSite = this.entityManager.merge(noteSite);
									itemData.setNoteSite(noteSite);
									if (itemData.hasNoteSiteResponse()){
										for (Nota_ensayo itemNote : itemData.getNoteSiteResponses()){
											itemNote.setCid(this.cid);
											itemNote.setEliminado(false);
											itemNote.setCrdEspecifico(this.hoja);
											itemNote.setUsuario(this.usuario);
											itemNote.setVariable(itemData.getVariable());
											itemNote.setVariableDato(itemData.getVariableData());
											itemNote.setNotaPadre(noteSite);
											this.entityManager.persist(itemNote);
										}
									}
								}
								if (itemData.hasNoteMonitoring()){
									Nota_ensayo noteMonitoring = itemData.getNoteMonitoring();
									noteMonitoring.setCid(this.cid);
									noteMonitoring.setEliminado(false);
									noteMonitoring.setCrdEspecifico(this.hoja);
									noteMonitoring.setUsuario(this.usuario);
									noteMonitoring.setVariable(itemData.getVariable());
									noteMonitoring.setVariableDato(itemData.getVariableData());
									if (!itemDataPrevious.hasNoteMonitoring())
										this.entityManager.persist(noteMonitoring);
									else
										noteMonitoring = this.entityManager.merge(noteMonitoring);
									itemData.setNoteMonitoring(noteMonitoring);
									if (itemData.hasNoteMonitoringResponse()){
										for (Nota_ensayo itemNote : itemData.getNoteMonitoringResponses()){
											itemNote.setCid(this.cid);
											itemNote.setEliminado(false);
											itemNote.setCrdEspecifico(this.hoja);
											itemNote.setUsuario(this.usuario);
											itemNote.setVariable(itemData.getVariable());
											itemNote.setVariableDato(itemData.getVariableData());
											itemNote.setNotaPadre(noteMonitoring);
											this.entityManager.persist(itemNote);
										}
									}
								}
								if (itemData.hasNotification()){
									Notificacion_ensayo notification = itemData.getNotification();
									notification.setCrdEspecifico(this.hoja);
									notification.setUsuario(this.usuario);
									notification.setVariable(itemData.getVariable());
									notification.setVariableDato(itemData.getVariableData());
									if (!itemDataPrevious.hasNotification())
										this.entityManager.persist(notification);
									else {
										notification = this.entityManager.merge(this.copyWithSameId(itemData.getNotification(), notification));
										itemData.setNotification(notification);
									}
										
								}
							}
						}
					}
				}
				if (this.pesquisajeHabilitado() && this.obtenerMomentoPesquisaje()){
					GrupoSujetos_ensayo grupoViejo = this.sujetoIncluido.getGrupoSujetos();
					EstadoInclusion_ensayo estadoTratam = (EstadoInclusion_ensayo) this.entityManager.createQuery("select est from EstadoInclusion_ensayo est where est.codigo = 4").getSingleResult();
					EstadoInclusion_ensayo estadoTratamNoIn = (EstadoInclusion_ensayo) this.entityManager.createQuery("select est from EstadoInclusion_ensayo est where est.codigo = 3").getSingleResult();
					if (this.mapWD != null && !this.mapWD.isEmpty()){
						for (WrapperGroupData wgd : this.mapWD.values()){
							if (wgd != null && wgd.validPartialElements()){
								for (MapWrapperDataPlus itemData : wgd.getData().values()){
									if (itemData.getValue() != null && itemData.getValue().toString() != null && !itemData.getValue().toString().isEmpty()){
										if ((itemData.getValue().toString().equals("incluido") || itemData.getValue().toString().equals("Incluido")) && itemData.getVariable().getDescripcionVariable().equals("Resultados Pesquisaje"))
											this.sujetoIncluido.setEstadoInclusion(estadoTratam);
										if ((itemData.getValue().toString().equals("No incluido") || itemData.getValue().toString().equals("No Incluido")) && itemData.getVariable().getDescripcionVariable().equals("Resultados Pesquisaje"))
											this.sujetoIncluido.setEstadoInclusion(estadoTratamNoIn);
										if (itemData.getVariable().getDescripcionVariable().equals(SeamResourceBundle.getBundle().getString("horaInclusion")))
											this.sujetoIncluido.setNombre(itemData.getValue().toString());
										if (itemData.getVariable().getDescripcionVariable().equals(SeamResourceBundle.getBundle().getString("fechaInclusion"))){
											String fechaAux = null;
											try {
												fechaAux = new SimpleDateFormat("dd/MM/yyyy").format((Date) itemData.getValue());
											} catch (Exception e){
												fechaAux = null;
											}
											if (fechaAux != null)
												this.sujetoIncluido.setFechaInclucion(fechaAux);
										}
										if (itemData.getVariable().getDescripcionVariable().equals(SeamResourceBundle.getBundle().getString("numeroInlcusion"))){
											Long numero = this.sujetoIncluido.getNumeroInclucion();
											this.sujetoIncluido.setNumeroInclucion(Long.parseLong(itemData.getValue().toString()));
											if (numero == null)
												this.sujetoIncluido.setCodigoPaciente(this.sujetoIncluido.getCodigoPaciente() + "_" + this.sujetoIncluido.getNumeroInclucion());
											else {
												String[] numeroArreglo = this.sujetoIncluido.getCodigoPaciente().split("_");
												String codigo = "";
												codigo = numeroArreglo[0] + "_" + numeroArreglo[1] + "_" + numeroArreglo[2] + "_" + numeroArreglo[3] + "_" + this.sujetoIncluido.getNumeroInclucion();
												this.sujetoIncluido.setCodigoPaciente(codigo);
											}
										}
										if (itemData.getVariable().getDescripcionVariable().equals("Grupo de sujetos")){
											GrupoSujetos_ensayo subjectGroup = this.groupByName(itemData.getValue().toString());
											if (subjectGroup != null  && subjectGroup.getHabilitado())
												this.sujetoIncluido.setGrupoSujetos(subjectGroup);
										}
									}
								}
							}
						}
					}
					this.sujetoIncluido = this.entityManager.merge(this.sujetoIncluido);

					Cronograma_ensayo cronograma_General = (Cronograma_ensayo) this.entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id = :id").setParameter("id", this.sujetoIncluido.getGrupoSujetos().getId()).getSingleResult(); 
					// Aqui tengo el listado de los momento de seguimientos general 
					String dia = "0"; 
					Integer diaEsp = 0;
					
					// Visualizar las hojas CRD en los momentos 0
					List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral = (List<MomentoSeguimientoGeneral_ensayo>) this.entityManager.createQuery("select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id = :id and msg.programado = true and msg.dia = :eva1 and (msg.eliminado = null or msg.eliminado = false)").setParameter("eva1", dia).setParameter("id", cronograma_General.getId()).getResultList(); 
					//Dias Planificados 
					List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneralDias = entityManager 
							.createQuery( 
									"select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id=:id and msg.programado=True") 
							.setParameter("id", cronograma_General.getId()).getResultList(); 
					for (int i = 0; i < listadoMomentosGeneralDias.size(); i++) { 
						MomentoSeguimientoGeneral_ensayo ms = listadoMomentosGeneralDias 
								.get(i); 
						if ((ms.getEliminado() != true) && (!ms.getDia().equals("0"))) { 
							List<String> listaDiasPlanificados = picarCadena(ms); 
							if(listaDiasPlanificados.contains("0")){ 
								listadoMomentosGeneral.add(ms); 
							} 
						} 
					} 
	 
					//Fin Dias Planificados 
					MomentoSeguimientoEspecifico_ensayo momentoEvaluacion =  new MomentoSeguimientoEspecifico_ensayo(); 
					for (int i = 0; i < listadoMomentosGeneral.size(); i++) { 
						try { 
							 momentoEvaluacion = (MomentoSeguimientoEspecifico_ensayo) this.entityManager.createQuery("select crd from MomentoSeguimientoEspecifico_ensayo crd where crd.sujeto.id = :idSujeto and (crd.eliminado = null or crd.eliminado = false) and crd.momentoSeguimientoGeneral.id = :eva1 and crd.dia = :dia").setParameter("eva1", listadoMomentosGeneral.get(i).getId()).setParameter("dia", diaEsp).setParameter("idSujeto", this.sujetoIncluido.getId()).getSingleResult(); 
						} catch (Exception e){ 
							momentoEvaluacion = null; 
						} 
						if (this.sujetoIncluido.getEstadoInclusion().getCodigo() == 4 && momentoEvaluacion == null){ 
							long idEstadoSeguimiento = 2; 
							EstadoMomentoSeguimiento_ensayo estadoMomento = this.entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimiento); 
							EstadoMonitoreo_ensayo estadoMonitoreo = this.entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento); 
							List<HojaCrd_ensayo> listaHojas = this.entityManager.createQuery("select momentoGhojaCRD.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoGhojaCRD where momentoGhojaCRD.momentoSeguimientoGeneral.id = :id and (momentoGhojaCRD.eliminado = null or momentoGhojaCRD.eliminado = false)").setParameter("id", listadoMomentosGeneral.get(i).getId()).getResultList(); 
							MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo(); 
							Calendar cal = Calendar.getInstance(); 
							momentoEspecifico.setCid(this.bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("momentoCrear"))); 
							momentoEspecifico.setDia(0);  
							momentoEspecifico.setEliminado(false); 
							momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomento); 
							momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo); 
							momentoEspecifico.setFechaCreacion(cal.getTime()); 
							Date fechaAux = null; 
							if (this.sujetoIncluido.getFechaInclucion() != null && !this.sujetoIncluido.getFechaInclucion().isEmpty()){ 
								try { 
									fechaAux = new SimpleDateFormat("dd/MM/yyyy").parse(this.sujetoIncluido.getFechaInclucion()); 
								} catch (Exception e){ 
									fechaAux = null; 
								} 
							} 
							if (fechaAux != null) 
								momentoEspecifico.setFechaInicio(fechaAux); 
							momentoEspecifico.setUsuario(this.usuario); 
							cal.add(Calendar.DATE, listadoMomentosGeneral.get(i).getTiempoLlenado()); 
							momentoEspecifico.setFechaFin(cal.getTime()); 
							momentoEspecifico.setMomentoSeguimientoGeneral(listadoMomentosGeneral.get(i)); 
							momentoEspecifico.setSujeto(this.sujetoIncluido); 
							this.entityManager.persist(momentoEspecifico); 
							String noIniciada = "No iniciada"; 
							EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) this.entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.nombre = :noIniciada").setParameter("noIniciada", noIniciada).getSingleResult(); 
							for (int j2 = 0; j2 < listaHojas.size(); j2++){ 
								CrdEspecifico_ensayo crdEsp = new CrdEspecifico_ensayo(); 
								crdEsp.setCid(this.bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("hojaCrear"))); 
								crdEsp.setEliminado(false); 
								crdEsp.setEstadoHojaCrd(listaHojas.get(j2).getEstadoHojaCrd()); 
								crdEsp.setEstadoMonitoreo(estadoMonitoreo); 
								crdEsp.setHojaCrd(listaHojas.get(j2)); 
								crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico); 
								crdEsp.setEstadoHojaCrd(estadoNoIniciada); 
								this.entityManager.persist(crdEsp); 
							} 
						} else if ((this.obtainRole() != null && (this.rolLogueado.getCodigo().equals("ecCord") || this.rolLogueado.getCodigo().equals("ecInv"))) && momentoEvaluacion != null && this.sujetoIncluido.getEstadoInclusion().getCodigo() == 4){ 
							Date fechaAux = null; 
							if (this.sujetoIncluido.getFechaInclucion() != null && !this.sujetoIncluido.getFechaInclucion().isEmpty()){ 
								try { 
									fechaAux = new SimpleDateFormat("dd/MM/yyyy").parse(this.sujetoIncluido.getFechaInclucion()); 
								} catch (Exception e){ 
									fechaAux = null; 
								} 
							} 
							if (fechaAux != null) 
								momentoEvaluacion.setFechaInicio(fechaAux); 
							this.entityManager.persist(momentoEvaluacion); 
						} else if (momentoEvaluacion != null && this.sujetoIncluido.getEstadoInclusion().getCodigo() == 4 && this.sujetoIncluido.getGrupoSujetos().getId() != grupoViejo.getId()){ 
							// Busco el cronograma que le corresponde al sujeto. 
							momentoEvaluacion.setEliminado(true); 
							this.entityManager.persist(momentoEvaluacion); 
							List<CrdEspecifico_ensayo> lista = (List<CrdEspecifico_ensayo>) this.entityManager.createQuery("select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id = :id and (crd.eliminado = null or crd.eliminado = false)").setParameter("id", momentoEvaluacion.getId()).getResultList(); 
							for (int k = 0; k < lista.size(); k++){ 
								lista.get(k).setEliminado(true); 
								this.entityManager.merge(lista.get(k)); 
							} 
							long idEstadoSeguimiento = 2; 
							EstadoMomentoSeguimiento_ensayo estadoMomento = this.entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimiento); 
							EstadoMonitoreo_ensayo estadoMonitoreo = this.entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento); 
							List<HojaCrd_ensayo> listaHojas = this.entityManager.createQuery("select momentoGhojaCRD.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoGhojaCRD where momentoGhojaCRD.momentoSeguimientoGeneral.id = :id and (momentoGhojaCRD.eliminado = null or momentoGhojaCRD.eliminado = false)").setParameter("id", listadoMomentosGeneral.get(i).getId()).getResultList(); 
							MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo(); 
							Calendar cal = Calendar.getInstance(); 
							momentoEspecifico.setCid(this.bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("momentoCrear"))); 
							//momentoEspecifico.setDia(Integer.valueOf(listadoMomentosGeneral.get(i).getDia())); 
							momentoEspecifico.setDia(0); 
							momentoEspecifico.setEliminado(false); 
							momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomento); 
							momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo); 
							momentoEspecifico.setFechaCreacion(cal.getTime()); 
							Date fechaAux = null; 
							if (this.sujetoIncluido.getFechaInclucion() != null && !this.sujetoIncluido.getFechaInclucion().isEmpty()){ 
								try { 
									fechaAux = new SimpleDateFormat("dd/MM/yyyy").parse(this.sujetoIncluido.getFechaInclucion()); 
								} catch (Exception e){ 
									fechaAux = null; 
								} 
							} 
							if (fechaAux != null) 
								momentoEvaluacion.setFechaInicio(fechaAux); 
							cal.add(Calendar.DATE, listadoMomentosGeneral.get(i).getTiempoLlenado()); 
							momentoEspecifico.setFechaFin(cal.getTime()); 
							momentoEspecifico.setUsuario(this.usuario); 
							momentoEspecifico.setMomentoSeguimientoGeneral(listadoMomentosGeneral.get(i)); 
							momentoEspecifico.setSujeto(this.sujetoIncluido); 
							this.entityManager.persist(momentoEspecifico); 
							String noIniciada = "No iniciada"; 
							EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) this.entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.nombre = :noIniciada").setParameter("noIniciada", noIniciada).getSingleResult(); 
							for (int j2 = 0; j2 < listaHojas.size(); j2++){ 
								CrdEspecifico_ensayo crdEsp = new CrdEspecifico_ensayo(); 
								crdEsp.setCid(this.bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("hojaCrear"))); 
								crdEsp.setEliminado(false); 
								crdEsp.setEstadoHojaCrd(listaHojas.get(j2).getEstadoHojaCrd()); 
								crdEsp.setEstadoMonitoreo(estadoMonitoreo); 
								crdEsp.setHojaCrd(listaHojas.get(j2)); 
								crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico); 
								crdEsp.setEstadoHojaCrd(estadoNoIniciada); 
								this.entityManager.persist(crdEsp); 
							} 
						} 
					} 					 
				} 
				
				if (this.hoja.getEstadoHojaCrd().getCodigo() == 2 && !this.isMonitor()){
					EstadoHojaCrd_ensayo estadoIniciada = (EstadoHojaCrd_ensayo) this.entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.codigo = 1").getSingleResult();
					this.hoja.setEstadoHojaCrd(estadoIniciada);
					if (this.hoja.getMomentoSeguimientoEspecifico().getEstadoMomentoSeguimiento().getCodigo() == 2 || this.hoja.getMomentoSeguimientoEspecifico().getEstadoMomentoSeguimiento().getCodigo() == 4){
						EstadoMomentoSeguimiento_ensayo estadoIniciadaMom = (EstadoMomentoSeguimiento_ensayo) this.entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.codigo = 1").getSingleResult();
						this.hoja.getMomentoSeguimientoEspecifico().setEstadoMomentoSeguimiento(estadoIniciadaMom);
						this.entityManager.merge(this.hoja.getMomentoSeguimientoEspecifico());
					}
				}
				if (this.completada && this.hoja.getEstadoHojaCrd().getCodigo() != 3 && this.hoja.getEstadoHojaCrd().getCodigo() != 4){
					EstadoHojaCrd_ensayo estadoCompletada = (EstadoHojaCrd_ensayo) this.entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.codigo = 3").getSingleResult();
					this.hoja.setEstadoHojaCrd(estadoCompletada);
				}
				if (this.isMonitor()){
					if (!this.monitoringNotStarted && this.hoja.getEstadoMonitoreo().getNombre().equals("No iniciado")){
						long idMon = 1;
						this.hoja.setEstadoMonitoreo(this.entityManager.find(EstadoMonitoreo_ensayo.class, idMon));
						this.hoja.getMomentoSeguimientoEspecifico().setEstadoMonitoreo(this.entityManager.find(EstadoMonitoreo_ensayo.class, idMon));
						this.entityManager.persist(this.hoja.getMomentoSeguimientoEspecifico());
					}
					if (this.monitoringCompleted && !this.hoja.getEstadoMonitoreo().getNombre().equals("Completado")){
						long idEstadoSeguimiento = 3;
						EstadoMonitoreo_ensayo estadoMonitoreo = this.entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
						this.hoja.setEstadoMonitoreo(estadoMonitoreo);
						this.wrapperMomento.cambiarEstadoMonitoreoACompletado(this.hoja.getMomentoSeguimientoEspecifico());
					} else if (!this.monitoringCompleted && this.hoja.getEstadoMonitoreo().getNombre().equals("Completado")){
						List<Nota_ensayo> lista = (List<Nota_ensayo>) this.entityManager.createQuery("select nota from Nota_ensayo nota where nota.crdEspecifico.id = :hojaId and nota.notaSitio = false and (nota.eliminado = null or nota.eliminado = false) and nota.notaPadre = null").setParameter("hojaId", this.hoja.getId()).getResultList();
						long idEstadoSeguimientoInic = 1;
						long idEstadoSeguimientoNoInic = 2;
						EstadoMonitoreo_ensayo estadoMonitoreo = this.entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimientoInic);
						EstadoMonitoreo_ensayo estadoMonitoreoNoInic = this.entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimientoNoInic);
						if (lista.size() != 0){
							this.hoja.setEstadoMonitoreo(estadoMonitoreo);
							this.wrapperMomento.cambiarEstadoMonitoreoAIniciado(this.hoja.getMomentoSeguimientoEspecifico());
						} else {
							this.hoja.setEstadoMonitoreo(estadoMonitoreoNoInic);
							this.wrapperMomento.cambiarEstadoMonitoreoANoIniciado(this.hoja.getMomentoSeguimientoEspecifico());
						}
						long cid = this.bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrearCausa"));
						Causa_ensayo causa = new Causa_ensayo();
						causa.setCid(cid);
						Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
								user.getId());
						causa.setUsuario(usuario);
						causa.setEstudio(seguridadEstudio.getEstudioActivo().getDiccionario().getId());
						causa.setCronograma(this.hoja.getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getCronograma());
						causa.setMomentoSeguimientoEspecifico(this.hoja.getMomentoSeguimientoEspecifico());
						causa.setCrdEspecifico(this.entityManager.find(CrdEspecifico_ensayo.class, this.idcrd));
						causa.setSujeto(this.sujetoIncluido);
						causa.setDescripcion(this.causaDescMonitoreo);
						Calendar cal = Calendar.getInstance();
						causa.setFecha(cal.getTime());
						causa.setTipoCausa(SeamResourceBundle.getBundle().getString("bitCrearCausa"));
						this.entityManager.persist(causa);
					}
				}
				this.hoja = this.entityManager.merge(this.hoja);
				this.wrapperMomento.cambiarEstadoACompletado(this.hoja.getMomentoSeguimientoEspecifico());
				this.wrapperMomento.cambiarEstadoTratamientoSuj(this.hoja.getMomentoSeguimientoEspecifico());
				this.entityManager.flush();
				this.refillData();
				if(this.completada /*&& estudioE.getEmailContacto()!= null*/){
					List<String> destin = new ArrayList<String>();
				    destin.add(/*estudioE.getEmailContacto()*/"aasantos@uci.cu");
				    String asunto= "Variable critica vacía";
				    String menssage = "Después del completamiento de la hoja " + this.hoja.getHojaCrd().getNombreHoja() + " del momento de seguimiento "
				    		+ this.hoja.getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre() + " del estudio " + this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getNombre() +
				    		" se detectaron la(s) siguiente(s) variable(s) critica(s) vacia(s): \n" + variablesP;
				mail.SendNot(destin, asunto, menssage);
				}
				return "ok";
			} catch (Exception e){
				this.facesMessages.add(e.getMessage());
				return null;
			}
		}
		
	
	
	// Codigo para picar la cadena de dias by Evelio. 
				private List<String> picarCadena(MomentoSeguimientoGeneral_ensayo ms) { 
					String dias = ms.getDia(); 
					String[] listaDiasSelecc; 
					String[] listaUltDiaSelecc; 
					List<String> listaDiasPlanificados = new ArrayList<String>(); 
					String y = "y"; 
					if (dias.contains(",")) { 
						listaDiasSelecc = dias.split(", "); 
						listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1] 
								.split(" " + y + " "); 
						for (int j = 0; j < listaDiasSelecc.length - 1; j++) { 
							listaDiasPlanificados.add(listaDiasSelecc[j]); 
						} 
						listaDiasPlanificados.add(listaUltDiaSelecc[0]); 
						listaDiasPlanificados.add(listaUltDiaSelecc[1]); 
	 
					} else if (dias.contains(y)) { 
						listaUltDiaSelecc = dias.split(" " + y + " "); 
						listaDiasPlanificados.add(listaUltDiaSelecc[0]); 
						listaDiasPlanificados.add(listaUltDiaSelecc[1]); 
	 
					} else { 
						listaDiasPlanificados.add(dias); 
	 
					} 
					return listaDiasPlanificados; 
				} 

	public GrupoSujetos_ensayo groupByName(String groupName){
		GrupoSujetos_ensayo grup = null;
		for (int i = 0; i < this.listarGrupo.size(); i++){
			if (this.listarGrupo.get(i).getNombreGrupo().equals(groupName)){
				grup = this.listarGrupo.get(i);
				break;
			}
		}
		return grup;
	}
	
	private Notificacion_ensayo copyWithSameId(Notificacion_ensayo original, Notificacion_ensayo newNotification){
		original.settituloEst(newNotification.gettituloEst());
		original.setcodigoEst(newNotification.getcodigoEst());
		original.setidentSujeto(newNotification.getidentSujeto());
		original.setedadSuj(newNotification.getedadSuj());
		original.setTipoEdad(newNotification.getTipoEdad());
		original.setprodInv(newNotification.getprodInv());
		original.setsitioInv(newNotification.getsitioInv());
		original.setinvestigador(newNotification.getinvestigador());
		original.setpromotor(newNotification.getpromotor());
		original.setfechaOcu(newNotification.getfechaOcu());
		original.setfechaUltA(newNotification.getfechaUltA());
		original.setnotifico(newNotification.getnotifico());
		original.setfecha_notifico(newNotification.getfecha_notifico());
		original.setdescrEven(newNotification.getdescrEven());
		original.setaccTomada(newNotification.getaccTomada());
		original.setnombreN(newNotification.getnombreN());
		original.setapellidosN(newNotification.getapellidosN());
		original.setapellidosN(newNotification.getapellidosN());
		original.setcargoN(newNotification.getcargoN());
		original.setdireccN(newNotification.getdireccN());
		original.settelefonoN(newNotification.gettelefonoN());
		original.setopiInves(newNotification.getopiInves());
		original.setVariable(newNotification.getVariable());
		original.setVariableDato(newNotification.getVariableDato());
		original.setCrdEspecifico(newNotification.getCrdEspecifico());
		original.setIdSujeto(newNotification.getIdSujeto());
		original.setIdEstudio(newNotification.getIdEstudio());
		original.setIdEntidad(newNotification.getIdEntidad());
		original.setViaNoti(newNotification.getViaNoti());
		original.setUsuario(newNotification.getUsuario());
		return original;
	}

	@SuppressWarnings("unchecked")
	public void listarGrupo(){
		if (this.listarGrupo == null || this.listarGrupo.isEmpty())
			this.listarGrupo = (List<GrupoSujetos_ensayo>) this.entityManager.createQuery("select g from GrupoSujetos_ensayo g where g.estudio.id = :estudioId and g.habilitado = true and (g.eliminado = null or g.eliminado = false)").setParameter("estudioId", this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
	}

	/**
	 * Metodos para eliminar los acentos del nombre de la hoja ppara expportarla
	 */
	private static final String ORIGINAL = "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00D1\u00F1\u00DC\u00FC";
	private static final String REPLACEMENT = "AaEeIiOoUuNnUu";

	public static String stripAccents(String str){
		if (str == null)
			return null;
		char[] array = str.toCharArray();
		for (int index = 0; index < array.length; index++){
			int pos = ORIGINAL.indexOf(array[index]);
			if (pos > -1){
				array[index] = REPLACEMENT.charAt(pos);
			}
		}
		return new String(array);
	}

	@End
	public void salir(){

	}

	@Remove
	@Destroy
	public void destroy(){
	}

	// Refactoring changes methods

	public String convertKey(Long section){
		if (section != null && section.toString() != null && !section.toString().trim().isEmpty())
			return (section.toString());
		else
			return null;
	}

	@SuppressWarnings("static-access")
	public String convertKey(Long section, Long group){
		if (section != null && section.toString() != null && !section.toString().trim().isEmpty() && group != null && group.toString() != null && !group.toString().trim().isEmpty())
			return (section.toString() + this.keySeparator + group.toString());
		else
			return null;
	}

	@SuppressWarnings("static-access")
	public String convertKey(Long section, Long group, Long variable){
		if (section != null && section.toString() != null && !section.toString().trim().isEmpty() && group != null && group.toString() != null && !group.toString().trim().isEmpty() && variable != null && variable.toString() != null && !variable.toString().trim().isEmpty())
			return (section.toString() + this.keySeparator + group.toString() + this.keySeparator + variable.toString());
		else
			return null;
	}

	@SuppressWarnings("static-access")
	public String convertKey(Long section, Long group, Long variable, Integer repetition){
		if (section != null && section.toString() != null && !section.toString().trim().isEmpty() && group != null && group.toString() != null && !group.toString().trim().isEmpty() && variable != null && variable.toString() != null && !variable.toString().trim().isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().trim().isEmpty())
			return (section.toString() + this.keySeparator + group.toString() + this.keySeparator + variable.toString() + this.keySeparator + repetition.toString());
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public List<GrupoVariables_ensayo> groupsFromSection(Long sectionId){
		if(this.mapGroupsList == null)
			this.mapGroupsList = new HashMap<Long, List<GrupoVariables_ensayo>>();
		if(sectionId != null){
			if(!this.mapGroupsList.containsKey(sectionId)){
				List<GrupoVariables_ensayo> gs = (List<GrupoVariables_ensayo>) this.entityManager.createQuery("select grup from GrupoVariables_ensayo grup where grup.seccion.id = :sectionId and grup.eliminado=false").setParameter("sectionId", sectionId).getResultList();
				this.mapGroupsList.put(sectionId, gs);
			}
			return this.mapGroupsList.get(sectionId);
		} else			
			return new ArrayList<GrupoVariables_ensayo>();
	}

	/*@SuppressWarnings("unchecked")
	public List<GrupoVariables_ensayo> variablesFromSection(Long seccionId){
		List<GrupoVariables_ensayo> l = (List<GrupoVariables_ensayo>) this.entityManager.createQuery("select grup from GrupoVariables_ensayo grup where grup.seccion.id = :sectionId").setParameter("sectionId", seccionId).getResultList();
		return l;
	}*/

	public boolean variablesGroups(Long sectionId, Long groupId){
		String key = this.convertKey(sectionId, groupId);
		return (this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(key) && this.mapWGD.get(key) != null && !this.mapWGD.get(key).isEmpty());
	}

	private List<Long> extractIds(List<Variable_ensayo> variables){
		List<Long> ids = new ArrayList<Long>();
		for (Variable_ensayo itemV : variables)
			ids.add(itemV.getId());
		return ids;
	}

	@SuppressWarnings("unchecked")
	private List<WrapperGroupData> loadWrapperList(Seccion_ensayo section, GrupoVariables_ensayo group){
		List<WrapperGroupData> wgds = new ArrayList<WrapperGroupData>();
		if (section != null && group != null){
			List<Variable_ensayo> tempVariables = this.entityManager.createQuery("select variable from Variable_ensayo variable where (variable.eliminado = null or variable.eliminado = false) and variable.seccion.id = :idS and variable.grupoVariables.id = :idG ORDER BY variable.idClinica, variable.numeroPregunta ASC").setParameter("idS", section.getId()).setParameter("idG", group.getId()).getResultList();
			List<Integer> repetitions = this.entityManager.createQuery("select distinct(vd.contGrupo) from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.contGrupo != null and vd.variable.id in (:ids) and vd.crdEspecifico.id = :hojaId order by vd.contGrupo ASC").setParameter("hojaId", this.hoja.getId()).setParameter("ids", this.extractIds(tempVariables)).getResultList();
			if(repetitions != null && !repetitions.isEmpty()){
				for (Integer itemRepetition : repetitions){
					WrapperGroupData wgd = new WrapperGroupData();
					wgd.setSection(section);
					wgd.setGroup(group);
					wgd.setRepetition(itemRepetition);
					wgd.setCreating(false);
					for (Variable_ensayo itemVariable : tempVariables){
						MapWrapperDataPlus itemData = new MapWrapperDataPlus(section, group, itemRepetition, itemVariable);
						boolean mustUpdateValue = true;
						if ((itemVariable.getPresentacionFormulario().getNombre().equals("checkbox") || itemVariable.getPresentacionFormulario().getNombre().equals("multi-select")) && itemVariable.getTipoDato().getCodigo().equals("NOM")){
							itemData.setVariableDatas(this.obtainValues(itemVariable, itemData.getRepetition()));
							itemData.setValues(this.extractCheckboxValues(itemData.getVariableDatas()));
							mustUpdateValue = false;
						} else if (itemVariable.getTipoDato().getCodigo().equals("FILE")){
							UploadItem item = this.obtainFileData(itemVariable, itemData.getRepetition());
							String fileName = null;
							byte[] data = null;
							if (item != null){
								fileName = item.getFileName();
								data = this.readFileData(item.getFile());
							}
							itemData.setFileName(fileName);
							itemData.setData(data);
						}
						VariableDato_ensayo vd = null;
						if (itemData.getVariableDatas() == null || itemData.getVariableDatas().isEmpty()){
							try {
								vd = (VariableDato_ensayo) this.entityManager.createQuery("select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId and vd.contGrupo != null and vd.contGrupo = :repetition order by vd.id desc").setParameter("variableId", itemVariable.getId()).setParameter("hojaId", this.hoja.getId()).setParameter("repetition", itemData.getRepetition()).getSingleResult();
							} catch (Exception e){
								vd = null;
							}
						} else
							vd = itemData.getVariableDatas().get(0);
						itemData.setVariableData(vd);
						if (vd != null){
							if (itemData.getVariableDatas() != null && !itemData.getVariableDatas().isEmpty()){
								for (VariableDato_ensayo itemVd : itemData.getVariableDatas()){
									if (!itemData.hasReport())
										itemData.setReport(this.loadReport(itemVariable, itemVd));
									if (!itemData.hasNotification())
										itemData.setNotification(this.loadNotification(itemVariable, itemVd));
									if (!itemData.hasNoteSite())
										itemData.setNoteSite(this.loadNoteSite(itemVariable, itemVd));
									if (!itemData.hasNoteMonitoring())
										itemData.setNoteMonitoring(this.loadNoteMonitoring(itemVariable, itemVd));
								}
							} else {
								itemData.setReport(this.loadReport(itemVariable, vd));
								itemData.setNotification(this.loadNotification(itemVariable, vd));
								itemData.setNoteSite(this.loadNoteSite(itemVariable, vd));
								itemData.setNoteMonitoring(this.loadNoteMonitoring(itemVariable, vd));
							}
							if (vd.getValor() != null && !vd.getValor().isEmpty()){
								if ((itemVariable.getTipoDato().getCodigo().equals("DATE") && itemVariable.getPresentacionFormulario().getNombre().equals("calendar"))){
									String[] fechaArreglo = vd.getValor().toString().split("/");
									String tempValue = "";
									if (fechaArreglo.length == 1)
										tempValue = "01" + "/" + "01" + "/" + fechaArreglo[0];
									else if (fechaArreglo.length == 2)
										tempValue = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
									else if (fechaArreglo.length == 3){
										if (fechaArreglo[0].equals("****"))
											tempValue = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
										else if (fechaArreglo[0].length() == 4 && !fechaArreglo[0].equals("****"))
											tempValue = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
										else
											tempValue = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
									}
									if (tempValue != null)
										itemData.setValue((Object) tempValue);
								} else if (mustUpdateValue)
									itemData.setValue((Object) vd.getValor());
							}
						}
						if (!wgd.hasReport() && itemData.hasReport())
							wgd.setReport(itemData.getReport());
						if (!wgd.hasNotification() && itemData.hasNotification())
							wgd.setNotification(itemData.getNotification());
						if (!wgd.getData().containsKey(itemVariable.getId()))
							wgd.getData().put(itemVariable.getId(), itemData);
					}
					if (!wgd.getData().isEmpty())
						wgds.add(wgd);
				}
			} else{ //Load old data				
				Integer tempMaxRepetition = null;				
				try {
					Long tempR = ((tempVariables != null && !tempVariables.isEmpty()) ? ((Long) this.entityManager.createQuery("select count(*) from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId and vd.contGrupo = null").setParameter("hojaId", this.hoja.getId()).setParameter("variableId", tempVariables.get(0).getId()).getSingleResult()) : null);
					if(tempR != null)
						tempMaxRepetition = Integer.parseInt(tempR.toString());
				} catch (Exception e) {					
					e.printStackTrace();
				}
				if(tempMaxRepetition != null && tempMaxRepetition > 0){
					Integer countRepetition = 1;
					Map<Long, List<VariableDato_ensayo>> tempVds = new HashMap<Long, List<VariableDato_ensayo>>();
					while(countRepetition <= tempMaxRepetition){
						WrapperGroupData wgd = new WrapperGroupData();
						wgd.setSection(section);
						wgd.setGroup(group);
						wgd.setRepetition(countRepetition);
						wgd.setCreating(false);
						for (Variable_ensayo itemVariable : tempVariables){
							MapWrapperDataPlus itemData = new MapWrapperDataPlus(section, group, countRepetition, itemVariable);
							itemData.setOldFormat(true);
							if(!tempVds.containsKey(itemVariable.getId())){
								List<VariableDato_ensayo> oldVds = this.entityManager.createQuery("select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId and vd.contGrupo = null order by vd.id ASC").setParameter("hojaId", this.hoja.getId()).setParameter("variableId", itemVariable.getId()).getResultList();
								tempVds.put(itemVariable.getId(), oldVds);
							}
							itemData.setVariableData(tempVds.get(itemVariable.getId()).get(countRepetition - 1));
							if (itemData.getVariableData() != null){
								itemData.setReport(this.loadReport(itemVariable, itemData.getVariableData()));
								itemData.setNotification(this.loadNotification(itemVariable, itemData.getVariableData()));
								itemData.setNoteSite(this.loadNoteSite(itemVariable, itemData.getVariableData()));
								itemData.setNoteMonitoring(this.loadNoteMonitoring(itemVariable, itemData.getVariableData()));
								if(itemData.getVariableData().getValor() != null){
									if ((itemVariable.getTipoDato().getCodigo().equals("DATE") && itemVariable.getPresentacionFormulario().getNombre().equals("calendar"))){
										if(itemData.getVariableData().getValor().toString().contains("/")){
											String[] fechaArreglo = itemData.getVariableData().getValor().toString().split("/");
											String tempValue = "";
											if (fechaArreglo.length == 1)
												tempValue = "01" + "/" + "01" + "/" + fechaArreglo[0];
											else if (fechaArreglo.length == 2)
												tempValue = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
											else if (fechaArreglo.length == 3){
												if (fechaArreglo[0].equals("****"))
													tempValue = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
												else if (fechaArreglo[0].length() == 4 && !fechaArreglo[0].equals("****"))
													tempValue = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
												else
													tempValue = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
											}
											if (tempValue != null)
												itemData.setValue((Object) tempValue);
										}
									} else
										itemData.setValue((Object) itemData.getVariableData().getValor());
								}
							}
							if (!wgd.hasReport() && itemData.hasReport())
								wgd.setReport(itemData.getReport());
							if (!wgd.hasNotification() && itemData.hasNotification())
								wgd.setNotification(itemData.getNotification());
							if (!wgd.getData().containsKey(itemVariable.getId()))
								wgd.getData().put(itemVariable.getId(), itemData);
						}
						if (!wgd.getData().isEmpty())
							wgds.add(wgd);
						countRepetition++;
					}					
				}				
			}				
		}
		if (!wgds.isEmpty())
			Collections.sort(wgds);
		return wgds;
	}

	private MapWrapperDataPlus loadPreviousData(String key, Integer repetition, Long variableId){
		if (key != null && !key.isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().isEmpty() && variableId != null && variableId.toString() != null && !variableId.toString().isEmpty()){
			if (this.mapBackupWGD != null && !this.mapBackupWGD.isEmpty() && this.mapBackupWGD.containsKey(key)){
				int index = this.indexOfGroupData(this.mapBackupWGD.get(key), repetition);
				if (index != -1 && this.mapBackupWGD.get(key).get(index) != null && this.mapBackupWGD.get(key).get(index).getData() != null && !this.mapBackupWGD.get(key).get(index).getData() .isEmpty() && this.mapBackupWGD.get(key).get(index).getData().containsKey(variableId))
					return this.mapBackupWGD.get(key).get(index).getData().get(variableId);
				else
					return null;
			} else
				return null;
		} else
			return null;
	}

	private MapWrapperDataPlus loadPreviousData(Long sectionId, Long variableId){
		if (sectionId != null && sectionId.toString() != null && !sectionId.toString().isEmpty() && variableId != null && variableId.toString() != null && !variableId.toString().isEmpty()){
			if (this.mapBackupWD != null && !this.mapBackupWD.isEmpty() && this.mapBackupWD.containsKey(sectionId)){
				if (this.mapBackupWD.get(sectionId) != null && this.mapBackupWD.get(sectionId).getData() != null && !this.mapBackupWD.get(sectionId).getData().isEmpty() && this.mapBackupWD.get(sectionId).getData().containsKey(variableId))
					return this.mapBackupWD.get(sectionId).getData().get(variableId);
				else
					return null;
			} else
				return null;
		} else
			return null;
	}

	private List<ReConsecuenciaspaciente_ensayo> extractCs(String[] source){
		List<ReConsecuenciaspaciente_ensayo> result = new ArrayList<ReConsecuenciaspaciente_ensayo>();
		if (source != null && source.length > 0){
			for (int i = 0; i < source.length; i++){
				if (source[i] != null && !source[i].isEmpty()){
					ReConsecuenciaspaciente_ensayo tempCs = null;
					try {
						tempCs = (ReConsecuenciaspaciente_ensayo) this.entityManager.createQuery("select cs from ReConsecuenciaspaciente_ensayo cs where cs.descripcion = :paramDescription").setParameter("paramDescription", source[i]).getSingleResult();
					} catch (Exception e){
						tempCs = null;
					}
					if (tempCs != null)
						result.add(tempCs);
				}
			}
		}
		return result;
	}

	public void orderValues(String key){
		if (this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(key)){
			List<WrapperGroupData> l = this.mapWGD.get(key);
			Collections.sort(l);
			this.mapWGD.put(key, l);
		}
	}

	public int indexOfGroupData(List<WrapperGroupData> wgds, Integer repetition){
		int index = -1;
		if (wgds != null && !wgds.isEmpty()){
			for (int i = 0; i < wgds.size(); i++){
				if (wgds.get(i).getRepetition() != null && wgds.get(i).getRepetition().equals(repetition)){
					index = i;
					break;
				}
			}
		}
		return index;
	}

	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> loadVariables(Long sectionId, Long groupId){
		if(this.mapVariablesGroupsList == null)
			this.mapVariablesGroupsList = new HashMap<String, List<Variable_ensayo>>();
		String key = this.convertKey(sectionId, groupId);
		if(key != null){
			if(!this.mapVariablesGroupsList.containsKey(key)){
				List<Variable_ensayo> vs = this.entityManager.createQuery("select variable from Variable_ensayo variable where (variable.eliminado = null or variable.eliminado = false) and variable.seccion.id = :idS and variable.grupoVariables.id = :idG ORDER BY variable.idClinica, variable.numeroPregunta ASC").setParameter("idS", sectionId).setParameter("idG", groupId).getResultList();
				this.mapVariablesGroupsList.put(key, vs);
			}
			return this.mapVariablesGroupsList.get(key);
		} else			
			return new ArrayList<Variable_ensayo>();
	}

	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> loadVariables(Long sectionId){
		if(this.mapVariablesNotGroupsList == null)
			this.mapVariablesNotGroupsList = new HashMap<Long, List<Variable_ensayo>>();
		if(sectionId != null){
			if(!this.mapVariablesNotGroupsList.containsKey(sectionId)){
				List<Variable_ensayo> vs = this.entityManager.createQuery("select variable from Variable_ensayo variable where (variable.eliminado = null or variable.eliminado = false) and variable.seccion.id = :idS and variable.grupoVariables = null ORDER BY variable.idClinica, variable.numeroPregunta ASC").setParameter("idS", sectionId).getResultList();
				this.mapVariablesNotGroupsList.put(sectionId, vs);
			}
			return this.mapVariablesNotGroupsList.get(sectionId);
		} else			
			return new ArrayList<Variable_ensayo>();
	}

	public void deleteRow(Long sectionId, Long groupId, Integer repetition){
		if (sectionId != null && sectionId.toString() != null && !sectionId.toString().trim().isEmpty() && groupId != null && groupId.toString() != null && !groupId.toString().trim().isEmpty() && repetition != null && repetition.toString() != null && !repetition.toString().isEmpty()){
			String tempKey = this.convertKey(sectionId, groupId);
			if (this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(tempKey)){
				int index = this.indexOfGroupData(this.mapWGD.get(tempKey), repetition);
				if (index != -1)
					this.mapWGD.get(tempKey).remove(index);
			}
		}
	}

	public boolean deleteEnabled(Long sectionId, Long groupId){
		if (sectionId != null && sectionId.toString() != null && !sectionId.toString().trim().isEmpty() && groupId != null && groupId.toString() != null && !groupId.toString().trim().isEmpty()){
			String tempKey = this.convertKey(sectionId, groupId);
			if (this.mapWGD != null && !this.mapWGD.isEmpty() && this.mapWGD.containsKey(tempKey))
				return (this.mapWGD.get(tempKey) != null && !this.mapWGD.get(tempKey).isEmpty() && this.mapWGD.get(tempKey).size() > 1);
			else
				return false;
		} else
			return false;
	}
	
	public boolean mostrarDescompletar(){
		return this.completada && !this.monitoringCompleted && !this.firmada && !this.isMonitor();
	}
	
	public boolean mostrarReiniciar(){
		return this.hoja.getEstadoHojaCrd().getCodigo()!=2 && !this.monitoringCompleted && !this.firmada &&  !this.isMonitor();
	}

	public boolean saveEnabled(){
		return ((this.mapWD != null && !this.mapWD.isEmpty()) || (this.mapWGD != null && !this.mapWGD.isEmpty()));
	}

	@SuppressWarnings("unchecked")
	private WrapperReReporteexpedito loadReport(Variable_ensayo variable, VariableDato_ensayo variableData){
		WrapperReReporteexpedito wr = null;
		ReReporteexpedito_ensayo report = null;
		String[] cs = null;
		List<ReMedicacion_ensayo> ip = null, mc = null, ei = null, mr = null;
		if (variable != null && variableData != null){
			try {
				report = (ReReporteexpedito_ensayo) this.entityManager.createQuery("select report from ReReporteexpedito_ensayo report where report.idSujeto = :sujetoId and report.idEstudio = :estudioId and report.idEntidad = :entidadId and report.crdEspecifico.id = :hojaId and report.variable.id = :variableId and report.variableDato.id = :variableDatoId").setParameter("sujetoId", this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getId()).setParameter("hojaId", this.hoja.getId()).setParameter("estudioId", this.estudioEntidad.getEstudio().getId()).setParameter("entidadId", this.estudioEntidad.getEstudio().getEntidad().getId()).setParameter("variableId", variable.getId()).setParameter("variableDatoId", variableData.getId()).getSingleResult();
			} catch (Exception e){
				report = null;
			}
		}
		if (report != null){
			List<ReConsecuenciaspaciente_ensayo> tempCs = this.entityManager.createQuery("select cs from ReConsecuenciaspaciente_ensayo cs where cs.id in (select hr.idReEnfermedades from ReHistoriarelevante_ensayo hr where hr.reReporteexpedito.id = :reportId)").setParameter("reportId", report.getId()).getResultList();
			if (tempCs != null && !tempCs.isEmpty()){
				cs = new String[tempCs.size()];
				for (int i = 0; i < tempCs.size(); i++)
					cs[i] = tempCs.get(i).getDescripcion();
			}
			ip = this.entityManager.createQuery("select ip.reMedicacion from ReInfoproducto_ensayo ip where ip.reReporteexpedito.id = :reportId and (ip.reMedicacion.eliminado is null or ip.reMedicacion.eliminado = false)").setParameter("reportId", report.getId()).getResultList();
			mc = this.entityManager.createQuery("select mc.reMedicacion from ReMedicacionconcomitante_ensayo mc where mc.reReporteexpedito.id = :reportId and (mc.reMedicacion.eliminado is null or mc.reMedicacion.eliminado = false)").setParameter("reportId", report.getId()).getResultList();
			ei = this.entityManager.createQuery("select ei.reMedicacion from ReEnfermedadesinter_ensayo ei where ei.reReporteexpedito.id = :reportId and (ei.reMedicacion.eliminado is null or ei.reMedicacion.eliminado = false)").setParameter("reportId", report.getId()).getResultList();
			mr = this.entityManager.createQuery("select mr.reMedicacion from ReMedicacionreciente_ensayo mr where mr.reReporteexpedito.id = :reportId and (mr.reMedicacion.eliminado is null or mr.reMedicacion.eliminado = false)").setParameter("reportId", report.getId()).getResultList();
			wr = new WrapperReReporteexpedito(report);
			if (cs != null)
				wr.setConsecuencias(cs);
			if (ip != null && !ip.isEmpty())
				wr.setProductosEstudios(ip);
			if (mc != null && !mc.isEmpty())
				wr.setMedicacionConcomitante(mc);
			if (ei != null && !ei.isEmpty())
				wr.setEnfermedadesInter(ei);
			if (mr != null && !mr.isEmpty())
				wr.setMedicacionReciente(mr);
			List<EFaseEstudio_ensayo> lf = this.entityManager.createQuery("select phase from EFaseEstudio_ensayo phase inner join phase.estudios e where (e.eliminado = null or e.eliminado = false) and e.id = :estudioId order by phase.id desc").setParameter("estudioId", report.getIdEstudio()).getResultList();
			if (lf != null && !lf.isEmpty())
				wr.setFase(lf.get(0));
		}
		return wr;
	}

	private Notificacion_ensayo loadNotification(Variable_ensayo variable, VariableDato_ensayo variableData){
		if (variable != null && variableData != null){
			try {
				return (Notificacion_ensayo) this.entityManager.createQuery("select notification from Notificacion_ensayo notification where notification.idSujeto = :sujetoId and notification.idEstudio = :estudioId and notification.idEntidad = :entidadId and notification.crdEspecifico.id = :hojaId and notification.variable.id = :variableId and notification.variableDato.id = :variableDatoId").setParameter("sujetoId", this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getId()).setParameter("hojaId", this.hoja.getId()).setParameter("estudioId", this.estudioEntidad.getEstudio().getId()).setParameter("entidadId", this.estudioEntidad.getEstudio().getEntidad().getId()).setParameter("variableId", variable.getId()).setParameter("variableDatoId", variableData.getId()).getSingleResult();
			} catch (NonUniqueResultException e){
				try {
					return (Notificacion_ensayo) this.entityManager.createQuery("select notification from Notificacion_ensayo notification where notification.idSujeto = :sujetoId and notification.idEstudio = :estudioId and notification.idEntidad = :entidadId and notification.crdEspecifico.id = :hojaId and notification.variable.id = :variableId and notification.variableDato.id = :variableDatoId order by notification.id desc").setParameter("sujetoId", this.hoja.getMomentoSeguimientoEspecifico().getSujeto().getId()).setParameter("hojaId", this.hoja.getId()).setParameter("estudioId", this.estudioEntidad.getEstudio().getId()).setParameter("entidadId", this.estudioEntidad.getEstudio().getEntidad().getId()).setParameter("variableId", variable.getId()).setParameter("variableDatoId", variableData.getId()).setMaxResults(1).getSingleResult();
				} catch (Exception e2) {
					return null;
				}
			}catch (Exception e){
				return null;
			}
		}

		return null;
	}

	private Object[] extractCheckboxValues(List<VariableDato_ensayo> vds){
		if (vds == null || vds.isEmpty())
			return null;
		Object[] values = new Object[vds.size()];
		for (int i = 0; i < vds.size(); i++)
			values[i] = ((vds.get(i) != null && vds.get(i).getValor() != null) ? vds.get(i).getValor().toString(): null);
		return values;
	}

	private boolean valueIn(List<VariableDato_ensayo> values, VariableDato_ensayo vd){
		if (values == null || vd == null)
			return true;
		for (VariableDato_ensayo itemVd : values){
			if (((itemVd.getValor() == null || itemVd.getValor().trim().isEmpty()) && (vd.getValor() == null || vd.getValor().trim().isEmpty())) || (itemVd.getValor() != null && !itemVd.getValor().trim().isEmpty() && vd.getValor() != null && !vd.getValor().trim().isEmpty() && vd.getValor().trim().equals(itemVd.getValor().trim())))
				return true;
		}
		return false;
	}

	private List<VariableDato_ensayo> extractVdsFor(List<VariableDato_ensayo> from, List<VariableDato_ensayo> to){
		List<VariableDato_ensayo> result = new ArrayList<VariableDato_ensayo>();
		if (from != null && !from.isEmpty()){
			for (VariableDato_ensayo itemVd : from){
				if (itemVd != null){
					boolean exists = false;
					if (to != null && !to.isEmpty()){
						for (int i = 0; i < to.size(); i++){
							if (to.get(i) != null){
								if (((itemVd.getValor() == null || itemVd.getValor().trim().isEmpty()) && (to.get(i).getValor() == null || to.get(i).getValor().trim().isEmpty())) || (itemVd.getValor() != null && !itemVd.getValor().trim().isEmpty() && to.get(i).getValor() != null && !to.get(i).getValor().trim().isEmpty() && to.get(i).getValor().trim().equals(itemVd.getValor().trim()))){
									exists = true;
									break;
								}
							}
						}
					}
					if (!exists && !this.valueIn(result, itemVd))
						result.add(itemVd);
				}
			}
		}
		return result;
	}

	private List<VariableDato_ensayo> extractVdsForNone(List<VariableDato_ensayo> from, List<VariableDato_ensayo> to){
		List<VariableDato_ensayo> result = new ArrayList<VariableDato_ensayo>();
		if (from != null && !from.isEmpty()){
			for (VariableDato_ensayo itemVd : from){
				if (itemVd != null && itemVd.getValor() != null && !itemVd.getValor().isEmpty()){
					boolean exists = false;
					if (to != null && !to.isEmpty()){
						for (int i = 0; i < to.size(); i++){
							if (to.get(i) != null){
								if (((itemVd.getValor() == null || itemVd.getValor().trim().isEmpty()) && (to.get(i).getValor() == null || to.get(i).getValor().trim().isEmpty())) || (itemVd.getValor() != null && !itemVd.getValor().trim().isEmpty() && to.get(i).getValor() != null && !to.get(i).getValor().trim().isEmpty() && to.get(i).getValor().trim().equals(itemVd.getValor().trim()))){
									exists = true;
									break;
								}
							}
						}
					}
					if (exists && !this.valueIn(result, itemVd))
						result.add(itemVd);
				}
			}
		}
		return result;
	}
	

	@SuppressWarnings("unchecked")
	private List<VariableDato_ensayo> obtainValues(Variable_ensayo variable, Integer repetition){
		List<VariableDato_ensayo> values = new ArrayList<VariableDato_ensayo>();
		if (variable != null){
			boolean grouped = (repetition != null && repetition.toString() != null && !repetition.toString().isEmpty());
			String query = "select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId";
			String queryGrouped = query + " and vd.contGrupo != null and vd.contGrupo = :repetition";			
			try {
				if (!grouped)
					values = (List<VariableDato_ensayo>) this.entityManager.createQuery(query).setParameter("variableId", variable.getId()).setParameter("hojaId", this.hoja.getId()).getResultList();
				else{
					values = (List<VariableDato_ensayo>) this.entityManager.createQuery(queryGrouped).setParameter("variableId", variable.getId()).setParameter("hojaId", this.hoja.getId()).setParameter("repetition", repetition).getResultList();
					if(values.isEmpty())
						values = (List<VariableDato_ensayo>) this.entityManager.createQuery(query).setParameter("variableId", variable.getId()).setParameter("hojaId", this.hoja.getId()).getResultList();						
				}					
			} catch (Exception e){
				values = new ArrayList<VariableDato_ensayo>();
				e.printStackTrace();
			}
		}
		return values;
	}

	private UploadItem obtainFileData(Variable_ensayo variable, Integer repetition){
		if (variable == null || !this.fileFolder.exists())
			return null;
		VariableDato_ensayo vd = null;
		boolean grouped = (repetition != null && repetition.toString() != null && !repetition.toString().isEmpty());
		String query = "select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId and vd.contGrupo == null";
		if (grouped)
			query = "select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId and vd.contGrupo != null and vd.contGrupo = :repetition";
		try {
			if (!grouped)
				vd = (VariableDato_ensayo) this.entityManager.createQuery(query).setParameter("variableId", variable.getId()).setParameter("hojaId", this.hoja.getId()).getSingleResult();
			else
				vd = (VariableDato_ensayo) this.entityManager.createQuery(query).setParameter("variableId", variable.getId()).setParameter("hojaId", this.hoja.getId()).setParameter("repetition", repetition).getSingleResult();
		} catch (Exception e){
			vd = null;
		}
		if (vd != null){
			File f = new File(this.fileFolder, vd.getValor());
			if (!f.exists())
				return null;
			return new UploadItem(f.getName(), (int) f.length(), "", f);
		} else
			return null;
	}

	public void clearSelectedFile(Variable_ensayo variable){
		if (this.mapWD != null && !this.mapWD.isEmpty() && this.mapWD.containsKey(variable.getSeccion().getId()) && this.mapWD.get(variable.getSeccion().getId()).getData() != null && !this.mapWD.get(variable.getSeccion().getId()).getData().isEmpty() && this.mapWD.get(variable.getSeccion().getId()).getData().containsKey(variable.getId()) && this.mapWD.get(variable.getSeccion().getId()).getData().get(variable.getId()) != null){
			this.mapWD.get(variable.getSeccion().getId()).getData().get(variable.getId()).setFileName(null);
			this.mapWD.get(variable.getSeccion().getId()).getData().get(variable.getId()).setData(null);
		}
	}

	private Long extractSection(){
		if (this.selectedTab != null && !this.selectedTab.isEmpty() && this.selectedTab.contains("_"))
			return Long.parseLong(this.selectedTab.split("_")[1]);
		return null;
	}

	public void cambiarTab(Long sectionId){
		if (sectionId != null){
			Long tempSelectedTabId = this.extractSection();
			if (tempSelectedTabId == null || !tempSelectedTabId.equals(sectionId))
				this.selectedTab = "id_" + sectionId.toString();
		}
	}

	public boolean isMonitor(){
		Role_ensayo role = this.obtainRole();
		return (role != null && role.getCodigo().equals("ecMon"));
	}

	private Nota_ensayo loadNoteSite(Variable_ensayo variable, VariableDato_ensayo variableData){
		if (variable != null && String.valueOf(variable.getId()) != null && !String.valueOf(variable.getId()).isEmpty()){
			boolean variableDat = (variableData != null && String.valueOf(variableData.getId()) != null && !String.valueOf(variableData.getId()).isEmpty());
			String query = "select nota from Nota_ensayo nota where nota.crdEspecifico.id = :hojaId and nota.variable.id = :variableId and nota.notaSitio = true and (nota.eliminado = null or nota.eliminado = false)";
			String queryGrouped = query + " and nota.variableDato.id = :variableDatoId";	
			
			
			Nota_ensayo nota = null;
			try {
				if(!variableDat || (variableDat && variable.getGrupoVariables() == null)){
					nota = (Nota_ensayo) this.entityManager.createQuery(query).setParameter("hojaId", this.hoja.getId()).setParameter("variableId", variable.getId()).getSingleResult();
				}else if((variableDat && variable.getGrupoVariables() != null)){
					nota = (Nota_ensayo) this.entityManager.createQuery(queryGrouped).setParameter("hojaId", this.hoja.getId()).setParameter("variableId", variable.getId()).setParameter("variableDatoId", variableData.getId()).getSingleResult();
				}
			} catch (Exception e){
				nota = null;
			}
			return nota;
		} else
			return null;
	}

	private Nota_ensayo loadNoteMonitoring(Variable_ensayo variable, VariableDato_ensayo variableData){
		if (variable != null && String.valueOf(variable.getId()) != null && !String.valueOf(variable.getId()).isEmpty()){
			boolean variableDat = (variableData != null && String.valueOf(variableData.getId()) != null && !String.valueOf(variableData.getId()).isEmpty());
			String query = "select nota from Nota_ensayo nota where nota.crdEspecifico.id = :hojaId and nota.variable.id = :variableId and nota.notaSitio = false and nota.notaPadre = null and (nota.eliminado = null or nota.eliminado = false)";
			String queryGrouped = query + " and nota.variableDato.id = :variableDatoId";	
			
			
			Nota_ensayo nota = null;
			try {
				if(!variableDat || (variableDat && variable.getGrupoVariables() == null)){
					nota = (Nota_ensayo) this.entityManager.createQuery(query).setParameter("hojaId", this.hoja.getId()).setParameter("variableId", variable.getId()).getSingleResult();
				}else if((variableDat && variable.getGrupoVariables() != null)){
					nota = (Nota_ensayo) this.entityManager.createQuery(queryGrouped).setParameter("hojaId", this.hoja.getId()).setParameter("variableId", variable.getId()).setParameter("variableDatoId", variableData.getId()).getSingleResult();
				}
			} catch (Exception e){
				nota = null;
			}
			return nota;
		} else
			return null;
	}
	private List<Variable_ensayo> tempVariables;
	private void loadVariableData(Seccion_ensayo section) {
		if (this.mapWD != null && !this.mapWD.containsKey(section.getId())) {
			WrapperGroupData itemData = new WrapperGroupData();
			itemData.setSection(section);
			tempVariables = this.loadVariables(section
					.getId());
			for (Variable_ensayo itemVariable : tempVariables) {
				boolean mustUpdateValue = true;
				if (itemData.getData() != null && !itemData.getData().containsKey(itemVariable.getId())){
					MapWrapperDataPlus tempItemData = new MapWrapperDataPlus(section, itemVariable);
					if ((itemVariable.getPresentacionFormulario().getNombre().equals("checkbox") || itemVariable.getPresentacionFormulario().getNombre().equals("multi-select")) && itemVariable.getTipoDato().getCodigo().equals("NOM")){
						tempItemData.setVariableDatas(this.obtainValues(itemVariable, itemData.getRepetition()));
						tempItemData.setValues(this.extractCheckboxValues(tempItemData.getVariableDatas()));
						mustUpdateValue = false;
					} else if (itemVariable.getTipoDato().getCodigo().equals("FILE")){
						UploadItem item = this.obtainFileData(itemVariable, itemData.getRepetition());
						String fileName = null;
						byte[] data = null;
						if (item != null){
							fileName = item.getFileName();
							data = this.readFileData(item.getFile());
						}
						tempItemData.setFileName(fileName);
						tempItemData.setData(data);
					}
					VariableDato_ensayo vd = null;
					if (tempItemData.getVariableDatas() == null || tempItemData.getVariableDatas().isEmpty()){
						try {
							vd = (VariableDato_ensayo) this.entityManager.createQuery("select vd from VariableDato_ensayo vd where (vd.eliminado = null or vd.eliminado = false) and vd.variable.id = :variableId and vd.crdEspecifico.id = :hojaId and vd.contGrupo = null order by vd.id desc").setParameter("variableId", itemVariable.getId()).setParameter("hojaId", this.hoja.getId()).getSingleResult();
						} catch (Exception e){
							vd = null;
						}
					} else
						vd = tempItemData.getVariableDatas().get(0);
					if (vd != null){
						if (vd.getValor() != null){
							if ((itemVariable.getTipoDato().getCodigo().equals("DATE") && itemVariable.getPresentacionFormulario().getNombre().equals("calendar"))){
								String[] fechaArreglo = vd.getValor().toString().split("/");
								String tempValue = "";
								if (fechaArreglo.length == 1)
									tempValue = "01" + "/" + "01" + "/" + fechaArreglo[0];
								else if (fechaArreglo.length == 2)
									tempValue = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
								else if (fechaArreglo.length == 3){
									if (fechaArreglo[0].equals("****"))
										tempValue = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
									else if (fechaArreglo[0].length() == 4 && !fechaArreglo[0].equals("****"))
										tempValue = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
									else
										tempValue = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
								}
								if (tempValue != null)
									tempItemData.setValue((Object) tempValue);
							} else if (mustUpdateValue)
								tempItemData.setValue((Object) vd.getValor());
						}
						if ((itemData.getCreating() == null || itemData.getCreating() == true))
							itemData.setCreating(false);
					}
					tempItemData.setVariableData(vd);
					if (tempItemData.getVariableDatas() != null && !tempItemData.getVariableDatas().isEmpty()){
						for (VariableDato_ensayo itemVd : tempItemData.getVariableDatas()){
							if (!tempItemData.hasReport())
								tempItemData.setReport(this.loadReport(itemVariable, itemVd));
							if (!tempItemData.hasNotification())
								tempItemData.setNotification(this.loadNotification(itemVariable, itemVd));
							if (!tempItemData.hasNoteSite())
								tempItemData.setNoteSite(this.loadNoteSite(itemVariable, itemVd));
							if (!tempItemData.hasNoteMonitoring())
								tempItemData.setNoteMonitoring(this.loadNoteMonitoring(itemVariable, itemVd));
						}
					} else {
						tempItemData.setNoteSite(this.loadNoteSite(itemVariable, vd));
						tempItemData.setNoteMonitoring(this.loadNoteMonitoring(itemVariable, vd));
						tempItemData.setNotification(this.loadNotification(itemVariable, vd));
						tempItemData.setReport(this.loadReport(itemVariable, vd));
					}
					itemData.getData().put(itemVariable.getId(), tempItemData);
					if (!itemData.hasReport() && tempItemData.hasReport())
						itemData.setReport(tempItemData.getReport());
					if (!itemData.hasNotification() && tempItemData.hasNotification())
						itemData.setNotification(tempItemData.getNotification());
				}
			}
			if (itemData.getData() != null && !itemData.getData().isEmpty())
				this.mapWD.put(section.getId(), itemData);
		}
	}

	private void loadGroupData(Seccion_ensayo section){
		List<GrupoVariables_ensayo> groups = this.groupsFromSection(section.getId());
		for (GrupoVariables_ensayo itemGroup : groups){
			String tempKey = this.convertKey(section.getId(), itemGroup.getId());
			List<WrapperGroupData> wgds = this.loadWrapperList(section, itemGroup);
			if (!wgds.isEmpty()){
				this.mapPositions.put(tempKey, wgds.get(wgds.size() - 1).getRepetition());
				this.mapWGD.put(tempKey, wgds);
			}
		}
	}

	private Map<String, List<WrapperGroupData>> groupDataAdded(){
		Map<String, List<WrapperGroupData>> result = new HashMap<String, List<WrapperGroupData>>();
		for (String itemKey : this.mapWGD.keySet()){
			if (!this.mapBackupWGD.containsKey(itemKey))
				result.put(itemKey, this.mapWGD.get(itemKey));
			else {
				List<WrapperGroupData> tempWGD = new ArrayList<WrapperGroupData>();
				for (WrapperGroupData itemWGD : this.mapWGD.get(itemKey)){
					int index = this.indexOfGroupData(this.mapBackupWGD.get(itemKey), itemWGD.getRepetition());
					if (index == -1)
						tempWGD.add(itemWGD);
				}
				if (tempWGD != null && !tempWGD.isEmpty())
					result.put(itemKey, tempWGD);
			}
		}
		return result;
	}

	private Map<String, List<WrapperGroupData>> groupDataDeleted(){
		Map<String, List<WrapperGroupData>> result = new HashMap<String, List<WrapperGroupData>>();
		for (String itemKey : this.mapBackupWGD.keySet()){
			if (!this.mapWGD.containsKey(itemKey))
				result.put(itemKey, this.mapBackupWGD.get(itemKey));
			else {
				List<WrapperGroupData> tempWGD = new ArrayList<WrapperGroupData>();
				for (WrapperGroupData itemWGD : this.mapBackupWGD.get(itemKey)){
					int index = this.indexOfGroupData(this.mapWGD.get(itemKey), itemWGD.getRepetition());
					if (index == -1)
						tempWGD.add(itemWGD);
				}
				if (tempWGD != null && !tempWGD.isEmpty())
					result.put(itemKey, tempWGD);
			}
		}
		return result;
	}

	private Map<String, List<WrapperGroupData>> groupDataUpdated(){
		Map<String, List<WrapperGroupData>> result = new HashMap<String, List<WrapperGroupData>>();
		for (String itemKey : this.mapWGD.keySet()){
			if (this.mapBackupWGD.containsKey(itemKey)){
				List<WrapperGroupData> tempWGD = new ArrayList<WrapperGroupData>();
				for (WrapperGroupData itemWGD : this.mapWGD.get(itemKey)){
					int index = this.indexOfGroupData(this.mapBackupWGD.get(itemKey), itemWGD.getRepetition());
					if (index != -1){
						WrapperGroupData other = this.mapBackupWGD.get(itemKey).get(index);
						if (other != null){
							if (itemWGD.hasChanges(other))
								tempWGD.add(itemWGD);
						}
					}
				}
				if (tempWGD != null && !tempWGD.isEmpty()){
					Collections.sort(tempWGD);
					result.put(itemKey, tempWGD);
				}
			}
		}
		return result;
	}

	private List<MapWrapperDataPlus> extractVariables(Map<Long, MapWrapperDataPlus> from, Map<Long, MapWrapperDataPlus> to, boolean forUpdate){
		List<MapWrapperDataPlus> result = new ArrayList<MapWrapperDataPlus>();
		if (from != null && !from.isEmpty()){
			for (Long itemKey : from.keySet()){
				if (from.get(itemKey) != null){
					if ((!forUpdate && (to == null || to.isEmpty() || !to.containsKey(itemKey) || (to.get(itemKey) == null))) || (forUpdate && to != null && !to.isEmpty() && to.containsKey(itemKey) && to.get(itemKey) != null && from.get(itemKey).hasChanges(to.get(itemKey))))
						result.add(from.get(itemKey));
				}
			}
		}
		return result;
	}

	private Map<Long, List<MapWrapperDataPlus>> variableDataAdded(){
		Map<Long, List<MapWrapperDataPlus>> result = new HashMap<Long, List<MapWrapperDataPlus>>();
		for (Long itemKey : this.mapWD.keySet()){
			WrapperGroupData itemData = this.mapWD.get(itemKey);
			if (itemData != null){
				List<MapWrapperDataPlus> tempResult = new ArrayList<MapWrapperDataPlus>();
				if (this.mapBackupWD.containsKey(itemKey)){
					WrapperGroupData otherData = this.mapBackupWD.get(itemKey);
					tempResult = ((otherData != null) ? (this.extractVariables(itemData.getData(), otherData.getData(), false)) : (new ArrayList<MapWrapperDataPlus>(itemData.getData().values())));
				} else if (itemData.getData() != null && !itemData.getData().isEmpty()){
					for (MapWrapperDataPlus itemDataData : itemData.getData().values()){
						if (itemDataData != null)
							tempResult.add(itemDataData);
					}
				}
				if (!tempResult.isEmpty())
					result.put(itemKey, tempResult);
			}
		}
		return result;
	}

	private Map<Long, List<MapWrapperDataPlus>> variableDataUpdated(){
		Map<Long, List<MapWrapperDataPlus>> result = new HashMap<Long, List<MapWrapperDataPlus>>();
		for (Long itemKey : this.mapWD.keySet()){
			WrapperGroupData itemData = this.mapWD.get(itemKey);
			if (itemData != null){
				List<MapWrapperDataPlus> tempResult = new ArrayList<MapWrapperDataPlus>();
				if (this.mapBackupWD.containsKey(itemKey)){
					WrapperGroupData otherData = this.mapBackupWD.get(itemKey);
					tempResult = ((otherData != null) ? (this.extractVariables(itemData.getData(), this.mapBackupWD.get(itemKey).getData(), true)) : (new ArrayList<MapWrapperDataPlus>()));
				}
				if (!tempResult.isEmpty())
					result.put(itemKey, tempResult);
			}
		}
		return result;
	}

	public void changeCompleted(){
		this.completada = !this.completada;
	}

	public boolean completeEnabled(){
		return (this.rolLogueado != null && (this.rolLogueado.getCodigo().equals("ecCord") || this.rolLogueado.getCodigo().equals("ecInv")));
	}

	// End Refactoring changes methods

	public CrdEspecifico_ensayo getHoja(){
		return hoja;
	}

	public void setHoja(CrdEspecifico_ensayo hoja){
		this.hoja = hoja;
	}

	public long getIdcrd(){
		return idcrd;
	}

	public void setIdcrd(long idcrd){
		this.idcrd = idcrd;
	}

	public long getCid(){
		return cid;
	}

	public void setCid(long cid){
		this.cid = cid;
	}

	public boolean isInicializado(){
		return inicializado;
	}

	public void setInicializado(boolean inicializado){
		this.inicializado = inicializado;
	}

	public List<Seccion_ensayo> getSecciones(){
		if (secciones == null || secciones.isEmpty())
			this.loadSections();
		return secciones;
	}

	public void setSecciones(List<Seccion_ensayo> secciones){
		this.secciones = secciones;
	}

	public List<GrupoVariables_ensayo> getGrupoVariables(){
		return grupoVariables;
	}

	public void setGrupoVariables(List<GrupoVariables_ensayo> grupoVariables){
		this.grupoVariables = grupoVariables;
	}

	public Map<Long, List<GrupoVariables_ensayo>> getListaGrupoVariables(){
		return listaGrupoVariables;
	}

	public void setListaGrupoVariables(Map<Long, List<GrupoVariables_ensayo>> listaGrupoVariables){
		this.listaGrupoVariables = listaGrupoVariables;
	}

	public Long getIdTabala(){
		return idTabala;
	}

	public void setIdTabala(Long idTabala){
		this.idTabala = idTabala;
	}

	public Long getIdMS(){
		return idMS;
	}

	public void setIdMS(Long idMS){
		this.idMS = idMS;
	}

	public Long getIdSujeto(){
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto){
		this.idSujeto = idSujeto;
	}

	public boolean isCompletada(){
		return completada;
	}

	public void setCompletada(boolean completada){
		this.completada = completada;
	}

	public ArrayList<Integer> getListaCant(){
		return listaCant;
	}

	public void setListaCant(ArrayList<Integer> listaCant){
		this.listaCant = listaCant;
	}

	public SeguridadEstudio getSeguridadEstudio(){
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio){
		this.seguridadEstudio = seguridadEstudio;
	}

	public Long getIdGrupo(){
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo){
		this.idGrupo = idGrupo;
	}

	public CrdEspecifico_ensayo getHojaCRD(){
		return hojaCRD;
	}

	public void setHojaCRD(CrdEspecifico_ensayo hojaCRD){
		this.hojaCRD = hojaCRD;
	}

	public boolean isCausaRequired(){
		return causaRequired;
	}

	public void setCausaRequired(boolean causaRequired){
		this.causaRequired = causaRequired;
	}

	public String getFrom(){
		return from;
	}

	public void setFrom(String from){
		this.from = from;
	}

	public Role_ensayo getRolLogueado(){
		return rolLogueado;
	}

	public void setRolLogueado(Role_ensayo rolLogueado){
		this.rolLogueado = rolLogueado;
	}

	public HSSFFont getHeaderFont(){
		return headerFont;
	}

	public void setHeaderFont(HSSFFont headerFont){
		this.headerFont = headerFont;
	}

	public HSSFFont getContentFont(){
		return contentFont;
	}

	public void setContentFont(HSSFFont contentFont){
		this.contentFont = contentFont;
	}

	public String getPath(){
		return path;
	}

	public void setPath(String path){
		this.path = path;
	}

	public List<ReReporteexpedito_ensayo> getListaReporte(){
		return listaReporte;
	}

	public void setListaReporte(List<ReReporteexpedito_ensayo> listaReporte){
		this.listaReporte = listaReporte;
	}

	public String getNombreReport(){
		return nombreReport;
	}

	public void setNombreReport(String nombreReport){
		this.nombreReport = nombreReport;
	}

	@SuppressWarnings("rawtypes")
	public Map getPars(){
		return pars;
	}

	@SuppressWarnings("rawtypes")
	public void setPars(Map pars){
		this.pars = pars;
	}

	@SuppressWarnings("rawtypes")
	public List<Collection> getListadoreporteExpeditoGeneral(){
		return listadoreporteExpeditoGeneral;
	}

	@SuppressWarnings("rawtypes")
	public void setListadoreporteExpeditoGeneral(List<Collection> listadoreporteExpeditoGeneral){
		this.listadoreporteExpeditoGeneral = listadoreporteExpeditoGeneral;
	}

	public List<ReporteExpeditoUno> getListadoreporteExpeditoUno(){
		return listadoreporteExpeditoUno;
	}

	public void setListadoreporteExpeditoUno(List<ReporteExpeditoUno> listadoreporteExpeditoUno){
		this.listadoreporteExpeditoUno = listadoreporteExpeditoUno;
	}

	public List<ReporteExpeditoDos> getListadoreporteExpeditoDos(){
		return listadoreporteExpeditoDos;
	}

	public void setListadoreporteExpeditoDos(List<ReporteExpeditoDos> listadoreporteExpeditoDos){
		this.listadoreporteExpeditoDos = listadoreporteExpeditoDos;
	}

	public List<ReporteExpeditoTres> getListadoreporteExpeditoTres(){
		return listadoreporteExpeditoTres;
	}

	public void setListadoreporteExpeditoTres(List<ReporteExpeditoTres> listadoreporteExpeditoTres){
		this.listadoreporteExpeditoTres = listadoreporteExpeditoTres;
	}

	public List<String> getSubReportNames(){
		return subReportNames;
	}

	public void setSubReportNames(List<String> subReportNames){
		this.subReportNames = subReportNames;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> getSubReportPars(){
		return subReportPars;
	}

	@SuppressWarnings("rawtypes")
	public void setSubReportPars(List<Map> subReportPars){
		this.subReportPars = subReportPars;
	}

	public String getPathExportedReport(){
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport){
		this.pathExportedReport = pathExportedReport;
	}

	public EstudioEntidad_ensayo getEstudioEntidad(){
		return estudioEntidad;
	}

	public void setEstudioEntidad(EstudioEntidad_ensayo estudioEntidad){
		this.estudioEntidad = estudioEntidad;
	}

	public String getFileformatToExport(){
		return fileformatToExport;
	}

	public void setFileformatToExport(String fileformatToExport){
		this.fileformatToExport = fileformatToExport;
	}

	public List<String> getFilesFormatCombo(){
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo){
		this.filesFormatCombo = filesFormatCombo;
	}

	public List<GrupoSujetos_ensayo> getListarGrupo(){
		return listarGrupo;
	}

	public void setListarGrupo(List<GrupoSujetos_ensayo> listarGrupo){
		this.listarGrupo = listarGrupo;
	}

	public Sujeto_ensayo getSujetoIncluido(){
		return sujetoIncluido;
	}

	public void setSujetoIncluido(Sujeto_ensayo sujetoIncluido){
		this.sujetoIncluido = sujetoIncluido;
	}

	public String getFromP(){
		return fromP;
	}

	public void setFromP(String fromP){
		this.fromP = fromP;
	}

	public boolean isEsReq(){
		return esReq;
	}

	public void setEsReq(boolean esReq){
		this.esReq = esReq;
	}

	// Refactoring changes properties

	public Map<String, Integer> getMapPositions(){
		return mapPositions;
	}

	public void setMapPositions(Map<String, Integer> mapPositions){
		this.mapPositions = mapPositions;
	}

	public Map<String, List<WrapperGroupData>> getMapWGD(){
		return mapWGD;
	}

	public void setMapWGD(Map<String, List<WrapperGroupData>> mapWGD){
		this.mapWGD = mapWGD;
	}

	public Map<Long, WrapperGroupData> getMapWD(){
		return mapWD;
	}

	public void setMapWD(Map<Long, WrapperGroupData> mapWD){
		this.mapWD = mapWD;
	}

	public String getSelectedTab(){
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab){
		this.selectedTab = selectedTab;
	}

	public boolean isMonitoringCompleted(){
		return monitoringCompleted;
	}

	public void setMonitoringCompleted(boolean monitoringCompleted){
		this.monitoringCompleted = monitoringCompleted;
	}

	public boolean isMonitoringNotStarted(){
		return monitoringNotStarted;
	}

	public void setMonitoringNotStarted(boolean monitoringNotStarted){
		this.monitoringNotStarted = monitoringNotStarted;
	}

	public boolean isCompletedFromStart(){
		return completedFromStart;
	}

	public void setCompletedFromStart(boolean completedFromStart){
		this.completedFromStart = completedFromStart;
	}

	public boolean isDesdeCompleta() {
		return desdeCompleta;
	}

	public void setDesdeCompleta(boolean desdeCompleta) {
		this.desdeCompleta = desdeCompleta;
	}

	public boolean isFirmada() {
		return firmada;
	}

	public void setFirmada(boolean firmada) {
		this.firmada = firmada;
	}

	
	public String getCausaDescMonitoreo() {
		return causaDescMonitoreo;
	}

	public void setCausaDescMonitoreo(String causaDescMonitoreo) {
		this.causaDescMonitoreo = causaDescMonitoreo;
	}

	public String getDescripcionCausaReiniciarHoja() {
		return descripcionCausaReiniciarHoja;
	}

	public void setDescripcionCausaReiniciarHoja(
			String descripcionCausaReiniciarHoja) {
		this.descripcionCausaReiniciarHoja = descripcionCausaReiniciarHoja;
	}

	// End Refactoring changes properties
}