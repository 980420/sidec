//CU 13 Gestionar datos de hoja CRD
package gehos.ensayo.ensayo_disenno.session.reporteExpedito;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
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
import gehos.ensayo.entity.Variable_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("reporteExpeditoConduccion")
@Scope(ScopeType.CONVERSATION)
public class ReporteExpeditoConduccion extends IActionWithUserInput {

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	protected @In(create = true) FacesMessages facesMessages;
	@In(create = true)
	IdUtil idUtil;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;
	@In
	private IActiveModule activeModule;
	@In(value = "gestionarHoja", required = false)
	GestionarHoja gestionarHoja;
	private long cid = -1;

	CrdEspecifico_ensayo objetoGestionarHoja;

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
	
	private Usuario_ensayo usuario = new Usuario_ensayo();

	private String nombreEst = "";
	private String fase = "";
	private String notificador = "";
	private String email = "";
	private Long telefono;
	private String institucion = "";
	private String provincia = "";

	private String paciente = "";

	private String eAdverso = "";
	private String noDosis = "";
	private String desEAdverso = "";
	private String producInvs = "";
	private String lote = "";
	private String fechaCadu = "";
	private String intensidad = "";
	private String consecuencia = "";
	private String conductaEAS = "";
	private String modificacionI = "";
	private String tipoResultado = "";
	private String reaparecio;
	private String[] historia = new String[10];
	private String datosLab = "";
	private String copiaBiopsia = "";
	private String copiaAutopsia = "";
	private String autopsia = "";
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

	String medicamento = "";
	String medicamento1 = "";
	String dosisDia;
	String dosisDia1;
	String frecuencia = "";
	String frecuencia1 = "";
	String viaAdmon = "";
	String viaAdmon1 = "";
	String fechaIni;
	String fechaIni1;
	String fechaFin;
	String fechaFin1;
	String indicacion = "";
	String indicacion1 = "";

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

	private final String seleccione = SeamResourceBundle.getBundle().getString(
			"cbxSeleccionPorDefecto");

	private Variable_ensayo variableRegla;
	private ReReporteexpedito_ensayo objetoRepExpedito;

	private List<ReMedicacion_ensayo> listaMedicacionConm = new ArrayList<ReMedicacion_ensayo>();
	private List<ReMedicacion_ensayo> listaMedicacionRec = new ArrayList<ReMedicacion_ensayo>();
	private Variable_ensayo variablereglaGlobal = null;

	private Long sectionId, groupId;
	private Integer repetition;
	private int index;
	
	public void assignValues(Long sectionId, Long groupId, Integer repetition, int index){
		this.sectionId = null;
		this.groupId = null;
		this.repetition = null;
		this.index = -1;
		this.inicializado = false;
		if(sectionId != null && groupId != null && repetition != null && index != -1){
			this.sectionId = sectionId;
			this.groupId = groupId;
			this.repetition = repetition;
			this.index = index;
		}
	}

