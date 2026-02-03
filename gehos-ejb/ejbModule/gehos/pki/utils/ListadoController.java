package gehos.pki.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListadoController<T>{

	private List<T> elementos;
	private Integer firstResult;
	private Integer maxResult;
	
	public ListadoController(){
		
		this.firstResult = 0;
		this.maxResult = 8;
	}

	public ListadoController(List<T> elementos){
		this.elementos = elementos;
		this.firstResult = 0;
		this.maxResult = 8;
	}
	
	public ListadoController(Set<T> elementos){
		List<T> aux = new ArrayList<T>();
		for (T t : elementos) {
			aux.add(t);
		}
		this.elementos = aux;
		this.firstResult = 0;
		this.maxResult = 8;
	}
	
	public Integer getMaxResults(){
		return maxResult;
	}
	
	public void setMaxResult(Integer maxResult){
		this.maxResult = maxResult;
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
		if(pos >= fin){
			pos-= getMaxResults();
		}
		pos = pos < 0 ? 0 : pos;
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
		if(firstResult >= elementos.size()){
			firstResult-= getMaxResults();
		}
		firstResult = firstResult < 0 ? 0 : firstResult;
		return firstResult != 0;
	}
	
	public boolean getNextExists(){
		return (firstResult + getMaxResults()) < elementos.size();
	}
	
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	private String order;
	
	private T first;
	
	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
		elementos.remove(first);
		elementos.add(0, first);
	}

	public String getOrder(){
		return order;
	}
	
	boolean repetir = false;
	
	@SuppressWarnings("unchecked")
	public void setOrder(String order){
		if(order != null && !order.equals(this.order) || repetir){
			Object[] lista = elementos.toArray();
			for(int i = 0; i < elementos.size() - 1; i++){
				for(int j = i + 1; j < elementos.size(); j++){
					Comparable ini = (Comparable)value(lista[i], order);
					Comparable inj = (Comparable)value(lista[j], order);
					if(ini.compareTo(inj) > 0){
						Object aux = lista[i];
						lista[i] = lista[j];
						lista[j] = aux;
					}
				}
			}
			elementos.clear();
			if(order.endsWith("desc")){
				for(int i = lista.length - 1; i >= 0; i--){
					elementos.add((T)lista[i]);
				}
			}
			else{
				for(int i = 0; i < lista.length; i++){
					elementos.add((T)lista[i]);
				}
			}
		}
		this.order = order;
	}
	
	private Object value(Object element, String properties){
		try{
			String prop = properties.contains(".") ? 
					properties.substring(0, properties.indexOf(".")) : properties;
			prop = prop.contains(" ") ? prop.substring(0, prop.indexOf(" ")) : prop;
			Method m = element.getClass().getMethod("get" + prop.substring(0,1).toUpperCase()
					+ prop.substring(1), new Class[0]);
			Object aux = m.invoke(element, new Object[0]);
			if(!properties.contains(".")){
				return aux;
			}
			String sub = properties.substring(properties.indexOf(".") + 1);
			return value(aux, sub);
		}
		catch (Exception e) {
			@SuppressWarnings("unused")
			String a = e.getMessage();
		}
		return null;
	}
	
	public void next(){
		setFirstResult(getNextFirstResult());
	}
	
	public void previous(){
		setFirstResult(getPreviousFirstResult());
	}
	
	public void first(){
		setFirstResult(0);
	}
	
	public void last(){
		setFirstResult(getLastFirstResult());
	}
	
}
