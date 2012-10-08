
package gui.panels.subcontrolpanels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import glassline.Glass;
import glassline.Glass.GlassActivity;
import glassline.GlassRecipe;
import gui.panels.ControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

/**
 * The GlassInfoPanel class displays information on glass in production
 */
@SuppressWarnings("serial")
public class GlassInfoPanel extends JPanel implements TReceiver
{
	String[] ids = {"Glass Num: ", "Shape: ", "Cutter: ", "Breakout: ", "Manual Breakout: ",
			"Driller: ", "CrossSeamer: ", "Grinder: ","Washer: ", "Painter: ", "UVLamp: ", "Oven: "
	};
	ArrayList<JLabel> glassInfoFields = new ArrayList<JLabel>();
	JPanel infoPanel = new JPanel();
	/** The ControlPanel this is linked to */
	private ControlPanel parent;
	Transducer myTransducer;
	JButton popOutButton = new JButton("Pop Out Window");
	/**
	 * Creates a new GlassInfoPanel and links it to the control panel
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public GlassInfoPanel(ControlPanel cp, Transducer t)
	{
		myTransducer = t;
		t.register(this, TChannel.GLASS);
		parent = cp;
		this.setLayout(new BorderLayout());
		this.add(popOutButton, BorderLayout.NORTH);
		infoPanel.setLayout(new GridLayout(ids.length, 2));
		
		//adds labels to the glass info panel
		for(int i = 0; i < ids.length; i++){
			JLabel l = new JLabel(ids[i], JLabel.TRAILING);
			infoPanel.add(l);
			JLabel j = new JLabel();
			j.setPreferredSize(new Dimension(10, 5));
			j.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			glassInfoFields.add(j);
			infoPanel.add(j);
		}
		this.add(infoPanel, BorderLayout.CENTER);
		popOutButton.addActionListener(new ActionListener(){

			//makes the popup window
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame popOutWindow = new JFrame("Glass Status");
				JPanel popOutPanel = new JPanel();
				popOutPanel.setLayout(new GridLayout(ids.length, 2));
				for(int i = 0; i < ids.length; i++){
					JLabel l = new JLabel(ids[i], JLabel.TRAILING);
					popOutPanel.add(l);
					popOutPanel.add(glassInfoFields.get(i));
				}
				popOutWindow.add(popOutPanel);
				popOutWindow.setSize(new Dimension(250, 250));
				popOutWindow.setVisible(true);
				popOutWindow.setAlwaysOnTop(true);
				popOutWindow.addWindowListener(new WindowListener(){
					@Override
					public void windowActivated(WindowEvent arg0) {
					}
					@Override
					public void windowClosed(WindowEvent arg0) {
					}
					@Override
					public void windowClosing(WindowEvent arg0) {
						popOutWindowExit();
						infoPanel.validate();
					}
					@Override
					public void windowDeactivated(WindowEvent arg0) {	
					}
					@Override
					public void windowDeiconified(WindowEvent arg0) {	
					}
					@Override
					public void windowIconified(WindowEvent arg0) {	
					}
					@Override
					public void windowOpened(WindowEvent arg0) {
					}

				});
			}

		});
	}
	private int popOutWindowExit(){
		infoPanel.removeAll();
		for(int i = 0; i < ids.length; i++){
			JLabel l = new JLabel(ids[i], JLabel.TRAILING);
			infoPanel.add(l);
			infoPanel.add(glassInfoFields.get(i));
		}
		return 1;
	}

	/**
	 * Returns the parent panel
	 * @return the parent panel
	 */
	public ControlPanel getGuiParent()
	{
		return parent;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(event == TEvent.GLASS_STATUS_UPDATE){
			glassInfoFields.get(0).setText(args[0].toString());
			glassInfoFields.get(1).setText(args[1].toString());
			GlassRecipe recipe = (GlassRecipe)args[3];
			java.util.List<GlassActivity> activity = (List<GlassActivity>) args[2];
			int j = 0;
			//sets the text of the info panel
			//j goes through the glass's activites 
			//i goes through all possible glass activities
			for(int i = 0; i < GlassActivity.values().length; i++){
				if(j < activity.size() && GlassActivity.values()[i].toString().equals(activity.get(j).toString())){
					glassInfoFields.get(i+2).setText("Done");
					j++;
				}
				else{
					glassInfoFields.get(i+2).setText("Not Done");
				}
			}
			for(int i = 0; i < 3; i++){
				if(recipe.workstations.containsKey(Glass.WorkStation.values()[i])){
					if(recipe.workstations.get(Glass.WorkStation.values()[i])){
						glassInfoFields.get(i+5).setText("Done");
					}
					else{
						glassInfoFields.get(i+5).setText("Not Done");
					}
				}
				else{
					glassInfoFields.get(i+5).setText("N/A");
				}
			}
		}
	}
}
