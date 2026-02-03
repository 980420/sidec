package gehos.pki.ra;

import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.pki.entity.UsuarioKeystore_pki;
import gehos.pki.entity.Usuario_pki;
import gehos.pki.utils.ListadoController;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;

@SuppressWarnings("serial")
@Name("identidadesDigitalesControlador")
@Scope(ScopeType.CONVERSATION)
public class IdentidadesDigitalesControlador extends
		EntityQuery<UsuarioKeystore_pki> {

	private static final String EJBQL = "select usuario_keystore from UsuarioKeystore_pki usuario_keystore where usuario_keystore.usuario.eliminado = false";

	// busqeuda avanzada
	private static final String[] RESTRICTIONSA = {
			"lower(usuario_keystore.usuario.nombre) like concat(lower(#{identidadesDigitalesControlador.nombre.trim()}),'%')",
			"lower(usuario_keystore.usuario.username) like concat(lower(#{identidadesDigitalesControlador.nombreUsuario.trim()}),'%')",
			"lower(usuario_keystore.usuario.primerApellido) like concat(lower(#{identidadesDigitalesControlador.primerApellido.trim()}),'%')",
			"lower(usuario_keystore.usuario.segundoApellido) like concat(lower(#{identidadesDigitalesControlador.segundoApellido.trim()}),'%')",
			};

	

	// usuario
	private String nombreUsuario = "";
	private String nombre = "";
	private String primerApellido = "";
	private String segundoApellido = "";
	private String vencidas = "";
	private Date date;
	

	// otras funcionalidades
	private boolean busquedaTipo = false;
	private boolean openSimpleTogglePanel = true;
	
	

	@In
	EntityManager entityManager;
	
	@In(create = true)
	RaFunctions raFunctions;

	@In(create = true)
	FacesMessages facesMessages;
	
	

	// METODOS----------------------------------------------------------------------
	// constructor	
	public IdentidadesDigitalesControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSA));
		setMaxResults(5);
		setOrder("usuario_keystore.id desc");
		
	}

	@Begin(join=true,flushMode=FlushModeType.MANUAL)
	public void begin(){
		
	}
	

	// cargar el estado de la restriccion a partir del tipo de busqueda
	public void setBusquedaTipo(boolean busquedaTipo) {
		this.busquedaTipo = busquedaTipo;
		
	}

	// dado el tipo de busqueda carga la restriccion
	public void busqueda(boolean avan) {
		setFirstResult(0);
		setOrder("usuario_keystore.id desc");
		
	}
	
	public List<String> vencidasList() {
		List<String> list = new ArrayList<String>(3);
		list.add(SeamResourceBundle.getBundle().getString("seleccione"));
		list.add(SeamResourceBundle.getBundle().getString("si"));
		list.add(SeamResourceBundle.getBundle().getString("no"));
		return list;
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
	
	public String DateFormat(Date date){
		if(date == null)
			return "";
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
		return dateformat.format(date);
	}
	

	// PROPIEDADES--------------------------------------------------------------
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
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

	public String getVencidas() {
		return vencidas;
	}

	public void setVencidas(String vencidas) {
		this.vencidas = vencidas;
		this.date = new Date();
		this.setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSA));
		if (this.vencidas.equals(SeamResourceBundle.getBundle().getString("si"))) {
			
			List<String> restr = this.getRestrictionExpressionStrings();
			restr.add("usuario_keystore.fechavencimiento < #{identidadesDigitalesControlador.date}");
			this.setRestrictionExpressionStrings(restr);
		}

		else if (this.vencidas
				.equals(SeamResourceBundle.getBundle().getString("no"))) {
			List<String> restr = this.getRestrictionExpressionStrings();
			restr.add("usuario_keystore.fechavencimiento >= #{identidadesDigitalesControlador.date}");
			this.setRestrictionExpressionStrings(restr);
		}else{
			this.vencidas = "";
		}
		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	

}

