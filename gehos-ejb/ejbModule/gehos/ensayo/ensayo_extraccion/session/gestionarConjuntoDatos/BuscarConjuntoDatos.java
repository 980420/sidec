package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos.JobService;
import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.Jobs;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.DatatypeConverter;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.pentaho.di.core.annotations.Inject;


@Name("buscarConjuntoDatos")
@Scope(ScopeType.CONVERSATION)
public class BuscarConjuntoDatos
{
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;
	private ConjuntoDatos_ensayo conjuntoDatos = new ConjuntoDatos_ensayo();
	private long cid;

	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	@Create
	public void start() {}

	public BuscarConjuntoDatos() {}

	public void seleccionarConjuntoDatos(ConjuntoDatos_ensayo conjuntoDatos)
	{
		this.conjuntoDatos = conjuntoDatos;
	}
	
	public void eliminar() {
		cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraEliminar_ens")).longValue();
		conjuntoDatos.setCid(cid);  
		conjuntoDatos.setEliminado(Boolean.valueOf(true));
		entityManager.persist(conjuntoDatos);
		entityManager.flush();
	}
	
	private JobService jobService = new JobService();
	private Long idConjuntoDatos;
	private String estadoDelJob;
	private String ruta;
	private Jobs jobslocal;
	
   

	public void guardarJob() {
    	// Verificar que idConjuntoDatos no sea null
        if (idConjuntoDatos == null) {
            facesMessages.add("El ID del conjunto de datos no puede ser nulo.");
            return;
        }
        final FacesContext aFacesContext = FacesContext.getCurrentInstance();
		final ServletContext context = (aFacesContext != null) ? ((ServletContext)aFacesContext.getExternalContext().getContext()) : null;
		
		//Obtengo el Path de la aplicacion en la 1ra linea 
		//Defino donde voy a guardar los jobs
		//Por ultimo las combino
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		
		String scheme = request.getScheme(); // "http" or "https"
		String serverName = request.getServerName(); // e.g., "localhost"
		int serverPort = request.getServerPort(); // e.g., 8080
		String contextPath1 = request.getContextPath(); // e.g., "/gehos"

		// Build the base URL
		String baseUrl = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort) + contextPath1 + "/";
		String specificPath = "resources/jobs/ConjuntoDatos/";
		String context_job = baseUrl + specificPath;
        // Obtener el ID del conjunto de datos
        Long conjunto_datos_id = idConjuntoDatos;
        
        String tipoJob = "ConjuntoDato";
        String estado = "Pendiente";
        String ruta = " ";
        String context2 = context.getRealPath("/resources/jobs/ConjuntoDatos");
        
