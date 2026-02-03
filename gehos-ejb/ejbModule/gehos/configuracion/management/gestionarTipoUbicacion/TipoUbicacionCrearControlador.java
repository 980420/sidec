package gehos.configuracion.management.gestionarTipoUbicacion;

import java.io.File;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoUbicacion_configuracion;

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

@Scope(ScopeType.CONVERSATION)
@Name("tipoUbicacionCrearControlador")
public class TipoUbicacionCrearControlador {
	
	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;	
	private Long cid=-1l;	
	
	private String codigo;
	private String descripcion;
	private String icon = "default.png";
	
	TipoUbicacionValidarControlador validator;
	
	private TipoUbicacion_configuracion tipo;	
	private int error;

	// Methods--------------------------------------------------------		
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void begin() {	
		this.codigo = "";
		this.descripcion = "";
		this.tipo = new TipoUbicacion_configuracion();
		this.tipo.setEliminado(false);	
		
		try {
			if (cid.equals(-1l)) {
				this.cid = bitacora.registrarInicioDeAccion("Creando tipo de ubicacion");		
				this.tipo.setCid(cid);		 
			}	
		} catch(Exception e) {
			
		}
	}

	//validate if the icon passed as parameter is selected
	public boolean isSelected(String icon) {		
		if (this.icon.equals(icon))
			return true;
		return false;
	}
	
	//returns the list of icons contained in the address
	public String[] getExistingModuleIcons() {
		try {
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String rootpath = context
					.getRealPath("/resources/modCommon/ubicacionesimages");

			File file = new File(rootpath);
			return file.list();
		} catch (Exception e){
			return new String[0];
		}		
	}
	
	@End
	public void end() {		
	}
	
	@Transactional
	public void crear() {		
		try{  
			error = 0;					
			tipo.setCodigo(this.codigo.trim());
			tipo.setDescripcion(this.descripcion.trim());	
			tipo.setIcono(this.icon);
		    tipo.setCid(this.cid);
			entityManager.persist(tipo);
			entityManager.flush();
			this.end();
		}
		catch(Exception e){
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",Severity.ERROR,"errorInesperado");			
		}		
	}	

	// Properties--------------------------------------------------------------
	public TipoUbicacion_configuracion getTipo() {
		return tipo;
	}

	public void setTipo(TipoUbicacion_configuracion tipo) {
		this.tipo = tipo;
	}
		
	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
