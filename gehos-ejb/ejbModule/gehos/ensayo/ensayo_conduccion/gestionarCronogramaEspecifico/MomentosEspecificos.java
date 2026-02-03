package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import java.sql.Date;
import java.util.List;

import gehos.ensayo.entity.Estado_ensayo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("momentosEspecificos")
@Scope(ScopeType.CONVERSATION)
public class MomentosEspecificos {
	
	private String nombreMomento;
	private String fecha;
	private String etapa;
	
	
	public MomentosEspecificos(String nombreMomento, String fecha, String etapa)
	{
		super();
		this.nombreMomento=nombreMomento;
		this.fecha= fecha;
		this.etapa=etapa;
		
	}
	
	public MomentosEspecificos()
	{
		super();
	}

	public String getNombreMomento() {
		return nombreMomento;
	}

	public void setNombreMomento(String nombreMomento) {
		this.nombreMomento = nombreMomento;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

}
