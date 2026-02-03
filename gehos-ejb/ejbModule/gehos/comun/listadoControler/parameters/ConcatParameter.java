/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

/**
 *
 * @author Gerardo
 */
public class ConcatParameter extends Parameter {

    private Parameter left;
    private Parameter right;

    @Override
    public Object getValue(Object obj) {
        String l = getLeft().getValue(obj).toString();
        String r = getRight().getValue(obj).toString();
        return l + r;
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
