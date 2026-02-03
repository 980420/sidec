package gehos.ensayo.ensayo_extraccion.session.custom;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.session.common.auto.VariableList_ensayo;
import java.util.Arrays;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;








@Name("variablesCustomList_ensayo")
@Scope(ScopeType.PAGE)
public class VariablesCustomList_ensayo
  extends VariableList_ensayo
{
  @In
  SeguridadEstudio seguridadEstudio;
  private static final String EJBQL = "select variable from Variable_ensayo variable inner join variable.seccion seccion inner join seccion.hojaCrd hojaCrd inner join hojaCrd.momentoSeguimientoGeneralHojaCrds momentoSeguimientoGeneralHojaCrds inner join momentoSeguimientoGeneralHojaCrds.momentoSeguimientoGeneral momentoSeguimientoGeneral inner join momentoSeguimientoGeneral.cronograma cronograma ";
  private static final String[] RESTRICTIONS = { "lower(variable.nombreVariable) like concat(lower(#{variableList_ensayo.variable.nombreVariable}),'%')" };
  
  public VariablesCustomList_ensayo()
  {
    setEjbql("select variable from Variable_ensayo variable inner join variable.seccion seccion inner join seccion.hojaCrd hojaCrd inner join hojaCrd.momentoSeguimientoGeneralHojaCrds momentoSeguimientoGeneralHojaCrds inner join momentoSeguimientoGeneralHojaCrds.momentoSeguimientoGeneral momentoSeguimientoGeneral inner join momentoSeguimientoGeneral.cronograma cronograma ");
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
