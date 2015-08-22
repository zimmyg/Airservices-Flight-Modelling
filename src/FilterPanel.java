import gov.nasa.worldwind.WorldWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

public class FilterPanel extends JPanel
{
	protected JPanel filtersPanel;
    protected JPanel westPanel;
    protected JScrollPane scrollPane;
    protected Font defaultFont;
    
    private FlightController fc;
    
    public FilterPanel(FlightController fc)
    {
        // Make a panel at a default size.
        this(fc, new Dimension(200, 400));
    }

    public FilterPanel(FlightController fc, Dimension size)
    {
        // Make a panel at a specified size.
        super(new BorderLayout());
        this.makePanel(fc, size);
    }

    protected void makePanel(FlightController fc, Dimension size)
    {
        // Make and fill the panel holding the layer titles.
        this.filtersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.filtersPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill(fc);

        // Must put the layer grid in a container to prevent scroll panel from stretching their vertical spacing.
        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(this.filtersPanel, BorderLayout.NORTH);

        // Put the name panel in a scroll bar.
        this.scrollPane = new JScrollPane(dummyPanel);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        if (size != null)
            this.scrollPane.setPreferredSize(size);

        // Add the scroll bar and name panel to a titled panel that will resize with the main window.
        westPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        westPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Filters")));
        westPanel.setToolTipText("Filters applied to the system");
        westPanel.add(scrollPane);
        this.add(westPanel, BorderLayout.CENTER);
    }

    protected void fill(FlightController fc)
    {
    	List<Filter> opFilters = fc.getAllFiltersOfType(Filter.FilterCategory.OPERATION);
    	List<Filter> apFilters = fc.getAllFiltersOfType(Filter.FilterCategory.AIRPORT);
    	List<Filter> rwyFilters = fc.getAllFiltersOfType(Filter.FilterCategory.RUNWAY);
    	List<Filter> acTypeFilters = fc.getAllFiltersOfType(Filter.FilterCategory.AIRCRAFT_TYPE);
    	List<Filter> flTypeFilters = fc.getAllFiltersOfType(Filter.FilterCategory.FLIGHT_TYPE);
    	List<Filter> wtcFilters = fc.getAllFiltersOfType(Filter.FilterCategory.WTC);
    	
    	
    	JLabel opLabel = new JLabel("Operations");
    	
    	Font headingFont = opLabel.getFont();
    	headingFont.deriveFont(Font.BOLD, headingFont.getSize() + 5);
    	Border headingBorder = BorderFactory.createRaisedSoftBevelBorder();
    	
    	opLabel.setFont(headingFont);
    	opLabel.setForeground(Color.RED);
    	opLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	opLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(opLabel);
    	for(Filter f: opFilters)
    	{
    		FilterAction action = new FilterAction(fc, f);
    		JCheckBox jcb = new JCheckBox(action);
    		
    		// I was trying out setting the text L_aligned and the Box R-aligned, doesnt work
    		//jcb.setHorizontalTextPosition(SwingConstants.LEFT);
    		//jcb.setHorizontalAlignment(SwingConstants.RIGHT);
    		
    		jcb.setSelected(true);
    		this.filtersPanel.add(jcb);
    	}
    	
    	
    	JLabel apLabel = new JLabel("Airports");
    	apLabel.setFont(headingFont);
    	apLabel.setForeground(Color.RED);
    	apLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	apLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(apLabel);
    	for(Filter f: apFilters)
    	{
    		FilterAction action = new FilterAction(fc, f);
    		JCheckBox jcb = new JCheckBox(action);
    		jcb.setSelected(true);
    		this.filtersPanel.add(jcb);
    	}
    	
    	JLabel rwyLabel = new JLabel("Runways");
    	rwyLabel.setFont(headingFont);
    	rwyLabel.setForeground(Color.RED);
    	rwyLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	rwyLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(rwyLabel);
    	for(Filter f: rwyFilters)
    	{
    		FilterAction action = new FilterAction(fc, f);
    		JCheckBox jcb = new JCheckBox(action);
    		jcb.setSelected(true);
    		this.filtersPanel.add(jcb);
    	}
    	
    	JLabel acTypeLabel = new JLabel("Aircraft Types");
    	acTypeLabel.setFont(headingFont);
    	acTypeLabel.setForeground(Color.RED);
    	acTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	acTypeLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(acTypeLabel);
    	for(Filter f: acTypeFilters)
    	{
    		FilterAction action = new FilterAction(fc, f);
    		JCheckBox jcb = new JCheckBox(action);
    		jcb.setSelected(true);
    		this.filtersPanel.add(jcb);
    	}
    	
    	JLabel flTypeLabel = new JLabel("Flight Types");
    	flTypeLabel.setFont(headingFont);
    	flTypeLabel.setForeground(Color.RED);
    	flTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	flTypeLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(flTypeLabel);
    	for(Filter f: flTypeFilters)
    	{
    		FilterAction action = new FilterAction(fc, f);
    		JCheckBox jcb = new JCheckBox(action);
    		jcb.setSelected(true);
    		this.filtersPanel.add(jcb);
    	}
    	
    	JLabel wtcLabel = new JLabel("Wake Turbulence Categories");
    	wtcLabel.setFont(headingFont);
    	wtcLabel.setForeground(Color.RED);
    	wtcLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	wtcLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(wtcLabel);
    	for(Filter f: wtcFilters)
    	{
    		FilterAction action = new FilterAction(fc, f);
    		JCheckBox jcb = new JCheckBox(action);
    		jcb.setSelected(true);
    		this.filtersPanel.add(jcb);
    		
    		// Do this in the last list just to make sure we have a defaultfont
            if (defaultFont == null)
            {
                this.defaultFont = jcb.getFont();
            }
    	}
    }

    @Override
    public void setToolTipText(String string)
    {
        this.scrollPane.setToolTipText(string);
    }

    protected static class FilterAction extends AbstractAction
    {
        private FlightController fc;
        private Filter filter;

        public FilterAction(FlightController fc, Filter filter)
        {
            super(filter.getName());
            this.fc = fc;
            this.filter = filter;
        }

        public void actionPerformed(ActionEvent actionEvent)
        {
            // Simply enable or disable the layer based on its toggle button.
            if (((JCheckBox) actionEvent.getSource()).isSelected())
            	fc.mutateFilter(filter, true);
            else
            	fc.mutateFilter(filter, false);
            
            fc.updateFlightVisibilities();
        }
    }
}
