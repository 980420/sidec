package gehos.pki.servidores.proxy;


import org.jboss.seam.annotations.Name;

@Name("seleccionaProxy")
public class SeleccionaProxy {
	
	private String from;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
}
