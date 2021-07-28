package tools;
//A panel that displays information about a specific player.    
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import app.Aggravation;
import app.Style;
import app.Utility.ColorManager;
import app.Utility.Log;
import board.space.Space;
import game.Game;
import game.Player;

public class StatusView extends JPanel {
    private static final int BORDER_WIDTH = 3;
    private static final Border PADDING = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    private static final String FORMAT =
        "<html><center><font color=%s>" +
        "<b><u>%s</u></b><br>" +
        "<br>" +
        "%d at Base<br>" +
        "%d at Home<br>" +
        "%d Exposed<br>" +
        "<br>" +
        "%s" +
        "</font></center></html>";

    private static final Dimension
    MIN_SIZE = new Dimension(100, 150);

    private Player p;
    private JLabel name;

    public StatusView(Player p) {
        super(new BorderLayout());
        this.setMinimumSize(StatusView.MIN_SIZE);
        this.setBackground(Style.COLOR_BACKGROUND);
        this.p = p;

        name = new JLabel();
        name.setBorder(StatusView.PADDING);
        this.add(name, BorderLayout.CENTER);
    }
    public void updateContent() {
        Log.v("PSV", "\'" + p.getName() + "\' Update");

        int inBase = 0, inHome = 0, free;
        for (Space s: Game.getCurrentInstance().getDisplay().getBoard().getPlayerBases(p))
            if (s.hasToken())
                ++inBase;
        for (Space s: Game.getCurrentInstance().getDisplay().getBoard().getPlayerHomes(p))
            if (s.hasToken())
                ++inHome;
        free = Aggravation.TOKENS_PER_PLAYER - inBase - inHome;


        name.setText(String.format(StatusView.FORMAT,
            ColorManager.getHexString(this.p.getColor()),
            this.p.getName(),
            inBase, inHome, free,
            this.p.getStatusString()
        ));

        if (this.p.getStatus() == Player.Status.CURRENT_PLAYER) {
            this.setBorder(BorderFactory.createLineBorder(p.getColor(), BORDER_WIDTH, true));
        } else {
            this.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        }
    }
}

