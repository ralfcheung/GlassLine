package gui.components;

import glassline.Glass.GlassShape;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import shared.enums.ComponentOperations;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

/**
 * GUIPart is a graphical representation of the glass that
 * is to be removed around on the conveyer
 * @author Anoop Kamboj
 *
 */
@SuppressWarnings("serial")
public class GUIGlass extends GuiComponent implements Serializable, TReceiver, MouseListener
{
	Transducer myTransducer;
	/**
	 * The rectangle of the glass pane
	 */
	Rectangle2D glassRect;
	/**
	 * Boolean to reflect if the part is broken or not. Initialized to false.
	 */
	boolean stateBroken = false;
	/**
	 * Enum that contains the last operation done to the glass
	 */
	ComponentOperations lastOperation;
	/**
	 * A number representing the part's position (block) on the conveyor
	 */
	int posInLine;
	/**
	 * The current angle of the part
	 */
	double currentAngle;
	/**
	 * The angle that the part needs to change to
	 */
	double targetAngle;
	/**
	 * Boolean to tell whether or not the part is done rotating
	 */
	boolean doneRotating;
	/**
	 * The speed that the part should rotate
	 */
	double rotationSpeed;
	/**
	 * File names for each of the various images for glass
	 */
	String filePathNONE = "imageicons/glassImage_NONE.png";//Base image
	String filePathCircle = "imageicons/glassImage_NONE_circle.png"; //Circle image
	String filePathTriangle = "imageicons/glassImage_NONE_triangle.png"; //Triangle image
	/**
	 * Instances of image holder that hold the overlays for the glass for breakout
	 */
	ImageHolder imageBREAKOUT;
	/**
	 * Instances of image holder that hold the overlays for the glass for manual breakout
	 */
	ImageHolder imageMANUALBREAKOUT;
	/**
	 * Instances of image holder that hold the overlays for the glass for cross seamer
	 */
	ImageHolder imageCROSSSEAMER;
	/**
	 * Instances of image holder that hold the overlays for the glass for cutter
	 */
	ImageHolder imageCUTTER;
	/**
	 * Instances of image holder that hold the overlays for the glass for drill
	 */
	ImageHolder imageDRILL;
	/**
	 * Instances of image holder that hold the overlays for the glass for grinder
	 */
	ImageHolder imageGRINDER;
	/**
	 * Instances of image holder that hold the overlays for the glass for oven
	 */
	ImageHolder imageOVEN;
	/**
	 * Instances of image holder that hold the overlays for the glass for paint
	 */
	ImageHolder imagePAINT;
	/**
	 * Instances of image holder that hold the overlays for the glass for UVlamp
	 */
	ImageHolder imageUVLAMP;
	/**
	 * Instances of image holder that hold the overlays for the glass for washer
	 */
	ImageHolder imageWASHER;
	/**
	 * The current imageHolder overlay
	 */
	ImageHolder currentImageHolder;
	/**
	 * List of all image holders for the glass
	 */
	ArrayList<ImageHolder> imageHolders;
	/**
	 * The list of operations done on the part
	 */
	ArrayList<ComponentOperations> operationsOnPart;

