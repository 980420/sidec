package gehos.ensayo.ensayo_disenno.gestionarGruposujetos;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.GrupoSujetos_ensayo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

@Scope(ScopeType.CONVERSATION)
@Name("gruposujetosList")
public class GruposujetosList extends EntityQuery<GrupoSujetos_ensayo> {

	private GrupoSujetos_ensayo gruposujetos = new GrupoSujetos_ensayo();
	boolean busquedaAvanzada = false;
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;
	int error = -1;
	private int pagina;
	long idGruposujetos, idGruposujetoHab;
	Date desde, hasta;
	private Long idGruposujetosEliminar;

	private static final String EJBQL = "select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.eliminado = false";

	/*
	 * Restricciones para la busqueda simple
	 */
	private static final String[] RESTRICTIONSS = {
			"lower (gruposujetos.nombreGrupo) like concat('%', concat(lower(#{gruposujetosList.gruposujetos.nombreGrupo.trim()}),'%'))",
			"gruposujetos.fechaCreacion >= #{gruposujetosList.desde}",
			"gruposujetos.fechaCreacion <= #{gruposujetosList.hasta}",
			"gruposujetos.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()}" };

	/*
	 * Restricciones para la busqueda avanzada
	 */
	private static final String[] RESTRICTIONSA = {};

	public GruposujetosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("id desc");

	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
	}

	public boolean puedeAdd() {
		Long estado = seguridadEstudio.getEstudioEntidadActivo().getEstudio()
				.getEstadoEstudio().getCodigo();

		if (estado > 2) {
			return false;

		} else
			return true;

	}

	/**
	 * Metodo que cambia el tipo de busqueda que se realizara
	 */
	public void cambiarBusqueda() {
		busquedaAvanzada = !busquedaAvanzada;
		gruposujetos = new GrupoSujetos_ensayo();
		desde = null;
		hasta = null;

		if (busquedaAvanzada) {
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSA));
			this.refresh();
		} else {
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
			this.refresh();
		}
	}

	public void cancelarBusqueda() {
		gruposujetos = new GrupoSujetos_ensayo();
	}

	/**
	 * Metodo para seleccionar el(la) Gruposujetos a modificar
	 */
	public void seleccionarModificar(long idGruposujetos)
			throws IllegalStateException, SecurityException, SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", idGruposujetos).getSingleResult();

			this.idGruposujetos = idGruposujetos;
			error = 0;
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}

	/**
	 * Método para seleccionar el Grupo de Sujeto a ver
	 */
	public void seleccionarVer(long idGruposujetos)
			throws IllegalStateException, SecurityException, SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", idGruposujetos).getSingleResult();

			this.idGruposujetos = idGruposujetos;
			error = 0;
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}

	/**
	 * Método para seleccionar el Grupo de Sujeto a eliminar
	 */
	public void seleccionarEliminar(long idGruposujetos)
			throws IllegalStateException, SecurityException, SystemException {
		this.idGruposujetos = idGruposujetos;
	}

	// select item to removal
	@SuppressWarnings("unchecked")
	public String seleccionarE() throws IllegalStateException,
			SecurityException, SystemException {
		try {
			List<GrupoSujetos_ensayo> rolConcu = entityManager
					.createQuery(
							"select grupoS from GrupoSujetos_ensayo grupoS where grupoS.id =:id and grupoS.eliminado = true")
					.setParameter("id", idGruposujetosEliminar).getResultList();
			if (rolConcu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("form",
						Severity.ERROR, "eliminado");
				Transaction.instance().rollback();
			} else {
				return "good";
			}
		} catch (Exception e) {
			e.printStackTrace();
			Transaction.instance().rollback();
		}
		return "null";

	}

	/**
	 * Método para seleccionar el Grupo de Sujetos a habilitar o deshabilitar
	 */
	public void seleccionarHabilitar(long idGruposujetoHab)
			throws IllegalStateException, SecurityException, SystemException {
		this.idGruposujetoHab = idGruposujetoHab;
	}

	/**
	 * Elimina el Grupo de sujeto, no lo elimina físicamente de la Base de datos
	 */
	public void eliminar() throws IllegalStateException, SecurityException,
			SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", idGruposujetos).getSingleResult();
			/*
			 * List<Cronograma_ensayo> cronogramas= entityManager.createQuery(
			 * "select cronograma from Cronograma_ensayo cronograma join cronograma.gruposujetos gs where gs.id=:param1"
			 * ) .setParameter("param1", this.idGruposujetos) .getResultList();
			 */

			// Validando que el Grupo de Sujeto está asociado a un Cronograma
			if (gruposujetos.getCronogramas().size() != 0) {
				facesMessages.addToControlFromResourceBundle(
						"listOfGruposujetos", Severity.ERROR, "msjEliminar");
				return;
			}
			gruposujetos.setEliminado(true);
			gruposujetos.setCid(bitacora
					.registrarInicioDeAccion(SeamResourceBundle.getBundle()
							.getString("biteliminandoGS_enDis")
							+ " -"
							+ gruposujetos.getNombreGrupo().toString()));
			entityManager.persist(gruposujetos);
			entityManager.flush();

			error = 0;

			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
			this.refresh();

		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}

	/**
	 * Cambiar el estado del grupo de sujetos habilitado/deshabilitado
	 */
	public void cambiarEstado() throws IllegalStateException,
			SecurityException, SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", idGruposujetoHab).getSingleResult();

			if (gruposujetos.getHabilitado()) {
				gruposujetos.setHabilitado(false);
				gruposujetos.setCid(bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("bitDeshaGS_enDis")
								+ " -"
								+ gruposujetos.getNombreGrupo().toString()));
			} else {
				gruposujetos.setHabilitado(true);
				gruposujetos.setCid(bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("bitDeshaGS_enDis")));

			}
			entityManager.persist(gruposujetos);
			entityManager.flush();
			error = 0;
			this.refresh();
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
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

	public boolean isBusquedaAvanzada() {
		return busquedaAvanzada;
	}

	public void setBusquedaAvanzada(boolean busquedaAvanzada) {
		this.busquedaAvanzada = busquedaAvanzada;
	}

	public GrupoSujetos_ensayo getGruposujetos() {
		return gruposujetos;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public long getIdGruposujetos() {
		return idGruposujetos;
	}

	public void setIdGruposujetos(long idGruposujetos) {
		this.idGruposujetos = idGruposujetos;
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

	public Long getIdGruposujetosEliminar() {
		return idGruposujetosEliminar;
	}

	public void setIdGruposujetosEliminar(Long idGruposujetosEliminar) {
		this.idGruposujetosEliminar = idGruposujetosEliminar;
	}
}