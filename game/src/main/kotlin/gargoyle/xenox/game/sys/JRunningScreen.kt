package gargoyle.xenox.game.sys

import gargoyle.xenox.i.ScreenAssets
import java.awt.*

abstract class JRunningScreen protected constructor(assets: ScreenAssets, id: String, applet: Component) :
    JScreen(assets, id, applet) {

    protected var image: Image? = null
    protected var message: String = ""

    private var running = true
    private var timer: Long = 0

    protected constructor(
        assets: ScreenAssets,
        id: String,
        applet: Component,
        image: Image?
    ) : this(assets, id, applet) {
        this.image = image
    }

    override fun doProcess(): String {
        return processWait()
    }

    override fun destroy() {
        running = false
    }

    protected fun clear() {
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
