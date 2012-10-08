package glassline.mock;
import glassline.Glass;
import glassline.interfaces.*;

public class MockPopup implements Popup
{
	public EventLog log = new EventLog();


	public void msgHereIsGlass(Glass glass) {
		log.add(new LoggedEvent("recieved msgHereIsGlass from conveyor"));
		
	}

	@Override
	public void msgHereIsGlass(Glass glass, Workstation workstation) {
		log.add(new LoggedEvent("recieved msgHereIsGlass from workstation"));
		
	}

	@Override
	public void msgComeUp(Workstation w) {
		log.add(new LoggedEvent("recieved msgComeUp"));
		
	}

	@Override
	public void addWorkstation(Workstation w) {
		
	}

	@Override
	public void msgIHaveSpaceAvailable(boolean spaceAvailable) {
		// TODO Auto-generated method stub
		
	}

}
