package gehos.ensayo.ensayo_disenno.session.gestionarMS.crearMS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_disenno.session.custom.HojasCRDCustomList_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoCronograma_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearMSProgramado")
@Scope(ScopeType.CONVERSATION)
public class CrearMSProgramado {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;
	@In Usuario user;
	@In(create = true)
	HojasCRDCustomList_ensayo hojasCRDCustomList_ensayo;

	FacesMessage facesMessage;
	private MomentoSeguimientoGeneral_ensayo ms = new MomentoSeguimientoGeneral_ensayo();
	MomentoSeguimientoGeneralHojaCrd_ensayo msCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();
	private Cronograma_ensayo cronograma;
	private List<String> listaDiasValidos;
	private List<String> listaDiasSeleccionados = new ArrayList<String>();
	private Integer diaFinUltimaEtapa;
	private long cid;
	private long idCronograma;

	private String planificacion = "visita";
	private String tiempoLlenado;
	private Integer primerDia;
	private String frecuencia = "0";
	private String periodoFrecuancia = "dias";
	private String durante = "0";
	private String periodoDurante = "dias";
	private boolean visita = true;
	private boolean inicioConversacion = false;
	boolean evaluacion;
	boolean tratamiento;
	boolean seguimiento;
	String dias;
	String etapa;

	private String nombreHoja;
	private HojaCrd_ensayo hojaCRD = new HojaCrd_ensayo();
	Hashtable<Long, HojaCrd_ensayo> hojaCRDSeleccionada = new Hashtable<Long, HojaCrd_ensayo>();
	private long idHojaCRD = -1L;
	List<HojaCrd_ensayo> listaHojasCRD = new ArrayList<HojaCrd_ensayo>();

