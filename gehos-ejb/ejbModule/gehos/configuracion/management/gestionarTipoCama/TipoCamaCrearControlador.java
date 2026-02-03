package gehos.configuracion.management.gestionarTipoCama;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoCama_configuracion;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import java.io.File;

@Name("tipoCamaCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class TipoCamaCrearControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;		
	
	private String codigo;
	private String valor;
	private String icon = "camas.png";
	private TipoCama_configuracion tipoCama;
	
	//other functions
	private int error;
	private Long cid = -1l;
			
	//Methods------------------------------------------------------------	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public String begin(){
		error = 0;
		
		try {
			this.codigo = "";
			this.valor = "";
			tipoCama = new TipoCama_configuracion();			
			
			this.cid = bitacora.registrarInicioDeAccion("bitCrear");		
			this.tipoCama.setCid(cid);				
		} 
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
		return "go";			
	}
	
	// validate if the icon passed as parameter is selected
	public boolean isSelected(String icon) {
		if (this.icon.equals(icon))
			return true;
		return false;
	}
	
	// return the list of icons contained in the address
	public String[] getExistingModuleIcons() {		
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String rootpath = context
				.getRealPath("/resources/modCommon/tipocamaimage");

		File file = new File(rootpath);
		return file.list();
	}	
	
	@End
	public void end() {		
	}
	
	@Transactional
	public void crear(){
		error = 0;
		try { 
			this.tipoCama.setIcono(icon);
			this.tipoCama.setCodigo(this.codigo.trim());
			this.tipoCama.setValor(this.valor.trim());
			this.tipoCama.setEliminado(false);
			
			entityManager.persist(tipoCama);
			entityManager.flush();	
			this.end();
		} 
		catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",Severity.ERROR,"errorInesperado");			
		}
	}
	
	//Properties--------------------------------------------------
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}	
	
	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}
	public void setTipoCama(TipoCama_configuracion tipoCama) {
		this.tipoCama = tipoCama;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
