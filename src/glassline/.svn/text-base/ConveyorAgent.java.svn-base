package glassline;

import glassline.interfaces.Conveyor;
import glassline.interfaces.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;

public class ConveyorAgent extends FactoryAgent implements Conveyor, TReceiver {

	boolean manualOverride = false;
	public boolean sentAlertGlassBlockingEntry = false;
	
	public class MyGlass
	{
		public Glass glass;
		public boolean onBeginningSensor = true;
		public MyGlass(Glass glas)
		{
			glass = glas;
		}
	}
	
	public List<MyGlass> myGlass = Collections.synchronizedList(new ArrayList<MyGlass>());
	
	
	public class ConveyorSensor {
		public Sensor sensor; //All instances related to the Sensor should use the interface, not the agent, for proper unit testing
		public boolean isSensing = false;
		
		public ConveyorSensor(Sensor sensor) {
			this.sensor = sensor;
		}
	}
	
	ConveyorSensor beginningSensor = null;
	ConveyorSensor endSensor = null;
	public List<ConveyorSensor> sensorHits = Collections.synchronizedList(new ArrayList<ConveyorSensor>()); // List of unprocessed sensor state changes;
	
	public ConveyorAgent(int conveyorID) {
		super(conveyorID);
		Factory.transducer.register(this, TChannel.CONVEYOR);
		start();
	}
	
	//Messages
	/* (non-Javadoc)
	 * @see glassline.Conveyor#msgSensedGlass(sun.management.Sensor, boolean)
	 */
	public void msgSensedGlass(Sensor sensor, boolean sensing) {
		//print("received message that " + sensor + " sensing is " + sensing);
		
		ConveyorSensor sender = null;
		if (beginningSensor.sensor == sensor) {
			sender = beginningSensor;
		} else if (endSensor.sensor == sensor) {
			sender = endSensor;
		} else return;
		
		sender.isSensing = sensing;
		
		if(sender == beginningSensor && !sender.isSensing) {
			synchronized(myGlass) {
				for (int i = 0; i < myGlass.size(); i++) {
					if (myGlass.get(i).onBeginningSensor) {
						myGlass.get(i).onBeginningSensor = false;
						break;
					}
				}
			}
		}
		
		sensorHits.add(sender);
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see glassline.Conveyor#msgHereIsGlass(glassline.Glass)
	 */
	public void msgHereIsGlass(Glass g) {
		//print(" Received " + g);
		myGlass.add(new MyGlass(g));
		stateChanged();
	}
	
	//scheduler
	public boolean pickAndExecuteAnAction() {
		
		// Rule: Prevents collisions by moving a glass onto a stopped conveyor with glass at the beginning.
		if (on && !nextSpaceAvailable()) {
			if (endSensor.isSensing) {
				stop();
				return true;
			}
		}
		
		// Rule: Restarts conveyor if nothing is blocking the glass.
		if (!on && (nextSpaceAvailable() || !endSensor.isSensing)) {
			start();
			return true;
		}
		
		// Rule: Process a sensor state change.
		if (!sensorHits.isEmpty()) {
			ConveyorSensor sensor = sensorHits.remove(0);
				if (sensor == endSensor) {
					if (myGlass.isEmpty()) {
						Do("Error: The end sensor was tripped on conveyor " + componentNumber + " with an empty myGlass list");
					}
					
					if (sensor.isSensing) { // Glass just triggered sensor;
						return true;
					}
					else { // Glass just left sensor
						if (!on) {
							Do("Error: glass left last sensor but I'm off!");
						}
						return true;
					}
				}
			if (sensor == beginningSensor) {
				if (sensor.isSensing) {// Glass just triggered sensor
					
						return true;
				}
				else { // Glass just left sensor
					if(!on)
					{
						print("beggining glass sensor tripped but I am off");
					}
					if (sentAlertGlassBlockingEntry) {
						Do("telling " + previousComponent + " i have space available");
						sentAlertGlassBlockingEntry = false;
						previousComponent.msgIHaveSpaceAvailable(true);
					}
					return true;
				}
			}
		}
		synchronized (myGlass) {
			for (MyGlass g : myGlass) {
				if (!on && g.onBeginningSensor && !sentAlertGlassBlockingEntry) {
					alertPreviousComponent();
					return true;
				}
			}
		}
		return false;
	}
	
	//Actions
	protected void passGlassForward() {
		Glass g = myGlass.get(0).glass;
		Do("passing " + g + " to " + nextComponent.component);
		if (nextComponent != null) {
			nextComponent.component.msgHereIsGlass(g);
		}
		myGlass.remove(0);
		stateChanged();
	}
	
	protected void alertPreviousComponent() {
		Do("telling " + previousComponent + " there is no space available");
		sentAlertGlassBlockingEntry = true;
		previousComponent.msgIHaveSpaceAvailable(false);
		stateChanged();
	}
	
	protected void start() {
		on = true;
		/*
		if (sentAlertGlassBlockingEntry) {
			previousComponent.msgIHaveSpaceAvailable(true);
		}
		sentAlertGlassBlockingEntry = false;*/
		Do("Start conveyor");
		if (endSensor != null) {
			endSensor.sensor.msgIAmOn(true);
		}
		Object[] args = {componentNumber};
		Factory.transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		stateChanged();
	}
	
	protected void stop() {
		on = false;
		Do("Stop Conveyor");
		endSensor.sensor.msgIAmOn(false);
		// Tell the previous component that there is no space for a glass to move onto this conveyor.
		// This handles the case where the conveyor is stopped with glass on the beginning sensor.
		synchronized(myGlass){
			for(MyGlass g : myGlass)
			{
				if (beginningSensor.isSensing && g.onBeginningSensor) {
					Do("telling " + previousComponent + " there is no space available");
					sentAlertGlassBlockingEntry = true;
					previousComponent.msgIHaveSpaceAvailable(false);
					break;
				}
			}
		}
		// Tell GUI to stop
		Object[] args = {componentNumber};
		Factory.transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		stateChanged();
	}
	
	//Utilities	
	public void setBeginningSensor(Sensor sensor) {
		beginningSensor = new ConveyorSensor(sensor);
	}
	
	public void setEndSensor(Sensor sensor) {
		endSensor = new ConveyorSensor(sensor);
	}

	public String toString() {
		return "Conveyor " + componentNumber;
	}
	
	public void eventFired(TChannel channel,TEvent event, Object[] args)
	{
		if(channel == TChannel.CONVEYOR)
		{
			if(event == TEvent.CONVEYOR_GUI_RELEASE_FINISHED && (Integer)args[0] == componentNumber)
			{
				passGlassForward();
			}
		}
	}
}