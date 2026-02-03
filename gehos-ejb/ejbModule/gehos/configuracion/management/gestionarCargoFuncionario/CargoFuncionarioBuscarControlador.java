package gehos.configuracion.management.gestionarCargoFuncionario;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@SuppressWarnings("serial") 
@Scope(ScopeType.CONVERSATION)
@Name("cargoFuncionarioBuscarControlador")
public class CargoFuncionarioBuscarControlador extends EntityQuery<CargoFuncionario_configuracion> {

	private static final String EJBQL = "select cargo from CargoFuncionario_configuracion cargo";
			
	private static final String[] RESTRICTIONS = { 
		"lower(cargo.valor) like concat(lower(#{cargoFuncionarioBuscarControlador.cargo.valor.trim()}),'%')", 
		"lower(cargo.tipoFuncionario.valor) like concat(lower(#{cargoFuncionarioBuscarControlador.cargo.tipoFuncionario.valor.trim()}),'%')" };
	
	@In EntityManager entityManager;	
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search criteria
	private String nombre = "";
	private String tipoFuncionario = "";
	
	private CargoFuncionario_configuracion cargo = new CargoFuncionario_configuracion();
	
	// other functions
	private Integer cargoId = -1;
	private boolean state = true;
	private Long cid = -1l;	

	public CargoFuncionarioBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("cargo.id desc");
	}	
	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.refresh();
		
		if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());
	}
	
	// search
	public void buscar(){				
		cargo.setValor(nombre);
		
		TipoFuncionario_configuracion t = new TipoFuncionario_configuracion();
		t.setValor(tipoFuncionario);
		cargo.setTipoFuncionario(t);
		
		setFirstResult(0);
	}	
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// seleccionar to removal
	public void seleccionar(int idcama){
		this.cargoId = idcama;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listadoTipoFuncionario()
	{	
		List<String> ltf = new ArrayList<String>();		
		try {
			ltf =  entityManager.createQuery("select f.valor from TipoFuncionario_configuracion f order by f.valor")
								.getResultList();
			
			ltf.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return ltf;
			
		} catch (Exception e) {
			e.printStackTrace();
			ltf.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return ltf;
		}
	}
	
	// remove
	@Transactional
	public void eliminar(){
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			CargoFuncionario_configuracion cargo = (CargoFuncionario_configuracion) 
			 entityManager.createQuery("select c from CargoFuncionario_configuracion c where c.id =:id")
			 			  .setParameter("id", cargoId)
			 			  .getSingleResult();
			
			this.cargoId = -1;
			entityManager.remove(cargo);
			entityManager.flush();					 			
		
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}
	
	// change visibility
	@Transactional
	public void cambiarVisibilidad(int id){		
		try{
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
		
			CargoFuncionario_configuracion aux = (CargoFuncionario_configuracion) 
												 entityManager.createQuery("select c from CargoFuncionario_configuracion c " +
												 						   "where c.id =:id")
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
	
	// Important properties --------------------------------------
	public void setTipoFuncionario(String tipoFuncionario) {
		if(tipoFuncionario.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.tipoFuncionario = "";
		else
		    this.tipoFuncionario =  tipoFuncionario;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
	
	// Properties ------------------------------------------------
	public CargoFuncionario_configuracion getCargo() {
		return cargo;
	}

	public void setCargo(CargoFuncionario_configuracion cargo) {
		this.cargo = cargo;
	}

	public Integer getCargoId() {
		return cargoId;
	}

	public void setCargoId(Integer cargoId) {
		this.cargoId = cargoId;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getTipoFuncionario() {
		return tipoFuncionario;
	}	
}