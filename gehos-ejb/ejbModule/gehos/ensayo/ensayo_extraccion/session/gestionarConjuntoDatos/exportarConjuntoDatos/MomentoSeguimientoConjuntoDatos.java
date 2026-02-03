package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos; 
 
import java.util.List; 
 
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo; 
 
public class MomentoSeguimientoConjuntoDatos { 
  private String id; 
  private MomentoSeguimientoGeneral_ensayo ms; 
  private List<MsHojaCrdConjuntoDatos> listadoMsHojasCrd; 
   
  public MomentoSeguimientoConjuntoDatos(String id, 
      MomentoSeguimientoGeneral_ensayo ms, 
      List<MsHojaCrdConjuntoDatos> listadoMsHojasCrd) { 
    super(); 
    this.id = id; 
    this.ms = ms; 
    this.listadoMsHojasCrd = listadoMsHojasCrd; 
  } 
   
  public String getId() { 
    return id; 
  } 
  public void setId(String id) { 
    this.id = id; 
  } 
  public MomentoSeguimientoGeneral_ensayo getMs() { 
    return ms; 
  } 
  public void setMs(MomentoSeguimientoGeneral_ensayo ms) { 
    this.ms = ms; 
  } 
  public List<MsHojaCrdConjuntoDatos> getListadoMsHojasCrd() { 
    return listadoMsHojasCrd; 
  } 
  public void setListadoMsHojasCrd(List<MsHojaCrdConjuntoDatos> listadoMsHojasCrd) { 
    this.listadoMsHojasCrd = listadoMsHojasCrd; 
  } 
} 
