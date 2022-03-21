package gargoyle.xenox.game.scr.menu

import gargoyle.xenox.game.scr.game.JGame
import gargoyle.xenox.game.scr.hi.JHi
import gargoyle.xenox.game.scr.menu.JFlame.FlameParams
import gargoyle.xenox.game.scr.menu.JFlame.FlameParams.Align
import gargoyle.xenox.game.scr.menu.config.JControls
import gargoyle.xenox.game.scr.menu.config.JOptions
import gargoyle.xenox.game.scr.status.JStatus
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Resources
import gargoyle.xenox.util.res.audio.AudioClip
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridLayout
import java.awt.Image
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

class JIntro(
    applet: Component,
    resources: Resources?,
    messages: Messages,
    audio: AudioManager,
    screens: ScreenManager,
    options: Options,
    controls: Controls,
    title: String,
    application: Boolean
) : JScreen(
    SCR_INTRO, applet, resources, messages, controls, audio, screens, options
) {
    private val application: Boolean
    private val btnExit: JButton
    private val flame: JFlame

    private val image: Image?
    private val jControls: JControls = JControls(this)
    private val jOptions: JOptions = JOptions(this)
    private val pnlControls: JPanel

    private var action: String? = null

    private var toInit = true

    init {
        layout = GridLayout(0, 1)
        val params = FlameParams()
        params.text = title
        params.setAlign(Align.TOP)
        params.isFade = false
        params.alpha = ALPHA
        flame = JFlame(params)
        flame.isVisible = false
        add(flame)
        pnlControls = object : JPanel() {
            public override fun paintComponent(g: Graphics) {
                if (g is Graphics2D) {
                    val c = g.composite
                    g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA / 100.0f)
                    super.paintComponent(g)
                    g.composite = c
                } else {
                    super.paintComponent(g)
                }
            }
        }
        pnlControls.setVisible(false)
        pnlControls.setBackground(Color.BLACK)
        background = Color.BLACK
        val mgr = GridLayout(0, 1)
        pnlControls.setLayout(mgr)
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val size = size
                if (size.width == 0 && size.height == 0) {
                    return
                }
                synchronized(flame) {
                    val w = width / 4
                    val h = height / 64
                    if (mgr.hgap != w || mgr.vgap != h) {
                        mgr.hgap = w
                        mgr.vgap = h
                        pnlControls.setBorder(BorderFactory.createEmptyBorder(h, w, h, w))
                    }
                    val font = scaleFont(params.text, getSize(), flame.graphics)
                    params.setFontSize(font.size)
                    if (isVisible && flame.parent != null) {
                        if (flame.isRunning) {
                            flame.stop()
                            flame.destroy()
                        }
                        flame.init()
                        flame.start()
                        repaint()
                    }
                }
            }

            override fun componentShown(e: ComponentEvent) {
                componentResized(e)
            }
        })
        val btnStart = JButton(messages[STR_START_GAME])
        btnStart.addActionListener { action = JGame.SCR_GAME }
        pnlControls.add(btnStart)
        val btnHi = JButton(messages[STR_HISCORE])
        btnHi.addActionListener { action = JHi.SCR_HI }
        pnlControls.add(btnHi)
        val btnOptions = JButton(messages[JOptions.STR_OPTIONS])
        btnOptions.addActionListener {
            jOptions.pack()
            jOptions.setLocationRelativeTo(this)
            jOptions.isVisible = true
            audio.musicStop()
            audio.musicPlay()
        }
        pnlControls.add(btnOptions)
        val btnControls = JButton(messages[JControls.STR_CONTROLS])
        btnControls.addActionListener {
            jControls.pack()
            jControls.setLocationRelativeTo(this)
            jControls.isVisible = true
        }
        pnlControls.add(btnControls)
        btnExit = JButton(messages[STR_EXIT])
        btnExit.addActionListener { action = JStatus.SCR_STATUS }
        pnlControls.add(btnExit)
        add(pnlControls)
        image = this.resources!!.load(BufferedImage::class.java, INTRO_IMAGE)
        this.application = application
    }

    override fun _process(): String? {
        musicSet(resources!!.load(AudioClip::class.java, INTRO_MUSIC)!!)
        btnExit.isEnabled = application
        action = null
        flame.start()
        if (toInit) {
            Thread.sleep(1000)
            flame.isVisible = isVisible
            pnlControls.isVisible = isVisible
            toInit = false
        }
        musicLoop()
        repaint()
        while (action == null && !Thread.currentThread().isInterrupted) {
            Thread.sleep(10)
        }
        val fadeDelay = flame.fade()
        if (flame.isCanDisplay) {
            try {
                Thread.sleep(fadeDelay)
            } catch (e: InterruptedException) {
                flame.stop()
                musicStop()
                flame.destroy()
                return action
            }
        }
        flame.stop()
        musicStop()
        return action
    }

    override fun destroy() {
        flame.stop()
        flame.destroy()
        SwingUtilities.invokeLater {
            jOptions.dispose()
            jControls.dispose()
        }
    }

    override fun getPreferredSize(): Dimension {
        return if (image == null) super.getPreferredSize() else Dimension(image.getWidth(this), image.getHeight(this))
    }

    public override fun paintComponent(g: Graphics) {
        if (image != null) {
            g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(this), image.getHeight(this), this)
        }
    }

    companion object {
        const val INTRO_IMAGE = "intro.jpg"
        const val SCR_INTRO = "Intro"
        const val STR_START_GAME = "start_game"
        private const val ALPHA = 40
        private const val FONT_SIZE = 20.0f
        private const val INTRO_MUSIC = "intro.au"
        private const val STR_EXIT = "exit"
        private const val STR_HISCORE = "hi_score"
        private fun scaleFont(text: String, rect: Dimension, g: Graphics): Font {
            var fontSize = FONT_SIZE
            val font = g.font.deriveFont(fontSize)
            val width = g.getFontMetrics(font).stringWidth(text)
            fontSize *= rect.width / width.toFloat()
            return g.font.deriveFont(fontSize)
        }
    }
}
