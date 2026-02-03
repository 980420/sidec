package gehos.ensayo.session.comunes;

import org.richfaces.model.CalendarDataModelItem;

public class FechaDataModelItem implements CalendarDataModelItem {

	private Object data;
	private String styleClass;
	private Object toolTip;
	private int day;
	private boolean enabled = true;
	
	public Object getData() {
		return this.data;
	}

	public int getDay() {
		return this.day;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public Object getToolTip() {
		return this.toolTip;
	}

	public boolean hasToolTip() {
		return (this.toolTip != null);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setToolTip(Object toolTip) {
		this.toolTip = toolTip;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