	/**
	 * Public constructor for GUIPart
	 */
	public void printMovingGlassOut(GuiComponent gc){
		System.out.println("GUI Component " + gc.getName() + " is releasing glass " + this.getName() + " to " + gc.nextComponent.getName());
	}
	public void printMovingGlassGen(GuiComponent gc){
		System.out.println("GUI Component " + gc.getName() + " is moving glass " + this.getName());
	}
	public void printMovingGlassIn(GuiComponent gc){
		System.out.println("GUI Component " + gc.getName() + "is receiving glass " + this.getName());
	}
	public GUIGlass(int num, Transducer t) 
	{
		super();
		this.addMouseListener(this);
		this.myTransducer = t;
		myTransducer.register(this, TChannel.GLASS);
		this.setName(Integer.toString(num));
		imageHolders = new ArrayList<GUIGlass.ImageHolder>();
		operationsOnPart = new ArrayList<ComponentOperations>();
		currentAngle = 0;
		rotationSpeed = 3;
		doneRotating = true;
		glassRect = new Rectangle2D.Double();

		lastOperation = ComponentOperations.NONE;
		setIcon(new ImageIcon(genfilePath + "CUTTER.png"));
		setSize(getIcon().getIconWidth(),getIcon().getIconHeight());
		setImageHolders();
	}
	/**
	 * Initializes the image holders
	 */
	public void setImageHolders()
	{
		imageBREAKOUT = new ImageHolder("imageicons/glassOverlays/glassImageHolder_BREAKOUT.png");
		imageHolders.add(imageBREAKOUT);
		imageCROSSSEAMER = new ImageHolder("imageicons/glassOverlays/glassImageHolder_CROSSSEAMER.png");
		imageHolders.add(imageCROSSSEAMER);
		imageCUTTER = new ImageHolder("imageicons/glassOverlays/glassImageHolder_CUTTER.png");
		imageHolders.add(imageCUTTER);
		imageDRILL = new ImageHolder("imageicons/glassOverlays/glassImageHolder_DRILL.png");
		imageHolders.add(imageDRILL);
		imageGRINDER = new ImageHolder("imageicons/glassOverlays/glassImageHolder_GRINDER.png");
		imageHolders.add(imageGRINDER);
		imageMANUALBREAKOUT = new ImageHolder("imageicons/glassOverlays/glassImageHolder_MANUALBREAKOUT.png");
		imageHolders.add(imageMANUALBREAKOUT);
		imageOVEN = new ImageHolder("imageicons/glassOverlays/glassImageHolder_OVEN.png");
		imageHolders.add(imageOVEN);
		imagePAINT = new ImageHolder("imageicons/glassOverlays/glassImageHolder_PAINT.png");
		imageHolders.add(imagePAINT);
		imageUVLAMP = new ImageHolder("imageicons/glassOverlays/glassImageHolder_UVLAMP.png");
		imageHolders.add(imageUVLAMP);
		imageWASHER = new ImageHolder("imageicons/glassOverlays/glassImageHolder_WASHER.png");
		imageHolders.add(imageWASHER);
	}
	/**
	 * Message to tell the part to rotate to an angle
	 * @param angle The angle that the part should rotate to
	 */
	public void msgRotatePart(double angle)
	{
		targetAngle = angle;
		doneRotating = false;
	}
	/**
	 * Method to animate rotating the part
	 */
	public void animate()
	{
		double angleDiff = targetAngle - currentAngle;
		if (currentAngle >= 360)
		{
			currentAngle -= 360;
		}
		else if (currentAngle <= -360)
		{
			currentAngle += 360;
		}
		if (angleDiff <= rotationSpeed)
		{
			currentAngle = targetAngle;
			doneRotating = true;
		}
		if (angleDiff == 0)
		{
			//Send message to agent
			doneRotating = true;
		}
		else if (angleDiff > 0)
		{
			currentAngle += rotationSpeed;
		}
		else 
		{
			currentAngle -= rotationSpeed;
		}
	}
	/**
	 * Changes the state of the display of the glass according to what the last operation
	 * was 
	 */
	public void changeState() 
	{
		switch (lastOperation)
		{
		case NONE:
			break;
		case BREAKOUT:
			imageBREAKOUT.display = true;
			break;
		case CROSSSEAMER:
			imageCROSSSEAMER.display = true;
			break;
		case CUTTER:
			imageCUTTER.display = true;
			break;
		case DRILL:
			imageDRILL.display = true;
			break;
		case GRINDER:
			imageGRINDER.display = true;
			break;
		case OVEN:
			imageOVEN.display = true;
			break;
		case PAINT:
			imagePAINT.display = true;
			break;
		case UVLAMP:
			imageUVLAMP.display = true;
			break;
		case WASHER:
			imageWASHER.display = true;
			break;
		}
	}
	/**
	 * Moves the glass's rectangle and sets its width and height to match that of the JLabel
	 */
	public void setupRect()
	{
		glassRect.setRect(getX(), getY(), getIcon().getIconWidth(), getIcon().getIconHeight());
	}

