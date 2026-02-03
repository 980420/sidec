package gehos.comun.nomencladores;


import gehos.comun.mantenimiento.funcionalidades.dataaccess.DBInfo;
import gehos.comun.mantenimiento.funcionalidades.dataaccess.IConnection;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("nomencladoresController")
public class NomencladoresController {
	
	@In EntityManager entityManager;
	private String nombreNomenclador = "";
	private String order = "";
	private boolean problems;
	@In(create = true)
	IConnection connectionSQL;
	@In(create = true)
	DBInfo dbInfo;
	public List<String> nomencladoresNames(){
		try{
			List<String> l=new LinkedList<String>();
			Connection con=connectionSQL.createConnection();
			String[] types={"TABLE"};
			ResultSet set=con.getMetaData().getTables(null, "nomencladores", null, types);
			while(set.next()){
				if(set.getString("TABLE_NAME").contains(this.nombreNomenclador)){
					 l.add(set.getString("TABLE_NAME"));;
				}
			}
			con.close();
			if(this.order.equals("asc")){
				Collections.sort(l, new Comparator<String>(){
					public int compare(String arg0, String arg1) {
						return arg0.compareTo(arg1);
					}});
			}
			else if(this.order.equals("desc")){
				Collections.sort(l, new Comparator<String>(){
					public int compare(String arg0, String arg1) {
						return -1 * arg0.compareTo(arg1);
					}});
			}
			return l;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<String> nomencladoresNames(Object arg){
		try{
			List<String> l=new LinkedList<String>();
			Connection con=connectionSQL.createConnection();
			String[] types={"TABLE"};
			String key = (String) arg;
			ResultSet set=con.getMetaData().getTables(null, "nomencladores", null, types);
			while(set.next()){
				if(set.getString("TABLE_NAME").contains(key))
					l.add(set.getString("TABLE_NAME"));
			}
			con.close();
			return l;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public String getNombreNomenclador() {
		return nombreNomenclador;
	}

	public void setNombreNomenclador(String nombreNomenclador) {
		this.nombreNomenclador = nombreNomenclador;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public boolean isProblems() {
		return problems;
	}

	public void setProblems(boolean problems) {
		this.problems = problems;
	}
	
}
