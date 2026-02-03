/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

import java.lang.reflect.Method;

/**
 *
 * @author Gerardo
 */
public class ObjectParameter extends Parameter {

    @Override
    public Object getValue(Object obj) {
        return value(obj, getParameter());
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
			String a = e.getMessage();
		}
		return null;
	}

}
