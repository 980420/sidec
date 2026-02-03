package gehos.comun.listadoControler;


import gehos.comun.listadoControler.compiler.Compiler;
import gehos.comun.listadoControler.restrictions.GeneralRestriction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListadoControler<T>{

	private List<T> elementos;
	private Integer firstResult;
	private Integer maxResult;
	private List<String> restrictionExpressionStrings;
	private List<T> originals;

	public ListadoControler(List<T> elementos){
		this.originals = elementos;
		this.elementos = elementos;
		this.firstResult = 0;
		this.maxResult = 10;
		restrictionExpressionStrings = new ArrayList<String>();
		restrictions = new ArrayList<GeneralRestriction>();
	}
	
	private List<GeneralRestriction> restrictions;
	
	public List<String> getRestrictionExpressionStrings() {
		return restrictionExpressionStrings;
	}

	public void setRestrictionExpressionStrings(
			List<String> restrictionExpressionStrings) throws Exception {
		this.restrictionExpressionStrings = restrictionExpressionStrings;
		restrictions = new ArrayList<GeneralRestriction>();
		for (String string : restrictionExpressionStrings) {
			restrictions.add(Compiler.getRestriction(string));
		}
	}
	
	public void refresh(){
		List<GeneralRestriction> rest = new ArrayList<GeneralRestriction>();
		for (GeneralRestriction generalRestriction : restrictions) {
			if(generalRestriction.getExpressionParameter().getValue(null) != null
					&& !generalRestriction.getExpressionParameter().getValue(null).toString().isEmpty()){
				rest.add(generalRestriction);
			}
		}
		elementos = new ArrayList<T>();
		for (T t : originals) {
			boolean cumple = true;
			for (GeneralRestriction generalRestriction : rest) {
				if(!generalRestriction.getBaseRestriction().valid(t)){
					cumple = false;
				}
			}
			if(cumple){
				elementos.add(t);
			}
		}
		if(order != null){
			Object[] lista = elementos.toArray();
			for(int i = 0; i < elementos.size() - 1; i++){
				for(int j = i + 1; j < elementos.size(); j++){
					Comparable ini = (Comparable)value(lista[i], order);
					Comparable inj = (Comparable)value(lista[j], order);
					int comp = ini.compareTo(inj);
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
	}

	public Integer getMaxResult() {
		return maxResult;
	}

	public ListadoControler(Set<T> elementos){
		List<T> aux = new ArrayList<T>();
		for (T t : elementos) {
			aux.add(t);
		}
		this.elementos = aux;
		this.originals = this.elementos;
		this.firstResult = 0;
		this.maxResult = 10;
		restrictionExpressionStrings = new ArrayList<String>();
		restrictions = new ArrayList<GeneralRestriction>();
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
		return originals;
	}

	public void setElementos(List<T> elementos) {
		this.originals = elementos;
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
		refresh();

		if(order != null){
			repetir = true;
			setOrder(order);
			repetir = false;
			}
		

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
		refresh();
		if(firstResult >= elementos.size()){
			firstResult-= getMaxResults();
		}
		firstResult = firstResult < 0 ? 0 : firstResult;
		return firstResult != 0;
	}
	
	public boolean getNextExists(){
		refresh();
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
	
	public void setOrder(String order){
		if(order != null && !order.equals(this.order) || repetir){
			Object[] lista = elementos.toArray();
			for(int i = 0; i < elementos.size() - 1; i++){
				for(int j = i + 1; j < elementos.size(); j++){
					Comparable ini = (Comparable)value(lista[i], order);
					Comparable inj = (Comparable)value(lista[j], order);
					
					int comp = ini.compareTo(inj);
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

		
			//cambio para cdo el string a ordenar venga en mayusculas
			//porque no ordena correctamente
			//YURIEN LOPEZ HERNANDEZ 09/01/2012
			if(aux instanceof String)
				aux = ((String)aux).toLowerCase();

			if(!properties.contains(".")){
				return aux;
			}
			String sub = properties.substring(properties.indexOf(".") + 1);
			return value(aux, sub);
		}
		catch (Exception e) {
			String a = e.getMessage();
		}
		return null;
	}
	
	public void next(){
		refresh();
		setFirstResult(getNextFirstResult());
	}
	
	public void previous(){
		refresh();
		setFirstResult(getPreviousFirstResult());
	}
	
	public void first(){
		refresh();
		setFirstResult(0);
	}
	
	public void last(){
		refresh();
		setFirstResult(getLastFirstResult());
	}

	
	
	public List<T> getOriginals() {
		return originals;
	}

	
	
	public void setOriginals(List<T> originals) {
		this.originals = originals;
	}
	
}
