package gehos.ensayo.ensayo_extraccion.session.custom;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralHojaCrdList_ensayo;
import java.util.Arrays;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;



@Name("momentoSeguimientoGeneralHojaCrdCustomList_ensayo")
@Scope(ScopeType.PAGE)
public class MomentoSeguimientoGeneralHojaCrdCustomList_ensayo
  extends MomentoSeguimientoGeneralHojaCrdList_ensayo
{
  @In
  SeguridadEstudio seguridadEstudio;
  private static final String EJBQL = "select msHojaCRD from MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCRD ";
  private static final String[] RESTRICTIONS = { "lower(msHojaCRD.hojaCrd.seccions.variables.nombreVariable) like concat(lower(#{variableList_ensayo.variable.nombreVariable}),'%')" };
  
  public MomentoSeguimientoGeneralHojaCrdCustomList_ensayo()
  {
    setEjbql("select msHojaCRD from MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCRD ");
    setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    setOrder("variable.id");
    setMaxResults(Integer.valueOf(10));
  }
  

  private String nombre = "";
  

  private int pagina;
  


  public String getNombre()
  {
    return nombre;
  }
  
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  


  public int getPagina()
  {
    if (getNextFirstResult() != 0) {
      return getNextFirstResult() / 10;
    }
    return 1;
  }
  
  public void setPagina(int pagina) {
    this.pagina = pagina;
    
    long num = getResultCount().longValue() / 10L + 1L;
    if (this.pagina > 0) {
      if (getResultCount().longValue() % 10L != 0L) {
        if (pagina <= num) {
          setFirstResult(Integer.valueOf((this.pagina - 1) * 10));
        }
      }
      else if (pagina < num) {
        setFirstResult(Integer.valueOf((this.pagina - 1) * 10));
      }
    }
  }
}
