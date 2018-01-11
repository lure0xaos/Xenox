package gargoyle.xenox.util.applet;

import gargoyle.xenox.util.res.audio.AudioClip;

import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public interface AppletStub {

    AudioClip getAudioClip(URL url);

    Image getImage(URL url);

    Applet getApplet(String name);

    Enumeration<Applet> getApplets();

    URL getDocumentBase();

    URL getCodeBase();

    void showStatus(String status);

    void setStream(String key, InputStream stream);

    InputStream getStream(String key);

    Iterator<String> getStreamKeys();

    void showDocument(URL url);

    void showDocument(URL url, String target);

    String getParameter(String name);

    AppletContext getAppletContext();

    void appletResize(int width, int height);
}
