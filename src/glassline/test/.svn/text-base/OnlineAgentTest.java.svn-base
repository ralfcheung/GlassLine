package glassline.test;

import glassline.Factory;
import glassline.Glass;
import glassline.OnlineAgent;
import glassline.Glass.GlassActivity;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.mock.MockFactoryComponent;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class OnlineAgentTest extends TestCase{
	
	public OnlineAgent onlineAgent;
	public MockFactoryComponent previousComponent;
	public MockFactoryComponent nextComponent;
	public Glass glass, glass1, glass2;
	
	
	//Tests for one glass going through an OnlineAgent
	@Test
	public void testGlassGoingThrough() {
		Factory.transducer = new Transducer();
		
		
		onlineAgent = new OnlineAgent(TChannel.CUTTER, GlassActivity.Breakout); //using cutter to represent all onlineAgents
		glass = new Glass(GlassShape.Circle, new ArrayList<WorkStation>(), 1);
		previousComponent = new MockFactoryComponent();
		onlineAgent.setPreviousComponent(previousComponent);
		nextComponent = new MockFactoryComponent();
		onlineAgent.setNextComponent(nextComponent);
		
		System.out.println("Start test");
		assertTrue("OnlineAgent should not have any glass at the beginning. " + onlineAgent.currentGlass, onlineAgent.currentGlass == null);
		
		onlineAgent.msgHereIsGlass(glass);
		
		assertTrue("Mock previous component should have received a message msgIHaveSpaceAvailable(false). Event log: "
				+ previousComponent.log.toString(), previousComponent.log
				.containsString("msgIHaveSpaceAvailable"));
		assertTrue("Glass should not be done yet. Current glass done?: " + onlineAgent.currentGlass.done, !onlineAgent.currentGlass.done);
		
		onlineAgent.eventFired(TChannel.CUTTER, TEvent.WORKSTATION_GUI_ACTION_FINISHED, null);
		assertTrue("Glass should be marked as done. Current glass done?: " + onlineAgent.currentGlass.done, onlineAgent.currentGlass.done);
		assertEquals("NextComponent should not have gotten anything from the onlineAgent.", nextComponent.log.size(), 0);
		
		onlineAgent.eventFired(onlineAgent.channel, TEvent.WORKSTATION_RELEASE_FINISHED, null);

		onlineAgent.pickAndExecuteAnAction();

		assertTrue("Current glass should have turned null. Current glass: " + onlineAgent.currentGlass, onlineAgent.currentGlass == null);
		assertTrue("Mock nextComponent should have received a message msgGlassIsHere. Event log: "
				+ nextComponent.log.toString(), nextComponent.log
				.containsString("msgHereIsGlass"));
		assertTrue("Mock previousComponent should have received a message msgIHaveSpaceAvailable. Event log: "
				+ previousComponent.log.toString(), previousComponent.log
				.containsString("msgIHaveSpaceAvailable"));
		
		
	}
	
	//Test for trying to pass a piece of glass to OnlineAgent when there is something already there. 
	@Test
	public void testTwoGlassTest(){
		
		onlineAgent = new OnlineAgent(TChannel.CUTTER, GlassActivity.Breakout);
		glass1 = new Glass(GlassShape.Circle, new ArrayList<WorkStation>(), 1);
		glass2 = new Glass(GlassShape.Square, new ArrayList<WorkStation>(), 2);
		previousComponent = new MockFactoryComponent();
		onlineAgent.setPreviousComponent(previousComponent);
		nextComponent = new MockFactoryComponent();
		onlineAgent.setNextComponent(nextComponent);
		
		//Test to check that there is no glass initially. 
		assertTrue("OnlineAgent should not have any glass at the beginning. " + onlineAgent.currentGlass, onlineAgent.currentGlass == null);
		
		//Pass one glass to onlineAgent
		onlineAgent.msgHereIsGlass(glass1);

		//check to see that the previous component got a warning
		assertTrue("Mock previous component should have received a message msgIHaveSpaceAvailable(false). Event log: "
				+ previousComponent.log.toString(), previousComponent.log
				.containsString("msgIHaveSpaceAvailable"));
		
		//Check to make sure that the workstation is working on glass until it calls 
		assertTrue("Glass should not be done yet. Current glass done?: " + onlineAgent.currentGlass.done, !onlineAgent.currentGlass.done);

		//Passing a second piece of glass to onlineAgent, should refuse to receive 
		onlineAgent.msgHereIsGlass(glass2);//how to check?
			
	}
	
	//Test to see if OnlineAgent passes the glass when it knows that there is something on the next factoryAgent
	@Test
	public void testPassGlassBlockTest(){
		onlineAgent = new OnlineAgent(TChannel.CUTTER, GlassActivity.Breakout);
		glass1 = new Glass(GlassShape.Circle, new ArrayList<WorkStation>(), 1);
		nextComponent = new MockFactoryComponent();
		onlineAgent.setNextComponent(nextComponent);		
		previousComponent = new MockFactoryComponent();
		
		onlineAgent.setPreviousComponent(previousComponent);
		
		//Test to check that there is no glass initially. 
		assertTrue("OnlineAgent should not have any glass at the beginning. " + onlineAgent.currentGlass, onlineAgent.currentGlass == null);
		
		//Pass one glass to onlineAgent
		onlineAgent.msgHereIsGlass(glass1);

	}
}
