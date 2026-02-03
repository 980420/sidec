/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

/**
 *
 * @author Gerardo
 */
public abstract class Parameter {
    private String parameter;

    /**
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public abstract Object getValue(Object obj);
}
