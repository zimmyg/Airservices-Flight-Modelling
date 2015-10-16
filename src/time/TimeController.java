/** 
 * @author Group K
 * La Trobe University
 * CSE3PRA/B 2015
 */
package time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimeController
{
	// Earliest time and latest time represent the earliest and latest 
	// dates on flights loaded into the system
	private Date earliestTime, latestTime;
	private GregorianCalendar currentTime;
	private int timeScale;
	private boolean paused = true;
	
	public TimeController()
	{
		this(Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), 1);
	}
	
	public TimeController(Date startDate, Date endDate, int timeScale)
	{
		this.earliestTime = startDate;
		this.latestTime = endDate;
		this.currentTime = new GregorianCalendar();
		this.currentTime.setTime(earliestTime);
		
		this.timeScale = timeScale;
	}
	
	public Date advance()
	{
		return this.advance(this.timeScale * 1000);
	}
	
	public Date advance(int millis)
	{
		Date result = currentTime.getTime();
		
		if(!paused)
		{
			// This is a serious issue, I'm not sure why it doesn't have a long add
			currentTime.add(Calendar.MILLISECOND, millis);
			Date proposed = currentTime.getTime();
			
			if(proposed.after(latestTime))
			{
				proposed = latestTime;
				currentTime.setTime(latestTime);
			}
			
			result = proposed;
		}
		
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
	
	public long getTimeScale()
	{
		return this.timeScale;
	}
	
	public void setTimeScale(int scale)
	{
		this.timeScale = scale;
	}
	
	public Date getEarliest()
	{
		return this.earliestTime;
	}
	
	public Date getLatest()
	{
		return this.latestTime;
	}
	
	public boolean isPaused()
	{
		return this.paused;
	}
	
	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}
}
