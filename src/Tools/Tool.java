package tools;
//The tool contains game-related information as well as certain actions that players may do.
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import app.Style;
import game.Game;

@SuppressWarnings("serial")
public class Tool extends JPanel {
    private ArrayList < StatusView > pViews = new ArrayList < StatusView > ();


    ArrayList < String > logData = new ArrayList < String > ();
    private JPanel logBox;
    private JScrollPane logScroll;
    private JButton bRoll = new JButton("Roll Die");


    private static Dimension DIM_MAX = new Dimension(10000, 100);
    private static Dimension DIM_LOG_MAX = new Dimension(100, 100);

    public Tool() {
        super(new GridBagLayout());
        this.setBackground(Style.COLOR_BACKGROUND);
        this.setMaximumSize(DIM_MAX);
    }

    public void init() {


        logBox = new JPanel(new BorderLayout());
        logScroll = new JScrollPane();
        logScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        logScroll.setBackground(Style.COLOR_BACKGROUND);
        logScroll.setBorder(BorderFactory.createTitledBorder(
            Style.BORDER_BASIC,
            "Game Log",
            TitledBorder.DEFAULT_POSITION,
            TitledBorder.DEFAULT_JUSTIFICATION,
            Style.FONT_TITLE,
            Style.COLOR_FONT));
        logScroll.setMaximumSize(Tool.DIM_LOG_MAX);

        addLogMessage("GAME START", false);

        logBox.add(logScroll, BorderLayout.CENTER);

        bRoll.setEnabled(false);
        bRoll.setOpaque(false);
        bRoll.setActionCommand(Game.AK_ROLL);
        logBox.add(bRoll, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(logBox, gbc);

        ArrayList < Integer > turnOrder = Game.getCurrentInstance().getTurnOrder();
        for (int i = 0; i < turnOrder.size(); ++i) {
            StatusView psv = new StatusView(Game.getCurrentInstance().getPlayer(turnOrder.get(i)));
            psv.setOpaque(false);

            this.pViews.add(psv);

            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = .2;
            gbc.weighty = 1.0;
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            this.add(psv, gbc);
        }

    }

    public void updateContent() {
        for (StatusView psv: this.pViews)
            psv.updateContent();
    }


    public void addLogMessage(String m) {
        this.addLogMessage(m, true);
    }
    public void addLogMessage(String m, boolean includeDate) {
        String newLog = m;
        SimpleDateFormat t = new SimpleDateFormat("hh:mm:ss");
        if (includeDate) {
            newLog = "[" + t.format(new Date()) + "] " + newLog;
        }

        logData.add(newLog);
        JList list = new JList(logData.toArray());
        logScroll.setViewportView(list);
        logScroll.getVerticalScrollBar().setValue(logScroll.getVerticalScrollBar().getMaximum());
    }

    public JButton getRollButton() {
        return this.bRoll;
    }
}