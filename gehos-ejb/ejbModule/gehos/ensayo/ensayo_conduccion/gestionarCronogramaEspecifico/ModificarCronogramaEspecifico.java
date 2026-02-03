package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.Bitacora;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("modificarCronogramaEspecifico")
@Scope(ScopeType.CONVERSATION)
public class ModificarCronogramaEspecifico {

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	@In
	Usuario user;
	@In
	SeguridadEstudio seguridadEstudio;
	private Sujeto_ensayo sujeto;
	private Long idSujeto;
	private Date fechaInclusion;
	private boolean puedeModificar;
	private boolean puedeEliminar;
	private String causaModificacion = "";
	List<MomentoSeguimientoEspecifico_ensayo> momentos;

	private String causaGuardar = "";
	private List<Integer> tamannoEtapas = new ArrayList<Integer>(3);
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle()
			.getString("caracteresEspeciales");

	public String getCausaGuardar() {
		return causaGuardar;
	}

	public void setCausaGuardar(String causaGuardar) {
		this.causaGuardar = causaGuardar;
	}

	private Object causa = null;

	public Object getCausa() {
		return causa;
	}

	public void setCausa(Object causa) {
		this.causa = causa;
	}

	private Date fechaVieja;

	private Date fecha;

	private Long idmomento;
	private MomentoSeguimientoEspecifico_ensayo momento;

