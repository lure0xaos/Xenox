package gargoyle.xenox.game.sys;

import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class JRunningScreen extends JScreen {
    protected transient volatile Image image;
    protected String message;
    protected String title;
    private volatile boolean running;
    private long timer;

    protected JRunningScreen(String id, Component applet, Resources resources, Messages messages, AudioManager audio, ScreenManager screens, Options options, Controls controls, String title) {
        super(id, applet, resources, messages, controls, audio, screens, options);
        this.title = title;
        running = true;
    }

    protected JRunningScreen(String id, Component applet, BufferedImage image, Resources resources, Messages messages, AudioManager audio, ScreenManager screens, Options options, Controls controls, String title) {
        this(id, applet, resources, messages, audio, screens, options, controls, title);
        this.image = image;
    }

    @Override
    protected String _process() throws InterruptedException {
        return processWait();
    }

    @Override
    public void destroy() {
        running = false;
    }

    protected void clear() {
        title = "";
        message = "";
        image = null;
        repaint();
    }

    protected abstract void draw(Graphics g);

    protected void drawImage(Graphics g) {
        if (image == null) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, image.getWidth(this), image.getHeight(this), this);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawImage(g);
        draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            return super.getPreferredSize();
        }
        Dimension size = new Dimension(image.getWidth(this), image.getHeight(this));
        setPreferredSize(size);
        return size;
    }

    protected String processWait() throws InterruptedException {
        try {
            long t = System.currentTimeMillis();
            while (!controls.isAction() && running) {
                Thread.sleep(10);
                if (active && timer != 0 && System.currentTimeMillis() - t > timer) {
                    break;
                }
            }
        } finally {
            clear();
//            applet.showStatus("");
        }
        return id;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }
}
