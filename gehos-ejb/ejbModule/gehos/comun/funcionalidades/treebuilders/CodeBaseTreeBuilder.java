package gehos.comun.funcionalidades.treebuilders;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import edu.emory.mathcs.backport.java.util.Arrays;
import gehos.autorizacion.treebuilders.physical.model.ArchivoWrapper;
import gehos.autorizacion.treebuilders.physical.model.DirectoryWrapper;
import gehos.autorizacion.treebuilders.physical.model.ITreeData;

@Name("codeBaseTreeBuilder")
@Scope(ScopeType.CONVERSATION)
public class CodeBaseTreeBuilder {
	
	@SuppressWarnings("unchecked")
	private TreeNode treeData;	

	@Create
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void constructor(){
		;
	}
	

	@SuppressWarnings("unchecked")
	public void loadTreeBuilderData(String codebase){
		if(codebase == null || codebase == "")
			return;
	    FacesContext aFacesContext = FacesContext.getCurrentInstance();
	    ServletContext context = (ServletContext)aFacesContext.getExternalContext().getContext();
	    String rootpath = context.getRealPath(codebase);
	    
	    //root
		treeData = new TreeNodeImpl();

	    File dir = new File(rootpath);
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return !name.startsWith("funSelector") 
	            			&& !name.endsWith("xml") 
	            				&& !name.endsWith("properties");
	        }
	    };
	    
	    if(dir.listFiles(filter) != null){
	    	List<File> children = Arrays.asList(dir.listFiles(filter));
			Collections.sort(children, new Comparator<File>(){
				public int compare(File arg0, File arg1) {
					return arg0.getName().compareTo(arg1.getName());
				}});
			for (int i = 0; i < children.size(); i++) {
				if(children.get(i).isDirectory()){
					TreeNode loadingNode = new TreeNodeImpl();
					loadingNode.setData("...");
	
					TreeNode moduloNode = new TreeNodeImpl();
					DirectoryWrapper w;
					if(codebase.endsWith("/")){
						w = new DirectoryWrapper(children.get(i), false, codebase + children.get(i).getName());
					}
					else{
						w = new DirectoryWrapper(children.get(i), false, codebase + "/" + children.get(i).getName());
					}
					moduloNode.setData(w);
					moduloNode.addChild("...", loadingNode);
	
					treeData.addChild(w.hashCode(), moduloNode);
				}
				else{
					TreeNode archivoNode = new TreeNodeImpl();
					ArchivoWrapper w;
					if(codebase.endsWith("/")){
						w = new ArchivoWrapper(children.get(i), false, codebase + children.get(i).getName());
					}
					else{
						w = new ArchivoWrapper(children.get(i), false, codebase + "/" + children.get(i).getName());
					}
					archivoNode.setData(w);

					treeData.addChild(w.hashCode(), archivoNode);
				}
			}
	    }
	    else
	    	;
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
		
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return !name.startsWith("funSelector") 
	            			&& !name.endsWith("xml") 
	            				&& !name.endsWith("properties");
	        }
	    };   
		List<File> children = Arrays.asList(value.getValue().listFiles(filter));
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

	public TreeNode getTreeData() {
		return treeData;
	}

	public void setTreeData(TreeNode treeData) {
		this.treeData = treeData;
	}	

}
