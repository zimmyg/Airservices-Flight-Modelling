import filter.Filter;
import filter.FilterPanel;
import flight.DirectedPositionLabelPath;
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
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.util.StatisticsPanel;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.ToolTipController;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import time.TimeControlPanel;
import util.CSVFileLoader;

import com.jogamp.opengl.util.Animator;


public class Main extends JFrame
{
	// App panel
	private JPanel appPanel;
	private WorldWindow wwd;
	private RenderableLayer flightLayer;
	private RenderableLayer labelLayer;
	private GlobeAnnotation flightLabel;
	private StatusBar statusBar;
	private ToolTipController toolTipController;
	private HighlightController highlightController;
	
	// App Frame
	private Dimension canvasSize = new Dimension(800, 600);
	private JMenuBar menuBar;
	private FilterPanel filterPanel;
	private TimeControlPanel timePanel;
	private StatisticsPanel statsPanel;
	
	// Main
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	private FlightController flightController;
	
	// Flight path animation
	Animator animator;
	private long lastTime;
	
	public Main(boolean includeStatusBar, boolean includeFilterPanel, boolean includeStatsPanel)
	{
		flightController = new FlightController();
		flightController.setDateFormatter(DATE_FORMATTER);
		
		appPanel = new JPanel(new BorderLayout());
		wwd = new WorldWindowGLCanvas();
		((Component) wwd).setPreferredSize(canvasSize);
		
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
		this.wwd.addSelectListener(new ClickAndGoSelectListener(wwd, WorldMapLayer.class));

		appPanel.add((Component) this.wwd, BorderLayout.CENTER);
		if (includeStatusBar)
		{
			this.statusBar = new StatusBar();
			this.add(statusBar, BorderLayout.PAGE_END);
			this.statusBar.setEventSource(wwd);
		}

		// Add controllers to manage highlighting and tool tips.
		this.toolTipController = new ToolTipController(wwd, AVKey.DISPLAY_NAME, null);
		this.highlightController = new HighlightController(wwd, SelectEvent.ROLLOVER);
		
		appPanel.setPreferredSize(canvasSize);
		
		
		this.menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openCSVMenuItem = new JMenuItem("Open CSV File");
		openCSVMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser(".");
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "CSV Files", "csv");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	loadCSVFile(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		
		fileMenu.add(openCSVMenuItem);
		this.menuBar.add(fileMenu);
		this.getContentPane().add(menuBar, BorderLayout.PAGE_START);
		
		// Put the pieces together.
		this.getContentPane().add(appPanel, BorderLayout.CENTER);
		if (includeFilterPanel)
		{
			// Filter Panel
			filterPanel = new FilterPanel(flightController);
			
			// Time Panel
			timePanel = new TimeControlPanel(flightController);
			timePanel.setPreferredSize(new Dimension(200, 100));
			
			//TODO: Theres some weird resizing bug here, its to do with swing. Fix later.
			JPanel westPanel = new JPanel(new BorderLayout());
			westPanel.add(this.filterPanel, BorderLayout.NORTH);
			westPanel.add(this.timePanel, BorderLayout.SOUTH);
			
			flightController.setFilterPanel(filterPanel);
			flightController.setTimePanel(timePanel);
			this.getContentPane().add(westPanel, BorderLayout.WEST);
		}
	
		if (includeStatsPanel
				|| System.getProperty("gov.nasa.worldwind.showStatistics") != null)
		{
			this.statsPanel = new StatisticsPanel(wwd, new Dimension(250, canvasSize.height));
			this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
		}
	
		// Register a rendering exception listener that's notified when
		// exceptions occur during rendering.
		wwd.addRenderingExceptionListener(
				new RenderingExceptionListener() {
					public void exceptionThrown(Throwable t)
					{
						if (t instanceof WWAbsentRequirementException)
						{
							String message = "Computer does not meet minimum graphics requirements.\n";
							message += "Please install up-to-date graphics driver and try again.\n";
							message += "Reason: " + t.getMessage() + "\n";
							message += "This program will end when you press OK.";
	
							JOptionPane.showMessageDialog(null,
									message, "Unable to Start Program",
									JOptionPane.ERROR_MESSAGE);
							System.exit(-1);
						}
					}
				});
		
		wwd.addRenderingListener(new RenderingListener() {
			public void stageChanged(RenderingEvent event)
			{
				if (event.getStage().equals(RenderingEvent.BEFORE_RENDERING))
		        {
		            long now = System.currentTimeMillis();
		            long delta = (now - lastTime);
		            lastTime = now;
		            
		            flightController.notifyTimeChange(delta);
		        }
			}
		});
		
		wwd.addSelectListener(new SelectListener() {
			public void selected(SelectEvent event)
			{
				if(event.getEventAction().equals(SelectEvent.LEFT_CLICK) &&
						event.hasObjects() && 
						(event.getTopObject() instanceof DirectedPositionLabelPath) &&
						flightLabel == null) 
				{
					DirectedPositionLabelPath path = (DirectedPositionLabelPath) event.getTopObject();
					
					String text = flightController.getFlightDetailsForPath(path);
					
					Position pickPoint = wwd.getCurrentPosition();
					Position pos = path.getNearestPositionFor(pickPoint);
					
					flightLabel = new GlobeAnnotation(text, pos);
					flightLabel.getAttributes().setVisible(true);
					labelLayer.addRenderable(flightLabel);
				}
				else if(event.getEventAction().equals(SelectEvent.LEFT_CLICK))
				{
					labelLayer.removeAllRenderables();
					flightLabel = null;
				}
			}
		});
	
		// Search the layer list for layers that are also select listeners
		// and register them with the World
		// Window. This enables interactive layers to be included without
		// specific knowledge of them here.
		for (Layer layer : wwd.getModel().getLayers())
		{
			if (layer instanceof SelectListener)
			{
				wwd.addSelectListener((SelectListener) layer);
			}
		}
	
		this.pack();
	
		// Center the application on the screen.
		WWUtil.alignComponent(null, this, AVKey.CENTER);
		this.setResizable(true);

		// Create the layer and insert it into the layer list just before the compass.
		flightLayer = new RenderableLayer();
		flightLayer.setName("Flight Paths");
		
		labelLayer = new RenderableLayer();
		labelLayer.setName("Flight Label");
		
		int compassPosition = 0;
		LayerList layerList = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof CompassLayer)
				compassPosition = layerList.indexOf(l);
		}
		layerList.add(compassPosition, labelLayer);
		layerList.add(compassPosition, flightLayer);

		// Setup animation
		lastTime = System.currentTimeMillis();
		animator = new Animator((WorldWindowGLCanvas) wwd);
		animator.start();
	}
	
	private void loadCSVFile(String filename)
	{
		CSVFileLoader CSVLoader = new CSVFileLoader();
		CSVFileLoader.CSVLoadResult loadResult = null;
		
		try
		{
			 loadResult = CSVLoader.loadCSVFile(filename, DATE_FORMATTER);
		} 
		catch(Exception e)
		{
			// Logging, dealing with errors. For now we'll crash if it messes up.
			e.printStackTrace();
			System.exit(-1);
		}
		
		for(Flight f: loadResult.flights)
		{			
			flightController.addFlight(f);
			f.addToRenderLayer(flightLayer);
		}
		
		for(Filter filt: loadResult.filters)
		{
			flightController.addFilter(filt, true);
		}
		
		this.filterPanel.recomputeFilters();
		this.flightController.setTime(loadResult.earliestDate);
	}
	
	public static void main(String[] args)
	{
		Configuration.setValue(AVKey.INITIAL_LATITUDE, -37.67);
		Configuration.setValue(AVKey.INITIAL_LONGITUDE, 144.84);
		Configuration.setValue(AVKey.INITIAL_ALTITUDE, 5e5);
		
		String appName = "ASA - Flight Planner";
		
		if (Configuration.isMacOS() && appName != null)
		{
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", appName);
		}

		try
		{
			final Main main = new Main(true, true, false);
			main.setTitle(appName);
			main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run()
				{
					main.setVisible(true);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
