package gehos.ensayo.ensayo_configuracion.session.custom;

//#region

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.wrappers.EstudioEntidadWrapper;
import gehos.ensayo.ensayo_configuracion.session.custom.wrappers.EstudioWrapper;
import gehos.ensayo.ensayo_configuracion.session.custom.wrappers.IWrapper;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.theme.Theme;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;


//#endregion

/**
 * Controladora de la relacion Estudio-Centro
 * 
 * @author Yero
 * 
 */
@Name("centroEstudioController")
public class CentroEstudiosController {


	//#region Variables
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
	protected @In IBitacora bitacora;
	
	
	
	/**
	 * Arbol principal que contiene la relacion de todos los estudios	 
	 */
	private TreeNode treeRootData;

	/**
	 * Obtener arbol principal que contiene la relacion de todos los estudios
	 * 
	 * @return Devuelve el arbol
	 */
	public TreeNode getTreeRootData() {
		return treeRootData;
	}

	/**
	 * Asigna arbol principal que contiene la relacion de todos los estudios
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
		this.estudioCentroSelected = -11l;

		if(this.selectedItem==null)
			loadData();
	}
	
	long estudioCentroSelected = -11l;
	
	public void setEstudioCentroSelected(long estudioCentroSelected,boolean selectedNull) {
		this.estudioCentroSelected = estudioCentroSelected;
		if(selectedNull)
			this.selectedItem = null;
	}
	
	public long getEstudioCentroSelected() {
		return estudioCentroSelected;
	}
	
	public String NombreEstudioSelected(){
		try{
			EstudioEntidad_ensayo ensayo = (EstudioEntidad_ensayo)entityManager.find(EstudioEntidad_ensayo.class, estudioCentroSelected);					
			return ensayo.getEntidad().getNombre() + "/" + ensayo.getEstudio().getNombre();
		}catch(Exception exception){
			return "";
		}
	} 
	
	//#endregion
	
	// #region Inicializador

		/**
		 * Carga los datos iniciales de la clase al ser instanciada
		 * 
		 * @author Yero
		 */
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

			// Cargo todos los estudios
			List<Estudio_ensayo> estudios = entityManager.createQuery(
					"from Estudio_ensayo ent where eliminado <> true order by ent.tituloOficial")
					.getResultList();

