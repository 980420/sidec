package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.wrappers.EntidadWrapper;
import gehos.ensayo.ensayo_configuracion.session.custom.wrappers.IWrapper;
import gehos.ensayo.entity.DireccionIp_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.bouncycastle.util.IPAddress;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.theme.Theme;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import sun.net.util.IPAddressUtil;

@Name("asignaripControlador")
public class AsignarIP {

	/**
	 * Para almacenar los IP de la entidad
	 */

	// Almacena el ip asignado a una entida
	private String ipString;
	// Almacena el ip de inicio del rango de ip asignado a una entidad
	private String ipInicioString;
	// Almacena el ip de fin del rango de ip asignado a una entidad
	private String ipFinalString;

	/**
	 * Para manejar los datos en la BD
	 */
	@In
	private EntityManager entityManager;

	/**
	 * Para manejar los mensajes a mostrar
	 * 
	 */
	@In
	FacesMessages facesMessages;

	/**
	 * Gestionar las trazas en la bitacora
	 */
	protected @In
	IBitacora bitacora;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
		ipString = "";
		ipInicioString = "";
		ipFinalString = "";

	}

	public void clean() {
		ipString = "";
		ipInicioString = "";
		ipFinalString = "";
	}

	public String getIpString() {
		return ipString;
	}

	public void setIpString(String ipString) {
		this.ipString = ipString;
	}

	public String getIpInicioString() {
		return ipInicioString;
	}

	public void setIpInicioString(String ipInicioString) {
		this.ipInicioString = ipInicioString;
	}

	public String getIpFinalString() {
		return ipFinalString;
	}

	public void setIpFinalString(String ipFinalString) {
		this.ipFinalString = ipFinalString;
	}

	// #region Propiedades del arbol
	/**
	 * Arbol principal que contiene la relacion de todas las entidades
	 */
	private TreeNode treeRootData;

	/**
	 * Obtener arbol principal que contiene la relacion de todas las entidades
	 * 
	 * @return Devuelve el arbol
	 */
	public TreeNode getTreeRootData() {
		return treeRootData;
	}

	/**
	 * Asigna arbol principal que contiene la relacion de todas las entidades
	 * 
	 * @param treeDataque
	 */
	public void setTreeRootData(TreeNode treeData) {
		this.treeRootData = treeData;
	}

	/**
	 * Nodo seleccionado en el arbol
	 */
	private IWrapper selectedItem;

	/**
	 * Obtener nodo seleccionado en el arbol
	 * 
	 * @return
	 */
	public IWrapper getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Asigna Nodo seleccionado en el arbol
	 * 
	 * @param selectedItem
	 */
	public void setSelectedItem(IWrapper selectedItem) {
		this.selectedItem = selectedItem;
		this.entidadSelected = -11l;

		if (this.selectedItem == null)
			loadData();
	}

	long entidadSelected = -11l;

	public void setEntidadSelected(long entidadSelected, boolean selectedNull) {
		this.entidadSelected = entidadSelected;
		if (selectedNull)
			this.selectedItem = null;
	}

	public long getEntidadSelected() {
		return entidadSelected;
	}

	public String NombreEntidadSelected() {
		try {
			Entidad_ensayo ensayo = (Entidad_ensayo) entityManager.find(
					Entidad_ensayo.class, entidadSelected);
			return ensayo.getNombre() + "/" + ensayo.getNombre();
		} catch (Exception exception) {
			return "";
		}
	}

	// Metodos para crear el arbol
	@Create
	public void createROOT() {
		// Si el arbol raiz se encuentra null
		if (this.treeRootData == null) {
			loadData();
		}
	}

	/**
	 * Cargar todos los datos para el arbol inicial
	 * 
	 * @author Yero
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadData() {
		treeRootData = new TreeNodeImpl();

		// Cargo todos las entidades
		List<Entidad_ensayo> entidad = entityManager
				.createQuery(
						"from Entidad_ensayo ent where eliminado <> true order by ent.nombre")
				.getResultList();

		// Convierto las entidades al tipo EntidadWrapper
		for (int i = 0; i < entidad.size(); i++) {
			TreeNode estudioNode = new TreeNodeImpl();
			estudioNode.setData(new EntidadWrapper(entidad.get(i), false,
					entidad.get(i).getId()));
			treeRootData.addChild(entidad.get(i), estudioNode);
		}
	}

	// #endregion

	// Para la flecha de al lado de la entidad
	/**
	 * Utiliza este metodo para recibir el evento OnNodeCollapseExpand
	 * 
	 * @param event
	 */
	@SuppressWarnings("rawtypes")
	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		TreeNode selected = tree.getTreeNode();
	}

	// Ponen el icino de la entidad
	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;

	public String entidadIcon(EntidadWrapper node) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modCommon/entidades_logos/"
				+ theme.getTheme().get("name") + "/"
				+ theme.getTheme().get("color") + "/"
				+ node.getValue().getLogo();

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/generic.png";
	}

	public String entidadIcon(Entidad_ensayo node) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modCommon/entidades_logos/"
				+ theme.getTheme().get("name") + "/"
				+ theme.getTheme().get("color") + "/" + node.getLogo();

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/generic.png";
	}

	/**
	 * Retorna una lista de todos los Ips que tiene asignados una Entidad
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DireccionIp_ensayo> IPRango() {

		if (this.selectedItem instanceof EntidadWrapper) {

			List<DireccionIp_ensayo> ips = entityManager
					.createQuery(
							"select dip "
									+ " from DireccionIp_ensayo dip where dip.entidad.id=:entID"
									+ " and dip.eliminado <> true and dip.ipFinal <> ''"
									+ " order by dip.ip")
					.setParameter("entID", this.selectedItem.getId())
					.getResultList();

			return ips;
		}

		return null;
	}

	/**
	 * Retorna una lista de todos los Ips que tiene asignados una Entidad
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DireccionIp_ensayo> IPs() {

		if (this.selectedItem instanceof EntidadWrapper) {

			List<DireccionIp_ensayo> ips = entityManager
					.createQuery(
							"select dip "
									+ " from DireccionIp_ensayo dip where dip.entidad.id=:entID"
									+ " and dip.eliminado <> true and dip.ipFinal = ''"
									+ " order by dip.ip")
					.setParameter("entID", this.selectedItem.getId())
					.getResultList();

			return ips;
		}

		return null;
	}

	public String onComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("ipString").size() > 0
				|| facesMessages.getCurrentMessagesForControl("ipFinalString")
						.size() > 0
				|| facesMessages.getCurrentMessagesForControl("ipsString")
						.size() > 0) {
			return "return false;";

		} else {
			this.ipInicioString = "";
			this.ipFinalString = "";
			this.ipString = "";
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	/***
	 * Valida que la direccion IP sea correcta
	 * */

	public boolean validarIP() {
		Pattern patron = Pattern
				.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
						+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
						+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
						+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher matI = patron.matcher(ipString);

		if (!matI.matches()) {
			facesMessages.addToControlFromResourceBundle("ipsString",
					Severity.ERROR, "msg_ipNoValida");
			return false;
		} else {
			return true;
		}
	}

	/***
	 * Valida que la direccion IP de inicio y la direccion ip de fin sean
	 * correctas
	 * */

	public boolean validarRangoIP() {
		Pattern patron = Pattern
				.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
						+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
						+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
						+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher matI = patron.matcher(ipInicioString);
		Matcher matF = patron.matcher(ipFinalString);
		String[] ipI = ipInicioString.split("\\.");
		String[] ipF = ipFinalString.split("\\.");
		String ipInicio = "";
		for (int i = 0; i < ipI.length; i++) {
			ipInicio += ipI[i];
		}
		String ipFin = "";
		for (int i = 0; i < ipF.length; i++) {
			ipFin += ipF[i];
		}

		if (!matI.matches())
			facesMessages.addToControlFromResourceBundle("ipString",
					Severity.ERROR, "msg_ipNoValida");
		if (!matF.matches())
			facesMessages.addToControlFromResourceBundle("ipFinalString",
					Severity.ERROR, "msg_ipNoValida");
		if (!matI.matches() || !matF.matches()) {
			return false;
		} else {
			Long pinicio = Long.parseLong(ipInicio);
			Long pfin = Long.parseLong(ipFin);
			if (pinicio > pfin) {
				facesMessages.addToControlFromResourceBundle("ipString",
						Severity.ERROR, "msg_ipInMayor");
				return false;
			} else
				return true;
		}
	}

	/**
	 * Adiciona un nuevo rango de ip a la entidad seleccionada
	 */
	public void ipAdd(String modalPanel) {

		if (this.selectedItem instanceof EntidadWrapper) {

			DireccionIp_ensayo direcciones = new DireccionIp_ensayo();

			Entidad_ensayo et = this.entityManager.find(Entidad_ensayo.class,
					this.selectedItem.getId());
			try {
				if (modalPanel.equals("agregar_IP") && validarIP()) {
					direcciones.setIp(ipString);
					direcciones.setIpFinal(ipFinalString);
					direcciones.setEliminado(false);
					direcciones.setEntidad(et);
					direcciones.setCid(bitacora
							.registrarInicioDeAccion(SeamResourceBundle
									.getBundle().getString(
											"msg_bitAgregandoIP_ensClin")));
					this.entityManager.persist(direcciones);
					this.entityManager.flush();
				} else if (modalPanel.equals("agregar_rangoIP")
						&& validarRangoIP()) {
					direcciones.setIp(ipInicioString);
					direcciones.setIpFinal(ipFinalString);
					direcciones.setEliminado(false);
					direcciones.setEntidad(et);
					direcciones.setCid(bitacora
							.registrarInicioDeAccion(SeamResourceBundle
									.getBundle().getString(
											"msg_bitAgregandoRango_ensClin")));

					this.entityManager.persist(direcciones);
					this.entityManager.flush();
				} else {
					return;
				}

			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle("error",
						Severity.ERROR, "msg_errorinesperado_ensClin");
			}
		}
	}

	/**
	 * Recoge el id del IP que se desea eliminar
	 */
	long idIP = -11l;

	public void setIPToRemove(long iddell) {
		this.idIP = iddell;
	}

	/**
	 * Elimina el rango IP
	 */
	public void ipRemove() {

		if (this.idIP == -11l)
			return;

		try {

			// Tomo el IP con el ID
			DireccionIp_ensayo dir = (DireccionIp_ensayo) entityManager.find(
					DireccionIp_ensayo.class, idIP);

			dir.setCid(this.bitacora.registrarInicioDeAccion(SeamResourceBundle
					.getBundle().getString("msg_eliminar1_ensClin")));

			dir.setEliminado(true);

			entityManager.persist(dir);
			entityManager.flush();

			this.idIP = -11l;

			loadData();

		} catch (Exception exception) {
			this.facesMessages.add(exception.getMessage());
		}

	}

}
