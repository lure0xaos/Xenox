package gargoyle.xenox.game.scr.menu

import java.awt.*
import java.awt.image.MemoryImageSource
import java.awt.image.PixelGrabber
import java.util.*
import javax.swing.JComponent

class JFlame(params: FlameParams) : JComponent(), Runnable {
    private val pal = IntArray(INT256)
    private val params: FlameParams
    private val rnd = Random()
    private val twColor = IntArray(TW_MAX_COLOR)

    private var active = true
    private var alpha = 0

    private lateinit var art: Image
    private lateinit var buf: IntArray
    private var burn = true

    var isCanDisplay: Boolean = false
        private set
    private var colMask = INT0x00FF0000
    private var colShift = INT16

    private lateinit var flameImage: Image

    private lateinit var flameSource: MemoryImageSource

    @Volatile
    var isRunning: Boolean = false
        private set

    private lateinit var thread: Thread
    private var twTimer: Long = 0
    private lateinit var twWords: IntArray
    private var xSize = 0
    private var ySize = 0

    init {
        this.params = FlameParams(params)
    }

    fun destroy() {
        isCanDisplay = false
        if (isThread()) {
            if (::art.isInitialized) {
                art.flush()
            }
            isRunning = false
        }
    }

    fun fade(): Long {
        burn = false
        return params.fadeDelay
    }

    fun init() {
        background = Color.BLACK
        isCanDisplay = false
        if (isThread()) {
            with(thread) { if (state == Thread.State.NEW) start() }
        } else {
            thread = Thread(this, JFlame::class.java.name).also {
                it.priority = Thread.MIN_PRIORITY
                it.start()
            }
        }
    }

    private fun isThread() = this::thread.isInitialized

    private fun makePal(aSolid: Boolean) {
        if (aSolid) {
            for (i in 0 until TW_MAX_COLOR) {
                twColor[i] = pal[INT210 + rnd.nextInt(INT40) and INT0xff]
            }
        }
    }

    private fun makePal(n: Int) {
        when (n) {
            2 -> makePal2()
            3 -> makePal3()
            4 -> makePal4()
            5 -> makePal5()
            6 -> makePal6()
            1 -> makePal1()
            else -> makePal1()
        }
    }

