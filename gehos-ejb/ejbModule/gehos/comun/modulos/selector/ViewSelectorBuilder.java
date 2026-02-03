package gehos.comun.modulos.selector;

import gehos.autenticacion.entity.Usuario;
import gehos.autorizacion.management.logical.LogicPermissionResolver;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.funcionalidades.entity.User_comun;
import gehos.comun.funcionalidades.entity.custom.Favoritos;
import gehos.comun.funcionalidades.entity.custom.FavoritosId;
import gehos.comun.modulos.selector.model.Module;
import gehos.comun.shell.IModSelectorController;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import net.sf.jasperreports.components.barbecue.BarcodeProviders.NW7Provider;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("viewSelectorBuilder")
@Scope(ScopeType.PAGE)
public class ViewSelectorBuilder {

	@In
	IModSelectorController modSelectorController;
	@In("logicPermissionResolver")
	LogicPermissionResolver permissionResolver;
	@In
	EntityManager entityManager;
	// @In
	// IBitacora bitacora;

	@In(value = "user")
	Usuario usuario;

	List<Module> parentModules = null;

	Long moduleId;

	HashMap<String, Favoritos> favorites = null;

	public IModSelectorController getModSelectorController() {
		return modSelectorController;
	}

	public void setModSelectorController(
			IModSelectorController modSelectorController) {
		this.modSelectorController = modSelectorController;
	}

	Funcionalidad moduloConfiguracion = null;

	@Factory(value = "configuracion", scope = ScopeType.PAGE)
	public Funcionalidad configuracion() {
		if (moduloConfiguracion == null) {
			moduloConfiguracion = (Funcionalidad) entityManager.createQuery(
					"from Funcionalidad mod where mod.nombre='configuracion'")
					.getSingleResult();
		}
		return moduloConfiguracion;
	}

