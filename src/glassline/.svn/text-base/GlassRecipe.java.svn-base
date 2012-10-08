package glassline;

import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlassRecipe {
	public GlassShape shape;
	public Map<WorkStation, Boolean> workstations = new HashMap<WorkStation, Boolean>();
	
	public GlassRecipe(GlassShape shape, ArrayList<WorkStation> stations) {
		this.shape = shape;
		
		// Initialize everything to false.
		for (WorkStation ws : stations) {
			workstations.put(ws, false);
		}
	}
}
