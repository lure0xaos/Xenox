package gargoyle.xenox.game.sys

import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenAssets
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.audio.AudioClip
import java.awt.Component
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.*
import javax.swing.JPanel

@Suppress("LeakingThis")
abstract class JScreen protected constructor(val assets: ScreenAssets, val id: String, component: Component) :
    JPanel() {
    val messages: ResourceBundle
        get() = assets.messages
    val controls: Controls
        get() = assets.controls
    val audio: AudioManager
        get() = assets.audio
    val screens: ScreenManager
        get() = assets.screens
    val options: Options
        get() = assets.options
    val title: String
        get() = assets.title

    var isActive: Boolean = false

    init {
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                component.setSize(e.component.width, e.component.height)
            }
        })
    }

    protected abstract fun doProcess(): String?
    open fun destroy() {}
    protected fun musicLoop() {
        if (options.isSound) audio.musicLoop()
    }

    protected fun musicSet(clip: AudioClip) {
        if (options.isSound) audio.musicSet(clip)
    }

    protected fun musicStop() {
        audio.musicStop()
    }

    fun process(): String? {
        controls.reset()
        screens.screenShow(id)
        repaint()
        isActive = true
        return doProcess()
    }

    fun soundPlay(clip: AudioClip) {
        if (options.isSound) audio.soundPlay(clip)
    }

    fun musicPlay(clip: AudioClip) {
        if (options.isSound) {
            audio.musicSet(clip)
            audio.musicLoop()
        }
    }
}
