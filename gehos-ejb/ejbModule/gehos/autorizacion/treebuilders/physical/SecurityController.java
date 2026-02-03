package gehos.autorizacion.treebuilders.physical;

import edu.emory.mathcs.backport.java.util.Arrays;
import gehos.autorizacion.treebuilders.physical.model.ArchivoWrapper;
import gehos.autorizacion.treebuilders.physical.model.DirectoryWrapper;
import gehos.autorizacion.treebuilders.physical.model.ITreeData;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.SESSION)
@Name("securityController")
public class SecurityController {

	@Logger
	private Log log;

	@In
	FacesMessages facesMessages;

	private String value;
	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	
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
		}

		else {
			if (selected.getData() instanceof DirectoryWrapper) {
				expandDirectorio(selected);
			} 
		}
	}

	@SuppressWarnings("unchecked")
	private void expandDirectorio(TreeNode selected) {
		DirectoryWrapper value = ((DirectoryWrapper) selected.getData());
		selected.removeChild("...");
		
		List<File> children = Arrays.asList(value.getValue().listFiles());
		Collections.sort(children, new Comparator<File>(){
			public int compare(File arg0, File arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}});
		for (int i = 0; i < children.size(); i++) {
			if(children.get(i).isDirectory())
			{
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");

				TreeNode directorioNode = new TreeNodeImpl();
				DirectoryWrapper w = new DirectoryWrapper(children.get(i), false, value.getVirtualPath() + "/" + children.get(i).getName());
				directorioNode.setData(w);
				directorioNode.addChild("...", loadingNode);

				selected.addChild(w.hashCode(), directorioNode);
			}
			else{
				TreeNode archivoNode = new TreeNodeImpl();
				ArchivoWrapper w = new ArchivoWrapper(children.get(i), false, value.getVirtualPath() + "/" + children.get(i).getName());
				archivoNode.setData(w);

				selected.addChild(w.hashCode(), archivoNode);
			}
		}

		((ITreeData) selected.getData()).setExpanded(true);	
	}

	@Create
	@SuppressWarnings("unchecked")
	public void constructor(){
	    FacesContext aFacesContext = FacesContext.getCurrentInstance();
	    ServletContext context = (ServletContext)aFacesContext.getExternalContext().getContext();
	    String rootpath = context.getRealPath("/");
	    
	    //root
		treeData = new TreeNodeImpl();

	    File dir = new File(rootpath);
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.startsWith("mod");
	        }
	    };
		List<File> children = Arrays.asList(dir.listFiles(filter));
		Collections.sort(children, new Comparator<File>(){
			public int compare(File arg0, File arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}});
		for (int i = 0; i < children.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode moduloNode = new TreeNodeImpl();
			DirectoryWrapper w = new DirectoryWrapper(children.get(i), false, "/" + children.get(i).getName());
			moduloNode.setData(w);
			moduloNode.addChild("...", loadingNode);

			treeData.addChild(w.hashCode(), moduloNode);
		}
	}

	public void checkSecurity() {
		// implement your business logic here
		log.info("securityController.checkSecurity() action called with: #{securityController.value}");
		facesMessages.add("checkSecurity #{securityController.value}");
	}

	@Length(max = 10)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

}
