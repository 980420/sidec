package gehos.configuracion.management.gestionarServiciosNoClinicos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Scope(ScopeType.CONVERSATION)
@Name("servicioNoClinicoModificarControlador")
public class ServicioNoClinicoModificarControlador {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	// service
	private String nombre;
	private String codigo;
	private Servicio_configuracion servicio;
	private String categoriaSeleccionada;// servicio no fisico hijo de un

	// departament
	private String categoria;
	private String departamentoSeleccionado;
	private String subCateogoriaSeleccionada;
	private List<SubCartegorias> subCategorias;

	// other functions
	private int error;
	private boolean errorLoadData;
	private Long cid;
	private Long servicioId;

	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setServicioId(Long id) {
		this.servicioId = id;
		errorLoadData = false;
		try {

			categoria = "";
			departamentoSeleccionado = "";
			subCateogoriaSeleccionada = "";
			categoriaSeleccionada = "";
			subCategorias = new ArrayList<SubCartegorias>();

			this.servicio = (Servicio_configuracion) entityManager
					.createQuery(
							"select s from Servicio_configuracion s "
									+ "join s.departamento "
									+ "where s.id = :servicioId "
									+ "and (s.departamento.eliminado = false or s.departamento.eliminado = null)")
					.setParameter("servicioId", this.servicioId)
					.getSingleResult();

			buscarSubcategoriasInverso();

			if (servicio.getDepartamento() != null)
				departamentoSeleccionado = servicio.getDepartamento()
						.getNombre();
			if (servicio.getServicio() != null)
				categoriaSeleccionada = servicio.getServicio().getNombre();

			nombre = servicio.getNombre();
			codigo = servicio.getCodigo();

			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));
					

		} catch (Exception e) {
			errorLoadData = true;
		}
	}

	@SuppressWarnings("unchecked")
	public Servicio_configuracion servicioPadre(long servicioId) {
		// search father service
		Servicio_configuracion servicioPadre = new Servicio_configuracion();
		List<Servicio_configuracion> servicioPadreList = entityManager
				.createQuery(
						"select s.servicio from Servicio_configuracion s "
								+ "where s.id = :servicioId "
								+ "and (s.departamento.eliminado = false or s.departamento. eliminado = null) "
								+ "and s.departamento.esClinico = false")
				.setParameter("servicioId", servicioId).getResultList();
		if (servicioPadreList.size() != 0)
			servicioPadre = servicioPadreList.get(0);

		return servicioPadre;
	}

	public void buscarSubcategoriasInverso() {
		List<Servicio_configuracion> serviciosPadres = new ArrayList<Servicio_configuracion>();
		Servicio_configuracion servicioPadre = servicioPadre(servicio.getId());

		if (servicioPadre.getNombre() != null) {
			do {
				serviciosPadres.add(servicioPadre);
				servicioPadre = servicioPadre(servicioPadre.getId());
			} while (servicioPadre.getNombre() != null);

			// invet list
			for (int i = 0; i < serviciosPadres.size() / 2; i++) {
				Servicio_configuracion aux = serviciosPadres
						.get(serviciosPadres.size() - 1 - i);
				serviciosPadres.set(serviciosPadres.size() - 1 - i,
						serviciosPadres.get(i));
				serviciosPadres.set(i, aux);
			}

			categoriaSeleccionada = serviciosPadres.get(0).getNombre();
			buscarSubCategorias("cat", 0);

			for (int i = 0; i < serviciosPadres.size() - 1; i++) {
				subCategorias.get(i).setPadre(
						serviciosPadres.get(i + 1).getNombre());
				buscarSubCategorias("sub", i);
			}
			categoriaSeleccionada = serviciosPadres.get(0).getNombre();
		}
	}

	@SuppressWarnings("unchecked")
	public void buscarSubCategorias(String categoria, int p) {
		try {
			SubCartegorias subCategoria = new SubCartegorias();
			this.subCateogoriaSeleccionada = "";

			if (categoria.equals("cat")) {
				subCategorias = new ArrayList<SubCartegorias>();
				List<String> subCategorias = entityManager
						.createQuery(
								"select serv.nombre from Servicio_configuracion serv "
										+ "where serv.servicioFisico = false "
										+ "and serv.servicio.nombre =:categoria "
										+ "and serv.id <> :id "
										+ "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) "
										+ "and serv.departamento.esClinico = false "
										+ "order by serv.nombre")
						.setParameter("id", servicioId)
						.setParameter("categoria", categoriaSeleccionada)
						.getResultList();

				subCategorias.add(0,
						SeamResourceBundle.getBundle().getString("seleccione"));
				subCategoria.setCategorias(subCategorias);
				subCategoria.setPos(0);

				this.categoria = categoriaSeleccionada;
				this.subCateogoriaSeleccionada = this.categoria;

				if (subCategorias.size() != 0)
					this.subCategorias.add(subCategoria);
				return;

			} else if (categoria.equals("sub")) {
				categoria = subCategorias.get(p).getPadre();
				this.subCateogoriaSeleccionada = categoria;
			}
			for (int i = 0; i < subCategorias.size(); i++) {

				if (subCategorias.get(i).getPadre().equals(categoria)) {
					if (categoria.equals(SeamResourceBundle.getBundle()
							.getString("seleccione"))) {
						if (i == 0) {
							buscarSubCategorias("cat", 0);
							return;
						} else
							categoria = subCategorias.get(i - 1).getPadre();
						borrarSubCategorias(i);
					}
					borrarSubCategorias(i + 1);
					List<String> subCategorias = entityManager
							.createQuery(
									"select serv.nombre from Servicio_configuracion serv "
											+ "where serv.servicioFisico = false "
											+ "and serv.servicio.nombre =:categoria "
											+ "and serv.id <> :id "
											+ "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) "
											+ "and serv.departamento.esClinico = false "
											+ "order by serv.nombre")
							.setParameter("id", servicioId)
							.setParameter("categoria", categoria)
							.getResultList();

					subCategorias.add(0, SeamResourceBundle.getBundle()
							.getString("seleccione"));
					subCategoria.setCategorias(subCategorias);
					subCategoria.setPos(this.subCategorias.size());

					if (subCategorias.size() != 0)
						this.subCategorias.add(subCategoria);
					return;
				}
			}
		} catch (Exception e) {
			facesMessages.add("Error inesperado");
		}
	}

	public void borrarSubCategorias(int pos) {
		try {
			int longitud = subCategorias.size() - 1;
			for (int i = longitud; i >= pos; i--) {
				subCategorias.remove(i);
			}
		} catch (Exception e) {
			facesMessages.add("errorInesperado");
		}
	}

	// return a depatament list
	@SuppressWarnings("unchecked")
	public List<String> departamentos() {
		try {
			return entityManager
					.createQuery(
							"select dep.nombre from Departamento_configuracion dep "
									+ "where dep.esClinico = false "
									+ "and (dep.eliminado = false or dep.eliminado = null) "
									+ "order by dep.nombre").getResultList();
		} catch (Exception e) {
			facesMessages.add("errorInesperado");
			return new ArrayList<String>();
		}
	}

	// return a services list of selected departament
	@SuppressWarnings("unchecked")
	public List<String> categorias() {
		try {
			if (!departamentoSeleccionado.equals("")) {
				List<String> topserv = entityManager
						.createQuery(
								"select serv.nombre from Servicio_configuracion serv "
										+ "where serv.departamento.nombre =:departamentoSeleccionado "
										+ "and serv.servicio = null "
										+
										// "and serv.servicioFisico = false " +
										"and serv.id <> :servicioId "
										+ "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) "
										+ "and serv.departamento.esClinico = false "
										+ "order by serv.nombre")
						.setParameter("departamentoSeleccionado",
								departamentoSeleccionado)
						.setParameter("servicioId", this.servicioId)
						.getResultList();
				List<String> tempresult = new ArrayList<String>();
				for (String s : topserv) {
					obtenerServiciosRecursivamente(s, tempresult);
				}
				topserv.addAll(tempresult);
				Collections.sort(topserv, new Comparator<String>() {
					public int compare(String arg0, String arg1) {
						return arg0.compareTo(arg1);
					}
				});
				return topserv;
			}
			return new ArrayList<String>();
		} catch (Exception e) {
			facesMessages.add("errorInesperado");
			return new ArrayList<String>();
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerServiciosRecursivamente(String servPadre,
			List<String> result) {
		List<String> servHijos = entityManager
				.createQuery(
						"select serv.nombre from Servicio_configuracion serv "
								+ "where serv.servicio.nombre = :servpadre order by serv.nombre")
				.setParameter("servpadre", servPadre).getResultList();
		if (servHijos.size() > 0) {
			result.addAll(servHijos);
			for (String h : servHijos) {
				obtenerServiciosRecursivamente(h, result);
			}
		}
	}

	// inicialize search departament values
	public void filtrarDepartamento() {
		subCategorias = new ArrayList<SubCartegorias>();
		categoria = "";
		categoriaSeleccionada = "";
		subCateogoriaSeleccionada = "";
	}

	@End
	public void end() {
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void modificar() {
		error = 0;
		try {
			this.servicio.setNombre(this.nombre.trim());
			this.servicio.setCodigo(this.codigo.trim());
			this.servicio.setEliminado(false);

			List<Departamento_configuracion> dep = entityManager
					.createQuery(
							"select dep from Departamento_configuracion dep "
									+ "where dep.esClinico = false "
									+ "and (dep.eliminado = false or dep.eliminado = null) "
									+ "and dep.nombre =:departamentoSeleccionado")
					.setParameter("departamentoSeleccionado",
							this.departamentoSeleccionado).getResultList();
			this.servicio.setDepartamento(dep.get(0));

			// if (!subCateogoriaSeleccionada.equals("")) {
			// Servicio_configuracion categoria = (Servicio_configuracion)
			// entityManager
			// .createQuery(
			// "select serv from Servicio_configuracion serv "
			// + "where serv.nombre = :categoria "
			// +
			// "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) "
			// + "and serv.departamento.esClinico = false")
			// .setParameter("categoria",
			// this.subCateogoriaSeleccionada)
			// .getSingleResult();
			// this.servicio.setServicio(categoria);
			// } else
			if (!categoriaSeleccionada.equals("")) {
				Servicio_configuracion categoria = (Servicio_configuracion) entityManager
						.createQuery(
								"select serv from Servicio_configuracion serv "
										+ "where serv.nombre = :categoria "
										+ "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) "
										+ "and serv.departamento.esClinico = false")
						.setParameter("categoria", this.categoriaSeleccionada)
						.getSingleResult();
				this.servicio.setServicio(categoria);
			}
			// if (subCateogoriaSeleccionada.equals("")
			// && categoriaSeleccionada.equals(""))
			// servicio.setServicio(null);

			this.servicio.setCid(cid);
			entityManager.persist(this.servicio);
			entityManager.flush();
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "errorInesperado");
		}
	}

	// Properties --------------------------------------------------
	public String getNombre() {
		return nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}

	public Servicio_configuracion getServicio() {
		return servicio;
	}

	public void setServicio(Servicio_configuracion servicio) {
		this.servicio = servicio;
	}

	public Long getServicioId() {
		return servicioId;
	}

	public String getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(String categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public List<SubCartegorias> getSubCategorias() {
		return subCategorias;
	}

	public void setSubCategorias(List<SubCartegorias> subCategorias) {
		this.subCategorias = subCategorias;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public void setDepartamentoSeleccionado(String departamentoSeleccionado) {
		this.departamentoSeleccionado = departamentoSeleccionado;
	}

	public String getSubCateogoriaSeleccionada() {
		return subCateogoriaSeleccionada;
	}

	public void setSubCateogoriaSeleccionada(String subCateogoriaSeleccionada) {
		this.subCateogoriaSeleccionada = subCateogoriaSeleccionada;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}
}