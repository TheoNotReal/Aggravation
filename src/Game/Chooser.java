package game;
//Sets up the players. When a new game is begun, this object is created.
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import app.Aggravation;
import app.Style;
import app.Utility.Log;

@SuppressWarnings("serial")
public class Chooser extends JPanel {
    private JLabel title;
    private PlayerOption[] options = new PlayerOption[Aggravation.MAX_PLAYERS];
    private JButton bStart = new JButton("START");
    private Aggravation context;
    private BufferedImage tPic;

    public Chooser(Aggravation ctxt) {
        super(new GridBagLayout());
        GridBagConstraints gbc;
        this.context = ctxt;

        this.setOpaque(false);

        try {
            tPic = ImageIO.read(this.getClass().getResource("/img/boxCover.jpg"));
        } catch (IOException e1) {
           
            e1.printStackTrace();
        }
        title = new JLabel(" ");
        title.setVerticalAlignment(JLabel.CENTER);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(Style.FONT_TITLE);
        title.setForeground(Color.WHITE);
        bStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean validInput = true;

                if (validInput) {
                    Player[] pSet = new Player[options.length];
                    for (int i = 0; i < pSet.length; ++i) {
                        if (options[i].isEnabled())
                            pSet[i] = new Player(i, options[i].getColor(), options[i].getName());
                    }
                    context.startNewGame(pSet);
                }

            }
        });


        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = .5;
        add(title, gbc);


        for (int i = 0; i < options.length; ++i) {
            options[i] = new PlayerOption("Player " + (i + 1), Style.DEFAULT_PLAYER_COLORS[i], true);

            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.weightx = 1.0;
            gbc.weighty = .2;
            add(options[i], gbc);
        }

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.weighty = .4;
        add(bStart, gbc);


    }
    public void resizeImage() {
        int w, h;
        final double ratio = (double) tPic.getWidth() / tPic.getHeight();

        w = title.getWidth();
        h = title.getHeight();

        if (h > 0 && w > 0) {
            title.setText("");
            if (h < w) {
                title.setIcon(new ImageIcon(tPic.getScaledInstance(w, (int)(w / ratio), Image.SCALE_DEFAULT)));
            } else {
                title.setIcon(new ImageIcon(tPic.getScaledInstance((int)(h / ratio), h, Image.SCALE_DEFAULT)));
            }
        } else {
            Log.e("ILOAD", "Lable = 0");
        }

    }

    private class PlayerOption extends JPanel {
        private boolean enabled;
        private JEditorPane nameInput = new JEditorPane();
        private JCheckBox activeToggle = new JCheckBox();
        private JButton bPickColor = new JButton("Choose Color");
        private Color pColor;

        public PlayerOption(String defaultName, Color defaultColor, Boolean enabled) {
            super(new GridBagLayout());

            nameInput.setText(defaultName);
            bPickColor.setOpaque(false);
            activeToggle.setOpaque(false);
            activeToggle.setSelected(enabled);
            activeToggle.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setOptionEnabled(!PlayerOption.this.enabled);
                }
            });
            bPickColor.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Color c = JColorChooser.showDialog(null, "Choose color for " + getName(), pColor);

                    if (c != null)
                        setColor(c);
                }
            });

            add(activeToggle);
            add(nameInput);
            add(bPickColor);

            setColor(defaultColor);
            setOptionEnabled(enabled);

        }

        private void setOptionEnabled(boolean b) {
            enabled = b;
            nameInput.setEnabled(enabled);
        }
        private void setColor(Color c) {
            pColor = c;
            setBackground(pColor);
        }

        public Color getColor() {
            return this.pColor;
        }
        public String getName() {
            return this.nameInput.getText();
        }
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}