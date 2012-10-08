package glassline.test;

import glassline.Factory;
import glassline.Glass;
import glassline.PopupConveyorAgent;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.mock.MockFactoryComponent;
import glassline.mock.MockGUI;
import glassline.mock.MockPopup;
import glassline.mock.MockSensor;

import java.util.ArrayList;

import junit.framework.TestCase;
import transducer.TChannel;
import transducer.Transducer;

public class PopupConveyorAgentTest extends TestCase {
	PopupConveyorAgent conveyor;
	MockPopup popup;
	MockFactoryComponent nextComponent;
	MockSensor beginningSensor;
	MockSensor endSensor;
	MockGUI gui;
	Glass glass1;
	Glass glass2;
	ArrayList<WorkStation> workstations = new ArrayList<WorkStation>();
	
	public void setUp() throws Exception {
		workstations.add(WorkStation.CrossSeamer);
		workstations.add(WorkStation.Drill);
		workstations.add(WorkStation.Grinder);
		Factory.transducer = new Transducer();
		Factory.tp = null;
		gui = new MockGUI();
		Factory.transducer.register(gui, TChannel.CONVEYOR);
		Factory.transducer.processNextEvent();//to register gui
		conveyor = new PopupConveyorAgent(5);
		Factory.transducer.processNextEvent();//to start conveyor
		popup = new MockPopup();
		nextComponent = new MockFactoryComponent();
		conveyor.setNextComponent(nextComponent);
		beginningSensor = new MockSensor();
		endSensor = new MockSensor();
		conveyor.setBeginningSensor(beginningSensor);
		conveyor.setEndSensor(endSensor);
		ArrayList<WorkStation> recipe1 = new ArrayList<WorkStation>();
		recipe1.add(WorkStation.CrossSeamer);
		recipe1.add(WorkStation.Drill);
		recipe1.add(WorkStation.Grinder);
	
		glass1 = new Glass(GlassShape.Circle, recipe1, 1);
		glass2 = new Glass(GlassShape.Circle, recipe1, 2);
	
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
		assertTrue("Conveyor should have one piece of glass", !conveyor.myGlass.isEmpty());
		conveyor.pickAndExecuteAnAction();
		conveyor.msgSensedGlass(endSensor, false);
		assertTrue("Conveyor should not have an empty sensor hit list", !conveyor.sensorHits.isEmpty());
		conveyor.pickAndExecuteAnAction();
		assertTrue("Glass should have the type of the conveyor", glass1.shouldStopAtStation(conveyor.popupType));

		assertTrue("next component should have gotten a hereisglass msg", nextComponent.log.containsString("msgHereIsGlass"));
	}
	
	public void testOneGlassBothWorkstationsFull(){
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(endSensor, false);
		conveyor.msgWorkstationsFull(true);
		assertTrue("Conveyor should think that both workstations are full", conveyor.popupFull);
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should not have passed on the glass", conveyor.myGlass.size() == 1);
	}
	
	public void testOneGlassBothWorkstationsFullButGlassGoesThrough(){
		
		glass1 = new Glass(GlassShape.Circle, new ArrayList<WorkStation>(), 1);
		conveyor.myGlass.clear();
		nextComponent.log.clear();
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(endSensor, false);
		conveyor.msgWorkstationsFull(true);
		assertTrue("Conveyor should think that both workstations are full", conveyor.popupFull);
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have passed on the glass", conveyor.myGlass.isEmpty());
		assertTrue("Next component should have gotten the glass", nextComponent.log.containsString("Received msgHereIsGlass"));
	}
	
	public void testOneGlassWorkstationsGoFromFullToEmpty(){
		glass1 = new Glass(GlassShape.Circle, workstations, 1);
		conveyor.myGlass.clear();
		nextComponent.log.clear();
		conveyor.msgHereIsGlass(glass1);
		conveyor.msgSensedGlass(endSensor, false);
		conveyor.msgWorkstationsFull(true);
		assertTrue("Conveyor should think that both workstations are full", conveyor.popupFull);
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should not have passed on the glass", conveyor.myGlass.size() == 1);
		conveyor.msgWorkstationsFull(false);
		conveyor.msgSensedGlass(endSensor, false);
		assertTrue("Conveyor should think that at least one workstation is empty", !conveyor.popupFull);
		assertTrue("Conveyor should have a sensor event", conveyor.sensorHits.size() == 1);
		conveyor.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have passed on the glass", conveyor.myGlass.isEmpty());
		assertTrue("Nextcomponent should have gotten the glass", nextComponent.log.containsString("Received msgHereIsGlass"));
	}
}
