import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

public class Main extends ApplicationTemplate
{
	protected static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, true);
            
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