package gehos.ensayo.ensayo_estadisticas.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;
/**
 * This class provides methods to help query database and 
 * also serves to reuse code.
 * 
 * 
 * @author Yoandry González Castro
 *
 */
@Name("easyQuery")
@AutoCreate
public class EasyQuery {
	
	@In EntityManager entityManager;
	/**
	 * @param Class property of the given Class representing a database table
	 * @return A list containing all the objects (rows) on the given class (table)
	 */
	public <T> List<T> findAll(Class<T> clas)
	{
		String query = "select t from %s t";
		query = String.format(query, clas.getSimpleName());
		return (List<T>)entityManager.createQuery(query).getResultList();
	}
	
	/**
	 * Selects value specified by fieldName parameter of the entity related to clas parameter
	 * @param Class property of the given Class representing a database table
	 * @return A list of values
	 */
	public <T> List<T> selectField(Class clas, String fieldName, Class<T> fieldType)
	{
		String query = "select t.%s from %s t";
		query = String.format(query, fieldName, clas.getSimpleName());
		return (List<T>)entityManager.createQuery(query).getResultList();
	}
	
	/**
	 * @param clas property of the given Class representing a database table
	 * @param orderField
	 * @param orderType
	 * @return A list containing all the objects (rows) on the given class (table)
	 */
	public <T> List<T> findAll(Class<T> clas, String orderField, String orderType)
	{
		String query = "select t from %s t order by t.%s %s";
		query = String.format(query, clas.getSimpleName(), orderField, orderType);
		return (List<T>)entityManager.createQuery(query).getResultList();
	}
	
	/**
	 * @param Class property of the given Class representing a database nomenclature
	 * @return A list containing all the objects (rows) on the given class (table)
	 */
	public List findAll(Class clas, Map filters)
	{
		String query = "select t from %s t where";
		query = String.format(query, clas.getSimpleName());	
		List<String> keys = new ArrayList<String>(filters.keySet());
		for (int i = 0; i < keys.size(); i++) {
			String mapKey= keys.get(i);			
			query+=" t."+mapKey+"=?"; 
			if(i<keys.size()-1)
				query+=" and";
		}
		
		Query qq = entityManager.createQuery(query);
		for (int i = 1; i <= keys.size(); i++) {
			qq.setParameter(i, filters.get(keys.get(i-1)));
		}	
		
		return (List) qq.getResultList();
	}
	
	/**
	 * @param Class property of the given Class representing a database nomenclature
	 * @return A list containing all the objects (rows) on the given class (table)
	 * @throws JSONException 
	 */
	public List findAll(Class clas, JSONObject filters)
	{
		try {
		String query = "select t from %s t where";
		query = String.format(query, clas.getSimpleName());	
		List<String> keys = new ArrayList<String>();
		Iterator<String> keysIterator = filters.keys();
		while(keysIterator.hasNext())
			keys.add(keysIterator.next().toString());
		
		for (int i = 0; i < keys.size(); i++) {
			String mapKey= keys.get(i);			
			query+=" t."+mapKey+"=?"; 
			if(i<keys.size()-1)
				query+=" and";
		}
		
		Query qq = entityManager.createQuery(query);
		for (int i = 1; i <= keys.size(); i++) 			
				qq.setParameter(i, filters.get(keys.get(i-1)));
		
		return (List) qq.getResultList();
		
		} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return null;
			}
			
		
		
	}
	
	public List findAll(Class clas, String filters) throws JSONException
	{
		filters = "{"+filters+"}";
		return findAll(clas, new JSONObject(filters));	
		
	}
	public  <T> T findOne(Class<T> clas, String filters)
	{
		try 
		{
			filters = "{"+filters+"}";
			JSONObject jsonObject = new JSONObject(filters);	
			String query = "select t from %s t where";
			query = String.format(query, clas.getSimpleName());	
			List<String> keys = new ArrayList<String>();
			Iterator<String> keysIterator = jsonObject.keys();
			while(keysIterator.hasNext())
				keys.add(keysIterator.next());
			
			for (int i = 0; i < keys.size(); i++) {
				String mapKey= keys.get(i);			
				query+=" t."+mapKey+"=?"; 
				if(i<keys.size()-1)
					query+=" and";
			}
			
			Query qq = entityManager.createQuery(query);
			for (int i = 1; i <= keys.size(); i++) 			
					qq.setParameter(i, jsonObject.get(keys.get(i-1)));
			
			return  (T) qq.getSingleResult();
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public  <T>T findOne(Class<T> clas, String field, Object value)
	{
		 
			String query = "select t from %s t where %s=:param";
			query = String.format(query, clas.getSimpleName(),"t."+field);			
			Query qq = entityManager.createQuery(query);
			qq.setParameter("param", value);
			return  (T) qq.getSingleResult();
		
	}
	
	
	
}
