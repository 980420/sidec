package gehos.comun.funcionalidades.treebuilders;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.funcionalidades.treebuilders.model.CategoriaWrapper;
import gehos.comun.funcionalidades.treebuilders.model.FuncionalidadWrapper;
import gehos.comun.funcionalidades.treebuilders.model.ITreeData;
import gehos.comun.funcionalidades.treebuilders.model.ModuloWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
 

@Name("funcionalidadesTreeBuilder")
@Scope(ScopeType.SESSION)
public class FuncionalidadesTreeBuilder {

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	private TreeNode treeData;	
	
	@SuppressWarnings("unused")
	private HtmlTree tree;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;	

	
	@SuppressWarnings("unchecked")
	@Create
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void ifNotPostBackLoadData() {
		if (this.treeData == null) {
			treeData = new TreeNodeImpl();
			
			List<Funcionalidad> modulos = entityManager.createQuery(
					"from Funcionalidad m where m.funcionalidadPadre.id = 0 " +
					"and m.eliminado = false order by m.label asc").getResultList();
			for (int i = 0; i < modulos.size(); i++) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");

				TreeNode moduloNode = new TreeNodeImpl();
				moduloNode.setData(new ModuloWrapper(modulos.get(i), false));
				moduloNode.addChild("...", loadingNode);

				treeData.addChild(modulos.get(i), moduloNode);
			}
		}
	}
	
	public void updateNode(){
		collapseOrExpand(this.selectedNode);
		collapseOrExpand(this.selectedNode);
	}
	
	@SuppressWarnings("unchecked")
	private TreeNode nodeToFind;
	
	@SuppressWarnings("unchecked")
	public void updateNode(TreeNode node){
		this.nodeToFind = node;
		//TreeNode nodeToUpdate = this.childAt(this.treeData);
		//collapseOrExpand(nodeToUpdate);
		//collapseOrExpand(nodeToUpdate);
		collapseOrExpand(node);
		collapseOrExpand(node);
	}
	
	private ITreeData dataToFind;
	@SuppressWarnings("unchecked")
	public ITreeData getDataAt(ITreeData data, TreeNode node){
		this.dataToFind = data;
		return dataAt(node);
	}
	
	@SuppressWarnings("unchecked")
	public ITreeData dataAt(TreeNode node){
		Iterator<Map.Entry<Object, TreeNode>> iterator = node.getChildren();
		while (iterator.hasNext()) {
			TreeNode tempnode = iterator.next().getValue();
			if ((tempnode.getData() instanceof ITreeData) &&
					(((ITreeData) tempnode.getData()).getId() == this.dataToFind.getId()) )
				return (ITreeData)tempnode.getData();
			else{
				Iterator<Map.Entry<Object, TreeNode>> iterator2 = tempnode.getChildren();
				while(iterator2.hasNext()){
					TreeNode tempnode2 = iterator2.next().getValue();
					if(tempnode2.getData() instanceof ITreeData){
						if(((ITreeData)tempnode2.getData()).getId() == this.dataToFind.getId())
							return (ITreeData)tempnode2.getData();
						ITreeData result = dataAt(tempnode2);
						if (result == null) 
							continue;
						else 
							return result;
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private TreeNode childAt(TreeNode node){
		Iterator<Map.Entry<Object, TreeNode>> iterator = node.getChildren();
		while (iterator.hasNext()) {
			TreeNode tempnode = iterator.next().getValue();
			if (((ITreeData) tempnode.getData()).getId() == ((ITreeData) this.nodeToFind
					.getData()).getId())
				return tempnode;
			else{
				Iterator<Map.Entry<Object, TreeNode>> iterator2 = tempnode.getChildren();
				while(iterator2.hasNext()){
					TreeNode tempnode2 = iterator2.next().getValue();
					if (childAt(tempnode2) == null)
						continue;
				}
			}
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public void collapseOrExpandNode(TreeNode node){
		collapseOrExpand(node);
	}
	
	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		this.selectedNode = tree.getTreeNode();
		collapseOrExpand(this.selectedNode);
	}

	@SuppressWarnings("unchecked")
	private void collapseOrExpand(TreeNode selected) {
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			selected.addChild("...", loadingNode);
			((ITreeData) selected.getData()).setExpanded(false);
		}

		else {
			if (selected.getData() instanceof ModuloWrapper) {
				expandModulo(selected);
			} else if (selected.getData() instanceof CategoriaWrapper) {
				expandCategoria(selected);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void expandCategoria(TreeNode selected) {
		CategoriaWrapper value = ((CategoriaWrapper) selected.getData());
		selected.removeChild("...");
		
		//List<Funcionalidad> funcionalidades = new ArrayList<Funcionalidad>(value.getValue().getFuncionalidadesHijas());
		List<Funcionalidad> funcionalidades = entityManager
			.createQuery("from Funcionalidad f where f.funcionalidadPadre.id = :pid order by orden")
			.setParameter("pid", value.getValue().getId()).getResultList();

		for (int i = 0; i < funcionalidades.size(); i++) {
			if(funcionalidades.get(i).getUrl().indexOf("selector") != -1){
				TreeNode categoriaNode = new TreeNodeImpl();
				CategoriaWrapper w = new CategoriaWrapper(funcionalidades.get(i), false);
				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);
				
				selected.addChild(w.hashCode(), categoriaNode);
			}
			else{
				TreeNode funcionalidadNode = new TreeNodeImpl();
				FuncionalidadWrapper w = new FuncionalidadWrapper(funcionalidades.get(i), false);
				funcionalidadNode.setData(w);

				selected.addChild(w.hashCode(), funcionalidadNode);
			}
			
		}
		((ITreeData) selected.getData()).setExpanded(true);		
	}


	
	@SuppressWarnings("unchecked")
	private void expandModulo(TreeNode selected) {
		ModuloWrapper value = ((ModuloWrapper) selected.getData());
		//selected.getChildren().remove();
		selected.removeChild("...");
		//selected.
		
		if(value.getValue().getModuloFisico() == false){
			List<Funcionalidad> modulosHijos = entityManager.createQuery("from Funcionalidad f " +
					"where f.esModulo = true and f.moduloFisico = false and f.funcionalidadPadre.id = :idmodulo order by orden")
					.setParameter("idmodulo", value.getValue().getId())
					.getResultList();
			for (int i = 0; i < modulosHijos.size(); i++) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");

				TreeNode moduloNode = new TreeNodeImpl();
				ModuloWrapper mw = new ModuloWrapper(modulosHijos.get(i), false);

				moduloNode.setData(mw);
				moduloNode.addChild("...", loadingNode);

				selected.addChild(mw.hashCode(), moduloNode);
			}
		}

		List<Funcionalidad> funcionalidades = entityManager
			.createQuery("from Funcionalidad f " +
					"where f.funcionalidadPadre.id = :moduleId " +
					"and f.esModulo = false order by f.orden")
				.setParameter("moduleId", value.getValue().getId()).getResultList();

		for (int i = 0; i < funcionalidades.size(); i++) {
			if(funcionalidades.get(i).getUrl().indexOf("selector") != -1){
				TreeNode categoriaNode = new TreeNodeImpl();
				CategoriaWrapper w = new CategoriaWrapper(funcionalidades.get(i), false);
				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);
				
				selected.addChild(w.hashCode(), categoriaNode);
			}
			else{
				TreeNode funcionalidadNode = new TreeNodeImpl();
				FuncionalidadWrapper w = new FuncionalidadWrapper(funcionalidades.get(i), false);
				funcionalidadNode.setData(w);

				selected.addChild(w.hashCode(), funcionalidadNode);
			}
			
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
		this.treeData = treeData;
	}


	@SuppressWarnings("unchecked")
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public HtmlTree getTree() {
		HtmlTree tree = new HtmlTree();
		
		return tree;
	}

	public void setTree(HtmlTree tree) {
		this.tree = tree;
	}

}
