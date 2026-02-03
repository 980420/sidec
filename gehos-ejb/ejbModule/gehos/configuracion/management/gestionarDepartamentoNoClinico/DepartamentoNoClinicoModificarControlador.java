package gehos.configuracion.management.gestionarDepartamentoNoClinico;

import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Departamento_configuracion;

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

@Name("departamentoNoClinicoModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class DepartamentoNoClinicoModificarControlador {

	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
		
	private String nombre;
	private Departamento_configuracion departamento;

	// other functions
	private Long departamentoId;
	private int error;
	private boolean errorLoadData;
	private Long cid = -1l;
	
	// Methods ------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setDepartamentoId(Long departamentoId) {
		errorLoadData = false;
		
		try {
			this.departamentoId = departamentoId;
			this.departamento = (Departamento_configuracion) 
			 					entityManager.createQuery("select departamento from Departamento_configuracion departamento " +
			 											  "where departamento.esClinico = false " +
			 						   					  "and departamento.id = :id")
			 						   		 .setParameter("id", this.departamentoId)
			 						   		 .getSingleResult();
			
			this.nombre = this.departamento.getNombre();
			
			if(cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));
				departamento.setCid(cid);
			}
		} catch (NoResultException e) {
			errorLoadData = true;
		} catch (Exception e) {
			errorLoadData = true;
		}
	}
	
	@End 
	public void end() {
		
	}

	@SuppressWarnings("unchecked")
	public boolean validacion() {
		error = 0;
		List<Departamento_configuracion> d = entityManager.createQuery(
				"select d from Departamento_configuracion d "
						+ "where d.nombre =:nombre and d.id <>:departamentoId")
				.setParameter("nombre", this.nombre.trim()).setParameter(
						"departamentoId", this.departamento.getId())
				.getResultList();

		if (d.size() != 0) {
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR, "entidadExistente");
			error = 1;
			return true;
		}
		return false;
	}

	@Transactional
	public void modificar() {
		error = 0;
		try {
			if (validacion()) return; 

			this.departamento.setNombre(nombre.trim());
			
			entityManager.persist(departamento);
			entityManager.flush();
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addFromResourceBundle("error", Severity.ERROR, "errorInesperado");
		}		
	}

	// Properties ---------------------------------------------------------
	public String getNombre() {
		return nombre;
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

	public Long getDepartamentoId() {
		return departamentoId;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}
}