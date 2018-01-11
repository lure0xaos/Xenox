package gargoyle.xenox.i;

import gargoyle.xenox.game.sys.JScreen;

public interface ScreenManager {
    JScreen screenGet(String name);

    void screenShow(String name);
}
