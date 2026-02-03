/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.restrictions;

/**
 *
 * @author Gerardo
 */
public class AndRestriction extends Restriction {

    private Restriction left;
    private Restriction rigth;

    @Override
    public boolean valid(Object obj) {
       return getLeft().valid(obj) && getRigth().valid(obj);
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
     * @return the rigth
     */
    public Restriction getRigth() {
        return rigth;
    }

    /**
     * @param rigth the rigth to set
     */
    public void setRigth(Restriction rigth) {
        this.rigth = rigth;
    }



}
