package glassline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import glassline.interfaces.FactoryComponent;

public class TruckAgent extends FactoryAgent implements FactoryComponent, TReceiver{
	
	final int MAX_LOAD = 20;
	Transducer myTransducer;
	List<Glass> truckGlass = Collections.synchronizedList(new ArrayList<Glass>());
	enum TruckState {LOADING, BUSY, FULL,  LEAVING, RETURNING};
	TruckState state;
	Object[] args = new Object[1];

	TruckAgent(Transducer t, int id) {
		super(id);
		myTransducer = t;
		t.register(this, TChannel.TRUCK);
		state = TruckState.LOADING;
	}

	public String toString(){
		return "Truck";
	}

	@Override
	public void msgHereIsGlass(Glass g) {
		truckGlass.add(g);
		previousComponent.msgIHaveSpaceAvailable(false);
		state = TruckState.BUSY;
		myTransducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_LOAD_GLASS, args);
		stateChanged();
	}

	@Override
	protected boolean pickAndExecuteAnAction() {
		if(state == TruckState.FULL){
			print("Truck is full");
			state = TruckState.LEAVING; 
			myTransducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY, args);
			previousComponent.msgIHaveSpaceAvailable(false);
			return true;
		}
		if(state == TruckState.LOADING){
			previousComponent.msgIHaveSpaceAvailable(true);
			state = TruckState.BUSY;
			return true;
		}
		else if(state == TruckState.RETURNING){
			truckGlass.clear();
			state = TruckState.LOADING;
			previousComponent.msgIHaveSpaceAvailable(true);
			return true;
		}
		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args){
		if(event == TEvent.TRUCK_GUI_EMPTY_FINISHED){
			print("Truck is empty");
			state = TruckState.RETURNING;
			stateChanged();
		}
		else if(event == TEvent.TRUCK_GUI_LOAD_FINISHED){
			if(truckGlass.size() == MAX_LOAD){
				state = TruckState.FULL;
			}
			else{
				state = TruckState.LOADING;
			}
			stateChanged();
		}
	}
}
