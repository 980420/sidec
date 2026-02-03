package gehos.autorizacion.entity;

import gehos.configuracion.management.entity.Entidad_configuracion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "user_entity_permission", schema = "seguridad")
public class UserEntityPermission implements java.io.Serializable {

	private Long id;
	private Integer version;
	private Boolean eliminado;
	private Long cid;

	private Usuario_permissions usuario;
	private Entidad_configuracion entidad;
	private boolean allowed;

	public UserEntityPermission() {
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public Usuario_permissions getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario_permissions usuario) {
		this.usuario = usuario;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_id")
	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "eliminado")
	public Boolean getEliminado() {
		return this.eliminado;
	}

	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}

	@Column(name = "cid")
	public Long getCid() {
		return this.cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	@Column(name = "allowed", nullable = false)
	@NotNull
	public boolean isAllowed() {
		return this.allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

}
