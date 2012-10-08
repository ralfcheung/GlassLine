package glassline;

import glassline.interfaces.Robot;
import glassline.interfaces.RobotConveyor;

import java.util.Timer;
import java.util.TimerTask;

public class RobotConveyorAgent extends ConveyorAgent implements RobotConveyor {
	Robot previousComponent;
	
	public RobotConveyorAgent(int id) {
		super(id);
	}

	// Scheduler
	
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
			start();
			return true;
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
					
				}
				else { // Glass just left sensor
					if (!on) {
						Do("Error: glass left last sensor but I'm off!");
					}
				//	passGlassForward();
					return true;
				}
			}
			else if (sensor == beginningSensor) {
				if (sensor.isSensing) { // Glass just triggered sensor
					if (!sentAlertGlassBlockingEntry) {
						alertPreviousComponent();
						return true;
					}
					
					return true;
				}
				else { // Glass just left sensor
					if (sentAlertGlassBlockingEntry) {
						Do("telling " + previousComponent + " i have space available");
						sentAlertGlassBlockingEntry = false;
						previousComponent.msgIHaveSpaceAvailable(true);
					}
				}
			}
			return true;
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
	
	// Actions
	
	// Setters
	
	public void setPreviousComponent(Robot robot) {
		previousComponent = robot;
	}
}
