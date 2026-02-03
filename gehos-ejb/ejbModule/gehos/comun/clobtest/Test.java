package gehos.comun.clobtest;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "aatest")
public class Test implements java.io.Serializable {
	private static final Long serialVersionUID = -831834892314948786L;
	private Long id;
	private String Longtext;

	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name = "Longtext")
	public String getLongtext() {
		return Longtext;
	}

	public void setLongtext(String Longtext) {
		this.Longtext = Longtext;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@NotNull
	public Long getId() {
		return this.id;
	}
}
