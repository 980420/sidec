package gehos.configuracion.clinicaldata.ubicaciones.management;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;

@Name("identificadorManager")
public class IdentificadorManager {
	
	private String rule;
	private boolean editing = false;
	private List<String> keywords = new ArrayList<String>();
	
	public String ruleToHtml(){
		String result = "";
		result = rule.replace("\n", "<br/>");
		result = result.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		for (String keyword : keywords) {
			result = result.replace(keyword, "<strong>" + keyword + "</strong>");
		}
		return result;
	}
	
	@Create
	public void constructor(){
		rule = loadFromFile("id_generator.drl");
		keywords.add("package");  keywords.add("import");
		keywords.add("function");	keywords.add("for");
		keywords.add("return"); keywords.add("rule");
		keywords.add("when"); keywords.add("then"); keywords.add("end");	
	}
	
	public String defaultCofiguration() {
		return loadFromFile("id_generator.dat");
	}
	
	public void porDefecto(){
		this.rule = this.defaultCofiguration();
		saveStringToFile(this.rule);
	}
	
	public void Salvar(){
		saveStringToFile(this.rule);
		this.editing = false;
	}

	private void saveStringToFile(String rule) {
		try {
			File ruleFile = getFile("id_generator.drl");
			FileOutputStream foutput = new FileOutputStream(ruleFile);
			IOUtils.write(rule, foutput);
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String loadFromFile(String filename) {
		File ruleFile = getFile(filename);
		String content = "";
		try {
			content = FileUtils.readFileToString(ruleFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	private File getFile(String filename) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();

		File dir = new File(context.getRealPath("/"));
		String rulepath = dir.getParent() + "//business_rules//configuracion//ubicaciones//" + filename; 
		File ruleFile = new File(rulepath);
		return ruleFile;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

}
