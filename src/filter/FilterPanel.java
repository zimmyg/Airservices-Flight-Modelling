package filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import flight.FlightController;

/**
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * This is a View class that deals with displaying the filters than can be applied to the system.
 */
public class FilterPanel extends JPanel
{
	// Class for creating filter panel
	// and realize the functions of filters
	protected JPanel filtersPanel;
    protected JPanel westPanel;
    protected JScrollPane scrollPane;
    protected Font defaultFont;
    
    private FlightController fc;
    
    public FilterPanel(FlightController fc)
    {
        // Make a panel at a default size.
        this(fc, new Dimension(200, 500));
    }

    public FilterPanel(FlightController fc, Dimension size)
    {
        // Make a panel at a specified size.
        super(new BorderLayout());
        this.makePanel(fc, size);
    }

    protected void makePanel(FlightController fc, Dimension size)
    {
    	this.fc = fc;
    	
        // Make and fill the panel holding the layer titles.
        this.filtersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.filtersPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.recomputeFilters();

        // Must put the layer grid in a container to prevent scroll panel from stretching their vertical spacing.
        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(this.filtersPanel, BorderLayout.CENTER);

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

    // Fill the initial data into the filter panel
    public void recomputeFilters()
    {
    	this.filtersPanel.removeAll();
    	
    	List<Filter> opFilters = fc.getAllFiltersOfType(Filter.FilterCategory.OPERATION);
    	List<Filter> apFilters = fc.getAllFiltersOfType(Filter.FilterCategory.AIRPORT);
    	//List<Filter> rwyFilters = fc.getAllFiltersOfType(Filter.FilterCategory.RUNWAY);
    	List<Filter> acTypeFilters = fc.getAllFiltersOfType(Filter.FilterCategory.AIRCRAFT_TYPE);
    	List<Filter> flTypeFilters = fc.getAllFiltersOfType(Filter.FilterCategory.FLIGHT_TYPE);
    	//List<Filter> wtcFilters = fc.getAllFiltersOfType(Filter.FilterCategory.WTC);
    	
    	// creating the Operations filter panel.
    	JLabel opLabel = new JLabel("Operations");
    	
    	// Getting the font and creating the border.
        Font headingFont = opLabel.getFont();
    	headingFont.deriveFont(Font.BOLD, headingFont.getSize() + 5);
    	Border headingBorder = BorderFactory.createRaisedSoftBevelBorder();
    	
        // Setting the font, foreground colour, swing constants alignment and border.
    	opLabel.setFont(headingFont);
    	opLabel.setForeground(Color.RED);
    	opLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	opLabel.setBorder(headingBorder);
    	
    	// adding opLabel to the filters panel.
        this.filtersPanel.add(opLabel);
    	for(Filter f: opFilters)
    	{
    		FilterMouseListener action = new FilterMouseListener(fc, f);
    		JCheckBox jcb = new JCheckBox(f.getName());
    		jcb.addMouseListener(action);
    		
    		// I was trying out setting the text L_aligned and the Box R-aligned, doesnt work
    		//jcb.setHorizontalTextPosition(SwingConstants.LEFT);
    		//jcb.setHorizontalAlignment(SwingConstants.RIGHT);
    		
    		jcb.setSelected(fc.getFilterState(f));
    		this.filtersPanel.add(jcb);
    	}
    	
    	// Creating the Airports label and settings its font, font colour ... etc.
    	JLabel apLabel = new JLabel("Airports");
    	apLabel.setFont(headingFont);
    	apLabel.setForeground(Color.RED);
    	apLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	apLabel.setBorder(headingBorder);
    	
        // Adding apLabel to the filters panel.
    	this.filtersPanel.add(apLabel);
    	for(Filter f: apFilters)
    	{
            // Adding filter controller and filter to the filter action.
    		FilterMouseListener action = new FilterMouseListener(fc, f);
    		JCheckBox jcb = new JCheckBox(f.getName());
    		jcb.addMouseListener(action);
            
            // set J checkbox as selected by default and add it to the filters panel.
    		jcb.setSelected(fc.getFilterState(f));
    		this.filtersPanel.add(jcb);
    	}
    	
    	/* Delete "runways" from filter panel
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
    		jcb.setSelected(fc.getFilterState(f));
    		this.filtersPanel.add(jcb);
    	}
    	*/
    	
    	JLabel acTypeLabel = new JLabel("Aircraft Types");
    	acTypeLabel.setFont(headingFont);
    	acTypeLabel.setForeground(Color.RED);
    	acTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	acTypeLabel.setBorder(headingBorder);
    	
    	this.filtersPanel.add(acTypeLabel);
    	for(Filter f: acTypeFilters)
    	{
    		FilterMouseListener action = new FilterMouseListener(fc, f);
    		JCheckBox jcb = new JCheckBox(f.getName());
    		jcb.addMouseListener(action);
    		
    		jcb.setSelected(fc.getFilterState(f));
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
    		FilterMouseListener action = new FilterMouseListener(fc, f);
    		JCheckBox jcb = new JCheckBox(f.getName());
    		jcb.addMouseListener(action);
    		
    		jcb.setSelected(fc.getFilterState(f));
    		this.filtersPanel.add(jcb);
    	}
    	
    	/* Delete Wake Turbulence Categories from filter panel
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
    		jcb.setSelected(fc.getFilterState(f));
    		this.filtersPanel.add(jcb);
    		
    		// Do this in the last list just to make sure we have a defaultfont
            if (defaultFont == null)
            {
                this.defaultFont = jcb.getFont();
            }
    	}
    	*/
    }

    @Override
    public void setToolTipText(String string)
    {
        this.scrollPane.setToolTipText(string);
    }

    // A class that deals with setting/unsetting filter values when the filter item is selected/deselected.
    protected static class FilterMouseListener implements MouseListener
    {
    	// Class for update the selections of filter actions
        private FlightController fc;
        private Filter filter;

        private boolean lastState = true;
        
        public FilterMouseListener(FlightController fc, Filter filter)
        {
            this.fc = fc;
            this.filter = filter;
        }

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				// Left-Click
				fc.mutateFilter(filter, ((JCheckBox) e.getSource()).isSelected());
			}
			else if (e.getButton() == MouseEvent.BUTTON3)
			{
				// Right-Click
				lastState = !lastState;
				// There's a bug here that I'm not quite sure how to fix.
				// When the filteres are updated, their respective checkboxes are not, 
				// creating a disconnect between the model and view
				fc.mutateAllInTypeExcept(filter, lastState);
			}
            
            fc.updateFlightVisibilities();
		}

		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			
		}

		@Override
		public void mouseExited(MouseEvent arg0)
		{
			
		}

		@Override
		public void mousePressed(MouseEvent arg0)
		{
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			
		}
    }
}
