/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.restrictions;

/**
 *
 * @author Gerardo
 */
public abstract class Restriction {
    private String restiction;

    /**
     * @return the restiction
     */
    public String getRestiction() {
        return restiction;
    }

    /**
     * @param restiction the restiction to set
     */
    public void setRestiction(String restiction) {
        this.restiction = restiction;
    }

    public abstract boolean valid(Object obj);
    
}
