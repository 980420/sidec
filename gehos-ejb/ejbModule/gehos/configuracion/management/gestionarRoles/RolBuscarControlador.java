package gehos.configuracion.management.gestionarRoles;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("rolBuscarControlador")
public class RolBuscarControlador extends EntityQuery<Role_configuracion> {

	private static final String EJBQL = "select rol from Role_configuracion rol where rol.eliminado <> true";
	private static final String[] RESTRICTIONS = {
		"lower(rol.name) like concat(lower(#{rolBuscarControlador.nombre.trim()}),'%')",
		"lower(rol.codigo) like concat(lower(#{rolBuscarControlador.codigo.trim()}),'%')"
		};
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search 
	private String nombre;
	private String codigo;
	private int pagina;
	// other fuctionalities	
	private Long idRolEliminar;
	private boolean state = true;
	private Long cid = -1l;

	private Role_configuracion rol = new Role_configuracion();	
	private int error = 0;	

	public RolBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("rol.id desc");
	}

	//Methods-----------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.refresh();
		
		/**
		 * @author yurien
		 * **/
		//Se agrego este try{}catch() porque cuando uno existen roles en la bd
		//se lanza un NullPointerException en el metodo getFirstResult()
		try {
			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// search 
	public void buscar() {
		rol = new Role_configuracion();
		rol.setName(nombre);
		rol.setCodigo(codigo);		
		setFirstResult(0);
	}
		
	public int getPagina() {
		if(this.getNextFirstResult() != 0)
			return this.getNextFirstResult()/10;
			else
				return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
		
		long num=(getResultCount()/10)+1;
		if(this.pagina>0){
		if(getResultCount()%10!=0){
			if(pagina<=num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		else{
			if(pagina<num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		}
	}
	
	// select item to removal
	@SuppressWarnings("unchecked")
	public String seleccionar() throws IllegalStateException, SecurityException, SystemException {
		try {
			List<Role_configuracion> rolConcu = entityManager.createQuery("select rol from Role_configuracion rol where rol.id =:id and rol.eliminado = true").setParameter("id", idRolEliminar).getResultList();
			if (rolConcu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
				Transaction.instance().rollback();				
			} 
			else {
				return "good";
			}
		} catch (Exception e) {
			e.printStackTrace();	
			Transaction.instance().rollback();
		}
		return "null";	
		
	}
	
	// selecciona rol para modificar
	@SuppressWarnings("unchecked")
	public void seleccionarModificar() {
		try {
			error  = 0;
			List<Role_configuracion> rolConcu = entityManager.createQuery("select rol from Role_configuracion rol where rol.id =:id and rol.eliminado = true").setParameter("id", idRolEliminar).getResultList();
			if (rolConcu.size() != 0) {					
				throw new Exception("eliminado");
			}
		} catch (Exception e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error = 1;
		}
				
	}
	
	@SuppressWarnings("unchecked")
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error  = 0;
			List<Role_configuracion> rolConcu = entityManager.createQuery("select rol from Role_configuracion rol where rol.id =:id and rol.eliminado = true").setParameter("id", idRolEliminar).getResultList();
			if (rolConcu.size() != 0) {					
				throw new Exception("eliminado");
			}
		} catch (Exception e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error = 1;
		}			
	}
	
	// remove item
	@SuppressWarnings("unchecked")
	public void eliminar() {
		try {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			Role_configuracion rol = (Role_configuracion) 
			 						 entityManager.createQuery("select r from Role_configuracion r where r.id =:id")
			 						 			  .setParameter("id", idRolEliminar)
			 						 			  .getSingleResult();	
			
			List<Usuario_configuracion> usuarios = entityManager.createQuery("select u from Usuario_configuracion u " +
																			 "join u.roles r where r.id = :idRol")
																.setParameter("idRol", idRolEliminar)
																.getResultList();
			
			// Validando que el rol está asociado a un usuario
			if(usuarios.size() != 0) {
				facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "enuso");
				return;
			}	
			
			rol.setEliminado(true);
			rol.setCid(cid);
			//entityManager.remove(rol); esta line fue sustituida por la de arriba
			entityManager.flush();

		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}

	//change visibility
	public void cambiarVisibilidad(Long id) {
		try{
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
			
			Role_configuracion aux = (Role_configuracion) 
									 entityManager.createQuery("select r from Role_configuracion r where r.id =:id")
									 			  .setParameter("id", id)
									 			  .getSingleResult();
			aux.setCid(cid);			
			if(aux.getEliminado() == null || aux.getEliminado() == false)
				aux.setEliminado(true);
			else
				aux.setEliminado(false);	
			
			entityManager.persist(aux);
			entityManager.flush();
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	// Properties-------------------------------------------------------
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	
	public Role_configuracion getRol() {
		return rol;
	}

	public void setRol(Role_configuracion rol) {
		this.rol = rol;
	}

	public Long getIdRolEliminar() {
		return idRolEliminar;
	}

	public void setIdRolEliminar(Long idRolEliminar) {
		this.idRolEliminar = idRolEliminar;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}
