package board.space;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

public class Corner extends Space {
	

	private static final Color 
		BORDER_COLOR = Space.BORDER_COLOR_DEFAULT,
		FILL_COLOR = Space.FILL_COLOR_DEFAULT;
	
	public Corner(int id) {
		super(id);
	}
	
	@Override
	public Type getType() {
		return Space.Type.CORNER;
	}

	@Override
	public void paint(Graphics2D g2d) {
		Color prevColor = g2d.getColor();
		Stroke prevStroke = g2d.getStroke();

		g2d.setColor(Corner.FILL_COLOR);
		g2d.fill(this.getShape());

		g2d.setColor(Corner.BORDER_COLOR);
		switch(this.borderStatus) {
		case BOLD: 
			g2d.setStroke(Space.BORDER_BOLD);
			g2d.draw(this.getShape());
			break;
		case NORMAL: 
			g2d.setStroke(Space.BORDER_NORMAL); 
			g2d.draw(this.getShape());
			break;
		case NONE:
			break;
		}

		double nr = this.getShape().getWidth()/2*1.6;
		Ellipse2D.Double ring = new Ellipse2D.Double(
				this.getShape().getCenterX() - nr,
				this.getShape().getCenterY() - nr,
				2*nr,2*nr
				);
		g2d.setStroke(Space.BORDER_NORMAL);
		g2d.draw(ring);
		g2d.setColor(prevColor);
		g2d.setStroke(prevStroke);
	}

}
