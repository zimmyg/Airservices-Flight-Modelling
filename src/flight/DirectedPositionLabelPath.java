package flight;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.terrain.Terrain;
import gov.nasa.worldwindx.examples.util.DirectedPath;
import gov.nasa.worldwindx.examples.util.LabeledPath;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Date;

import com.jogamp.common.nio.Buffers;

/**
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * This class represents an object that can be rendered by the worldwind system,
 *  which shows a flight path as well as a direction arrow indicating where the
 *  flight is at the current simulation time.
 */
public class DirectedPositionLabelPath extends DirectedPath
{
	private Date currentTime;
	private ArrayList<Date> positionTimes;
	private ArrayList<Double> positionVelocities;
	
	public DirectedPositionLabelPath()
	{
		super();
		positionTimes = new ArrayList<Date>();
		positionVelocities = new ArrayList<Double>();
	}
	
	public DirectedPositionLabelPath(Iterable<? extends Position> positions, ArrayList<Date> times, ArrayList<Double> velocities)
	{
		super(positions);
		positionTimes = times;
		positionVelocities = velocities;
	}
	
	protected void computeDirectionArrows(DrawContext dc, PathData pathData)
    {
		int closestIndexBefore = getClosestIndexBefore(currentTime);
		int closestIndexAfter = getClosestIndexAfter(currentTime);
		
		if(closestIndexBefore == -1)
		{
			closestIndexBefore = 0;
		}
		
		if(closestIndexAfter == -1)
		{
			closestIndexAfter = positionTimes.size() - 1;
		}
		
		Position positionA = null;
		Position positionB = null;
		
		int index = 0;
		for(Position p: positions)
		{
			if(index == closestIndexBefore)
			{
				positionA = p;
			}
			if(index == closestIndexAfter)
			{
				positionB = p;
			}
			
			index++;
		}
		
		Terrain terrain = dc.getTerrain();
		double arrowBase = this.getArrowLength() * this.getArrowAngle().tanHalfAngle();
		
		final int FLOATS_PER_ARROWHEAD = 9; // 3 points * 3 coordinates per point
        FloatBuffer buffer = (FloatBuffer) pathData.getValue(ARROWS_KEY);
        if (buffer == null || buffer.capacity() < FLOATS_PER_ARROWHEAD)
            buffer = Buffers.newDirectFloatBuffer(FLOATS_PER_ARROWHEAD);
        pathData.setValue(ARROWS_KEY, buffer);
        buffer.clear();

        if(positionA == null || positionB == null) return;
        
        Vec4 polePtA = this.computePoint(terrain, positionA);
        Vec4 polePtB = this.computePoint(terrain, positionB);
        Vec4 ptA = this.computePoint(terrain, positionA);
        Vec4 ptB = this.computePoint(terrain, positionB);
 
        this.computeArrowheadGeometry(dc, polePtA, polePtB, ptA, ptB, 
        		this.getArrowLength(), arrowBase, buffer, pathData);
    }
    
    private int getClosestIndexBefore(Date time)
    {
    	int closestIndex = -1;
    	
    	if(currentTime != null)
    	{	
			for(int i = 0; i < positionTimes.size(); ++i)
			{
				Date d = positionTimes.get(i);
				if(d.before(time))
				{
					closestIndex = i; 
				}
				else
				{
					break;
				}
			}    	
    	}
    	
    	return closestIndex;
    }
    
    private int getClosestIndexAfter(Date time)
    {
    	int closestIndex = -1;
    	
    	if(currentTime != null)
    	{	
			for(int i = positionTimes.size() - 1; i >= 0; --i)
			{
				Date d = positionTimes.get(i);
				if(d.after(time))
				{
					closestIndex = i; 
				}
				else
				{
					break;
				}
			}
    	}
    	
    	return closestIndex;
    }

	public void setVelocities(ArrayList<Double> velocities)
	{
		this.positionVelocities = velocities;
	}

	public ArrayList<Double> getVelocities()
	{
		return positionVelocities;
	}

	public void setTimestamps(ArrayList<Date> timestamps)
	{
		positionTimes = timestamps;
	}
	
	public ArrayList<Date> setTimestamps()
	{
		return positionTimes;
	}

	public void setTime(Date time)
	{
		this.currentTime = time;
	}

	// This algorithm is just totally wrong, but if it works, who cares
	public Position getNearestPositionFor(Position p)
	{
		Position result = null;
		
		double closest = Double.MAX_VALUE;
		for(Position pos: positions)
		{
			double diff = p.linearDistance(p, pos).degrees;
			if( diff < closest )
			{
				closest = diff;
				result = pos;
			}
		}
		
		return result;
	}
}
