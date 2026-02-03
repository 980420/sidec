package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos; 
 
import gehos.ensayo.entity.Cronograma_ensayo; 
 
import java.util.List; 
 
public class CronogramaConjuntoDatos { 
  private String id; 
  private Cronograma_ensayo cronograma; 
  private List<MomentoSeguimientoConjuntoDatos> listadoMs; 
  public CronogramaConjuntoDatos(String id, Cronograma_ensayo cronograma, 
      List<MomentoSeguimientoConjuntoDatos> listadoMs) { 
    super(); 
    this.id = id; 
    this.cronograma = cronograma; 
    this.listadoMs = listadoMs; 
  } 
  public String getId() { 
    return id; 
  } 
  public void setId(String id) { 
    this.id = id; 
  } 
  public Cronograma_ensayo getCronograma() { 
    return cronograma; 
  } 
  public void setCronograma(Cronograma_ensayo cronograma) { 
    this.cronograma = cronograma; 
  } 
  public List<MomentoSeguimientoConjuntoDatos> getListadoMs() { 
    return listadoMs; 
  } 
  public void setListadoMs(List<MomentoSeguimientoConjuntoDatos> listadoMs) { 
    this.listadoMs = listadoMs; 
  } 
 
} 
