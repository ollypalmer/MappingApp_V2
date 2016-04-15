package main;

import javax.swing.JFrame;

import frame.AppFrame;

/**
 * Controller class to run the application
 * 
 * @author Oliver Palmer
 *
 */
public class Control {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				AppFrame frame = new AppFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(620,640);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}