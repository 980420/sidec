package gehos.configuracion.management.gestionarNaciones;

import gehos.bitacora.session.traces.IBitacora;

import gehos.configuracion.management.entity.Nacion_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("nacionCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class NacionCrearControlador {
	
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In(create = true) FacesMessages facesMessages;
	@In IBitacora bitacora; 	

	private String codigo;
	private String valor;
	private String nacionalidad;
	private Nacion_configuracion nacion;
	
	// other functions
	private int error;
	private Long cid = -1l;
	
	// Methods ------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		error = 0;
		try {
			this.codigo = "";
			this.valor = "";
			this.nacionalidad = "";
			this.nacion = new Nacion_configuracion();
			
			if(cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
			}	
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}	

	@End
	public void end() {	
		
	}
	
	@Transactional
	public void crear() {
		error = 0;
		try {
			this.nacion.setCodigo(codigo.trim());
			this.nacion.setValor(valor.trim());
			this.nacion.setNacionalidad(nacionalidad.trim());
			this.nacion.setEliminado(false);
			this.nacion.setCid(cid);
			
			entityManager.persist(nacion);
			entityManager.flush();		
			
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR, "errorInesperado");
		}
	}

	// Properties -------------------------------------------------------------
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}	

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public Nacion_configuracion getNacion() {
		return nacion;
	}

	public void setNacion(Nacion_configuracion nacion) {
		this.nacion = nacion;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}