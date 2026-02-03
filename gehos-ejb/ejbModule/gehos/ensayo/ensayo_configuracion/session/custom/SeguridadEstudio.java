package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.UserTools;
import gehos.autorizacion.management.physical.Permission;
import gehos.autorizacion.management.physical.PermissionResolverDataLoader;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.entity.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Clase para la gestion de la seguridad en un estudio
 * 
 * @author tgonzalez
 * 
 */
@AutoCreate
@Name("seguridadEstudio")
@Scope(ScopeType.SESSION)
public class SeguridadEstudio {

	// Variables del Sistema

	/**
	 * Para manejar los datos en la BD
	 */
	@In(create=true)
	EntityManager entityManager;

	/**
	 * Para obtener la direccion en que se encuentra el usuario
	 */
	@In
	FacesContext facesContext;

	/**
	 * Para obtener la entidad en que se encuentra el usuario
	 */
	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	/**
	 * Para ver el usuario autenticado en la entidad
	 */
	@In
	Usuario user;

	/**
	 * Para las trazas del sistema
	 */
	@In
	IBitacora bitacora;

	@In("permissionResolverDataLoader")
	PermissionResolverDataLoader dataLoader;

	@In(required = false)
	UserTools userTools;
	
	public static Long idEstudioActivo;

	// Variables locales

	/**
	 * Almacena el Estudio Entidad activo
	 */
	private EstudioEntidad_ensayo estudioEntidadActivo;

	public EstudioEntidad_ensayo getEstudioEntidadActivo() {
		return estudioEntidadActivo;
	}

	public void setEstudioEntidadActivo(
			EstudioEntidad_ensayo estudioEntidadActivo) {
		this.estudioEntidadActivo = estudioEntidadActivo;
	}

	/**
	 * Estudio Activo en la seccion
	 * 
	 * @return Estudio Activo
	 */
	public Estudio_ensayo EstudioActivo() {
		
		idEstudioActivo = estudioEntidadActivo.getEstudio().getId();
		return estudioEntidadActivo.getEstudio();
	}

	// Metodos

	/**
	 * Comprueba si existe un estudio activo en la sesion actual
	 * 
	 * @return true ssi existe estudio activo
	 */
	public boolean VerificarActivo() {
		try {

			this.estudioEntidadActivo = entityManager.find(
					EstudioEntidad_ensayo.class,
					this.estudioEntidadActivo.getId());

			if (this.estudioEntidadActivo != null)
				return true;
			else 			
				return false;

		} catch (Exception e) 
		{
			return false;
		}
	}

	/**
	 * Activa en la sesion actual el estudio seleccionado
	 * 
	 * @param estudioEntidad 
	 * Estudio a activar
	 * @return true ssi se activa el estudio correctamente
	 */
	public boolean PonerActivo(EstudioEntidad_ensayo estudioEntidad) {

		estudioEntidadActivo = null;

		estudioEntidadActivo = entityManager.find(EstudioEntidad_ensayo.class,
				estudioEntidad.getId());

		bitacora.registrarInicioDeAccion("Seleccionando - estudio "
				+ estudioEntidadActivo.getEstudio().getNombre()
				+ " en la - entidad "
				+ estudioEntidadActivo.getEntidad().getNombre());

		return VerificarActivo();
	}

	private final String NO_ACCESS = "not-authorized";
	private final String OK = "ok";
	private final String NOT_SELECTED = "not-selected";

	/**
	 * @author Tania
	 * Este metodo es para las trazas de conduccion.
	 * Verifica si el estudio activo esta en conduccion sino redirecciona al 
	 * seleccionar estidio cuando se entra a trazas de conduccion
	 */
	@SuppressWarnings({ "static-access", "unused" })
	public String CanSeeThisFunctionalityNowTrazas() {
		String ret = CanSeeThisFunctionalityNow();
		if(ret==OK){

			//

			List m = entityManager.createQuery("select estudioEntidad from "
					+ "EstudioEntidad_ensayo estudioEntidad "
					+ "JOIN estudioEntidad.entidad entidadE "
					+ "JOIN estudioEntidad.estudio estudioE where estudioEntidad.eliminado <> true "
					+ "and entidadE.eliminado <> true "
					+ "and estudioE.eliminado <> true "
					+ "and estudioE.estadoEstudio.codigo in (3,6) and estudioEntidad.id =:idest").setParameter("idest", this.estudioEntidadActivo.getId()).getResultList();

			if(m.size()==0){
				return NOT_SELECTED;
			}

		}
		return ret;
	}

