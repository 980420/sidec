package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos;

import java.util.Hashtable;
import java.util.List;

import gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.VariableConjuntoDatos;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

public class SujetoConjuntoDatos {
	private Integer id;
	private Integer posMsEspesifico;
	private Integer posFilaGrupo;
	
	private Sujeto_ensayo sujeto;
	private Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspecificos;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	
	
	public Integer getPosMsEspesifico() {
		return posMsEspesifico;
	}
	public void setPosMsEspesifico(Integer posMsEspesifico) {
		this.posMsEspesifico = posMsEspesifico;
	}
	public SujetoConjuntoDatos(
			Integer id,
			Integer posMsEspesifico,
			Integer posFilaGrupo,
			Sujeto_ensayo sujeto,
			Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspecificos) {
		super();
		this.id = id;
		this.posMsEspesifico=posMsEspesifico;
		this.posFilaGrupo=posFilaGrupo;
		this.sujeto = sujeto;
		this.momentosEspecificos = momentosEspecificos;
	}
	
	public Integer getPosFilaGrupo() {
		return posFilaGrupo;
	}
	public void setPosFilaGrupo(Integer posFilaGrupo) {
		this.posFilaGrupo = posFilaGrupo;
	}
	public Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> getMomentosEspecificos() {
		return momentosEspecificos;
	}
	public void setMomentosEspecificos(
			Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspecificos) {
		this.momentosEspecificos = momentosEspecificos;
	}
	
	
}
