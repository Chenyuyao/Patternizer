/*
* CS 349 Java Code Examples
*
* ShapeDemo    Demo of MyShape class: draw shapes using mouse.
*
*/
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.vecmath.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

// create the window and run the demo
public class Patternizer extends JPanel implements MouseInputListener {

	MyShape shape;
	MyCircle solid,left;
	ArrayList<MyShape> shapes;
	ArrayList<ArrayList<MyShape> > clonedShapes;
	int selectedIndex;
	boolean drawing;
	boolean started;
	boolean invalidDraw;
	double resizeX, resizeY;

	Patternizer() {
        	// add listeners
        	addMouseListener(this);
        	addMouseMotionListener(this);  
    	}

    	public static void main(String[] args) {
        	// create the window         
        	Patternizer canvas = new Patternizer();
        	JFrame f = new JFrame("Patternizer"); // jframe is the app window
        	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	f.setSize(400, 400); // window size
        	f.setContentPane(canvas); // add canvas to jframe
        	f.setVisible(true); // show the window
    	}
    	// custom graphics drawing 
    	public void paintComponent(Graphics g) {
        	super.paintComponent(g);
        	Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                            RenderingHints.VALUE_ANTIALIAS_ON);
		resizeX = ( getWidth() - 400 )/2;
		resizeY = ( getHeight()- 400 )/2;
		AffineTransform T = AffineTransform.getTranslateInstance(resizeX, resizeY);

		if (started == false) {
			solid = new MyCircle(30,200,200,true);
			left = new MyCircle(8,30,30,true);
			left.setColour(Color.BLACK);
			started = true;
		}
		g2.transform(T);		
		if (shape != null) shape.paint(g2);
        	if (shapes != null) {
			for (int i = 0; i < shapes.size(); i++) {
            			shapes.get(i).paint(g2);
			}
		}
		if (clonedShapes != null) {
			for (int i = 0; i < clonedShapes.size(); i++) {
				ArrayList<MyShape> shapeList = clonedShapes.get(i);
				if (shapeList != null) {
					for (int j = 0; j < shapeList.size(); j++) {
						shapeList.get(j).paint(g2);
					}
				}
			}
		}
		solid.paint(g2);
		left.paint(g2);		
    	}

    	@Override
    	public void mouseClicked(MouseEvent arg0) {
		double mx = arg0.getX() - resizeX;
		double my = arg0.getY() - resizeY;
			
		if (left.hittest(mx,my)) {
			left.resize();
		}
		else if (arg0.getClickCount() == 2) {
			shape = null;
			shapes.clear();
			clonedShapes.clear();
			selectedIndex =0;
			drawing = false;
			started = false;		
		}
		else {
			if (solid.hittest(mx,my)) {
				solid.changeColour();
				for (int i = 0; i < shapes.size(); i++) {
					shapes.get(i).setSelected(false);
				}
			}
			else {		

				boolean alreadySet = true;
				for (int i = 0; i < shapes.size(); i++) {
					boolean res = shapes.get(i).hittest(mx,my) && alreadySet;
            				shapes.get(i).setSelected(res);
					if ( res ) {
						selectedIndex = i;
						shape = shapes.get(i);
						alreadySet = false;
						if (arg0.getButton() == MouseEvent.BUTTON3) {      
							shape.setColour(solid.getColour());
							if (clonedShapes.get(i) != null) {
								for (int j =0; j < clonedShapes.get(i).size(); j++) {									clonedShapes.get(i).get(j).setColour(solid.getColour());
								}
							}
						}
					}
				}
				if (alreadySet) {		
		  			if (clonedShapes != null) {
		   				for (int i = 0; i < clonedShapes.size(); i++) {
		        				ArrayList<MyShape> shapeList = clonedShapes.get(i);
							if (shapeList != null) {
			  					for (int j = 0; j < shapeList.size(); j++) {
			  						boolean res = shapeList.get(j).hittest(mx,my) && alreadySet;
									shapes.get(i).setSelected(res);
									if ( res ) {
										selectedIndex = i;
										shape = shapes.get(i);
										alreadySet = false;
										break;
									}
			  					}
							}
		    				}
		  			}
				}
			}
		}
		repaint();
		
    	}

    	@Override
    	public void mouseEntered(MouseEvent arg0) {
    	}

    	@Override
    	public void mouseExited(MouseEvent arg0) {
    	}

    	@Override
    	public void mousePressed(MouseEvent arg0) {
		double mx = arg0.getX() - resizeX;
		double my = arg0.getY() - resizeY;
		if (shape!=null && shape.isSelected && shape.hittest(mx,my)) {
			shape.transforming = true;
			shape.setOriginal(mx,my);
			repaint();
		}

		else {
        		shape = new MyShape();
        		shape.setIsClosed(false);
        		shape.setIsFilled(false);
        		shape.setColour(solid.getColour());
			if (solid.hittest(mx,my) == false) {
				//shape.setColour(Color.WHITE);
				invalidDraw = true;
			}
			shape.setStrokeThickness(3.0f * left.getSize());
        		repaint();
		}
    	}	

    	@Override
    	public void mouseReleased(MouseEvent arg0) {
		if (shape.transforming) {
			shape.transforming = false;
		}
		else if (drawing && invalidDraw == false) {
			if (shapes == null)
    				shapes = new ArrayList<MyShape>();
			if (clonedShapes == null)
				clonedShapes = new ArrayList<ArrayList<MyShape> >();
			shapes.add(shape);
			clonedShapes.add(null);
			drawing = false;

		}
		else {
			if (shapes == null)
    				shapes = new ArrayList<MyShape>();
			if (clonedShapes == null)
				clonedShapes = new ArrayList<ArrayList<MyShape> >();		
		}
		invalidDraw = false;
    	}

    	@Override
    	public void mouseDragged(MouseEvent arg0) {
		double mx = arg0.getX() - resizeX;
		double my = arg0.getY() - resizeY;
		if (shape.transforming && invalidDraw == false) {
			shape.trans(mx, my,200,200);
			repaint();
			double theta = shape.getRotation();
			theta = (theta < 0 )?-theta:theta;
			if (theta > Math.toRadians(5) ) {
				int n = (int) (2 * Math.PI / theta);
				ArrayList<MyShape> shapeList = new ArrayList<MyShape>(n);
				for (int j =1; j < n; j++) {
					MyShape s = new MyShape();
					s.setIsClosed(false);
        				s.setIsFilled(false);
					s.setStrokeThickness(shape.getStrokeThickness());
        				s.setColour(shape.getColour());
					s.setPoints(shape.exportPoints(theta, j));
					shapeList.add(s);
				}
				clonedShapes.remove(selectedIndex);
				clonedShapes.add(selectedIndex,shapeList);
			}
		}
		else if (invalidDraw == true ) {
			shape.addPoint(mx, my);
			drawing = true;
		}
		else {
        		shape.addPoint(mx, my);
			drawing = true;
        		repaint();
		}    
   	}

    	@Override
    	public void mouseMoved(MouseEvent arg0) {
    	}
}

