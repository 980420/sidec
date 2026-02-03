package gehos.comun.funcionalidades.session.management;

import gehos.autorizacion.entity.RoleMenuitemPermission;
import gehos.autorizacion.entity.UserMenuitemPermission;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.funcionalidades.entity.custom.Favoritos;
import gehos.comun.funcionalidades.treebuilders.CodeBaseTreeBuilder;
import gehos.comun.funcionalidades.treebuilders.FuncionalidadesTreeBuilder;
import gehos.comun.funcionalidades.treebuilders.model.CategoriaWrapper;
import gehos.comun.funcionalidades.treebuilders.model.FuncionalidadWrapper;
import gehos.comun.funcionalidades.treebuilders.model.ITreeData;
import gehos.comun.funcionalidades.treebuilders.model.ModuloWrapper;
import gehos.configuracion.management.utilidades.Validations_configuracion;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Pattern;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.DropEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeRowKey;

@Name("funcionalidadesManager")
@Scope(ScopeType.CONVERSATION)
public class FuncionalidadesManager {
	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;

	@In(required = false, create = true, value = "funcionalidadesTreeBuilder")
	FuncionalidadesTreeBuilder treeBuilder;

	// @In(required = false, create = true)
	CodeBaseTreeBuilder codeBaseTreeBuilder = new CodeBaseTreeBuilder();

	private ITreeData selectedFunctionality;
	@SuppressWarnings("unchecked")
	private TreeNode selectedTreenode;

