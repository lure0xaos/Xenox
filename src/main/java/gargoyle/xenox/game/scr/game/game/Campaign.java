package gargoyle.xenox.game.scr.game.game;

import gargoyle.xenox.util.ini.PropertyMap;
import gargoyle.xenox.util.log.Log;
import gargoyle.xenox.util.res.Res;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Campaign {
    private final List<Level> levels = new ArrayList<>();
    private int currentLevelNumber;

    public Campaign(URL url, Charset charset) {
        try (Reader reader = new InputStreamReader(url.openStream(), charset)) {
            PropertyMap file = new PropertyMap(reader);
            for (Map.Entry<String, String> entry : file.entrySet()) {
                URL levelUrl = Res.url(url, entry.getValue().trim());
                if (levelUrl != null) {
                    levels.add(new Level(levelUrl, charset));
                }
            }
        } catch (IOException e) {
            Log.error(String.format("cannot load campaign from %s", url), e);
        }
    }

    public Level getCurrentLevel() {
        return getLevel(currentLevelNumber);
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    private Level getLevel(int num) {
        return num < levels.size() ? levels.get(num) : null;
    }

    public void init() {
        currentLevelNumber = 0;
    }

    public boolean next() {
        currentLevelNumber++;
        return currentLevelNumber < levels.size();
    }
}
