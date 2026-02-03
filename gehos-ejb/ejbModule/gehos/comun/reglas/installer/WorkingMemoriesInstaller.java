package gehos.comun.reglas.installer;

import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;

import java.util.List;

import javax.persistence.EntityManager;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.JbpmContext;

@Startup
@Scope(ScopeType.APPLICATION)
@Name("workingMemoriesInstaller")
public class WorkingMemoriesInstaller {

	@In
	RulesParser rulesParser;
	@In
	EntityManager entityManager;
	@In
	JbpmContext jbpmContext;

	@Create
	@Transactional
	@SuppressWarnings("unchecked")
	public void constructor() throws Exception {
		List listaRevDeProcesos = entityManager
				.createQuery(
						"select distinct proc.funcionalidad.nombre, "
								+ "proc.nombreProceso, proc.revision, proc.tieneReglas from ProcesoDesplegado proc")
				.getResultList();
		System.out
				.println("Instalando workingMemories para los procesos del negocio...");
		for (Object revProc : listaRevDeProcesos) {
			Object[] revValues = (Object[]) revProc;
			Boolean tieneReglas = (Boolean) revValues[3];
			if (tieneReglas) {
				RuleBase ruleBase;
				try {
					ruleBase = rulesParser.readRule("/" + revValues[0] + "/"
							+ revValues[1] + "/revision-" + revValues[2]
							+ "/processrules.drl",
							RulesDirectoryBase.business_processes);
					if (ruleBase != null) {
						WorkingMemory memory = ruleBase.newStatefulSession();
						String workingMemName = revValues[1]
								+ "-workingMemory-v" + revValues[2];
						Contexts.getApplicationContext().set(workingMemName,
								memory);
					}
				} catch (Exception e) {
					System.out.println("reglas del proceso " + revValues[1]
							+ " del módulo " + revValues[0]
							+ " de la revisión " + revValues[2]
							+ " no pudieron ser parseadas");
					continue;
				}
			}
		}

	}

}
