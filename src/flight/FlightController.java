package flight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import time.TimeControlPanel;
import filter.Filter;
import filter.FilterPanel;

/**
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * 
 * This is a Controller class that acts as an interface for interacting with flights.
 */
public class FlightController
{
	// Views
	private TimeControlPanel tcPanel;
	private FilterPanel filPanel;
	
	// Model(s)
	private ArrayList<Flight> flights;
	private Hashtable<Filter, Boolean> filters;
	
	// Time/Animation
	private Date earliestTime, latestTime;
	private GregorianCalendar currentTime;
	private float timeScale;
	private boolean paused;
	
	// For displaying times properly
	SimpleDateFormat dateFormatter;
	
	public FlightController()
	{
		flights = new ArrayList<Flight>();
		filters = new Hashtable<Filter, Boolean>();
		
		earliestTime = new Date(Long.MAX_VALUE);
		latestTime = new Date(0);
		currentTime = new GregorianCalendar();
		timeScale = 1.0f;
		paused = true;
	}
	
	public void setDateFormatter(SimpleDateFormat formatter)
	{
		dateFormatter = formatter;
	}
	
	public void setTimePanel(TimeControlPanel tcPanel)
	{
		this.tcPanel = tcPanel;
	}
	
	public TimeControlPanel getTimePanel()
	{
		return tcPanel;
	}
	
	public void setFilterPanel(FilterPanel filPanel)
	{
		this.filPanel = filPanel;
	}
	
	public FilterPanel getFilterPanel()
	{
		return filPanel;
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean pause)
	{
		this.paused = pause;
	}
	
	public float getTimeScale()
	{
		return timeScale;
	}
	
	public void setTimeScale(float scale)
	{
		this.timeScale = scale;
	}
	
	public void addFlight(Flight f)
	{
		ArrayList<Date> timestamps = f.getTimestamps();
		if(timestamps.size() > 0)
		{
			if(earliestTime.after(timestamps.get(0)))
			{
				earliestTime = timestamps.get(0);
			}
			if(latestTime.before(timestamps.get(timestamps.size() - 1)))
			{
				latestTime = timestamps.get(timestamps.size() - 1);
			}
		}
		f.setTime(currentTime.getTime());
		
		flights.add(f);
	}
	
	public List<Flight> getFlights()
	{
		return flights;
	}
	
	public void addFlights(List<Flight> flights)
	{
		for(Flight f: flights)
		{
			this.addFlight(f);
		}
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
	
	public void mutateAllInTypeExcept(Filter exceptFilter, boolean newValue)
	{
		for(Filter f: getAllFiltersOfType(exceptFilter.getCategory()))
		{
			if(!f.equals(exceptFilter))
			{
				mutateFilter(f, newValue);
			}
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
	
	public boolean getFilterState(Filter f)
	{
		boolean result = false;
		
		if(filters.containsKey(f))
		{
			result = filters.get(f);
		}
		
		return result;
	}
	
	public void updateFlightVisibilities()
	{
		for (Flight f : this.flights)
		{
			f.setVisible(true);
			
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
				f.setVisible(false);
				continue;
			}

			// Arrival airport
			if(f.getOpertaion().equals("ARRIVAL") && !filters.get(searchFilters[1]))
			{
				f.setVisible(false);
				continue;
			}
			
			// Departure airport
			if(f.getOpertaion().equals("DEPARTURE") && !filters.get(searchFilters[2]))
			{
				f.setVisible(false);
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
				f.setVisible(false);
				continue;
			}
			
			// Flight Type
			if(!filters.get(searchFilters[4]))
			{
				f.setVisible(false);
				continue;
			}
			
			// WTC
//			if(!filters.get(searchFilters[6]))
//			{
//				f.getFlightPath().setVisible(false);
//				continue;
//			}
			
			Date time = currentTime.getTime();
			f.setTime(time);
			
			ArrayList<Date> flTimes = f.getTimestamps();
			if(flTimes.size() > 0)
			{				
				Date earliest = flTimes.get(0);
				Date latest = flTimes.get(flTimes.size() - 1);
				
				if(!(earliest.before(time) && latest.after(time)))
				{
					f.setVisible(false);
				}
			}
		}
	}

	//TODO: We've got bugs here
	public void notifyTimeChange(long delta)
	{	
		long actualMilliseconds = (long)( currentTime.getTimeInMillis() + (delta * (double)timeScale) );
		currentTime.setTimeInMillis(actualMilliseconds);
		
		if(tcPanel != null && dateFormatter != null)
		{
			String timeString = dateFormatter.format(currentTime.getTime());
			tcPanel.updateTimeDisplay(timeString);
		}
		
		updateFlightVisibilities();
	}

	public String getFlightDetailsForPath(DirectedPositionLabelPath path)
	{
		String result = "";
		
		for(Flight f: flights)
		{
			if(f.getFlightPath() == path)
			{
				result = f.toString();
			}
		}
		
		return result;
	}

	public void setTime(Date earliestDate)
	{
		long delta = currentTime.getTime().getTime() - earliestDate.getTime();
		notifyTimeChange(delta);
	}
}
