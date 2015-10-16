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

/**
 * @author Tim
 * This class defines the flight object, it is used to house attributes related 
 * to the flight and its renderable path that come from CSV files.
 */
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
		return new String[] { OP, ADEP, ADES, AC_TYPE, FL_TYPE };
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
}
