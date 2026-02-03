/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.parameters;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Gerardo
 */
public class CurrentDateParameter extends Parameter {

    @Override
    public Object getValue(Object obj) {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

}
