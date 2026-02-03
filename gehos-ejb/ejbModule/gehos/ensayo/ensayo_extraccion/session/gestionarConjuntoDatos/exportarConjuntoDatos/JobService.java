package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import gehos.ensayo.entity.Jobs;
import gehos.ensayo.entity.ConjuntoDatos_ensayo;

@Stateless
public class JobService {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveJob(Jobs job) {
        entityManager.persist(job);
    }

    public void saveJob(Long conjunto_datos_id, String tipoJob, String estado) {
        Jobs job = new Jobs();
        job.setIdConjuntoDatos(conjunto_datos_id);
        job.setTipo_job(tipoJob);
        job.setEstado(estado);
        entityManager.persist(job);
    }
}
