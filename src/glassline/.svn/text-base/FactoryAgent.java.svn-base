package glassline;

import glassline.interfaces.FactoryComponent;
import agent.Agent;

public abstract class FactoryAgent extends Agent implements FactoryComponent {
	public boolean on = true;
	Integer componentNumber;
	
	FactoryComponent previousComponent;
	NextFactoryComponent nextComponent = null;
	
	class NextFactoryComponent{
		FactoryComponent component;
		boolean spaceAvailable = true; //true if there is space available for incoming glass, false otherwise
		
		public NextFactoryComponent(FactoryComponent component) {
			this.component = component;
		}
		
		public String toString() {
			return component.toString();
		}
	}
	
	FactoryAgent(int id) {
		componentNumber = id;
	}
	
	/* (non-Javadoc)
	 * @see glassline.IFactory#msgHereIsGlass(glassline.Glass)
	 */
	abstract public void msgHereIsGlass(Glass g);
	
	/* (non-Javadoc)
	 * @see glassline.IFactory#msgIHaveSpaceAvailable(boolean)
	 */
	public void msgIHaveSpaceAvailable(boolean spaceAvailable) {
		nextComponent.spaceAvailable = spaceAvailable;
		stateChanged();
	}
	
	// Setters
	
	public void setPreviousComponent(FactoryComponent component) {
		previousComponent = component;
	}
	
	public void setNextComponent(FactoryComponent component) {
		nextComponent = new NextFactoryComponent(component);
	}
	
	// Helpers
	
	public boolean nextSpaceAvailable() {
		if (nextComponent != null) {
			return nextComponent.spaceAvailable;
		}
		return true;
	}
}
