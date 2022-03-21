package gargoyle.xenox.util.applet

import gargoyle.xenox.util.log.Log
import gargoyle.xenox.util.res.Resources
import java.awt.Component
import java.awt.Dialog
import java.awt.Frame
import java.net.URL
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JRootPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

abstract class GApplet protected constructor(resources: Resources) : JApplet() {
    protected var resources: Resources
    var application: GFrame? = null

    var isActive = false
        private set

    init {
        isFocusable = true
        this.resources = resources
    }

    protected fun ask(message: String?): Boolean {
        return JOptionPane.showConfirmDialog(
            this, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION
    }

    open fun canExit(): Boolean {
        return isApplication() && ask(STR_EXIT)
    }

    protected abstract fun doDestroy()
    protected abstract fun doInit()
    fun error(message: Throwable) {
        JOptionPane.showMessageDialog(
            this,
            String.format("%s: %s", message.javaClass.simpleName, message.localizedMessage),
            title,
            JOptionPane.ERROR_MESSAGE
        )
        Log.error(message.localizedMessage, message)
    }

    fun exit() {
        if (application != null) {
            application!!.exit()
        }
    }

    protected val currentPath: URL?
        get() = if (application == null) null else javaClass.protectionDomain.codeSource.location
    val title: String
        get() {
            val parent = SwingUtilities.getRoot(this)
            return if (parent is Frame) {
                parent.title
            } else javaClass.simpleName
        }

    fun showStatus(msg: String) {}

    fun init() {
        isActive = true
        isFocusable = true
        try {
            doInit()
        } catch (e: RuntimeException) {
            error(e)
        }
    }

    protected open fun doStart() {}
    protected open fun doStop() {}
    fun start() {
        isActive = true
        try {
            doStart()
        } catch (e: RuntimeException) {
            error(e)
        }
    }

    fun stop() {
        isActive = false
        try {
            doStop()
        } catch (e: RuntimeException) {
            error(e)
        }
    }

    //    public void setStub(AppletStub stub) {
    //    }

    fun destroy() {
        try {
            doDestroy()
        } catch (e: RuntimeException) {
            error(e)
        }
    }

    protected fun isApplication(): Boolean {
        return application != null
    }

    protected fun prompt(message: String?): String {
        return JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE)
    }

    companion object {
        private const val STR_EXIT = "Exit?"

        init {
            Log.debug("")
        }

        private fun changeLookAndFeel(laf: String, component: Component?) {
            try {
                var decor = UIManager.getLookAndFeel().supportsWindowDecorations
                if (component != null) {
                    decor = decor and (SwingUtilities.getRootPane(component).windowDecorationStyle != JRootPane.NONE)
                }
                UIManager.setLookAndFeel(laf)
                JFrame.setDefaultLookAndFeelDecorated(decor)
                JDialog.setDefaultLookAndFeelDecorated(decor)
                if (component != null) {
                    val frame = SwingUtilities.getWindowAncestor(component)
                    if (!frame.isDisplayable) {
                        if (frame is Frame) {
                            frame.isUndecorated = !decor
                        }
                        if (frame is Dialog) {
                            frame.isUndecorated = !decor
                        }
                    }
                }
                if (component != null) {
                    SwingUtilities.updateComponentTreeUI(component)
                }
            } catch (e: ClassNotFoundException) {
                Log.error(e.localizedMessage, e)
            } catch (e: UnsupportedLookAndFeelException) {
                Log.error(e.localizedMessage, e)
            } catch (e: IllegalAccessException) {
                Log.error(e.localizedMessage, e)
            } catch (e: InstantiationException) {
                Log.error(e.localizedMessage, e)
            }
        }

        fun run(clazz: Class<out GApplet>, args: Array<String>) {
            try {
                changeLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName(), null)
                val applet = clazz.newInstance()
                GFrame(applet, args, applet.resources).showMe()
                changeLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName(), applet)
            } catch (e: InstantiationException) {
                Log.fatal(e.localizedMessage, e)
            } catch (e: IllegalAccessException) {
                Log.fatal(e.localizedMessage, e)
            }
        }
    }
}
