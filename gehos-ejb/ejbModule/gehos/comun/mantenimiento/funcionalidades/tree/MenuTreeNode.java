package gehos.comun.mantenimiento.funcionalidades.tree;




import javax.persistence.EntityManager;

import org.jboss.seam.Component;



public class MenuTreeNode {
	private Long id, id_funcionalidad_padre, old_id;
	private String label,imagen="";
	private boolean is_modulo_fisico, is_modulo;
	
	public MenuTreeNode(long id, long id_funcionalidad_padre, String label,
			boolean es_modulo, boolean is_modulo_fisico) {
		super();
		this.id = id;
		this.id_funcionalidad_padre = id_funcionalidad_padre;
		this.label = label;
		this.old_id = id;
		this.is_modulo_fisico = is_modulo_fisico;
		this.is_modulo = es_modulo;
		
	}

	public MenuTreeNode() {

	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getOld_id() {
		return old_id;
	}

	public void setOld_id(long old_id) {
		this.old_id = old_id;
	}

	public boolean Is_modulo() {
		return is_modulo;
	}

	public void setIs_modulo(boolean is_modulo) {
		this.is_modulo = is_modulo;
	}

	public Long getId_funcionalidad_padre() {
		return id_funcionalidad_padre;
	}

	public void setId_funcionalidad_padre(long id_funcionalidad_padre) {
		this.id_funcionalidad_padre = id_funcionalidad_padre;
	}

	public boolean Is_modulo_fisico() {
		return is_modulo_fisico;
	}

	public void setIs_modulo_fisico(boolean is_modulo_fisico) {
		this.is_modulo_fisico = is_modulo_fisico;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public void persistId(){
		
		EntityManager entityManager=(EntityManager)Component.getInstance("entityManager", true);
		entityManager.createQuery("update Funcionalidad f set f.id = "+this.id+" where f.id = "+this.old_id)
		.executeUpdate();
		entityManager.flush();
	}
	public void persistId_funcionalidad_padre(){
		
		EntityManager entityManager=(EntityManager)Component.getInstance("entityManager", true);
		entityManager.createQuery("update Funcionalidad f set f.funcionalidadPadre.id = "+this.id_funcionalidad_padre+" where f.id = "+this.old_id)
		.executeUpdate();
		entityManager.flush();
		
	}

}
