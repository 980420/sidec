package gehos.comun.reportes.session;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("printingControlador")
@Scope(ScopeType.PAGE)
public class PrintingControlador {

	private List<String> printersList;
	private String printerComboSelectedValue; 
	
	private Boolean printAll;
	private String printRangeSelectedValue;
	
	private String colorSelectedValue;
	private String qualitySelectedValue;
	private String orientationSelectedValue;
	
	private Integer cantCopias;
	private Integer pageInicial;
	private Integer pageFinal;
	
	@Create
	public void inicializar(){
		cantCopias = 1;
		printRangeSelectedValue="all";
		colorSelectedValue="gris";
		qualitySelectedValue="normal";
		orientationSelectedValue="vertical";
	}
	
	public void imprimirButton(){
		
	}
	
	public void cancelarButton(){
		
	}
	
	
	public List<String> getPrintersList() {
		return printersList;
	}
	public void setPrintersList(List<String> printersList) {
		this.printersList = printersList;
	}
	public Integer getCantCopias() {
		return cantCopias;
	}
	public void setCantCopias(Integer cantCopias) {
		this.cantCopias = cantCopias;
	}
	public Integer getPageInicial() {
		return pageInicial;
	}
	public void setPageInicial(Integer pageInicial) {
		this.pageInicial = pageInicial;
	}
	public Integer getPageFinal() {
		return pageFinal;
	}
	public void setPageFinal(Integer pageFinal) {
		this.pageFinal = pageFinal;
	}
	public String getPrinterComboSelectedValue() {
		return printerComboSelectedValue;
	}
	public void setPrinterComboSelectedValue(String printerComboSelectedValue) {
		this.printerComboSelectedValue = printerComboSelectedValue;
	}


	public Boolean getPrintAll() {
		if (printRangeSelectedValue.equals("all"))
			printAll = true;
		else
			printAll = false;
		return printAll;
	}


	public void setPrintAll(Boolean printAll) {
		this.printAll = printAll;
	}


	public String getPrintRangeSelectedValue() {
		return printRangeSelectedValue;
	}


	public void setPrintRangeSelectedValue(String printRangeSelectedValue) {
		this.printRangeSelectedValue = printRangeSelectedValue;
	}


	public String getColorSelectedValue() {
		return colorSelectedValue;
	}


	public void setColorSelectedValue(String colorSelectedValue) {
		this.colorSelectedValue = colorSelectedValue;
	}


	public String getQualitySelectedValue() {
		return qualitySelectedValue;
	}


	public void setQualitySelectedValue(String qualitySelectedValue) {
		this.qualitySelectedValue = qualitySelectedValue;
	}


	public String getOrientationSelectedValue() {
		return orientationSelectedValue;
	}


	public void setOrientationSelectedValue(String orientationSelectedValue) {
		this.orientationSelectedValue = orientationSelectedValue;
	}
	
	
	
}
