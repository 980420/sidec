package gehos.configuracion.management.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tipo_actividad_investigacion_docencia", schema = "comun")
public class TipoActividadInvestigacionDocencia_configuracion implements
        java.io.Serializable {

    private long id;
    private String codigo;
    private String valor;
    private Set<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocencias = new HashSet<ActividadInvestigacionDocencia_configuracion>(
            0);

    public TipoActividadInvestigacionDocencia_configuracion() {

    }

    public TipoActividadInvestigacionDocencia_configuracion(long id) {
        this.id = id;
    }

    public TipoActividadInvestigacionDocencia_configuracion(
            long id,
            String codigo,
            String valor,
            Set<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocencias) {
        this.id = id;
        this.codigo = codigo;
        this.valor = valor;
        this.actividadInvestigacionDocencias = actividadInvestigacionDocencias;
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "codigo")
    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo != null)
            codigo = codigo.trim();
        this.codigo = codigo;
    }

    @Column(name = "valor")
    public String getValor() {
        return this.valor;
    }

    public void setValor(String valor) {
        if (valor != null)
            valor = valor.trim();
        this.valor = valor;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoActividadInvestigacionDocencia")
    public Set<ActividadInvestigacionDocencia_configuracion> getActividadInvestigacionDocencias() {
        return this.actividadInvestigacionDocencias;
    }

    public void setActividadInvestigacionDocencias(
            Set<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocencias) {
        this.actividadInvestigacionDocencias = actividadInvestigacionDocencias;
    }

}
