package gehos.comun.reglas.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

@Name("rulesParser")
@AutoCreate
public class RulesParser {

	public RuleBase readRule(String ruleFileName,
			RulesDirectoryBase rulesDirectoryBase) throws Exception {

		String rulespath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/" + rulesDirectoryBase.getDirectorio()
				+ ruleFileName;

		rulespath = rulespath.substring(5);

		File ruleFile = new File(rulespath);

		if (ruleFile.exists()) {
			FileInputStream fileInputStream = new FileInputStream(ruleFile);
			Reader source = new InputStreamReader(fileInputStream);
			PackageBuilder builder = new PackageBuilder();
			builder.addPackageFromDrl(source);
			Package pkg = builder.getPackage();
			RuleBase ruleBase = RuleBaseFactory.newRuleBase();
			ruleBase.addPackage(pkg);
			return ruleBase;
		}
		return null;
	}
}