	private List<Funcionalidad> modulosHijos;
	private Long moduloPadreId;

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> modulosHijos() {
		if (modulosHijos == null
				|| this.selectedFunctionality.getId() != moduloPadreId) {
			moduloPadreId = this.selectedFunctionality.getId();
			modulosHijos = entityManager
					.createQuery(
							"from Funcionalidad f where f.esModulo = true and f.moduloFisico = false "
									+ "and f.funcionalidadPadre.id = :idmodulo order by orden")
					.setParameter("idmodulo",
							this.selectedFunctionality.getId()).getResultList();
		}
		return modulosHijos;
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> modulos() {
		List<Funcionalidad> modulos = entityManager.createQuery(
				"from Funcionalidad m where m.funcionalidadPadre.id = 0 "
						+ "and m.eliminado = false order by m.label asc")
				.getResultList();
		return modulos;
	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {

	}

	public void subir() {
		if (this.selectedFunctionality instanceof ModuloWrapper) {
			Funcionalidad mod = ((ModuloWrapper) this.selectedFunctionality)
					.getValue();
			if (mod.getContenedorIconos() == true)
				this.selectedFunctionality = null;
			else {
				this.selectedFunctionality = new ModuloWrapper(
						mod.getFuncionalidadPadre(), false);
				this.moduleChildCategories = null;
				this.moduleChildFuncionalidades = null;
			}
		} else if (this.selectedFunctionality instanceof CategoriaWrapper) {
			Funcionalidad cat = ((CategoriaWrapper) this.selectedFunctionality)
					.getValue();
			if (cat.getFuncionalidadPadre().getEsModulo()) {
				this.selectedFunctionality = new ModuloWrapper(
						cat.getFuncionalidadPadre(), false);
				this.moduleChildCategories = null;
				this.moduleChildFuncionalidades = null;
			} else {
				this.selectedFunctionality = new CategoriaWrapper(
						cat.getFuncionalidadPadre(), false);
				this.moduleChildCategories = null;
				this.moduleChildFuncionalidades = null;
			}
		} else if (this.selectedFunctionality instanceof FuncionalidadWrapper) {
			Funcionalidad cat = ((FuncionalidadWrapper) this.selectedFunctionality)
					.getValue();
			if (cat.getFuncionalidadPadre().getEsModulo()) {
				this.selectedFunctionality = new ModuloWrapper(
						cat.getFuncionalidadPadre(), false);
				this.moduleChildCategories = null;
				this.moduleChildFuncionalidades = null;
			} else {
				this.selectedFunctionality = new CategoriaWrapper(
						cat.getFuncionalidadPadre(), false);
				this.moduleChildCategories = null;
				this.moduleChildFuncionalidades = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setSelectedFunctionality(ITreeData funct, TreeNode node) {
		if (this.selectedFunctionality == null
				|| funct == null
				|| this.selectedFunctionality.getValue().getId() != funct
						.getValue().getId()) {
			this.selectedFunctionality = funct;
			this.selectedTreenode = node;

			this.moduleChildCategories = null;
			this.moduleChildFuncionalidades = null;

			// buildCodebaseTree(this.selectedFunctionality.getValue());
		}
	}

	public void buildCodebaseTree() {
		buildCodebaseTree(this.selectedFunctionality.getValue());
	}

	public void setSelectedFunctionality(Funcionalidad module) {
		this.selectedFunctionality = new ModuloWrapper(module, false);
		this.moduleChildCategories = null;
		this.moduleChildFuncionalidades = null;
	}

	public void setSelectedFunctionality(Funcionalidad module, int dummy) {
		this.selectedFunctionality = new FuncionalidadWrapper(module, false);
		this.moduleChildCategories = null;
		this.moduleChildFuncionalidades = null;
	}

	public void setSelectedCategory(Funcionalidad cat) {
		this.selectedFunctionality = new CategoriaWrapper(cat, false);
		this.moduleChildCategories = null;
		this.moduleChildFuncionalidades = null;
	}

	private void buildCodebaseTree(Funcionalidad selectedFunctionality) {
		Funcionalidad codebaseHolder = selectedFunctionality;
		while (codebaseHolder != null
				&& (codebaseHolder.getCodebase() == null || codebaseHolder
						.getCodebase() == ""))
			codebaseHolder = codebaseHolder.getFuncionalidadPadre();
		if (codebaseHolder != null)
			this.codeBaseTreeBuilder.loadTreeBuilderData(codebaseHolder
					.getCodebase());
	}

	public void setSelectedFunctionality(ITreeData funct) {
		this.selectedFunctionality = funct;
	}

	public ITreeData getSelectedFunctionality() {
		return selectedFunctionality;
	}

	private List<Funcionalidad> moduleChildCategories;

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> getModuleChildCategories() {
		if (moduleChildCategories == null)
			moduleChildCategories = entityManager
					.createQuery(
							"from Funcionalidad f where f.funcionalidadPadre.id = :modulo and f.url like '%selector%' order by f.orden")
					.setParameter(
							"modulo",
							((Funcionalidad) this.selectedFunctionality
									.getValue()).getId()).getResultList();
		return moduleChildCategories;
	}

	public Funcionalidad moduleByFuncionality(Funcionalidad fun) {
		while (!fun.getContenedorIconos())
			fun = fun.getFuncionalidadPadre();
		return fun;
	}

	private List<Funcionalidad> moduleChildFuncionalidades;

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> getModuleChildFuncionalidades() {
		if (moduleChildFuncionalidades == null)
			moduleChildFuncionalidades = entityManager
					.createQuery(
							"from Funcionalidad f where f.funcionalidadPadre.id = :modulo and f.esModulo = false and f.url not like '%selector%' order by f.orden")
					.setParameter(
							"modulo",
							((Funcionalidad) this.selectedFunctionality
									.getValue()).getId()).getResultList();
		return moduleChildFuncionalidades;
	}

	public void changeFuncVisibility(String functId) {
		Funcionalidad funct = entityManager.find(Funcionalidad.class,
				Long.parseLong(functId));
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		funct.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitVisibilidad")
				+ " -"
				+ funct.getLabel()));
		entityManager.persist(funct);
		entityManager.flush();
		// moduleChildCategories = null;
	}

	private String functIdToRemove;

	public String getFunctIdToRemove() {
		return functIdToRemove;
	}

	public void setFunctIdToRemove(String functIdToRemove) {
		this.functIdToRemove = functIdToRemove;
	}

	@SuppressWarnings("unchecked")
	// @Restrict("#{s:hasRole('root')}")
	public void removeFunctionality() {
		Funcionalidad funct = entityManager.find(Funcionalidad.class,
				Long.parseLong(functIdToRemove));
		List<Funcionalidad> hijas = entityManager
				.createQuery(
						"from Funcionalidad f where f.funcionalidadPadre.id = :pid")
				.setParameter("pid", funct.getId()).getResultList();
		Funcionalidad padre = funct.getFuncionalidadPadre();
		for (Funcionalidad f : hijas) {
			f.setFuncionalidadPadre(padre);
			padre.getFuncionalidadesHijas().add(f);
			entityManager.persist(f);
		}
		entityManager.persist(padre);

		funct.getFuncionalidadesHijas().clear();
		funct.setFuncionalidadPadre(null);
		padre.getFuncionalidadesHijas().remove(funct);
		entityManager.persist(funct);
		entityManager.persist(padre);
		entityManager.flush();

		List<Favoritos> favoritos = entityManager
				.createQuery(
						"from Favoritos f where f.id.idFuncionalidad = :fid")
				.setParameter("fid", funct.getId()).getResultList();
		for (Favoritos favs : favoritos) {
			entityManager.remove(favs);
		}
		entityManager.flush();

		List<UserMenuitemPermission> userLogicPermissions = entityManager
				.createQuery(
						"from UserMenuitemPermission u where u.menuItem.id = :id")
				.setParameter("id", funct.getId()).getResultList();
		for (UserMenuitemPermission userMenuitemPermission : userLogicPermissions) {
			entityManager.remove(userMenuitemPermission);
		}
		entityManager.flush();

		List<RoleMenuitemPermission> roleLogicPermissions = entityManager
				.createQuery(
						"from RoleMenuitemPermission r where r.menuItem.id = :id")
				.setParameter("id", funct.getId()).getResultList();
		for (RoleMenuitemPermission roleMenuitemPermission : roleLogicPermissions) {
			entityManager.remove(roleMenuitemPermission);
		}
		entityManager.flush();
		funct.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitEliminar") + " -" + funct.getLabel()));
		entityManager.remove(funct);
		entityManager.flush();

		if (this.selectedTreenode != null)
			this.treeBuilder.updateNode(this.selectedTreenode);
		else if (this.treeBuilder.getSelectedNode() != null)
			this.treeBuilder.updateNode(this.treeBuilder.getSelectedNode());
		this.moduleChildCategories = null;
		this.moduleChildFuncionalidades = null;
	}

	public boolean createCodeBaseTreeBuilder() {
		this.codeBaseTreeBuilder.loadTreeBuilderData(this.selectedFunctionality
				.getValue().getCodebase());
		return false;
	}

	private boolean editingOrders = false;

	public void salvarOrden() {
		long cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitModOrden"));
		for (Funcionalidad funcionalidad : getModuleChildCategories()) {
			funcionalidad.setCid(cid);
			entityManager.persist(funcionalidad);
		}
		for (Funcionalidad funcionalidad : getModuleChildFuncionalidades()) {
			funcionalidad.setCid(cid);
			entityManager.persist(funcionalidad);
		}
		entityManager.flush();
	}

	public void convertToCategory() {
		Funcionalidad func = this.selectedFunctionality.getValue();
		entityManager.merge(func);
		String[] url_split = func.getUrl().split("/");
		String url = "/" + url_split[1] + "/funSelector/selector.gehos";
		func.setUrl(url);
		entityManager.persist(func);
		entityManager.flush();

		this.treeBuilder.updateNode(this.selectedTreenode.getParent());
		ITreeData temp = this.treeBuilder.getDataAt(this.selectedFunctionality,
				this.selectedTreenode.getParent());
		this.selectedFunctionality = temp;
	}

	@SuppressWarnings("unchecked")
	public void processDrop(DropEvent dropEvent) {
		UITreeNode destNode = (dropEvent.getSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getSource() : null;
		UITree destTree = destNode != null ? destNode.getUITree() : null;
		TreeRowKey dropNodeKey = (dropEvent.getDropValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDropValue() : null;
		TreeNode droppedInNode = dropNodeKey != null ? destTree
				.getTreeNode(dropNodeKey) : null;

		Funcionalidad funcDestino = ((ITreeData) droppedInNode.getData())
				.getValue();
		funcDestino = entityManager.find(Funcionalidad.class,
				funcDestino.getId());

		UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getDraggableSource() : null;
		UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
		TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDragValue() : null;
		TreeNode draggedNode = dragNodeKey != null ? srcTree
				.getTreeNode(dragNodeKey) : null;

		Funcionalidad funcDragged = ((ITreeData) draggedNode.getData())
				.getValue();
		funcDragged = entityManager.find(Funcionalidad.class,
				funcDragged.getId());

		funcDragged.setFuncionalidadPadre(funcDestino);
		funcDestino.getFuncionalidadesHijas().add(funcDragged);

		entityManager.persist(funcDestino);
		entityManager.persist(funcDragged);
		entityManager.flush();

		this.treeBuilder.updateNode(droppedInNode);
		if (draggedNode.getParent() != null)
			this.treeBuilder.updateNode(draggedNode.getParent());
	}

	public void clean() {
		this.funcionalidadToModule = new Funcionalidad();
		this.categoryToModule = new Funcionalidad();
		this.editingCategory = false;
		this.editingFunctionality = false;
		this.funLabel = "";
		this.funUrl = "";
	}

	public void setFuncionalityUrl(String url) {
		if (url.indexOf("xhtml") > 0)
			this.funUrl = url.replace("xhtml", "gehos");
		else
			this.funUrl = url;
	}

	// funcionalidades
	private boolean editingFunctionality = false;

	public boolean isEditingFunctionality() {
		return editingFunctionality;
	}

	public void setEditingFunctionality(boolean editingFunctionality) {
		this.editingFunctionality = editingFunctionality;
	}

	private String funLabel = "";
	private String funUrl = "";
	private String existingFuncIconName = "";
	private Funcionalidad funcionalidadToModule = new Funcionalidad();

	public void saveFuncionalidad() {
		uploadFuncionalidadAndSave();
	}

	public void uploadFuncionalidadAndSave() {
		if (this.funUrl.isEmpty() || this.funUrl == null) {
			facesMessages.addToControlFromResourceBundle(
					"urlInputText",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"msg_valorRequerido_modConfig"));
		} else {
			Long cid = null;
			this.funcionalidadToModule.setImagen(this.existingFuncIconName);
			this.funcionalidadToModule.setLabel(this.funLabel);
			this.funcionalidadToModule.setUrl(this.funUrl);

			if (!this.editingFunctionality) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
						.getBundle().getString("bitCrear")
						+ " -"
						+ funcionalidadToModule.getLabel());
				Funcionalidad mod = entityManager.find(Funcionalidad.class,
						selectedFunctionality.getValue().getId());

				this.moduleChildFuncionalidades = null;

				this.funcionalidadToModule.setFuncionalidadPadre(mod);
				this.funcionalidadToModule.setOrden(0);
				this.funcionalidadToModule.setNecesitaActivacion(false);
				this.funcionalidadToModule.setActivo(true);
				this.funcionalidadToModule.setEsModulo(false);
				this.funcionalidadToModule.setContenedorIconos(false);
			} else {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
						.getBundle().getString("bitModificar")
						+ " -"
						+ funcionalidadToModule.getLabel());
			}
			/*
			 * else{ entityManager.merge(this.funcionalidadToModule); }
			 */

			this.funcionalidadToModule.setCid(cid);
			entityManager.persist(this.funcionalidadToModule);
			entityManager.flush();
			// this.selectedFunctionality.setValue(this.funcionalidadToModule);

			this.funcionalidadToModule = new Funcionalidad();
			this.editingFunctionality = false;
			this.funLabel = "";

			if (selectedTreenode == null)
				this.selectedTreenode = this.treeBuilder.getSelectedNode();
			if (selectedTreenode != null) {
				if (selectedTreenode.getData() instanceof ModuloWrapper
						&& ((ITreeData) selectedTreenode.getData())
								.isExpanded()) {
					this.treeBuilder.updateNode(this.selectedTreenode);
				} else if (selectedTreenode.getData() instanceof CategoriaWrapper
						&& ((ITreeData) selectedTreenode.getData())
								.isExpanded()) {
					this.treeBuilder.updateNode(this.selectedTreenode);
				} else if (selectedTreenode.getData() instanceof FuncionalidadWrapper) {
					if (this.selectedTreenode.getParent() != null)
						this.treeBuilder.updateNode(this.selectedTreenode
								.getParent());
				}
			}
		}
	}

	public void editCurrentCategory() {
		this.categoryToModule = this.selectedFunctionality.getValue();
		this.categoryToModule = entityManager.find(Funcionalidad.class,
				this.categoryToModule.getId());
		// buildCodebaseTree(this.categoryToModule);

		this.editingCategory = true;
		this.existingCategoryIconName = this.selectedFunctionality.getValue()
				.getImagen();
		this.catLabel = this.selectedFunctionality.getValue().getLabel();
	}

	public void insertNewFunctionality() {
		this.funLabel = "";
		this.funUrl = "";
		buildCodebaseTree();
	}

	public void editCurrentFunctionality() {
		this.funcionalidadToModule = this.selectedFunctionality.getValue();
		this.funcionalidadToModule = entityManager.find(Funcionalidad.class,
				this.funcionalidadToModule.getId());
		buildCodebaseTree();

		this.editingFunctionality = true;
		this.existingFuncIconName = this.selectedFunctionality.getValue()
				.getImagen();
		this.funLabel = this.selectedFunctionality.getValue().getLabel();
		this.funUrl = this.selectedFunctionality.getValue().getUrl();
	}

	// categorias
	private boolean editingCategory = false;
	private String catLabel = "";
	private String existingCategoryIconName = "";
	private Funcionalidad categoryToModule = new Funcionalidad();

	public void uploadCategoryToModuleAndSave() {
		Long cid = null;
		this.categoryToModule.setLabel(this.catLabel);
		this.categoryToModule.setImagen(this.existingCategoryIconName);
		if (!this.editingCategory) {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
					.getBundle().getString("bitCrearC")
					+ " -"
					+ categoryToModule.getLabel());
			Funcionalidad mod = entityManager.find(Funcionalidad.class,
					selectedFunctionality.getValue().getId());

			this.moduleChildCategories = null;

			this.categoryToModule.setFuncionalidadPadre(mod);
			this.categoryToModule.setOrden(0);
			this.categoryToModule.setNecesitaActivacion(false);
			this.categoryToModule.setActivo(true);
			this.categoryToModule.setEsModulo(false);
			this.categoryToModule.setContenedorIconos(false);

			String[] url_split = mod.getUrl().split("/");
			String funSelecUrl = "/" + url_split[1]
					+ "/funSelector/selector.gehos";

			this.categoryToModule.setUrl(funSelecUrl);
		} else {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
					.getBundle().getString("bitModificarC")
					+ " -"
					+ categoryToModule.getLabel());
		}
		this.categoryToModule.setCid(cid);
		/*
		 * else{ entityManager.merge(this.categoryToModule); }
		 */

