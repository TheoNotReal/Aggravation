package board.space;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Loop extends Space {
	private static final Color 
		BORDER_COLOR = Space.BORDER_COLOR_DEFAULT,
		FILL_COLOR = Space.FILL_COLOR_DEFAULT;
	
	public Loop(int id) {
		super(id);
	}
	
	@Override
	public Type getType() {
		return Space.Type.LOOP;
	}

	@Override
	public void paint(Graphics2D g2d) {
		Color prevColor = g2d.getColor();
		Stroke prevStroke = g2d.getStroke();
		
		
		g2d.setColor(Loop.FILL_COLOR);
		g2d.fill(this.getShape());
		
		g2d.setColor(Loop.BORDER_COLOR);
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

		g2d.setColor(prevColor);
		g2d.setStroke(prevStroke);
	}

}
