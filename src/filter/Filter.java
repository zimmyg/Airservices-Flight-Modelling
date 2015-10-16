package filter;

/** 
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * 
 * This class defines a "Filter" that can be used to apply search filters to the flight data.
 */
public class Filter {
	
	// This enum represents categories that filters can appy to. This is the field that they affect.
	public enum FilterCategory
	{
		OPERATION, AIRPORT, RUNWAY, AIRCRAFT_TYPE, FLIGHT_TYPE, WTC
	}
	
	// filter name and category attributes.
	private String name;
	private FilterCategory category;
	
	// Constructor to initialize filter name and category.
	public Filter(String name, FilterCategory category)
	{
		this.name = name;
		this.category = category;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public FilterCategory getCategory()
	{
		return this.category;
	}
	
	//format name and category text.
	@Override
	public String toString()
	{
		return String.format("%s: %s", category, name);
	}
	
	// Object equality
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof Filter)
		{
			Filter o = (Filter)other;
			return o.name.equals(name);
		}
		
		return false;
	}
	
	// Needed for keeping this object in a Hashed data container.
	@Override
	public int hashCode()
	{
		int result = this.name.hashCode();
		
		return result;
	}
}
