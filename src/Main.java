import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.opencsv.CSVReader;

public class Main extends ApplicationTemplate
{
	public ArrayList<Flight> flights;
	private SimpleDateFormat dateFormatter;
	
	public Main()
	{
		flights = new ArrayList<Flight>();
		dateFormatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		
		try
		{
			CSVReader reader = new CSVReader(new FileReader("ODAS.csv"));
			
			// readNext returns an array of values from the line it reads.
			// Call once to get the column headings out of the way.
			String[] line = reader.readNext();
			Flight cur = null;
			ArrayList<Position> pathPositions = null;
			while((line = reader.readNext()) != null)
			{
				// Check the file format is somewhat correct
				if(line.length == 16)
				{
					if(line[0].equals("0"))
					{
						// Finish previous entry
						if(cur != null)
						{
							cur.flightPath.setPositions(pathPositions);
							flights.add(cur);
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
						
						cur = new Flight(ODAS, OP, ADEP, ADES, RWY, SID, CALL, AC_T, FL_T, WTC);
						pathPositions = new ArrayList<Position>();
					}
					
					double lat = Double.parseDouble(line[12]);
					double lon = Double.parseDouble(line[13]);
					double alt = Double.parseDouble(line[14]);
					double vel = Double.parseDouble(line[15]);
					Date time = dateFormatter.parse(line[2]);
					
					pathPositions.add(Position.fromDegrees(lat, lon, alt));
					cur.velocities.add(vel);
					cur.timestamps.add(time);
				}
				else
				{
					break;
				}
			}
			
			// Since the last read will always fail (EOF),
			// we must finish the trailing flight, if there was one.
			if(cur != null)
			{
				cur.flightPath.setPositions(pathPositions);
				flights.add(cur);
			}
			
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, true);
            Main m = new Main();
            
            RenderableLayer layer = new RenderableLayer();
            layer.setName("Flight Paths");
            
            // Just for now, we'll use the same attributes for testing
            ShapeAttributes att = new BasicShapeAttributes();

            // Set shape attributes for each path and add it to the layer
            for(Flight f: m.flights)
            {
            	Path fp = f.flightPath;
            	fp.setAttributes(att);
                fp.setVisible(true);
                fp.setAltitudeMode(WorldWind.ABSOLUTE);
                fp.setPathType(AVKey.GREAT_CIRCLE);

                layer.addRenderable(fp);
            }
            
            insertBeforeCompass(getWwd(), layer);
            this.getLayerPanel().update(this.getWwd());
        }
    }

	public static void main(String[] args)
	{
		Configuration.setValue(AVKey.INITIAL_LATITUDE, -37.67);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 144.84);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 5e4);
        
		ApplicationTemplate.start("World Wind Application", AppFrame.class);
	}
}