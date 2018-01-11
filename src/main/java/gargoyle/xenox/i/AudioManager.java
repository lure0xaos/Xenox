package gargoyle.xenox.i;

import gargoyle.xenox.util.res.audio.AudioClip;

import java.net.URL;

public interface AudioManager {
    void musicLoop();

    void musicPlay();

    void musicSet(AudioClip clip);

    void musicSet(URL... clip);

    void musicStop();

    void soundPlay(AudioClip clip);

    void soundPlay(URL... clip);
}
