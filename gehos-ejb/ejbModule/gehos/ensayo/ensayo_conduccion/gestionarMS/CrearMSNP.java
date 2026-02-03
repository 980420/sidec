//CU 18 Adicionar MSNP
package gehos.ensayo.ensayo_conduccion.gestionarMS;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("crearMSNP")
@Scope(ScopeType.CONVERSATION)
public class CrearMSNP {

	@In
	private IActiveModule activeModule;
	@In
	private Usuario user;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;

	// Cronnogrma Especifico by Eiler
	private List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral;

	// Valores seleccionados de nomencladores--------------------------

	protected String momento = "";

	// Nomencladores-------------------------------------------------------

	// protected List<MomentoSeguimientoGeneral_ensayo> listarMomentosGenerales;

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	protected @In FacesMessages facesMessages;
	private Usuario_ensayo usuario = new Usuario_ensayo();

	private long cid = -1;
	private Long idSujeto;
	protected String seleccione;
	private boolean inicializado = false;

	// variables que definen si los campos son obligatorios o no
	protected boolean momentosRequired;

	protected String pagAnterior;

	protected Sujeto_ensayo sujeto;

	public void initConversation() {
		this.momentosRequired = true;
		this.sujeto = entityManager.find(Sujeto_ensayo.class, idSujeto);
		listarMomentos();
		inicializado = true;
	}

	@SuppressWarnings("unchecked")
	public void listarMomentos() {
		// Busco el cronograma que le corresponde al sujeto.
		Cronograma_ensayo cronograma_General = (Cronograma_ensayo) entityManager
				.createQuery(
						"select c from Cronograma_ensayo c where c.grupoSujetos.id=:id")
				.setParameter("id", sujeto.getGrupoSujetos().getId())
				.getSingleResult();
		// Aqui tengo el listado de los momento de seguimientos general
		listadoMomentosGeneral = entityManager
				.createQuery(
						"select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id=:id and msg.programado=False and msg.eliminado=False")
				.setParameter("id", cronograma_General.getId()).getResultList();
	}

