package gargoyle.xenox.game.sys;

import gargoyle.xenox.util.config.Config;

public class Options extends Config {
    private static final String PARAM_RESIZE = "resize";
    private static final String PARAM_SOUND = "sound";
    private static final long serialVersionUID = -2434365584897663718L;

    public boolean isResize() {
        return getBoolean(PARAM_RESIZE, false);
    }

    public void setResize(boolean resize) {
        putBoolean(PARAM_RESIZE, resize);
    }

    public boolean isSound() {
        return getBoolean(PARAM_SOUND, true);
    }

    public void setSound(boolean sound) {
        putBoolean(PARAM_SOUND, sound);
    }
}
