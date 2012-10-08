package glassline.mock;
import glassline.Glass;
import glassline.Glass.WorkStation;
import glassline.interfaces.Workstation;

public class MockWorkstation implements Workstation{
	
	public String name;
	public boolean busy = false;

	public EventLog log = new EventLog();
	Glass.WorkStation type;
	
	public String toString(){
		return name;
	}
	
	@Override
	public void msgHereIsGlass(Glass g) {
		log.add(new LoggedEvent("recieved msgHereIsGlass"));
	}

	@Override
	public void msgIAmUp() {
		log.add(new LoggedEvent("recieved msgIAmUp"));
		
	}

	@Override
	public WorkStation getType() {
		return type;
	}
	
	public MockWorkstation(Glass.WorkStation ty, String name){
		this.name = name;
		type = ty;
	}

	@Override
	public void msgIAmBusy(boolean b) {
		busy = b;
	}
	
}
