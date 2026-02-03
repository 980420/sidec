package gehos.configuracion.management.gestionarTipoEntidad;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("tipoEntidadBuscarControlador")
public class TipoEntidadBuscarControlador extends EntityQuery<TipoEntidad_configuracion> {

	private static final String EJBQL = "select entidad from TipoEntidad_configuracion entidad";
			
	private static final String[] RESTRICTIONS = { "lower(entidad.valor) like concat(lower(#{tipoEntidadBuscarControlador.tipoEntidad.valor.trim()}),'%')"};
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	//search criteria
	private String valor = "";	
	
	//otras funcionalidades
	private Long idEliminar = -1l;
	private boolean state = true;
	private Long cid = -1l;

	private TipoEntidad_configuracion tipoEntidad = new TipoEntidad_configuracion();
	
	public TipoEntidadBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("entidad.id desc");
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin(){
		this.refresh();
		
		if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());
	}
	
	public void changeStateSimpleTugglePanel() {
		state = !state;
	}
	
	public void buscar(){	
		tipoEntidad = new TipoEntidad_configuracion();
		tipoEntidad.setValor(valor);
		
		setFirstResult(0);
	}
	
	public void seleccionar(Long idEliminar){
		this.idEliminar = idEliminar;
	}
	
	//remove entity type
	@Transactional
	public void eliminar(){
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			TipoEntidad_configuracion t = (TipoEntidad_configuracion) entityManager.createQuery("select t from TipoEntidad_configuracion t " +
						  				  								  "where t.id = :id")
						  				  			         .setParameter("id", idEliminar)
						  				  			         .getSingleResult();					
			entityManager.remove(t);			
			entityManager.flush();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
			e.printStackTrace();
		}		
	}
	
	//change visibility
	@Transactional
	public void cambiarVisibilidad(Long id){
		try {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
			
			TipoEntidad_configuracion t = (TipoEntidad_configuracion) 
						  				  entityManager.createQuery("select t from TipoEntidad_configuracion t " +
		    										                "where t.id = :id")
		    						                   .setParameter("id", id)
		    						                   .getSingleResult();
			t.setEliminado(!t.getEliminado());
			t.setCid(cid);
			entityManager.persist(t);
			entityManager.flush();			
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}

	// Properties -------------------------------------------------	
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public TipoEntidad_configuracion getTipoEntidad() {
		return tipoEntidad;
	}

	public void setTipoEntidad(TipoEntidad_configuracion tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}
}