	@SuppressWarnings("unchecked")
	// @Factory(value = "parentModules", scope = ScopeType.EVENT)
	public List<Module> entitiesModules() throws Exception {

		// if(modSelectorController.getSelectedEntityId() != null){
		try {
			if (modSelectorController.getHasChanged() || parentModules == null
					|| moduleId != modSelectorController.getSelectedEntityId()) {

				moduleId = modSelectorController.getSelectedEntityId();
				modSelectorController.setHasChanged(false);

				Long entId = modSelectorController.getSelectedEntityId();
				if (null == entId) {
					modSelectorController.selectDefaultEntity();
					entId = modSelectorController.getSelectedEntityId();
					moduleId = entId;
				}

				/** 15/01/2014 @author yurien **/

				// Si el usuario no tiene una entidad asociada
				// se evita que se lance una excepcion
				if (null != entId) {

					modSelectorController.setSelectedModuleTypeId(entId);

					modSelectorController.setSelectedModuleTypeId(new Long(-1));
					Long selectedEntityId = modSelectorController
							.getSelectedEntityId();
					Entidad_configuracion ent = entityManager.find(
							Entidad_configuracion.class, selectedEntityId);
					modSelectorController.setHasChanged(false);
					parentModules = new ArrayList<Module>();

					if (modSelectorController.getSelectedEntityId() != null) {
						List<Funcionalidad> tiposModulosExistentesParaEntidad = entityManager
								.createQuery(
										"select distinct func.funcionalidadPadre.funcionalidadPadre "
												+ "from Funcionalidad func where func.esModulo=true "
												+ "and func.moduloFisico=true and func.entidad.id=:entId "
												+ "and (func.eliminado = false or func.eliminado = null) "
												+ "and func.funcionalidadPadre.funcionalidadPadre.id != -1 "
												+ "and (func.funcionalidadPadre.funcionalidadPadre.eliminado = null or func.funcionalidadPadre.funcionalidadPadre.eliminado = false) "
												+ "order by func.funcionalidadPadre.funcionalidadPadre.label")
								.setParameter("entId", selectedEntityId)
								.getResultList();
						for (int i = 0; i < tiposModulosExistentesParaEntidad
								.size(); i++) {

							List<Funcionalidad> instanciasModulo = entityManager
									.createQuery(
											"from Funcionalidad func where func.esModulo=true "
													+ "and func.moduloFisico=true "
													+ "and (func.eliminado = false or func.eliminado = null) "
													+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modId "
													+ "and func.entidad.id=:entId "
													+ "order by func.orden")
									.setParameter(
											"modId",
											tiposModulosExistentesParaEntidad
													.get(i).getId())
									.setParameter("entId", selectedEntityId)
									.getResultList();
							if (instanciasModulo.size() > 1) {
								int conpermisocount = 0;
								Funcionalidad unicaconpermiso = null;
								for (Funcionalidad funcionalidad : instanciasModulo) {
									if (permissionResolver
											.currentUserCanSeeThisFunctionality(
													funcionalidad,
													funcionalidad, false)) {
										conpermisocount++;
										unicaconpermiso = funcionalidad;
									}
								}
								if (conpermisocount == 1) {
									Module module = new Module(
											(unicaconpermiso
													.getFuncionalidadPadre()
													.getCodebase() != null && unicaconpermiso
													.getFuncionalidadPadre()
													.getCodebase()
													.equals("external")),
											unicaconpermiso.getId(),
											unicaconpermiso.getNombre(),
											unicaconpermiso.getLabel(),
											unicaconpermiso.getUrl(), true,
											false, !unicaconpermiso
													.getModuloFisico(),
											unicaconpermiso.getImagen(), ent
													.getNombre(),
											selectedEntityId.toString(), true);
									if (unicaconpermiso.getEliminado() != null
											&& !unicaconpermiso.getEliminado())
										parentModules.add(module);
								} else if (conpermisocount > 1) {
									Funcionalidad fun = tiposModulosExistentesParaEntidad
											.get(i);
									Module module = new Module((fun
											.getFuncionalidadPadre()
											.getCodebase() != null && fun
											.getFuncionalidadPadre()
											.getCodebase().equals("external")),
											fun.getId(), fun.getNombre(),
											fun.getLabel(), fun.getUrl(), true,
											false, !fun.getModuloFisico(),
											fun.getImagen(), ent.getNombre(),
											selectedEntityId.toString(), true);
									if (fun.getEliminado() != null
											&& !fun.getEliminado())
										parentModules.add(module);
								} else {
									Funcionalidad fun = tiposModulosExistentesParaEntidad
											.get(i);
									Module module = new Module((fun
											.getFuncionalidadPadre()
											.getCodebase() != null && fun
											.getFuncionalidadPadre()
											.getCodebase().equals("external")),
											fun.getId(), fun.getNombre(),
											fun.getLabel(), fun.getUrl(), true,
											true, !fun.getModuloFisico(),
											fun.getImagen(), ent.getNombre(),
											selectedEntityId.toString(), true);
									if (fun.getEliminado() != null
											&& !fun.getEliminado())
										parentModules.add(module);
								}
							} else if (instanciasModulo.size() == 1) {
								Funcionalidad fun = instanciasModulo.get(0);
								Module module = null;
								if (permissionResolver
										.currentUserCanSeeThisFunctionality(
												instanciasModulo.get(0),
												instanciasModulo.get(0), false)) {
									module = new Module((fun
											.getFuncionalidadPadre()
											.getCodebase() != null && fun
											.getFuncionalidadPadre()
											.getCodebase().equals("external")),
											fun.getId(), fun.getNombre(),
											fun.getLabel(), fun.getUrl(), true,
											false, !fun.getModuloFisico(),
											fun.getImagen(), ent.getNombre(),
											selectedEntityId.toString(), true);
									if (fun.getEliminado() != null
											&& !fun.getEliminado())
										parentModules.add(module);
								} else {
									module = new Module((fun
											.getFuncionalidadPadre()
											.getCodebase() != null && fun
											.getFuncionalidadPadre()
											.getCodebase().equals("external")),
											fun.getId(), fun.getNombre(),
											fun.getLabel(), fun.getUrl(), true,
											true, !fun.getModuloFisico(),
											fun.getImagen(), ent.getNombre(),
											selectedEntityId.toString(), true);
									if (fun.getEliminado() != null
											&& !fun.getEliminado())
										parentModules.add(module);
								}
							}
						}
					}

					Integer count = 0;
					for (Module m : parentModules) {
						m.setFavorites(isInFavorites(m.getId()));
						m.setOrder(count++);
					}

					for (Module m : parentModules) {
						count = 0;
						List<Funcionalidad> instanciasModulo = entityManager
								.createQuery(
										"from Funcionalidad func where func.esModulo=true "
												+ "and func.moduloFisico=true "
												+ "and (func.eliminado = false or func.eliminado = null) "
												+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modId "
												+ "and func.entidad.id=:entId")
								.setParameter("modId", m.getId())
								.setParameter(
										"entId",
										modSelectorController
												.getSelectedEntityId())
								.getResultList();
						for (Funcionalidad funcionalidad : instanciasModulo) {
							Module mm = new Module(
									(funcionalidad.getFuncionalidadPadre()
											.getCodebase() != null && funcionalidad
											.getFuncionalidadPadre()
											.getCodebase().equals("external")),
									funcionalidad.getId(), funcionalidad
											.getNombre(), funcionalidad
											.getLabel(),
									funcionalidad.getUrl(),
									isInFavorites(funcionalidad.getId()),
									false, false, funcionalidad.getImagen(),
									ent.getNombre(), selectedEntityId
											.toString(), true);
							if (permissionResolver
									.currentUserCanSeeThisFunctionality(
											funcionalidad, funcionalidad, false)) {
								mm.setLock(false);

							} else {
								mm.setLock(true);
							}
							mm.setOrder(count++);
							m.getChildrenList().add(mm);
						}

					}

					return parentModules;
				}

				// modSelectorController.setHasChanged(false);

			}
			
		}

		catch (Exception e) {
			e.printStackTrace();
			
		}
		return parentModules;

	}

