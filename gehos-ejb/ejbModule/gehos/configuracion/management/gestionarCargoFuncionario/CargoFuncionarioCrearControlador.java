package gehos.configuracion.management.gestionarCargoFuncionario;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("cargoFuncionarioCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class CargoFuncionarioCrearControlador {
	
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In(create = true) FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	@In(required = false, value = "cargoFuncionarioBuscarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "cargoFuncionarioBuscarControlador", scope = ScopeType.CONVERSATION)
	CargoFuncionarioBuscarControlador cargoFuncionarioBuscarControlador;

	private String nombre;
	private String tipoFuncionarioSeleccionado;
	private CargoFuncionario_configuracion cargoFuncionario;
	
	// other functions
	private int error;
	private Long cid = -1l;
	
	// Methods ------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		try {
			this.nombre = "";
			this.tipoFuncionarioSeleccionado = "";
			this.cargoFuncionario = new CargoFuncionario_configuracion();
			
			if(cid == -1) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
			}	
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> listadoTipoFuncionario()
	{	
		try {
			return entityManager.createQuery("select f.valor from TipoFuncionario_configuracion f order by f.valor")
								.getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}	

	@End
	public void end() {
	}
	
	@Transactional
	public void crear() {
		error = 0;
		try {		
			TipoFuncionario_configuracion t = (TipoFuncionario_configuracion) 
			  								  entityManager.createQuery("select t from TipoFuncionario_configuracion t " +
			  								  							"where t.valor = :tf and t.eliminado = false")
			  								  			   .setParameter("tf", this.tipoFuncionarioSeleccionado)
			  								  			   .getSingleResult();
			
			this.cargoFuncionario.setValor(nombre.trim());
			this.cargoFuncionario.setTipoFuncionario(t);
			this.cargoFuncionario.setEliminado(false);
			this.cargoFuncionario.setCid(cid);
			
			entityManager.persist(cargoFuncionario);
			entityManager.flush();		
			
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
		}
	}

	// Properties -------------------------------------------------------------
	public String getNombre() {
		return nombre;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getTipoFuncionarioSeleccionado() {
		return tipoFuncionarioSeleccionado;
	}

	public void setTipoFuncionarioSeleccionado(String tipoFuncionarioSeleccionado) {
		this.tipoFuncionarioSeleccionado = tipoFuncionarioSeleccionado;
	}

	public CargoFuncionario_configuracion getCargoFuncionario() {
		return cargoFuncionario;
	}

	public void setCargoFuncionario(CargoFuncionario_configuracion cargoFuncionario) {
		this.cargoFuncionario = cargoFuncionario;
	}	
}