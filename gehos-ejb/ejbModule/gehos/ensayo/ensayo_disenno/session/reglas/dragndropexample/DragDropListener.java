package gehos.ensayo.ensayo_disenno.session.reglas.dragndropexample;
/*package gehos.ensayo.ensayo_estadisticas.session.reglas.dragndropexample;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.richfaces.component.Dropzone;
import org.richfaces.event.DropEvent;
import org.richfaces.event.DropListener;

@Name("dragDropEventBean")
public class DragDropListener implements DropListener
{
	
	@In
    private DragDropExample dragDropBean; 
 
    public void setDragDropBean(DragDropExample dragDropBean) { 
        this.dragDropBean = dragDropBean; 
    } 
 
    public void processDrop(DropEvent event) { 
        dragDropBean.moveFramework((Framework) event.getDragValue()); 
    } 
	//Other variable declarations....
	private List<Component> sourceComponents = new ArrayList<Component>();
	private List<List<Component>> dropGridComponents = new ArrayList<List<Component>>();
	 
	//Getter for dropGridComponents
	public List<List<Component>> getdropGridComponents() 
	{
		if(dropGridComponents.isEmpty()) 
		{
			//Create a set of 3 drop-zones
			while(dropGridComponents.size() < 3)
			{
				dropGridComponents.add(new ArrayList<Component>());
			}
		}
	return dropGridComponents;
	}
	//Setter for dropGridComponents
	public void setdropGridComponents(List<List<Component>> dropGridComponents) {
	this.dropGridComponents = dropGridComponents;
	}
	//Getter for sourceComponents
	public List<Component> getSourceComponents() {
	if(sourceComponents.isEmpty()){
	sourceComponents = getAllDraggableComponents();
	}
	return sourceComponents;
	}
	//Setter for sourceComponents
	public void setSourceComponents(List<Component> sourceComponents) {
	this.sourceComponents = sourceComponents;
	}
	 
	public List<Component> getAllDraggableComponents() 
	{
		String query = "select component from Component component";
		List<Component> components = entityManager.createQuery(query).getResultList();
		return components;
	}
	 
	
	* Listener method called when any draggable component is droppped in a panel
	* 
	public void processDrop(DropEvent dropEvent) 
	{
		Dropzone dropZone = (Dropzone) dropEvent.getComponent();
		Component component = (Component) dropEvent.getDragValue();
		moveComponent(component, dropZone.getDropValue());
	}
	 
	private void moveComponent(Component component, Object dropPlace)
	{
		Integer dropIndex = (Integer) dropPlace;
		List<Component> tempList = dropGridComponents.get(dropIndex);
		tempList.add(component);
		dropGridComponents.set(dropIndex, tempList);
		sourceComponents.remove(component);
	}
	 
	public String reset() 
	{
		sourceComponents.clear();
		dropGridComponents.clear();
		return null;
	}
	
}*/