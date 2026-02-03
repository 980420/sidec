package gehos.ensayo.ensayo_disenno.session.custom;


import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.*;
import gehos.ensayo.session.common.auto.CronogramaList_ensayo;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralList_ensayo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;





import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
@Scope(ScopeType.CONVERSATION)
@Name("cronogramasMSNoProgramadoCutomList_ensayo")
public class CronogramasMSNoProgramadoCutomList_ensayo extends CronogramaList_ensayo{
	
	@In EntityManager entityManager;
	@In SeguridadEstudio seguridadEstudio;
	
	private static final String EJBQL = "select cronograma from Cronograma_ensayo cronograma "
			+ "inner join cronograma.grupoSujetos grupoSujetos "
			+ "where grupoSujetos.habilitado <> false "
			+ "and cronograma.eliminado = false "
			+ "and (cronograma.estadoCronograma.codigo <= 2 "//Cronogramas en estado Nuevo, Elaboracion o Desaprobado
			+ "or cronograma.estadoCronograma.codigo = 5) "
			+ "and grupoSujetos.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()} "
			+ "and cronograma.grupoSujetos.nombreGrupo <> 'Grupo Pesquisaje'";
	
	/*
	 * Restricciones para la busqueda simple
	 */
	private static final String[] RESTRICTIONS = {
		"lower (cronograma.grupoSujetos.nombreGrupo) like concat('%', concat(lower(#{cronogramasMSNoProgramadoCutomList_ensayo.nombreGrupo}),'%'))",
		"cronograma.fechaCreacion >= #{cronogramasMSNoProgramadoCutomList_ensayo.desde}",
		"cronograma.fechaCreacion <= #{cronogramasMSNoProgramadoCutomList_ensayo.hasta}"};

	private int pagina;
	Date desde, hasta;
	
	private String nombreGrupo="";
	
	public CronogramasMSNoProgramadoCutomList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("cronograma.id desc");
		setMaxResults(10);
	}
	
	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}	

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}	
	public int getPagina() {
        if(this.getNextFirstResult() != 0)
            return this.getNextFirstResult()/10;
            else
                return 1;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
       
        long num=(getResultCount()/10)+1;
        if(this.pagina>0){
        if(getResultCount()%10!=0){
            if(pagina<=num)
                this.setFirstResult((this.pagina - 1 )*10);
        }
        else{
            if(pagina<num)
                this.setFirstResult((this.pagina - 1 )*10);
        }
        }
    }

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	
	
	

}
