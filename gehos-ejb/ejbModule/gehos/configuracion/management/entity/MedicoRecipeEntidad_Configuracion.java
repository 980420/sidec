package gehos.configuracion.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "medico_recipe_entidad", schema = "comun")
public class MedicoRecipeEntidad_Configuracion implements java.io.Serializable{

	private Long id;
	private Long id_medico;
	private Long id_recipe;
	private Long id_entidad;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "id_medico")
	public Long getId_medico() {
		return id_medico;
	}

	public void setId_medico(Long id_medico) {
		this.id_medico = id_medico;
	}

	@Column(name = "id_recipe")
	public Long getId_recipe() {
		return id_recipe;
	}

	public void setId_recipe(Long id_recipe) {
		this.id_recipe = id_recipe;
	}

	@Column(name = "id_entidad")
	public Long getId_entidad() {
		return id_entidad;
	}

	public void setId_entidad(Long id_entidad) {
		this.id_entidad = id_entidad;
	}
}
