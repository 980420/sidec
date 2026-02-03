package gehos.ensayo.ensayo_disenno.session.custom;

import java.util.Arrays;

import javax.persistence.EntityManager;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralList_ensayo;










import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("momentosNoProgramadosAprobarCronogramaCustomList")
@Scope(ScopeType.PAGE)
public class MomentosNoProgramadosAprobarCronogramaCustomList extends MomentoSeguimientoGeneralList_ensayo{
	
	@In EntityManager entityManager;
	@In SeguridadEstudio seguridadEstudio;
	private static final String EJBQL = "select msNoProgramado from MomentoSeguimientoGeneral_ensayo msNoProgramado "
			+ "inner join msNoProgramado.cronograma cronograma "			
			+ "where msNoProgramado.eliminado <> #{momentosNoProgramadosAprobarCronogramaCustomList.falso} "
			+ "and msNoProgramado.programado  = #{momentosNoProgramadosAprobarCronogramaCustomList.programado} "
			+ "and cronograma.id = #{momentosNoProgramadosAprobarCronogramaCustomList.idCronograma}";

	private static final String[] RESTRICTIONS = {
			"lower(msNoProgramado.nombre) like concat(lower(#{momentosNoProgramadosAprobarCronogramaCustomList.nombre.trim()}),'%')"};


	
	private String nombre="";
	
	private boolean falso = true;
	private boolean programado = false;
	private long idCronograma;
	
	public MomentosNoProgramadosAprobarCronogramaCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("msNoProgramado.id");
		
	}
	/*Getters and Setters*/
	public void buscar(String nombre)
	{
		this.nombre=nombre;
		this.refresh();
	}


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

