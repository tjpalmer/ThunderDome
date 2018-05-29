package virassan.main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.JFrame;

/**
 * Creates the main Window
 * @author Virassan
 *
 */
public class Display {

	private JFrame frame;
	private Canvas canvas;
	private String title;
	//public static int WIDTH = 1280, HEIGHT = (int)(WIDTH * (9.0f / 16.0f));
	private int width, height;
	
	private GraphicsDevice vc;
	//public int WIDTH = 1280, HEIGHT = WIDTH / 16 * 9;
	
	public Display(String title)
	{
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		vc = genv.getDefaultScreenDevice();
		//width = vc.getDisplayMode().getWidth();
		//height = vc.getDisplayMode().getHeight();
		width = 1240;
		height = 900;
		DisplayMode dm = new DisplayMode(width, height, DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
		this.title = title;
		createDisplay();
		
		//setFullScreen(dm, frame);
		
	}
	/*
	public void setFullScreen(DisplayMode dm, JFrame frame){
		vc.setFullScreenWindow(frame);
		
		if(dm != null && vc.isDisplayChangeSupported()){
			try{
				vc.setDisplayMode(dm);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	*/
	
	public void restoreScreen(){
		Window w = vc.getFullScreenWindow();
		if(w != null){
			w.dispose();
		}
		vc.setFullScreenWindow(null);
	}
	
	public void createDisplay()
	{
		frame = new JFrame(title);
		frame.setUndecorated(true);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);
		
		frame.add(canvas);
		frame.pack();	
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public Canvas getCanvas(){
		return canvas;
	}
	
	public Window getFullScreenWindow(){
		return vc.getFullScreenWindow();
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}