    private fun makePal1() {
        colMask = INT0x00FF0000
        colShift = INT16
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var g = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 7
                g++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 3
                r++
            }
            if (i2 == 0) {
                i2 = 6
                g++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, g, i)
            if (r < INT255) {
                r++
            }
            if (i2 == 0) {
                i2 = 6
                g++
            }
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, g, i)
            r += 2
            if (r >= INT255) {
                r = INT255
            }
            if (i2 == 0) {
                i2 = 5
                g++
            }
            i2--
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, g, i)
            r += 3
            if (r >= INT255) {
                r = INT255
            }
            if (i2 == 0) {
                i2 = 4
                g++
            }
            i2--
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = pal[INT240 - i] and INT0xFFFFFF00 or pal[INT240 - i] shr INT16 and INT0xff
        }
    }

    private fun makePal2() {
        colMask = INT65280
        colShift = INT8
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var g = 1
        var b = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(i, g, b)
            if (i1 == 0) {
                i1 = 5
                g++
            }
            if (i2 == 0) {
                i2 = 7
                b++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(i, g, b)
            if (i1 == 0) {
                i1 = 3
                g++
            }
            if (i2 == 0) {
                i2 = 5
                b++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(i, g, b)
            if (g < INT255) {
                g++
            }
            if (b < INT255) {
                b++
            }
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(i, g, b)
            g += 2
            if (g >= INT255) {
                g = INT255
            }
            if (b < INT255) {
                b++
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(i, g, b)
            g += 3
            if (g >= INT255) {
                g = INT255
            }
            b += 2
            if (b >= INT255) {
                b = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = pal[INT240 - i] and INT0xFF00FF00 or INT0x00FF0000 and pal[INT240 - i] shl INT8
        }
    }

    private fun makePal3() {
        colMask = INT255
        colShift = 0
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var b = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, i, b)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 7
                b++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, i, b)
            if (i1 == 0) {
                i1 = 3
                r++
            }
            if (i2 == 0) {
                i2 = 5
                b++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, i, b)
            if (r < INT255) {
                r++
            }
            if (b < INT255) {
                b++
            }
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, i, b)
            ++r
            if (r >= INT255) {
                r = INT255
            }
            if (b < INT255) {
                b++
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, i, b)
            r += 3
            if (r >= INT255) {
                r = INT255
            }
            b += 2
            if (b >= INT255) {
                b = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i)
        }
    }

    private fun makePal4() {
        colMask = INT0x00FF0000
        colShift = INT16
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var g = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 7
                r++
            }
            if (i2 == 0) {
                i2 = 3
                g++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 2
                g++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 3
                r++
            }
            if (g < INT255) {
                g++
            }
            i1--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, g, i)
            if (r < INT255) {
                r++
            }
            g += 2
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, g, i)
            r += 2
            if (r >= INT255) {
                r = INT255
            }
            g += 4
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i)
        }
    }

    private fun makePal5() {
        colMask = INT65280
        colShift = INT8
        for (i in 0 until INT256) {
            pal[i] = rgb(i, i, i)
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = pal[INT240 - i] and INT0xFF00FFFF
        }
    }

    private fun makePal6() {
        colMask = INT0x00FF0000
        colShift = INT16
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var g = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 9
                r++
            }
            if (i2 == 0) {
                i2 = 6
                g++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = INT8
                r++
            }
            if (i2 == 0) {
                i2 = 5
                g++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 3
                g++
            }
            i1--
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, g, i)
            ++r
            if (r >= INT255) {
                r = INT255
            }
            ++g
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, g, i)
            r += 3
            if (r >= INT255) {
                r = INT255
            }
            g += 3
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i)
        }
    }

    override fun paint(g: Graphics) {
        if (g is Graphics2D) {
            val c = g.composite
            if (alpha != 0) {
                g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 100f)
            }
            paint0(g)
            g.composite = c
        } else {
            paint0(g)
        }
    }

    private fun paint0(g: Graphics) {
        if (isCanDisplay && ::art.isInitialized) {
            g.drawImage(art, 0, 0, width, height, 0, 0, art.getWidth(this), art.getHeight(this), Color.BLACK, this)
        } else {
            g.color = Color.BLACK
            g.fillRect(0, 0, xSize, ySize)
        }
    }

    private fun paintActive(): Boolean {
        for (l in 1 until xSize - 1 - 1) {
            var bb = xSize
            var aa = 0 /*bb - xSize*/
            if (l < xSize - 1) {
                for (j in 1 until ySize) {
                    if (buf[l + bb] and colMask shr colShift < params.decay) {
                        buf[l + aa] = INT0xFF000000
                    } else {
                        buf[aa + l - (rnd.nextInt(3) - 1)] =
                            pal[(buf[l + bb] and colMask shr colShift) - rnd.nextInt(params.decay) and INT0xff]
                    }
                    aa += xSize
                    bb += xSize
                }
            }
        }
        val l = time()
        if (burn && (l < twTimer - params.fadeDelay || !params.isFade)) {
            var i = 1
            var j = 0
            while (i < twWords[0]) {
                j += twWords[i]
                i++
                var cnt = twWords[i]
                i++
                while (cnt > 0) {
                    buf[j] = twColor[rnd.nextInt(TW_MAX_COLOR)]
                    j++
                    --cnt
                }
            }
        }
        if (l > twTimer) {
            twTimer = time() + params.delay
        }
        flameSource.newPixels(0, 0, xSize, ySize)
        art.graphics.drawImage(flameImage, 0, 0, null)
        try {
            Thread.sleep(SLEEP20.toLong())
        } catch (e: InterruptedException) {
            return true
        }
        repaint()
        return false
    }

    private fun prepare() {
        xSize = width
        ySize = height
        val size = xSize * ySize
        art = createImage(xSize, ySize)
        buf = IntArray(size)
        flameSource = MemoryImageSource(xSize, ySize, buf, 0, xSize)
        flameSource.setAnimated(true)
        flameImage = createImage(flameSource)
        alpha = params.alpha
        val twLen = params.fontSize * (xSize / (params.fontSize / INT8))
        makePal(params.color)
        makePal(!params.isSolid)
        val g = art.graphics
        g.font = Font(params.font, Font.PLAIN, params.fontSize)
        val fontMetrics = g.fontMetrics
        val word = params.text
        val pixelGrabber = PixelGrabber(art, 0, 0, xSize, ySize, buf, 0, xSize)
        var i1 = size - 1
        while (i1 >= 0) {
            buf[i1] = INT0xFF000000
            i1--
        }
        g.color = Color.BLACK
        g.fillRect(0, 0, xSize, ySize)
        g.color = Color.WHITE
        val y: Int
        val align = params.align
        y = when (align) {
            FlameParams.Align.CENTER -> (ySize - fontMetrics.height) / 2 + fontMetrics.ascent + params.position
            FlameParams.Align.TOP -> fontMetrics.ascent + params.position
            FlameParams.Align.BOTTOM -> ySize - fontMetrics.height + fontMetrics.ascent + params.position
        }
        g.drawString(word, (xSize - fontMetrics.stringWidth(word)) / 2, y)
        try {
            isCanDisplay = pixelGrabber.grabPixels()
        } catch (e: InterruptedException) {
            isRunning = false
        }
        twWords = IntArray(twLen)
        if (isCanDisplay) {
            g.color = Color.BLACK
            g.fillRect(0, 0, xSize, ySize)
            var j = 1
            var l = 0
            while (l < size - 1 && j < twLen - 2) {
                var k = 0
                while (l < size - 1 && buf[l] and INT0x00FFFFFF == 0) {
                    l++
                    k++
                }
                twWords[j] = k
                j++
                var i = 0
                while (l < size - 1 && buf[l] and INT0x00FFFFFF != 0) {
                    l++
                    i++
                }
                twWords[j] = i
                j++
            }
            twWords[0] = j - 2
        }
        for (i in 0 until size) {
            buf[i] = INT0xFF000000
        }
        burn = true
        twTimer = 0
        if (params.delay != 0L) {
            twTimer = time() + params.delay - params.fadeDelay
        }
    }

    override fun run() {
        prepare()
        isCanDisplay = true
        isRunning = true
        while (isRunning) {
            if (active) {
                try {
                    if (paintActive()) {
                        break
                    }
                } catch (ignored: RuntimeException) {
                }
            } else {
                try {
                    Thread.sleep(SLEEP100.toLong())
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        isRunning = false
    }

    fun start() {
        active = true
    }

    fun stop() {
        active = false
    }

    fun onFlameResize(dimension: Dimension, visible: Boolean) {
        val fontSize = scaleFont(params.text, dimension, graphics).size
        params.fontSize = fontSize
        if (visible && parent != null) {
            if (isRunning) {
                stop()
                destroy()
            }
            init()
            start()
            repaint()
        }
    }

    private fun scaleFont(text: String, rect: Dimension, g: Graphics): Font {
        var fontSize = FONT_SIZE
        val font = g.font.deriveFont(fontSize)
        val width = g.getFontMetrics(font).stringWidth(text)
        fontSize *= rect.width / width.toFloat()
        return g.font.deriveFont(fontSize)
    }


    class FlameParams {
        var align: Align = DEFAULT_ALIGN
        var alpha: Int = DEFAULT_ALPHA
        var color: Int = DEFAULT_COLOR
            set(value) {
                field = if (value < MIN_COLOR || value > MAX_COLOR) MIN_COLOR else value
            }
        var decay: Int = DEFAULT_DECAY
            set(value) {
                field = if (value < MIN_DECAY || value > MAX_DECAY) DEFAULT_DECAY else value
            }
        var delay: Long = DEFAULT_DELAY
        var isFade: Boolean = DEFAULT_FADE
        var fadeDelay: Long = DEFAULT_FADE_DELAY
        var font: String = DEFAULT_FONT
            set(value) {
                field = when (value) {
                    FONT_VALUE_COURIER -> FONT_FAMILY_COURIER
                    FONT_VALUE_DIALOG -> FONT_FAMILY_DIALOG
                    FONT_VALUE_DIALOG_INPUT -> FONT_FAMILY_DIALOG_INPUT
                    FONT_VALUE_HELVETICA -> FONT_FAMILY_HELVETICA
                    FONT_VALUE_SYMBOL -> FONT_FAMILY_SYMBOL
                    else -> if (GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.contains(font))
                        font
                    else
                        DEFAULT_FONT
                }
            }
        var fontSize: Int = DEFAULT_FONT_SIZE
            set(value) {
                field = if (value < MIN_FONT_SIZE) MIN_FONT_SIZE else value
            }
        var position: Int = DEFAULT_POSITION
        var isSolid: Boolean = DEFAULT_SOLID
        var text: String = DEFAULT_TEXT

        constructor(
            align: Align = DEFAULT_ALIGN,
            alpha: Int = DEFAULT_ALPHA,
            color: Int = DEFAULT_COLOR,
            decay: Int = DEFAULT_DECAY,
            delay: Long = DEFAULT_DELAY,
            isFade: Boolean = DEFAULT_FADE,
            fadeDelay: Long = DEFAULT_FADE_DELAY,
            font: String = DEFAULT_FONT,
            fontSize: Int = DEFAULT_FONT_SIZE,
            position: Int = DEFAULT_POSITION,
            isSolid: Boolean = DEFAULT_SOLID,
            text: String = DEFAULT_TEXT
        ) {
            this.align = align
            this.alpha = alpha
            this.color = color
            this.decay = decay
            this.delay = delay
            this.isFade = isFade
            this.fadeDelay = fadeDelay
            this.font = font
            this.fontSize = fontSize
            this.position = position
            this.isSolid = isSolid
            this.text = text
        }

        constructor(p: FlameParams) : this(
            align = p.align,
            alpha = p.alpha,
            color = p.color,
            decay = p.decay,
            delay = p.delay,
            isFade = p.isFade,
            fadeDelay = p.fadeDelay,
            font = p.font,
            fontSize = p.fontSize,
            position = p.position,
            isSolid = p.isSolid,
            text = p.text
        )

        enum class Align {
            TOP, BOTTOM, CENTER
        }

        companion object {
            private val DEFAULT_ALIGN = Align.TOP
            private const val DEFAULT_ALPHA = 0
            private const val DEFAULT_COLOR = 1
            private const val DEFAULT_DECAY = 20
            private const val DEFAULT_DELAY = 10000L
            private const val DEFAULT_FADE = false
            private const val DEFAULT_FADE_DELAY = 2000L
            private const val DEFAULT_FONT = "Times New Roman"
            private const val DEFAULT_FONT_SIZE = 140
            private const val DEFAULT_POSITION = 0
            private const val DEFAULT_SOLID = false
            private const val DEFAULT_TEXT = "Flames|by|IoN CheN"
            private const val FONT_FAMILY_COURIER = "Courier"
            private const val FONT_FAMILY_DIALOG = "Dialog"
            private const val FONT_FAMILY_DIALOG_INPUT = "DialogInput"
            private const val FONT_FAMILY_HELVETICA = "Helvetica"
            private const val FONT_FAMILY_SYMBOL = "Symbol"
            private const val FONT_VALUE_COURIER = "courier"
            private const val FONT_VALUE_DIALOG = "dialog"
            private const val FONT_VALUE_DIALOG_INPUT = "dialoginput"
            private const val FONT_VALUE_HELVETICA = "helvetica"
            private const val FONT_VALUE_SYMBOL = "symbol"
            private const val MAX_COLOR = 6
            private const val MAX_DECAY = 20
            private const val MIN_COLOR = 1
            private const val MIN_DECAY = 5
            private const val MIN_FONT_SIZE = 10
        }
    }

    companion object {
        private const val INT0x00FF0000 = 0x00ff0000
        private const val INT0x00FFFFFF = 0x00ffffff
        private const val INT0xFF000000 = -0x1000000
        private const val INT0xFF00FF00 = -0xff0100
        private const val INT0xFF00FFFF = -0xff0001
        private const val INT0xFFFFFF00 = -0x100
        private const val INT130 = 130
        private const val INT16 = 16
        private const val INT210 = 210
        private const val INT255 = 255
        private const val INT0xff = INT255
        private const val INT256 = 256
        private const val INT240 = INT256 - INT16
        private const val INT40 = INT256 / 6
        private const val INT60 = INT256 / 4
        private const val INT65280 = INT0xff * INT256
        private const val INT8 = 8
        private const val INT90 = 90
        private const val SLEEP100 = 100
        private const val SLEEP20 = 20
        private const val TW_MAX_COLOR = 50
        private fun rgb(r: Int, g: Int, b: Int): Int = INT0xFF000000 or (b shl INT16) or (r shl INT8) or g

        private fun time(): Long = System.currentTimeMillis()

        private const val FONT_SIZE = 20.0f
    }
}
