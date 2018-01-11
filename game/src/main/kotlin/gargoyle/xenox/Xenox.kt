package gargoyle.xenox

import gargoyle.xenox.game.scr.game.JGame
import gargoyle.xenox.game.scr.hi.JHi
import gargoyle.xenox.game.scr.menu.JIntro
import gargoyle.xenox.game.scr.status.JStatus
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.HiScore
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.game.sys.gui.JResizablePanel
import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenAssets
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.i18n.I18N
import gargoyle.xenox.info.CampaignInfo
import gargoyle.xenox.info.LevelInfo
import gargoyle.xenox.res.Res
import gargoyle.xenox.util.applet.GApplet
import gargoyle.xenox.util.audio.AudioClip
import gargoyle.xenox.util.i18n.get
import java.awt.CardLayout
import java.awt.Color
import java.awt.FlowLayout
import java.util.*


class Xenox : GApplet(),
    Runnable, AudioManager, ScreenManager, ScreenAssets {
    override val audio: AudioManager = this
    override val controls: Controls = Controls()
    private val cardLayout: CardLayout
    override val messages: ResourceBundle =
        I18N.getResourceBundle("${Xenox::class.simpleName}", Locale.getDefault())
    override val options: Options = Options()
    private val pnlCards: JResizablePanel
    override val screens: ScreenManager = this
    private val screenMap: MutableMap<String, JScreen> = mutableMapOf()


    private lateinit var clip: AudioClip
    private lateinit var currentScreen: JScreen
    private lateinit var game: JGame
    private lateinit var hi: JHi
    private val hiScore: HiScore = HiScore()
    private lateinit var intro: JIntro

    private var running = false
    private lateinit var status: JStatus

    private lateinit var thread: Thread

    init {
        addKeyListener(controls)
        controls.addDefaultConfig()
        layout = FlowLayout(FlowLayout.CENTER, 0, 0)
        add(JResizablePanel(CardLayout().also { cardLayout = it }).also { pnlCards = it })
    }

    override fun canExit(): Boolean {
        return isApplication && ask(messages[STR_EXIT])
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
        if (::game.isInitialized) {
            game.destroy()
        }
        musicStop()
    }

    override fun doInit() {
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
        screenAdd(JGame(this, this).also { game = it })
        screenAdd(JStatus(this, this).apply { setTimer(5000) }.also { status = it })
        screenAdd(JIntro(this, this, this.isApplication).also { intro = it })
        screenAdd(JHi(this, this, this.hiScore).apply { setTimer(5000) }.also { hi = it })
    }

    private fun goGame() {
        status.process(messages[JIntro.STR_START_GAME], null)
        firstLevel()
        do {
            val level = currentLevel ?: break
            status.process(
                messages[JStatus.STR_GET_READY], level.cover,
                level.music, true
            )
            game.loadLevel(currentLevelNumber, level)
            while (null == game.process()) {
                musicStop()
                if (!running || !game.isPlayerAlive) {
                    break
                }
                status.process(messages[JStatus.STR_LIFE_LOST], null)
            }
            musicStop()
            status.process(messages[JStatus.STR_LEVEL_FINISHED], level.image)
            if (!nextLevel()) {
                break
            }
        } while (game.isPlayerAlive)
        musicStop()
        if (game.isPlayerAlive) {
            status.process(
                messages[JStatus.STR_GAME_OVER], null, Res.url(SND_GAME_OVER),
                false
            )
        } else {
            status.process(
                messages[JStatus.STR_GAME_OVER], null, Res.url(SND_GAME_OVER),
                false
            )
        }
    }

    private val userName: String
        get() {
            if (hiScore.userName.isEmpty()) {
                try {
                    hiScore.userName = System.getProperty("user.name")
                } catch (e: SecurityException) {
                    hiScore.userName = prompt(messages[STR_NAME])
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
        if (::clip.isInitialized) this.clip.stop()
        this.clip = clip
    }

    override fun musicStop() {
        if (::clip.isInitialized) clip.stop()
    }

    override fun screenShow(name: String) {
        screenShow(screenGet(name))
    }

    private fun screenShow(screen: JScreen) {
        pnlCards.component = screen
        cardLayout.show(pnlCards, screen.id)
        size = screen.preferredSize
        pnlCards.reset()
        currentScreen = screen
    }

    override fun run() {
        running = true
        try {
            while (running) {
                try {
                    when (intro.process() ?: return) {
                        JGame.SCR_GAME -> {
                            goGame()
                            hiScore.score(userName, game.playerScore)
                        }

                        JStatus.SCR_STATUS -> running = false
                        JHi.SCR_HI -> hi.process()
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
        screenMap[screen.id] = screen
        pnlCards.add(screen, screen.id)
    }

    override fun screenGet(name: String): JScreen = screenMap[name]!!

    override fun soundPlay(clip: AudioClip) {
        clip.play()
    }

    override fun setSize(width: Int, height: Int) {
        if (options.isResize) {
            pnlCards.setManaged(false)
            super.setSize(width, height)
        } else {
            pnlCards.setManaged(true)
        }
    }


    override var campaign: CampaignInfo = ServiceLoader.load(CampaignInfo::class.java).first()

    var currentLevelNumber: Int = 0
        private set


    val currentLevel: LevelInfo?
        get() = if (currentLevelNumber < campaign.levels.size) campaign.levels[currentLevelNumber] else null

    fun firstLevel() {
        currentLevelNumber = 0
    }

    fun nextLevel(): Boolean {
        currentLevelNumber++
        return currentLevelNumber < campaign.levels.size
    }

    companion object {
        const val ITEM_HIDE: Int = 100
        const val ITEM_SHOW: Int = 100
        const val LIVES: Int = 5
        const val PF_CELL: Int = 8
        const val PF_HEIGHT: Int = 100
        const val PF_WIDTH: Int = 100
        const val SND_GOT: String = "got.wav"
        const val STR_CANCEL: String = "cancel"
        const val STR_OK: String = "ok"
        private const val SND_GAME_OVER = "gameover.wav"
        private const val STR_EXIT = "exitq"
        private const val STR_NAME = "name"

        @JvmStatic
        fun main(args: Array<String>) {
            run(Xenox::class, args)
        }
    }
}
