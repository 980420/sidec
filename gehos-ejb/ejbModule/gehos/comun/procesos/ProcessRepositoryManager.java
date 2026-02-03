package gehos.comun.procesos;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.procesos.entity.ProcesoDesplegado;
import gehos.comun.procesos.treebuilders.ProcessRepositoryTreeBuilder;
import gehos.comun.procesos.treebuilders.model.DefinicionProceso;
import gehos.comun.procesos.treebuilders.model.DefinicionProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.DiagramaProceso;
import gehos.comun.procesos.treebuilders.model.DiagramaProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.ITreeData;
import gehos.comun.procesos.treebuilders.model.InstanciaProceso;
import gehos.comun.procesos.treebuilders.model.ModuloWrapper;
import gehos.comun.procesos.treebuilders.model.Proceso;
import gehos.comun.procesos.treebuilders.model.ProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.ReglasProceso;
import gehos.comun.procesos.treebuilders.model.ReglasProcesoWrapper;
import gehos.comun.procesos.treebuilders.model.Revision;
import gehos.comun.procesos.treebuilders.model.RevisionWrapper;
import gehos.comun.procesos.util.DatabaseUtility;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.richfaces.model.TreeNode;

@Name("processRepositoryManager")
public class ProcessRepositoryManager {

	@In
	EntityManager entityManager;
	@In
	JbpmContext jbpmContext;
	@In
	ProcessManager processManager;
	@In
	RulesParser rulesParser;

	@In(required = false, create = true, value = "processRepositoryTreeBuilder")
	ProcessRepositoryTreeBuilder treeBuilder;

	private boolean showForm = false;

	public boolean showSlowForm() {
		return showForm;
	}

	InstanciaProceso instanceToDelete;

	public void putInstanceToPossiblyDelete(InstanciaProceso instanciaProceso) {
		this.instanceToDelete = instanciaProceso;
	}

	public void deleteInstance() {
		jbpmContext.getGraphSession().deleteProcessInstance(
				this.instanceToDelete.getId());
	}

	DefinicionProceso definicionToUndeploy;

	public void putDefinitionToPossiblyUndeploy(DefinicionProceso definicion) {
		this.definicionToUndeploy = definicion;
	}

	public void undeployDefinition() {
		this.undeployDefinition(this.definicionToUndeploy);
	}

	public void undeployDefinition(DefinicionProceso definicion) {
		BigInteger procDefId = (BigInteger) entityManager.createNativeQuery(
				"SELECT id_ FROM jbpm_processdefinition "
						+ "where name_=:procName and version_=:version")
				.setParameter("procName", definicion.getNombreProceso())
				.setParameter("version", definicion.getVersion())
				.getSingleResult();
		jbpmContext.getGraphSession().deleteProcessDefinition(
				new Long(procDefId.toString()));
		ProcesoDesplegado proceso = (ProcesoDesplegado) entityManager
				.createQuery(
						"from ProcesoDesplegado p where p.nombreProceso=:procName and p.version=:version")
				.setParameter("procName", definicion.getNombreProceso())
				.setParameter("version", definicion.getVersion())
				.getSingleResult();
		entityManager.remove(proceso);
		entityManager.flush();
	}

	public Integer processCountForModule(Funcionalidad modulo) {
		String procDirectoryPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/"
				+ modulo.getNombre();
		procDirectoryPath = procDirectoryPath.substring(5);
		File processesDirectory = new File(procDirectoryPath);
		return processesDirectory.list().length;
	}

	public Integer revisionsCountForProcess(Proceso proceso) {
		Integer result = 0;
		String revDirectoryPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/"
				+ proceso.getModulo() + "/" + proceso.getNombre();
		revDirectoryPath = revDirectoryPath.substring(5);
		File revDirectory = new File(revDirectoryPath);

		String[] revisions = revDirectory.list();
		Pattern pattern = Pattern.compile("^(revision)-[0-9]+$");
		for (int i = 0; i < revisions.length; i++) {
			Matcher matcher = pattern.matcher(revisions[i]);
			if (matcher.matches()) {
				result++;
			}
		}
		return result;
	}

