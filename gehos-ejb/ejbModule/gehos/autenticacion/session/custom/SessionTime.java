package gehos.autenticacion.session.custom;

import java.util.Calendar;
import java.util.Date;

public class SessionTime implements Comparable {
	
	private String id;
	private Date date;
	
	public SessionTime(){
		
	}
	
	public SessionTime(String id){
		this.id = id;
		date = Calendar.getInstance().getTime();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public int compareTo(Object o) {
		if(o instanceof SessionTime){
			return id.equals(((SessionTime)o).getId()) ?
					0 : 1;
		}
		else if(o instanceof String){
			return id.equals(o) ? 0 : 1;
		}
		else{
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object o){
		return compareTo(o) == 0;
	}
	
}
