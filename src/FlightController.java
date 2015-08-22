import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;


public class FlightController
{
	// Model
	private ArrayList<Flight> flights = new ArrayList<Flight>();
	
	// View
	private WorldWindow wwd;
	
	// Controller
	private Hashtable<Filter, Boolean> filters = new Hashtable<Filter, Boolean>();
	
	
	public FlightController()
	{
		this.wwd = null;
	}
	
	public FlightController(WorldWindow wwd)
	{
		this.wwd = wwd;
	}
	
	public WorldWindow getWWD()
	{
		return this.wwd;
	}
	
	public void setWWD(WorldWindow wwd)
	{
		this.wwd = wwd;
	}
	
	public void addFlight(Flight f)
	{
		flights.add(f);
	}
	
	public List<Flight> getFlights()
	{
		return flights;
	}
	
	public void addFlights(List<Flight> flights)
	{
		this.flights.addAll(flights);
	}
	
	public void addFilter(Filter f, boolean isActivated)
	{
		this.filters.putIfAbsent(f, isActivated);
	}
	
	public void mutateFilter(Filter f, boolean newValue)
	{
		if(filters.get(f) != null)
		{
			filters.put(f, newValue);
		}
	}
	
	public List<Filter> getAllFiltersOfType(Filter.FilterCategory type)
	{
		List<Filter> result = new LinkedList<Filter>();
		
		for(Filter f: filters.keySet())
		{
			if(f.getCategory().equals(type))
			{
				result.add(f);
			}
		}
		
		return result;
	}

	public List<Filter> getFilters()
	{
		return new LinkedList<Filter>(filters.keySet());	
	}
	
	public void updateFlightVisibilities()
	{
		for (Flight f : this.flights)
		{
			String[] filterFields = f.getFilterableFields();
			Filter searchFilters[] = new Filter[filterFields.length];
			for(int i = 0; i < filterFields.length; i++)
			{
				String s = filterFields[i];
				searchFilters[i] = new Filter(s, null);
			}

			// Operation
			if(!filters.get(searchFilters[0]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			
			// Check again for bugs here after change
			// -------------------------

			// Departure airport
			if(!filters.get(searchFilters[2]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// Arrival airport
			if(!filters.get(searchFilters[1]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// -------------------------
			
			// Runway
			if(!filters.get(searchFilters[3]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// Aircraft Type
			if(!filters.get(searchFilters[4]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// Flight Type
			if(!filters.get(searchFilters[5]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// WTC
			if(!filters.get(searchFilters[6]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			f.getFlightPath().setVisible(true);
		}
		
		this.wwd.redraw();
	}
}
