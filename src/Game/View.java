package game;
//display game-related objects

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import app.Style;
import app.Utility.Log;
import board.Board;
import tools.Tool;

public class View extends JPanel {
	public static final double BOARD_WEIGHT_Y = .9;
	public static final Dimension
		MINIMUM_SIZE = new Dimension(
				700,
				(int)(700/View.BOARD_WEIGHT_Y)
				),
		PREFERRED_SIZE = new Dimension(
				800,
				(int)(800/View.BOARD_WEIGHT_Y)
				);
	
	
	private Board board = new Board();
	private Tool tool = new Tool();
	
	public View() {
		super(new BorderLayout());
		
		this.setMinimumSize(View.MINIMUM_SIZE);
		this.setPreferredSize(View.PREFERRED_SIZE);
		this.setBackground(Style.COLOR_BACKGROUND);
		
		this.add(board, BorderLayout.CENTER);
		this.add(tool, BorderLayout.SOUTH);
		init();
	}

	public void refresh() {
		this.board.updateGeometry();
		this.tool.updateContent();
		repaint();
	}

	public void init() {
		Log.v("GAME DISPLAY","Initializing...");
		this.board.init();
		this.tool.init();
		Log.v("GAME DISPLAY","Initialization complete.");
	}
	
	public Board getBoard() { return this.board; }
	public Tool getTool() { return this.tool; }
}