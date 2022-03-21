package gargoyle.xenox.game.sys

import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Resources
import gargoyle.xenox.util.res.audio.AudioClip
import java.awt.Component
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.net.URL
import javax.swing.JPanel

abstract class JScreen protected constructor(
    val id: String,
    component: Component,
    protected val resources: Resources?,
    val messages: Messages,
    val controls: Controls,
    private val audio: AudioManager,
    private val screens: ScreenManager,
    val options: Options
) : JPanel() {
    var isActive = false

    init {
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                component.setSize(e.component.width, e.component.height)
            }
        })
    }

    protected abstract fun _process(): String?
    open fun destroy() {}
    protected fun musicLoop() {
        audio.musicLoop()
    }

    protected fun musicSet(clip: AudioClip) {
        audio.musicSet(clip)
    }

    protected fun musicSet(vararg clip: URL) {
        audio.musicSet(*clip)
    }

    protected fun musicStop() {
        audio.musicStop()
    }

    fun process(): String? {
        controls.reset()
        screens.screenShow(id)
        repaint()
        isActive = true
        return _process()
    }

    fun soundPlay(clip: AudioClip) {
        audio.soundPlay(clip)
    }

    protected fun soundPlay(vararg clip: URL) {
        audio.soundPlay(*clip)
    }
}
