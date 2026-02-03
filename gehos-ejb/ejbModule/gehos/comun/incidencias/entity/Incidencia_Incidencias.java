package gehos.comun.incidencias.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "incidencia", schema = "incidencias")
public class Incidencia_Incidencias {

	private Long id;
	private Estados_Incidencias estado;
	private String tema;
	private String descripcion;
	private String comentario;
	private Date fechaInicio;
	private Date fechaFin;
	private Date fechaFinEstimada;
	private int prociento;
	private Usuario_Incidencias usuarioByIdUsuario;
	private Usuario_Incidencias usuarioByIdAsignado;
	private Componente_Incidencias componente;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado", nullable = false)
	@NotNull
	public Estados_Incidencias getEstado() {
		return estado;
	}
	
	public void setEstado(Estados_Incidencias estado) {
		this.estado = estado;
	}
	
	@Column(name = "tema", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getTema() {
		return tema;
	}
	
	public void setTema(String tema) {
		this.tema = tema;
	}
	
	@Column(name = "descripcion")
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}	
	
	@Column(name = "comentario")
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_inicio", length = 13)
	@NotNull
	public Date getFechaInicio() {
		return fechaInicio;
	}
	
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_fin", length = 13)
	public Date getFechaFin() {
		return fechaFin;
	}
	
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_fin_estimada", length = 13)
	public Date getFechaFinEstimada() {
		return fechaFinEstimada;
	}
	
	public void setFechaFinEstimada(Date fechaFinEstimada) {
		this.fechaFinEstimada = fechaFinEstimada;
	}
	
	@Column(name = "porciento", nullable = false)
	@NotNull
	public int getProciento() {
		return prociento;
	}
	
	public void setProciento(int prociento) {
		this.prociento = prociento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	@NotNull
	public Usuario_Incidencias getUsuarioByIdUsuario() {
		return usuarioByIdUsuario;
	}
	
	public void setUsuarioByIdUsuario(Usuario_Incidencias usuarioByIdUsuario) {
		this.usuarioByIdUsuario = usuarioByIdUsuario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_asignado")
	public Usuario_Incidencias getUsuarioByIdAsignado() {
		return usuarioByIdAsignado;
	}
	
	public void setUsuarioByIdAsignado(Usuario_Incidencias usuarioByIdAsignado) {
		this.usuarioByIdAsignado = usuarioByIdAsignado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente")
	public Componente_Incidencias getComponente() {
		return componente;
	}
	
	public void setComponente(Componente_Incidencias componente) {
		this.componente = componente;
	}
	
	
}
