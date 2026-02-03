package gehos.ensayo.ensayo_disenno.session.custom;

import java.util.Arrays;

import javax.persistence.EntityManager;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.session.common.auto.HojaCrdList_ensayo;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralList_ensayo;












import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("hojasCRDCustomList_ensayo")
@Scope(ScopeType.CONVERSATION)
public class HojasCRDCustomList_ensayo extends HojaCrdList_ensayo{
	
	@In SeguridadEstudio seguridadEstudio;
	@In	EntityManager entityManager;
	@In	FacesMessages facesMessages;
	
	private static final String EJBQL = "select crd from HojaCrd_ensayo crd "
			+ "where crd.eliminado <> true "
			+ "and crd.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()}";

	private static final String[] RESTRICTIONS = {
			"lower(crd.nombreHoja) like concat('%',concat(lower(#{hojasCRDCustomList_ensayo.nombre}),'%'))"};
			

	public HojasCRDCustomList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("crd.nombreHoja");
		setMaxResults(10);
	}
	
	private String nombre= "";	
	private int pagina;
	private long idGrupoSujeto;
	
	public boolean estaAsociada(long idHojaCrd){
		try {
			long countMS = (Long) entityManager.createQuery("select count(momentoHojaCRD) from MomentoSeguimientoGeneralHojaCrd_ensayo momentoHojaCRD "
					+ "where momentoHojaCRD.momentoSeguimientoGeneral.eliminado <> true "
					+ "and momentoHojaCRD.eliminado<>true "					
					+ "and momentoHojaCRD.hojaCrd.id = :idHojaCrd")
					.setParameter("idHojaCrd", idHojaCrd)
					.getSingleResult();
			if(countMS>0)
				return true;
			else
				return false;
		} catch (Exception e) {
			// TODO: handle exception
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return false;
		}
	}
	
	public boolean cartelListado(){
		if(this.getNombre() == null)
			return true;
		else return false;
	}
	
	/*Getters and Setters*/
	


	public String getNombre() {
		return nombre;
	}	

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	

	public long getIdGrupoSujeto() {
		return idGrupoSujeto;
	}

	public void setIdGrupoSujeto(long idGrupoSujeto) {
		this.idGrupoSujeto = idGrupoSujeto;
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
