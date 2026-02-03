package gehos.configuracion.management.gestionarRoles;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Role_configuracion;

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

@Name("rolModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class RolModificarControlador {

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private String name;
	private String codigo;
	private Role_configuracion rol;
	
	//other functions
	private int error;
	
	private Long cid = -1l;
	
	// Methods------------------------------------------------------
	@Begin(flushMode=FlushModeType.MANUAL, join=true)
	public void setId(Long id) {	
		error = 0;
	
		try {	
			rol = new Role_configuracion();	
			this.id = id;		
			this.rol = (Role_configuracion) 
					   entityManager.createQuery("select r from Role_configuracion r " +
												 "where r.id =:id " +
												 "and r.eliminado <> true")
									.setParameter("id", id)
									.getSingleResult();			
			this.name = rol.getName();
			this.codigo= rol.getCodigo();
			
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
			rol.setName(this.name.trim());
			rol.setCodigo(this.codigo.trim());
			
			rol.setCid(
					bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar")));
			entityManager.persist(rol);
			entityManager.flush();	
			
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}		
	}

	// Properties-----------------------------------------------------
	public Long getId() {
		return id;
	}
	
	public Role_configuracion getRol() {
		return rol;
	}

	public void setRol(Role_configuracion rol) {
		this.rol = rol;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
