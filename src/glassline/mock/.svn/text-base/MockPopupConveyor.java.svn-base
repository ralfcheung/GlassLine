package glassline.mock;

import glassline.Glass;
import glassline.interfaces.PopupConveyor;
import glassline.interfaces.Sensor;

public class MockPopupConveyor implements PopupConveyor  {
	public EventLog log = new EventLog();

	@Override
	public void msgWorkstationsFull(boolean b) {
		log.add(new LoggedEvent("Received msgWorkstationsFull is " + b));
	}

	@Override
	public void msgHereIsGlass(Glass g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIHaveSpaceAvailable(boolean choice) {
		log.add(new LoggedEvent("Received msgIHaveSpaceAvailable: " + choice));		
	}

	@Override
	public void msgSensedGlass(Sensor sensor, boolean sensing) {
		// TODO Auto-generated method stub
		
	}

	

}
