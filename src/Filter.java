public class Filter {
	public enum FilterCategory
	{
		OPERATION, AIRPORT, RUNWAY, AIRCRAFT_TYPE, FLIGHT_TYPE, WTC
	}
	
	private String name;
	private FilterCategory category;
	
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
	
	@Override
	public String toString()
	{
		return String.format("%s: %s", category, name);
	}
	
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
	
	@Override
	public int hashCode()
	{
		int result = this.name.hashCode();
		
		return result;
	}
}
