package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.ensayo.entity.Entidad_ensayo;

import java.util.Arrays;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;

@SuppressWarnings("serial")
public class EntidadEnsayoBuscarControler extends EntityQuery<Entidad_ensayo> {

	private static final String EJBQL = "select entidad from Entidad_ensayo entidad ";

	private static final String[] RESTRICTIONS = { "lower(entidad.nombre) like concat(lower(#{entidadBuscarControler.nombre.trim()}),'%')" };


	private Entidad_ensayo entidad = new Entidad_ensayo();
	private String nombre = "";	
	private Long entidadId = -1l;

	
	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	public EntidadEnsayoBuscarControler() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("entidad.id desc");
	}


	public void seleccionar(Long entidadId) {
		this.entidadId = entidadId;
	}

	public void buscar() {
		setFirstResult(0);	
	}

	public void cancelar() {
		this.nombre = "";
	}

	public String getNombre() {
		return nombre;
	}
		

	public Entidad_ensayo getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_ensayo entidad) {
		this.entidad = entidad;
	}

	public Long getEntidadId() {
		return entidadId;
	}

	public void setEntidadId(Long entidadId) {
		this.entidadId = entidadId;
	}

}
