package gehos.ensayo.ensayo_disenno.gestionarEstudio;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.shell.ModSelectorController;
import gehos.ensayo.entity.Diccionarios_ensayo;
import gehos.ensayo.entity.EAjusteTemporal_ensayo;
import gehos.ensayo.entity.EAleatorizacion_ensayo;
import gehos.ensayo.entity.EAsignacion_ensayo;
import gehos.ensayo.entity.EControl_ensayo;
import gehos.ensayo.entity.EEnfermedadCie_ensayo;
import gehos.ensayo.entity.EEnmascaramiento_ensayo;
import gehos.ensayo.entity.EFaseEstudio_ensayo;
import gehos.ensayo.entity.EIntervencion_ensayo;
import gehos.ensayo.entity.EProducto_ensayo;
import gehos.ensayo.entity.EProposito_ensayo;
import gehos.ensayo.entity.EPuntoFinal_ensayo;
import gehos.ensayo.entity.ESeleccion_ensayo;
import gehos.ensayo.entity.ESexo_ensayo;
import gehos.ensayo.entity.ETipoIntervencion_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.SessionBean;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.swing.plaf.basic.ComboPopup;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("verEstudioControlador")
@Scope(ScopeType.CONVERSATION)
public class verEstudioControlador {

	@In
	FacesMessages facesMessages;
	@In
	EntityManager entityManager;

	@In
	IBitacora bitacora;
	String desde = "";

	private String seleccione;
	private String comboduracion;

	private boolean mostrarFormulario;

	private Long totalEstudiosActivos;
	private Long totalSujetosEnsayo;
	private Long totalUsuarioPromotor;
	private Long totalInvestigadores;
	private Long totalMonitores;
	public static Long idEntidad = null;

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		this.seleccione = SeamResourceBundle.getBundle()
				.getString("seleccione");

		this.mostrarFormulario = false;

