package gargoyle.xenox.game.scr.game.game;

import gargoyle.xenox.Xenox;
import gargoyle.xenox.util.ini.PropertyMap;
import gargoyle.xenox.util.log.Log;
import gargoyle.xenox.util.res.Res;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Level {
    private static final int DEFAULT_BALLS = 80;
    private static final int DEFAULT_PERCENT = 80;
    private static final int DEFAULT_SPEED = 50;
    private static final String PARAM_BALLS = "balls";
    private static final String PARAM_COVER = "cover";
    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_IMAGE = "image";
    private static final String PARAM_LIVES = "lives";
    private static final String PARAM_MUSIC = "music";
    private static final String PARAM_PERCENT = "percent";
    private static final String PARAM_SPEED = "speed";
    private static final String PARAM_WIDTH = "width";
    private static final String PROP_TITLE = "title";
    private String title;
    private int balls = DEFAULT_BALLS;
    private URL cover;
    private int height = Xenox.PF_HEIGHT;
    private URL image;
    private int lives = Xenox.LIVES;
    private URL music;
    private int percent = DEFAULT_PERCENT;
    private int speed = DEFAULT_SPEED;
    private int width = Xenox.PF_WIDTH;

    public Level(URL url, Charset charset) {
        try (Reader reader = new InputStreamReader(url.openStream(), charset)) {
            PropertyMap file = new PropertyMap(reader);
            title = file.get(PROP_TITLE);
            balls = file.get(PARAM_BALLS, DEFAULT_BALLS);
            height = file.get(PARAM_HEIGHT, Xenox.PF_HEIGHT);
            lives = file.get(PARAM_LIVES, Xenox.LIVES);
            percent = file.get(PARAM_PERCENT, DEFAULT_PERCENT);
            speed = file.get(PARAM_SPEED, DEFAULT_SPEED);
            width = file.get(PARAM_WIDTH, Xenox.PF_WIDTH);
            cover = Res.url(url, file.get(PARAM_COVER));
            image = Res.url(url, file.get(PARAM_IMAGE));
            music = Res.url(url, file.get(PARAM_MUSIC));
        } catch (IOException e) {
            Log.error(String.format("cannot load level from %s", url), e);
        }
    }

    public int getBalls() {
        return balls;
    }

    public URL getCover() {
        return cover;
    }

    public int getHeight() {
        return height;
    }

    public URL getImage() {
        return image;
    }

    public int getLives() {
        return lives;
    }

    public URL getMusic() {
        return music;
    }

    public int getPercent() {
        return percent;
    }

    public int getSpeed() {
        return speed;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }
}
