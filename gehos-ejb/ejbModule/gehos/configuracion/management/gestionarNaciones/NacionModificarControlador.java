package gehos.configuracion.management.gestionarNaciones;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Nacion_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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

@Name("nacionModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class NacionModificarControlador {
	
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;	
	@In(create = true) FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	private String codigo;
	private String valor;
	private String nacionalidad;
	private Nacion_configuracion nacion;

	// other functions 
	private Long nacionId;
	private int error; 	
	private Long cid = -1l;
	
	// Methods --------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setNacionId(Long nacionId) {
		error = 0;			
		try {
			this.nacionId = nacionId;
			cid = -1l;
			
			nacion = (Nacion_configuracion)
					 entityManager.createQuery("select n from Nacion_configuracion n " +
					 						   "where n.id = :id")
								  .setParameter("id", this.nacionId)
								  .getSingleResult();
			
			codigo = nacion.getCodigo();
			valor = nacion.getValor();
			nacionalidad = nacion.getNacionalidad();
			
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
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
	public void modificar() {
		error = 0;
		try {
			this.nacion.setCodigo(codigo.trim());
			this.nacion.setValor(valor.trim());
			this.nacion.setNacionalidad(nacionalidad.trim());
			this.nacion.setCid(cid);
			entityManager.persist(nacion);
			entityManager.flush();
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "errorInesperado");
		}		
	}

	// Properties ---------------------------------------------------------
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

	public Long getNacionId() {
		return nacionId;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}		
}