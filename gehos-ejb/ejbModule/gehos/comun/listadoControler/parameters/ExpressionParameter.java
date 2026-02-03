/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

import org.jboss.seam.core.Expressions;

/**
 *
 * @author Gerardo
 */
public class ExpressionParameter extends Parameter {

    @Override
    public Object getValue(Object obj) {
        return Expressions.instance().createValueExpression(getParameter()).getValue();
    }

}
