/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.restrictions;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;
import gehos.comun.listadoControler.parameters.Parameter;

/**
 *
 * @author Gerardo
 */
public class GeneralRestriction {
    private Restriction baseRestriction;
    private Parameter expressionParameter;

    /**
     * @return the baseRestriction
     */
    public Restriction getBaseRestriction() {
        return baseRestriction;
    }

    /**
     * @param baseRestriction the baseRestriction to set
     */
    public void setBaseRestriction(Restriction baseRestriction) {
        this.baseRestriction = baseRestriction;
    }

    /**
     * @return the expressionParameter
     */
    public Parameter getExpressionParameter() {
        return expressionParameter;
    }

    /**
     * @param expressionParameter the expressionParameter to set
     */
    public void setExpressionParameter(Parameter expressionParameter) {
        this.expressionParameter = expressionParameter;
    }

    
}
