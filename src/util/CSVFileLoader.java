package util;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVReader;

import filter.Filter;
import flight.Flight;
import gov.nasa.worldwind.geom.Position;


public class CSVFileLoader
{
	private SimpleDateFormat dateFormatter;
	private float FEET_TO_METERS = 0.3048f;
	
	public CSVLoadResult loadCSVFile(String filename, SimpleDateFormat formatter) throws Exception
	{
		this.dateFormatter = formatter;
		CSVLoadResult result = new CSVLoadResult();
		
		CSVReader reader = new CSVReader(new FileReader(filename));

		// readNext returns an array of values from the line it reads.
		// Call once to get the column headings out of the way.
		String[] line = reader.readNext();
		
		Flight cur = null;
		ArrayList<Position> pathPositions = null;
		
		while ((line = reader.readNext()) != null)
		{
			// Check the file format is at least somewhat correct
			if (line.length == 16)
			{
				if (line[0].equals("0"))
				{
					// Finish previous entry
					if (cur != null)
					{
						cur.getFlightPath().setPositions(pathPositions);
						result.flights.add(cur);
					}

					// New entry
					String ODAS = line[1];
					String OP = line[3];
					String ADEP = line[4];
					String ADES = line[5];
					String RWY = line[6];
					String SID = line[7];
					String CALL = line[8];
					String AC_T = line[9];
					String FL_T = line[10];
					String WTC = line[11];

					Filter acTypeFilter = new Filter(AC_T, Filter.FilterCategory.AIRCRAFT_TYPE);
					if(!result.filters.contains(acTypeFilter))
						result.filters.add(acTypeFilter);

					Filter adepFilter = new Filter(ADEP, Filter.FilterCategory.AIRPORT);
					if(!result.filters.contains(adepFilter))
						result.filters.add(adepFilter);

					Filter adesFilter = new Filter(ADES, Filter.FilterCategory.AIRPORT);
					if(!result.filters.contains(adesFilter))
						result.filters.add(adesFilter);

					Filter flTypeFilter = new Filter(FL_T, Filter.FilterCategory.FLIGHT_TYPE);
					if(!result.filters.contains(flTypeFilter))
						result.filters.add(flTypeFilter);

					Filter rwyFilter = new Filter(RWY, Filter.FilterCategory.RUNWAY);
					if(!result.filters.contains(rwyFilter))
						result.filters.add(rwyFilter);

					Filter wtcFilter = new Filter(WTC, Filter.FilterCategory.WTC);
					if(!result.filters.contains(wtcFilter))
						result.filters.add(wtcFilter);

					cur = new Flight(ODAS, OP, ADEP, ADES, RWY, SID, CALL,
							AC_T, FL_T, WTC);
					pathPositions = new ArrayList<Position>();
				}

				double lat = Double.parseDouble(line[12]);
				double lon = Double.parseDouble(line[13]);
				double alt = FEET_TO_METERS * Double.parseDouble(line[14]);
				double vel = Double.parseDouble(line[15]);
				
				Date time = dateFormatter.parse(line[2]);
				if(time.before(result.earliestDate))
				{
					result.earliestDate = time;
				}
				if(time.after(result.latestDate))
				{
					result.latestDate = time;
				}

				pathPositions.add(Position.fromDegrees(lat, lon, alt));
				cur.getVelocities().add(vel);
				cur.getTimestamps().add(time);
			}
			else
			{
				break;
			}
		}

		// When we reach EOF we must
		// finish the trailing flight, if there was one.
		if (cur != null)
		{
			cur.getFlightPath().setPositions(pathPositions);
			result.flights.add(cur);
		}

		reader.close();
	
		result.filters.add(new Filter("DEP", Filter.FilterCategory.OPERATION));
		result.filters.add(new Filter("ARR", Filter.FilterCategory.OPERATION));
		
		return result;
	}
	
	public class CSVLoadResult
	{	
		public Date earliestDate, latestDate;
		public List<Flight> flights;
		public List<Filter> filters;
		
		public CSVLoadResult()
		{
			earliestDate = new Date(Long.MAX_VALUE);
			latestDate = new Date(0);
			
			flights = new LinkedList<Flight>();
			filters = new LinkedList<Filter>();
		}
	}
}
