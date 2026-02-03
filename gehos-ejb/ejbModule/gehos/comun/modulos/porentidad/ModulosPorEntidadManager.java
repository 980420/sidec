package gehos.comun.modulos.porentidad;

import gehos.bitacora.session.traces.Bitacora;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.modulos.porentidad.treebuilders.ModulosPorEntidadTreeBuilder;
import gehos.comun.modulos.porentidad.treebuilders.model.EntidadWrapper;
import gehos.comun.modulos.porentidad.treebuilders.model.ITreeData;
import gehos.comun.modulos.porentidad.treebuilders.model.ModuloWrapper;
import gehos.comun.shell.ModSelectorController;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.ServicioSubmodulo_configuracion;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.ioc.spring.SeamComponentPostProcessor;
import org.jboss.seam.jsf.SeamApplication;
import org.jboss.seam.theme.Theme;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.DropEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeRowKey;

@Name("modulosPorEntidadManager")
public class ModulosPorEntidadManager {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	
	@In(required = false, create = true, value = "modulosPorEntidadTreeBuilder")
	ModulosPorEntidadTreeBuilder treeBuilder;

	private ITreeData selectedItem;
	@SuppressWarnings({ "unchecked" })
	private TreeNode selectedTreenode;

	// ServletContext context;

	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;

	public String entidadIcon(Entidad_configuracion node) {
		EntidadWrapper wrapper = new EntidadWrapper(node, false, node.getId());
		return entidadIcon(wrapper);
	}

