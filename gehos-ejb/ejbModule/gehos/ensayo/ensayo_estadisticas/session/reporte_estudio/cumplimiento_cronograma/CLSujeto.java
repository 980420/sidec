package gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma;

import gehos.ensayo.entity.Sujeto_ensayo;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

@Name("clSujeto")
@Scope(ScopeType.CONVERSATION)
public class CLSujeto extends EntityQuery<Sujeto_ensayo> 
{
	private static final String EJBQL = "select sujeto from Sujeto_ensayo sujeto";

	private static final String[] RESTRICTIONS = {
			"lower(sujeto.nombre) like concat(lower(#{clSujeto.nombre}),'%')",
			"lower(sujeto.codigoPaciente) like concat(lower(#{clSujeto.codigo}),'%')",
			"lower(sujeto.inicialesCentro) like concat(lower(#{clSujeto.inicialesCentro}),'%')",
			"lower(sujeto.inicialesPaciente) like concat(lower(#{clSujeto.inicialesPaciente}),'%')", };


	String  codigo,
			nombre,
			inicialesCentro,
			inicialesPaciente;
	
	private int pagina;
	
	public CLSujeto() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}
	

	public boolean Buscar()
	{
		setFirstResult(0);
		return true;
	}
	
	public void Listar()
	{
		codigo = nombre = null;
		
	}
	
	public int getPagina() {
		if(this.getNextFirstResult() != 0)
			return this.getNextFirstResult()/10;
			else
				return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
		
		long num=(getResultCount()/10)+1;
		if(this.pagina>0){
		if(getResultCount()%10!=0){
			if(pagina<=num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		else{
			if(pagina<num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		}
	}


	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getInicialesCentro() {
		return inicialesCentro;
	}


	public void setInicialesCentro(String inicialesCentro) {
		this.inicialesCentro = inicialesCentro;
	}


	public String getInicialesPaciente() {
		return inicialesPaciente;
	}


	public void setInicialesPaciente(String inicialesPaciente) {
		this.inicialesPaciente = inicialesPaciente;
	}
	
}

