package glassline;

import transducer.TChannel;
import transducer.TEvent;
import glassline.interfaces.PopupConveyor;

public class PopupConveyorAgent extends ConveyorAgent implements PopupConveyor {



	public Glass.WorkStation popupType;
	public PopupConveyorAgent(int conveyorID) {
		super(conveyorID);
		switch(conveyorID){ //check with someone with a higher resolution screen :(
		case 5:
			popupType = Glass.WorkStation.Drill;
			break;
		case 6:
			popupType = Glass.WorkStation.CrossSeamer;
			break;
		case 7:
			popupType = Glass.WorkStation.Grinder;
			break;
		}
	}

	public boolean popupFull = false;

	@Override
	public void msgWorkstationsFull(boolean b) {
		popupFull = b;
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {

		// Rule: Prevents collisions by moving a glass onto a stopped conveyor with glass at the beginning.
		if (on && !nextSpaceAvailable()) {

			if (endSensor.isSensing) {
				stop();
				return true;
			}
		}

		// Rule: Restarts conveyor if nothing is blocking the glass.
		if (!on && (nextSpaceAvailable())) {
			if (myGlass.get(0).glass.shouldStopAtStation(popupType)) {
				if(!popupFull) {
					start();
					return true;
				}
			} else {
				start();
				return true;
			}
		}
		// Rule: Process a sensor state change.
		if (!sensorHits.isEmpty()) {

			ConveyorSensor sensor = sensorHits.get(0);
			sensorHits.remove(0);
			
			if (sensor == endSensor) {
				if (myGlass.isEmpty()) {
					Do("Error: The end sensor was tripped on conveyor " + componentNumber + " with an empty myGlass list");
				}
				if (sensor.isSensing) { // Glass just triggered sensor

					if(popupFull && myGlass.get(0).glass.shouldStopAtStation(popupType) && on){
						stop();
						return true;
					}
				}
				else { // Glass just left sensor
					if (!on) {
					Do("Error: glass left last sensor but I'm off!");
					}
					if(popupFull){
//						if(myGlass.get(0).shoudStopAtStation(popupType)){ // if its full and the glass uses the popup, then stops
//							stop();
//							return true;
//						}
//						else if(!myGlass.get(0).shoudStopAtStation(popupType)){
//							//if glass is not same type as popup send it through
//							passGlassForward();
//							return true;
//						}
						if(!myGlass.get(0).glass.shouldStopAtStation(popupType)){
							passGlassForward();
							return true;
						}
					}
					else{
						passGlassForward();
					}
					return true;
				}
			}
			else if (sensor == beginningSensor) {
				if (sensor.isSensing) { // Glass just triggered sensor
					return true;
				}
				else { 
					if(!on)
					{
						print("beggining glass sensor tripped but I am off");
					}// Glass just left sensor
					if (sentAlertGlassBlockingEntry) {
						Do("telling " + previousComponent + " i have space available");
						sentAlertGlassBlockingEntry = false;
						previousComponent.msgIHaveSpaceAvailable(true);						
					}
					return true;
				}
			}
			return true;
		}
		synchronized(myGlass){
			for(MyGlass g : myGlass)
			{
				if(!on && g.onBeginningSensor && !sentAlertGlassBlockingEntry)
				{
					alertPreviousComponent();
					return true;
				}
			}
		}
		return false;
	}

	public void eventFired(TChannel channel,TEvent event, Object[] args) {
		
	}
}
