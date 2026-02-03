package gehos.comun.mantenimiento.funcionalidades.tree;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.mantenimiento.funcionalidades.dataaccess.IConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;


import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("funcionalidadesOrganizer")

public class MenuOperation {
	@In
	EntityManager entityManager;
	@In(create = true)
	IConnection connectionSQL;
	public int INIT = 100000000;
	public MenuTree principal_tree = null;
	private boolean show=false;
	
	public LinkedList<MenuTree> selectByIdFather(Long id){
		LinkedList<MenuTree> dev = null;
		Connection con = connectionSQL.createConnection();
		try {
			dev = new LinkedList<MenuTree>();
			ResultSet set = con
					.createStatement()
					.executeQuery(
							"select id,label,id_funcionalidad_padre,modulo_fisico,es_modulo,url,imagen from "
									+ "seguridad.funcionalidad where id_funcionalidad_padre = "
									+ id + " order by id");
			while (set.next()) {
				MenuTreeNode node = new MenuTreeNode();
				node.setId(set.getInt("id"));
				node.setOld_id(set.getInt("id"));
				node.setId_funcionalidad_padre(id);
				node.setLabel(set.getString("label"));
				node.setIs_modulo_fisico(set.getBoolean("modulo_fisico"));
				node.setIs_modulo(set.getBoolean("es_modulo"));
				if(!set.getString("url").equals("/")){
					String[] aux=set.getString("url").split("/");
			        aux=aux[1].split("mod");
			        if(aux.length>1){
				        char c=Character.toLowerCase(aux[1].charAt(0));
				        aux[1]=aux[1].substring(1);
				        aux[1]=c+aux[1];
				        node.setImagen(aux[1]+"/"+set.getString("imagen"));
			        }
				}
				dev.add(new MenuTree(node));
			}
			con.close();
		} catch (SQLException e) {
			System.out
					.println("Error in LinkedList<Tree> selectByIdFather(Long id)");
			e.printStackTrace();
			return null;
		}
		return dev;
	}
	public void buildinitialTree() {
		principal_tree = new MenuTree(new MenuTreeNode(0, -1, "Módulo-HIS",
				true, false));
		ArrayDeque<MenuTree> q = new ArrayDeque<MenuTree>();
		q.add(principal_tree);
		while (!q.isEmpty()) {
			MenuTree t = q.poll();
			LinkedList<MenuTree> aux = selectByIdFather(t.getRoot().getId());
			q.addAll(aux);
			t.setList_children(aux);
		}

	}
	
	public void init(){
			
			Connection con=connectionSQL.createConnection();
			try {
				Statement st=con.createStatement();
				st.executeUpdate("update seguridad.funcionalidad set id = id + "+ INIT +",id_funcionalidad_padre = id_funcionalidad_padre + "+ INIT +
						" where id > " + (-INIT));
				st.executeUpdate("update seguridad.funcionalidad set id = -1 where id = "+(INIT-1));
				st.executeUpdate("update seguridad.funcionalidad set id = 0,id_funcionalidad_padre = -1 where id = "+INIT);
				st.executeUpdate("update seguridad.funcionalidad set id_funcionalidad_padre = 0 where id_funcionalidad_padre = "+INIT);
				con.close();
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		
		
	}
	
	private List<MenuTreeNode> changeFunc=new LinkedList<MenuTreeNode>();
	
	public void order() {
		
		Int count = new Int(1);
		Int passmod = new Int(1);
		this.buildinitialTree();
		for (MenuTree t : principal_tree.getList_children()) {
			Int passfun = new Int(1);
			t.preorderTravelmod1(count, passmod, passfun,changeFunc);
			count.setVal(passmod.getVal());
		}
		entityManager.flush();
		entityManager.createNativeQuery("update seguridad.funcionalidad set id = id - "+INIT+
				" where id > 0").executeUpdate();
		entityManager.createNativeQuery("update seguridad.funcionalidad set id_funcionalidad_padre = id_funcionalidad_padre - "+INIT+
		" where id_funcionalidad_padre >= 0").executeUpdate();
		
	}
	public String obtainLabelPadre(Long id){
		return (String)entityManager.find(Funcionalidad.class, id).getLabel();
	}
	public List<MenuTreeNode> getChangeFunc() {
		return changeFunc;
	}
	public void setChangeFunc(List<MenuTreeNode> changeFunc) {
		this.changeFunc = changeFunc;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	
	
	
}
