package gehos.comun.procesos;

import gehos.comun.procesos.entity.Modulo;
import gehos.comun.procesos.entity.ProcesoDesplegado;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@AutoCreate
@Name("processManager")
public class ProcessManager {

	@In
	EntityManager entityManager;

	public Integer getProcessRevisionOfTask(
			org.jbpm.taskmgmt.exe.TaskInstance task) {
		return this.getRevisionOfDeployedProcessDefinitionVersion(task
				.getProcessInstance().getProcessDefinition().getName(), task
				.getProcessInstance().getProcessDefinition().getVersion());
	}

	public Integer getRevisionOfDeployedProcessDefinitionVersion(
			String processName, Integer processVersion) {
		ProcesoDesplegado procesoDesplegado = (ProcesoDesplegado) (entityManager
				.createQuery(
						"from ProcesoDesplegado proc where proc.nombreProceso=:procname "
								+ "and proc.version=:defVersion").setParameter(
						"procname", processName).setParameter("defVersion",
						processVersion).getSingleResult());
		return procesoDesplegado.getRevision();
	}

	public void registerProcessDefinitionRevisionForVersion(String modulo,
			String nombreProceso, Integer version, Integer numeroRevision,
			Boolean tieneReglas) {
		Modulo mod = (Modulo) entityManager.createQuery(
				"from Modulo f where f.nombre=:modName").setParameter(
				"modName", modulo).getSingleResult();
		ProcesoDesplegado desplegado = new ProcesoDesplegado();
		desplegado.setFuncionalidad(mod);
		desplegado.setNombreProceso(nombreProceso);
		desplegado.setRevision(numeroRevision);
		desplegado.setVersion(version);
		desplegado.setTieneReglas(tieneReglas);
		entityManager.persist(desplegado);
		entityManager.flush();
	}
}
