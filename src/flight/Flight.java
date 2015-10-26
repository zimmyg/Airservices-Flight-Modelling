package flight;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * 
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
	
	private DirectedPositionLabelPath flightPath;
	
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
				
		ShapeAttributes attributes = new BasicShapeAttributes();
		attributes.setOutlineMaterial(new Material(OP.equals("ARRIVAL") ? Color.GREEN : Color.RED));
		ShapeAttributes highlightAttributes = new BasicShapeAttributes();
		highlightAttributes.setOutlineMaterial(new Material(Color.BLACK));
		
		this.flightPath = new DirectedPositionLabelPath(new ArrayList<Position>(), new ArrayList<Date>(), new ArrayList<Double>());
		this.flightPath.setAttributes(attributes);
		this.flightPath.setHighlightAttributes(highlightAttributes);
		this.flightPath.setVisible(true);
		this.flightPath.setAltitudeMode(WorldWind.ABSOLUTE);
		this.flightPath.setPathType(AVKey.GREAT_CIRCLE);
	}
	
	public String getOpertaion()
	{
		return this.OP;
	}
	
	public ShapeAttributes getRenderAttributes()
	{
		return flightPath.getAttributes();
	}
	
	public String[] getFilterableFields()
	{
		return new String[] { OP, ADEP, ADES, AC_TYPE, FL_TYPE };
	}
	
	public void setVelocities(ArrayList<Double> velocities)
	{
		flightPath.setVelocities(velocities);
	}
	
	public ArrayList<Double> getVelocities()
	{
		return flightPath.getVelocities();
	}
	
	public void setTimestamps(ArrayList<Date> timestamps)
	{
		flightPath.setTimestamps(timestamps);
	}
	
	public ArrayList<Date> getTimestamps()
	{
		return flightPath.setTimestamps();
	}
	
	public void setPositions(ArrayList<Position> positions)
	{
		flightPath.setPositions(positions);
	}
	
	public Iterable<? extends Position> getPositions()
	{
		return flightPath.getPositions();
	}
	
	public void setVisible(boolean visible)
	{
		flightPath.setVisible(visible);
	}

	public void addToRenderLayer(RenderableLayer layer)
	{
		if(layer != null) 
		{
			layer.addRenderable(flightPath);
		}
	}
	
	public void setTime(Date time)
	{
		flightPath.setTime(time);
	}

	public String getID()
	{
		return ODAS_ID;
	}

	public void setHighlighted(boolean b)
	{
		flightPath.setHighlighted(b);
	}

	public DirectedPositionLabelPath getFlightPath()
	{
		return flightPath;
	}
	
	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();
		
		output.append("ODAS Id: ");
		output.append(ODAS_ID);
		output.append("\n");
		
		output.append("Operation: ");
		output.append(OP);
		output.append("\n");
		
		output.append("Departure AP: ");
		output.append(ADEP);
		output.append("\n");
		
		output.append("Arrival AP: ");
		output.append(ADES);
		output.append("\n");
		
		output.append("Runway: ");
		output.append(RWY);
		output.append("\n");
		
		output.append("SID STAR: ");
		output.append(SID_STAR);
		output.append("\n");
		
		output.append("Callsign: ");
		output.append(CALL);
		output.append("\n");
		
		output.append("Aircraft Type: ");
		output.append(AC_TYPE);
		output.append("\n");
		
		output.append("Flight Type: ");
		output.append(FL_TYPE);
		output.append("\n");
		
		output.append("Wake Turbulence Category: ");
		output.append(WTC);
		
		return output.toString();
	}
}
