package gehos.comun.funcionalidades.entity.custom;


public class FuncionalidadWrapper {
	
	private Long id;
	private String label;
	private String url;
	private String modulo;
	private String imagen;
	
	public FuncionalidadWrapper(Long id, String label, String url,
			String modulo, String imagen) {
		super();
		this.id = id;
		this.label = label;
		this.url = url;
		this.modulo = modulo;
		this.imagen = imagen;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	
	
}
