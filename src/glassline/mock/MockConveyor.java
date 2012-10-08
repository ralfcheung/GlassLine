package glassline.mock;

import glassline.Glass;
import glassline.interfaces.Conveyor;
import glassline.interfaces.FactoryComponent;
import glassline.interfaces.Sensor;

public class MockConveyor implements Conveyor, FactoryComponent {
	public EventLog log = new EventLog();

	@Override
	public void msgHereIsGlass(Glass g) {
		log.add(new LoggedEvent("" +
				"Received msgHereIsGlass with glass " + g));
	}

	@Override
	public void msgSensedGlass(Sensor sensor, boolean sensing) {
		// Mock conveyors don't receive sensor input
	}

	@Override
	public void msgIHaveSpaceAvailable(boolean spaceAvailable) {

	}
	
	
}
