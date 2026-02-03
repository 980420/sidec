package gehos.comun.procesos.treebuilders.model;

import java.math.BigInteger;
import java.util.Date;

public class InstanciaProceso {

	public InstanciaProceso(String nombreProceso, Integer numeroRev) {
		super();
		this.nombreProceso = nombreProceso;
		this.numeroRevision = numeroRev;
	}

	public InstanciaProceso() {
	}

	@Override
	public String toString() {
		return "definicion";
	}

	private String nombreProceso;
	private Integer numeroRevision;
	private Integer version;
	private Long id;
	private Date iniciadoEn;

	public String getNombreProceso() {
		return nombreProceso;
	}

	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
	}

	public Integer getNumeroRevision() {
		return numeroRevision;
	}

	public void setNumeroRevision(Integer numeroRevision) {
		this.numeroRevision = numeroRevision;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getIniciadoEn() {
		return iniciadoEn;
	}

	public void setIniciadoEn(Date iniciadoEn) {
		this.iniciadoEn = iniciadoEn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
