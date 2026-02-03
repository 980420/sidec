package gehos.ensayo.ensayo_disenno.session.custom;

import java.util.Arrays;

import javax.persistence.EntityManager;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralList_ensayo;









import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("momentoSeguimientoNoProgramdoCustomList")
public class MomentoSeguimientoNoProgramdoCustomList extends MomentoSeguimientoGeneralList_ensayo{

	@In EntityManager entityManager;
	@In SeguridadEstudio seguridadEstudio;
	private String nombre="";	
	private boolean falso = true;
	private boolean programado = true;
	private long idCronograma;
	private int pagina;
	
	

	private static final String EJBQL = "select msNoProgramado from MomentoSeguimientoGeneral_ensayo msNoProgramado "
			+ "inner join msNoProgramado.cronograma cronograma "			
			+ "where msNoProgramado.eliminado <> #{momentoSeguimientoNoProgramdoCustomList.falso} "
			+ "and msNoProgramado.programado  <> #{momentoSeguimientoNoProgramdoCustomList.programado} "
			+ "and cronograma.id = #{momentoSeguimientoNoProgramdoCustomList.idCronograma}";

	private static final String[] RESTRICTIONS = {
			"lower(msNoProgramado.nombre) like concat(lower(#{momentoSeguimientoNoProgramdoCustomList.nombre}),'%')"};


	
	public MomentoSeguimientoNoProgramdoCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("msNoProgramado.id");
		setMaxResults(10);
	}
	
	public void Buscar(){
		this.setFirstResult(0);
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

	

	

}