	public Integer definitiosForAProcessRevision(Revision revision) {
		BigInteger cantDefPorRev = (BigInteger) entityManager
				.createNativeQuery(
						" SELECT count(*) "
								+ "FROM comun.proceso_desplegado pdo where pdo.nombre_proceso = :procName "
								+ "and pdo.revision = :revNumb").setParameter(
						"procName", revision.getNombreProceso()).setParameter(
						"revNumb", revision.getNumero()).getSingleResult();
		return cantDefPorRev.intValue();
	}

	public Integer instacesForAProcessRevision(Revision revision) {
		BigInteger cantInstPorRev = (BigInteger) entityManager
				.createNativeQuery(
						" SELECT count(*) "
								+ "FROM comun.proceso_desplegado pdo INNER JOIN public.jbpm_processdefinition pdon "
								+ "ON (pdo.version=pdon.version_ and pdo.nombre_proceso = pdon.name_) "
								+ "INNER JOIN public.jbpm_processinstance pinst "
								+ "ON (pdon.id_=pinst.processdefinition_) where pdo.revision = :revNumb "
								+ "and pinst.end_ is null and pdo.nombre_proceso = :procName")
				.setParameter("procName", revision.getNombreProceso())
				.setParameter("revNumb", revision.getNumero())
				.getSingleResult();
		return cantInstPorRev.intValue();
	}

	@SuppressWarnings("unchecked")
	public Integer actualDeployedRevisionForProcess() {
		Proceso proceso = ((ProcesoWrapper) this.selectedItem).getValue();
		List<Integer> result = entityManager
				.createNativeQuery(
						"select revision from comun.proceso_desplegado "
								+ "where nombre_proceso = :procName order by version desc limit 1")
				.setParameter("procName", proceso.getNombre()).getResultList();
		if (result.size() > 0)
			return result.get(0);
		return -1;
	}

