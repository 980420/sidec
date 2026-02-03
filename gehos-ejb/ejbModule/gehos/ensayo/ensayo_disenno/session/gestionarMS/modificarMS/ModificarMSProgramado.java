package gehos.ensayo.ensayo_disenno.session.gestionarMS.modificarMS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;

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

@Name("modificarMSProgramado")
@Scope(ScopeType.CONVERSATION)
public class ModificarMSProgramado {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;

	FacesMessage facesMessage;
	private MomentoSeguimientoGeneral_ensayo ms = new MomentoSeguimientoGeneral_ensayo();
	private MomentoSeguimientoGeneralHojaCrd_ensayo msCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();
	private List<MomentoSeguimientoGeneralHojaCrd_ensayo> listaMsHojaCrd = new ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>();
	private Cronograma_ensayo cronograma;
	private List<String> listaDiasValidos;
	private List<String> listaDiasSeleccionados = new ArrayList<String>();
	private List<String> listaDiasActuales = new ArrayList<String>();
	private List<String> listaDiasPlanificados = new ArrayList<String>();
	private Integer diaFinUltimaEtapa;
	private long cid;
	private long idCronograma;
	private long idMomentoSeguimiento;

	private String planificacion = "visita";
	private String tiempoLlenado;
	private Integer primerDia;
	private String frecuencia = "0";
	private String periodoFrecuancia = "dias";
	private String durante = "0";
	private String periodoDurante = "dias";
	private boolean visita = true;
	private boolean periodoRequerido = false;
	boolean evaluacion;
	boolean tratamiento;
	boolean seguimiento;
	private boolean inicioConversacion = false;
	private String dias;
	private String etapa;

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	Hashtable<Long, HojaCrd_ensayo> hojaCRDSeleccionada = new Hashtable<Long, HojaCrd_ensayo>();// enfSelected
	private long idHojaCRD;// idEnfSelect
	List<HojaCrd_ensayo> listaHojasCRD = new ArrayList<HojaCrd_ensayo>();

	// protesis

	// Inicializar.
	public void inicializarMSProgramado() {

		inicioConversacion = true;
		listaDiasValidos = new ArrayList<String>();
		this.cronograma = (Cronograma_ensayo) entityManager.find(
				Cronograma_ensayo.class, idCronograma);
		//add Evelio para grupo pesquisaje
		if(cronograma.getGrupoSujetos().getNombreGrupo().equals("Grupo Pesquisaje")){
			diaFinUltimaEtapa=0;
			//fin add Evelio
		}else{
			diaFinUltimaEtapa = (Integer) entityManager
					.createQuery(
							"select e.finEtapa from Etapa_ensayo e "
									+ "where e.cronograma.id = :idCrono "
									+ "and e.inicioEtapa>1")
									.setParameter("idCrono", cronograma.getId()).getSingleResult();
		}
		for (Integer i = 0; i <= diaFinUltimaEtapa; i++) {
			listaDiasValidos.add(i.toString());
		}
		this.ms = (MomentoSeguimientoGeneral_ensayo) entityManager.find(
				MomentoSeguimientoGeneral_ensayo.class, idMomentoSeguimiento);
		tiempoLlenado = ms.getTiempoLlenado().toString();
		dias = ms.getDia();
		String[] listaDiasSelecc;
		String[] listaUltDiaSelecc;
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
		if (dias.contains(",")) {
			listaDiasSelecc = dias.split(", ");
			listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
					.split(" " + y + " ");
			for (int i = 0; i < listaDiasSelecc.length - 1; i++) {
				listaDiasActuales.add(listaDiasSelecc[i]);
				listaDiasPlanificados.add(listaDiasSelecc[i]);
			}
			listaDiasActuales.add(listaUltDiaSelecc[0]);
			listaDiasActuales.add(listaUltDiaSelecc[1]);
			listaDiasPlanificados.add(listaUltDiaSelecc[0]);
			listaDiasPlanificados.add(listaUltDiaSelecc[1]);

		} else if (dias.contains(y)) {
			listaUltDiaSelecc = dias.split(" " + y + " ");
			listaDiasActuales.add(listaUltDiaSelecc[0]);
			listaDiasActuales.add(listaUltDiaSelecc[1]);
			listaDiasPlanificados.add(listaUltDiaSelecc[0]);
			listaDiasPlanificados.add(listaUltDiaSelecc[1]);

		} else {
			listaDiasActuales.add(dias);
			listaDiasPlanificados.add(dias);

		}

		listaMsHojaCrd = (List<MomentoSeguimientoGeneralHojaCrd_ensayo>) entityManager
				.createQuery(
						"select msCrd from MomentoSeguimientoGeneralHojaCrd_ensayo msCrd "
								+ "where msCrd.momentoSeguimientoGeneral.id = :idMs")
								.setParameter("idMs", ms.getId()).getResultList();
		for (int i = 0; i < listaMsHojaCrd.size(); i++) {
			if (!listaMsHojaCrd.get(i).getEliminado()) {
				HojaCrd_ensayo crd = listaMsHojaCrd.get(i).getHojaCrd();
				listaHojasCRD.add(crd);
				hojaCRDSeleccionada.put(crd.getId(), crd);
			}
		}
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("prm_bitacoraModificar_ens"));
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

