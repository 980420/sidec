/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.restrictions;

import gehos.comun.listadoControler.parameters.Parameter;

/**
 *
 * @author Gerardo
 */
public class NotRestriction extends Restriction {
    private Parameter right;

    @Override
    public boolean valid(Object obj) {
        Boolean bool = (Boolean)right.getValue(obj);
        return !bool;
    }


}
