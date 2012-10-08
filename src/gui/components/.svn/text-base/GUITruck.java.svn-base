
package gui.components;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * The truck collects glass that has been taken off of the line collecting three
 * glass parts and emptying itself on the fourth
 */
@SuppressWarnings("serial")
public class GUITruck extends GuiComponent
{
	/**
	 * Image of default truck
	 */
	ImageIcon truckLeft = new ImageIcon("imageicons/truck/truckV3Image.png");

	/**
	 * Image of truck facing right
	 */
	ImageIcon truckRight = new ImageIcon("imageicons/truckRight.png");

	/**
	 * Trucks original location
	 */
	Point truckOriginalLoc;

	/**
	 * Trucks part
	 */
	GUIGlass guiPart;
	List<GUIGlass> glassList = Collections.synchronizedList(new ArrayList<GUIGlass>());

	enum TruckState
	{
		LOADING, LEAVING, RETURNING
	};

	TruckState state;

	/**
	 * Trucks original location
	 */
	Point truckOrig;

	/** Public constructor for GUITruck */
	public GUITruck(Transducer t, int x, int y)
	{
		setIcon(truckLeft);
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
		state = TruckState.LOADING;
		truckOrig = new Point(x, y);
		transducer = t;
		transducer.register(this, TChannel.TRUCK);
	}

	/**
	 * Moving the stack of glass to and from the truck to the bin, depending on whether
	 * glass is being taken from bin or bin is being refilled
	 */
	public void movePartIn()
	{
		if (part.getCenterX() < getCenterX())
			part.setCenterLocation(part.getCenterX() + 1, part.getCenterY());
		else if (part.getCenterX() > getCenterX())
			part.setCenterLocation(part.getCenterX() - 1, part.getCenterY());

		if (part.getCenterY() < getCenterY())
			part.setCenterLocation(part.getCenterX(), part.getCenterY() + 1);
		else if (part.getCenterY() > getCenterY())
			part.setCenterLocation(part.getCenterX(), part.getCenterY() - 1);

		if (part.getCenterX() == getCenterX() && part.getCenterY() == getCenterY())
		{
			glassList.add(part);
			part.setVisible(false);
			part = null;
			transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_GUI_LOAD_FINISHED, null);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (state == TruckState.LOADING && part != null)
		{
			movePartIn();
		}
		if (state == TruckState.LEAVING)
		{
			moveTruckOut();
		}
		if (state == TruckState.RETURNING)
		{
			moveTruckIn();
		}
	}

	private void moveTruckOut()
	{
		setCenterLocation(getCenterX() + 2, getCenterY());
		if (getCenterX() > parent.getParent().getGuiParent().getWidth())
		{
			state = TruckState.RETURNING;
		}
	}

	private void moveTruckIn()
	{
		setCenterLocation(getCenterX() - 1, getCenterY());
		if (getCenterX() < truckOrig.getX() + (this.getIcon().getIconWidth()/2))
		{
			state = TruckState.LOADING;
			transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_GUI_EMPTY_FINISHED, null);
			for(GUIGlass g : glassList){
				transducer.unregister(g, TChannel.GLASS);
			}
			glassList.clear();
		}
	}

	@Override
	public void addPart(GUIGlass part)
	{
		this.part = part;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args)
	{
		if (event == TEvent.TRUCK_DO_EMPTY)
		{
			state = TruckState.LEAVING;
		}
	}
}
