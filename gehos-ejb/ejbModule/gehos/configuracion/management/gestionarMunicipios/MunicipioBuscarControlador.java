package gehos.configuracion.management.gestionarMunicipios;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION) 
@Name("municipioBuscarControlador")
public class MunicipioBuscarControlador extends EntityQuery<Municipio_configuracion> {

	private static final String EJBQL = "select municipio from Municipio_configuracion municipio where municipio.estado.eliminado = false and municipio.estado.nacion.eliminado = false";
	private static final String[] RESTRICTIONS = {"lower(municipio.valor) like concat(lower(#{municipioBuscarControlador.municipio.valor.trim()}),'%')",
		  										  "lower(municipio.codigo) like concat(lower(#{municipioBuscarControlador.municipio.codigo.trim()}),'%')",
		  										  "lower(municipio.estado.valor) like concat(lower(#{municipioBuscarControlador.municipio.estado.valor.trim()}),'%')",
		  										  "lower(municipio.estado.nacion.valor) like concat(lower(#{municipioBuscarControlador.municipio.estado.nacion.valor.trim()}),'%')"};
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search 
	private String codigo = "";
	private String valor = "";
	private String estado = "";
	private String nacion = "";
	
	// other fuctionalities
	private Long idMunicipioEliminar;
	private boolean state = true;	
	private Long cid = -1l;

	private Municipio_configuracion municipio = new Municipio_configuracion();	

	public MunicipioBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("municipio.id desc");
	}

	//Methods-----------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.refresh();
		
		/*if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());*/
	}
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// search 
	public void buscar() {
		Estado_configuracion e = new Estado_configuracion();
		Nacion_configuracion n = new Nacion_configuracion();
		
		n.setValor(nacion);
		e.setValor(estado);
		e.setNacion(n);
		
		municipio.setCodigo(codigo);
		municipio.setValor(valor);
		
		municipio.setEstado(e);
		municipio.getEstado().setNacion(n);
		
		setFirstResult(0);
	}
		
	// select item to removal
	public void seleccionar(Long id) {
		this.idMunicipioEliminar = id;
	}
	
	// return a contry list
	@SuppressWarnings("unchecked")
	public List<String> listNaciones(){
		List<String> le = new ArrayList<String>();
		try {
			le = entityManager.createQuery("select n.valor from Nacion_configuracion n " +
										   "where n.eliminado = false " +
										    "order by n.valor")
							  .getResultList();
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		} catch (Exception e) {
			e.printStackTrace();
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listEstados(){
		List<String> le = new ArrayList<String>();
		try {
			if(!nacion.equals(""))
				le = entityManager.createQuery("select e.valor from Estado_configuracion e " +
											   "where e.eliminado = false " +
											   "and e.nacion.valor = :nacion " +
											   "order by e.valor")
								  .setParameter("nacion", nacion)
								  .getResultList();
			
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		} catch (Exception e) {
			e.printStackTrace();
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		}
	}	
	
	// remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			Municipio_configuracion municipio = (Municipio_configuracion) 
			  							        entityManager.createQuery("select m from Municipio_configuracion m " +
			  							   							      "where m.id =:id")
			  							   				     .setParameter("id", idMunicipioEliminar)
			  							   				     .getSingleResult();
			
			entityManager.remove (municipio);
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
			
			Municipio_configuracion municipio = (Municipio_configuracion) 
			   							        entityManager.createQuery("select m from Municipio_configuracion m " +
			   							  							      "where m.id =:id")
			   							  			         .setParameter("id", id)
			   							  			         .getSingleResult();		
			
			if(municipio.getEliminado() == null || municipio.getEliminado() == false)
				municipio.setEliminado(true);
			else
				municipio.setEliminado(false);	
			
			municipio.setCid(cid);
			
			entityManager.persist(municipio);
			entityManager.flush();
			
			this.refresh();
		}catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	// Properties-------------------------------------------------------
	public void setNacion(String nacion) {
		if(nacion.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.nacion = "";
		else
			this.nacion = nacion;
	}
	
	public void setEstado(String estado) {
		if(estado.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.estado = "";
		else
			this.estado = estado;		
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

	public String getNacion() {
		return nacion;
	}	

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Municipio_configuracion getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio_configuracion municipio) {		
		this.municipio = municipio;
	}

	public String getEstado() {
		return estado;
	}	

	public Long getIdMunicipioEliminar() {
		return idMunicipioEliminar;
	}

	public void setIdMunicipioEliminar(Long idMunicipioEliminar) {
		this.idMunicipioEliminar = idMunicipioEliminar;
	}
}