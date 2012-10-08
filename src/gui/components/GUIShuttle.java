
package gui.components;

import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import shared.enums.ConveyorDirections;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * GUIConveyorConnector is a graphical representation of the conveyer connector
 * component connecting conveyors
 */
@SuppressWarnings("serial")
public class GUIShuttle extends GuiComponent
{

	/** the current GUI glass part */
	GUIGlass currentPart;

	/** a boolean that controls whether the line is moving */
	boolean moving = true;

	/** a number that tracks how far a GUIPart has moved */
	int moveCounter = 0;

	int movementQueue = 0;

	/** a boolean to track whether the GUIPart the GUIConveyorConnector has is snapped to the center */
	boolean doneAligning = false;

	/** the direction of the conveyor connector */
	public ConveyorDirections direction;

	/** An int used to represent X-velocity */
	private int vX;

	/** An int used to represent Y-velocity */
	private int vY;

	int myIndex;
	
	enum State {IDLE, MOVING_IN, MOVING_OUT};
	State state = State.IDLE;
	
	boolean releasePart = true;
	
	/** a boolean to determine if this GUIConveyorConnector rotates the GUIPart */
	boolean rotates = false;

	/** a boolean to check if the GUIPart has finished being rotated */
	boolean doneRotating = false;

	int rotationDegrees = 0;

	public static ImageIcon upConnector = new ImageIcon("imageicons/conveyorConnectorImage_up.png");

	public static ImageIcon downConnector = new ImageIcon("imageicons/conveyorConnectorImage_down.png");

	public static ImageIcon leftConnector = new ImageIcon("imageicons/conveyorConnectorImage_left.png");

	public static ImageIcon rightConnector = new ImageIcon("imageicons/conveyorConnectorImage_right.png");

	/** checks whether the GUIPart on this connector needs to be trashed */
	boolean trashPart = false;
	
	/**
	 * A constructor for GUIConveyorConnector that takes in a cardinal direction
	 * and sets the correct direction-image and velocity direction
	 * @param dir
	 *        the conveyor direction
	 */
	public GUIShuttle(ConveyorDirections dir)
	{
		direction = dir;
		this.rotates = true;

		if (direction == ConveyorDirections.UP)
		{
			setIcon(upConnector);
			setvX(0);
			setvY(-6);
		}
		else if (direction == ConveyorDirections.DOWN)
		{
			setIcon(downConnector);
			setvX(0);
			setvY(6);
		}
		else if (direction == ConveyorDirections.LEFT)
		{
			setIcon(leftConnector);
			setvX(-6);
			setvY(0);
		}
		else if (direction == ConveyorDirections.RIGHT)
		{
			setIcon(rightConnector);
			setvX(6);
			setvY(0);
		}
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
	}

	/**
	 * A constructor for corner GUIConveyorConnectors that takes in a cardinal direction
	 * and sets the correct direction-image and velocity direction, and says it rotates
	 * 
	 * @param dir
	 *        the conveyor direction
	 * @param rotates
	 *        this GUIConveyorConnector rotates the part
	 */
	public GUIShuttle(ConveyorDirections dir, boolean rotates, int index, Transducer t)
	{
		direction = dir;
		this.rotates = true;
		myIndex = index;
		transducer = t;
		t.register(this, TChannel.SHUTTLE);

		if (direction == ConveyorDirections.UP)
		{
			setIcon(upConnector);
			setvX(0);
			setvY(-6);
		}
		else if (direction == ConveyorDirections.DOWN)
		{
			setIcon(downConnector);
			setvX(0);
			setvY(6);
		}
		else if (direction == ConveyorDirections.LEFT)
		{
			setIcon(leftConnector);
			setvX(-6);
			setvY(0);
		}
		else if (direction == ConveyorDirections.RIGHT)
		{
			setIcon(rightConnector);
			setvX(6);
			setvY(0);
		}
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
		// guiPartMover = new GUIPartMover();
	}

