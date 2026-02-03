package gehos.ensayo.ensayo_configuracion.session.custom;

import edu.emory.mathcs.backport.java.util.Arrays;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.session.common.auto.EstudioList_ensayo;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Buscar los estudios al que tiene permiso un usuario
 * 
 * @author tgonzalez
 * 
 */
@Name("permisosSeleccionarEstudio")
@Scope(ScopeType.CONVERSATION)
public class PermisosSeleccionarEstudio extends EstudioList_ensayo {

	/**
	 * Para manejar los datos en la BD
	 */
	@In
	EntityManager entityManager;

	/**
	 * Para obtener la entidad en que se encuentra el usuario
	 */
	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	/**
	 * Para filtrar en la busqueda
	 */
	private String filterNombre = "";
	private int pagina;
	private static final String EJBQL = "select estudio from Estudio_ensayo estudio "
			+ "where estudio.eliminado <> true ";

	private static final String[] RESTRICTIONSS = {
			"lower(estudio.nombre) like concat('%',concat(lower(#{permisosSeleccionarEstudio.filterNombre.trim()}),'%'))",
			"estudio.entidad.id= #{permisosSeleccionarEstudio.activeModule.getActiveModule().getEntidad().getId()}" };

	// ----Properties-----

	public PermisosSeleccionarEstudio() {
		super();
		setEjbql(EJBQL);
		setMaxResults(10);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setOrder("estudio.nombre desc");
	}

	public int getPagina() {
		if (this.getNextFirstResult() != 0)
			return this.getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getResultCount() / 10) + 1;
		if (this.pagina > 0) {
			if (getResultCount() % 10 != 0) {
				if (pagina <= num)
					this.setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

	/*
	 * Devuelve la url del icono para estado pasado por parametro.
	 * 
	 * @parametro: estado, estado del cual vamos a obtener el icono.
	 */
	public String estadoIcon(EstadoEstudio_ensayo estado) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modEnsayo/estadosIcon/"
				+ estado.getClass().getSimpleName().split("_")[0] + "/"
				+ estado.getNombre() + ".png";

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modEnsayo/estadosIcon/" + "generic.png";

	}

	public String getFilterNombre() {
		return filterNombre;
	}

	public void setFilterNombre(String filterNombre) {
		this.filterNombre = filterNombre;
	}

	public IActiveModule getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	// -----Functions--------
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
	}

}