		actualizarTotalEstudios();
		actualizarTotalSujetoEnsayo();
		actualizarUsuarioPromotor();
		actualizarTotalInvestigadores();
		actualizarTotalMonitores();
	}

	private Estudio_ensayo estudio = new Estudio_ensayo();
	private UsuarioEstudio_ensayo usuarioestudio;
	private EstudioEntidad_ensayo entidadensayo;
	private Long idestudioelim;

	private EstadoEstudio_ensayo estado;
	private List<EstadoEstudio_ensayo> listadeestado = new ArrayList<EstadoEstudio_ensayo>();
	private List<EstudioEntidad_ensayo> listadeentidadensayo = new ArrayList<EstudioEntidad_ensayo>();
	private List<Entidad_ensayo> listaEntidadTarget = new ArrayList<Entidad_ensayo>();

	private List<UsuarioEstudio_ensayo> listadeusuarioestudio = new ArrayList<UsuarioEstudio_ensayo>();

	private List<EAleatorizacion_ensayo> listadalateorizacion = new ArrayList<EAleatorizacion_ensayo>();
	private List<EEnmascaramiento_ensayo> listadenmascaramiento = new ArrayList<EEnmascaramiento_ensayo>();
	private List<EControl_ensayo> listadcontrol = new ArrayList<EControl_ensayo>();
	private List<EProposito_ensayo> listaproposito = new ArrayList<EProposito_ensayo>();
	private List<EProposito_ensayo> listapropositoobservacional = new ArrayList<EProposito_ensayo>();

	private List<EAsignacion_ensayo> listaasignacion = new ArrayList<EAsignacion_ensayo>();
	private List<EPuntoFinal_ensayo> listapuntofinal = new ArrayList<EPuntoFinal_ensayo>();
	private List<EIntervencion_ensayo> listaintervencion = new ArrayList<EIntervencion_ensayo>();
	private List<EFaseEstudio_ensayo> listafase = new ArrayList<EFaseEstudio_ensayo>();
	private List<ESexo_ensayo> listasexo = new ArrayList<ESexo_ensayo>();
	private List<ESeleccion_ensayo> listaseleccion = new ArrayList<ESeleccion_ensayo>();
	private List<EAjusteTemporal_ensayo> listajustetemporal = new ArrayList<EAjusteTemporal_ensayo>();

	private List<ETipoIntervencion_ensayo> listatipointervencion = new ArrayList<ETipoIntervencion_ensayo>();

	private String aleatorizacion = "";
	private String enmascaramiento = "";
	private String control = "";

	private String asignacion = "";
	private String puntofinal = "";
	private String intervencion = "";
	private String fase = "";
	private String sexo = "";
	private String diccionario = "";

	private String seleccion = "";
	private String ajustetemporal = "";
	private String proposito = "";
	private String otroproposito = "";

	private String otrafase = "";

	private EIntervencion_ensayo intervencionadd;

	// add Evelio
	private List<EProducto_ensayo> listaProductos = new ArrayList<EProducto_ensayo>();

	private String combo = "inter";

	private boolean combointer = true; // Si true muestra combo madre
	private boolean comboobser = false; // Si true muestra combo padre

	public void showCombo() {
		if (combo.equals("inter")) {
			setCombointer(true);
			setComboobser(false);
		} else if (combo.equals("obser")) {
			setComboobser(true);
			setCombointer(false);
		}
	}

	private String combofasedef = "fase";

	private boolean combofase = true; // Si true muestra combo madre
	private boolean combootrafase = false; // Si true muestra combo padre

	public void showComboFase() {
		if (combofasedef.equals("fase")) {
			setCombofase(true);
			setCombootrafase(false);
		} else if (combofasedef.equals("otrafase")) {
			setCombootrafase(true);
			setCombofase(false);
		}
	}

	private String combopropositodef = "propositos";
	private boolean comboproposito = true; // Si true muestra combo madre
	private boolean combootrtroproposito = false; // Si true muestra combo padre

	public void showComboProposito() {
		if (combopropositodef.equals("propositos")) {
			setComboproposito(true);
			setCombootrtroproposito(false);
		} else if (combopropositodef.equals("otropropositos")) {
			setComboproposito(false);
			setCombootrtroproposito(true);
		}
	}

	private String tipointervencion = "";
	private String nombreintervencion = "";

	private String estadoestudio = "";

	private Long estudyId;

	public EstadoEstudio_ensayo estadoseleccionado() {
		try {
			EstadoEstudio_ensayo estadoselecconado = new EstadoEstudio_ensayo();
			estadoselecconado = (EstadoEstudio_ensayo) entityManager
					.createQuery(
							"select e from EstadoEstudio_ensayo e where e.nombre= :valor")
					.setParameter("valor", estadoestudio).getSingleResult();
			return estadoselecconado;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public String devolverTitulo() {
		String nombre = "";
		if (this.desde.equals("listado")) {
			nombre = SeamResourceBundle.getBundle().getString("verDatos");
		} else {
			nombre = SeamResourceBundle.getBundle().getString("verDetalles");
		}
		return nombre;
	}

	@SuppressWarnings("unchecked")
	public void iniVerestudio() {
		try {

			intervencionadd = new EIntervencion_ensayo();

			estudio = entityManager.find(Estudio_ensayo.class, estudyId);
			if (estudio.getDuracion().equals("Transversal")) {
				comboduracion = "transversal";
			} else {
				comboduracion = "longitudinal";
			}
			if (estudio.getETipoProtocolo().getNombre() != null
					&& estudio.getETipoProtocolo().getNombre()
							.equals("Observacional")) {
				combo = "obser";
				this.combointer = false;
				this.comboobser = true;
			}
			if (estudio.getTipofase().equals("OtraFase")) {
				combofasedef = "otrafase";
				combofase = false;
				combootrafase = true;
			}
			if (estudio.getTipoproposito().equals("OtraPropositos")) {
				combopropositodef = "otropropositos";
				comboproposito = false;
				combootrtroproposito = true;
			}
			// add Evelio
			listaProductos = entityManager
					.createQuery(
							"Select eProducto from EProducto_ensayo eProducto "
									+ "where eProducto.estudio.id =:id "
									+ "and eProducto.eliminado <> true")
					.setParameter("id", estudio.getId()).getResultList();

			listadeentidadensayo = entityManager
					.createQuery(
							"Select ee from EstudioEntidad_ensayo ee "
									+ "JOIN ee.entidad ent "
									+ "JOIN ee.estudio e " + "where e.id =:id "
									+ "and ee.eliminado <> true "
									+ "and ent.eliminado <> true "
									+ "and e.eliminado <> true "
									+ "order by ent.nombre")
					.setParameter("id", estudio.getId()).getResultList();

			listadeusuarioestudio = entityManager
					.createQuery(
							"Select ue from UsuarioEstudio_ensayo ue "
									+ "JOIN ue.usuario u "
									+ "JOIN ue.estudioEntidad ee "
									+ "JOIN ee.estudio e " + "where e.id =:id "
									+ "and ee.eliminado <> true "
									+ "and ue.eliminado <> true "
									+ "and u.eliminado <> true "
									+ "and e.eliminado <> true "
									+ "order by u.username")
					.setParameter("id", estudio.getId()).getResultList();

			listadeestado = (List<EstadoEstudio_ensayo>) entityManager
					.createQuery("select e from EstadoEstudio_ensayo e ")
					.getResultList();

			listadalateorizacion = (List<EAleatorizacion_ensayo>) entityManager
					.createQuery("select a from EAleatorizacion_ensayo a ")
					.getResultList();
			listadenmascaramiento = (List<EEnmascaramiento_ensayo>) entityManager
					.createQuery("select a from EEnmascaramiento_ensayo a ")
					.getResultList();
			listadcontrol = (List<EControl_ensayo>) entityManager.createQuery(
					"select a from EControl_ensayo a ").getResultList();
			listaproposito = (List<EProposito_ensayo>) entityManager
					.createQuery(
							"select a from EProposito_ensayo a where a.ETipoProtocolo.id=1")
					.getResultList();

			listapropositoobservacional = (List<EProposito_ensayo>) entityManager
					.createQuery(
							"select a from EProposito_ensayo a where a.ETipoProtocolo.id=2")
					.getResultList();
			listaseleccion = (List<ESeleccion_ensayo>) entityManager
					.createQuery("select a from ESeleccion_ensayo a ")
					.getResultList();
			listajustetemporal = (List<EAjusteTemporal_ensayo>) entityManager
					.createQuery("select a from EAjusteTemporal_ensayo a ")
					.getResultList();
			listaasignacion = (List<EAsignacion_ensayo>) entityManager
					.createQuery("select a from EAsignacion_ensayo a ")
					.getResultList();
			listaintervencion = (List<EIntervencion_ensayo>) entityManager
					.createQuery(
							"select a from EIntervencion_ensayo a where a.estudio.id =:ide and a.eliminado=false ")
					.setParameter("ide", estudio.getId()).getResultList();
			listapuntofinal = (List<EPuntoFinal_ensayo>) entityManager
					.createQuery("select a from EPuntoFinal_ensayo a ")
					.getResultList();
			listafase = (List<EFaseEstudio_ensayo>) entityManager.createQuery(
					"select a from EFaseEstudio_ensayo a ").getResultList();
			listasexo = (List<ESexo_ensayo>) entityManager.createQuery(
					"select a from ESexo_ensayo a ").getResultList();
			this.diccionario= this.estudio.getDiccionario().getNombre();
			listatipointervencion = (List<ETipoIntervencion_ensayo>) entityManager
					.createQuery("select a from ETipoIntervencion_ensayo a ")
					.getResultList();

			if (estudio.getEstadoEstudio() != null) {
				estadoestudio = estudio.getEstadoEstudio().getNombre();
			}
			if (estudio.getEAleatorizacion() != null) {
				aleatorizacion = estudio.getEAleatorizacion().getNombre();
			}
			if (estudio.getEEnmascaramiento() != null) {
				enmascaramiento = estudio.getEEnmascaramiento().getNombre();
			}
			if (estudio.getEControl() != null) {
				control = estudio.getEControl().getNombre();
			}

			if (estudio.getEAsignacion() != null) {
				asignacion = estudio.getEAsignacion().getNombre();
			}
			if (estudio.getEPuntoFinal() != null) {
				puntofinal = estudio.getEPuntoFinal().getNombre();
			}
			/*
			 * if(estudio.getEIntervencion()!=null){
			 * intervencion=estudio.getEIntervencion().getNombre();}
			 */
			if (estudio.getEFaseEstudio() != null
					&& estudio.getTipofase().equals("Fase")) {
				fase = estudio.getEFaseEstudio().getNombre();
				otrafase = "-";
			} else if (estudio.getEFaseEstudio() != null
					&& estudio.getTipofase().equals("OtraFase")
					&& estudio.getOtrosFase() != null) {
				fase = "<Seleccione>";
				otrafase = estudio.getOtrosFase();
			} else if (estudio.getEFaseEstudio() == null
					&& estudio.getOtrosFase() != null) {
				otrafase = estudio.getOtrosFase();
			}

			if (estudio.getEProposito() != null
					&& estudio.getTipoproposito().equals("Propositos")) {
				proposito = estudio.getEProposito().getNombre();
				otroproposito = "-";
			} else if (estudio.getEProposito() != null
					&& estudio.getTipoproposito().equals("OtraPropositos")
					&& estudio.getOtroProposito() != null) {
				proposito = "<Seleccione>";
				otroproposito = estudio.getOtroProposito();
			} else if (estudio.getEProposito() == null
					&& estudio.getOtroProposito() != null) {
				otroproposito = estudio.getOtroProposito();
			}
			if (estudio.getESexo() != null) {
				sexo = estudio.getESexo().getValor();
			}
			if (estudio.getDiccionario() != null){
				diccionario = estudio.getDiccionario().getNombre();
			}

			if (estudio.getEAjusteTemporal() != null) {
				ajustetemporal = estudio.getEAjusteTemporal().getNombre();
			}
			if (estudio.getESeleccion() != null) {
				seleccion = estudio.getESeleccion().getNombre();
			}

		} catch (Exception e) {
			return;
		}
	}

	public List<String> ListaEstados() {
		List<String> listaestadosS = new ArrayList<String>();

		for (int i = 0; i < listadeestado.size(); i++) {
			listaestadosS.add(listadeestado.get(i).getNombre());
		}
		listaestadosS.add("<Seleccione>");
		return listaestadosS;

	}

	public List<String> ListaAlateorizacion() {
		List<String> listadalateorizacionS = new ArrayList<String>();

		for (int i = 0; i < listadalateorizacion.size(); i++) {
			listadalateorizacionS.add(listadalateorizacion.get(i).getNombre());
		}
		listadalateorizacionS.add("<Seleccione>");
		return listadalateorizacionS;

	}

	public List<String> ListaEEnmascaramiento() {
		List<String> listadenmascaramientoS = new ArrayList<String>();

		for (int i = 0; i < listadenmascaramiento.size(); i++) {
			listadenmascaramientoS
					.add(listadenmascaramiento.get(i).getNombre());
		}
		listadenmascaramientoS.add("<Seleccione>");
		return listadenmascaramientoS;

	}

	public List<String> ListaEControl() {
		List<String> listadEControlS = new ArrayList<String>();

		for (int i = 0; i < listadcontrol.size(); i++) {
			listadEControlS.add(listadcontrol.get(i).getNombre());
		}
		listadEControlS.add("<Seleccione>");
		return listadEControlS;

	}

	public List<String> ListaEProposito() {
		List<String> listadEPropositoS = new ArrayList<String>();

		for (int i = 0; i < listaproposito.size(); i++) {
			listadEPropositoS.add(listaproposito.get(i).getNombre());
		}
		listadEPropositoS.add("<Seleccione>");
		return listadEPropositoS;

	}

	public List<String> ListaEPropositoObservacional() {
		List<String> listadEPropositoS = new ArrayList<String>();

		for (int i = 0; i < listapropositoobservacional.size(); i++) {
			listadEPropositoS.add(listapropositoobservacional.get(i)
					.getNombre());
		}
		listadEPropositoS.add("<Seleccione>");
		return listadEPropositoS;

	}

	public List<String> ListaEAsignacion() {
		List<String> listadAsignacionS = new ArrayList<String>();

		for (int i = 0; i < listaasignacion.size(); i++) {
			listadAsignacionS.add(listaasignacion.get(i).getNombre());
		}
		listadAsignacionS.add("<Seleccione>");
		return listadAsignacionS;

	}

	public List<String> ListaEPuntofinal() {
		List<String> listadEPuntofinalS = new ArrayList<String>();

		for (int i = 0; i < listapuntofinal.size(); i++) {
			listadEPuntofinalS.add(listapuntofinal.get(i).getNombre());
		}
		listadEPuntofinalS.add("<Seleccione>");
		return listadEPuntofinalS;

	}

	public List<String> ListaEIntervenciones() {
		List<String> listadEIntervencionesS = new ArrayList<String>();

		for (int i = 0; i < listaintervencion.size(); i++) {
			listadEIntervencionesS.add(listaintervencion.get(i).getNombre());
		}
		listadEIntervencionesS.add("<Seleccione>");
		return listadEIntervencionesS;

	}

	public List<String> ListaEFase() {
		List<String> listadEFaseS = new ArrayList<String>();

		for (int i = 0; i < listafase.size(); i++) {
			listadEFaseS.add(listafase.get(i).getNombre());
		}
		listadEFaseS.add("<Seleccione>");
		return listadEFaseS;

	}

	public List<String> ListaESexo() {
		List<String> listadESexoS = new ArrayList<String>();

		for (int i = 0; i < listasexo.size(); i++) {
			listadESexoS.add(listasexo.get(i).getValor());
		}
		listadESexoS.add("<Seleccione>");
		return listadESexoS;

	}
	
	

	public List<String> ListaESeleccion() {
		List<String> listadESeleccionS = new ArrayList<String>();

		for (int i = 0; i < listaseleccion.size(); i++) {
			listadESeleccionS.add(listaseleccion.get(i).getNombre());
		}
		listadESeleccionS.add("<Seleccione>");
		return listadESeleccionS;

	}

	public List<String> ListaEAjusteTemporal() {
		List<String> listadEAjusteTemporalS = new ArrayList<String>();

		for (int i = 0; i < listajustetemporal.size(); i++) {
			listadEAjusteTemporalS.add(listajustetemporal.get(i).getNombre());
		}
		listadEAjusteTemporalS.add("<Seleccione>");
		return listadEAjusteTemporalS;

	}

	public List<String> ListaETipoIntervencion() {
		List<String> listatipointervencionS = new ArrayList<String>();

		for (int i = 0; i < listatipointervencion.size(); i++) {
			listatipointervencionS
					.add(listatipointervencion.get(i).getNombre());
		}
		listatipointervencionS.add("<Seleccione>");
		return listatipointervencionS;

	}

	public EstadoEstudio_ensayo Estadoseleccionado(
			String estadoseleccionadovista) {
		try {
			EstadoEstudio_ensayo estadoseleccionado = new EstadoEstudio_ensayo();
			estadoseleccionado = (EstadoEstudio_ensayo) entityManager
					.createQuery(
							"select e from EstadoEstudio_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", estadoseleccionadovista)
					.getSingleResult();
			return estadoseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EAleatorizacion_ensayo Aleatorizacionseleccionado(
			String aleatorizacionseleccionadovista) {
		try {
			EAleatorizacion_ensayo aleatorizacionseleccionado = new EAleatorizacion_ensayo();
			aleatorizacionseleccionado = (EAleatorizacion_ensayo) entityManager
					.createQuery(
							"select e from EAleatorizacion_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", aleatorizacionseleccionadovista)
					.getSingleResult();
			return aleatorizacionseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EControl_ensayo EControLSeleccionado(String controlseleccionadovista) {
		try {
			EControl_ensayo controlseleccionado = new EControl_ensayo();
			controlseleccionado = (EControl_ensayo) entityManager
					.createQuery(
							"select e from EControl_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", controlseleccionadovista)
					.getSingleResult();
			return controlseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EEnmascaramiento_ensayo EEnmascaramientoSeleccionado(
			String enmascaramientoseleccionadovista) {
		try {
			EEnmascaramiento_ensayo enmascaramientoseleccionado = new EEnmascaramiento_ensayo();
			enmascaramientoseleccionado = (EEnmascaramiento_ensayo) entityManager
					.createQuery(
							"select e from EEnmascaramiento_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", enmascaramientoseleccionadovista)
					.getSingleResult();
			return enmascaramientoseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EProposito_ensayo EPropositoSeleccionado(
			String eropositoseleccionadovista) {
		try {
			EProposito_ensayo eropositoseleccionado = new EProposito_ensayo();
			eropositoseleccionado = (EProposito_ensayo) entityManager
					.createQuery(
							"select e from EProposito_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", eropositoseleccionadovista)
					.getSingleResult();
			return eropositoseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EAsignacion_ensayo EAsignacionSeleccionado(
			String asignacionseleccionadovista) {
		try {
			EAsignacion_ensayo asignacionseleccionado = new EAsignacion_ensayo();
			asignacionseleccionado = (EAsignacion_ensayo) entityManager
					.createQuery(
							"select e from EAsignacion_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", asignacionseleccionadovista)
					.getSingleResult();
			return asignacionseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EPuntoFinal_ensayo EPuntoFinalSeleccionado(
			String puntofinalseleccionadovista) {
		try {
			EPuntoFinal_ensayo puntofinalseleccionado = new EPuntoFinal_ensayo();
			puntofinalseleccionado = (EPuntoFinal_ensayo) entityManager
					.createQuery(
							"select e from EPuntoFinal_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", puntofinalseleccionadovista)
					.getSingleResult();
			return puntofinalseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EIntervencion_ensayo EIntervencionSeleccionado(
			String intervencionseleccionadovista) {
		try {
			EIntervencion_ensayo intervencionseleccionado = new EIntervencion_ensayo();
			intervencionseleccionado = (EIntervencion_ensayo) entityManager
					.createQuery(
							"select e from EIntervencion_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", intervencionseleccionadovista)
					.getSingleResult();
			return intervencionseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EFaseEstudio_ensayo EFaseEstudioSeleccionado(
			String faseEstudioseleccionadovista) {
		try {
			EFaseEstudio_ensayo faseEstudioseleccionado = new EFaseEstudio_ensayo();
			faseEstudioseleccionado = (EFaseEstudio_ensayo) entityManager
					.createQuery(
							"select e from EFaseEstudio_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", faseEstudioseleccionadovista)
					.getSingleResult();
			return faseEstudioseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public ESexo_ensayo ESexoSeleccionado(String sexoseleccionadovista) {
		try {
			ESexo_ensayo sexoseleccionado = new ESexo_ensayo();
			sexoseleccionado = (ESexo_ensayo) entityManager
					.createQuery(
							"select e from ESexo_ensayo e where e.valor =:nombre ")
					.setParameter("nombre", sexoseleccionadovista)
					.getSingleResult();
			return sexoseleccionado;

		} catch (Exception e) {
			return null;
		}
	}
	
	public Diccionarios_ensayo diccionarioSeleccionado(String diccionarioseleccionadovista) {
		try {
			Diccionarios_ensayo diccionarioseleccionado = new Diccionarios_ensayo();
			diccionarioseleccionado = (Diccionarios_ensayo) entityManager
					.createQuery(
							"select e from Diccionario_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", diccionarioseleccionadovista)
					.getSingleResult();
			return diccionarioseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public ETipoIntervencion_ensayo ETipoIntervencionSeleccionado(
			String tipoIntervencionseleccionadovista) {
		try {
			ETipoIntervencion_ensayo tipoIntervencionseleccionado = new ETipoIntervencion_ensayo();
			tipoIntervencionseleccionado = (ETipoIntervencion_ensayo) entityManager
					.createQuery(
							"select e from ETipoIntervencion_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", tipoIntervencionseleccionadovista)
					.getSingleResult();
			return tipoIntervencionseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public ESeleccion_ensayo ESeleccionSeleccionado(
			String seleccionseleccionadovista) {
		try {
			ESeleccion_ensayo seleccionseleccionado = new ESeleccion_ensayo();
			seleccionseleccionado = (ESeleccion_ensayo) entityManager
					.createQuery(
							"select e from ESeleccion_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", seleccionseleccionadovista)
					.getSingleResult();
			return seleccionseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public EAjusteTemporal_ensayo EAjusteTemporalSeleccionado(
			String ajusteTemporalseleccionadovista) {
		try {
			EAjusteTemporal_ensayo ajusteTemporalseleccionado = new EAjusteTemporal_ensayo();
			ajusteTemporalseleccionado = (EAjusteTemporal_ensayo) entityManager
					.createQuery(
							"select e from EAjusteTemporal_ensayo e where e.nombre =:nombre ")
					.setParameter("nombre", ajusteTemporalseleccionadovista)
					.getSingleResult();
			return ajusteTemporalseleccionado;

		} catch (Exception e) {
			return null;
		}
	}

	public List<EIntervencion_ensayo> ListaIntervencionSinEliminar() {
		try {
			List<EIntervencion_ensayo> listasineliminadotemp = new ArrayList<EIntervencion_ensayo>();
			for (int i = 0; i < listaintervencion.size(); i++) {

				if (!listaintervencion.get(i).getEliminado()) {
					listasineliminadotemp.add(listaintervencion.get(i));
				}
			}
			return listasineliminadotemp;

		} catch (Exception e) {
			return null;
		}

	}

	public List<UsuarioEstudio_ensayo> ListaUsuarioEstudio() {
		try {

			return listadeusuarioestudio;

		} catch (Exception e) {
			return null;
		}

	}

	public List<EstudioEntidad_ensayo> ListaEntidadesEstudio() {
		try {

			return listadeentidadensayo;

		} catch (Exception e) {
			return null;
		}

	}

	public List<Entidad_ensayo> ListaEntidadesrelacionadas() {
		try {
			List<Entidad_ensayo> listaEntidadTargettemp = new ArrayList<Entidad_ensayo>();
			for (int i = 0; i < listaEntidadTarget.size(); i++) {

				if (!listaEntidadTarget.get(i).getEliminado()) {
					listaEntidadTargettemp.add(listaEntidadTarget.get(i));
				}
			}
			return listaEntidadTargettemp;

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Elimina el estudio desde la vista Ver estudio cuando se crea el estudio o
	 * cuando se modifica
	 * 
	 * @author Tania
	 */
	public void eliminarEstudio() {
		Estudio_ensayo estudioelimino = new Estudio_ensayo();
		estudioelimino = entityManager.find(Estudio_ensayo.class, estudyId);
		estudioelimino.setEliminado(true);
		estudioelimino.setCid(bitacora
				.registrarInicioDeAccion("prm_bitEliminarEst_ens"));
		entityManager.persist(estudioelimino);
		entityManager.flush();
	}

	/* Get and set */
	public Estudio_ensayo getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}

	public void setIdestudioelim(Long idestudioelim) {
		this.idestudioelim = idestudioelim;
	}

	public Long getIdestudioelim() {
		return idestudioelim;
	}

	public EstadoEstudio_ensayo getEstadoEstudio_ensayo() {
		return estado;
	}

	public void setEstadoEstudio_ensayo(EstadoEstudio_ensayo estado) {
		this.estado = estado;
	}

	public void setListadeestado(List<EstadoEstudio_ensayo> listadeestado) {
		this.listadeestado = listadeestado;
	}

	public List<EstadoEstudio_ensayo> getListadeestado() {
		return listadeestado;
	}

	public void setSeleccione(String seleccione) {
		this.seleccione = seleccione;
	}

	public String getSeleccione() {
		return seleccione;
	}

	public void setMostrarFormulario(boolean mostrarFormulario) {
		this.mostrarFormulario = mostrarFormulario;
	}

	public boolean isMostrarFormulario() {
		return mostrarFormulario;
	}

	public void setEstadoestudio(String estadoestudio) {
		this.estadoestudio = estadoestudio;
	}

	public String getEstadoestudio() {
		return estadoestudio;
	}

	public Long getEstudyId() {
		return estudyId;
	}

	public void setEstudyId(Long estudyId) {
		this.estudyId = estudyId;
	}

	public String getCombo() {
		return combo;
	}

	public void setCombo(String combo) {
		this.combo = combo;
	}

	public boolean isCombointer() {
		return combointer;
	}

	public void setCombointer(boolean combointer) {
		this.combointer = combointer;
	}

	public boolean isComboobser() {
		return comboobser;
	}

	public void setComboobser(boolean comboobser) {
		this.comboobser = comboobser;
	}

	public List<EAleatorizacion_ensayo> getListadalateorizacion() {
		return listadalateorizacion;
	}

	public void setListadalateorizacion(
			List<EAleatorizacion_ensayo> listadalateorizacion) {
		this.listadalateorizacion = listadalateorizacion;
	}

	public List<EEnmascaramiento_ensayo> getListadenmascaramiento() {
		return listadenmascaramiento;
	}

	public void setListadenmascaramiento(
			List<EEnmascaramiento_ensayo> listadenmascaramiento) {
		this.listadenmascaramiento = listadenmascaramiento;
	}

	public List<EControl_ensayo> getListadcontrol() {
		return listadcontrol;
	}

	public void setListadcontrol(List<EControl_ensayo> listadcontrol) {
		this.listadcontrol = listadcontrol;
	}

	public List<EProposito_ensayo> getListaproposito() {
		return listaproposito;
	}

	public void setListaproposito(List<EProposito_ensayo> listaproposito) {
		this.listaproposito = listaproposito;
	}

	public List<EAsignacion_ensayo> getListaasignacion() {
		return listaasignacion;
	}

	public void setListaasignacion(List<EAsignacion_ensayo> listaasignacion) {
		this.listaasignacion = listaasignacion;
	}

	public List<EPuntoFinal_ensayo> getListapuntofinal() {
		return listapuntofinal;
	}

	public void setListapuntofinal(List<EPuntoFinal_ensayo> listapuntofinal) {
		this.listapuntofinal = listapuntofinal;
	}

	public List<EIntervencion_ensayo> getListaintervencion() {
		return listaintervencion;
	}

	public void setListaintervencion(
			List<EIntervencion_ensayo> listaintervencion) {
		this.listaintervencion = listaintervencion;
	}

	public List<EFaseEstudio_ensayo> getListafase() {
		return listafase;
	}

	public void setListafase(List<EFaseEstudio_ensayo> listafase) {
		this.listafase = listafase;
	}

	public List<ESexo_ensayo> getListasexo() {
		return listasexo;
	}

	public void setListasexo(List<ESexo_ensayo> listasexo) {
		this.listasexo = listasexo;
	}
	

	public String getAleatorizacion() {
		return aleatorizacion;
	}

	public void setAleatorizacion(String aleatorizacion) {
		this.aleatorizacion = aleatorizacion;
	}

	public String getEnmascaramiento() {
		return enmascaramiento;
	}

	public void setEnmascaramiento(String enmascaramiento) {
		this.enmascaramiento = enmascaramiento;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getProposito() {
		return proposito;
	}

	public void setProposito(String proposito) {
		this.proposito = proposito;
	}

	public String getAsignacion() {
		return asignacion;
	}

	public void setAsignacion(String asignacion) {
		this.asignacion = asignacion;
	}

	public String getPuntofinal() {
		return puntofinal;
	}

	public void setPuntofinal(String puntofinal) {
		this.puntofinal = puntofinal;
	}

	public String getIntervencion() {
		return intervencion;
	}

	public void setIntervencion(String intervencion) {
		this.intervencion = intervencion;
	}

	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getdiccionarios() {
		return diccionario;
	}

	public void setdiccionario(String diccionario) {
		this.diccionario = diccionario;
	}

	public String getTipointervencion() {
		return tipointervencion;
	}

	public void setTipointervencion(String tipointervencion) {
		this.tipointervencion = tipointervencion;
	}

	public String getNombreintervencion() {
		return nombreintervencion;
	}

	public void setNombreintervencion(String nombreintervencion) {
		this.nombreintervencion = nombreintervencion;
	}

	public List<ETipoIntervencion_ensayo> getListatipointervencion() {
		return listatipointervencion;
	}

	public void setListatipointervencion(
			List<ETipoIntervencion_ensayo> listatipointervencion) {
		this.listatipointervencion = listatipointervencion;
	}

	public EIntervencion_ensayo getIntervencionadd() {
		return intervencionadd;
	}

	public void setIntervencionadd(EIntervencion_ensayo intervencionadd) {
		this.intervencionadd = intervencionadd;
	}

	public String getAjustetemporal() {
		return ajustetemporal;
	}

	public void setAjustetemporal(String ajustetemporal) {
		this.ajustetemporal = ajustetemporal;
	}

	public String getSeleccion() {
		return seleccion;
	}

	public void setSeleccion(String seleccion) {
		this.seleccion = seleccion;
	}

	public boolean isCombofase() {
		return combofase;
	}

	public void setCombofase(boolean combofase) {
		this.combofase = combofase;
	}

	public boolean isCombootrafase() {
		return combootrafase;
	}

	public void setCombootrafase(boolean combootrafase) {
		this.combootrafase = combootrafase;
	}

	public String getCombofasedef() {
		return combofasedef;
	}

	public void setCombofasedef(String combofasedef) {
		this.combofasedef = combofasedef;
	}

	public String getOtrafase() {
		return otrafase;
	}

	public void setOtrafase(String otrafase) {
		this.otrafase = otrafase;
	}

	public List<EProposito_ensayo> getListapropositoobservacional() {
		return listapropositoobservacional;
	}

	public void setListapropositoobservacional(
			List<EProposito_ensayo> listapropositoobservacional) {
		this.listapropositoobservacional = listapropositoobservacional;
	}

	public boolean isComboproposito() {
		return comboproposito;
	}

	public void setComboproposito(boolean comboproposito) {
		this.comboproposito = comboproposito;
	}

	public boolean isCombootrtroproposito() {
		return combootrtroproposito;
	}

	public void setCombootrtroproposito(boolean combootrtroproposito) {
		this.combootrtroproposito = combootrtroproposito;
	}

	public String getCombopropositodef() {
		return combopropositodef;
	}

	public void setCombopropositodef(String combopropositodef) {
		this.combopropositodef = combopropositodef;
	}

	public String getOtroproposito() {
		return otroproposito;
	}

	public void setOtroproposito(String otroproposito) {
		this.otroproposito = otroproposito;
	}

	public UsuarioEstudio_ensayo getUsuarioestudio() {
		return usuarioestudio;
	}

	public void setUsuarioestudio(UsuarioEstudio_ensayo usuarioestudio) {
		this.usuarioestudio = usuarioestudio;
	}

	public EstudioEntidad_ensayo getEntidadensayo() {
		return entidadensayo;
	}

	public void setEntidadensayo(EstudioEntidad_ensayo entidadensayo) {
		this.entidadensayo = entidadensayo;
	}

	public List<UsuarioEstudio_ensayo> getListadeusuarioestudio() {
		return listadeusuarioestudio;
	}

	public void setListadeusuarioestudio(
			List<UsuarioEstudio_ensayo> listadeusuarioestudio) {
		this.listadeusuarioestudio = listadeusuarioestudio;
	}

	public List<EstudioEntidad_ensayo> getListadeentidadensayo() {
		return listadeentidadensayo;
	}

	public void setListadeentidadensayo(
			List<EstudioEntidad_ensayo> listadeentidadensayo) {
		this.listadeentidadensayo = listadeentidadensayo;
	}

	public List<Entidad_ensayo> getListaEntidadTarget() {
		return listaEntidadTarget;
	}

	public void setListaEntidadTarget(List<Entidad_ensayo> listaEntidadTarget) {
		this.listaEntidadTarget = listaEntidadTarget;
	}

	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}

	public List<EEnfermedadCie_ensayo> listaEnfermedadesCie() {
		List<EEnfermedadCie_ensayo> listaEnfermedadesCie = new ArrayList<EEnfermedadCie_ensayo>();
		try {
			listaEnfermedadesCie = entityManager
					.createQuery(
							"select enf from EEnfermedadCie_ensayo enf where enf.estudio.id=:id")
					.setParameter("id", estudio.getId()).getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return listaEnfermedadesCie;
	}

	public List<EProducto_ensayo> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<EProducto_ensayo> listaProductos) {
		this.listaProductos = listaProductos;
	}

	public String getComboduracion() {
		return comboduracion;
	}

	public void setComboduracion(String comboduracion) {
		this.comboduracion = comboduracion;
	}


	@WebRemote
	public Long consultaTotalEstudioEnsayo() {

		try {

			return (Long) entityManager
					.createQuery(
							"SELECT COUNT(e) FROM Estudio_ensayo e WHERE e.eliminado = false and e.estadoEstudio.codigo = 3 and e.entidad.id = :idEntidadParam")
					.setParameter("idEntidadParam", idEntidad)
					.getSingleResult();
		} catch (Exception e) {
			// manejar la excepción
			return null;
		}
	}

	public Long consultaTotalSujetosEnsayo() {
		
		BigInteger cantSujetos = (BigInteger) entityManager.createNativeQuery("SELECT ensayo.consulta_total_sujetos_ensayo(:idEntidadParam)")
		          .setParameter("idEntidadParam", idEntidad)
		          .getSingleResult();
		
		return cantSujetos.longValue();		
	}

	public Long consultaTotalUsuariosPromotores() {
		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"SELECT count(DISTINCT ue.id_usuario) FROM ensayo.usuario_estudio ue JOIN ensayo.estudio_entidad ee ON ue.id_estudio = ee.id JOIN comun.entidad e ON ee.id_entidad = e.id "
							+ "JOIN comun.usuario u ON ue.id_usuario = u.id JOIN seguridad.role r ON ue.id_rol = r.id WHERE r.id = 1001000000000001223 AND ue.eliminado = false AND u.eliminado = false AND e.id = :idEntidadParam")
					.setParameter("idEntidadParam", idEntidad)
					.getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error consultaTotalUsuariosPromotores: "
					+ e.getMessage());
			return null;
		}
	}

	private Long consultaTotaInvestigadores() {
		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"SELECT count(DISTINCT ue.id_usuario) FROM ensayo.usuario_estudio ue JOIN ensayo.estudio_entidad ee ON ue.id_estudio = ee.id JOIN comun.entidad e ON ee.id_entidad = e.id "
							+ "JOIN comun.usuario u ON ue.id_usuario = u.id JOIN seguridad.role r ON ue.id_rol = r.id WHERE r.id = 1001000000000001215 AND ue.eliminado = false AND u.eliminado = false AND e.id = :idEntidadParam")
					.setParameter("idEntidadParam", idEntidad)
					.getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error consultaTotaInvestigadores: "
					+ e.getMessage());
			return null;
		}
	}

	public Long consultaTotalMonitores() {
		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"SELECT count(DISTINCT ue.id_usuario) FROM ensayo.usuario_estudio ue JOIN ensayo.estudio_entidad ee ON ue.id_estudio = ee.id JOIN comun.entidad e ON ee.id_entidad = e.id "
									+ "JOIN comun.usuario u ON ue.id_usuario = u.id JOIN seguridad.role r ON ue.id_rol = r.id WHERE r.id = 1001000000000001216 AND ue.eliminado = false AND u.eliminado = false AND e.id = :idEntidadParam")
					.setParameter("idEntidadParam", idEntidad)
					.getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error consultaTotalMonitores: "
					+ e.getMessage());
			return null;
		}
	}

	public void actualizarTotalEstudios() {
		setTotalEstudiosActivos(consultaTotalEstudioEnsayo());
	}

	public void actualizarTotalSujetoEnsayo() {
		setTotalSujetosEnsayo(consultaTotalSujetosEnsayo());
	}

	public void actualizarUsuarioPromotor() {
		setTotalUsuarioPromotor(consultaTotalUsuariosPromotores());
	}

	public void actualizarTotalInvestigadores() {
		setTotalInvestigadores(consultaTotaInvestigadores());
	}

	public void actualizarTotalMonitores() {
		setTotalMonitores(consultaTotalMonitores());
	}
	
	
	public Long getTotalEstudiosActivos() {
		return totalEstudiosActivos;
	}

	public void setTotalEstudiosActivos(Long totalEstudiosActivos) {
		this.totalEstudiosActivos = totalEstudiosActivos;
	}

	public Long getTotalSujetosEnsayo() {
		return totalSujetosEnsayo;
	}

	public void setTotalSujetosEnsayo(Long totalSujetosEnsayo) {
		this.totalSujetosEnsayo = totalSujetosEnsayo;
	}

	public Long getTotalUsuarioPromotor() {
		return totalUsuarioPromotor;
	}

	public void setTotalUsuarioPromotor(Long totalUsuarioPromotor) {
		this.totalUsuarioPromotor = totalUsuarioPromotor;
	}

	public Long getTotalInvestigadores() {
		return totalInvestigadores;
	}

	public void setTotalInvestigadores(Long totalInvestigadores) {
		this.totalInvestigadores = totalInvestigadores;
	}

	public Long getTotalMonitores() {
		return totalMonitores;
	}

	public void setTotalMonitores(Long totalMonitores) {
		this.totalMonitores = totalMonitores;
	}

}
