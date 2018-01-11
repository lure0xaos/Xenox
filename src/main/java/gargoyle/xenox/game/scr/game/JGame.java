package gargoyle.xenox.game.scr.game;

import gargoyle.xenox.game.scr.game.game.Field;
import gargoyle.xenox.game.scr.game.game.Item;
import gargoyle.xenox.game.scr.game.game.Level;
import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.game.sys.JScreen;
import gargoyle.xenox.game.sys.Options;
import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Resources;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;

public class JGame extends JScreen {
    public static final String SCR_GAME = "Game";
    private final Field field;
    private final JField jfield;
    private Level level;
    private volatile boolean running;

    public JGame(Component applet, Resources resources, Messages messages, Controls controls, AudioManager audio, ScreenManager screens, Options options) {
        super(JGame.SCR_GAME, applet, resources, messages, controls, audio, screens, options);
        field = new Field(resources);
        setLayout(new BorderLayout());
        jfield = new JField(this, resources);
        add(jfield, BorderLayout.CENTER);
        JStats stats = new JStats(this);
        add(stats, BorderLayout.SOUTH);
    }

    @Override
    protected String _process() {
        field.resetBalls();
        musicLoop();
        running = true;
        while (running) {
            if (field.getPercent() > level.getPercent()) {
                return id;
            }
            if (!isPlayerAlive()) {
                return null;
            }
            if (field.hasGotItem(true, Item.I_LEVEL)) {
                return id;
            }
            long l = System.currentTimeMillis();
            if (active) {
                if (field.gameStep(this) || field.hasGotItem(true, Item.I_DEATH)) {
                    return null;
                }
                int speed = (int) Math.max(10, level.getSpeed() - System.currentTimeMillis() + l);
                repaint(speed);
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    return null;
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        running = false;
    }

    public Field getField() {
        return field;
    }

    public int getPlayerScore() {
        return field.getPlayer().getScore();
    }

    public boolean isPlayerAlive() {
        return field.getPlayer().isAlive();
    }

    public void loadLevel(int levelNum, Level level) {
        this.level = level;
        musicSet(level.getMusic());
        setImage(level.getImage());
        setCover(level.getCover());
        setSize(getPreferredSize());
        field.init(levelNum, level);
    }

    private void setCover(URL cover) {
        jfield.setCover(cover);
    }

    private void setImage(URL image) {
        jfield.setImage(image);
    }
}
