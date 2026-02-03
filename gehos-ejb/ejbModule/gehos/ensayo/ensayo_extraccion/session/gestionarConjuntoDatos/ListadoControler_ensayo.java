package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import java.util.ArrayList;
import java.util.List;

public class ListadoControler_ensayo<T>
{
  private List<T> elementos;
  private Integer firstResult;
  private Integer maxResults = Integer.valueOf(10);
  private int pagina;
  
  public ListadoControler_ensayo(List<T> elementos) {
    this.elementos = elementos;
    firstResult = Integer.valueOf(0);
  }
  
  public Integer getResultCount() {
    return Integer.valueOf(elementos.size());
  }
  
  public Integer getMaxResults() { return maxResults; }
  
  public void setMaxResults(Integer maxResults) {
    this.maxResults = maxResults;
  }
  
  public int getPreviousFirstResult() {
    if (getFirstResult().intValue() == 0) {
      return 0;
    }
    return getFirstResult().intValue() - getMaxResults().intValue();
  }
  
  public List<T> getElementos() {
    return elementos;
  }
  
  public void setElementos(List<T> elementos) {
    this.elementos = elementos;
  }
  
  public int getNextFirstResult() {
    return getFirstResult().intValue() + getMaxResults().intValue();
  }
  
  public int getLastFirstResult() {
    int pos = elementos.size() / getMaxResults().intValue();
    if (elementos.size() % getMaxResults().intValue() == 0) {
      pos--;
    }
    return pos * getMaxResults().intValue();
  }
  
  public List<T> getResultList() {
    int pos = getFirstResult().intValue();
    int fin = getMaxResults().intValue() + pos;
    if (fin > elementos.size()) {
      fin = elementos.size();
    }
    List<T> result = new ArrayList();
    for (int i = pos; i < fin; i++) {
      result.add(elementos.get(i));
    }
    return result;
  }
  
  public Integer getFirstResult() {
    return firstResult;
  }
  
  public boolean getPreviousExists() {
    return firstResult.intValue() != 0;
  }
  
  public boolean getNextExists() {
    return firstResult.intValue() + getMaxResults().intValue() < elementos.size();
  }
  
  public void setFirstResult(Integer firstResult) {
    this.firstResult = firstResult;
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
    
    long num = getResultCount().intValue() / 10 + 1;
    if (this.pagina > 0) {
      if (getResultCount().intValue() % 10 != 0) {
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
