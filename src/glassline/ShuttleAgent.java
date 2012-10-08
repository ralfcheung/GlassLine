package glassline;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;

public class ShuttleAgent extends FactoryAgent implements TReceiver {
	public ShuttleAgent(int componentNumber) {
		super(componentNumber);
		
		// Register channel with the transducer
		Factory.transducer.register(this, TChannel.SHUTTLE);
	}
	
	boolean alertSent = false;
	
	public class MyGlass {
		Glass g;
		public boolean done = false;
		
		public MyGlass(Glass g) {
			this.g = g;
		}
	}
	public MyGlass currentGlass = null;
	
	// Messages
	/* (non-Javadoc)
	 * @see glassline.Cutter#msgHereIsGlass(glassline.Glass)
	 */
	@Override
	public void msgHereIsGlass(Glass g) {
		if (currentGlass != null) {
			System.out.println("ERROR: Inline components can't hold 2 pieces of glass!");
		}
		
		previousComponent.msgIHaveSpaceAvailable(false);
		currentGlass = new MyGlass(g);
	}
	
	public void msgReadyToRelease() {
		currentGlass.done = true;
		stateChanged();
	}

	/* (non-Javadoc)
	 * @see glassline.Cutter#pickAndExecuteAnAction()
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		// Rule 1
		if (!nextSpaceAvailable() && !alertSent /*&& currentGlass != null*/) {
			alertShuttleSpaceAvailable(false);
			return true;
		}
		if(nextSpaceAvailable() && alertSent) {
			alertShuttleSpaceAvailable(true);
			return true;
		}
		if (currentGlass != null && currentGlass.done && nextSpaceAvailable()) {
			releaseGlass();
			return true;
		}
		
		return false;
	}
	
	private void alertShuttleSpaceAvailable(boolean space) {
		Do("Telling shuttle gui space available is " + space);
		Object[] id = new Object[1];
		id[0] = componentNumber;
		if (!space) {
			alertSent = true;
			Factory.transducer.fireEvent(TChannel.SHUTTLE, TEvent.SHUTTLE_STOPPED, id);
		} else {
			alertSent = false;
			Factory.transducer.fireEvent(TChannel.SHUTTLE, TEvent.SHUTTLE_CAN_START, id);
		}
	}

	private void releaseGlass() {
		print("passing " + currentGlass.g + " to " + nextComponent.component);
		nextComponent.component.msgHereIsGlass(currentGlass.g);
		currentGlass = null;
		previousComponent.msgIHaveSpaceAvailable(true);
	}
	
	// Transducer event listener.
	/* (non-Javadoc)
	 * @see glassline.Cutter#eventFired(transducer.TChannel, transducer.TEvent, java.lang.Object[])
	 */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.SHUTTLE) {
			if (event == TEvent.SHUTTLE_GLASS_RELEASED) {
				if ((Integer)args[0] == componentNumber) {
					this.msgReadyToRelease();
				}
			}
		}
	}
	
	public String toString() {
		return "Shuttle " + componentNumber;
	}
}
