package gehos.comun.util.reglasMedicamentos;

import java.util.Calendar;
import java.util.Date;

public class FechaCaducidad {
	String codigoTipoRecipe;	
	Calendar hoy = Calendar.getInstance();	
	Integer dias;
	
	public Integer getDias() {
		return dias;
	}
	public void setDias(Integer dias) {
		this.dias = dias;
		hoy.setTime(new Date());
		hoy.add(Calendar.DAY_OF_MONTH, dias);		
	}
	public Calendar getHoy() {
		return hoy;
	}
	public void setHoy(Calendar hoy) {
		this.hoy = hoy;		
	}
	public String getCodigoTipoRecipe() {
		return codigoTipoRecipe;
	}
	public void setCodigoTipoRecipe(String codigoTipoRecipe) {
		this.codigoTipoRecipe = codigoTipoRecipe;
	}	
	
}
