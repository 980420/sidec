package gehos.ensayo.ensayo_disenno.gestionarEstudio;

import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.Cie_ensayo;
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
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.session.custom.CieConsList_custom;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.Init;
import javax.persistence.EntityManager;
import javax.swing.plaf.basic.ComboPopup;

import oracle.net.aso.b;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarEstudioControlador")
@Scope(ScopeType.CONVERSATION)
public class modificarEstudioControlador {

	@In
	FacesMessages facesMessages;
	@In
	EntityManager entityManager;

	@In
	IBitacora bitacora;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	Usuario user;


	private Boolean flag = true;
	private BigDecimal edadMinima;
	private BigDecimal edadMaxima;
	private String cantidadSujetosEsperado;


	String desde = "";

	private String seleccione;
	private String comboduracion;
	private boolean mostrarFormulario;

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		this.seleccione = SeamResourceBundle.getBundle()
				.getString("lbl_seleccione_ens");
		this.mostrarFormulario = false;

		//inimodificarestudio();
	}

	private Estudio_ensayo estudio;
	private Long idestudioelim;
	private EstadoEstudio_ensayo estado;
	private List<EstadoEstudio_ensayo> listadeestado = new ArrayList<EstadoEstudio_ensayo>();

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
	private List<Diccionarios_ensayo> listadiccionarios = new ArrayList<Diccionarios_ensayo>();

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
	private String idDic;

	private String seleccion = "";
	private String ajustetemporal = "";
	private String proposito = "";
	private String otroproposito = "";

	private String otrafase = "";

	private EIntervencion_ensayo intervencionadd;

	private String combo = "inter";

	//add Evelio
	private List<EProducto_ensayo> listaProductos = new ArrayList<EProducto_ensayo>();	
	private List<EProducto_ensayo> listaProductosBD = new ArrayList<EProducto_ensayo>();
	private EProducto_ensayo producto = new EProducto_ensayo();

	private boolean combointer = true; // Si true muestra combo madre
	private boolean comboobser = false; // Si true muestra combo padre


	@In(create = true)
	CieConsList_custom cieConsList_custom;

	public void showCombo() {
		if (combo.equals("inter")) {
			setCombointer(true);
			setComboobser(false);
		} else if (combo.equals("obser")) {
			setComboobser(true);
			setCombointer(false);
		}
	}

	private String combofasedef;

	private boolean combofase = true; // Si true muestra combo madre
	private boolean combootrafase = false; // Si true muestra combo padre

	public void showComboFase(String fases) {
		if (fases.equals("Fase")) {
			setCombofasedef("fase");
			setCombofase(true);
			setCombootrafase(false);
		} else if (fases.equals("OtraFase")) {
			setCombofasedef("otrafase");
			setCombootrafase(true);
			setCombofase(false);
		}
	}

	public void showComboFase() {
		if (combofasedef.equals("fase")) {
			//setCombofasedef("fase");
			setCombofase(true);
			setCombootrafase(false);
		} else if (getCombofasedef().equals("otrafase")) {
			//setCombofasedef("otrafase");
			setCombootrafase(true);
			setCombofase(false);
		}
	}

	private String combopropositodef;
	private boolean comboproposito = true; // Si true muestra combo madre
	private boolean combootrtroproposito = false; // Si true muestra combo padre

	public void showComboProposito(String proposito) {
		if (proposito.equals("Propositos")) {
			setCombopropositodef("propositos");
			setComboproposito(true);
			setCombootrtroproposito(false);
		} else if (proposito.equals("OtraPropositos")) {
			setCombopropositodef("otropropositos");
			setComboproposito(false);
			setCombootrtroproposito(true);
		}
	}

	public void showComboProposito() {
		if (combopropositodef.equals("propositos")) {
			//setCombopropositodef("propositos");
			setComboproposito(true);
			setCombootrtroproposito(false);
		} else if (combopropositodef.equals("otropropositos")) {
			//setCombopropositodef("otropropositos");
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


	@SuppressWarnings("unchecked")
	public void inimodificarestudio() {
		try {
			if(flag)
				flag = false;
			cargarDiagnosticoAnterior();
			intervencionadd = new EIntervencion_ensayo();

			estudio = entityManager.find(Estudio_ensayo.class, estudyId);
			if (estudio.getDuracion().equals("Transversal")) {
				comboduracion = "transversal";
			} else {
				comboduracion = "longitudinal";
			}
			if(estudio.getEdadMinima() != null)
				setEdadMinima(estudio.getEdadMinima());
			if(estudio.getEdadMaxima() != null)
				setEdadMaxima(estudio.getEdadMaxima());
			if(estudio.getCantidadSujetosEsperado() != null)
				setCantidadSujetosEsperado(estudio.getCantidadSujetosEsperado().toString());
			if (estudio.getETipoProtocolo().getNombre() != null
					&& estudio.getETipoProtocolo().getNombre()
					.equals("Observacional")) {
				combo = "obser";
				this.combointer = false;
				this.comboobser = true;
			}

			//add Evelio
			//add Evelio
			listaProductosBD = entityManager
					.createQuery(
							"Select eProducto from EProducto_ensayo eProducto "
									+"where eProducto.estudio.id =:id "
									+ "and eProducto.eliminado <> true")
									.setParameter("id", estudio.getId()).getResultList();
			//listaProductos=listaProductosBD;
			for (int i = 0; i < listaProductosBD.size(); i++)
				listaProductos.add(listaProductosBD.get(i));
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
			listadiccionarios = (List<Diccionarios_ensayo>) entityManager.createQuery(
					"select a from Diccionarios_ensayo a ").getResultList();
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

			showComboFase(estudio.getTipofase());
			showComboProposito(estudio.getTipoproposito());

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
			// TODO: handle exception
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
	
	public List<String> ListaEDiccionario() {
		List<String> listaEDiccionarioS = new ArrayList<String>();

		for (int i = 0; i < listadiccionarios.size(); i++) {
			listaEDiccionarioS.add(listadiccionarios.get(i).getNombre());
		}
		listaEDiccionarioS.add("<Seleccione>");
		return listaEDiccionarioS;

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
		//listatipointervencionS.add("<Seleccione>");
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
							"select e from Diccionarios_ensayo e where e.nombre =:nombre ")
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

	public void cancelarEstudio(){

		flag = true;

	}

	public String modificarEstudio() {

		//if(intervencionadd != null)
		//entityManager.persist(intervencionadd);

		List identific = entityManager
				.createQuery(
						"Select est From Estudio_ensayo est "
								+ "where est.identificador =:identest  and (est.eliminado is null or est.eliminado = false) and est.id <> :a")
								.setParameter("identest", this.estudio.getIdentificador())
								.setParameter("a", estudyId).getResultList();

		List name = entityManager
				.createQuery(
						"Select est From Estudio_ensayo est where est.nombre =:nomest and (est.eliminado is null or est.eliminado = false) and est.id <> :a")
						.setParameter("nomest", this.estudio.getNombre())
						.setParameter("a", estudyId).getResultList();

		if (!identific.isEmpty()) {
			this.facesMessages.addToControlFromResourceBundle(
					"crearMSProgramado", Severity.INFO,
					"msg_eidentificadorCreado_ensClin");
			return "no";
		} else if (!name.isEmpty()) {

			this.facesMessages.addToControlFromResourceBundle(
					"crearMSProgramado", Severity.INFO,
					"msg_nombreCreado_ensClin");
			return "no";

		} else {

			estudio.setFechaActualizacion(Calendar.getInstance().getTime());

			estudio.setEstadoEstudio(Estadoseleccionado(estadoestudio));
			estudio.setEAleatorizacion(Aleatorizacionseleccionado(aleatorizacion));
			estudio.setEEnmascaramiento(EEnmascaramientoSeleccionado(enmascaramiento));
			estudio.setEControl(EControLSeleccionado(control));

			estudio.setEAsignacion(EAsignacionSeleccionado(asignacion));
			estudio.setEPuntoFinal(EPuntoFinalSeleccionado(puntofinal));


			if (getEdadMinima() != null && getEdadMaxima() != null) {
				Validations_ensayo valida = new Validations_ensayo();
				Integer edadMin =Integer.parseInt(getEdadMinima().toString());
				Integer edadMax = Integer.parseInt(getEdadMaxima().toString());
				if(estudio.getTipoEdadMin().equals("meses"))
					edadMin = edadMin*30;
				if(estudio.getTipoEdadMin().equals("annos"))
					edadMin = edadMin*365;
				if(estudio.getTipoEdadMax().equals("meses"))
					edadMax = edadMax*30;
				if(estudio.getTipoEdadMax().equals("annos"))
					edadMax = edadMax*365;
				if (edadMin > edadMax) {
					this.facesMessages.addToControlFromResourceBundle(
							"crearMSProgramado", Severity.INFO,
							"edadMinimaMenorMaxima");
					return "no";
				}
			}

			estudio.setEdadMinima(this.edadMinima);
			estudio.setEdadMaxima(this.edadMaxima);
			if(!cantidadSujetosEsperado.equals("")){
				estudio.setCantidadSujetosEsperado(new BigDecimal(cantidadSujetosEsperado));
			}


			if (combofasedef.equals("fase")) {
				estudio.setTipofase("Fase");
				estudio.setEFaseEstudio(EFaseEstudioSeleccionado(fase));
				estudio.setOtrosFase("-");
			} else if (combofasedef.equals("otrafase")) {
				estudio.setTipofase("OtraFase");
				estudio.setOtrosFase(otrafase);
			}
			if (combopropositodef.equals("propositos")) {
				estudio.setTipoproposito("Propositos");
				estudio.setEProposito(EPropositoSeleccionado(proposito));
				estudio.setOtroProposito("-");
			} else if (combopropositodef.equals("otropropositos")) {
				estudio.setTipoproposito("OtraPropositos");
				estudio.setOtroProposito(otroproposito);
			}
			if (comboduracion.equals("longitudinal")) {
				estudio.setDuracion("Longitudinal");
			} else if (comboduracion.equals("transversal")) {
				estudio.setDuracion("Transversal");
			}
			estudio.setESeleccion(ESeleccionSeleccionado(seleccion));
			estudio.setEAjusteTemporal(EAjusteTemporalSeleccionado(ajustetemporal));
			estudio.setESexo(ESexoSeleccionado(sexo));
			estudio.setDiccionario(diccionarioSeleccionado(diccionario));
			estudio.setEstadoEstudio(Estadoseleccionado(estadoestudio));
			estudio.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
					.getBundle().getString("prm_modificar_ens")));
			Usuario_ensayo usuario = entityManager.find(
					Usuario_ensayo.class, user.getId());
			estudio.setUsuario(usuario);

			eliminarDiagnostico();
			
			//add Evelio Producto 
			for (int i = 0; i < listaProductos.size(); i++)
				entityManager.persist(listaProductos.get(i));
			//fin Evelio Producto
			for (int i = 0; i < listaintervencion.size(); i++) {
				listaintervencion.get(i).setEstudio(estudio);
				entityManager.persist(listaintervencion.get(i));
			}

			for (int i = 0; i < enfermedadesSeleccionadas.size(); i++) {
				enfermedadesSeleccionadas.get(i).setEstudio(estudio);
				entityManager.persist(enfermedadesSeleccionadas.get(i));
			}

			entityManager.persist(estudio);
			entityManager.flush();
			return "ok";

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

	public void eliminarInterv(int pos) {

		// listaintervencion.remove(pos);
		EIntervencion_ensayo intervencionelim = new EIntervencion_ensayo();
		intervencionelim = listaintervencion.get(pos);
		listaintervencion.remove(pos);
		intervencionelim.setEliminado(true);
		entityManager.persist(intervencionelim);
		entityManager.flush();
	}

	public void addintervencion() {
		try {

			intervencionadd = new EIntervencion_ensayo();
			intervencionadd.setNombre(nombreintervencion);
			intervencionadd.setETipoIntervencion(ETipoIntervencionSeleccionado(tipointervencion));
			intervencionadd.setEliminado(false);
			intervencionadd.setEstudio(estudio);


			listaintervencion.add(intervencionadd);
			setNombreintervencion("");
			setTipointervencion("");
			//entityManager.persist(intervencionadd);


		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	//add Evelio metodo para adicionar productos 	
	public void adicionarProducto() {
		try {
			producto.setEstudio(estudio);
			producto.setEliminado(false);
			listaProductos.add(producto);	
			producto = new EProducto_ensayo();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void eliminarProducto(EProducto_ensayo producto){
		
		if(listaProductosBD.contains(producto)){
			producto.setEliminado(true);
			entityManager.persist(producto);
		}
		listaProductos.remove(producto);
	}
	//fin add

	public void eliminarEstudio() {
		Estudio_ensayo estudioelimino = new Estudio_ensayo();
		estudioelimino = (Estudio_ensayo) entityManager
				.createQuery("select e from Estudio_ensayo e where e.id=:id")
				.setParameter("id", idestudioelim).getSingleResult();
		estudioelimino.setEliminado(true);
		entityManager.persist(estudioelimino);
		entityManager.flush();
	}

	public void changeFuncHabilitar(String functId) {
		Estudio_ensayo estudy = entityManager.find(Estudio_ensayo.class,
				Long.parseLong(functId));
		if (estudy.getHabilitado() == null || estudy.getHabilitado() == false)
			estudy.setHabilitado(true);
		else
			estudy.setHabilitado(false);
		entityManager.persist(estudy);
		entityManager.flush();

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
		if (listagruposujetos.size() > 0) {
			result = false;
			estadoestudio = "En Dise\u00F1o";
		}

		return result;

	}
	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	public IActiveModule getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
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
	
	public List<Diccionarios_ensayo> getListadiccionarios() {
		return listadiccionarios;
	}

	public void setListadiccionarios(List<Diccionarios_ensayo> listadiccionarios) {
		this.listadiccionarios = listadiccionarios;
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
	
	public String getdiccionario() {
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

	public String getComboduracion() {
		return comboduracion;
	}

	public void setComboduracion(String comboduracion) {
		this.comboduracion = comboduracion;
	}

	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}
	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}




	/**
	 * Enfermedades CIE relacionadas con el estudio
	 * **/

	private Hashtable<Long, Cie_ensayo> enfSelected = new Hashtable<Long, Cie_ensayo>();
	private Long idEnfSelect;
	private List<EEnfermedadCie_ensayo> enfermedadesSeleccionadas = new ArrayList<EEnfermedadCie_ensayo>();

	List<Cie_ensayo> listaCie = new ArrayList<Cie_ensayo>();

	public void seleccionarEnfermedad() {
		if (!getEnfSelected().containsKey(getIdEnfSelect())) {
			Cie_ensayo cie = (Cie_ensayo) entityManager
					.createQuery(
							"select cie from Cie_ensayo cie where cie.id =:idCie")
							.setParameter("idCie", this.getIdEnfSelect())
							.getSingleResult();
			listaCie.add(cie);

			EEnfermedadCie_ensayo diag = new EEnfermedadCie_ensayo();
			diag.setCodigoEnfermedad(cie.getCodigo());
			diag.setDescripcionEnfermedad(cie.getDescripcion());
			getEnfermedadesSeleccionadas().add(diag);
			getEnfSelected().put(cie.getId(), cie);
		} else {
			Cie_ensayo c = getEnfSelected().get(getIdEnfSelect());
			int pos = listaCie.indexOf(c);
			getEnfermedadesSeleccionadas().remove(pos);
			getEnfSelected().remove(getIdEnfSelect());
			listaCie.remove(c);

		}
	}

	public void eliminarEnfermedad(int pos) {
		Cie_ensayo c = listaCie.get(pos);
		getEnfermedadesSeleccionadas().remove(pos);
		listaCie.remove(c);
		getEnfSelected().remove(c.getId());
	}
	private void eliminarDiagnostico()
	{
		List<EEnfermedadCie_ensayo> listaEliminar= entityManager.createQuery("select d from EEnfermedadCie_ensayo d where d.estudio.id = :idhc)")
				.setParameter("idhc", estudyId).getResultList();
		for (int i = 0; i < listaEliminar.size(); i++) {
			entityManager.remove(listaEliminar.get(i));
		}
	}

	private void cargarDiagnosticoAnterior() {
		// cargar las enfermedades

		List<Cie_ensayo> l = entityManager
				.createQuery(
						"select distinct c from Cie_ensayo c "
								+ "where c.codigo IN (select d.codigoEnfermedad from EEnfermedadCie_ensayo d where d.estudio.id = :idhc)")
								.setParameter("idhc", estudyId).getResultList();

		if (!listaCie.containsAll(l)) {
			for (int i = 0; i < l.size(); i++) {
				listaCie.add(l.get(i));
				EEnfermedadCie_ensayo diag = new EEnfermedadCie_ensayo();
				diag.setCodigoEnfermedad(l.get(i).getCodigo());
				diag.setDescripcionEnfermedad(l.get(i).getDescripcion());
				enfermedadesSeleccionadas.add(diag);
				enfSelected.put(l.get(i).getId(), l.get(i));
			}

		}
	}
	public void selectDiccionario() {
		Diccionarios_ensayo d = this.entityManager.find(Diccionarios_ensayo.class, Long.parseLong(this.idDic));
		this.diccionario = d.getNombre();
	}

	public List<Cie_ensayo> getListaCie() {
		return listaCie;
	}

	public void setListaCie(List<Cie_ensayo> listaCie) {
		this.listaCie = listaCie;
	}

	public Hashtable<Long, Cie_ensayo> getEnfSelected() {
		return enfSelected;
	}

	public void setEnfSelected(Hashtable<Long, Cie_ensayo> enfSelected) {
		this.enfSelected = enfSelected;
	}

	public List<EEnfermedadCie_ensayo> getEnfermedadesSeleccionadas() {
		return enfermedadesSeleccionadas;
	}

	public void setEnfermedadesSeleccionadas(
			List<EEnfermedadCie_ensayo> enfermedadesSeleccionadas) {
		this.enfermedadesSeleccionadas = enfermedadesSeleccionadas;
	}

	public Long getIdEnfSelect() {
		return idEnfSelect;
	}

	public void setIdEnfSelect(Long idEnfSelect) {
		this.idEnfSelect = idEnfSelect;
	}

	public CieConsList_custom getCieConsList_custom() {
		return cieConsList_custom;
	}

	public void setCieConsList_custom(CieConsList_custom cieConsList_custom) {
		this.cieConsList_custom = cieConsList_custom;
	}


	public BigDecimal getEdadMaxima() {
		return edadMaxima;
	}

	public void setEdadMaxima(BigDecimal edadMaxima) {
		this.edadMaxima = edadMaxima;
	}

	public BigDecimal getEdadMinima() {
		return edadMinima;
	}

	public void setEdadMinima(BigDecimal edadMinima) {
		this.edadMinima = edadMinima;
	}


	public Boolean getFlag() {
		return flag;
	}


	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getCantidadSujetosEsperado() {
		return cantidadSujetosEsperado;
	}

	public void setCantidadSujetosEsperado(String cantidadSujetosEsperado) {
		this.cantidadSujetosEsperado = cantidadSujetosEsperado;
	}

	public List<EProducto_ensayo> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<EProducto_ensayo> listaProductos) {
		this.listaProductos = listaProductos;
	}

	public EProducto_ensayo getProducto() {
		return producto;
	}

	public void setProducto(EProducto_ensayo producto) {
		this.producto = producto;
	}

	public String getIdDic() {
		return idDic;
	}

	public void setIdDic(String idDic) {
		this.idDic = idDic;
	}
}
