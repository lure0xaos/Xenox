package gargoyle.xenox.game.scr.status;

import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.game.sys.JRunningScreen;
import gargoyle.xenox.game.sys.Options;
import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

public class JStatus extends JRunningScreen {
    public static final String SCR_STATUS = "Status";
    public static final String STR_GAME_OVER = "game_over";
    public static final String STR_GET_READY = "get_ready";
    public static final String STR_LEVEL_FINISHED = "level_finished";
    public static final String STR_LIFE_LOST = "life_lost";
    private static final int SIZE_BIG = 36;
    private static final Font FONT_BIG = new Font(Font.MONOSPACED, Font.PLAIN, SIZE_BIG);
    private static final int SIZE_SMALL = 18;
    private static final Font FONT_SMALL = new Font(Font.MONOSPACED, Font.PLAIN, SIZE_SMALL);

    public JStatus(Component applet, Resources resources, Messages messages, AudioManager audio, ScreenManager screens, Options options, Controls controls, String title) {
        super(JStatus.SCR_STATUS, applet, resources, messages, audio, screens, options, controls, title);
    }

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    protected void draw(Graphics g) {
        if (!isEmpty(title)) drawString(g, Pos.TOP, FONT_SMALL, title);
        if (!isEmpty(title)) {
            drawString(g, Pos.CENTER, FONT_BIG, title);
        }
        if (!isEmpty(message)) {
            drawString(g, Pos.BOTTOM, FONT_SMALL, message);
        }
    }

    private void drawString(Graphics g, Pos pos, Font f, String str) {
        FontMetrics fm = g.getFontMetrics(f);
        Rectangle bb = fm.getStringBounds(str, 0, str.length(), g).getBounds();
        g.setColor(Color.BLACK);
        int y;
        switch (pos) {
            case TOP:
                y = 0;
                break;
            case CENTER:
                y = (getHeight() - bb.height) / 2;
                break;
            case BOTTOM:
                y = getHeight() - bb.height;
                break;
            default:
                y = 0;
        }
        g.fillRect(0, y, getWidth(), bb.height);
        g.setColor(Color.WHITE);
        g.setFont(f);
        g.drawString(str, (getWidth() - bb.width) / 2, y + fm.getAscent());
    }

    public void process(String title, String message, URL imageUrl) throws InterruptedException {
        clear();
//        applet.showStatus(message);
        this.title = title;
        this.message = message;
        if (imageUrl != null) {
            image = resources.load(BufferedImage.class, imageUrl);
        }
        if (image != null) {
            Dimension size = new Dimension(image.getWidth(this), image.getHeight(this));
            setPreferredSize(size);
            setSize(size);
        }
        process();
    }

    @SuppressWarnings("LawOfDemeter")
    public void process(String title, String message, URL imageUrl, URL soundUrl) throws InterruptedException {
        boolean sound = options.isSound();
        if (!sound) {
            musicStop();
        }
        if (soundUrl != null && sound) {
            soundPlay(soundUrl);
        }
        process(title, message, imageUrl);
        musicStop();
    }

    enum Pos {
        TOP, CENTER, BOTTOM
    }
}
