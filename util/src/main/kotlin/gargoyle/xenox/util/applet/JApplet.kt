package gargoyle.xenox.util.applet

import gargoyle.xenox.util.audio.AudioClip
import java.awt.Image
import java.io.InputStream
import java.net.URL
import javax.swing.JPanel

open class JApplet : JPanel(), Applet {
    internal lateinit var stub: AppletStub

    final override val appletContext: AppletContext
        get() = stub.appletContext

    final override fun getAudioClip(url: URL): AudioClip = appletContext.getAudioClip(url)
    final override fun getAudioClip(url: InputStream): AudioClip = appletContext.getAudioClip(url)

    final override fun getImage(url: URL): Image = appletContext.getImage(url)
    final override fun getImage(url: InputStream): Image = appletContext.getImage(url)

    final override fun getApplet(name: String): Applet? = appletContext.getApplet(name)

    final override fun getParameter(name: String): String? = stub.getParameter(name)

    final override fun isActive(): Boolean = stub.isActive()

    final override fun showStatus(msg: String): Unit = appletContext.showStatus(msg)

    override fun init() {
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun destroy() {
    }

    final override val codeBase: URL?
        get() = stub.codeBase
    final override val documentBase: URL?
        get() = stub.documentBase
}
