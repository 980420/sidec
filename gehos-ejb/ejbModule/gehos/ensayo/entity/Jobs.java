package gehos.ensayo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;

@Entity
@Table(name = "jobs", schema = "ensayo")
public class Jobs {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O el tipo de generación que necesites
    private int id;
    
    private String tipo_job;
    private String estado;
    private Long conjunto_datos_id;
    private String ruta;
    private String context_job;
    private String context_for_job;
    private Boolean eliminado;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Column(name = "tipo_job", length = 500)
	@Length(max = 500)
    public String getTipo_job() {
        return tipo_job;
    }

    public void setTipo_job(String tipo_job) {
        this.tipo_job = tipo_job;
    }
    @Column(name = "estado", length = 500)
	@Length(max = 500)
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    @Column(name = "conjunto_datos_id")
    public Long getConjuntoDatos() {
        return conjunto_datos_id;
    }

    public void setIdConjuntoDatos(Long conjunto_datos_id) {
        this.conjunto_datos_id = conjunto_datos_id;
    }
    @Column(name = "ruta")
    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    @Column(name = "context_job")
    public String getContextJob(){
    return context_job;
    }
    
    public void setContextJob(String context_job){
    	this.context_job = context_job;
    }
    @Column(name = "context_for_job")
    public String getContextForJob(){
        return context_for_job;
    }
        
    public void setContextForJob(String context_for_job){
        this.context_for_job = context_for_job;
    }
    @Column(name = "eliminado")
	public Boolean getEliminado() {
		return eliminado;
	}

	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}
}