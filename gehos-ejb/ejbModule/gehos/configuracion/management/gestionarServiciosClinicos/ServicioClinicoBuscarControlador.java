package gehos.configuracion.management.gestionarServiciosClinicos;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Name("servicioClinicoBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class ServicioClinicoBuscarControlador extends
		EntityQuery<Servicio_configuracion> { 

	private static final String EJBQL = "select servicio from Servicio_configuracion servicio " +
										"join servicio.departamento departamento " +
										"where departamento.esClinico = true " +
										"and (departamento.eliminado = false or departamento.eliminado = null )";

	private static final String[] RESTRICTIONS = {
			"lower(servicio.nombre) like concat(lower(#{servicioClinicoBuscarControlador.servicio.nombre.trim()}),'%')",
			"lower(servicio.codigo) like concat(lower(#{servicioClinicoBuscarControlador.servicio.codigo.trim()}),'%')",
			"lower(servicio.departamento.nombre) like concat(lower(#{servicioClinicoBuscarControlador.servicio.departamento.nombre}),'%')",
			"lower(servicio.servicio.nombre) like concat(lower(#{servicioClinicoBuscarControlador.servicio.servicio.nombre}),'%')",};

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search criteria
	private String codigo = "";
	private String nombre = "";
	private String departamentoSeleccionado = "";
	private String categoriaSeleccionada = "";
	private String subCateogoriaSeleccionada = "";
	private String categoria = "";
	private Servicio_configuracion servicio = new Servicio_configuracion();
	private List<SubCartegorias> subCategorias = new ArrayList<SubCartegorias>();

	// validations
	List<String> departamentosSource = new ArrayList<String>();
	List<String> categoriasSource = new ArrayList<String>();

	// other functions
	private Long servicioClinicoEliminarId = -1l;
	private Long cid = -1l;
	private boolean state = true;
	private String from = "buscar";
	private Parameters parameters = new Parameters();

	private int error = 0;

	public ServicioClinicoBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("servicio.id desc");
	}

	// Methods ---------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}
	
	public void changeStateSimpleTogglePanel() {
		state = !state;
	}
	
	public void buscar() {
		servicio.setCodigo(codigo);
		servicio.setNombre(nombre);
		
		Departamento_configuracion d = new Departamento_configuracion();
		d.setNombre(departamentoSeleccionado);		
		servicio.setDepartamento(d);
		
		Servicio_configuracion s = new Servicio_configuracion();
		s.setNombre(subCateogoriaSeleccionada);		
		servicio.setServicio(s);
		
		setFirstResult(0);
	}

	// return a list of clinic departament
	@SuppressWarnings("unchecked")
	public List<String> departamentos() {
		departamentosSource = new ArrayList<String>();
		
		try {
			List<String> d1 = entityManager.createQuery("select dep.nombre from Departamento_configuracion dep " +
														"where dep.esClinico = true " +
														"and (dep.eliminado = false or dep.eliminado = null) " +
														"order by dep.nombre")
										   .getResultList();

			departamentosSource.addAll(d1);
			departamentosSource.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			
			return departamentosSource;
			
		} catch (Exception e) {
			departamentosSource.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return departamentosSource;
		}
	}

	// initialized clinic departament search values
	public void filtrarDepartamento() {
		subCategorias = new ArrayList<SubCartegorias>();
		categoria = "";
		categoriaSeleccionada = "";
		subCateogoriaSeleccionada = "";
	}

	// search subcategory (reverse)
	public void buscarSubcategoriasInverso() {
		try {
			List<Servicio_configuracion> serviciosPadres = new ArrayList<Servicio_configuracion>();
			Servicio_configuracion servicioPadre = servicioPadre(servicio.getId());

			if (servicioPadre.getNombre() != null) {

				do {
					serviciosPadres.add(servicioPadre);
					servicioPadre = servicioPadre(servicioPadre.getId());
				} while (servicioPadre.getNombre() != null);

				// invest list
				for (int i = 0; i < serviciosPadres.size() / 2; i++) {
					Servicio_configuracion aux = serviciosPadres.get(serviciosPadres.size() - 1 - i);
					serviciosPadres.set(serviciosPadres.size() - 1 - i,	serviciosPadres.get(i));
					serviciosPadres.set(i, aux);
				}

				categoriaSeleccionada = serviciosPadres.get(0).getNombre();
				buscarSubCategorias("cat", 0);
				serviciosPadres.add(this.servicio);

				for (int i = 0; i < serviciosPadres.size() - 1; i++) {
					subCategorias.get(i).setPadre(serviciosPadres.get(i + 1).getNombre());
					buscarSubCategorias("sub", i);
				}
				categoriaSeleccionada = serviciosPadres.get(0).getNombre();

			}
		} catch (Exception e) {
			facesMessages.add("Error inesperado");
		}
	}

	// search subcategory
	@SuppressWarnings("unchecked")
	public void buscarSubCategorias(String categoria, int p) {
		try {			
			SubCartegorias subCategoria = new SubCartegorias();
			this.subCateogoriaSeleccionada = "";

			if (categoria.equals("cat")) {
				subCategorias = new ArrayList<SubCartegorias>();
				List<String> subCategorias = entityManager.createQuery("select serv.nombre from Servicio_configuracion serv " +
																	   "where serv.servicioFisico = false " +
																	   "and serv.servicio.nombre =:categoria " +
																	   "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) " +
																	   "and serv.departamento.esClinico = true " +
																	   "order by serv.nombre")
														  .setParameter("categoria", categoriaSeleccionada)
														  .getResultList();
				
				subCategorias.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
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
						if(categoria.equals(SeamResourceBundle.getBundle().getString("seleccione"))) {
							if(i == 0) {
								buscarSubCategorias("cat", 0);
								return;
							}
							else
							categoria = subCategorias.get(i - 1).getPadre();							
							borrarSubCategorias(i);	
						}
						borrarSubCategorias(i + 1);						
						List<String> subCategorias = entityManager.createQuery("select serv.nombre from Servicio_configuracion serv " +
																			   "where serv.servicioFisico = false " +
																			   "and serv.servicio.nombre =:categoria " +
																			   "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) " +
																			   "and serv.departamento.esClinico = true " +
																			   "order by serv.nombre")
																  .setParameter("categoria", categoria)
																  .getResultList();
						
						subCategorias.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
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
			facesMessages.add("Error inesperado");
		}
	}

	// return father service
	@SuppressWarnings("unchecked")
	public Servicio_configuracion servicioPadre(long servicioId) {
		try {
			Servicio_configuracion servicioPadre = new Servicio_configuracion();
			List<Servicio_configuracion> servicioPadreList = entityManager.createQuery("select s.servicio from Servicio_configuracion s " +
																					   "where s.id = :servicioId " +
																					   "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) " +
																					   "and serv.departamento.esClinico = true")
																		  .setParameter("servicioId", servicioId)
																		  .getResultList();
			if (servicioPadreList.size() != 0)
				servicioPadre = servicioPadreList.get(0);

			return servicioPadre;
		} catch (Exception e) {
			facesMessages.add("Error inesperado");
			return new Servicio_configuracion(); 
		}
	}

	// return services list of selected departament
	@SuppressWarnings("unchecked")
	public List<String> categorias() {
		try {
			categoriasSource = new ArrayList<String>();
			if (!departamentoSeleccionado.equals("")) {
				
				
				List<String> c = entityManager.createQuery("select serv.nombre from Servicio_configuracion serv " +
														   "where serv.departamento.nombre =:departamentoSeleccionado " +
														   "and serv.servicio = null " +
														   "and serv.servicioFisico = false " +
														   "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) " +
														   "and serv.departamento.esClinico = true " +
														   "order by serv.nombre")
											  .setParameter("departamentoSeleccionado",	departamentoSeleccionado)
											  .getResultList();
				
				categoriasSource.addAll(c);
				categoriasSource.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			}
			return categoriasSource;
		} catch (Exception e) {
			facesMessages.add("Error inesperado");
			return new ArrayList<String>();
		}		
	}

	// select service to removal
	public void seleccionarServicio() throws IllegalStateException, SecurityException, SystemException {
		try {
			error  = 0;
			@SuppressWarnings("unused")
			Servicio_configuracion serConcu = (Servicio_configuracion) entityManager.createQuery("select s from Servicio_configuracion s " +
				 						 "where s.id = :id " + 
				 						 "and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
										 "and s.departamento.esClinico = true")
			 			  .setParameter("id", this.servicioClinicoEliminarId)
			 			  .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
			error = 1;
			Transaction.instance().rollback();
		}		
	}
	
	// selecciona serv para modificar
	
	public void seleccionarModificar() {
		try {			
			error  = 0;
			@SuppressWarnings("unused")
			Servicio_configuracion serConcu = (Servicio_configuracion) entityManager.createQuery("select s from Servicio_configuracion s " +
				 						 "where s.id = :id " + 
				 						 "and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
										 "and s.departamento.esClinico = true")
			 			  .setParameter("id", this.servicioClinicoEliminarId)
			 			  .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
			error = 1;
		}
				
	}
	
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error = 0;
			@SuppressWarnings("unused")
			Servicio_configuracion serConcu = (Servicio_configuracion) entityManager.createQuery("select s from Servicio_configuracion s " +
				 						 "where s.id = :id " + 
				 						 "and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
										 "and s.departamento.esClinico = true")
		  .setParameter("id", this.servicioClinicoEliminarId)
		  .getSingleResult();
			
		} catch (NoResultException e) {	
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
			error = 1;				
		}				
	}

	// removal clinic service
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			Servicio_configuracion s = (Servicio_configuracion) 
									   entityManager.createQuery("select s from Servicio_configuracion s " +
									   							 "where s.id = :id " +
									   							 "and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
																 "and s.departamento.esClinico = true")
									   				.setParameter("id", servicioClinicoEliminarId)
									   				.getSingleResult();
			entityManager.remove(s);
			entityManager.flush();

			servicioClinicoEliminarId = -1l;
			
			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
		} 
		catch (NoResultException e) {
			servicioClinicoEliminarId = -1l;
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR,"eliminado");
		}
		catch (Exception e) {
			servicioClinicoEliminarId = -1l;
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR,"msjEliminar");
		}
	}

	// change visibility
	public void cambiarVisibilidad(Long servicioClinicoVisibilidadId) {
		try {			
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
			
			Servicio_configuracion s = (Servicio_configuracion) 
			   entityManager.createQuery("select s from Servicio_configuracion s " +
				 						 "where s.id = :id " + 
				 						 "and (s.departamento.eliminado = false or s.departamento.eliminado = null) " +
										 "and s.departamento.esClinico = true")
				 			.setParameter("id", servicioClinicoVisibilidadId)
				 			.getSingleResult();
				
			if (s.getEliminado() == null) {
				s.setEliminado(true);
			}
			else {
				s.setEliminado(!s.getEliminado());
			}			
			s.setCid(cid);

			entityManager.persist(s);
			entityManager.flush();	
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "errorInesperado");
		}
	}

	// Important properties -----------------------------------------------------
	public void setDepartamentoSeleccionado(String departamentoSeleccionado) {
		if (departamentoSeleccionado.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.departamentoSeleccionado = "";
		else
			this.departamentoSeleccionado = parameters.decodec(departamentoSeleccionado);
	}

	public void setCategoriaSeleccionada(String categoriaSeleccionada) {
		if (categoriaSeleccionada.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.categoriaSeleccionada = "";
		else if(!categoriaSeleccionada.equals(this.subCateogoriaSeleccionada))
			this.categoriaSeleccionada = parameters.decodec(categoriaSeleccionada);
	}

	public void setSubCateogoriaSeleccionada(String subCateogoriaSeleccionada) {
		this.subCateogoriaSeleccionada = parameters.decodec(subCateogoriaSeleccionada);
	}

	public void setFrom(String from) {
		this.from = from;
		try {
			if (!subCateogoriaSeleccionada.equals("") && !from.equals("buscar")) {
				servicio = (Servicio_configuracion) 
						   entityManager.createQuery("select s from Servicio_configuracion s " +
						   							 "where s.nombre =:servicioNombre " + 
						   							 "and (serv.departamento.eliminado = false or serv.departamento.eliminado = null) " +
													 "and serv.departamento.esClinico = true")
						   			    .setParameter("servicioNombre", subCateogoriaSeleccionada)
						   			    .getSingleResult();
				
				buscarSubcategoriasInverso();
				from = "buscar";
			}	
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.add("errorInesperado");
		}
	}
	
	// Properties --------------------------------------------------
	public void setNombre(String nombre) {
		this.nombre = parameters.decodec(nombre);
	}

	public void setCodigo(String codigo) {
		this.codigo = parameters.decodec(codigo);
	}

	public Servicio_configuracion getServicio() {
		return servicio;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCodigo() {
		return codigo;
	}
	
	public void setServicio(Servicio_configuracion servicio) {
		this.servicio = servicio;
	}

	public Long getServicioClinicoEliminarId() {
		return servicioClinicoEliminarId;
	}

	public void setServicioClinicoEliminarId(Long servicioClinicoEliminarId) {
		this.servicioClinicoEliminarId = servicioClinicoEliminarId;
	}

	public String getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}

	public String getCategoriaSeleccionada() {
		return categoriaSeleccionada;
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

	public String getSubCateogoriaSeleccionada() {
		return subCateogoriaSeleccionada;
	}	

	public String getFrom() {
		return from;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}