	@SuppressWarnings("unchecked")
	public void initConversation(){
		if(this.sectionId != null && this.groupId != null && this.repetition != null && this.index != -1 && this.variableRegla != null){
			this.usuario = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, user.getId());
			this.objetoGestionarHoja = this.gestionarHoja.getHoja();
			this.objetoRepExpedito = new ReReporteexpedito_ensayo();
			this.estudioE = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio();
			this.nombreEst = this.estudioE.getNombre();
			try {
				this.objetoRepExpedito = this.gestionarHoja.getMapWGD().get(this.gestionarHoja.convertKey(this.sectionId, this.groupId)).get(this.index).getData().get(this.variableRegla.getId()).getReport().getReport();
			} catch (Exception e){
				this.objetoRepExpedito = null;
			}
			if(this.objetoRepExpedito != null){
				this.edad = this.objetoRepExpedito.getEdad();
				this.sexo = this.objetoRepExpedito.getSexo();
				this.peso = this.objetoRepExpedito.getPeso().toString();
				this.raza = this.objetoRepExpedito.getRaza();
				this.eAdverso = this.objetoRepExpedito.getEventoadverso();
				if (this.objetoRepExpedito.getDescripcionea() != null)
					this.desEAdverso = objetoRepExpedito.getDescripcionea();
				this.intensidad = this.objetoRepExpedito.getReIntensidad().getValor();
				this.consecuencia = this.objetoRepExpedito.getReConsecuenciaspaciente().getDescripcion();
				this.relacion = this.objetoRepExpedito.getReRecausalpea().getDescripcion();
				this.producInvs = this.objetoRepExpedito.getProductoinvestigado();
				this.lote = this.objetoRepExpedito.getLote();
				if (this.objetoRepExpedito.getFechacaducidad() != null)
					this.fechaCadu = objetoRepExpedito.getFechacaducidad();
				this.iniTratamiento = objetoRepExpedito.getIniciotratamiento();
				if (this.objetoRepExpedito.getDosisrecibida() != null)
					this.cantDosis = this.objetoRepExpedito.getDosisrecibida().toString();
				if (this.objetoRepExpedito.getUltimadosis() != null)
					this.fechaUltiDosisRecibida = this.objetoRepExpedito.getUltimadosis();
				this.dosisAdmin = this.objetoRepExpedito.getDosisadministrada();
				this.viaA = this.objetoRepExpedito.getReViasadmi().getDescripcion();
				this.fechaEvento = this.objetoRepExpedito.getFechareporteA();
				if (this.objetoRepExpedito.getNo_dosis() != null)
					this.noDosis = this.objetoRepExpedito.getNo_dosis();
				if (this.objetoRepExpedito.getTiempoaparicion() != null)
					this.tiempoAparicion = this.objetoRepExpedito.getTiempoaparicion();
				if (this.objetoRepExpedito.getReCrealizadaeas() != null)
					this.conductaEAS = this.objetoRepExpedito.getReCrealizadaeas().getDescripcion();
				if (this.objetoRepExpedito.getReRespuestaByModificacionintensidad() != null)
					this.modificacionI = this.objetoRepExpedito.getReRespuestaByModificacionintensidad().getDescripcion();
				this.reaparecio = this.objetoRepExpedito.getReRespuestaByReaparicionea().getDescripcion();
				if (this.objetoRepExpedito.getSecuela() != null)
					this.secuela = this.objetoRepExpedito.getSecuela();
				if (this.objetoRepExpedito.getReResultadofinal() != null)
					this.tipoResultado = this.objetoRepExpedito.getReResultadofinal().getDescripcion();
				if (this.objetoRepExpedito.getDiashospitalizacion() != null)
					this.dias = this.objetoRepExpedito.getDiashospitalizacion().toString();
				if (this.objetoRepExpedito.getAutopsia() != null)
					this.autopsia = (this.objetoRepExpedito.getAutopsia() ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no"));
				this.listaMedicacionConm = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionconcomitante_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False").setParameter("idReporteexpedito", this.objetoRepExpedito.getId()).getResultList();
				this.listaMedicacionRec = (List<ReMedicacion_ensayo>) this.entityManager.createQuery("select medicacion.reMedicacion from ReMedicacionreciente_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False").setParameter("idReporteexpedito", this.objetoRepExpedito.getId()).getResultList();
				List<ReHistoriarelevante_ensayo> listaHistoria = (List<ReHistoriarelevante_ensayo>) this.entityManager.createQuery("select historia from ReHistoriarelevante_ensayo historia where historia.reReporteexpedito.id=:idReporteexpedito").setParameter("idReporteexpedito", this.objetoRepExpedito.getId()).getResultList();
				for (int i = 0; i < listaHistoria.size(); i++) {
					ReEnfermedades_ensayo enfermedad = this.entityManager.find(ReEnfermedades_ensayo.class, listaHistoria.get(i).getIdReEnfermedades());
					this.historia[i] = enfermedad.getDescripcion();
				}
				if (this.objetoRepExpedito.getOtroshistoria() != null)
					this.otroEsp = this.objetoRepExpedito.getOtroshistoria();
				if (this.objetoRepExpedito.getOtroscomentarios() != null)
					this.otroEsp1 = this.objetoRepExpedito.getOtroscomentarios();
				if (this.objetoRepExpedito.getDatoslaboratorio() != null)
					this.datosLab = (this.objetoRepExpedito.getDatoslaboratorio() ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no"));
				if (this.objetoRepExpedito.getCopiabiopsia() != null)
					this.copiaBiopsia = (this.objetoRepExpedito.getCopiabiopsia() ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no"));					
				if (this.objetoRepExpedito.getCopiaautopsia() != null)
					this.copiaAutopsia = (this.objetoRepExpedito.getCopiaautopsia() ? SeamResourceBundle.getBundle().getString("lbl_si") : SeamResourceBundle.getBundle().getString("lbl_no"));
				try {
					this.trazabilidaSitioInv = (ReTrazabilidadreporte_ensayo) this.entityManager.createQuery("select reporte from ReTrazabilidadreporte_ensayo reporte where reporte.reReporteexpedito.id=:idReporteexpedito and reporte.codigo = 'sitioInv'").setParameter("idReporteexpedito", this.objetoRepExpedito.getId()).getSingleResult();
				} catch (Exception e){
					this.trazabilidaSitioInv = new ReTrazabilidadreporte_ensayo();
				}
				try {
					this.trazabilidaPromotor = (ReTrazabilidadreporte_ensayo) this.entityManager.createQuery("select reporte from ReTrazabilidadreporte_ensayo reporte where reporte.reReporteexpedito.id=:idReporteexpedito and reporte.codigo = 'revProm'").setParameter("idReporteexpedito", this.objetoRepExpedito.getId()).getSingleResult();
				} catch (Exception e){
					this.trazabilidaPromotor = new ReTrazabilidadreporte_ensayo();
				}
				try {
					this.trazabilidaResponsable = (ReTrazabilidadreporte_ensayo) this.entityManager.createQuery("select reporte from ReTrazabilidadreporte_ensayo reporte where reporte.reReporteexpedito.id=:idReporteexpedito and reporte.codigo = 'revResp'").setParameter("idReporteexpedito", this.objetoRepExpedito.getId()).getSingleResult();
				} catch (Exception e){
					this.trazabilidaResponsable = new ReTrazabilidadreporte_ensayo();
				}
			}
		}
		this.fase = ((this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEFaseEstudio() == null) ? null : this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEFaseEstudio().getNombre());
		Usuario_ensayo usuarioCambiar = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
		this.notificador = usuarioCambiar.getNombre() + " " + usuarioCambiar.getPrimerApellido() + " " + usuarioCambiar.getSegundoApellido();
		this.email = ((usuarioCambiar.getTelefono().equals("") || usuarioCambiar.getTelefono() == null) ? null : usuarioCambiar.getTelefono());
		this.telefono = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getTelefonoContacto();
		this.institucion = this.seguridadEstudio.getEstudioEntidadActivo().getEntidad().getNombre();
		this.provincia = this.seguridadEstudio.getEstudioEntidadActivo().getEntidad().getEstado().getValor();
		this.paciente = this.gestionarHoja.getHoja().getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
		this.inicializado = true;
	}

	public boolean esMuerte(){
		return (this.tipoResultado != null && this.tipoResultado.equals("Muerte"));
	}
	
	public void activar(){
		//boolean activar = false;
		if (!this.fechaEvento.equals(SeamResourceBundle.getBundle().getString(
				"lbl_terminadaAdministracion"))) {
			if(!this.noDosis.equals("")){
				this.setNoDosis("");
			}
			
		}
	}

	public boolean activarNoDosis() {
		boolean activar = false;
		if (this.fechaEvento.equals(SeamResourceBundle.getBundle().getString(
				"lbl_terminadaAdministracion"))) {
			activar = true;
		}
		return activar;
	}

	public boolean requirioHosp() {
		boolean activar = false;
		if (this.tipoResultado.equals("Requiri\u00F3 hospitalizaci\u00F3n")) {
			activar = true;
		}

		return activar;
	}

	@SuppressWarnings("unchecked")
	public List ValoresIntensidad() {
		List listaRadio = new ArrayList();
		listaIntensidad = (List<ReIntensidad_ensayo>) entityManager
				.createQuery(
						"select nom from ReIntensidad_ensayo nom ORDER BY nom.codigo ASC")
				.getResultList();
		for (int i = 0; i < listaIntensidad.size(); i++) {
			listaRadio.add(new SelectItem(listaIntensidad.get(i).getValor(),
					listaIntensidad.get(i).getValor()));
		}
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public List ValoresconductaEAS() {

		List<String> listaRadio = new ArrayList<String>();
		listaConducta = (List<ReCrealizadaeas_ensayo>) entityManager
				.createQuery(
						"select nom from ReCrealizadaeas_ensayo nom ORDER BY nom.codigo ASC")
				.getResultList();
		listaRadio.add("<Seleccione>");
		for (int i = 0; i < listaConducta.size(); i++) {
			listaRadio.add(listaConducta.get(i).getDescripcion());
		}
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public List ValoresHistoria() {
		List listaCombo = new ArrayList();
		listaEnfermedades = (List<ReEnfermedades_ensayo>) entityManager
				.createQuery(
						"select nom from ReEnfermedades_ensayo nom ORDER BY nom.codigo ASC")
				.getResultList();
		for (int i = 0; i < listaEnfermedades.size(); i++) {
			listaCombo.add(new SelectItem(listaEnfermedades.get(i)
					.getDescripcion(), listaEnfermedades.get(i)
					.getDescripcion()));
		}

		return listaCombo;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresmodificacionI(){
		List<ReRespuesta_ensayo> lista = new ArrayList<ReRespuesta_ensayo>();
		List listaRadio = new ArrayList();
		lista = (List<ReRespuesta_ensayo>) this.entityManager.createQuery("select nom from ReRespuesta_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		for (int i = 0; i < lista.size(); i++)
			listaRadio.add(new SelectItem(lista.get(i).getDescripcion(), lista.get(i).getDescripcion()));
		return listaRadio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List ValoresmodificacionICombo(){
		List<String> listaRadio = new ArrayList<String>();
		this.listaModificacion = (List<ReRespuesta_ensayo>) this.entityManager.createQuery("select nom from ReRespuesta_ensayo nom ORDER BY nom.codigo ASC").getResultList();
		listaRadio.add("<Seleccione>");
		for (int i = 0; i < this.listaModificacion.size(); i++)
			listaRadio.add(this.listaModificacion.get(i).getDescripcion());
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public List<String> ValoresTipoResultado() {
		List<String> listaRadio = new ArrayList<String>();
		listaResultado = (List<ReResultadofinal_ensayo>) entityManager
				.createQuery(
						"select nom from ReResultadofinal_ensayo nom ORDER BY nom.codigo ASC")
				.getResultList();
		listaRadio.add("<Seleccione>");
		for (int i = 0; i < listaResultado.size(); i++) {
			listaRadio.add(listaResultado.get(i).getDescripcion());
		}
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public List ValoresConsecuencia() {
		List listaRadio = new ArrayList();
		listaConsecuencias = (List<ReConsecuenciaspaciente_ensayo>) entityManager
				.createQuery(
						"select nom from ReConsecuenciaspaciente_ensayo nom ORDER BY nom.codigo ASC")
				.getResultList();
		for (int i = 0; i < listaConsecuencias.size(); i++) {
			listaRadio.add(new SelectItem(listaConsecuencias.get(i)
					.getDescripcion(), listaConsecuencias.get(i)
					.getDescripcion()));
		}
		return listaRadio;
	}

	@SuppressWarnings("unchecked")
	public List ValoresRelacion() {
		List listaRadio = new ArrayList();
		listaRelacion = (List<ReRecausalpea_ensayo>) entityManager
				.createQuery(
						"select nom from ReRecausalpea_ensayo nom ORDER BY nom.codigo ASC")
				.getResultList();
		for (int i = 0; i < listaRelacion.size(); i++) {
			listaRadio.add(new SelectItem(
					listaRelacion.get(i).getDescripcion(), listaRelacion.get(i)
							.getDescripcion()));
		}
		return listaRadio;
	}

	public void limpiarVariables() {
		this.variablereglaGlobal = variableRegla;
		this.variableRegla = null;
		this.edad = "";
		this.sexo = "";
		this.peso = "";
		this.raza = "";
		this.eAdverso = "";

		this.desEAdverso = "";

		this.intensidad = "";
		this.consecuencia = "";
		this.relacion = "";
		this.producInvs = "";
		this.lote = "";

		this.fechaCadu = "";

		this.iniTratamiento = "";

		this.cantDosis = "";

		this.fechaUltiDosisRecibida = "";

		this.dosisAdmin = "";
		this.viaA = "";
		this.fechaEvento = "";

		this.noDosis = "";

		this.tiempoAparicion = "";

		this.conductaEAS = "";

		this.modificacionI = "";

		this.reaparecio = "";

		this.secuela = "";

		this.tipoResultado = "";

		this.dias = "";
		this.autopsia = "";

		listaMedicacionConm = new ArrayList<ReMedicacion_ensayo>();
		listaMedicacionRec = new ArrayList<ReMedicacion_ensayo>();
		historia = new String[10];

		this.otroEsp = "";

		this.otroEsp1 = "";

		this.datosLab = "";

		this.copiaBiopsia = "";

		this.copiaAutopsia = "";
		trazabilidaSitioInv = new ReTrazabilidadreporte_ensayo();

		trazabilidaPromotor = new ReTrazabilidadreporte_ensayo();

		trazabilidaResponsable = new ReTrazabilidadreporte_ensayo();
	}

	@SuppressWarnings("unchecked")
	public void Adicionar() {
		ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
		medicacion.setMedicamento(medicamento);
		medicacion.setDosisxdia(dosisDia);
		medicacion.setFrecuencia(frecuencia);
		List<ReViasadmi_ensayo> va = (List<ReViasadmi_ensayo>) entityManager
				.createQuery(
						"select v from ReViasadmi_ensayo v order by v.codigo ")
				.getResultList();
		ReViasadmi_ensayo via = new ReViasadmi_ensayo();

		for (int i = 0; i < va.size(); i++) {

			if (va.get(i).getDescripcion().equals(viaAdmon)) {

				via = va.get(i);
				break;

			}

		}
		medicacion.setReViasadmi(via);
		//medicacion.setFechainicio(fechaIni);
		//medicacion.setFechafin(fechaFin);
		//medicacion.setIndicacion(indicacion);
		medicacion.setEliminado(false);
		listaMedicacionConm.add(medicacion);
		this.medicamento = "";
		this.dosisDia = "";
		this.frecuencia = "";
		this.viaAdmon = "";
		this.fechaIni = "";
		this.fechaFin = "";
		this.indicacion = "";
	}

	@SuppressWarnings("unchecked")
	public void Adicionar1() {
		ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
		medicacion.setMedicamento(medicamento1);
		medicacion.setDosisxdia(dosisDia1);
		medicacion.setFrecuencia(frecuencia1);
		List<ReViasadmi_ensayo> va = (List<ReViasadmi_ensayo>) entityManager
				.createQuery(
						"select v from ReViasadmi_ensayo v order by v.codigo ")
				.getResultList();
		ReViasadmi_ensayo via = new ReViasadmi_ensayo();

		for (int i = 0; i < va.size(); i++) {

			if (va.get(i).getDescripcion().equals(viaAdmon1)) {

				via = va.get(i);
				break;

			}

		}
		medicacion.setReViasadmi(via);
		//medicacion.setFechainicio(fechaIni1);
		//medicacion.setFechafin(fechaFin1);
		//medicacion.setIndicacion(indicacion1);
		medicacion.setEliminado(false);
		listaMedicacionRec.add(medicacion);
		this.medicamento1 = "";
		this.dosisDia1 = "";
		this.frecuencia1 = "";
		this.viaAdmon1 = "";
		this.fechaIni1 = "";
		this.fechaFin1 = "";
		this.indicacion1 = "";
	}

	public List<ReMedicacion_ensayo> ListaMedicacionSinEliminar() {
		try {
			List<ReMedicacion_ensayo> listasineliminadotemp = new ArrayList<ReMedicacion_ensayo>();
			for (int i = 0; i < listaMedicacionConm.size(); i++) {

				if (!listaMedicacionConm.get(i).getEliminado()) {
					listasineliminadotemp.add(listaMedicacionConm.get(i));
				}
			}
			return listasineliminadotemp;

		} catch (Exception e) {
			return null;
		}

	}

	public List<ReMedicacion_ensayo> ListaMedicacionSinEliminar1() {
		try {
			List<ReMedicacion_ensayo> listasineliminadotemp = new ArrayList<ReMedicacion_ensayo>();
			for (int i = 0; i < listaMedicacionRec.size(); i++) {

				if (!listaMedicacionRec.get(i).getEliminado()) {
					listasineliminadotemp.add(listaMedicacionRec.get(i));
				}
			}
			return listasineliminadotemp;

		} catch (Exception e) {
			return null;
		}

	}

	public void eliminarMedicacion(int pos) {
		// ReMedicacion_ensayo intervencionelim = new ReMedicacion_ensayo();
		// intervencionelim = listaMedicacionConm.get(pos);
		// listaMedicacionConm.remove(pos);
		listaMedicacionConm.get(pos).setEliminado(true);
		// entityManager.persist(intervencionelim);
		// entityManager.flush();
	}

	public void eliminarMedicacion1(int pos) {
		// ReMedicacion_ensayo intervencionelim = new ReMedicacion_ensayo();
		// intervencionelim = listaMedicacionConm.get(pos);
		// listaMedicacionConm.remove(pos);
		listaMedicacionRec.get(pos).setEliminado(true);
		// entityManager.persist(intervencionelim);
		// entityManager.flush();
	}

	// Listado de las vias de administracion
	private List<String> va = new ArrayList<String>();

	// llenar las fases
	@SuppressWarnings("unchecked")
	public List<String> llenarFASES() {
		fases = (List<EFaseEstudio_ensayo>) entityManager.createQuery(
				"select f from EFaseEstudio_ensayo f order by f.codigo ")
				.getResultList();

		List<String> listaAux = new ArrayList<String>();
		listaAux.add(0, "<Seleccione>");
		for (int i = 0; i < fases.size(); i++) {
			listaAux.add(fases.get(i).getNombre());
		}

		return listaAux;
	}

	// llenar las vias de administracion
	@SuppressWarnings("unchecked")
	public List<String> llenarVA() {
		va = entityManager
				.createQuery(
						"select v.descripcion from ReViasadmi_ensayo v order by v.codigo ")
				.getResultList();
		return va;
	}

	// llenar sexo
	@SuppressWarnings("unchecked")
	public List<String> llenarSexo() {
		List<String> listaSex = new ArrayList<String>();
		listaSex.add("Masculino");
		listaSex.add("Femenino");
		return listaSex;
	}

	// llenar sexo
	@SuppressWarnings("unchecked")
	public List<String> llenarRaza() {
		List<String> listaSex = new ArrayList<String>();
		listaSex.add("Blanca");
		listaSex.add("Negra");
		listaSex.add("Amarilla");
		listaSex.add("Mestiza");
		return listaSex;
	}

	public String getNombreEst() {
		return nombreEst;
	}

	public void setNombreEst(String nombreEst) {
		this.nombreEst = nombreEst;
	}

	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public String getNotificador() {
		return notificador;
	}

	public void setNotificador(String notificador) {
		this.notificador = notificador;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getTelefono() {
		return telefono;
	}

	public void setTelefono(Long telefono) {
		this.telefono = telefono;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String geteAdverso() {
		return eAdverso;
	}

	public void seteAdverso(String eAdverso) {
		this.eAdverso = eAdverso;
	}

	public String getDesEAdverso() {
		return desEAdverso;
	}

	public void setDesEAdverso(String desEAdverso) {
		this.desEAdverso = desEAdverso;
	}

	public String getProducInvs() {
		return producInvs;
	}

	public void setProducInvs(String producInvs) {
		this.producInvs = producInvs;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getFechaCadu() {
		return fechaCadu;
	}

	public void setFechaCadu(String fechaCadu) {
		this.fechaCadu = fechaCadu;
	}

	public String getIniTratamiento() {
		return iniTratamiento;
	}

	public void setIniTratamiento(String iniTratamiento) {
		this.iniTratamiento = iniTratamiento;
	}

	public String getFechaUltiDosisRecibida() {
		return fechaUltiDosisRecibida;
	}

	public void setFechaUltiDosisRecibida(String fechaUltiDosisRecibida) {
		this.fechaUltiDosisRecibida = fechaUltiDosisRecibida;
	}

	public String getDosisAdmin() {
		return dosisAdmin;
	}

	public void setDosisAdmin(String dosisAdmin) {
		this.dosisAdmin = dosisAdmin;
	}

	public String getFechaEvento() {
		return fechaEvento;
	}

	public void setFechaEvento(String fechaEvento) {
		this.fechaEvento = fechaEvento;
	}

	public String getTiempoAparicion() {
		return tiempoAparicion;
	}

	public void setTiempoAparicion(String tiempoAparicion) {
		this.tiempoAparicion = tiempoAparicion;
	}

	public String getSecuela() {
		return secuela;
	}

	public void setSecuela(String secuela) {
		this.secuela = secuela;
	}

	public Integer getDiasHosp() {
		return diasHosp;
	}

	public void setDiasHosp(Integer diasHosp) {
		this.diasHosp = diasHosp;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public String getMedicamento1() {
		return medicamento1;
	}

	public void setMedicamento1(String medicamento1) {
		this.medicamento1 = medicamento1;
	}

	public String getDosisDia() {
		return dosisDia;
	}

	public void setDosisDia(String dosisDia) {
		this.dosisDia = dosisDia;
	}

	public String getDosisDia1() {
		return dosisDia1;
	}

	public void setDosisDia1(String dosisDia1) {
		this.dosisDia1 = dosisDia1;
	}

	public String getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}

	public String getFrecuencia1() {
		return frecuencia1;
	}

	public void setFrecuencia1(String frecuencia1) {
		this.frecuencia1 = frecuencia1;
	}

	public String getViaAdmon() {
		return viaAdmon;
	}

	public void setViaAdmon(String viaAdmon) {
		this.viaAdmon = viaAdmon;
	}

	public String getViaAdmon1() {
		return viaAdmon1;
	}

	public void setViaAdmon1(String viaAdmon1) {
		this.viaAdmon1 = viaAdmon1;
	}

	public String getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(String fechaIni) {
		this.fechaIni = fechaIni;
	}

	public String getFechaIni1() {
		return fechaIni1;
	}

	public void setFechaIni1(String fechaIni1) {
		this.fechaIni1 = fechaIni1;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getFechaFin1() {
		return fechaFin1;
	}

	public void setFechaFin1(String fechaFin1) {
		this.fechaFin1 = fechaFin1;
	}

	public String getIndicacion() {
		return indicacion;
	}

	public void setIndicacion(String indicacion) {
		this.indicacion = indicacion;
	}

	public String getIndicacion1() {
		return indicacion1;
	}

	public void setIndicacion1(String indicacion1) {
		this.indicacion1 = indicacion1;
	}

	public String getOtroEsp() {
		return otroEsp;
	}

	public void setOtroEsp(String otroEsp) {
		this.otroEsp = otroEsp;
	}

	public String getOtroEsp1() {
		return otroEsp1;
	}

	public void setOtroEsp1(String otroEsp1) {
		this.otroEsp1 = otroEsp1;
	}

	public String getReportSitio() {
		return reportSitio;
	}

	public void setReportSitio(String reportSitio) {
		this.reportSitio = reportSitio;
	}

	public String getRevPromotor() {
		return revPromotor;
	}

	public void setRevPromotor(String revPromotor) {
		this.revPromotor = revPromotor;
	}

	public String getRevRespFarma() {
		return revRespFarma;
	}

	public void setRevRespFarma(String revRespFarma) {
		this.revRespFarma = revRespFarma;
	}

	public String getResponsabilidad() {
		return responsabilidad;
	}

	public void setResponsabilidad(String responsabilidad) {
		this.responsabilidad = responsabilidad;
	}

	public String getResponsabilidad1() {
		return responsabilidad1;
	}

	public void setResponsabilidad1(String responsabilidad1) {
		this.responsabilidad1 = responsabilidad1;
	}

	public String getResponsabilidad2() {
		return responsabilidad2;
	}

	public void setResponsabilidad2(String responsabilidad2) {
		this.responsabilidad2 = responsabilidad2;
	}

	public String getViaA() {
		return viaA;
	}

	public void setViaA(String viaA) {
		this.viaA = viaA;
	}

	public String getSeleccione() {
		return seleccione;
	}

	@SuppressWarnings("rawtypes")
	public List getVA() {
		return llenarVA();
	}

	public List<String> getVa() {
		return va;
	}

	public void setVa(List<String> va) {
		this.va = va;
	}

	public ReReporteexpedito_ensayo getObjetoRepExpedito() {
		return objetoRepExpedito;
	}

	public void setObjetoRepExpedito(ReReporteexpedito_ensayo objetoRepExpedito) {
		this.objetoRepExpedito = objetoRepExpedito;
	}

	public List<ReMedicacion_ensayo> getListaMedicacionConm() {
		return listaMedicacionConm;
	}

	public void setListaMedicacionConm(
			List<ReMedicacion_ensayo> listaMedicacionConm) {
		this.listaMedicacionConm = listaMedicacionConm;
	}

	public List<ReMedicacion_ensayo> getListaMedicacionRec() {
		return listaMedicacionRec;
	}

	public void setListaMedicacionRec(
			List<ReMedicacion_ensayo> listaMedicacionRec) {
		this.listaMedicacionRec = listaMedicacionRec;
	}

	public List<EFaseEstudio_ensayo> getFases() {
		return fases;
	}

	public void setFases(List<EFaseEstudio_ensayo> fases) {
		this.fases = fases;
	}

	public String getEdad() {
		return edad;
	}

	public void setEdad(String edad) {
		this.edad = edad;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		this.raza = raza;
	}

	public UIInput FindComponent(String componentId) {
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIInput component = (UIInput) viewRoot.findComponent(componentId);
		return component;
	}

	// Persistiendo reporte expedito
	@Override
	public void Process() {
		/*try {
			if (this.objetoRepExpedito == null)
				this.objetoRepExpedito = new ReReporteexpedito_ensayo();
			EntityManager entityM = (EntityManager) Component.getInstance("entityManager");
			GestionarHoja gestionarHoj = (GestionarHoja) Component.getInstance("gestionarHoja");
			// Cambia fase del estudio
			if (this.estudioE.getEFaseEstudio() == null){
				if (!this.fase.equals("")) {
					EFaseEstudio_ensayo faseEst = new EFaseEstudio_ensayo();
					for (int i = 0; i < this.fases.size(); i++) {
						if (this.fases.get(i).getNombre().equals(this.fase)){
							faseEst = this.fases.get(i);
							break;
						}
					}
					this.estudioE.setEFaseEstudio(faseEst);
					entityM.persist(this.estudioE);
				}
			}
			// persiste reporte expedito
			// gestionarHoj.persistir();
			// gestionarHoj.initConversation();
			VariableDato_ensayo datoGuardar = new VariableDato_ensayo();
			long idDatoVar = gestionarHoj
					.VariableGrupoWrapper(
							variableRegla.getGrupoVariables().getId(),
							variableRegla.getId()).getListaValores().get(row)
					.getId();
			if (String.valueOf(idDatoVar).length() != 19) {
				VariableDato_ensayo Dato;
				// for (int i = 0; i <
				// gestionarHoj.VariableGrupoWrapper(variableRegla.getGrupoVariables().getId(),
				// variableRegla.getId()).getListaValores().size();
				// i++) {
				// guardar todas las variables.....
				GrupoWrapper grupo = gestionarHoj.grupoWrapper(variableRegla
						.getGrupoVariables().getId());
				this.cid = this.bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("modificandoHoja"));
				for (int i = 0; i < grupo.getListaVarWrapper().size(); i++) {
					// for (int j = 0; j <
					// grupo.getListaVarWrapper().get(i).getListaValoresGuardar().size();
					// j++) {

					if (!grupo.getListaVarWrapper().get(i)
							.getListaValoresGuardar().get(row).isEliminado()) {
						Dato = new VariableDato_ensayo();
						Dato.setVariable(grupo.getListaVarWrapper().get(i)
								.getVariable());
						Dato.setCid(this.cid);
						Object valor = grupo.getListaVarWrapper().get(i)
								.getListaValoresGuardar().get(row).getValor();
						Dato.setUsuario(usuario);
						Dato.setValor(valor != null ? (String) valor.toString()
								: "");// TODO
						// change
						// this
						Dato.setCrdEspecifico(this.objetoGestionarHoja);
						Dato.setEliminado(false);
						entityM.persist(Dato);
						entityM.flush();
						if (grupo.getListaVarWrapper().get(i).getVariable() == variableRegla) {
							datoGuardar = Dato;
						}
					}
				}
				gestionarHoj.MeHaceFalta();
			} else {
				datoGuardar = entityM.find(VariableDato_ensayo.class, idDatoVar);
			}
			// objetoRepExpedito.setPosicion(row);
			objetoRepExpedito.setVariable(variableRegla);
			objetoRepExpedito.setVariableDato(datoGuardar);
			objetoRepExpedito.setCrdEspecifico(gestionarHoj.getHoja());
			objetoRepExpedito.setEdad(this.edad);
			objetoRepExpedito.setSexo(this.sexo);
			objetoRepExpedito.setPeso(Float.parseFloat(this.peso));
			objetoRepExpedito.setRaza(this.raza);
			objetoRepExpedito.setEventoadverso(this.eAdverso);			
			for (int i = 0; i < listaIntensidad.size(); i++) {
				if (listaIntensidad.get(i).getValor().equals(this.intensidad)) {
					objetoRepExpedito.setReIntensidad(listaIntensidad.get(i));
					break;
				}
			}
			for (int i = 0; i < listaConsecuencias.size(); i++) {
				if (listaConsecuencias.get(i).getDescripcion()
						.equals(this.consecuencia)) {
					objetoRepExpedito
							.setReConsecuenciaspaciente(listaConsecuencias
									.get(i));
					break;
				}
			}
			for (int i = 0; i < listaRelacion.size(); i++) {
				if (listaRelacion.get(i).getDescripcion().equals(this.relacion)) {
					objetoRepExpedito.setReRecausalpea(listaRelacion.get(i));
					break;
				}
			}
			if (!this.desEAdverso.equals("")
					|| (this.desEAdverso.equals("") && objetoRepExpedito
							.getDescripcionea() != null)) {
				objetoRepExpedito.setDescripcionea(desEAdverso);
			}
			objetoRepExpedito.setProductoinvestigado(producInvs);
			objetoRepExpedito.setLote(this.lote);
			if (!fechaCadu.equals("")
					|| (this.fechaCadu.equals("") && objetoRepExpedito
							.getFechacaducidad() != null)) {
				objetoRepExpedito.setFechacaducidad(fechaCadu);
			}
			objetoRepExpedito.setIniciotratamiento(iniTratamiento);
			if (!cantDosis.equals("")
					|| (this.cantDosis.equals("") && objetoRepExpedito
							.getDosisrecibida() != null)) {
				objetoRepExpedito.setDosisrecibida(Integer.parseInt(cantDosis));
			}
			if (!fechaUltiDosisRecibida.equals("")
					|| (this.fechaUltiDosisRecibida.equals("") && objetoRepExpedito
							.getUltimadosis() != null)) {
				objetoRepExpedito.setUltimadosis(fechaUltiDosisRecibida);
			}
			objetoRepExpedito.setDosisadministrada(this.dosisAdmin);
			List<ReViasadmi_ensayo> viaAdm = (List<ReViasadmi_ensayo>) entityM
					.createQuery(
							"select v from ReViasadmi_ensayo v order by v.codigo ")
					.getResultList();
			for (int i = 0; i < viaAdm.size(); i++) {
				if (viaAdm.get(i).getDescripcion().equals(this.viaA)) {
					objetoRepExpedito.setReViasadmi(viaAdm.get(i));
					break;
				}
			}
			objetoRepExpedito.setFechareporteA(fechaEvento);
			
				objetoRepExpedito.setNo_dosis(noDosis);
			
			if (!tiempoAparicion.equals("")
					|| (this.tiempoAparicion.equals("") && objetoRepExpedito
							.getTiempoaparicion() != null)) {
				objetoRepExpedito.setTiempoaparicion(tiempoAparicion);
			}
			if ((!modificacionI.equals("<Seleccione>") && !modificacionI
					.equals(""))) {
				for (int i = 0; i < listaModificacion.size(); i++) {
					if (listaModificacion.get(i).getDescripcion()
							.equals(this.modificacionI)) {
						objetoRepExpedito
								.setReRespuestaByModificacionintensidad(listaModificacion
										.get(i));
						break;
					}
				}
				// objetoRepExpedito.setModificacion(modificacionI);
			}
			if ((this.modificacionI.equals("") || modificacionI
					.equals("<Seleccione>"))
					&& objetoRepExpedito
							.getReRespuestaByModificacionintensidad() != null) {

				objetoRepExpedito.setReRespuestaByModificacionintensidad(null);

			}

			if ((!conductaEAS.equals("<Seleccione>") && !conductaEAS.equals(""))) {
				for (int i = 0; i < listaConducta.size(); i++) {
					if (listaConducta.get(i).getDescripcion()
							.equals(this.conductaEAS)) {
						objetoRepExpedito.setReCrealizadaeas(listaConducta
								.get(i));
						break;
					}
				}
				// objetoRepExpedito.setModificacion(modificacionI);
			}
			if ((this.conductaEAS.equals("") || conductaEAS
					.equals("<Seleccione>"))
					&& objetoRepExpedito.getReCrealizadaeas() != null) {
				objetoRepExpedito.setReCrealizadaeas(null);
			}

			for (int i = 0; i < listaModificacion.size(); i++) {
				if (listaModificacion.get(i).getDescripcion()
						.equals(this.reaparecio)) {
					objetoRepExpedito
							.setReRespuestaByReaparicionea(listaModificacion
									.get(i));
					break;
				}
			}
			if (!this.secuela.equals("")
					|| (this.secuela.equals("") && objetoRepExpedito
							.getSecuela() != null)) {
				objetoRepExpedito.setSecuela(secuela);
			}

			if (!tipoResultado.equals("<Seleccione>")
					&& !tipoResultado.equals("")) {
				for (int i = 0; i < listaResultado.size(); i++) {
					if (listaResultado.get(i).getDescripcion()
							.equals(this.tipoResultado)) {
						objetoRepExpedito.setReResultadofinal(listaResultado
								.get(i));
						break;
					}
				}
				// objetoRepExpedito.setModificacion(modificacionI);
			}
			if ((this.tipoResultado.equals("") || tipoResultado
					.equals("<Seleccione>"))
					&& objetoRepExpedito.getReResultadofinal() != null) {
				objetoRepExpedito.setReResultadofinal(null);
			}

			if (!dias.equals("")) {
				objetoRepExpedito
						.setDiashospitalizacion(Integer.parseInt(dias));
			}

			if (!autopsia.equals("")) {
				if (autopsia.equals("No")) {
					objetoRepExpedito.setAutopsia(false);
				} else {
					objetoRepExpedito.setAutopsia(true);
				}
			}

			if (!datosLab.equals("")) {
				if (datosLab.equals("No")) {
					objetoRepExpedito.setDatoslaboratorio(false);
				} else {
					objetoRepExpedito.setDatoslaboratorio(true);
				}
			}

			if (!copiaBiopsia.equals("")) {
				if (copiaBiopsia.equals("No")) {
					objetoRepExpedito.setCopiabiopsia(false);
				} else {
					objetoRepExpedito.setCopiabiopsia(true);
				}
			}

			if (!copiaAutopsia.equals("")) {
				if (copiaAutopsia.equals("No")) {
					objetoRepExpedito.setCopiaautopsia(false);
				} else {
					objetoRepExpedito.setCopiaautopsia(true);
				}
			}
			if (!this.otroEsp1.equals("")
					|| (this.otroEsp1.equals("") && objetoRepExpedito
							.getOtroscomentarios() != null)) {
				objetoRepExpedito.setOtroscomentarios(otroEsp1);
			}

			if (!this.otroEsp.equals("")
					|| (this.otroEsp.equals("") && objetoRepExpedito
							.getOtroshistoria() != null)) {
				objetoRepExpedito.setOtroshistoria(otroEsp);
			}
			objetoRepExpedito.setIdSujeto(objetoGestionarHoja
					.getMomentoSeguimientoEspecifico().getSujeto().getId());
			objetoRepExpedito.setIdEstudio(estudioE.getId());
			objetoRepExpedito.setIdEntidad(estudioE.getEntidad().getId());
			objetoRepExpedito.setUsuario(usuario);
			entityM.persist(objetoRepExpedito);

			List<ReHistoriarelevante_ensayo> listaHistoria = (List<ReHistoriarelevante_ensayo>) entityM
					.createQuery(
							"select historia from ReHistoriarelevante_ensayo historia where historia.reReporteexpedito.id=:idReporteexpedito")
					.setParameter("idReporteexpedito",
							objetoRepExpedito.getId()).getResultList();

			if (this.historia.length > 0) {
				for (int j = 0; j < historia.length; j++) {
					for (int i = 0; i < listaEnfermedades.size(); i++) {
						if (listaEnfermedades.get(i).getDescripcion()
								.equals(historia[j])) {
							boolean esta = false;
							for (int k = 0; k < listaHistoria.size(); k++) {
								if (listaHistoria.get(k).getIdReEnfermedades() == listaEnfermedades
										.get(i).getId()) {
									esta = true;
									break;
								}
							}
							if (!esta) {
								ReHistoriarelevante_ensayo hitosriaR = new ReHistoriarelevante_ensayo();
								hitosriaR
										.setReReporteexpedito(objetoRepExpedito);
								hitosriaR.setIdReEnfermedades(listaEnfermedades
										.get(i).getId());
								entityM.persist(hitosriaR);
								break;
							}
						}
					}

				}
				for (int j = 0; j < listaHistoria.size(); j++) {
					ReHistoriarelevante_ensayo hitosriaR = listaHistoria.get(j);
					boolean esta = false;
					for (int k = 0; k < historia.length; k++) {
						if ((entityM.find(ReEnfermedades_ensayo.class,
								listaHistoria.get(j).getIdReEnfermedades()))
								.getDescripcion().equals(historia[k])) {
							// hitosriaR =
							esta = true;
							break;
						}
					}
					if (!esta) {
						// hitosriaR.setReReporteexpedito(null);
						entityM.remove(hitosriaR);

					}
				}
			}
			if (!trazabilidaSitioInv.getNombreapellido().equals("")
					|| !trazabilidaSitioInv.getResponsabilidad().equals("")
					|| !trazabilidaSitioInv.getFecha().equals("")) {
				trazabilidaSitioInv.setCodigo("sitioInv");
				trazabilidaSitioInv.setReReporteexpedito(objetoRepExpedito);
				entityM.persist(trazabilidaSitioInv);
			}
			if (!trazabilidaPromotor.getNombreapellido().equals("")
					|| !trazabilidaPromotor.getResponsabilidad().equals("")
					|| !trazabilidaPromotor.getFecha().equals("")) {
				trazabilidaPromotor.setCodigo("revProm");
				trazabilidaPromotor.setReReporteexpedito(objetoRepExpedito);
				entityM.persist(trazabilidaPromotor);
			}
			if (!trazabilidaResponsable.getNombreapellido().equals("")
					|| !trazabilidaResponsable.getResponsabilidad().equals("")
					|| !trazabilidaResponsable.getFecha().equals("")) {
				trazabilidaResponsable.setCodigo("revResp");
				trazabilidaResponsable.setReReporteexpedito(objetoRepExpedito);
				entityM.persist(trazabilidaResponsable);
			}
			List<ReMedicacion_ensayo> listaMedicacionConmAxu = (List<ReMedicacion_ensayo>) entityM
					.createQuery(
							"select medicacion.reMedicacion from ReMedicacionconcomitante_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False")
					.setParameter("idReporteexpedito",
							objetoRepExpedito.getId()).getResultList();
			List<ReMedicacion_ensayo> listaMedicacionRecAux = (List<ReMedicacion_ensayo>) entityM
					.createQuery(
							"select medicacion.reMedicacion from ReMedicacionreciente_ensayo medicacion where medicacion.reReporteexpedito.id=:idReporteexpedito and medicacion.reMedicacion.eliminado = False")
					.setParameter("idReporteexpedito",
							objetoRepExpedito.getId()).getResultList();

			for (int i = 0; i < listaMedicacionConm.size(); i++) {
				boolean esta = false;
				for (int j = 0; j < listaMedicacionConmAxu.size(); j++) {
					if (listaMedicacionConm.get(i).getId() == listaMedicacionConmAxu
							.get(j).getId()) {
						esta = true;
						break;
					}
				}
				if (!esta) {
					ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
					medicacion = listaMedicacionConm.get(i);
					entityM.persist(medicacion);
					ReMedicacionconcomitante_ensayo conco = new ReMedicacionconcomitante_ensayo();
					conco.setReMedicacion(medicacion);
					conco.setReReporteexpedito(objetoRepExpedito);
					entityM.persist(conco);
				}

			}

			for (int i = 0; i < listaMedicacionRec.size(); i++) {
				boolean esta = false;
				for (int j = 0; j < listaMedicacionRecAux.size(); j++) {
					if (listaMedicacionRec.get(i).getId() == listaMedicacionRecAux
							.get(j).getId()) {
						esta = true;
						break;
					}
				}
				if (!esta) {
					ReMedicacion_ensayo medicacion = new ReMedicacion_ensayo();
					medicacion = listaMedicacionRec.get(i);
					entityM.persist(medicacion);
					ReMedicacionreciente_ensayo reciente = new ReMedicacionreciente_ensayo();
					reciente.setReMedicacion(medicacion);
					reciente.setReReporteexpedito(objetoRepExpedito);
					entityM.persist(reciente);
				}

			}
			Usuario_ensayo usuarioCambiar = (Usuario_ensayo) entityM.find(
					Usuario_ensayo.class, user.getId());
			// Cambia telefono del usuario
			if (usuarioCambiar.getTelefono().equals("")
					|| usuarioCambiar.getTelefono() == null) {
				if (!this.email.equals("")) {
					usuarioCambiar.setTelefono(email);
					entityM.persist(usuarioCambiar);
				}

			}

			entityM.flush();
			setCompleted(true);
			// this.variableRegla = null;
		//	this.limpiarVariables();

			// gestionarHoja.persistir();
		} catch (Exception e) {
			// TODO: handle exception
		}*/

	}

	@Override
	public String Export() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean CanExport() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getIntensidad() {
		return intensidad;
	}

	public void setIntensidad(String intensidad) {
		this.intensidad = intensidad;
	}

	public String getConsecuencia() {
		return consecuencia;
	}

	public void setConsecuencia(String consecuencia) {
		this.consecuencia = consecuencia;
	}

	public String getRelacion() {
		return relacion;
	}

	public void setRelacion(String relacion) {
		this.relacion = relacion;
	}

	public String getConductaEAS() {
		return conductaEAS;
	}

	public void setConductaEAS(String conductaEAS) {
		this.conductaEAS = conductaEAS;
	}

	public String getModificacionI() {
		return modificacionI;
	}

	public void setModificacionI(String modificacionI) {
		this.modificacionI = modificacionI;
	}

	public String getReaparecio() {
		return reaparecio;
	}

	public void setReaparecio(String reaparecio) {
		this.reaparecio = reaparecio;
	}

	public String getDatosLab() {
		return datosLab;
	}

	public void setDatosLab(String datosLab) {
		this.datosLab = datosLab;
	}

	public String getCopiaBiopsia() {
		return copiaBiopsia;
	}

	public void setCopiaBiopsia(String copiaBiopsia) {
		this.copiaBiopsia = copiaBiopsia;
	}

	public String getCopiaAutopsia() {
		return copiaAutopsia;
	}

	public void setCopiaAutopsia(String copiaAutopsia) {
		this.copiaAutopsia = copiaAutopsia;
	}

	public String getTipoResultado() {
		return tipoResultado;
	}

	public void setTipoResultado(String tipoResultado) {
		this.tipoResultado = tipoResultado;
	}

	public String getAutopsia() {
		return autopsia;
	}

	public void setAutopsia(String autopsia) {
		this.autopsia = autopsia;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public String[] getHistoria() {
		return historia;
	}

	public void setHistoria(String[] historia) {
		this.historia = historia;
	}

	public List<ReIntensidad_ensayo> getListaIntensidad() {
		return listaIntensidad;
	}

	public void setListaIntensidad(List<ReIntensidad_ensayo> listaIntensidad) {
		this.listaIntensidad = listaIntensidad;
	}

	public List<ReConsecuenciaspaciente_ensayo> getListaConsecuencias() {
		return listaConsecuencias;
	}

	public void setListaConsecuencias(
			List<ReConsecuenciaspaciente_ensayo> listaConsecuencias) {
		this.listaConsecuencias = listaConsecuencias;
	}

	public List<ReRecausalpea_ensayo> getListaRelacion() {
		return listaRelacion;
	}

	public void setListaRelacion(List<ReRecausalpea_ensayo> listaRelacion) {
		this.listaRelacion = listaRelacion;
	}

	public List<ReEnfermedades_ensayo> getListaEnfermedades() {
		return listaEnfermedades;
	}

	public void setListaEnfermedades(
			List<ReEnfermedades_ensayo> listaEnfermedades) {
		this.listaEnfermedades = listaEnfermedades;
	}

	public ReTrazabilidadreporte_ensayo getTrazabilidaSitioInv() {
		return trazabilidaSitioInv;
	}

	public void setTrazabilidaSitioInv(
			ReTrazabilidadreporte_ensayo trazabilidaSitioInv) {
		this.trazabilidaSitioInv = trazabilidaSitioInv;
	}

	public ReTrazabilidadreporte_ensayo getTrazabilidaPromotor() {
		return trazabilidaPromotor;
	}

	public void setTrazabilidaPromotor(
			ReTrazabilidadreporte_ensayo trazabilidaPromotor) {
		this.trazabilidaPromotor = trazabilidaPromotor;
	}

	public ReTrazabilidadreporte_ensayo getTrazabilidaResponsable() {
		return trazabilidaResponsable;
	}

	public void setTrazabilidaResponsable(
			ReTrazabilidadreporte_ensayo trazabilidaResponsable) {
		this.trazabilidaResponsable = trazabilidaResponsable;
	}

	public String getNoDosis() {
		return noDosis;
	}

	public void setNoDosis(String noDosis) {
		this.noDosis = noDosis;
	}

	public CrdEspecifico_ensayo getObjetoGestionarHoja() {
		return objetoGestionarHoja;
	}

	public void setObjetoGestionarHoja(CrdEspecifico_ensayo objetoGestionarHoja) {
		this.objetoGestionarHoja = objetoGestionarHoja;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public Variable_ensayo getVariableRegla() {
		return variableRegla;
	}

	public void setVariableRegla(Variable_ensayo variableRegla) {
		this.variableRegla = variableRegla;
	}

	public String getCantDosis() {
		return cantDosis;
	}

	public void setCantDosis(String cantDosis) {
		this.cantDosis = cantDosis;
	}

	public Variable_ensayo getVariablereglaGlobal() {
		return variablereglaGlobal;
	}

	public void setVariablereglaGlobal(Variable_ensayo variablereglaGlobal) {
		this.variablereglaGlobal = variablereglaGlobal;
	}
	
	public Long getSectionId(){
		return sectionId;
	}
	
	public void setSectionId(Long sectionId){
		this.sectionId = sectionId;
	}

}