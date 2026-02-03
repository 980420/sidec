package gehos.configuracion.samples.reglasdelnegocio;

import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("rulesTestController")
public class RulesTestController {

	@In
	RulesParser rulesParser;

	private Entity entity;
	private RuleResult ruleResult;

	@Create
	public void create() {
		if (this.entity == null) {
			ChildEntity childEntity = new ChildEntity(0);
			this.entity = new Entity("test", childEntity);
			ruleResult = new RuleResult();
		}
	}

	public void checkAmount() throws Exception {
		RuleBase ruleBase = rulesParser.readRule(
				"/configuracion/test/test.drl",
				RulesDirectoryBase.business_processes);
		StatefulSession statefulSession = ruleBase.newStatefulSession();

		statefulSession.insert(entity);
		statefulSession.insert(entity.getChildEntity());
		statefulSession.insert(ruleResult);
		statefulSession.fireAllRules();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public RuleResult getRuleResult() {
		return ruleResult;
	}

	public void setRuleResult(RuleResult ruleResult) {
		this.ruleResult = ruleResult;
	}

}