	@SuppressWarnings("unchecked")
	private void buildModulesBySelectedEntities() {
		try {
			if (favorites == null) {
				favorites = new HashMap<String, Favoritos>();

				List<Favoritos> fav = (List<Favoritos>) entityManager
						.createQuery(
								"select f from Favoritos f "
										+ "where f.funcionalidadByIdModulo.id=f.funcionalidadByIdFuncionalidad.id"
										+ " and "
										+ " f.usuario.id=:idUser and f.entidad<>null")
						.setParameter("idUser", usuario.getId())
						.getResultList();
				for (Favoritos fun : fav) {
					favorites.put(fun.getFuncionalidadByIdModulo().getId()
							+ "_" + fun.getEntidad().getId(), fun);
				}

			}

		} catch (Exception e) {

		}

	}

	private Boolean isInFavorites(Long idFunc) {
		try {
			if (favorites == null) {
				buildModulesBySelectedEntities();

			}
			return favorites.containsKey(idFunc + "_"
					+ modSelectorController.getSelectedEntityId());

		} catch (Exception e) {
			return false;
		}

	}

	public List<Module> favoritos() throws Exception {
		List<Module> f = new ArrayList<Module>();
		if (favorites == null)
			buildModulesBySelectedEntities();

		Favoritos[] funs = favorites.values().toArray(
				new Favoritos[favorites.size()]);

		for (Favoritos ff : funs) {
			String parentName = "";
			Boolean children = true;
			if (!ff.getFuncionalidadByIdModulo().getFuncionalidadPadre()
					.getNombre().equals("M\u00F3dulo-HIS")) {
				parentName = ff.getFuncionalidadByIdModulo()
						.getFuncionalidadPadre().getFuncionalidadPadre()
						.getLabel();
				children = false;
			}
			Module module = new Module((ff.getFuncionalidadByIdModulo()
					.getFuncionalidadPadre().getCodebase() != null && ff
					.getFuncionalidadByIdModulo().getFuncionalidadPadre()
					.getCodebase().equals("external")), ff
					.getFuncionalidadByIdModulo().getId(), ff
					.getFuncionalidadByIdModulo().getNombre(), ff
					.getFuncionalidadByIdModulo().getLabel(), ff
					.getFuncionalidadByIdModulo().getUrl(), true,
					hasPermission(ff), children, ff
							.getFuncionalidadByIdModulo().getImagen(), ff
							.getEntidad().getNombre(), ""
							+ ff.getEntidad().getId(), true);
			module.setParentName(parentName);
			Module mm = isGroup2(module.getId());
			if (mm != null)
				module.setOrder(mm.getOrder());
			f.add(module);
		}
		return f;
	}

	public Boolean hasPermission(Favoritos fun) {
		if (permissionResolver.currentUserCanSeeThisFunctionality(
				fun.getFuncionalidadByIdModulo(),
				fun.getFuncionalidadByIdModulo(), false))
			return true;
		return false;
	}

