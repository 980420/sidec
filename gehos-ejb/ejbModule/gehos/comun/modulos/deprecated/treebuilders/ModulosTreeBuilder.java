package gehos.comun.modulos.deprecated.treebuilders;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.funcionalidades.treebuilders.model.ModuloWrapper;
import gehos.comun.funcionalidades.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

@Name("modulosTreeBuilder")
@Scope(ScopeType.SESSION)
public class ModulosTreeBuilder {
	
	@In
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	private TreeNode treeData;	
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
					" order by m.label asc").getResultList();
			for (int i = 0; i < modulos.size(); i++) {

				TreeNode moduloNode = new TreeNodeImpl();
				moduloNode.setData(new ModuloWrapper(modulos.get(i), false));

				if(modulos.get(i).getModuloFisico() == false){
					TreeNode loadingNode = new TreeNodeImpl();
					loadingNode.setData("...");
					moduloNode.addChild("...", loadingNode);
				}

				treeData.addChild(modulos.get(i), moduloNode);
			}
		}
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
			} 
		}
	}
	
	@SuppressWarnings("unchecked")
	private void expandModulo(TreeNode selected) {
		ModuloWrapper value = ((ModuloWrapper) selected.getData());
		selected.removeChild("...");

		if (value.getValue().getModuloFisico() == false) {
			List<Funcionalidad> modulosHijos = entityManager
					.createQuery("from Funcionalidad f "
									+ "where f.esModulo = true " +
											"and f.funcionalidadPadre.id = :idmodulo")
					.setParameter("idmodulo", value.getValue().getId())
					.getResultList();
			for (int i = 0; i < modulosHijos.size(); i++) {

				TreeNode moduloNode = new TreeNodeImpl();
				ModuloWrapper mw = new ModuloWrapper(modulosHijos.get(i), false);
				moduloNode.setData(mw);

				if(modulosHijos.get(i).getModuloFisico() == false){
					TreeNode loadingNode = new TreeNodeImpl();
					loadingNode.setData("...");
					moduloNode.addChild("...", loadingNode);
				}

				selected.addChild(mw.hashCode(), moduloNode);
			}
			((ITreeData) selected.getData()).setExpanded(true);		
		}
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
	
}


