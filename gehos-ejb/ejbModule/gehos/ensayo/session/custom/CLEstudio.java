package gehos.ensayo.session.custom;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.session.common.auto.EstudioList_ensayo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

@Name("clEstudio")
@Scope(ScopeType.CONVERSATION)
public class CLEstudio extends EstudioList_ensayo {
	private String nombre;
	private boolean existResultados = true;
	private String apellido1;
	private String apellido2;
	private String cedula;
	private String EFaseEstudio;
	private String estadoEstudio;
	private Date fechacreacion;
	private String entidad;
	private List<String> lisEntidad;
	private List<String> lisfase;
	private List<String> lisEstados;
	private boolean busquedaTipo = false;
	private boolean openSimpleTogglePanel = true;
	private Long idestudioelim;

	@In
	Usuario user;
	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;

	private static final String EJBQL = 
			/*"select e from Estudio_ensayo e "
			+ " inner join EstudioEntidad_ensayo estudioEntidad"
			+ " where e.eliminado=false"
			+ " and e.entidad.id=#{clEstudio.activeModule.getActiveModule().getEntidad().getId()}";*/
			
			"select distinct e from EstudioEntidad_ensayo estudioEntidad "
			+ " JOIN estudioEntidad.estudio e "
			+ " JOIN estudioEntidad.entidad entidadE "
			+ " where e.eliminado=false "
			+ " AND e.entidad.id= #{permisosSeleccionarEstudio.activeModule.getActiveModule().getEntidad().getId()}"
			+ " AND entidadE.id = #{clEstudio.activeModule.getActiveModule().getEntidad().getId()}"
			+ " AND estudioEntidad.id in (select u.estudioEntidad.id from UsuarioEstudio_ensayo u"
			+ " where u.usuario.id = #{clEstudio.user.id} and u.eliminado <> true "
			+ "and u.role.eliminado <> true and u.estudioEntidad.eliminado <> true)";

	/**
	 * Para obtener la entidad en que se encuentra el usuario
	 */
	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	public IActiveModule getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	// busqueda avanzada
	private static final String[] RESTRICTIONSA = {
			"lower(e.nombre) like concat('%',concat(lower(#{clEstudio.estudio.nombre}),'%'))",
			"lower(e.investigadorPrincipal) like concat('%',concat(lower(#{clEstudio.estudio.investigadorPrincipal}),'%'))",
			"lower(e.EFaseEstudio.nombre) like concat (lower(#{clEstudio.EFaseEstudio}), '%')",
			"lower(e.estadoEstudio.nombre) like concat (lower(#{clEstudio.estadoEstudio}), '%')",};

	// busqueda simple
	private static final String[] RESTRICTIONSS = { "lower(e.nombre) like concat('%',concat(lower(#{clEstudio.estudio.nombre}),'%'))"};
													

	public void eliminarEstudio() {
		Estudio_ensayo estudioelimino = new Estudio_ensayo();
		estudioelimino = (Estudio_ensayo) entityManager
				.createQuery("select e from Estudio_ensayo e where e.id=:id")
				.setParameter("id", idestudioelim).getSingleResult();
		estudioelimino.setEliminado(true);
		estudioelimino.setCid(bitacora
				.registrarInicioDeAccion(SeamResourceBundle.getBundle()
						.getString("bitEliminar")));
		entityManager.persist(estudioelimino);
		entityManager.flush();
		if (getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult() - getMaxResults());
		this.refresh();
	}

	public void setIdestudioelim(Long idestudioelim) {
		this.idestudioelim = idestudioelim;
	}

