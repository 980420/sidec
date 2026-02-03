package gehos.configuracion.management.gestionarEnfermero;

import java.util.Arrays;

import gehos.configuracion.management.entity.Usuario_configuracion;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

@Name("enfermeroUsuarioSelect_custom")
@Scope(ScopeType.CONVERSATION)
public class EnfermeroUsuarioSelect_custom  extends EntityQuery<Usuario_configuracion> {
	
	private static final String[] RESTRICTIONS = {
		"lower(user.nombre) like concat(lower(#{enfermeroUsuarioSelect_custom.user.nombre.trim()}),'%')",
		"lower(user.username) like concat(lower(#{enfermeroUsuarioSelect_custom.user.username.trim()}),'%')",};
		
	private String selectOption = "2";	//2:Para que aparezca la opción de 'No' seleccionada por defecto
	private Long idUsuario = -1l;
	private Usuario_configuracion user = new Usuario_configuracion();
	
	//CONTRUCTOR----------------------------------------------------------
	public EnfermeroUsuarioSelect_custom() {
		setEjbql("select user from Usuario user where user.id not in (select enfermera.id from Enfermera_configuracion enfermera) and user.id not in (select medico.id from Medico_configuracion medico)");
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));		
		setMaxResults(5);
		setOrder("user.id desc");
	}

	//METODOS------------------------------------------------------------------------
	@Begin(flushMode=FlushModeType.MANUAL, join=true)
	public void begin(){	
		idUsuario = -1l;		
	}
			
	//realizar una búsqueda
	public void Buscar(){
		setFirstResult(0);
	}
	
	//cancela la busqueda
	public void cancelarBusqueda(){
		if(!user.getNombre().equals("") || !user.getUsername().equals(""))
			setFirstResult(0);
		user.setNombre("");
		user.setUsername("");
	}
		
	//PROPIEDADES
	public String getSelectOption() {
		return selectOption;
	}

	public void setSelectOption(String selectOption) {
		this.selectOption = selectOption;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Usuario_configuracion getUser() {
		return user;
	}

	public void setUser(Usuario_configuracion user) {
		this.user = user;
	}
}

	