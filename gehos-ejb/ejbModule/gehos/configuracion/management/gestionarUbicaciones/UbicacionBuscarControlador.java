package gehos.configuracion.management.gestionarUbicaciones;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Name("ubicacionBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class UbicacionBuscarControlador extends EntityQuery<Ubicacion_configuracion> {
	
	private static final String EJBQL = "select ubicacion from Ubicacion_configuracion ubicacion where (ubicacion.eliminado = false or ubicacion.eliminado = null)";
	
	private static final String[] RESTRICTIONSS = {"lower(ubicacion.identificador) like concat(lower(#{ubicacionBuscarControlador.identificador.trim()}),'%')",
												   "#{ubicacionBuscarControlador.ubicacion.id} <> ubicacion.id" };
	private static final String[] RESTRICTIONSA = {"lower(ubicacion.identificador) like concat(lower(#{ubicacionBuscarControlador.identificador.trim()}),'%')",
												   "lower(ubicacion.entidad.nombre) like concat(lower(#{ubicacionBuscarControlador.entidad.trim()}),'%')",
												   "lower(ubicacion.tipoUbicacion.descripcion) like concat(lower(#{ubicacionBuscarControlador.tipoUbicacion.trim()}),'%')",
												   "#{ubicacionBuscarControlador.ubicacion.id} <> ubicacion.id" };
	
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
		
	// search
	private String entidad = "";
	private String tipoUbicacion = "";
	private String identificador = "";	
	
	// others functios
	private Long idElimimar;
	private boolean state = true;
	private boolean searchMode = false;	
	private Parameters parameters = new Parameters();
	
	private Ubicacion_configuracion ubicacion = new Ubicacion_configuracion();

	private int error =0;

	// Contructor-------------------------------------------------------
	public UbicacionBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("ubicacion.id desc");
	}

	// Methods----------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}

	// return a list of RHIO entities
	@SuppressWarnings("unchecked")
	public List<String> entidades() {
		try {
			List<String>  l = entityManager.createQuery("select e.nombre from Entidad_configuracion e " +
														"where e.perteneceARhio = true and (e.eliminado = false or e.eliminado = null) order by e.nombre")
									       .getResultList();		
			l.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return l;			
		} catch (Exception e) {
			return new ArrayList<String>();
		}		
	}

	// return a list of ubication type
	@SuppressWarnings("unchecked")
	public List<String> tipoUbicaciones() {
		try {
			List<String> l = entityManager.createQuery("select t.descripcion from TipoUbicacion_configuracion t " +
													   "where (t.eliminado = false or t.eliminado = null) order by t.descripcion")
										  .getResultList();
			l.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return l;
		} catch(Exception e) {
			return new ArrayList<String>();
		}				
	}

	// change the search mode (simple/advanced)
	public void changeSearchMode(boolean searchMode) {
		this.searchMode = searchMode;
		
		String[] aux = (this.searchMode) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	// change the state of sinpleTugglePanel (open/closed)
	public void changeStateSimpleTugglePanel() {
		this.state = !state;
	}

	// search
	public void buscar() {
		setFirstResult(0);		
	}
	
	//metodo de seleccionar la cama para eliminar, validacion de concurrencia
	@SuppressWarnings("unchecked")
	public String seleccionarEliminar() throws IllegalStateException, SecurityException, SystemException {
		try {
			List<Ubicacion_configuracion> ubicacionConcu = entityManager.createQuery("select ubi from Ubicacion_configuracion ubi where ubi.id =:id and ubi.eliminado = true").setParameter("id", idElimimar).getResultList();
			if (ubicacionConcu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
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
	
	// selecciona cama para modificar
	@SuppressWarnings("unchecked")
	public void seleccionarModificar() {
		try {
			error  = 0;
			List<Ubicacion_configuracion> ubicacionConcu = entityManager.createQuery("select ubi from Ubicacion_configuracion ubi where ubi.id =:id and ubi.eliminado = true").setParameter("id", idElimimar).getResultList();
			if (ubicacionConcu.size() != 0) {					
				throw new Exception("eliminado");
			}
		} catch (Exception e) {			
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
			error = 1;
		}
				
	}
	
	@SuppressWarnings("unchecked")
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error = 0;
			List<Cama_configuracion> ubicacionConcu = entityManager.createQuery("select ubi from Ubicacion_configuracion ubi where ubi.id =:id and ubi.eliminado = true").setParameter("id", idElimimar).getResultList();
			if (ubicacionConcu.size() != 0) {
				throw new Exception("eliminado");			
			}		
		} catch (Exception e) {	
			error = 1;
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");				
		}				
	}
		
	// remove ubication
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion("Eliminando ubicacion");
			
			Ubicacion_configuracion aux = (Ubicacion_configuracion) 
										  entityManager.createQuery("select u from Ubicacion_configuracion u " +
										  							"where u.id =:id and (u.eliminado = false or u.eliminado = null)")
										  			   .setParameter("id", idElimimar)
										  			   .getSingleResult();
			this.idElimimar = -1l;
			aux.setEliminado(true);
			entityManager.persist(aux);
			entityManager.flush();
	
			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
		}catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");	
		}
		catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("userSearch",Severity.ERROR, "msjEliminar");
		}
	}
	
	// Important properties---------------------------------------
	public void setSearchMode(boolean searchMode) {
		this.searchMode = searchMode;		

		String[] aux = (this.searchMode) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}
	
	// select ubication for removal
	public void setIdElimimar(Long idElimimar) {
		this.idElimimar = idElimimar;
	}

	// Properties-------------------------------------------------
	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		if (entidad.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.entidad = "";
		else
			this.entidad = parameters.decodec(entidad);		
	}

	public String getTipoUbicacion() {
		return tipoUbicacion;
	}

	public void setTipoUbicacion(String tipoUbicacion) {
		if (tipoUbicacion.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.tipoUbicacion = "";
		else
			this.tipoUbicacion = parameters.decodec(tipoUbicacion);		
	}

	public Ubicacion_configuracion getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(Ubicacion_configuracion ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = parameters.decodec(identificador);
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Long getIdElimimar() {
		return idElimimar;
	}

	public boolean isSearchMode() {
		return searchMode;
	}

	

	public Parameters getParametros() {
		return parameters;
	}

	public void setParametros(Parameters parametros) {
		this.parameters = parametros;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

}