		entityManager.persist(this.categoryToModule);
		entityManager.flush();
		// this.selectedFunctionality.setValue(this.categoryToModule);

		this.categoryToModule = new Funcionalidad();
		this.editingCategory = false;
		this.catLabel = "";
		this.existingCategoryIconName = "";

		if (selectedTreenode == null
				&& this.treeBuilder.getSelectedNode() != null)
			this.selectedTreenode = this.treeBuilder.getSelectedNode();
		if (selectedTreenode != null) {
			if (selectedTreenode.getData() instanceof ModuloWrapper
					&& ((ITreeData) selectedTreenode.getData()).isExpanded()) {
				this.treeBuilder.updateNode(this.selectedTreenode);
			} else if (selectedTreenode.getData() instanceof CategoriaWrapper
					&& ((ITreeData) selectedTreenode.getData()).isExpanded()) {
				this.treeBuilder.updateNode(this.selectedTreenode);
			}
		}
	}

	// genericos
	public boolean isSelected(String icon) {
		if (this.existingCategoryIconName.equals(icon))
			return true;
		return false;
	}

	public boolean isFuncIconSelected(String icon) {
		if (this.existingFuncIconName.equals(icon))
			return true;
		return false;
	}

	public String[] getExistingModuleIcons() {
		if (selectedFunctionality != null) {
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();
			Funcionalidad iconsMod = moduleByFuncionality(this.selectedFunctionality
					.getValue());
			String rootpath = context.getRealPath("/resources");
			File file = new File(rootpath + "/funcionalidadesIcons/"
					+ iconsMod.getNombre());
			if (file.exists()) {
				List<String> iconos = Arrays.asList(file.list());
				if (iconos.size() > 0
						&& (this.existingCategoryIconName == "" || !iconos
								.contains(this.existingCategoryIconName)))
					this.existingCategoryIconName = file.list()[0];
				if (iconos.size() > 0
						&& (this.existingFuncIconName == "" || !iconos
								.contains(this.existingFuncIconName)))
					this.existingFuncIconName = file.list()[0];
				return file.list();
			}
		} else {
			return new String[] {};
		}
		return new String[] {};
	}

	@In
	FacesMessages facesMessages;

	public String onAdicionarFuncComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("funclabelinput").size() > 0
				|| facesMessages.getCurrentMessagesForControl("urlInputText")
						.size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onAdicionarCatComplete(String modalName) {
		if ((facesMessages.getCurrentMessagesForControl("catlabelinput").size() > 0)
				|| (facesMessages
						.getCurrentMessagesForControl("catlabelinputm").size() > 0))
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	// getters and setters

	public Funcionalidad getCategoryToModule() {
		return categoryToModule;
	}

	public void setCategoryToModule(Funcionalidad categoryToModule) {
		this.categoryToModule = categoryToModule;
	}

	public String getExistingCategoryIconName() {
		return existingCategoryIconName;
	}

	public void setExistingCategoryIconName(String existingCategoryIconName) {
		this.existingCategoryIconName = existingCategoryIconName;
	}

	@SuppressWarnings("unchecked")
	public TreeNode getSelectedTreenode() {
		return selectedTreenode;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedTreenode(TreeNode selectedTreenode) {
		this.selectedTreenode = selectedTreenode;
	}

	public void setModuleChildCategories(
			List<Funcionalidad> moduleChildCategories) {
		this.moduleChildCategories = moduleChildCategories;
	}

	public FuncionalidadesTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(FuncionalidadesTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public Funcionalidad getFuncionalidadToModule() {
		return funcionalidadToModule;
	}

	public void setFuncionalidadToModule(Funcionalidad funcionalidadToModule) {
		this.funcionalidadToModule = funcionalidadToModule;
	}

	public String getExistingFuncIconName() {
		return existingFuncIconName;
	}

	public void setExistingFuncIconName(String existingFuncIconName) {
		this.existingFuncIconName = existingFuncIconName;
	}

	public CodeBaseTreeBuilder getCodeBaseTreeBuilder() {
		return codeBaseTreeBuilder;
	}

	public void setCodeBaseTreeBuilder(CodeBaseTreeBuilder codeBaseTreeBuilder) {
		this.codeBaseTreeBuilder = codeBaseTreeBuilder;
	}

	public boolean isEditingOrders() {
		return editingOrders;
	}

	public void setEditingOrders(boolean editingOrders) {
		this.editingOrders = editingOrders;
	}

	@Length(min = 1, max = 70)
	@NotEmpty
	@Pattern(regex = "^([A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789 ]+)+$", message = "Caracteres incorrectos")
	public String getFunLabel() {
		return funLabel;
	}

	public void setFunLabel(String funLabel) {
		this.funLabel = funLabel;
	}

	@NotEmpty
	public String getFunUrl() {
		return funUrl;
	}

	public void setFunUrl(String funUrl) {
		this.funUrl = funUrl;
	}

	@Length(min = 1, max = 25)
	@NotEmpty
	@Pattern(regex = "^(\\s*[.A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$", message = "Caracteres incorrectos")
	public String getCatLabel() {
		return catLabel;
	}

	public void setCatLabel(String catLabel) {
		this.catLabel = catLabel;
	}

}
