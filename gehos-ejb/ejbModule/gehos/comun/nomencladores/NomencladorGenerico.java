package gehos.comun.nomencladores;


public class NomencladorGenerico {
	
	private Long id;
	private Long cid;
	private Integer version;
	private Boolean eliminado;
	private String codigo;
	private String valor;
	private Boolean tieneCodigo;
	private Boolean tieneValor;
	private String entityName;
		
	public NomencladorGenerico() {
		super();
	}
	
	public NomencladorGenerico(Long id, Long cid, Integer version,
			Boolean eliminado, String codigo, String valor) {
		super();
		this.id = id;
		this.cid = cid;
		this.version = version;
		this.eliminado = eliminado;
		this.codigo = codigo;
		this.valor = valor;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getCid() {
		return cid;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Boolean getEliminado() {
		return eliminado;
	}
	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public Boolean getTieneCodigo() {
		return tieneCodigo;
	}
	public void setTieneCodigo(Boolean tieneCodigo) {
		this.tieneCodigo = tieneCodigo;
	}
	public Boolean getTieneValor() {
		return tieneValor;
	}
	public void setTieneValor(Boolean tieneValor) {
		this.tieneValor = tieneValor;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
