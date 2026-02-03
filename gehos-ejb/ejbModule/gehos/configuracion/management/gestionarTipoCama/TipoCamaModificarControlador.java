package gehos.configuracion.management.gestionarTipoCama;

import java.io.File;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.TipoCama_configuracion;

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
@Name("tipoCamaModificarControlador")
public class TipoCamaModificarControlador {

	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;
	@In
	FacesMessages facesMessages;

	private String codigo;
	private String valor;
	private Long id;
	private String icon = "";
	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();

	// other functions
	private int error;
	private Long cid = -1l;

	// Methods-------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {
		if (this.id == null) {
			try {
				error = 0;
				this.id = id;
				tipoCama = new TipoCama_configuracion();

				this.tipoCama = (TipoCama_configuracion) entityManager
						.createQuery(
								"select t from TipoCama_configuracion t where t.id =:id")
						.setParameter("id", this.id).getSingleResult();

				this.codigo = tipoCama.getCodigo();
				this.valor = tipoCama.getValor();
				this.icon = this.tipoCama.getIcono();

				cid = bitacora.registrarInicioDeAccion("bitModificar");
				tipoCama.setCid(cid);

			} catch (NoResultException e) {
				error = 1;
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "eliminado");
				e.printStackTrace();
			} catch (Exception e) {
				error = 1;
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "errorInesperado");
				e.printStackTrace();
			}
		}
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
	public void modificar() {
		error = 0;
		try {
			this.tipoCama.setIcono(icon);
			this.tipoCama.setCodigo(this.codigo.trim());
			this.tipoCama.setValor(this.valor.trim());

			entityManager.merge(tipoCama);
			entityManager.flush();
			this.end();

		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}

	// Properties-----------------------------------------
	public Long getId() {
		return id;
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
