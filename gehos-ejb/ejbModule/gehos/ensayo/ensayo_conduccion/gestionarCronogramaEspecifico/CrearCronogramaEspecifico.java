package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("crearCronogramaEspecifico")
@Scope(ScopeType.CONVERSATION)
public class CrearCronogramaEspecifico {

	protected @In
	EntityManager entityManager;
	protected @In(create = true)
	FacesMessages facesMessages;
	protected @In
	IBitacora bitacora;
	@In
	private Usuario user;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	private Sujeto_ensayo sujeto;
	private Long idSujeto;
	private Date fechaInclusion;

	// Cronnogrma Especifico by Eiler
	private List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral;
	private Date fechainicio;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void loadData() throws ParseException {
		this.sujeto = (Sujeto_ensayo) entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id=:id")
				.setParameter("id", idSujeto).getSingleResult();
		if(this.sujeto.getFechaInclucion() != null){
			fechaInclucion();
		}
		

	}

	/**
	 * @author Modificado Yasmani
	 * 
	 * @return true (si tiene MS Pesquisaje y está completado) y Cualquier otro
	 *         MS del 0 el estado es distinto de No iniciado
	 * 
	 * **/
	@SuppressWarnings("unchecked")
	public Boolean puedeCrearCronogramaEspecifico() {
		Integer dia = 0;
		int cantMS = 0;
		List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEvaluacion = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
		List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEval = entityManager
				.createQuery(
						"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:id and mse.eliminado!=true and mse.dia=:dia0")
				.setParameter("id", sujeto.getId()).setParameter("dia0", dia)
				.getResultList();
		for (int j = 0; j < listadoMomentosEval.size(); j++) {
			if (listadoMomentosEval.get(j).getMomentoSeguimientoGeneral()
					.getNombre().equals("Evaluaci\u00F3n Inicial")) {
				listadoMomentosEvaluacion.add(listadoMomentosEval.get(j));
			}
		}

		for (int i = 0; i < listadoMomentosEvaluacion.size(); i++) {

			if ((listadoMomentosEvaluacion.get(i)
					.getMomentoSeguimientoGeneral().getNombre()
					.equals("Evaluaci\u00F3n Inicial"))
					&& (listadoMomentosEvaluacion.get(i)
							.getEstadoMomentoSeguimiento().getCodigo() == 3
							|| listadoMomentosEvaluacion.get(i)
									.getEstadoMomentoSeguimiento().getCodigo() == 5 || ((listadoMomentosEvaluacion
							.get(i).getEstadoMomentoSeguimiento().getCodigo() == 1 || listadoMomentosEvaluacion
							.get(i).getEstadoMomentoSeguimiento().getCodigo() == 4) && atrasadoIniciado(listadoMomentosEvaluacion
							.get(i))))) {
				cantMS++;
			}
		}
		if ((listadoMomentosEvaluacion.size() == 1)
				&& listadoMomentosEvaluacion.size() == cantMS) {
			return true;
		} else
			return false;

	}

