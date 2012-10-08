	package glassline;

import glassline.Glass.GlassActivity;
import glassline.interfaces.Popup;
import glassline.interfaces.Workstation;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import agent.Agent;
import java.util.*;

public class WorkstationAgent extends Agent implements Workstation, TReceiver
{
	/*
	 * Data
	 */
	private Popup pop;//associated popup
	public Glass glassToWorkOn;//the glass to work on
	private Glass glassToSend;//the glass being sent
	private Boolean busy;//if workstation is working on a glass
	private Boolean on;// turn workstation on or off
	public Boolean readyToSend;// tells the workstation popup is ready for glass
	public Boolean askToCome; //tells the popup to come up
	private Glass.WorkStation type;//type of workstation CrossSeamer... etc..
	private int id;//workstation id to identify transducer messages
	private String name;
	public Boolean loadFinished;
	public Boolean releaseFinished;
	//Laurence's code --
	private boolean popupBusy;
	public void msgIAmBusy(boolean b){
		popupBusy = b;
		stateChanged();
	}
	//--
	

	
	private TChannel myChannel;
	//private TEvent event;
	/*Constructor
	 * 
	 * Initializes all data
	 */
	public WorkstationAgent(Popup p,Glass.WorkStation ty,int ids)
	{
		pop = p;
		on = true;
		busy = false;
		readyToSend = false;
		type = ty;
		id = ids;
		askToCome = false;
		name = "Workstation " + type.name() + " " + id;
		loadFinished = false;
		releaseFinished = false;
		
		
		setUp();
	}
	public void setUp()
	{
		if(type == Glass.WorkStation.CrossSeamer)
		{
			print("I am a cross seamer");
			myChannel = TChannel.CROSS_SEAMER;
		}
		if(type == Glass.WorkStation.Drill)
		{
			myChannel = TChannel.DRILL;
			
		}
		if(type == Glass.WorkStation.Grinder)
		{
			myChannel = TChannel.GRINDER;
		}
		Factory.transducer.register(this, myChannel);
	}
	
	/* (non-Javadoc)
	 * @see glassline.Workstation#msgHereIsGlass(glassline.Glass)
	 */
	@Override
	public void msgHereIsGlass(Glass g)
	{
		glassToWorkOn = g;
		busy = true;
		print("Glass Recieved");
		Object array[] = {id};
		Factory.transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_LOAD_GLASS, array);
		
		
		stateChanged();
	}
	/* (non-Javadoc)
	 * @see glassline.Workstation#msgIAmUp()
	 */
	@Override
	public void msgIAmUp()
	{
		readyToSend = true;
		print("popup is ready");
		stateChanged();
	}
	/*method turnOnOff()
	 * 
	 * switches on boolean to turn on or off the workstation
	 */
	public void turnOnOff()
	{
		if(on == true)
		{
			on = false;
		}
		else
		{
			on = true;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see agent.Agent#pickAndExecuteAnAction()
	 * 
	 * Scheduler
	 * 
	 * determines logic. if there is a glass to work on, do work on glass
	 * if need to ask popup to come up, ask to come
	 * if glass is ready to send send a glass back to the popup
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		if(glassToWorkOn != null && loadFinished)
		{
			doWorkOnGlass(glassToWorkOn);
			return true;
		}
		if(askToCome == true)
		{
			askToCome();
			return true;
		}
		
		if(readyToSend == true)
		{
			sendGlass(glassToSend);
			return true;
		}
		/*if(releaseFinished == true)
		{
			release();
			return true;
		}*/
		return false;
	}
	/*
	 *			Actions 
	 */
	/*method askToCome()
	 * 
	 * asks the poup to come up
	 */
	public void askToCome()
	{
		print("asking popup to come up");
		askToCome = false;
		pop.msgComeUp(this);
		stateChanged();
	}
	/*method doWorkOnGlass(Glass g)
	 * 
	 * does animation on glass g sets glass g to the glass to send
	 */
	public void doWorkOnGlass(Glass g)
	{
		print("working on glass");
		glassToSend = g;
		doAnimateWork(g);
		loadFinished = false;
		stateChanged();
		
	}
	/*method sendGlass(Glass g)
	 * 
	 * sends glass back to popup
	 */
	public void sendGlass(Glass g)
	{
		print("sending glass");
		readyToSend = false;
		Object array[] = {id};
		releaseFinished = true;
		release();
		Factory.transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, array);
		stateChanged();
	}
	/*doAnimateWork(Glass g)
	 * 
	 * does the animation, tells glass it has been worked on
	 */
	public void doAnimateWork(Glass g)
	{
		Object array[] = {id};
		glassToWorkOn = null;
		
		
		Factory.transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_ACTION,array);
		g.recipe.workstations.put(type, true);
	}
	public void release()
	{
		releaseFinished = false;
		pop.msgHereIsGlass(glassToSend,this);
		stateChanged();
	}
	/*
	 * (non-Javadoc)
	 * @see transducer.TReceiver#eventFired(transducer.TChannel, transducer.TEvent, java.lang.Object[])
	 * 
	 * listens to when animation is finished and then sets askToCome to true to ask popup to come
	 */
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) 
	{
		
		if(channel == myChannel && ((Integer) args[0]).intValue() == id)
		{
			if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
			{
				busy = false;
				askToCome = true;
				Object[] args1 = {glassToSend.glassNumber};
				switch(type){
				case Grinder:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_GRINDER, args1);
					glassToSend.addActivity(GlassActivity.Grinder);
					break;
				case Drill:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_DRILL, args1);
					glassToSend.addActivity(GlassActivity.Driller);
					break;
				case CrossSeamer:
					Factory.transducer.fireEvent(TChannel.GLASS, TEvent.GLASS_CROSSSEAMER, args1);
					glassToSend.addActivity(GlassActivity.CrossSeamer);
					break;
				}
				stateChanged();
			}
			if(event == TEvent.WORKSTATION_LOAD_FINISHED)
			{
				//Do("received WORKSTATION_LOAD_FINISHED");
				loadFinished = true;
				stateChanged();
			}
			if(event == TEvent.WORKSTATION_RELEASE_FINISHED)
			{
				print("Glass released");
				stateChanged();
			}
		}
		
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * return name
	 */
	public String toString()
	{
		return name;
	}
	/*
	 * (non-Javadoc)
	 * @see glassline.interfaces.Workstation#getType()
	 */
	public Glass.WorkStation getType()
	{
		return type;
	}

}
