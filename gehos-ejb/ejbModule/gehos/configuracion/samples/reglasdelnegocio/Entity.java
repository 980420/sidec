package gehos.configuracion.samples.reglasdelnegocio;

public class Entity {
	private String name;
	private ChildEntity childEntity;

	public ChildEntity getChildEntity() {
		return childEntity;
	}

	public void setChildEntity(ChildEntity childEntity) {
		this.childEntity = childEntity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Entity(String name, ChildEntity childEntity) {
		super();
		this.name = name;
		this.childEntity = childEntity;
	}
}
