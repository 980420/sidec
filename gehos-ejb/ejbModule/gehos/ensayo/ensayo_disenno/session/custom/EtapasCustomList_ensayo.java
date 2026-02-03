package gehos.ensayo.ensayo_disenno.session.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.session.common.auto.EtapaList_ensayo;















import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("etapasCustomList_ensayo")
@Scope(ScopeType.PAGE)
public class EtapasCustomList_ensayo extends EtapaList_ensayo{
	
	@In EntityManager entityManager;
	@In SeguridadEstudio seguridadEstudio;
	private static final String EJBQL = "select etapa from Etapa_ensayo etapa "
			+ "where etapa.cronograma.id = #{etapasCustomList_ensayo.idCronograma}";

	private long idCronograma;
	private long idGrupoSujetoEtapas;
	
	private static final String[] RESTRICTIONS = {
		"lower(etapa.nombreEtapa) like concat(lower(#{etapasCustomList_ensayo.etapa.nombreEtapa}),'%')",
		"lower(etapa.descripcion) like concat(lower(#{etapasCustomList_ensayo.etapa.descripcion}),'%')", };



	public EtapasCustomList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("etapa.inicioEtapa");
		setMaxResults(3);
	}
	
	private Etapa_ensayo etapa = new Etapa_ensayo();
	
	public boolean etapaInicial(Integer val)
	{
		return val.equals(0);
		
	}
	public long getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(long idCronograma) {
		this.idCronograma = idCronograma;
	}
	
	public Etapa_ensayo getEtapa() {
		return etapa;
	}
	public long getIdGrupoSujetoEtapas() {
		return idGrupoSujetoEtapas;
	}
	public void setIdGrupoSujetoEtapas(long idGrupoSujetoEtapas) {
		this.idGrupoSujetoEtapas = idGrupoSujetoEtapas;
		GrupoSujetos_ensayo grupoSubjeto =(GrupoSujetos_ensayo) entityManager.find(GrupoSujetos_ensayo.class, this.idGrupoSujetoEtapas);
		List<Cronograma_ensayo> cronogramas = new ArrayList<Cronograma_ensayo>(grupoSubjeto.getCronogramas());
		this.idCronograma = cronogramas.get(0).getId();
	}
	
	public String nombreCronograma(){
		GrupoSujetos_ensayo grupoSubjeto =(GrupoSujetos_ensayo) entityManager.find(GrupoSujetos_ensayo.class, this.idGrupoSujetoEtapas);
		String nom = grupoSubjeto.getNombreGrupo();	
		return nom;
	}

}
