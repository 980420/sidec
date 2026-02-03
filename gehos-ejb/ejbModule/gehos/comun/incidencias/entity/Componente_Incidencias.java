package gehos.comun.incidencias.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "componente", schema = "incidencias")
public class Componente_Incidencias {

	private Long id;
	private String valor;
	private boolean eliminado;
	private String codigo;
	private Usuario_Incidencias usuario;
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
	
	@Column(name = "cid", nullable = false)
	@NotNull
	public long getCid() {
		return cid;
	}
	
	public void setCid(long cid) {
		this.cid = cid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	@NotNull
	public Usuario_Incidencias getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_Incidencias usuario) {
		this.usuario = usuario;
	}
	
}
