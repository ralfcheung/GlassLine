package glassline.interfaces;

public interface Conveyor extends FactoryComponent {

	public abstract void msgSensedGlass(Sensor sensor, boolean sensing);

}