package glassline.interfaces;

import glassline.Glass;
import glassline.Glass.WorkStation;

public interface Workstation {

	/* method msgHereIsGlass(Glass g)
	 * 
	 * Gives the workstation a piece of glass
	 */
	public abstract void msgHereIsGlass(Glass g);

	/*method msgIAmUp()
	 * 
	 * tells the workstation the poup is up
	 */
	public abstract void msgIAmUp();
	
	/*method getType()
	 * 
	 * returns the type of workstation
	 */
	public abstract WorkStation getType();
	
	public abstract void msgIAmBusy(boolean b);

}