import java.util.Calendar;
import java.util.Date;


public class TimeController
{
	// Earliest time and latest time represent the earliest and latest 
	// dates on flights loaded into the system
	private Date earliestTime, latestTime;
	private Calendar currentTime;
	private int timeAdvanceSec;
	
	public TimeController()
	{
		this(Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), 1);
	}
	
	public TimeController(Date startDate, Date endDate, int timeAdvanceSec)
	{
		this.earliestTime = startDate;
		this.latestTime = endDate;
		this.currentTime = Calendar.getInstance();
		this.currentTime.setTime(earliestTime);
		
		this.timeAdvanceSec = timeAdvanceSec;
	}
	
	public Date advance()
	{
		return this.advance(this.timeAdvanceSec);
	}
	
	public Date advance(int seconds)
	{
		Date result;
		
		currentTime.add(Calendar.SECOND, seconds);
		Date proposed = currentTime.getTime();
		
		if(proposed.after(latestTime))
		{
			proposed = latestTime;
			currentTime.setTime(latestTime);
		}
		
		result = proposed;
		
		return result;
	}
	
	public Date getTime()
	{
		return this.currentTime.getTime();
	}
	
	public void setTime(Date time)
	{
		this.currentTime.setTime(time);
	}
	
	public int getTimeAdvance()
	{
		return this.timeAdvanceSec;
	}
	
	public void setTimeAdvance(int seconds)
	{
		this.timeAdvanceSec = seconds;
	}
}
