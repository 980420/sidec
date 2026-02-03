package gehos.bitacora.entity;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.core.SeamResourceBundle;


public class Mes {
	private String enero = SeamResourceBundle.getBundle().getString("month_0");
	private String febrero = SeamResourceBundle.getBundle().getString("month_1");
	private String marzo = SeamResourceBundle.getBundle().getString("month_2");
	private String abril = SeamResourceBundle.getBundle().getString("month_3");
	private String mayo = SeamResourceBundle.getBundle().getString("month_4");
	private String junio = SeamResourceBundle.getBundle().getString("month_5");
	private String julio = SeamResourceBundle.getBundle().getString("month_6");
	private String agosto = SeamResourceBundle.getBundle().getString("month_7");
	private String septiembre = SeamResourceBundle.getBundle().getString("month_8");
	private String octubre = SeamResourceBundle.getBundle().getString("month_9");
	private String noviembre = SeamResourceBundle.getBundle().getString("month_10");
	private String diciembre = SeamResourceBundle.getBundle().getString("month_11");
	
	public Mes() {
		// TODO Auto-generated constructor stub
	}
	
	public Mes (String enero, String febrero, String marzo, String abril, String mayo, String junio, String julio, String agosto, String septiembre, String octubre, String noviembre, String diciembre){
		this.enero = enero;
		this.febrero = febrero;
		this.marzo = marzo;
		this.abril = abril;
		this.mayo = mayo;
		this.junio = junio;
		this.julio = julio;
		this.agosto = agosto;
		this.septiembre = septiembre;
		this.octubre = octubre;
		this.noviembre = noviembre;
		this.diciembre = diciembre;		
	}
	
	public List<String> meses(){
		List<String> meses =  new ArrayList<String>();
		meses.add(getEnero());
		meses.add(getFebrero());
		meses.add(getMarzo());
		meses.add(getAbril());
		meses.add(getMayo());
		meses.add(getJunio());
		meses.add(getJulio());
		meses.add(getAgosto());
		meses.add(getSeptiembre());
		meses.add(getOctubre());
		meses.add(getNoviembre());
		meses.add(getDiciembre());
		return meses;
		
	}

	public String getEnero() {
		return enero;
	}

	public void setEnero(String enero) {
		this.enero = enero;
	}

	public String getFebrero() {
		return febrero;
	}

	public void setFebrero(String febrero) {
		this.febrero = febrero;
	}

	public String getMarzo() {
		return marzo;
	}

	public void setMarzo(String marzo) {
		this.marzo = marzo;
	}

	public String getAbril() {
		return abril;
	}

	public void setAbril(String abril) {
		this.abril = abril;
	}

	public String getMayo() {
		return mayo;
	}

	public void setMayo(String mayo) {
		this.mayo = mayo;
	}

	public String getJunio() {
		return junio;
	}

	public void setJunio(String junio) {
		this.junio = junio;
	}

	public String getJulio() {
		return julio;
	}

	public void setJulio(String julio) {
		this.julio = julio;
	}

	public String getAgosto() {
		return agosto;
	}

	public void setAgosto(String agosto) {
		this.agosto = agosto;
	}

	public String getSeptiembre() {
		return septiembre;
	}

	public void setSeptiembre(String septiembre) {
		this.septiembre = septiembre;
	}

	public String getOctubre() {
		return octubre;
	}

	public void setOctubre(String octubre) {
		this.octubre = octubre;
	}

	public String getNoviembre() {
		return noviembre;
	}

	public void setNoviembre(String noviembre) {
		this.noviembre = noviembre;
	}

	public String getDiciembre() {
		return diciembre;
	}

	public void setDiciembre(String diciembre) {
		this.diciembre = diciembre;
	}
	
	

}
