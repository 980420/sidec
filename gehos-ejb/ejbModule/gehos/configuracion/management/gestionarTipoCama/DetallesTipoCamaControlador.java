package gehos.configuracion.management.gestionarTipoCama;

import gehos.configuracion.management.entity.TipoCama_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("detallesTipoCamaControlador")
@Scope(ScopeType.CONVERSATION)
public class DetallesTipoCamaControlador {
	
	@In 
	EntityManager entityManager;
	
	@In
	LocaleSelector localeSelector;
	
	@In(create = true)
	FacesMessages facesMessages;
	
	//Campos	
	private Long id;
	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();
	
	//Metodos
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){		
	}
	
	public String eliminar(){
		try {
			entityManager.remove(tipoCama);
			entityManager.flush();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, "Este tipo de cama está en uso por lo tanto no puede ser eliminado.");
			return "fail";
		}
		return "eliminar";
		
	}

	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}
	
	//Propiedades

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if (this.id == null) {
			this.id = id;
			
			this.tipoCama = entityManager.find(TipoCama_configuracion.class, this.id);
		}		
	}

	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}

	public void setTipoCama(TipoCama_configuracion tipoCama) {
		this.tipoCama = tipoCama;
	}

}
