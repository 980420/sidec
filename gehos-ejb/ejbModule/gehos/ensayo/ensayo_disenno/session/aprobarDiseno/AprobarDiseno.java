package gehos.ensayo.ensayo_disenno.session.aprobarDiseno;

import gehos.autenticacion.entity.Usuario;
import gehos.configuracion.management.utilidades.Validations_configuracion;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.gestionarEstudio.Validations_ensayo;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoCronograma_ensayo;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.EstadoRegla_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.session.common.auto.CronogramaList_ensayo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Scope(ScopeType.CONVERSATION)
@Name("aprobardiseno")
public class AprobarDiseno extends CronogramaList_ensayo {

	private static final long serialVersionUID = 1L;
	@In
	EntityManager entityManager;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	Usuario user;
	@In
	FacesMessages facesMessages;

	List<Cronograma_ensayo> cronograma = new ArrayList<Cronograma_ensayo>();
	Causa_ensayo causa = new Causa_ensayo();
	String descripcionCausa = "";

	private static final String EJBQL = "select cronograma from Cronograma_ensayo cronograma "
			+ "inner join cronograma.grupoSujetos grupoSujetos "
			+ "where grupoSujetos.habilitado <> false "
			+ "and cronograma.eliminado = false "
			+ "and grupoSujetos.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()}";

	/*
	 * Restricciones para la busqueda simple
	 */
	private static final String[] RESTRICTIONS = {
			"lower (cronograma.grupoSujetos.nombreGrupo) like concat('%', concat(lower(#{aprobardiseno.nombreGrupo}),'%'))",
			"cronograma.fechaCreacion >= #{aprobardiseno.desde}",
			"cronograma.fechaCreacion <= #{aprobardiseno.hasta}" };
	private static final String[] RESTRICTIONSA = {};
	private int pagina;
	Date desde, hasta;

	private String nombreGrupo = "";

