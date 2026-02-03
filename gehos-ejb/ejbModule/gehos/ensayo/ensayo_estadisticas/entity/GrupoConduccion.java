package gehos.ensayo.ensayo_estadisticas.entity;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;


@Scope(ScopeType.CONVERSATION)
public class GrupoConduccion {
	
	private String nombre;
	private List<Sujeto> sujetos;
	 
	public GrupoConduccion(String nombre, List<Sujeto> sujetos)
	{
		super();
		this.nombre=nombre;
		this.sujetos=sujetos;
	}
	
	public GrupoConduccion()
	{
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Sujeto> getSujetos() {
		return sujetos;
	}

	public void setSujetos(List<Sujeto> sujetos) {
		this.sujetos = sujetos;
	}
	
	
	
	


}
