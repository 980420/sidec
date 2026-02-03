package gehos.comun.mantenimiento.funcionalidades.dataaccess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("dbInfo")
public class DBInfo {
	@In(create = true)
	IConnection connectionSQL;
	@SuppressWarnings("unchecked")
	private List listauxiliar;
	
	@SuppressWarnings("unchecked")
	public List getMetadataColumns(String schemaname, String tablename){
		try{
		Connection con=connectionSQL.createConnection();
		DatabaseMetaData dbmd=con.getMetaData();
		ResultSet set=dbmd.getColumns(null, schemaname, tablename, null); //se puede especificar el esquema....
		List l=new LinkedList<Object[]>();
		while(set.next()){
			Object[] add=new Object[2];
			add[0]=new Short((short)set.getInt("ORDINAL_POSITION"));
			add[1]=set.getString("COLUMN_NAME");
			l.add(add);
		}
		con.close();
		return l;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	@SuppressWarnings("unchecked")
	public List<String[]> getReferencias(String schemaname, String tablename,String order2){
		try{
			List result=new LinkedList<String[]>();
			Connection con=connectionSQL.createConnection();
			DatabaseMetaData dbmd=con.getMetaData();
			ResultSet set=dbmd.getImportedKeys(null, schemaname, tablename);
			while(set.next()){
				if(set.getString("PKTABLE_SCHEM").equals("nomencladores")){
					String[] add=new String[2];
					add[0]=set.getString("PKTABLE_NAME");
					add[1]=set.getString("FKCOLUMN_NAME");
					result.add(add);
				}
			}
			con.close();
			if(order2.equals("asc")){
				Collections.sort(result, new Comparator<String[]>(){
					public int compare(String[] arg0, String[] arg1) {
						return arg0[0].compareTo(arg1[0]);
					}});
			}
			else if(order2.equals("desc")){
				Collections.sort(result, new Comparator<String[]>(){
					public int compare(String[] arg0, String[] arg1) {
						return -1 * arg0[0].compareTo(arg1[0]);
					}});
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public boolean[] existColumns(String schemaname, String tablename,String[] ask){
		boolean[] result=new boolean[ask.length];
		for (int i = 0; i < result.length; i++) {
			result[i]=false;
		}
		List aux=this.getMetadataColumns(schemaname, tablename);
		this.setListauxiliar(aux);
		for (int j = 0; j < aux.size(); j++) {
			Object[] temp = (Object[])aux.get(j);
			String nombre_atributo = (String)temp[1];
			for(int i=0;i<ask.length;i++){
				if(nombre_atributo.equals(ask[i])){
					result[i]=true;
					break;
				}
			}
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public boolean existColumn(String schemaname, String tablename,String ask){
		
		boolean result=false;
		List aux=this.getMetadataColumns(schemaname, tablename);
		this.setListauxiliar(aux);
		for (int j = 0; j < aux.size(); j++) {
			Object[] temp = (Object[])aux.get(j);
			String nombre_atributo = (String)temp[1];
			if(nombre_atributo.equals(ask)){
				result=true;
				break;
			}
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public List getListauxiliar() {
		return listauxiliar;
	}
	@SuppressWarnings("unchecked")
	public void setListauxiliar(List listauxiliar) {
		this.listauxiliar = listauxiliar;
	}
	
}
