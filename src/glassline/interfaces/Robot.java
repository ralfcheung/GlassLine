package glassline.interfaces;

import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;

import java.util.ArrayList;

public interface Robot extends FactoryComponent
{	
	public abstract void msgGetGlassFromBin(GlassShape shape, ArrayList<WorkStation> stations);

}
