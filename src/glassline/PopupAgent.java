package glassline;

import glassline.interfaces.Popup;
import glassline.interfaces.PopupConveyor;
import glassline.interfaces.Workstation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class PopupAgent extends FactoryAgent implements Popup, TReceiver {

	public enum AlertStatus {NeedAlert, Alerted, RemoveAlert, None};
	public AlertStatus alertStatus = AlertStatus.None;
	
	public enum WorkstationStatus {isFull, isDone, isWaitingForPopup, isEmpty};
	public class MyWorkstation{
		public WorkstationStatus status;
		Workstation work;

		public MyWorkstation(Workstation w, WorkstationStatus s){
			status = s;
			work = w;
		}
	}
	//list of workstations associated with the popup
	public List<MyWorkstation> workstations = new ArrayList<MyWorkstation>();

	//ID to be used with args in the transducer
	Object[] myID = new Object[1];

	public boolean alert = false;
	public PopupConveyor previousComponent;
	
	public Workstation currentWorkstation;

	public enum PopupStatus {LOADING, COME_UP, COME_DOWN, WAITING_FOR_UP, WAITING_FOR_DOWN, LOCK_DOWN, FROM_WORKSTATION, FROM_CONVEYOR, UP_EMPTY, UP_FULL, DOWN_EMPTY, DOWN_FULL, UP_REQUESTED};
	public enum GlassStatus {onPopup, inWorkstation};

	public class MyGlass{
		public Glass glass;
		//MySensor location;
		public GlassStatus state;


		public MyGlass(Glass g,  GlassStatus s){
			glass = g;
			//location = m;
			state = s;
		}
	}

	public MyGlass theGlass;
	Transducer transducer;
	public PopupStatus status;

	//list of workstations requesting popup to come up
	public List<Workstation> requestingWorkstations = new ArrayList<Workstation>();

	public boolean fromConveyor;

	private Semaphore glassRelease = new Semaphore(0);
	private Semaphore loadGlass = new Semaphore(0);
	
	//constructor
	public PopupAgent(Transducer t, int i){
		super(i);
		transducer = t;
		status = PopupStatus.DOWN_EMPTY;
		transducer.register(this, TChannel.POPUP);
		myID[0] = componentNumber;
	}

	//assigns a workstation to this PopupAgent
	public void addWorkstation(Workstation w){
		MyWorkstation work = new MyWorkstation(w, WorkstationStatus.isEmpty);
		workstations.add(work);
	}

	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		if (status != PopupStatus.LOADING && status != PopupStatus.WAITING_FOR_DOWN && status != PopupStatus.WAITING_FOR_UP) {
			if (on) {
				if (alertStatus == AlertStatus.NeedAlert) {
					tellConveyorToStop(true);
					return true;
				}

				else if (alertStatus == AlertStatus.RemoveAlert) {
					tellConveyorToStop(false);
					return true;
				}

				//if popup is up and empty, just go down
				if (status == PopupStatus.UP_EMPTY
						&& requestingWorkstations.isEmpty()) {
					goDown();
					return true;
				}

				//if popup is up and full,
				//if the glass in the popup needs to go to either of the workstations,
				//give glass to correct workstation
				//otherwise, go down
				else if (status == PopupStatus.UP_FULL) {


					for (MyWorkstation w : workstations) {
						if (w.status == WorkstationStatus.isEmpty) {
							if (theGlass.glass.recipe.workstations
									.containsKey(w.work.getType())) {
								if (theGlass.glass.recipe.workstations
										.get(w.work.getType()) == false) {
									giveGlassToWorkstation(w);
									return true;
								}
							}

							goDown();
							return true;
						}
					}
				}

				//if popup is down and full,
				//if the glass in the popup needs to go to either of the workstations,
				//go up so that they can be given
				//otherwise, give glass back to conveyor
				else if (status == PopupStatus.DOWN_FULL) {


					for (MyWorkstation w : workstations) {
						if (theGlass.glass.recipe.workstations
								.containsKey(w.work.getType())) {
							if (theGlass.glass.recipe.workstations.get(w.work
									.getType()) == false) {
								goUp(null);
								return true;
							}
						}
					}
					if(this.nextSpaceAvailable())
					{
						giveGlassToConveyor();
						return true;
					}
					

				}

				//if popup animation going down has finished,
				//tell conveyor
				else if (status == PopupStatus.COME_DOWN) {
					tellConveyorIAmDown();
					return true;
				}

				//if popup animation going up has finished,
				//tell workstation
				else if (status == PopupStatus.COME_UP) {
					tellWorkstationIAmUp();
					return true; //should this be here?
				}

				else if (requestingWorkstations.isEmpty()) {
					for (MyWorkstation w : workstations) {
						if (w.status == WorkstationStatus.isDone) {
							goUp(w);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	//Messages
	
	//glass recieved from a conveyor
	@Override
	public void msgHereIsGlass(Glass glass) {
		Do("recieved " + glass + " from a conveyor");
		theGlass = new MyGlass(glass, GlassStatus.onPopup);
		status = PopupStatus.LOADING; //implies PopupStatus.DOWN_FULL
		fromConveyor = true;
		loadGlass.release();
		//TODO: Make the popup tell conveyor it has no space available as soon as it receives glass.
		//right now this only happens while the popup moves up
		
		//if both workstations full, set alertStatus to NeedAlert
		for(MyWorkstation w: workstations){
				if(theGlass.glass.recipe.workstations.containsKey(w.work.getType())){
					if(theGlass.glass.recipe.workstations.get(w.work.getType()) == false){
						
						for(MyWorkstation work: workstations){
							if(work.status == WorkstationStatus.isFull){
								alertStatus = AlertStatus.NeedAlert;
							}
						}

				}
				}
		}
		
		stateChanged();
	}

	//glass recieved from a workstation
	@Override
	public void msgHereIsGlass(Glass glass, Workstation workstation) {
		Do("recieved " + glass + " from a workstation");
		theGlass = new MyGlass(glass, GlassStatus.onPopup); //implies PopupStatus.UP_FULL
		status = PopupStatus.LOADING;
		fromConveyor = false;
		currentWorkstation = workstation;
		
		loadGlass.release();

		
		
		
			if(alertStatus == AlertStatus.Alerted){
				alertStatus = AlertStatus.RemoveAlert;
			}
		stateChanged();
	}

	//message telling popup to go up
	public void msgComeUp(Workstation w){
		Do("recieved msgComeUp");
		for (MyWorkstation myW: workstations) {
			if (myW.work == w) {
				myW.status = WorkstationStatus.isDone;
				stateChanged();
			}
		}
	}

	//Actions
	public void tellConveyorThereIsSpace(boolean choice){
		if(choice)
			Do("Telling " + previousComponent + " there is space");
		else{
			Do("Telling " + previousComponent + " there is not space");
			alert = true;
		}
		previousComponent.msgIHaveSpaceAvailable(choice);
	}

	public void tellConveyorToStop(boolean choice){
		if(choice) {
			Do("Telling " + previousComponent + " workstations are full.");
			alertStatus = AlertStatus.Alerted;
		}
		else {
			Do("Telling " + previousComponent + " workstations are not full.");
			alertStatus = AlertStatus.None;
		}		
		previousComponent.msgWorkstationsFull(choice);
		stateChanged();
	}
	
	//gives glass to workstation w and sets it to full
	public void giveGlassToWorkstation(MyWorkstation w){
		Do("Giving " + theGlass.glass + " to " + w.work);
		if (!requestingWorkstations.isEmpty()) {
			status = PopupStatus.COME_UP;
		} else status = PopupStatus.UP_EMPTY;
		w.status = WorkstationStatus.isFull;
		w.work.msgHereIsGlass(theGlass.glass);
		theGlass = null;
		stateChanged();
	}

	//gives glass to conveyor and sets popup to down and empty
	public void giveGlassToConveyor(){
		Do("Giving " + theGlass.glass + " to conveyor " + nextComponent);
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, myID);
		try {
			glassRelease.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do(theGlass.glass + " successfully released");
		status = PopupStatus.DOWN_EMPTY;
		nextComponent.component.msgHereIsGlass(theGlass.glass);
		theGlass = null;
		tellConveyorThereIsSpace(true);
		stateChanged();
	}

	//sends proper msgIAmUp to conveyor based on whether or not the workstations or full
	//also adds workstation requesting it to go up (if it exists) to proper list
	public void goUp(MyWorkstation w){
		Do("Popup going up");
		if (status != PopupStatus.DOWN_FULL) {
			tellConveyorThereIsSpace(false);
		}
		status = PopupStatus.WAITING_FOR_UP;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, myID);
		
		if(w != null) {
			w.status = WorkstationStatus.isWaitingForPopup;
			requestingWorkstations.add(w.work);
		}
		stateChanged();
	}

	//goes down
	public void goDown(){
		Do("Popup going down");
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, myID);
		status = PopupStatus.WAITING_FOR_DOWN;
		stateChanged();
	}


	public void tellConveyorIAmDown(){
		Do("Telling " + previousComponent + " I am down and have space");
		status = PopupStatus.DOWN_EMPTY;
		previousComponent.msgIHaveSpaceAvailable(true);
		stateChanged();
	}

	//messages workstation popup is up
	public void tellWorkstationIAmUp(){
		Do("Telling " + requestingWorkstations.get(0) + " I am up");
		status = PopupStatus.UP_EMPTY;
		requestingWorkstations.get(0).msgIAmUp();
		stateChanged();
	}

	//eventFired method for transducer
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel == TChannel.POPUP)
		{
			if(event == TEvent.POPUP_GUI_MOVED_DOWN)
			{
				if(args[0].equals(myID[0])){
					if(theGlass == null)
						status = PopupStatus.COME_DOWN;
					else
						status = PopupStatus.DOWN_FULL;
					stateChanged();
				}
			}

			else if(event == TEvent.POPUP_GUI_MOVED_UP){
				if(args[0].equals(myID[0])){
					if(theGlass == null)
						status = PopupStatus.COME_UP;
					else
						status = PopupStatus.UP_FULL;
					stateChanged();
				}
			}

			else if(event == TEvent.POPUP_GUI_LOAD_FINISHED){
				if(args[0].equals(myID[0])){
					try {
						loadGlass.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(fromConveyor){
						status = PopupStatus.DOWN_FULL;
						tellConveyorThereIsSpace(false);
					}

					else{
						status = PopupStatus.UP_FULL;
						
						for(MyWorkstation w: workstations){
							if(w.work == currentWorkstation){
								w.status = WorkstationStatus.isEmpty;
								requestingWorkstations.remove(w.work);
							}
						}
					}
					stateChanged();
				}
			} else if (event == TEvent.POPUP_GUI_RELEASE_FINISHED) {
				if(args[0].equals(myID[0])){
					glassRelease.release();
				}
			}
		}
	}

	//toString method for testing purposes
	public String toString()
	{
		return ("Popup " + myID[0]);
	}

	public void setPreviousComponent(PopupConveyor p){
		previousComponent = p;
	}



}


