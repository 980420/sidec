package gehos.comun.reglas.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;

@Name("rulesEditor")
public class RulesEditor {

	private String rule;
	private List<String> keywords = new ArrayList<String>();

	public String ruleToHtml(String rulePath) {
		String result = "";
		rule = loadFromFile(rulePath);
		result = rule.replace("\n", "<br/>");
		result = result.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		for (String keyword : keywords) {
			result = result
					.replace(keyword, "<strong>" + keyword + "</strong>");
		}
		return result;
	}

	@Create
	public void constructor() {
		// rule = loadFromFile("id_generator.drl");
		keywords.add("package");
		keywords.add("import");
		keywords.add("function");
		keywords.add("for");
		keywords.add("return");
		keywords.add("rule");
		keywords.add("when");
		keywords.add("then");
		keywords.add("end");
	}

	private String loadFromFile(String rulePath) {
		File ruleFile = getFile(rulePath);
		String content = "";
		try {
			content = FileUtils.readFileToString(ruleFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	private File getFile(String rulePath) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		File dir = new File(context.getRealPath("/"));
		String rulepath = dir.getParent() + rulePath;
		File ruleFile = new File(rulepath);
		return ruleFile;
	}
}
