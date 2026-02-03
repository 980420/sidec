package gehos.configuracion.management.gestionarEntidadOrganizacional;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.EntidadOrganizacional_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;


@Name("entidadOrganizacionalCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadOrganizacionalCrearControlador {	
	
	@In IBitacora bitacora;
	@In FacesMessages facesMessages;	
	@In EntityManager entityManager;
	
	private String valor;
	private EntidadOrganizacional_configuracion entidadOrganizacional;
	
	@In(required = false, value = "entidadOrganizacionalBuscarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadOrganizacionalBuscarControlador", scope = ScopeType.CONVERSATION)
	EntidadOrganizacionalBuscarControlador entidadOrganizacionalBuscarControlador;
	
	// other functionalities
	private int error;
	private Long cid = -1l;
		
	// Methods----------------------------------------------------	
	@Begin(flushMode=FlushModeType.MANUAL, nested = true)
	public void begin(){
		error = 0;
		try {
			valor = "";
			entidadOrganizacional = new EntidadOrganizacional_configuracion();
			entidadOrganizacional.setEliminado(false);
			
			if (cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion("Creando entidad organizacional");
				entidadOrganizacional.setCid(cid);
			}
		} catch (Exception e) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean validar() {
		error = 1;
		List<String> lr = new ArrayList<String>();
					
		lr = entityManager.createQuery("select e from EntidadOrganizacional_configuracion e " +
									   "where e.valor =:valor")
		  				  .setParameter("valor", valor.trim())										  
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
		entidadOrganizacionalBuscarControlador.clean();
	}
	
	@Transactional
	public void crear() {	
		error = 0;
		try {	
			if(validar()) return;
			
			error = 0;
			entidadOrganizacional.setValor(valor.trim());
			entityManager.persist(entidadOrganizacional);
			entityManager.flush();	
			end();
		}			
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR, "errorInesperado");
		}
	}
	
	// Properties--------------------------------------------------	
	public EntidadOrganizacional_configuracion getEntidadOrganizacional() {
		return entidadOrganizacional;
	}

	public void setRol(EntidadOrganizacional_configuracion entidadOrganizacional) {
		this.entidadOrganizacional = entidadOrganizacional;
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
}
