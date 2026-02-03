package gehos.configuracion.management.gestionarRoles;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;

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
import org.jboss.seam.international.StatusMessage.Severity;

@Name("rolCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class RolCrearControlador {	
	
	@In IBitacora bitacora;
	@In FacesMessages facesMessages;	
	@In EntityManager entityManager;
	
	private Long cid = -1l;
	private String name;
	private String codigo;
	private Role_configuracion rol;
	
	// other functionalities
	private int error;
		
	// Methods----------------------------------------------------	
	@Begin(flushMode=FlushModeType.MANUAL, join = true)
	public void begin(){	
		error = 0;
		try {
			rol = new Role_configuracion();
			this.name = "";	
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
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
				
			rol.setEliminado(false);
			rol.setName(this.name.trim());
			rol.setCodigo(this.codigo.trim());
			rol.setCid(cid);	
			entityManager.persist(rol);
			entityManager.flush();	
			facesMessages.addToControlFromResourceBundle("message",Severity.INFO,"addRol");
			this.end();
		}			
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",Severity.ERROR,"errorInesperado");	
			e.printStackTrace();
		}
	}
	
	// Properties--------------------------------------------------	
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
	

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
