package gehos.comun.util;

import java.io.FileWriter;
import java.io.PrintWriter;

import org.hibernate.EmptyInterceptor;

public class QueriesInterceptor extends EmptyInterceptor {
	
	public String onPrepareStatement(String sql){
		String a = super.onPrepareStatement(sql);
		if(a.contains("select")){
			try{
				FileWriter fichero = new FileWriter("query.sql",true);
				PrintWriter pw = new PrintWriter(fichero);
				pw.println(a);
				fichero.close();
			}
			catch (Exception e) {
				String aa = "";
				// TODO: handle exception
			}
		}
		return a;
	}

}
