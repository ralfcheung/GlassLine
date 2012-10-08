package glassline;

import glassline.interfaces.Conveyor;
import glassline.interfaces.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import agent.Agent;

public class SensorAgent extends Agent implements TReceiver, Sensor
{
	/*
	 * Data
	 */
	public Conveyor conveyor;
	public int componentNumber; // To match up respective GUISensor.
	public boolean on = true;
	
	public List<Boolean> triggers = Collections.synchronizedList(new ArrayList<Boolean>());
	
	public SensorAgent(Conveyor conveyor, int id)
	{
		this.conveyor = conveyor;
		this.componentNumber = id;
		
		Factory.transducer.register(this, TChannel.SENSOR);
	}
	
	/*
	 * Messages
	 */
	
	public void msgSensedGlass(boolean sensing)
	{
		triggers.add(sensing);
		stateChanged();
	}
	
	public void msgIAmOn(boolean on) {
		this.on = on;
		stateChanged();
	}
	
	/*
	 * (non-Javadoc)
	 * @see agent.Agent#pickAndExecuteAnAction()
	 * 
	 * Scheduler
	 */
	@Override
	public boolean pickAndExecuteAnAction()
	{
		if (on) {
			if (!triggers.isEmpty())
			{
				boolean sensing = triggers.get(0);
				triggers.remove(0);
		
				sensedGlass(sensing);
				
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see glassline.Sensor#eventFired(transducer.TChannel, transducer.TEvent, java.lang.Object[])
	 * 
	 * Transducer event listener
	 */
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args)
	{
		if(channel == TChannel.SENSOR)
		{
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if(componentNumber == (Integer) args[0])
				{
					this.msgSensedGlass(true);
				}
			}
			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if(componentNumber == (Integer) args[0])
				{
					this.msgSensedGlass(false);
				}
			}
		}
	}
	
	/* 
	 * Sends a message to the conveyor when sensor state changes
	 */
	public void sensedGlass(boolean sensing)
	{
		//Do("Telling " + conveyor + " I sensed glass " + sensing);
		conveyor.msgSensedGlass(this, sensing);
		stateChanged();
	}
	
	public String toString() {
		return "Sensor " + componentNumber;
	}
}
