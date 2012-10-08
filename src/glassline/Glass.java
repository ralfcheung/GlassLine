package glassline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class Glass implements TReceiver {
	int glassNumber;
	Transducer myTransducer;
	public enum GlassActivity {
		Cutter,
		Breakout,
		ManualBreakout,
		Driller,
		CrossSeamer,
		Grinder,
		Washer,
		Painter,
		UVLamp,
		Oven,
		// What has been done to this glass?
	}
	List<GlassActivity> activity = Collections.synchronizedList(new ArrayList<GlassActivity>());

	public enum WorkStation {
		Drill,
		CrossSeamer,
		Grinder
		// etc, any workstations or components that may or may not operate on the glass.
	}

	// TODO: Adapt this to the capabilities of the animation
	public enum GlassShape {
		Square,
		Circle,
		Triangle,
	}

	public enum GlassCondition {
		//Raw, // Glass is only raw while moving from the robot to the conveyor, I don't think this is necessary.
		Broken,
		Processing,
		Done,
	}
	Map<GlassCondition, Boolean> condition = new HashMap<GlassCondition, Boolean>();

	public GlassRecipe recipe;
	
	public GlassShape shape;

	public Glass(GlassShape shape, ArrayList<WorkStation> stations, int number) {
		// Initialize all conditions to false.
		for (GlassCondition cond : GlassCondition.values()) {
			condition.put(cond, false);
		}
		// Initialize recipe.
		recipe = new GlassRecipe(shape, stations);
		glassNumber = number;
	}
	
	public Glass(GlassShape shape, ArrayList<WorkStation> stations, int number, Transducer t){
		// Initialize all conditions to false.
		myTransducer = t;
		t.register(this, TChannel.GLASS);
		for (GlassCondition cond : GlassCondition.values()) {
			condition.put(cond, false);
		}
		// Initialize recipe.
		recipe = new GlassRecipe(shape, stations);
		glassNumber = number;
		this.shape = shape;
	}

	public void addActivity(GlassActivity ga) {
		activity.add(ga);
	}

	/*
	// Should never need this.
	public void removeActivity(GlassActivity ga) {
		activity.remove(ga);
	}
	 */

	public void addCondition(GlassCondition gc) {
		condition.put(gc, true);
	}

	public void removeCondition(GlassCondition gc) {
		condition.put(gc, false);
	}

	// Returns true if it should stop at the specified work station, false otherwise.
	public boolean shouldStopAtStation(WorkStation ws) {
		return this.recipe.workstations.containsKey(ws);
	}

	public String toString() {
		return "glass " + glassNumber;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(event == TEvent.GLASS_GET_STATUS){
			if(Integer.valueOf((String)args[0]) == glassNumber){
				Object[] arg0 = {glassNumber, shape, activity, recipe}; //need to figure out if glass has gone to workstation
				myTransducer.fireEvent(TChannel.GLASS, TEvent.GLASS_STATUS_UPDATE, arg0);
			}
		}
	}
}
