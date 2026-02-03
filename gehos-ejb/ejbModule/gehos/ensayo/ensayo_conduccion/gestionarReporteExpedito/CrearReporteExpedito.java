//CU 13 Gestionar datos de hoja CRD
package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.WrapperReReporteexpedito;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.entity.EFaseEstudio_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.ReConsecuenciaspaciente_ensayo;
import gehos.ensayo.entity.ReCrealizadaeas_ensayo;
import gehos.ensayo.entity.ReEnfermedades_ensayo;
import gehos.ensayo.entity.ReHistoriarelevante_ensayo;
import gehos.ensayo.entity.ReIntensidad_ensayo;
import gehos.ensayo.entity.ReMedicacion_ensayo;
import gehos.ensayo.entity.ReRecausalpea_ensayo;
import gehos.ensayo.entity.ReReporteexpedito_ensayo;
import gehos.ensayo.entity.ReRespuesta_ensayo;
import gehos.ensayo.entity.ReResultadofinal_ensayo;
import gehos.ensayo.entity.ReTrazabilidadreporte_ensayo;
import gehos.ensayo.entity.ReViasadmi_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearReporteExpedito")
@Scope(ScopeType.CONVERSATION)
public class CrearReporteExpedito extends IActionWithUserInput {

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	protected @In(create = true) FacesMessages facesMessages;
	@In(create = true) IdUtil idUtil;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In private Usuario user;
	@In private IActiveModule activeModule;
	@In(value = "gestionarHoja", required = false) GestionarHoja gestionarHoja;
	
	private Long idcrd;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	List<EFaseEstudio_ensayo> fases = new ArrayList<EFaseEstudio_ensayo>();
	List<ReIntensidad_ensayo> listaIntensidad = new ArrayList<ReIntensidad_ensayo>();
	List<ReConsecuenciaspaciente_ensayo> listaConsecuencias = new ArrayList<ReConsecuenciaspaciente_ensayo>();
	List<ReRecausalpea_ensayo> listaRelacion = new ArrayList<ReRecausalpea_ensayo>();
	List<ReRespuesta_ensayo> listaModificacion = new ArrayList<ReRespuesta_ensayo>();
	List<ReCrealizadaeas_ensayo> listaConducta = new ArrayList<ReCrealizadaeas_ensayo>();
	List<ReResultadofinal_ensayo> listaResultado = new ArrayList<ReResultadofinal_ensayo>();
	List<ReEnfermedades_ensayo> listaEnfermedades = new ArrayList<ReEnfermedades_ensayo>();

	ReTrazabilidadreporte_ensayo trazabilidaSitioInv = new ReTrazabilidadreporte_ensayo();
	ReTrazabilidadreporte_ensayo trazabilidaPromotor = new ReTrazabilidadreporte_ensayo();
	ReTrazabilidadreporte_ensayo trazabilidaResponsable = new ReTrazabilidadreporte_ensayo();
	
	Usuario_ensayo usuarioCambiar = new Usuario_ensayo();

	private String nombreEst = "";
	private String fase = "";
	private String notificador = "";
	private String email = "";
	private Long telefono;
	private String indicacion = "";
	private String codigoEst = "";
	private String institucion = "";
	private String presentacion = "";
	private String investigador = "";
	private String provincia = "";
	private String lugar = "";
	private String paciente = "";
	private String especifiqueOtra = "";
	private String fechaInicio = "";
	private String terminoTrat = "";
	private String acontecimiento = "";
	private String lbldescripcion = "";
	private String especTrat = "";
	private String horaInicio = "";
	private String minutosInicio;
	private String am_pmInicio;
	
	private String horaReportepromotor = "";
	String minutosReportepromotor = "";
	String am_pmReportepromotor = "";
	
	private String tratamientoQuir = "";
	private String fechaFinalizacion = "";
	private String nombreapellido = "";
	private String causalidad = "";
	private String fuente = "";
	private String fechaReportepromotor = "";
	private String fuenteOtra = "";
	private String horaFinalizacion = "";
	private String minutosFinalizacion = "";
	private String am_pmFinalizacion = "";
	
	private String fechaReporteA = "";
	private String fechaReporteB = "";
	private String fechaReporteC = "";
	private String fechaReporteD = "";
	private String eAdverso = "";
	private String nombreInvestigador = "";
	private String accion = "";
	private String noDosis = "";
	private String desEAdverso = "";
	private String direccion = "";
	private String abandonado = "";
	private String fechaReporte1 = "";
	private String fechaReporte2 = "";
	private String fechaReporte3 = "";
	private String fechaReporte4 = "";
	private String fechaReporte5 = "";
	private String profesion = "";
	private String iniciales = "";
	private String producInvs = "";
	private String lote = "";
	private String fechaCadu = "";
	private String intensidad = "";
	private String especifiqueAcont = "";
	private String especifiqueOtros = "";
	private String[] consecuencia = new String[10];
	private String conductaEAS = "";
	private String modificacionI = "";
	private String tipoResultado = "";
	private String fechaAltaSiProlonga = "";
	private String lugarOtrosEsp = "";
	private String reaparecio;
	private String[] historia = new String[10];
	private String datosLab = "";
	private String copiaBiopsia = "";
	private String copiaAutopsia = "";
	private String autopsia = "";
	private String fechaIngreso = "";
	private String fechaNac = "";
	private String fechaAlta = "";
	private String informe = "";
	private String talla = "";
	private String dias = "";
	private String relacion;
	private String iniTratamiento;
	private String cantDosis;
	private String fechaUltiDosisRecibida;
	private String dosisAdmin;
	private String viaA = "";
	private String fechaEvento = "";
	private String tiempoAparicion = "";
	private String secuela = "";
	private Integer diasHosp;
	private String from;
	String medicamento = "";
	String medicamentoP = "";
	String medicamento1 = "";
	String presentacionVac = "";
	String loteVac = "";
	String loteVacP = "";
	String dosisDia;
	String dosisDiaP;
	String dosisDia1;
	String frecuencia = "";
	String frecuenciaP = "";
	String frecuencia1 = "";
	String viaAdmon = "";
	String enfermedad = "";
	String persiste = "";
	String viaAdmonP = "";
	String viaAdmon1 = "";
	String fechaVac;
	String fechaVacP;
	private String horaVac;
	private String minutosVac;
	private String am_pmVac;
	
	private String horaVacP;
	private String minutosVacP;
	private String am_pmVacP;
	
	
	
	String medicamentoF;
	String dosisDiaF;
	String frecuenciaF;
	String viaAdmonF;
	String fechaVacF;
	String horaVacF;
	String minutosVacF;
	String am_pmVacF;

	String otroEsp = "";
	String otroEsp1 = "";

	String reportSitio = "";
	String revPromotor = "";
	String revRespFarma = "";
	String responsabilidad = "";
	String responsabilidad1 = "";
	String responsabilidad2 = "";
	private String edad = "";
	private String sexo = "";
	private String peso;
	private String raza = "";
	private Estudio_ensayo estudioE;
	private boolean inicializado = false;
	private final String seleccione = SeamResourceBundle.getBundle().getString("cbxSeleccionPorDefecto");
	private Variable_ensayo variableRegla;
	private ReReporteexpedito_ensayo objetoRepExpedito;
	private List<ReMedicacion_ensayo> listaMedicacionConm = new ArrayList<ReMedicacion_ensayo>();
	private List<ReMedicacion_ensayo> listaMedicacionRec = new ArrayList<ReMedicacion_ensayo>();
	private List<ReMedicacion_ensayo> listaProductosEstudios = new ArrayList<ReMedicacion_ensayo>();
	private List<ReMedicacion_ensayo> listaEnfermedadesInter = new ArrayList<ReMedicacion_ensayo>();
	private Variable_ensayo variablereglaGlobal = null;
	private boolean editing = false;

