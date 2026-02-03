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
public class EqualsRestrinction extends Restriction {
    private Parameter left;
    private Parameter rigth;

    @Override
    public boolean valid(Object obj) {
        return left.getValue(obj).equals(rigth.getValue(obj));
    }

    /**
     * @return the left
     */
    public Parameter getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(Parameter left) {
        this.left = left;
    }

    /**
     * @return the rigth
     */
    public Parameter getRigth() {
        return rigth;
    }

    /**
     * @param rigth the rigth to set
     */
    public void setRigth(Parameter rigth) {
        this.rigth = rigth;
    }
}