	@SuppressWarnings({ "static-access", "unused" })
	public String CanSeeThisFunctionalityNow() {

		// Si no existe un estudio activo
		if (!this.VerificarActivo())
			return this.NOT_SELECTED;

		// Si el usuario es root puede entrar a donde sea
		if (user.getUsername().equals("root"))
			return this.OK;		

		// La direcci�n actual en el HIS
		String viewId = facesContext.getCurrentInstance().getViewRoot()
				.getViewId();

		// No se controla la seguridad a las paginas necesarias para el sistema
		if (viewId.equals("/error.xhtml")
				|| viewId
				.equals("/modConfiguracion/anillo/ConfigurarAnillo.xhtml")
				|| viewId.equals("/modCommons/login/login.xhtml"))
			return this.OK;

		// Para las paginas necesarias
		if (viewId.equals("/index.html")
				|| viewId.equals("/profile/home.xhtml")
				|| viewId.indexOf("codebase") != -1
				|| viewId.indexOf("modCommons") != -1
				|| viewId.startsWith("/modConfiguracion/incidencias/"))
			return this.OK;

		do {
			if (dataLoader.getPermissionsTable().containsKey(viewId)) {

				Permission temp = dataLoader.getPermissionsTable().get(viewId);

				// El usuario tiene restricciones con esta entrada
				if (temp.getRestrictedUsersHashMap().containsKey(
						this.user.getUsername()))
					return this.NO_ACCESS;

				// El usuario no tiene restricciones con esta entrada
				if (temp.getPermittedUsersHashMap().containsKey(
						this.user.getUsername()))
					return this.OK;

				HashSet<String> userRole = new HashSet<String>();

				@SuppressWarnings("unchecked")
				List<String> roles = this.entityManager
				.createQuery(
						"Select ueeRole.name from UsuarioEstudio_ensayo uee JOIN uee.role ueeRole"
								+ " where "
								+ "uee.estudioEntidad.id = :idEstudioEntidad "
								+ "and uee.eliminado <> true "
								+ "and uee.usuario.id = :idUsuario "
								+ "and ueeRole.eliminado <> true")
								.setParameter("idEstudioEntidad",
										estudioEntidadActivo.getId())
										.setParameter("idUsuario", this.user.getId())
										.getResultList();

				for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					userRole.add(string);
				}

				HashSet<String> intersection = new HashSet<String>(temp
						.getRestrictedRolesHashMap().keySet());
				intersection.retainAll(userRole);
				if (intersection.size() >= 1)
					return this.NO_ACCESS;

				intersection = new HashSet<String>(temp
						.getPermittedRolesHashMap().keySet());
				intersection.retainAll(userRole);
				if (intersection.size() >= 1)
					return this.OK;
			}

			viewId = viewId.substring(0, viewId.lastIndexOf("/"));

		} while (viewId.length() > 1);


		return this.NO_ACCESS;

	}

	/**
	 * Convirte un valor del tipo inetAddress a long
	 * 
	 * @param ip
	 * @return octeto con el valor del ip.
	 */
	private long ipToLong(InetAddress ip) {

		byte[] octets = ip.getAddress();

		long result = 0;

		for (byte octet : octets) {

			result <<= 8;

			result |= octet & 0xff;

		}

		return result;
	}

	/**
	 * Para almacenar el ip que esta intentando acceder al sistema
	 */
	@In(value = "#{remoteAddr}", required = true)
	private String ipVerf;

	/**
	 * Verifica si el ip por el que se esta tratando de acceder al sistema
	 * pertenece a la entidad que esta activa en la sesion
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	@SuppressWarnings("unchecked")
	public String verificarIP() throws UnknownHostException {
		// Si el usuario es root puede entrar a donde sea
		if (user.getUsername().equals("root"))
			return "true";

		String viewId = facesContext.getCurrentInstance().getViewRoot()
				.getViewId();

		// No se controla la seguridad a las paginas necesarias para el sistema
		if (viewId.equals("/error.xhtml")
				|| viewId
				.equals("/modConfiguracion/anillo/ConfigurarAnillo.xhtml")
				|| viewId.equals("/modCommons/login/login.xhtml")
				|| viewId.equals("/index.html"))
			return "true";

		List<DireccionIp_ensayo> dir = entityManager
				.createQuery(
						"select dirIP from Entidad_ensayo ee JOIN ee.direccionIps dirIP Where ee.id = :idee and ee.eliminado <> true")
						.setParameter(
								"idee",
								this.activeModule.getActiveModule().getEntidad()
								.getId()).getResultList();
		if (dir.size() != 0) {
			for (int a = 0; a < dir.size(); a++) {

				// Tomo el IP DE INICIO EN LONG
				long ipLo = ipToLong(InetAddress.getByName(dir.get(a).getIp()));
				// Tomo el IP FINAL EN LONG
				long ipHi = ipToLong(InetAddress.getByName(dir.get(a)
						.getIpFinal()));
				// TOMO EL IP A TESTEAR EN LONG
				long ipToTest = ipToLong(InetAddress.getByName(this.ipVerf));
				// Comparao el intervalo
				boolean check = (ipToTest >= ipLo && ipToTest <= ipHi);

				if (check)
					return "true";

			}
		} else
			return "true";

		return "false";

	}

	public String rolUsuarioEstudio() {
		try {
			String rol = "";
			if(VerificarActivo()){
				
				rol = (String)entityManager.createQuery("Select usuarioEstudio.role.name from UsuarioEstudio_ensayo usuarioEstudio "
						+ "where usuarioEstudio.usuario.id =:idUser "
						+ "and usuarioEstudio.estudioEntidad.id =:idEstudioEntidad")
						.setParameter("idUser", user.getId())
						.setParameter("idEstudioEntidad", this.estudioEntidadActivo.getId()).getSingleResult();

				
			}
			return rol;

		} catch (Exception e) 
		{
			return "";
		}
	}
	
	public Long getActiveStudyId() {
	    if (estudioEntidadActivo != null && estudioEntidadActivo.getEstudio() != null) {
	        return estudioEntidadActivo.getEstudio().getId();
	    } else {
	        return null;
	    }
	}

	public Estudio_ensayo getEstudioActivo() {
	    if (estudioEntidadActivo != null && estudioEntidadActivo.getEstudio() != null) {
	        return estudioEntidadActivo.getEstudio();
	    } else {
	        return null;
	    }
	}
	
}
