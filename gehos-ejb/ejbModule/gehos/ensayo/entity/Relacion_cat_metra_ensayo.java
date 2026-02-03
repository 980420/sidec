package gehos.ensayo.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "relacion_cat_metra", schema = "ensayo")
public class Relacion_cat_metra_ensayo  implements java.io.Serializable{
	
	private long id;
	private Ctc_ensayo ctc;
	private CtcCategoria_ensayo categoria;
	
	public Relacion_cat_metra_ensayo(){}
	
	public Relacion_cat_metra_ensayo(long id, Ctc_ensayo ctc,
			CtcCategoria_ensayo categoria) {
		super();
		this.id = id;
		this.ctc = ctc;
		this.categoria = categoria;
	}
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ctc")
	public Ctc_ensayo getCtc() {
		return this.ctc;
	}

	public void setCtc(Ctc_ensayo ctc_ensayo) {
		this.ctc = ctc_ensayo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	public CtcCategoria_ensayo getCategoria() {
		return this.categoria;
	}

	public void setCategoria(CtcCategoria_ensayo categoria) {
		this.categoria = categoria;
	}
	
	
}
