package gehos.ensayo.ensayo_extraccion.session;

import gehos.bitacora.entity.TrazaAccion;
import gehos.bitacora.entity.TrazaAtributoModificado;
import gehos.bitacora.entity.TrazaModuloAccedido;
import gehos.bitacora.entity.TrazaSession;
import gehos.bitacora.session.traces.IBitacora;
import gehos.bitacora.treebuilders.model.AccionRealizadaWrapper;
import gehos.bitacora.treebuilders.model.Anno;
import gehos.bitacora.treebuilders.model.AtributoModificadoWrapper;
import gehos.bitacora.treebuilders.model.Dia;
import gehos.bitacora.treebuilders.model.IP;
import gehos.bitacora.treebuilders.model.ITreeData;
import gehos.bitacora.treebuilders.model.Mes;
import gehos.bitacora.treebuilders.model.ModuloAccedidoWrapper;
import gehos.bitacora.treebuilders.model.SessionWrapper;
import gehos.bitacora.treebuilders.model.Usuario;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.theme.Theme;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.NodeExpandedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

//import gehos.ensayo.ensayo_estadisticas.session.reportes.ReporteEstudioSource;

@Scope(ScopeType.CONVERSATION)
@Name("trazasControladora")
public class TrazasControladora {

	@In
	private EntityManager entityManager;
	@In
	IBitacora bitacora;
	private TreeNode treeData;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	@In
	gehos.autenticacion.entity.Usuario user;
	@In
	SeguridadEstudio seguridadEstudio;
	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;
	// Criterios de busqueda
	private String anno = "";
	private String mes = "";
	private String dia = "";
	private String ip = "";
	private String usuario = "";
	private String modulo = "";
	private Map parsPedro;
	private Boolean flag = false;
	private Boolean flag2 = true;
	private String pathExportedReport = "";

	public String getPathExportedReport() {
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}

	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	

	List<TrazasControladoraSource> listaPedro = new ArrayList<TrazasControladoraSource>(); // listaPedro
																							// ->
																							// disimulando
																							// ctrl+c...
																							// LOL
	List<TrazasControladoraSource> listaTemp = new ArrayList<TrazasControladoraSource>();

	List<TrazasAcc> acciones = new ArrayList<TrazasAcc>();

	protected String querySelect;
	protected String queryFrom;
	protected String queryWhere;
	protected String queryOrder;
	protected String query;
	protected String tipo_reporte;
	
    private String ensayo1 = SeamResourceBundle.getBundle().getString("ensayosClinicos");
	

	private String fileformatToExport;
	private List<String> filesFormatCombo;

	public List<String> getFilesFormatCombo() {
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}

	// protected Map<Integer, Object> columnas;
	protected Map<String, Object> queryParameters;

	// Array que contiene datos importantes para imprimir
	private Object[] printingResult;

	@SuppressWarnings("rawtypes")
	@Out(value = "causaCancelacionList_estadisticas", scope = ScopeType.CONVERSATION)
	private List causaCancelacionList = new ArrayList();
	private String pathToReport;
	@SuppressWarnings("rawtypes")
	@Out(value = "cirugiasList_estadisticas", scope = ScopeType.CONVERSATION)
	private List cirugiasList = new ArrayList();

	private Integer cantResults = -1;

	public String getPathToReport() {
		return pathToReport;
	}

	public void setPathToReport(String pathToReport) {
		this.pathToReport = pathToReport;
	}

	public String getTipo_reporte() {
		return tipo_reporte;
	}

	public void setTipo_reporte(String tipo_reporte) {
		this.tipo_reporte = tipo_reporte;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Boolean getFlag2() {
		return flag2;
	}

	public void setFlag2(Boolean flag2) {
		this.flag2 = flag2;
	}

	public String getNoResult() {
		return noResult;
	}

	public void setNoResult(String noResult) {
		this.noResult = noResult;
	}

	public Map<String, Object> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(Map<String, Object> queryParameters) {
		this.queryParameters = queryParameters;
	}
	
	public String getEnsayo() {
		return ensayo1;
	}

	public void setEnsayo(String ensayo1) {
		this.ensayo1 = ensayo1;
	}

	@SuppressWarnings("rawtypes")
	public List getAnnos() {
		return annos;
	}

	@SuppressWarnings("rawtypes")
	public List getIps() {
		return IPS();
	}

	@SuppressWarnings("rawtypes")
	public List getUsuarios() {
		return llenarUsers();
	}

	@SuppressWarnings("rawtypes")
	public void setAnnos(List annos) {
		this.annos = annos;
	}

	List<String> subReportsList;
	@SuppressWarnings("rawtypes")
	List<Collection> subReportsDataSourceList;
	@SuppressWarnings("rawtypes")
	List<Map> subReportsParameters;
	List<String> aux = new ArrayList<String>();

	// private ArrayList<String> trazaPrint = new ArrayList<String>();

	// Mes seleccionado en el combo para buscar
	private Meses mesSeleccionado;
	// Listado de los anos por los que se puede buscar
	private List<String> annos = new ArrayList<String>();
	// Listado de dias del mesSeleccionado
	private List<String> dias = new ArrayList<String>();
	// Listado de los ip por los que se puede buscar
	private List<String> ips = new ArrayList<String>();
	// Listado de los usuarios por los que se puede buscar
	private List<String> usuarios = new ArrayList<String>();

	// llenar usuarios que se puede buscar
	public List<String> llenarUsers() {
		usuarios = entityManager
				.createQuery(
						"select distinct s.user.username from TrazaSession s order by s.user.username asc")
				.getResultList();
		usuarios.add(0, seleccione);
		return usuarios;
	}

	// llenar ips
	public List<String> IPS() {
		ips = entityManager
				.createQuery(
						"select distinct s.direccionIp from TrazaSession s order by s.direccionIp desc")
				.getResultList();
		ips.add(0, seleccione);
		return ips;
	}

	// Mapa con el nombre del modulo y sus hijos
	private HashMap<String, List<String>> modules = new HashMap<String, List<String>>();
	private List<String> listadoModulesNames = new ArrayList<String>();
	List<TrazasControladoraSource> listadatos = new ArrayList<TrazasControladoraSource>();

	private final String seleccione = SeamResourceBundle.getBundle().getString(
			"cbxSeleccionPorDefecto");

	private List<NodeExpandedEvent> nodeExpandedEvents = new ArrayList<NodeExpandedEvent>();

	public enum Meses {
		        ENERO(0, 31), FEBRERO(1, 29), MARZO(2, 31), ABRIL(
				3, 30), MAYO(4, 31), JUNIO(5, 30), JULIO(6, 31), AGOSTO(7, 31), SEPTIEMBRE(
				8, 30), OCTUBRE(9, 31), NOVIEMBRE(10, 30), DICIEMBRE(11, 31);

		int mes;
		int dias;

		Meses(int mes, int dias) {

			this.mes = mes;
			this.dias = dias;
		}

	}

	@SuppressWarnings("unchecked")
	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void buildBitacoraTree() {

		if (annos.isEmpty())
			llenarAnnos();
		if (modules.isEmpty())
			listadoModulos();

		treeData = new TreeNodeImpl();
		final String orderBy = "order by s.anno desc";
		String query = "select distinct s.anno from TrazaSession s ";
		List<Integer> annos = null;

		filesFormatCombo = reportManager.fileFormatsToExport();

		bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle()
				.getString("bitConsulta"));

		// Si se busca por anno
		if (anno != null && !anno.isEmpty() && !anno.equals(seleccione)) {
			query += " where s.anno =:anno " + orderBy;
			annos = entityManager.createQuery(query)
					.setParameter("anno", Integer.parseInt(anno))
					.getResultList();

		} else {
			annos = entityManager.createQuery(
					"select distinct s.anno from TrazaSession s " + orderBy)
					.getResultList();
		}

		for (int i = 0; i < annos.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode annoNode = new TreeNodeImpl();
			annoNode.setData(new Anno(annos.get(i)));
			annoNode.addChild("...", loadingNode);

			treeData.addChild(annos.get(i), annoNode);
		}
	}

