package gehos.configuracion.management.gestionarCama;

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
import javax.persistence.IdClass;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Name("camaBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class CamaBuscarControlador extends EntityQuery<Cama_configuracion> {

	private static final String EJBQL = "select cama from Cama_configuracion cama where (cama.eliminado = false or cama.eliminado = null)";

	private static final String[] RESTRICTIONSS = {"lower(cama.descripcion) like concat(lower(#{camaBuscarControlador.descripcion.trim()}),'%')",
												   "#{camaBuscarControlador.cama.id} <> cama.id" };
	private static final String[] RESTRICTIONSA = {"lower(cama.descripcion) like concat(lower(#{camaBuscarControlador.descripcion.trim()}),'%')",
												   "lower(cama.estadoCama.valor) like concat(lower(#{camaBuscarControlador.estadoCama.trim()}),'%')",
												   "lower(cama.categoriaCama.valor) like concat(lower(#{camaBuscarControlador.categoriaCama.trim()}),'%')",
												   "lower(cama.tipoCama.valor) like concat(lower(#{camaBuscarControlador.tipoCama.trim()}),'%')",
												   "lower(cama.servicioInEntidadByIdServicio.servicio.nombre) like concat(lower(#{camaBuscarControlador.servicio.trim()}),'%')",
												   "#{camaBuscarControlador.cama.id} <> cama.id" };
	
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;	
	@In IBitacora bitacora;
	
	// search criteria
	private String descripcion = "";
	private String estadoCama = "";
	private String categoriaCama = ""; 
	private String tipoCama = "";
	private String servicio = "";
	private Cama_configuracion cama = new Cama_configuracion();
	
	// other functions	
	private Long idEliminar;
	private int error = 0;
	private boolean state = true;
	private boolean searchMode = false;	
	private Parameters parametros = new Parameters();

	// Methods----------------------------------------------------
	public CamaBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("cama.id desc");
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}
	
	// search
	public void buscar() {
		setFirstResult(0);
	}

	// return a list of bed category
	@SuppressWarnings("unchecked")
	public List<String> categorias() {
		try {
			List<String> l = entityManager.createQuery("select cat.valor from CategoriaCama_configuracion cat " +
													   "where (cat.eliminado = false or cat.eliminado = null) " +
													   "order by cat.valor")
										  .getResultList();		
			l.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return l;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of bed state
	@SuppressWarnings("unchecked")
	public List<String> estados() {
		try {
			List<String> l = entityManager.createQuery("select est.valor from EstadoCama_configuracion est " +
														"where (est.eliminado = false or est.eliminado = null) " +
														"order by est.valor")
										   .getResultList();
			l.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return l;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of bed kind
	@SuppressWarnings("unchecked")
	public List<String> tipos() {
		try {
			List<String> l = entityManager.createQuery("select tipo.valor from TipoCama_configuracion tipo " +
													   "where (tipo.eliminado = false or tipo.eliminado = null) " +
													   "order by tipo.valor")
										  .getResultList();
			l.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return l;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of services
	@SuppressWarnings("unchecked")
	public List<String> servicios() {
		try {
			List<String> l = entityManager.createQuery("select serv.nombre from Servicio_configuracion serv " +
							   						   "where (serv.eliminado = false or serv.eliminado = null) " +
							   						   "order by serv.nombre")
							   			  .getResultList();
			l.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return l;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}
	
	//metodo de seleccionar la cama para eliminar, validacion de concurrencia
	@SuppressWarnings("unchecked")
	public String seleccionarEliminar() throws IllegalStateException, SecurityException, SystemException {
		try {
			List<Cama_configuracion> camaConcu = entityManager.createQuery("select cama from Cama_configuracion cama where cama.id =:id and cama.eliminado = true").setParameter("id", idEliminar).getResultList();
			if (camaConcu.size() != 0) {
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
			error = 0;
			List<Cama_configuracion> camaConcu = entityManager.createQuery("select cama from Cama_configuracion cama where cama.id =:id and cama.eliminado = true").setParameter("id", idEliminar).getResultList();
			if (camaConcu.size() != 0) {					
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
			List<Cama_configuracion> camaConcu = entityManager.createQuery("select cama from Cama_configuracion cama where cama.id =:id and cama.eliminado = true").setParameter("id", idEliminar).getResultList();
			if (camaConcu.size() != 0) {
				throw new Exception("eliminado");			
			}		
		} catch (Exception e) {	
			error = 1;
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");				
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
	
	// remove bed	
	public void eliminar() {
		Cama_configuracion c = new Cama_configuracion();
		
		try {
			Long cid = bitacora.registrarInicioDeAccion("Eliminando cama");
			c.setCid(cid);
			
			c = (Cama_configuracion) entityManager.createQuery("select c from Cama_configuracion c " +
															   "where c.id = :id and (c.eliminado = false or c.eliminado = null)")
												  .setParameter("id", idEliminar)
												  .getSingleResult();
			this.idEliminar = -1L;
			c.setEliminado(true);
			entityManager.persist(c);
			entityManager.flush();			
			
			if (getResultList().size() == 1 && getFirstResult() != null)
				setFirstResult(getFirstResult() - getMaxResults());
			
		}catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");	
		}
		catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("userSearch",Severity.ERROR, "msjEliminar");
		}		
	}	
	
	// Important properties ----------------------------------
	public void setAvanzada(boolean searchMode) {
		this.searchMode = searchMode;

		String[] aux = (this.searchMode) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}
	
	// select bed for removal
	public void setIdEliminar(Long idEliminar) {
		this.idEliminar = idEliminar;
	}
	
	public void setServicio(String servicio) {
		if (servicio.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.servicio = "";
		else
			this.servicio = parametros.decodec(servicio);
	}
	
	public void setTipoCama(String tipoCama) {
		if (tipoCama.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.tipoCama = "";
		else 
			this.tipoCama = parametros.decodec(tipoCama);
	}
	
	public void setEstadoCama(String estadoCama) {
		if (estadoCama.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.estadoCama = "";
		else
			this.estadoCama = parametros.decodec(estadoCama);
	}
	
	public void setCategoriaCama(String categoriaCama) {
		if (categoriaCama.endsWith(SeamResourceBundle.getBundle().getString("seleccione")))
			this.categoriaCama = "";
		else
			this.categoriaCama = parametros.decodec(categoriaCama);
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = parametros.decodec(descripcion);
	}
	
	// Properties --------------------------------------------
	public Cama_configuracion getCama() {
		return cama;
	}

	public String getDescripcion() {
		return descripcion;
	}	

	public String getEstadoCama() {
		return estadoCama;
	}

	public String getCategoriaCama() {
		return categoriaCama;
	}

	public String getTipoCama() {
		return parametros.decodec(tipoCama);
	}

	public void setCama(Cama_configuracion cama) {
		this.cama = cama;
	}

	public Long getIdEliminar() {
		return idEliminar;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isSearchMode() {
		return searchMode;
	}

	public void setSearchMode(boolean searchMode) {
		this.searchMode = searchMode;
	}

	public String getServicio() {
		return servicio;
	}

	public Parameters getParametros() {
		return parametros;
	}

	public void setParametros(Parameters parametros) {
		this.parametros = parametros;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}