	public void validacionPeriodo() {
		if (listaDiasActuales.size() == 0)
			periodoRequerido = true;
		else
			periodoRequerido = false;
	}

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

	// Metodo para modificar el MS programado
	public String modificarMSProgramado() {
		try {

			evaluacion = false;
			tratamiento = false;
			seguimiento = false;
			if (planificacion.equals("visita")) {
				if (listaDiasSeleccionados.size() == 0
						&& listaDiasActuales.size() == 0) {
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("msg_validacionDiasSeleccionados_ens");
					return "error";
				}
				for (int i = 0; i < listaDiasActuales.size(); i++) {
					if (!listaDiasSeleccionados.contains(listaDiasActuales
							.get(i)))
						listaDiasSeleccionados
						.add(listaDiasActuales.get(i));
				}
				etapa = etapasMS(listaDiasSeleccionados);
				ordenarLista(listaDiasSeleccionados);
				dias = construirCadenaDias(listaDiasSeleccionados);

			} else {

				if (primerDia != null) {
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
						facesMessages.add(new FacesMessage(
								SeamResourceBundle.getBundle().getString(
										"msg_validacionFrecuancia_ens")
										+ " " + diasDiferencia.toString(),
										null));
						return "error";
					}
					if (tiempoDurante > diasDiferencia) {
						facesMessages.clear();
						facesMessages.add(new FacesMessage(
								SeamResourceBundle.getBundle().getString(
										"msg_validacionDuranteMenor_ens")
										+ " " + diasDiferencia.toString(),
										null));
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
					for (int i = 0; i < listaDiasActuales.size(); i++) {
						if (!listaDiasSeleccionados
								.contains(listaDiasActuales.get(i)))
							listaDiasSeleccionados.add(listaDiasActuales
									.get(i));
					}

				} else {

					for (int i = 0; i < listaDiasActuales.size(); i++) {
						if (!listaDiasSeleccionados
								.contains(listaDiasActuales.get(i)))
							listaDiasSeleccionados.add(listaDiasActuales
									.get(i));
					}

				}
				etapa = etapasMS(listaDiasSeleccionados);
				ordenarLista(listaDiasSeleccionados);
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
			ms.setFechaActualizacion(Calendar.getInstance().getTime());
			ms.setCid(cid);
			entityManager.persist(ms);

			modificarMSHojaCRD();
			entityManager.flush();
			return "ok";

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return "error";

		}
	}

	// Metodo que devuelve un MomentoSeguimientoGeneralHojaCrd_ensayo dado una
	// hojaCRD
	public MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd(
			HojaCrd_ensayo hojaCRD, MomentoSeguimientoGeneral_ensayo ms) {
		for (int i = 0; i < listaMsHojaCrd.size(); i++) {
			if (listaMsHojaCrd.get(i).getHojaCrd().getId() == hojaCRD.getId() && 
					listaMsHojaCrd.get(i).getMomentoSeguimientoGeneral().getId() == ms.getId())
				return listaMsHojaCrd.get(i);
		}

		return null;

	}

	// Metodo para modificar la relacion entre MS programado y las hojas CRD
	public void modificarMSHojaCRD() {
		for (int i = 0; i < listaHojasCRD.size(); i++) {
			MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd = msHojaCrd(listaHojasCRD.get(i),ms);
			
			if (msHojaCrd == null) {
				msHojaCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();
				msHojaCrd.setHojaCrd(listaHojasCRD.get(i));
				msHojaCrd.setMomentoSeguimientoGeneral(ms);
				msHojaCrd.setEliminado(false);
				listaMsHojaCrd.add(msHojaCrd);
			} else{
				listaMsHojaCrd.remove(msHojaCrd);
				msHojaCrd.setEliminado(false);				
				listaMsHojaCrd.add(msHojaCrd);
			}	
			if(listaMsHojaCrd.size()==0){
				listaMsHojaCrd.add(msHojaCrd);			
			}

		}
		
		for (int i = 0; i < listaMsHojaCrd.size(); i++) {

			if (!listaHojasCRD.contains(listaMsHojaCrd.get(i).getHojaCrd()))
				listaMsHojaCrd.get(i).setEliminado(true);
			entityManager.persist(listaMsHojaCrd.get(i));

		}
		
	}

	// Hojas CRD
	// Metodo para marcar y desmarcar las Hojas CRD
	public void seleccionarHojaCRD() {
		try {
			if (!hojaCRDSeleccionada.containsKey(idHojaCRD)) {
				// cie
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

	/**
	 * Habilitar el panel de los datos de los Momentos de Seguimiento no se
	 * llaman Pesquisaje, Eval Inicial e Interrupcion
	 * 
	 * @return true si el nombre no coincide false si coincide
	 * @author Tania
	 */

	public boolean habilitarModificar() {
		if (ms.getNombre().equals(
				SeamResourceBundle.getBundle().getString("prm_pesquisaje_ens"))
				|| ms.getNombre().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_evaluacionInicial_ens"))
								|| ms.getNombre().equals(
										SeamResourceBundle.getBundle().getString(
												"prm_interrupcion_ens"))) {
			return false;


		} else
			return true;

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


	// get y set
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

	public long getIdMomentoSeguimiento() {
		return idMomentoSeguimiento;
	}

	public void setIdMomentoSeguimiento(long idMomentoSeguimiento) {
		this.idMomentoSeguimiento = idMomentoSeguimiento;
	}

	public List<String> getListaDiasActuales() {
		return listaDiasActuales;
	}

	public void setListaDiasActuales(List<String> listaDiasActuales) {
		this.listaDiasActuales = listaDiasActuales;
	}

	public MomentoSeguimientoGeneralHojaCrd_ensayo getMsCrd() {
		return msCrd;
	}

	public void setMsCrd(MomentoSeguimientoGeneralHojaCrd_ensayo msCrd) {
		this.msCrd = msCrd;
	}

	public List<MomentoSeguimientoGeneralHojaCrd_ensayo> getListaMsHojaCrd() {
		return listaMsHojaCrd;
	}

	public void setListaMsHojaCrd(
			List<MomentoSeguimientoGeneralHojaCrd_ensayo> listaMsHojaCrd) {
		this.listaMsHojaCrd = listaMsHojaCrd;
	}

	public boolean isInicioConversacion() {
		return inicioConversacion;
	}

	public void setInicioConversacion(boolean inicioConversacion) {
		this.inicioConversacion = inicioConversacion;
	}

	public boolean isPeriodoRequerido() {
		return periodoRequerido;
	}

	public void setPeriodoRequerido(boolean periodoRequerido) {
		this.periodoRequerido = periodoRequerido;
	}

	public List<String> getListaDiasPlanificados() {
		return listaDiasPlanificados;
	}

	public void setListaDiasPlanificados(List<String> listaDiasPlanificados) {
		this.listaDiasPlanificados = listaDiasPlanificados;
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

	public long getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(long idCronograma) {
		this.idCronograma = idCronograma;
	}

}
