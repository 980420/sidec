package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos; 
 

 
import java.util.List;

import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo; 
import gehos.ensayo.entity.Variable_ensayo;
 
 
public class MsHojaCrdConjuntoDatos { 
  private String id; 
  private MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd; 
  private List<Variable_ensayo> listadoVariables;
  public MsHojaCrdConjuntoDatos(String id, 
      MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd,
      List<Variable_ensayo> listadoVariables) { 
    super(); 
    this.id = id; 
    this.msHojaCrd = msHojaCrd; 
    this.listadoVariables = listadoVariables;
  } 
  public String getId() { 
    return id; 
  } 
  public void setId(String id) { 
    this.id = id; 
  } 
  public MomentoSeguimientoGeneralHojaCrd_ensayo getMsHojaCrd() { 
    return msHojaCrd; 
  } 
  public void setMsHojaCrd(MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd) { 
    this.msHojaCrd = msHojaCrd; 
  }
public List<Variable_ensayo> getListadoVariables() {
	return listadoVariables;
}
public void setListadoVariables(List<Variable_ensayo> listadoVariables) {
	this.listadoVariables = listadoVariables;
} 
   
} 
