/** 
 * @author Group K
 * La Trobe University
 * CSE3PRA/B 2015
 */
package time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
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


public class TimeControlPanel extends JPanel
{
	private SimpleDateFormat dateFormatter;
	
	private JLabel dateDisplayLabel;
	private JButton playPauseButton;
	private JButton reverseButton;
	private JButton forwardButton;
	
    protected JPanel timePanel;
    
    protected Font defaultFont;

    public TimeControlPanel(TimeController tc, SimpleDateFormat format)
    {
        // Make a panel at a default size.
        super(new BorderLayout());
        this.dateFormatter = format;
        this.makePanel(tc, new Dimension(200, 100));
    }

    public TimeControlPanel(TimeController tc, SimpleDateFormat format, Dimension size)
    {
        // Make a panel at a specified size.
        super(new BorderLayout());
        this.dateFormatter = format;
        this.makePanel(tc, size);
    }

    protected void makePanel(TimeController tc, Dimension size)
    {
        // Make and fill the panel holding the time controller.
        this.timePanel = new JPanel(new GridLayout(2, 3, 0, 4));
        this.timePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill(tc);

        // Add the time panel to a titled panel that will resize with the main window.
        JPanel encompassPanel = new JPanel( new BorderLayout() ); //new GridLayout(0, 1, 0, 10));
        encompassPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Time Controls")));
        encompassPanel.setToolTipText("Controls for altering time");
        encompassPanel.add(timePanel, BorderLayout.CENTER);
        this.add(encompassPanel, BorderLayout.CENTER);
    }

    protected void fill(TimeController tc)
    {
    	// We need to add all the buttons and set up their actions/listeners here
    	dateDisplayLabel = new JLabel( dateFormatter.format(tc.getEarliest()) );
    	reverseButton = new JButton("Rew");
    	playPauseButton = new JButton("Play");
    	forwardButton = new JButton("Fwd");
    	
    	reverseButton.setAction(new TimeButtonAction(tc, TimeButtonAction.Operation.REW));
    	playPauseButton.setAction(new TimeButtonAction(tc, TimeButtonAction.Operation.PLAY_PAUSE));
    	forwardButton.setAction(new TimeButtonAction(tc, TimeButtonAction.Operation.FWD));
    	
    	timePanel.add(dateDisplayLabel);
    	timePanel.add(reverseButton);
    	timePanel.add(playPauseButton);
    	timePanel.add(forwardButton);
    }

    public void update(TimeController tc)
    {
        // Update the view to match what we've set in the controller (For when the time is edited outside of here).
    }

    protected static class TimeButtonAction extends AbstractAction
    {
    	public enum Operation {
    		REW, PLAY_PAUSE, FWD
    	}
    	
    	static final int[] timeScales = { -60, -20, -10, -5, -1, 0, 1, 5, 10, 20, 60 };
    	TimeController tc;
    	Operation op;
    	
    	public TimeButtonAction(TimeController tc, Operation op)
    	{
    		this.tc = tc;
    		this.op = op;
    	}
    	
    	//TODO: There's more strange bugs here, I'm not sure what the issue is
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(true)
			{
				return;
			}
			
			JButton button = (JButton)e.getSource();
			
			switch(op)
			{
				case REW:
				{
					for(int  i = 0; i < timeScales.length; i++)
					{					
						int scale = timeScales[i];
						
						if(tc.getTimeScale() == scale && i != 0)
						{
							tc.setTimeScale(timeScales[i -1]);
							break;
						}
					}
				} break;
				case PLAY_PAUSE:
				{
					if( button.getText().equals("Play") )
					{
						button.setText("Pause");
						tc.setPaused(false);
					}
					else
					{
						button.setText("Play");
						tc.setPaused(true);
					}
				} break;
				case FWD:
				{
					for(int  i = 0; i < timeScales.length; i++)
					{					
						int scale = timeScales[i];
						
						if(tc.getTimeScale() == scale && i != (timeScales.length - 1))
						{
							tc.setTimeScale(timeScales[i + 1]);
							break;
						}
					}
				} break;
			}
				
		}
    }
}