	/**
	 * Moves a GUIPart one block in a direction.
	 * Before moving a block, calls alignPart to make sure the part is squarely on the conveyor.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		if (state == State.MOVING_IN)
		{
			movePartIn();
		}
		if (state == State.MOVING_OUT) {
			//System.out.println("Shuttle " + myIndex +  " moving out");
			movePartOut();
		}
	}
	
	private void movePartIn()
	{
			if (direction == ConveyorDirections.UP)
			{
				if (part.getCenterX() > getCenterX())
				{
					part.setCenterLocation(part.getCenterX() - 1, part.getCenterY());
				}
				else
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() - 1);
				}
			}
			else if (direction == ConveyorDirections.DOWN)
			{
				if (part.getCenterX() < getCenterX())
				{
					part.setCenterLocation(part.getCenterX() + 1, part.getCenterY());
				}
				else
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() + 1);
				}
			}
			else if (direction == ConveyorDirections.LEFT)
			{
				if (part.getCenterY() < getCenterY())
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() + 1);
				}
				else
				{
					part.setCenterLocation(part.getCenterX() - 1, part.getCenterY());
				}
			}
			else if (direction == ConveyorDirections.RIGHT)
			{
				if (part.getCenterY() > getCenterY())
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() - 1);
				}
				else
				{
					part.setCenterLocation(part.getCenterX() + 1, part.getCenterY());
				}
			}
		if (part.getCenterX() == getCenterX() && part.getCenterY() == getCenterY())
		{
			state = State.MOVING_OUT;
		}
	}
	
	private void movePartOut() {
		if (releasePart)
		{
			//System.out.println("Shuttle " + myIndex +  "release part");
			if (direction == ConveyorDirections.UP)
			{
				if (part.getCenterX() > getCenterX())
				{
					part.setCenterLocation(part.getCenterX() - 1, part.getCenterY());
				}
				else
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() - 1);
				}
			}
			else if (direction == ConveyorDirections.DOWN)
			{
				if (part.getCenterX() < getCenterX())
				{
					part.setCenterLocation(part.getCenterX() + 1, part.getCenterY());
				}
				else
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() + 1);
				}
			}
			else if (direction == ConveyorDirections.LEFT)
			{
				if (part.getCenterY() < getCenterY())
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() + 1);
				}
				else
				{
					part.setCenterLocation(part.getCenterX() - 1, part.getCenterY());
				}
			}
			else if (direction == ConveyorDirections.RIGHT)
			{
				if (part.getCenterY() > getCenterY())
				{
					part.setCenterLocation(part.getCenterX(), part.getCenterY() - 1);
				}
				else
				{
					part.setCenterLocation(part.getCenterX() + 1, part.getCenterY());
				}
			}
		}
		if (!part.getBounds().intersects(getBounds()))
		{
			state = State.IDLE;
			nextComponent.addPart(part);
			//System.out.println("GUIShuttle " + myIndex + " passing glass onward " + part.getName());
			part = null;
			Object[] array = new Object[1];
			array[0] = myIndex;
			
			transducer.fireEvent(TChannel.SHUTTLE, TEvent.SHUTTLE_GLASS_RELEASED, array);
		}
	}
	
	public void addPart(GUIGlass part)
	{
		this.part = part;
		
		state = State.MOVING_IN;
		doneAligning = false;
		doneRotating = false;
		//System.out.println("GUIShuttle " + myIndex + " recieved glass " + part.getName());
	}

	/**
	 * This message tells GUIConveyorConnector to initiate a one-block move
	 */
	public void msgDoMoveOneBlock()
	{
		if (!moving)
		{

			moving = true;
			moveCounter = 0;
		}
		else
		{
			movementQueue++;
		}
	}

	/**
	 * lets the backend agent give the frontend GUI a reference to the agent
	 * @param agent
	 *        the agent for this GUIConveyorConnector
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
	}

	public int getvX()
	{
		return vX;
	}

	public void setvX(int vX)
	{
		this.vX = vX;
	}

	public int getvY()
	{
		return vY;
	}

	public void setvY(int vY)
	{
		this.vY = vY;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args)
	{
		if (channel == TChannel.SHUTTLE && (Integer)args[0] == myIndex) {
			if (event == TEvent.SHUTTLE_STOPPED) {
				releasePart = false;
			}
			if (event == TEvent.SHUTTLE_CAN_START) {
				releasePart = true;
			}
		}
	}
}
