package gargoyle.xenox

import gargoyle.xenox.game.scr.game.JGame
import gargoyle.xenox.game.scr.game.game.Campaign
import gargoyle.xenox.game.scr.hi.JHi
import gargoyle.xenox.game.scr.menu.JIntro
import gargoyle.xenox.game.scr.status.JStatus
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.HiScore
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.applet.GApplet
import gargoyle.xenox.util.gui.JResizablePanel
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Res
import gargoyle.xenox.util.res.Resources
import gargoyle.xenox.util.res.audio.AudioClip
import java.awt.CardLayout
import java.awt.Color
import java.awt.FlowLayout
import java.net.URL
import java.nio.charset.Charset
import java.util.Locale

class Xenox : GApplet(
    Resources(
        true, Res.nearClassUrl(
            Xenox::class.java, "res"
        )!!
    )
), Runnable, AudioManager, ScreenManager {
    val controls: Controls = Controls()
    private var layout: CardLayout
    private val messages: Messages = Messages(
        String.format("%s/i18n/%s", Xenox::class.java.getPackage().name, Xenox::class.java.simpleName),
        Locale.getDefault()
    )
    val options: Options = Options()
    private var pnlCards: JResizablePanel
    private val screens: MutableMap<String?, JScreen> = HashMap()


    private lateinit var clip: AudioClip
    private lateinit var currentScreen: JScreen
    private var game: JGame? = null
    private lateinit var hi: JHi
    val hiScore: HiScore = HiScore()
    private lateinit var intro: JIntro

    private var running = false
    private lateinit var status: JStatus

    private lateinit var thread: Thread

    init {
        addKeyListener(controls)
        controls.addDefaultConfig()
        setLayout(FlowLayout(FlowLayout.CENTER, 0, 0))
        add(JResizablePanel(CardLayout().also { layout = it }).also { pnlCards = it })
    }

    override fun canExit(): Boolean {
        return isApplication() && ask(getMessages()[STR_EXIT])
    }

    override fun doDestroy() {
        if (::intro.isInitialized) {
            intro.destroy()
        }
        if (::thread.isInitialized) {
            running = false
            thread.interrupt()
        }
        if (::status.isInitialized) {
            status.destroy()
        }
        game!!.destroy()
        musicStop()
    }

    override fun doInit() {
        resources.addRoot(documentBase)
        resources.addRoot(codeBase)
        background = Color.BLACK
        val p = parent
        if (p != null) {
            p.background = Color.BLACK
        }
        construct()
        if (!::thread.isInitialized) {
            thread = Thread(this, Xenox::class.java.name)
            thread.start()
        }
    }

    override fun doStart() {
        if (::currentScreen.isInitialized) {
            currentScreen.isActive = (true)
        }
    }

    override fun doStop() {
        if (::currentScreen.isInitialized) {
            currentScreen.isActive = (false)
        }
    }

    private fun construct() {
        screenAdd(JGame(this, resources, messages, controls, this, this, options).also { game = it })
        screenAdd(JStatus(this, resources, messages, this, this, options, controls, title).also { status = it })
        status.setTimer(5000)
        screenAdd(
            JIntro(
                this,
                resources,
                messages,
                this,
                this,
                options,
                controls,
                this.title,
                this.isApplication()
            ).also { intro = it })
        screenAdd(JHi(this, resources, messages, hiScore, this, this, options, controls, title).also { hi = it })
    }

    private fun game() {
        val urlLevels = urlLevels
        val campaign = Campaign(urlLevels, Charset.defaultCharset())
        status.process(null, messages[JIntro.STR_START_GAME], null)
        campaign.init()
        do {
            val level = campaign.currentLevel ?: break
            status.process(
                level.title, messages[JStatus.STR_GET_READY],
                level.cover, level.music
            )
            game!!.loadLevel(campaign.currentLevelNumber, level)
            while (null == game!!.process()) {
                musicStop()
                if (!running || !game!!.isPlayerAlive) {
                    break
                }
                status.process(null, messages[JStatus.STR_LIFE_LOST], null)
            }
            musicStop()
            status.process(null, messages[JStatus.STR_LEVEL_FINISHED], level.image)
            if (!campaign.next()) {
                break
            }
        } while (game!!.isPlayerAlive)
        musicStop()
        if (game!!.isPlayerAlive) {
            status.process(
                null, messages[JStatus.STR_GAME_OVER], null,
                resources.url(true, SND_GAME_OVER)
            )
        } else {
            status.process(
                null, messages[JStatus.STR_GAME_OVER], null,
                resources.url(true, SND_GAME_OVER)
            )
        }
    }

    fun getMessages(): Messages {
        return messages
    }

    private val urlLevels: URL
        get() {
            var urlLevels: URL? = null
            run {
                val file = resources.url(true, "campaign.zip")
                if (file != null) {
                    val zip = Res.toURL(String.format("jar:%s!/campaign.properties", file.toExternalForm()))
                    if (Res.isUrlOk(zip)) {
                        urlLevels = zip
                    }
                }
            }
            if (urlLevels == null) {
                urlLevels = resources.url(true, "levels/campaign.properties")
            }
            return urlLevels!!
        }
    private val userName: String?
        get() {
            if (hiScore.userName != null && hiScore.userName!!.isEmpty()) {
                try {
                    hiScore.userName = System.getProperty("user.name")
                } catch (e: SecurityException) {
                    hiScore.userName = prompt(getMessages()[STR_NAME])
                }
            }
            return hiScore.userName
        }

    override fun musicLoop() {
        if (::clip.isInitialized) {
            clip.stop()
            if (options.isSound) clip.loop()
        }
    }

    override fun musicPlay() {
        if (::clip.isInitialized) {
            clip.stop()
            if (options.isSound) clip.play()
        }
    }

    override fun musicSet(clip: AudioClip) {
        clip.stop()
        this.clip = clip
    }

    override fun musicSet(vararg clip: URL) {
        musicSet(resources.load(AudioClip::class.java, *clip)!!)
    }

    override fun musicStop() {
        if (::clip.isInitialized) clip.stop()
    }

    override fun screenShow(name: String) {
        screenShow(screenGet(name)!!)
    }

    private fun screenShow(screen: JScreen) {
        pnlCards.component = screen
        layout.show(pnlCards, screen.id)
        size = screen.preferredSize
        pnlCards.reset()
        currentScreen = screen
    }

    override fun run() {
        running = true
        try {
            while (running) {
                try {
                    val action = intro.process() ?: return
                    if (JGame.SCR_GAME == action) {
                        game()
                        hiScore.score(userName, game!!.playerScore)
                    }
                    if (JStatus.SCR_STATUS == action) {
                        running = false
                    }
                    if (JHi.SCR_HI == action) {
                        hi.process()
                    }
                } catch (e: InterruptedException) {
                    break
                }
            }
        } finally {
            doDestroy()
            exit()
        }
    }

    private fun screenAdd(screen: JScreen) {
        screens[screen.id] = screen
        pnlCards.add(screen, screen.id)
    }

    override fun screenGet(name: String): JScreen? {
        return screens[name]
    }

    override fun soundPlay(clip: AudioClip) {
        clip.play()
    }

    override fun soundPlay(vararg clip: URL) {
        soundPlay(resources.load(AudioClip::class.java, *clip)!!)
    }

    override fun setSize(width: Int, height: Int) {
        if (options.isResize) {
            pnlCards.setManaged(false)
            super.setSize(width, height)
        } else {
            pnlCards.setManaged(true)
        }
    }

    companion object {
        const val ITEM_HIDE = 100
        const val ITEM_SHOW = 100
        const val LIVES = 5
        const val PF_CELL = 8
        const val PF_HEIGHT = 100
        const val PF_WIDTH = 100
        const val SND_GOT = "got.wav"
        const val STR_CANCEL = "cancel"
        const val STR_OK = "ok"
        private const val SND_GAME_OVER = "gameover.wav"
        private const val STR_EXIT = "exitq"
        private const val STR_NAME = "name"

        @JvmStatic
        fun main(args: Array<String>) {
            run(Xenox::class.java, args)
        }
    }
}
