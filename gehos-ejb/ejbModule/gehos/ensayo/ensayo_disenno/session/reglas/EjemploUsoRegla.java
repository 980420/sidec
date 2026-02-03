package gehos.ensayo.ensayo_disenno.session.reglas;

import gehos.ensayo.entity.Variable_ensayo;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;

public class EjemploUsoRegla 
{
	@In 
	ReglasControlador reglasControlador;
	
	@In
	EntityManager entityManager;
	
	public void evaluar(long idVariable)
	{
		Variable_ensayo v = entityManager.find(Variable_ensayo.class, idVariable);
		v.getReglas();
	}
}
