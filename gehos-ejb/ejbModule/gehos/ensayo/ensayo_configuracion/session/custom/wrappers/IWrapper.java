package gehos.ensayo.ensayo_configuracion.session.custom.wrappers;

public interface IWrapper {
	public boolean isExpanded();
	public void setExpanded(boolean expanded);
	public Long getId();
	public Object getValue();
	public void setValue(Object find);
}