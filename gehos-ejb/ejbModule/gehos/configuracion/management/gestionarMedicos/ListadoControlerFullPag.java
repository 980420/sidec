package gehos.configuracion.management.gestionarMedicos;


import java.util.ArrayList;
import java.util.List;

public class ListadoControlerFullPag<T>{

	private List<T> elementos;
	private Integer firstResult;

	public ListadoControlerFullPag(List<T> elementos){
		this.elementos = elementos;
		this.firstResult = 0;
	}
	
	public Integer getMaxResults(){
		return 50;
	}
	
	public int getPreviousFirstResult(){
		if(getFirstResult() == 0){
			return 0;
		}
		return getFirstResult() - getMaxResults();
	}
	
	public List<T> getElementos() {
		return elementos;
	}
		
	public void setElementos(List<T> elementos) {
		this.elementos = elementos;
	}

	public int getNextFirstResult(){
		return getFirstResult() + getMaxResults();
	}
	
	public int getLastFirstResult(){
		int pos = elementos.size()/getMaxResults();
		if(elementos.size()%getMaxResults() == 0){
			pos--;
		}
		return pos * getMaxResults();
	}
	
	public List<T> getResultList(){
		int pos = getFirstResult();
		int fin = getMaxResults() + pos;
		if(fin > elementos.size()){
			fin = elementos.size();
		}
		List<T> result = new ArrayList<T>();
		for(int i = pos; i < fin; i++){
			result.add(elementos.get(i));
		}
		return result;
	}
	
	public Integer getFirstResult() {
		return firstResult;
	}
    
	public boolean getPreviousExists(){
		return firstResult != 0;
	}
	
	public boolean getNextExists(){
		return (firstResult + getMaxResults()) < elementos.size();
	}
	
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
}
