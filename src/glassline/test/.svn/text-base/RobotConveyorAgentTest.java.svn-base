package glassline.test;

import java.util.ArrayList;

import glassline.Factory;
import glassline.Glass;
import glassline.RobotConveyorAgent;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.mock.MockFactoryComponent;
import glassline.mock.MockGUI;
import glassline.mock.MockRobot;
import glassline.mock.MockSensor;
import junit.framework.TestCase;
import transducer.TChannel;
import transducer.Transducer;

public class RobotConveyorAgentTest extends TestCase {
	RobotConveyorAgent conveyor;
	MockRobot robot;
	MockFactoryComponent nextComponent;
	MockSensor beginningSensor;
	MockGUI gui;
	Glass glass1;
	Glass glass2;
	
	public void setUp() throws Exception {
		Factory.transducer = new Transducer();
		Factory.tp = null;
		gui = new MockGUI();
		Factory.transducer.register(gui, TChannel.CONVEYOR);
		Factory.transducer.processNextEvent();//to register gui
		conveyor = new RobotConveyorAgent(2);
		Factory.transducer.processNextEvent();//to start conveyor
		robot = new MockRobot();
		nextComponent = new MockFactoryComponent();
		conveyor.setPreviousComponent(robot);
		conveyor.setNextComponent(nextComponent);
		beginningSensor = new MockSensor();
		conveyor.setBeginningSensor(beginningSensor);
		glass1 = new Glass(GlassShape.Circle, new ArrayList<WorkStation>(), 1);
		glass2 = new Glass(GlassShape.Circle, new ArrayList<WorkStation>(), 2);
	
	}

	public void testOneGlassMovingThroughConveyor() {
		//preconditions
		assertTrue("Conveyor's myGlass is empty",
						conveyor.myGlass.isEmpty());
		assertTrue("Conveyor should be on",
						conveyor.on);
		assertTrue("Conveyor's sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		//give glass
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.pickAndExecuteAnAction();
		try { Thread.sleep(4000); } catch(InterruptedException e) {} // Have to wait for the delayed message.
		assertTrue("conveyor should have messaged the robot that it received glass",
					robot.log.containsString("msgReceivedGlass"));
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
	}
}
