package virassan.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import virassan.gfx.Assets;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Handler;

public class ScrollPanel {
	
	public static final int barWidth = 16;
	
	private ArrayList<Object> drawObjects;
	private ArrayList<Point> drawPoints;
	private Rectangle shownBox, completeBox;
	private Font font;
	private Color color;
	private int fontHeight;
	private Rectangle buttonUp, buttonDown;
	// private float yOffset;
	

	public ScrollPanel(Rectangle shownBox) {
		this.shownBox = shownBox;
		completeBox = new Rectangle(shownBox.x, shownBox.y, shownBox.width, shownBox.height);
		drawObjects = new ArrayList<>();
		drawPoints = new ArrayList<>();
		// yOffset = 0;
		buttonUp = new Rectangle(0, 0, Assets.scrollButtonUp.getWidth(), Assets.scrollButtonUp.getWidth());
		buttonDown = new Rectangle(0, 0, Assets.scrollButtonDown.getWidth(), Assets.scrollButtonDown.getWidth());
	}
	
	public ScrollPanel(Rectangle shownBox, ArrayList<String> drawStrings, Font stringFont, Color color){
		this(shownBox);
		for(String text : drawStrings){
			drawObjects.add(text);
		}
		font = stringFont;
		this.color = color;
	}
	
	public ScrollPanel(Rectangle shownBox, ArrayList<String> drawStrings, Font stringFont, Color color, BufferedImage image, int imageIndex){
		this(shownBox, drawStrings, stringFont, color);
		drawObjects.add(imageIndex, image);
	}
	
	public void addStrings(ArrayList<String> strings, Font stringFont, FontMetrics fontMetrics, Color color){
		ArrayList<Object> temp = new ArrayList<>();
		for(String text : strings){
			temp.add(text);
		}
		drawObjects = temp;
		font = stringFont;
		this.color = color;
		fontHeight = fontMetrics.getHeight();
		getBoxHeight(fontMetrics);
	}
	
	public void getBoxHeight(FontMetrics fontMetrics){
		int height = 0;
		int x = shownBox.x + 5;
		int y = shownBox.y;
		int stringHeight = fontMetrics.getHeight();
		int heightPadding = 0;
		for(Object obj : drawObjects){
			y += heightPadding;
			if(obj instanceof BufferedImage){
				drawPoints.add(new Point(x, y));
				y += ((BufferedImage)obj).getHeight();
			}else if(obj instanceof String){
				drawPoints.add(new Point(x, y));
				y += stringHeight;
			}
		}
		height = y - shownBox.y;
		completeBox.height = height;
		// System.out.println(height);
	}
	
	public void move(float yAmt){
		//TODO: isn't moving quite right
		if(shownBox.y + shownBox.height + yAmt < shownBox.y + shownBox.height && shownBox.y + shownBox.height + yAmt > completeBox.y){
			for(Point points : drawPoints){
				points.y += yAmt;
			}
		}
	}
	
	public void render(Graphics g){
		renderBar(g);
		//TODO it's being weird especially when it's all the way at the bottom it won't scroll back up and visa versa
		g.setColor(color);
		g.setFont(font);
		int yStart = completeBox.y - 5;
		int yEnd = shownBox.y + shownBox.height - ( 2 * g.getFontMetrics(font).getHeight());
		int y = yStart;
		outer: {
			int stringHeight = g.getFontMetrics(font).getHeight();
			int heightPadding = 0;
			if(drawPoints.size() >= drawObjects.size()){
				// System.out.println("Gonna Try to draw the Thing");
				for(Object obj : drawObjects){
					if(drawPoints.get(drawObjects.indexOf(obj)).y > y){
						y += heightPadding;
						if(obj instanceof BufferedImage){
							g.drawImage((BufferedImage)obj, drawPoints.get(drawObjects.indexOf(obj)).x, drawPoints.get(drawObjects.indexOf(obj)).y, null);
							y += ((BufferedImage)obj).getHeight();
							if(y > yEnd){
								break outer;
							}
						}else if(obj instanceof String){
							g.drawString((String)obj, drawPoints.get(drawObjects.indexOf(obj)).x, drawPoints.get(drawObjects.indexOf(obj)).y);
							// System.out.println(drawPoints.get(drawObjects.indexOf(obj)));
							y += stringHeight;
							if(y > yEnd){
								// System.out.println("last thing: " + (String)obj);
								break outer;
							}
						}
					}
				}
				break outer;
			}else{
				break outer;
			}
		}
	}
	
	public void renderBar(Graphics g){
		//g.setColor(Handler.MANA_BLUE);
		int scrollX = shownBox.x + shownBox.width - barWidth - 2;
		// barHeight = shownBox.height - 4;
		//g.fillOval(scrollX, shownBox.y - 15, barWidth, barHeight);
		g.setColor(Handler.BARELY_GRAY);
		buttonUp.y = shownBox.y + 10;
		buttonDown.y = shownBox.y + 10 + Assets.scrollButtonDown.getHeight();
		buttonUp.x = scrollX;
		buttonDown.x = scrollX;
		g.fillRect(scrollX, buttonUp.y, Assets.scrollButtonUp.getWidth(), Assets.scrollButtonUp.getHeight());
		g.fillRect(scrollX, buttonDown.y, Assets.scrollButtonDown.getWidth(), Assets.scrollButtonDown.getHeight());
		g.drawImage(Assets.scrollButtonUp, scrollX, buttonUp.y, null);
		g.drawImage(Assets.scrollButtonDown, scrollX, buttonDown.y, null);
	}
	
	public void tick(MouseInput mouseInput){
		//TODO make it do things when you click the buttons	
		LinkedQueue clicks = mouseInput.getLeftClicks();

	}

	public int getFontHeight(){
		return fontHeight;
	}
	
	public Rectangle getButtonUp(){
		return buttonUp;
	}
	
	public Rectangle getButtonDown(){
		return buttonDown;
	}
	
	public ArrayList<Object> getDrawObjects(){
		return drawObjects;
	}
	
	public ArrayList<Point> getDrawPoints(){
		return drawPoints;
	}
}
