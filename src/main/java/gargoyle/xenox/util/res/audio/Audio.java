package gargoyle.xenox.util.res.audio;

import java.net.URL;

public final class Audio {
    private Audio() {
    }

    public static AudioClip newAudioClip(URL location) {
        return new AudioClipImpl(location);
    }
}
