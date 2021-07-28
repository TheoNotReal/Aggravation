package board;
//The Game Board, which contains all of the game's components.

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import app.Aggravation;
import app.Style;
import app.Utility.Log;
import board.space.Base;
import board.space.Center;
import board.space.Corner;
import board.space.Home;
import board.space.Loop;
import board.space.Space;
import board.space.Start;
import game.Game;
import game.Player;


@SuppressWarnings("serial")
public class Board extends JPanel implements ComponentListener, MouseMotionListener, MouseInputListener {
	
	private static final int MIN_SIZE = 500;	

	private static final int ZONE_OFFSET = 14;
	private static final int START_OFFSET = 9;	
	private static final int HOME_OFFSET = 7;	

	Space[] loop = new Space[84];	
	Space center;					
	Space[][] home = new Space[Aggravation.MAX_PLAYERS][Aggravation.TOKENS_PER_PLAYER];	
	Space[][] base = new Space[Aggravation.MAX_PLAYERS][Aggravation.TOKENS_PER_PLAYER];	

	Space foundSpace, hoverSpace;

	Rectangle2D bg;
	double size;
	
	public Board() {
		super();
		this.setMinimumSize(new Dimension(Board.MIN_SIZE, Board.MIN_SIZE));
		
		this.setBackground(Style.COLOR_BACKGROUND);
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	
	
	public boolean isCorner(int pos) {
		return (pos % ZONE_OFFSET) == 0;
	}
	
	
	public void updateGeometry() {
		Log.v("BOARD", "Updating geometry...");
		final double ZONE_RAD_OFFSET = 2*Math.PI / Aggravation.MAX_PLAYERS;
		final int SPACE_OFFSET_UNITS = 2;
		final int CENTER_OFFSET_UNITS = SPACE_OFFSET_UNITS*4;
		final int BASE_OFFSET_UNITS = SPACE_OFFSET_UNITS*3;
		
		Rectangle2D b = (Rectangle2D) this.getBounds();
		Point2D origin = new Point2D.Double(b.getWidth()/2, b.getHeight()/2);
		
		Point2D[][] zGrid = new Point2D[Aggravation.TOKENS_PER_PLAYER+1][5];	//grid of points to make up zone
		
		double u; 
		double rad; 		
		double x, y, r, d;		
		
		
		if(b.getWidth() < b.getHeight())
			size = b.getWidth();
		else
			size = b.getHeight();
		if(size < Board.MIN_SIZE)
			size = Board.MIN_SIZE;
		bg = new Rectangle2D.Double(origin.getX()-size/2, origin.getY()-size/2, size, size);
		
		
		
		u = size/45;
		
		
		
		for(int i = 0; i < zGrid.length; ++i) {
			r = (CENTER_OFFSET_UNITS + i*SPACE_OFFSET_UNITS+1)*u;
			for(int j = 0; j < zGrid[i].length; ++j) {
				d = SPACE_OFFSET_UNITS*(j-2)*u; 
				zGrid[i][j] = new Point2D.Double(origin.getX()+d, origin.getY()-r);
				zGrid[i][j] = AffineTransform.getRotateInstance(-ZONE_RAD_OFFSET/2, origin.getX(), origin.getY()).transform(zGrid[i][j], null);
			}
		}
		
		
		Space.setDiameter(u*1.3);
		center.setLocation(origin.getX(),origin.getY());
		for(int zone = 0, pos, curr; zone < Aggravation.MAX_PLAYERS; ++zone) {
			pos = zone*ZONE_OFFSET;
			rad = Math.PI/2 - zone*ZONE_RAD_OFFSET;
			
			
			r = CENTER_OFFSET_UNITS*u;
			x = r*Math.cos(rad);
			y = r*Math.sin(rad);
			loop[pos].setLocation(origin.getX()+x,origin.getY()-y);
			
			
			for(int i = 0; i < Aggravation.TOKENS_PER_PLAYER; ++i) {
				r = CENTER_OFFSET_UNITS*u + (BASE_OFFSET_UNITS+SPACE_OFFSET_UNITS*i)*u;
				x = r*Math.cos(rad);
				y = r*Math.sin(rad);
				base[zone][i].setLocation(origin.getX()+x,origin.getY()-y);
			}
			
			
			rad -= ZONE_RAD_OFFSET/2;
			for(int i = 0; i < zGrid.length; ++i) {
				for(int j = 0; j < zGrid[i].length; ++j) {
					
					zGrid[i][j] = AffineTransform.getRotateInstance(ZONE_RAD_OFFSET, origin.getX(), origin.getY()).transform(zGrid[i][j], null);
					
				
					switch(j) {
					case 0:
						curr = pos+1+i;
						loop[curr].setLocation(zGrid[i][j].getX(),zGrid[i][j].getY());
						break;
					case 1:
						if(i == zGrid.length-1) {
							curr = pos+1 + zGrid.length + j - 1;
							loop[curr].setLocation(zGrid[i][j].getX(),zGrid[i][j].getY());
						}
							
						break;
					case 2:
						if(i == zGrid.length-1) {
							curr = pos+1 + zGrid.length + j - 1;
							loop[curr].setLocation(zGrid[i][j].getX(),zGrid[i][j].getY());
						} else {
							curr = home[zone].length - i - 1;
							home[zone][curr].setLocation(zGrid[i][j].getX(),zGrid[i][j].getY());
						}
						break;
					case 3:
						if(i == zGrid.length-1) {
							curr = pos+1 + zGrid.length + j - 1;
							loop[curr].setLocation(zGrid[i][j].getX(),zGrid[i][j].getY());
						}
						break;
					case 4:
						curr = pos+1 + zGrid.length*2 + zGrid[i].length-3 - i;
						loop[curr].setLocation(zGrid[i][j].getX(),zGrid[i][j].getY());
						break;
					}
				}
			}
			
		}
		Log.v("BOARD", "Geometry update complete.");
		repaint();
	}
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		int pos = 0;
		Color c;
		
		g2d.setRenderingHints(Style.RENDERING_HINTS);
		g2d.setFont(Style.FONT_SMALL);
		
		
		g2d.setColor(Style.COLOR_BOARD_BACKGROUND);
		g2d.fill(bg);
		
	
		g2d.setColor(Space.COLOR);
		
		paintSpace(g2d,center);
		for(Space s : loop)
			paintSpace(g2d,s);
		for(Space[] b : base)
			for(Space s : b)
				if(s != null)
					paintSpace(g2d,s);
		for(Space[] h : home)
			for(Space s : h)
				paintSpace(g2d,s);
		
		
		
	}
	
