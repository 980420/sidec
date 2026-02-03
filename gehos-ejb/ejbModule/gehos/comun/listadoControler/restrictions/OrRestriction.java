/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.restrictions;

/**
 *
 * @author Gerardo
 */
public class OrRestriction extends Restriction {

    private Restriction left;
    private Restriction right;

    @Override
    public boolean valid(Object obj) {
        return getLeft().valid(obj) || getRight().valid(obj);
    }

    /**
     * @return the left
     */
    public Restriction getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(Restriction left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public Restriction getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(Restriction right) {
        this.right = right;
    }

}
