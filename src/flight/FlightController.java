package flight;

import filter.Filter;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import time.TimeController;

/**
 * @author Tim
 * This is a Controller class that acts as an interface for interacting with flights.
 */
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
	
	public void updateFilter_FlightVisibilities()
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

			// Arrival airport
			if(f.getOpertaion().equals("ARRIVAL") && !filters.get(searchFilters[1]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// Departure airport
			if(f.getOpertaion().equals("DEPARTURE") && !filters.get(searchFilters[2]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// Runway
//			if(!filters.get(searchFilters[3]))
//			{
//				f.getFlightPath().setVisible(false);
//				continue;
//			}
			
			// Aircraft Type
			if(!filters.get(searchFilters[3]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// Flight Type
			if(!filters.get(searchFilters[4]))
			{
				f.getFlightPath().setVisible(false);
				continue;
			}
			
			// WTC
//			if(!filters.get(searchFilters[6]))
//			{
//				f.getFlightPath().setVisible(false);
//				continue;
//			}
//			
			f.getFlightPath().setVisible(true);
		}
		
		this.wwd.redraw();
	}

	//TODO: We've got bugs here
	public void updateTime_FlightVisibilities(TimeController tc)
	{	
		Date time = tc.getTime();
		
		for(Flight f: flights)
		{
			ArrayList<Date> flTimes = f.getTimestamps();
			if(flTimes.size() > 0)
			{				
				Date earliest = flTimes.get(0);
				Date latest = flTimes.get(flTimes.size() - 1);
				
				if(earliest.before(time) && latest.after(time))
				{
					f.getFlightPath().setVisible(true);
				}
				else
				{
					f.getFlightPath().setVisible(false);
				}
			}
			
			// Dealing with position interpolation
			Date closestBefore = flTimes.get(0);
			Date closestAfter = null;
			for(Date d: flTimes)
			{
				if(d.before(time))
				{
					closestBefore = d;
				}
				else
				{
					closestAfter = d;
					break;
				}
			}
			
			f.getFlightPath();
			// ---
		}
	}
}