	@SuppressWarnings("unchecked")
	public boolean atrasadoIniciado(
			MomentoSeguimientoEspecifico_ensayo momeSegui) {
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager
				.createQuery(
						"select hoja from CrdEspecifico_ensayo hoja where hoja.momentoSeguimientoEspecifico=:momentoSeguimientoEspecifico and hoja.eliminado= 'false'")
				.setParameter("momentoSeguimientoEspecifico", momeSegui)
				.getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if (listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 1
					&& listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 3
					&& listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 4 && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())) {
				completa = false;
				break;
			}
		}
		return completa;

	}
	
	public boolean estaEliminadaMomentoHojaCrd(MomentoSeguimientoGeneral_ensayo general, HojaCrd_ensayo hoja){
		boolean esta = false;
		MomentoSeguimientoGeneralHojaCrd_ensayo otro = (MomentoSeguimientoGeneralHojaCrd_ensayo) entityManager.createQuery("select mom from MomentoSeguimientoGeneralHojaCrd_ensayo mom where mom.momentoSeguimientoGeneral.id=:idMomGen and mom.hojaCrd.id=:idHoja")
				.setParameter("idMomGen", general.getId()).setParameter("idHoja", hoja.getId()).getSingleResult();
		
		if(otro.getEliminado()){
			esta = true;
		}
		return esta;
	}

	public Date fechaInclucion() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String[] fechaArreglo = this.sujeto.getFechaInclucion().split("/");
		String fecha = "";
		if (fechaArreglo.length == 1) {
			fecha = "01" + "/" + "01" + "/" + fechaArreglo[0];
		} else if (fechaArreglo.length == 2) {
			fecha = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
		} else if (fechaArreglo.length == 3) {
			if (fechaArreglo[0].equals("****")) {
				fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
			} else {
				fecha = fechaArreglo[0] + "/" + fechaArreglo[1] + "/"
						+ fechaArreglo[2];
			}

		}
		this.fechaInclusion = formatter.parse(fecha);

		return fechaInclusion;
	}

	// CU 16 Crear cronograma especifico.
	// Cronnograma Especifico by Eiler
	@SuppressWarnings("unchecked")
	public void Cronograma_Especifico() {
		try {

			// Busco el cronograma que le corresponde al sujeto.
			Sujeto_ensayo sub = entityManager.find(Sujeto_ensayo.class,
					idSujeto);
			long idG = sub.getGrupoSujetos().getId();
			usuario = (Usuario_ensayo) entityManager.find(Usuario_ensayo.class,
					user.getId());
			// Cronograma_ensayo
			// cronograma_General=entityManager.find(Cronograma_ensayo.class,
			// idG);
			long id_cronograma_General = (Long) entityManager
					.createQuery(
							"select c.id from Cronograma_ensayo c where c.grupoSujetos.id=:id")
					.setParameter("id", idG).getSingleResult();
			// Cronograma_ensayo
			// cronograma_General1=(Cronograma_ensayo)entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:id").setParameter("id",
			// idG).getSingleResult();
			// Aqui tengo el listado de los momento de seguimientos general
			String etapa = "Evaluaci\u00F3n";

			listadoMomentosGeneral = entityManager
					.createQuery(
							"select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id=:id and msg.programado=True")
					.setParameter("id", id_cronograma_General).getResultList();
			for (int i = 0; i < listadoMomentosGeneral.size(); i++) {

				MomentoSeguimientoGeneral_ensayo ms = listadoMomentosGeneral
						.get(i);
				if ((ms.getEliminado() != true) && (!ms.getDia().equals("0"))) {
					List<String> listaDiasPlanificados = picarCadena(ms);
					long codigo = 2;
					// EstadoMomentoSeguimiento_ensayo
					// estadoMomento=(EstadoMomentoSeguimiento_ensayo)
					// entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.id=:id").setParameter("id",
					// idEstadoSeguimiento).getSingleResult();
					EstadoMonitoreo_ensayo estadoMonitoreo = (EstadoMonitoreo_ensayo) entityManager
							.createQuery(
									"select e from EstadoMonitoreo_ensayo e where e.codigo=:codigo")
							.setParameter("codigo", codigo).getSingleResult();
					EstadoMomentoSeguimiento_ensayo estadoMomento = entityManager
							.find(EstadoMomentoSeguimiento_ensayo.class, codigo);
					// EstadoMonitoreo_ensayo
					// estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class,
					// idEstadoSeguimiento);
					for (int j = 0; j < listaDiasPlanificados.size(); j++) {
						//
						
						if(!listaDiasPlanificados.get(j).equals("0")){
						List<HojaCrd_ensayo> listaHojas = entityManager
								.createQuery(
										"select crdMomento.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo crdMomento where crdMomento.momentoSeguimientoGeneral.id=:id and crdMomento.eliminado = 'false'")
								.setParameter("id", ms.getId()).getResultList();

						MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo();

						momentoEspecifico
								.setCid(bitacora
										.registrarInicioDeAccion(SeamResourceBundle
												.getBundle().getString(
														"momentoCrear")));
						momentoEspecifico.setDia(Integer
								.valueOf(listaDiasPlanificados.get(j)));
						momentoEspecifico.setEliminado(false);
						momentoEspecifico
								.setEstadoMomentoSeguimiento(estadoMomento);
						momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo);
						Calendar cal = Calendar.getInstance();
						momentoEspecifico.setFechaCreacion(cal.getTime());
						momentoEspecifico.setUsuario(usuario);
						cal.setTime(fechainicio);
					
						//momentoEspecifico.setFechaInicio(fechainicio);
						
						
						cal.add(Calendar.DATE, Integer.valueOf(listaDiasPlanificados.get(j)) - 1);
						momentoEspecifico.setFechaInicio(cal.getTime());
						
							
						
						cal.add(Calendar.DATE, ms.getTiempoLlenado());
						momentoEspecifico.setFechaFin(cal.getTime());
						momentoEspecifico.setMomentoSeguimientoGeneral(ms);
						momentoEspecifico.setSujeto(sujeto);
						entityManager.persist(momentoEspecifico);
						entityManager.flush();

						long noIniciada = 2;
						EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) entityManager
								.createQuery(
										"select e from EstadoHojaCrd_ensayo e where e.codigo=:noIniciada")
								.setParameter("noIniciada", noIniciada)
								.getSingleResult();

						for (int j2 = 0; j2 < listaHojas.size(); j2++) {
							CrdEspecifico_ensayo crdEsp = new CrdEspecifico_ensayo();
							crdEsp.setCid(bitacora
									.registrarInicioDeAccion(SeamResourceBundle
											.getBundle().getString("hojaCrear")));
							crdEsp.setEliminado(false);
							crdEsp.setEstadoHojaCrd(estadoNoIniciada);
							crdEsp.setEstadoMonitoreo(estadoMonitoreo);
							crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
							crdEsp.setHojaCrd(listaHojas.get(j2));
							crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
							crdEsp.setEstadoHojaCrd(estadoNoIniciada);
							// crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
							entityManager.persist(crdEsp);
							entityManager.flush();
						}
					  }
					}
				}

			}
			Calendar cal = Calendar.getInstance();
			sujeto.setCronogramaEspecifico(true);
			sujeto.setFechaInicronograma(fechainicio);
			sujeto.setFechaActualizacion(cal.getTime());
			sujeto.setCid(bitacora
					.registrarInicioDeAccion(SeamResourceBundle.getBundle()
							.getString("creandoCronogramaEspecificoSujeto")));
			entityManager.persist(sujeto);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Codigo para picar la cadena de dias by Evelio.
	private List<String> picarCadena(MomentoSeguimientoGeneral_ensayo ms) {
		String dias = ms.getDia();
		String[] listaDiasSelecc;
		String[] listaUltDiaSelecc;
		List<String> listaDiasPlanificados = new ArrayList<String>();
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
		if (dias.contains(",")) {
			listaDiasSelecc = dias.split(", ");
			listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
					.split(" " + y + " ");
			for (int j = 0; j < listaDiasSelecc.length - 1; j++) {
				listaDiasPlanificados.add(listaDiasSelecc[j]);
			}
			listaDiasPlanificados.add(listaUltDiaSelecc[0]);
			listaDiasPlanificados.add(listaUltDiaSelecc[1]);

		} else if (dias.contains(y)) {
			listaUltDiaSelecc = dias.split(" " + y + " ");
			listaDiasPlanificados.add(listaUltDiaSelecc[0]);
			listaDiasPlanificados.add(listaUltDiaSelecc[1]);

		} else {
			listaDiasPlanificados.add(dias);

		}
		return listaDiasPlanificados;
	}

	// Listado de momentos de seguimiento especificos by Eiler
	public List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto() {
		List<MomentoSeguimientoEspecifico_ensayo> momentos = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
		try {
			momentos = entityManager
					.createQuery(
							"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:id order by mse.fechaInicio asc")
					.setParameter("id", sujeto.getId()).getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return momentos;
	}

	// Listado de nombres de los momentos de Seguimiento by Eiler
	public List<String> nombresMomentos() {
		List<MomentoSeguimientoEspecifico_ensayo> momentos = momentosSujeto();
		List<String> nombres = new ArrayList<String>();
		for (int i = 0; i < momentos.size(); i++) {
			int cont = 0;
			for (int j = 0; j < nombres.size(); j++) {
				if (momentos.get(i).getMomentoSeguimientoGeneral().getNombre()
						.equals(nombres.get(j))) {
					cont++;
				}
			}
			if (cont == 0) {
				nombres.add(momentos.get(i).getMomentoSeguimientoGeneral()
						.getNombre());
			}

		}
		return nombres;
	}

	// Compruebo si el nombre del momentopertenece al momento de seguimiento by
	// Eiler
	public Boolean PerteneceMomento(
			MomentoSeguimientoEspecifico_ensayo momento, String nombre,
			Date fecha) {
		String nombreMomento = momento.getMomentoSeguimientoGeneral()
				.getNombre();
		Date fechaMomento = momento.getFechaInicio();
		if ((nombre.equals(nombreMomento)) && (fecha.equals(fechaMomento))) {
			return true;
		}
		return false;
	}

	@End
	public void salir() {

	}

	@Remove
	@Destroy
	public void destroy() {
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public Date getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(Date fechainicio) {
		this.fechainicio = fechainicio;
	}

	public Date getFechaInclusion() {
		return fechaInclusion;
	}

	public void setFechaInclusion(Date fechaInclusion) {
		this.fechaInclusion = fechaInclusion;
	}

}
