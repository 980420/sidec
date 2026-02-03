package gehos.configuracion.management.gestionarTipoCama;

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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("tipoCamaBuscarControlador")
public class TipoCamaBuscarControlador extends EntityQuery<TipoCama_configuracion> {

	private static final String EJBQL = "select tipoCama from TipoCama_configuracion tipoCama";

	private static final String[] RESTRICTIONS = {
			"lower(tipoCama.valor) like concat(lower(#{tipoCamaBuscarControlador.valor.trim()}),'%')",
			"lower(tipoCama.codigo) like concat(lower(#{tipoCamaBuscarControlador.codigo.trim()}),'%')", };

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;

	// search criteria
	private String codigo, valor = "";
	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();
	
	// other functiones 
	private Long idEliminar = -1l;
	private Long cid = -1l;
	private boolean state = true;//state of singletuglepanel
	private Parameters parametros = new Parameters();  //codec and decodec parameters

	private int error = 0;
	

	public TipoCamaBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("tipoCama.id desc");
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {		
		this.refresh();
		
		if (getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult() - getMaxResults());
	}

	// change the state of singleTugglePanel (open/close)
	public void cambiarEstadoSimpleTugglePanel() {
		this.state = !state;
	}

	// search
	public void buscar() {		
		tipoCama = new TipoCama_configuracion();
		tipoCama.setValor(valor);
		tipoCama.setCodigo(codigo);
		
		setFirstResult(0);		
	}

	// select item for removal
	public void seleccionarEliminar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			TipoCama_configuracion tipoCamaConcu = (TipoCama_configuracion) 
				  entityManager.createQuery("select t from TipoCama_configuracion t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}
	}
	
	// selecciona serv para modificar
	
	public void seleccionarModificar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			TipoCama_configuracion tipoCamaConcu = (TipoCama_configuracion) 
				  entityManager.createQuery("select t from TipoCama_configuracion t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}
				
	}
	
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			TipoCama_configuracion tipoCamaConcu = (TipoCama_configuracion) 
				  entityManager.createQuery("select t from TipoCama_configuracion t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}	
	}

	//remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
						
			TipoCama_configuracion tipoCama = (TipoCama_configuracion) 
					   						  entityManager.createQuery("select t from TipoCama_configuracion t where t.id =:id")
					   				          .setParameter("id", idEliminar)
					   				          .getSingleResult();			
			entityManager.remove(tipoCama);
			entityManager.flush();
			

		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}

	// change item visibility
	public void cambiarVisibilidad(Long id) {
		try{		
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
			
			TipoCama_configuracion aux = (TipoCama_configuracion) entityManager.createQuery("select tipo from TipoCama_configuracion tipo where tipo.id =:id").setParameter("id", id).getSingleResult();
			
			
			if (aux.getEliminado() == null || aux.getEliminado() == false)
				aux.setEliminado(true);
			else
				aux.setEliminado(false);
			entityManager.persist(aux);
			entityManager.flush();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	//Properties--------------------------------------------------------
	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = parametros.decodec(codigo);
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = parametros.decodec(valor);
	}

	public void setTipoCama(TipoCama_configuracion tipoCama) {
		this.tipoCama = tipoCama;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Long getIdEliminar() {
		return idEliminar;
	}

	public void setIdEliminar(Long idEliminar) {
		this.idEliminar = idEliminar;
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