	@SuppressWarnings("unchecked")
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation(){		
		this.objetoRepExpedito = null;
		this.estudioE = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio();
		this.nombreEst = this.estudioE.getNombre();
		this.codigoEst = this.estudioE.getIdentificador();
		this.usuario = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
		this.editing = false;
		if(this.sectionId != null && this.groupId != null && this.repetition != null && this.variableRegla != null && this.keyData != null && this.index != -1){
			this.editing = this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).hasReport();
			this.objetoRepExpedito = ((this.editing) ? this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().getReport() : null);
		}
		if (this.objetoRepExpedito != null){
			this.fechaReporteA = this.objetoRepExpedito.getFechaCECMEDA();
			this.fechaReporteB = this.objetoRepExpedito.getFechaCECMEDB();
			this.fechaReporteC = this.objetoRepExpedito.getFechaCECMEDC();
			this.fechaReporteD = this.objetoRepExpedito.getFechaCECMEDD();
			this.nombreapellido = this.objetoRepExpedito.getPromotor();
			this.producInvs = this.objetoRepExpedito.getProductoinvestigado();
			this.dosisAdmin = this.objetoRepExpedito.getDosisadministrada();
			this.presentacion = this.objetoRepExpedito.getPresentacion();
			this.viaA = this.objetoRepExpedito.getReViasadmi().getDescripcion();
			this.investigador = this.objetoRepExpedito.getInvestigador();
			if (this.objetoRepExpedito.getInformeinicial() != null)
				this.informe = ((this.objetoRepExpedito.getInformeinicial()) ? SeamResourceBundle.getBundle().getString("lbl_informeIni") : SeamResourceBundle.getBundle().getString("lbl_informeAdd"));
			this.listaMedicacionConm = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionconcomitante_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False").setParameter("idReporteexpedito", objetoRepExpedito.getId()).getResultList();
			this.listaMedicacionRec = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionreciente_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False").setParameter("idReporteexpedito", objetoRepExpedito.getId()).getResultList();
			this.listaProductosEstudios = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReInfoproducto_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False").setParameter("idReporteexpedito", objetoRepExpedito.getId()).getResultList();
			this.listaEnfermedadesInter = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReEnfermedadesinter_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False").setParameter("idReporteexpedito", objetoRepExpedito.getId()).getResultList();
			List<ReHistoriarelevante_ensayo> listaHistoria = (List<ReHistoriarelevante_ensayo>) this.entityManager.createQuery("select historia from ReHistoriarelevante_ensayo historia where historia.reReporteexpedito.id=:idReporteexpedito").setParameter("idReporteexpedito", objetoRepExpedito.getId()).getResultList();
			for (int i = 0; i < listaHistoria.size(); i++){
				ReConsecuenciaspaciente_ensayo enfermedad = this.entityManager.find(ReConsecuenciaspaciente_ensayo.class, listaHistoria.get(i).getIdReEnfermedades());
				this.consecuencia[i] = enfermedad.getDescripcion();
			}
			if (this.objetoRepExpedito.getAutopsia() != null)
				this.autopsia = ((this.objetoRepExpedito.getAutopsia()) ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no")); 
			if (this.objetoRepExpedito.getFechaingreso() != null)
				this.fechaIngreso = this.objetoRepExpedito.getFechaingreso();
			if (this.objetoRepExpedito.getFechaalta() != null)
				this.fechaAlta = this.objetoRepExpedito.getFechaalta();
			if (!this.objetoRepExpedito.getFechaaltaprol().equals(""))
				this.fechaAltaSiProlonga = this.objetoRepExpedito.getFechaaltaprol();
			if (!this.objetoRepExpedito.getEspecifiqueacont().equals("") || this.objetoRepExpedito.getEspecifiqueacont() != null)
				this.especifiqueAcont = this.objetoRepExpedito.getEspecifiqueacont();
			if (!this.objetoRepExpedito.getEspecifiqueotros().equals("") || this.objetoRepExpedito.getEspecifiqueotros() != null)
				this.especifiqueOtros = this.objetoRepExpedito.getEspecifiqueotros();
			this.direccion = this.objetoRepExpedito.getDireccion();
			this.profesion = this.objetoRepExpedito.getProfesion();
			this.fechaReporte1 = this.objetoRepExpedito.getFechareporteA();
			this.fechaReporte2 = this.objetoRepExpedito.getFechareporteB();
			this.fechaReporte3 = this.objetoRepExpedito.getFechareporteC();
			this.fechaReporte4 = this.objetoRepExpedito.getFechareporteD();
			this.fechaNac = this.objetoRepExpedito.getFechanacimiento();
			this.sexo = this.objetoRepExpedito.getSexo();
			this.peso = this.objetoRepExpedito.getPeso().toString();
			this.raza = this.objetoRepExpedito.getRaza();
			this.talla = this.objetoRepExpedito.getTalla().toString();
			if (!this.objetoRepExpedito.getOtraraza().equals("") || this.objetoRepExpedito.getOtraraza() != null)
				this.especifiqueOtra = this.objetoRepExpedito.getOtraraza();
			this.eAdverso = this.objetoRepExpedito.getEventoadverso();
			this.fechaInicio = this.objetoRepExpedito.getFechainicio();
			
			
			if((!horaInicio.isEmpty() && (minutosInicio.isEmpty() || am_pmInicio.isEmpty()))){
				this.facesMessages
				.addToControlFromResourceBundle(
						"horaInicio",
						Severity.ERROR,
						"Introduzca los valores restantes");
				return;
			}
			if(!horaInicio.isEmpty() && !minutosInicio.isEmpty() && !am_pmInicio.isEmpty()){
				this.objetoRepExpedito.setHorainicio(horaInicio + ":" + minutosInicio + ":00 " + am_pmInicio.replace("1", ""));
			}
			this.fechaFinalizacion = this.objetoRepExpedito.getFechafinalizacion();
			if((!horaFinalizacion.isEmpty() && (minutosFinalizacion.isEmpty() || am_pmFinalizacion.isEmpty()))){
				
				this.facesMessages
				.addToControlFromResourceBundle(
						"horaFinalizacion",
						Severity.ERROR,
						"Introduzca los valores restantes");
				return;
			}			
			if(!horaFinalizacion.isEmpty() && !minutosFinalizacion.isEmpty() && !am_pmFinalizacion.isEmpty()){
				this.objetoRepExpedito.setHorafinalizacion(horaFinalizacion + ":" + minutosFinalizacion + ":00 " + am_pmFinalizacion.replace("1", ""));
			}
			
			this.intensidad = this.objetoRepExpedito.getReIntensidad().getCodigo().toString();
			this.lugar = this.objetoRepExpedito.getDondeocurrio();
			if (!this.objetoRepExpedito.getOtrolugar().equals(""))
				this.lugarOtrosEsp = this.objetoRepExpedito.getOtrolugar();
			this.accion = this.objetoRepExpedito.getReCrealizadaeas().getDescripcion();
			if(this.objetoRepExpedito.getAcontecimiento() != null)
				this.acontecimiento = this.objetoRepExpedito.getAcontecimiento();
			this.causalidad = this.objetoRepExpedito.getCausalidad();
			this.fuente = objetoRepExpedito.getFuente();
			if (!this.objetoRepExpedito.getOtrafuente().equals(""))
				this.fuenteOtra = this.objetoRepExpedito.getOtrafuente();
			if(this.objetoRepExpedito.getReResultadofinal() != null)
				this.tipoResultado = this.objetoRepExpedito.getReResultadofinal().getDescripcion();
			this.abandonado = ((this.objetoRepExpedito.getAbndonoelsujeto()) ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no")); 
			this.terminoTrat = (this.objetoRepExpedito.getTerminotratamiento() ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no")); 
			this.indicacion = this.objetoRepExpedito.getIndicacion();
			this.tratamientoQuir = (this.objetoRepExpedito.getTratamientoquirurgico() ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no"));
			if (!this.objetoRepExpedito.getEspecifiquetrat().equals(""))
				this.especTrat = this.objetoRepExpedito.getEspecifiquetrat();
			this.lbldescripcion = this.objetoRepExpedito.getDescripciongeneral();
			this.fechaReportepromotor = this.objetoRepExpedito.getFechareportepromotor();			
			if((!horaReportepromotor.isEmpty() && (minutosReportepromotor.isEmpty() || am_pmReportepromotor.isEmpty()))){
				
				this.facesMessages
				.addToControlFromResourceBundle(
						"horaReportepromotor",
						Severity.ERROR,
						"Introduzca los valores restantes");
				return;
			}			
			if(!horaReportepromotor.isEmpty() && !minutosReportepromotor.isEmpty() && !am_pmReportepromotor.isEmpty()){
				this.objetoRepExpedito.setHorareportepromotor(horaReportepromotor + ":" + minutosReportepromotor + ":00 " + am_pmReportepromotor.replace("1", ""));
			}			
			
			this.nombreInvestigador = this.objetoRepExpedito.getNombreinvestigador();
			this.fechaReporte5 = this.objetoRepExpedito.getFechareportee();
		}
		this.fase = ((this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEFaseEstudio() == null) ? null : this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEFaseEstudio().getNombre());
		usuarioCambiar = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
		this.notificador = usuarioCambiar.getNombre() + " " + usuarioCambiar.getPrimerApellido() + " " + usuarioCambiar.getSegundoApellido();
		this.email = ((usuarioCambiar.getTelefono().equals("") || usuarioCambiar.getTelefono() == null || usuarioCambiar.getTelefono().equals("prueba")) ? null : usuarioCambiar.getTelefono());
		this.telefono = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getTelefonoContacto();
		this.institucion = this.seguridadEstudio.getEstudioEntidadActivo().getEntidad().getNombre();
		this.provincia = this.seguridadEstudio.getEstudioEntidadActivo().getEntidad().getEstado().getValor();
		this.iniciales = this.gestionarHoja.getHoja().getMomentoSeguimientoEspecifico().getSujeto().getInicialesPaciente();
		this.paciente = this.gestionarHoja.getHoja().getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
		this.inicializado = true;
	}

	public boolean requiereHosp(){
		boolean activar = false;
		if (this.consecuencia.length > 0){
			for (int i = 0; i < this.consecuencia.length; i++){
				if (this.consecuencia[i] != null && this.consecuencia[i].equals(SeamResourceBundle.getBundle().getString("lbl_requirieHosp"))){
					activar = true;
					break;
				}
			}
		}
		return activar;
	}

	public boolean prolongaEstancia(){
		boolean activar = false;
		if (this.consecuencia.length > 0){
			for (int i = 0; i < this.consecuencia.length; i++){
				if (this.consecuencia[i] != null && this.consecuencia[i].equals(SeamResourceBundle.getBundle().getString("lbl_prolongaEstancia"))){
					activar = true;
					break;
				}
			}
		}
		return activar;
	}

	public boolean especifiqueOtra(){
		return (this.raza.equals("Otra"));
	}

	public boolean especifiqueAcontecimiento(){
		boolean activar = false;
		if (this.consecuencia.length > 0){
			for (int i = 0; i < this.consecuencia.length; i++){
				if (this.consecuencia[i] != null && this.consecuencia[i].equals(SeamResourceBundle.getBundle().getString("lbl_especifiqueAcontecimiento"))){
					activar = true;
					break;
				}
			}
		}
		return activar;
	}

	public boolean especifiqueOtros(){
		boolean activar = false;
		if (this.consecuencia.length > 0){
			for (int i = 0; i < this.consecuencia.length; i++){
				if (this.consecuencia[i] != null && this.consecuencia[i].equals("Otros")){
					activar = true;
					break;
				}
			}
		}
		return activar;
	}

	public boolean lugarOtros(){
		return (this.lugar.equals("8"));
	}

	public boolean lugarOtros1(){
		return (this.tratamientoQuir.equals(SeamResourceBundle.getBundle().getString("lbl_si")));
	}

	public boolean fuenteOtra(){
		return (this.fuente.equals("Otras"));
	}

	public boolean esMuerte(){
		boolean activar = false;
		if (this.consecuencia.length > 0){
			for (int i = 0; i < this.consecuencia.length; i++){
				if (this.consecuencia[i] != null && this.consecuencia[i].equals("Provoca la muerte")){
					activar = true;
					break;
				}
			}
		}
		return activar;
	}

	public void activarOtraRaza(){
		if (!this.raza.equals("Otra") && !this.especifiqueOtra.equals(""))
			this.setEspecifiqueOtra("");
	}
	
	public void activarOtroLugar(){
		if (!this.lugar.equals("Otros") && !this.lugarOtrosEsp.equals(""))
			this.setLugarOtrosEsp("");
	}
	
	public void activarOtraFuente(){
		if (!this.fuente.equals("Otras") && !this.fuenteOtra.equals(""))
			this.setFuenteOtra("");
	}

	public boolean activarNoDosis(){
		return (this.fechaEvento.equals(SeamResourceBundle.getBundle().getString("lbl_terminadaAdministracion")));
	}

	public boolean requirioHosp(){
		return (this.tipoResultado.equals("Requiri\u00F3 hospitalizaci\u00F3n"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresIntensidad(){
		List listaRadio = new ArrayList();
		if(this.listaIntensidad == null || this.listaIntensidad.isEmpty())
			this.listaIntensidad = (List<ReIntensidad_ensayo>) this.entityManager.createQuery("select nom from ReIntensidad_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < this.listaIntensidad.size(); i++)
			listaRadio.add(new SelectItem(this.listaIntensidad.get(i).getCodigo().toString(), this.listaIntensidad.get(i).getCodigo().toString()));
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresconductaEAS(){
		List<String> listaRadio = new ArrayList<String>();
		if(this.listaConducta == null || this.listaConducta.isEmpty())
			this.listaConducta = (List<ReCrealizadaeas_ensayo>) this.entityManager.createQuery("select nom from ReCrealizadaeas_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		listaRadio.add("<Seleccione>");
		for (int i = 0; i < this.listaConducta.size(); i++)
			listaRadio.add(this.listaConducta.get(i).getDescripcion());
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresAccion(){
		List listaRadio = new ArrayList();
		this.listaConducta = (List<ReCrealizadaeas_ensayo>) this.entityManager.createQuery("select nom from ReCrealizadaeas_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < this.listaConducta.size(); i++)
			listaRadio.add(new SelectItem(this.listaConducta.get(i).getDescripcion(), this.listaConducta.get(i).getDescripcion()));
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresHistoria(){
		List listaCombo = new ArrayList();
		if(this.listaEnfermedades == null || this.listaEnfermedades.isEmpty())
			this.listaEnfermedades = (List<ReEnfermedades_ensayo>) this.entityManager.createQuery("select nom from ReEnfermedades_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < this.listaEnfermedades.size(); i++)
			listaCombo.add(new SelectItem(this.listaEnfermedades.get(i).getDescripcion(), this.listaEnfermedades.get(i).getDescripcion()));
		return listaCombo;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresmodificacionI(){
		List listaRadio = new ArrayList();
		List<ReRespuesta_ensayo> lista = (List<ReRespuesta_ensayo>) this.entityManager.createQuery("select nom from ReRespuesta_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < lista.size(); i++)
			listaRadio.add(new SelectItem(lista.get(i).getDescripcion(), lista.get(i).getDescripcion()));
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresmodificacionICombo(){
		List<String> listaRadio = new ArrayList<String>();
		if(this.listaModificacion == null || this.listaModificacion.isEmpty())
			this.listaModificacion = (List<ReRespuesta_ensayo>) this.entityManager.createQuery("select nom from ReRespuesta_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		listaRadio.add("<Seleccione>");
		for (int i = 0; i < this.listaModificacion.size(); i++)
			listaRadio.add(this.listaModificacion.get(i).getDescripcion());
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresTipoResultado(){
		List listaRadio = new ArrayList();
		if(this.listaResultado == null || this.listaResultado.isEmpty())
			this.listaResultado = (List<ReResultadofinal_ensayo>) this.entityManager.createQuery("select nom from ReResultadofinal_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < this.listaResultado.size(); i++)
			listaRadio.add(new SelectItem(this.listaResultado.get(i).getDescripcion(), this.listaResultado.get(i).getDescripcion()));
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresConsecuencia(){
		List listaRadio = new ArrayList();
		if(this.listaConsecuencias == null || this.listaConsecuencias.isEmpty())
			this.listaConsecuencias = (List<ReConsecuenciaspaciente_ensayo>) this.entityManager.createQuery("select nom from ReConsecuenciaspaciente_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < this.listaConsecuencias.size(); i++)
			listaRadio.add(new SelectItem(this.listaConsecuencias.get(i).getDescripcion(), this.listaConsecuencias.get(i).getDescripcion()));
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresRelacion(){
		List listaRadio = new ArrayList();
		if(this.listaRelacion == null || this.listaRelacion.isEmpty())
			this.listaRelacion = (List<ReRecausalpea_ensayo>) this.entityManager.createQuery("select nom from ReRecausalpea_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < this.listaRelacion.size(); i++)
			listaRadio.add(new SelectItem(this.listaRelacion.get(i).getDescripcion(), this.listaRelacion.get(i).getDescripcion()));
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public void Adicionar(){
		ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
		medicacion.setMedicamento(this.medicamento);
		medicacion.setPresentacion(this.presentacionVac);
		medicacion.setLote(this.loteVac);
		medicacion.setDosisxdia(this.dosisDia);
		medicacion.setFrecuencia(this.frecuencia);
		List<ReViasadmi_ensayo> va = (List<ReViasadmi_ensayo>) this.entityManager.createQuery("select v from ReViasadmi_ensayo v order by v.codigo").getResultList();
		ReViasadmi_ensayo via = new ReViasadmi_ensayo();
		for (int i = 0; i < va.size(); i++){
			if (va.get(i).getDescripcion().equals(this.viaAdmon)){
				via = va.get(i);
				break;
			}
		}
		medicacion.setReViasadmi(via);
		medicacion.setFechaAdm(this.fechaVac);
		medicacion.setEliminado(false);
		this.listaMedicacionConm.add(medicacion);
		this.medicamento = "";
		this.dosisDia = "";
		this.frecuencia = "";
		this.viaAdmon = "";
		this.fechaVac = "";
		this.presentacionVac = "";
		this.loteVac = "";
        if((!horaVac.isEmpty() && (minutosVac.isEmpty() || am_pmVac.isEmpty()))){
			this.facesMessages
			.addToControlFromResourceBundle(
					"HoraVac",
					Severity.ERROR,
					"Introduzca los valores restantes");
			return;
		}
		if(!horaVac.isEmpty() && !minutosVac.isEmpty() && !am_pmVac.isEmpty()){
			medicacion.setHoraAdm(horaVac + ":" + minutosVac + ":00 " + am_pmVac.replace("1", ""));
		}
	}

	@SuppressWarnings("unchecked")
	public void Adicionar1(){
		ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
		medicacion.setMedicamento(this.medicamentoF);
		medicacion.setDosisxdia(this.dosisDiaF);
		medicacion.setFrecuencia(this.frecuenciaF);
		List<ReViasadmi_ensayo> va = (List<ReViasadmi_ensayo>) this.entityManager.createQuery("select v from ReViasadmi_ensayo v order by v.codigo").getResultList();
		ReViasadmi_ensayo via = new ReViasadmi_ensayo();
		for (int i = 0; i < va.size(); i++){
			if (va.get(i).getDescripcion().equals(this.viaAdmonF)){
				via = va.get(i);
				break;
			}
		}
		medicacion.setReViasadmi(via);
		medicacion.setFechaAdm(this.fechaVacF);
		medicacion.setEliminado(false);
		this.listaMedicacionRec.add(medicacion);
		this.medicamentoF = "";
		this.dosisDiaF = "";
		this.frecuenciaF = "";
		this.viaAdmonF = "";
		this.fechaVacF = "";
		if((!horaVacF.isEmpty() && (minutosVacF.isEmpty() || am_pmVacF.isEmpty()))){
			this.facesMessages
			.addToControlFromResourceBundle(
					"horaVacF",
					Severity.ERROR,
					"Introduzca los valores restantes");
			return;
		}
		if(!horaVacF.isEmpty() && !minutosVacF.isEmpty() && !am_pmVacF.isEmpty()){
			medicacion.setHoraAdm(horaVacF + ":" + minutosVacF + ":00 " + am_pmVacF.replace("1", ""));
		}
	}

	@SuppressWarnings("unchecked")
	public void AdicionarP(){
		ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
		medicacion.setMedicamento(this.medicamentoP);
		medicacion.setLote(this.loteVacP);
		medicacion.setDosisxdia(this.dosisDiaP);
		medicacion.setFrecuencia(this.frecuenciaP);
		List<ReViasadmi_ensayo> va = (List<ReViasadmi_ensayo>) this.entityManager.createQuery("select v from ReViasadmi_ensayo v order by v.codigo").getResultList();
		ReViasadmi_ensayo via = new ReViasadmi_ensayo();
		for (int i = 0; i < va.size(); i++){
			if (va.get(i).getDescripcion().equals(this.viaAdmonP)){
				via = va.get(i);
				break;
			}
		}
		medicacion.setReViasadmi(via);
		medicacion.setFechaAdm(this.fechaVacP);
		medicacion.setEliminado(false);
		this.listaProductosEstudios.add(medicacion);
		this.medicamentoP = "";
		this.dosisDiaP = "";
		this.frecuenciaP = "";
		this.viaAdmonP = "";
		this.fechaVacP = "";
		if((!horaVacP.isEmpty() && (minutosVacP.isEmpty() || am_pmVacP.isEmpty()))){
			
			this.facesMessages
			.addToControlFromResourceBundle(
					"HoraVacP",
					Severity.ERROR,
					"Introduzca los valores restantes");
			return;
		}
		if(!horaVacP.isEmpty() && !minutosVacP.isEmpty() && !am_pmVacP.isEmpty()){
			medicacion.setHoraAdm(horaVacP + ":" + minutosVacP + ":00 " + am_pmVacP.replace("1", ""));
		}
		this.loteVacP = "";
	}

	public void AdicionarE(){
		ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
		medicacion.setMedicamento(this.enfermedad);
		medicacion.setPresentacion(this.persiste);
		medicacion.setEliminado(false);
		this.listaEnfermedadesInter.add(medicacion);
		this.enfermedad = "";
		this.persiste = "";
	}

	public List<ReMedicacion_ensayo> ListaMedicacionSinEliminar(){
		try {
			List<ReMedicacion_ensayo> listasineliminadotemp = new ArrayList<ReMedicacion_ensayo>();
			for (int i = 0; i < this.listaMedicacionConm.size(); i++){
				if (!this.listaMedicacionConm.get(i).getEliminado())
					listasineliminadotemp.add(this.listaMedicacionConm.get(i));
			}
			return listasineliminadotemp;
		} catch (Exception e){
			return null;
		}
	}

	public List<ReMedicacion_ensayo> ListaMedicacionSinEliminar1(){
		try {
			List<ReMedicacion_ensayo> listasineliminadotemp = new ArrayList<ReMedicacion_ensayo>();
			for (int i = 0; i < this.listaMedicacionRec.size(); i++){
				if (!this.listaMedicacionRec.get(i).getEliminado())
					listasineliminadotemp.add(this.listaMedicacionRec.get(i));
			}
			return listasineliminadotemp;
		} catch (Exception e){
			return null;
		}
	}

	public List<ReMedicacion_ensayo> ListaMedicacionSinEliminar2(){
		try {
			List<ReMedicacion_ensayo> listasineliminadotemp = new ArrayList<ReMedicacion_ensayo>();
			for (int i = 0; i < this.listaProductosEstudios.size(); i++){
				if (!this.listaProductosEstudios.get(i).getEliminado())
					listasineliminadotemp.add(this.listaProductosEstudios.get(i));
			}
			return listasineliminadotemp;
		} catch (Exception e){
			return null;
		}
	}

	public List<ReMedicacion_ensayo> ListaMedicacionSinEliminar3(){
		try {
			List<ReMedicacion_ensayo> listasineliminadotemp = new ArrayList<ReMedicacion_ensayo>();
			for (int i = 0; i < this.listaEnfermedadesInter.size(); i++){
				if (!this.listaEnfermedadesInter.get(i).getEliminado())
					listasineliminadotemp.add(this.listaEnfermedadesInter.get(i));
			}
			return listasineliminadotemp;
		} catch (Exception e){
			return null;
		}
	}

	public void eliminarMedicacion(int pos){
		this.listaMedicacionConm.get(pos).setEliminado(true);
	}

	public void eliminarMedicacion3(int pos){
		this.listaEnfermedadesInter.get(pos).setEliminado(true);
	}

	public void eliminarMedicacion1(int pos){
		this.listaMedicacionRec.get(pos).setEliminado(true);
	}

	public void eliminarMedicacion2(int pos){
		this.listaProductosEstudios.get(pos).setEliminado(true);
	}

	// Listado de las vias de administracion
	private List<String> va = new ArrayList<String>();

	// llenar las fases
	@SuppressWarnings("unchecked")
	public List<String> llenarFASES(){
		if(this.fases == null || this.fases.isEmpty())
			this.fases = (List<EFaseEstudio_ensayo>) this.entityManager.createQuery("select f from EFaseEstudio_ensayo f order by f.codigo").getResultList();
		List<String> listaAux = new ArrayList<String>();
		listaAux.add(0, "<Seleccione>");
		for (int i = 0; i < this.fases.size(); i++)
			listaAux.add(this.fases.get(i).getNombre());
		return listaAux;
	}

	// llenar las vias de administracion
	@SuppressWarnings("unchecked")
	public List<String> llenarVA(){
		if(this.va == null || this.va.isEmpty())
			this.va = this.entityManager.createQuery("select v.descripcion from ReViasadmi_ensayo v order by v.codigo").getResultList();
		return va;
	}

	// llenar sexo
	public List<String> llenarSexo(){
		List<String> listaSex = new ArrayList<String>();
		listaSex.add("Masculino");
		listaSex.add("Femenino");
		return listaSex;
	}

	// llenar sexo
	public List<String> llenarRaza(){
		List<String> listaSex = new ArrayList<String>();
		listaSex.add("Blanca");
		listaSex.add("Negra");
		listaSex.add("Mestizo");
		listaSex.add("Otra");
		return listaSex;
	}

	public String getNombreEst(){
		return nombreEst;
	}

	public void setNombreEst(String nombreEst){
		this.nombreEst = nombreEst;
	}

	public String getFase(){
		return fase;
	}

	public void setFase(String fase){
		this.fase = fase;
	}

	public String getNotificador(){
		return notificador;
	}

	public void setNotificador(String notificador){
		this.notificador = notificador;
	}

	public String getEmail(){
		return email;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public Long getTelefono(){
		return telefono;
	}

	public void setTelefono(Long telefono){
		this.telefono = telefono;
	}

	public String getInstitucion(){
		return institucion;
	}

	public void setInstitucion(String institucion){
		this.institucion = institucion;
	}

	public String getProvincia(){
		return provincia;
	}

	public void setProvincia(String provincia){
		this.provincia = provincia;
	}

	public String getPaciente(){
		return paciente;
	}

	public void setPaciente(String paciente){
		this.paciente = paciente;
	}

	public String geteAdverso(){
		return eAdverso;
	}

	public void seteAdverso(String eAdverso){
		this.eAdverso = eAdverso;
	}

	public String getDesEAdverso(){
		return desEAdverso;
	}

	public void setDesEAdverso(String desEAdverso){
		this.desEAdverso = desEAdverso;
	}

	public String getProducInvs(){
		return producInvs;
	}

	public void setProducInvs(String producInvs){
		this.producInvs = producInvs;
	}

	public String getLote(){
		return lote;
	}

	public void setLote(String lote){
		this.lote = lote;
	}

	public String getFechaCadu(){
		return fechaCadu;
	}

	public void setFechaCadu(String fechaCadu){
		this.fechaCadu = fechaCadu;
	}

	public String getIniTratamiento(){
		return iniTratamiento;
	}

	public void setIniTratamiento(String iniTratamiento){
		this.iniTratamiento = iniTratamiento;
	}

	public String getFechaUltiDosisRecibida(){
		return fechaUltiDosisRecibida;
	}

	public void setFechaUltiDosisRecibida(String fechaUltiDosisRecibida){
		this.fechaUltiDosisRecibida = fechaUltiDosisRecibida;
	}

	public String getDosisAdmin(){
		return dosisAdmin;
	}

	public void setDosisAdmin(String dosisAdmin){
		this.dosisAdmin = dosisAdmin;
	}

	public String getFechaEvento(){
		return fechaEvento;
	}

	public void setFechaEvento(String fechaEvento){
		this.fechaEvento = fechaEvento;
	}

	public String getTiempoAparicion(){
		return tiempoAparicion;
	}

	public void setTiempoAparicion(String tiempoAparicion){
		this.tiempoAparicion = tiempoAparicion;
	}

	public String getSecuela(){
		return secuela;
	}

	public void setSecuela(String secuela){
		this.secuela = secuela;
	}

	public Integer getDiasHosp(){
		return diasHosp;
	}

	public void setDiasHosp(Integer diasHosp){
		this.diasHosp = diasHosp;
	}

	public String getMedicamento(){
		return medicamento;
	}

	public void setMedicamento(String medicamento){
		this.medicamento = medicamento;
	}

	public String getMedicamento1(){
		return medicamento1;
	}

	public void setMedicamento1(String medicamento1){
		this.medicamento1 = medicamento1;
	}

	public String getDosisDia(){
		return dosisDia;
	}

	public void setDosisDia(String dosisDia){
		this.dosisDia = dosisDia;
	}

	public String getDosisDia1(){
		return dosisDia1;
	}

	public void setDosisDia1(String dosisDia1){
		this.dosisDia1 = dosisDia1;
	}

	public String getFrecuencia(){
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia){
		this.frecuencia = frecuencia;
	}

	public String getFrecuencia1(){
		return frecuencia1;
	}

	public void setFrecuencia1(String frecuencia1){
		this.frecuencia1 = frecuencia1;
	}

	public String getViaAdmon(){
		return viaAdmon;
	}

	public void setViaAdmon(String viaAdmon){
		this.viaAdmon = viaAdmon;
	}

	public String getViaAdmon1(){
		return viaAdmon1;
	}

	public void setViaAdmon1(String viaAdmon1){
		this.viaAdmon1 = viaAdmon1;
	}

	public String getOtroEsp(){
		return otroEsp;
	}

	public void setOtroEsp(String otroEsp){
		this.otroEsp = otroEsp;
	}

	public String getOtroEsp1(){
		return otroEsp1;
	}

	public void setOtroEsp1(String otroEsp1){
		this.otroEsp1 = otroEsp1;
	}

	public String getReportSitio(){
		return reportSitio;
	}

	public void setReportSitio(String reportSitio){
		this.reportSitio = reportSitio;
	}

	public String getRevPromotor(){
		return revPromotor;
	}

	public void setRevPromotor(String revPromotor){
		this.revPromotor = revPromotor;
	}

	public String getRevRespFarma(){
		return revRespFarma;
	}

	public void setRevRespFarma(String revRespFarma){
		this.revRespFarma = revRespFarma;
	}

	public String getResponsabilidad(){
		return responsabilidad;
	}

	public void setResponsabilidad(String responsabilidad){
		this.responsabilidad = responsabilidad;
	}

	public String getResponsabilidad1(){
		return responsabilidad1;
	}

	public void setResponsabilidad1(String responsabilidad1){
		this.responsabilidad1 = responsabilidad1;
	}

	public String getResponsabilidad2(){
		return responsabilidad2;
	}

	public void setResponsabilidad2(String responsabilidad2){
		this.responsabilidad2 = responsabilidad2;
	}

	public String getViaA(){
		return viaA;
	}

	public void setViaA(String viaA){
		this.viaA = viaA;
	}

	public String getSeleccione(){
		return seleccione;
	}

	@SuppressWarnings("rawtypes")
	public List getVA(){
		return llenarVA();
	}

	public List<String> getVa(){
		return va;
	}

	public void setVa(List<String> va){
		this.va = va;
	}

	public ReReporteexpedito_ensayo getObjetoRepExpedito(){
		return objetoRepExpedito;
	}

	public void setObjetoRepExpedito(ReReporteexpedito_ensayo objetoRepExpedito){
		this.objetoRepExpedito = objetoRepExpedito;
	}

	public List<ReMedicacion_ensayo> getListaMedicacionConm(){
		return listaMedicacionConm;
	}

	public void setListaMedicacionConm(List<ReMedicacion_ensayo> listaMedicacionConm){
		this.listaMedicacionConm = listaMedicacionConm;
	}

	public List<ReMedicacion_ensayo> getListaMedicacionRec(){
		return listaMedicacionRec;
	}

	public void setListaMedicacionRec(List<ReMedicacion_ensayo> listaMedicacionRec){
		this.listaMedicacionRec = listaMedicacionRec;
	}

	public List<EFaseEstudio_ensayo> getFases(){
		return fases;
	}

	public void setFases(List<EFaseEstudio_ensayo> fases){
		this.fases = fases;
	}

	public String getEdad(){
		return edad;
	}

	public void setEdad(String edad){
		this.edad = edad;
	}

	public String getSexo(){
		return sexo;
	}

	public void setSexo(String sexo){
		this.sexo = sexo;
	}

	public String getPeso(){
		return peso;
	}

	public void setPeso(String peso){
		this.peso = peso;
	}

	public String getRaza(){
		return raza;
	}

	public void setRaza(String raza){
		this.raza = raza;
	}

	public UIInput FindComponent(String componentId){
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIInput component = (UIInput) viewRoot.findComponent(componentId);
		return component;
	}

	private long idGrupoP;

	/*public void grupoPos(long idGrupo, Integer pos){
		this.idGrupoP = idGrupo;
		this.row = pos;
		GrupoWrapper grupo = null;
		for (int i = 0; i < gestionarHoja.getListaGrupoW().size(); i++){
			if(gestionarHoja.getListaGrupoW().get(i).getGrupoVariables().getId() == idGrupoP){
				grupo = gestionarHoja.getListaGrupoW().get(i);
				break;
			}
		}
		
		for (VariableGrupoWrapper variable : grupo.getListaVarWrapper()){
			if (variable.getVariable().getDescripcionVariable()
					.equals("Evento Adverso")){
				variableRegla = variable.getVariable();
				break;
			}
		}

	}*/
	
	public String devolverTitulo(){
		return ((this.objetoRepExpedito != null) ? (this.editing ? SeamResourceBundle.getBundle().getString("modificarReporteE") : SeamResourceBundle.getBundle().getString("crearReporteE")) : SeamResourceBundle.getBundle().getString("crearReporteE"));		
	}
	
	private Long sectionId, groupId;
	private Integer repetition;
	private String keyData;
	private int index = -1;
	
	public String assignValues(Long sectionId, Long groupId, Integer repetition){
		this.cancelar();
		this.inicializado = false;
		if(sectionId != null && groupId != null && repetition != null){
			this.sectionId = sectionId;
			this.groupId = groupId;
			this.repetition = repetition;
			this.keyData = this.gestionarHoja.convertKey(sectionId, groupId);
			if(this.gestionarHoja.getMapWGD() != null && !this.gestionarHoja.getMapWGD().isEmpty() && this.gestionarHoja.getMapWGD().containsKey(this.keyData)){
				this.index = this.gestionarHoja.indexOfGroupData(this.gestionarHoja.getMapWGD().get(this.keyData), this.repetition);
				if(this.index != -1){
					this.variableRegla = this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).findVariable(this.sectionId, this.groupId, this.repetition, "Evento Adverso");
					return (this.variableRegla != null ? "access" : null);
				} 
			}
		}
		return null;
	}
	
	public void cancelar(){
		this.sectionId = null;
		this.groupId = null;
		this.repetition = null;
		this.variableRegla = null;
		this.objetoRepExpedito = null;
		this.keyData = null;
		this.index = -1;
	}

	// Persistiendo reporte expedito
	@SuppressWarnings("unchecked")
	public void persistir(){
		try {
			if (this.objetoRepExpedito == null)
				this.objetoRepExpedito = new ReReporteexpedito_ensayo();
			this.objetoRepExpedito.setVariable(this.variableRegla);			
			this.objetoRepExpedito.setCrdEspecifico(this.gestionarHoja.getHoja());
			this.objetoRepExpedito.setFechaCECMEDA(this.fechaReporteA);
			this.objetoRepExpedito.setFechaCECMEDB(this.fechaReporteB);
			this.objetoRepExpedito.setFechaCECMEDC(this.fechaReporteC);
			this.objetoRepExpedito.setFechaCECMEDD(this.fechaReporteD);
			this.objetoRepExpedito.setUsuario(this.usuario);
			if (this.informe != null)
				this.objetoRepExpedito.setInformeinicial(this.informe.equals(SeamResourceBundle.getBundle().getString("lbl_informeIni")));
			this.objetoRepExpedito.setPromotor(this.nombreapellido);
			this.objetoRepExpedito.setProductoinvestigado(this.producInvs);
			List<ReViasadmi_ensayo> viaAdm = (List<ReViasadmi_ensayo>) this.entityManager.createQuery("select v from ReViasadmi_ensayo v order by v.codigo").getResultList();
			for (int i = 0; i < viaAdm.size(); i++){
				if (viaAdm.get(i).getDescripcion().equals(this.viaA)){
					this.objetoRepExpedito.setReViasadmi(viaAdm.get(i));
					break;
				}
			}
			this.objetoRepExpedito.setDosisadministrada(this.dosisAdmin);
			this.objetoRepExpedito.setPresentacion(this.presentacion);
			this.objetoRepExpedito.setInvestigador(this.investigador);
			if (!this.autopsia.equals(""))
				this.objetoRepExpedito.setAutopsia(!this.autopsia.equals("No"));
			if (!this.fechaIngreso.equals(""))
				this.objetoRepExpedito.setFechaingreso(this.fechaIngreso);
			if (!this.fechaAlta.equals(""))
				this.objetoRepExpedito.setFechaalta(this.fechaAlta);
			if (!this.fechaAltaSiProlonga.equals("") || this.fechaAltaSiProlonga != null)
				this.objetoRepExpedito.setFechaaltaprol(this.fechaAltaSiProlonga);
			this.objetoRepExpedito.setEspecifiqueacont(this.especifiqueAcont);
			this.objetoRepExpedito.setEspecifiqueotros(this.especifiqueOtros);
			this.objetoRepExpedito.setNombrereporta(this.notificador);
			this.objetoRepExpedito.setDireccion(this.direccion);
			this.objetoRepExpedito.setProfesion(this.profesion);
			this.objetoRepExpedito.setFechareporteA(this.fechaReporte1);
			this.objetoRepExpedito.setFechareporteB(this.fechaReporte2);
			this.objetoRepExpedito.setFechareporteC(this.fechaReporte3);
			this.objetoRepExpedito.setFechareporteD(this.fechaReporte4);
			this.objetoRepExpedito.setFechareportee(this.fechaReporte5);
			this.objetoRepExpedito.setIdSujeto(this.gestionarHoja.getSujetoIncluido().getId());
			this.objetoRepExpedito.setIdEstudio(this.estudioE.getId());
			this.objetoRepExpedito.setIdEntidad(this.estudioE.getEntidad().getId());
			this.objetoRepExpedito.setFechanacimiento(this.fechaNac);
			this.objetoRepExpedito.setSexo(this.sexo);
			this.objetoRepExpedito.setPeso(Float.parseFloat(this.peso));
			this.objetoRepExpedito.setTalla(Float.parseFloat(this.talla));
			this.objetoRepExpedito.setRaza(this.raza);
			this.objetoRepExpedito.setOtraraza(this.especifiqueOtra);
			this.objetoRepExpedito.setEventoadverso(this.eAdverso);
			this.objetoRepExpedito.setFechainicio(this.fechaInicio);
			this.objetoRepExpedito.setHorainicio(this.horaInicio);
			this.objetoRepExpedito.setFechafinalizacion(this.fechaFinalizacion);
			this.objetoRepExpedito.setHorafinalizacion(this.horaFinalizacion);
			for (int i = 0; i < this.listaIntensidad.size(); i++){
				if (this.listaIntensidad.get(i).getCodigo().toString().equals(this.intensidad)){
					this.objetoRepExpedito.setReIntensidad(this.listaIntensidad.get(i));
					break;
				}
			}
			this.objetoRepExpedito.setDondeocurrio(this.lugar);
			this.objetoRepExpedito.setOtrolugar(this.lugarOtrosEsp);
			for (int i = 0; i < this.listaConducta.size(); i++){
				if (this.listaConducta.get(i).getDescripcion().equals(this.accion)){
					this.objetoRepExpedito.setReCrealizadaeas(this.listaConducta.get(i));
					break;
				}
			}
			this.objetoRepExpedito.setAcontecimiento(this.acontecimiento);
			this.objetoRepExpedito.setCausalidad(this.causalidad);
			this.objetoRepExpedito.setFuente(this.fuente);
			this.objetoRepExpedito.setOtrafuente(this.fuenteOtra);
			if (this.tipoResultado != null){
				for (int i = 0; i < this.listaResultado.size(); i++){
					if (this.listaResultado.get(i).getDescripcion().equals(this.tipoResultado)){
						this.objetoRepExpedito.setReResultadofinal(this.listaResultado.get(i));
						break;
					}
				}
			}
			this.objetoRepExpedito.setAbndonoelsujeto(!this.abandonado.equals("No"));
			this.objetoRepExpedito.setTerminotratamiento(!this.terminoTrat.equals("No"));
			this.objetoRepExpedito.setIndicacion(this.indicacion);
			this.objetoRepExpedito.setTratamientoquirurgico(!this.tratamientoQuir.equals("No"));
			this.objetoRepExpedito.setEspecifiquetrat(this.especTrat);
			this.objetoRepExpedito.setDescripciongeneral(this.lbldescripcion);
			this.objetoRepExpedito.setNombreinvestigador(this.nombreInvestigador);
			this.objetoRepExpedito.setFechareportepromotor(this.fechaReportepromotor);
			this.objetoRepExpedito.setHorareportepromotor(this.horaReportepromotor);			
			EFaseEstudio_ensayo tempFaseEst = null;
			if(this.estudioE.getEFaseEstudio() == null && !this.fase.equals("")){				
				for (int i = 0; i < this.fases.size(); i++){
					if (this.fases.get(i).getNombre().equals(this.fase)){
						tempFaseEst = this.fases.get(i);
						break;
					}
				}
			}			
			if(this.sectionId != null && this.groupId != null && this.repetition != null && this.keyData != null && this.index != -1 && this.variableRegla != null){
				MapWrapperDataPlus tempData = ((this.gestionarHoja.getMapWGD() != null && !this.gestionarHoja.getMapWGD().isEmpty() && this.gestionarHoja.getMapWGD().containsKey(this.keyData) && this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().containsKey(this.variableRegla.getId())) ? this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()) : null);
				if(tempData != null){
					VariableDato_ensayo tempVariableData = ((tempData.getVariableData() != null) ? tempData.getVariableData() : new VariableDato_ensayo());
					tempVariableData.setVariable(tempData.getVariable());
					tempVariableData.setValor((tempData.getValue() != null) ? (String) tempData.getValue() : "");
					tempVariableData.setCrdEspecifico(this.gestionarHoja.getHoja());
					tempVariableData.setContGrupo(this.repetition);
					tempVariableData.setEliminado(false);
					this.objetoRepExpedito.setVariableDato(tempVariableData);
					if(this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport() == null)
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).setReport(new WrapperReReporteexpedito());
					if(this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport() == null)
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).setReport(new WrapperReReporteexpedito());
					this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setReport(this.objetoRepExpedito);
					this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setReport(this.objetoRepExpedito);					
					this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setModified(this.editing);
					this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setModified(this.editing);
					this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setFase(tempFaseEst);
					this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setFase(tempFaseEst);
					if(this.consecuencia != null && this.consecuencia.length > 0){
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setConsecuencias(this.consecuencia);
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setConsecuencias(this.consecuencia);
					}
					if(this.listaProductosEstudios != null && !this.listaProductosEstudios.isEmpty()){
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setProductosEstudios(this.listaProductosEstudios);
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setProductosEstudios(this.listaProductosEstudios);
					}
					if(this.listaMedicacionConm != null && !this.listaMedicacionConm.isEmpty()){
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setMedicacionConcomitante(this.listaMedicacionConm);
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setMedicacionConcomitante(this.listaMedicacionConm);
					}
					if(this.listaEnfermedadesInter != null && !this.listaEnfermedadesInter.isEmpty()){
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setEnfermedadesInter(this.listaEnfermedadesInter);
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setEnfermedadesInter(this.listaEnfermedadesInter);
					}
					if(this.listaMedicacionRec != null && !this.listaMedicacionRec.isEmpty()){
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getReport().setMedicacionReciente(this.listaMedicacionRec);
						this.gestionarHoja.getMapWGD().get(this.keyData).get(this.index).getData().get(this.variableRegla.getId()).getReport().setMedicacionReciente(this.listaMedicacionRec);
					}
					this.setCompleted(true);
				}				
			}
					
			
			if (usuarioCambiar.getTelefono().equals("") || usuarioCambiar.getTelefono() == null){
				if (!this.email.equals("")){
					usuarioCambiar.setTelefono(email);
					entityManager.persist(usuarioCambiar);
				}
			}
			entityManager.flush();							
		} catch (Exception e){}
	}

	@Override
	public String Export(){
		return null;
	}

	@Override
	public boolean CanExport(){
		return false;
	}

	public String getIntensidad(){
		return intensidad;
	}

	public void setIntensidad(String intensidad){
		this.intensidad = intensidad;
	}

	public String getRelacion(){
		return relacion;
	}

	public void setRelacion(String relacion){
		this.relacion = relacion;
	}

	public String getConductaEAS(){
		return conductaEAS;
	}

	public void setConductaEAS(String conductaEAS){
		this.conductaEAS = conductaEAS;
	}

	public String getModificacionI(){
		return modificacionI;
	}

	public void setModificacionI(String modificacionI){
		this.modificacionI = modificacionI;
	}

	public String getReaparecio(){
		return reaparecio;
	}

	public void setReaparecio(String reaparecio){
		this.reaparecio = reaparecio;
	}

	public String getDatosLab(){
		return datosLab;
	}

	public void setDatosLab(String datosLab){
		this.datosLab = datosLab;
	}

	public String getCopiaBiopsia(){
		return copiaBiopsia;
	}

	public void setCopiaBiopsia(String copiaBiopsia){
		this.copiaBiopsia = copiaBiopsia;
	}

	public String getCopiaAutopsia(){
		return copiaAutopsia;
	}

	public void setCopiaAutopsia(String copiaAutopsia){
		this.copiaAutopsia = copiaAutopsia;
	}

	public String getTipoResultado(){
		return tipoResultado;
	}

	public void setTipoResultado(String tipoResultado){
		this.tipoResultado = tipoResultado;
	}

	public String getAutopsia(){
		return autopsia;
	}

	public void setAutopsia(String autopsia){
		this.autopsia = autopsia;
	}

	public String getDias(){
		return dias;
	}

	public void setDias(String dias){
		this.dias = dias;
	}

	public String[] getHistoria(){
		return historia;
	}

	public void setHistoria(String[] historia){
		this.historia = historia;
	}

	public List<ReIntensidad_ensayo> getListaIntensidad(){
		return listaIntensidad;
	}

	public void setListaIntensidad(List<ReIntensidad_ensayo> listaIntensidad){
		this.listaIntensidad = listaIntensidad;
	}

	public List<ReConsecuenciaspaciente_ensayo> getListaConsecuencias(){
		return listaConsecuencias;
	}

	public void setListaConsecuencias(List<ReConsecuenciaspaciente_ensayo> listaConsecuencias){
		this.listaConsecuencias = listaConsecuencias;
	}

	public List<ReRecausalpea_ensayo> getListaRelacion(){
		return listaRelacion;
	}

	public void setListaRelacion(List<ReRecausalpea_ensayo> listaRelacion){
		this.listaRelacion = listaRelacion;
	}

	public List<ReEnfermedades_ensayo> getListaEnfermedades(){
		return listaEnfermedades;
	}

	public void setListaEnfermedades(List<ReEnfermedades_ensayo> listaEnfermedades){
		this.listaEnfermedades = listaEnfermedades;
	}

	public ReTrazabilidadreporte_ensayo getTrazabilidaSitioInv(){
		return trazabilidaSitioInv;
	}

	public void setTrazabilidaSitioInv(ReTrazabilidadreporte_ensayo trazabilidaSitioInv){
		this.trazabilidaSitioInv = trazabilidaSitioInv;
	}

	public ReTrazabilidadreporte_ensayo getTrazabilidaPromotor(){
		return trazabilidaPromotor;
	}

	public void setTrazabilidaPromotor(ReTrazabilidadreporte_ensayo trazabilidaPromotor){
		this.trazabilidaPromotor = trazabilidaPromotor;
	}

	public ReTrazabilidadreporte_ensayo getTrazabilidaResponsable(){
		return trazabilidaResponsable;
	}

	public void setTrazabilidaResponsable(ReTrazabilidadreporte_ensayo trazabilidaResponsable){
		this.trazabilidaResponsable = trazabilidaResponsable;
	}

	public String getNoDosis(){
		return noDosis;
	}

	public void setNoDosis(String noDosis){
		this.noDosis = noDosis;
	}

	public boolean isInicializado(){
		return inicializado;
	}

	public void setInicializado(boolean inicializado){
		this.inicializado = inicializado;
	}

	public Variable_ensayo getVariableRegla(){
		return variableRegla;
	}

	public void setVariableRegla(Variable_ensayo variableRegla){
		this.variableRegla = variableRegla;
	}

	public String getCantDosis(){
		return cantDosis;
	}

	public void setCantDosis(String cantDosis){
		this.cantDosis = cantDosis;
	}

	public Variable_ensayo getVariablereglaGlobal(){
		return variablereglaGlobal;
	}

	public void setVariablereglaGlobal(Variable_ensayo variablereglaGlobal){
		this.variablereglaGlobal = variablereglaGlobal;
	}

	public Long getIdcrd(){
		return idcrd;
	}

	public void setIdcrd(Long idcrd){
		this.idcrd = idcrd;
	}

	public String getPresentacion(){
		return presentacion;
	}

	public void setPresentacion(String presentacion){
		this.presentacion = presentacion;
	}

	public String getInvestigador(){
		return investigador;
	}

	public void setInvestigador(String investigador){
		this.investigador = investigador;
	}

	public String getInforme(){
		return informe;
	}

	public void setInforme(String informe){
		this.informe = informe;
	}

	public String[] getConsecuencia(){
		return consecuencia;
	}

	public void setConsecuencia(String[] consecuencia){
		this.consecuencia = consecuencia;
	}

	public String getFechaIngreso(){
		return fechaIngreso;
	}

	public void setFechaIngreso(String fechaIngreso){
		this.fechaIngreso = fechaIngreso;
	}

	public String getFechaAlta(){
		return fechaAlta;
	}

	public void setFechaAlta(String fechaAlta){
		this.fechaAlta = fechaAlta;
	}

	public String getFechaAltaSiProlonga(){
		return fechaAltaSiProlonga;
	}

	public void setFechaAltaSiProlonga(String fechaAltaSiProlonga){
		this.fechaAltaSiProlonga = fechaAltaSiProlonga;
	}

	public String getEspecifiqueAcont(){
		return especifiqueAcont;
	}

	public void setEspecifiqueAcont(String especifiqueAcont){
		this.especifiqueAcont = especifiqueAcont;
	}

	public String getEspecifiqueOtros(){
		return especifiqueOtros;
	}

	public void setEspecifiqueOtros(String especifiqueOtros){
		this.especifiqueOtros = especifiqueOtros;
	}

	public String getDireccion(){
		return direccion;
	}

	public void setDireccion(String direccion){
		this.direccion = direccion;
	}

	public String getFechaReporte1(){
		return fechaReporte1;
	}

	public void setFechaReporte1(String fechaReporte1){
		this.fechaReporte1 = fechaReporte1;
	}

	public String getFechaReporte2(){
		return fechaReporte2;
	}

	public void setFechaReporte2(String fechaReporte2){
		this.fechaReporte2 = fechaReporte2;
	}

	public String getFechaReporte3(){
		return fechaReporte3;
	}

	public void setFechaReporte3(String fechaReporte3){
		this.fechaReporte3 = fechaReporte3;
	}

	public String getFechaReporte4(){
		return fechaReporte4;
	}

	public void setFechaReporte4(String fechaReporte4){
		this.fechaReporte4 = fechaReporte4;
	}

	public String getProfesion(){
		return profesion;
	}

	public void setProfesion(String profesion){
		this.profesion = profesion;
	}

	public String getIniciales(){
		return iniciales;
	}

	public void setIniciales(String iniciales){
		this.iniciales = iniciales;
	}

	public String getFechaNac(){
		return fechaNac;
	}

	public void setFechaNac(String fechaNac){
		this.fechaNac = fechaNac;
	}

	public String getTalla(){
		return talla;
	}

	public void setTalla(String talla){
		this.talla = talla;
	}

	public String getEspecifiqueOtra(){
		return especifiqueOtra;
	}

	public void setEspecifiqueOtra(String especifiqueOtra){
		this.especifiqueOtra = especifiqueOtra;
	}

	public String getFechaInicio(){
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio){
		this.fechaInicio = fechaInicio;
	}

	public String getHoraInicio(){
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio){
		this.horaInicio = horaInicio;
	}
	
	public String getMinutosInicio() {
		return minutosInicio;
	}

	public void setMinutosInicio(String minutosInicio) {
		this.minutosInicio = minutosInicio;
	}

	public String getAm_pmInicio() {
		return am_pmInicio;
	}

	public void setAm_pmInicio(String am_pmInicio) {
		this.am_pmInicio = am_pmInicio;
	}

	public String getFechaFinalizacion(){
		return fechaFinalizacion;
	}

	public void setFechaFinalizacion(String fechaFinalizacion){
		this.fechaFinalizacion = fechaFinalizacion;
	}

	public String getHoraFinalizacion(){
		return horaFinalizacion;
	}

	public void setHoraFinalizacion(String horaFinalizacion){
		this.horaFinalizacion = horaFinalizacion;
	}
	
	public String getMinutosFinalizacion(){
		return minutosFinalizacion;
	}
	
	public void setMinutosFinalizacion(String minutosFinalizacion){
		this.minutosFinalizacion = minutosFinalizacion;
	}
	
	public String getAm_pmFinalizacion(){
		return am_pmFinalizacion;
	}
	
	public void setAm_pmFinalizacion(String am_pmFinalizacion){
		this.am_pmFinalizacion = am_pmFinalizacion;
	}

	public String getLugar(){
		return lugar;
	}

	public void setLugar(String lugar){
		this.lugar = lugar;
	}

	public String getLugarOtrosEsp(){
		return lugarOtrosEsp;
	}

	public void setLugarOtrosEsp(String lugarOtrosEsp){
		this.lugarOtrosEsp = lugarOtrosEsp;
	}

	public String getAccion(){
		return accion;
	}

	public void setAccion(String accion){
		this.accion = accion;
	}

	public String getAcontecimiento(){
		return acontecimiento;
	}

	public void setAcontecimiento(String acontecimiento){
		this.acontecimiento = acontecimiento;
	}

	public String getCausalidad(){
		return causalidad;
	}

	public void setCausalidad(String causalidad){
		this.causalidad = causalidad;
	}

	public String getFuente(){
		return fuente;
	}

	public void setFuente(String fuente){
		this.fuente = fuente;
	}

	public String getFuenteOtra(){
		return fuenteOtra;
	}

	public void setFuenteOtra(String fuenteOtra){
		this.fuenteOtra = fuenteOtra;
	}

	public String getAbandonado(){
		return abandonado;
	}

	public void setAbandonado(String abandonado){
		this.abandonado = abandonado;
	}

	public String getCodigoEst(){
		return codigoEst;
	}

	public void setCodigoEst(String codigoEst){
		this.codigoEst = codigoEst;
	}

	public String getFechaReporteA(){
		return fechaReporteA;
	}

	public String getFechaReporteB(){
		return fechaReporteB;
	}

	public String getFechaReporteC(){
		return fechaReporteC;
	}

	public String getFechaReporteD(){
		return fechaReporteD;
	}

	public void setFechaReporteA(String fechaReporteA){
		this.fechaReporteA = fechaReporteA;
	}

	public void setFechaReporteB(String fechaReporteB){
		this.fechaReporteB = fechaReporteB;
	}

	public void setFechaReporteC(String fechaReporteC){
		this.fechaReporteC = fechaReporteC;
	}

	public void setFechaReporteD(String fechaReporteD){
		this.fechaReporteD = fechaReporteD;
	}

	public String getPresentacionVac(){
		return presentacionVac;
	}

	public void setPresentacionVac(String presentacionVac){
		this.presentacionVac = presentacionVac;
	}

	public String getLoteVac(){
		return loteVac;
	}

	public void setLoteVac(String loteVac){
		this.loteVac = loteVac;
	}

	public String getFechaVac(){
		return fechaVac;
	}
	public String getMinutosVac() {
		return minutosVac;
	}

	public void setMinutosVac(String minutosVac) {
		this.minutosVac = minutosVac;
	}

	public String getAm_pmVac(){
		return am_pmVac;
	}

	public void setFechaVac(String fechaVac){
		this.fechaVac = fechaVac;
	}

		
	public String getHoraVac() {
		return horaVac;
	}

	public void setHoraVac(String horaVac) {
		this.horaVac = horaVac;
	}

	public void setAm_pmVac(String am_pmVac){
		this.am_pmVac = am_pmVac;
	}

	public List<ReMedicacion_ensayo> getListaProductosEstudios(){
		return listaProductosEstudios;
	}

	public void setListaProductosEstudios(List<ReMedicacion_ensayo> listaProductosEstudios){
		this.listaProductosEstudios = listaProductosEstudios;
	}

	public String getMedicamentoP(){
		return medicamentoP;
	}

	public String getLoteVacP(){
		return loteVacP;
	}

	public String getDosisDiaP(){
		return dosisDiaP;
	}

	public String getFrecuenciaP(){
		return frecuenciaP;
	}

	public String getViaAdmonP(){
		return viaAdmonP;
	}

	public String getFechaVacP(){
		return fechaVacP;
	}

	public String getAm_pmVacP(){
		return am_pmVacP;
	}
	
	public String getHoraVacP() {
		return horaVacP;
	}

	public void setHoraVacP(String horaVacP) {
		this.horaVacP = horaVacP;
	}

	public String getMinutosVacP() {
		return minutosVacP;
	}

	public void setMinutosVacP(String minutosVacP) {
		this.minutosVacP = minutosVacP;
	}

	public void setMedicamentoP(String medicamentoP){
		this.medicamentoP = medicamentoP;
	}

	public void setLoteVacP(String loteVacP){
		this.loteVacP = loteVacP;
	}

	public void setDosisDiaP(String dosisDiaP){
		this.dosisDiaP = dosisDiaP;
	}

	public void setFrecuenciaP(String frecuenciaP){
		this.frecuenciaP = frecuenciaP;
	}

	public void setViaAdmonP(String viaAdmonP){
		this.viaAdmonP = viaAdmonP;
	}

	public void setFechaVacP(String fechaVacP){
		this.fechaVacP = fechaVacP;
	}

	public void setAm_pmVacP(String am_pmVacP){
		this.am_pmVacP = am_pmVacP;
	}

	public String getTerminoTrat(){
		return terminoTrat;
	}

	public void setTerminoTrat(String terminoTrat){
		this.terminoTrat = terminoTrat;
	}

	public String getIndicacion(){
		return indicacion;
	}

	public void setIndicacion(String indicacion){
		this.indicacion = indicacion;
	}

	public String getEnfermedad(){
		return enfermedad;
	}

	public String getPersiste(){
		return persiste;
	}

	public void setEnfermedad(String enfermedad){
		this.enfermedad = enfermedad;
	}

	public void setPersiste(String persiste){
		this.persiste = persiste;
	}

	public List<ReMedicacion_ensayo> getListaEnfermedadesInter(){
		return listaEnfermedadesInter;
	}

	public void setListaEnfermedadesInter(List<ReMedicacion_ensayo> listaEnfermedadesInter){
		this.listaEnfermedadesInter = listaEnfermedadesInter;
	}

	public String getMedicamentoF(){
		return medicamentoF;
	}

	public String getDosisDiaF(){
		return dosisDiaF;
	}

	public String getFrecuenciaF(){
		return frecuenciaF;
	}

	public String getViaAdmonF(){
		return viaAdmonF;
	}

	public String getFechaVacF(){
		return fechaVacF;
	}

	public String getHoraVacF(){
		return horaVacF;
	}
	
	public String getMinutosVacF(){
		return minutosVacF;
	}
	
	public String getAm_pmVacF(){
		return am_pmVacF;
	}

	public void setMedicamentoF(String medicamentoF){
		this.medicamentoF = medicamentoF;
	}

	public void setDosisDiaF(String dosisDiaF){
		this.dosisDiaF = dosisDiaF;
	}

	public void setFrecuenciaF(String frecuenciaF){
		this.frecuenciaF = frecuenciaF;
	}

	public void setViaAdmonF(String viaAdmonF){
		this.viaAdmonF = viaAdmonF;
	}

	public void setFechaVacF(String fechaVacF){
		this.fechaVacF = fechaVacF;
	}

	public void setHoraVacF(String horaVacF){
		this.horaVacF = horaVacF;
	}
	
	public void setMinutosVacF(String minutosVacF){
		this.minutosVacF = minutosVacF;
	}
	
	public void setAm_pmVacF(String am_pmVacF){
		this.am_pmVacF = am_pmVacF;
	}

	public String getTratamientoQuir(){
		return tratamientoQuir;
	}

	public void setTratamientoQuir(String tratamientoQuir){
		this.tratamientoQuir = tratamientoQuir;
	}

	public String getEspecTrat(){
		return especTrat;
	}

	public void setEspecTrat(String especTrat){
		this.especTrat = especTrat;
	}

	public String getLbldescripcion(){
		return lbldescripcion;
	}

	public void setLbldescripcion(String lbldescripcion){
		this.lbldescripcion = lbldescripcion;
	}

	@Override
	public void Process(){
	}

	public String getNombreapellido(){
		return nombreapellido;
	}

	public void setNombreapellido(String nombreapellido){
		this.nombreapellido = nombreapellido;
	}

	public long getIdGrupoP(){
		return idGrupoP;
	}

	public void setIdGrupoP(long idGrupoP){
		this.idGrupoP = idGrupoP;
	}

	public String getFechaReportepromotor(){
		return fechaReportepromotor;
	}

	public void setFechaReportepromotor(String fechaReportepromotor){
		this.fechaReportepromotor = fechaReportepromotor;
	}

	public String getHoraReportepromotor(){
		return horaReportepromotor;
	}
	
	public String getMinutosReportepromotor(){
		return minutosReportepromotor;
	}
	
	public String getAm_pmReportepromotor(){
		return am_pmReportepromotor;
	}

	public void setHoraReportepromotor(String horaReportepromotor){
		this.horaReportepromotor = horaReportepromotor;
	}

	public void setMinutosReportepromotor(String minutosReportepromotor){
		this.minutosReportepromotor = minutosReportepromotor;
	}
	
	public void setAm_pmReportepromotor(String am_pmReportepromotor){
		this.am_pmReportepromotor = am_pmReportepromotor;
	}
	
	public String getFechaReporte5(){
		return fechaReporte5;
	}

	public void setFechaReporte5(String fechaReporte5){
		this.fechaReporte5 = fechaReporte5;
	}

	public String getNombreInvestigador(){
		return nombreInvestigador;
	}

	public void setNombreInvestigador(String nombreInvestigador){
		this.nombreInvestigador = nombreInvestigador;
	}

	public String getFrom(){
		return from;
	}

	public void setFrom(String from){
		this.from = from;
	}
	
	public boolean isEditing(){
		return editing;
	}
	
	public void setEditing(boolean editing){
		this.editing = editing;
	}

	public Usuario_ensayo getUsuarioCambiar() {
		return usuarioCambiar;
	}

	public void setUsuarioCambiar(Usuario_ensayo usuarioCambiar) {
		this.usuarioCambiar = usuarioCambiar;
	}

}