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
					switch (ADEP) {
						case "YBCG":
							ADEP = "Gold Coast";
							break;
						case "YMML":
							ADEP = "Melbourne";
							break;
						case "NFFN":
							ADEP = "Nadi";
							break;
						case "YMLT":
							ADEP = "Launceston";
							break;
						case "YBBN":
							ADEP = "Brisbane";
							break;
						case "YBTL":
							ADEP = "Townsville";
							break;
						case "ZGGG":
							ADEP = "Guangzhou";
							break;
						case "YBNA":
							ADEP = "Yoona";
							break;
						case "OMDB":
							ADEP = "Dubai";
							break;
						case "YBAS":
							ADEP = "Alice Springs";
							break;
						case "YMER":
							ADEP = "Merimbula";
							break;
						case "WMKK":
							ADEP = "Kuala Lumpur";
							break;
						case "NZAA":
							ADEP = "Auckland";
							break;
						case "YBSU":
							ADEP = "Sunshine Coast";
							break;
						case "OTBD":
							ADEP = "Doha";
							break;
						case "YBMK":
							ADEP = "Mackay";
							break;
						case "YPDN":
							ADEP = "Darwin";
							break;
						case "WBSB":
							ADEP = "Brunei";
							break;
						case "ZSPD":
							ADEP = "Shanghai Pudong";
							break;
						case "YPPH":
							ADEP = "Perth";
							break;
						case "VTBS":
							ADEP = "Suvarnabhumi";
							break;
						case "VVTS":
							ADEP = "Tan Son Nhat";
							break;
						case "YSCB":
							ADEP = "Canberra";
							break;
						case "KLAX":
							ADEP = "Los Angeles";
							break;
						case "YWYY":
							ADEP = "Burnie";
							break;
						case "NZWN":
							ADEP = "Wellington";
							break;
						case "YMIA":
							ADEP = "Mildura";
							break;
						case "YKII":
							ADEP = "King Island";
							break;
						case "WIII":
							ADEP = "Soekarno-Hatta";
							break;
						case "OMAA":
							ADEP = "Abu Dhabi";
							break;
						case "WSSS":
							ADEP = "Singapore";
							break;
						case "NZDN":
							ADEP = "Dunedin";
							break;
						case "PHNL":
							ADEP = "Honolulu";
							break;
						case "YSSY":
							ADEP = "Sydney";
							break;
						case "YDPO":
							ADEP = "Devonport";
							break;
						case "WADD":
							ADEP = "Ngurah Rai";
							break;
						case "YWLM":
							ADEP = "Newcastle";
							break;
						case "YMHB":
							ADEP = "Hobart";
							break;
						case "YMAY":
							ADEP = "Albury";
							break;
						case "RPLL":
							ADEP = "Ninoy Aquino";
							break;
						case "YMTG":
							ADEP = "Mount Gambier";
							break;
						case "YPAD":
							ADEP = "Adelaide";
							break;
						case "VHHH":
							ADEP = "Hong Kong";
							break;
						case "YBCS":
							ADEP = "Cairns";
							break;
						case "NZCH":
							ADEP = "Christchurch";
							break;
					}
					
					// change the arrival airport codes by name
					switch (ADES) {
					case "YBCG":
						ADES = "Gold Coast";
						break;
					case "YMML":
						ADES = "Melbourne";
						break;
					case "NFFN":
						ADES = "Nadi";
						break;
					case "YMLT":
						ADES = "Launceston";
						break;
					case "YBBN":
						ADES = "Brisbane";
						break;
					case "YBTL":
						ADES = "Townsville";
						break;
					case "ZGGG":
						ADES = "Guangzhou";
						break;
					case "YBNA":
						ADES = "Yoona";
						break;
					case "OMDB":
						ADES = "Dubai";
						break;
					case "YBAS":
						ADES = "Alice Springs";
						break;
					case "YMER":
						ADES = "Merimbula";
						break;
					case "WMKK":
						ADES = "Kuala Lumpur";
						break;
					case "NZAA":
						ADES = "Auckland";
						break;
					case "YBSU":
						ADES = "Sunshine Coast";
						break;
					case "OTBD":
						ADES = "Doha";
						break;
					case "YBMK":
						ADES = "Mackay";
						break;
					case "YPDN":
						ADES = "Darwin";
						break;
					case "WBSB":
						ADES = "Brunei";
						break;
					case "ZSPD":
						ADES = "Shanghai Pudong";
						break;
					case "YPPH":
						ADES = "Perth";
						break;
					case "VTBS":
						ADES = "Suvarnabhumi";
						break;
					case "VVTS":
						ADES = "Tan Son Nhat";
						break;
					case "YSCB":
						ADES = "Canberra";
						break;
					case "KLAX":
						ADES = "Los Angeles";
						break;
					case "YWYY":
						ADES = "Burnie";
						break;
					case "NZWN":
						ADES = "Wellington";
						break;
					case "YMIA":
						ADES = "Mildura";
						break;
					case "YKII":
						ADES = "King Island";
						break;
					case "WIII":
						ADES = "Soekarno-Hatta";
						break;
					case "OMAA":
						ADES = "Abu Dhabi";
						break;
					case "WSSS":
						ADES = "Singapore";
						break;
					case "NZDN":
						ADES = "Dunedin";
						break;
					case "PHNL":
						ADES = "Honolulu";
						break;
					case "YSSY":
						ADES = "Sydney";
						break;
					case "YDPO":
						ADES = "Devonport";
						break;
					case "WADD":
						ADES = "Ngurah Rai";
						break;
					case "YWLM":
						ADES = "Newcastle";
						break;
					case "YMHB":
						ADES = "Hobart";
						break;
					case "YMAY":
						ADES = "Albury";
						break;
					case "RPLL":
						ADES = "Ninoy Aquino";
						break;
					case "YMTG":
						ADES = "Mount Gambier";
						break;
					case "YPAD":
						ADES = "Adelaide";
						break;
					case "VHHH":
						ADES = "Hong Kong";
						break;
					case "YBCS":
						ADES = "Cairns";
						break;
					case "NZCH":
						ADES = "Christchurch";
						break;
				}
					
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
	
		result.filters.add(new Filter("DEPARTURE", Filter.FilterCategory.OPERATION));
		result.filters.add(new Filter("ARRIVAL", Filter.FilterCategory.OPERATION));
		
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