	@Out(scope = ScopeType.CONVERSATION, value = "subtitulo_Estadisticas")
	private String subtitulo = "";

	public String exportAccion() {
		exportReportToFileFormat();
		if (pathExportedReport == null)
			return "return false;";
		if (!pathExportedReport.equals("")) {
			listaTemp.clear();
			fileformatToExport = "";
			return "window.open('"
					+ FacesContext.getCurrentInstance().getExternalContext()
							.getRequestContextPath() + pathExportedReport
					+ "'); Richfaces.hideModalPanel('trazaExportPanel')";
		}
		return "return false;";
	}

	// Se implementa el requisito funcional 4: Permite exportar las trazas
	// generadas en el sistema al formato WORD, EXCEL y PDF.
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		
		
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportTrazas",
					parsPedro, listaTemp, FileType.PDF_FILE);
			
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportTrazas",
					parsPedro, listaTemp, FileType.RTF_FILE);
			
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportTrazas",
					parsPedro, listaTemp, FileType.EXCEL_FILE);
			
		}

	}

	@SuppressWarnings("unchecked")
	public void listadoModulos() {
		// la que pincha
		// listadoModulesNames = entityManager
		// .createQuery("select f.label from Funcionalidad f where f.label<>'Ensayos Clínicos' and f.esModulo = true and f.activo = true and f.eliminado = false and f.grupo = 'ensayo' and f.moduloFisico = false ").getResultList();

		// la original 1era// select f from Funcionalidad f where
		// f.funcionalidadPadre.id = 0 and f.esModulo = true and f.activo = true
		// and f.eliminado = false

		// la original 2da//select f from Funcionalidad f where f.esModulo =
		// true and f.activo = true and (f.eliminado = false or f.eliminado is
		// null)"+ " and f.entidad is not null
        
				
		List<Funcionalidad> listadoModulesRoot = (entityManager
				.createQuery(
						"select f from Funcionalidad f where f.label <> :ensayo and f.esModulo = true and f.activo = true and f.eliminado = false and f.grupo = 'ensayo' and f.moduloFisico = false and f.label <> 'Estad\u00EDsticas' and f.label <> 'Extracci\u00F3n' ")
						.setParameter("ensayo", ensayo1))
				.getResultList();

		Funcionalidad aux = (Funcionalidad) (entityManager
				.createQuery(
						" select f from Funcionalidad f where f.funcionalidadPadre.id = 0 and f.label<> :ensayo and f.esModulo = true and f.activo = true and f.eliminado = false ")
				      .setParameter("ensayo", ensayo1))
						.getSingleResult();
		listadoModulesRoot.add(aux);

		List<Funcionalidad> listadoModules = entityManager
				.createQuery(
						"select distinct f from Funcionalidad f where f.esModulo = true and f.activo = true and (f.eliminado = false or f.eliminado is null)"
								+ " and f.entidad is not null and f.label <> 'Estad\u00EDsticas' and f.label <> 'Extracci\u00F3n' ").getResultList();

		for (Funcionalidad funcionalidad : listadoModulesRoot) {

			List<String> modulosHijos = new ArrayList<String>();
			for (Iterator iterator = listadoModules.iterator(); iterator
					.hasNext();) {

				Funcionalidad fHija = (Funcionalidad) iterator.next();
				String fHijaNombre = fHija.getNombre();

				while (true) {
					try {
						if (fHija.getId() != funcionalidad.getId())
							fHija = fHija.getFuncionalidadPadre();
						else
							break;
					} catch (NullPointerException e) {
						fHijaNombre = null;
						break;
					}

				}
				if (fHijaNombre != null)
					modulosHijos.add(fHijaNombre);

			}
			if (funcionalidad.getNombre().equals("configuracion"))
				modules.put(funcionalidad.getLabel(),
						Arrays.asList("configuracion"));
			else
				modules.put(funcionalidad.getLabel(), modulosHijos);
		}
		listadoModulesNames.addAll(modules.keySet());
		listadoModulesNames.add(0, seleccione);
		Collections.sort(listadoModulesNames);

	}

	// Para llenar el combo de los meses
	public List<Meses> mesesCombo() {
		return Arrays.asList(Meses.values());
	}

	// Para actualizar el estado de los nodos en el visual
	public void updateNodesState() {
		for (NodeExpandedEvent nee : nodeExpandedEvents) {
			Object source = nee.getSource();
			UITree treee = (UITree) ((HtmlTree) source);

			if (treee == null) {
				return;
			}
			TreeState state = (TreeState) treee.getComponentState();
			try {
				state.collapseAll(treee);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Llena los annos
	@SuppressWarnings("deprecation")
	public void llenarAnnos() {
		if (annos == null || annos.isEmpty()) {
			int annoInicial = 2012;
			int annoActual = Calendar.getInstance().get(Calendar.YEAR);

			while (annoInicial <= annoActual) {
				annos.add(String.valueOf(annoInicial));
				annoInicial++;
			}

		}
		annos.add(0, seleccione);
	}

	public void llenarDias() {

		if (mesSeleccionado != null) {
			int cantDias = mesSeleccionado.dias;
			if ((dias.size() - 1) != cantDias) {
				dias = new ArrayList<String>();
				int count = 0;
				while (++count <= cantDias) {
					dias.add(String.valueOf(count));
				}
				if (!dias.contains(seleccione))
					dias.add(0, seleccione);

			}

		}
	}

	// Para buscar por los criterios seleccionados
	public void buscar() {

		// updateNodesState();
		// buildBitacoraTree();
	}

	public void limpiar() {
		anno = null;
		mes = null;
		dia = null;
		ip = null;
		usuario = null;
		modulo = null;
		mesSeleccionado = null;
		listaPedro.clear();
		// listaTemp.clear();
		// parsPedro.clear();
		// pathToReport="";
		pathExportedReport = "";
		flag = false;
		flag2 = false;
		buildBitacoraTree();

	}

	@SuppressWarnings("unchecked")
	public boolean OnNodeOpened(org.richfaces.component.UITree tree) {
		TreeNode selected = tree.getTreeNode();
		if (selected.getData() instanceof ITreeData) {
			ITreeData data = (ITreeData) selected.getData();
			return data.isExpanded();
		}
		return false;
	}

	// La talla que se expande
	@SuppressWarnings("unchecked")
	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {

		HtmlTree tree = (HtmlTree) event.getSource();
		TreeNode selected = tree.getTreeNode();
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			selected.addChild("...", loadingNode);
			((ITreeData) selected.getData()).setExpanded(false);
			nodeExpandedEvents.remove(event);
		}

		else {
			if (selected.getData() instanceof Anno) {
				anno = selected.getData().toString();
				expandAnno(selected);
			} else if (selected.getData() instanceof Mes) {
				mes = selected.getData().toString();
				expandMes(selected);
			} else if (selected.getData() instanceof Dia) {
				dia = selected.getData().toString();
				expandDia(selected);
			} else if (selected.getData() instanceof IP) {
				ip = selected.getData().toString();
				expandIp(selected);
			} else if (selected.getData() instanceof Usuario) {
				usuario = selected.getData().toString();
				expandUsuario(selected);
			} else if (selected.getData() instanceof SessionWrapper) {
				expandSession(selected);
			} else if (selected.getData() instanceof ModuloAccedidoWrapper) {
				expandModulo(selected);
			} else if (selected.getData() instanceof AccionRealizadaWrapper) {
				expandAccionRealizada(selected);
			}
			nodeExpandedEvents.add(event);
		}
	}

	@SuppressWarnings("unchecked")
	private void expandAccionRealizada(TreeNode selected) {
		AccionRealizadaWrapper value = ((AccionRealizadaWrapper) selected
				.getData());
		selected.removeChild("...");

		// select at from TrazaAtributoModificado at where at.trazaAccion.id =
		// 735 order by a.id asca
		// TrazaAtributoModificado[] acciones = new
		// TrazaAtributoModificado[value.getValue().getTrazaAtributoModificados().size()];
		// value.getValue().getTrazaAtributoModificados().toArray(acciones);

		List<TrazaAtributoModificado> acciones = entityManager
				.createQuery(
						"select at from TrazaAtributoModificado at where at.trazaAccion.id = :accionid order by at.id asc")
				.setParameter("accionid", value.getValue().getId())
				.getResultList();

		if (acciones.size() > 0) {
			AtributoModificadoWrapper w = new AtributoModificadoWrapper(false,
					acciones);

			TreeNode accionNode = new TreeNodeImpl();
			accionNode.setData(w);
			selected.addChild(w.hashCode(), accionNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandModulo(TreeNode selected) {
		ModuloAccedidoWrapper value = ((ModuloAccedidoWrapper) selected
				.getData());
		modulo = value.getValue().toString();
		selected.removeChild("...");

		List<TrazaAccion> acciones = entityManager
				.createQuery(
						"select a from TrazaAccion a where a.trazaModuloAccedido.id = :moduleid order by a.horaInicio desc")
				.setParameter("moduleid", value.getValue().getId())
				.getResultList();

		for (int i = 0; i < acciones.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode accionNode = new TreeNodeImpl();

			AccionRealizadaWrapper w = new AccionRealizadaWrapper(false,
					acciones.get(i));

			accionNode.setData(w);
			selected.addChild(w.hashCode(), accionNode);
			accionNode.addChild("...", loadingNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandSession(TreeNode selected) {
		SessionWrapper value = ((SessionWrapper) selected.getData());
		selected.removeChild("...");

		String query = "select m from TrazaModuloAccedido m where m.trazaSession.id = :sessionid ";
		final String orderBy = "order by m.hora desc";
		List<TrazaModuloAccedido> modulos = null;

		if (modulo != null && !modulo.isEmpty() && !modulo.equals(seleccione)) {
			query += "and m.modulo.nombre in (:lista) " + orderBy;

			modulos = entityManager
					.createQuery(query)
					.setParameter("sessionid", value.getValue().getId())
					.setParameter(
							"lista",
							modules.get(modulo).isEmpty() ? Arrays
									.asList("nada") : modules.get(modulo))
					.getResultList();

		} else {
			modulos = entityManager.createQuery(query + orderBy)
					.setParameter("sessionid", value.getValue().getId())
					.getResultList();
		}

		for (int i = 0; i < modulos.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode moduloNode = new TreeNodeImpl();

			ModuloAccedidoWrapper w = new ModuloAccedidoWrapper(false,
					modulos.get(i));

			moduloNode.setData(w);
			selected.addChild(w.hashCode(), moduloNode);
			moduloNode.addChild("...", loadingNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandUsuario(TreeNode selected) {
		Usuario value = ((Usuario) selected.getData());
		usuario = value.getValue().toString();
		selected.removeChild("...");

		List<TrazaSession> sessiones = entityManager
				.createQuery(
						"select distinct s from TrazaSession s "
								+ "where s.anno = :anno and s.mes = :mes and s.dia = :dia and s.direccionIp = :ip "
								+ "and s.user.username = :user order by s.horaInicio desc")
				.setParameter("anno", value.getAnno())
				.setParameter("mes", value.getMes())
				.setParameter("dia", value.getDia())
				.setParameter("ip", value.getIp())
				.setParameter("user", value.getValue()).getResultList();

		for (int i = 0; i < sessiones.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode sessionNode = new TreeNodeImpl();

			SessionWrapper w = new SessionWrapper(false, sessiones.get(i));

			sessionNode.setData(w);
			selected.addChild(w.hashCode(), sessionNode);
			sessionNode.addChild("...", loadingNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandDia(TreeNode selected) {
		Dia value = ((Dia) selected.getData());
		dia = value.getValue().toString();
		selected.removeChild("...");

		String query = "select distinct s.direccionIp from TrazaSession s where s.anno = :anno and s.mes = :mes and s.dia = :dia ";
		final String orderBy = "order by s.direccionIp desc ";
		List<String> ips = null;

		if (ip != null && !ip.isEmpty()) {

			query += "and s.direccionIp =:ip " + orderBy;
			ips = entityManager.createQuery(query)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getMes())
					.setParameter("dia", value.getValue())
					.setParameter("ip", ip).getResultList();
		} else {
			ips = entityManager.createQuery(query + orderBy)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getMes())
					.setParameter("dia", value.getValue()).getResultList();
		}

		for (int i = 0; i < ips.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode ipNode = new TreeNodeImpl();
			IP u = new IP(ips.get(i), value.getValue(), value.getMes(),
					value.getAnno(), false);
			ipNode.setData(u);
			selected.addChild(u.hashCode(), ipNode);
			ipNode.addChild("...", loadingNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandIp(TreeNode selected) {
		IP value = ((IP) selected.getData());
		ip = value.getValue().toString();
		/*
		 * anno = String.valueOf(value.getAnno()); mes=
		 * String.valueOf(value.getMes()); dia = String.valueOf(value.getDia());
		 */
		selected.removeChild("...");

		String query = "select distinct s.user.username from TrazaSession s "
				+ "where s.anno = :anno and s.mes = :mes and s.dia = :dia "
				+ "and s.direccionIp =:ip ";

		final String orderBy = "order by s.user.username desc";
		List<String> usuarios = null;

		if (usuario != null && !usuario.isEmpty()) {
			query += "and s.user.username =:user " + orderBy;

			usuarios = entityManager.createQuery(query)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getMes())
					.setParameter("dia", value.getDia())
					.setParameter("ip", value.getValue())
					.setParameter("user", usuario).getResultList();
		} else {
			usuarios = entityManager.createQuery(query + orderBy)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getMes())
					.setParameter("dia", value.getDia())
					.setParameter("ip", value.getValue()).getResultList();
		}

		for (int i = 0; i < usuarios.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode userNode = new TreeNodeImpl();
			Usuario u = new Usuario(usuarios.get(i), value.getDia(),
					value.getMes(), value.getAnno(), value.getValue(), false);
			userNode.setData(u);
			selected.addChild(u.hashCode(), userNode);
			userNode.addChild("...", loadingNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandMes(TreeNode selected) {
		Mes value = ((Mes) selected.getData());
		mes = value.getValue().toString();
		selected.removeChild("...");

		String query = "select distinct s.dia from TrazaSession s where s.anno = :anno and s.mes = :mes ";
		final String orderBy = "order by s.dia desc";

		List<Integer> dias = new ArrayList<Integer>();

		if (dia != null && !dia.isEmpty() && !dia.equals(seleccione)) {
			query += " and s.dia =:dia " + orderBy;

			dias = entityManager.createQuery(query)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getValue())
					.setParameter("dia", Integer.parseInt(dia)).getResultList();
		} else {
			dias = entityManager.createQuery(query + orderBy)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getValue()).getResultList();
		}

		for (int i = 0; i < dias.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode diaNode = new TreeNodeImpl();
			Dia d = new Dia(dias.get(i), value.getValue(), value.getAnno(),
					false);
			diaNode.setData(d);
			selected.addChild(d.hashCode(), diaNode);
			diaNode.addChild("...", loadingNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void expandAnno(TreeNode selected) {
		Integer value = ((Anno) selected.getData()).getValue();
		anno = value.toString();
		selected.removeChild("...");

		List<Integer> meses = null;
		String query = "select distinct s.mes from TrazaSession s where s.anno = :anno ";
		final String orderBy = "order by s.mes desc";

		// Se busca por mes
		if (mesSeleccionado != null && mesSeleccionado.mes != -1) {
			query += "and s.mes =:mes " + orderBy;
			meses = entityManager.createQuery(query)
					.setParameter("anno", value)
					.setParameter("mes", mesSeleccionado.mes).getResultList();

		} else {
			meses = entityManager
					.createQuery(
							"select distinct s.mes from TrazaSession s where s.anno = :anno "
									+ orderBy).setParameter("anno", value)
					.getResultList();
		}

		for (int i = 0; i < meses.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode mesNode = new TreeNodeImpl();
			Mes m = new Mes(meses.get(i), value, false);
			mesNode.setData(m);
			selected.addChild(m.hashCode(), mesNode);
			mesNode.addChild("...", loadingNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings("unchecked")
	private void prune(TreeNode selected) {
		ArrayList<Integer> hashcodes = new ArrayList<Integer>();
		for (Iterator iterator = selected.getChildren(); iterator.hasNext();) {
			java.util.Map.Entry obj = (java.util.Map.Entry) iterator.next();
			TreeNode node = (TreeNode) obj.getValue();
			prune(node);
			hashcodes.add(node.getData().hashCode());
		}
		for (int i = 0; i < hashcodes.size(); i++) {
			selected.removeChild(hashcodes.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	public TreeNode getTreeData() {
		return treeData;
	}

	@SuppressWarnings("unchecked")
	public void setTreeData(TreeNode treeData) {
		buildBitacoraTree();
		this.treeData = treeData;
	}

	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public Meses getMesSeleccionado() {
		return mesSeleccionado;
	}

	public void setMesSeleccionado(Meses mesSeleccionado) {
		this.mesSeleccionado = mesSeleccionado;
	}

	public List<String> getDias() {
		return dias;
	}

	public void setDias(List<String> dias) {
		this.dias = dias;
	}

	public List<String> getListadoModulesNames() {
		return listadoModulesNames;
	}

	public void setListadoModulesNames(List<String> listadoModulesNames) {
		this.listadoModulesNames = listadoModulesNames;
	}

	// Aqui se implementa el requisito funcional 1:Visualizar trazas de usuarios
	@SuppressWarnings("unchecked")
	public void Exportar() {
		
	if (anno != "" | mesSeleccionado != null | dia != "" | ip != ""
				| usuario != ""| modulo != "") {
			/*
			 * private String anno; private String mes; private String dia;
			 * private String ip; private String usuario; private String modulo;
			 */
			Integer day = 0;
			Integer month = 0;
			Integer year = 0;

			if (anno.equals("<Seleccione>"))
				anno = "";
			if (mes.equals("<Seleccione>"))
				mes = "";
			if (dia.equals("<Seleccione>"))
				dia = "";
			if (modulo.equals("<Seleccione>"))
				modulo = "";

			if (!dia.isEmpty()) {
				day = Integer.parseInt(dia);
			}
			if (mesSeleccionado != null) {
				month = mesSeleccionado.mes + 1;
			}
			if (anno != "") {
				year = Integer.parseInt(anno);
			}
			if (ip.equals("<Seleccione>")) {
				ip = "";
			}
			if (usuario.equals("<Seleccione>")) {
				usuario = "";
			}


			pathToReport = "";
			listaPedro = new ArrayList<TrazasControladoraSource>();
			listaTemp = new ArrayList<TrazasControladoraSource>();

			ArrayList<Object[]> trazasPrint = new ArrayList<Object[]>();

			// dia
			if (anno == "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery("select str(s.id),s.user.username,str(s.fechaInicio)"
								+ ",str(s.horaInicio),s.direccionIp, m.nombre, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
								+ " from TrazaSession s "
								+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
								+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia")
						.setParameter("dia", day)).getResultList();

			// mes
			if (anno.equals("") && mesSeleccionado != null && dia.equals("")
					&& ip.equals("") && usuario.equals("") && modulo.equals(""))
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery("select str(s.id),s.user.username,str(s.fechaInicio)"
								+ ",str(s.horaInicio),s.direccionIp, m.nombre, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
								+ " from TrazaSession s "
								+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
								+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes")
						.setParameter("mes", month)).getResultList();

			// anno
			if (anno != "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager.createQuery("select str(s.id),s.user.username,str(s.fechaInicio) "
								+ ",str(s.horaInicio),s.direccionIp, m.nombre, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id) "
								+ " from TrazaSession s "
								+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
								+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno ")
						.setParameter("anno", year)).getResultList();

			// ip
			if (anno == "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.nombre, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.direccionIp=:ip")
						.setParameter("ip", ip).getResultList());

			// usuario
			if (anno == "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery("select str(s.id),s.user.username,str(s.fechaInicio)"
								+ ",str(s.horaInicio),s.direccionIp, m.nombre, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
								+ " from TrazaSession s "
								+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
								+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario")
						.setParameter("usuario", usuario)).getResultList();
			// modulo
			if (anno == "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where m.label=:modulo")
						.setParameter("modulo", modulo.toString())
						.getResultList());

			// dia y mes
			if (anno == "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and month(s.fechaInicio)=:mes")
						.setParameter("dia", day).setParameter("mes", month))
						.getResultList();
			// dia y anno
			if (anno != "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno"
										+ "and day(s.fechaInicio)=:dia")
						.setParameter("anno", year).setParameter("dia", day)
						.getResultList());
			// dia y ip
			if (anno == "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and s.direccionIp=:ip ")
						.setParameter("dia", day).setParameter("ip", ip)
						.getResultList());
			// dia y usuario
			if (anno == "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and s.user.username=:usuario")
						.setParameter("dia", day)
						.setParameter("usuario", usuario).getResultList());
			// dia y modulo
			if (anno == "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and m.label=:modulo ")
						.setParameter("dia", day)
						.setParameter("modulo", modulo).getResultList());
			// mes y modulo
			if (anno == "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and m.label=:modulo ")
						.setParameter("mes", month)
						.setParameter("modulo", modulo).getResultList());

			// mes y anno
			if (anno != "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes")
						.setParameter("anno", year).setParameter("mes", month))
						.getResultList();
			// mes y usuario
			if (anno == "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.nombre, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario and month(s.fechaInicio)=:mes")
						.setParameter("usuario", usuario).setParameter("mes",
						month)).getResultList();
			// usuario y modulo
			if (anno == "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where m.label=:modulo and s.user.username=:usuario")
						.setParameter("modulo", modulo).setParameter("usuario",
						usuario)).getResultList();

			// usuario e ip
			if (anno == "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario and s.direccionIp=:ip")
						.setParameter("usuario", usuario)
						.setParameter("ip", ip)).getResultList();

			// anno y usuario
			if (anno != "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario and year(s.fechaInicio)=:anno")
						.setParameter("usuario", usuario).setParameter("anno",
						year)).getResultList();

			// anno y modulo
			if (anno != "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and m.label=:modulo ")
						.setParameter("anno", year)
						.setParameter("modulo", modulo).getResultList());
			// ip y modulo
			if (anno == "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where m.label=:modulo and s.direccionIp=:ip ")
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());

			// anno e ip
			if (anno != "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.direccionIp=:ip and year(s.fechaInicio)=:anno")
						.setParameter("ip", ip).setParameter("anno", year))
						.getResultList();

			// mes e ip
			if (anno == "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.direccionIp=:ip and month(s.fechaInicio)=:mes")
						.setParameter("ip", ip).setParameter("mes", month))
						.getResultList();

			// anno mes y dia
			if (anno != "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes "
										+ "and day(s.fechaInicio)=:dia")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("dia", day).getResultList());
			// dia mes y modulo
			if (anno == "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes "
										+ "and day(s.fechaInicio)=:dia and m.label=:modulo ")
						.setParameter("mes", month).setParameter("dia", day)
						.setParameter("modulo", modulo).getResultList());

			// dia anno e ip
			if (anno != "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and day(s.fechaInicio)=:dia and s.direccionIp=:ip ")
						.setParameter("anno", year).setParameter("dia", day)
						.setParameter("ip", ip).getResultList());
			// dia anno y usuario
			if (anno != "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and day(s.fechaInicio)=:dia and s.user.username=:usuario")
						.setParameter("anno", year).setParameter("dia", day)
						.setParameter("usuario", usuario).getResultList());
			// dia anno y modulo
			if (anno != "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and day(s.fechaInicio)=:dia and m.label=:modulo ")
						.setParameter("anno", year).setParameter("dia", day)
						.setParameter("modulo", modulo).getResultList());
			// dia ip y usuario
			if (anno == "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("dia", day)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// dia ip y modulo
			if (anno == "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip ")
						.setParameter("dia", day)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());
			// dia usuario y modulo
			if (anno == "" && mesSeleccionado == null && dia != "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where day(s.fechaInicio)=:dia and m.label=:modulo and s.user.username=:usuario")
						.setParameter("dia", day)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario).getResultList());
			// mes anno e ip
			if (anno != "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and s.direccionIp=:ip ")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("ip", ip).getResultList());
			// ip usuario y modulo
			if (anno == "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// usuario modulo y mes
			if (anno == "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and m.label=:modulo and s.user.username=:usuario")
						.setParameter("mes", month)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario).getResultList());
			// modulo mes y anno
			if (anno != "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and m.label=:modulo ")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("modulo", modulo).getResultList());
			// mes anno y usuario
			if (anno != "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("usuario", usuario).getResultList());
			// anno ip y modulo
			if (anno != "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and m.label=:modulo and s.direccionIp=:ip ")
						.setParameter("anno", year)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());
			// usuario modulo y anno
			if (anno != "" && mesSeleccionado == null && dia == "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and m.label=:modulo and s.user.username=:usuario")
						.setParameter("anno", year)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario).getResultList());
			// modulo mes e ip
			if (anno == "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and m.label=:modulo and s.direccionIp=:ip ")
						.setParameter("mes", month)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());

			// anno usuario e ip
			if (anno != "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.direccionIp=:ip and year(s.fechaInicio)=:anno and s.user.username=:usuario")
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).setParameter("anno", year))
						.getResultList();

			// mes dia y usuario
			if (anno == "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia")
						.setParameter("dia", day)
						.setParameter("usuario", usuario).setParameter("mes",
						month)).getResultList();

			// mes dia e ip
			if (anno == "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.direccionIp=:ip and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia")
						.setParameter("dia", day).setParameter("ip", ip)
						.setParameter("mes", month)).getResultList();

			// mes usuario e ip
			if (anno == "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario and s.direccionIp=:ip and month(s.fechaInicio)=:mes")
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).setParameter("mes", month))
						.getResultList();

			// dia mes anno usuario
			if (anno != "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and s.user.username=:usuario and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia")
						.setParameter("anno", year)
						.setParameter("usuario", usuario)
						.setParameter("dia", day).setParameter("mes", month))
						.getResultList();

			// dia mes anno ip
			if (anno != "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario == "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and s.direccionIp=:ip and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia")
						.setParameter("anno", year).setParameter("ip", ip)
						.setParameter("dia", day).setParameter("mes", month))
						.getResultList();

			// anno mes dia modulo
			if (anno != "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("dia", day)
						.setParameter("modulo", modulo).getResultList());
			// dia mes ip usuario
			if (anno == "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("dia", day)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// dia mes ip modulo
			if (anno == "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip ")
						.setParameter("mes", month).setParameter("dia", day)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());
			// dia mes modulo usuario
			if (anno == "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("dia", day)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario).getResultList());
			// mes anno ip usuario
			if (anno != "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// ano ip usuario modulo
			if (anno != "" && mesSeleccionado == null && dia == "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("anno", year)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// mes ip usuario modulo
			if (anno == "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("mes", month)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// anno mes usuario modulo
			if (anno != "" && mesSeleccionado != null && dia == "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and m.label=:modulo and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario).getResultList());
			// anno mes ip modulo
			if (anno != "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and m.label=:modulo and s.direccionIp=:ip ")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());
			// dia anno ip usuario
			if (anno != "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and day(s.fechaInicio)=:dia and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("anno", year).setParameter("dia", day)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// dia anno ip mod
			if (anno != "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and day(s.fechaInicio)=:dia and m.nombre=:modulo and s.direccionIp=:ip ")
						.setParameter("anno", year).setParameter("dia", day)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());

			// anno mes dia ip modulo
			if (anno != "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario == "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("dia", day)
						.setParameter("modulo", modulo).setParameter("ip", ip)
						.getResultList());

			// dia mes anno ip usuario
			if (anno != "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario != "" && modulo == "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where s.user.username=:usuario and  s.direccionIp=:ip and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and year(s.fechaInicio)=:anno")
						.setParameter("anno", year)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).setParameter("dia", day)
						.setParameter("mes", month)).getResultList();

			// anno mes dia usuario modulo
			if (anno != "" && mesSeleccionado != null && dia != "" && ip == ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("dia", day)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario).getResultList());
			// anno mes ip usuario modulo
			if (anno != "" && mesSeleccionado != null && dia == "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// dia mes ip usuario modulo
			if (anno == "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("dia", day)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());
			// anno dia ip usuario modulo
			if (anno != "" && mesSeleccionado == null && dia != "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("anno", year).setParameter("dia", day)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());

			// general
			if (anno != "" && mesSeleccionado != null && dia != "" && ip != ""
					&& usuario != "" && modulo != "")
				trazasPrint = (ArrayList<Object[]>) (entityManager
						.createQuery(
								"select str(s.id),s.user.username,str(s.fechaInicio)"
										+ ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
										+ " from TrazaSession s "
										+ "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
										+ "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes and day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario")
						.setParameter("mes", month).setParameter("anno", year)
						.setParameter("dia", day)
						.setParameter("modulo", modulo)
						.setParameter("usuario", usuario)
						.setParameter("ip", ip).getResultList());

			// general (copia)
			// if(anno!="" && mesSeleccionado!=null && dia!="" && ip!="" &&
			// usuario!="" && modulo!="")
			// trazasPrint = (ArrayList<Object[]>)
			// (entityManager.createQuery("select str(s.id),s.user.username,str(s.fechaInicio)"
			// +
			// ",str(s.horaInicio),s.direccionIp, m.label, tac.accionRealizada, str(tam.id), tam.entidad, str(tam.idEntidad), tam.atributo, tam.valorAntes, tam.valorDespues, str(tac.id)"
			// + " from TrazaSession s "
			// +
			// "inner join s.trazaModuloAccedidos tma inner join tma.modulo m "
			// +
			// "inner join tma.trazaAccions tac inner join tac.trazaAtributoModificados tam where year(s.fechaInicio)=:anno and month(s.fechaInicio)=:mes "
			// +
			// "and day(s.fechaInicio)=:dia and m.label=:modulo and s.direccionIp=:ip and s.user.username=:usuario").setParameter("mes",
			// month).setParameter("anno", year)
			// .setParameter("dia", day).setParameter("modulo",
			// modulo).setParameter("usuario", usuario).setParameter("ip",
			// ip).getResultList());

			if (trazasPrint.size() > 0) {
				int cont = 0;
				TrazasControladoraSource tmp = new TrazasControladoraSource();
				TrazasAcc tmp1 = new TrazasAcc();
				for (int i = 0; i < trazasPrint.size(); i++) {

					if (i == 0) {
						cont++;
						String usuarioT = trazasPrint.get(i)[1].toString();
						String fechaI = trazasPrint.get(i)[2].toString();
						String horaI = trazasPrint.get(i)[3].toString();
						//String ipD = trazasPrint.get(i)[4].toString();
						String moduloT = trazasPrint.get(i)[5].toString();
						String accion = trazasPrint.get(i)[6].toString();
						//String idTrazaMod = trazasPrint.get(i)[7].toString();
						String entidad = trazasPrint.get(i)[8].toString();
						//String idEntidad = trazasPrint.get(i)[9].toString();
						String atributo = trazasPrint.get(i)[10].toString();
						String valorAntes = "";
						String valorDespues = "";
						String idAcc = trazasPrint.get(i)[13].toString();

						try {
							valorAntes = trazasPrint.get(i)[11].toString();
							valorDespues = trazasPrint.get(i)[12].toString();
						} catch (Exception e) {
							System.out.println(e);
						}

						tmp.setNo(String.valueOf(cont));
						tmp.setUsuario(usuarioT);
						tmp.setFecha_inicio(fechaI);
						tmp.setHora_inicio(horaI);
					    
						tmp.setModulo(moduloT);
						tmp.setAccion_realizada(accion);
						tmp.setId_accion(idAcc);
						String usr = FacesContext.getCurrentInstance()
								.getExternalContext().getRemoteUser();
						tmp.setRol_genera(usr);

						tmp1.setValor_antes(valorAntes);
						tmp1.setValor_despues(valorDespues);
						
						tmp1.setEntidad(entidad);
					
						tmp1.setAtributo(atributo);
						acciones.add(tmp1);
						tmp.setAcciones(acciones);
						listaPedro.add(tmp);
						listaTemp.add(tmp);

					}

					else if ((trazasPrint.get(i)[13].toString()
							.equals(trazasPrint.get(i - 1)[13].toString()))) {
						/*
						 * String usuarioT = trazasPrint.get(i)[1].toString();
						 * String fechaI = trazasPrint.get(i)[2].toString();
						 * String horaI = trazasPrint.get(i)[3].toString();
						 * String ipD = trazasPrint.get(i)[4].toString(); String
						 * moduloT = trazasPrint.get(i)[5].toString(); String
						 * accion = trazasPrint.get(i)[6].toString();
						 */
						//String idTrazaMod = trazasPrint.get(i)[7].toString();
						String entidad = trazasPrint.get(i)[8].toString();
						//String idEntidad = trazasPrint.get(i)[9].toString();
						String atributo = trazasPrint.get(i)[10].toString();
						String valorAntes = "";
						String valorDespues = "";
						// String idAcc = trazasPrint.get(i)[13].toString();

						try {
							valorAntes = trazasPrint.get(i)[11].toString();
							valorDespues = trazasPrint.get(i)[12].toString();
						} catch (Exception e) {
							System.out.println(e);
						}

						// TrazasControladoraSource tmp = new
						// TrazasControladoraSource();
						tmp1 = new TrazasAcc();
						/*
						 *tmp.setNo(""); tmp.setUsuario("");
						 *tmp.setFecha_inicio(""); tmp.setHora_inicio("");
						 * tmp.setDireccion_ip(""); tmp.setModulo("");
						 * tmp.setAccion_realizada(""); tmp.setId_accion(idAcc);
						 * String usr =
						 * FacesContext.getCurrentInstance().getExternalContext
						 * ().getRemoteUser(); tmp.setRol_genera(usr);
						 */

						tmp1.setValor_antes(valorAntes);
						tmp1.setValor_despues(valorDespues);
						//tmp1.setId_trazaMOD(idTrazaMod);
						tmp1.setEntidad(entidad);
						//tmp1.setId_Entidad(idEntidad);
						tmp1.setAtributo(atributo);
						acciones.add(tmp1);
						listaPedro.get(cont - 1).setAcciones(acciones);

						// listaPedro.add(tmp);
						// listaTemp.add(tmp);

					} else {
						cont++;
						String usuarioT = trazasPrint.get(i)[1].toString();
						String fechaI = trazasPrint.get(i)[2].toString();
						String horaI = trazasPrint.get(i)[3].toString();
						//String ipD = trazasPrint.get(i)[4].toString();
						String moduloT = trazasPrint.get(i)[5].toString();
						String accion = trazasPrint.get(i)[6].toString();

						tmp = new TrazasControladoraSource();
						tmp1 = new TrazasAcc();
						tmp.setNo(String.valueOf(cont));
						tmp.setUsuario(usuarioT);
						tmp.setFecha_inicio(fechaI);
						tmp.setHora_inicio(horaI);
						//tmp.setDireccion_ip(ipD);
						tmp.setModulo(moduloT);
						tmp.setAccion_realizada(accion);
						//tmp.setId_accion(trazasPrint.get(i)[13].toString());
						String usr = FacesContext.getCurrentInstance()
								.getExternalContext().getRemoteUser();
						tmp.setRol_genera(usr);
						//String idTrazaMod = trazasPrint.get(i)[7].toString();
						String entidad = trazasPrint.get(i)[8].toString();
						//String idEntidad = trazasPrint.get(i)[9].toString();
						String atributo = trazasPrint.get(i)[10].toString();
						String valorAntes = "";
						String valorDespues = "";
						String idAcc = trazasPrint.get(i)[13].toString();
						tmp.setId_accion(idAcc);

						try {
							valorAntes = trazasPrint.get(i)[11].toString();
							valorDespues = trazasPrint.get(i)[12].toString();
						} catch (Exception e) {
							System.out.println(e);
						}
						acciones = new ArrayList<TrazasAcc>();

						tmp1.setValor_antes(valorAntes);
						tmp1.setValor_despues(valorDespues);
						//tmp1.setId_trazaMOD(idTrazaMod);
						tmp1.setEntidad(entidad);
						//tmp1.setId_Entidad(idEntidad);
						tmp1.setAtributo(atributo);
						acciones.add(tmp1);
						tmp.setAcciones(acciones);
						listaPedro.add(tmp);
						listaTemp.add(tmp);

					}

				}

				parsPedro = new HashMap();
				parsPedro.put("numero", SeamResourceBundle.getBundle()
						.getString("numero"));
				parsPedro.put("user",
						SeamResourceBundle.getBundle().getString("user"));
				parsPedro.put("fecha", SeamResourceBundle.getBundle()
						.getString("fecha"));
				parsPedro.put("hora_i", SeamResourceBundle.getBundle()
						.getString("hora_i"));
				//parsPedro.put("direccion", SeamResourceBundle.getBundle()
						//.getString("direccion"));
				parsPedro.put("mod",
						SeamResourceBundle.getBundle().getString("mod"));
				parsPedro.put("accion", SeamResourceBundle.getBundle()
						.getString("accion"));
				//parsPedro.put("id_traza_mod", SeamResourceBundle.getBundle()
						//.getString("id_traza_mod"));
				parsPedro.put("enti",
						SeamResourceBundle.getBundle().getString("enti"));
				//parsPedro.put("id_enti", SeamResourceBundle.getBundle()
						//.getString("id_enti"));
				parsPedro.put("atrib", SeamResourceBundle.getBundle()
						.getString("atrib"));
				parsPedro.put("v_antes", SeamResourceBundle.getBundle()
						.getString("v_antes"));
				parsPedro.put("v_despues", SeamResourceBundle.getBundle()
						.getString("v_despues"));
				//parsPedro.put("tras_sis", SeamResourceBundle.getBundle()
				//		.getString("tras_sis"));

				pathToReport = reportManager.ExportReport("reportTrazas",
						parsPedro, listaPedro, FileType.HTML_FILE);
				listaPedro = new ArrayList<TrazasControladoraSource>();
				

				flag = true;
				flag2 = false;
				trazasPrint.clear();
			} else {
				noResult = SeamResourceBundle.getBundle().getString(
						"noResult2");
				flag = false;
				flag2 = true;
			}
			
			anno = "";
			mesSeleccionado = null;
			dia = "";
			ip = "";
			usuario = "";
			modulo = "";
			
		} else {
			noResult = SeamResourceBundle.getBundle().getString(
					"noResult3");
			flag2 = true;
			flag = false;
		}
		
	  
		
	}

	/**
	 * Autocompletamiento (las mieles)
	 */

	public List<String> autoCompleteUser(Object o) {
		List<String> list = entityManager.createQuery(
				"select s.user.name from TrazaSession s").getResultList();
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			if (!ret.contains(list.get(i))
					&& list.get(i).toLowerCase()
							.contains(o.toString().toLowerCase())) {
				ret.add(list.get(i));
			}
		}
		return ret;
	}

	public List<String> autoCompleteIP(Object o) {

		List<String> list = entityManager.createQuery(
				"select s.direccionIp from TrazaSession s").getResultList();
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			if (!ret.contains(list.get(i))
					&& list.get(i).toLowerCase()
							.contains(o.toString().toLowerCase())) {
				ret.add(list.get(i));
			}
		}

		return ret;

	}

	public List<TrazasControladoraSource> getListaTemp() {
		return listaTemp;
	}

	public void setListaTemp(List<TrazasControladoraSource> listaTemp) {
		this.listaTemp = listaTemp;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getFileformatToExport() {
		return fileformatToExport;
	}

	public void setFileformatToExport(String fileformatToExport) {
		this.fileformatToExport = fileformatToExport;
	}

	public List<TrazasControladoraSource> getListaPedro() {
		return listaPedro;
	}

	public void setListaPedro(List<TrazasControladoraSource> listaPedro) {
		this.listaPedro = listaPedro;
	}

}