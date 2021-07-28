package board.space;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import board.Token;

abstract public class Space {
	public static enum Type {
		HOME, BASE, CENTER, LOOP, CORNER
	}
	public static enum BorderStatus {
		BOLD, NORMAL, NONE
	}
	
	public static final Stroke
		BORDER_BOLD = new BasicStroke(6),
		BORDER_NORMAL = new BasicStroke(3);
	
	public static final Color 
		BORDER_COLOR_DEFAULT = Color.BLACK,
		FILL_COLOR_DEFAULT = new Color(117,117,117,100);
	
	
	private static double diameter = 50;
	public static Color COLOR = new Color(245, 245, 220);
	
	private Ellipse2D.Double circle = null;
	private Token token = null;
	private int id;
	private boolean
		hoverHighight = false,		
		possibleHighlight = false;	
		
	protected BorderStatus borderStatus = Space.BorderStatus.NORMAL;
	
	public Space(int id) {
		this.id = id;
		this.setLocation(0,0);
	}
	
	public void setLocation(double x, double y) {
		this.circle = new Ellipse2D.Double(
				x-Space.diameter/2,
				y-Space.diameter/2,
				Space.diameter, 
				Space.diameter);
	}
	
	abstract public Type getType();
	abstract public void paint(Graphics2D g2d);
	
	
	public static double getDiameter() { return Space.diameter; }
	public static void setDiameter(double d) { Space.diameter = d; }
	
	public String getLabel() { 
		return "" + this.getType().name().charAt(0) + this.id;
	}
	public void setFocus(boolean b) {
		if(b)
			this.borderStatus = Space.BorderStatus.BOLD;
		else
			this.borderStatus = Space.BorderStatus.NORMAL;
	}
	
	@Override
	public String toString() { return this.getLabel(); }
	
	public Ellipse2D.Double getHoverShape() {
		double x, y, d;
		
		if(circle != null) {
			d = Space.diameter / 4;
			x = circle.getCenterX() - d/2;
			y = circle.getCenterY() - d/2;
			
			return new Ellipse2D.Double(x, y, d, d);
		} else {
			return new Ellipse2D.Double();
		}
	}
	public Ellipse2D.Double getShape() { return this.circle; }	
	public Token getToken() { return this.token; }
	public int getId() { return this.id; }
	public boolean hasHoverHighlight() { return this.hoverHighight; }
	public boolean hasPossibleHighlight() { return this.possibleHighlight; }

	public void setToken(Token m) { this.token = m; }
	public void setHoverHighlight(Boolean b) { this.hoverHighight = b; }
	public void setPossibleHighlight(Boolean b) { this.possibleHighlight = b; }
	

	public void clearToken() {
		this.token = null;
	}
	public boolean hasToken() {
		return this.token != null;
	}
}