	public void subir() {
		if (this.selectedItem instanceof DefinicionProcesoWrapper) {
			DefinicionProceso definicionProceso = ((DefinicionProcesoWrapper) this.selectedItem)
					.getValue();
			Revision revision = new Revision(definicionProceso
					.getNombreProceso(), definicionProceso.getModulo(),
					definicionProceso.getNumeroRevision(), definicionProceso
							.getTieneReglas());
			this.selectedItem = new RevisionWrapper(revision, false);
		} else if (this.selectedItem instanceof ReglasProcesoWrapper) {
			ReglasProceso reglasProceso = ((ReglasProcesoWrapper) this.selectedItem)
					.getValue();
			Revision revision = new Revision(reglasProceso.getNombreProceso(),
					reglasProceso.getModulo(), reglasProceso
							.getNumeroRevision(), true);
			this.selectedItem = new RevisionWrapper(revision, false);
		} else if (this.selectedItem instanceof DiagramaProcesoWrapper) {
			DiagramaProceso diagramaProceso = ((DiagramaProcesoWrapper) this.selectedItem)
					.getValue();
			Revision revision = new Revision(
					diagramaProceso.getNombreProceso(), diagramaProceso
							.getModulo(), diagramaProceso.getNumeroRevision(),
					diagramaProceso.getTieneReglas());
			this.selectedItem = new RevisionWrapper(revision, false);
		} else if (this.selectedItem instanceof RevisionWrapper) {
			Revision revision = ((RevisionWrapper) this.selectedItem)
					.getValue();
			Proceso proceso = new Proceso(revision.getNombreProceso(), revision
					.getModulo());
			this.selectedItem = new ProcesoWrapper(proceso, false);
		} else if (this.selectedItem instanceof ProcesoWrapper) {
			Proceso proceso = ((ProcesoWrapper) this.selectedItem).getValue();
			Funcionalidad funcionalidad = (Funcionalidad) entityManager
					.createQuery("from Funcionalidad f where f.nombre=:modName")
					.setParameter("modName", proceso.getModulo())
					.getSingleResult();
			this.selectedItem = new ModuloWrapper(funcionalidad, false);
		} else if (this.selectedItem instanceof ModuloWrapper) {
			this.selectedItem = null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> modules() {
		List<Funcionalidad> result = new ArrayList<Funcionalidad>();
		if (this.selectedItem == null) {
			String procDirectoryPath = System
					.getProperty("jboss.server.home.url")
					+ "/deploy/gehos-ear.ear/business_processes/";
			procDirectoryPath = procDirectoryPath.substring(5);
			File processesDirectory = new File(procDirectoryPath);
			File[] procPerModule = processesDirectory.listFiles();
			for (int i = 0; i < procPerModule.length; i++) {
				File file = procPerModule[i];
				List<Funcionalidad> modulos = entityManager.createQuery(
						"from Funcionalidad mod where mod.nombre=:modName")
						.setParameter("modName", file.getName())
						.getResultList();
				if (modulos.size() > 0) {
					Funcionalidad modulo = modulos.get(0);
					result.add(modulo);
				}
			}
		}
		return result;
	}

	public List<Proceso> processesForSelectedModule() {
		List<Proceso> result = new ArrayList<Proceso>();
		if (this.selectedItem instanceof ModuloWrapper) {
			Funcionalidad modulo = ((ModuloWrapper) this.selectedItem)
					.getValue();
			String procDirectoryPath = System
					.getProperty("jboss.server.home.url")
					+ "/deploy/gehos-ear.ear/business_processes/"
					+ modulo.getNombre();
			procDirectoryPath = procDirectoryPath.substring(5);
			File processesDirectory = new File(procDirectoryPath);
			String[] procesos = processesDirectory.list();
			for (int i = 0; i < procesos.length; i++) {
				Proceso proceso = new Proceso(procesos[i], modulo.getNombre());
				result.add(proceso);
			}
		}
		return result;
	}

	public List<Revision> revisionsForSelectedProcess() {
		List<Revision> result = new ArrayList<Revision>();
		if (this.selectedItem instanceof ProcesoWrapper) {
			Proceso process = ((ProcesoWrapper) this.selectedItem).getValue();
			String revDirectoryPath = System
					.getProperty("jboss.server.home.url")
					+ "/deploy/gehos-ear.ear/business_processes/"
					+ process.getModulo() + "/" + process.getNombre();
			revDirectoryPath = revDirectoryPath.substring(5);
			File revDirectory = new File(revDirectoryPath);
			String[] revisions = revDirectory.list();
			Pattern pattern = Pattern.compile("^(revision)-[0-9]+$");
			for (int i = 0; i < revisions.length; i++) {
				Matcher matcher = pattern.matcher(revisions[i]);
				if (matcher.matches()) {
					String number = revisions[i].split("-")[1].trim();
					File reglas = new File(revDirectoryPath + "/revision-"
							+ number + "/processrules.drl");
					Revision revision = new Revision(process.getNombre(),
							process.getModulo(), Integer.parseInt(number),
							reglas.exists());
					result.add(revision);
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DefinicionProceso> deployedDefinitionsForSelectedProcess() {
		List<DefinicionProceso> result = new ArrayList<DefinicionProceso>();
		if (this.selectedItem instanceof ProcesoWrapper) {
			Proceso process = ((ProcesoWrapper) this.selectedItem).getValue();
			List defParaProceso = entityManager
					.createNativeQuery(
							"SELECT version, revision FROM comun.proceso_desplegado where nombre_proceso = :procName")
					.setParameter("procName", process.getNombre())
					.getResultList();
			for (Object def : defParaProceso) {
				Object[] definit = (Object[]) def;
				Integer version = (Integer) definit[0];
				Integer revision = (Integer) definit[1];
				DefinicionProceso definicion = new DefinicionProceso();
				definicion.setVersion(version);
				definicion.setNumeroRevision(revision);
				definicion.setNombreProceso(process.getNombre());
				BigInteger cantInstPorRevYVersion = (BigInteger) entityManager
						.createNativeQuery(
								" SELECT count(*) "
										+ "FROM comun.proceso_desplegado pdo INNER JOIN public.jbpm_processdefinition pdon "
										+ "ON (pdo.version=pdon.version_ and pdo.nombre_proceso = pdon.name_) "
										+ "INNER JOIN public.jbpm_processinstance pinst "
										+ "ON (pdon.id_=pinst.processdefinition_) where pdo.revision = :revNumb "
										+ "and pinst.end_ is null and pdo.version=:vers "
										+ "and pdo.nombre_proceso = :procName")
						.setParameter("procName", process.getNombre())
						.setParameter("revNumb", revision).setParameter("vers",
								version).getSingleResult();
				definicion.setNumeroInstancias(cantInstPorRevYVersion
						.intValue());
				result.add(definicion);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DefinicionProceso> deployedDefinitionsForSelectedRevision() {
		List<DefinicionProceso> result = new ArrayList<DefinicionProceso>();
		if (this.selectedItem instanceof RevisionWrapper) {
			Revision revision = ((RevisionWrapper) this.selectedItem)
					.getValue();
			List versParaRev = entityManager.createNativeQuery(
					"SELECT version FROM comun.proceso_desplegado where revision = :revNumb "
							+ "and nombre_proceso = :procName").setParameter(
					"revNumb", revision.getNumero()).setParameter("procName",
					revision.getNombreProceso()).getResultList();
			for (Object ver : versParaRev) {
				Integer version = (Integer) ver;
				DefinicionProceso definicion = new DefinicionProceso();
				definicion.setVersion(version);
				definicion.setNombreProceso(revision.getNombreProceso());
				BigInteger cantInstPorRevYVersion = (BigInteger) entityManager
						.createNativeQuery(
								" SELECT count(*) "
										+ "FROM comun.proceso_desplegado pdo INNER JOIN public.jbpm_processdefinition pdon "
										+ "ON (pdo.version=pdon.version_ and pdo.nombre_proceso = pdon.name_) "
										+ "INNER JOIN public.jbpm_processinstance pinst "
										+ "ON (pdon.id_=pinst.processdefinition_) where pdo.revision = :revNumb "
										+ "and pinst.end_ is null and pdo.version=:vers "
										+ "and pdo.nombre_proceso = :procName")
						.setParameter("procName", revision.getNombreProceso())
						.setParameter("revNumb", revision.getNumero())
						.setParameter("vers", version).getSingleResult();
				definicion.setNumeroInstancias(cantInstPorRevYVersion
						.intValue());
				result.add(definicion);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<InstanciaProceso> runningInstancesForSelectedProcess() {
		List<InstanciaProceso> result = new ArrayList<InstanciaProceso>();
		if (this.selectedItem instanceof ProcesoWrapper) {
			Proceso process = ((ProcesoWrapper) this.selectedItem).getValue();
			List instParaProc = entityManager
					.createNativeQuery(
							" SELECT pinst.id_, pdon.name_ as nombre, pdo.version, pdo.revision, pinst.start_ as iniciado "
									+ "FROM comun.proceso_desplegado pdo INNER JOIN public.jbpm_processdefinition pdon "
									+ "ON (pdo.version=pdon.version_ and pdo.nombre_proceso = pdon.name_) "
									+ "INNER JOIN public.jbpm_processinstance pinst "
									+ "ON (pdon.id_=pinst.processdefinition_) where "
									+ "pinst.end_ is null and pdo.nombre_proceso = :procName")
					.setParameter("procName", process.getNombre())
					.getResultList();
			for (Object inst : instParaProc) {
				Object[] datosInst = (Object[]) inst;
				InstanciaProceso instancia = new InstanciaProceso();
				instancia.setId(Long.parseLong(datosInst[0].toString()));
				instancia.setNombreProceso((String) datosInst[1]);
				instancia.setVersion((Integer) datosInst[2]);
				instancia.setNumeroRevision((Integer) datosInst[3]);
				instancia.setIniciadoEn((Date) datosInst[4]);
				result.add(instancia);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<InstanciaProceso> runningInstancesForSelectedProcessRevision() {
		List<InstanciaProceso> result = new ArrayList<InstanciaProceso>();
		if (this.selectedItem instanceof RevisionWrapper) {
			Revision revision = ((RevisionWrapper) this.selectedItem)
					.getValue();
			List instParaRev = entityManager
					.createNativeQuery(
							" SELECT pinst.id_, pdon.name_ as nombre, pdo.version, pdo.revision, pinst.start_ as iniciado "
									+ "FROM comun.proceso_desplegado pdo INNER JOIN public.jbpm_processdefinition pdon "
									+ "ON (pdo.version=pdon.version_ and pdo.nombre_proceso = pdon.name_) "
									+ "INNER JOIN public.jbpm_processinstance pinst "
									+ "ON (pdon.id_=pinst.processdefinition_) where pdo.revision = :revNumb "
									+ "and pinst.end_ is null and pdo.nombre_proceso = :procName")
					.setParameter("revNumb", revision.getNumero())
					.setParameter("procName", revision.getNombreProceso())
					.getResultList();
			for (Object inst : instParaRev) {
				Object[] datosInst = (Object[]) inst;
				InstanciaProceso instancia = new InstanciaProceso();
				instancia.setId(Long.parseLong(datosInst[0].toString()));
				instancia.setNombreProceso((String) datosInst[1]);
				instancia.setVersion((Integer) datosInst[2]);
				instancia.setNumeroRevision((Integer) datosInst[3]);
				instancia.setIniciadoEn((Date) datosInst[4]);
				result.add(instancia);
			}
		}
		return result;
	}

	private ITreeData selectedItem;
	@SuppressWarnings( { "unchecked", "unused" })
	private TreeNode selectedTreenode;

	public void deploySelectedProcess() {
		try {
			DefinicionProceso proceso = ((DefinicionProcesoWrapper) this.selectedItem)
					.getValue();
			String sourcePath = System.getProperty("jboss.server.home.url")
					+ "/deploy/gehos-ear.ear/business_processes/"
					+ proceso.getModulo() + "/" + proceso.getNombreProceso()
					+ "/revision-" + proceso.getNumeroRevision()
					+ "/processdefinition.xml";
			String targetPath = System.getProperty("jboss.server.home.url")
					+ "/deploy/gehos-ear.ear/gehos-ejb.jar/processdefinition.xml";

			sourcePath = sourcePath.substring(5);
			targetPath = targetPath.substring(5);
			File sourceFile = new File(sourcePath);
			File targetFile = new File(targetPath);
			if (targetFile.exists())
				targetFile.delete();
			FileUtils.copyFile(sourceFile, targetFile);

			ProcessDefinition processDefinition = ProcessDefinition
					.parseXmlResource("processdefinition.xml");
			jbpmContext.deployProcessDefinition(processDefinition);
			Integer version;
			ProcessDefinition definition = jbpmContext.getGraphSession()
					.findLatestProcessDefinition(proceso.getNombreProceso());
			if (definition == null)
				version = 1;
			else
				version = definition.getVersion()/* + 1 */;

			processManager.registerProcessDefinitionRevisionForVersion(proceso
					.getModulo(), proceso.getNombreProceso(), version, proceso
					.getNumeroRevision(), proceso.getTieneReglas());

			if (proceso.getTieneReglas()) {
				RuleBase ruleBase;
				ruleBase = rulesParser.readRule("/" + proceso.getModulo() + "/"
						+ proceso.getNombreProceso() + "/revision-"
						+ proceso.getNumeroRevision() + "/processrules.drl",
						RulesDirectoryBase.business_processes);
				WorkingMemory memory = ruleBase.newStatefulSession();
				String workingMemName = proceso.getNombreProceso()
						+ "-workingMemory-v" + proceso.getNumeroRevision();
				Contexts.getApplicationContext().set(workingMemName, memory);
			}
			// jbpmContext.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void cleanDeployedProcessesDefinitions() {
		try {
			DatabaseUtility.cleanDeployedProcesses();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setSelectedItem(Object param) {
		if (param instanceof Proceso) {
			ProcesoWrapper wrapper = new ProcesoWrapper((Proceso) param, false);
			this.selectedItem = wrapper;
		} else if (param instanceof Revision) {
			RevisionWrapper wrapper = new RevisionWrapper((Revision) param,
					false);
			this.selectedItem = wrapper;
		} else if (param instanceof Funcionalidad) {
			ModuloWrapper wrapper = new ModuloWrapper((Funcionalidad) param,
					false);
			this.selectedItem = wrapper;
		}
	}

	public void setSelectedItem(Revision revision, String recurso) {
		if (recurso.equals("definicion")) {
			DefinicionProceso definicionProceso = new DefinicionProceso(
					revision.getNombreProceso(), revision.getModulo(), revision
							.getNumero(), revision.getTieneReglas());
			this.selectedItem = new DefinicionProcesoWrapper(definicionProceso,
					false);
		} else if (recurso.equals("diagrama")) {
			DiagramaProceso diagramaProceso = new DiagramaProceso(revision
					.getNombreProceso(), revision.getModulo(), revision
					.getNumero(), revision.getTieneReglas());
			this.selectedItem = new DiagramaProcesoWrapper(diagramaProceso,
					false);
			createTempProcessImage(diagramaProceso);
		} else if (recurso.equals("reglas")) {
			ReglasProceso reglasProceso = new ReglasProceso(revision
					.getNombreProceso(), revision.getModulo(), revision
					.getNumero());
			this.selectedItem = new ReglasProcesoWrapper(reglasProceso, false);
		}
	}

	private String tempImgUUID = "";

	@SuppressWarnings("unchecked")
	public void setSelectedItem(ITreeData selectedItem, TreeNode node) {
		this.selectedItem = selectedItem;
		this.selectedTreenode = node;

		if (this.selectedItem instanceof DiagramaProcesoWrapper) {
			DiagramaProceso diagrama = (DiagramaProceso) this.selectedItem
					.getValue();
			createTempProcessImage(diagrama);
		}
	}

	private void createTempProcessImage(DiagramaProceso diagrama) {
		String diagPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/business_processes/"
				+ diagrama.getModulo() + "/" + diagrama.getNombreProceso()
				+ "/revision-" + diagrama.getNumeroRevision()
				+ "/processimage.jpg";
		diagPath = diagPath.substring(5);
		File imgOrigen = new File(diagPath);
		this.tempImgUUID = UUID.randomUUID().toString();
		String destPath = System.getProperty("jboss.server.home.url")
				+ "/deploy/gehos-ear.ear/gehos.war/modConfiguracion/procesos/tmp/"
				+ tempImgUUID + ".png";
		destPath = destPath.substring(5);
		File destFile = new File(destPath);
		try {
			String tempPath = System.getProperty("jboss.server.home.url")
					+ "/deploy/gehos-ear.ear/gehos.war/modConfiguracion/procesos/tmp/";
			tempPath = tempPath.substring(5);
			File tmp = new File(tempPath);
			if (tmp.list().length > 10)
				FileUtils.deleteDirectory(tmp);
			FileUtils.copyFile(imgOrigen, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ProcessRepositoryTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(ProcessRepositoryTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public ITreeData getSelectedItem() {
		return selectedItem;
	}

	public String getTempImgUUID() {
		return tempImgUUID;
	}

	public void setTempImgUUID(String tempImgUUID) {
		this.tempImgUUID = tempImgUUID;
	}

	public boolean isShowForm() {
		return showForm;
	}

	public void setShowForm(boolean showForm) {
		this.showForm = showForm;
	}

}
