package testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import flight.Flight;

public class FlightTests
{
	@Test
	public void testGetFilterableFields()
	{
		String ODAS = "ODAS_ID";
		String OP = "OPERATION";
		String ADEP = "DEPART";
		String ADES = "ARRIVE";
		String RWY = "RUNWAY";
		String SID = "SID_STAR";
		String CALL = "CALLSIGN";
		String AC_T = "AC_TYPE";
		String FL_T = "FLIGHT_TYPE";
		String WTC = "WTC";
		
		Flight f = new Flight( ODAS, OP, ADEP, ADES, RWY, SID, CALL, AC_T, FL_T, WTC );
		
		String[] fields = f.getFilterableFields();
		String[] expectedFields = { OP, ADEP, ADES, RWY, AC_T, FL_T, WTC };
		
		assertArrayEquals("Checking filterable fields are returned correctly.", expectedFields, fields);
	}

	@Test
	public void testGetVelocities()
	{
		ArrayList<Double> velocities = new ArrayList<Double>();
		
		velocities.add(10.0);
		velocities.add(10.0);
		velocities.add(20.0);
		velocities.add(20.0);
		velocities.add(50.0);
		velocities.add(50.0);
		velocities.add(100000.0);
		
		Flight f = new Flight();
		for(Double d: velocities)
		{
			f.getVelocities().add(d);
		}
		
		for(int i = 0; i < velocities.size(); i++)
		{
			double expected = velocities.get(i);
			double actual = f.getVelocities().get(i);
			
			assertTrue(expected == actual);
		}
	}

	@Test
	public void testGetTimestamps()
	{
		ArrayList<Date> times = new ArrayList<Date>();
		
		times.add(new Date(0));
		times.add(new Date(1));
		times.add(new Date(10));
		times.add(new Date(100));
		times.add(new Date(1000));
		times.add(new Date(10000));
		times.add(new Date(100000));
		
		Flight f = new Flight();
		for(Date d: times)
		{
			f.getTimestamps().add(d);
		}
		
		for(int i = 0; i < times.size(); i++)
		{
			Date expected = times.get(i);
			Date actual = f.getTimestamps().get(i);
			
			assertEquals(expected, actual);
		}
	}

}