	public String entidadIcon(EntidadWrapper node) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modCommon/entidades_logos/"
				+ theme.getTheme().get("name") + "/"
				+ theme.getTheme().get("color") + "/"
				+ node.getValue().getLogo();

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/generic.png";
	}
	
	private String servicioSelected = "";
	private Boolean moduleService = false;
	
	public ServicioSubmodulo_configuracion findServicioSubmdulo(Funcionalidad mod){
		
		ServicioSubmodulo_configuracion s;
		try{
			 s = (ServicioSubmodulo_configuracion)
												entityManager
											    .createQuery("SELECT s FROM ServicioSubmodulo_configuracion s "
												+ "WHERE s.funcionalidad.id = :idModulo "
												+ "AND (s.servicioInEntidad.eliminado = false OR s.servicioInEntidad.eliminado IS NULL) "
												+ "AND (s.servicioInEntidad.servicio.eliminado = false OR s.servicioInEntidad.servicio.eliminado IS NULL) "
												+ "AND (s.eliminado = false OR s.eliminado IS NULL) ")
												.setParameter("idModulo", mod.getId())
												.getSingleResult();

		}catch(NoResultException e){
			return null;
		}
		return s;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getServiciosInEntidad(){
		
		List<String> servicios = entityManager.createQuery("SELECT s.nombre FROM Servicio_configuracion s INNER JOIN s.servicioInEntidads si "
														   + "WHERE s.departamento.esClinico = true "
														   + "AND si.entidad.id = :idEntidad "
														   + "AND (si.eliminado = false OR si.eliminado is null) "
														   + "AND (s.eliminado = false OR s.eliminado is null)")
														   .setParameter("idEntidad", this.selectedItem.getEntidadID())
														   .getResultList();
		
		return servicios;
	}

	public void salvarModulo() {
		
		Funcionalidad mod = ((ModuloWrapper) this.selectedItem).getValue();
		mod = entityManager.merge(mod);
		entityManager.persist(mod);
		
		if( !this.servicioSelected.equals( "" ) && findServicioSubmdulo(mod) == null ){
			
			ServicioInEntidad_configuracion servicio = (ServicioInEntidad_configuracion)
														   entityManager.createQuery("SELECT si FROM Servicio_configuracion s "
														   + "INNER JOIN s.servicioInEntidads si "
														   + "WHERE s.departamento.esClinico = true "
														   + "AND si.entidad.id = :idEntidad "
														   + "AND s.nombre = :nombreServicio "
														   + "AND (si.eliminado = false OR si.eliminado is null) "
														   + "AND (s.eliminado = false OR s.eliminado is null)")
														   .setParameter("idEntidad", this.selectedItem.getEntidadID())
														   .setParameter("nombreServicio", this.servicioSelected)
														   .getSingleResult();
			
			ServicioSubmodulo_configuracion servSubModulo = new ServicioSubmodulo_configuracion();
			servSubModulo.setFuncionalidad(mod);
			servSubModulo.setServicioInEntidad(servicio);
			servSubModulo.setEliminado(false);
			
			entityManager.persist(servSubModulo);
			
		}
		
		entityManager.flush();
		((ModuloWrapper) this.selectedItem).setValue(mod);
	}

	@SuppressWarnings("unchecked")
	public void crearModulosFisicamente() {
		int creados = 0;
		int sincrear = 0;
		List<Funcionalidad> modulosFisicos = entityManager
				.createQuery(
						"from Funcionalidad f where f.moduloFisico = true and f.esModulo = true")
				.getResultList();
		for (Funcionalidad modulo : modulosFisicos) {
			try {
				if (!this.moduloTieneDirectorio(modulo)) {
					this.crearDirectorioDeModulo(modulo.getId());
					creados++;
				}
			} catch (Exception e) {
				sincrear++;
				continue;
			}
		}

		System.out.println("se crearon " + creados + " módulos.");
		System.out.println("no se pudieron crear " + sincrear + " módulos.");

	}

	// @Create
	// public void constructor() {
	// FacesContext aFacesContext = FacesContext.getCurrentInstance();
	// context = (ServletContext) aFacesContext.getExternalContext()
	// .getContext();
	// }

	public Boolean moduloTieneDirectorio(Funcionalidad modulo) {
		Funcionalidad tipo = modulo.getFuncionalidadPadre();
		String path = tipo.getCodebase().substring(0,
				tipo.getCodebase().indexOf("codebase"));
		path = System.getProperty("jboss.server.home.url").replace("file:", "")
				+ "/deploy/gehos-ear.ear/gehos.war/" + path;

		path = path + "/modules/" + modulo.getNombre();
		// path = context.getRealPath(path);
		return new File(path).exists();
	}

	public void crearSubModuloFisicamente(Long modId) {
		try {
			this.crearDirectorioDeModulo(modId);
		} catch (IOException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "error_create_module");
		}
	}

	public void crearDirectorioDeModulo(Long modId) throws IOException {
		Funcionalidad mod = entityManager.find(Funcionalidad.class, modId);
		Funcionalidad tipo = mod.getFuncionalidadPadre();
		String path = tipo.getCodebase().substring(0,
				tipo.getCodebase().indexOf("codebase"));
		path = System.getProperty("jboss.server.home.url").replace("file:", "")
				+ "/deploy/gehos-ear.ear/gehos.war/" + path;
		path = path + "/modules/" + mod.getNombre();

		String base = tipo.getCodebase().substring(0,
				tipo.getCodebase().indexOf("codebase"));
		base = System.getProperty("jboss.server.home.url").replace("file:", "")
				+ "/deploy/gehos-ear.ear/gehos.war/" + base;
		File template = new File(base + "/template/");
		File target = new File(path);

		FileUtils.copyDirectory(template, target);
	}

	public String moduloIdToRemove;

	public void eliminarModulo() {
		Funcionalidad modulo = entityManager.find(Funcionalidad.class,
				Long.parseLong(this.moduloIdToRemove));
		try {
			
			entityManager.remove(modulo);
			entityManager.flush();
			Funcionalidad tipo = modulo.getFuncionalidadPadre();
			String path = tipo.getCodebase().substring(0,
					tipo.getCodebase().indexOf("codebase"));
			path = System.getProperty("jboss.server.home.url").replace("file:",
					"")
					+ "/deploy/gehos-ear.ear/gehos.war/" + path;
			path = path + "/modules/" + modulo.getNombre();
			File directoryToRemove = new File(path);
			if (directoryToRemove.exists())
				FileUtils.deleteDirectory(directoryToRemove);
			this.lastEntityId = new Long(-200);
			this.lastSubModEntityId = new Long(-200);
			this.lastSubModFatherId = new Long(-200);
			this.treeBuilder.updateNode(selectedTreenode);
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_modulo_fisico",
					modulo.getLabel());
		}
	}

	public void cambiarVisibilidadDeEntidad(Long entId) {
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, entId);
		if (entidad.getEliminado() == null || entidad.getEliminado() == false)
			entidad.setEliminado(true);
		else
			entidad.setEliminado(false);
		entityManager.persist(entidad);
		entityManager.flush();
		ModSelectorController.reloadsEntities();
	}

	public void cambiarVisibilidadDeModulo(Long modId) {
		Bitacora bitacora=new Bitacora();
		Funcionalidad modulo = entityManager.find(Funcionalidad.class, modId);
		if (modulo.getEliminado() == null || modulo.getEliminado() == false)
			modulo.setEliminado(true);
		else
			modulo.setEliminado(false);
		
		entityManager.persist(modulo);
		entityManager.flush();
		this.lastEntityId = new Long(-200);
		this.lastSubModEntityId = new Long(-200);
		this.lastSubModFatherId = new Long(-200);
	}

	private Integer modulosCount = 1;

	public void crearModulos() {
		Funcionalidad tipoSubModulo = (Funcionalidad) entityManager
				.createQuery(
						"from Funcionalidad mod where mod.funcionalidadPadre.funcionalidadPadre.id=0 "
								+ "and mod.esModulo=true and mod.funcionalidadPadre.label=:padreLabel "
								+ "and mod.label=:modLabel")
				.setParameter("padreLabel", this.selectedModule)
				.setParameter("modLabel", this.selectedSubModule)
				.getSingleResult();
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, this.selectedItem.getEntidadID());
		if (!tipoSubModulo.getCodebase().equals("external")) {
			String path = tipoSubModulo.getCodebase().substring(0,
					tipoSubModulo.getCodebase().indexOf("codebase"));
			path = path + "/modules/";
			path = System.getProperty("jboss.server.home.url").replace("file:",
					"")
					+ "/deploy/gehos-ear.ear/gehos.war/" + path;
			File modulesDirectory = new File(path);
			List<String> existentModules = null;
			try {
				existentModules = Arrays.asList(modulesDirectory.list());
			} catch (Exception e1) {
				facesMessages.addToControlFromResourceBundle("error",
						Severity.ERROR, "error_create_module");
				return;
			}
			Integer initialNamesIndex = existentModules.size() + 1;
			for (int i = 1; i <= modulosCount; i++) {
				String grandpaName = tipoSubModulo.getFuncionalidadPadre()
						.getNombre();
				String dadName = tipoSubModulo.getNombre();
				String tentativeName = "";
				boolean alreadyExist = true;
				while (alreadyExist) {
					tentativeName = grandpaName + "_" + dadName + "_"
							+ initialNamesIndex;
					boolean fisicallyAlreadyExist = new File(path
							+ tentativeName).exists();
					boolean logicallyAlreadyExist = entityManager
							.createQuery(
									"from Funcionalidad f where f.nombre = :nombre")
							.setParameter("nombre", tentativeName)
							.getResultList().size() > 0;
					alreadyExist = logicallyAlreadyExist
							|| fisicallyAlreadyExist;
					initialNamesIndex++;
				}

				// File newModuleDirectory = new File(path + "/" +
				// tentativeName);
				// newModuleDirectory.mkdir();
				String base = tipoSubModulo.getCodebase().substring(0,
						tipoSubModulo.getCodebase().indexOf("codebase"));
				base = System.getProperty("jboss.server.home.url").replace(
						"file:", "")
						+ "/deploy/gehos-ear.ear/gehos.war/" + base;
				File template = new File(base + "/template/");
				File target = new File(path + "/" + tentativeName);

				try {
					FileUtils.copyDirectory(template, target);
				} catch (IOException e) {
					e.printStackTrace();
				}

				Funcionalidad moduloFisico = new Funcionalidad();
				moduloFisico.setLabel(tipoSubModulo.getLabel() + " "
						+ (--initialNamesIndex).toString());
				String url = tipoSubModulo.getCodebase().substring(0,
						tipoSubModulo.getCodebase().indexOf("codebase"));
				url = url + "modules/" + tentativeName + "/home.gehos";
				moduloFisico.setUrl(url);
				//moduloFisico.setVersion(1);
				moduloFisico.setEliminado(false);
				moduloFisico.setCid(new Long(-1));
				moduloFisico.setOrden(0);
				moduloFisico.setImagen(tipoSubModulo.getImagen());
				moduloFisico.setEsModulo(true);
				moduloFisico.setFuncionalidadPadre(tipoSubModulo);
				moduloFisico.setLabelSize(10);
				moduloFisico.setNombre(tentativeName);
				moduloFisico.setModuloFisico(true);
				moduloFisico.setContenedorIconos(false);
				moduloFisico.setDescripcion(tipoSubModulo.getDescripcion());
				moduloFisico.setGrupo(tipoSubModulo.getFuncionalidadPadre()
						.getNombre());
				moduloFisico.setEntidad(entidad);
				moduloFisico.setNecesitaActivacion(false);
				moduloFisico.setActivo(!tipoSubModulo.getNecesitaActivacion());
				entityManager.persist(moduloFisico);
				entityManager.flush();

				initialNamesIndex++;
			}
		} else {
			Funcionalidad moduloFisico = new Funcionalidad();
			moduloFisico.setLabel(tipoSubModulo.getLabel());
			moduloFisico.setUrl(tipoSubModulo.getUrl());
			//moduloFisico.setVersion(1);
			moduloFisico.setEliminado(false);
			moduloFisico.setCid(new Long(-1));
			moduloFisico.setOrden(0);
			moduloFisico.setImagen(tipoSubModulo.getImagen());
			moduloFisico.setEsModulo(true);
			moduloFisico.setFuncionalidadPadre(tipoSubModulo);
			moduloFisico.setLabelSize(10);
			moduloFisico.setNombre(tipoSubModulo.getNombre() + "1");
			moduloFisico.setModuloFisico(true);
			moduloFisico.setContenedorIconos(false);
			moduloFisico.setDescripcion(tipoSubModulo.getDescripcion());
			moduloFisico.setGrupo(tipoSubModulo.getFuncionalidadPadre()
					.getNombre());
			moduloFisico.setEntidad(entidad);
			moduloFisico.setNecesitaActivacion(false);
			moduloFisico.setActivo(!tipoSubModulo.getNecesitaActivacion());
			entityManager.persist(moduloFisico);
			entityManager.flush();
		}
		this.lastEntityId = new Long(-200);
		this.lastSubModEntityId = new Long(-200);
		this.lastSubModFatherId = new Long(-200);
		if (this.selectedTreenode != null)
			this.treeBuilder.updateNode(selectedTreenode);
	}

	public String onModuleEditComplete(String modalName, String label, String descripcion) {
		if (facesMessages.getCurrentMessagesForControl(label).size() > 0
				|| facesMessages.getCurrentMessagesForControl(descripcion)
						.size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onComplete(String modalName) {
		if (this.selectedModule.isEmpty() || this.selectedSubModule.isEmpty())
			return "return false;";
		else {
			this.selectedModule = "";
			this.selectedSubModule = "";
			this.modulosCount = 1;
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	private String selectedModule = "";

	@SuppressWarnings("unchecked")
	public List<String> modulosNames() {
		List<String> result = entityManager
				.createQuery(
						"select mod.label from Funcionalidad mod "
								+ "where mod.funcionalidadPadre.id=0 order by mod.label")
				.getResultList();

		result.remove("Configuración");

		return result;
	}

	private String selectedSubModule = "";

	@SuppressWarnings("unchecked")
	public List<String> submodulosNames() {
		if (this.selectedItem instanceof ModuloWrapper)
			this.selectedModule = ((ModuloWrapper) (this.selectedItem))
					.getValue().getLabel();
		return entityManager
				.createQuery(
						"select mod.label from Funcionalidad mod "
								+ "where mod.funcionalidadPadre.funcionalidadPadre.id=0 "
								+ "and mod.esModulo=true "
								+ "and mod.funcionalidadPadre.label=:padreLabel order by mod.label")
				.setParameter("padreLabel", this.selectedModule)
				.getResultList();
	}

	public void subir() {
		if (this.selectedItem instanceof EntidadWrapper) {
			this.selectedItem = null;
		} else if (this.selectedItem instanceof ModuloWrapper
				&& !((Funcionalidad) (this.selectedItem.getValue()))
						.getModuloFisico()) {
			Entidad_configuracion entidad = entityManager.find(
					Entidad_configuracion.class,
					((ModuloWrapper) (this.selectedItem)).getEntidadID());
			this.selectedItem = new EntidadWrapper(entidad, false,
					entidad.getId());
		} else if (this.selectedItem instanceof ModuloWrapper
				&& ((Funcionalidad) (this.selectedItem.getValue()))
						.getModuloFisico()
				&& !((ModuloWrapper) (this.selectedItem)).isModuloUnico()) {
			Funcionalidad moduloSeleccionado = ((Funcionalidad) (this.selectedItem
					.getValue()));
			Funcionalidad moduloaSeleccionar = moduloSeleccionado
					.getFuncionalidadPadre().getFuncionalidadPadre();
			this.selectedItem = new ModuloWrapper(moduloaSeleccionar, false,
					this.selectedItem.getEntidadID());
			((ModuloWrapper) this.selectedItem).setType("modulo-padre");

		} else if (this.selectedItem instanceof ModuloWrapper
				&& ((Funcionalidad) (this.selectedItem.getValue()))
						.getModuloFisico()
				&& ((ModuloWrapper) (this.selectedItem)).isModuloUnico()) {
			Entidad_configuracion entidad = entityManager.find(
					Entidad_configuracion.class,
					((Funcionalidad) (this.selectedItem.getValue()))
							.getEntidad().getId());
			this.selectedItem = new EntidadWrapper(entidad, false,
					entidad.getId());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Entidad_configuracion> entidades() {
		
		/**
		 * @author yurien 27/03/2014
		 * Se agrega la restriccion a la consulta para que muestre las entidades que pertenecen al anillo
		 * **/
		return entityManager.createQuery(
//				"from Entidad_configuracion ent where ent.perteneceARhio = true "
				"from Entidad_configuracion ent where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
						+ "order by ent.nombre").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Entidad_configuracion> entidades2() {
		
		/**
		 * @author yurien 27/03/2014
		 * Se agrega la restriccion a la consulta para que muestre las entidades que pertenecen al anillo
		 * **/
		return entityManager.createQuery(
				"from Entidad_configuracion ent where ent.eliminado = false and ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//				"from Entidad_configuracion ent where ent.eliminado = false and ent.perteneceARhio = true "
						+ "order by ent.nombre").getResultList();
	}

	private List<Funcionalidad> submodulos = new ArrayList<Funcionalidad>();
	private Long lastSubModEntityId = new Long(-200);
	private Long lastSubModFatherId = new Long(-200);

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> modulosDelTipoSeleccionado() {
		if (this.selectedItem.getEntidadID() != lastSubModEntityId
				|| this.selectedItem.getId() != lastSubModFatherId) {
			lastSubModEntityId = this.selectedItem.getEntidadID();
			lastSubModFatherId = this.selectedItem.getId();
			submodulos = entityManager
					.createQuery(
							"from Funcionalidad func where func.esModulo=true "
									+ "and func.moduloFisico=true and func.entidad.id=:entId "
									+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modPadreId "
									+ "order by func.label")
					.setParameter("entId", this.selectedItem.getEntidadID())
					.setParameter("modPadreId", this.selectedItem.getId())
					.getResultList();
		}
		return submodulos;
	}

	private List<Funcionalidad> modulos = new ArrayList<Funcionalidad>();
	private Long lastEntityId = new Long(-200);

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> modulos() {
		if (this.selectedItem.getId() != lastEntityId) {
			lastEntityId = this.selectedItem.getId();
			List<Funcionalidad> tiposModulosEntidad = entityManager
					.createQuery(
							"select distinct func.funcionalidadPadre.funcionalidadPadre "
									+ "from Funcionalidad func where func.esModulo=true "
									+ "and func.moduloFisico=true and func.entidad.id=:entId "
									+ "and func.funcionalidadPadre.funcionalidadPadre.id != -1 "
									+ "order by func.funcionalidadPadre.funcionalidadPadre.label")
					.setParameter("entId", this.selectedItem.getId())
					.getResultList();
			modulos = new ArrayList<Funcionalidad>();
			for (Funcionalidad tipoModulo : tiposModulosEntidad) {
				List<Funcionalidad> modulosFisicos = entityManager
						.createQuery(
								"from Funcionalidad func where func.esModulo=true "
										+ "and func.moduloFisico=true and func.entidad.id=:entId "
										+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modPadreId "
										+ "order by func.label")
						.setParameter("entId", this.selectedItem.getId())
						.setParameter("modPadreId", tipoModulo.getId())
						.getResultList();
				if (modulosFisicos.size() == 1)
					modulos.add(modulosFisicos.get(0));
				else
					modulos.add(tipoModulo);
			}
		}
		return modulos;
	}

	@SuppressWarnings("unchecked")
	public void processDrop(DropEvent dropEvent) {
		UITreeNode destNode = (dropEvent.getSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getSource() : null;
		UITree destTree = destNode != null ? destNode.getUITree() : null;
		TreeRowKey dropNodeKey = (dropEvent.getDropValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDropValue() : null;
		TreeNode droppedInNode = dropNodeKey != null ? destTree
				.getTreeNode(dropNodeKey) : null;

		UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getDraggableSource() : null;
		UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
		TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDragValue() : null;
		TreeNode draggedNode = dragNodeKey != null ? srcTree
				.getTreeNode(dragNodeKey) : null;

		ITreeData destino = (ITreeData) droppedInNode.getData();
		ITreeData dragged = ((ITreeData) draggedNode.getData());

		if (destino instanceof EntidadWrapper) {
			Funcionalidad modulo = entityManager
					.merge(((ModuloWrapper) dragged).getValue());
			Entidad_configuracion entidad = entityManager.find(
					Entidad_configuracion.class,
					((EntidadWrapper) destino).getId());
			modulo.setEntidad(entidad);
			entityManager.persist(modulo);
		}
		entityManager.flush();

		this.treeBuilder.updateNode(droppedInNode);
		if (draggedNode.getParent() != null)
			this.treeBuilder.updateNode(draggedNode.getParent());
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem(ITreeData selectedItem, TreeNode node) {
		this.selectedItem = selectedItem;
		this.selectedTreenode = node;
		if (this.selectedItem instanceof EntidadWrapper)
			this.selectedModule = "";
		if (this.selectedItem instanceof ModuloWrapper) {
			Funcionalidad mod = ((ModuloWrapper) this.selectedItem).getValue();
			mod = entityManager.merge(mod);
			((ModuloWrapper) this.selectedItem).setValue(mod);
		}
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem(ITreeData selectedItem, TreeNode node,
			boolean moduloUnico) {
		this.selectedItem = selectedItem;
		this.selectedTreenode = node;
		if (this.selectedItem instanceof EntidadWrapper)
			this.selectedModule = "";
		if (this.selectedItem instanceof ModuloWrapper) {
			Funcionalidad mod = ((ModuloWrapper) this.selectedItem).getValue();
			mod = entityManager.merge(mod);
			((ModuloWrapper) this.selectedItem).setValue(mod);
			((ModuloWrapper) this.selectedItem).setModuloUnico(moduloUnico);
			
			List<String> subModulosServicios = Arrays.asList( SeamResourceBundle.getBundle().getString( "conf_modulosNombre_servicios" ).split( ":" ) );
			
			if( subModulosServicios.contains( mod.getFuncionalidadPadre().getFuncionalidadPadre().getNombre() ) ){
				this.moduleService = true;

				ServicioSubmodulo_configuracion s = findServicioSubmdulo(mod);
				
				if( s == null ){
					this.servicioSelected = "";
					return;
				}
				
				this.servicioSelected = s.getServicioInEntidad().getServicio().getNombre(); 
				
			}else{
				this.servicioSelected = "";
				this.moduleService = false;
			}
		}
	}

	public void setSelectedItem(Funcionalidad modulo, boolean moduloUnico) {
		if (moduloUnico) {
			this.selectedItem = new ModuloWrapper(modulo, false, modulo
					.getEntidad().getId());
			((ModuloWrapper) this.selectedItem).setType("modulo-fisico");
			((ModuloWrapper) this.selectedItem).setModuloUnico(moduloUnico);
			
		} else {
			this.selectedItem = new ModuloWrapper(modulo, false,
					this.selectedItem.getEntidadID());
			((ModuloWrapper) this.selectedItem).setModuloUnico(moduloUnico);
			if (modulo.getModuloFisico()){
				((ModuloWrapper) this.selectedItem).setType("modulo-fisico");
				
				List<String> subModulosServicios = Arrays.asList( SeamResourceBundle.getBundle().getString( "conf_modulosNombre_servicios" ).split( ":" ) );
				
				if( subModulosServicios.contains( modulo.getFuncionalidadPadre().getFuncionalidadPadre().getNombre() ) ){
					
					this.moduleService = true;
					ServicioSubmodulo_configuracion s = findServicioSubmdulo( modulo );
					if( s == null ){
						this.servicioSelected = "";
						return;
					}
					this.servicioSelected = s.getServicioInEntidad().getServicio().getNombre(); 

				}else{
				
					this.servicioSelected = "";
					this.moduleService = false;
			
				}
			}
			else
				((ModuloWrapper) this.selectedItem).setType("modulo-padre");
		}
	}

	public void setSelectedItem(Long entidadId) {
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, entidadId);
		this.selectedItem = new EntidadWrapper(entidad, false, entidadId);
		if (this.selectedItem instanceof EntidadWrapper)
			this.selectedModule = "";
	}

	public ModulosPorEntidadTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(ModulosPorEntidadTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public ITreeData getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ITreeData selectedItem) {
		this.selectedItem = selectedItem;
		if (this.selectedItem instanceof EntidadWrapper)
			this.selectedModule = "";
	}

	public String getSelectedModule() {
		return selectedModule;
	}

	public void setSelectedModule(String selectedModule) {
		this.selectedModule = selectedModule;
		this.selectedSubModule = "";
	}

	public String getSelectedSubModule() {
		return selectedSubModule;
	}

	public void setSelectedSubModule(String selectedSubModule) {
		this.selectedSubModule = selectedSubModule;
	}

	public Integer getModulosCount() {
		return modulosCount;
	}

	public void setModulosCount(Integer modulosCount) {
		this.modulosCount = modulosCount;
	}

	public String getModuloIdToRemove() {
		return moduloIdToRemove;
	}

	public void setModuloIdToRemove(String moduloIdToRemove) {
		this.moduloIdToRemove = moduloIdToRemove;
	}

	public String getServicioSelected() {
		return servicioSelected;
	}

	public void setServicioSelected(String servicioSelected) {
		this.servicioSelected = servicioSelected;
	}

	public Boolean getModuleService() {
		return moduleService;
	}

	public void setModuleService(Boolean moduleService) {
		this.moduleService = moduleService;
	}

}
