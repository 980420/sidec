package gehos.configuracion.management.gestionarCargoFuncionario;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("cargoFuncionarioVerDetallesControlador")
public class CargoFuncionarioVerDetallesControlador {
			
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
		
	private Integer cargoId;
	private CargoFuncionario_configuracion cargoFuncionario;
			
	//other functions
	private String from = "";
	private int error;
		
	//Methods ---------------------------------------------------------------------
	public void setCargoId(Integer cargoId) {
		this.cargoId = cargoId;
		error = 0;
		try {
			cargoFuncionario = (CargoFuncionario_configuracion)
							   entityManager.createQuery("select cf from CargoFuncionario_configuracion cf " +
									  					 "where cf.id = :id")
									  		.setParameter("id", this.cargoId)
									  		.getSingleResult();
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void eliminar(){
		error = 0;
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			entityManager.remove(cargoFuncionario);
			entityManager.flush();			
		
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
			e.printStackTrace();
		}		
	}

	//Properties -------------------------------------------------
	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}
	
	public Integer getCargoId() {
		return cargoId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public CargoFuncionario_configuracion getCargoFuncionario() {
		return cargoFuncionario;
	}

	public void setCargoFuncionario(CargoFuncionario_configuracion cargoFuncionario) {
		this.cargoFuncionario = cargoFuncionario;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}