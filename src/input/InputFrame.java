package input;

import java.awt.Dimension;
import javax.swing.JFrame;

public class InputFrame extends JFrame 
{
	private Dimension panel_size = new Dimension(300,200);
	protected InputPanel filechoose;
	
	public InputFrame(Dimension size)
	{
		this.panel_size = size;
		// put input panel together here
		this.initialize();
	}
	
	protected void initialize()
	{	
		this.filechoose = this.createPanel(this.panel_size);
		this.filechoose.setPreferredSize(panel_size);
	}
	
	protected InputPanel createPanel(Dimension size)
	{
		return new InputPanel(size);
	}

}
