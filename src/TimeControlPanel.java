import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.Layer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TimeControlPanel extends JPanel
{
    protected JPanel timePanel;
    private JSlider slider;
    
    protected Font defaultFont;

    public TimeControlPanel(TimeController tc)
    {
        // Make a panel at a default size.
        super(new BorderLayout());
        this.makePanel(tc, new Dimension(200, 100));
    }

    public TimeControlPanel(TimeController tc, Dimension size)
    {
        // Make a panel at a specified size.
        super(new BorderLayout());
        this.makePanel(tc, size);
    }

    protected void makePanel(TimeController tc, Dimension size)
    {
        // Make and fill the panel holding the time controller.
        this.timePanel = new JPanel(new GridLayout(2, 3, 0, 4));
        this.timePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill(tc);

        // Add the time panel to a titled panel that will resize with the main window.
        JPanel encompassPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        encompassPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Time Controls")));
        encompassPanel.setToolTipText("Controls for altering time");
        encompassPanel.add(timePanel);
        this.add(encompassPanel, BorderLayout.CENTER);
    }

    protected void fill(TimeController tc)
    {
    	// We need to add all the buttons and set up their actions/listeners here
    }

    public void update(TimeController tc)
    {
        // Update the view to match what we've set in the controller (For when the time is edited outside of here).
    }

    protected static class SliderChangeListener implements ChangeListener
    {
        protected TimeController tc;

        public SliderChangeListener(TimeController tc)
        {
            this.tc = tc;
        }

		@Override
		public void stateChanged(ChangeEvent e)
		{
			// This means we're scrubbing the slider, we need to set our timecontroller time (which should probably update the animation?)
		}
    }
}
