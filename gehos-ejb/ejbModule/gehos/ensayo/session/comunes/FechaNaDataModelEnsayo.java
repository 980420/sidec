package gehos.ensayo.session.comunes;

import gehos.ensayo.session.comunes.FechaDataModelItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.model.CalendarDataModel;
import org.richfaces.model.CalendarDataModelItem;


/*----------------------------------
Cambiar el nombre por fechaDataModel_<modulo>
Agregar un estilo a la página 

.dateDisabled{color: red; text-decoration: line-through;}
------------------------------------*/
@Name("fechaNaDataModelEnsayo")
@Scope(ScopeType.PAGE)
public class FechaNaDataModelEnsayo implements CalendarDataModel{

	private int month;
	private Date d = new Date();
	
	@SuppressWarnings("static-access")
	public CalendarDataModelItem[] getData(Date[] arg0) {
		if(arg0 == null) return null;
		Calendar c = new GregorianCalendar();
		c.setTime(arg0[0]);
		month = c.get(Calendar.MONTH);		
		
		if(month == 2 && arg0.length == 30){
			Calendar c1 = new GregorianCalendar();
			c1.setTime(arg0[29]);
			c1.add(c.DATE, 1);
							
			Date[] dates1 = new Date[31];
			
			for (int i = 0; i < arg0.length; i++) {
				dates1[i] = arg0[i];
			}
			dates1[30] = c1.getTime();
				
			CalendarDataModelItem[] items = new CalendarDataModelItem[dates1.length];
			for(int i = 0; i < dates1.length; i++){
				items[i] = createDateModelItem(dates1[i]);
			}
			return items;
		}
		
		CalendarDataModelItem[] items = new CalendarDataModelItem[arg0.length];
		for(int i = 0; i < arg0.length; i++){
			items[i] = createDateModelItem(arg0[i]);
		}
		return items;
	}

	private CalendarDataModelItem createDateModelItem(Date date) {
		FechaDataModelItem item = new FechaDataModelItem();
		item.setEnabled(true);
				
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		if(df.format(d).equals(df.format(date)) || date.before(d)){
			item.setEnabled(false);
			item.setStyleClass("noavailable");													
		}				
		return item;
	}

	public Object getToolTip(Date arg0) {
		return null;
	}
	
}
