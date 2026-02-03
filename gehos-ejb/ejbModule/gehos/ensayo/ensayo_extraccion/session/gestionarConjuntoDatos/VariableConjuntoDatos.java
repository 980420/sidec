package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Variable_ensayo;


public class VariableConjuntoDatos
{
  private int id;
  private Variable_ensayo variable;
  private MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral;
  
  public VariableConjuntoDatos(int id, Variable_ensayo variable, MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral)
  {
    this.id = id;
    this.variable = variable;
    this.momentoSeguimientoGeneral = momentoSeguimientoGeneral;
  }
  
  public int getId() { return id; }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public Variable_ensayo getVariable() { return variable; }
  
  public void setVariable(Variable_ensayo variable) {
    this.variable = variable;
  }
  
  public MomentoSeguimientoGeneral_ensayo getMomentoSeguimientoGeneral() { return momentoSeguimientoGeneral; }
  
  public void setMomentoSeguimientoGeneral(MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral)
  {
    this.momentoSeguimientoGeneral = momentoSeguimientoGeneral;
  }
}
