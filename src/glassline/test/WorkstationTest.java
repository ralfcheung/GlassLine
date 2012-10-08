package glassline.test;

import java.util.ArrayList;

import glassline.Factory;
import glassline.Glass;
import glassline.WorkstationAgent;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.mock.MockPopup;
import junit.framework.TestCase;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class WorkstationTest extends TestCase
{
	/* Tests for a glass coming in and out of the workstation
	 * 
	 * Starts from the msgHereIsGlass from the popup
	 * Goes through putting the glass in, working on the glass and sending it back out.
	 */
	public void testGlassInAndOut()
	{
		Factory.transducer = new Transducer();
		Factory.tp = null;
		MockPopup pop = new MockPopup();
		WorkstationAgent work = new WorkstationAgent(pop,Glass.WorkStation.CrossSeamer,0);
		ArrayList<WorkStation> works = new ArrayList<WorkStation>();
		works.add(WorkStation.CrossSeamer);
		Glass g = new Glass(GlassShape.Circle,works, 1);
		Factory.transducer.register(work,TChannel.CROSS_SEAMER);
		//t.setDebugMode(TransducerDebugMode.EVENTS_ONLY);
		Factory.transducer.startTransducer();
		Object[] args = {0};
		
		
		work.msgHereIsGlass(g);
		
		
		assertEquals(
				"Mock Popup should have an empty event log before the workstation's scheduler is called. Instead, the mock popup's event log reads: "
						+ pop.log.toString(), 0, pop.log.size());
		
		
		assertTrue("Workstation recieved Glass",work.glassToWorkOn != null);
		
		work.eventFired(TChannel.CROSS_SEAMER,TEvent.WORKSTATION_LOAD_FINISHED,args);
		
		assertTrue("Workstation load finished should be true is instead" +work.loadFinished,work.loadFinished);
		
		work.pickAndExecuteAnAction();
		
		work.eventFired(TChannel.CROSS_SEAMER, TEvent.WORKSTATION_GUI_ACTION_FINISHED, args);
		
		assertTrue("Workstation ask to come is true", work.askToCome);
		
		work.pickAndExecuteAnAction();
		
		
		assertTrue("Mock Popup should have received message to come up. Event log: "
				+ pop.log.toString(), pop.log
				.containsString("msgComeUp"));
		
		work.msgIAmUp();
		
		assertTrue("Workstation is ready to send", work.readyToSend);
		
		work.pickAndExecuteAnAction();
		
		assertTrue("Workstation ready to send is false", !(work.readyToSend));
		
		work.eventFired(TChannel.CROSS_SEAMER, TEvent.WORKSTATION_RELEASE_FINISHED, args);
		
		assertTrue("Workstation release is true", work.releaseFinished);
		
		work.pickAndExecuteAnAction();
		
		
		assertTrue("Mock Popup should have recieved message with glass. Event log: " + pop.log.toString(),pop.log.containsString("msgHereIsGlass"));
		
		

		
	}
}
