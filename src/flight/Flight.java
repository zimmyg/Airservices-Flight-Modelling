/** 
 * @author Group K
 * La Trobe University
 * CSE3PRA/B 2015
 */

package flight;

import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;


public class Flight 
{
	private String 	ODAS_ID, 
					OP, 
					ADEP, 
					ADES, 
					RWY, 
					SID_STAR,
					CALL,
					AC_TYPE, 
					FL_TYPE, 
					WTC;
	
	private Path flightPath;
	private ShapeAttributes attributes;
	private Color color;
	private ShapeAttributes lineAtts;
	
	private ArrayList<Double> velocities;
	private ArrayList<Date> timestamps;
	
	public Flight()
	{
		this("", "", "", "", "", "", "", "", "", "");
	}
	
	public Flight(String ODAS, String OP, String ADEP, String ADES, String RWY, String SID_STAR, String CALL, String AC_TYPE, String FL_TYPE, String WTC)
	{
		this.ODAS_ID = ODAS;
		this.OP = OP;
		this.ADEP = ADEP;
		this.ADES = ADES;
		this.RWY = RWY;
		this.SID_STAR = SID_STAR;
		this.CALL = CALL;
		this.AC_TYPE = AC_TYPE;
		this.FL_TYPE = FL_TYPE;
		this.WTC = WTC;
		
		
		this.flightPath = new Path();
		this.attributes = new BasicShapeAttributes();
		
		this.velocities = new ArrayList<Double>();
		this.timestamps = new ArrayList<Date>();
		
		// Differ the flight path color by operation
		// TODO: Bugs here to be fixed: can't change the color
		switch(OP){
			case "DEPARTURE":
				this.color = Color.BLUE;
				break;
			case "ARRIVAL":
				this.color = Color.BLACK;
				break;
		}
		this.flightPath.setAttributes(getLineAtts(color));
	}
	
	public String getOpertaion()
	{
		return this.OP;
	}
	
	public ShapeAttributes getRenderAttributes()
	{
		return this.attributes;
	}
	
	public String[] getFilterableFields()
	{
		return new String[] { OP, ADEP, ADES, RWY, AC_TYPE, FL_TYPE, WTC };
	}
	
	public Path getFlightPath()
	{
		return this.flightPath;
	}
	
	public ArrayList<Double> getVelocities()
	{
		return this.velocities;
	}
	
	public ArrayList<Date> getTimestamps()
	{
		return this.timestamps;
	}
	
	private ShapeAttributes getLineAtts(Color color)
    {  
		// Set the Flight Path attributes
        if (lineAtts == null)
            lineAtts = new BasicShapeAttributes();

        lineAtts.setDrawInterior(true);
        lineAtts.setEnableLighting(false);
        lineAtts.setDrawOutline(true);

        double dblLineWidth = 1.0;
        lineAtts.setOutlineWidth(dblLineWidth);
        lineAtts.setOutlineStippleFactor(0);
        lineAtts.setOutlineStipplePattern((short)0xFFFF);
        lineAtts.setOutlineMaterial(new Material(color));
        lineAtts.setOutlineOpacity(0.8);

        lineAtts.setInteriorMaterial(new Material(color));
        lineAtts.setEnableAntialiasing(true);
        lineAtts.setInteriorOpacity(1);
        
        return lineAtts;
    }
}