	private void paintSpace(Graphics2D g2d, Space s) {
		Color c = g2d.getColor();
		Ellipse2D circle;
		
		if(s != null) {
			circle = s.getShape();
			s.paint(g2d);
			if(Style.OPTION_VIEW_SPACE_NUMBERS)
				g2d.drawString(s.getLabel(), (float)circle.getX(), (float)circle.getY());
			if(s.hasToken()) {
				
				c = g2d.getColor();
				g2d.setColor(s.getToken().getColor());
				g2d.fill(circle);
				g2d.setColor(c);
				
				if((Game.getCurrentInstance().getStatus() == Game.Status.WAITING_FOR_Token_SELECTION ||
						Game.getCurrentInstance().getStatus() == Game.Status.WAITING_FOR_MOVE_CHOICE) &&
						this.hoverSpace == s && 
						s.hasHoverHighlight() &&
						s.getToken().getOwner() == Game.getCurrentInstance().getCurrentPlayer()) {
					
					g2d.setColor(Style.COLOR_SPACE_HIGHLIGHT);
					g2d.fill(s.getHoverShape());
				}
			} else if(Game.getCurrentInstance().getStatus() == Game.Status.WAITING_FOR_MOVE_CHOICE &&
					this.hoverSpace == s && 
					s.hasHoverHighlight()) {
				
				g2d.setColor(Style.COLOR_SPACE_HIGHLIGHT);
				g2d.fill(s.getHoverShape());
			}
			
		}
		
		g2d.setColor(c);
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentResized(ComponentEvent e) {
		Log.v("BOARD", "Resized to "+ this.getWidth() + " x " + this.getHeight());
		updateGeometry();
	}
	
	
	
	public void init() {
		Player p;

		center = new Center();
		for(int i = 0; i < loop.length; ++i)
			if(i % ZONE_OFFSET == 0)
				loop[i] = new Corner(i/ZONE_OFFSET);
			else if (i % ZONE_OFFSET == START_OFFSET) {
				int zone = i/ZONE_OFFSET;
				loop[i] = new Start(zone,i,Game.getCurrentInstance().getPlayer(zone));
			} else 
				loop[i] = new Loop(i);
		for(int zone = 0; zone < base.length; ++zone)
			for(int i = 0; i < base[zone].length; ++i) {
				p = Game.getCurrentInstance().getPlayer(zone);
				if(p == null)
					p = Player.NONE;
				base[zone][i] = new Base(zone, i, p);
			}
				
		for(int zone = 0; zone < home.length; ++zone)
			for(int i = 0; i < home[zone].length; ++i) {
				p = Game.getCurrentInstance().getPlayer(zone);
				if(p == null)
					p = Player.NONE;
				home[zone][i] = new Home(zone, i, p);
			}
		
		
		this.updateGeometry(); 
		
		
		for(int zone = 0; zone < base.length; ++zone) {
			try {
				p = Game.getCurrentInstance().getPlayer(zone);
				Log.v("BOARD","Player \'"+p.getName()+"\' set for zone "+zone);
				for(Space s : base[zone])
					s.setToken(new Token(p, p.getColor()));
			} catch(NullPointerException e) {
				
				Log.v("BOARD","Player not set for zone "+zone);
			}
		}
	}
	
	private boolean boardContains(Point p) {
		if(bg != null) {
			return bg.contains(p);
		} else {
			return false;
		}
	}
	

	private boolean findSpace(Point p) {
		
		if(center.getShape().contains(p)) {
			this.foundSpace = center;
			return true;
		}
		
		for(Space[] zone : base)
			for(Space s : zone)
				if(s.getShape().contains(p)) {
					this.foundSpace = s;
					return true;
				}
		for(Space[] zone : home)
			for(Space s : zone)
				if(s.getShape().contains(p)) {
					this.foundSpace = s;
					return true;
				}
		for(Space s : loop)
			if(s.getShape().contains(p)) {
				this.foundSpace = s;
				return true;
			}
		return false;
	}
	
	private Space getFoundSpace() { return this.foundSpace; }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		final String KEY = "BOARD-Mouse-Click";
		Point p = e.getPoint();
		if(this.boardContains(p)) {
			Log.v(KEY, "Clicked on board");
			if(this.findSpace(p)) {
				Log.d(KEY, "Clicked on a space " + this.getFoundSpace().getLabel());
				Game.getCurrentInstance().onSpaceClicked(this.foundSpace);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
	
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		final String KEY = "BOARD-Mouse-Move";
		Point p = e.getPoint();
		
		if(this.boardContains(p) && this.findSpace(p)) {
			if(this.foundSpace != this.hoverSpace) {
				Log.v(KEY, "Entered space " + this.foundSpace.getLabel());
				this.hoverSpace = this.foundSpace;
				this.hoverSpace.setHoverHighlight(true);
				repaint();
			} 
		} else if(this.hoverSpace != null) {
			Log.v(KEY, "Exited space " + this.hoverSpace.getLabel());
			this.hoverSpace.setHoverHighlight(false);
			this.hoverSpace = null;
			repaint();
		}
		
	}
	
	//Space finders
	public Space getCenter() { return center; }
	public Space[] getCorners() {
		Space corners[] = new Space[Aggravation.MAX_PLAYERS];
		
		for(int i = 0; i < Aggravation.MAX_PLAYERS; ++i)
			corners[i] = loop[ZONE_OFFSET*i];
		
		return corners;
	}
	public Space[] getPlayerHomes(Player p) {
		return home[p.getZone()];
	}
	public Space[] getPlayerBases(Player p) {
		return base[p.getZone()];
	}
	public Space getPlayerStart(Player p) {
		return loop[p.getZone()*ZONE_OFFSET + START_OFFSET];
	}
	public Space getPlayerHomeEntrance(Player p) {
		return loop[p.getZone()*ZONE_OFFSET + HOME_OFFSET];
	}
	public Space[] getPlayerTokenSpaces(Player p) {
		Space[] ms = new Space[Aggravation.TOKENS_PER_PLAYER];
		int zone = p.getZone();
		int curr = 0;
		Space s;
	
		s = center;
		if(curr < ms.length && s.hasToken() && s.getToken().getOwner() == p)
			ms[curr++] = s;
		for(int i = 0; i < base[zone].length; ++i){
			s = base[zone][i];
			if(s.hasToken() && s.getToken().getOwner() == p)
				ms[curr++] = s;
		}
		for(int i = 0; curr < ms.length && i < home[zone].length; ++i){
			s = home[zone][i];
			if(s.hasToken() && s.getToken().getOwner() == p)
				ms[curr++] = s;
		}
		for(int i = 0; curr < ms.length && i < loop.length; ++i){
			s = loop[i];
			if(s.hasToken() && s.getToken().getOwner() == p)
				ms[curr++] = s;
		}
		
		return ms;
	}
	
	

	public Space[] getNextSpaces(Space src) {
		Space[] adj = new Space[0];
		int zone;
		
		switch(src.getType()) {
		case BASE:
			zone = ((Base) src).getZone();
			adj = new Space[1]; 
			adj[0] = loop[zone % ZONE_OFFSET + START_OFFSET];
			break;
		case HOME:
			zone = ((Home) src).getZone();
			if(src.getId()+1 < home.length) {
				adj = new Space[1];
				adj[0] = home[zone][src.getId()+1];
			}
			break;
		case CENTER:
			adj = getCorners();
			break;
		case LOOP:
			if(src.getId() % ZONE_OFFSET == (HOME_OFFSET - 1)) {
				adj = new Space[2];
				adj[1] = home[src.getId()/ZONE_OFFSET][0];
			} else {
				adj = new Space[1];
			}
			adj[0] = loop[src.getId()+1];
			break;
		case CORNER:
			adj = new Space[3];
			adj[0] = center;										
			adj[1] = loop[src.getId()*ZONE_OFFSET+1];							
			adj[2] = loop[(src.getId()*ZONE_OFFSET + ZONE_OFFSET)%loop.length]; 
			break;
		default: break;
		}
		
		return adj;
	}
	
	
}