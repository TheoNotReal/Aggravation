package board.space;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import app.Utility.ColorManager;
import game.Player;

public class Home extends PlayerSpace {
	
	public Home(int zone, int id, Player p) {
		super(zone,id,p);
	}
	
	@Override
	public Type getType() {
		return Space.Type.HOME;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		Color prevColor = g2d.getColor();
		Stroke prevStroke = g2d.getStroke();

		g2d.setColor(ColorManager.fade(this.owner.getColor()));
		g2d.fill(this.getBox());
	
		g2d.setColor(this.fillColor);
		g2d.fill(this.getShape());

		g2d.setColor(this.borderColor);
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