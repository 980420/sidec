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
public class MayorEqualsRestriction extends Restriction {
    private Parameter left;
    private Parameter right;

    @Override
    public boolean valid(Object obj) {
        Comparable comp1 = (Comparable)getLeft().getValue(obj);
        Comparable comp2 = (Comparable)getRight().getValue(obj);
        return comp1.compareTo(comp2) < 0;
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
     * @return the right
     */
    public Parameter getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(Parameter right) {
        this.right = right;
    }
}
