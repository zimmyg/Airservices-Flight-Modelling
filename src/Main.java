import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.ScalebarLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.layers.Earth.OSMMapnikLayer;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.StatisticsPanel;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.ToolTipController;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.opencsv.CSVReader;

public class Main
{
	//MUST IMPLEMENT
	//---------------
	//TODO: Add time controlling
	//		- Need to add models (Or something to represent the flight, possibly a variation on the directedpath arrows)
	//TODO: Add flight info viewing (using popups, probably)
	
	//Optional Extras
	//---------------
	//TODO: Clip view into range around Australasia (Not really necessary, but could be a nice touch)
	//TODO: Rendering to video output (IF WE HAVE TIME!!)
	
	// This is peters revision
	
	// This is Pearl's revision
	
	public ArrayList<Flight> flights;
	public Hashtable<Filter, Boolean> filters;

	private SimpleDateFormat dateFormatter;

	public Main()
	{
		flights = new ArrayList<Flight>();
		filters = new Hashtable<Filter, Boolean>();
		dateFormatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

		try
		{
			CSVReader reader = new CSVReader(new FileReader("ODAS.csv"));

			// readNext returns an array of values from the line it reads.
			// Call once to get the column headings out of the way.
			String[] line = reader.readNext();
			Flight cur = null;
			ArrayList<Position> pathPositions = null;
			while ((line = reader.readNext()) != null)
			{
				// Check the file format is somewhat correct
				if (line.length == 16)
				{
					if (line[0].equals("0"))
					{
						// Finish previous entry
						if (cur != null)
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

						Filter acTypeFilter = new Filter(AC_T,
								Filter.FilterCategory.AIRCRAFT_TYPE);
						filters.putIfAbsent(acTypeFilter, true);

						Filter adepFilter = new Filter(ADEP,
								Filter.FilterCategory.AIRPORT);
						filters.putIfAbsent(adepFilter, true);

						Filter adesFilter = new Filter(ADES,
								Filter.FilterCategory.AIRPORT);
						filters.putIfAbsent(adesFilter, true);

						Filter flTypeFilter = new Filter(FL_T,
								Filter.FilterCategory.FLIGHT_TYPE);
						filters.putIfAbsent(flTypeFilter, true);

						Filter rwyFilter = new Filter(RWY,
								Filter.FilterCategory.RUNWAY);
						filters.putIfAbsent(rwyFilter, true);

						Filter wtcFilter = new Filter(WTC,
								Filter.FilterCategory.WTC);
						filters.putIfAbsent(wtcFilter, true);

						cur = new Flight(ODAS, OP, ADEP, ADES, RWY, SID, CALL,
								AC_T, FL_T, WTC);
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

			// When we reach EOF we must
			// finish the trailing flight, if there was one.
			if (cur != null)
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

		filters.putIfAbsent(new Filter("DEP", Filter.FilterCategory.OPERATION),
				true);
		filters.putIfAbsent(new Filter("ARR", Filter.FilterCategory.OPERATION),
				true);
		
		// System.out.printf("Filters in system: %s", filters.toString());
	}

	public void updateFlightFilters()
	{
		for (Flight f : flights)
		{
			Filter searchFilters[] = { new Filter(f.OP, null),
					new Filter(f.ADEP, null), new Filter(f.ADES, null),
					new Filter(f.RWY, null), new Filter(f.AC_TYPE, null),
					new Filter(f.FL_TYPE, null), new Filter(f.WTC, null) };

			// Operation
			if(!filters.get(searchFilters[0]))
			{
				f.flightPath.setVisible(false);
				continue;
			}
			
			//Checking against operation and airport
			if (f.OP.equals("DEP"))
			{
				if(!filters.get(searchFilters[1]))
				{
					f.flightPath.setVisible(false);
					continue;
				}
			}
			else
			{
				if(!filters.get(searchFilters[2]))
				{
					f.flightPath.setVisible(false);
					continue;
				}
			}
			
			// Runway
			if(!filters.get(searchFilters[3]))
			{
				f.flightPath.setVisible(false);
				continue;
			}
			
			// Aircraft Type
			if(!filters.get(searchFilters[4]))
			{
				f.flightPath.setVisible(false);
				continue;
			}
			
			// Flight Type
			if(!filters.get(searchFilters[5]))
			{
				f.flightPath.setVisible(false);
				continue;
			}
			
			// WTC
			if(!filters.get(searchFilters[6]))
			{
				f.flightPath.setVisible(false);
				continue;
			}
			
			f.flightPath.setVisible(true);
		}
	}

	public static class AppPanel extends JPanel
	{
		protected WorldWindow wwd;
		protected StatusBar statusBar;
		protected ToolTipController toolTipController;
		protected HighlightController highlightController;

		public AppPanel(Dimension canvasSize, boolean includeStatusBar)
		{
			super(new BorderLayout());

			this.wwd = this.createWorldWindow();
			((Component) this.wwd).setPreferredSize(canvasSize);

			String globeName = Configuration
					.getStringValue(AVKey.GLOBE_CLASS_NAME);
			if (globeName == null)
				return;
			Globe globe = (Globe) WorldWind.createComponent(globeName);

			Layer layers[] = { new SkyGradientLayer(), new OSMMapnikLayer(),
					new ScalebarLayer(), new WorldMapLayer(),
					new CompassLayer() };

			Model m = new BasicModel(globe, new LayerList(layers));
			this.wwd.setModel(m);

			// Setup a select listener for the worldmap click-and-go feature
			this.wwd.addSelectListener(new ClickAndGoSelectListener(this
					.getWwd(), WorldMapLayer.class));

			this.add((Component) this.wwd, BorderLayout.CENTER);
			if (includeStatusBar)
			{
				this.statusBar = new StatusBar();
				this.add(statusBar, BorderLayout.PAGE_END);
				this.statusBar.setEventSource(wwd);
			}

			// Add controllers to manage highlighting and tool tips.
			this.toolTipController = new ToolTipController(this.getWwd(),
					AVKey.DISPLAY_NAME, null);
			this.highlightController = new HighlightController(this.getWwd(),
					SelectEvent.ROLLOVER);
		}

		protected WorldWindow createWorldWindow()
		{
			return new WorldWindowGLCanvas();
		}

		public WorldWindow getWwd()
		{
			return wwd;
		}

		public StatusBar getStatusBar()
		{
			return statusBar;
		}
	}

	protected static class AppFrame extends JFrame
	{
		private Dimension canvasSize = new Dimension(800, 600);

		protected AppPanel wwjPanel;
		protected FilterPanel filterPanel;
		protected StatisticsPanel statsPanel;

		public AppFrame()
		{
			this(true, true, false);
		}

		public AppFrame(Dimension size)
		{
			this.canvasSize = size;
			this.initialize(true, true, false);
		}

		public AppFrame(boolean includeStatusBar, boolean includeFilterPanel,
				boolean includeStatsPanel)
		{
			this.initialize(includeStatusBar, includeFilterPanel,
					includeStatsPanel);
		}

		protected void initialize(boolean includeStatusBar,
				boolean includeFilterPanel, boolean includeStatsPanel)
		{
			// Need this up here so we can create the FilterPanel
			Main main = new Main();

			// Create the WorldWindow.
			this.wwjPanel = this.createAppPanel(this.canvasSize,
					includeStatusBar);
			this.wwjPanel.setPreferredSize(canvasSize);

			// Put the pieces together.
			this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
			if (includeFilterPanel)
			{
				// NOTE: WE NEED TO REARRANGE SO THE MAIN IS CREATED BEFORE
				// HERE!!
				this.filterPanel = new FilterPanel(this.wwjPanel.getWwd(), main);
				this.getContentPane().add(this.filterPanel, BorderLayout.WEST);
			}

			if (includeStatsPanel
					|| System.getProperty("gov.nasa.worldwind.showStatistics") != null)
			{
				this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(),
						new Dimension(250, canvasSize.height));
				this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
			}

			// Register a rendering exception listener that's notified when
			// exceptions occur during rendering.
			this.wwjPanel.getWwd().addRenderingExceptionListener(
					new RenderingExceptionListener() {
						public void exceptionThrown(Throwable t)
						{
							if (t instanceof WWAbsentRequirementException)
							{
								String message = "Computer does not meet minimum graphics requirements.\n";
								message += "Please install up-to-date graphics driver and try again.\n";
								message += "Reason: " + t.getMessage() + "\n";
								message += "This program will end when you press OK.";

								JOptionPane.showMessageDialog(AppFrame.this,
										message, "Unable to Start Program",
										JOptionPane.ERROR_MESSAGE);
								System.exit(-1);
							}
						}
					});

			// Search the layer list for layers that are also select listeners
			// and register them with the World
			// Window. This enables interactive layers to be included without
			// specific knowledge of them here.
			for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers())
			{
				if (layer instanceof SelectListener)
				{
					this.getWwd().addSelectListener((SelectListener) layer);
				}
			}

			this.pack();

			// Center the application on the screen.
			WWUtil.alignComponent(null, this, AVKey.CENTER);
			this.setResizable(true);

			// This is our part, we load in our CSV and add the renderable layer
			RenderableLayer layer = new RenderableLayer();
			layer.setName("Flight Paths");

			// Just for now, we'll use the same attributes for testing
			ShapeAttributes att = new BasicShapeAttributes();

			// Set shape attributes for each path and add it to the layer
			for (Flight f : main.flights)
			{
				f.attributes = att;

				Path fp = f.flightPath;
				fp.setAttributes(att);
				fp.setVisible(true);
				fp.setAltitudeMode(WorldWind.ABSOLUTE);
				fp.setPathType(AVKey.GREAT_CIRCLE);

				layer.addRenderable(fp);
			}

			insertBeforeCompass(getWwd(), layer);
			//
		}

		protected AppPanel createAppPanel(Dimension canvasSize,
				boolean includeStatusBar)
		{
			return new AppPanel(canvasSize, includeStatusBar);
		}

		public Dimension getCanvasSize()
		{
			return canvasSize;
		}

		public AppPanel getWwjPanel()
		{
			return wwjPanel;
		}

		public WorldWindow getWwd()
		{
			return this.wwjPanel.getWwd();
		}

		public StatusBar getStatusBar()
		{
			return this.wwjPanel.getStatusBar();
		}

		public StatisticsPanel getStatsPanel()
		{
			return statsPanel;
		}

		public void setToolTipController(ToolTipController controller)
		{
			if (this.wwjPanel.toolTipController != null)
				this.wwjPanel.toolTipController.dispose();

			this.wwjPanel.toolTipController = controller;
		}

		public void setHighlightController(HighlightController controller)
		{
			if (this.wwjPanel.highlightController != null)
				this.wwjPanel.highlightController.dispose();

			this.wwjPanel.highlightController = controller;
		}
	}

	public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
	{
		// Insert the layer into the layer list just before the compass.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof CompassLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

	public static void insertBeforePlacenames(WorldWindow wwd, Layer layer)
	{
		// Insert the layer into the layer list just before the placenames.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof PlaceNameLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

	public static void insertAfterPlacenames(WorldWindow wwd, Layer layer)
	{
		// Insert the layer into the layer list just after the placenames.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof PlaceNameLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition + 1, layer);
	}

	public static void insertBeforeLayerName(WorldWindow wwd, Layer layer,
			String targetName)
	{
		// Insert the layer into the layer list just before the target layer.
		int targetPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l.getName().indexOf(targetName) != -1)
			{
				targetPosition = layers.indexOf(l);
				break;
			}
		}
		layers.add(targetPosition, layer);
	}

	static
	{
		System.setProperty("java.net.useSystemProxies", "true");
		if (Configuration.isMacOS())
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					"World Wind Application");
			System.setProperty("com.apple.mrj.application.growbox.intrudes",
					"false");
			System.setProperty("apple.awt.brushMetalLook", "true");
		}
		else if (Configuration.isWindowsOS())
		{
			System.setProperty("sun.awt.noerasebackground", "true"); // prevents
																		// flashing
																		// during
																		// window
																		// resizing
		}
	}

	public static AppFrame start(String appName, Class appFrameClass)
	{
		if (Configuration.isMacOS() && appName != null)
		{
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", appName);
		}

		try
		{
			final AppFrame frame = (AppFrame) appFrameClass.newInstance();
			frame.setTitle(appName);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run()
				{
					frame.setVisible(true);
				}
			});

			return frame;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args)
	{
		Configuration.setValue(AVKey.INITIAL_LATITUDE, -37.67);
		Configuration.setValue(AVKey.INITIAL_LONGITUDE, 144.84);
		Configuration.setValue(AVKey.INITIAL_ALTITUDE, 5e5);

		start("World Wind Application", AppFrame.class);
	}
}