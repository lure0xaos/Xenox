package gargoyle.xenox.game.sys

import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Resources
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage

abstract class JRunningScreen protected constructor(
    id: String,
    applet: Component,
    resources: Resources?,
    messages: Messages,
    audio: AudioManager,
    screens: ScreenManager,
    options: Options,
    controls: Controls,
    protected var title: String?
) : JScreen(id, applet, resources, messages, controls, audio, screens, options) {

    protected var image: Image? = null
    protected var message: String? = null

    private var running = true
    private var timer: Long = 0

    protected constructor(
        id: String,
        applet: Component,
        image: BufferedImage?,
        resources: Resources?,
        messages: Messages,
        audio: AudioManager,
        screens: ScreenManager,
        options: Options,
        controls: Controls,
        title: String
    ) : this(id, applet, resources, messages, audio, screens, options, controls, title) {
        this.image = image
    }

    override fun _process(): String {
        return processWait()
    }

    override fun destroy() {
        running = false
    }

    protected fun clear() {
        title = ""
        message = ""
        image = null
        repaint()
    }

    protected abstract fun draw(g: Graphics)
    protected fun drawImage(g: Graphics) {
        if (image == null) {
            g.color = Color.BLACK
            g.fillRect(0, 0, width, height)
        } else {
            g.drawImage(image, 0, 0, width, height, 0, 0, image!!.getWidth(this), image!!.getHeight(this), this)
        }
    }

    override fun paintComponent(g: Graphics) {
        drawImage(g)
        draw(g)
    }

    override fun getPreferredSize(): Dimension {
        if (image == null) {
            return super.getPreferredSize()
        }
        val size = Dimension(image!!.getWidth(this), image!!.getHeight(this))
        preferredSize = size
        return size
    }

    protected fun processWait(): String {
        try {
            val t = System.currentTimeMillis()
            while (!controls.isAction && running) {
                Thread.sleep(10)
                if (isActive && timer != 0L && System.currentTimeMillis() - t > timer) {
                    break
                }
            }
        } finally {
            clear()
            //            applet.showStatus("");
        }
        return id
    }

    fun setTimer(timer: Long) {
        this.timer = timer
    }
}
