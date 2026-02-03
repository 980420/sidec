package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos;
import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.Jobs;
import java.io.File;
import java.util.List;

import javax.ejb.MessageDriven;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.ResourceAdapter;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@MessageDriven(name = "programarConjuntoDatos")
@ResourceAdapter("quartz-ra.rar")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class ProgramarConjuntoDatos implements Job {
	
	@PersistenceContext(unitName = "gehos")
	EntityManager entityManager;
	// Inyección de la clase de exportación
	
	public ProgramarConjuntoDatos(){
	}
	
	@Transactional
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ExportarConjuntoDatosJobs exportarConjuntoDatosJobs = new ExportarConjuntoDatosJobs();
		exportarConjuntoDatosJobs.inicializarInyecciones(this.entityManager);
		  
		
		// Paso 1: Obtener los trabajos pendientes
				@SuppressWarnings("unchecked")
				List<Jobs> trabajosPendientes = entityManager.createQuery("SELECT j FROM Jobs j WHERE j.estado = 'Pendiente'").getResultList(); 

			    // Paso 2: Procesar cada trabajo pendiente
			    for (Jobs trabajo : trabajosPendientes) {
			        Long idConjuntoDatos = trabajo.getConjuntoDatos();
			        final String rootpath = trabajo.getContextForJob();
			        if(rootpath != null && (exportarConjuntoDatosJobs.getRootpath() == null || exportarConjuntoDatosJobs.getRootpath().trim().isEmpty())){
			        	exportarConjuntoDatosJobs.setRootpath(rootpath);
			        }
			        			        // Paso 3: Buscar el conjunto de datos correspondiente
			        ConjuntoDatos_ensayo conjuntoDatos = entityManager.find(ConjuntoDatos_ensayo.class, idConjuntoDatos);
			        
			        exportarConjuntoDatosJobs.setRootpath(rootpath + "/jobs/ConjuntoDatos/");	
			        if (conjuntoDatos != null) {
    	
			        	// Paso 3: Establecer el ID
			        	 if(!exportarConjuntoDatosJobs.puedeProcesar())
			        		 throw new IllegalStateException("ExportarConjuntoDatos no ha sido inyectado.");
			        	 exportarConjuntoDatosJobs.setIdConjuntoDatos(idConjuntoDatos); 
			        	 // Paso 4: Inicializar datos
			        	 exportarConjuntoDatosJobs.inicializarConjuntoDatos(); 
			             // Paso 5: Llamar al método para exportar a Excel
			        	 System.out.println("Inicializando Exportar: " + rootpath);
			        	 String nombre = exportarConjuntoDatosJobs.exportarExcel(rootpath); 
			            // Actualizar el estado del trabajo
			        	 String rutaDesc = trabajo.getContextJob();
			        	 trabajo.setRuta(rutaDesc + nombre);
			        	 //Actualizar el context para quedarme con la direccion fisica de jobs
//			        	 String rutafisica = trabajo.getContextForJob();
//			        	 trabajo.setContextForJob(rutafisica + nombre);
			            trabajo.setEstado("Completado");

			            entityManager.merge(trabajo); // Persistir los cambios en el trabajo

			            // Aquí puedes agregar más lógica según sea necesario
			        } else {
			            // Manejar el caso donde no se encuentra el conjunto de datos
			            // Por ejemplo, podrías marcar el trabajo como fallido
			            trabajo.setEstado("Fallido");
			            entityManager.merge(trabajo);
			        }
			    }
		
		
	}
}
