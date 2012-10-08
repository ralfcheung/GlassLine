package glassline;

import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.interfaces.Robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;

public class RobotAgent extends FactoryAgent implements Robot, TReceiver {
	public List<Glass> glassQueue = Collections.synchronizedList(new ArrayList<Glass>());
	static int glassCount = 1;
	
	public boolean transportingGlass = false;
	Semaphore partCreated = new Semaphore(0);
	
	public RobotAgent() {
		super(0);
		Factory.transducer.register(this, TChannel.BIN);
	}
	
	// Messages

	/* (non-Javadoc)
	 * @see glassline.Robot#msgHereIsGlass(glassline.Glass)
	 */
	public void msgHereIsGlass(Glass g) {} // Not needed.

	/* (non-Javadoc)
	 * @see glassline.Robot#msgGetGlassFromBin()
	 */
	public void msgGetGlassFromBin(GlassShape shape, ArrayList<WorkStation> stations) {
		//print("Received order to move glass to the conveyor.");
		
		glassQueue.add(new Glass(shape, stations, glassCount++, Factory.transducer));
		updateControlPanelQueueSize();
		
		stateChanged();
	}
	
	// Scheduler

	/* (non-Javadoc)
	 * @see glassline.Cutter#pickAndExecuteAnAction()
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		// Rule #1
		if (!glassQueue.isEmpty() && nextSpaceAvailable()) {
			createGlass();
			return true;
		}
		return false;
	}

	// Actions
	
	private void createGlass() {
		Object[] args = new Object[1];
		args[0] = (Integer)(glassQueue.get(0).glassNumber);
		Factory.transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, args);
		try {
			partCreated.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nextComponent.component.msgHereIsGlass(glassQueue.get(0));
		glassQueue.remove(0);
		updateControlPanelQueueSize();
		stateChanged();
	}
	
	// Utilities
	
	private void updateControlPanelQueueSize() {
		// Tell the control panel to update the queue.
		Object[] args = {glassQueue.size()};
		Factory.transducer.fireEvent(TChannel.CONTROL_PANEL, TEvent.GLASS_QUEUE_UPDATED, args);
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.BIN) {
			if (event == TEvent.BIN_PART_CREATED) {
				partCreated.release();
			}
		}
		
	}
	
	public String toString() {
		return "Robot";
	}
}
