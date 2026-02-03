package gehos.configuracion.management.gestionarTipoUbicacion;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("tipoUbicacionBuscarControlador")
public class TipoUbicacionBuscarControlador extends EntityQuery<TipoUbicacion_configuracion> {

	private static final String EJBQL = "select tipoUbicacion from TipoUbicacion_configuracion tipoUbicacion";
	private static final String[] RESTRICTIONS = {
			"lower(tipoUbicacion.codigo) like concat(lower(#{tipoUbicacionBuscarControlador.codigo.trim()}),'%')",
			"lower(tipoUbicacion.descripcion) like concat(lower(#{tipoUbicacionBuscarControlador.descripcion.trim()}),'%')",
			"tipoUbicacion.id <> #{tipoUbicacionBuscarControlador.tipoUbicacion.id}" };

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;

	// search
	private String codigo = "";
	private String descripcion = "";

	// other functionalities	
	private Long idEliminar = -1l;
	private Long cid = -1l;
	private boolean state = true;//state of singletuglepanel
	private Parameters parametros = new Parameters();  //codec and decodec parameters
	
	private TipoUbicacion_configuracion tipoUbicacion = new TipoUbicacion_configuracion();
	private int error = 0;
	
	//Methods-------------------------------------------------------
	public TipoUbicacionBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("tipoUbicacion.id desc");
	}

	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void begin() {
	}

	// change the state of search simpleTugglePanel (open or closed)
	public void cambiarEstadoSimpleTugglePanel() {
		state = !state;
	}
	
	// search
	public void buscar() {
		setFirstResult(0);
	}
				
	// select items for removal
	public void seleccionarEliminar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			TipoUbicacion_configuracion tipoUbicaConcu = (TipoUbicacion_configuracion) 
				  entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}
	}
	
	// selecciona tipo ubicacion para modificar
	
	public void seleccionarModificar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			TipoUbicacion_configuracion tipoUbicaConcu = (TipoUbicacion_configuracion) 
			  entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
		          .setParameter("id", idEliminar)
		          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}
				
	}
	
	// selecciona tipo ubicacion para ver	
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			TipoUbicacion_configuracion tipoUbicaConcu = (TipoUbicacion_configuracion) 
			  entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
		          .setParameter("id", idEliminar)
		          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}	
	}


	// remove item
	public void eliminar() {
		try { 
			bitacora.registrarInicioDeAccion("Eliminando tipo de ubicacion");
			
			
			TipoUbicacion_configuracion t = (TipoUbicacion_configuracion) 
			  								entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
			  										     .setParameter("id", idEliminar)
			  										     .getSingleResult();

			entityManager.remove(t);
			entityManager.flush();

			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
			
		}catch (NoResultException e) {
				facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
				e.printStackTrace();			
		 }catch (Exception e) {	
			facesMessages.addToControlFromResourceBundle("form",Severity.ERROR,"msjEliminar");
		}
	}
	
	// change visibility
	public void cambiarTipoUbicacionVisibilidad(Long idTipoU) {
		try {
			cid = bitacora.registrarInicioDeAccion("Cambiando visibilidad de tipo de ubicacion");			
			TipoUbicacion_configuracion aux = (TipoUbicacion_configuracion) 
											  entityManager.createQuery("select t from TipoUbicacion_configuracion t where t.id =:id")
											  .setParameter("id", idTipoU)
											  .getSingleResult();			
			aux.setCid(cid);

			if (aux.getEliminado() == null || aux.getEliminado() == false)
				aux.setEliminado(true);
			else
				aux.setEliminado(false);
			entityManager.persist(aux);
			entityManager.flush();	
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} 		
		catch (Exception e) {		
			facesMessages.addToControlFromResourceBundle("form",Severity.ERROR,"errorInesperado");			
		}
	}

	// Properties----------------------------------------------------------------
	public TipoUbicacion_configuracion getTipoUbicacion() {
		return tipoUbicacion;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = parametros.decodec(codigo);
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = parametros.decodec(descripcion);
	}
	
	public Long getIdEliminar() {
		return idEliminar;
	}

	public void setIdEliminar(Long idEliminar) {
		this.idEliminar = idEliminar;
	}

	public void setTipoUbicacion(TipoUbicacion_configuracion tipoUbicacion) {
		this.tipoUbicacion = tipoUbicacion;
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
