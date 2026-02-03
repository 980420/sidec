package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;

import java.util.ArrayList;
import java.util.Arrays;


import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.theme.Theme;

import com.itextpdf.text.List;

/**
 * Buscar los estudios al que tiene permiso un usuario
 * 
 * @author tgonzalez
 * 
 */
@Name("seleccionarEstudio")
@Scope(ScopeType.CONVERSATION)
public class SeleccionarEstudio {

	/**
	 * Para obtener la entidad en que se encuentra el usuario
	 */
	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	/**
	 * Para manejar los datos en la BD
	 */
	@In
	EntityManager entityManager;

	/**
	 * Para ver el usuario autenticado en la entidad
	 */
	@In
	Usuario user;

	/**
	 * Tema activo
	 */
	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;

	/**
	 * Direccion a la que se redireccionara
	 */
	private String dirredirect;
	private String trazasD;
	private String trazasC;
	private String cantMS;
	private String cDatos;
	
	
	private boolean inicializado;
	/**
	 * Para filtrar en la busqueda
	 */
	private String filterNombre;

	/**
	 * Saber cuando la direccion viene nula 
	 * 1 cuando viene nula 
	 * 0 cuando viene con una direccion
	 * **/
	private int direccionNula = -1;
	private ListaSeleccionaEstudio listadoSeleccionaEstudio = new ListaSeleccionaEstudio();
	private ListaSeleccionaEstudio aux = new ListaSeleccionaEstudio();
	private java.util.List<EstudioEntidad_ensayo> lisEst = new ArrayList<EstudioEntidad_ensayo>();

	public String getDirredirect() {
		if (dirredirect == null) {
			setDireccionNula(1);
		}else{
		setDireccionNula(0);
		}
		return dirredirect;
	}

	public void setDirredirect(String dirredirect) {
		this.dirredirect = dirredirect;
	}

	// ----Properties-----

	public SeleccionarEstudio(String filterNombre, String dirredirect) {
		this.filterNombre = filterNombre;
		this.dirredirect = dirredirect;
	}

	public SeleccionarEstudio() {
	}

	public IActiveModule getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	// -----Functions--------
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
		listaEstudios();
		inicializado = true;
	}

	/**
	 * Selecciona la imagen que va a tener la entidad en el listado en
	 * dependencia del tema que escoja el usuario para trabajar
	 * 
	 * @param entidad
	 * @return tema
	 */
	public String entidadIcon(EstudioEntidad_ensayo entidad) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modCommon/entidades_logos/"
				+ theme.getTheme().get("name") + "/"
				+ theme.getTheme().get("color") + "/"
				+ entidad.getEntidad().getLogo();

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/generic.png";
	}

	public String entidadIconEstadisticas(Entidad_ensayo entidad) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modCommon/entidades_logos/"
				+ theme.getTheme().get("name") + "/"
				+ theme.getTheme().get("color") + "/" + entidad.getLogo();

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/generic.png";
	}

	public ListaSeleccionaEstudio listaEstudios() {
		//rafa
				String EJBQL2 = "select estudioEntidad from "
						+ "EstudioEntidad_ensayo estudioEntidad "
						+ "JOIN estudioEntidad.entidad entidadE "
						+ "JOIN estudioEntidad.estudio estudioE " + "where "
						+ "estudioEntidad.eliminado <> true "
						+ "and entidadE.eliminado <> true "
						+ "and estudioE.eliminado <> true "
						+ "and estudioE.estadoEstudio.codigo in (3,8)";
				
				String EJBQL3 = "select estudioEntidad from "
						+ "EstudioEntidad_ensayo estudioEntidad "
						+ "JOIN estudioEntidad.entidad entidadE "
						+ "JOIN estudioEntidad.estudio estudioE where estudioEntidad.eliminado <> true "
						+ "and entidadE.eliminado <> true "
						+ "and estudioE.eliminado <> true "
						+ "and estudioE.estadoEstudio.codigo in (3,6)";
				
				String EJBQL1=listadoSeleccionaEstudio.getEjbql();
				
				if ((!activeModule.getActiveModule().getNombre()
						.contains("ensayo_disenno"))) {
					listadoSeleccionaEstudio.setEjbql(EJBQL2);
					listadoSeleccionaEstudio.getResultList();
					
				}
				if(activeModule.getActiveModule().getNombre().contains("ensayo_extraccion") && trazasD!=null)
				{
					listadoSeleccionaEstudio.setEjbql(EJBQL1);
					listadoSeleccionaEstudio.getResultList();
					
				}
				if((activeModule.getActiveModule().getNombre().contains("ensayo_extraccion") || activeModule.getActiveModule().getNombre().contains("ensayo_estadisticas")) && (trazasC!=null || cantMS!=null || cDatos!=null))
				{
					listadoSeleccionaEstudio.setEjbql(EJBQL3);
					listadoSeleccionaEstudio.getResultList();
				}
				
					
				return listadoSeleccionaEstudio;

	}

	public void buscarEstudio() {
		listadoSeleccionaEstudio.setFirstResult(0);
		String[] RESTRICTIONS = {
				"lower(estudioE.nombre) like concat('%',concat(lower(#{seleccionarEstudio.filterNombre.trim()}),'%'))",
				"entidadE.id = #{seleccionarEstudio.activeModule.getActiveModule().getEntidad().getId()}",
				"estudioEntidad.id in (select u.estudioEntidad.id from UsuarioEstudio_ensayo u where u.usuario.id = #{seleccionarEstudio.user.id} and u.eliminado <> true and u.role.eliminado <> true and u.estudioEntidad.eliminado <> true)" };
		listadoSeleccionaEstudio.setRestrictionExpressionStrings(Arrays
				.asList(RESTRICTIONS));
		listadoSeleccionaEstudio.refresh();
	}

	

	public String getFilterNombre() {
		return filterNombre;
	}

	public void setFilterNombre(String filterNombre) {
		this.filterNombre = filterNombre;
	}

	public int getDireccionNula() {
		return direccionNula;
	}

	public void setDireccionNula(int direccionNula) {
		this.direccionNula = direccionNula;
	}

	public ListaSeleccionaEstudio getListadoSeleccionaEstudio() {
		return listadoSeleccionaEstudio;
	}

	public void setListadoSeleccionaEstudio(
			ListaSeleccionaEstudio listadoSeleccionaEstudio) {
		this.listadoSeleccionaEstudio = listadoSeleccionaEstudio;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public String getTrazasD() {
		return trazasD;
	}

	public void setTrazasD(String trazasD) {
		this.trazasD = trazasD;
	}

	public String getTrazasC() {
		return trazasC;
	}

	public void setTrazasC(String trazasC) {
		this.trazasC = trazasC;
	}

	public ListaSeleccionaEstudio getAux() {
		return aux;
	}

	public void setAux(ListaSeleccionaEstudio aux) {
		this.aux = aux;
	}

	public java.util.List<EstudioEntidad_ensayo> getLisEst() {
		return lisEst;
	}

	public void setLisEst(java.util.List<EstudioEntidad_ensayo> lisEst) {
		this.lisEst = lisEst;
	}

	public String getCantMS() {
		return cantMS;
	}

	public void setCantMS(String cantMS) {
		this.cantMS = cantMS;
	}

	public String getcDatos() {
		return cDatos;
	}

	public void setcDatos(String cDatos) {
		this.cDatos = cDatos;
	}
	
	public boolean isActiveModuleDisenno() {
		return activeModule.getActiveModule().getNombre().contains("ensayo_disenno");
	}
}
