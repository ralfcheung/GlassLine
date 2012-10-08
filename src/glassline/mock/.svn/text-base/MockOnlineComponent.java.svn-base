package glassline.mock;

import glassline.Glass;
import glassline.interfaces.FactoryComponent;
import glassline.interfaces.OnlineComponent;

public class MockOnlineComponent implements OnlineComponent, FactoryComponent {

	EventLog log = new EventLog();
	
	public void msgHereIsGlass(Glass g) {
		log.add(new LoggedEvent(
					"Received msgHereIsGlass containg " + g));
	}

	@Override
	public void msgIHaveSpaceAvailable(boolean spaceAvailable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgDone() {
		// TODO Auto-generated method stub

	}

}
