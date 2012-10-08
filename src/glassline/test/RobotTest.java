package glassline.test;

import glassline.Factory;
import glassline.RobotAgent;
import glassline.Glass.WorkStation;
import glassline.mock.MockConveyor;
import glassline.mock.MockGUI;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import transducer.TChannel;
import transducer.Transducer;

public class RobotTest extends TestCase {
	
	RobotAgent robot;
	MockConveyor conveyor;
	MockGUI gui;
	
	@Override
	public void setUp() {
		Factory.transducer = new Transducer();
		Factory.tp = null;
		
		robot = new RobotAgent();
		conveyor = new MockConveyor();
		gui = new MockGUI();
		Factory.transducer.register(gui, TChannel.BIN);
		Factory.transducer.processNextEvent();
		
		robot.setNextComponent(conveyor);
	}
	
	@Override
	public void tearDown() {
		
	}
	
	@Test
	public void testRobotPutGlassOnEmptyConveyor() {
		// Preconditions
		assertTrue("Make sure there are no pieces of glass in the queue",
				robot.glassQueue.isEmpty());
		assertFalse("The robot shouldn't be transporting any glass",
				robot.transportingGlass);
		assertTrue("Should be space on the conveyor",
				robot.nextSpaceAvailable());
		
		robot.msgGetGlassFromBin(null, new ArrayList<WorkStation>());
		
		assertTrue("There should be 1 glass in the queue",
				robot.glassQueue.size() == 1);
		
		robot.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		Factory.transducer.processNextEvent();
		
		assertTrue("Conveyor should have received the glass ref from the robot",
				conveyor.log.containsString("msgHereIsGlass"));
		assertTrue("MockGUI should have received a transducer event telling it to make glass",
				gui.log.containsString("BIN_CREATE_PART"));
		assertTrue("Robot is transporting glass",
				robot.transportingGlass);
		assertTrue("Should be no more glass in the queue",
				robot.glassQueue.isEmpty());
		
		robot.msgGetGlassFromBin(null, new ArrayList<WorkStation>());
		
		assertTrue("There should be 1 glass in the queue",
				robot.glassQueue.size() == 1);
		
		robot.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should not have received the glass ref from the robot so there should only be one log msg",
				conveyor.log.size() == 1);
		assertTrue("Robot is still transporting glass",
				robot.transportingGlass);
		assertTrue("Should still be one glass in the queue",
				robot.glassQueue.size() == 1);
		
		//robot.msgReceivedGlass();
		
		assertFalse("Robot is not transporting glass anymore",
				robot.transportingGlass);
		
		robot.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have received the glass ref from the robot",
				conveyor.log.size() == 2 && conveyor.log.getLastLoggedEvent().getMessage().contains("msgHereIsGlass"));
		assertTrue("Robot is transporting glass",
				robot.transportingGlass);
		assertTrue("Should be no more glass in the queue",
				robot.glassQueue.isEmpty());
	}
	
	@Test
	public void testRobotPutGlassOnOccupiedConveyor() {
		robot.msgIHaveSpaceAvailable(false);
		robot.msgGetGlassFromBin(null, new ArrayList<WorkStation>());
		robot.pickAndExecuteAnAction();
		
		assertFalse("Conveyor should not have received the glass ref from the robot",
				conveyor.log.containsString("msgHereIsGlass"));
		assertTrue("Should still be one glass in the queue",
				robot.glassQueue.size() == 1);
		assertFalse("Schedule shouldn't do anything if there is no available spot for new glass",
				robot.pickAndExecuteAnAction());
		
		robot.msgIHaveSpaceAvailable(true);
		robot.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have received the glass ref from the robot",
				conveyor.log.containsString("msgHereIsGlass"));
		assertTrue("Robot is transporting glass",
				robot.transportingGlass);
		assertTrue("Should be no more glass in the queue",
				robot.glassQueue.isEmpty());
		
	}
}
