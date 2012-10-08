package glassline.test;

import java.util.ArrayList;

import glassline.ConveyorAgent;
import glassline.Factory;
import glassline.Glass;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.mock.MockFactoryComponent;
import glassline.mock.MockGUI;
import glassline.mock.MockSensor;
import junit.framework.TestCase;
import transducer.TChannel;
import transducer.Transducer;

public class ConveyorAgentTest extends TestCase {

	ConveyorAgent conveyor;
	MockFactoryComponent previousComponent;
	MockFactoryComponent nextComponent;
	MockSensor beginningSensor;
	MockSensor endSensor;
	MockGUI gui;
	Glass glass1;
	Glass glass2;
	
	public void setUp() throws Exception {
		Factory.transducer = new Transducer();
		Factory.tp = null;
		gui = new MockGUI();
		Factory.transducer.register(gui, TChannel.CONVEYOR);
		Factory.transducer.processNextEvent();//to register gui
		conveyor = new ConveyorAgent(2);
		previousComponent = new MockFactoryComponent();
		nextComponent = new MockFactoryComponent();
		conveyor.setPreviousComponent(previousComponent);
		conveyor.setNextComponent(nextComponent);
		beginningSensor = new MockSensor();
		endSensor = new MockSensor();
		conveyor.setBeginningSensor(beginningSensor);
		conveyor.setEndSensor(endSensor);
		Factory.transducer.processNextEvent();//to start conveyor
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
		assertTrue("There is space available ahead",
						conveyor.nextSpaceAvailable());
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		//give glass
		conveyor.msgHereIsGlass(glass1);
		
		assertTrue("myGlass should have 1 element that is glass1",
						conveyor.myGlass.size() == 1 && conveyor.myGlass.get(0).glass == glass1);
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(beginningSensor, true);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be beginningSensor and isSensing should be true",
						conveyor.sensorHits.get(0).sensor == beginningSensor && conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(beginningSensor, false);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be beginningSensor and isSensing should be false",
						conveyor.sensorHits.get(0).sensor == beginningSensor && !conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(endSensor, true);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be endSensor and isSensing should be true",
						conveyor.sensorHits.get(0).sensor == endSensor && conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(endSensor, false);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be endSensor and isSensing should be false",
						conveyor.sensorHits.get(0).sensor == endSensor && !conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Next component received glass",
						nextComponent.log.containsString("msgHereIsGlass"));
		assertTrue("myGlass should be empty now",
						conveyor.myGlass.isEmpty());
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());

	}

	public void testTwoGlassesMovingThroughConveyor() {
		//give glass
		conveyor.msgHereIsGlass(glass1);
		
		assertTrue("myGlass should have 1 element that is glass1",
						conveyor.myGlass.size() == 1 && conveyor.myGlass.get(0).glass == glass1);
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(beginningSensor, true);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be beginningSensor and isSensing should be true",
						conveyor.sensorHits.get(0).sensor == beginningSensor && conveyor.sensorHits.get(0).isSensing);

		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(beginningSensor, false);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be beginningSensor and isSensing should be false",
						conveyor.sensorHits.get(0).sensor == beginningSensor && !conveyor.sensorHits.get(0).isSensing);

		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgHereIsGlass(glass2);
		
		assertTrue("myGlass's 2nd element should be glass2",
				conveyor.myGlass.size() == 2 && conveyor.myGlass.get(1).glass == glass2);
		assertFalse("Scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.msgSensedGlass(endSensor, true);
		
		assertTrue("sensor hits should have 2 elements",
						conveyor.sensorHits.size() == 2);
		assertTrue("sensor hits first element should be beginningSensor and isSensing should be true",
						conveyor.sensorHits.get(0).sensor == beginningSensor && conveyor.sensorHits.get(0).isSensing);
		assertTrue("sensor hits second element should be endSensor and isSensing should be true",
						conveyor.sensorHits.get(1).sensor == endSensor && conveyor.sensorHits.get(1).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should have one left",
						conveyor.sensorHits.size() == 1 && conveyor.sensorHits.get(0).sensor == endSensor);		
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
				conveyor.sensorHits.isEmpty());		
		
		conveyor.msgSensedGlass(beginningSensor, false);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be beginningSensor and isSensing should be false",
						conveyor.sensorHits.get(0).sensor == beginningSensor && !conveyor.sensorHits.get(0).isSensing);

		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(endSensor, false);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be endSensor and isSensing should be false",
						conveyor.sensorHits.get(0).sensor == endSensor && !conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Next component received glass",
						nextComponent.log.containsString("msgHereIsGlass"));
		assertTrue("myGlass should be 1 now",
						conveyor.myGlass.size() == 1 && conveyor.myGlass.get(0).glass == glass2);
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(endSensor, true);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be endSensor and isSensing should be true",
						conveyor.sensorHits.get(0).sensor == endSensor && conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
				conveyor.sensorHits.isEmpty());
		assertFalse("Scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
		
		conveyor.msgSensedGlass(endSensor, false);
		
		assertTrue("sensor hits should have 1 element",
						conveyor.sensorHits.size() == 1);
		assertTrue("sensor hits single element should be endSensor and isSensing should be false",
						conveyor.sensorHits.get(0).sensor == endSensor && !conveyor.sensorHits.get(0).isSensing);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Next component received glass",
						nextComponent.log.containsString("msgHereIsGlass"));
		assertTrue("myGlass should be empty now",
						conveyor.myGlass.isEmpty());
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
	}

	public void testConveyorStoppingWhenNextComponentHasNoSpace() {
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(beginningSensor, false);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgIHaveSpaceAvailable(false);
		
		assertFalse("Next component should have no space available",
						conveyor.nextSpaceAvailable());
		assertFalse("Conveyor should still be on, i.e. scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(endSensor, true);
		conveyor.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();

		assertFalse("Conveyor should be stopped",
						conveyor.on);
		assertTrue("MockGUI should have received conveyor stop event",
						gui.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("Event received should have been from our conveyor",
						gui.log.containsString("2"));
		
		conveyor.msgIHaveSpaceAvailable(true);
		
		assertTrue("Next component should have space available",
						conveyor.nextSpaceAvailable());
		
		conveyor.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have received conveyor start event from our conveyor",
						gui.log.getLastLoggedEvent().getMessage().contains("CONVEYOR_DO_START") 
						&& gui.log.getLastLoggedEvent().getMessage().contains("2"));
		assertTrue("Conveyor should be on",
						conveyor.on);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("sensor hits should be empty",
						conveyor.sensorHits.isEmpty());
		
		assertFalse("Scheduler should return false",
				conveyor.pickAndExecuteAnAction());
		
	}

	public void testStoppedConveyorAlertingPreviousComponentWhenGlassAtBeginning() {
		//precondition
		assertFalse("sentAlertGlassBlockingEntry should be false",
						conveyor.sentAlertGlassBlockingEntry);
		
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(beginningSensor, false);
		conveyor.msgHereIsGlass(glass2);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(endSensor, true);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgIHaveSpaceAvailable(false);
		conveyor.pickAndExecuteAnAction();		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have received conveyor stop event",
						gui.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("Event received should have been from our conveyor",
						gui.log.containsString("2"));
		assertFalse("Conveyor should be stopped",
						conveyor.on);
		assertTrue("previous component should have received message space isnt available from conveyor",
						previousComponent.log.containsString("msgIHaveSpaceAvailable") && previousComponent.log.containsString("false"));
		assertTrue("sentAlertGlassBlockingEntry should be true",
						conveyor.sentAlertGlassBlockingEntry);
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		conveyor.msgIHaveSpaceAvailable(true);
		conveyor.pickAndExecuteAnAction();		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have received conveyor start event from our conveyor",
						gui.log.getLastLoggedEvent().getMessage().contains("CONVEYOR_DO_START") 
						&& gui.log.getLastLoggedEvent().getMessage().contains("2"));
		assertTrue("Conveyor should be on",
						conveyor.on);
		assertFalse("sentAlertGlassBlockingEntry should be false",
						conveyor.sentAlertGlassBlockingEntry);
		assertTrue("previous component should have received message space is available from conveyor",
						previousComponent.log.getLastLoggedEvent().getMessage().contains("msgIHaveSpaceAvailable"));
						
		assertTrue("message space is available from conveyor should be true",
						previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));		
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
	}		
	
	public void testStoppedConveyorAlertingPreviousComponentWhenGlassArrivesAtBeginning() {
		//precondition
		assertFalse("sentAlertGlassBlockingEntry should be false",
						conveyor.sentAlertGlassBlockingEntry);
		
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(beginningSensor, false);
		conveyor.msgHereIsGlass(glass2);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(endSensor, true);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgIHaveSpaceAvailable(false); // comes from next component
		conveyor.pickAndExecuteAnAction();		
		Factory.transducer.processNextEvent();
		
		assertFalse("previous component should not have received message space isnt available from conveyor",
						previousComponent.log.containsString("msgIHaveSpaceAvailable") && previousComponent.log.containsString("false"));
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
		
		conveyor.msgSensedGlass(beginningSensor, true);
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("previous component should have received message space isnt available from conveyor",
						previousComponent.log.containsString("msgIHaveSpaceAvailable") && previousComponent.log.containsString("false"));
		assertTrue("sentAlertGlassBlockingEntry should be true",
						conveyor.sentAlertGlassBlockingEntry);
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());

		conveyor.msgIHaveSpaceAvailable(true);
		conveyor.pickAndExecuteAnAction();		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have received conveyor start event from our conveyor",
						gui.log.getLastLoggedEvent().getMessage().contains("CONVEYOR_DO_START") 
						&& gui.log.getLastLoggedEvent().getMessage().contains("2"));
		assertTrue("Conveyor should be on",
						conveyor.on);
		assertFalse("sentAlertGlassBlockingEntry should be false",
						conveyor.sentAlertGlassBlockingEntry);
		assertTrue("previous component should have received message space is available from conveyor",
						previousComponent.log.getLastLoggedEvent().getMessage().contains("msgIHaveSpaceAvailable"));
						
		assertTrue("message space is available from conveyor should be true",
						previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));		
		assertFalse("Scheduler should return false",
						conveyor.pickAndExecuteAnAction());
	}
}
