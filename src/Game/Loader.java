package game;
//Loads the game configuration

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import app.Aggravation;
import app.Style;

public class Loader extends JPanel {
	
	Thread load;
	JLabel status;
	Aggravation parent;
	
	public Loader(Aggravation p) {
		super(new BorderLayout());
		
		parent = p;
		
		setOpaque(false);
		
		status = new JLabel("Loading...");
		status.setForeground(Style.COLOR_FONT);
		status.setFont(Style.FONT_TITLE);
		status.setHorizontalAlignment(JLabel.CENTER);
		status.setOpaque(false);
		this.add(status, BorderLayout.CENTER);
		
		load = new Thread() {
			@Override
			public void run() {
				Game.load();
				Chooser pc = new Chooser(parent);
				parent.startChooser(pc);
				status.setText("Load Complete! Launching game...");
				super.run();
			}
		};
		
	}
	
	public void load() {
		load.start();
	}
}