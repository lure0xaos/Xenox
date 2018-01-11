package gargoyle.xenox.game.scr.menu

import gargoyle.xenox.game.scr.game.JGame
import gargoyle.xenox.game.scr.hi.JHi
import gargoyle.xenox.game.scr.menu.JFlame.FlameParams
import gargoyle.xenox.game.scr.menu.JFlame.FlameParams.Align
import gargoyle.xenox.game.scr.menu.config.JControls
import gargoyle.xenox.game.scr.menu.config.JOptions
import gargoyle.xenox.game.scr.select.JSelect
import gargoyle.xenox.game.scr.status.JStatus
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.gui.JCompositePanel
import gargoyle.xenox.i.ScreenAssets
import gargoyle.xenox.res.Res
import gargoyle.xenox.util.applet.GApplet
import gargoyle.xenox.util.i18n.get
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

class JIntro(assets: ScreenAssets, private val applet: GApplet, application: Boolean) :
    JScreen(assets, SCR_INTRO, applet) {
    private val flame: JFlame

    private val image: Image?
    private val jControls: JControls = JControls(this)
    private val jOptions: JOptions = JOptions(this)
    private val pnlControls: JPanel

    private var screen: String? = null

    private var isInit = false

    init {
        layout = GridLayout(0, 1)
        flame = JFlame(FlameParams(text = title, align = Align.TOP, isFade = false, alpha = ALPHA))
        flame.isVisible = false
        add(flame)
        pnlControls = JCompositePanel(ALPHA.toFloat())
        pnlControls.setVisible(false)
        pnlControls.setBackground(Color.BLACK)
        background = Color.BLACK
        val mgr = GridLayout(0, 1)
        pnlControls.setLayout(mgr)
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val size = size
                if (size.width != 0 || size.height != 0) {
                    synchronized(flame) {
                        val w = width / 4
                        val h = height / 64
                        if (mgr.hgap != w || mgr.vgap != h) {
                            mgr.hgap = w
                            mgr.vgap = h
                            pnlControls.setBorder(BorderFactory.createEmptyBorder(h, w, h, w))
                        }
                        flame.onFlameResize(getSize(), isVisible)
                    }
                }
            }

            override fun componentShown(e: ComponentEvent) {
                componentResized(e)
            }
        })
        pnlControls.add(JButton(messages[STR_START_GAME]).apply {
            addActionListener {
                assets.campaign = JSelect(this@JIntro).also {
                    it.pack()
                    it.setLocationRelativeTo(this@JIntro)
                    it.isVisible = true
                }.campaign
                screen = JGame.SCR_GAME
            }
        })
        pnlControls.add(JButton(messages[STR_HISCORE]).apply { addActionListener { screen = JHi.SCR_HI } })
        pnlControls.add(JButton(messages[JOptions.STR_OPTIONS]).apply {
            addActionListener {
                jOptions.pack()
                jOptions.setLocationRelativeTo(this)
                val wasSound = options.isSound
                jOptions.isVisible = true
                val sound = options.isSound
                if (wasSound != sound) {
                    if (sound) {
                        audio.musicPlay()
                    } else {
                        audio.musicStop()
                    }
                }
            }
        })
        pnlControls.add(JButton(messages[JControls.STR_CONTROLS]).apply {
            addActionListener {
                jControls.pack()
                jControls.setLocationRelativeTo(this)
                jControls.isVisible = true
            }
        })
        pnlControls.add(JButton(messages[STR_EXIT]).apply {
            isEnabled = application
            addActionListener { screen = JStatus.SCR_STATUS }
        })
        add(pnlControls)
        image = applet.getImage(Res.url(INTRO_IMAGE))
    }


    override fun doProcess(): String? {
        musicSet(applet.getAudioClip(Res.url(INTRO_MUSIC)))
        screen = null
        flame.start()
        if (!isInit) {
            Thread.sleep(1000)
            flame.isVisible = isVisible
            pnlControls.isVisible = isVisible
            isInit = true
        }
        musicLoop()
        repaint()
        while (screen == null && !Thread.currentThread().isInterrupted) {
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
                return screen
            }
        }
        flame.stop()
        musicStop()
        return screen
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
        const val INTRO_IMAGE: String = "intro.jpg"
        const val SCR_INTRO: String = "Intro"
        const val STR_START_GAME: String = "start_game"
        private const val ALPHA = 40
        private const val INTRO_MUSIC = "intro.au"
        private const val STR_EXIT = "exit"
        private const val STR_HISCORE = "hi_score"
    }
}
