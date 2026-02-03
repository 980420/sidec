package gehos.comun.procesos.treebuilders;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.procesos.treebuilders.model.DefinicionProceso;
import gehos.comun.procesos.treebuilders.model.DefinicionProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.DiagramaProceso;
import gehos.comun.procesos.treebuilders.model.DiagramaProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.ITreeData;
import gehos.comun.procesos.treebuilders.model.ModuloWrapper;
import gehos.comun.procesos.treebuilders.model.Proceso;
import gehos.comun.procesos.treebuilders.model.ProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.ReglasProceso;
import gehos.comun.procesos.treebuilders.model.ReglasProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.Revision;
import gehos.comun.procesos.treebuilders.model.RevisionWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@Scope(ScopeType.SESSION)
@Name("processRepositoryTreeBuilder")
public class ProcessRepositoryTreeBuilder {

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void ifNotPostBackLoadData() {
		if (this.treeData == null) {
			loadData();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadData() {
		treeData = new TreeNodeImpl();
		String procDirectoryPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/";
		procDirectoryPath = procDirectoryPath.substring(5);
		File processesDirectory = new File(procDirectoryPath);
		File[] procPerModule = processesDirectory.listFiles();
		for (int i = 0; i < procPerModule.length; i++) {
			File file = procPerModule[i];
			List<Funcionalidad> modulos = entityManager.createQuery(
					"from Funcionalidad mod where mod.nombre=:modName")
					.setParameter("modName", file.getName()).getResultList();
			if (modulos.size() > 0) {
				Funcionalidad modulo = modulos.get(0);
				TreeNode entidadNode = new TreeNodeImpl();
				entidadNode.setData(new ModuloWrapper(modulo, false));

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				entidadNode.addChild("...", loadingNode);

				treeData.addChild(modulo, entidadNode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean collapseOrExpand(TreeNode selected, boolean putLoadingNode) {
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			if (putLoadingNode) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				selected.addChild("...", loadingNode);
			}
			((ITreeData) selected.getData()).setExpanded(false);
		} else {
			if (selected.getData() instanceof ProcesoWrapper) {
				return expandProceso(selected);
			} else if (selected.getData() instanceof ModuloWrapper) {
				return expandModulo(selected);
			} else if (selected.getData() instanceof RevisionWrapper) {
				return expandRevision(selected);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean expandRevision(TreeNode selected) {
		RevisionWrapper value = ((RevisionWrapper) selected.getData());
		Revision revision = value.getValue();
		selected.removeChild("...");

		String revFilesDirectoryPath = System
				.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/"
				+ value.getValue().getModulo()
				+ "/"
				+ value.getValue().getNombreProceso()
				+ "/revision-"
				+ value.getValue().getNumero() + "/";
		revFilesDirectoryPath = revFilesDirectoryPath.substring(5);
		File processFile = new File(revFilesDirectoryPath
				+ "processdefinition.xml");
		File processRuleFile = new File(revFilesDirectoryPath
				+ "processrules.drl");
		if (processFile.exists()) {
			TreeNode defProcesoNode = new TreeNodeImpl();
			DefinicionProceso defNode = new DefinicionProceso(revision
					.getNombreProceso(), revision.getModulo(), revision
					.getNumero(), processRuleFile.exists());
			DefinicionProcesoWrapper w = new DefinicionProcesoWrapper(defNode,
					false);
			defProcesoNode.setData(w);
			selected.addChild(w.hashCode(), defProcesoNode);
		}
		File processImgFile = new File(revFilesDirectoryPath
				+ "processimage.jpg");
		if (processImgFile.exists()) {
			TreeNode imgProcesoNode = new TreeNodeImpl();
			DiagramaProceso defNode = new DiagramaProceso(revision
					.getNombreProceso(), revision.getModulo(), revision
					.getNumero(), processRuleFile.exists());
			DiagramaProcesoWrapper w = new DiagramaProcesoWrapper(defNode,
					false);
			imgProcesoNode.setData(w);
			selected.addChild(w.hashCode(), imgProcesoNode);
		}
		if (processRuleFile.exists()) {
			TreeNode reglasProcesoNode = new TreeNodeImpl();
			ReglasProceso defNode = new ReglasProceso(revision
					.getNombreProceso(), revision.getModulo(), revision
					.getNumero());
			ReglasProcesoWrapper w = new ReglasProcesoWrapper(defNode, false);
			reglasProcesoNode.setData(w);
			selected.addChild(w.hashCode(), reglasProcesoNode);
		}
		((ITreeData) selected.getData()).setExpanded(true);
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean expandProceso(TreeNode selected) {
		ProcesoWrapper value = ((ProcesoWrapper) selected.getData());
		selected.removeChild("...");

		String revDirectoryPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/"
				+ value.getValue().getModulo() + "/"
				+ value.getValue().getNombre();
		revDirectoryPath = revDirectoryPath.substring(5);
		File revDirectory = new File(revDirectoryPath);

		String[] revisions = revDirectory.list();

		Pattern pattern = Pattern.compile("^(revision)-[0-9]+$");
		for (int i = 0; i < revisions.length; i++) {
			Matcher matcher = pattern.matcher(revisions[i]);
			if (matcher.matches()) {
				String number = revisions[i].split("-")[1].trim();
				File reglas = new File(revDirectoryPath + "/revision-" + number
						+ "/processrules.drl");
				Revision revision = new Revision(value.getValue().getNombre(),
						value.getValue().getModulo(), Integer.parseInt(number),
						reglas.exists());
				TreeNode procesoNode = new TreeNodeImpl();
				RevisionWrapper w = new RevisionWrapper(revision, false);
				procesoNode.setData(w);
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				procesoNode.addChild("...", loadingNode);
				selected.addChild(w.hashCode(), procesoNode);

			}
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean expandModulo(TreeNode selected) {
		ModuloWrapper value = ((ModuloWrapper) selected.getData());
		selected.removeChild("...");

		String procDirectoryPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/"
				+ value.getValue().getNombre();
		procDirectoryPath = procDirectoryPath.substring(5);
		File processesDirectory = new File(procDirectoryPath);

		String[] procesos = processesDirectory.list();

		for (int i = 0; i < procesos.length; i++) {
			Proceso proceso = new Proceso(procesos[i], value.getValue()
					.getNombre());
			TreeNode procesoNode = new TreeNodeImpl();
			ProcesoWrapper w = new ProcesoWrapper(proceso, false);
			procesoNode.setData(w);
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			procesoNode.addChild("...", loadingNode);
			selected.addChild(w.hashCode(), procesoNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return true;
	}

	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		this.selectedNode = tree.getTreeNode();
		collapseOrExpand(this.selectedNode, true);
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
