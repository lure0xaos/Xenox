package gargoyle.xenox.util.applet;

import javax.swing.JPanel;
import java.net.URL;

public class JApplet extends JPanel implements Applet {
    private AppletStub stub;

    protected URL getCodeBase() {
        return stub.getCodeBase();
    }

    protected URL getDocumentBase() {
        return stub.getDocumentBase();
    }

    protected void setStub(AppletStub stub) {
        this.stub = stub;
    }
}
