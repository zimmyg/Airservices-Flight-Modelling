package time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import flight.FlightController;

/**
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * 
 * A View class that is responsible for displaying details about the time data.
 */
public class TimeControlPanel extends JPanel
{
	private FlightController fc;
	
	private JLabel dateDisplayLabel;
	private JButton playPauseButton;
	private JButton reverseButton;
	private JButton forwardButton;
	
    protected JPanel timePanel;
    
    protected Font defaultFont;

    public TimeControlPanel(FlightController fc)
    {
        // Make a panel at a default size.
        super(new FlowLayout());
        this.fc = fc;
        
        this.makePanel(new Dimension(200, 100));
    }

    public TimeControlPanel(FlightController fc, Dimension size)
    {
        // Make a panel at a specified size.
        super(new FlowLayout());
        this.fc = fc;
        
        this.makePanel(size);
    }

    protected void makePanel(Dimension size)
    {
        // Make and fill the panel holding the time controller.
        this.timePanel = new JPanel(new GridLayout(2, 3, 0, 4));
        this.timePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill();

        // Add the time panel to a titled panel that will resize with the main window.
        JPanel encompassPanel = new JPanel( new BorderLayout() ); //new GridLayout(0, 1, 0, 10));
        encompassPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Time Controls")));
        encompassPanel.setToolTipText("Controls for altering time");
        encompassPanel.add(timePanel, BorderLayout.CENTER);
        this.add(encompassPanel, BorderLayout.CENTER);
    }

    protected void fill()
    {
    	// We need to add all the buttons and set up their actions/listeners here
    	dateDisplayLabel = new JLabel("Time");
    	reverseButton = new JButton();
    	playPauseButton = new JButton();
    	forwardButton = new JButton();
    	
    	reverseButton.setAction(new TimeButtonAction(this, TimeButtonAction.Operation.REW));
    	playPauseButton.setAction(new TimeButtonAction(this, TimeButtonAction.Operation.PLAY_PAUSE));
    	forwardButton.setAction(new TimeButtonAction(this, TimeButtonAction.Operation.FWD));
    	
    	Dimension buttonSize = new Dimension(50, 30);
    	reverseButton.setMinimumSize(buttonSize);
    	reverseButton.setMaximumSize(buttonSize);
    	reverseButton.setPreferredSize(buttonSize);
    	reverseButton.setSize(buttonSize);
    	
    	playPauseButton.setMinimumSize(buttonSize);
    	playPauseButton.setMaximumSize(buttonSize);
    	playPauseButton.setPreferredSize(buttonSize);
    	playPauseButton.setSize(buttonSize);
    	
    	forwardButton.setMinimumSize(buttonSize);
    	forwardButton.setMaximumSize(buttonSize);
    	forwardButton.setPreferredSize(buttonSize);
    	forwardButton.setSize(buttonSize);
    	
    	timePanel.add(dateDisplayLabel);
    	timePanel.add(reverseButton);
    	timePanel.add(playPauseButton);
    	timePanel.add(forwardButton);
    }

    public void updateTimeDisplay(String timeString)
    {
    	dateDisplayLabel.setText(timeString);
    }
    
    private void updateTimeScale(float timeScale)
    {
        fc.setTimeScale(timeScale);
    }
    
	private float getTimeScale()
	{
		return fc.getTimeScale();
	}
    
    protected static class TimeButtonAction extends AbstractAction
    {
    	public enum Operation {
    		REW, PLAY_PAUSE, FWD
    	}
    	
    	TimeControlPanel tcPanel;
    	static final float[] timeScales = { -240.0f, -120.0f, -60.0f, -30.0f, -10.0f, -2.0f -1.0f, 0.0f, 1.0f, 2.0f, 10.0f,  30.0f, 60.0f, 120.0f, 240.0f };
    	Operation op;
    	
    	public TimeButtonAction(TimeControlPanel tcPanel, Operation op)
    	{
    		super(op.toString().equals("PLAY_PAUSE") ? "PLAY" : op.toString());
    		
    		this.tcPanel = tcPanel;
    		this.op = op;
    	}
    	
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton)e.getSource();
			
			switch(op)
			{
				case REW:
				{
					for(int  i = 0; i < timeScales.length; i++)
					{		
						float testScale = timeScales[i];
						if(tcPanel.getTimeScale() == testScale && i != 0)
						{
							tcPanel.updateTimeScale(timeScales[i -1]);
							break;
						}
					}
				} break;
				case PLAY_PAUSE:
				{
					if( button.getText().equals("Play") )
					{
						button.setText("Pause");
						tcPanel.updateTimeScale(0.0f);
					}
					else
					{
						button.setText("Play");
						tcPanel.updateTimeScale(1.0f);
					}
				} break;
				case FWD:
				{
					for(int  i = 0; i < timeScales.length; i++)
					{					
						float testScale = timeScales[i];
						
						if(tcPanel.getTimeScale() == testScale && i != (timeScales.length - 1))
						{
							tcPanel.updateTimeScale(timeScales[i + 1]);
							break;
						}
					}
				} break;
			}
				
		}
    }
}
