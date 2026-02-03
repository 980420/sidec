/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.compiler;

/**
 *
 * @author Gerardo
 */
public class Token {
    private String value;
    private TokenType type;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TokenType type) {
        this.type = type;
    }

    
}
