//The main class is in charge of the whole game.

package app;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import app.Utility.Log;
import game.Game;
import game.View;
import game.Loader;
import game.Player;
import game.Chooser;

@SuppressWarnings("serial")
public class Aggravation extends JFrame implements ActionListener {

    public static final int MAX_PLAYERS = 6;
    public static final int TOKENS_PER_PLAYER = 4;

    private static final String GAME_TITLE = "Aggravation - by Jared Bennett";
    private static final Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    public static final Dimension
    MINIMUM_SIZE = View.MINIMUM_SIZE,
        PREFERRED_SIZE = View.PREFERRED_SIZE;


    public static final String
    AC_GAME_START = "AC_GAME_START",
        AC_GAME_LOAD = "AC_GAME_LOAD",
        AC_GAME_SAVE = "AC_GAME_SAVE",
        AC_GAME_SAVE_AS = "AC_GAME_SAVE_AS",
        AC_GAME_TITLE = "AC_GAME_TITLE",
        AC_GAME_EXIT = "AC_GAME_EXIT",
        AC_VIEW_RULES = "AC_VIEW_RULES";

    private JPanel content;
    private JMenuBar menuBar;


    public static void main(String[] args) {
        Aggravation game;


        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {

        } catch (ClassNotFoundException e) {

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }


        game = new Aggravation(GAME_TITLE);
        game.setSize(Aggravation.PREFERRED_SIZE);
        game.setMinimumSize(Aggravation.MINIMUM_SIZE);
        game.setPreferredSize(PREFERRED_SIZE);
        game.getContentPane().setBackground(Style.COLOR_BACKGROUND);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        game.setLocation(dim.width / 2 - game.getSize().width / 2, dim.height / 2 - game.getSize().height / 2);

        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game.setVisible(true);

        Log.d("APP", "START");
    }

    public Aggravation(String title) {
        super(title);

        this.buildMenuBar();
        this.content = new JPanel(new BorderLayout());
        this.content.setBorder(Aggravation.padding);
        this.content.setBackground(Style.COLOR_BACKGROUND);
        this.add(content);
    }



    private void buildMenuBar() {
        JMenu menu;
        JMenuItem menuItem;
        JCheckBoxMenuItem cbMenuItem;


        this.menuBar = new JMenuBar();


        menu = new JMenu("Game");
        menu.setMnemonic(KeyEvent.VK_G);
        menu.getAccessibleContext().setAccessibleDescription("Game management");
        this.menuBar.add(menu);

        menuItem = new JMenuItem("Start New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.getAccessibleContext().setAccessibleDescription("Start a new game");
        menuItem.setActionCommand(Aggravation.AC_GAME_START);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Load game...");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.getAccessibleContext().setAccessibleDescription("Load game from file");
        menuItem.setActionCommand(Aggravation.AC_GAME_LOAD);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Save");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription("Save game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.setActionCommand(Aggravation.AC_GAME_SAVE);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save as...");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription("Save game to new file");
        menuItem.setActionCommand(Aggravation.AC_GAME_SAVE_AS);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem("Return to Title");
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.getAccessibleContext().setAccessibleDescription("Quit game to title screen");
        menuItem.setActionCommand(Aggravation.AC_GAME_TITLE);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.getAccessibleContext().setAccessibleDescription("Exit program");
        menuItem.setActionCommand(Aggravation.AC_GAME_EXIT);
        menuItem.addActionListener(this);
        menu.add(menuItem);



        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("Display management");
        this.menuBar.add(menu);


        menuItem = new JMenuItem("Rules...");
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.getAccessibleContext().setAccessibleDescription("Display game rules in a new window");
        menuItem.setActionCommand(Aggravation.AC_VIEW_RULES);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu.addSeparator();

        cbMenuItem = new JCheckBoxMenuItem("Space Numbers");
        cbMenuItem.setMnemonic(KeyEvent.VK_N);
        cbMenuItem.getAccessibleContext().setAccessibleDescription("Display space numbers next to spaces");
        cbMenuItem.setSelected(false);
        cbMenuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    Log.d("ITEM_EVENT", "View->Space Numbers = OFF");
                    Style.OPTION_VIEW_SPACE_NUMBERS = false;
                    repaint();
                } else {
                    Log.d("ITEM_EVENT", "View->Space Numbers = ON");
                    Style.OPTION_VIEW_SPACE_NUMBERS = true;
                    repaint();
                }
            }
        });
        menu.add(cbMenuItem);



        this.setJMenuBar(this.menuBar);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Log.v("ACTION_EVENT", e.getActionCommand());

        switch (e.getActionCommand()) {
            case Aggravation.AC_GAME_EXIT:
                this.setVisible(false);
                this.dispose();
                break;
            case Aggravation.AC_GAME_LOAD:

                break;
            case Aggravation.AC_GAME_SAVE:

                break;
            case Aggravation.AC_GAME_SAVE_AS:

                break;
            case Aggravation.AC_GAME_START:
                Loader gl = new Loader(this);
                this.content.removeAll();
                this.content.add(gl);
                this.pack();
                this.repaint();
                gl.load();
                break;
            case Aggravation.AC_GAME_TITLE:

                break;
            case Aggravation.AC_VIEW_RULES:

                break;
        }
    }

    public void startChooser(Chooser pc) {
        this.content.removeAll();
        this.content.add(pc);
        this.pack();
        pc.resizeImage();
    }

    public void startNewGame(Player[] pSet) {
        Game.getCurrentInstance().start(pSet);
        this.content.removeAll();
        this.content.add(Game.getCurrentInstance().getDisplay(), BorderLayout.CENTER);
        this.pack();
        Game.getCurrentInstance().getDisplay().refresh();
    }
}
