package frame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.Observation;
import mapping.OccupancyGridMap;

/**
 * This is the frame of the application. It is responsible for loading the data from a file
 * and interaction with the sliders.
 * @author Oliver Palmer
 *
 */
public class AppFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private OccupancyGridMap gridMap;
	private JButton load;
	private JLabel zoomLabel = new JLabel("Zoom", JLabel.CENTER);
	private JLabel smoothLabel = new JLabel("Smoothing", JLabel.CENTER);
	private JSlider zoomSlider, smoothingSlider;
	private ArrayList<Observation> data;
	
	public AppFrame(){
		super("Map Viewer");
		
		// Load Button
		load = new JButton("Load");
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				load();
			}
		});
		
		// Zoom Slider
		zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 2);
		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setPaintTicks(true);
		zoomSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				zoomSlider = (JSlider)e.getSource();
				gridMap.zoom((int)zoomSlider.getValue());
			}
		});
		
		// Smoothing Slider
		smoothingSlider = new JSlider(JSlider.HORIZONTAL, 10, 50, 20);
		smoothingSlider.setMajorTickSpacing(2);
		smoothingSlider.setPaintTicks(true);
		smoothingSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				smoothingSlider = (JSlider)e.getSource();
				gridMap.smooth((int)smoothingSlider.getValue());
			}
		});
		
		gridMap = new OccupancyGridMap();
		
		// Configuration for the scroll pane
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(gridMap);
		jsp.setPreferredSize(new Dimension(600, 500));
		jsp.getViewport().setViewPosition(new Point(480, 480));
		
		// Layout Configuration, uses grid bag layout
		Container contentPane = this.getContentPane();
		
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 3;
		contentPane.add(jsp, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 2;
		contentPane.add(zoomLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = 2;
		gc.gridwidth = 2;
		contentPane.add(zoomSlider, gc);
		
		gc.gridx = 0;
		gc.gridy = 3;
		gc.gridwidth = 2;
		contentPane.add(smoothLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = 4;
		gc.gridwidth = 2;
		contentPane.add(smoothingSlider, gc);
		
		gc.gridx = 2;
		gc.gridy = 4;
		gc.gridwidth = 1;
		gc.gridheight = 2;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(0, 0, 0, 5);
		contentPane.add(load, gc);
	}
	
	/**
	 * Used to load the data collected by the EV3 into the program. Steps through each line of the .csv
	 * file adding the data contained to an array list of observations. This array list is then added
	 * to the mapping JPanel for rendering.
	 */
	public void load() {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text", "csv");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(AppFrame.this);
		BufferedReader br = null;

		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				data = new ArrayList<Observation>();
				
				br = new BufferedReader(new FileReader(file));
				String split = ",\\s*";
				String line = "";
				@SuppressWarnings("unused")
				String headerLine = br.readLine();
				
				while ((line = br.readLine()) != null) {
					
					String[] points = line.split(split);
					data.add(new Observation(Float.parseFloat(points[0]), Float.parseFloat(points[1]), Float.parseFloat(points[2]), Float.parseFloat(points[3])));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) { }
			}
		}
		if (data != null) {
			gridMap.addData(data);
		}
	}


}
