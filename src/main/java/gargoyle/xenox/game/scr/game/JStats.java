package gargoyle.xenox.game.scr.game;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

class JStats extends JPanel {
    private static final int SIZE_BIG = 15;
    private static final int SIZE_SMALL = 8;
    private static final String STR_STAT_BALLS = "balls";
    private static final String STR_STAT_LEVEL = "level";
    private static final String STR_STAT_LIVES = "lives";
    private static final String STR_STAT_NEEDED = "target";
    private static final String STR_STAT_PERCENTS = "open";
    private static final String STR_STAT_SCORE = "score";
    private final JLabel balls;
    private final JGame game;
    private final JLabel level;
    private final JLabel lives;
    private final JLabel needed;
    private final JLabel percent;
    private final JLabel score;

    public JStats(JGame game) {
        this.game = game;
        setLayout(new GridLayout(0, 6));
        setBackground(Color.BLACK);
        add(createLabel(STR_STAT_SCORE));
        add(score = createLabel2());
        add(createLabel(STR_STAT_LEVEL));
        add(level = createLabel2());
        add(createLabel(STR_STAT_LIVES));
        add(lives = createLabel2());
        add(createLabel(STR_STAT_PERCENTS));
        add(percent = createLabel2());
        add(createLabel(STR_STAT_NEEDED));
        add(needed = createLabel2());
        add(createLabel(STR_STAT_BALLS));
        add(balls = createLabel2());
        setOpaque(true);
    }

    private JLabel createLabel(String str) {
        JLabel label = new JLabel(String.format("%s: ", game.getMessages().get(str)));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, SIZE_SMALL));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.LIGHT_GRAY);
        label.setOpaque(true);
        return label;
    }

    private JLabel createLabel2() {
        JLabel label = new JLabel("");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, SIZE_BIG));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        return label;
    }

    @Override
    public void paint(Graphics g) {
        if (game.getField() == null) {
            return;
        }
        if (game.getField().getPlayer() == null) {
            return;
        }
        score.setText(Integer.toString(game.getField().getPlayer().getScore()));
        level.setText(Integer.toString(game.getField().getLevelNum()));
        lives.setText(Integer.toString(game.getField().getPlayer().getLives()));
        percent.setText(Integer.toString(game.getField().getPercent()));
        needed.setText(Integer.toString(game.getField().getLevel().getPercent()));
        balls.setText(Integer.toString(game.getField().getBalls().size()));
        super.paint(g);
    }
}
