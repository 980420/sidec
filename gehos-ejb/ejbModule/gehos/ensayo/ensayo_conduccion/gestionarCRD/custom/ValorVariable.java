package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

public class ValorVariable extends Object{
	long id;
	Object valor;
	Object[] valores;
	boolean eliminado;
	boolean yaesta;
	
	public ValorVariable(){
	
	}
	
	public ValorVariable(long id) {
		super();
		this.id = id;
		this.eliminado = false;
		this.yaesta = false;
	}
	
	public ValorVariable(Object valor, Object[] valores) {
		super();
		this.valor = valor;
		this.valores = valores;
		this.eliminado = false;
		this.yaesta = false;
	}
	
	public ValorVariable(long id, Object valor, Object[] valores) {
		super();
		this.id = id;
		this.valor = valor;
		this.valores = valores;
		this.eliminado = false;
		this.yaesta = false;
	}

	public Object[] getValores() {
		return valores;
	}

	public void setValores(Object[] valores) {
		this.valores = valores;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public long getId() {
		return id;
	}
	
	

	public void setId(long id) {
		this.id = id;
	}

	public boolean isEliminado() {
		return eliminado;
	}

	public boolean isYaesta() {
		return yaesta;
	}

	public void setYaesta(boolean yaesta) {
		this.yaesta = yaesta;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}
	
}
