package gehos.bitacora.treebuilders;


import gehos.bitacora.entity.Funcionalidad_Bitacora;
import gehos.bitacora.entity.TrazaAccion;
import gehos.bitacora.entity.TrazaAtributoModificado;
import gehos.bitacora.entity.TrazaModuloAccedido;
import gehos.bitacora.entity.TrazaSession;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.ejbca.cvc.example.Parse;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.NodeExpandedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.CONVERSATION)
@Name("bitacoraReview")
public class BitacoraReview {

	@In
	private EntityManager entityManager;

	private TreeNode treeData;

	// Criterios de busqueda
	private String anno;
	private String mes;
	private String dia;
	private String ip;
	private String usuario;
	private String modulo;
	
	
	private String mesSeleccionadoComboBox;//Mes seleccionado en el combobox
	private LinkedHashMap<Integer, String> meses = new LinkedHashMap<Integer, String>();// Mapa con los meses
	
	


	
	// Listado de los anos por los que se puede buscar
	private List<String> annos = new ArrayList<String>();
	// Listado de dias del mesSeleccionado
	private List<String> dias = new ArrayList<String>();

	// Mapa con el nombre del modulo y sus hijos
	private HashMap<String, List<String>> modules = new HashMap<String, List<String>>();
	private List<String> listadoModulesNames = new ArrayList<String>();

	private final String seleccione = SeamResourceBundle.getBundle().getString(
			"cbxSeleccionPorDefecto");

	private List<NodeExpandedEvent> nodeExpandedEvents = new ArrayList<NodeExpandedEvent>();

	
	
	public void inicializarMeses(){
		if(meses.isEmpty()){
			meses.put(-1, SeamResourceBundle.getBundle().getString("cbxSeleccionPorDefecto"));
			meses.put(0, SeamResourceBundle.getBundle().getString("month_0"));
			meses.put(1, SeamResourceBundle.getBundle().getString("month_1"));
			meses.put(2, SeamResourceBundle.getBundle().getString("month_2"));
			meses.put(3, SeamResourceBundle.getBundle().getString("month_3"));
			meses.put(4, SeamResourceBundle.getBundle().getString("month_4"));
			meses.put(5, SeamResourceBundle.getBundle().getString("month_5"));
			meses.put(6, SeamResourceBundle.getBundle().getString("month_6"));
			meses.put(7, SeamResourceBundle.getBundle().getString("month_7"));
			meses.put(8, SeamResourceBundle.getBundle().getString("month_8"));
			meses.put(9, SeamResourceBundle.getBundle().getString("month_9"));
			meses.put(10, SeamResourceBundle.getBundle().getString("month_10"));
			meses.put(11, SeamResourceBundle.getBundle().getString("month_11"));				
		}
	}

	@SuppressWarnings("unchecked")
	@Create
	@Begin(join = true)
	public void buildBitacoraTree() {
		inicializarMeses();
		llenarSiNadaEstaSeleccionado();

		if (annos.isEmpty())
			llenarAnnos();
		if (modules.isEmpty())
			listadoModulos();

		treeData = new TreeNodeImpl();
		final String orderBy = "order by s.anno desc";
		String query = "select distinct s.anno from TrazaSession s ";
		List<Integer> annos = null;

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

	@SuppressWarnings("unchecked")
	public void listadoModulos() {

		List<Funcionalidad> listadoModulesRoot = entityManager
				.createQuery(
						"select f from Funcionalidad f where f.funcionalidadPadre.id = 0 and f.esModulo = true and f.activo = true and f.eliminado = false ")
				.getResultList();

		List<Funcionalidad> listadoModules = entityManager
				.createQuery(
						"select f from Funcionalidad f where f.esModulo = true and f.activo = true and (f.eliminado = false or f.eliminado is null)"
								+ " and f.entidad is not null ")
				.getResultList();

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
			if(funcionalidad.getNombre().equals("configuracion"))
				modules.put(funcionalidad.getLabel(), Arrays.asList("configuracion"));
			else
				modules.put(funcionalidad.getLabel(), modulosHijos);
		}
		listadoModulesNames.addAll(modules.keySet());

	}
    //Listado de los meses
	public List<String> mesesComboBox(){
		List<String> result = new ArrayList<String>();
		result.addAll(meses.values());		
		return result;
		
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
	
	//Para saber el numero del mes seleccionado en el HashMap
	private int numeroMes(){
		Iterator entries = meses.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();		    
		    String value = (String)entry.getValue();
		    Integer key = (Integer)entry.getKey();
		    if (value.equals(getMesSeleccionadoComboBox())) {
		    	return key;
		    }
		    }
		return -1;
		
	}

