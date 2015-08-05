import gov.nasa.worldwindx.examples.util.DirectedPath;

import java.util.ArrayList;
import java.util.Date;


public class Flight 
{
	public String 	ODAS_ID, 
					OP, 
					ADEP, 
					ADES, 
					RWY, 
					SID_STAR,
					CALL,
					AC_TYPE, 
					FL_TYPE, 
					WTC;
	
	public DirectedPath flightPath;
	public ArrayList<Double> velocities;
	public ArrayList<Date> timestamps;
}
