package glassline;

import java.util.concurrent.Semaphore;

import glassline.Glass.GlassActivity;
import glassline.interfaces.OnlineComponent;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;


public class OnlineAgent extends FactoryAgent implements OnlineComponent, TReceiver {

	public TChannel channel = null;
	GlassActivity activity = null;
	Semaphore semaphore = new Semaphore(0);

	public OnlineAgent(TChannel channel, GlassActivity activity) {
		super(0);

		this.channel = channel;
		this.activity = activity;

		// Register channel with the transducer
		Factory.transducer.register(this, channel);
	}

	public class MyGlass {
		Glass g;
		public boolean done = false;

		public MyGlass(Glass g) {
			this.g = g;
		}
	}
	public MyGlass currentGlass = null;

	// Messages
	/* (non-Javadoc)
	 * @see glassline.Cutter#msgHereIsGlass(glassline.Glass)
	 */
	@Override
	public void msgHereIsGlass(Glass g) {
		if (currentGlass != null) {
			print("ERROR: Inline components can't hold 2 pieces of glass!");
		}

		previousComponent.msgIHaveSpaceAvailable(false);
		currentGlass = new MyGlass(g);
		stateChanged();
	}

	/* (non-Javadoc)
	 * @see glassline.Cutter#msgDoneCuttingGlass()
	 */
	@Override
	public void msgDone() {
		currentGlass.done = true;
		stateChanged();
	}

	/* (non-Javadoc)
	 * @see glassline.Cutter#pickAndExecuteAnAction()
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		// Rule #1
		if (currentGlass != null && currentGlass.done && nextSpaceAvailable()) {
			releaseGlass();
			return true;
		}
		return false;
	}

	// Transducer event listener.
	/* (non-Javadoc)
	 * @see glassline.Cutter#eventFired(transducer.TChannel, transducer.TEvent, java.lang.Object[])
	 */
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (this.channel == channel) {
			if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
				currentGlass.g.addActivity(activity); // Log activity on the glass
				Object[] args1 = {currentGlass.g.glassNumber};
				switch(this.activity){
				case Breakout:
					switch(currentGlass.g.recipe.shape){
					case Square:
						Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_SQUARE, args1);
						break;
					case Circle:
						Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_CIRCLE, args1);
						break;
					case Triangle:
						Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_TRIANGLE, args1);
						break;
					}
					break;
				case Cutter:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_CUTTER, args1);
					break;
				case CrossSeamer:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_CROSSSEAMER, args1);
					break;
				case Driller:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_DRILL, args1);
					break;
				case Oven:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_OVEN, args1);
					break;
				case Painter:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_PAINT, args1);
					break;
				case UVLamp:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_UVLAMP, args1);
					break;
				case Washer:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_WASHER, args1);
					break;

				}
			}

			this.msgDone();
		}
		if(event == TEvent.WORKSTATION_RELEASE_FINISHED){
			semaphore.release();
		}
	}

	private void releaseGlass() {
		Do("releasing " + currentGlass.g + " to " + nextComponent.component);
		try{
			Factory.transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
			semaphore.acquire();
			nextComponent.component.msgHereIsGlass(currentGlass.g);
			currentGlass = null;
			previousComponent.msgIHaveSpaceAvailable(true);

		}catch (InterruptedException ex) {
			ex.printStackTrace();
		}finally{
		}
	}

	public String toString() {
		return activity.name();
	}
}
