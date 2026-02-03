package gehos.configuracion.management.gestionarTipoUbicacion;

import java.io.File;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoUbicacion_configuracion;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
@Name("tipoUbicacionModificarControlador")
public class TipoUbicacionModificarControlador {

	@In
	IBitacora bitacora;
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	private Long cid = -1l;

	private Long id;
	private String codigo;
	private String descripcion;
	private String icon = "default.png";
	private TipoUbicacion_configuracion tipo;
	private int error;
	private boolean errorLoadData;

	// Methods-----------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {
		if (this.id == null) {
			errorLoadData = false;
			this.id = id;
			tipo = new TipoUbicacion_configuracion();
			try {
				this.tipo = (TipoUbicacion_configuracion) entityManager
						.createQuery(
								"select t from TipoUbicacion_configuracion t where t.id =:id")
						.setParameter("id", this.id).getSingleResult();
				this.icon = tipo.getIcono();
				this.codigo = tipo.getCodigo();
				this.descripcion = tipo.getDescripcion();

				if (cid.equals(-1l)) {
					cid = bitacora
							.registrarInicioDeAccion("Modificando tipo de ubicacion");
					this.tipo.setCid(cid);
				}
			} catch (NoResultException e) {
				errorLoadData = true;
			}
		}
	}

	// validate if the icon passed as parameter is selected
	public boolean isSelected(String icon) {
		if (this.icon.equals(icon))
			return true;
		return false;
	}

	// returns the list of icons contained in the address
	public String[] getExistingModuleIcons() {
		try {
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String rootpath = context
					.getRealPath("/resources/modCommon/ubicacionesimages");

			File file = new File(rootpath);
			return file.list();
		} catch (Exception e) {
			return new String[0];
		}
	}

	@End
	public void end() {
	}

	@Transactional
	public void modificar() {
		try {
			error = 0;
			tipo.setCodigo(this.codigo.trim());
			tipo.setDescripcion(this.descripcion.trim());
			tipo.setIcono(this.icon);
			entityManager.persist(tipo);
			entityManager.flush();
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "errorInesperado");
		}
	}

	// Properties------------------------------------------------------
	public Long getId() {
		return id;
	}

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

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
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
