package glassline.test;

import glassline.Factory;
import glassline.Glass;
import glassline.PopupAgent;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import glassline.PopupAgent.AlertStatus;
import glassline.PopupAgent.GlassStatus;
import glassline.PopupAgent.PopupStatus;
import glassline.PopupAgent.WorkstationStatus;
import glassline.mock.MockFactoryComponent;
import glassline.mock.MockGUI;
import glassline.mock.MockPopupConveyor;
import glassline.mock.MockWorkstation;

import java.util.ArrayList;

import junit.framework.TestCase;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class PopupAgentTest extends TestCase {
	PopupAgent popup;
	MockPopupConveyor previousComponent;
	MockFactoryComponent nextComponent;
	MockWorkstation w1, w2;
	MockGUI gui;
	Glass glass1;
	Glass glass2;
	Glass glass3;
	Glass glass4;
	Object[] myID;
	
	public void setUp() throws Exception {
		Factory.transducer = new Transducer();
		Factory.tp = null;
		
		gui = new MockGUI();
		Factory.transducer.register(gui, TChannel.POPUP);
		Factory.transducer.processNextEvent();//to register gui
		
		popup = new PopupAgent(Factory.transducer, 2);
		
		previousComponent = new MockPopupConveyor();
		nextComponent = new MockFactoryComponent();
		popup.setPreviousComponent(previousComponent);
		popup.setNextComponent(nextComponent);
		
		w1 = new MockWorkstation(Glass.WorkStation.Drill, "Workstation 1");
		w2 = new MockWorkstation(Glass.WorkStation.Drill, "Workstation 2");
		
		popup.addWorkstation(w1);
		popup.addWorkstation(w2);
		Factory.transducer.processNextEvent();//to start popup
		
		ArrayList<WorkStation> recipe1 = new ArrayList<WorkStation>();
		ArrayList<WorkStation> recipe2 = new ArrayList<WorkStation>();
		recipe1.add(WorkStation.Drill);
		recipe2.add(WorkStation.CrossSeamer);
		glass1 = new Glass(GlassShape.Circle, recipe1, 1);
		glass2 = new Glass(GlassShape.Circle, recipe1, 2);
		glass3 = new Glass(GlassShape.Circle, recipe2, 3);
		glass4 = new Glass(GlassShape.Circle, recipe1, 4);
		
		myID = new Object[1];
		myID[0] = 2;
	}
	
	public void testOneGlassMovingToWorkstation(){
		
		//preconditions
		assertTrue("PopupAgent should be empty", popup.status == PopupStatus.DOWN_EMPTY);
		
		assertTrue("Both workstations should be empty", popup.workstations.get(0).status == WorkstationStatus.isEmpty && popup.workstations.get(1).status == WorkstationStatus.isEmpty);
		
		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());
		
		assertTrue("alertStatus should be Alerted", popup.alertStatus == AlertStatus.Alerted);
		
		popup.msgHereIsGlass(glass1);
		
		//check that states were changed properly
		
		assertTrue("PopupStatus should be LOADING", popup.status == PopupStatus.LOADING);
		
		assertTrue("FromConveyor should be TRUE", popup.fromConveyor);
		
		assertTrue("alertStatus should be Alerted", popup.alertStatus == AlertStatus.Alerted);
		
/*		popup.pickAndExecuteAnAction();
				
		assertTrue("Previous conveyor should have recieved msgIHaveSpaceAvailable: " + previousComponent.log.toString(), previousComponent.log.containsString("msgIHaveSpaceAvailable") && previousComponent.log.containsString("false"));
		
		assertTrue("PopupStatus should be LOADING", popup.status == PopupStatus.LOADING);
		
		assertTrue("alert should be TRUE", popup.alert);*/
		
		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		
		assertTrue("PopupStatus should be DOWN_FULL", popup.status == PopupStatus.DOWN_FULL);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("PopupStatus should be WAITING_FOR_UP", popup.status == PopupStatus.WAITING_FOR_UP);
		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have recieved POPUP_DO_MOVE_UP", gui.log.containsString("POPUP_DO_MOVE_UP"));
		
		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		
		assertTrue("PopupStatus should be UP_FULL", popup.status == PopupStatus.UP_FULL);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("PopupStatus should be UP_EMPTY", popup.status == PopupStatus.UP_EMPTY);
		
		assertTrue("w1's state should be isFull", popup.workstations.get(0).status == WorkstationStatus.isFull);
		
		assertTrue("w1 should have recieved msgHereIsGlass", w1.log.containsString("msgHereIsGlass"));
		
		assertTrue("theGlass should be NULL", popup.theGlass == null);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("PopupStatus should be WAITING_FOR_DOWN", popup.status == PopupStatus.WAITING_FOR_DOWN);
		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have recieved POPUP_DO_MOVE_DOWN", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_DO_MOVE_DOWN"));
		
		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, myID);
		
		assertTrue("PopupStatus should be COME_DOWN", popup.status == PopupStatus.COME_DOWN);
		
		popup.pickAndExecuteAnAction();

		assertTrue("PopupStatus should be DOWN_EMPTY", popup.status == PopupStatus.DOWN_EMPTY);

		assertTrue("Previous conveyor should have recieved msgIHaveSpaceAvailable", previousComponent.log.getLastLoggedEvent().getMessage().contains("msgIHaveSpaceAvailable") && previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));

		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());

		//so now the workstation gives us the glass back
		popup.msgComeUp(w1);
		
		assertTrue("First work station status should be is done", popup.workstations.get(0).status == WorkstationStatus.isDone);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("PopupStatus should be WAITING_FOR_UP", popup.status == PopupStatus.WAITING_FOR_UP);
		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have recieved POPUP_DO_MOVE_UP", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_DO_MOVE_UP"));
		
		assertTrue("First work station status should be waiting for popup", popup.workstations.get(0).status == WorkstationStatus.isWaitingForPopup);
		
		assertTrue("Requesting workstations should have w1 as sole element", popup.requestingWorkstations.get(0) == w1);
		
		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		
		assertTrue("PopupStatus should be COME_UP", popup.status == PopupStatus.COME_UP);
				
		popup.pickAndExecuteAnAction();
		
		assertTrue("PopupStatus should be UP_EMPTY", popup.status == PopupStatus.UP_EMPTY);
		
		assertTrue("Workstation received message that popup is up", w1.log.getLastLoggedEvent().getMessage().contains("msgIAmUp"));
		
		assertTrue("scheduler should return false", !popup.pickAndExecuteAnAction());
		
		glass1.recipe.workstations.put(WorkStation.Drill, true);
		popup.msgHereIsGlass(glass1, w1);
		
		assertTrue("Requesting workstations should be empty", popup.requestingWorkstations.isEmpty());
		
		assertTrue("glass status is on popup", popup.theGlass.state == GlassStatus.onPopup);
		
		assertTrue("popup status should be loading", popup.status == PopupStatus.LOADING);
		
		assertTrue("from Conveyor should be false", !popup.fromConveyor);
		
		assertTrue("Workstation should be empty", popup.workstations.get(0).status == WorkstationStatus.isEmpty);
		
		assertTrue("scheduler should return false", !popup.pickAndExecuteAnAction());
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		
		assertTrue("PopupStatus should be UP_FULL", popup.status == PopupStatus.UP_FULL);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("PopupStatus should be WAITING_FOR_DOWN", popup.status == PopupStatus.WAITING_FOR_DOWN);
		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have recieved POPUP_DO_MOVE_DOWN", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_DO_MOVE_DOWN"));
		
		assertTrue("Scheduler should return false", !popup.pickAndExecuteAnAction());
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, myID);
		
		assertTrue("PopupStatus should be DOWN_FULL", popup.status == PopupStatus.DOWN_FULL);
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, myID);
		
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();

		assertTrue("PopupStatus should be DOWN_EMPTY", popup.status == PopupStatus.DOWN_EMPTY);

		assertTrue("MockGUI should have recieved POPUP_RELEASE_GLASS", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_RELEASE_GLASS"));

		assertTrue("theGlass is null", popup.theGlass == null);
		
		assertTrue("next component received msgHereIsGlass", nextComponent.log.containsString("msgHereIsGlass"));
		
		assertTrue("previous conveyor receive message that space is available", previousComponent.log.getLastLoggedEvent().getMessage().contains("msgIHaveSpaceAvailable")
					&& previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));
		
		assertTrue("schedulder should return false", !popup.pickAndExecuteAnAction());
	}
	
	/*public void testThreePiecesOfGlass(){
		
		popup.msgHereIsGlass(glass1);
		popup.pickAndExecuteAnAction();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		//popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		//popup.pickAndExecuteAnAction();
		
		//second piece of glass
		popup.msgHereIsGlass(glass2);		

		assertTrue("alertStatus should be needAlert", popup.alertStatus == AlertStatus.NeedAlert);

		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		
		assertTrue("alertStatus should be Alerted", popup.alertStatus == AlertStatus.Alerted);
		
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();
		
		assertTrue("Both workstations should have status of isFull", 
				popup.workstations.get(0).status == WorkstationStatus.isFull 
				&& popup.workstations.get(1).status == WorkstationStatus.isFull);
		
		assertTrue("Previous conveyor should have recieved msgWorkstationsFull(true)" + previousComponent.log.toString(), previousComponent.log.getLastLoggedEvent().getMessage().contains("Received msgWorkstationsFull"));

		//now have one of the pieces of glass leave its workstation. the previous conveyor should
		//have received msgIAmFull(false)
		popup.msgComeUp(w1);
		
		assertTrue("w1's status should now be isDone", popup.workstations.get(0).status == WorkstationStatus.isDone);
		
		
		
		
	}*/
	
	public void testTwoPiecesOfGlassMovingToWorkstations(){
		popup.msgHereIsGlass(glass1);
		popup.pickAndExecuteAnAction();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		popup.pickAndExecuteAnAction();
		
		//second piece of glass
		popup.msgHereIsGlass(glass2);
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();
		
		
		
		assertTrue("Second workstation received msgHereIsGlass", w2.log.getLastLoggedEvent().getMessage().contains("msgHereIsGlass"));
		
		assertTrue("Both workstations should be full", popup.workstations.get(0).status == WorkstationStatus.isFull
				&& popup.workstations.get(1).status == WorkstationStatus.isFull);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have gotten msgWorkstationsFull", previousComponent.log.getLastLoggedEvent().getMessage().contains("msgWorkstationsFull") &&
				previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));
		
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		
		
		popup.pickAndExecuteAnAction();
		
		
		popup.msgComeUp(w1);
		popup.pickAndExecuteAnAction();
		
