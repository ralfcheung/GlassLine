package glassline;

import glassline.Glass.GlassActivity;
import glassline.Glass.GlassShape;
import glassline.Glass.WorkStation;
import gui.panels.subcontrolpanels.TracePanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import transducer.TChannel;
import transducer.Transducer;

public class Factory {
	public static Transducer transducer;
	public static TracePanel tp;
	
	List<FactoryAgent> factoryAgents = Collections.synchronizedList(new ArrayList<FactoryAgent>());
	
	RobotAgent robot; // To start glass moving around the line.
	
	static int conveyorIDCounter = 0;
	static int sensorIDCounter = 0;
	static int shuttleIDCounter = 0;
	static int popupIDCounter = 0;
	
	public Factory(Transducer t, TracePanel tp) {
		Factory.transducer = t;
		Factory.tp = tp;
		
		// Set up robot conveyor agent.
		createRobot();
		createRobotConveyor(robot);
		createOnlineAgent(TChannel.CUTTER, GlassActivity.Cutter);
		createConveyorWithTwoSensors();
		createShuttle();
		createConveyorWithTwoSensors();
		createOnlineAgent(TChannel.BREAKOUT, GlassActivity.Breakout);
		createConveyorWithTwoSensors();
		createOnlineAgent(TChannel.MANUAL_BREAKOUT, GlassActivity.ManualBreakout);
		createConveyorWithTwoSensors();
		createShuttle();
		createPopupConveyorDuo(WorkStation.Drill);
		createPopupConveyorDuo(WorkStation.CrossSeamer);
		createPopupConveyorDuo(WorkStation.Grinder);
		createConveyorWithTwoSensors();
		createOnlineAgent(TChannel.WASHER, GlassActivity.Washer);
		createConveyorWithTwoSensors();
		createShuttle();
		createConveyorWithTwoSensors();
		createOnlineAgent(TChannel.PAINTER, GlassActivity.Painter);
		createConveyorWithTwoSensors();
		createOnlineAgent(TChannel.UV_LAMP, GlassActivity.UVLamp);
		createConveyorWithTwoSensors();
		createShuttle();
		createConveyorWithTwoSensors();
		createOnlineAgent(TChannel.OVEN, GlassActivity.Oven);
		createConveyorWithTwoSensors();
		createTruckAgent();
	}
	
	public void addFactoryAgent(FactoryAgent c) {
		factoryAgents.add(c);
		
		int size = factoryAgents.size();
		
		// Introduce these 2 agents.
		if (size > 1) {
			c.setPreviousComponent(factoryAgents.get(size-2));
			factoryAgents.get(size-2).setNextComponent(c);
		}
		
		// The agent is ready to start processing.
		c.startThread();
	}
	
	public void createRobot() {
		robot = new RobotAgent();
		addFactoryAgent(robot);
	}	

	public void createRobotConveyor(RobotAgent robot) {
		RobotConveyorAgent robotConveyor = new RobotConveyorAgent(conveyorIDCounter++);
		robotConveyor.setPreviousComponent(robot);
		createConveyorWithTwoSensors(robotConveyor);
	}
	
	public void createOnlineAgent(TChannel channel, GlassActivity activity) {
		addFactoryAgent(new OnlineAgent(channel, activity));
	}
	
	public void createConveyorWithTwoSensors() {
		ConveyorAgent conveyor = new ConveyorAgent(conveyorIDCounter++);
		SensorAgent sensor1 = new SensorAgent(conveyor, sensorIDCounter++);
		SensorAgent sensor2 = new SensorAgent(conveyor, sensorIDCounter++);
		sensor1.startThread();
		sensor2.startThread();
		conveyor.setBeginningSensor(sensor1);
		conveyor.setEndSensor(sensor2);
		addFactoryAgent(conveyor);
	}
	
	public void createConveyorWithTwoSensors(ConveyorAgent conveyor) {
		SensorAgent sensor1 = new SensorAgent(conveyor, sensorIDCounter++);
		SensorAgent sensor2 = new SensorAgent(conveyor, sensorIDCounter++);
		sensor1.startThread();
		sensor2.startThread();
		conveyor.setBeginningSensor(sensor1);
		conveyor.setEndSensor(sensor2);
		addFactoryAgent(conveyor);
	}
	
	public void createShuttle() {
		ShuttleAgent shuttle = new ShuttleAgent(shuttleIDCounter++);
		shuttle.startThread();
		addFactoryAgent(shuttle);
	}
	
	// This creates a PopupConveyor/Popup/PopupConveyor trio.
	public void createPopupConveyorDuo(WorkStation type) {
		// Set up pop-ups.
		PopupConveyorAgent popupConveyor = new PopupConveyorAgent(conveyorIDCounter++);
		PopupAgent popup = new PopupAgent(Factory.transducer, popupIDCounter++);
		popup.setPreviousComponent(popupConveyor);
		WorkstationAgent workstation1 = new WorkstationAgent(popup, type, 0);
		WorkstationAgent workstation2 = new WorkstationAgent(popup, type, 1);
		workstation1.startThread();
		workstation2.startThread();
		popup.addWorkstation(workstation1);
		popup.addWorkstation(workstation2);
		createConveyorWithTwoSensors(popupConveyor);
		addFactoryAgent(popup);
	}
	
	public void createTruckAgent(){
		TruckAgent truck = new TruckAgent(transducer, 0);
		addFactoryAgent(truck);
	}
	
	public void startGlass(GlassShape shape, ArrayList<WorkStation> stations) {
		robot.msgGetGlassFromBin(shape, stations);
	}
}