	//Para actualizar la lista de los dias si cambia el año y el mes esta seleccionado
	public void activarListaDias(){
		int aux = numeroMes();
		if (aux != -1) {
			llenarDiasSegunMes();						
		}
	}
	
	
	//Para saber la cantidad de dias segun mes teniendo en cuenta el año
	public void llenarDiasSegunMes(){
		boolean parar = false;	
		
		Iterator entries = meses.entrySet().iterator();
		while (entries.hasNext() && !parar) {
		    Map.Entry entry = (Map.Entry) entries.next();		    
		    String value = (String)entry.getValue();
		    Integer key = (Integer)entry.getKey();
		    if (!key.equals(-1) && value.equals(getMesSeleccionadoComboBox())) {
		    	if (key.equals(0) || key.equals(2) || key.equals(4) || key.equals(6) || key.equals(7) || key.equals(9)|| key.equals(11)) {
		    		llenarListaDias(31);					
				}
		    	else if (key.equals(3) || key.equals(5) || key.equals(8) || key.equals(10)) {
		    		llenarListaDias(30);					
				}
		    	else {
		    		try {
		    			int criterioanno = Integer.parseInt(getAnno());
		    			if (((criterioanno%100 == 0) && (criterioanno%400 == 0)) || ((criterioanno%100 != 0) && (criterioanno%4 == 0))) {
		    				llenarListaDias(29);//año bisiesto
						}
		    			else {
		    				llenarListaDias(28);		    				
		    			}
						
					} catch (Exception e) {
						System.out.println(e.getMessage());
						llenarListaDias(29);//sin seleccionar año
						
					}
		    	
		    	}
		    	parar = true;
		    	
		    	
			}
	
		    
		}
		
	}
	
	//Llena la cantidad de dias del mes
	private void llenarListaDias(int pdias){
		dias = new ArrayList<String>();
		int count = 0;
		while (++count <= pdias) {
			dias.add(String.valueOf(count));
		}
		if (!dias.contains(seleccione))
			dias.add(0, seleccione);
		
	}
	
	public void llenarSiNadaEstaSeleccionado(){
		int aux = numeroMes();		
		if (aux == -1 || dias.size() == 0) {
			llenarListaDias(31);			
		}
	}


	// Para buscar por los criterios seleccionados
	public void buscar() {

		updateNodesState();
		buildBitacoraTree();
	}

	public void limpiar() {
		anno = null;
		mes = null;
		dia = null;
		ip = null;
		usuario = null;
		modulo = null;
		mesSeleccionadoComboBox = null;

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
				expandAnno(selected);
			} else if (selected.getData() instanceof Mes) {
				expandMes(selected);
			} else if (selected.getData() instanceof Dia) {
				expandDia(selected);
			} else if (selected.getData() instanceof IP) {
				expandIp(selected);
			} else if (selected.getData() instanceof Usuario) {
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
		// 735 order by a.id asc
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
		
		if(modulo != null && !modulo.isEmpty() && !modulo.equals(seleccione)){
			query += "and m.modulo.nombre in (:lista) " + orderBy;
			
			modulos = entityManager
					.createQuery(query)
					.setParameter("sessionid", value.getValue().getId())
					.setParameter("lista", modules.get(modulo).isEmpty()?Arrays.asList("nada") : modules.get(modulo))
					.getResultList();
			
		}
		else{
			modulos = entityManager
					.createQuery(query + orderBy)
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
		selected.removeChild("...");
		
		String query = "select distinct s.direccionIp from TrazaSession s where s.anno = :anno and s.mes = :mes and s.dia = :dia ";
		final String orderBy = "order by s.direccionIp desc ";
		List<String> ips = null;
		
		if(ip != null && !ip.isEmpty()){
			
			query += "and s.direccionIp =:ip " + orderBy;
			ips = entityManager
					.createQuery(query)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getMes())
					.setParameter("dia", value.getValue())
					.setParameter("ip", ip)
					.getResultList();
		}
		else{
			ips = entityManager
					.createQuery(query + orderBy)
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
		selected.removeChild("...");
		
		String query = "select distinct s.user.username from TrazaSession s "
				+ "where s.anno = :anno and s.mes = :mes and s.dia = :dia "
				+ "and s.direccionIp =:ip ";
		
		final String orderBy = "order by s.user.username desc";
		List<String> usuarios = null;
		
		if(usuario != null && !usuario.isEmpty()){
			query += "and s.user.username =:user " + orderBy;
			
			usuarios = entityManager
					.createQuery(query)
					.setParameter("anno", value.getAnno())
					.setParameter("mes", value.getMes())
					.setParameter("dia", value.getDia())
					.setParameter("ip", value.getValue())
					.setParameter("user", usuario)
					.getResultList();
		}
		else{
			usuarios = entityManager
					.createQuery(query + orderBy)
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
			dias = entityManager
					.createQuery(query+orderBy)
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
		selected.removeChild("...");

		List<Integer> meses = null;
		String query = "select distinct s.mes from TrazaSession s where s.anno = :anno ";
		final String orderBy = "order by s.mes desc";
		
		
		int mesAux = numeroMes();

		// Se busca por mes
		if (getMesSeleccionadoComboBox() != null && mesAux != -1) {
			query += "and s.mes =:mes " + orderBy;
			meses = entityManager.createQuery(query)
					.setParameter("anno", value)
					.setParameter("mes", mesAux).getResultList();

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

	

	public List<String> getAnnos() {
		return annos;
	}

	public void setAnnos(List<String> annos) {
		this.annos = annos;
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

	public String getMesSeleccionadoComboBox() {
		return mesSeleccionadoComboBox;
	}

	public void setMesSeleccionadoComboBox(String mesSeleccionadoComboBox) {
		this.mesSeleccionadoComboBox = mesSeleccionadoComboBox;
	}





}
