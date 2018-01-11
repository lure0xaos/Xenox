package gargoyle.xenox.util.applet;

import gargoyle.xenox.util.log.Log;
import gargoyle.xenox.util.res.Resources;

import gargoyle.xenox.util.applet.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.net.URL;

public abstract class GApplet extends JApplet {
    private static final String STR_EXIT = "Exit?";

    static {
        Log.debug("");
    }

    protected Resources resources;
    GFrame application;
    private volatile boolean active;

    protected GApplet(Resources resources) {
        application = null;
        setFocusable(true);
        this.resources = resources;
    }

    private static void changeLookAndFeel(String laf, Component component) {
        try {
            boolean decor = UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (component != null) {
                decor &= SwingUtilities.getRootPane(component).getWindowDecorationStyle() != JRootPane.NONE;
            }
            UIManager.setLookAndFeel(laf);
            JFrame.setDefaultLookAndFeelDecorated(decor);
            JDialog.setDefaultLookAndFeelDecorated(decor);
            if (component != null) {
                Window frame = SwingUtilities.getWindowAncestor(component);
                if (!frame.isDisplayable()) {
                    if (frame instanceof Frame) {
                        ((Frame) frame).setUndecorated(!decor);
                    }
                    if (frame instanceof Dialog) {
                        ((Dialog) frame).setUndecorated(!decor);
                    }
                }
            }
            if (component != null) {
                SwingUtilities.updateComponentTreeUI(component);
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            Log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void run(Class<? extends GApplet> clazz, String[] args) {
        try {
            changeLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName(), null);
            GApplet applet = clazz.newInstance();
            new GFrame(applet, args, applet.resources).showMe();
            changeLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName(), applet);
        } catch (InstantiationException | IllegalAccessException e) {
            Log.fatal(e.getLocalizedMessage(), e);
        }
    }

    protected final boolean ask(String message) {
        return JOptionPane.showConfirmDialog(this, message, getTitle(), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public boolean canExit() {
        return isApplication() && ask(STR_EXIT);
    }

    protected abstract void doDestroy();

    protected abstract void doInit();

    public final void error(Throwable message) {
        JOptionPane.showMessageDialog(this, String.format("%s: %s", message.getClass().getSimpleName(), message.getLocalizedMessage()), getTitle(), JOptionPane.ERROR_MESSAGE);
        Log.error(message.getLocalizedMessage(), message);
    }

    public void exit() {
        if (application != null) {
            application.exit();
        }
    }

    protected final URL getCurrentPath() {
        return application == null ?
                null :
                getClass().getProtectionDomain().getCodeSource().getLocation();
    }

    public String getTitle() {
        Component parent = SwingUtilities.getRoot(this);
        if (parent instanceof Frame) {
            return ((Frame) parent).getTitle();
        }
        return getClass().getSimpleName();
    }

    public boolean isActive() {
        return active;
    }

    public void showStatus(String msg) {
    }

    public final synchronized void init() {
        active = true;
        setFocusable(true);
        try {
            doInit();
        } catch (RuntimeException e) {
            error(e);
        }
    }

    protected void doStart() {
    }

    protected void doStop() {
    }

    public void start() {
        active = true;
        try {
            doStart();
        } catch (RuntimeException e) {
            error(e);
        }
    }

    public void stop() {
        active = false;
        try {
            doStop();
        } catch (RuntimeException e) {
            error(e);
        }
    }
//    public void setStub(AppletStub stub) {
//    }

    public final synchronized void destroy() {
        try {
            doDestroy();
        } catch (RuntimeException e) {
            error(e);
        }
    }

    protected final boolean isApplication() {
        return application != null;
    }

    protected final String prompt(String message) {
        return JOptionPane.showInputDialog(this, message, getTitle(), JOptionPane.QUESTION_MESSAGE);
    }
}
