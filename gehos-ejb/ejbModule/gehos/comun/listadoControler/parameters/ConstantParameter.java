/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

/**
 *
 * @author Gerardo
 */
public class ConstantParameter extends Parameter {

    @Override
    public Object getValue(Object obj) {
        if(getParameter().startsWith("'")){
            String a = getParameter();
            a = a.replaceAll("'", "");
            return a;
        }
        return getParameter();
    }

}
