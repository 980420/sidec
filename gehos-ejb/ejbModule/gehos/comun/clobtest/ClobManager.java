/*package gehos.comun.clobtest;

import java.rmi.RemoteException;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.ActividadInvestigacionDocencia_configuracion;
import gehos.configuracion.management.entity.Cama_configuracion;
import gehos.configuracion.management.entity.CategoriaCama_configuracion;
import gehos.configuracion.management.entity.EstadoCama_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoActividadInvestigacionDocencia_configuracion;
import gehos.configuracion.management.entity.TipoCama_configuracion;
import gehos.interfaces.ris.client.PatientDTO;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("clobManager")
public class ClobManager {

	@In
	EntityManager entityManager;

	@In
	IBitacora bitacora;

	@In
	gehos.interfaces.ris.beans.RISClient RISClient;

	public void ristest() throws RemoteException {
		PatientDTO dto = new PatientDTO();
		dto.setBirthDate("16/01/1983");
		dto.setCI("83011617146");
		dto.setClinicalHistoryNumber("15151515");
		dto.setFirstLastName("velazquez");
		dto.setId(1000055);
		dto.setName("alejandro");
		dto.setNationality("cuabana");
		dto.setSecondLastName("carralero");
		dto.setSex("M");
		String result = RISClient.insertarPaciente(dto);
		// RISClient.testReportService();
		System.out.println(result);
	}

	private ActividadInvestigacionDocencia_configuracion actividad = new ActividadInvestigacionDocencia_configuracion();

	public void testActividad() {
		TipoActividadInvestigacionDocencia_configuracion tipo = (TipoActividadInvestigacionDocencia_configuracion) entityManager
				.createQuery(
						"select a from TipoActividadInvestigacionDocencia_configuracion a where a.valor = :val")
				.setParameter("val", "Curso de postgrado").getSingleResult();
		this.actividad.setTipoActividadInvestigacionDocencia(tipo);
		tipo.getActividadInvestigacionDocencias().add(this.actividad);
		this.actividad.setCid(-1L);
		this.actividad.setValor("nombreee");
		this.actividad.setDescripcion("descripcionnn");
		this.actividad.setEliminado(false);
		entityManager.persist(tipo);
		entityManager.persist(actividad);
		entityManager.flush();
	}

	public void testTrazaAccion() {
		bitacora.registrarInicioDeAccion("test traza accion");
	}

	public void testInsert() {
		Cama_configuracion cama = new Cama_configuracion();
		CategoriaCama_configuracion categoriaCama = entityManager.find(
				CategoriaCama_configuracion.class, new Long(1));
		cama.setCategoriaCama(categoriaCama);
		cama.setDescripcion("cama test");
		cama.setX(10);
		cama.setY(100);
		EstadoCama_configuracion estadoCama = entityManager.find(
				EstadoCama_configuracion.class, new Long(1));
		cama.setEstadoCama(estadoCama);
		TipoCama_configuracion tipoCama = entityManager.find(
				TipoCama_configuracion.class, new Long(1));
		cama.setTipoCama(tipoCama);
		ServicioInEntidad_configuracion servicioInEntidadByIdServicio = entityManager
				.find(ServicioInEntidad_configuracion.class, new Long(1));
		cama.setServicioInEntidadByIdServicio(servicioInEntidadByIdServicio);
		entityManager.persist(cama);
		entityManager.flush();
		System.out.println("EL ID NUEVO ES " + cama.getId());

	}

	public void createBlob() {
		Test test = new Test();
		test.setId(3);
		String Longtext = "";
		for (int i = 0; i < 400; i++) {
			Longtext += "aaaaaaaaaa" + i;
		}
		test.setLongtext(Longtext.toString());
		entityManager.persist(test);
		entityManager.flush();
	}

	public void readClob() {
		Test test = entityManager.find(Test.class, 3);
		System.out.println(test.getLongtext());
	}
}
*/