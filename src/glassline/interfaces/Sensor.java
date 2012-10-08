package glassline.interfaces;

public interface Sensor {

	/*
	 * Called when GUISensor senses glass.
	 */
	public abstract void msgSensedGlass(boolean sensing);

	public abstract void msgIAmOn(boolean b);

}