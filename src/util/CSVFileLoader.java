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

/**
 * @author Tim, Hui
 * This class can load flight data from a properly formatted CSV file.
 */
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
					
					// change the operation by name
					switch (OP) {
						case "DEP":
							OP = "DEPARTURE";
							break;
						case "ARR":
							OP = "ARRIVAL";
							break;
					}
					
					// change the departure airport codes by name
					ADEP = getAirportNameForCode(ADEP);
					
					// change the arrival airport codes by name
					ADES = getAirportNameForCode(ADES);
					
					Filter adepFilter = new Filter(ADEP, Filter.FilterCategory.AIRPORT);
					if(!result.filters.contains(adepFilter))
						result.filters.add(adepFilter);

					Filter adesFilter = new Filter(ADES, Filter.FilterCategory.AIRPORT);
					if(!result.filters.contains(adesFilter))
						result.filters.add(adesFilter);

					Filter flTypeFilter = new Filter(FL_T, Filter.FilterCategory.FLIGHT_TYPE);
					if(!result.filters.contains(flTypeFilter))
						result.filters.add(flTypeFilter);

					//Filter rwyFilter = new Filter(RWY, Filter.FilterCategory.RUNWAY);
					//if(!result.filters.contains(rwyFilter))
					//	result.filters.add(rwyFilter);

					//Filter wtcFilter = new Filter(WTC, Filter.FilterCategory.WTC);
					//if(!result.filters.contains(wtcFilter))
					//	result.filters.add(wtcFilter);
					
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
	
		result.filters.add(new Filter("DEPARTURE", Filter.FilterCategory.OPERATION));
		result.filters.add(new Filter("ARRIVAL", Filter.FilterCategory.OPERATION));
		
		return result;
	}
	
	private String getAirportNameForCode(String code)
	{
		String name = code;
		switch (code) {
			case "YBCG":
				name = "Gold Coast";
				break;
			case "YMML":
				name = "Melbourne";
				break;
			case "NFFN":
				name = "Nadi";
				break;
			case "YMLT":
				name = "Launceston";
				break;
			case "YBBN":
				name = "Brisbane";
				break;
			case "YBTL":
				name = "Townsville";
				break;
			case "ZGGG":
				name = "Guangzhou";
				break;
			case "YBNA":
				name = "Yoona";
				break;
			case "OMDB":
				name = "Dubai";
				break;
			case "YBAS":
				name = "Alice Springs";
				break;
			case "YMER":
				name = "Merimbula";
				break;
			case "WMKK":
				name = "Kuala Lumpur";
				break;
			case "NZAA":
				name = "Auckland";
				break;
			case "YBSU":
				name = "Sunshine Coast";
				break;
			case "OTBD":
				name = "Doha";
				break;
			case "YBMK":
				name = "Mackay";
				break;
			case "YPDN":
				name = "Darwin";
				break;
			case "WBSB":
				name = "Brunei";
				break;
			case "ZSPD":
				name = "Shanghai Pudong";
				break;
			case "YPPH":
				name = "Perth";
				break;
			case "VTBS":
				name = "Suvarnabhumi";
				break;
			case "VVTS":
				name = "Tan Son Nhat";
				break;
			case "YSCB":
				name = "Canberra";
				break;
			case "KLAX":
				name = "Los Angeles";
				break;
			case "YWYY":
				name = "Burnie";
				break;
			case "NZWN":
				name = "Wellington";
				break;
			case "YMIA":
				name = "Mildura";
				break;
			case "YKII":
				name = "King Island";
				break;
			case "WIII":
				name = "Soekarno-Hatta";
				break;
			case "OMAA":
				name = "Abu Dhabi";
				break;
			case "WSSS":
				name = "Singapore";
				break;
			case "NZDN":
				name = "Dunedin";
				break;
			case "PHNL":
				name = "Honolulu";
				break;
			case "YSSY":
				name = "Sydney";
				break;
			case "YDPO":
				name = "Devonport";
				break;
			case "WADD":
				name = "Ngurah Rai";
				break;
			case "YWLM":
				name = "Newcastle";
				break;
			case "YMHB":
				name = "Hobart";
				break;
			case "YMAY":
				name = "Albury";
				break;
			case "RPLL":
				name = "Ninoy Aquino";
				break;
			case "YMTG":
				name = "Mount Gambier";
				break;
			case "YPAD":
				name = "Adelaide";
				break;
			case "VHHH":
				name = "Hong Kong";
				break;
			case "YBCS":
				name = "Cairns";
				break;
			case "NZCH":
				name = "Christchurch";
				break;
		}
		
		return name;
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