	public Boolean existFile(String path) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		return dir.exists();
	}

	public void selectEntity(Long id) {
		
		LimpiarEstudioActivo();

		moduleId = id;
		modSelectorController.setSelectedEntityId(id);
		int count = 0;
		for (Serializable obj : modSelectorController.entitiesList()) {
			if (obj instanceof Entidad_configuracion
					&& ((Entidad_configuracion) obj).getId().equals(id)) {
				modSelectorController.entitiesList().add(0, obj);
				modSelectorController.entitiesList().remove(++count);
				break;
			}
			count++;
		}

	}

	String idFun, idEntidad, idEnt, idFuncionalidad;

	public String getIdFuncionalidad() {
		return idFuncionalidad;
	}

	public void setIdFuncionalidad(String idFuncionalidad) {
		try {
			Long id = Long.parseLong(idFuncionalidad);

			this.idFuncionalidad = idFuncionalidad;
		} catch (Exception e) {
			this.idEnt = null;
			this.idFuncionalidad = null;
		}

	}

	public String getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(String idEntidad) {
		this.idEntidad = idEntidad;
	}

	public String getIdFun() {
		return idFun;
	}

	public void setIdFun(String idFun) {
		this.idFun = idFun;
	}

	public void addorRemoveFavorites() {
		Long id = Long.parseLong(idFun);
		FavoritosId funcionalidadInUserId = new FavoritosId();

		funcionalidadInUserId.setIdUser(this.usuario.getId());

		funcionalidadInUserId.setIdFuncionalidad(id);

		funcionalidadInUserId.setIdModulo(id);

		Favoritos fav;
		try {
			fav = (Favoritos) entityManager
					.createQuery(
							"select f from Favoritos f "
									+ "where f.funcionalidadByIdModulo.id= :idF"
									+ " and "
									+ " f.usuario.id=:idUser and f.entidad.id= :idEnt"
									+ " and f.funcionalidadByIdFuncionalidad.id= :idF")
					.setParameter("idUser", usuario.getId())
					.setParameter("idEnt", Long.parseLong(idEntidad))
					.setParameter("idF", id).getSingleResult();
		} catch (Exception e) {
			fav = null;
		}

		if (fav == null && favorites.containsKey(idFun + "_" + idEntidad))
			favorites.remove(idFun + "_" + idEntidad);
		if (fav != null && !favorites.containsKey(idFun + "_" + idEntidad))
			favorites.put(id + "_" + idEntidad, fav);

		if (!favorites.containsKey(idFun + "_" + idEntidad)) {
			Favoritos f = new Favoritos();

			User_comun us = entityManager.find(User_comun.class,
					usuario.getId());
			f.setUsuario(us);

			f.setFuncionalidadByIdFuncionalidad(entityManager.find(
					Funcionalidad.class, id));

			f.setFuncionalidadByIdModulo(entityManager.find(
					Funcionalidad.class, id));

			Entidad_configuracion ent = entityManager.find(
					Entidad_configuracion.class, Long.parseLong(idEntidad));
			f.setEntidad(ent);
			f.setId(funcionalidadInUserId);

			// Long cid = bitacora
			// .registrarInicioDeAccion("Adicionando m�dulo a favoritos");
			f.setCid(-1L);

			entityManager.persist(f);
			entityManager.flush();
			favorites.put(id + "_" + idEntidad, f);
		} else {
			// Long cid = bitacora
			// .registrarInicioDeAccion("Eliminando m�dulo de favoritos");
			fav.setCid(-1L);

			entityManager.remove(fav);
			entityManager.flush();
			favorites.remove(idFun + "_" + idEntidad);
		}
	}

	public Boolean isGroup(Long id) {
		/**27/003/2014 @author yurien**/
		//Se verifica que si no existen modulos 
		//no se realice ninguna accion evitando 
		//una excepcion
		if(parentModules != null){
			for (Module m : parentModules) {
				if (m.getId().equals(id))
					if (m.getChildrenList().size() != 0)
						return true;
					else
						return false;

			}
		}
		
		return false;
	}

	public Module isGroup2(Long id) throws Exception {
		try {
			if (null == parentModules)
				entitiesModules();
			
			/**15/01/2014 @author yurien**/
			//Se verifica que si no existen modulos 
			//no se realice ninguna accion evitando 
			//una excepcion
			if (parentModules != null)
				for (Module m : parentModules) {
					if (m.getId().equals(id))
						if (m.getChildrenList().size() != 0)
							return m;
						else
							return null;

				}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return null;
	}

	public String getIdEnt() {
		return idEnt;
	}

	public void setIdEnt(String idEnt) {

		this.idEnt = idEnt;

	}

	public Boolean existEntity(Long id) {
		List<Serializable> ent = modSelectorController.entitiesList();
		for (Serializable var : ent) {
			if (var instanceof Entidad_configuracion
					&& ((Entidad_configuracion) var).getId() == id)
				return true;

		}
		return false;
	}

	Boolean ok = false;

	public void parametersValidate() {
		try {
			Long ent = Long.parseLong(this.idEnt);
			Long ff = Long.parseLong(this.idFuncionalidad);
			if (!ent.equals(modSelectorController.getSelectedEntityId())
					&& existEntity(ent))
				selectEntity(ent);
			entitiesModules();
			if (existFunInEnt(ff)) {
				ok = true;
			} else
				throw new Exception();
		LimpiarEstudioActivo();
		} catch (Exception e) {

			ok = false;

		}
	}
	@In
	SeguridadEstudio seguridadEstudio;
	public void LimpiarEstudioActivo(){
		seguridadEstudio.setEstudioEntidadActivo(null); 
	}
	
	public Boolean existFunInEnt(Long id) {
		Long ent = Long.parseLong(this.idEnt);
		Long ff = Long.parseLong(this.idFuncionalidad);
		for (Module m : parentModules)
			if (m.getEntityId().equals(this.idEnt) && m.getId().equals(ff)) {

				return true;

			}
		return false;

	}

	public Boolean getOk() {
		return ok;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}

}
