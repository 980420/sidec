//CU 21 Visualizar estado de monitoreo 
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.List;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.ListadoControler_ensayo;
import gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.VariableConjuntoDatos;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.NotaGeneral_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

@Name("visualizarEstadoMonitoreo")
@Scope(ScopeType.CONVERSATION)
public class VisualizarEstadoMonitoreo {

	/*
	 * private static final String EJBQL =
	 * "select sujeto from Sujeto_ensayo sujeto " +
	 * "where sujeto.eliminado = false and sujeto.grupoSujetos.id = #{visualizarEstadoMonitoreo.idGrupo} "
	 * +
	 * "and sujeto.entidad = #{visualizarEstadoMonitoreo.getHospitalActivo()} ";
	 */

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	@In
	private Usuario user;

	private boolean gestionar = false;
	private boolean inicializado = false;

	private Sujeto_ensayo sujeto;
	private ArrayList<Integer> listaCant;
	private Long idSujeto;
	private Long idGrupo;
	private Long ideliminarNota;

	private String tabSeleccionado = "tabMonitoreo";

	List<NotaGeneral_ensayo> listaNotasGenerales;
	ListadoControler_ensayo<NotaGeneral_ensayo> listaNotas;
	List<Sujeto_ensayo> listaSujetosNotasMon;
	List<Sujeto_ensayo> listaSujetosNotaSitio;
	List<MomentoSeguimientoGeneral_ensayo> listaMomentos;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		SujetosConNotasMonitoreo();
		SujetosConNotasSitio();
		listadoMomentosXSujeto();
		// notaGenerales();
		this.inicializado = true;
	}

	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}

	// Notas generales
	@SuppressWarnings("unchecked")
	public ListadoControler_ensayo<NotaGeneral_ensayo> notaGenerales() {
		listaNotasGenerales = new ArrayList<NotaGeneral_ensayo>();
		listaNotasGenerales = (List<NotaGeneral_ensayo>) entityManager
				.createQuery(
						"select notaG from NotaGeneral_ensayo notaG where notaG.entidad=:Entidad and notaG.eliminado='FALSE' ")
				.setParameter("Entidad", getHospitalActivo()).getResultList();
		listaNotas = new ListadoControler_ensayo<NotaGeneral_ensayo>(
				listaNotasGenerales);
		return listaNotas;
	}

	@SuppressWarnings("unchecked")
	public List<MomentoSeguimientoGeneral_ensayo> listadoMomentosXSujeto() {
		listaMomentos = new ArrayList<MomentoSeguimientoGeneral_ensayo>();
		listaMomentos = (List<MomentoSeguimientoGeneral_ensayo>) entityManager
				.createQuery(
						"select momento from MomentoSeguimientoGeneral_ensayo momento where momento.cronograma.grupoSujetos.id=:GrupoId")
				.setParameter("GrupoId", this.idGrupo).getResultList();

		return listaMomentos;
	}

	@SuppressWarnings("unchecked")
	public List<MomentoSeguimientoEspecifico_ensayo> esMomentoDeSujeto(
			MomentoSeguimientoGeneral_ensayo momento, Sujeto_ensayo sujeto) {
		List<MomentoSeguimientoEspecifico_ensayo> momentoEs = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
		try {
			momentoEs = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
					.createQuery(
							"select momentoEsp from MomentoSeguimientoEspecifico_ensayo momentoEsp where momentoEsp.momentoSeguimientoGeneral=:Momento and momentoEsp.sujeto=:Sujeto")
					.setParameter("Momento", momento)
					.setParameter("Sujeto", sujeto).getResultList();

		} catch (Exception e) {
			return null;
		}
		return momentoEs;
	}

	// CU 38 Eliminar nota general
	public void EliminarInstanciaNota() {
		try {
			NotaGeneral_ensayo notaEliminar = entityManager.find(
					NotaGeneral_ensayo.class, this.ideliminarNota);
			notaEliminar.setEliminado(true);
			// entityManager.refresh(notaEliminar);
			entityManager.persist(notaEliminar);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unchecked")
	public List<NotaGeneral_ensayo> DevolverNotaGeneralAsociadas(
			NotaGeneral_ensayo Nota) {
		List<NotaGeneral_ensayo> listaHijas = new ArrayList<NotaGeneral_ensayo>();
		listaHijas = (List<NotaGeneral_ensayo>) entityManager
				.createQuery(
						"select nota from NotaGeneral_ensayo nota where nota.notaGeneralPadre=:notaPadre and nota.eliminado = 'FALSE' ")
				.setParameter("notaPadre", Nota).getResultList();

		return listaHijas;
	}

	// Sujetos con notas de monitoreo
	@SuppressWarnings("unchecked")
	public List<Sujeto_ensayo> SujetosConNotasMonitoreo() {
		listaSujetosNotasMon = new ArrayList<Sujeto_ensayo>();

		if (this.getHospitalActivo()
				.getTipoEntidad()
				.getValor()
				.equals(SeamResourceBundle.getBundle().getString(
						"bioTecnologica"))) {
			String queryNotas = "select distinct sujeto "
					+ "from Nota_ensayo notaEnsayo "
					+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
					+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
					+ "inner join momento.sujeto sujeto "
					+ "where sujeto.grupoSujetos.id=:idGrupo and notaEnsayo.notaSitio='FALSE' and sujeto.eliminado='FALSE' and notaEnsayo.eliminado='FALSE' and notaEnsayo.notaPadre = null ";

			listaSujetosNotasMon = entityManager.createQuery(queryNotas)
					.setParameter("idGrupo", this.idGrupo)
					.getResultList();

		} else {
			String queryNotas = "select distinct sujeto "
					+ "from Nota_ensayo notaEnsayo "
					+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
					+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
					+ "inner join momento.sujeto sujeto "
					+ "where sujeto.grupoSujetos.id=:idGrupo and sujeto.entidad=:Entidad and notaEnsayo.notaSitio='FALSE' and sujeto.eliminado='FALSE' and notaEnsayo.eliminado='FALSE' and notaEnsayo.notaPadre = null ";

			listaSujetosNotasMon = entityManager.createQuery(queryNotas)
					.setParameter("idGrupo", this.idGrupo)
					.setParameter("Entidad", getHospitalActivo())
					.getResultList();
		}

		return listaSujetosNotasMon;
	}

	// METODOS---------------------------------------------------------------------------------
	// guarda el tab seleccionado
	public void cambiarTabSeleccionado(String tabSeleccionado) {
		this.tabSeleccionado = tabSeleccionado;
	}

	// Sujetos con notas de sitio
	@SuppressWarnings("unchecked")
	public List<Sujeto_ensayo> SujetosConNotasSitio() {
		listaSujetosNotaSitio = new ArrayList<Sujeto_ensayo>();
		
		if (this.getHospitalActivo()
				.getTipoEntidad()
				.getValor()
				.equals(SeamResourceBundle.getBundle().getString(
						"bioTecnologica"))) {


			String queryNotas = "select distinct sujeto "
					+ "from Nota_ensayo notaEnsayo "
					+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
					+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
					+ "inner join momento.sujeto sujeto "
					+ "where sujeto.grupoSujetos.id=:idGrupo and notaEnsayo.notaSitio='TRUE' and sujeto.eliminado='FALSE' and notaEnsayo.eliminado='FALSE' ";

			listaSujetosNotaSitio = entityManager.createQuery(queryNotas)
					.setParameter("idGrupo", this.idGrupo)
					.getResultList();
		}else{
			String queryNotas = "select distinct sujeto "
					+ "from Nota_ensayo notaEnsayo "
					+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
					+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
					+ "inner join momento.sujeto sujeto "
					+ "where sujeto.grupoSujetos.id=:idGrupo and sujeto.entidad=:Entidad and notaEnsayo.notaSitio='TRUE' and sujeto.eliminado='FALSE' and notaEnsayo.eliminado='FALSE' ";

			listaSujetosNotaSitio = entityManager.createQuery(queryNotas)
					.setParameter("idGrupo", this.idGrupo)
					.setParameter("Entidad", getHospitalActivo()).getResultList();
		}

		

		return listaSujetosNotaSitio;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Integer> CantidadNotasMonitoreo(
			MomentoSeguimientoGeneral_ensayo momento, Sujeto_ensayo sujeto) {
		listaCant = new ArrayList<Integer>();
		List<Nota_ensayo> listaNotas = new ArrayList<Nota_ensayo>();

		String queryNotas = "select notaEnsayo "
				+ "from Nota_ensayo notaEnsayo "
				+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
				+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
				+ "where momento.momentoSeguimientoGeneral=:Momento and momento.sujeto=:Sujeto and notaEnsayo.notaSitio='FALSE' and notaEnsayo.eliminado='FALSE' and notaEnsayo.notaPadre = null ";

		listaNotas = entityManager.createQuery(queryNotas)
				.setParameter("Momento", momento)
				.setParameter("Sujeto", sujeto).getResultList();
		int contNuevas = 0;
		int contResueltas = 0;
		int contCerradas = 0;
		int contActualizadas = 0;
		for (int i = 0; i < listaNotas.size(); i++) {
			if (listaNotas.get(i).getEstadoNota().getCodigo() == 1) {
				contNuevas++;
			} else if (listaNotas.get(i).getEstadoNota().getCodigo() == 2) {
				contActualizadas++;
			} else if (listaNotas.get(i).getEstadoNota().getCodigo() == 3) {
				contResueltas++;
			} else {
				contCerradas++;
			}
		}

		listaCant.add(0, contNuevas);
		listaCant.add(1, contActualizadas);
		listaCant.add(2, contResueltas);
		listaCant.add(3, contCerradas);

		return listaCant;
	}

	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> CantidadNotasSitio(
			MomentoSeguimientoGeneral_ensayo momento, Sujeto_ensayo sujeto) {
		List<Nota_ensayo> listaNotas = new ArrayList<Nota_ensayo>();

		String queryNotas = "select notaEnsayo "
				+ "from Nota_ensayo notaEnsayo "
				+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
				+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
				+ "where momento.momentoSeguimientoGeneral=:Momento and momento.sujeto=:Sujeto and notaEnsayo.notaSitio='TRUE' and notaEnsayo.eliminado='FALSE' ";

		listaNotas = entityManager.createQuery(queryNotas)
				.setParameter("Momento", momento)
				.setParameter("Sujeto", sujeto).getResultList();

		return listaNotas;
	}

	public void SeleccionarInstanciaNota(long id) {
		this.setIdeliminarNota(id);
	}

	/*
	 * Devuelve la url del icono para estado pasado por parametro.
	 * 
	 * @parametro: estado, estado del cual vamos a obtener el icono.
	 */
	public String estadoIconMomento(EstadoMomentoSeguimiento_ensayo estado) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modEnsayo/estadosIcon/"
				+ estado.getClass().getSimpleName().split("_")[0] + "/"
				+ estado.getCodigo() + ".png";

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modEnsayo/estados/" + "generic.png";

	}

	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}

	public String estadoMomentoMonitoreo(EstadoMonitoreo_ensayo estado) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modEnsayo/estadosIcon/"
				+ estado.getClass().getSimpleName().split("_")[0] + "/"
				+ estado.getCodigo() + ".gif";

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modEnsayo/estados/" + "generic.png";

	}

	@SuppressWarnings("unchecked")
	public boolean ObtenerCRD() {
		List<CrdEspecifico_ensayo> Listcrd = new ArrayList<CrdEspecifico_ensayo>();
		boolean completa = false;
		String queryCRD = "select CrdEspecifico "
				+ "from CrdEspecifico_ensayo CrdEspecifico "
				+ "inner join CrdEspecifico.momentoSeguimientoEspecifico momentoEspecifico "
				+ "inner join momentoEspecifico.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "inner join momentoEspecifico.sujeto sujeto "
				+ "where sujeto.id=:idSujeto and momentoSeguimientoGeneral.nombre = 'Evaluaci\u00F3n Inicial' ";

		Listcrd = (List<CrdEspecifico_ensayo>) entityManager
				.createQuery(queryCRD).setParameter("idSujeto", this.idSujeto)
				.getResultList();
		if (Listcrd.size() == 0) {
			completa = true;
		}

		for (int i = 0; i < Listcrd.size(); i++) {
			if (Listcrd.get(i).getEstadoHojaCrd().getCodigo() != 1
					&& Listcrd.get(i).getEstadoHojaCrd().getCodigo() != 3) {
				completa = true;
				break;
			}
		}

		return completa;
	}

	public Long getidSujeto() {
		return idSujeto;
	}

	public void setidSujeto(Long idS) {
		this.idSujeto = idS;
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public ArrayList<Integer> getListaCant() {
		return listaCant;
	}

	public void setListaCant(ArrayList<Integer> listaCant) {
		this.listaCant = listaCant;
	}

	public Long getIdeliminarNota() {
		return ideliminarNota;
	}

	public void setIdeliminarNota(Long ideliminarNota) {
		this.ideliminarNota = ideliminarNota;
	}

	public String getTabSeleccionado() {
		return tabSeleccionado;
	}

	public void setTabSeleccionado(String tabSeleccionado) {
		this.tabSeleccionado = tabSeleccionado;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public List<NotaGeneral_ensayo> getListaNotasGenerales() {
		return listaNotasGenerales;
	}

	public void setListaNotasGenerales(
			List<NotaGeneral_ensayo> listaNotasGenerales) {
		this.listaNotasGenerales = listaNotasGenerales;
	}

	public List<Sujeto_ensayo> getListaSujetosNotasMon() {
		return listaSujetosNotasMon;
	}

	public void setListaSujetosNotasMon(List<Sujeto_ensayo> listaSujetosNotasMon) {
		this.listaSujetosNotasMon = listaSujetosNotasMon;
	}

	public List<Sujeto_ensayo> getListaSujetosNotaSitio() {
		return listaSujetosNotaSitio;
	}

	public void setListaSujetosNotaSitio(
			List<Sujeto_ensayo> listaSujetosNotaSitio) {
		this.listaSujetosNotaSitio = listaSujetosNotaSitio;
	}

	public List<MomentoSeguimientoGeneral_ensayo> getListaMomentos() {
		return listaMomentos;
	}

	public void setListaMomentos(
			List<MomentoSeguimientoGeneral_ensayo> listaMomentos) {
		this.listaMomentos = listaMomentos;
	}

	public ListadoControler_ensayo<NotaGeneral_ensayo> getListaNotas() {
		return listaNotas;
	}

	public void setListaNotas(
			ListadoControler_ensayo<NotaGeneral_ensayo> listaNotas) {
		this.listaNotas = listaNotas;
	}

}
