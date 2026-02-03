/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

/**
 *
 * @author Gerardo
 */
public class LowerParameter extends Parameter {

    private Parameter right;

    @Override
    public Object getValue(Object obj) {
        String r = getRight().getValue(obj).toString();
        return r.toLowerCase();
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
