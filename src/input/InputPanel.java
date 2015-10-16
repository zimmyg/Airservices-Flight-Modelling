/** 
 * @author Group K
 * La Trobe University
 * CSE3PRA/B 2015
 */
package input;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

// InputPanel is used to enable user to choose the CSV file 
// to input into the program

public class InputPanel extends JPanel
{	
	private JLabel MessageLabel;
	private JButton confirmButton;
	private JFileChooser fileChoose;
	
	protected JPanel filePanel1;
	
	public InputPanel()
	{
		// Make a panel at a default size.
		super(new BorderLayout());
		this.makePanel(new Dimension(250,200));
	}
	
	public InputPanel(Dimension size)
	{
		// Make a panel at a specified size.
		super(new BorderLayout());
		this.makePanel(size);
	}

	private void makePanel(Dimension size) {
		// Make and fill the panel holding the input file controller
		// TODO: size of the panel need to be fixed
		this.filePanel1 = new JPanel(new GridLayout(2,2));
		this.filePanel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.filePanel1.setPreferredSize(new Dimension(250,200));
		
		JLabel MessageLabel = new JLabel("Please choose the csv file to input");
		JFileChooser fileChoose = new JFileChooser();
		JButton confirmButton = new JButton("INPUT");
		
		filePanel1.add(MessageLabel);
		filePanel1.add(fileChoose);
		filePanel1.add(confirmButton);
		
		// Add the file panel to a titled panel that will resize with the main window.
        JPanel encompassPanel = new JPanel( new BorderLayout() ); //new GridLayout(0, 1, 0, 10));
        encompassPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("File Input")));
        encompassPanel.setToolTipText("Controls for altering time");
        encompassPanel.add(filePanel1, BorderLayout.CENTER);
        this.add(encompassPanel, BorderLayout.CENTER);
	}
}