        Jobs job = new Jobs();
        job.setIdConjuntoDatos(conjunto_datos_id);
        job.setTipo_job(tipoJob);
        job.setEstado(estado);
        job.setRuta(ruta);
        job.setContextJob(context_job);
        job.setContextForJob(context2);
        job.setEliminado(false);
        entityManager.persist(job);
        
    }
    public String obtenerEstadoDelJob(Long conjuntoDatosId) {
    	 // Almacena el estado en el atributo
    	try {
    		Jobs job = (Jobs) entityManager.createQuery("SELECT j FROM Jobs j WHERE j.conjunto_datos_id = :conjuntoDatosId and j.eliminado != true")
                    .setParameter("conjuntoDatosId", conjuntoDatosId)
                    .getSingleResult();
        	estadoDelJob = job.getEstado();
        } catch (NoResultException e) {
            estadoDelJob = null; // No hay job asociado
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de otras excepciones
            estadoDelJob = null; // Opcional: establecer a null si hay un error
        }
        return estadoDelJob;
    }
    public void cambiarEstadoDelJob(Long conjuntoDatosId) {
   	 // Almacena el estado en el atributo
   	try {
   		Jobs job = (Jobs) entityManager.createQuery("SELECT j FROM Jobs j WHERE j.conjunto_datos_id = :conjuntoDatosId and j.eliminado != true")
                   .setParameter("conjuntoDatosId", conjuntoDatosId)
                   .getSingleResult();
       	job.setEstado("Descargado");
       	job.setEliminado(true);
       	entityManager.flush();
       } catch (NoResultException e) {
           estadoDelJob = null; // No hay job asociado
       } catch (Exception e) {
           e.printStackTrace(); // Manejo de otras excepciones
           estadoDelJob = null; // Opcional: establecer a null si hay un error
       }
   }
    
    public String obtenerRutaDelJob(Long conjuntoDatosId) {
   	 // Almacena la ruta en el atributo
   	try {
       	ruta = jobslocal.getRuta();
       } catch (NoResultException e) {
           ruta = null; // No hay job asociado
       } catch (Exception e) {
           e.printStackTrace(); // Manejo de otras excepciones
           ruta = null; // Opcional: establecer a null si hay un error
       }
       return ruta;
   } 
    public String obtenerNombreArchivoDesdeRuta(String ruta) {
        if (ruta != null && !ruta.isEmpty()) {
            // Normalizar la ruta: reemplazar barras invertidas por diagonales
            String rutaNormalizada = ruta.replace("\\", "/");

            // Extraer el nombre del archivo desde la ruta normalizada
            return rutaNormalizada.substring(rutaNormalizada.lastIndexOf("/") + 1);
        }
        return null; // Si la ruta es nula o vacía, devolver null
    }
    public String obtenerRutaCompletaArchivo(Long conjuntoDatosId) {
        Jobs job = obtenerJobPorConjuntoDatosId(conjuntoDatosId);
        if (job != null) {
            return job.getContextForJob() + job.getRuta().substring(job.getRuta().lastIndexOf("/") + 1);
        }
        return null;
    }	
	String rutaArchivo;
	
	public Jobs prepararDescarga(Long conjuntoDatosId) {
	    // Obtener el trabajo desde la base de datos
	    Jobs job = obtenerJobPorConjuntoDatosId(conjuntoDatosId);
	    jobslocal = job;
	    if (job == null) {
	        facesMessages.add("No se encontró el trabajo asociado al conjunto de datos.");
	    }

	    // Obtener la ruta física del archivo desde el trabajo
	    String rutaFisicaArchivo = job.getContextForJob() + "/" + job.getRuta().substring(job.getRuta().lastIndexOf("/") + 1);

	    // Almacenar la ruta en un atributo para usarla en el JavaScript
	    this.rutaArchivo = rutaFisicaArchivo;
	    cambiarEstadoDelJob(conjuntoDatosId);
	    
	    return jobslocal;
	}
	
	// Método auxiliar para obtener un trabajo por su conjunto de datos ID
	private Jobs obtenerJobPorConjuntoDatosId(Long conjuntoDatosId) {
	    try {
	        return (Jobs) entityManager.createQuery("SELECT j FROM Jobs j WHERE j.conjunto_datos_id = :conjuntoDatosId and j.eliminado != true")
	                .setParameter("conjuntoDatosId", conjuntoDatosId)
	                .getSingleResult();
	    } catch (NoResultException e) {
	        return null; // No se encontró el trabajo
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null; // Manejo de otras excepciones
	    }
	}
	
	public String exportAccion(Long conjuntoDatosId) {
		String rutaArchivo = obtenerRutaDelJob(conjuntoDatosId);
		String pepe = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		if (!rutaArchivo.equals("")) {
			return "window.open('"
					+ FacesContext.getCurrentInstance().getExternalContext()
							.getRequestContextPath() + rutaArchivo
					+ "'); Richfaces.hideModalPanel('exportPanel')";
		}
		return "return false;";
	}
	
	public ConjuntoDatos_ensayo getConjuntoDatos() {
		return conjuntoDatos;
	}

	public void setConjuntoDatos(ConjuntoDatos_ensayo conjuntoDatos) {
		this.conjuntoDatos = conjuntoDatos;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}
	public Long getIdConjuntoDatos() {
        return idConjuntoDatos;
    }

    public void setIdConjuntoDatos(Long idConjuntoDatos) {
        this.idConjuntoDatos = idConjuntoDatos;
    }
    public String getEstadoDelJob() {
        return estadoDelJob;
    }
    
    public String getRuta() {
        return ruta;
    }
    
    public String getRutaArchivo(){
    	return rutaArchivo;
    }
    public void setRutaArchivo(String rutaArchivo){
    	this.rutaArchivo = rutaArchivo;
    }
    public Jobs getJobslocal() {
		return jobslocal;
	}

	public void setJobslocal(Jobs jobslocal) {
		this.jobslocal = jobslocal;
	}
}