			// Convierto los estudios al tipo EstudioWrapper
			for (int i = 0; i < estudios.size(); i++) {
				TreeNode estudioNode = new TreeNodeImpl();

				estudioNode.setData(new EstudioWrapper(estudios.get(i), false,
						estudios.get(i).getId()));

				if (!estudios.get(i).getEstudioEntidads().isEmpty()) {
					TreeNode loadingNode = new TreeNodeImpl();
					loadingNode.setData("...");
					estudioNode.addChild("...", loadingNode);
				}

				treeRootData.addChild(estudios.get(i), estudioNode);
			}
		}

		// #endregion
		
	
		// #region Opciones de seleccion del panel IZQ

		
		/**
		 * Utiliza este metodo para recibir el evento OnNodeCollapseExpand
		 * 
		 * @param event
		 */
		@SuppressWarnings("rawtypes")
		public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
			HtmlTree tree = (HtmlTree) event.getSource();
			TreeNode selected = tree.getTreeNode();
			collapseOrExpand(selected, true);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private boolean collapseOrExpand(TreeNode selected, boolean putLoadingNode) {

			// Si el nodo esta expandido lo colapso
			if (((IWrapper) selected.getData()).isExpanded()) {
				
				//Colapso al nodo
				Colapse(selected);
				
				if (putLoadingNode) {
					TreeNode loadingNode = new TreeNodeImpl();
					loadingNode.setData("...");
					selected.addChild("...", loadingNode);
				}
				
				((IWrapper) selected.getData()).setExpanded(false);
			}

			else {

				if (selected.getData() instanceof EstudioWrapper)
					return expandEnsayo(selected);
				}
			return false;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private boolean expandEnsayo(TreeNode selected) {

			EstudioWrapper value = ((EstudioWrapper) selected.getData());
			selected.removeChild("...");

			List<EstudioEntidad_ensayo> centros = entityManager
					.createQuery(
							"select eee "
									+ " from EstudioEntidad_ensayo eee where eliminado <> true and eee.estudio.id=:estID"
									+ " order by eee.entidad.nombre")
					.setParameter("estID", value.getValue().getId())
					.getResultList();

			for (int i = 0; i < centros.size(); i++) {
				TreeNode ensayoNode = new TreeNodeImpl();
				EstudioEntidadWrapper w = new EstudioEntidadWrapper(centros.get(i), false, value.getEstudioID());
				ensayoNode.setData(w);
				selected.addChild(w.hashCode(), ensayoNode);
			}

			((IWrapper) selected.getData()).setExpanded(true);
			return centros.size() > 0;
		}
		
		@SuppressWarnings({ "rawtypes" })
		private void Colapse(TreeNode selected) {
			ArrayList<Integer> hashcodes = new ArrayList<Integer>();
			for (Iterator iterator = selected.getChildren(); iterator.hasNext();) {
				java.util.Map.Entry obj = (java.util.Map.Entry) iterator.next();
				TreeNode node = (TreeNode) obj.getValue();
				Colapse(node);
				hashcodes.add(node.getData().hashCode());
			}
			for (int i = 0; i < hashcodes.size(); i++) {
				selected.removeChild(hashcodes.get(i));
			}
		}

		// #endregion
		
		
		
		
		
		// #region ICONO DE LA ENTIDAD

		@In("org.jboss.seam.theme.themeFactory")
		Theme theme;

		public String entidadIcon(EstudioEntidad_ensayo node) {
			EstudioEntidadWrapper wrapper = new EstudioEntidadWrapper(node, false, node.getId());
			return entidadIcon(wrapper);
		}

		public String entidadIcon(EstudioEntidadWrapper node) {
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String path = "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/"
					+ node.getValue().getEntidad().getLogo();

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

		// #endregion
		
		/**
		 * Retorno todo las Entidades que estan en el Estudio Seleccionado
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public List<EstudioEntidad_ensayo> EstudioEntidad() {			
			
			if (this.selectedItem instanceof EstudioWrapper) {

				List<EstudioEntidad_ensayo> centros = entityManager
						.createQuery(
								"select eee "
										+ " from EstudioEntidad_ensayo eee where eee.estudio.id=:estID"
										+ " and eliminado <> true"
										+ " order by eee.estudio.nombre")
						.setParameter("estID", this.selectedItem.getId())
						.getResultList();

				return centros;
			}

			return null;
		}
		
		
		EntidadEnsayoBuscarControler buscarControler = new EntidadEnsayoBuscarControler();

		public EntidadEnsayoBuscarControler getBuscarControler() {
			return buscarControler;
		}

		public void setBuscarControler(EntidadEnsayoBuscarControler buscarControler) {
			this.buscarControler = buscarControler;
		}
		
		public String onComplete(String modalName) {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
		
		public void crearRelacionEntidadEstudio(Entidad_ensayo entidad) {

			if (this.selectedItem instanceof EstudioWrapper) {

				//No existe una realcion activa
				int a = entityManager
						.createQuery(
								"select stcent "
										+ " from EstudioEntidad_ensayo stcent where stcent.estudio.id=:estID"
										+ " and stcent.entidad.id =:entID"
										+ " and stcent.eliminado <> true")
						.setParameter("estID", this.selectedItem.getId())
						.setParameter("entID", entidad.getId())
						.getResultList().size();

				if (a != 0) {
					facesMessages.addToControlFromResourceBundle("error",
							Severity.ERROR, "epc_errorAdd",
							entidad.getNombre());
					return;
				}

				//Creo la relacion 
				EstudioEntidad_ensayo nuevaRel = new EstudioEntidad_ensayo();
				
				nuevaRel.setEntidad(entidad);				
				nuevaRel.setEstudio(((EstudioWrapper) this.selectedItem).getValue());
				nuevaRel.setEliminado(false);
				nuevaRel.setCid(
						this.bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("epc_crearRelacionEstudioEntidad"))
				);
				
				entityManager.persist(nuevaRel);
				entityManager.flush();
				
				loadData();
			}

		}
		
		Long idEntidadEstudio=-11l;

		public void setIdEntidadEstudioToRemove(long idRemove) {
			this.idEntidadEstudio = idRemove;
		}

		public void EntidadEstudioRemove() {

			if (this.idEntidadEstudio == -11)
				return;

			try {
				
				//Tomo la entidad con el ID
				EstudioEntidad_ensayo estudioCentro = (EstudioEntidad_ensayo) entityManager
						.createQuery(
								"select eee "
										+ " from EstudioEntidad_ensayo eee where eee.id=:idEntidadEstudio"
										+ " and eee.eliminado <> true ")
						.setParameter("idEntidadEstudio", this.idEntidadEstudio)
						.getSingleResult();
				
				estudioCentro.setCid(
						this.bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("epc_eliminarRelacionEstudioEntidad")));
				
				estudioCentro.setEliminado(true);
				
				entityManager.persist(estudioCentro);
				entityManager.flush();

				this.idEntidadEstudio = -11l;
				

				loadData();
				
			} catch (Exception exception) {
				this.facesMessages.add(exception.getMessage());
			}

		}
			
		
		@SuppressWarnings("unchecked")
		public List<UsuarioEstudio_ensayo> usuariosEstudio() {
			
			List<UsuarioEstudio_ensayo> centros = entityManager
						.createQuery(
								"select uee "
										+ " from UsuarioEstudio_ensayo uee where uee.estudioEntidad.id=:estID"
										+ " and uee.eliminado <> true"
										+ " order by uee.usuario.nombre")
						.setParameter("estID", estudioCentroSelected)
						.getResultList();

			return centros;			
			
		}
		
		
		
		private String selectedUser;

		
		public String getSelectedUser() {
			return selectedUser;
		}

		public void setSelectedUser(String selectedUser) {
			this.selectedUser = selectedUser;
		}
		

		private String selectedRole;

		public String getSelectedRole() {
			return selectedRole;
		}

		public void setSelectedRole(String selectedRole) {
			this.selectedRole = selectedRole;
		}
		
		@SuppressWarnings("unchecked")
		public List<String> allUsers(){
			//Todos los usuarios con el rol seleccionado 
			return entityManager.createQuery("select u.username from Usuario_ensayo u JOIN u.roles r where u.eliminado <> true and r.eliminado <> true and r.name = :rname")
					.setParameter("rname", selectedRole)
					.getResultList();
		}
		
		@SuppressWarnings("unchecked")
		public List<String> allRoles(){
			return entityManager.createQuery("select r.name from Role_ensayo r where r.eliminado <> true order by r.name asc").getResultList();
		}
		
		
		public void provideAccessToUser(){
			try {				
				
				
					EstudioEntidad_ensayo ensayo = (EstudioEntidad_ensayo)entityManager.find(EstudioEntidad_ensayo.class, estudioCentroSelected);					
						
					
					Usuario_ensayo usuario = (Usuario_ensayo) entityManager.
							createQuery("from Usuario_ensayo u " +
							"where u.username = :username")
							.setParameter("username", selectedUser)
							.getSingleResult();
					
					
					Role_ensayo role = (Role_ensayo)entityManager.
							createQuery("from Role_ensayo r " +
							"where r.name = :role")
							.setParameter("role", selectedRole)
							.getSingleResult();
					
					int a = entityManager
							.createQuery(
									"select uee "
											+ " from UsuarioEstudio_ensayo uee where uee.eliminado <> true"
											+ " and uee.estudioEntidad.id=:estID"
											+ " and uee.usuario.id=:usrID"
											+ " order by uee.usuario.nombre")
											.setParameter("estID", ensayo.getId())
											.setParameter("usrID", usuario.getId())							
							.getResultList().size();

					if(a!=0){
							facesMessages.add(SeamResourceBundle.getBundle()
									.getString("msg_provideAccessToUser1_ensClin") + usuario.getUsername()+ " " +SeamResourceBundle.getBundle()
									.getString("msg_provideAccessToUser2_ensClin"));
							return;
					}
					
					a = entityManager
							.createQuery(
									"select uee "
											+ " from UsuarioEstudio_ensayo uee where uee.eliminado <> true"
											+ " and uee.estudioEntidad.id=:estID"
											+ " and uee.role.id=:rolID")
											.setParameter("estID", ensayo.getId())
											.setParameter("rolID", role.getId())							
							.getResultList().size();

					if(a!=0){
							facesMessages.add(SeamResourceBundle.getBundle()
									.getString("msg_provideAccessToUser3_ensClin") +" "+ role.getName());
							return;
					}
					
					UsuarioEstudio_ensayo  usuarioEstudio= new UsuarioEstudio_ensayo();
					
					usuarioEstudio.setUsuario(usuario);
					
					usuarioEstudio.setRole(role);
					
					usuarioEstudio.setEstudioEntidad(ensayo);
			       
					usuarioEstudio.setEliminado(false);
					
					usuarioEstudio.setCid(
							this.bitacora
							.registrarInicioDeAccion(SeamResourceBundle.getBundle()
									.getString("epc_creandoRelacionUsuarioEstudioEntidad")));
					entityManager.persist(usuarioEstudio);
					entityManager.flush();
				
				
			} catch (RuntimeException e) {

				facesMessages.add(SeamResourceBundle.getBundle()
						.getString("msg_provideAccessToUser4_ensClin"));
				
				e.printStackTrace();
			}
		}
		
		
		Long idUserRemove=-11l;

		public void setIdUsuarioToRemove(long idRemove) {
			this.idUserRemove = idRemove;
		}

		public void UsuarioRemove() {

			if (this.idUserRemove == -11)
				return;

			try {
				
				//Tomo la entidad con el ID
				UsuarioEstudio_ensayo usuarioEstudio_ensayo = (UsuarioEstudio_ensayo) entityManager
						.createQuery(
								"select eee "
										+ " from UsuarioEstudio_ensayo eee where eee.id=:ide")
						.setParameter("ide", this.idUserRemove)
						.getSingleResult();
				
				usuarioEstudio_ensayo.setEliminado(true);
				
				usuarioEstudio_ensayo.setCid(
						this.bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("epc_eliminarRelacionUsuarioEstudioEntidad")));
				
				entityManager.persist(usuarioEstudio_ensayo);
				entityManager.flush();

				this.idUserRemove = -11l;
				

				loadData();
				
			} catch (Exception exception) {
				this.facesMessages.add(exception.getMessage());
			}

		}
		
	
			
}