	public AprobarDiseno() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("cronograma.id desc");
		setMaxResults(10);
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public int getPagina() {
		if (this.getNextFirstResult() != 0)
			return this.getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getResultCount() / 10) + 1;
		if (this.pagina > 0) {
			if (getResultCount() % 10 != 0) {
				if (pagina <= num)
					this.setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	/***
	 * metodos para la busqueda
	 */
	public void BuscarCronogramas() {
		this.first();

	}

	/**
	 * Metodos para aprobar el diseno del estudio
	 * 
	 * @author tania
	 */

	@SuppressWarnings("unchecked")
	public boolean UsuarioPromotor() {
		try {

			UsuarioEstudio_ensayo usuario = new UsuarioEstudio_ensayo();

			String promotor = "ecProm";
			usuario = (UsuarioEstudio_ensayo) entityManager
					.createQuery(
							"Select ue from UsuarioEstudio_ensayo ue "
									+ "JOIN ue.usuario u "
									+ "JOIN ue.estudioEntidad ee "
									+ "JOIN ue.role r " + "JOIN ee.estudio e "
									+ "JOIN ee.entidad ent "
									+ "where e.id =:id "
									+ "and ent.id =:ident "
									+ "and r.codigo =:promotor "
									+ "and u.id =:idUsuario "
									+ "and ee.eliminado <> true "
									+ "and ue.eliminado <> true "
									+ "and u.eliminado <> true "
									+ "and e.eliminado <> true ")
					.setParameter(
							"ident",
							seguridadEstudio.getEstudioEntidadActivo()
									.getEntidad().getId())
					.setParameter(
							"id",
							seguridadEstudio.getEstudioEntidadActivo()
									.getEstudio().getId())
					.setParameter("promotor", promotor)
					.setParameter("idUsuario", user.getId()).getSingleResult();

			if (usuario!= null) {
				return true;
			}
			else
				return false;

		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean PuedeAprobar() {
		this.cronograma = (List<Cronograma_ensayo>) entityManager
				.createQuery(
						"Select cronograma from Cronograma_ensayo cronograma "
								+ "inner join cronograma.grupoSujetos grupoSujetos "
								+ "where grupoSujetos.habilitado <> false "
								+ "and cronograma.eliminado = false "
								+ "and grupoSujetos.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()}")
				.getResultList();

		return (UsuarioPromotor() && cronogramasCompletados() && reglasCompletadas() && estudioEnDiseno());
	}

	public boolean estudioEnDiseno() {
		//si el estudio activo tiene el estado en diseno
		return seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEstadoEstudio().getCodigo() == 2;
	}
	
	public boolean reglasCompletadas() {
		// a cada cronograma le pregunto si el estado de las reglas del
		// cronograma es completado y retorno true si es asi
		int contEregla = 0;
		for (int i = 0; i < cronograma.size(); i++) {
			if (cronograma.get(i).getEstadoReglas() != null)
				if (cronograma.get(i).getEstadoReglas().getCodigo() == 2)
					contEregla++;
		}
		return (contEregla == cronograma.size());
	}

	public boolean cronogramasCompletados() {
		// a cada cronograma le pregunto si su estado es completado y retorno
		// true si es asi
		int contEcrono = 0;
		for (int i = 0; i < cronograma.size(); i++) {
			if (cronograma.get(i).getEstadoCronograma() != null)
				if (cronograma.get(i).getEstadoCronograma().getCodigo() == 3)
					contEcrono++;
		}
		return (contEcrono == cronograma.size());
	}

	public void Aprobar() {

		// obtengo el estado cronograma que sea aprobado
		EstadoCronograma_ensayo estC = (EstadoCronograma_ensayo) entityManager
				.createQuery(
						"select ec from EstadoCronograma_ensayo ec where ec.codigo = 4")
				.getSingleResult();
		// obtengo el estado cronograma que sea aprobado
		EstadoRegla_ensayo estR = (EstadoRegla_ensayo) entityManager
				.createQuery(
						"select ec from EstadoRegla_ensayo ec where ec.codigo = 3")
				.getSingleResult();
		// obtengo el estado del estudio que sea en ejecucion
		EstadoEstudio_ensayo estE = (EstadoEstudio_ensayo) entityManager
				.createQuery(
						"select ec from EstadoEstudio_ensayo ec where ec.codigo = 3")
				.getSingleResult();

		for (int i = 0; i < cronograma.size(); i++) {
			cronograma.get(i).setEstadoCronograma(estC);
			cronograma.get(i).setEstadoReglas(estR);
			entityManager.persist(cronograma.get(i));
		}
		seguridadEstudio.getEstudioEntidadActivo().getEstudio()
				.setEstadoEstudio(estE);

		entityManager.persist(seguridadEstudio.getEstudioEntidadActivo()
				.getEstudio());
		entityManager.flush();

	}

	/**
	 * Metodos para desaprobar el diseno del estudio
	 * 
	 * @author tania
	 */

	@SuppressWarnings("unchecked")
	public boolean PuedeDesaprobar() {
		this.cronograma = (List<Cronograma_ensayo>) entityManager
				.createQuery(
						"Select cronograma from Cronograma_ensayo cronograma "
								+ "inner join cronograma.grupoSujetos grupoSujetos "
								+ "where grupoSujetos.habilitado <> false "
								+ "and cronograma.eliminado = false "
								+ "and grupoSujetos.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()}")
				.getResultList();

		return (UsuarioPromotor() && cronogramasAprobados() && reglasAprobadas());
	}

	public boolean reglasAprobadas() {
		// a cada cronograma le pregunto si el estado de las reglas del
		// cronograma es aprobado y retorno true si es asi
		int contEregla = 0;
		for (int i = 0; i < cronograma.size(); i++) {
			if (cronograma.get(i).getEstadoReglas() != null) {
				if (cronograma.get(i).getEstadoReglas().getCodigo() == 3)
					contEregla++;
			}
		}
		return (contEregla == cronograma.size());
	}

	public boolean cronogramasAprobados() {
		// a cada cronograma le pregunto si su estado es aprobado y retorno
		// true si es asi
		int contEcrono = 0;

		for (int i = 0; i < cronograma.size(); i++) {
			if (cronograma.get(i).getEstadoCronograma() != null)
				if (cronograma.get(i).getEstadoCronograma().getCodigo() == 4)
					contEcrono++;
		}
		return (contEcrono == cronograma.size());
	}

	public void Limpiar() {
		descripcionCausa = "";

	}

	public String onCompleteDesaprobar(String modalName) {
		if ((facesMessages.getCurrentMessagesForControl("caus").size() > 0))
			return "return false;";
		else {
			descripcionCausa = "";
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public void Desaprobar() {

		Validations_ensayo validations = new Validations_ensayo();
		boolean[] r = new boolean[1];
		r[0] = validations.textM250(this.descripcionCausa, "caus",
				facesMessages);
		if (!r[0]) {
			// obtengo el estado cronograma que sea desaprobado
			EstadoCronograma_ensayo estCD = (EstadoCronograma_ensayo) entityManager
					.createQuery(
							"select ec from EstadoCronograma_ensayo ec where ec.codigo = 5")
					.getSingleResult();
			// obtengo el estado cronograma que sea desaprobado
			EstadoRegla_ensayo estRD = (EstadoRegla_ensayo) entityManager
					.createQuery(
							"select ec from EstadoRegla_ensayo ec where ec.codigo = 4")
					.getSingleResult();
			// obtengo el estado del estudio que sea en diseno
			EstadoEstudio_ensayo estER = (EstadoEstudio_ensayo) entityManager
					.createQuery(
							"select ec from EstadoEstudio_ensayo ec where ec.codigo = 2")
					.getSingleResult();

			for (int i = 0; i < cronograma.size(); i++) {
				cronograma.get(i).setEstadoCronograma(estCD);
				cronograma.get(i).setEstadoReglas(estRD);
				entityManager.persist(cronograma.get(i));
			}

			causa.setTipoCausa(SeamResourceBundle.getBundle().getString(
					"lbl_desaprobarDiseno_ens"));
			causa.setDescripcion(descripcionCausa);
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					user.getId());
			causa.setUsuario(usuario);
			causa.setFecha(Calendar.getInstance().getTime());
			causa.setEstudio(seguridadEstudio.getEstudioActivo());
			seguridadEstudio.getEstudioEntidadActivo().getEstudio()
					.setEstadoEstudio(estER);

			// persisto el desaprobar
			entityManager.persist(causa);
			entityManager.persist(seguridadEstudio.getEstudioEntidadActivo()
					.getEstudio());
			entityManager.flush();
			setDescripcionCausa("");
		}
	}

	public String getDescripcionCausa() {
		return descripcionCausa;
	}

	public void setDescripcionCausa(String descripcionCausa) {
		this.descripcionCausa = descripcionCausa;
	}

}