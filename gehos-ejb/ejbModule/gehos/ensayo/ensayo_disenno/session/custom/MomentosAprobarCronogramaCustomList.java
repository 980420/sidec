package gehos.ensayo.ensayo_disenno.session.custom;

import java.util.Arrays;

import javax.persistence.EntityManager;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralList_ensayo;










import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("momentosAprobarCronogramaCustomList")
@Scope(ScopeType.PAGE)
public class MomentosAprobarCronogramaCustomList extends MomentoSeguimientoGeneralList_ensayo{
	
	@In EntityManager entityManager;
	@In SeguridadEstudio seguridadEstudio;
	
	
	private static final String EJBQL = "select msProgramado from MomentoSeguimientoGeneral_ensayo msProgramado "
			+ "inner join msProgramado.cronograma cronograma "			
			+ "where msProgramado.eliminado <> #{momentosAprobarCronogramaCustomList.falso} "
			+ "and msProgramado.programado  = #{momentosAprobarCronogramaCustomList.programado} "
			+ "and cronograma.id = #{momentosAprobarCronogramaCustomList.idCronograma}";

	private static final String[] RESTRICTIONS = {
			"lower(msProgramado.nombre) like concat(lower(#{momentosAprobarCronogramaCustomList.nombre.trim()}),'%')"};


	
	private String nombre="";
	
	private boolean falso = true;
	private boolean programado = true;
	private long idCronograma;
	
	public MomentosAprobarCronogramaCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("msProgramado.id");
		
	}
	/*Getters and Setters*/
	


	public String getNombre() {
		
		return nombre;
	}

	public boolean isFalso() {
		return falso;
	}

	public void setFalso(boolean falso) {
		this.falso = falso;
	}

	public void setNombre(String nombre) {
		
		this.nombre = nombre;
	}

	public boolean isProgramado() {
		return programado;
	}

	public void setProgramado(boolean programado) {
		this.programado = programado;
	}



	public long getIdCronograma() {
		
		return idCronograma;
	}



	public void setIdCronograma(long idCronograma) {
		
		this.idCronograma = idCronograma;
	}



		

	

}

