package gehos.configuracion.management.gestionarServiciosNoClinicos;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.entity.TipoCama_configuracion;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Pattern;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;

@Name("servicioNoClinicoEditarControlador")
@Scope(ScopeType.CONVERSATION)
public class ServicioNoClinicoEditarControlador {
	
	@In 
	EntityManager entityManager;
	
	@In
	LocaleSelector localeSelector;
	
	@In(create = true)
	FacesMessages facesMessages; 
	
	//Campos	
	private Long id;
	private String codigo,nombre,nombreDep;
	private Servicio_configuracion servicio = new Servicio_configuracion();	
	private Departamento_configuracion dep = new Departamento_configuracion(); 
	private Boolean nombreRep = false,codigoRep = false;
	
	//Metodos
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public String editar(){
		nombreRep = false;
		codigoRep = false;
		
		List<Servicio_configuracion> aux = entityManager.createQuery("select serv from Servicio_configuracion serv where serv.nombre=:nombre").setParameter("nombre", this.nombre).getResultList();
		
		if (!this.nombre.equals(servicio.getNombre())) {
			if (aux.size()!=0) {
				facesMessages.add(new FacesMessage("Nombre repetido"));
				nombreRep = true;
				return "valor requerido";
			}
		}
		
		List<Servicio_configuracion> auxC = entityManager.createQuery("select serv from Servicio_configuracion serv where serv.codigo=:codigo").setParameter("codigo", this.codigo).getResultList();
		
		if (!this.codigo.equals(servicio.getCodigo())) {
			if (auxC.size()!=0) {
				facesMessages.add(new FacesMessage("Coodigo repetido"));
				codigoRep = true;
				return "valor requerido";
			}
		}		
		
		dep = (Departamento_configuracion) entityManager.createQuery("select dep from Departamento_configuracion dep where dep.nombre=:nombreDep").setParameter("nombreDep", this.nombreDep).getSingleResult();
		
		this.servicio.setNombre(this.nombre);
		this.servicio.setCodigo(this.codigo);
		this.servicio.setDepartamento(this.dep);
		this.servicio.setEliminado(false);
		
		entityManager.merge(servicio);
		entityManager.flush();
		
		return "editar";
	}
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){		
	}
	
	public String limpiar(){
		this.nombre = "";
		this.codigo = "";
		this.nombreDep = "";		
		this.nombreRep = false;
		this.codigoRep = false;
		
		return "salir";
	}
	
	@SuppressWarnings("unchecked")
	public List<String> departamentosNoClinicos(){		
		return entityManager.createQuery("select dep.nombre from Departamento_configuracion dep where dep.esClinico = false and dep.eliminado = 'false'").getResultList();		
	}
	
	//Propiedades

	public Long getId() {		
		return id;
	}

	public void setId(Long id) {
		if (this.id == null){
			this.id = id;
			servicio = entityManager.find(Servicio_configuracion.class, this.id);
			this.nombre = servicio.getNombre();
			this.codigo = servicio.getCodigo();
			this.nombreDep = servicio.getDepartamento().getNombre();
		}
		
	}

	@Length(min=1, max=25,message="El máximo de caracteres es: 25")	
	@NotEmpty
	@Pattern(regex="^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$", message="Caracteres incorrectos")
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo.trim();
	}

	@Length(min=1, max=25,message="El máximo de caracteres es: 25")	
	@NotEmpty
	@Pattern(regex="^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$", message="Caracteres incorrectos")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre.trim();
	}

	public String getNombreDep() {
		return nombreDep;
	}

	public void setNombreDep(String nombreDep) {
		this.nombreDep = nombreDep;
	}

	public Servicio_configuracion getServicio() {
		return servicio;
	}

	public void setServicio(Servicio_configuracion servicio) {
		this.servicio = servicio;
	}

	public Departamento_configuracion getDep() {
		return dep;
	}

	public void setDep(Departamento_configuracion dep) {
		this.dep = dep;
	}

	public Boolean getNombreRep() {
		return nombreRep;
	}

	public void setNombreRep(Boolean nombreRep) {
		this.nombreRep = nombreRep;
	}

	public Boolean getCodigoRep() {
		return codigoRep;
	}

	public void setCodigoRep(Boolean codigoRep) {
		this.codigoRep = codigoRep;
	}
	
	//Propiedades
	

}
