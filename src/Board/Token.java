package board;
//The Token travels around the board. It's possible to change the color.

import java.awt.Color;
import game.Player;

@SuppressWarnings("serial")
public class Token {
	private Player owner;
	private Color tokenColor;
	
	public Token(Player owner, Color tokenColor) {
		this.owner = owner;
		this.tokenColor = tokenColor;
	}
	
	public Player getOwner() { return this.owner; }
	public Color getColor() { return this.tokenColor; }
}
