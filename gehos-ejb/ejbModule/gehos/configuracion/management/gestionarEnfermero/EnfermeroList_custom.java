package gehos.configuracion.management.gestionarEnfermero;

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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial") 
@Name("enfermeroList_custom")
@Scope(ScopeType.CONVERSATION)
public class EnfermeroList_custom extends EntityQuery<Enfermera_configuracion> {

	private static final String EJBQL = "select enfermera from Enfermera_configuracion enfermera join enfermera.usuario usuario where enfermera.eliminado = false";

	private static final String[] RESTRICTIONS = { "enfermera.id <> #{enfermeroList_custom.enfermeroId}" };

	private static final String[] RESTRICTIONSS = {
			"lower(enfermera.usuario.nombre) like concat(lower(#{enfermeroList_custom.nombre.trim()}),'%')",
			"enfermera.id <> #{enfermeroList_custom.enfermeroId}" };

	private static final String[] RESTRICTIONSA = {
			"lower(enfermera.usuario.nombre) like concat(lower(#{enfermeroList_custom.nombre.trim()}),'%')",
			"lower(enfermera.usuario.primerApellido) like concat(lower(#{enfermeroList_custom.primerApellido.trim()}),'%')",
			"lower(enfermera.usuario.segundoApellido) like concat(lower(#{enfermeroList_custom.segundoApellido.trim()}),'%')",
			"lower(enfermera.matriculaColegioEnfermeria) like concat(lower(#{enfermeroList_custom.matriculaEnfermeria.trim()}),'%')",
			"enfermera.id <> #{enfermeroList_custom.enfermeroId}" };

	@In FacesMessages facesMessages;
	private Enfermera_configuracion enfermero = new Enfermera_configuracion();
	private String nombre = "";
	private String primerApellido = "";
	private String segundoApellido = "";
	private String matriculaEnfermeria = "";
	private Long enfermeroId = -1l;
	private int error = 0;

	// validation
	private boolean avanzada = false;
	private boolean openSimpleTogglePanel = true;
	private int pFirstResult = -1;
	private String pOrder = "";

	// otras funcionalidades
	private boolean open = true;
	private Parameters parametros = new Parameters();

	@In
	EntityManager entityManager;

	// CONTRUCTOR
	public EnfermeroList_custom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("enfermera.id desc");
	}

	// METODOS---------------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}

	// cambia el tipo de busqueda (avanzada o simple)
	public void tipoBusqueda() {
		avanzada = !avanzada;
	}
	
	// cambia el estado del simpleTogglePanel (abierto o cerrado)
	public void cambiarEstadoSimpleTogglePanel() {
		openSimpleTogglePanel = !openSimpleTogglePanel;
	}

	// Inicializa el indice del paginado cuando se busca
	public void Buscar() {
		setFirstResult(0);
		String[] aux = (avanzada) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	// para mantener los datos de paginado y filtro
	public void setPFirstResult(int pFirstResult) {
		if (this.pFirstResult == -1 && pFirstResult != -1) {
			setFirstResult(pFirstResult);
		}
		this.pFirstResult = pFirstResult;
	}

	// cancela la busqueda
	public void cancelarBusqueda() {
		if (!avanzada && !nombre.equals("")) {
			setFirstResult(0);
		} else if (avanzada
				&& (!nombre.equals("") || !primerApellido.equals("")
						|| !segundoApellido.equals("") || !matriculaEnfermeria
						.equals(""))) {
			setFirstResult(0);
		}
		nombre = "";
		primerApellido = "";
		segundoApellido = "";
		matriculaEnfermeria = "";
	}
	
	// selecciona enfermera para modificar
	@SuppressWarnings("unchecked")
	public void seleccionarModificar() {
		try {
			error = 0;
			List<Enfermera_configuracion> enfConcu = entityManager.createQuery("select enf from Enfermera_configuracion enf where enf.id =:id and enf.eliminado = true").setParameter("id", enfermeroId).getResultList();
			if (enfConcu.size() != 0) {					
				throw new Exception("eliminado");
			}
		} catch (Exception e) {			
			facesMessages.addToControlFromResourceBundle("idForm", Severity.ERROR, "eliminado");
			error = 1;
		}
				
	}
	
	@SuppressWarnings("unchecked")
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error = 0;
			List<Enfermera_configuracion> enfConcu = entityManager.createQuery("select enf from Enfermera_configuracion enf where enf.id =:id and enf.eliminado = true").setParameter("id", enfermeroId).getResultList();
			if (enfConcu.size() != 0) {
				throw new Exception("eliminado");			
			}		
		} catch (Exception e) {	
			error = 1;
			facesMessages.addToControlFromResourceBundle("idForm", Severity.ERROR, "eliminado");				
		}				
	}

	// selecciona enfermera para eliminar
	@SuppressWarnings("unchecked")
	public String seleccionarEnfermero() throws IllegalStateException, SecurityException, SystemException {
		try {
			List<Enfermera_configuracion> enfConcu = entityManager.createQuery("select enf from Enfermera_configuracion enf where enf.id =:id and enf.eliminado = true").setParameter("id", enfermeroId).getResultList();
			if (enfConcu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("idForm", Severity.ERROR, "eliminado");
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

	// elimina enfermero selecionado
	@SuppressWarnings("unchecked")
	public void remov() {
		try {
			Enfermera_configuracion enfermeroEliminar = (Enfermera_configuracion) entityManager.createQuery("select enf from Enfermera_configuracion enf where enf.id =:id and enf.eliminado = false")
																	.setParameter("id", enfermeroId)
																	.getSingleResult();
			
			enfermeroEliminar.setEliminado(true);
			this.enfermeroId = -1l;
			entityManager.persist(enfermeroEliminar);

			List<EnfermeraInEntidad> aux = entityManager
					.createQuery(
							"select enferInEntity from EnfermeraInEntidad enferInEntity where enferInEntity.enfermera.id =:id")
					.setParameter("id", enfermeroEliminar.getId()).getResultList();
			for (int i = 0; i < aux.size(); i++) {
				aux.get(i).setEliminado(true);
			}
			entityManager.flush();

			if (getResultList().isEmpty() && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("idForm", Severity.ERROR, "eliminado");	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// PROPIEDADES------------------------------------------------------------------------
	public Enfermera_configuracion getEnfermero() {
		return enfermero;
	}

	public void setEnfermero(Enfermera_configuracion enfermero) {
		this.enfermero = enfermero;
	} 
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = parametros.decodec(nombre);
	}

	public Long getEnfermeroId() {
		return enfermeroId;
	}

	public void setEnfermeroId(Long enfermeroId) {
		this.enfermeroId = enfermeroId;
	}

	public int getPFirstResult() {
		return pFirstResult;
	}

	public String getPOrder() {
		return pOrder;
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = parametros.decodec(primerApellido);
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = parametros.decodec(segundoApellido);
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {		
		this.avanzada = avanzada;
		String[] aux = (avanzada) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	public boolean isOpenSimpleTogglePanel() {
		return openSimpleTogglePanel;
	}

	public void setOpenSimpleTogglePanel(boolean openSimpleTogglePanel) {
		this.openSimpleTogglePanel = openSimpleTogglePanel;
	}

	public void setPOrder(String order) {
		pOrder = order;
	}

	public String getMatriculaEnfermeria() {
		return matriculaEnfermeria;
	}

	public void setMatriculaEnfermeria(String matriculaEnfermeria) {
		this.matriculaEnfermeria = parametros.decodec(matriculaEnfermeria);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

}