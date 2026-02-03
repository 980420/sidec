package gehos.configuracion.management.gestionarEntidadOrganizacional;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.EntidadOrganizacional_configuracion;

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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("entidadOrganizacionalModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadOrganizacionalModificarControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private String valor;
	private EntidadOrganizacional_configuracion entidadOrganizacional;
	
	//other functions
	private int error;
	private boolean errorLoadData;
	private Long cid = -1l;
	
	// Methods------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void cambId(Integer id) {	
		errorLoadData = false;
		this.id.equals(id);
		
		try {
			this.entidadOrganizacional = (EntidadOrganizacional_configuracion) 
										  entityManager.createQuery("select e from EntidadOrganizacional_configuracion e " +
										  							"where e.id =:id")
										  			   .setParameter("id", id)
										  			   .getSingleResult();
			
			this.valor = entidadOrganizacional.getValor();
			
			cid = bitacora.registrarInicioDeAccion("Modificando entidadOrganizacional");
			entidadOrganizacional.setCid(cid);
		}
		catch(NoResultException e) {			
			errorLoadData = true;
		}								
	}	
	
	@SuppressWarnings("unchecked")
	public boolean validar() {
		error = 0;
		List<String> lr = new ArrayList<String>();
					
		lr = entityManager.createQuery("select e from EntidadOrganizacional_configuracion e where " +
									   "e.valor =:valor and e.id <>:id")
						  .setParameter("valor", valor.toString().trim())		
						  .setParameter("id", entidadOrganizacional.getId())	
						  .getResultList();
		
		if(lr.size() != 0){
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR, "entidadExistente");
			return true;
		}
		return false;
	}
	
	@End
	public void end() {		
	}
	
	@Transactional
	public void modificar() {
		error = 0;
		try {			
			if(validar()) return;			
			
			this.entidadOrganizacional.setValor(valor.trim());
			entityManager.persist(entidadOrganizacional);
			entityManager.flush();		
			end();
		}
		catch(Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "errorInesperado");
		}
	}

	// Properties-----------------------------------------------------
	public Long getId() {
		return id;
	}
	
	public EntidadOrganizacional_configuracion getEntidadOrganizacional() {
		return entidadOrganizacional;
	}

	public void setEntidadOrganizacional(EntidadOrganizacional_configuracion entidadOrganizacional) {
		this.entidadOrganizacional = entidadOrganizacional;
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

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
