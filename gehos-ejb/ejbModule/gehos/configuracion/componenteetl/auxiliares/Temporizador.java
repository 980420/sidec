package gehos.configuracion.componenteetl.auxiliares;

import java.text.DecimalFormat;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Name("temporizador")
@Scope(ScopeType.SESSION)
@AutoCreate
@Startup
public class Temporizador {
	private long inicio;
	private long fin;
	private long tiempo;
	
	public Temporizador() {
		inicio = System.currentTimeMillis();
	}
	
	public String tiempoTranscurrido(){
		fin = System.currentTimeMillis();
		tiempo = fin - inicio;
		String tiempoForma;
		if(tiempo < 1000){
			tiempoForma = tiempo + "ms";
		}else{
			double tiempoS = Math.rint((tiempo / 1000.0)*10)/10;
			if(tiempoS < 60){
				tiempoForma = tiempoS + "s";
			}else{
				double min = Math.rint((tiempoS / 60.0)*10)/10;
				tiempoForma = min + "min";
			}
		}
		return tiempoForma;
	}
	
	
}
