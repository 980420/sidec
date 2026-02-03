package gehos.configuracion.management.gestionarTipoEstablecimiento;

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

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("tipoEstablecimientoBuscarControlador")
public class TipoEstablecimientoBuscarControlador extends EntityQuery<TipoEstablecimientoSalud_configuracion> {

	private static final String EJBQL = "select establecimiento from TipoEstablecimientoSalud_configuracion establecimiento";
	private static final String[] RESTRICTIONS = {"lower(establecimiento.valor) like concat(lower(#{tipoEstablecimientoBuscarControlador.tipoEstablecimiento.valor.trim()}),'%')" };
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search 
	private String valor = "";
	
	// other fuctionalities	
	private Long idEstablecimientoEliminar;
	private boolean state = true;	
	private Long cid = -1l;

	private TipoEstablecimientoSalud_configuracion tipoEstablecimiento = new TipoEstablecimientoSalud_configuracion();	

	public TipoEstablecimientoBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("establecimiento.id desc");
	}

	//Methods-----------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.refresh();
		
		if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());
	}
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// search 
	public void buscar() {
		tipoEstablecimiento = new TipoEstablecimientoSalud_configuracion();
		tipoEstablecimiento.setValor(valor);
		
		setFirstResult(0);
	}
		
	// select item to removal
	public void seleccionar(Long id) {
		this.idEstablecimientoEliminar = id;
	}
	
	// remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			TipoEstablecimientoSalud_configuracion t = (TipoEstablecimientoSalud_configuracion) 
													   entityManager.createQuery("select t from TipoEstablecimientoSalud_configuracion t " +
													   							 "where t.id = :id")
													   				.setParameter("id", this.idEstablecimientoEliminar)
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
	public void cambiarVisibilidad(Long id) {
		try{
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
			
			TipoEstablecimientoSalud_configuracion t = (TipoEstablecimientoSalud_configuracion) 
													   entityManager.createQuery("select t from TipoEstablecimientoSalud_configuracion t " +
													   							 "where t.id = :id")
													   				.setParameter("id", id)
													   				.getSingleResult();			
			
			if(t.getEliminado() == null || t.getEliminado() == false)
				t.setEliminado(true);
			else
				t.setEliminado(false);	
			
			t.setCid(cid);
			entityManager.persist(t);
			entityManager.flush();
		} catch(NoResultException e){
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} 
		catch(Exception e){
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	// Properties-------------------------------------------------------	
	public Long getIdEstablecimientoEliminar() {
		return idEstablecimientoEliminar;
	}

	public void setIdEstablecimientoEliminar(Long idEstablecimientoEliminar) {
		this.idEstablecimientoEliminar = idEstablecimientoEliminar;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public TipoEstablecimientoSalud_configuracion getTipoEstablecimiento() {
		return tipoEstablecimiento;
	}

	public void setTipoEstablecimiento(
			TipoEstablecimientoSalud_configuracion tipoEstablecimiento) {
		this.tipoEstablecimiento = tipoEstablecimiento;
	}
}
