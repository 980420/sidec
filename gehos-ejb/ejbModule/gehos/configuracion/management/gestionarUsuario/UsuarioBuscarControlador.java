package gehos.configuracion.management.gestionarUsuario;

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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Name("usuarioBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class UsuarioBuscarControlador extends
		EntityQuery<Usuario_configuracion> {

	private static final String EJBQL = "select usuario from Usuario_configuracion usuario where usuario.eliminado = false";

	// busqueda avanzada
	private static final String[] RESTRICTIONSA = {
			"lower(usuario.nombre) like concat(lower(#{usuarioBuscarControlador.nombre.trim()}),'%')",
			"lower(usuario.username) like concat(lower(#{usuarioBuscarControlador.nombreUsuario.trim()}),'%')",
			"lower(usuario.primerApellido) like concat(lower(#{usuarioBuscarControlador.primerApellido.trim()}),'%')",
			"lower(usuario.segundoApellido) like concat(lower(#{usuarioBuscarControlador.segundoApellido.trim()}),'%')",
			"usuario.id <> #{usuarioBuscarControlador.id}" };

	// busqueda simple
	private static final String[] RESTRICTIONSS = {
			"usuario.id <> #{usuarioBuscarControlador.id}",
			"lower(usuario.nombre) like concat(lower(#{usuarioBuscarControlador.nombre.trim()}),'%')",
			"lower(usuario.username) like concat(lower(#{usuarioBuscarControlador.nombreUsuario.trim()}),'%')",
			"usuario.id <> #{usuarioBuscarControlador.id}" };

	// usuario
	private String nombreUsuario = "";
	private String nombre = "";
	private String primerApellido = "";
	private String segundoApellido = "";
	private Usuario_configuracion usuario = new Usuario_configuracion();
	private Long id = -1l;
	private Long usuarioId = -1l;
	private Parameters parametros = new Parameters();

	// otras funcionalidades
	private boolean busquedaTipo = false;
	private boolean openSimpleTogglePanel = true;
	private int pagina;
	
	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	private int error = 0;
	private Long cid = -1l;

	// METODOS----------------------------------------------------------------------
	// constructor	
	public UsuarioBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("usuario.id desc");
	}

	// inicia la conversacion
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}

	// cargar el estado de la restriccion a partir del tipo de busqueda
	public void setBusquedaTipo(boolean busquedaTipo) {
		this.busquedaTipo = busquedaTipo;
		// setFirstResult(0);
		String[] aux = (busquedaTipo) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	// dado el tipo de busqueda carga la restriccion
	public void busqueda(boolean avan) {
		setFirstResult(0);
		String[] aux = (avan) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	// cambia de tipo de busqueda (avanzada o normal)
	public void cambiar(boolean a) {
		this.busquedaTipo = a;
	}

	// cambia el estado del simpleTogglePanel (abierto o cerrado)
	public void cambiarEstadoSimpleTogglePanel() {
		openSimpleTogglePanel = !openSimpleTogglePanel;
	}

	// cancelar busqueda
	public void cancelarBusqueda() {
		nombreUsuario = "";
		nombre = "";
		primerApellido = "";
		segundoApellido = "";
	}	

	
	public String seleccionarEliminar() throws IllegalStateException, SecurityException, SystemException {
		try {
			@SuppressWarnings("unused")
			Usuario_configuracion userConcu = (Usuario_configuracion)entityManager.createQuery("select user from Usuario_configuracion user where user.id =:id").setParameter("id", usuarioId).getSingleResult();
			
				return "good";
			
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();			
		}
		catch (Exception e) {
			e.printStackTrace();	
			Transaction.instance().rollback();
		}
		return "null";	
	}
	
	// selecciona usuario para modificar	
	public void seleccionarModificar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error  = 0;
			@SuppressWarnings("unused")
			Usuario_configuracion userConcu = (Usuario_configuracion)entityManager.createQuery("select user from Usuario_configuracion user where user.id =:id").setParameter("id", usuarioId).getSingleResult();
			
			
		}catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();				
		}
		catch (Exception e) {
			e.printStackTrace();	
			Transaction.instance().rollback();
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "msjEliminar");
			error = 1;
		}				
	}	
	
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error  = 0;
			@SuppressWarnings("unused")
			Usuario_configuracion userConcu = (Usuario_configuracion)entityManager.createQuery("select user from Usuario_configuracion user where user.id =:id").setParameter("id", usuarioId).getSingleResult();
			
			
		}catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();	
			e.printStackTrace();	
			error = 1;
		}
		catch (Exception e) {
			e.printStackTrace();	
			Transaction.instance().rollback();
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "msjEliminar");
			error = 1;
		}		
	}

	public void seleccionarEliminar(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	// eliminar usuario
	public void eliminar() {
		try {
			Usuario_configuracion aux = (Usuario_configuracion) entityManager
					.find(Usuario_configuracion.class, this.usuarioId);

			Profile_configuracion prof = entityManager.find(
					Profile_configuracion.class, this.usuarioId);
			prof.setEliminado(true);
			entityManager.persist(prof);
			aux.setCid(
					bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar")+" -"+aux.getUsername()));
			aux.setEliminado(true);
			entityManager.persist(aux);
			entityManager.flush();
			
			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());

			id = usuarioId;
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");	
		}catch (Exception exc) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "msjEliminar");							
		}

	}
	public int getPagina() {
		if(this.getNextFirstResult() != 0)
			return this.getNextFirstResult()/10;
			else
				return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
		
		long num=(getResultCount()/10)+1;
		if(this.pagina>0){
		if(getResultCount()%10!=0){
			if(pagina<=num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		else{
			if(pagina<num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		}
	}

	// PROPIEDADES--------------------------------------------------------------
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = parametros.decodec(nombreUsuario);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = parametros.decodec(nombre);
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = parametros.decodec(primerApellido);
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = parametros.decodec(segundoApellido);
	}

	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public boolean isOpenSimpleTogglePanel() {
		return openSimpleTogglePanel;
	}

	public void setOpenSimpleTogglePanel(boolean openSimpleTogglePanel) {
		this.openSimpleTogglePanel = openSimpleTogglePanel;
	}

	public boolean isBusquedaTipo() {
		return busquedaTipo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Parameters getParametros() {
		return parametros;
	}

	public void setParametros(Parameters parametros) {
		this.parametros = parametros;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

}
