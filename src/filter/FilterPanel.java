package filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
    private HashMap<String, FilterGroup> filterGroups;
    
    public FilterPanel(FlightController fc)
    {
        // Make a panel at a default size.
        this(fc, new Dimension(200, 500));
    }

    public FilterPanel(FlightController fc, Dimension size)
    {
        // Make a panel at a specified size.
        super(new BorderLayout());
        this.fc = fc;
        this.filterGroups = new HashMap<String, FilterGroup>();
        
        this.makePanel(size);
    }
    
    private FilterGroup getGroup(String name)
    {
    	return filterGroups.get(name);
    }
    
    private void addGroup(FilterGroup group)
	{
    	filterGroups.put(group.getName(), group);
	}

    protected void makePanel(Dimension size)
    {
        // Make and fill the panel holding the layer titles.
        filtersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        filtersPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.recomputeFilters();

        // Must put the layer grid in a container to prevent scroll panel from stretching their vertical spacing.
        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(this.filtersPanel, BorderLayout.CENTER);

        // Put the name panel in a scroll bar.
        scrollPane = new JScrollPane(dummyPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        if (size != null)
            scrollPane.setPreferredSize(size);

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
    	filtersPanel.removeAll();
    	
    	// We do it this way because the filters need to be sorted for this to work properly.
    	// An alternate way to do it would be to sort the full list, which is probably faster,
    	// but I already had this code.
    	recomputeFilterGroup(fc.getAllFiltersOfType(FilterCategory.OPERATION));
    	recomputeFilterGroup(fc.getAllFiltersOfType(FilterCategory.AIRCRAFT_TYPE));
    	recomputeFilterGroup(fc.getAllFiltersOfType(FilterCategory.FLIGHT_TYPE));
    	recomputeFilterGroup(fc.getAllFiltersOfType(FilterCategory.AIRPORT));
    }
    
    private void recomputeFilterGroup(List<Filter> categoryFilters)
    {
    	if(categoryFilters.size() > 0)
    	{
			String filterGroupName = categoryFilters.get(0).getCategory().toString();
			FilterGroup group = getGroup(filterGroupName);
			if(group == null)
			{
				group = new FilterGroup(filterGroupName);
				group.addMouseListener(new GroupMouseListener(this));
				
				addGroup(group);
			}
			filtersPanel.add(group);
			
			for(Filter f: categoryFilters)
			{
				if(group.expanded)
				{
					String filterName = f.getName();
					JCheckBox filterBox = group.getFilterBox(filterName);
					if(filterBox == null)
					{
						FilterMouseListener listener = new FilterMouseListener(fc, this, f);
			    		filterBox = new JCheckBox(filterName);
			    		filterBox.addMouseListener(listener);
			    		filterBox.setSelected(fc.getFilterState(f));
			    		
			    		group.addFilterBox(filterBox);
					}
					
					filtersPanel.add(filterBox);
				}
			}
    	}
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
        private FilterPanel panel;
        private Filter filter;

        private boolean lastState = true;
        
        public FilterMouseListener(FlightController fc, FilterPanel panel, Filter filter)
        {
            this.fc = fc;
            this.panel = panel;
            this.filter = filter;
        }

		@Override
		public void mouseClicked(MouseEvent e)
		{
			// Right-Click
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				// updating model
				lastState = !lastState;
				fc.mutateAllInTypeExcept(filter, lastState);
				
				// Updating view
				FilterGroup group = panel.filterGroups.get(filter.getCategory().toString());
				if(group != null)
				{
					for(JCheckBox box: group.filterBoxes)
					{
						if(box != e.getSource())
						{
							box.doClick();
						}
					}
				}
				
			} // Left-Click
			else if(e.getButton() == MouseEvent.BUTTON1)
			{
				fc.mutateFilter(filter, ((JCheckBox) e.getSource()).isSelected());
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

    protected static class GroupMouseListener implements MouseListener
    {
    	private FilterPanel panel;

    	public GroupMouseListener(FilterPanel panel)
    	{
    		this.panel = panel;
    	}
    	
		@Override
		public void mouseClicked(MouseEvent e)
		{
			FilterGroup source = (FilterGroup)e.getSource();
			source.expanded = !source.expanded;
			
			panel.recomputeFilters();
		}

		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
    }
    
    protected static class FilterGroup extends JLabel
    {
    	private String name;
    	private boolean expanded;
    	private ArrayList<JCheckBox> filterBoxes;
    	
    	public FilterGroup(String name)
    	{
    		this(name, false);
    	}
    	
    	public FilterGroup(String name, boolean expanded)
    	{
    		super(name);
    		
    		this.name = name;
    		this.expanded = expanded;
    		
    		filterBoxes = new ArrayList<JCheckBox>();
    		
    		// Getting the font and creating the border.
            Font headingFont = this.getFont();
        	headingFont.deriveFont(Font.BOLD, headingFont.getSize() + 5);
        	Border headingBorder = BorderFactory.createRaisedSoftBevelBorder();
        	
            // Setting the font, foreground colour, swing constants alignment and border.
        	this.setFont(headingFont);
        	this.setForeground(Color.RED);
        	this.setHorizontalAlignment(SwingConstants.CENTER);
        	this.setBorder(headingBorder);
    	}
    	
    	@Override 
    	public String getName()
    	{
    		return this.name;
    	}
    	
    	public void addFilterBox(JCheckBox box)
    	{
    		filterBoxes.add(box);
    	}
    	
    	public JCheckBox getFilterBox(String name)
    	{
    		for(JCheckBox box: filterBoxes)
    		{
    			if(box.getText().equals(name))
    			{
    				return box;
    			}
    		}
    		
    		return null;
    	}
    }
}
