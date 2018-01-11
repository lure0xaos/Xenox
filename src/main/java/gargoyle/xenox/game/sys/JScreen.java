package gargoyle.xenox.game.sys;

import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Resources;

import javax.swing.JPanel;
import gargoyle.xenox.util.res.audio.AudioClip;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

public abstract class JScreen extends JPanel {
    protected final Controls controls;
    protected final String id;
    protected final Messages messages;
    protected final Options options;
    protected final Resources resources;
    private final AudioManager audio;
    private final ScreenManager screens;
    protected volatile boolean active;

    protected JScreen(String id, Component component, Resources resources, Messages messages, Controls controls, AudioManager audio, ScreenManager screens, Options options) {
        this.id = id;
        this.resources = resources;
        this.messages = messages;
        this.controls = controls;
        this.audio = audio;
        this.screens = screens;
        this.options = options;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                component.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });
    }

    protected abstract String _process() throws InterruptedException;

    public void destroy() {
    }

    public final Controls getControls() {
        return controls;
    }

    public final String getId() {
        return id;
    }

    public final Messages getMessages() {
        return messages;
    }

    public final Options getOptions() {
        return options;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected final void musicLoop() {
        audio.musicLoop();
    }

    protected final void musicSet(AudioClip clip) {
        audio.musicSet(clip);
    }

    protected final void musicSet(URL... clip) {
        audio.musicSet(clip);
    }

    protected final void musicStop() {
        audio.musicStop();
    }

    public final String process() throws InterruptedException {
        controls.reset();
        screens.screenShow(id);
        repaint();
        active = true;
        return _process();
    }

    public final void soundPlay(AudioClip clip) {
        audio.soundPlay(clip);
    }

    protected final void soundPlay(URL... clip) {
        audio.soundPlay(clip);
    }
}
