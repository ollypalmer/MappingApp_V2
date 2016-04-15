package mapping;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import data.Observation;

/**
 * This class is responsible for holding the map data. It adds the data set supplied from the app frame
 * to the map, a two dimensional array, the contents of this array are then drawn to the JPanel
 * @author Oliver Palmer
 *
 */
public class OccupancyGridMap extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Observation> data;
	private int zoom = 2;
	private int width = 1500;
	private int height = 1500;
	private int mapSizeX = 0;
	private int mapSizeY = 0;
	private int smoothing = 20;
	private int maxX = 0;
	private int minX = 0;
	private int maxY = 0;
	private int minY = 0;
	private float[][] map;
	
	public OccupancyGridMap() {
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.lightGray);
	}
	
	/**
	 * Recalculates the map size and adds observation data to the map
	 * @param data
	 */
	public void addData(ArrayList<Observation> data) {
		calcSize(data);
		map = new float[mapSizeX][mapSizeY];
		this.data = data;
		//int count = 0;
		for (Observation o : data) {
			int x = Math.round(o.getX()) / smoothing + (minX * -1) + 10;
			int y = Math.round(o.getY()) / smoothing + (minY * -1) + 10;
			
			// Debug
			// System.out.println(count + ": x = " + (x) + " y = " + (y));
			// count++;
			
			// Determines if an observation is of occupied or unoccupied space
			if (o.getValue() == 1) {
				addLandmark(x, y, o.getHeading());
			} else if (o.getValue() == 0) {
				addLocation(x, y);
			}
		}
		repaint();
	}
	
	/**
	 * Calculates the maximum size of the array required to hold the data
	 * @param data - the observations from the EV3
	 */
	public void calcSize(ArrayList<Observation> data) {
		
		for (Observation o : data) {
			int x = Math.round(o.getX()) / smoothing;
			int y = Math.round(o.getY()) / smoothing;
			if (x > maxX) {
				maxX = x;
			} else if (x < minX) {
				minX = x;
			}
			if (y > maxY) {
				maxY = y;
			} else if (y < minY) {
				minY = y;
			}
		}
		
		// 20 is added to the map size to accommodate for the additional values added to account for error
		mapSizeX = (maxX - minX) + 20;
		mapSizeY = (maxY - minY) + 20;
		
		// Debug
		// System.out.println("max x = " + maxX + " min x = " + minX + " - mapSize: " + mapSizeX);
		// System.out.println("max y = " + maxY + " min y = " + minY + " - mapSize: " + mapSizeY);
	}
	
	/**
	 * Adds to the value in the array held at the X and Y location to represent occupied space
	 * @param x location for the array
	 * @param y location for the array
	 * @param heading - direction the EV3 was facing when the observation was made
	 */
	public void addLandmark(int x, int y, float heading) {
		map[x][y] += 0.5;

		// for loop for width
		for (int i = -2; i < 3; i++) {
			// for loop for length
			for (int j = -8; j < 9; j++) {
				// Determines which direction the EV3 was facing
				if ((heading < -45 && heading > -135) || (heading > 45 && heading < 135)) {
					// Decreases amount added when further away from the original value
					if (i <= -2 || j <= -4 || i <= 2 || j <= 4) {
						map[x + j][y + i] += 0.8;
					} else {
						map[x + j][y + i] += 0.5;
					}
				} else {
					// Decreases amount added when further away from the original value
					if (i <= -1 || j <= -4 || i <= 1 || j <= 4) {
						map[x + i][y + j] += 0.8;
					} else {
						map[x + i][y + j] += 0.5;
					}
				}
			}
		}
	}
	
	/**
	 * Subtracts from the value in the array held at the X and Y location to represent unoccupied space
	 * @param x location for the array
	 * @param y location for the array
	 */
	public void addLocation(int x, int y) {
		map[x][y] -= 0.5;
		
		// for loop for y axis
		for (int i = -5; i < 6; i++) {
			//for loop for x axis
			for (int j = -5; j < 6; j++) {
				// Decreases amount subtracted when further away from the original value
				if (i <= -3 || j <= -3 || i <= 3 || j <= 3) {
					map[x + j][y + i] -= 0.5;
				} else {
					map[x + j][y + i] -= 0.2;
				}
			}
		}
	}
	
	/**
	 * Sets the zoom value and redraws the map
	 * @param value from the zoom slider
	 */
	public void zoom(int value){
		this.zoom = value;
		repaint();
	}
	
	/**
	 * Sets the smoothing value and refreshes the map data
	 * @param value from the smoothing slider
	 */
	public void smooth(int value) {
		this.smoothing = value;
		addData(data);
	}
	
	/**
	 * The map is stepped through one cell at a time, the contents of the cell are drawn according to the
	 * value of the figure contained at that location in the array. Black represents occupied space, dark gray
	 * represents probably occupied space, gray represents undetermined space and white represents unoccupied space
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int cellSize = zoom;
		
		// Finds the centre of the panel
		int x = width / 2 - ((mapSizeX / 2) * cellSize);
		int y = height / 2 - ((mapSizeY / 2) * cellSize);
		
		if (data != null) {
			// for loop for y
			for (int i = 0; i < mapSizeY; i++) {
				// for loop for x
				for (int j = 0; j < mapSizeX; j++) {

					if (map[j][i] >= 1) {
						g.setColor(Color.BLACK);
						g.fillRect(x, y, cellSize, cellSize);
					} else if (map[j][i] < 1 && map[j][i] >= 0.5 ){
						g.setColor(Color.DARK_GRAY);
						g.fillRect(x, y, cellSize, cellSize);
					} else if (map[j][i] < 0.5 && map[j][i] > 0 ) {
						g.setColor(Color.GRAY);
						g.fillRect(x, y, cellSize, cellSize);
					}else if (map[j][i] < 0) {
						g.setColor(Color.WHITE);
						g.fillRect(x, y, cellSize, cellSize);
					}
					x += cellSize;
				}
				y += cellSize;
				x -= mapSizeX * cellSize;
			} 
		}
	}
}
