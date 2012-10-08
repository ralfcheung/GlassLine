package glassline.mock;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;

public class MockGUI implements TReceiver {

	public EventLog log = new EventLog();
	//private Transducer t;
	
	public MockGUI() {
		//this.t = t;
		//t.register(this, TChannel.CONVEYOR);
		//t.register(this, TChannel.BIN);
	}
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.CONVEYOR) {
			if (event == TEvent.CONVEYOR_DO_STOP) {
				log.add(new LoggedEvent(
								"Received event CONVEYOR_DO_STOP for conveyor " + args[0]));
			}
			if (event == TEvent.CONVEYOR_DO_START) {
				log.add(new LoggedEvent(
								"Received event CONVEYOR_DO_START for conveyor " + args[0]));
			}
		}
		else if (channel == TChannel.BIN) {
			if (event == TEvent.BIN_CREATE_PART) {
				log.add(new LoggedEvent(
								"Received event BIN_CREATE_PART for bin"));
			}
		}
		
		else if(channel == TChannel.POPUP){
			if(event == TEvent.POPUP_DO_MOVE_UP){
				log.add(new LoggedEvent(
								"Received event POPUP_DO_MOVE_UP for popup " + args[0]));
			}
			
			if(event == TEvent.POPUP_RELEASE_GLASS){
				log.add(new LoggedEvent(
								"Received event POPUP_RELEASE_GLASS for popup " + args[0]));
			}
			
			if(event == TEvent.POPUP_DO_MOVE_DOWN){
				log.add(new LoggedEvent(
								"Received event POPUP_DO_MOVE_DOWN for popup " + args[0]));
			}
		}
	}
}
