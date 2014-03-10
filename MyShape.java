
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.*;


// simple shape class
class MyShape {

    	// shape model
    	ArrayList<Point2d> points;
    	Boolean isFilled = false; // shape is polyline or polygon
    	Boolean isClosed = false; // polygon is filled or not
    	Color colour = Color.BLACK;
	float strokeThickness = 3.0f;
    	public Color getColour() {
		return colour;
	}
	public void setColour(Color colour) {
		this.colour = colour;
	}
    	public float getStrokeThickness() {
		return strokeThickness;
	}
	public void setStrokeThickness(float strokeThickness) {
		this.strokeThickness = strokeThickness;
	}
	public Boolean getIsFilled() {
		return isFilled;
	}
	public void setIsFilled(Boolean isFilled) {
		this.isFilled = isFilled;
	}
	public Boolean getIsClosed() {
		return isClosed;
	}
	public void setIsClosed(Boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	// for selection
	boolean isSelected;
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	// for drawing
    	Boolean hasChanged = false; // dirty bit if shape geometry changed
   	int[] x_points, y_points;

	public float rotation = 0;
	public double scale = 1.0;

    	// replace all points with array
   	public void setPoints(double[][] pts) {
        	points = new ArrayList<Point2d>();
        	for (double[] p : pts) {
            		points.add(new Point2d(p[0],p[1]));
        	}
        	hasChanged = true;
    	}
    
    	// add a point to end of shape
    	public void addPoint(double x, double y) {
    		if (points == null)
    			points = new ArrayList<Point2d>();
    		points.add(new Point2d(x,y));
    		hasChanged = true;
    	}	

    
    	// paint the shape
    	public void paint(Graphics2D g2) {

	        //update the shape in java Path2D object if it changed
	        if (hasChanged) {
	        	x_points = new int[points.size()];
	        	y_points = new int[points.size()];
	        	for (int i=0; i < points.size(); i++) {
	                	x_points[i] = (int)points.get(i).x;
	                	y_points[i] = (int)points.get(i).y;
	            	}
	            	hasChanged = false;
	        }

	        //don't draw if path2D is empty (not shape)
	        if (x_points != null) {
        	
	        	// special draw for selection
	        	if (isSelected) {
	        		g2.setColor(Color.YELLOW);
	        		g2.setStroke(new BasicStroke(strokeThickness * 4));
	            		if (isClosed)
	                    		g2.drawPolygon(x_points, y_points, points.size());
	                	else
	                    		g2.drawPolyline(x_points, y_points, points.size());
	        	}
        	
	        	g2.setColor(colour);

	            	// call right drawing function
            		if (isFilled) {
            	    		g2.fillPolygon(x_points, y_points, points.size());
            		}
            		else {
            			g2.setStroke(new BasicStroke(strokeThickness)); 
            			if (isClosed)
            			        g2.drawPolygon(x_points, y_points, points.size());
            			else
            	        		g2.drawPolyline(x_points, y_points, points.size());
	        	}
		}
    	}
    
    	// find closest point
    	static Point2d closestPoint(Point2d M, Point2d P1, Point2d P2)
    	{
		// TODO: implement

    		return new Point2d();
    	}
    
    	// return perpendicular vector
    	static public Vector2d perp(Vector2d a)
    	{
    		return new Vector2d(-a.y, a.x);
    	}
    
    	// line-line intersection
    	// return (NaN,NaN) if not intersection, otherwise returns intersecting point
    	static Point2d lineLineIntersection(Point2d P0, Point2d P1, Point2d Q0, Point2d Q1)
    	{
    	
    		// TODO: implement
	
    		return new Point2d();
    	}
    

	//for rotate and scale
	double originX, originY;
	public boolean transforming = false;
	public void setOriginal(double x, double y) {
		originX = x;
		originY = y;
	}

	public void translate( double x, double y) {
		AffineTransform T = AffineTransform.getTranslateInstance(x, y);
		double[][] pts;	
		pts = new double[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			Point2d p = transform(T,points.get(i));
			pts[i][0] = p.x;
			pts[i][1] = p.y;
		}

	}

	public void trans(double x, double y, double x0, double y0) {

		Point2d originalP = new Point2d(originX,originY);
		Point2d O 	  = new Point2d(x0,y0);
		Point2d nowP      = new Point2d(x,y);

		double d1 = O.distance(originalP);
		double d2 = O.distance(nowP);

		Vector2d v1 = new Vector2d(originX - x0, originY - y0);
		Vector2d v2 = new Vector2d(x - x0, y - y0);
		double theta = v1.angle(v2);
		double dir = (x-x0)*(originY-y0) - (y-y0)*(originX-x0);
		theta = (dir < 0)?theta:-theta;
		scale = d2/d1;

		double[][] pts;	
		pts = new double[points.size()][2];
		AffineTransform Ts= AffineTransform.getTranslateInstance(-x0,-y0);
		AffineTransform S = AffineTransform.getScaleInstance(scale,scale);
		AffineTransform R = AffineTransform.getRotateInstance(theta);
		AffineTransform Te= AffineTransform.getTranslateInstance(x0,y0);
		for (int i = 0; i < points.size(); i++) {
			Point2d p = transform(Ts,points.get(i));
			p = transform(S,p);
			p = transform(R,p);
			p = transform(Te,p);
			pts[i][0] = p.x;
			pts[i][1] = p.y;
		}
		originX = x;
		originY = y;
		scale = 1.0;
		rotation += theta;
		setPoints(pts);
	}


    	// affine transform helper
    	// return P_prime = T * P    
    	Point2d transform( AffineTransform T, Point2d P) {
    		Point2D.Double p = new Point2D.Double(P.x, P.y);
    		Point2D.Double q = new Point2D.Double();
    		T.transform(p, q);
    		return new Point2d(q.x, q.y);   	
    	}
	
    
    	// hit test with this shape
	double minSatisfy = 10.0;
    	public boolean hittest(double x, double y)
    	{   
    		if (points != null) {
			for (int i=0; i < points.size(); i++) {
				Point2D.Double p = new Point2D.Double(x, y);
				double d = p.distance(points.get(i).x, points.get(i).y);
				//System.out.format("distance = %d \n", (int)d);
				if (d < minSatisfy) return true;
			}	
    		}
    		//System.out.format("Null \n"); 
    		return false;
    	}

	//for clone shapes
	double[][] exportPoints(double angle, int n) {
		double theta = angle * n;
		AffineTransform Ts= AffineTransform.getTranslateInstance(-200,-200);
		AffineTransform R = AffineTransform.getRotateInstance(theta);
		AffineTransform Te= AffineTransform.getTranslateInstance(200,200);
		double[][] pts;	
		pts = new double[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			Point2d p = transform(Ts,points.get(i));
			p = transform(R,p);
			p = transform(Te,p);
			pts[i][0] = p.x;
			pts[i][1] = p.y;
		}
		return pts;
	}

	double getRotation() {
		return rotation;
	}
}
