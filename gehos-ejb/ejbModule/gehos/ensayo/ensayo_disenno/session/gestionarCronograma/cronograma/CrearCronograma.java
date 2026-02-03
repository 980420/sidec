package gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma;

import java.util.Calendar;
import java.util.Date;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoCronograma_ensayo;
import gehos.ensayo.entity.EstadoRegla_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
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
import org.jboss.seam.framework.CurrentDate;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearCronograma")
@Scope(ScopeType.CONVERSATION)
public class CrearCronograma {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;
	@In
	Usuario user;

	FacesMessage facesMessage;

	private Cronograma_ensayo cronograma = new Cronograma_ensayo();
	private GrupoSujetos_ensayo grupoSubjeto;
	private Etapa_ensayo etapa = new Etapa_ensayo();
	private long cid;
	private long idGrupoSujeto;
	private String periodo = "dias";

	private String tiempoDuracion;

	// Inicializar.
	public void inicializarCronograma() {

		this.grupoSubjeto = (GrupoSujetos_ensayo) entityManager.find(
				GrupoSujetos_ensayo.class, idGrupoSujeto);
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("prm_bitacoraCrear_ens"));
	}

	// Metodo para convertir el tiempoDuracion en dias.
	public Integer tiempoDuracionDias() {
		Integer tiempoResult = Integer.parseInt(tiempoDuracion);

		if (periodo.equals("semanas"))
			tiempoResult = tiempoResult * 7;
		else if (periodo.equals("meses"))
			tiempoResult = tiempoResult * 30;
		else if (periodo.equals("annos"))
			tiempoResult = tiempoResult * 365;

		return tiempoResult;

	}

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
		// }
	}

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
	public void number4cifras(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"));

		if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
			// tenga caracteres
			// // extranos
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"));
		if (value.toString().length() > 4)
			// que no exceda las 4 cifras
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

	// Metodo para crear el cronograma.
	public String insertarCronograma() {
		try {
			Integer tiempoDuracion = tiempoDuracionDias();
			// Validamos el tiempo de duracion para que se mayor que 5
			if (tiempoDuracion < 6) {
				facesMessages.clear();
				facesMessages.addFromResourceBundle("msg_validacionTiempo_ens");
				return "error";
			}
			// Al crear el cronograma el estado es Nuevo
			long codigoEstado = 1;
			EstadoCronograma_ensayo estadoCronograma = (EstadoCronograma_ensayo) entityManager
					.createQuery(
							"select e from EstadoCronograma_ensayo e "
									+ "where e.codigo = :codigo")
					.setParameter("codigo", codigoEstado).getSingleResult();
			EstadoRegla_ensayo estadoReglas = (EstadoRegla_ensayo) entityManager
					.createQuery(
							"select e from EstadoRegla_ensayo e "
									+ "where e.codigo = :codigo")
					.setParameter("codigo", codigoEstado).getSingleResult();
			cronograma.setEstadoCronograma(estadoCronograma);
			cronograma.setEliminado(false);
			cronograma.setFechaCreacion(Calendar.getInstance().getTime());
			cronograma.setGrupoSujetos(grupoSubjeto);
			cronograma.setTiempoDuracion(tiempoDuracionDias());
			cronograma.setCid(cid);
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					user.getId());
			cronograma.setUsuario(usuario);
			cronograma.setEstadoReglas(estadoReglas);
			entityManager.persist(cronograma);
			entityManager.flush();
			crearEtapa();
			//Comentariado por Evelio el momento programado pesquisaje.
			/*crearMomSeguimiento(
					SeamResourceBundle.getBundle().getString(
							"prm_pesquisaje_ens"),
					null,
					5,
					"0",
					true,
					SeamResourceBundle.getBundle().getString(
							"prm_evaluacion_ens"), new Date(), cid);*/
			crearMomSeguimiento(
					SeamResourceBundle.getBundle().getString(
							"prm_evaluacionInicial_ens"),
					null,
					15,
					"0",
					true,
					SeamResourceBundle.getBundle().getString(
							"prm_evaluacion_ens"), new Date(), cid);
			crearMomSeguimiento(
					SeamResourceBundle.getBundle().getString(
							"prm_interrupcion_ens"), SeamResourceBundle
							.getBundle().getString("prm_unaVez_ens"), null,
					null, false, null, new Date(), cid);
			return "ok";
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return "error";

		}
	}

	/**
	 * Crear los mementos de segumientos por defecto de casa
	 * cronograma(Pesquisaje, Eval Inicial e Interrupcion)
	 * 
	 * @param nombre
	 *            nombre del momento de segumiento
	 * @param descripcion
	 *            solo se llena en caso del MS no prog que se pone la ocurrencia
	 * @param tiempoLlenado
	 *            siempre 5 dias
	 * @param dia
	 *            dia 0
	 * @param programado
	 *            true si el MS es programado
	 * @param etapa
	 *            siempre evaluacion
	 * @param fechaCreacion
	 *            fecha en que se crea
	 * @param cid
	 *            bitacora
	 * @author Tania
	 */
	public void crearMomSeguimiento(String nombre, String descripcion,
			Integer tiempoLlenado, String dia, Boolean programado,
			String etapa, Date fechaCreacion, Long cid) {
		MomentoSeguimientoGeneral_ensayo momento = new MomentoSeguimientoGeneral_ensayo();
		momento.setNombre(nombre);
		momento.setDescripcion(descripcion);
		momento.setTiempoLlenado(tiempoLlenado);
		momento.setDia(dia);
		momento.setDiasEvaluacion(dia);
		momento.setProgramado(programado);
		momento.setEtapa(etapa);
		momento.setFechaCreacion(fechaCreacion);
		momento.setCronograma(this.getCronograma());
		momento.setEliminado(false);
		momento.setCid(cid);
		entityManager.persist(momento);
		entityManager.flush();

	}

	// Creamos las tres etapas
	public void crearEtapa() {
		try {

			for (int i = 0; i < 3; i++) {
				this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
						.getBundle().getString("prm_bitacoraCrearEtapa_ens"));
				Etapa_ensayo etapa = new Etapa_ensayo();
				etapa.setCid(cid);
				etapa.setFechaCreacion(Calendar.getInstance().getTime());
				etapa.setCronograma(cronograma);
				Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
						user.getId());
				etapa.setUsuario(usuario);
				// Etapa Evaluacion
				/**
				 * @author Tania
				 */
				if (i == 0) {

					etapa.setNombreEtapa(SeamResourceBundle.getBundle()
							.getString("prm_evaluacion_ens"));
					etapa.setDescripcion(SeamResourceBundle.getBundle()
							.getString("prm_etapainicial_ens"));
					etapa.setInicioEtapa(0);
					etapa.setFinEtapa(0);
				}
				// Etapa Tratamiento
				else if (i == 1) {
					etapa.setNombreEtapa(SeamResourceBundle.getBundle()
							.getString("prm_tratamiento_ens"));
					etapa.setDescripcion(SeamResourceBundle.getBundle()
							.getString("prm_etapatratamiento_ens"));
					etapa.setInicioEtapa(1);
					etapa.setFinEtapa(cronograma.getTiempoDuracion() / 2);
				}
				// Etapa Seguimiento
				else {
					etapa.setNombreEtapa(SeamResourceBundle.getBundle()
							.getString("prm_seguimiento_ens"));
					etapa.setDescripcion(SeamResourceBundle.getBundle()
							.getString("prm_etapaseguimiento_ens"));
					etapa.setInicioEtapa(cronograma.getTiempoDuracion() / 2 + 1);
					etapa.setFinEtapa(cronograma.getTiempoDuracion() - 1);
				}
				entityManager.persist(etapa);
				entityManager.flush();

			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.print(e.getMessage());
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

	public GrupoSujetos_ensayo getGrupoSubjeto() {
		return grupoSubjeto;
	}

	public void setGrupoSubjeto(GrupoSujetos_ensayo grupoSubjeto) {
		this.grupoSubjeto = grupoSubjeto;
	}

	public Etapa_ensayo getEtapa() {
		return etapa;
	}

	public void setEtapa(Etapa_ensayo etapa) {
		this.etapa = etapa;
	}

	public String getTiempoDuracion() {
		return tiempoDuracion;
	}

	public void setTiempoDuracion(String tiempoDuracion) {
		this.tiempoDuracion = tiempoDuracion;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public long getIdGrupoSujeto() {
		return idGrupoSujeto;
	}

	public void setIdGrupoSujeto(long idGrupoSujeto) {
		this.idGrupoSujeto = idGrupoSujeto;
	}

}