	public List<String> listarMomentos1() {
		List<String> nombreMomento = new ArrayList<String>();
		try {
			for (int i = 0; i < listadoMomentosGeneral.size(); i++) {
				if (listadoMomentosGeneral.get(i).getDescripcion()
						.equals("Una vez cuando se presente") || listadoMomentosGeneral.get(i).getDescripcion()
						.equals("Una vez que se presente")) {
					@SuppressWarnings("unchecked")
					List<MomentoSeguimientoEspecifico_ensayo> cant = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
							.createQuery(
									"select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.momentoSeguimientoGeneral= :momentoS and ms.eliminado = FALSE and ms.sujeto=:Sujeto")
							.setParameter("momentoS",
									listadoMomentosGeneral.get(i))
							.setParameter("Sujeto", sujeto).getResultList();
					if (cant.size() == 0) {
						if (sujeto.getFechaInterrupcion() == null) {
							if (!listadoMomentosGeneral.get(i).getNombre().equals("Fallecimiento") && !listadoMomentosGeneral.get(i).getNombre().equals("Interrupción")) {
								nombreMomento.add(listadoMomentosGeneral.get(i).getNombre());
							}
						} else {
							nombreMomento.add(listadoMomentosGeneral.get(i).getNombre());
						}
					}
				} else {
					if (sujeto.getFechaInterrupcion() == null) {
						if (!listadoMomentosGeneral.get(i).getNombre().equals("Fallecimiento") && !listadoMomentosGeneral.get(i).getNombre().equals("Interrupción")) {
							nombreMomento.add(listadoMomentosGeneral.get(i).getNombre());
						}
					} else {
						nombreMomento.add(listadoMomentosGeneral.get(i).getNombre());
					}
				}
			}

		} catch (Exception e) {

		}
		return nombreMomento;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void persistir() throws ParseException {
		if (this.cid == -1) {
			this.cid = this.bitacora.registrarInicioDeAccion(SeamResourceBundle
					.getBundle().getString("creandoMSNP"));
		}

		long idEstadoSeguimiento = 2;
		EstadoMomentoSeguimiento_ensayo estadoMomento = entityManager.find(
				EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimiento);
		EstadoMonitoreo_ensayo estadoMonitoreo = entityManager.find(
				EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
		String noIniciada = "No iniciada";
		EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) entityManager
				.createQuery(
						"select e from EstadoHojaCrd_ensayo e where e.nombre=:noIniciada")
				.setParameter("noIniciada", noIniciada).getSingleResult();
		usuario = (Usuario_ensayo) entityManager.find(
				Usuario_ensayo.class, user.getId());
		for (int i = 0; i < listadoMomentosGeneral.size(); i++) {
			MomentoSeguimientoGeneral_ensayo ms = listadoMomentosGeneral.get(i);
			if ((ms.getNombre().equals(momento))) {
				@SuppressWarnings("unchecked")
				List<HojaCrd_ensayo> listaHojas = entityManager
						.createQuery(
								"select momentoGhojaCRD.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoGhojaCRD where momentoGhojaCRD.momentoSeguimientoGeneral.id=:id and momentoGhojaCRD.eliminado = 'false'")
						.setParameter("id", ms.getId()).getResultList();

				MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo();

				momentoEspecifico.setCid(this.cid);
				momentoEspecifico.setEliminado(false);
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomento);
				momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo);
				Calendar cal = Calendar.getInstance();
				momentoEspecifico.setFechaCreacion(cal.getTime());
				momentoEspecifico.setUsuario(usuario);
				// cal.setTime(fechainicio);
				// cal.add(Calendar.DATE,
				// Integer.valueOf(listaDiasPlanificados.get(j)));
				momentoEspecifico.setFechaInicio(cal.getTime());

				List<MomentoSeguimientoEspecifico_ensayo> momentosPro = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
						.createQuery(
								"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.eliminado = FALSE and mse.sujeto.id=:id order by  mse.dia, mse.fechaInicio asc")
						.setParameter("id", sujeto.getId()).getResultList();

				List<MomentoSeguimientoEspecifico_ensayo> momentos = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
				for (int k = 0; k < momentosPro.size(); k++) {
					if (!momentosPro.get(k).getMomentoSeguimientoGeneral()
							.getNombre().equals("Pesquisaje")
							&& !momentosPro.get(k)
									.getMomentoSeguimientoGeneral().getNombre()
									.equals("Evaluaci\u00F3n Inicial")) {
						momentos.add(momentosPro.get(k));
					}
				}

				if (momentos.size() != 0) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date fechaInicial = dateFormat.parse(dateFormat
							.format(momentos.get(0).getFechaInicio()));
					Date fechaFinal = dateFormat.parse(dateFormat
							.format(momentoEspecifico.getFechaInicio()));
					int dias = (int) ((fechaFinal.getTime() - fechaInicial
							.getTime()) / 86400000);
					momentoEspecifico.setDia(dias + momentos.get(0).getDia());
				} else {
					momentoEspecifico.setDia(0);
				}
				momentoEspecifico.setMomentoSeguimientoGeneral(ms);
				momentoEspecifico.setSujeto(sujeto);
				entityManager.persist(momentoEspecifico);
				entityManager.flush();

				for (int j2 = 0; j2 < listaHojas.size(); j2++) {
					CrdEspecifico_ensayo crdEsp = new CrdEspecifico_ensayo();
					crdEsp.setCid(bitacora
							.registrarInicioDeAccion(SeamResourceBundle
									.getBundle().getString("hojaCrear")));
					crdEsp.setEliminado(false);
					crdEsp.setEstadoHojaCrd(listaHojas.get(j2)
							.getEstadoHojaCrd());
					crdEsp.setEstadoMonitoreo(estadoMonitoreo);
					crdEsp.setHojaCrd(listaHojas.get(j2));
					crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
					crdEsp.setEstadoHojaCrd(estadoNoIniciada);
					entityManager.persist(crdEsp);
					entityManager.flush();
				}

			}

		}

	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getSeleccione() {
		return seleccione;
	}

	public void setSeleccione(String seleccione) {
		this.seleccione = seleccione;
	}

	public String getPagAnterior() {
		return pagAnterior;
	}

	public void setPagAnterior(String pagAnterior) {
		this.pagAnterior = pagAnterior;
	}

	public String getMomento() {
		return momento;
	}

	public void setMomento(String momento) {
		this.momento = momento;
	}

	public boolean isMomentosRequired() {
		return momentosRequired;
	}

	public void setMomentosRequired(boolean momentosRequired) {
		this.momentosRequired = momentosRequired;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public List<MomentoSeguimientoGeneral_ensayo> getListadoMomentosGeneral() {
		return listadoMomentosGeneral;
	}

	public void setListadoMomentosGeneral(
			List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral) {
		this.listadoMomentosGeneral = listadoMomentosGeneral;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

}