package gehos.configuracion.management.gestionarEstablecimientosAreaInfluenciaHospital;

import gehos.configuracion.management.entity.EstablecimientoAreaInfluenciaHospital_configuracion;
import gehos.configuracion.management.entity.TipoEstablecimientoSalud_configuracion;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaCrearControlador;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaModificarControlador;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

@Name("establecimientoAreaInfluenciaHospitalBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class EstablecimientoAreaInfluenciaHospitalBuscarControlador {

	// listar y seleccion
	private ListControlerEstablecimiento establecimientosControler = new ListControlerEstablecimiento(
			new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>());
	private List<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientos = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();
	private List<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientosSelect = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();

	// crear, modificr, eliminar
	private EstablecimientoAreaInfluenciaHospital_configuracion establecimientoAreaInfluenciaHospital = new EstablecimientoAreaInfluenciaHospital_configuracion();
	private String nombre = "";
	private String tipoEstablecimiento = "";

	// otras funcionalidades
	private boolean gestionarTipoEstablecimiento;
	private String from = "";
	private String estadoFuncionalidad = "crear";// crear, modificar, ver

	private Long idEstablecimiento = -1l;
	private Long entidadId;

	private int posEstablecimiento;
	private int salida = 1;
	private String valor = "";
	private String segundoValor = "";

	FacesMessage facesMessage;

	@In(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaModificarControlador entidadSistemaModificarControlador;

	@In(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaCrearControlador entidadSistemaCrearControlador;

	@In
	EntityManager entityManager;

	@In
	FacesMessages facesMessages;

	// METODOS--------------------------------------------------------------
	// Cargar los datos de la entidad
	@SuppressWarnings("unchecked")
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		establecimientos = entityManager
				.createQuery(
						"select est from EstablecimientoAreaInfluenciaHospital_configuracion est "
								+ "where est.eliminado = false and est not in "
								+ "(select est from EstablecimientoAreaInfluenciaHospital_configuracion est "
								+ "join est.entidads ent where ent.id =:entidadId and est.eliminado = false) "
								+ "order by est.id desc").setParameter(
						"entidadId", this.entidadId).getResultList();

		if (from.equals("modificar")) {
			// elimina los departamentos que ya estan asignados a una entidad
			for (int i = 0; i < entidadSistemaModificarControlador
					.getEstablecimientosAreaInfluenciaHosp().size(); i++)
				for (int j = 0; j < establecimientos.size(); j++) {
					{
						if (entidadSistemaModificarControlador
								.getEstablecimientosAreaInfluenciaHosp().get(i)
								.getId().equals(establecimientos.get(j).getId())) {
							establecimientos.remove(j);
							break;
						}
					}
				}
		} else {
			// elimina los departamentos que ya estan asignados a una entidad
			for (int i = 0; i < entidadSistemaCrearControlador
					.getEstablecimientosAreaInfluenciaHosp().size(); i++)
				for (int j = 0; j < establecimientos.size(); j++) {
					{
						if (entidadSistemaCrearControlador
								.getEstablecimientosAreaInfluenciaHosp().get(i)
								.getId().equals(establecimientos.get(j).getId())) {
							establecimientos.remove(j);
							break;
						}
					}
				}
		}

		establecimientosControler.setElementos(establecimientos);

		if (salida == 1) {
			establecimientosSelect = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();
			establecimientosControler.setFirstResult(0);
			establecimientosControler.setSegundoValor("");
			establecimientosControler.setValor("");
			this.valor = "";
			this.segundoValor = "";
			establecimientosControler.setFirstResult(0);
		}
		salida = 0;
	}

	// realizar busqueda
	public void buscar() {
		establecimientosControler.setFirstResult(0);
		this.establecimientosControler.setValor(this.valor);
		establecimientosControler.setSegundoValor(this.segundoValor);
	}

	// cancela la busqueda
	public void cancelarBusqueda() {
		this.valor = "";
		this.segundoValor = "";
		establecimientosControler.setValor("");
		establecimientosControler.setSegundoValor("");
	}

	// CREAR MODIFICAR
	// ELIMINAR----------------------------------------------------------
	// devuelve la lista de tipos de establecimientos
	@SuppressWarnings("unchecked")
	public List<String> tiposEstablecimientosBuscar() {
		List li = new ArrayList<String>();
		li.add(SeamResourceBundle.getBundle().getString("seleccione"));
		List<String> l = entityManager
				.createQuery(
						"select t.valor from TipoEstablecimientoSalud_configuracion t order by t.valor")
				.getResultList();
		li.addAll(l);
		return li;
	}

	@SuppressWarnings("unchecked")
	public List<String> tiposEstablecimientos() {
		List li = new ArrayList<String>();
		List<String> l = entityManager
				.createQuery(
						"select t.valor from TipoEstablecimientoSalud_configuracion t order by t.valor")
				.getResultList();
		li.addAll(l);
		return li;
	}

	// seleccionar para modificar y eliminar
	public void seleccionar(Long establecimientoId) {
		establecimientoAreaInfluenciaHospital = (EstablecimientoAreaInfluenciaHospital_configuracion) entityManager
				.createQuery(
						"select e from EstablecimientoAreaInfluenciaHospital_configuracion e join e.tipoEstablecimientoSalud t where e.id =:establecimientoId")
				.setParameter("establecimientoId", establecimientoId)
				.getSingleResult();
		nombre = establecimientoAreaInfluenciaHospital.getNombre();
		tipoEstablecimiento = establecimientoAreaInfluenciaHospital
				.getTipoEstablecimientoSalud().getValor();
		estadoFuncionalidad = "modificar";
	}

	// limpia los campos despues de una operacion (crear, modificar, eliminar)
	public void limpiarCampos() {
		this.facesMessage = new FacesMessage();
		establecimientoAreaInfluenciaHospital = new EstablecimientoAreaInfluenciaHospital_configuracion();
		nombre = "";
		tipoEstablecimiento = "";
		estadoFuncionalidad = "crear";
	}

	public void limpiarCamposCrear() {
		this.facesMessage = new FacesMessage();
		establecimientoAreaInfluenciaHospital = new EstablecimientoAreaInfluenciaHospital_configuracion();
		nombre = "";
		tipoEstablecimiento = "";
		estadoFuncionalidad = "crear";
	}

	public String onAdicionarCatComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("nombreCrear").size() > 0
				|| facesMessages.getCurrentMessagesForControl(
						"tipoEstablecimientoCrear").size() > 0
				|| this.nombre.equals("")
				|| this.tipoEstablecimiento.equals(""))
			return "return false;";

		else {
			if (estadoFuncionalidad.equals("crear"))
				limpiarCampos();
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onShow() {
		if (estadoFuncionalidad.equals("crear"))
			return "document.getElementById('form:nombreCrear').value=''; "
					+ "document.getElementById('form:tipoEstablecimientoCrearcomboboxField').value='"
					+ SeamResourceBundle.getBundle().getString("seleccione")
					+ "'";
		else
			return "";
	}

	// crear establecimiento
	@SuppressWarnings("unchecked")
	public String crear() {
		try {
			List<EstablecimientoAreaInfluenciaHospital_configuracion> le = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();

			if (estadoFuncionalidad.equals("crear")) {
				le = entityManager
						.createQuery(
								"select e from EstablecimientoAreaInfluenciaHospital_configuracion e where e.nombre =:nombreEstablecimiento "
										+ "and e.tipoEstablecimientoSalud.valor=:tipoEstablecimiento")
						.setParameter("nombreEstablecimiento", this.nombre.trim())
						.setParameter("tipoEstablecimiento",
								this.tipoEstablecimiento).getResultList();
			} else {
				le = entityManager
						.createQuery(
								"select e from EstablecimientoAreaInfluenciaHospital_configuracion e where e.nombre =:nombreEstablecimiento and e.id<>:establecimientoId "
										+ "and e.tipoEstablecimientoSalud.valor=:tipoEstablecimiento and e.id <> :establecimientoId")
						.setParameter("nombreEstablecimiento", this.nombre.trim())
						.setParameter("establecimientoId",
								establecimientoAreaInfluenciaHospital.getId())
						.setParameter("tipoEstablecimiento",
								this.tipoEstablecimiento).setParameter(
								"establecimientoId",
								this.establecimientoAreaInfluenciaHospital
										.getId()).getResultList();
			}

			if (le.size() != 0) {
				facesMessages.addToControlFromResourceBundle("buttonAceptar",
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("establecimientoRepetido"));
				return "fail";
			}

			this.establecimientoAreaInfluenciaHospital.setEliminado(false);

			TipoEstablecimientoSalud_configuracion tipoEst = (TipoEstablecimientoSalud_configuracion) entityManager
					.createQuery(
							"select t from TipoEstablecimientoSalud_configuracion t where t.valor =:valor")
					.setParameter("valor", tipoEstablecimiento)
					.getSingleResult();

			this.establecimientoAreaInfluenciaHospital
					.setTipoEstablecimientoSalud(tipoEst);
			this.establecimientoAreaInfluenciaHospital.setNombre(nombre);

			entityManager.persist(establecimientoAreaInfluenciaHospital);
			entityManager.flush();
			estadoFuncionalidad = "ver";

			return "gotolist";

		} catch (RuntimeException e) {
			return "fail";
		}
	}

	// eliminar establecimiento
	@SuppressWarnings("unchecked")
	public void eliminar() {
		try {
			entityManager.remove(establecimientoAreaInfluenciaHospital);
			entityManager.flush();
			if (establecimientosControler.getElementos().contains(
					establecimientoAreaInfluenciaHospital))
				establecimientosControler.getElementos().remove(
						establecimientoAreaInfluenciaHospital);
			if (establecimientosSelect
					.contains(establecimientoAreaInfluenciaHospital))
				establecimientosSelect
						.remove(establecimientoAreaInfluenciaHospital);

			establecimientos = entityManager
					.createQuery(
							"select est from EstablecimientoAreaInfluenciaHospital_configuracion est "
									+ "where est.eliminado = false and est not in "
									+ "(select est from EstablecimientoAreaInfluenciaHospital_configuracion est "
									+ "join est.entidads ent where ent.id =:entidadId and est.eliminado = false) "
									+ "order by est.id desc").setParameter(
							"entidadId", this.entidadId).getResultList();

			if (from.equals("modificar")) {
				// elimina los departamentos que ya estan asignados a una
				// entidad
				for (int i = 0; i < entidadSistemaModificarControlador
						.getEstablecimientosAreaInfluenciaHosp().size(); i++)
					for (int j = 0; j < establecimientos.size(); j++) {
						{
							if (entidadSistemaModificarControlador
									.getEstablecimientosAreaInfluenciaHosp()
									.get(i).getId().equals(establecimientos.get(j)
									.getId())) {
								establecimientos.remove(j);
								establecimientosControler.remove(j);
								break;
							}
						}
					}
			} else {
				// elimina los departamentos que ya estan asignados a una
				// entidad
				for (int i = 0; i < entidadSistemaCrearControlador
						.getEstablecimientosAreaInfluenciaHosp().size(); i++)
					for (int j = 0; j < establecimientos.size(); j++) {
						{
							if (entidadSistemaCrearControlador
									.getEstablecimientosAreaInfluenciaHosp()
									.get(i).getId().equals(establecimientos.get(j)
									.getId())) {
								establecimientos.remove(j);
								establecimientosControler.remove(j);
								break;
							}
						}
					}
			}

			this.establecimientosControler.setValor(this.valor);

			if (establecimientosControler.getResultList().size() == 0
					&& establecimientosControler.getFirstResult() != 0)
				establecimientosControler
						.setFirstResult(establecimientosControler
								.getFirstResult()
								- establecimientosControler.getMaxResults());

		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"msjEliminar"));

		}
		limpiarCampos();
	}

	// asignar establecimientos a la entidad
	public void asignarEstablecimiento() {
		if (existeEstablecimiento(this.idEstablecimiento))
			establecimientosSelect.remove(posEstablecimiento);
		else {
			EstablecimientoAreaInfluenciaHospital_configuracion est = entityManager
					.find(
							EstablecimientoAreaInfluenciaHospital_configuracion.class,
							this.idEstablecimiento);
			establecimientosSelect.add(est);
		}

	}

	// verificar que la entidad tenga asignado un establecimiento
	public boolean existeEstablecimiento(Long idEstablecimiento) {
		for (int i = 0; i < establecimientosSelect.size(); i++) {
			if (idEstablecimiento.equals(establecimientosSelect.get(i).getId())) {
				posEstablecimiento = i;
				return true;
			}
		}
		return false;
	}

	// asignar departamentos a la entidad
	public void asignar() {
		if (from.equals("modificar")) {
			entidadSistemaModificarControlador
					.getEstablecimientosAreaInfluenciaHosp().addAll(
							establecimientosSelect);
			establecimientos.removeAll(establecimientosSelect);
			entidadSistemaModificarControlador
					.getEstablecimientosAreaInfluenciaHosp_controler()
					.setElementos(
							entidadSistemaModificarControlador
									.getEstablecimientosAreaInfluenciaHosp());
		} else {
			entidadSistemaCrearControlador
					.getEstablecimientosAreaInfluenciaHosp().addAll(
							establecimientosSelect);
			establecimientos.removeAll(establecimientosSelect);
			entidadSistemaCrearControlador
					.getEstablecimientosAreaInfluenciaHosp_controler()
					.setElementos(
							entidadSistemaCrearControlador
									.getEstablecimientosAreaInfluenciaHosp());
		}
		salida = 1;
		this.valor = "";
		this.segundoValor = "";
		establecimientosControler.setFirstResult(0);
	}

	// cancelar asignacion de departamentos a la entidad
	public void cancelar() {
		salida = 1;
		this.valor = "";
	}

	// PROPIEDADES-----------------------------------------------------------------
	public void setSalida(int salida) {
		this.salida = salida;
		this.valor = "";
		this.segundoValor = "";
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public EstablecimientoAreaInfluenciaHospital_configuracion getEstablecimientoAreaInfluenciaHospital() {
		return establecimientoAreaInfluenciaHospital;
	}

	public void setEstablecimientoAreaInfluenciaHospital(
			EstablecimientoAreaInfluenciaHospital_configuracion establecimientoAreaInfluenciaHospital) {
		this.establecimientoAreaInfluenciaHospital = establecimientoAreaInfluenciaHospital;
	}

	public Long getIdEstablecimiento() {
		return idEstablecimiento;
	}

	public void setIdEstablecimiento(Long idEstablecimiento) {
		this.idEstablecimiento = idEstablecimiento;
	}

	public Long getEntidadId() {
		return entidadId;
	}

	public void setEntidadId(Long entidadId) {

		this.entidadId = entidadId;
	}

	public ListControlerEstablecimiento getEstablecimientosControler() {
		return establecimientosControler;
	}

	public void setEstablecimientosControler(
			ListControlerEstablecimiento establecimientosControler) {
		this.establecimientosControler = establecimientosControler;
	}

	public List<EstablecimientoAreaInfluenciaHospital_configuracion> getEstablecimientos() {
		return establecimientos;
	}

	public void setEstablecimientos(
			List<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientos) {
		this.establecimientos = establecimientos;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		if (valor
				.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.valor = "";
		else
			this.valor = valor;
	}

	public List<EstablecimientoAreaInfluenciaHospital_configuracion> getEstablecimientosSelect() {
		return establecimientosSelect;
	}

	public void setEstablecimientosSelect(
			List<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientosSelect) {
		this.establecimientosSelect = establecimientosSelect;
	}

	public int getPosEstablecimiento() {
		return posEstablecimiento;
	}

	public void setPosEstablecimiento(int posEstablecimiento) {
		this.posEstablecimiento = posEstablecimiento;
	}

	public int getSalida() {
		return salida;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipoEstablecimiento() {
		return tipoEstablecimiento;
	}

	public void setTipoEstablecimiento(String tipoEstablecimiento) {
		this.tipoEstablecimiento = tipoEstablecimiento;
	}

	public boolean isGestionarTipoEstablecimiento() {
		return gestionarTipoEstablecimiento;
	}

	public void setGestionarTipoEstablecimiento(
			boolean gestionarTipoEstablecimiento) {
		this.gestionarTipoEstablecimiento = gestionarTipoEstablecimiento;
	}

	public EntidadSistemaModificarControlador getentidadSistemaModificarControlador() {
		return entidadSistemaModificarControlador;
	}

	public void setentidadSistemaModificarControlador(
			EntidadSistemaModificarControlador entidadSistemaModificarControlador) {
		this.entidadSistemaModificarControlador = entidadSistemaModificarControlador;
	}

	public EntidadSistemaCrearControlador getentidadSistemaCrearControlador() {
		return entidadSistemaCrearControlador;
	}

	public void setentidadSistemaCrearControlador(
			EntidadSistemaCrearControlador entidadSistemaCrearControlador) {
		this.entidadSistemaCrearControlador = entidadSistemaCrearControlador;
	}

	public String getSegundoValor() {
		return segundoValor;
	}

	public void setSegundoValor(String segundoValor) {
		if (segundoValor.equals(SeamResourceBundle.getBundle().getString(
				"seleccione")))
			this.segundoValor = "";
		else
			this.segundoValor = segundoValor;
	}

	public String getEstadoFuncionalidad() {
		return estadoFuncionalidad;
	}

	public void setEstadoFuncionalidad(String estadoFuncionalidad) {
		this.estadoFuncionalidad = estadoFuncionalidad;
	}
}
