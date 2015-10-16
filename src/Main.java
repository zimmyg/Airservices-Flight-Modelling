import filter.Filter;
import filter.FilterPanel;
import flight.Flight;
import flight.FlightController;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
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
import input.InputPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import time.TimeControlPanel;
import time.TimeController;
import util.CSVFileLoader;

/**
 * @author Tim
 * La Trobe University
 * CSE3PRA/B 2015
 * 
 * This class is the main controller and entry point of the program.
 */
public class Main implements RenderingListener
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
	
	
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	
	private FlightController fc;
	private TimeController timeController;
	
	private FilterPanel filterPanel;
	private TimeControlPanel timePanel;
	//Input panel need to be changed to popup window
	//private InputPanel filePanel;
	
	// Flight path animation
	private long lastTime;
	
	public Main()
	{
		CSVFileLoader CSVLoader = new CSVFileLoader();
		CSVFileLoader.CSVLoadResult loadResult = null;
		
		try
		{
			 loadResult = CSVLoader.loadCSVFile("ODAS.csv", DATE_FORMATTER);
		} 
		catch(Exception e)
		{
			// Logging, dealing with errors. For now we'll crash if it messes up.
			e.printStackTrace();
			System.exit(-1);
		}
		
		fc = new FlightController();
		fc.addFlights(loadResult.flights);
		
		for(Filter filt: loadResult.filters)
		{
			fc.addFilter(filt, true);
		}
		
		timeController = new TimeController(loadResult.earliestDate, loadResult.latestDate, 1);
	}
	
	public static class AppPanel extends JPanel
	{
		protected WorldWindow wwd;
		protected StatusBar statusBar;
		protected ToolTipController toolTipController;
		protected HighlightController highlightController;
		protected TimeController timeController;
		
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
			
			this.timeController = new TimeController();
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

		protected JMenuBar menuBar;
		protected AppPanel wwjPanel;
		protected FilterPanel filterPanel;
		protected TimeControlPanel timePanel;
		//protected InputPanel filePanel;
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
			// Create the WorldWindow.
			this.wwjPanel = this.createAppPanel(this.canvasSize,
					includeStatusBar);
			this.wwjPanel.setPreferredSize(canvasSize);
			
			this.menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			JMenuItem openCSVMenuItem = new JMenuItem("Open CSV File");
			// Hui, you can use this menuitem to open your file dialog window. You'll have to use an ActionListener to detect when the button was clicked
			// Add action listener for opencsv file button
			openCSVMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					// this is where you deal with the button press
				}
			});
			
			fileMenu.add(openCSVMenuItem);
			this.menuBar.add(fileMenu);
			this.getContentPane().add(menuBar, BorderLayout.PAGE_START);
			
			// Need this up here so we can create the FilterPanel
			Main main = new Main();
			main.fc.setWWD(wwjPanel.wwd);
			
			// Put the pieces together.
			this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
			if (includeFilterPanel)
			{
				// Add Filter Panel
				this.filterPanel = new FilterPanel(main.fc);
				main.filterPanel = this.filterPanel;
				
				// Add Time Panel
				this.timePanel = new TimeControlPanel(main.timeController, DATE_FORMATTER);
				this.timePanel.setPreferredSize(new Dimension(200, 100));
				main.timePanel = this.timePanel;
				
				//Input panel need to be changed to popup window
				// Add File Choose Panel
				//this.filePanel = new InputPanel();
				//this.filePanel.setVisible(true);
				//this.filePanel.setPreferredSize(new Dimension(200, 200));
				//main.filePanel = this.filePanel;
				
				//TODO: Theres some weird resizing bug here, its to do with swing. Fix later.
				JPanel westPanel = new JPanel(new BorderLayout());
				//westPanel.add(this.filePanel, BorderLayout.NORTH);
				westPanel.add(this.filterPanel, BorderLayout.NORTH);
				westPanel.add(this.timePanel, BorderLayout.SOUTH);
				
				//westPanel.setPreferredSize(new Dimension(200, 400));

				this.getContentPane().add(westPanel, BorderLayout.WEST);
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
			
			this.wwjPanel.getWwd().addRenderingListener(main);

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

			RenderableLayer layer = new RenderableLayer();
			layer.setName("Flight Paths");

			// Set shape attributes for each path and add it to the layer
			for (Flight f : main.fc.getFlights())
			{
				Path fp = f.getFlightPath();
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

		AppFrame frame = start("Aircraft World Wind Application", AppFrame.class);
	}

	// This is where we handle the animation of the flight paths
	// Its here because we need info from both the TimeController and the FlightController
	// I think we'll need to change this, because the screen only gets re-rendered when it has changed.
	// Maybe use Animator or something?
	@Override
	public void stageChanged(RenderingEvent event)
	{
		if (event.getStage().equals(RenderingEvent.BEFORE_RENDERING))
        {
            long now = System.currentTimeMillis();
            long delta = (now - lastTime);
            lastTime = now;
            
            // Cast to an int because the calendar class doesn't do long.
            // Also translate timeScale into milliseconds.
            int timeAdvance = (int)( timeController.getTimeScale() * 1000 * delta);
            timeController.advance( timeAdvance );
            timePanel.update(timeController);
            
            fc.updateTime_FlightVisibilities(timeController);
        }
	}
}