package gargoyle.xenox.game.scr.hi;

import gargoyle.xenox.game.scr.menu.JIntro;
import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.game.sys.HiScore;
import gargoyle.xenox.game.sys.HiScore.Record;
import gargoyle.xenox.game.sys.JRunningScreen;
import gargoyle.xenox.game.sys.Options;
import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public class JHi extends JRunningScreen {
    public static final String SCR_HI = "Hi";
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color COLOR_ME = Color.BLUE;
    private static final Font FONT = new Font(Font.SERIF, Font.BOLD, 36);
    private static final Color FOREGROUND = Color.YELLOW;
    private static final int NAME_LIMIT = 10;
    private final HiScore hiScore;

    public JHi(Component applet, Resources resources, Messages messages, HiScore hiScore, AudioManager audio, ScreenManager screens, Options options, Controls controls, String title) {
        super(JHi.SCR_HI, applet, resources.load(BufferedImage.class, JIntro.INTRO_IMAGE), resources, messages, audio, screens, options, controls, title);
        setBackground(BACKGROUND);
        setForeground(FOREGROUND);
        setFont(FONT);
        this.hiScore = hiScore;
    }

    @Override
    protected void draw(Graphics g) {
        String userName = hiScore.getUserName();
        List<Record> records = hiScore.getRecords();
        int i = 0;
        for (Record record : records) {
            Color color = Objects.equals(record.getName(), userName) ? COLOR_ME : i % 3 == 0 ? FOREGROUND.darker() : FOREGROUND;
            drawLine(g, i, record.getName(), record.getScore(), color);
            i++;
        }
    }

    private void drawLine(Graphics g, int i, String name, int score, Color color) {
        FontMetrics fm = getFontMetrics(getFont());
        int y0 = 5 + fm.getAscent() + Math.max(0, (getHeight() - fm.getHeight() * HiScore.LINES) / 2);
        g.setColor(color);
        int y = y0 + i * fm.getHeight();
        g.drawString(name.length() > NAME_LIMIT ? name.substring(0, NAME_LIMIT) : name, 5, y);
        String textScore = String.valueOf(score);
        g.drawString(textScore, getWidth() - fm.stringWidth(textScore) - 5, y);
    }
}