	// Inicializar.
	public void inicializarMSProgramado() {

		listaDiasValidos = new ArrayList<String>();
		this.cronograma = (Cronograma_ensayo) entityManager.find(
				Cronograma_ensayo.class, idCronograma);
		diaFinUltimaEtapa = (Integer) entityManager
				.createQuery(
						"select e.finEtapa from Etapa_ensayo e "
								+ "where e.cronograma.id = :idCrono "
								+ "and e.inicioEtapa>1")
				.setParameter("idCrono", cronograma.getId()).getSingleResult();
		for (Integer i = 0; i <= diaFinUltimaEtapa; i++) {
			listaDiasValidos.add(i.toString());
		}

		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("prm_bitacoraCrear_ens"));
		inicioConversacion=true;
	}

	// En caso de que la planificacion sea por periodo
	// Metodo para convertir la frecuencia en dias.
	public Integer tiempoFrecuanciaDias() {
		Integer tiempoResult = Integer.parseInt(frecuencia);

		if (periodoFrecuancia.equals("semanas"))
			tiempoResult = tiempoResult * 7;
		else if (periodoFrecuancia.equals("meses"))
			tiempoResult = tiempoResult * 30;
		return tiempoResult;

	}

	// Metodo para convertir tiempo de duracion (durante) en dias.
	public Integer tiempoDuranteDias() {
		Integer tiempoResult = Integer.parseInt(durante);

		if (periodoDurante.equals("semanas"))
			tiempoResult = tiempoResult * 7;
		else if (periodoDurante.equals("meses"))
			tiempoResult = tiempoResult * 30;
		return tiempoResult;

	}

	// Fin periodo
	// Validar que el tiempo de duracion sea un numero entero positivo.
	public void numeroEnteroPositivo(FacesContext context,
			UIComponent component, Object value) {

		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
		// negativo
		{
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
					.getString(

					"msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}

		if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
		// tenga caracteres extrannos

		{
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
					.getString("msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}

		try {// valida que sea entero
			@SuppressWarnings("unused")
			Integer val = new Integer(value.toString());

		} catch (Exception e) {
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
					.getString("msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}

	}

	// Metodo para construir la cadena de dias
	public String construirCadenaDias(List<String> listaDias) {
		String cadenaDias = "";
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
		for (int i = 0; i < listaDias.size(); i++) {
			if (i == listaDias.size() - 2)
				cadenaDias += listaDias.get(i) + " " + y + " ";
			else if (i == listaDias.size() - 1)
				cadenaDias += listaDias.get(i);
			else
				cadenaDias += listaDias.get(i) + ", ";
		}
		return cadenaDias;
	}

	// Metodo para ordenar la lista de dias planificados.
	public void ordenarLista(List<String> listaDias) {
		List<Integer> dias = new ArrayList<Integer>();
		for (int i = 0; i < listaDias.size(); i++) {
			dias.add(Integer.parseInt(listaDias.get(i)));
		}
		Collections.sort(dias);
		listaDias.clear();
		for (int i = 0; i < dias.size(); i++) {
			listaDias.add(dias.get(i).toString());
		}

	}

	// Metodo para saber las etapas del MS
	public String etapasMS(List<String> listaDias) {
		String e = "";
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");

		List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
				.createQuery(
						"select e from Etapa_ensayo e "
								+ "where e.cronograma.id = :idCrono "
								+ "order by e.id")
				.setParameter("idCrono", cronograma.getId()).getResultList();
		for (int i = 0; i < listaDias.size(); i++) {
			int dia = Integer.parseInt(listaDias.get(i));
			if (dia == 0)
				evaluacion = true;
			else if (!tratamiento && dia >= 1
					&& dia <= etapas.get(1).getFinEtapa())
				tratamiento = true;
			else if (!seguimiento && dia >= etapas.get(2).getInicioEtapa()
					&& dia <= etapas.get(2).getFinEtapa())
				seguimiento = true;
			if (evaluacion && tratamiento && seguimiento) {
				e = etapas.get(0).getNombreEtapa() + ", "
						+ etapas.get(1).getNombreEtapa() + " " + y + " "
						+ etapas.get(2).getNombreEtapa();
				return e;
			}
		}
		if (evaluacion && !tratamiento && !seguimiento)
			e = etapas.get(0).getNombreEtapa();
		else if (evaluacion && tratamiento && !seguimiento)
			e = etapas.get(0).getNombreEtapa() + " " + y + " "
					+ etapas.get(1).getNombreEtapa();
		else if (evaluacion && !tratamiento && seguimiento)
			e = etapas.get(0).getNombreEtapa() + " " + y + " "
					+ etapas.get(2).getNombreEtapa();
		else if (!evaluacion && tratamiento && !seguimiento)
			e = etapas.get(1).getNombreEtapa();
		else if (!evaluacion && tratamiento && seguimiento)
			e = etapas.get(1).getNombreEtapa() + " " + y + " "
					+ etapas.get(2).getNombreEtapa();
		else
			e = etapas.get(2).getNombreEtapa();
		return e;
	}

	// Metodo para construir la cadena con los dias de visita.
	public String diasEtapa(String etapa, List<String> listaDias) {
		List<String> listaDiasEtapa = new ArrayList<String>();
		List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
				.createQuery(
						"select e from Etapa_ensayo e "
								+ "where e.cronograma.id = :idCrono "
								+ "order by e.id")
				.setParameter("idCrono", cronograma.getId()).getResultList();
		for (int i = 0; i < listaDias.size(); i++) {
			Integer dia = Integer.parseInt(listaDias.get(i));
			if (dia == 0 && etapa.equals("evaluacion")) {
				listaDiasEtapa.add(dia.toString());
				return construirCadenaDias(listaDiasEtapa);
				/**
				 * @author Tania
				 */
			} else if (dia >= 1 && dia <= etapas.get(1).getFinEtapa()
					&& etapa.equals("tratamiento"))
				listaDiasEtapa.add(dia.toString());
			else if (dia >= etapas.get(2).getInicioEtapa()
					&& dia <= etapas.get(2).getFinEtapa()
					&& etapa.equals("seguimiento"))
				listaDiasEtapa.add(dia.toString());
		}
		return construirCadenaDias(listaDiasEtapa);
	}

	// Metodo para crear el MS programado
	@SuppressWarnings("deprecation")
	public String insertarMSProgramado() {
		//try {
			
			List esta = entityManager
					.createQuery(
							"Select m from MomentoSeguimientoGeneral_ensayo m "
									+ "where m.nombre =:nomb and "
									+ "m.cronograma.id =:crono and m.eliminado<>true")
					.setParameter("nomb", this.ms.getNombre())
					.setParameter("crono", this.cronograma.getId())
					.getResultList();
			if (esta.isEmpty()) {
				evaluacion = false;
				tratamiento = false;
				seguimiento = false;
				if (planificacion.equals("visita")) {
					if (listaDiasSeleccionados.size() == 0) {
						facesMessages.clear();
						facesMessages
								.addFromResourceBundle("msg_validacionDiasSeleccionados_ens");
						return "error";
					}
					etapa = etapasMS(listaDiasSeleccionados);
					ordenarLista(listaDiasSeleccionados);
					dias = construirCadenaDias(listaDiasSeleccionados);

				} else {
					Integer tiempoFrecuencia = tiempoFrecuanciaDias();
					Integer tiempoDurante = tiempoDuranteDias();
					// Validaciones
					Integer diasDiferencia = diaFinUltimaEtapa - primerDia;
					if(diasDiferencia<0){
						facesMessages.clear();
						facesMessages.add(new FacesMessage(
								SeamResourceBundle.getBundle().getString(
										"msg_validacionPrimerDia_ens")+ " " + listaDiasValidos.get(0).toString()+ " " +SeamResourceBundle.getBundle().getString(
												"prm_y_ens")+ " " + listaDiasValidos.get(listaDiasValidos.size()-1).toString(),
								null));
						return "error";
					}
					if (tiempoFrecuencia > diasDiferencia) {

						facesMessages.clear();
						facesMessages.add(new FacesMessage(SeamResourceBundle
								.getBundle().getString(
										"msg_validacionFrecuancia_ens")
								+ " " + diasDiferencia.toString(), null));
						return "error";
					}
					if (tiempoDurante > diasDiferencia) {
						facesMessages.clear();
						facesMessages.add(new FacesMessage(SeamResourceBundle
								.getBundle().getString(
										"msg_validacionDuranteMenor_ens")
								+ " " + diasDiferencia.toString(), null));
						return "error";
					}
					if (tiempoDurante < tiempoFrecuencia) {
						facesMessages.clear();
						facesMessages
								.addFromResourceBundle("msg_validacionDuranteMayor_ens");
						return "error";
					}

					if (tiempoFrecuencia != 0) {
						for (Integer i = primerDia; i <= primerDia
								+ tiempoDurante; i += tiempoFrecuencia) {
							listaDiasSeleccionados.add(i.toString());
						}
					} else
						listaDiasSeleccionados.add(primerDia.toString());
					etapa = etapasMS(listaDiasSeleccionados);
					dias = construirCadenaDias(listaDiasSeleccionados);

				}
				if (listaHojasCRD.size() == 0) {
					facesMessages.clear();
					facesMessages
							.addFromResourceBundle("msg_validacionHojasCrd_ens");
					return "error";

				}

				ms.setEtapa(etapa);
				ms.setDia(dias);
				if (evaluacion)
					ms.setDiasEvaluacion(diasEtapa("evaluacion",
							listaDiasSeleccionados));
				if (tratamiento)
					ms.setDiasTratamiento(diasEtapa("tratamiento",
							listaDiasSeleccionados));
				if (seguimiento)
					ms.setDiasSeguimiento(diasEtapa("seguimiento",
							listaDiasSeleccionados));
				ms.setTiempoLlenado(Integer.parseInt(this.tiempoLlenado));
				// Al crear un MS el estado del cronograma es Elaboracion
				long codigoEstado = 2;
				EstadoCronograma_ensayo estadoCronograma = (EstadoCronograma_ensayo) entityManager
						.createQuery(
								"select e from EstadoCronograma_ensayo e "
										+ "where e.codigo = :codigo")
						.setParameter("codigo", codigoEstado).getSingleResult();
				cronograma.setEstadoCronograma(estadoCronograma);

				ms.setCronograma(cronograma);
				ms.setEliminado(false);
				ms.setFechaCreacion(Calendar.getInstance().getTime());
				ms.setProgramado(true);
				Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class, user.getId());
				ms.setUsuario(usuario);
				ms.setCid(cid);
				entityManager.persist(ms);
				crearMSHojaCRD();
				entityManager.flush();
				return "ok";
			} else {
				this.facesMessages.addToControlFromResourceBundle(
						"crearMSProgramado", Severity.INFO, "msCreado");
				return "no";
			}
	/*	} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

			return "error";

		}*/
	}

	// Metodo para crear la relacion entre MS programado y las hojas CRD
	public void crearMSHojaCRD() {
		for (int i = 0; i < listaHojasCRD.size(); i++) {
			MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();
			msHojaCrd.setHojaCrd(listaHojasCRD.get(i));
			msHojaCrd.setMomentoSeguimientoGeneral(ms);
			msHojaCrd.setEliminado(false);
			entityManager.persist(msHojaCrd);

		}

	}

	// Hojas CRD
	// Metodo para maracar y desmarcar las Hojas CRD

	public void seleccionarHojaCRD() {
		try {

			if (!hojaCRDSeleccionada.containsKey(idHojaCRD)) {

				HojaCrd_ensayo crd = (HojaCrd_ensayo) entityManager
						.createQuery(
								"select crd from HojaCrd_ensayo crd where crd.id = :idCRD")
						.setParameter("idCRD", idHojaCRD).getSingleResult();
				listaHojasCRD.add(crd);

				hojaCRDSeleccionada.put(crd.getId(), crd);
			} else {
				HojaCrd_ensayo c = hojaCRDSeleccionada.get(idHojaCRD);
				hojaCRDSeleccionada.remove(idHojaCRD);
				listaHojasCRD.remove(c);

			}
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
		}

	}

	// Metodo para eliminar una hoja CRD seleccionada
	public void eliminarHojaCRD(long idHojaCRD) {

		HojaCrd_ensayo c = hojaCRDSeleccionada.get(idHojaCRD);
		hojaCRDSeleccionada.remove(idHojaCRD);
		listaHojasCRD.remove(c);
	}

	// Fin hojas CRD
	
	/**
	 * 
	 * Validaciones
	 
	 */
	private String message = "";

	// mensajes en los campos (azterizcos rojos)
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	// mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message", Severity.ERROR,
				mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}

	// Validacion de numeros
		public void number4cifras(FacesContext context, UIComponent component, Object value) {

			if (value.toString().matches("-^?\\d+$"))// valida que no sea //
				// negativo
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"msg_validacionNumero_ens"));

			if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
				// tenga caracteres
				// // extranos
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"msg_validacionNumero_ens"));
			if(value.toString().length()>4)
				//que no exceda las 4 cifras
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"mas4cifras"));
			try {// valida que sea entero
				@SuppressWarnings("unused")
				Integer val = new Integer(value.toString());
			} catch (Exception e) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"msg_validacionNumero_ens"));
			}

		}
		private static final String CARACTERES_ESPECIALES = SeamResourceBundle
				.getBundle().getString("caracteresEspeciales");
		
		
		public void textnumberCE100(FacesContext context, UIComponent component,
				Object value) {

			if (value.toString().length() > 100) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"maximoCaracteres100"));
			}

		}
		
		public void textnumber100(FacesContext context, UIComponent component,
				Object value) {

			if (!value.toString()
					.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"caracteresIncorrectos"));
			}

			if (value.toString().length() > 100) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"maximoCaracteres100"));
			}

		}
		
		
		public void textnumber250(FacesContext context, UIComponent component,
				Object value) {

			if (!value.toString()
					.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"caracteresIncorrectos"));
			}

			if (value.toString().length() > 250) {
				validatorManagerExeption(SeamResourceBundle.getBundle().getString(
						"maximoCaracteres250"));
			}

		}

	public Cronograma_ensayo getCronograma() {
		return cronograma;
	}

	public void setCronograma(Cronograma_ensayo cronograma) {
		this.cronograma = cronograma;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public String getTiempoLlenado() {
		return tiempoLlenado;
	}

	public void setTiempoLlenado(String tiempoLlenado) {
		this.tiempoLlenado = tiempoLlenado;
	}

	public MomentoSeguimientoGeneral_ensayo getMs() {
		return ms;
	}

	public void setMs(MomentoSeguimientoGeneral_ensayo ms) {
		this.ms = ms;
	}

	public Integer getDiaFinUltimaEtapa() {
		return diaFinUltimaEtapa;
	}

	public void setDiaFinUltimaEtapa(Integer diaFinUltimaEtapa) {
		this.diaFinUltimaEtapa = diaFinUltimaEtapa;
	}

	public String getPlanificacion() {

		if (this.planificacion.equals("visita"))
			visita = true;
		else
			visita = false;
		return planificacion;
	}

	public void setPlanificacion(String planificacion) {
		this.planificacion = planificacion;
		if (this.planificacion.equals("visita"))
			visita = true;
		else
			visita = false;
	}

	public List<String> getListaDiasValidos() {
		return listaDiasValidos;
	}

	public void setListaDiasValidos(List<String> listaDiasValidos) {

		this.listaDiasValidos = listaDiasValidos;
	}

	public List<String> getListaDiasSeleccionados() {

		return listaDiasSeleccionados;
	}

	public void setListaDiasSeleccionados(List<String> listaDiasSeleccionados) {

		this.listaDiasSeleccionados = listaDiasSeleccionados;
	}

	public Integer getPrimerDia() {
		return primerDia;
	}

	public void setPrimerDia(Integer primerDia) {
		this.primerDia = primerDia;
	}

	public String getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}

	public String getPeriodoFrecuancia() {
		return periodoFrecuancia;
	}

	public void setPeriodoFrecuancia(String periodoFrecuancia) {
		this.periodoFrecuancia = periodoFrecuancia;
	}

	public String getDurante() {
		return durante;
	}

	public void setDurante(String durante) {
		this.durante = durante;
	}

	public String getPeriodoDurante() {
		return periodoDurante;
	}

	public void setPeriodoDurante(String periodoDurante) {
		this.periodoDurante = periodoDurante;
	}

	public boolean isVisita() {
		return visita;
	}

	public void setVisita(boolean visita) {
		this.visita = visita;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public long getIdHojaCRD() {
		return idHojaCRD;
	}

	public void setIdHojaCRD(long idHojaCRD) {
		this.idHojaCRD = idHojaCRD;
	}

	public Hashtable<Long, HojaCrd_ensayo> getHojaCRDSeleccionada() {
		return hojaCRDSeleccionada;
	}

	public void setHojaCRDSeleccionada(
			Hashtable<Long, HojaCrd_ensayo> hojaCRDSeleccionada) {
		this.hojaCRDSeleccionada = hojaCRDSeleccionada;
	}

	public List<HojaCrd_ensayo> getListaHojasCRD() {
		return listaHojasCRD;
	}

	public void setListaHojasCRD(List<HojaCrd_ensayo> listaHojasCRD) {
		this.listaHojasCRD = listaHojasCRD;
	}

	public HojaCrd_ensayo getHojaCRD() {
		return hojaCRD;
	}

	public void setHojaCRD(HojaCrd_ensayo hojaCRD) {
		this.hojaCRD = hojaCRD;
	}

	public boolean isEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(boolean evaluacion) {
		this.evaluacion = evaluacion;
	}

	public boolean isTratamiento() {
		return tratamiento;
	}

	public void setTratamiento(boolean tratamiento) {
		this.tratamiento = tratamiento;
	}

	public boolean isSeguimiento() {
		return seguimiento;
	}

	public void setSeguimiento(boolean seguimiento) {
		this.seguimiento = seguimiento;
	}

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	public String getNombreHoja() {
		return nombreHoja;
	}

	public void setNombreHoja(String nombreHoja) {
		this.nombreHoja = nombreHoja;
	}

	public HojasCRDCustomList_ensayo getHojasCRDCustomList_ensayo() {
		return hojasCRDCustomList_ensayo;
	}

	public void setHojasCRDCustomList_ensayo(
			HojasCRDCustomList_ensayo hojasCRDCustomList_ensayo) {
		this.hojasCRDCustomList_ensayo = hojasCRDCustomList_ensayo;
	}

	public long getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(long idCronograma) {
		this.idCronograma = idCronograma;
	}
	
	public boolean isInicioConversacion() {
		return inicioConversacion;
	}


	public void setInicioConversacion(boolean inicioConversacion) {
		this.inicioConversacion = inicioConversacion;
	}

}
