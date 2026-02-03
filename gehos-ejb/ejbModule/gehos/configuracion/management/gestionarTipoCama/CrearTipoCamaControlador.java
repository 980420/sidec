package gehos.configuracion.management.gestionarTipoCama;

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
import java.io.File;
import java.util.Arrays;
import java.util.List;


@Name("crearTipoCamaControlador")
@Scope(ScopeType.CONVERSATION)
public class CrearTipoCamaControlador {
	
	@In 
	EntityManager entityManager;
	
	@In
	LocaleSelector localeSelector;
	
	@In(create = true)
	FacesMessages facesMessages;
	
	//Campos	
	private Long id;
	private String codigo,valor;
	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();
	private byte[] categoryIcon;
	private String IconFileName,paramname;
	private String existingCategoryIconName = "camas.png";
	private Boolean valorRep = false;
	
	//Metodos
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){		
	}
	
	public String limpiar(){
		this.valor = "";
		this.codigo = "";
		this.existingCategoryIconName = "camas.png";
		this.valorRep = false;
		
		return "salir";
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public String crear(){
		valorRep = false;
		List<TipoCama_configuracion> aux = entityManager.createQuery("select tCama from TipoCama_configuracion tCama where tCama.valor=:valor").setParameter("valor", this.valor).getResultList();
		
		if (aux.size()!=0) {
			facesMessages.add(new FacesMessage("Valor repetido"));
			valorRep = true;
			return "valor requerido";
		}
		
		this.tipoCama.setIcono(existingCategoryIconName);
		this.tipoCama.setCodigo(codigo);
		this.tipoCama.setValor(valor);
		this.tipoCama.setEliminado(false);
		
		entityManager.persist(tipoCama);
		entityManager.flush();
		
		return "crear";
	}
	
	public boolean isSelected(String icon) {
		if (this.existingCategoryIconName.equals(icon))
			return true;
		return false;
	}
	
	public String[] getExistingModuleIcons() {
		
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();		
		
		
		String rootpath = context.getRealPath("/resources/modCommon/tipocamaimage");
		
		File file = new File(rootpath);		
		List<String> iconos = Arrays.asList(file.list());
		
		return file.list();	
	}
	
	
	//Propiedades

	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Length(max=25,message="El máximo de caracteres es: 25")		
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
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor.trim();
	}
	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}
	public void setTipoCama(TipoCama_configuracion tipoCama) {
		this.tipoCama = tipoCama;
	}

	public byte[] getCategoryIcon() {
		return categoryIcon;
	}

	public void setCategoryIcon(byte[] categoryIcon) {
		this.categoryIcon = categoryIcon;
	}

	public String getIconFileName() {
		return IconFileName;
	}

	public void setIconFileName(String iconFileName) {
		IconFileName = iconFileName;
	}

	public String getParamname() {
		return paramname;
	}

	public void setParamname(String paramname) {
		this.paramname = paramname;
	}

	public String getExistingCategoryIconName() {
		return existingCategoryIconName;
	}

	public void setExistingCategoryIconName(String existingCategoryIconName) {
		this.existingCategoryIconName = existingCategoryIconName;
	}

	public Boolean getValorRep() {
		return valorRep;
	}

	public void setValorRep(Boolean valorRep) {
		this.valorRep = valorRep;
	}

}
