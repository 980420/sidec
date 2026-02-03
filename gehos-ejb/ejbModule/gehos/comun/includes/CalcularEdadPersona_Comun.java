package gehos.comun.includes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;

@Name("calcularEdadPersona_Comun")
public class CalcularEdadPersona_Comun {

	private static final long SEGUNDOS = 1000L;
	private static final long MINUTOS = 60L * SEGUNDOS;
	private static final long HORAS = 60L * MINUTOS;
	private static final long DIAS = 24L * HORAS;
	private static final long SEMANAS = 7L * DIAS;
	private static final long MESES = 30L * DIAS;
	private static final long ANNOS = 365L * DIAS;
	
	private Long diferenciaDias(Date ini, Date fin) {
		return Math.abs(ini.getTime() - fin.getTime()) / DIAS;
	}

	private Long diferenciaMeses(Date ini, Date fin) {
		return Math.abs(ini.getTime() - fin.getTime()) / MESES;
	}

	private Long diferenciaAnnos(Date ini, Date fin) {
		return Math.abs(ini.getTime() - fin.getTime()) / ANNOS;
	}

	/*public String edad() {
		String edad = "";
		Date hoy = new Date();
		Date fechaNacimiento = hojaF.getFechaNacimiento();
		Long annos = diferenciaAnnos(hoy, fechaNacimiento);
		Long meses = diferenciaMeses(hoy, fechaNacimiento);
		Long dias = diferenciaDias(hoy, fechaNacimiento);
		if (annos > 2)
			edad = annos.toString() + " "
					+ SeamResourceBundle.getBundle("annos");
		else if (meses > 1)
			edad = meses.toString() + " "
					+ SeamResourceBundle.getBundle("meses");
		else
			edad = dias.toString() + " " + SeamResourceBundle.getBundle("dias");
		return edad;
	}*/

	public String edad(Date fechaNacimiento) {
		try {
			String edad = "";
			Date hoy = Calendar.getInstance().getTime();
			SimpleDateFormat dfa = new SimpleDateFormat("yyyy");
			SimpleDateFormat dfm = new SimpleDateFormat("MM");
			SimpleDateFormat dfd = new SimpleDateFormat("dd");
			int anoNac = Integer.parseInt(dfa.format(fechaNacimiento));
			int mesNac = Integer.parseInt(dfm.format(fechaNacimiento));
			int diaNac = Integer.parseInt(dfd.format(fechaNacimiento));
			int anoHoy = Integer.parseInt(dfa.format(hoy));
			int mesHoy = Integer.parseInt(dfm.format(hoy));
			int diaHoy = Integer.parseInt(dfd.format(hoy));
			Long dias = diferenciaDias(hoy, fechaNacimiento);
			Long meses = diferenciaMeses(hoy, fechaNacimiento);
			if (dias <= 28)
				edad = dias.toString() + " "
						+ SeamResourceBundle.getBundle().getString("dias");
			else if (meses <= 24)
				edad = meses.toString() + " "
						+ SeamResourceBundle.getBundle().getString("meses");
			else {
				int annos = anoHoy - anoNac;
				if (mesHoy < mesNac)
					annos--;
				else{
					if (mesHoy == mesNac && diaHoy < diaNac){					
						annos--;
				}
			}
				edad = annos + " "
						+ SeamResourceBundle.getBundle().getString("annos");
			}
			return edad;
		} catch (Exception e) {
			return SeamResourceBundle.getBundle().getString("vacio");
		}

	}
}
