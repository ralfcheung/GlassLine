package glassline.mock;

import glassline.Glass;
import glassline.interfaces.FactoryComponent;

public class MockFactoryComponent implements FactoryComponent {

	public EventLog log = new EventLog();
	
	public void msgHereIsGlass(Glass g) {
		log.add(new LoggedEvent(
				"Received msgHereIsGlass containg " + g));
	}

	@Override
	public void msgIHaveSpaceAvailable(boolean spaceAvailable) {
		log.add(new LoggedEvent(
					"Received msgIHaveSpaceAvailable and spaceAvailable is " + spaceAvailable));
	}

}
