package gehos.comun.modulos.selector.model;

import java.util.ArrayList;
import java.util.List;

public class Module {

	private Long id;
	private Integer order;
	private boolean external;

	private String name, label, link, image, entityName, parentName, entityId;

	private boolean favorites, lock, children;

	private List<Module> childrenList;

	public Module() {
		// TODO Auto-generated constructor stub
	}

	public Module(boolean external, Long Long1, String name, String label,
			String link, boolean favorites, boolean lock, boolean children,
			String image, String entityName, String entityId, boolean dummy) {
		super();
		this.image = image;
		this.order = 0;
		this.id = Long1;
		this.name = name;
		this.label = label;
		this.link = link;
		this.favorites = favorites;
		this.lock = lock;
		this.children = children;
		childrenList = new ArrayList<Module>();
		this.entityName = entityName;
		this.entityId = entityId;
		this.external = external;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isChildren() {
		return children;
	}

	public void setChildren(boolean children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isFavorites() {
		return favorites;
	}

	public void setFavorites(boolean favorites) {
		this.favorites = favorites;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<Module> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<Module> childrenList) {
		this.childrenList = childrenList;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

}
