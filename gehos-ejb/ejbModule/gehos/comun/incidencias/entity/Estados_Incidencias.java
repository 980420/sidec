package gehos.comun.incidencias.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "estados", schema = "incidencias")
public class Estados_Incidencias {

	private Long id;
	private String valor;
	private boolean eliminado;
	private String codigo;
	private int orden;
	private long cid;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "valor", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	@Column(name = "eliminado", nullable = false)
	@NotNull
	public boolean isEliminado() {
		return eliminado;
	}
	
	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}
	
	@Column(name = "codigo", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "orden", nullable = false)
	@NotNull
	public int getOrden() {
		return orden;
	}
	
	public void setOrden(int orden) {
		this.orden = orden;
	}
	
	@Column(name = "cid", nullable = false)
	@NotNull
	public long getCid() {
		return cid;
	}
	
	public void setCid(long cid) {
		this.cid = cid;
	}	
	
}