	/**
	 * Repaints, calls setupRect(), and calls changeState()
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		repaint();
		setupRect();
		if (!doneRotating)
		{
			animate();
		}
		if(getX() == lastX && getY() == lastY && !printedException){
			notMovingCount++;
		}
		else{
			notMovingCount = 0;
		}
		//
		//COMMENT THIS PART OUT IF YOU DONT WANT TO TRACK STOPPED GLASS
		//
		//
	/*	try{
			if(notMovingCount >= 200 && !printedException){
				printedException = true; //only prints exception once
				throw new Exception("******** YO DAWG THERS AN EXCEPTION -- Glass " + this.getName() + ": I HAVE STOPPED MOVING!!!! *********");
			}
		}
		catch(Exception ex){
			System.out.println(ex.toString());
		}*/
		lastX = getX();
		lastY = getY();
	}
	boolean printedException = false;
	int notMovingCount = 0;
	int lastX = 0;
	int lastY = 0;
	/**
	 * Sets the last operation done on the part so that a new image may be drawn
	 * @param operation
	 */
	public void setLastOperation(ComponentOperations operation)
	{
		lastOperation = operation;
		operationsOnPart.add(operation);
		changeState();
	}
	/**
	 * Draws the overlays over the part
	 * @param g2d The graphic that it should draw to
	 */
	public void drawOverlays(Graphics2D g2d)
	{
		if (imageBREAKOUT.display)
		{
			g2d.drawImage(imageBREAKOUT.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageMANUALBREAKOUT.display)
		{
			g2d.drawImage(imageMANUALBREAKOUT.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageCROSSSEAMER.display)
		{
			g2d.drawImage(imageCROSSSEAMER.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageCUTTER.display)
		{
			g2d.drawImage(imageCUTTER.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageDRILL.display)
		{
			g2d.drawImage(imageDRILL.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageGRINDER.display)
		{
			g2d.drawImage(imageGRINDER.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageOVEN.display)
		{
			g2d.drawImage(imageOVEN.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imagePAINT.display)
		{
			g2d.drawImage(imagePAINT.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageUVLAMP.display)
		{
			g2d.drawImage(imageUVLAMP.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
		if (imageWASHER.display)
		{
			g2d.drawImage(imageWASHER.holderImage.getImage(), 0, 0, getIcon().getIconWidth(), getIcon().getIconHeight(), this);
		}
	}

	/**
	 * Function to get the part's position in line
	 * @return The part's position in line
	 */
	public int getPosInLine()
	{
		return posInLine;
	}
	/**
	 * Sets the part's position in line
	 * @param newpos The part's new position in line
	 */
	public void setPosInLine(int newpos)
	{
		posInLine = newpos;
	}
	/**
	 * Sets the parts state to broken and changes its image icon to a broken one
	 */
	public void msgPartBroken()
	{
		stateBroken = true;
		for (int i = 0; i < imageHolders.size(); i++)
		{
			imageHolders.get(i).display = false;
		}
		setIcon( new ImageIcon("imageicons/glassImage_BROKEN.png"));
		lastOperation = ComponentOperations.SHATTERED;
	}
	/**
	 * Returns the current angle
	 * @return The part's current angle
	 */
	public double getCurrentAngle()
	{
		return currentAngle;
	}
	/**
	 * Increases the rotation by a number
	 * @param angle The amount to increase rotation
	 */
	public void rotatePartByAngle(double angle)
	{
		currentAngle += angle;
		targetAngle += angle;
	}
	/**
	 * Returns the last operation done on the GUIPart 
	 * @return The last operation done on the GUIPart
	 */
	public ComponentOperations getLastOperation()
	{
		return lastOperation;
	}
	/**
	 * Class to hold the image overlay for the glass
	 * @author Anoop Kamboj
	 *
	 */
	private class ImageHolder extends JLabel
	{
		ImageIcon holderImage;
		boolean display = false;

		public ImageHolder(String filePath)
		{
			holderImage = new ImageIcon(filePath);
		}
	}
	GlassShape shape = GlassShape.Square;
	String genfilePath = "imageicons/glassImage_";
	//Glass after number 127 don't get event fires
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(event != TEvent.GLASS_STATUS_UPDATE && event != TEvent.GLASS_GET_STATUS){
			if(((Integer)args[0]).intValue() == Integer.valueOf(this.name).intValue()){
				switch(event){
				case GLASS_CIRCLE:
					this.setIcon(new ImageIcon(filePathCircle));
					shape = GlassShape.Circle;
					break;
				case GLASS_SQUARE:
					this.setIcon(new ImageIcon(filePathNONE));
					shape = GlassShape.Square;
					break;
				case GLASS_TRIANGLE:
					this.setIcon(new ImageIcon(filePathTriangle));
					shape = GlassShape.Triangle;
					break;
				case GLASS_CROSSSEAMER:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "CROSSSEAMER_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "CROSSSEAMER.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "CROSSSEAMER_triangle.png"));
						break;
					}
					break;
				case GLASS_CUTTER:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "CUTTER_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "CUTTER.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "CUTTER_triangle.png"));
						break;
					}
					break;
				case GLASS_DRILL:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "DRILL_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "DRILL.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "DRILL_triangle.png"));
						break;
					}
					break;
				case GLASS_GRINDER:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "GRINDER_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "GRINDER.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "GRINDER_triangle.png"));
						break;
					}
					break;
				case GLASS_OVEN:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "OVEN_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "OVEN.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "OVEN_triangle.png"));
						break;
					}
					break;
				case GLASS_PAINT:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "PAINT_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "PAINT.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "PAINT_triangle.png"));
						break;
					}
					break;
				case GLASS_UVLAMP:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "UVLAMP_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "UVLAMP.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "UVLAMP_triangle.png"));
						break;
					}
					break;
				case GLASS_WASHER:
					switch(shape){
					case Circle:
						this.setIcon(new ImageIcon(genfilePath + "WASHER_circle.png"));
						break;
					case Square:
						this.setIcon(new ImageIcon(genfilePath + "WASHER.png"));
						break;
					case Triangle:
						this.setIcon(new ImageIcon(genfilePath + "WASHER_triangle.png"));
						break;
					}
					break;
				}
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		Object[] args = {name};
		myTransducer.fireEvent(TChannel.GLASS, TEvent.GLASS_GET_STATUS, args);
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