	public Long getIdestudioelim() {
		return idestudioelim;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	private Date fecha;

	public String getNombre() {
		return nombre;
	}

	public void setNombres(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public boolean isOpenSimpleTogglePanel() {
		return openSimpleTogglePanel;
	}

	public void setOpenSimpleTogglePanel(boolean openSimpleTogglePanel) {
		this.openSimpleTogglePanel = openSimpleTogglePanel;
	}

	public boolean isBusquedaTipo() {
		return busquedaTipo;
	}

	Estudio_ensayo estudio = new Estudio_ensayo();

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
	}
	
	// habilitar modificar y eliminar si grupo de sujetos creado
		@SuppressWarnings("unchecked")
		public boolean habilitarModificarEliminar(Estudio_ensayo estudiop) {
			boolean result = true;
			List<GrupoSujetos_ensayo> listagruposujetos = new ArrayList<GrupoSujetos_ensayo>();

			listagruposujetos = (List<GrupoSujetos_ensayo>) entityManager
					.createQuery(
							"select gs from GrupoSujetos_ensayo gs where gs.estudio=:estudio")
					.setParameter("estudio", estudiop).getResultList();
			if (listagruposujetos.size() > 1 || (listagruposujetos.size() == 1 && !listagruposujetos.get(0).getNombreGrupo().equals("Grupo Pesquisaje"))) {
				result = false;
				//estadoEstudio = "En Diseño";
			}

			return result;

		}

	public CLEstudio() {

		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("e.id desc");
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}

	public void Buscar() {
		this.setFirstResult(0);
	}

	public void Lista() {
		this.apellido1 = "";
		this.apellido2 = "";
		this.cedula = "";
		this.fechacreacion = null;
		this.nombre = "";
		this.setFirstResult(0);
	}

	public Date getFechacreacion() {
		return fechacreacion;
	}

	public void setFechacreacion(Date fechacreacion) {
		this.fechacreacion = fechacreacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	private int pagina;

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

	/**
	 * @return the lisEntidad
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLisEntidad() {
		if (this.lisEntidad == null || this.lisEntidad.size() == 0) {
			this.lisEntidad = (List<String>) entityManager.createQuery(
					"select e.nombre from Entidad_ensayo e "
							+ "where e.eliminado = FALSE").getResultList();
			this.lisEntidad.add(0,
					SeamResourceBundle.getBundle().getString("seleccione"));
		}
		return lisEntidad;
	}

	/**
	 * @param lisEntidad
	 *            the lisEntidad to set
	 */
	public void setLisEntidad(List<String> lisEntidad) {
		this.lisEntidad = lisEntidad;
	}

	// cargar el estado de la restriccion a partir del tipo de busqueda
	public void setBusquedaTipo(boolean busquedaTipo) {
		this.busquedaTipo = busquedaTipo;
		// setFirstResult(0);
		String[] aux = (busquedaTipo) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	// dado el tipo de busqueda carga la restriccion
	public void busqueda(boolean avan) {
		setFirstResult(0);
		String[] aux = (avan) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
		this.existResultados = (this.getResultCount() != 0);
	}

	// cambia de tipo de busqueda (avanzada o normal)
	public void cambiar(boolean a) {
		this.busquedaTipo = a;
	}

	/**
	 * @return the lisfase
	 */
	public List<String> getLisfase() {
		if (this.lisfase == null || this.lisfase.size() == 0) {
			this.lisfase = (List<String>) entityManager.createQuery(
					"select e.nombre from EFaseEstudio_ensayo e "
							+ "where e.eliminado = FALSE").getResultList();
			this.lisfase.add(0,
					SeamResourceBundle.getBundle().getString("seleccione"));
		}
		return lisfase;
	}

	/**
	 * @param lisfase
	 *            the lisfase to set
	 */
	public void setLisfase(List<String> lisfase) {
		this.lisfase = lisfase;
	}

	/**
	 * @return the lisEstados
	 */
	public List<String> getLisEstados() {
		if (this.lisEstados == null || this.lisEstados.size() == 0) {
			this.lisEstados = (List<String>) entityManager.createQuery(
					"select e.nombre from EstadoEstudio_ensayo e ")
					.getResultList();
			this.lisEstados.add(0,
					SeamResourceBundle.getBundle().getString("seleccione"));
		}
		return lisEstados;
	}

	/**
	 * @param lisEstados
	 *            the lisEstados to set
	 */
	public void setLisEstados(List<String> lisEstados) {
		this.lisEstados = lisEstados;
	}

	/**
	 * @return the eFaseEstudio
	 */
	public String getEFaseEstudio() {
		return EFaseEstudio;
	}

	/**
	 * @param eFaseEstudio
	 *            the eFaseEstudio to set
	 */
	public void setEFaseEstudio(String eFaseEstudio) {
		EFaseEstudio = eFaseEstudio;
		if(EFaseEstudio.equals("Seleccione"))
			EFaseEstudio=null;
	}

	/**
	 * @return the estadoEstudio
	 */
	public String getEstadoEstudio() {
		return estadoEstudio;
	}

	/**
	 * @param estadoEstudio
	 *            the estadoEstudio to set
	 */
	public void setEstadoEstudio(String estadoEstudio) {
		this.estadoEstudio = estadoEstudio;
		if(this.estadoEstudio.equals("Seleccione")){
			this.estadoEstudio = null;
		}
	}

	/**
	 * @return the entidad
	 */
	public String getEntidad() {
		
		return entidad;
	}

	/**
	 * @param entidad
	 *            the entidad to set
	 */
	public void setEntidad(String entidad) {
		this.entidad = entidad;
		if(entidad.equals("Seleccione"))
			this.entidad=null;
	}

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
	}

}