//		assertTrue("Previous component should have gotten msgWorkstationFull false", previousComponent.log.getLastLoggedEvent().getMessage().contains("msgWorkstationsFull") &&
//				previousComponent.log.getLastLoggedEvent().getMessage().contains("false"));
		
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("Workstation received message that popup is up", w1.log.getLastLoggedEvent().getMessage().contains("msgIAmUp"));
		
		glass1.recipe.workstations.put(WorkStation.Drill, true);
		popup.msgHereIsGlass(glass1, w1);
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.msgComeUp(w2);
		
		assertTrue("Second workstation should have status isDONE", popup.workstations.get(1).status == WorkstationStatus.isDone);
		
		popup.pickAndExecuteAnAction();
				
		Factory.transducer.processNextEvent();
		
//		assertTrue("MockGUI should have recieved POPUP_DO_MOVE_DOWN", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_DO_MOVE_DOWN"));

		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, myID);
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, myID);
		
		popup.pickAndExecuteAnAction();
		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have recieved POPUP_RELEASE_GLASS", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_RELEASE_GLASS"));
		
		popup.pickAndExecuteAnAction();
		
		Factory.transducer.processNextEvent();
		
		assertTrue("MockGUI should have recieved POPUP_DO_MOVE_UP", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_DO_MOVE_UP"));
		
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_UP, myID);
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("Requesting workstations should have 1 element 'w2'", popup.requestingWorkstations.contains(w2));

		
		assertTrue("Workstation received message that popup is up", w2.log.getLastLoggedEvent().getMessage().contains("msgIAmUp"));
		
		glass2.recipe.workstations.put(WorkStation.Drill, true);
		popup.msgHereIsGlass(glass2, w2);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		
		
		
		popup.pickAndExecuteAnAction();
		
		Factory.transducer.processNextEvent();
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, myID);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, myID);

		popup.pickAndExecuteAnAction();
		}

	public void testFullWorkstationsWhileOneGlassMovesThrough(){
		popup.msgHereIsGlass(glass1);
		popup.pickAndExecuteAnAction();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		popup.pickAndExecuteAnAction();
		
		//second piece of glass
		popup.msgHereIsGlass(glass2);
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();		
		popup.pickAndExecuteAnAction();		
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		popup.pickAndExecuteAnAction();
		
		assertTrue("popup is down and empty", popup.status == PopupStatus.DOWN_EMPTY);
		
		popup.msgHereIsGlass(glass3);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, myID);

		popup.pickAndExecuteAnAction();

		assertTrue("next component received msgHereIsGlass", nextComponent.log.containsString("msgHereIsGlass"));
		
		Factory.transducer.processNextEvent();
		
		assertTrue("PopupStatus should be DOWN_EMPTY", popup.status == PopupStatus.DOWN_EMPTY);

		assertTrue("MockGUI should have recieved POPUP_RELEASE_GLASS", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_RELEASE_GLASS"));

		assertTrue("theGlass is null", popup.theGlass == null);
		
		
		assertTrue("previous conveyor receive message that space is available", previousComponent.log.getLastLoggedEvent().getMessage().contains("msgIHaveSpaceAvailable")
					&& previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));
		
		assertTrue("schedulder should return false", !popup.pickAndExecuteAnAction());
		
	}
	
	public void testGlassNotGoingToWorkstation(){
		
		popup.msgHereIsGlass(glass1);
		popup.pickAndExecuteAnAction();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		popup.pickAndExecuteAnAction();
		
		//second piece of glass
		popup.msgHereIsGlass(glass2);
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		popup.msgComeUp(w1);
		popup.pickAndExecuteAnAction();
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, myID);
		popup.pickAndExecuteAnAction();		
		popup.pickAndExecuteAnAction();		
		Factory.transducer.processNextEvent();
		popup.eventFired(TChannel.POPUP,TEvent.POPUP_GUI_MOVED_DOWN, myID);
		popup.pickAndExecuteAnAction();
		
		assertTrue("popup is down and empty", popup.status == PopupStatus.DOWN_EMPTY);
		
		popup.msgHereIsGlass(glass3);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, myID);
		
		//assertTrue("Popup's lockDown should be true", popup.lockDown);
		
		popup.msgComeUp(w1);
		
		assertTrue("w1's status should be isDone", popup.workstations.get(0).status == WorkstationStatus.isDone);
		
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, myID);
		
		popup.pickAndExecuteAnAction();

		assertTrue("next component received msgHereIsGlass", nextComponent.log.containsString("msgHereIsGlass"));
		
		//assertTrue("Popup's lockDown should be false", !popup.lockDown);
		
		Factory.transducer.processNextEvent();

		assertTrue("MockGUI should have recieved POPUP_RELEASE_GLASS", gui.log.getLastLoggedEvent().getMessage().contains("POPUP_RELEASE_GLASS"));		
		
		assertTrue("theGlass is null", popup.theGlass == null);
		
		assertTrue("PopupStatus should be DOWN_EMPTY", popup.status == PopupStatus.DOWN_EMPTY);
		
		assertTrue("previous conveyor receive message that space is available", previousComponent.log.getLastLoggedEvent().getMessage().contains("msgIHaveSpaceAvailable")
					&& previousComponent.log.getLastLoggedEvent().getMessage().contains("true"));
		
		popup.pickAndExecuteAnAction();
		
		assertTrue("w1 should have been added to requestingWorkstations", popup.requestingWorkstations.contains(w1));
		
		assertTrue("w1's status should be isWaitingForPopup", popup.workstations.get(0).status == WorkstationStatus.isWaitingForPopup);

		//now need to make sure that the popup goes to w1
		popup.pickAndExecuteAnAction();
		
		
	}
	
	//add scenario where popup recieves glass to go to workstation and at same time, a workstation tells it it's done
}
