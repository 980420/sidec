package gehos.comun.modulos.deprecated.management;

import gehos.comun.funcionalidades.treebuilders.model.ITreeData;
import gehos.comun.modulos.deprecated.treebuilders.ModulosTreeBuilder;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.richfaces.model.TreeNode;

@Name("modulosManager")
public class ModulosManager {
	@In
	EntityManager entityManager;
	private ITreeData selectedModule;
	@SuppressWarnings("unchecked")
	private TreeNode selectedTreenode;
	private boolean editMode;
	
	@In(required=false, create=true, value="modulosTreeBuilder")
	ModulosTreeBuilder treeBuilder;
	
	@SuppressWarnings("unchecked")
	public void setSelectedFunctionality(ITreeData funct, TreeNode node){
		this.selectedModule = funct;
		this.selectedTreenode = node;
		//Funcionalidad codebaseHolder = this.selectedFunctionality.getValue();
		//codebaseHolder = entityManager.find(Funcionalidad.class, codebaseHolder.getId());
/*		while(codebaseHolder != null && (codebaseHolder.getCodebase() == null || codebaseHolder.getCodebase() == ""))
			codebaseHolder = codebaseHolder.getFuncionalidadPadre();
		if(codebaseHolder != null)
			this.codeBaseTreeBuilder.loadTreeBuilderData(codebaseHolder.getCodebase());*/
	}

	public ModulosTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(ModulosTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public ITreeData getSelectedModule() {
		return selectedModule;
	}

	public void setSelectedModule(ITreeData selectedModule) {
		this.selectedModule = selectedModule;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
}


