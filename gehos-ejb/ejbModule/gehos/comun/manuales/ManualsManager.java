package gehos.comun.manuales;

import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.comun.shell.IActiveModule;
import gehos.comun.updater.IpKey;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.PAGE)
@Name("manualsManager")
public class ManualsManager {

	private ManualData manual;
	
	@In
	private IActiveModule activeModule;
	@In
	private RulesParser rulesParser;

	public ManualData getManual() {
		if(manual == null){
			manual = new ManualData();
			try {
				manual.setSubmodule(activeModule.getActiveModule().getFuncionalidadPadre().getNombre());
			} catch (Exception e) {
				System.err.print("No existe funcionalidad padre");
			}
			
			RuleBase ruleBase = null;
			try {
				ruleBase = rulesParser.readRule(
						"/comun/manualSubmodulo.drl",
						RulesDirectoryBase.business_rules);
				
				//Este bloque de codigo estaba fuera del try{}catch(){}
				//Había un NullPointerException potencial
				StatefulSession session = ruleBase.newStatefulSession();
				session.insert(manual);
				session.fireAllRules();
				//Fin Bloque
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return manual;
	}

	public void setManual(ManualData manual) {
		this.manual = manual;
	}
	
	
	
	
}
