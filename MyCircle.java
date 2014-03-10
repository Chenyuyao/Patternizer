import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.*;

class MyCircle {
	int r,ro,x,y,colourCount,size;
	boolean isFilled = false;
	Color colour = Color.GREEN;
	public MyCircle(int r0, int x0, int y0, boolean f) {
		r = r0;
		ro= r0;
		x = x0;
		y = y0;
		isFilled = f;
		colourCount = 0;
		size = 1;
	}

	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public void setIsFilled(Boolean isFilled) {
		this.isFilled = isFilled;
	}

	public void paint(Graphics2D g2) {
		if (isFilled) {
			g2.setColor(colour);
			g2.fillOval(x - r/2, y - r/2, r, r);
		}
		else {
			g2.setColor(colour);
			g2.drawOval(x - r/2, y - r/2, r, r);
		}
	}

	public boolean hittest(double x1, double y1) {
		Point2D.Double p = new Point2D.Double(x, y);
		double d = p.distance(x1,y1);
		return (r > d)?true:false;
	}

	public void changeColour() {
		colourCount++;
		colourCount = colourCount % 6;
		switch (colourCount) {
			case 0:
				colour = Color.GREEN;
				break;
			case 1:
				colour = Color.ORANGE;
				break;
			case 2:
				colour = Color.RED;
				break;
			case 3:
				colour = Color.BLUE;
				break;
			case 4:
				colour = Color.PINK;
				break;
			case 5:
				colour = Color.GRAY;
				break;
		}
	}
	public void resize() {
		size = size % 3;
		size++;
		r = ro * size;
	}
	public int getSize() {
		return size;
	}
}
