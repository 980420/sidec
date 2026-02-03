package gehos.ensayo.ensayo_estadisticas.session.comun;

import gehos.comun.shell.IActiveModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;

@Name("utiles_estadisticas")
public class Utiles_estadisticas {
	
	@In	IActiveModule activeModule;
	@In	EntityManager entityManager;
	
	private String patronFecha=SeamResourceBundle.getBundle().getString("patronFecha");
	
 /**
  * Devuelve la fecha en el formato de la internacionalizacion
  * @param fecha
  * @return
  */
	public String formatearFecha(Date fecha){
		try{
			DateFormat df=new SimpleDateFormat(this.patronFecha);
			return df.format(fecha);
		}
		catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Devuelve los dias entre 2 fechas
	 * @param fechaini
	 * @param fechafin
	 * @return
	 */
	public String calcularDias(Date fechaini, Date fechafin)
	{
		String respuesta;
		respuesta=String.valueOf((fechafin.getTime()-fechaini.getTime())/(3600*24*1000));
	    return respuesta;
	}	

	
	/**
	 * Devuelve la edaad de una persona a partir de su fecha de nacimiento
	 * @param fechaNacimiento
	 * @return
	 */
	public int calcularEdad(Date fechaNacimiento) {
		try {
			Date fechaAct = Calendar.getInstance().getTime();
			SimpleDateFormat dfa = new SimpleDateFormat("yyyy");
			SimpleDateFormat dfm = new SimpleDateFormat("MM");
			SimpleDateFormat dfd = new SimpleDateFormat("dd");
			int anoNac = Integer.parseInt(dfa.format(fechaNacimiento));
			int mesNac = Integer.parseInt(dfm.format(fechaNacimiento));
			int diaNac = Integer.parseInt(dfd.format(fechaNacimiento));
			int anoAct = Integer.parseInt(dfa.format(fechaAct));
			int mesAct = Integer.parseInt(dfm.format(fechaAct));
			int diaAct = Integer.parseInt(dfd.format(fechaAct));
			int edad = anoAct - anoNac;
			if (edad <= 0)
				return 0;
			if (mesAct < mesNac)
				edad--;
			else {
				if (diaAct < diaNac)
					edad--;
			}
			return edad;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}
}