package gehos.comun.mantenimiento.funcionalidades.tree;


import java.util.LinkedList;
import java.util.List;



public class MenuTree {
	private MenuTreeNode root;
	private LinkedList<MenuTree> list_children;
	private int count_children;

	public MenuTree(MenuTreeNode root, LinkedList<MenuTree> list_children) {

		this.root = root;
		this.list_children = list_children;
		count_children = this.list_children.size();

	}

	public MenuTree(MenuTreeNode root) {
		this.root = root;
		this.list_children = new LinkedList<MenuTree>();
		count_children = 0;

	}

	public MenuTreeNode getRoot() {
		return root;
	}

	public void setRoot(MenuTreeNode root) {
		this.root = root;
	}

	public LinkedList<MenuTree> getList_children() {
		return list_children;
	}

	public void setList_children(LinkedList<MenuTree> list_children) {
		this.list_children = list_children;
		this.setCount_children(this.list_children.size());
	}

	public int getCount_children() {
		return count_children;
	}

	public void setCount_children(int count_children) {
		this.count_children = count_children;
	}

	public boolean isLeaf() {
		return ((this.list_children == null || this.count_children == 0) ? true
				: false);
	}

	public void addChild(MenuTree child) {
		if (list_children != null) {
			list_children.add(child);
			count_children++;
		}
	}

	public void preorderTravelmod1(Int num, Int passmod, Int passfun,List<MenuTreeNode> list) {
		int aux = -1;
		if ((this.getRoot().Is_modulo() && this.getRoot().Is_modulo_fisico())
				|| (this.getRoot().Is_modulo() && !this.getRoot()
						.Is_modulo_fisico())) {
			aux = passmod.getVal();
			this.getRoot().setId(100000000+aux);
			passmod.increment();

		}
		if ((!this.getRoot().Is_modulo() && this.getRoot().Is_modulo_fisico())
				|| (!this.getRoot().Is_modulo() && !this.getRoot()
						.Is_modulo_fisico())) {
			aux = 1000 * num.getVal() + passfun.getVal();
			this.getRoot().setId(100000000+aux);
			passfun.increment();
		}
		if (aux != -1){
			this.getRoot().persistId();
			if((this.getRoot().getId()%100000000) != this.getRoot().getOld_id())
				list.add(this.getRoot());
		}
		if (!this.isLeaf()) {
			for (MenuTree t : this.list_children) {
				t.getRoot().setId_funcionalidad_padre(100000000+aux);
				t.getRoot().persistId_funcionalidad_padre();
			}
			for (MenuTree t : this.list_children)
				t.preorderTravelmod1(num, passmod, passfun,list);
		}

	}

}