	// Cronnogrma Especifico by Eiler
	private List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral;
	private Date fechainicio;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void loadData() {
		this.sujeto = (Sujeto_ensayo) entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id=:id")
				.setParameter("id", idSujeto).getSingleResult();
		this.momentosSujeto();
	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void loadDataCompletar() throws ParseException{
		this.sujeto = (Sujeto_ensayo) entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id=:id")
				.setParameter("id", idSujeto).getSingleResult();
		

		this.fechaInclucion();
		this.setMomento((MomentoSeguimientoEspecifico_ensayo) entityManager
				.createQuery(
						"select m from MomentoSeguimientoEspecifico_ensayo m where m.id=:id")
				.setParameter("id", idmomento).getSingleResult());
		fechaVieja = getMomento().getFechaInicio();
		if (fecha == null)
			fecha = fechaVieja;

	}
	
	public Date fechaInclucion() throws ParseException{
		 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		 String[] fechaArreglo = this.sujeto.getFechaInclucion().split("/");
		 String fecha = "";
		 if(fechaArreglo.length == 1){
			 fecha = "01" + "/" + "01" + "/" + fechaArreglo[0];
		 }else if(fechaArreglo.length == 2){
			 fecha = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
		 }
		 else if(fechaArreglo.length == 3){
			 if(fechaArreglo[0].equals("****")){
				 fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
			 }else{
				 fecha = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
			 }
			 
		 }
	     this.fechaInclusion = formatter.parse(fecha);
		
		return fechaInclusion;
	}

	public List<String> listaDias(String dias) {

		List<String> listaDias = new ArrayList<String>();
		String[] listaDiasSelecc;
		String[] listaUltDiaSelecc;
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
		if (dias.contains(",")) {
			listaDiasSelecc = dias.split(", ");
			listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
					.split(" " + y + " ");
			for (int i = 0; i < listaDiasSelecc.length - 1; i++) {
				listaDias.add(listaDiasSelecc[i]);

			}
			listaDias.add(listaUltDiaSelecc[0]);
			listaDias.add(listaUltDiaSelecc[1]);

		} else if (dias.contains(y)) {
			listaUltDiaSelecc = dias.split(" " + y + " ");
			listaDias.add(listaUltDiaSelecc[0]);
			listaDias.add(listaUltDiaSelecc[1]);

		} else {
			listaDias.add(dias);

		}
		return listaDias;
	}

	// Metodo para construir la cadena con los dias de visita.
	@SuppressWarnings("unchecked")
	public List<String> diasEtapa(String etapa, String dias) {
		List<String> listaDias = new ArrayList<String>();
		List<String> listaDiasEtapa = new ArrayList<String>();
		if (dias != null) {
			listaDias = listaDias(dias);

			Cronograma_ensayo crono = (Cronograma_ensayo) entityManager
					.createQuery(
							"select e from Cronograma_ensayo e "
									+ "where e.grupoSujetos.id = :idG ")
					.setParameter("idG", this.sujeto.getGrupoSujetos().getId())
					.getSingleResult();
			List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
					.createQuery(
							"select e from Etapa_ensayo e "
									+ "where e.cronograma.id = :idCrono "
									+ "order by e.id")
					.setParameter("idCrono", crono.getId()).getResultList();
			for (int i = 0; i < listaDias.size(); i++) {
				Integer dia = Integer.parseInt(listaDias.get(i));
				if (dia == 0 && etapa.equals("evaluacion")) {
					listaDiasEtapa.add(dia.toString());
					return listaDiasEtapa;
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
		}

		return listaDiasEtapa;
	}

	// Listado de momentos de seguimiento especificos by Eiler
	public List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto() {

		momentos = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
		try {
			momentos = entityManager
					.createQuery(
							"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.eliminado = FALSE and mse.sujeto.id=:id order by  mse.dia, mse.fechaInicio asc")
					.setParameter("id", sujeto.getId()).getResultList();

			int contEva = 0;
			int contTra = 0;
			int contSeg = 0;

			Cronograma_ensayo crono = (Cronograma_ensayo) entityManager
					.createQuery(
							"select e from Cronograma_ensayo e "
									+ "where e.grupoSujetos.id = :idG ")
					.setParameter("idG", this.sujeto.getGrupoSujetos().getId())
					.getSingleResult();

			List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
					.createQuery(
							"select e from Etapa_ensayo e "
									+ "where e.cronograma.id = :idCrono "
									+ "order by e.id")
					.setParameter("idCrono", crono.getId()).getResultList();

			List<String> diaEva = new ArrayList<String>();
			List<String> diaTra = new ArrayList<String>();
			List<String> diaSeg = new ArrayList<String>();

			List<String> nombres = new ArrayList<String>();
			for (int i = 0; i < momentos.size(); i++) {
				int cont = 0;
				for (int j = 0; j < nombres.size(); j++) {
					if (momentos.get(i).getMomentoSeguimientoGeneral()
							.getNombre().equals(nombres.get(j))) {
						cont++;
					}
				}

				if (!momentos.get(i).getMomentoSeguimientoGeneral()
						.getProgramado()) {
					
					if (momentos.get(i).getDia() >= 1 && momentos.get(i).getDia() <= etapas.get(1).getFinEtapa()){
						contTra++;
					}else if (momentos.get(i).getDia() >= etapas.get(2).getInicioEtapa()
							&& momentos.get(i).getDia() <= etapas.get(2).getFinEtapa()
							){
						contSeg++;
					}else{
						contEva++;
					}
				} else {
					if (cont == 0) {

						// me quede no llena bienk
						diaEva = diasEtapa("evaluacion", momentos.get(i)
								.getMomentoSeguimientoGeneral().getDia());
						diaTra = diasEtapa("tratamiento", momentos.get(i)
								.getMomentoSeguimientoGeneral().getDia());
						diaSeg = diasEtapa("seguimiento", momentos.get(i)
								.getMomentoSeguimientoGeneral().getDia());

						if (diaEva.size() > 0) {
							contEva += diaEva.size();
						}
						if (diaTra.size() > 0) {
							contTra += diaTra.size();
						}
						if (diaSeg.size() > 0) {
							contSeg += diaSeg.size();
						}
						nombres.add(momentos.get(i)
								.getMomentoSeguimientoGeneral().getNombre());
					}
				}

			}

			tamannoEtapas.add(0, contEva);
			tamannoEtapas.add(1, contTra);
			tamannoEtapas.add(2, contSeg);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return momentos;
	}

	// Listado de nombres de los momentos de Seguimiento by Eiler
	public List<String> nombresMomentos() {
		List<MomentoSeguimientoEspecifico_ensayo> momentos = this.momentos;
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
	// Agrega la comparaci'on con el estado del MS para poder modificar solo los
	// MS que esten en No_Iniciado
	public Boolean PerteneceMomento(
			MomentoSeguimientoEspecifico_ensayo momento, String nombre,
			Date fecha) {
		String nombreMomento = momento.getMomentoSeguimientoGeneral()
				.getNombre();
		Long estadoMS = momento.getEstadoMomentoSeguimiento().getCodigo();
		Boolean esprogramado = momento.getMomentoSeguimientoGeneral()
				.getProgramado();
		Date fechaMomento = momento.getFechaInicio();
		if ((estadoMS == 2)) {
			if (esprogramado) {
				if ((nombre.equals(nombreMomento))
						&& (fecha.equals(fechaMomento))) {
					return true;
				}
			}
		}
		return false;

	}

	public Boolean PerteneceMomentoIna(
			MomentoSeguimientoEspecifico_ensayo momento, String nombre,
			Date fecha) {
		String nombreMomento = momento.getMomentoSeguimientoGeneral()
				.getNombre();
		Long estadoMS = momento.getEstadoMomentoSeguimiento().getCodigo();
		Boolean esprogramado = momento.getMomentoSeguimientoGeneral()
				.getProgramado();
		Date fechaMomento = momento.getFechaInicio();
		if ((estadoMS != 2) || (!esprogramado)) {
			if ((nombre.equals(nombreMomento)) && (fecha.equals(fechaMomento))) {
				return true;
			}
		}
		return false;

	}

	public Boolean puedeModificarseCronograma(Sujeto_ensayo idSujeto) {
		puedeModificar = false;
			@SuppressWarnings("unchecked")
			List<MomentoSeguimientoEspecifico_ensayo> momentoSujetoList = (List<MomentoSeguimientoEspecifico_ensayo>)entityManager
					.createQuery(
							"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:id and (mse.momentoSeguimientoGeneral.nombre != 'Evaluaci\u00F3n Inicial') and mse.momentoSeguimientoGeneral.programado = TRUE order by mse.fechaInicio asc")
					.setParameter("id", idSujeto.getId()).getResultList();
			for (int i = 0; i < momentoSujetoList.size(); i++) {
				if(momentoSujetoList.get(i).getEstadoMomentoSeguimiento().getCodigo() == 2 || (momentoSujetoList.get(i).getEstadoMomentoSeguimiento().getCodigo() == 4 && momentoAtrasadoNoIniciado(momentoSujetoList.get(i)))){
					puedeModificar = true;
					break;
				}
			}
			
					
		return puedeModificar;
	}
	
	@SuppressWarnings("unchecked")
	public boolean momentoAtrasadoNoIniciado(
			MomentoSeguimientoEspecifico_ensayo momeSegui) {
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();

		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(
				"select e from CrdEspecifico_ensayo e where e.momentoSeguimientoEspecifico.id=:IdMomento").setParameter("IdMomento", momeSegui.getId()).getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if (listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 2) {
				completa = false;
				break;
			}
		}
		return completa;

	}

	@SuppressWarnings("unchecked")
	public Boolean puedeEliminarseCronograma(Sujeto_ensayo sujeto) {
		puedeEliminar = true;

		List<MomentoSeguimientoEspecifico_ensayo> momentoSujetoList = entityManager
				.createQuery(
						"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:id and (mse.momentoSeguimientoGeneral.nombre != 'Pesquisaje' and mse.momentoSeguimientoGeneral.nombre != 'Evaluaci\u00F3n Inicial') order by mse.fechaInicio asc")
				.setParameter("id", sujeto.getId()).getResultList();
				for (int j = 0; j < momentoSujetoList.size(); j++) {
					List<CrdEspecifico_ensayo> crdMomento = entityManager
							.createQuery(
									"select crdE from CrdEspecifico_ensayo crdE where crdE.momentoSeguimientoEspecifico.id=:id")
							.setParameter("id", momentoSujetoList.get(j).getId())
							.getResultList();
					for (int k = 0; k < crdMomento.size(); k++) {
						if (crdMomento.get(k).getEstadoHojaCrd().getCodigo() == 3) {
							puedeEliminar = false;
							break;
						}
					}
					
				}

		return puedeEliminar;
	}

	// Modificar el cronograma especifico
	// CU 19 Modificar cronograma especifico
	public void actualizarCronograma() {

		List<MomentoSeguimientoEspecifico_ensayo> momentosEspecificos = entityManager
				.createQuery(
						"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:id order by mse.fechaInicio asc")
				.setParameter("id", sujeto.getId()).getResultList();
		MomentoSeguimientoEspecifico_ensayo momento = (MomentoSeguimientoEspecifico_ensayo) entityManager
				.createQuery(
						"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.id=:id")
				.setParameter("id", idmomento).getSingleResult();
		Integer dias = diferenciaFechas(fecha, fechaVieja);
		momento.setFechaInicio(fecha);
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.add(Calendar.DATE, dias);
		momento.setFechaFin(cal.getTime());
		momento.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("modificandoCronogramaEspecifico")));
		entityManager.persist(momento);
		entityManager.flush();

		for (int i = 0; i < momentosEspecificos.size(); i++) {
			if ((momentosEspecificos.get(i).getFechaInicio()
					.compareTo(fechaVieja) > 0)
					&& (momentosEspecificos.get(i).getId() != momento.getId())) {
				cal.setTime(momentosEspecificos.get(i).getFechaInicio());
				cal.add(Calendar.DATE, dias);
				momentosEspecificos.get(i).setFechaInicio(cal.getTime());
				if(momentosEspecificos.get(i).getFechaFin() != null){
					cal.setTime(momentosEspecificos.get(i).getFechaFin());
					cal.add(Calendar.DATE, dias);
					momentosEspecificos.get(i).setFechaFin(cal.getTime());
				}
				momentosEspecificos.get(i).setCid(
						bitacora.registrarInicioDeAccion(SeamResourceBundle
								.getBundle().getString(
										"modificandoCronogramaEspecifico")));
				entityManager.persist(momentosEspecificos.get(i));
				entityManager.flush();
			}
		}
		agregarCausaModificado();

	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void modificarCronogramaEspecifico(Date fecha) {
		fechaVieja = fecha;
		this.fecha = fecha;
		// this.idmomento=idmomento;

	}

	private Integer diferenciaFechas(Date nueva, Date vieja) {
		Long milisegundos;
		if(nueva.compareTo(vieja) > 0){
			milisegundos = nueva.getTime() - vieja.getTime();
		}else {
			milisegundos = vieja.getTime() - nueva.getTime();
		}
		Long dias = milisegundos / (1000 * 60 * 60 * 24);
		return dias.intValue();
	}

	public String validarCausa() {
		if (causa != null) {
			causaGuardar = causa.toString();
			int longitud = causa.toString().length();
			boolean noExtranno = causa.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$");
			if (causa != null && noExtranno && longitud <= 250) {
				causa = null;
				return "Richfaces.showModalPanel('mpAdvertenciaModificarCronogramaEsp')";
			}
		}
		return "";
	}

	public void agregarCausaModificado() {
		// Sujeto_ensayo sujeto=(Sujeto_ensayo)
		// entityManager.createQuery("select s from Sujeto_ensayo s where s.id=:idSujeto").setParameter("idSujeto",
		// idSujeto).getSingleResult();
		Cronograma_ensayo cronograma = (Cronograma_ensayo) entityManager
				.createQuery(
						"select c from Cronograma_ensayo c where c.grupoSujetos.id=:idGrupo")
				.setParameter("idGrupo", sujeto.getGrupoSujetos().getId())
				.getSingleResult();

		Causa_ensayo causa = new Causa_ensayo();
		causa.setMomentoSeguimientoEspecifico(getMomento());
		causa.setCronograma(cronograma);
		causa.setDescripcion(this.causaGuardar);
		causa.setSujeto(sujeto);
		causa.setTipoCausa("Modificando cronograma especifico");
		causa.setEstudio(seguridadEstudio.getEstudioEntidadActivo()
				.getEstudio());
		causa.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("causaModificarCronograma")));
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
				user.getId());
		causa.setUsuario(usuario);
		causa.setFecha(Calendar.getInstance().getTime());
		entityManager.persist(causa);
		entityManager.flush();
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Long getIdmomento() {
		return idmomento;
	}

	public void setIdmomento(Long idmomento) {
		this.idmomento = idmomento;
	}

	public void setFecha(Object fecha) {
		this.fecha = (Date) fecha;
	}

	public Date getFechaVieja() {
		return fechaVieja;
	}

	public void setFechaVieja(Date fechaVieja) {
		this.fechaVieja = fechaVieja;
	}

	public String getCausaModificacion() {
		return causaModificacion;
	}

	public void setCausaModificacion(String causaModificacion) {
		this.causaModificacion = causaModificacion;
	}

	public MomentoSeguimientoEspecifico_ensayo getMomento() {
		return momento;
	}

	public void setMomento(MomentoSeguimientoEspecifico_ensayo momento) {
		this.momento = momento;
	}

	public List<Integer> getTamannoEtapas() {
		return tamannoEtapas;
	}

	public void setTamannoEtapas(List<Integer> tamannoEtapas) {
		this.tamannoEtapas = tamannoEtapas;
	}

	public List<MomentoSeguimientoEspecifico_ensayo> getMomentos() {
		return momentos;
	}

	public void setMomentos(List<MomentoSeguimientoEspecifico_ensayo> momentos) {
		this.momentos = momentos;
	}

	public boolean isPuedeModificar() {
		return puedeModificar;
	}

	public void setPuedeModificar(boolean puedeModificar) {
		this.puedeModificar = puedeModificar;
	}

	public boolean isPuedeEliminar() {
		return puedeEliminar;
	}

	public void setPuedeEliminar(boolean puedeEliminar) {
		this.puedeEliminar = puedeEliminar;
	}

	public Date getFechaInclusion() {
		return fechaInclusion;
	}

	public void setFechaInclusion(Date fechaInclusion) {
		this.fechaInclusion = fechaInclusion;
	}

}
