package gehos.configuracion.management.gestionarDepartamentoClinico;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Departamento_configuracion;

import javax.persistence.EntityManager;

import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;
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

import java.util.List;

@Name("departamentoClinicoCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class DepartamentoClinicoCrearControlador {
	
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	private String nombre;
	private Departamento_configuracion departamento;	

	// other functions
	private Long cid = -1l;
	private int error;
	
	// Methods ------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void begin() {
		try {			
			this.nombre = "";
			this.departamento = new Departamento_configuracion();
			this.departamento.setEliminado(false);
			this.departamento.setEsClinico(true);
			
			if(cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
			}
		} catch (Exception e) {
			
		}
	}
	
	@End
	public void end() {		
	}
	
	@SuppressWarnings("unchecked")
	public boolean validacion() {
		error = 0;
		List<Departamento_configuracion> d = entityManager.createQuery("select d from Departamento_configuracion d " +
																	   "where d.nombre =:nombre")
														  .setParameter("nombre", this.nombre.trim())
														  .getResultList();
		if (d.size() != 0) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "entidadExistente");
			error = 1;
			return true;
		}
		return false;
	}
	
	@Transactional
	public void crear() {
		error = 0;
		try {
			if (validacion()) return; 
			
			this.departamento.setNombre(nombre.trim());
			this.departamento.setCid(cid);
			entityManager.persist(departamento);
			entityManager.flush();
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addFromResourceBundle("error", Severity.ERROR, "errorInesperado");
		}
	}

	// Properties -------------------------------------------------------------
	@Length(min=1,max=25,message="El máximo de caracteres es: 25")		
	@Pattern(regex="^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$", message="Caracteres incorrectos")
	public String getNombre() {
		return nombre;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Departamento_configuracion getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento_configuracion departamento) {
		this.departamento = departamento;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}